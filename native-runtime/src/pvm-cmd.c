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
#ifndef PVM
#	error "this file should be included inside of pvm-virtual-mashine.c"
#endif // PVM

#define FP_SIGN_BIT    0x8000000000000000UL

#define QUIET_NAN_MASK 0x0008000000000000UL

#define is_non_normal(raw) (((raw) & 0x7FF0000000000000UL) == 0x7FF0000000000000UL)

#define is_normal(raw) (((raw) & 0x7FF0000000000000UL) != 0x7FF0000000000000UL)

#define is_quiet_nan(raw) (((raw) & 0x7FFFFFFFFFFFFFFFUL) >= 0x7FF8000000000000UL)

#define is_quiet_nan_known_non_normal(raw) (((raw) & 0x0008000000000000UL) != 0UL)

#define is_any_nan(raw) (((raw) & 0x7FFFFFFFFFFFFFFFUL) > 0x7FF0000000000000UL)

#define is_any_nan_known_non_normal(raw) (((raw) & 0x000FFFFFFFFFFFFFUL) > 0x0000000000000000UL)

#define is_signal_nan_known_nan(raw) (((raw) & QUIET_NAN_MASK) == 0) \

#define is_signal_nan(raw) (\
		is_any_nan(raw) \
		&& is_signal_nan_known_nan(raw) \
)

static void c_ill() {
	interrupt(INT_ERROR_UNKNOWN_COMMAND, 0);
}

#define check_chaged(arg0, arg1) \
		if (p2.changed) { \
			param_byte_value_index = 7; \
			param_param_type_index = 2; \
			param_num_value_index = 1; \
			p1 = param(arg0, arg1); \
			if (!p1.valid) { \
				return; \
			} \
		}

#define incIPVal (param_num_value_index << 3)
#define incIP0(postfix) pvm.ip += (param_num_value_index postfix) << 3;
#define incIP incIP0()
#define incIPAddOneNum incIP0(+ 1)

#define doJmp pvm.ip += *ia.np >> 16;

static void c_extern() {
	struct memory *mem = chk(pvm.ip, 0).mem;
	struct pvm_extern_call *ext = mem->externs;
	if ((mem->flags & MEM_EXTERN) && ext) {
		num val = pvm.ip - mem->start;
		for (; ext->offset != -1; ext++) {
			if (val == ext->offset) {
				num oldip = pvm.ip;
				int exitnum = ext->func();
				if (exitnum != -1) {
					longjmp(call_pvm_env, (exitnum & 0xFF) + 1);
				}
				if (pvm.ip == oldip) {
					c_ret();
				}
				return;
			}
		}
	}
	c_ill();
}

static void c_mvb() {
	struct p p1 = param(1, 1);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 1);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 1)
	*p1.p.bp = p2.p.b;
	incIP
}
static void c_mvw() {
	struct p p1 = param(1, 2);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 2);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 2)
	*p1.p.wp = p2.p.w;
	incIP
}
static void c_mvdw() {
	struct p p1 = param(1, 4);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 4);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 4)
	*p1.p.dwp = p2.p.dw;
	incIP
}
static void c_mov() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	*p1.p.np = p2.p.n;
	incIP
}
static void c_lea() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	*p1.p.np = pvm.ip + p2.p.n;
	incIP
}
static void c_mvad() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	num old_num_index = param_num_value_index;
	if (remain_instruct_space <= ((old_num_index) << 3)) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	check_chaged(1, 8)
	*p1.p.np = p2.p.n + ia.np[old_num_index];
	incIPAddOneNum
}
static void c_swap() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	num tmp = *p1.p.np;
	*p1.p.np = *p2.p.np;
	*p2.p.np = tmp;
	incIP
}

static void c_or() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(0, 8)
	if (*p1.p.np | p2.p.n) {
		pvm.status &= ~S_ZERO;
	} else {
		pvm.status |= S_ZERO;
	}
	*p1.p.np |= p2.p.n;
	incIP
}
static void c_and() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(0, 8)
	if (*p1.p.np & p2.p.n) {
		pvm.status &= ~S_ZERO;
	} else {
		pvm.status |= S_ZERO;
	}
	*p1.p.np &= p2.p.n;
	incIP
}
static void c_xor() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(0, 8)
	if (*p1.p.np ^ p2.p.n) {
		pvm.status &= ~S_ZERO;
	} else {
		pvm.status |= S_ZERO;
	}
	*p1.p.np ^= p2.p.n;
	incIP
}
static void c_not() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	if (*p1.p.np == -1) {
		pvm.status |= S_ZERO;
	} else {
		pvm.status &= ~S_ZERO;
	}
	*p1.p.np = ~*p1.p.np;
	incIP
}
static void c_lsh() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 1);
	if (!p2.valid) {
		return;
	}
	check_chaged(0, 8)
	if (((*p1.p.np << p2.p.n) >> p2.p.n) == *p1.p.np) {
		pvm.status &= ~S_OVERFLOW;
	} else {
		pvm.status |= S_OVERFLOW;
	}
	*p1.p.np <<= p2.p.n;
	incIP
}
static void c_rash() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 1);
	if (!p2.valid) {
		return;
	}
	check_chaged(0, 8)
	if (((*p1.p.np >> p2.p.n) << p2.p.n) == p1.p.n) {
		pvm.status &= ~S_OVERFLOW;
	} else {
		pvm.status |= S_OVERFLOW;
	}
	*p1.p.np >>= p2.p.n;
	incIP
}
static void c_rlsh() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 1);
	if (!p2.valid) {
		return;
	}
	check_chaged(0, 8)
	if (((*p1.p.up >> p2.p.n) << p2.p.n) == *p1.p.up) {
		pvm.status &= ~S_OVERFLOW;
	} else {
		pvm.status |= S_OVERFLOW;
	}
	*p1.p.up >>= p2.p.n;
	incIP
}

static void c_add() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(0, 8)
	num op1 = *p1.p.np;
	*(unum*) p1.p.np += (unum) p2.p.n;
	if (((*p1.p.np < 0) & (p2.p.n > 0) & (op1 > 0))
			|| ((*p1.p.np > 0) & (p2.p.n < 0) & (op1 < 0))) {
		pvm.status = (pvm.status & ~(S_ZERO)) | S_OVERFLOW;
	} else if (*p1.p.np) {
		pvm.status = (pvm.status & ~(S_OVERFLOW | S_ZERO));
	} else {
		pvm.status = (pvm.status & ~(S_OVERFLOW)) | S_ZERO;
	}
	incIP
}
static void c_sub() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	num op1 = *p1.p.np;
	*p1.p.np -= p2.p.n;
	if (((*p1.p.np < 0) & (p2.p.n < 0) & (op1 > 0))
			|| ((*p1.p.np > 0) & (p2.p.n > 0) & (op1 < 0))) {
		pvm.status = (pvm.status & ~(S_ZERO)) | S_OVERFLOW;
	} else if (*p1.p.np) {
		pvm.status = (pvm.status & ~(S_OVERFLOW | S_ZERO));
	} else {
		pvm.status = (pvm.status & ~(S_OVERFLOW)) | S_ZERO;
	}
	incIP
}
static void c_mul() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	*p1.p.np *= p2.p.n;
	if (*p1.p.np) {
		pvm.status &= ~S_ZERO;
	} else {
		pvm.status |= S_ZERO;
	}
	incIP
}
static void c_div() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	num a = *p1.p.np;
	num b = *p2.p.np;
	if (!b) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.np = a / b;
	*p2.p.np = a % b;
	incIP
}
static void c_neg() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	num np1 = -*p1.p.np;
	if (np1 < 0 && *p1.p.np < 0) {
		pvm.status |= S_OVERFLOW;
	} else {
		pvm.status &= ~S_OVERFLOW;
	}
	*p1.p.np = np1;
	incIP
}
static void c_addc() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	num np1 = *p1.p.np + p2.p.n;
	if (pvm.status & S_OVERFLOW) {
		np1++;
	}
	if (np1 < 0) {
		if (*p1.p.np >= 0 && p2.p.n >= 0) {
			pvm.status |= S_OVERFLOW;
		} else {
			pvm.status &= ~S_OVERFLOW;
		}
	} else {
		if (*p1.p.np < 0 && p2.p.n < 0) {
			pvm.status |= S_OVERFLOW;
		} else {
			pvm.status &= ~S_OVERFLOW;
		}
	}
	*p1.p.np = np1;
	incIP
}
static void c_subc() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	num np1 = *p1.p.np + p2.p.n;
	if (pvm.status & S_OVERFLOW) {
		np1++;
	}
	if (np1 > 0) {
		if (*p1.p.np < 0 && p2.p.n >= 0) {
			pvm.status |= S_OVERFLOW;
		} else {
			pvm.status &= ~S_OVERFLOW;
		}
	} else {
		if (*p1.p.np > 0 && p2.p.n < 0) {
			pvm.status |= S_OVERFLOW;
		} else {
			pvm.status &= ~S_OVERFLOW;
		}
	}
	*p1.p.np = np1;
	incIP
}
static void c_inc() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	if (*p1.p.np == NUM_MAX_VALUE) {
		pvm.status = (pvm.status & ~(S_ZERO)) | (S_OVERFLOW);
	} else if (*p1.p.np == -1) {
		pvm.status = (pvm.status & ~(S_OVERFLOW)) | (S_ZERO);
	} else {
		pvm.status &= ~(S_ZERO | S_OVERFLOW);
	}
	(*p1.p.np)++;
	incIP
}
static void c_dec() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	if (*p1.p.np == NUM_MIN_VALUE) {
		pvm.status = (pvm.status & ~(S_ZERO)) | (S_OVERFLOW);
	} else if (*p1.p.np == 1) {
		pvm.status = (pvm.status & ~(S_OVERFLOW)) | (S_ZERO);
	} else {
		pvm.status &= ~(S_ZERO | S_OVERFLOW);
	}
	(*p1.p.np)--;
	incIP
}

static void c_jmperr() {
	if (pvm.err) {
		doJmp
	} else {
		incIP
	}
}
static void c_jmpeq() {
	if (pvm.status & S_EQUAL) {
		doJmp
	} else {
		incIP
	}
}
static void c_jmpne() {
	if (pvm.status & S_EQUAL) {
		incIP
	} else {
		doJmp
	}
}
static void c_jmpgt() {
	if (pvm.status & S_GREATHER) {
		doJmp
	} else {
		incIP
	}
}
static void c_jmpge() {
	if (pvm.status & (S_EQUAL | S_GREATHER)) {
		doJmp
	} else {
		incIP
	}
}
static void c_jmplt() {
	if (pvm.status & S_LOWER) {
		doJmp
	} else {
		incIP
	}
}
static void c_jmple() {
	if (pvm.status & (S_EQUAL | S_LOWER)) {
		doJmp
	} else {
		incIP
	}
}
static void c_jmpcs() {
	if (pvm.status & S_OVERFLOW) {
		doJmp
	} else {
		incIP
	}
}
static void c_jmpcc() {
	if (pvm.status & S_OVERFLOW) {
		incIP
	} else {
		doJmp
	}
}
static void c_jmpzs() {
	if (pvm.status & S_ZERO) {
		doJmp
	} else {
		incIP
	}
}
static void c_jmpzc() {
	if (pvm.status & S_ZERO) {
		incIP
	} else {
		doJmp
	}
}
static void c_jmpnan() {
	if (pvm.status & S_NAN) {
		doJmp
	} else {
		incIP
	}
}
static void c_jmpan() {
	if (pvm.status & S_NAN) {
		incIP
	} else {
		doJmp
	}
}
static void c_jmpab() {
	if (pvm.status & S_ALL_BITS) {
		doJmp
	} else {
		incIP
	}
}
static void c_jmpsb() {
	if (pvm.status & S_SOME_BITS) {
		doJmp
	} else {
		incIP
	}
}
static void c_jmpnb() {
	if (pvm.status & S_NONE_BITS) {
		doJmp
	} else {
		incIP
	}
}

static void c_jmp() {
	doJmp
}
static void c_jmpo() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	pvm.ip += p1.p.n + ia.np[param_num_value_index];
}
static void c_jmpno() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	pvm.ip += p1.p.n;
}

static void c_int() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	interrupt(p1.p.n, incIPVal);
}
static void c_iret() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct memory_check mem = chk(p1.p.n, 0);
	if (!mem.mem) {
		return;
	}
	if ((mem.mem->flags & MEM_INT) == 0) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
	}
	memcpy(&pvm, mem.mem->offset + p1.p.n, 128);
	free_memory(p1.p.n);
}
static void c_call() {
	struct memory_check mem = chk(pvm.sp, 8);
	if (!mem.mem) {
		return;
	}
	*(num*) (mem.mem->offset + pvm.sp) = pvm.ip + incIPVal;
	pvm.sp += 8;
	doJmp
}
static void c_calo() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERROR_ILLEGAL_MEMORY, 0);
		return;
	}
	struct memory_check mem = chk(pvm.sp, 8);
	if (!mem.mem) {
		return;
	}
	*(num*) (mem.mem->offset + pvm.sp) = pvm.ip + incIPVal;
	pvm.sp += 8;
	pvm.ip += p1.p.n + ia.np[param_num_value_index];
}
static void c_calno() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct memory_check mem = chk(pvm.sp, 8);
	if (!mem.mem) {
		return;
	}
	*(num*) (mem.mem->offset + pvm.sp) = pvm.ip + incIPVal;
	pvm.sp += 8;
	pvm.ip += p1.p.n;
}
static void c_ret() {
	struct memory_check mem = chk(pvm.sp - 8, 8);
	if (!mem.mem) {
		return;
	}
	pvm.sp -= 8;
	pvm.ip = *(num*) (mem.mem->offset + pvm.sp);
}
static void c_push() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct memory_check mem = chk(pvm.sp, 8);
	if (!mem.mem) {
		return;
	}
	*(num*) (mem.mem->offset + pvm.sp) = p1.p.n;
	pvm.sp += 8;
	incIP
}
static void c_pop() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct memory_check mem = chk(pvm.sp - 8, 8);
	if (!mem.mem) {
		return;
	}
	pvm.sp -= 8;
	*p1.p.np = *(num*) (mem.mem->offset + pvm.sp);
	incIP
}
static void c_pushblk() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	if (p2.p.n < 0) {
		interrupt(INT_ERROR_UNKNOWN_COMMAND, 0);
		return;
	}
	struct memory *smem = chk(pvm.sp, p2.p.n).mem;
	if (!smem) {
		return;
	}
	if (pvm.sp < smem->start || pvm.sp > smem->end - p2.p.n) {
		printf(
				"error sp=0x%lX=%ld push_size=0x%lX=%ld start=0x%lX=%ld end=0x%lX=%ld mem_len=0x%lX=%ld\n",
				pvm.sp, pvm.sp, p2.p.n, p2.p.n, smem->start, smem->start,
				smem->end, smem->end, smem->end - smem->start,
				smem->end - smem->start);
		fflush(NULL);
		abort();
	}
	struct memory_check pmem = chk(p1.p.n, p2.p.n);
	if (!pmem.mem) {
		return;
	}
	if (pmem.changed) {
		smem = chk(pvm.sp, p2.p.n).mem;
		if (!smem) {
			return;
		}
	}
	memmove(smem->offset + pvm.sp, pmem.mem->offset + p1.p.n, p2.p.n);
	pvm.sp += p2.p.n;
	incIP
}
static void c_popblk() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	if (p1.p.n < 0) {
		interrupt(INT_ERROR_UNKNOWN_COMMAND, 0);
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	num nsp = pvm.sp - p2.p.n;
	struct memory *smem = chk(nsp, p2.p.n).mem;
	if (!smem) {
		return;
	}
	struct memory_check pmem = chk(p1.p.n, p2.p.n);
	if (!pmem.mem) {
		return;
	}
	if (pmem.changed) {
		smem = chk(pvm.sp, 0).mem;
		if (!smem) {
			return;
		}
	}
	pvm.sp = nsp;
	memmove(pmem.mem->offset + p1.p.n, smem->offset + nsp, p2.p.n);
	incIP
}

static void c_cmp() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	if (p1.p.n > p2.p.n) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_LOWER)) | S_GREATHER;
	} else if (p1.p.n < p2.p.n) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_GREATHER)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_GREATHER | S_LOWER)) | S_EQUAL;
	}
	incIP
}
static void c_bcp() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	unum and = p1.p.u & p2.p.u;
	if (and == 0) {
		pvm.status = (pvm.status & ~(S_ALL_BITS | S_SOME_BITS)) | S_NONE_BITS;
	} else if (and == p2.p.u) {
		pvm.status = (pvm.status & ~S_NONE_BITS) | (S_ALL_BITS | S_SOME_BITS);
	} else {
		pvm.status = (pvm.status & ~(S_ALL_BITS | S_NONE_BITS)) | S_SOME_BITS;
	}
	incIP
}
static void c_cmpl() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	if ((p1.p.n & p2.p.n) == 0) {
		pvm.status = (pvm.status & ~(S_SOME_BITS | S_ALL_BITS)) | (S_NONE_BITS);
	} else if ((p1.p.n & p2.p.n) == p2.p.n) {
		pvm.status = (pvm.status & ~(S_NONE_BITS)) | (S_SOME_BITS | S_ALL_BITS);
	} else {
		pvm.status = (pvm.status & ~(S_NONE_BITS | S_ALL_BITS)) | (S_SOME_BITS);
	}
	incIP
}
static void c_cmpfp() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	if (is_any_nan(p1.p.n)) {
		if (is_signal_nan_known_nan(p1.p.n) || is_signal_nan(p2.p.n)) {
			signal_nan: interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		} else {
			quiet_nan: pvm.status = (pvm.status
					& ~(S_GREATHER | S_EQUAL | S_LOWER)) | S_NAN;
		}
	} else if (is_any_nan(p2.p.n)) {
		if (is_signal_nan_known_nan(p2.p.n)) {
			goto signal_nan;
		} else {
			goto quiet_nan;
		}
	} else if (p1.p.fpn > p2.p.fpn) {
		pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_LOWER)) | S_GREATHER;
	} else if (p1.p.fpn > p2.p.fpn) {
		pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_GREATHER)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_NAN | S_GREATHER | S_LOWER)) | S_EQUAL;
	}
	incIP
}
static void c_cmpsfp() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	if (is_any_nan(p1.p.n) || is_any_nan(p2.p.n)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
	} else if (p1.p.fpn > p2.p.fpn) {
		pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_LOWER)) | S_GREATHER;
	} else if (p1.p.fpn > p2.p.fpn) {
		pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_GREATHER)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_NAN | S_GREATHER | S_LOWER)) | S_EQUAL;
	}
	incIP
}
static void c_cmpqfp() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	if (is_any_nan(p1.p.n) || is_any_nan(p2.p.n)) {
		pvm.status = (pvm.status & ~(S_GREATHER | S_EQUAL | S_LOWER)) | S_NAN;
	} else if (p1.p.fpn > p2.p.fpn) {
		pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_LOWER)) | S_GREATHER;
	} else if (p1.p.fpn > p2.p.fpn) {
		pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_GREATHER)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_NAN | S_GREATHER | S_LOWER)) | S_EQUAL;
	}
	incIP
}
static void c_chkfp() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	if (is_non_normal(p1.p.n)) {
		if (is_any_nan_known_non_normal(p1.p.n)) {
			if (is_signal_nan_known_nan(p1.p.n)) {
				interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
			} else {
				pvm.status = (pvm.status & ~(S_GREATHER | S_EQUAL | S_LOWER))
						| S_NAN;
			}
		} else if (p1.p.n > 0) {
			pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_LOWER))
					| S_GREATHER;
		} else {
			pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_GREATHER))
					| S_LOWER;
		}
	} else {
		pvm.status = (pvm.status & ~(S_NAN | S_GREATHER | S_LOWER)) | S_EQUAL;
	}
	incIP
}
static void c_chksfp() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	if (is_non_normal(p1.p.n)) {
		if (is_any_nan_known_non_normal(p1.p.n)) {
			interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		} else if (p1.p.n > 0) {
			pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_LOWER))
					| S_GREATHER;
		} else {
			pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_GREATHER))
					| S_LOWER;
		}
	} else {
		pvm.status = (pvm.status & ~(S_NAN | S_GREATHER | S_LOWER)) | S_EQUAL;
	}
	incIP
}
static void c_chkqfp() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	if (is_non_normal(p1.p.n)) {
		if (is_any_nan_known_non_normal(p1.p.n)) {
			pvm.status = (pvm.status & ~(S_GREATHER | S_EQUAL | S_LOWER))
					| S_NAN;
		} else if (p1.p.n > 0) {
			pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_LOWER))
					| S_GREATHER;
		} else {
			pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_GREATHER))
					| S_LOWER;
		}
	} else {
		pvm.status = (pvm.status & ~(S_NAN | S_GREATHER | S_LOWER)) | S_EQUAL;
	}
	incIP
}
static void c_cmpu() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	if (p1.p.u > p2.p.u) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_LOWER)) | S_GREATHER;
	} else if (p1.p.u < p2.p.u) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_GREATHER)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_GREATHER | S_LOWER)) | S_EQUAL;
	}
	incIP
}
static void c_cmpb() {
	struct p p1 = param(1, 16);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 16);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 16)
	if (*p1.p.bigp > *p2.p.bigp) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_LOWER)) | S_GREATHER;
	} else if (*p1.p.bigp < *p2.p.bigp) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_GREATHER)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_GREATHER | S_LOWER)) | S_EQUAL;
	}
	incIP
}
static void c_sgn() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	if (p1.p.n > 0) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_LOWER)) | S_GREATHER;
	} else if (p1.p.n < 0) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_GREATHER)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_GREATHER | S_LOWER)) | S_EQUAL;
	}
	incIP
}
static void c_sgnfp() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	if (is_any_nan(p1.p.n)) {
		if (is_signal_nan_known_nan(p1.p.n)) {
			interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		} else {
			pvm.status = (pvm.status & ~(S_GREATHER | S_EQUAL | S_LOWER))
					| S_NAN;
		}
	} else if (p1.p.n > 0) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_LOWER | S_NAN)) | S_GREATHER;
	} else if (p1.p.n < 0) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_GREATHER | S_NAN)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_GREATHER | S_LOWER | S_NAN)) | S_EQUAL;
	}
	incIP
}
static void c_sgnsfp() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	if (is_any_nan(p1.p.n)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
	} else if (p1.p.n > 0) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_LOWER | S_NAN)) | S_GREATHER;
	} else if (p1.p.n < 0) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_GREATHER | S_NAN)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_GREATHER | S_LOWER | S_NAN)) | S_EQUAL;
	}
	incIP
}
static void c_sgnqfp() {
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	if (is_any_nan(p1.p.n)) {
		pvm.status = (pvm.status & ~(S_GREATHER | S_EQUAL | S_LOWER)) | S_NAN;
	} else if (p1.p.n > 0) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_LOWER | S_NAN)) | S_GREATHER;
	} else if (p1.p.n < 0) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_GREATHER | S_NAN)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_GREATHER | S_LOWER | S_NAN)) | S_EQUAL;
	}
	incIP
}
static void c_fptn() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	if (is_non_normal(*p1.p.np)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.np = *p1.p.fpnp;
	incIP
}
static void c_ntfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	*p1.p.fpnp = *p1.p.np;
	incIP
}

static void c_addfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_signal_nan(*p1.p.np) || is_signal_nan(p2.p.n)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.fpnp += p2.p.fpn;
	incIP
}
static void c_subfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_signal_nan(*p1.p.np) || is_signal_nan(p2.p.n)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.fpnp -= p2.p.fpn;
	incIP
}
static void c_mulfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_signal_nan(*p1.p.np) || is_signal_nan(p2.p.n)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.fpnp *= p2.p.fpn;
	incIP
}
static void c_divfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_signal_nan(*p1.p.np) || is_signal_nan(p2.p.n)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.fpnp /= p2.p.fpn;
	incIP
}
static void c_negfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	if (is_signal_nan(*p1.p.np)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.fpnp = -*p1.p.fpnp;
	incIP
}
static void c_modfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_signal_nan(*p1.p.np) || is_signal_nan(p2.p.n)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.fpnp = fmod(*p1.p.fpnp, p2.p.fpn);
	incIP
}

static void c_addqfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_signal_nan(*p1.p.np) || is_signal_nan(p2.p.n)) {
		union param qres;
		qres.n = *p1.p.np;
		if (is_signal_nan(qres.n)) {
			qres.n |= QUIET_NAN_MASK;
		}
		if (is_signal_nan(p2.p.n)) {
			p2.p.n |= QUIET_NAN_MASK;
		}
		qres.fpn -= p2.p.fpn;
		*p1.p.np = qres.n & (~QUIET_NAN_MASK);
		return;
	}
	*p1.p.fpnp += p2.p.fpn;
	incIP
}
static void c_subqfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_signal_nan(*p1.p.np) || is_signal_nan(p2.p.n)) {
		union param qres;
		qres.n = *p1.p.np;
		if (is_signal_nan(qres.n)) {
			qres.n |= QUIET_NAN_MASK;
		}
		if (is_signal_nan(p2.p.n)) {
			p2.p.n |= QUIET_NAN_MASK;
		}
		qres.fpn -= p2.p.fpn;
		*p1.p.np = qres.n & (~QUIET_NAN_MASK);
		return;
	}
	*p1.p.fpnp -= p2.p.fpn;
	incIP
}
static void c_mulqfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_signal_nan(*p1.p.np) || is_signal_nan(p2.p.n)) {
		union param qres;
		qres.n = *p1.p.np;
		if (is_signal_nan(qres.n)) {
			qres.n |= QUIET_NAN_MASK;
		}
		if (is_signal_nan(p2.p.n)) {
			p2.p.n |= QUIET_NAN_MASK;
		}
		qres.fpn *= p2.p.fpn;
		*p1.p.np = qres.n & (~QUIET_NAN_MASK);
		return;
	}
	*p1.p.fpnp *= p2.p.fpn;
	incIP
}
static void c_divqfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_signal_nan(*p1.p.np) || is_signal_nan(p2.p.n)) {
		union param qres;
		qres.n = *p1.p.np;
		if (is_signal_nan(qres.n)) {
			qres.n |= QUIET_NAN_MASK;
		}
		if (is_signal_nan(p2.p.n)) {
			p2.p.n |= QUIET_NAN_MASK;
		}
		qres.fpn /= p2.p.fpn;
		*p1.p.np = qres.n & (~QUIET_NAN_MASK);
		return;
	}
	*p1.p.fpnp /= p2.p.fpn;
	incIP
}
static void c_negqfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	*p1.p.np ^= FP_SIGN_BIT;
	incIP
}
static void c_modqfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_signal_nan(*p1.p.np) || is_signal_nan(p2.p.n)) {
		union param qres;
		qres.n = *p1.p.np;
		if (is_signal_nan(qres.n)) {
			qres.n |= QUIET_NAN_MASK;
		}
		if (is_signal_nan(p2.p.n)) {
			p2.p.n |= QUIET_NAN_MASK;
		}
		qres.fpn =  fmod(qres.fpn, p2.p.fpn);
		*p1.p.np = qres.n & (~QUIET_NAN_MASK);
		return;
	}
	*p1.p.fpnp = fmod(*p1.p.fpnp, p2.p.fpn);
	incIP
}

static void c_addsfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_any_nan(*p1.p.np) || is_any_nan(p2.p.n)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.fpnp += p2.p.fpn;
	incIP
}
static void c_subsfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_any_nan(*p1.p.np) || is_any_nan(p2.p.n)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.fpnp -= p2.p.fpn;
	incIP
}
static void c_mulsfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_any_nan(*p1.p.np) || is_any_nan(p2.p.n)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.fpnp *= p2.p.fpn;
	incIP
}
static void c_divsfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_any_nan(*p1.p.np) || is_any_nan(p2.p.n)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.fpnp /= p2.p.fpn;
	incIP
}
static void c_negsfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	if (is_any_nan(*p1.p.np)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.fpnp = -*p1.p.fpnp;
	incIP
}
static void c_modsfp() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (is_any_nan(*p1.p.np) || is_any_nan(p2.p.n)) {
		interrupt(INT_ERROR_ARITHMETIC_ERROR, 0);
		return;
	}
	*p1.p.fpnp = fmod(*p1.p.fpnp, p2.p.fpn);
	incIP
}

static void c_uadd() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	unum oldp1 = *p1.p.up;
	*p1.p.up += p2.p.u;
	if (oldp1 & p2.p.u & 0x8000000000000000ULL) {
		if (*p1.p.up) {
			pvm.status = (pvm.status & ~(S_ZERO)) | (S_OVERFLOW);
		} else {
			pvm.status = (pvm.status) | (S_OVERFLOW | S_ZERO);
		}
	} else if (*p1.p.up) {
		pvm.status = (pvm.status & ~(S_OVERFLOW | S_ZERO));
	} else {
		pvm.status = (pvm.status & ~(S_OVERFLOW)) | (S_ZERO);
	}
	incIP
}
static void c_usub() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	unum oldp1 = *p1.p.up;
	*p1.p.up = oldp1 - p2.p.u;
	if (oldp1 < p2.p.u) {
		if (*p1.p.up) {
			pvm.status = (pvm.status & ~(S_ZERO)) | (S_OVERFLOW);
		} else {
			pvm.status = (pvm.status) | (S_OVERFLOW | S_ZERO);
		}
	} else if (*p1.p.up) {
		pvm.status = (pvm.status & ~(S_OVERFLOW | S_ZERO));
	} else {
		pvm.status = (pvm.status & ~(S_OVERFLOW)) | (S_ZERO);
	}
	incIP
}
static void c_umul() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	*p1.p.up *= p2.p.u;
	incIP
}
static void c_udiv() {
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	unum a = *p1.p.up, b = *p2.p.up;
	*p1.p.up = a / b;
	*p2.p.up = a % b;
	incIP
}
static void c_badd() {
	struct p p1 = param(1, 16);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 16);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 16)
	*p1.p.bigp = *p1.p.bigp + *p2.p.bigp;
	incIP
}
static void c_bsub() {
	struct p p1 = param(1, 16);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 16);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 16)
	*p1.p.bigp = *p1.p.bigp - *p2.p.bigp;
	incIP
}
static void c_bmul() {
	struct p p1 = param(1, 16);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 16);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 16)
	*p1.p.bigp = *p1.p.bigp * *p2.p.bigp;
	incIP
}
static void c_bdiv() {
	struct p p1 = param(1, 16);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 16);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 16)
	*p1.p.bigp = *p1.p.bigp * *p2.p.bigp;
	*p1.p.bigp = *p1.p.bigp * *p2.p.bigp;
	incIP
}
static void c_bneg() {
	struct p p1 = param(1, 16);
	if (!p1.valid) {
		return;
	}
	*p1.p.bigp = -*p1.p.bigp;
	incIP
}
