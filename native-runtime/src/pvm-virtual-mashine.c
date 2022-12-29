/*
 * pvm-virtual-mashine.c
 *
 *  Created on: Jul 6, 2022
 *      Author: pat
 */
#define PVM

#include <pfs-constants.h>

#include "pvm-virtual-mashine.h"
#include "pvm-err.h"

#include "pvm-int.h"
#include "pvm-cmd.h"

#include <stdlib.h>
#include <string.h>

void pvm_init(char **argv, num argc, void *exe, num exe_size) {
	if (next_adress != REGISTER_START) {
		abort();
	}
	struct memory *pvm_mem = alloc_memory2(&pvm, sizeof(pvm),
	/*		*/MEM_NO_FREE | MEM_NO_RESIZE);
	if (!pvm_mem) {
		abort();
	}
	memset(&pvm, 0, sizeof(pvm));

	struct memory2 int_mem = alloc_memory(INTERRUPT_COUNT << 3, 0);
	if (!int_mem.mem) {
		abort();
	}
	memset(int_mem.adr, -1, INTERRUPT_COUNT << 3);

	if (exe) {
		// use different flags in future version?
		struct memory *exe_mem = alloc_memory2(exe, exe_size, 0);
		if (!exe_mem) {
			abort();
		}
		pvm.ip = exe_mem->start;
	}

	pvm.x[0] = argc;
	struct memory *args_mem = alloc_memory2(argv, argc * sizeof(char*), 0);
	pvm.x[1] = args_mem->start;
	for (; argc; argv++, argc--) {
		num len = strlen(*argv) + 1;
		struct memory *arg_mem = alloc_memory2(*argv, len, 0);
		*(num*)argv = arg_mem->start;
	}
	*(num*) argv = -1;
}

static inline void init_int() {
	struct memory2 mem = alloc_memory(128, 0);
	if (!mem.mem) {
		exit(127);
	}
	memcpy(mem.adr, &pvm, 128);
	pvm.x[0x09] = mem.mem->start;
}

static inline struct memory* chk(num pntr);

static inline void interrupt(num intnum) {
#ifdef PVM_DEBUG
#	define CALL_INT ints[intnum](intnum)
#else
#	define CALL_INT ints[intnum]();
#endif
	if (pvm.intcnt < intnum || intnum < 0) {
		if (intnum == INT_ERRORS_ILLEGAL_INTERRUPT || pvm.intcnt <= 0) {
			exit(128);
		}
		if (pvm.intp == -1) {
			pvm.x[0] = intnum;
			CALL_INT;
		} else {
			num adr = pvm.intp + (INT_ERRORS_ILLEGAL_INTERRUPT << 3);
			struct memory *mem = chk(adr);
			if (!mem) {
				return;
			}
			num deref = *(num*) mem->offset + adr;
			if (-1 == deref) {
				CALL_INT;
			} else {
				init_int();
				pvm.x[0] = intnum;
				pvm.ip = deref;
			}
		}
	} else if (pvm.intp == -1) {
		if (intnum >= INTERRUPT_COUNT) {
			pvm.x[0] = intnum;
			intnum = INT_ERRORS_ILLEGAL_INTERRUPT;
		}
		CALL_INT;
	} else {
		num adr = pvm.intp + (intnum << 3);
		struct memory *mem = chk(adr);
		if (!mem) {
			return;
		}
		num deref = *(num*) mem->offset + adr;
		if (-1 == deref) {
			CALL_INT;
		} else {
			init_int();
			pvm.ip = deref;
		}
	}
#undef CALL_INT
}

static inline struct memory* chk(num pntr) {
	for (struct memory *m = memory; m->start != -1; m++) {
		if (m->start > pntr) {
			interrupt(INT_ERRORS_ILLEGAL_MEMORY);
			return NULL;
		} else if (m->end <= pntr) {
			continue;
		}
		return m;
	}
	interrupt(INT_ERRORS_ILLEGAL_MEMORY);
	return NULL;
}

union param {
	fpnum fpn;
	fpnum *fpnp;
	num n;
	num *np;
	double_word dw;
	double_word *dwp;
	word w;
	word *wp;
	byte b;
	byte *bp;
	void *p;
};

struct p {
	union param p;
	_Bool valid;
};

#define get_param(name, pntr) \
	union param name; \
	{ \
		struct p _p = param(pntr); \
		if (!p.valid) { \
			int_errors_unknown_command(CI(INT_ERRORS_UNKNOWN_COMMAND)); \
			return; \
		} \
		name = p.p; \
	}

static int pol;
static int poh;
static int poq;

static union instruction_adres {
	void *pntr;
	num *np;
	byte *bp;
} ia;
static num ris;

static inline struct p param(_Bool pntr, int size) { // @suppress("Unused static function") (used in pvm-cmd.c)
#define PVM_PARAM_FAIL r.valid = 0; return r;
	struct p r;
	r.valid = 1;
	switch (ia.bp[pol++]) {
	case P_NUM:
		if (pntr) {
			r.valid = 0;
		} else if (ris <= size + ((poq - 1) << 3)) {
			interrupt(INT_ERRORS_ILLEGAL_MEMORY);
			PVM_PARAM_FAIL
		} else if (size == 8) {
			r.p.n = ia.np[poq++];
		} else if (size == 4) {
			r.p.dw = *(double_word*) (ia.np + poq++);
		} else if (size == 2) {
			r.p.w = *(word*) (ia.np + poq++);
		} else if (size == 1) {
			r.p.b = *(byte*) (ia.np + poq++);
		} else {
			abort();
		}
		break;
	case P_REG:
		if (pntr) {
			r.p.np = &pvm.regs[ia.bp[poh--]];
		} else {
			r.p.n = pvm.regs[ia.bp[poh--]];
		}
		break;
	case P_NUM_NUM: {
		if (ris <= size + ((poq - 1) << 3)) {
			interrupt(INT_ERRORS_ILLEGAL_MEMORY);
			PVM_PARAM_FAIL
		}
		num adr = ia.np[poq] + ia.np[poq + 1];
		struct memory *mem = chk(adr);
		if (!mem) {
			PVM_PARAM_FAIL
		}
		poq += 2;
		if (pntr) {
			r.p.np = mem->offset + adr;
		} else if (size == 8) {
			r.p.n = *(num*) (mem->offset + adr);
		} else if (size == 4) {
			r.p.dw = *(double_word*) (mem->offset + adr);
		} else if (size == 2) {
			r.p.w = *(word*) (mem->offset + adr);
		} else if (size == 1) {
			r.p.b = *(byte*) (mem->offset + adr);
		} else {
			abort();
		}
		break;
	}
	case P_REG_NUM:
	case P_NUM_REG: {
		if (ris <= size + ((poq - 1) << 3)) {
			interrupt(INT_ERRORS_ILLEGAL_MEMORY);
			PVM_PARAM_FAIL
		}
		num adr = ia.np[poq++] + pvm.regs[ia.bp[poh--]];
		struct memory *mem = chk(adr);
		if (!mem) {
			PVM_PARAM_FAIL
		}
		if (pntr) {
			r.p.np = mem->offset + adr;
		} else if (size == 8) {
			r.p.n = *(num*) (mem->offset + adr);
		} else if (size == 4) {
			r.p.dw = *(double_word*) (mem->offset + adr);
		} else if (size == 2) {
			r.p.w = *(word*) (mem->offset + adr);
		} else if (size == 1) {
			r.p.b = *(byte*) (mem->offset + adr);
		} else {
			abort();
		}
		break;
	}
	case P_REG_REG: {
		if (ris <= size + ((poq - 1) << 3)) {
			interrupt(INT_ERRORS_ILLEGAL_MEMORY);
			PVM_PARAM_FAIL
		}
		num adr = pvm.regs[ia.bp[poh]] + pvm.regs[ia.bp[poh - 1]];
		struct memory *mem = chk(adr);
		if (!mem) {
			PVM_PARAM_FAIL
		}
		poh -= 1;
		if (pntr) {
			r.p.np = mem->offset + adr;
		} else if (size == 8) {
			r.p.n = *(num*) (mem->offset + adr);
		} else if (size == 4) {
			r.p.dw = *(double_word*) (mem->offset + adr);
		} else if (size == 2) {
			r.p.w = *(word*) (mem->offset + adr);
		} else if (size == 1) {
			r.p.b = *(byte*) (mem->offset + adr);
		} else {
			abort();
		}
		break;
	}
	default:
		r.valid = 0;
	}
	return r;
#undef PVM_PARAM_FAIL
}

static inline void exec() {
	struct memory *ipmem = chk(pvm.ip);
	ris = ipmem->end - pvm.ip;
	if (ris < 8) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	ia.pntr = ipmem->offset + pvm.ip;
	pol = 1;
	poh = 7;
	poq = 1;
	cmds[ia.bp[0]]();
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
		pvm.errno = PE_OUT_OF_MEMORY;
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
PVM_SI_PREFIX struct memory* realloc_memory(num adr, num newsize) {
	struct memory *mem = chk(adr);
	if (!mem) {
		return NULL;
	}
	if ((mem->start != adr) || (adr & MEM_NO_RESIZE)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
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
			// index can not be zero, because all addresses are above the PVM address
			for (int ii = index - 1; 1; ii--) {
				if (memory[ii].start == -1) {
					continue;
				}
				maxsize = memory[i].start - memory[ii].end
						- (ADRESS_HOLE_MINIMUM_SIZE << 1);
				if (maxsize >= newsize) {
					num start = (maxsize - newsize) >> 1;
					mem->start = start;
					mem->end = start + newsize;
					mem->offset = new_pntr - start;
					return mem;
				}
				struct memory *res = alloc_memory2(new_pntr, newsize, mem->flags);
				mem->start = -1;
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
PVM_SI_PREFIX void free_memory(num adr) {
	struct memory *mem = chk(adr);
	if (!mem) {
		return;
	}
	if (mem->start != adr || adr == REGISTER_START) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	free(mem->offset + mem->start);
	mem->start = -1;
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
