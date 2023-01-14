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

static inline void init_int() {
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
};

static inline struct memory_check chk(num pntr, num size);

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
				init_int();
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
			init_int();
			pvm.ip = deref;
		}
	}
#undef callInt
}

static inline struct memory_check chk(num pntr, num size) {
	for (struct memory *m = memory; m->start != -1; m++) {
		if (m->start > pntr) {
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
	interrupt(INT_ERRORS_ILLEGAL_MEMORY, 0);
	struct memory_check result;
	result.mem = NULL;
	return result;
}

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
static inline void d_wait() {
	while (1) {
		switch (state) {
		case running:
			return;
		case waiting:
			struct timespec time = { //
					.tv_sec = 0, // 0 sec
							.tv_nsec = 5000000 // 5 ms
					};
			nanosleep(&time, NULL);
			continue;
		case stepping:
			if (depth <= 0) {
				state = waiting;
			}
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
