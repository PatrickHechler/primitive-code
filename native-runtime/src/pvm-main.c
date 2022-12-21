/*
 * pvm-setup.c
 *
 *  Created on: Oct 26, 2022
 *      Author: pat
 */

#include <bm.h>
#include <pfs.h>
#include <pfs-stream.h>
#include <pfs-constants.h>
#include <pfs-err.h>

#include "pvm-version.h"
#include "pvm-virtual-mashine.h"

#include <string.h>
#include <stdlib.h>
#include <stddef.h>
#include <stdio.h>
#include <errno.h>
#include <stdint.h>
#include <fcntl.h>

static void setup(int argc, char **argv);

static void print_help(void);
static void print_version(void);

#ifdef PVM_DEBUG
_Bool wait;
int input;
_Bool input_is_pipe;
#endif

static void setup(int argc, char **argv) {
#ifdef PVM_DEBUG
	wait = 0;
	input = -1;
#endif
	_Bool pfs_set = 0;
	char *cwd = NULL;
	for (argv++; *argv; argv++, argc--) {
		if (!strcmp("--help", *argv)) {
			print_help();
			exit(1);
		} else if (!strcmp("--version", *argv)) {
			print_version();
			exit(1);
#ifdef PVM_DEBUG
		} else if (!strcmp("--wait", *argv)) {
			wait = 1;
		} else if (!memcmp("--port=", *argv, 7)) {
			if (input != -1) {
				fprintf(stderr, "debug input already set!\n", *argv + 7);
				exit(1);
			}
			char *end;
			long val = strtol(*argv + 7, &end, 0);
			if (errno) {
				perror("strtol");
				fprintf(stderr, "could not parse the port '%s'\n", *argv + 7);
				exit(1);
			}
			if (val > UINT16_MAX || val < 0) {
				fprintf(stderr, "illegal port %ld (MAX=%d MIN=0)", val,
				UINT16_MAX);
				exit(1);
			}
			input_is_pipe = 0;
		} else if (!memcmp("--pipe=", *argv, 7)) {
			if (input != -1) {
				fprintf(stderr, "debug input already set!\n", *argv + 7);
				exit(1);
			}
			input = open(*argv + 7, O_RDONLY);
			if (input == -1) {
				perror("open");
				fprintf(stderr, "could not open the debug input (pipe)!\n",
						*argv + 7);
				exit(1);
			}
			input_is_pipe = 1;
#endif // PVM_DEBUG
		} else if (!memcmp("--cwd=", *argv, 6)) {
			if (cwd) {
				fprintf(stderr, "cwd already set!\n", *argv);
				exit(1);
			}
			if (pfs_set) {
				fprintf(stderr, "set cwd before pfs!\n", *argv);
				exit(1);
			}
			cwd = *argv + 6;
		} else if (!memcmp("--pfs=", *argv, 6)) {
			if (pfs_set) {
				fprintf(stderr, "pfs already set!\n", *argv);
				exit(1);
			}
			int fd = open(*argv + 6, O_RDWR);
			if (fd == -1) {
				perror("open");
				fprintf(stderr, "could not open the PFS!\n", *argv);
				exit(1);
			}
			struct bm_block_manager *bm;
			new_file_bm0(bm, fd,
					fprintf(stderr, "the PFS has an invalid magic start!\n"); exit(1);,
					perror("io"); fprintf(stderr, "could not open the PFS block manager!\n"); exit(1);)
			if (!pfs_load(bm, cwd)) {
				fprintf(stderr, "could not load the PFS (%s)!\n", pfs_error());
				exit(1);
			}
		} else if (!memcmp("--bin=", *argv, 6)) {
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
			(*argv) += 6;
			int fd = open(*argv, O_RDONLY);
			num exe_size = lseek(fd, 0, SEEK_END);
			lseek(fd, 0, SEEK_SET);
			void *exe_data = malloc(exe_size);
			if (!exe_data) {
				fprintf(stderr, "could not load the bin file in memory\n");
				exit(1);
			}
			for (num reat = exe_size; reat < exe_size;) {
				num reat_ = read(fd, exe_data + reat, exe_size - reat);
				if (reat_ == -1) {
					switch (errno) {
					case EAGAIN:
					case EINTR:
						continue;
					default:
						perror("read");
						fprintf(stderr, "failed to read the bin file\n");
						exit(1);
					}
				} else if (!reat_) {
					fprintf(stderr, "the bin file has been modified\n");
					exit(1);
				}
				reat += reat_;
			}
			pvm_init(argv, argc, exe_data, exe_size);
		} else {
			fprintf(stderr, "unknown argument: '%s'\n", *argv);
			exit(1);
		}
	}
#ifdef PVM_DEBUG
	if (!wait || (input == -1)) {
		fprintf(stderr,
				"no binary set! only allowedn if wait and debug input is set!\n");
		exit(1);
	}
	pvm_init(argv, 0, NULL, -1);
#else
	fprintf(stderr, "no binary set!\n");
	exit(1);
#endif
}

static void print_help(void) {
	printf(
#ifdef PVM_DEBUG
			"Usage: db-pvm [Options] --bin=[EXECUTE_FILE] [ARGUMENTS]\n"
#else
			"Usage: pvm [Options] --pmf=[EXECUTE_FILE] [ARGUMENTS]\n"
#endif
			"Options-Rules:\n"
			"  --pfs=[PFS_FILE]\n"
			"    this Option is NOT optional!\n"
			"  --bin=[EXECUTE_FILE]\n"
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
#else
			"    this Option is NOT optional!\n"
#endif
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
#endif
			"  --pfs=[PFS_FILE]\n"
			"    set the file which contains the Patr-File-System\n"
			"  --cwd=[FOLDER]\n"
			"    set the current working directory for the program\n"
			"  --bin=[EXECUTE_FILE]\n"
			"    set the file to executed.\n"
			"    all arguments after this option will be passed to the\n"
			"    program as program arguments.\n"
			"    the first argument will be [EXECUTE_FILE], the following\n"
			"    program arguments will be the following arguments.\n"
#ifdef PVM_DEBUG
			"    it is recommended to specify this option even when\n"
			"    --wait is specified.\n"
#endif
			"");
}

static void print_version(void) {
	printf(
#ifdef PVM_DEBUG
			"debug-"
#endif
					"primitive-virtual-mashine " PVM_VERSION_STR "\n");
}

int main(int argc, char **argv) {
	setup(argc, argv);
	execute();
}
