//This file is part of the Primitive Code Project
//DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//Copyright (C) 2023  Patrick Hechler
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <https://www.gnu.org/licenses/>.

/*
 * pvm-setup.c
 *
 *  Created on: Oct 26, 2022
 *      Author: pat
 */

#include <pfs/pfs.h>
#include <pfs/pfs-constants.h>
#include <pfs/pfs-element.h>
#include <pfs/pfs-err.h>
#include <pfs/pfs-file.h>
#include <pfs/pfs-folder.h>
#include <pfs/pfs-stream.h>
#include <asm-generic/errno-base.h>
#include <bits/types/FILE.h>
#include <endian.h>
#include <errno.h>
#include <fcntl.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "../include/pvm-version.h"
#include "../include/pvm-virtual-mashine.h"

#define print_version0() print_version(stdout)

#ifdef PVM_DEBUG
static _Bool wait;
static int input;
static _Bool input_is_pipe;
#endif

static inline void print_help(void) {
	printf(
#ifdef PVM_DEBUG
			"Usage: db-pvm [Options] --pmf=[EXECUTE_FILE] [ARGUMENTS]\n"
#else // PVM_DEBUG
			"Usage: pvm [Options] --pmf=[EXECUTE_FILE] [ARGUMENTS]\n"
#endif // PVM_DEBUG
					"Options-Rules:\n"
					"  --pfs=[PFS_FILE]\n"
					"    this Option is NOT optional!\n"
					"  --pmf=[EXECUTE_FILE]\n"
#ifdef PVM_DEBUG
			"    this Option is only optional when --wait is set!\n"
			"  --wait\n"
			"    load the program, but do not start the execution\n"
			"    when this option is set --port=[PORT] or --pipe=[PIPE]\n"
			"    has to be specified.\n"
			"  --port=[PORT]\n"
			"    if this option is set --pipe=[PIPE] is not allowed\n"
			"  --pipe=[PIPE]\n"
			"    if this option is set --port=[PORT] is not allowed\n"
#else // PVM_DEBUG
			"    this Option is NOT optional!\n"
#endif // PVM_DEBUG
			"Options:\n"
			"  --help\n"
			"    print this message and exit\n"
			"  --version\n"
			"    print the version and exit\n"
#ifdef PVM_DEBUG
			"  --port=[PORT]\n"
			"    set the debug port\n"
			"  --pipe=[PIPE]\n"
			"    set the debug pipe\n"
			"  --wait\n"
			"    to not start the execution of the program\n"
			"    wait until an attached debugger commands to execute\n"
			"    (with a normal continue or step command).\n"
#endif // PVM_DEBUG
			"  --cwd=[FOLDER]\n"
			"    set the current working directory for the program\n"
			"  --pfs=[PFS_FILE]\n"
			"    set the file which contains the Patr-File-System\n"
			"  --pmf=[EXECUTE_FILE]\n"
			"    set the file to executed.\n"
			"    all arguments after this option will be passed to the\n"
			"    program as program arguments.\n"
			"    the first argument will be [EXECUTE_FILE], the following\n"
			"    program arguments will be the following arguments.\n"
#ifdef PVM_DEBUG
			"    it is recommended to specify this option even when\n"
			"    --wait is specified.\n"
#endif // PVM_DEBUG
			"");
}

extern void print_version(FILE *file) {
	fprintf(file,
#ifdef PVM_DEBUG
			"debug-"
#endif
					"primitive-virtual-mashine " PVM_VERSION_STR "\n");
}

static void load_exec_in_pfs(int fh, num *exe_size, void **exe_data) {
	ui32 flags = pfs_element_get_flags(fh);
	if (flags == (ui32) -1) {
		fprintf(stderr,
				"could not get the flags of the primitive machine file\n");
		exit(1);
	}
	*exe_size = pfs_file_length(fh);
	*exe_data = malloc(*exe_size);
	if (!*exe_data) {
		fprintf(stderr,
				"could not load the primitive machine file in memory\n");
		exit(1);
	}
	int sh = pfs_open_stream(fh, PFS_SO_READ);
	pfs_element_close(fh);
	for (num total_reat = 0; total_reat < *exe_size;) {
		num reat = pfs_stream_read(sh, *exe_data + total_reat,
				*exe_size - total_reat);
		if (reat == -1) {
			fprintf(stderr, "failed to read the machine file (%s)\n",
					pfs_error());
			exit(1);
		} else if (!reat) {
			fprintf(stderr, "the machine file has been modified\n");
			exit(1);
		}

		total_reat += reat;
	}
	pfs_stream_close(sh);
}

#define word_from_chars_le(a, b)      (a | (b << 8))
#define int_from_chars_le(a, b, c, d) (a | (b << 8) | (c << 16) | (d << 24))
#if BYTE_ORDER == LITTLE_ENDIAN
#	define word_from_chars(a,b)    word_from_chars_le(a,b)
#	define int_from_chars(a,b,c,d) int_from_chars_le (a,b,c,d)
#elif BYTE_ORDER == BIG_ENDIAN
#	define word_from_chars(a,b)    word_from_chars_le(b,a)
#	define int_from_chars(a,b,c,d) int_from_chars_le (d,c,b,a)
#elif BYTE_ORDER == PDP_ENDIAN
#	define word_from_chars(a,b)    word_from_chars_le(a,b)
#	define int_from_chars(a,b,c,d) int_from_chars_le (c,d,a,b)
#else
#	error "non-supported byte order"
#endif

static inline void setup(int argc, char **argv0) {
#ifdef PVM_DEBUG
	wait = 0;
	input = -1;
#endif
	_Bool pmf_in_lfs = 0;
	_Bool pfs_set = 0;
	_Bool first_arg = 1;
	char *cwd = NULL;
	for (argv0++, argc--; *argv0; argv0++, argc--) {
		if (!**argv0) {
			illegal_arg: fprintf(stderr, "unknown argument: '%s'\n", *argv0);
			exit(1);
		}
		if (word_from_chars('-', '-') != *(uint16_t*) *argv0) {
			if (!first_arg) {
				goto illegal_arg;
			}
			char *argv = *argv0;
			struct bm_block_manager *bm = bm_new_file_block_manager_path(argv,
					0);
			if (!bm) {
				fprintf(stderr,
						"could not create the block manger (for the PFS) (%s) : %s\n",
						pfs_error(), argv);
				exit(1);
			}
			if (!pfs_load(bm, NULL, 0)) {
				fprintf(stderr, "could not load the PFS (%s) : %s\n",
						pfs_error(), argv);
				exit(1);
			}
			int bin = pfs_handle("/bin");
			if (bin == -1) {
				fprintf(stderr, "could not open /bin in the PFS (%s) : %s\n",
						pfs_error(), argv);
				exit(1);
			}
			ui32 flags = pfs_element_get_flags(bin);
			if (flags == (ui32) -1) {
				fprintf(stderr, "could not get the flags of /bin (%s) : %s\n",
						pfs_error(), argv);
				exit(1);
			}
			if (flags & PFS_F_FOLDER) {
				int bin_bin = pfs_folder_child_file(bin, "bin");
				if (bin_bin == -1) {
					fprintf(stderr,
							"could not open /bin/bin in the PFS (%s) : %s\n",
							pfs_error(), argv);
					exit(1);
				}
				if (!pfs_element_close(bin)) {
					fprintf(stderr,
							"could not close /bin in the PFS (%s) : %s\n",
							pfs_error(), argv);
					exit(1);
				}
				bin = bin_bin;
			}
			num exe_size;
			void *exe_data;
			load_exec_in_pfs(bin, &exe_size, &exe_data);
			pvm_init_execute(argv0, argc, exe_data, exe_size);
			return;
		}
		first_arg = 0;
		char *argv = *argv0;
#define word_argv(i) (*(uint16_t*) &argv[i])
#define int_argv(i) (*(uint32_t*) &argv[i])
#define word_argv_ne(i, ca, cb) (word_argv(i) != word_from_chars(ca, cb))
#define int_argv_ne(i, ca, cb, cc, cd) (int_argv(i) != int_from_chars(ca, cb, cc, cd))
		switch (word_argv(2)) {
		case word_from_chars('h', 'e'): {
			if (word_argv_ne(4, 'l', 'p') || (argv[6] != '\0')) {
				goto illegal_arg;
			}
			print_help();
			exit(1);
		}
		case word_from_chars('v', 'e'): {
			if (int_argv_ne(4, 'r', 's', 'i', 'o') || word_argv_ne(8, 'n', '\0')) {
				goto illegal_arg;
			}
			print_version0();
			exit(1);
		}
#ifdef PVM_DEBUG
		case word_from_chars('w', 'a'): {
			if (word_argv_ne(4, 'i', 't') || argv[6] != '=') {
				goto illegal_arg;
			}
			wait = 1;
			break;
		}
		case word_from_chars('p', 'o'): {
			if (word_argv_ne(4, 'r', 't') || argv[6] != '=') {
				goto illegal_arg;
			}
			if (input != -1) {
				fprintf(stderr, "debug input already set!\n");
				exit(1);
			}
			char *end;
			long val = strtol(argv + 7, &end, 0);
			if (errno) {
				perror("strtol");
				fprintf(stderr, "could not parse the port '%s'\n", argv + 7);
				exit(1);
			}
			if (val > UINT16_MAX || val < 0) {
				fprintf(stderr, "illegal port %ld (MAX=%d MIN=0)", val,
				UINT16_MAX);
				exit(1);
			}
			input = val;
			input_is_pipe = 0;
			break;
		}
		case word_from_chars('p', 'i'): {
			if (word_argv_ne(4, 'p', 'e') || argv[6] != '=') {
				goto illegal_arg;
			}
			if (input != -1) {
				fprintf(stderr, "debug input already set!\n");
				exit(1);
			}
			input = open(argv + 7, O_RDONLY);
			if (input == -1) {
				perror("open");
				fprintf(stderr, "could not open the debug input (%s)!\n",
						argv + 7);
				exit(1);
			}
			input_is_pipe = 1;
			break;
		}
		case word_from_chars('s', 't'): {
			if (word_argv_ne(4, 'd', '=')) {
				goto illegal_arg;
			}
			if (input != -1) {
				fprintf(stderr, "debug input already set!\n");
				exit(1);
			}
			char *end;
			long val = strtol(argv + 6, &end, 0);
			input = val;
			if (val != input || errno) {
				perror("strtol");
				fprintf(stderr, "could not parse the file descriptor '%s'\n",
						argv + 6);
				exit(1);
			}
			input_is_pipe = 1;
			break;
		}
#endif
		case word_from_chars('c', 'w'): {
			if (word_argv_ne(4, 'd', '=')) {
				goto illegal_arg;
			}
			if (cwd) {
				fprintf(stderr, "cwd already set!\n");
				exit(1);
			}
			cwd = argv + 6;
			if (pfs_set) {
				if (!pfs_change_dir_path(cwd)) {
					fprintf(stderr, "could not set cwd to '%s' (%s)!\n", cwd,
							pfs_error());
					exit(1);
				}
			}
			break;
		}
		case word_from_chars('p', 'f'): {
			if (word_argv_ne(4, 's', '=')) {
				goto illegal_arg;
			}
			if (pfs_set) {
				fprintf(stderr, "pfs already set!\n");
				exit(1);
			}
			pfs_set = 1;
			struct bm_block_manager *bm = bm_new_file_block_manager_path(
					argv + 6, 0);
			if (!bm) {
				fprintf(stderr,
						"could not create the block manger (for the PFS) (%s) : %s\n",
						pfs_error(), argv + 6);
				exit(1);
			}
			if (!pfs_load(bm, cwd, 0)) {
				fprintf(stderr, "could not load the PFS (%s)!\n", pfs_error());
				exit(1);
			}
			break;
		}
		case word_from_chars('p', 'm'): {
			switch (word_argv(4)) {
			case word_from_chars('f', '-'): {
				if (word_argv_ne(6, 'i', 'n')) {
					goto illegal_arg;
				}
				switch (int_argv(8)) {
				case int_from_chars('-', 'l', 'f', 's'):
					if (argv[12] != '\0') {
						goto illegal_arg;
					}
					pmf_in_lfs = 1;
					break;
				case int_from_chars('-', 'p', 'f', 's'): {
					if (argv[12] != '\0') {
						goto illegal_arg;
					}
					pmf_in_lfs = 0;
					break;
				}
				default:
					goto illegal_arg;
				}
				break;
			}
			case word_from_chars('f', '='): {
				if (!pfs_set) {
					fprintf(stderr, "pfs not set!\n");
					exit(1);
				}
#ifdef PVM_DEBUG
				if (wait && input == -1) {
					fprintf(stderr, "wait set, but no input is set!\n");
					exit(1);
				}
#endif
				argv += 6;
				*argv0 = argv;
				num exe_size;
				void *exe_data;
				if (pmf_in_lfs) {
					bm_fd fd = bm_fd_open_ro(argv);
#ifdef PFS_PORTABLE_BUILD
				if (!fd)
#else
					if (fd == -1)
#endif
							{
						fprintf(stderr,
								"could not open primitive machine file: %s\n",
								strerror(errno));
						exit(1);
					}
					exe_size = bm_fd_seek_eof(fd);
					bm_fd_seek(fd, 0);
					exe_data = malloc(exe_size);
					if (!exe_data) {
						fprintf(stderr,
								"could not load the primitive machine file in memory\n");
						exit(1);
					}
					for (num reat = exe_size; reat < exe_size;) {
						num reat_ = bm_fd_read(fd, exe_data + reat,
								exe_size - reat);
						if (reat_ == -1) {
							switch (errno) {
							case EAGAIN:
							case EINTR:
								continue;
							default:
								perror("read");
								fprintf(stderr,
										"failed to read the bin file\n");
								exit(1);
							}
						} else if (!reat_) {
							fprintf(stderr, "the bin file has been modified\n");
							exit(1);
						}
						reat += reat_;
					}
					bm_fd_close(fd);
				} else {
					int fh = pfs_handle_file(argv);
					if (fh == -1) {
						fprintf(stderr,
								"could not open the primitive machine file: %s\n",
								pfs_error());
						exit(1);
					}
					load_exec_in_pfs(fh, &exe_size, &exe_data);
				}
				pvm_init_execute(argv0, argc, exe_data, exe_size);
				return;
			}
			default:
				goto illegal_arg;
			}
			break;
		}
		default:
			goto illegal_arg;
		}
	}
#ifdef PVM_DEBUG
	if (!wait || (input == -1)) {
		fprintf(stderr,
				"no binary set! only allowed if wait and debug input is set!\n");
		exit(1);
	}
	pvm_init_execute(argv0, 0, NULL, -1);
#else
	fprintf(stderr, "no binary set!\n");
	exit(1);
#endif
}

int main(int argc, char **argv) {
	setup(argc, argv);
#ifdef PVM_DEBUG
	if (input != -1) {
		pvm_debug_init(input, input_is_pipe, wait);
	}
#endif
	execute();
}
