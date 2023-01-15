/*
 * pvm-virtual-mashine.c
 *
 *  Created on: Jul 6, 2022
 *      Author: pat
 */
#define PVM

#include <pfs-constants.h>
#include <pfs.h>
#include <pfs-stream.h>
#include <pfs-iter.h>
#include <pfs-element.h>
#include <pfs-folder.h>
#include <pfs-file.h>
#include <pfs-pipe.h>

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
#include <pthread.h>
#include <sys/socket.h>
#include <netinet/ip.h>
#include <sys/un.h>
#include <linux/membarrier.h>
#include <sys/syscall.h>
#endif // PVM_DEBUG

void pvm_init(char **argv, num argc, void *exe, num exe_size) {
	if (next_adress != REGISTER_START) {
		abort();
	}

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

	void *stack_pntr = malloc(256);
	if (!stack_pntr) {
		abort();
	}
	struct memory *stack_mem = alloc_memory2(stack_pntr, 256,
	/*		*/MEM_AUTO_GROW | (8 << MEM_AUTO_GROW_SHIFT));
	stack_mem->grow_size = 256;
	stack_mem->change_pntr = &pvm.sp;

	struct memory2 int_mem = alloc_memory(INTERRUPT_COUNT << 3, 0U);
	if (!int_mem.mem) {
		abort();
	}
	memset(int_mem.adr, -1, INTERRUPT_COUNT << 3);

	if (exe) {
		// use different flags in future version?
		struct memory *exe_mem = alloc_memory2(exe, exe_size, 0U);
		if (!exe_mem) {
			abort();
		}
		pvm.ip = exe_mem->start;
	}

	pvm.x[0] = argc;
	struct memory *args_mem = alloc_memory2(argv, argc * sizeof(char*), 0U);
	pvm.x[1] = args_mem->start;
	for (; argc; argv++, argc--) {
		num len = strlen(*argv) + 1;
		struct memory *arg_mem = alloc_memory2(*argv, len, 0);
		*(num*) argv = arg_mem->start;
	}
	*(num*) argv = -1;
}

#ifdef PVM_DEBUG

static inline void wait5ms() {
	struct timespec wait_time = { //
			/*	  */.tv_sec = 0, // 0 sec
					.tv_nsec = 5000000 // 5 ms
			};
	nanosleep(&wait_time, NULL);
}

// static FILE *new_stderr; // TODO use

static pthread_mutex_t debug_mutex;

struct pvm_thread_arg {
	int val;
};

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

static void* pvm_delegate_func(void *_arg) {
	struct pvm_delegate_arg arg = *(struct pvm_delegate_arg*) _arg;
	free(_arg);
	void *buffer = malloc(1024);
	if (!buffer) {
		fprintf(stderr, "could not allocate delegate buffer\n");
		exit(1);
	}
	while (1) {
		ssize_t reat = read(arg.srcfd, buffer, 1024);
		if (reat == -1) {
			switch (errno) {
			case EAGAIN:
				wait5ms();
				/* no break */
			case EINTR:
				errno = 0;
				continue;
			}
			perror("read");
			fprintf(stderr, "error on read\n");
			exit(1);
		} else if (read == 0) {
			return NULL;
		}
		for (int i = arg.dst_fds->setsize; i;) {
			if (!arg.dst_fds->entries[i]) {
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
					perror("write");
					fprintf(stderr, "error on write\n");
					exit(1);
				}
				wrote += w;
			}
		}
	}
}

static void* pvm_debug_thread_func(void *_arg);

#define DEBUG_SOCKET_BACKLOG 1

static void* pvm_debug_thread_deamon(void *_arg) {
	struct pvm_thread_arg arg = *(struct pvm_thread_arg*) _arg;
	free(_arg);
	int domain;
	union {
		struct sockaddr sa;
		struct sockaddr_in sa_in;
	} my_sock_adr;
	domain = AF_INET;
	my_sock_adr.sa_in = (struct sockaddr_in ) { //
			/*	  */.sin_family = AF_INET, //
					.sin_port = htons(arg.val), //
					.sin_addr.s_addr = INADDR_ANY, //
			};
	int my_sok = socket(domain, SOCK_STREAM, 0);
	if (my_sok == -1) {
		perror("socket");
		fprintf(stderr, "could not create my socket\n");
		exit(1);
	}
	if (listen(my_sok, DEBUG_SOCKET_BACKLOG) == -1) {
		perror("listen");
		fprintf(stderr, "could not open my socket for listening\n");
		exit(1);
	}
	while (1) {
		int sok = accept(my_sok, NULL, NULL);
		if (sok == -1) {
			perror("accept");
			fprintf(stderr, "could not accept a debug connection\n");
			exit(1);
		}

		struct pvm_thread_arg *child_arg = malloc(
				sizeof(struct pvm_thread_arg));
		if (!child_arg) {
			fprintf(stderr, "could not allocate the argument for the thread\n");
			exit(1);
		}
		child_arg->val = sok;
		pthread_t debug_thread;
		pthread_attr_t debug_attrs;
		pthread_attr_init(&debug_attrs);
		pthread_create(&debug_thread, &debug_attrs, pvm_debug_thread_func,
				child_arg);
		pthread_attr_destroy(&debug_attrs);
	}
}

static inline void make_std_noblock() {
	int flags = fcntl(STDIN_FILENO, F_GETFD);
	if (flags == -1) {
		perror("fcntl");
		fprintf(stderr, "could not get the flags of stdin\n");
		exit(1);
	}
	if ((flags & O_NONBLOCK) == 0) {
		if (fcntl(STDIN_FILENO, F_SETFD, flags | O_NONBLOCK) == -1) {
			perror("fcntl");
			fprintf(stderr, "could not set the NOBLOCK flag for stdin\n");
			exit(1);
		}
	}
	flags = fcntl(STDOUT_FILENO, F_GETFD);
	if (flags == -1) {
		perror("fcntl");
		fprintf(stderr, "could not get the flags of stdout\n");
		exit(1);
	}
	if ((flags & O_NONBLOCK) == 0) {
		if (fcntl(STDOUT_FILENO, F_SETFD, flags | O_NONBLOCK) == -1) {
			perror("fcntl");
			fprintf(stderr, "could not set the NOBLOCK flag for stdout\n");
			exit(1);
		}
	}
	flags = fcntl(STDERR_FILENO, F_GETFD);
	if (flags == -1) {
		perror("fcntl");
		fprintf(stderr, "could not get the flags of stderr\n");
		exit(1);
	}
	if ((flags & O_NONBLOCK) == 0) {
		if (fcntl(STDERR_FILENO, F_SETFD, flags | O_NONBLOCK) == -1) {
			perror("fcntl");
			fprintf(stderr, "could not set the NOBLOCK flag for stderr\n");
			exit(1);
		}
	}
}

static inline void create_pipes(int stdin_pipe[2], int stdout_pipe[2],
		int stderr_pipe[2]) {
	if (pipe2(stdin_pipe, O_NONBLOCK) == -1) {
		perror("pipe");
		fprintf(stderr, "could not open a debug pipe!\n");
		exit(1);
	}
	if (pipe2(stdout_pipe, O_NONBLOCK) == -1) {
		perror("pipe");
		fprintf(stderr, "could not open a debug pipe!\n");
		exit(1);
	}
	if (pipe2(stderr_pipe, O_NONBLOCK) == -1) {
		perror("pipe");
		fprintf(stderr, "could not open a debug pipe!\n");
		exit(1);
	}
}

static inline void overwrite_std(int stdin_pipe[2], int stdout_pipe[2],
		int stderr_pipe[2]) {
	if (dup2(stdin_pipe[0], STDIN_FILENO) == -1) {
		perror("pipe");
		fprintf(stderr, "could not set stdin!\n");
		exit(1);
	}
	if (dup2(stdout_pipe[1], STDOUT_FILENO) == -1) {
		perror("pipe");
		fprintf(stderr, "could not set stdout!\n");
		exit(1);
	}
	if (dup2(stderr_pipe[1], STDERR_FILENO) == -1) {
		perror("pipe");
		fprintf(stderr, "could not set stderr!\n");
		exit(1);
	}
}

static inline void init_syncronisation() {
	pthread_mutexattr_t attr;
	pthread_mutexattr_init(&attr);
	pthread_mutex_init(&debug_mutex, &attr);
	pthread_mutexattr_destroy(&attr);

	if (syscall(SYS_membarrier, MEMBARRIER_CMD_REGISTER_PRIVATE_EXPEDITED, 0U,
			0) == -1) {
		perror("membarrier");
		fprintf(stderr,
				"could not register myself for the memory barrier syscall\n");
		exit(1);
	}
}

void pvm_debug_init(int input, _Bool input_is_pipe, _Bool wait) {
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

	make_std_noblock();

	int stdin_pipe[2];
	int stdout_pipe[2];
	int stderr_pipe[2];
	create_pipes(stdin_pipe, stdout_pipe, stderr_pipe);

	overwrite_std(stdin_pipe, stdout_pipe, stderr_pipe);

	init_syncronisation();

	pvm_state = pvm_ds_init;
	if (wait) {
		pvm_next_state = pvm_ds_waiting;
	} else {
		pvm_next_state = pvm_ds_running;
	}

	struct pvm_thread_arg *arg = malloc(sizeof(struct pvm_thread_arg));
	if (!arg) {
		fprintf(stderr, "could not allocate the argument for the thread\n");
		exit(1);
	}
	arg->val = input;
	pthread_t deamon_thread;
	pthread_attr_t deamon_attrs;
	pthread_attr_init(&deamon_attrs);
	pthread_create(&deamon_thread, &deamon_attrs,
			input_is_pipe ? pvm_debug_thread_func : pvm_debug_thread_deamon,
			arg);
	pthread_attr_destroy(&deamon_attrs);
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
	if (pvm.intcnt < intnum || intnum < 0) {
		if (pvm.intcnt <= INT_ERRORS_ILLEGAL_INTERRUPT) {
			exit(128);
		}
		if (pvm.intp == -1) {
			pvm.x[0] = intnum;
			callInt;
		} else {
			num adr = pvm.intp + (INT_ERRORS_ILLEGAL_INTERRUPT << 3);
			struct memory *mem = chk(adr, 8).mem;
			if (!mem) {
				return;
			}
			num deref = *(num*) mem->offset + adr;
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
			intnum = INT_ERRORS_ILLEGAL_INTERRUPT;
		}
		callInt;
	} else {
		num adr = pvm.intp + (intnum << 3);
		struct memory *mem = chk(adr, 8).mem;
		if (!mem) {
			return;
		}
		if (incIPVal) {pvm.ip += incIPVal;}
		num deref = *(num*) mem->offset + adr;
		if (-1 == deref) {
			callInt;
		} else {
			int_init();
			pvm.ip = deref;
		}
	}
#undef callInt
}

#ifdef PVM_DEBUG
#undef chk
static inline struct memory_check chk0(num pntr, num size, _Bool use_valid) {
#else
static inline struct memory_check chk(num pntr, num size) {
#endif //  PVM_DEBUG
	for (struct memory *m = memory; m->start != -1; m++) {
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
							return result;
						}
					}
				}
			}
			interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
			struct memory_check result;
			result.mem = NULL;
			return result;
		} else if (m->end <= pntr) {
			continue;
		} else if (m->end <= pntr - size) {
			goto check_grow;
		}
		struct memory_check result;
		result.mem = m;
		result.changed = 0;
		return result;
	}
#ifdef PVM_DEBUG
#endif // PVM_DEBUG
	interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
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

#define get_param(name, pntr) \
	union param name; \
	{ \
		struct p _p = param(pntr); \
		if (!p.valid) { \
			return; \
		} \
		name = p.p; \
	}

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
			interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
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
					interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
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
			interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
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
			interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
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
		if (remain_instruct_space
				<= size + ((param_num_value_index - 1) << 3)) {
			interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
			paramFail
		}
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
	default:
		r.valid = 0;
		interrupt(INT_ERRORS_UNKNOWN_COMMAND, 0);
	}
	return r;
#undef paramFail
}

static inline void exec() {
	struct memory_check ipmem = chk(pvm.ip, 8);
	if (!ipmem.mem) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
		return;
	}
	remain_instruct_space = ipmem.mem->end - pvm.ip;
	if (remain_instruct_space < 8) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
		return;
	}
	ia.pntr = ipmem.mem->offset + pvm.ip;
	param_param_type_index = 2;
	param_byte_value_index = 7;
	param_num_value_index = 1;
	cmds[*ia.wp]();
}

PVM_SI_PREFIX struct memory* alloc_memory2(void *adr, num size, unsigned flags) {
	if (mem_size) {
		for (num index = mem_size - 1; index; index--) {
			if (memory[index].start == -1) {
				continue;
			}
			memory[index].start = next_adress;
			memory[index].end = memory->start + size;
			memory[index].offset = adr - memory->start;
			memory[index].flags = flags;
			next_adress = memory->end + ADRESS_HOLE_DEFAULT_SIZE;
			if (memory->end < 0) {
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
	memory[oms].end = memory->start + size;
	memory[oms].offset = adr - memory->start;
	memory[oms].flags = flags;
	/*
	 //	if (memory->start < 0 || memory->end < 0) {
	 * whould be the better check
	 * I know that the current check can fail when size is near 2^63
	 */
	if (memory->end < 0) {
		// overflow
		abort();
	}
	next_adress = memory->end + ADRESS_HOLE_DEFAULT_SIZE;
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
		interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
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
		interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
		return;
	}
	free_mem_impl(mem);
}

#ifdef PVM_DEBUG
static inline void pvm_lock() {
	if (pthread_mutex_lock(&debug_mutex) == -1) {
		perror("pthread_mutex_lock");
		fprintf(stderr, "could not lock the debug mutex\n");
		exit(1);
	}
	if (syscall(SYS_membarrier, MEMBARRIER_CMD_PRIVATE_EXPEDITED, 0U, 0)
			== -1) {
		perror("membarrier");
		fprintf(stderr, "membarrier failed\n");
		exit(1);
	}
}

static inline void pvm_unlock() {
	if (pthread_mutex_unlock(&debug_mutex) == -1) {
		perror("pthread_mutex_unlock");
		fprintf(stderr, "could not unlock the debug mutex\n");
		exit(1);
	}
}

static inline char to_hex(int val) {
	if (val < 10) {
		return '0' + val;
	} else {
		return 'A' - 10 + val;
	}
}

static inline void print_pvm(FILE *file, int end) {
	pvm_lock();
	if (pvm_state != pvm_ds_waiting) {
		fprintf(file, "I can not display the pvm, the pvm is not waiting!\n");
		return;
	}
	struct pvm pvm_copy = pvm;
	pvm_unlock();
	fprintf(file, "PVM:\n");
	if (end == 0) {
		return;
	}
	fprintf(file, "  IP:     UHEX-%lX : %ld\n", pvm_copy.ip, pvm_copy.ip);
	if (end == 1) {
		return;
	}
	fprintf(file, "  SP:     UHEX-%lX : %ld\n", pvm_copy.sp, pvm_copy.sp);
	if (end == 2) {
		return;
	}
	fprintf(file, "  INTP:   UHEX-%lX : %ld\n", pvm_copy.intp, pvm_copy.intp);
	if (end == 3) {
		return;
	}
	fprintf(file, "  INTCNT: UHEX-%lX : %ld\n", pvm_copy.intcnt,
			pvm_copy.intcnt);
	if (end == 4) {
		return;
	}
	fprintf(file, "  STATUS: UHEX-%lX : %ld\n", pvm_copy.status,
			pvm_copy.status);
	if (end == 5) {
		return;
	}
	fprintf(file, "  ERRNO:  UHEX-%lX : %ld\n", pvm_copy.err, pvm_copy.err);
	for (int i = 0; i < end - 6; i++) {
		int c0 = to_hex(0xF & (i >> 4)), c1 = to_hex(0xF & i);
		fprintf(file, "  X%c%c:     UHEX-%lX : %ld\n", //
				c0, c1, pvm.x[i], pvm.x[i]);
	}
}

static inline void state_to_stepping(int dep) {
	pvm_lock();
	pvm_next_state = pvm_ds_new_stepping;
	pvm_depth = dep;
	pvm_unlock();
}

static void* pvm_debug_thread_func(void *_arg) {
	struct pvm_thread_arg arg = *(struct pvm_thread_arg*) _arg;
	free(_arg);
	if (syscall(SYS_membarrier, MEMBARRIER_CMD_REGISTER_PRIVATE_EXPEDITED, 0U,
			0) == -1) {
		perror("membarrier");
		fprintf(stderr,
				"could not register a debug thread for the memory barrier syscall\n");
		exit(1);
	}
	FILE *file = fdopen(arg.val, "rw");
	if (!file) {
		perror("fdopen");
		fprintf(stderr, "could not open a FILE* from my file descriptor\n");
		exit(1);
	}
	char *buffer = malloc(128);
	if (!buffer) {
		fprintf(stderr, "could not allocate my debug string buffer\n");
		exit(1);
	}
	while (1) {
		big_loop_start: ;
		char white;
		if (fscanf(file, "%127s%1c", buffer, &white) == -1) {
			switch (errno) {
			case EAGAIN:
				wait5ms();
				/* no break */
			case EINTR:
				errno = 0;
				continue;
			}
			perror("fscanf");
			fprintf(stderr, "could not scanf the debug input\n");
			exit(1);
		}
		if (strcmp("help", buffer) == 0) {
			fprintf(file, "db-pvm debug console\n"
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
					"any state commands:"
					"  help"
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
					"    behaves like 'step'."
					"\n"
					"the following commands can only be used when\n"
					"the db-pvm is in a waiting state.\n"
					"only on wait commands:"
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
					"  mem <ADDRESS> <LENGTH>"
					"    display the given memory block.\n"
					""); // TODO more commands
		} else if (strcmp("version", buffer) == 0) {
			fprintf(file, "db-pvm " PVM_VERSION_STR "\n");
		} else if (strcmp("detach", buffer) == 0) {
			fprintf(file, "bye\n");
			fclose(file);
		} else if (strcmp("exit", buffer) == 0) {
			int exit_num;
			if (fscanf(file, "%i", &exit_num) == -1) {
				exit_num = 1;
			}
			fprintf(file, "bye, exit now with %d\n", exit_num);
			exit(exit_num);
		} else if (strcmp("state", buffer) == 0) {
			pvm_lock();
			enum pvm_db_state state = pvm_state, next_state = pvm_next_state;
			pvm_unlock();
			switch (state) {
			case pvm_ds_running:
				fprintf(file, "the db-pvm is currently running\n");
				break;
			case pvm_ds_stepping:
				fprintf(file, "the db-pvm is currently stepping\n");
				break;
			case pvm_ds_waiting:
				fprintf(file, "the db-pvm is currently waiting\n");
				break;
			default:
				fprintf(file, "the db-pvm currently has an unknown state: %d\n",
						pvm_state);
				break;
			}
			if (state != next_state) {
				switch (next_state) {
				case pvm_ds_running:
					fprintf(file, "the db-pvm will soon run\n");
					break;
				case pvm_ds_stepping:
					fprintf(file, "the db-pvm will soon step\n");
					break;
				case pvm_ds_waiting:
					fprintf(file, "the db-pvm will soon wait\n");
					break;
				default:
					fprintf(file,
							"the db-pvm will soon has an unknown state: %d\n",
							pvm_state);
					break;
				}
			}
		} else if (strcmp("wait", buffer) == 0) {
			pvm_lock();
			pvm_next_state = pvm_ds_waiting;
			pvm_unlock();
			fprintf(file, "the db-pvm will soon wait\n");
		} else if (strcmp("run", buffer) == 0) {
			pvm_lock();
			pvm_next_state = pvm_ds_running;
			pvm_unlock();
			fprintf(file, "the db-pvm will soon run\n");
		} else if (strcmp("step-in", buffer) == 0) {
			state_to_stepping(-1);
		} else if (strcmp("step", buffer) == 0) {
			state_to_stepping(0);
		} else if (strcmp("step-out", buffer) == 0) {
			state_to_stepping(1);
		} else if (strcmp("step-dep", buffer) == 0) {
			int dep;
			while (fscanf(file, "%i", &dep) == -1) {
				int e = errno;
				errno = 0;
				switch (e) {
				case EAGAIN:
					wait5ms();
					continue;
				case EINTR:
					continue;
				case EILSEQ:
					fscanf(file, "%127s", buffer);
					fprintf(file, "could not parse the depth ('%s')\n", buffer);
					break;
				case ERANGE:
					fscanf(file, "%127s", buffer);
					fprintf(file, "depth is out of range ('%s')\n", buffer);
					break;
				default:
					fscanf(file, "%127s", buffer);
					fprintf(file, "could not scanf the depth ('%s'): %s\n",
							buffer, strerror(e));
					break;
				}
				goto big_loop_start;
			}
			state_to_stepping(dep);
		} else if (strcmp("break", buffer) == 0) {
			num addr;
			while (fscanf(file, "%li", &addr) == -1) {
				int e = errno;
				errno = 0;
				switch (e) {
				case EAGAIN:
					wait5ms();
					continue;
				case EINTR:
					continue;
				case EILSEQ:
					fscanf(file, "%127s", buffer);
					fprintf(file, "could not parse the address ('%s')\n",
							buffer);
					break;
				case ERANGE:
					fscanf(file, "%127s", buffer);
					fprintf(file, "address is out of range ('%s')\n", buffer);
					break;
				default:
					fscanf(file, "%127s", buffer);
					fprintf(file, "could not scanf the number ('%s'): %s\n",
							buffer, strerror(e));
					break;
				}
				goto big_loop_start;
			}
		} else if (strcmp("pvm", buffer) == 0) {
			print_pvm(file, 256);
		} else if (strcmp("regs", buffer) == 0) {
			int len;
			while (fscanf(file, "%i", &len) == -1) {
				int e = errno;
				errno = 0;
				switch (e) {
				case EAGAIN:
					wait5ms();
					continue;
				case EINTR:
					continue;
				case EILSEQ:
					fscanf(file, "%127s", buffer);
					fprintf(file, "could not parse the length ('%s')\n",
							buffer);
					break;
				case ERANGE:
					fscanf(file, "%127s", buffer);
					fprintf(file, "length is out of range ('%s')\n", buffer);
					break;
				default:
					fscanf(file, "%127s", buffer);
					fprintf(file, "could not scanf the length ('%s'): %s\n",
							buffer, strerror(e));
					break;
				}
				goto big_loop_start;
			}
			if (len > 256) {
				fprintf(file, "length is out of range (%d) (max=256)\n", len);
			} else if (len < 0) {
				fprintf(file, "length is out of range (%d) (min=0)\n", len);
			} else {
				print_pvm(file, 256);
			}
		} else if (strcmp("mem", buffer) == 0) {
			num addr, len;
			while (fscanf(file, "%li%li", &addr, &len) == -1) {
				int e = errno;
				errno = 0;
				switch (e) {
				case EAGAIN:
					wait5ms();
					continue;
				case EINTR:
					continue;
				case EILSEQ:
					fscanf(file, "%127s", buffer);
					fprintf(file,
							"could not parse the address or length ('%s')\n",
							buffer);
					break;
				case ERANGE:
					fscanf(file, "%127s", buffer);
					fprintf(file, "address or length is out of range ('%s')\n",
							buffer);
					break;
				default:
					fscanf(file, "%127s", buffer);
					fprintf(file,
							"could not scanf the address and length ('%s'): %s\n",
							buffer, strerror(e));
					break;
				}
				goto big_loop_start;
			}
			if (len < 0) {
				fprintf(file,
						"the length of the memory block is negative (addr=HEX-%lX, len=HEX-%lX)\n",
						addr, len);
				goto big_loop_start;
			}
			struct memory_check mem_chk = chk0(addr, len, 1);
			if (!mem_chk.valid) {
				fprintf(file,
						"the given memory block is invalid (addr=HEX-%lX, len=HEX-%lX)\n",
						addr, len);
				goto big_loop_start;
			}
			ui8 buf[17];
			buf[16] = '\0';
			for (; len; len -= 8, addr += 8) {
				if (len > 8) {
					num n = *(num*) (mem_chk.mem->offset + addr);
					for (int i = 15; i; i--) {
						buf[i] = to_hex(0xF & (n >> (60 - (4 * i))));
					}
					fprintf(file, "HEX-%lX : %s\n", addr, buf);
				} else {
					ui8 *p = mem_chk.mem->offset + addr;
					buf[len << 1] = '\0';
					for (int i = 0; len; len--, p++, i++) {
						buf[i] = to_hex(0xF & (*p));
						buf[++i] = to_hex(0xF & ((*p) >> 4));
					}
					fprintf(file, "HEX-%lX : %s\n", addr, buf);
					break;
				}
			}
		} else {
			fprintf(file, "unknown command: '%s'\n", buffer);
		}
	}
}

static inline void d_wait() {
	while (1) {
		pvm_lock();
		pvm_state = pvm_next_state;
		switch (pvm_state) {
		case pvm_ds_running:
			pvm_unlock();
			return;
		case pvm_ds_waiting:
			state_wait: pvm_unlock();
			wait5ms();
			continue;
		case pvm_ds_stepping:
			if (pvm_depth <= 0) {
				pvm_state = pvm_ds_waiting;
				goto state_wait;
			}
			pvm_unlock();
			return;
		case pvm_ds_new_stepping:
			pvm_state = pvm_ds_stepping;
			pvm_next_state = pvm_ds_stepping;
			pvm_unlock();
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
		exec();
	}
}

#include "pvm-int.c"
#include "pvm-cmd.c"
