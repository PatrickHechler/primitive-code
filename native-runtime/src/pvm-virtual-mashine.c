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
 * pvm-virtual-mashine.c
 *
 *  Created on: Jul 6, 2022
 *      Author: pat
 */
#define PVM

#include <pfs.h>
#include <pfs-constants.h>
#include <pfs-stream.h>
#include <pfs-iter.h>
#include <pfs-element.h>
#include <pfs-folder.h>
#include <pfs-file.h>
#include <pfs-pipe.h>
#include <pfs-err.h>

#include "pvm-virtual-mashine.h"
#include "pvm-err.h"

#include "pvm-int.h"
#include "pvm-cmd.h"

#include <string.h>
#include <stdint.h>
#include <math.h>
#include <time.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>
#include <ctype.h>
#include <iconv.h>
#ifdef PVM_DEBUG
#include "pvm-version.h"
// not really usable here: #include <readline/readline.h>
#include <pthread.h>
#include <signal.h>
#include <sys/socket.h>
#include <netinet/ip.h>
#include <sys/un.h>
#include <linux/membarrier.h>
#include <sys/syscall.h>
#include <sys/wait.h>
#endif // PVM_DEBUG

#ifdef PVM_DEBUG
static FILE *old_stderr;
#endif

static int param_param_type_index;
static int param_byte_value_index;
static int param_num_value_index;

static union instruction_adres {
	void *pntr;
	num *np;
	byte *bp;
	word *wp;
} ia;
static num remain_instruct_space;

/* terminates the program with 126 when the PFS could not be closed and status is zero */
static void close_pfs_on_exit(int status, void *ignore) {
	if (!pfs_close() && !status) {
		fflush(NULL);
		sync();
		_exit(126);
	}
}

void pvm_init(char **argv, num argc, void *exe, num exe_size) {
	if (next_adress != REGISTER_START) {
		abort();
	}

	pfs_err_loc = (ui32*) &pvm.err;

	if (pfs_stream_open_delegate(STDIN_FILENO, PFS_SO_PIPE | PFS_SO_READ)
			!= 0) {
		abort();
	}
	if (pfs_stream_open_delegate(STDOUT_FILENO, PFS_SO_PIPE | PFS_SO_APPEND)
			!= 1) {
		abort();
	}
	if (pfs_stream_open_delegate(STDERR_FILENO, PFS_SO_PIPE | PFS_SO_APPEND)
			!= 2) {
		abort();
	}

	struct memory *pvm_mem = alloc_memory2(&pvm, sizeof(pvm),
	/*		*/MEM_NO_FREE | MEM_NO_RESIZE);
	if (!pvm_mem) {
		abort();
	}
	memset(&pvm, 0, sizeof(pvm));

	struct memory2 stack_mem = alloc_memory(256,
	/*		*/MEM_AUTO_GROW | (8 << MEM_AUTO_GROW_SHIFT));
	if (!stack_mem.adr) {
		abort();
	}
	stack_mem.mem->grow_size = 256;
	stack_mem.mem->change_pntr = &pvm.sp;
	pvm.sp = stack_mem.mem->start;

	struct memory2 int_mem = alloc_memory(INTERRUPT_COUNT << 3, 0U);
	if (!int_mem.mem) {
		abort();
	}
	memset(int_mem.adr, 0xFF, INTERRUPT_COUNT << 3);
	pvm.intp = int_mem.mem->start;
	pvm.intcnt = INTERRUPT_COUNT;

	if (exe) {
		// use different flags in future version?
		struct memory *exe_mem = alloc_memory2(exe, exe_size, 0U);
		if (!exe_mem) {
			abort();
		}
		pvm.ip = exe_mem->start;
	}

	struct memory *args_mem = alloc_memory2(argv, (argc + 1) * sizeof(char*),
	MEM_NO_FREE | MEM_NO_RESIZE);
	if (!args_mem) {
		abort();
	}
	pvm.x[0] = argc;
	pvm.x[1] = args_mem->start;
	for (; argc; argv++, argc--) {
		num len = strlen(*argv) + 1;
		struct memory *arg_mem = alloc_memory2(*argv, len,
		MEM_NO_FREE | MEM_NO_RESIZE);
		if (!arg_mem) {
			abort();
		}
		*(num*) argv = arg_mem->start;
	}
	*(num*) argv = -1;

	on_exit(close_pfs_on_exit, NULL);
}

#ifdef PVM_DEBUG

static pthread_mutex_t debug_mutex;

static inline void pvm_lock() {
	if (pthread_mutex_lock(&debug_mutex) == -1) {
		fprintf(old_stderr, "could not lock the debug mutex: %s\n",
				strerror(errno));
		exit(1);
	}
	if (syscall(SYS_membarrier, MEMBARRIER_CMD_PRIVATE_EXPEDITED, 0U, 0)
			== -1) {
		fprintf(old_stderr, "membarrier failed: %s\n", strerror(errno));
		exit(1);
	}
}

static inline void pvm_unlock() {
	if (pthread_mutex_unlock(&debug_mutex) == -1) {
		fprintf(old_stderr, "could not unlock the debug mutex: %s\n",
				strerror(errno));
		exit(1);
	}
}

static inline void wait5ms() {
	struct timespec wait_time = { //
			/*	  */.tv_sec = 0, // 0 sec
					.tv_nsec = 5000000 // 5 ms
			};
	nanosleep(&wait_time, NULL);
}

static int pvm_same_address(const void *a, const void *b) {
	return a == b;
}
static unsigned int pvm_address_hash(const void *a) {
	return (unsigned) (long) a;
}

struct pvm_delegate_arg {
	int srcfd;
	struct hashset *dst_fds;
};

static struct hashset delegate_set_stdout = { //
		/*	  */.entries = NULL, //
				.entrycount = 0, //
				.setsize = 0, //
				.equalizer = pvm_same_address, //
				.hashmaker = pvm_address_hash, //
		};

static struct hashset delegate_set_stderr = { //
		/*	  */.entries = NULL, //
				.entrycount = 0, //
				.setsize = 0, //
				.equalizer = pvm_same_address, //
				.hashmaker = pvm_address_hash, //
		};

static void* pvm_delegate_func(void *_arg) {
	struct pvm_delegate_arg arg = *(struct pvm_delegate_arg*) _arg;
	free(_arg);
	void *buffer = malloc(128);
	if (!buffer) {
		fprintf(old_stderr, "could not allocate delegate buffer\n");
		exit(1);
	}
	while (1) {
		ssize_t reat = read(arg.srcfd, buffer, 128);
		if (reat == -1) {
			switch (errno) {
			case EAGAIN:
				wait5ms();
				/* no break */
			case EINTR:
				errno = 0;
				continue;
			}
			fprintf(old_stderr, "error on read: %s\n", strerror(errno));
			exit(1);
		} else if (read == 0) {
			return NULL;
		}
		pvm_lock();
		for (int i = arg.dst_fds->setsize; i;) {
			if (!arg.dst_fds->entries[i]
					|| arg.dst_fds->entries[i] == &illegal) {
				continue;
			}
			int dstfd = ((int) (arg.dst_fds->entries[i] - NULL)) - 1;
			for (ssize_t wrote = 0; wrote < reat;) {
				ssize_t w = write(dstfd, buffer + wrote, reat - wrote);
				if (w == -1) {
					switch (errno) {
					case EAGAIN:
					case EINTR:
						errno = 0;
						continue;
					}
					fprintf(old_stderr, "error on write: %s\n",
							strerror(errno));
					goto big_break;
				}
				wrote += w;
			}
			big_break: ;
		}
		pvm_unlock();
	}
}

static inline void set_fd_flag(int fd, char *name, int flags, _Bool set_flag) {
	int cur_flags = fcntl(fd, F_GETFD);
	if (cur_flags == -1) {
		fprintf(old_stderr, "could not get the fd flags: %s\n",
				strerror(errno));
		exit(1);
	}
	if (set_flag && (cur_flags & flags) != flags) {
		if (fcntl(fd, F_SETFD, cur_flags | flags) == -1) {
			fprintf(old_stderr, "could not set the %s fd flag: %s\n", name,
					strerror(errno));
			exit(1);
		}
	} else if (!set_flag && (cur_flags & flags) != 0) {
		if (fcntl(fd, F_SETFD, cur_flags & ~flags) == -1) {
			fprintf(old_stderr, "could not clear the %s flag: %s\n", name,
					strerror(errno));
			exit(1);
		}
	}
}

static void* pvm_debug_thread_func(void *_arg);

#define DEBUG_SOCKET_BACKLOG 1

struct sok_data {
	FILE *read;
	FILE *write;
	pthread_t thread;
	int fd;
};

static void close_sock_data_on_exit(int status, void *arg) {
	struct sok_data *sd = arg;
	if (sd->thread != -1) {
		pthread_kill(sd->thread, SIGKILL);
	}
	close(sd->fd);
	if (sd->read) {
		fclose(sd->read);
	}
	if (sd->write) {
		fclose(sd->write);
	}
}

static void close_server_sok_on_exit(int status, void *arg) {
	int server_sok = *(int*) arg;
	close(server_sok);
}

static void* pvm_debug_thread_deamon(void *_arg) {
	int arg = *(int*) _arg;
	free(_arg);
	int domain;
	union {
		struct sockaddr sa;
		struct sockaddr_in sa_in;
	} my_sock_adr;
	my_sock_adr.sa_in = (struct sockaddr_in ) { //
			/*	  */.sin_family = AF_INET, //
					.sin_port = htons(arg), //
					.sin_addr.s_addr = INADDR_ANY, //
			};
	int server_sok = socket(AF_INET, SOCK_STREAM | SOCK_CLOEXEC, 0);
	if (server_sok == -1) {
		fprintf(old_stderr, "could not create my socket: %s\n",
				strerror(errno));
		exit(1);
	}
	if (bind(server_sok, &my_sock_adr.sa, sizeof(struct sockaddr_in)) == -1) {
		fprintf(old_stderr, "could not bind my socket: %s\n", strerror(errno));
		exit(1);
	}
	if (listen(server_sok, DEBUG_SOCKET_BACKLOG) == -1) {
		fprintf(old_stderr, "could not open my socket for listening: %s\n",
				strerror(errno));
		exit(1);
	}
	on_exit(close_server_sok_on_exit, &server_sok);
	while (1) {
		int sok = accept4(server_sok, NULL, NULL, SOCK_CLOEXEC);
		if (sok == -1) {
			fprintf(old_stderr, "could not accept a debug connection: %s\n",
					strerror(errno));
			exit(1);
		}

		struct sok_data *child_arg = malloc(sizeof(struct sok_data));
		if (!child_arg) {
			fprintf(old_stderr,
					"could not allocate the argument for the thread\n");
			close(sok);
			exit(1);
		}

		child_arg->fd = sok;
		child_arg->read = NULL;
		child_arg->write = NULL;
		child_arg->thread = -1;

		on_exit(close_sock_data_on_exit, child_arg);

		pthread_create(&child_arg->thread, NULL, pvm_debug_thread_func,
				child_arg);
	}
}

struct debug_cmd {
	const char *name;
	void (*func)(struct sok_data *sd, char *buffer);
};

static int debug_cmds_equal(const void *_a, const void *_b) {
	const struct debug_cmd *a = _a, *b = _b;
	return strcmp(a->name, b->name) == 0;
}

static unsigned debug_cmds_hash(const void *_a) {
	const struct debug_cmd *a = _a;
	const unsigned char *cs = a->name;
	unsigned res = 0;
	while (1) {
		if (*cs == '\0')
			break;
		res ^= *(cs++);
		if (*cs == '\0')
			break;
		res ^= (*(cs++)) << 8;
		if (*cs == '\0')
			break;
		res ^= (*(cs++)) << 16;
		if (*cs == '\0')
			break;
		res ^= (*(cs++)) << 24;
	}
	return res;
}

static struct hashset debug_commands = { //
		/*	  */.entrycount = 0, //
				.setsize = 0, //
				.equalizer = debug_cmds_equal, //
				.hashmaker = debug_cmds_hash, //
				.entries = NULL, //
		};

static void pvm_dbcmd_help(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_version(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_detach(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_exit(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_state(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_wait(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_run(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_step_in(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_step(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_step_out(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_step_dep(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_break(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_pvm(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_regs(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_mem(struct sok_data *sd, char *buffer);
static void pvm_dbcmd_disasm(struct sok_data *sd, char *buffer);

#define set_debug_cmd(name) set_debug_cmd0(name, #name)
#define set_debug_cmd0(name0, str) \
		dc = malloc(sizeof(struct debug_cmd)); \
		if (!dc) { \
			abort(); \
		} \
		dc->name = str; \
		dc->func = pvm_dbcmd_##name0; \
		hashset_put(&debug_commands, debug_cmds_hash(dc), dc);
static inline void init_debug_cmds_set() {
	struct debug_cmd *dc;
	set_debug_cmd(help)
	set_debug_cmd0(help, "h")
	set_debug_cmd(version)
	set_debug_cmd(detach)
	set_debug_cmd(exit)
	set_debug_cmd(state)
	set_debug_cmd(wait)
	set_debug_cmd(run)
	set_debug_cmd0(run, "r")
	set_debug_cmd0(step_in, "step-in")
	set_debug_cmd0(step_in, "si")
	set_debug_cmd(step)
	set_debug_cmd0(step, "s")
	set_debug_cmd0(step_out, "step-out")
	set_debug_cmd0(step_out, "so")
	set_debug_cmd0(step_dep, "step-dep")
	set_debug_cmd(break)
	set_debug_cmd0(break, "b")
	set_debug_cmd(pvm)
	set_debug_cmd(regs)
	set_debug_cmd(mem)
	set_debug_cmd(disasm)
}
#undef set_debug_cmd0
#undef set_debug_cmd

static inline void create_pipes(int stdin_pipe[2], int stdout_pipe[2],
		int stderr_pipe[2]) {
	if (pipe2(stdin_pipe, O_NONBLOCK | O_CLOEXEC) == -1) {
		fprintf(old_stderr, "could not open a debug pipe!: %s\n",
				strerror(errno));
		exit(1);
	}
	if (pipe2(stdout_pipe, O_NONBLOCK | O_CLOEXEC) == -1) {
		fprintf(old_stderr, "could not open a debug pipe!: %s\n",
				strerror(errno));
		exit(1);
	}
	if (pipe2(stderr_pipe, O_NONBLOCK | O_CLOEXEC) == -1) {
		fprintf(old_stderr, "could not open a debug pipe!: %s\n",
				strerror(errno));
		exit(1);
	}
}

static inline void overwrite_std(int new_stdin, int new_stdout, int new_stderr) {
	// std streams are overwritten between fork and exec
	if (dup2(new_stdin, STDIN_FILENO) == -1) {
		fprintf(old_stderr, "could not set stdin!: %s\n", strerror(errno));
		exit(1);
	}
	if (dup2(new_stdout, STDOUT_FILENO) == -1) {
		fprintf(old_stderr, "could not set stdout!: %s\n", strerror(errno));
		exit(1);
	}
	if (dup2(new_stderr, STDERR_FILENO) == -1) {
		fprintf(old_stderr, "could not set stderr!: %s\n", strerror(errno));
		exit(1);
	}
	set_fd_flag(STDIN_FILENO, "NONBLOCK", O_NONBLOCK, 1);
	set_fd_flag(STDOUT_FILENO, "NONBLOCK", O_NONBLOCK, 1);
	set_fd_flag(STDERR_FILENO, "NONBLOCK", O_NONBLOCK, 1);
}

//static void at_fork_child(void) {
//	pthread_mutex_destroy(&debug_mutex);
//}

static inline void init_syncronisation() {
	pthread_mutexattr_t attr;
	pthread_mutexattr_init(&attr);
	pthread_mutex_init(&debug_mutex, &attr);
	pthread_mutexattr_destroy(&attr);
//	pthread_atfork(NULL, NULL, at_fork_child);

	if (syscall(SYS_membarrier, MEMBARRIER_CMD_REGISTER_PRIVATE_EXPEDITED, 0U,
			0) == -1) {
		fprintf(old_stderr,
				"could not register myself for the memory barrier syscall: %s\n",
				strerror(errno));
		exit(1);
	}
}

static void (*do_calls[CALL_COMMANDS_COUNT])();
static void (*do_returns[RETURN_COMMANDS_COUNT])();

static void call_command_wrapper() {
	if (pvm_next_state == pvm_ds_stepping) {
		pvm_depth++;
	}
	do_calls[(*ia.wp) - CALL_COMMANDS_START]();
}

static void return_command_wrapper() {
	if (pvm_next_state == pvm_ds_stepping) {
		pvm_depth--;
	}
	do_returns[(*ia.wp) - RETURN_COMMANDS_START]();
}

static inline void init_debug_cmds() {
	for (int i = CALL_COMMANDS_COUNT-1; i--;) {
		do_calls[i] = cmds[CALL_COMMANDS_START + i];
		cmds[CALL_COMMANDS_START + i] = call_command_wrapper;
	}
	for (int i = RETURN_COMMANDS_COUNT-1; i --;) {
		do_returns[i] = cmds[RETURN_COMMANDS_START + i];
		cmds[RETURN_COMMANDS_START + i] = return_command_wrapper;
	}
}

void pvm_debug_init(int input, _Bool input_is_pipe, _Bool wait) {
	old_stderr = stderr;
	int stdin_dup = dup(STDIN_FILENO);
	int stdout_dup = dup(STDOUT_FILENO);
	int stderr_dup = dup(STDERR_FILENO);
	switch (input) {
	case STDIN_FILENO:
		input = stdin_dup;
		break;
	case STDOUT_FILENO:
		input = stdout_dup;
		break;
	case STDERR_FILENO:
		input = stderr_dup;
		break;
	}
	// save to do this not atomic, because here the programm is still single threaded
	set_fd_flag(stdin_dup, "NONBLOCK | CLOEXEC", O_NONBLOCK | O_CLOEXEC, 1);
	set_fd_flag(stdout_dup, "NONBLOCK | CLOEXEC", O_NONBLOCK | O_CLOEXEC, 1);
	set_fd_flag(stderr_dup, "NONBLOCK | CLOEXEC", O_NONBLOCK | O_CLOEXEC, 1);
	int stderr_dup2 = dup(STDERR_FILENO);
	set_fd_flag(stderr_dup2, "NONBLOCK | CLOEXEC", O_NONBLOCK | O_CLOEXEC, 1);
	old_stderr = fdopen(stderr_dup2, "w");

	int stdin_pipe[2];
	int stdout_pipe[2];
	int stderr_pipe[2];
	create_pipes(stdin_pipe, stdout_pipe, stderr_pipe);

	overwrite_std(stdin_pipe[0], stdout_pipe[1], stderr_pipe[1]);

	init_syncronisation();
	pvm_lock();

	init_debug_cmds();

	init_debug_cmds_set();

	pvm_state = pvm_ds_init;
	if (wait) {
		pvm_next_state = pvm_ds_waiting;
	} else {
		pvm_next_state = pvm_ds_running;
	}

	pthread_t some_other_thread;
	int *deamon_arg = malloc(sizeof(int));
	if (!deamon_arg) {
		fprintf(old_stderr, "could not allocate the argument for the thread\n");
		exit(1);
	}
	*deamon_arg = input; // debug deamon/thread
	pthread_create(&some_other_thread, NULL,
			input_is_pipe ? pvm_debug_thread_func : pvm_debug_thread_deamon,
			deamon_arg);

	struct pvm_delegate_arg *delegate_arg = malloc(
			sizeof(struct pvm_delegate_arg));
	if (!delegate_arg) {
		fprintf(old_stderr, "could not allocate the argument for the thread\n");
		exit(1);
	}
	delegate_arg->srcfd = stdout_dup; // stdout delegate
	pthread_create(&some_other_thread, NULL, pvm_delegate_func, delegate_arg);

	delegate_arg = malloc(sizeof(struct pvm_delegate_arg));
	if (!delegate_arg) {
		fprintf(old_stderr, "could not allocate the argument for the thread\n");
		exit(1);
	}
	delegate_arg->srcfd = stderr_dup; // stderr delegate
	pthread_create(&some_other_thread, NULL, pvm_delegate_func, delegate_arg);
}
#endif // PVM_DEBUG

static inline void int_init() {
	struct memory2 mem = alloc_memory(128, MEM_INT | MEM_NO_RESIZE);
	if (!mem.mem) {
		exit(127);
	}
	memcpy(mem.adr, &pvm, 128);
	pvm.x[0x09] = mem.mem->start;
}

struct memory_check {
	struct memory *mem;
	_Bool changed;
#ifdef PVM_DEBUG
	_Bool valid;
#endif // PVM_DEBUG
};

#ifdef PVM_DEBUG
static inline struct memory_check chk0(num pntr, num size, _Bool use_valid);
#else
static inline struct memory_check chk(num pntr, num size);
#endif // PVM_DEBUG

#ifdef PVM_DEBUG
#define chk(pntr, size) chk0(pntr, size, 0)
#endif // PVM_DEBUG

static inline void interrupt(num intnum, num incIPVal) {
#ifdef PVM_DEBUG
#	define callInt ints[intnum](intnum);
#else // PVM_DEBUG
#	define callInt ints[intnum]();
#endif // PVM_DEBUG
	static _Bool in_illegal_mem = 0;
	if (intnum != INT_ERROR_ILLEGAL_MEMORY) {
		in_illegal_mem = 0;
	} else {
		if (in_illegal_mem) {
			exit(127);
		}
		in_illegal_mem = 1;
	}
	if (pvm.intcnt < intnum || intnum < 0) {
		if (pvm.intcnt <= INT_ERROR_ILLEGAL_INTERRUPT) {
			exit(128);
		}
		if (pvm.intp == -1) {
			pvm.x[0] = intnum;
			callInt;
		} else {
			num adr = pvm.intp + (INT_ERROR_ILLEGAL_INTERRUPT << 3);
			struct memory *mem = chk(adr, 8).mem;
			if (!mem) {
				in_illegal_mem = 0;
				return;
			}
			num deref = *(num*) (mem->offset + adr);
			if (-1 == deref) {
				callInt;
			} else {
				int_init();
				pvm.x[0] = intnum;
				pvm.ip = deref;
			}
		}
	} else if (pvm.intp == -1) {
		if (incIPVal) {pvm.ip += incIPVal;}
		if (intnum >= INTERRUPT_COUNT) {
			pvm.x[0] = intnum;
			intnum = INT_ERROR_ILLEGAL_INTERRUPT;
		}
		callInt;
	} else {
		num adr = pvm.intp + (intnum << 3);
		struct memory *mem = chk(adr, 8).mem;
		if (!mem) {
			in_illegal_mem = 0;
			return;
		}
		if (incIPVal) {pvm.ip += incIPVal;}
		num deref = *(num*) (mem->offset + adr);
		if (-1 == deref) {
			callInt;
		} else {
			int_init();
			pvm.ip = deref;
		}
	}
	in_illegal_mem = 0;
#undef callInt
}

#ifdef PVM_DEBUG
#undef chk
static inline struct memory_check chk0(num pntr, num size, _Bool use_valid) {
#else
static inline struct memory_check chk(num pntr, num size) {
#endif //  PVM_DEBUG
	for (struct memory *m = memory; m < memory + mem_size; m++) {
		if (m->start > pntr) {
#ifdef PVM_DEBUG
			if (use_valid) {
				struct memory_check result;
				result.mem = NULL;
				result.valid = 0;
				return result;
			}
#endif // PVM_DEBUG
			if (m != memory) {
				m--;
				while (m->start == -1) {
					if (m == memory) {
						interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
						struct memory_check result;
						result.mem = NULL;
						return result;
					}
					m--;
				}
				check_grow: if (m->flags & MEM_AUTO_GROW) {
					num auto_grow_end = m->end
							+ ((m->flags & MEM_AUTO_GROW_BITS)
									>> MEM_AUTO_GROW_SHIFT);
					if (auto_grow_end < 0) {
						abort(); // num overflow
					}
					if (pntr < auto_grow_end) {
						num grow_size = (size / m->grow_size) + m->grow_size;
						num new_size = m->end - m->start + grow_size;
						num old_start = m->start;
						struct memory *new_mem = realloc_memory(m->start,
								new_size, 1);
						if (new_mem) {
							struct memory_check result;
							result.changed = new_mem->start != old_start;
							result.mem = new_mem;
#ifdef PVM_DEBUG
							result.valid = 1;
#endif
							return result;
						}
					}
				}
			}
			interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
			struct memory_check result;
			result.mem = NULL;
#ifdef PVM_DEBUG
			result.valid = 0;
#endif
			return result;
		} else if (m->end <= pntr) {
			continue;
		} else if (m->end - size < pntr) {
#ifdef PVM_DEBUG
			if (use_valid) {
				struct memory_check result;
				result.mem = NULL;
				result.valid = 0;
				return result;
			}
#endif
			goto check_grow;
		}
		struct memory_check result;
		result.mem = m;
		result.changed = 0;
#ifdef PVM_DEBUG
		result.valid = 1;
#endif
		return result;
	}
#ifdef PVM_DEBUG
#endif // PVM_DEBUG
	interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
	struct memory_check result;
	result.mem = NULL;
	return result;
}

#ifdef PVM_DEBUG
#define chk(pntr, size) chk0(pntr, size, 0)
#endif // PVM_DEBUG

union param {
	void *pntr;
	__int128 *bigp;
	fpnum fpn;
	fpnum *fpnp;
	unum u;
	unum *up;
	num n;
	num *np;
	double_word dw;
	double_word *dwp;
	word w;
	word *wp;
	byte b;
	byte *bp;
};

struct p {
	union param p;
	_Bool valid;
	_Bool changed;
};

//#define get_param(name, pntr) \
//	union param name; \
//	{ \
//		struct p _p = param(pntr); \
//		if (!p.valid) { \
//			return; \
//		} \
//		name = p.p; \
//	}

static inline struct p param(int pntr, num size) {
#define paramFail r.valid = 0; return r;
	struct p r;
	r.valid = 1;
	r.changed = 0;
	switch (ia.bp[param_param_type_index++]) {
	case P_NUM:
		if (pntr) {
			paramFail
		} else if (remain_instruct_space
				<= size + ((param_num_value_index - 1) << 3)) {
			interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
			paramFail
		} else if (size == 8) {
			r.p.n = ia.np[param_num_value_index++];
		} else if (size == 4) {
			r.p.dw = *(double_word*) (ia.np + param_num_value_index++);
		} else if (size == 2) {
			r.p.w = *(word*) (ia.np + param_num_value_index++);
		} else if (size == 1) {
			r.p.b = *(byte*) (ia.np + param_num_value_index++);
		} else {
			abort();
		}
		break;
	case P_REG:
		if (pntr) {
			r.p.pntr = &pvm.regs[ia.bp[param_byte_value_index--]];
			if (size > 8) {
				if ((256 - ia.bp[param_byte_value_index + 1])
						> ((size + 7) >> 3)) {
					interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
					paramFail
				}
			}
		} else {
			r.p.n = pvm.regs[ia.bp[param_byte_value_index--]];
		}
		break;
	case P_NUM_NUM: {
		if (remain_instruct_space
				<= size + ((param_num_value_index - 1) << 3)) {
			interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
			paramFail
		}
		num adr = ia.np[param_num_value_index]
				+ ia.np[param_num_value_index + 1];
		struct memory_check mem = chk(adr, size);
		if (!mem.mem) {
			paramFail
		}
		param_num_value_index += 2;
		if (pntr) {
			r.p.pntr = mem.mem->offset + adr;
			r.changed = 1;
		} else if (size == 8) {
			r.p.n = *(num*) (mem.mem->offset + adr);
		} else if (size == 4) {
			r.p.dw = *(double_word*) (mem.mem->offset + adr);
		} else if (size == 2) {
			r.p.w = *(word*) (mem.mem->offset + adr);
		} else if (size == 1) {
			r.p.b = *(byte*) (mem.mem->offset + adr);
		} else {
			abort();
		}
		break;
	}
	case P_REG_NUM:
	case P_NUM_REG: {
		if (remain_instruct_space
				<= size + ((param_num_value_index - 1) << 3)) {
			interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
			paramFail
		}
		num adr = ia.np[param_num_value_index++]
				+ pvm.regs[ia.bp[param_byte_value_index--]];
		struct memory_check mem = chk(adr, size);
		if (!mem.mem) {
			paramFail
		}
		if (pntr) {
			r.p.pntr = mem.mem->offset + adr;
			r.changed = 1;
		} else if (size == 8) {
			r.p.n = *(num*) (mem.mem->offset + adr);
		} else if (size == 4) {
			r.p.dw = *(double_word*) (mem.mem->offset + adr);
		} else if (size == 2) {
			r.p.w = *(word*) (mem.mem->offset + adr);
		} else if (size == 1) {
			r.p.b = *(byte*) (mem.mem->offset + adr);
		} else {
			abort();
		}
		break;
	}
	case P_REG_REG: {
		num adr = pvm.regs[ia.bp[param_byte_value_index]]
				+ pvm.regs[ia.bp[param_byte_value_index - 1]];
		struct memory_check mem = chk(adr, size);
		if (!mem.mem) {
			paramFail
		}
		param_byte_value_index -= 1;
		if (pntr) {
			r.p.np = mem.mem->offset + adr;
			r.changed = 1;
		} else if (size == 8) {
			r.p.n = *(num*) (mem.mem->offset + adr);
		} else if (size == 4) {
			r.p.dw = *(double_word*) (mem.mem->offset + adr);
		} else if (size == 2) {
			r.p.w = *(word*) (mem.mem->offset + adr);
		} else if (size == 1) {
			r.p.b = *(byte*) (mem.mem->offset + adr);
		} else {
			abort();
		}
		break;
	}
	case P_NUM_ADR: {
		if (remain_instruct_space
				<= size + ((param_num_value_index - 1) << 3)) {
			interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
			paramFail
		}
		num adr = ia.np[param_num_value_index++];
		struct memory_check mem = chk(adr, size);
		if (!mem.mem) {
			paramFail
		}
		if (pntr) {
			r.p.pntr = mem.mem->offset + adr;
			r.changed = 1;
		} else if (size == 8) {
			r.p.n = *(num*) (mem.mem->offset + adr);
		} else if (size == 4) {
			r.p.dw = *(double_word*) (mem.mem->offset + adr);
		} else if (size == 2) {
			r.p.w = *(word*) (mem.mem->offset + adr);
		} else if (size == 1) {
			r.p.b = *(byte*) (mem.mem->offset + adr);
		} else {
			abort();
		}
		break;
	}
	case P_REG_ADR: {
		num adr = pvm.regs[ia.bp[param_byte_value_index]];
		struct memory_check mem = chk(adr, size);
		if (!mem.mem) {
			paramFail
		}
		param_byte_value_index -= 1;
		if (pntr) {
			r.p.np = mem.mem->offset + adr;
			r.changed = 1;
		} else if (size == 8) {
			r.p.n = *(num*) (mem.mem->offset + adr);
		} else if (size == 4) {
			r.p.dw = *(double_word*) (mem.mem->offset + adr);
		} else if (size == 2) {
			r.p.w = *(word*) (mem.mem->offset + adr);
		} else if (size == 1) {
			r.p.b = *(byte*) (mem.mem->offset + adr);
		} else {
			abort();
		}
		break;
	}
	default:
		r.valid = 0;
		interrupt(INT_ERROR_UNKNOWN_COMMAND, 0);
	}
	return r;
#undef paramFail
}

static inline void exec_cmd() {
	struct memory_check ipmem = chk(pvm.ip, 8);
	if (!ipmem.mem) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	remain_instruct_space = ipmem.mem->end - pvm.ip;
	if (remain_instruct_space < 8) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	ia.pntr = ipmem.mem->offset + pvm.ip;
	param_param_type_index = 2;
	param_byte_value_index = 7;
	param_num_value_index = 1;
	cmds[*ia.wp]();
}

PVM_SI_PREFIX struct memory* alloc_memory2(void *adr, num size, unsigned flags) {
	if (mem_size && memory[mem_size - 1].start == -1) {
		for (num index = mem_size; index;) {
			if (memory[--index].start == -1) {
				continue;
			}
			index++;
			memory[index].start = next_adress;
			memory[index].end = memory[index].start + size;
			memory[index].offset = adr - memory[index].start;
			memory[index].flags = flags;
			next_adress = memory[index].end + ADRESS_HOLE_DEFAULT_SIZE;
			if (memory[index].end < 0) {
				// overflow
				abort();
			}
			return memory + index;
		}
	}
	num oms = mem_size;
	mem_size += 16;
	memory = realloc(memory, mem_size * sizeof(struct memory));
	memset(memory + oms + 1, -1, 15 * sizeof(struct memory));
	memory[oms].start = next_adress;
	memory[oms].end = memory[oms].start + size;
	memory[oms].offset = adr - memory[oms].start;
	memory[oms].flags = flags;
	/*
	 //	if (memory[oms].start < 0 || memory[oms].end < 0) {
	 * whould be the better check
	 * I know that the current check can fail when size is near 2^63
	 */
	if (memory[oms].end < 0) {
		// overflow
		abort();
	}
	next_adress = memory[oms].end + ADRESS_HOLE_DEFAULT_SIZE;
	return memory + oms;
}
PVM_SI_PREFIX struct memory2 alloc_memory(num size, unsigned flags) {
	struct memory2 r;
	void *mem = malloc(size);
	if (!mem) {
		r.mem = NULL;
		r.adr = NULL;
		pvm.err = PE_OUT_OF_MEMORY;
		return r;
	}
	r.mem = alloc_memory2(mem, size, flags);
	if (r.mem) {
		r.adr = mem;
	} else {
		free(mem);
		r.adr = NULL;
	}
	return r;
}
PVM_SI_PREFIX struct memory* realloc_memory(num adr, num newsize,
		_Bool auto_growing) {
	struct memory_check mem_chk = chk(adr, 0);
	struct memory *mem = mem_chk.mem;
	if (!mem) {
		return NULL;
	}
	if ((adr & MEM_NO_RESIZE) || ((!auto_growing) && (mem->start != adr))) {
		if (auto_growing) {
			abort();
		}
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return NULL;
	}
	void *new_pntr = realloc(mem->offset + mem->start, newsize);
	if (!new_pntr) {
		return NULL;
	}
	num oldsize = mem->end - mem->start;
	if (newsize < oldsize) {
		mem->end = mem->start + newsize;
		mem->offset = new_pntr - mem->start;
		return mem;
	}
	num index = mem - memory;
	if (index + 1 <= mem_size) {
		for (int i = index + 1; i < mem_size; i++) {
			if (memory[i].start == -1) {
				continue;
			}
			num maxsize = memory[i].start - mem->start
					- ADRESS_HOLE_MINIMUM_SIZE;
			if (maxsize >= newsize) {
				mem->end = mem->start + newsize;
				mem->offset = new_pntr - mem->start;
				return mem;
			}
			// index can not be zero, because all addresses are above the PVM address, which is not changeable
			for (int ii = index - 1; 1; ii--) {
				if (memory[ii].start == -1) {
					continue;
				}
				maxsize = memory[i].start - memory[ii].end
						- (ADRESS_HOLE_MINIMUM_SIZE << 1);
				if (maxsize >= newsize) {
					num new_start = (maxsize - newsize) >> 1;
					if (auto_growing) {
						mem->change_pntr += new_start - mem->start;
					}
					mem->start = new_start;
					mem->end = new_start + newsize;
					mem->offset = new_pntr - new_start;
					return mem;
				}
				struct memory *res = alloc_memory2(new_pntr, newsize,
						mem->flags);
				if (auto_growing) {
					mem->change_pntr += res->start - mem->start;
				}
				memset(mem, 0xFF, sizeof(struct memory));
				return res;
			}
		}
		goto no_next_adr;
	} else {
		no_next_adr: ;
		mem->end = mem->start + newsize;
		if (mem->end < 0) {
			// overflow
			abort();
		}
		next_adress = mem->end + ADRESS_HOLE_DEFAULT_SIZE;
		mem->offset = new_pntr - mem->start;
		return mem;
	}
}
static inline void free_mem_impl(struct memory *mem) {
	free(mem->offset + mem->start);
	memset(mem, 0xFF, sizeof(struct memory));
}

PVM_SI_PREFIX void free_memory(num adr) {
	struct memory *mem = chk(adr, 0).mem;
	if (!mem) {
		return;
	}
	if ((mem->start != adr) || (mem->flags & MEM_NO_FREE)) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	free_mem_impl(mem);
}

#ifdef PVM_DEBUG

static inline char to_hex(int val) {
	if (val < 10) {
		return '0' + val;
	} else {
		return 'A' - 10 + val;
	}
}

static inline void print_pvm(FILE *file_write, int end) {
	pvm_lock();
	if (pvm_state != pvm_ds_waiting) {
		pvm_unlock();
		fprintf(file_write,
				"I can not display the pvm, the pvm is not waiting!\n");
		return;
	}
	struct pvm pvm_copy = pvm;
	pvm_unlock();
	fprintf(file_write, "PVM:\n");
	if (end <= 0) {
		return;
	}
	fprintf(file_write, "  IP:     UHEX-%lX : %ld\n", pvm_copy.ip, pvm_copy.ip);
	if (end == 1) {
		return;
	}
	fprintf(file_write, "  SP:     UHEX-%lX : %ld\n", pvm_copy.sp, pvm_copy.sp);
	if (end == 2) {
		return;
	}
	fprintf(file_write, "  INTP:   UHEX-%lX : %ld\n", pvm_copy.intp,
			pvm_copy.intp);
	if (end == 3) {
		return;
	}
	fprintf(file_write, "  INTCNT: UHEX-%lX : %ld\n", pvm_copy.intcnt,
			pvm_copy.intcnt);
	if (end == 4) {
		return;
	}
	fprintf(file_write, "  STATUS: UHEX-%lX : %ld (", pvm_copy.status,
			pvm_copy.status);
	_Bool notFirst = 0;
#define print_flag(flag) if (pvm_copy.status & S_##flag) { if (notFirst) { fputs(" | ", file_write); } else { notFirst = 1; } fputs(#flag, file_write); }
	print_flag(LOWER)
	print_flag(GREATHER)
	print_flag(EQUAL)
	print_flag(OVERFLOW)
	print_flag(ZERO)
	print_flag(NAN)
	print_flag(ALL_BITS)
	print_flag(SOME_BITS)
	print_flag(NONE_BITS)
#undef print_flag
	fputs(")\n", file_write);
	if (end == 5) {
		return;
	}
	fprintf(file_write, "  ERRNO:  UHEX-%lX : %ld\n", pvm_copy.err,
			pvm_copy.err);
	int i = 0;
	while (1) {
		if (end == (i + 6)) {
			return;
		}
		int c0 = to_hex(0xF & (i >> 4)), c1 = to_hex(0xF & i);
		fprintf(file_write, "  X%c%c:    UHEX-%lX : %ld\n", //
				c0, c1, pvm.x[i], pvm.x[i]);
		i++;
	}
}

static inline void state_to_stepping(int dep) {
	pvm_lock();
	pvm_next_state = pvm_ds_new_stepping;
	pvm_depth = dep;
	pvm_unlock();
}

static inline _Bool scahnf_help(struct sok_data *sd, char *buffer,
		const char *str, const char *name, void *a, void *b) {
	while (fscanf(sd->read, str, a, b) == -1) {
		int e = errno;
		errno = 0;
		switch (e) {
		case EAGAIN:
			wait5ms();
			continue;
		case EINTR:
			continue;
		case EILSEQ:
			fscanf(sd->read, "%127s", buffer);
			fprintf(sd->write, "could not parse the %s ('%s')\n", name, buffer);
			break;
		case ERANGE:
			fscanf(sd->read, "%127s", buffer);
			fprintf(sd->write, "%s is out of range ('%s')\n", name, buffer);
			break;
		default:
			fscanf(sd->read, "%127s", buffer);
			fprintf(sd->write, "could not scanf the %s: %s\n", name,
					strerror(e));
			break;
		}
		return 0;
	}
	return 1;
}

static void pvm_dbcmd_help(struct sok_data *sd, char *buf) {
	fprintf(sd->write,
	/*	  */"db-pvm debug console\n"
			"\n"
			"numbers:\n"
			"  when a command accepts an number (or address)\n"
			"  by default the number will be interpreted as a\n"
			"  decimal number. When a '0' is put before the number\n"
			"  it will be interpreted as an octal number and if a\n"
			"  '0x' is before the number it will be interpreted as\n"
			"  an hexadecimal number.\n"
			"  for negative numbers the '-' prefix is put before the\n"
			"  prefix which indicates, which number system is used.\n"
			"\n"
			"the following commands can be used in any state\n"
			"of the db-pvm.\n"
			"any state commands:\n"
			"  help\n"
			"    display this message\n"
			"  version\n"
			"    display the version\n"
			"  detach\n"
			"    detach the debug console without\n"
			"    terminating the program.\n"
			"    WARN: when this is the last debug\n"
			"    console and the db-pvm does not\n"
			"    listen on a port, and the db-pvm is\n"
			"    currently not running, it will not\n"
			"    change it's state and has to be killed\n"
			"    with a signal.\n"
			"  exit [EXIT_NUM]\n"
			"    terminate the db-pvm with the given\n"
			"    exit number. If the exit number is not\n"
			"    given the db-pvm will terminate with\n"
			"    the exit code 1.\n"
			"  state\n"
			"    display the current state of the db-pvm\n"
			"  wait\n"
			"    change the db-pvm state to waiting\n"
			"  run\n"
			"    change the db-pvm state to running\n"
			"  step-in\n"
			"    change the db-pvm state to stepping\n"
			"    and set the step depth to -1.\n"
			"    this means, that only one command\n"
			"    will be executed.\n"
			"  step\n"
			"    change the db-pvm state to stepping\n"
			"    and set the step depth to 0.\n"
			"    this means, that the db-pvm will execute\n"
			"    commands until an equal amount of call and\n"
			"    return commands has been executed (the\n"
			"    db-pvm will execute at least one command).\n"
			"  step-out\n"
			"    change the db-pvm state to stepping\n"
			"    and set the step depth to 1.\n"
			"    this means, that the db-pvm will execute\n"
			"    commands until an less call than return\n"
			"    commands has been executed.\n"
			"  step-dep <DEPTH>\n"
			"    change the db-pvm state to stepping\n"
			"    and set the step depth to the given value.\n"
			"    the db-pvm will execute, until (at least) depth\n"
			"    more returns has been made than calls.\n"
			"    so if depth is negative the command behaves\n"
			"    like 'step-in' and if depth is zero the command\n"
			"    behaves like 'step'.\n"
			"  break <ADDRESS>\n"
			"    change the db-pvm state to waiting when the\n"
			"    Instruction at the given address should be\n"
			"    executed.\n"
			"    when the instruction pointer is already the\n"
			"    given address while the db-pvm is waiting and\n"
			"    the state is changed to a executing state, the\n"
			"    breakpoint will be ignored.\n"
			"      In other words, when telling the db-pvm to\n"
			"      execute, it will execute at least one command\n"
			"      until a breakpoint can change the state.\n"
			"the following commands can only be used when\n"
			"the db-pvm is in a waiting state.\n"
			"only on wait commands:\n"
			"  pvm\n"
			"    display the virtual machine\n"
			"    this includes the instruction pointer\n"
			"    the stack pointer\n"
			"    the interrupt table pointer\n"
			"    the interrupt count register\n"
			"    the status register\n"
			"    the error number register\n"
			"    all XNN registers\n"
			"  regs <LENGTH>\n"
			"    display first length registers of the\n"
			"    virtual machine.\n"
			"    this is similar to the pvm command, but\n"
			"    display only the first length registers and\n"
			"    not all registers. when length is 256 the two\n"
			"    commands are equally.\n"
			"  mem <ADDRESS> <LENGTH>\n"
			"    display the given memory block.\n"
			"  disasm <ADDRESS> <LENGTH>\n"
			"    disassemble the given memory block.\n"
			"");
}
static void pvm_dbcmd_version(struct sok_data *sd, char *buf) {
	print_version(sd->write);
}
static void pvm_dbcmd_detach(struct sok_data *sd, char *buf) {
	fprintf(sd->write, "bye\n");
	fclose(sd->write);
	fclose(sd->read);
	close(sd->fd);
	pthread_exit(NULL);
}
static void pvm_dbcmd_exit(struct sok_data *sd, char *buf) {
	int exit_num;
	if (fscanf(sd->read, "%i", &exit_num) == -1) {
		exit_num = 1;
	}
	fprintf(sd->write, "bye, exit now with %d\n", exit_num);
	exit(exit_num);
}
static void pvm_dbcmd_state(struct sok_data *sd, char *buf) {
	pvm_lock();
	enum pvm_db_state state = pvm_state, next_state = pvm_next_state;
	pvm_unlock();
	switch (state) {
	case pvm_ds_running:
		fprintf(sd->write, "the db-pvm is currently running\n");
		break;
	case pvm_ds_stepping:
		fprintf(sd->write, "the db-pvm is currently stepping\n");
		break;
	case pvm_ds_waiting:
		fprintf(sd->write, "the db-pvm is currently waiting\n");
		break;
	case pvm_ds_new_stepping:
		fprintf(sd->write, "the db-pvm is currently starting to step\n");
		break;
	default:
		fprintf(sd->write, "the db-pvm currently has an unknown state: %d\n",
				pvm_state);
		break;
	}
	if (state != next_state) {
		switch (next_state) {
		case pvm_ds_running:
			fprintf(sd->write, "the db-pvm will soon run\n");
			break;
		case pvm_ds_stepping:
			fprintf(sd->write, "the db-pvm will soon step\n");
			break;
		case pvm_ds_waiting:
			fprintf(sd->write, "the db-pvm will soon wait\n");
			break;
		case pvm_ds_new_stepping:
			fprintf(sd->write, "the db-pvm will soon start to step\n");
			break;
		default:
			fprintf(sd->write,
					"the db-pvm will soon has an unknown state: %d\n",
					pvm_state);
			break;
		}
	}
}
static void pvm_dbcmd_wait(struct sok_data *sd, char *buf) {
	pvm_lock();
	pvm_next_state = pvm_ds_waiting;
	pvm_unlock();
	fprintf(sd->write, "the db-pvm will soon wait\n");
}
static void pvm_dbcmd_run(struct sok_data *sd, char *buf) {
	pvm_lock();
	pvm_next_state = pvm_ds_running;
	pvm_unlock();
	fprintf(sd->write, "the db-pvm will soon run\n");
}
static void pvm_dbcmd_step_in(struct sok_data *sd, char *buf) {
	state_to_stepping(-1);
}
static void pvm_dbcmd_step(struct sok_data *sd, char *buf) {
	state_to_stepping(0);
}
static void pvm_dbcmd_step_out(struct sok_data *sd, char *buf) {
	state_to_stepping(1);
}
static void pvm_dbcmd_step_dep(struct sok_data *sd, char *buffer) {
	int dep;
	if (!scahnf_help(sd, buffer, "%i", "depth", &dep,
	NULL)) {
		return;
	}
	state_to_stepping(dep);
}
static void pvm_dbcmd_break(struct sok_data *sd, char *buffer) {
	num addr;
	if (scahnf_help(sd, buffer, "%li", "address", &addr,
	NULL)) {
		return;
	}
	pvm_lock();
	hashset_put(&breakpoints, (unsigned) addr, (void*) addr);
	pvm_unlock();
}
static void pvm_dbcmd_pvm(struct sok_data *sd, char *buf) {
	print_pvm(sd->write, 256);
}
static void pvm_dbcmd_regs(struct sok_data *sd, char *buffer) {
	int len;
	if (!scahnf_help(sd, buffer, "%i", "length", &len,
	NULL)) {
		return;
	}
	if (len > 256) {
		fprintf(sd->write, "length is out of range (%d) (max=256)\n", len);
	} else if (len < 0) {
		fprintf(sd->write, "length is out of range (%d) (min=0)\n", len);
	} else {
		print_pvm(sd->write, len);
	}
}
static void pvm_dbcmd_mem(struct sok_data *sd, char *buffer) {
	num addr, len;
	if (!scahnf_help(sd, buffer, "%li%li", "address or length", &addr, &len)) {
		return;
	}
	if (len < 0) {
		fprintf(sd->write,
				"the length of the memory block is negative (addr=HEX-%lX, len=HEX-%lX)\n",
				addr, len);
		return;
	}
	pvm_lock();
	if (pvm_state != pvm_ds_waiting) {
		pvm_unlock();
		fprintf(sd->write, "the pvm is not waiting\n");
		return;
	}
	struct memory_check mem_chk = chk0(addr, len, 1);
	if (!mem_chk.valid) {
		pvm_unlock();
		fprintf(sd->write,
				"the given memory block is invalid (addr=HEX-%lX, len=HEX-%lX)\n",
				addr, len);
		return;
	}
	ui8 buf[17];
	buf[16] = '\0';
	for (; len; len -= 8, addr += 8) {
		if (len >= 8) {
			num n = *(num*) (mem_chk.mem->offset + addr);
			fprintf(sd->write, "p-%lX : %.16lX\n", addr, n);
		} else {
			ui8 *p = mem_chk.mem->offset + addr;
			for (int i = 0; i < (8 - len); i++) {
				buf[(i << 1)] = ' ';
				buf[(i << 1) + 1] = ' ';
			}
			for (int i = 0; len > 0; len--, p++, i += 2) {
				buf[15 - i] = to_hex(0xF & *p);
				buf[14 - i] = to_hex(0xF & (*p >> 4));
			}
			fprintf(sd->write, "p-%lX : %s\n", addr, buf);
			break;
		}
	}
	pvm_unlock();
}
static void pvm_dbcmd_disasm(struct sok_data *sd, char *buffer) {
	num addr, len;
	if (!scahnf_help(sd, buffer, "%li%li", "address or length", &addr, &len)) {
		return;
	}
	if (len < 0) {
		fprintf(sd->write,
				"the length of the memory block is negative (addr=HEX-%lX, len=HEX-%lX)\n",
				addr, len);
		return;
	}
	pvm_lock();
	if (pvm_state != pvm_ds_waiting) {
		pvm_unlock();
		fprintf(sd->write, "the pvm is not waiting\n");
		return;
	}
	struct memory_check mem_chk = chk0(addr, len, 1);
	if (!mem_chk.valid) {
		pvm_unlock();
		fprintf(sd->write,
				"the given memory block is invalid (addr=HEX-%lX, len=HEX-%lX)\n",
				addr, len);
		return;
	}
	int pipes[2];
	if (pipe2(pipes, O_CLOEXEC) == -1) {
		pvm_unlock();
		fprintf(sd->write, "could not create the pipe: %s\n", strerror(errno));
		return;
	}
	fflush(sd->write);
	pid_t cpid = fork();
	if (cpid == -1) {
		pvm_unlock();
		fprintf(sd->write, "could not fork: %s\n", strerror(errno));
		errno = 0;
	} else if (cpid) {
		void *p = mem_chk.mem->offset + addr;
		for (num remain = len; remain;) {
			num wrote = write(pipes[1], p, remain);
			if (wrote == -1) {
				int e = errno;
				switch (e) {
				case EAGAIN:
				case EINTR:
					continue;
				default:
					pvm_unlock();
					fprintf(sd->write, "write failed: %s", strerror(e));
					return;
				}
			}
			remain -= wrote;
			p += wrote;
		}
		close(pipes[1]);
		pvm_unlock();
		while (1) {
			int wstatus;
			if (waitpid(cpid, &wstatus, 0) == -1) {
				int e = errno;
				errno = 0;
				switch (e) {
				case EINTR:
					continue;
				default:
					pvm_unlock();
					fprintf(sd->write, "could not wait: %s\n", strerror(e));
					return;
				}
			}
			if (WIFEXITED(wstatus)) {
				if (WEXITSTATUS(wstatus) != 0) {
					fprintf(sd->write,
							"disasm child terminated with exit status %d\n",
							WEXITSTATUS(wstatus));
				}
				return;
			} else if (WIFSIGNALED(wstatus)) {
				fprintf(sd->write, "child was terminated by the signal %d\n",
						WTERMSIG(wstatus));
				return;
			}
		}
	} else {
		if (dup2(pipes[0], STDIN_FILENO) == -1) {
			fprintf(sd->write, "could not overwrite stdin\n");
			exit(1);
		}
		if (dup2(sd->fd, STDOUT_FILENO) == -1) {
			fprintf(sd->write, "could not overwrite stdout\n");
			exit(1);
		}
		if (dup2(sd->fd, STDERR_FILENO) == -1) {
			fprintf(sd->write, "could not overwrite stderr\n");
			exit(1);
		}
		set_fd_flag(STDIN_FILENO, "CLOEXEC | NONBLOCK", O_CLOEXEC | O_NONBLOCK,
				0);
		set_fd_flag(STDOUT_FILENO, "CLOEXEC | NONBLOCK", O_CLOEXEC | O_NONBLOCK,
				0);
		set_fd_flag(STDERR_FILENO, "CLOEXEC | NONBLOCK", O_CLOEXEC | O_NONBLOCK,
				0);
		close(pipes[1]);
		char *arg3, *arg4;
		char *arg5, *arg6;
		int val = snprintf(buffer, 128, "HEX-%lX", len);
		if (val == -1 || val >= 128) {
			arg3 = NULL;
			fprintf(old_stderr, "could not convert the length to a string.\n");
			fflush(stderr);
			exit(1);
		} else {
			arg3 = "--len";
			arg4 = buffer;
			if (val < 64) {
				val = snprintf(buffer + 64, 64, "HEX-%lX", addr);
				if (val == -1 || val >= 64) {
					arg5 = NULL;
					fprintf(old_stderr,
							"could not convert the address to a string, discard --pos argument\n");
					fflush(stderr);
				} else {
					arg5 = "--pos";
					arg6 = buffer + 64;
				}
			}
		}
		char *const argv[7] = { "/bin/prim-disasm", "--analyse", arg3, arg4,
				arg5, arg6, NULL };
		execv("/bin/prim-disasm", argv);
	}
}

static void* pvm_debug_thread_func(void *_arg) {
	struct sok_data *sd = _arg;
	if (syscall(SYS_membarrier, MEMBARRIER_CMD_REGISTER_PRIVATE_EXPEDITED, 0U,
			0) == -1) {
		fprintf(old_stderr,
				"could not register a debug thread for the memory barrier syscall: %s\n",
				strerror(errno));
		exit(1);
	}
	set_fd_flag(sd->fd, "NONBLOCK | CLOEXEC", O_NONBLOCK | O_CLOEXEC, 1);
	int read_fd = dup(sd->fd);
	if (read_fd == -1) {
		fprintf(old_stderr,
				"could not duplicate my socket file descriptor: %s\n",
				strerror(errno));
		exit(1);
	}
	set_fd_flag(read_fd, "NONBLOCK | CLOEXEC", O_NONBLOCK | O_CLOEXEC, 1);
	int write_fd = dup(sd->fd);
	if (write_fd == -1) {
		fprintf(old_stderr,
				"could not duplicate my socket file descriptor: %s\n",
				strerror(errno));
		exit(1);
	}
	set_fd_flag(write_fd, "NONBLOCK | CLOEXEC", O_NONBLOCK | O_CLOEXEC, 1);
	sd->read = fdopen(read_fd, "r");
	if (!sd->read) {
		fprintf(old_stderr,
				"could not open a FILE* from my file descriptor: %s\n",
				strerror(errno));
		exit(1);
	}
	sd->write = fdopen(write_fd, "w");
	if (!sd->write) {
		fprintf(old_stderr,
				"could not open a FILE* from my file descriptor: %s\n",
				strerror(errno));
		exit(1);
	}
	pvm_lock();
	hashset_put(&delegate_set_stdout, (unsigned) sd->fd, (void*) (long) sd->fd);
	hashset_put(&delegate_set_stderr, (unsigned) sd->fd, (void*) (long) sd->fd);
	pvm_unlock();
	char *buffer = malloc(128);
	if (!buffer) {
		fprintf(old_stderr, "could not allocate my debug string buffer\n");
		exit(1);
	}
	while (1) {
		big_loop_start: ;
		char white;
		fputs("debug-shell> ", sd->write);
		fflush(sd->write);
		if (fscanf(sd->read, "%127s%1c", buffer, &white) == -1) {
			switch (errno) {
			case EAGAIN:
				wait5ms();
				/* no break */
			case EINTR:
				errno = 0;
				continue;
			}
			fprintf(old_stderr, "could not scanf the debug input: %s\n",
					strerror(errno));
			exit(1);
		}
		struct debug_cmd *debug_cmd = hashset_get(&debug_commands,
				debug_cmds_hash(&buffer), &buffer);
		if (debug_cmd) {
			debug_cmd->func(sd, buffer);
		} else {
			fprintf(sd->write, "unknown command: '%s'\n", buffer);
		}
	}
}

static inline void d_wait() {
	pvm_unlock();
	while (1) {
		pvm_lock();
		pvm_state = pvm_next_state;
		switch (pvm_state) {
		case pvm_ds_running:
			void *b = hashset_get(&breakpoints, (unsigned) pvm.ip,
					(void*) pvm.ip);
			if (b) {
				pvm_state = pvm_next_state = pvm_ds_waiting;
				pvm_unlock();
				break;
			}
			return;
		case pvm_ds_new_running:
			pvm_next_state = pvm_ds_running;
			return;
		case pvm_ds_waiting:
			pvm_unlock();
			state_wait: ;
			wait5ms();
			break;
		case pvm_ds_stepping:
			if (pvm_depth <= 0) {
				pvm_state = pvm_ds_waiting;
				pvm_unlock();
				break;
			}
			return;
		case pvm_ds_new_stepping:
			pvm_next_state = pvm_ds_stepping;
			return;
		default:
			abort();
		}
	}
}
#endif // PVM_DEBUG

void execute() {
	while (1) {
#ifdef PVM_DEBUG
		d_wait();
#endif
		exec_cmd();
	}
}

#include "pvm-int.c"
#include "pvm-cmd.c"
