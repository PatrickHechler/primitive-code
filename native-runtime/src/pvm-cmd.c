#ifndef PVM
#	error "this file should be included inside of pvm-virtual-mashine.c"
#endif // PVM

static void c_ill() /* --- */{
	interrupt(INT_ERRORS_UNKNOWN_COMMAND);
}

#define check_chaged(arg0, arg1) \
		if (p2.changed) { \
			param_byte_value_index = 7; \
			param_param_type_index = 1; \
			param_num_value_index = 1; \
			p1 = param(arg0, arg1); \
			if (!p1.valid) { \
				return; \
			} \
		}

#define incIP0(postfix) pvm.ip += (param_num_value_index postfix) << 3;
#define incIP incIP0()
#define incIPAddOneNum incIP0(+ 1)

static void c_mvb() /* 0x01 */{
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
static void c_mvw() /* 0x02 */{
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
static void c_mvdw() /* 0x03 */{
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
static void c_mov() /* 0x04 */{
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
static void c_lea() /* 0x05 */{
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
static void c_mvad() /* 0x06 */{
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
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	check_chaged(1, 8)
	*p1.p.np = p2.p.n + ia.np[old_num_index];
	incIPAddOneNum
}
static void c_swap() /* 0x07 */{
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

static void c_add() /* 0x10 */{
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
	*p1.p.np += p2.p.n;
	if (((*p1.p.np < 0) & (*p2.p.np > 0) & (op1 > 0))
			|| ((*p1.p.np > 0) & (*p2.p.np < 0) & (op1 < 0))) {
		pvm.status = (pvm.status & ~(S_ZERO)) | S_OVERFLOW;
	} else if (*p1.p.np) {
		pvm.status = (pvm.status & ~(S_OVERFLOW | S_ZERO));
	} else {
		pvm.status = (pvm.status & ~(S_OVERFLOW)) | S_ZERO;
	}
	incIP
}
static void c_sub() /* 0x11 */{
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
	if (((*p1.p.np < 0) & (*p2.p.np < 0) & (op1 > 0))
			|| ((*p1.p.np > 0) & (*p2.p.np > 0) & (op1 < 0))) {
		pvm.status = (pvm.status & ~(S_ZERO)) | S_OVERFLOW;
	} else if (*p1.p.np) {
		pvm.status = (pvm.status & ~(S_OVERFLOW | S_ZERO));
	} else {
		pvm.status = (pvm.status & ~(S_OVERFLOW)) | S_ZERO;
	}
	incIP
}
static void c_mul() /* 0x12 */{
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
static void c_div() /* 0x13 */{
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
		interrupt(INT_ERRORS_ARITHMETIC_ERROR);
		return;
	}
	*p1.p.np = a / b;
	*p2.p.np = a % b;
	incIP
}
static void c_neg() /* 0x14 */{
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
static void c_addc() /* 0x15 */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	num np1 = *p1.p.np + *p2.p.np;
	if (pvm.status & S_OVERFLOW) {
		np1++;
	}
	if (np1 < 0) {
		if (*p1.p.np >= 0 && *p2.p.np >= 0) {
			pvm.status |= S_OVERFLOW;
		} else {
			pvm.status &= ~S_OVERFLOW;
		}
	} else {
		if (*p1.p.np < 0 && *p2.p.np < 0) {
			pvm.status |= S_OVERFLOW;
		} else {
			pvm.status &= ~S_OVERFLOW;
		}
	}
	*p1.p.np = np1;
	incIP
}
static void c_subc() /* 0x16 */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	num np1 = *p1.p.np + *p2.p.np;
	if (pvm.status & S_OVERFLOW) {
		np1++;
	}
	if (np1 > 0) {
		if (*p1.p.np < 0 && *p2.p.np >= 0) {
			pvm.status |= S_OVERFLOW;
		} else {
			pvm.status &= ~S_OVERFLOW;
		}
	} else {
		if (*p1.p.np > 0 && *p2.p.np < 0) {
			pvm.status |= S_OVERFLOW;
		} else {
			pvm.status &= ~S_OVERFLOW;
		}
	}
	*p1.p.np = np1;
	incIP
}
static void c_inc() /* 0x17 */{
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
static void c_dec() /* 0x18 */{
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
static void c_or() /* 0x19 */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (*p1.p.np | *p2.p.np) {
		pvm.status &= ~S_ZERO;
	} else {
		pvm.status |= S_ZERO;
	}
	*p1.p.np |= *p2.p.np;
	incIP
}
static void c_and() /* 0x1A */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (*p1.p.np & *p2.p.np) {
		pvm.status &= ~S_ZERO;
	} else {
		pvm.status |= S_ZERO;
	}
	*p1.p.np &= *p2.p.np;
	incIP
}
static void c_xor() /* 0x1B */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (*p1.p.np ^ *p2.p.np) {
		pvm.status &= ~S_ZERO;
	} else {
		pvm.status |= S_ZERO;
	}
	*p1.p.np ^= *p2.p.np;
	incIP
}
static void c_not() /* 0x1C */{
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
static void c_lsh() /* 0x1D */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (((*p1.p.np << *p2.p.np) >> *p2.p.np) == *p1.p.np) {
		pvm.status &= ~S_OVERFLOW;
	} else {
		pvm.status |= S_OVERFLOW;
	}
	*p1.p.np <<= *p2.p.np;
	incIP
}
static void c_rash() /* 0x1E */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (((*p1.p.np >> *p2.p.np) << *p2.p.np) == *p1.p.np) {
		pvm.status &= ~S_OVERFLOW;
	} else {
		pvm.status |= S_OVERFLOW;
	}
	*p1.p.np >>= *p2.p.np;
	incIP
}
static void c_rlsh() /* 0x1F */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (((*p1.p.up >> *p2.p.np) << *p2.p.np) == *p1.p.up) {
		pvm.status &= ~S_OVERFLOW;
	} else {
		pvm.status |= S_OVERFLOW;
	}
	*p1.p.up >>= *p2.p.np;
	incIP
}

static void c_jmp() /* 0x20 */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	pvm.ip += ia.np[param_num_value_index];
}
static void c_jmpeq() /* 0x21 */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_EQUAL) {
		pvm.ip += ia.np[param_num_value_index];
	} else {
		incIPAddOneNum
	}
}
static void c_jmpne() /* 0x22 */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_EQUAL) {
		incIPAddOneNum
	} else {
		pvm.ip += ia.np[param_num_value_index];
	}
}
static void c_jmpgt() /* 0x23 */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_GREATHER) {
		pvm.ip += ia.np[param_num_value_index];
	} else {
		incIPAddOneNum
	}
}
static void c_jmpge() /* 0x24 */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & (S_EQUAL | S_GREATHER)) {
		pvm.ip += ia.np[param_num_value_index];
	} else {
		incIPAddOneNum
	}
}
static void c_jmplt() /* 0x25 */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_LOWER) {
		pvm.ip += ia.np[param_num_value_index];
	} else {
		incIPAddOneNum
	}
}
static void c_jmple() /* 0x26 */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & (S_EQUAL | S_LOWER)) {
		pvm.ip += ia.np[param_num_value_index];
	} else {
		incIPAddOneNum
	}
}
static void c_jmpcs() /* 0x27 */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_OVERFLOW) {
		pvm.ip += ia.np[param_num_value_index];
	} else {
		incIPAddOneNum
	}
}
static void c_jmpcc() /* 0x28 */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_OVERFLOW) {
		incIPAddOneNum
	} else {
		pvm.ip += ia.np[param_num_value_index];
	}
}
static void c_jmpzs() /* 0x29 */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_ZERO) {
		pvm.ip += ia.np[param_num_value_index];
	} else {
		incIPAddOneNum
	}
}
static void c_jmpzc() /* 0x2A */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_ZERO) {
		incIPAddOneNum
	} else {
		pvm.ip += ia.np[param_num_value_index];
	}
}
static void c_jmpnan() /* 0x2B */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_NAN) {
		pvm.ip += ia.np[param_num_value_index];
	} else {
		incIPAddOneNum
	}
}
static void c_jmpan() /* 0x2C */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_NAN) {
		incIPAddOneNum
	} else {
		pvm.ip += ia.np[param_num_value_index];
	}
}
static void c_jmpab() /* 0x2D */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_ALL_BITS) {
		pvm.ip += ia.np[param_num_value_index];
	} else {
		incIPAddOneNum
	}
}
static void c_jmpsb() /* 0x2E */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_SOME_BITS) {
		pvm.ip += ia.np[param_num_value_index];
	} else {
		incIPAddOneNum
	}
}
static void c_jmpnb() /* 0x2F */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	if (pvm.status & S_NONE_BITS) {
		pvm.ip += ia.np[param_num_value_index];
	} else {
		incIPAddOneNum
	}
}

static void c_int() /* 0x30 */{
	incIP
	interrupt(pvm.x[0]);
}
static void c_iret() /* 0x31 */{
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct memory_check mem = chk(p1.p.n, 0);
	if (!mem.mem) {
		return;
	}
	if ((mem.mem->flags & MEM_INT) == 0) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
	}
	memcpy(&pvm, mem.mem->offset + p1.p.n, 128);
	free_memory(p1.p.n);
}
static void c_call() /* 0x32 */{
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	struct memory_check mem = chk(pvm.sp, 8);
	if (!mem.mem) {
		return;
	}
	*(num*)(mem.mem->offset + pvm.sp) = pvm.ip;
	pvm.sp += 8;
	pvm.ip += ia.np[param_num_value_index];
}
static void c_calo() /* 0x33 */{
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	if (remain_instruct_space <= ((param_num_value_index + 1) << 3)) {
		interrupt(INT_ERRORS_ILLEGAL_MEMORY);
		return;
	}
	struct memory_check mem = chk(pvm.sp, 8);
	if (!mem.mem) {
		return;
	}
	*(num*)(mem.mem->offset + pvm.sp) = pvm.ip;
	pvm.sp += 8;
	pvm.ip += p1.p.n + ia.np[param_num_value_index];
}
static void c_ret() /* 0x34 */{
	struct memory_check mem = chk(pvm.sp - 8, 8);
	if (!mem.mem) {
		return;
	}
	pvm.sp -= 8;
	pvm.ip = *(num*)(mem.mem->offset + pvm.sp);
}
static void c_push() /* 0x35 */{
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct memory_check mem = chk(pvm.sp, 8);
	if (!mem.mem) {
		return;
	}
	*(num*)(mem.mem->offset + pvm.sp) = p1.p.n;
	pvm.sp += 8;
	incIP
}
static void c_pop() /* 0x36 */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct memory_check mem = chk(pvm.sp - 8, 8);
	if (!mem.mem) {
		return;
	}
	pvm.sp -= 8;
	*p1.p.np = *(num*)(mem.mem->offset + pvm.sp);
	incIP
}

static void c_cmp() /* 0x40 */{
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(0, 8)
	if (p1.p.n > p2.p.n) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_LOWER)) | S_GREATHER;
	} else if (p1.p.n < p2.p.n) {
		pvm.status = (pvm.status & ~(S_EQUAL | S_GREATHER)) | S_LOWER;
	} else {
		pvm.status = (pvm.status & ~(S_GREATHER | S_LOWER)) | S_EQUAL;
	}
	incIP
}
static void c_bcp() /* 0x41 */{
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(0, 8)
	if ((p1.p.n & p2.p.n) == p1.p.n) {
		pvm.status = (pvm.status & ~(S_NONE_BITS)) | (S_SOME_BITS | S_ALL_BITS);
	} else if (p1.p.n < p2.p.n) {
		pvm.status = (pvm.status & ~(S_NONE_BITS | S_ALL_BITS)) | (S_SOME_BITS);
	} else {
		pvm.status = (pvm.status & ~(S_SOME_BITS | S_ALL_BITS)) | (S_NONE_BITS);
	}
	incIP
}

static void c_cmpfp() /* 0x50 */{
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(0, 8)
	if (isnan(p1.p.fpn) || isnan(p2.p.fpn)) {
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
static void c_chkfp() /* 0x51 */{
	struct p p1 = param(0, 8);
	if (!p1.valid) {
		return;
	}
	if (isnan(p1.p.fpn)) {
		pvm.status = (pvm.status & ~(S_GREATHER | S_EQUAL | S_LOWER)) | S_NAN;
	} else {
		switch (isinf(p1.p.fpn)) {
		case 1:
			pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_LOWER))
					| S_GREATHER;
			break;
		case -1:
			pvm.status = (pvm.status & ~(S_NAN | S_EQUAL | S_GREATHER))
					| S_LOWER;
			break;
		default:
			pvm.status = (pvm.status & ~(S_NAN | S_GREATHER | S_LOWER))
					| S_EQUAL;
			break;
		}
	}
	incIP
}
static void c_addfp() /* 0x52 */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (isnan(*p1.p.fpnp) || isnan(p2.p.fpn)) {
		interrupt(INT_ERRORS_ARITHMETIC_ERROR);
		return;
	}
	*p1.p.fpnp += p2.p.fpn;
}
static void c_subfp() /* 0x53 */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (isnan(*p1.p.fpnp) || isnan(p2.p.fpn)) {
		interrupt(INT_ERRORS_ARITHMETIC_ERROR);
		return;
	}
	*p1.p.fpnp -= p2.p.fpn;
}
static void c_mulfp() /* 0x54 */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (isnan(*p1.p.fpnp) || isnan(p2.p.fpn)) {
		interrupt(INT_ERRORS_ARITHMETIC_ERROR);
		return;
	}
	*p1.p.fpnp *= p2.p.fpn;
}
static void c_divfp() /* 0x55 */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	if (isnan(*p1.p.fpnp) || isnan(p2.p.fpn)) {
		interrupt(INT_ERRORS_ARITHMETIC_ERROR);
		return;
	}
	*p1.p.fpnp /= p2.p.fpn;
}
static void c_negfp() /* 0x56 */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	if (isnan(*p1.p.fpnp)) {
		interrupt(INT_ERRORS_ARITHMETIC_ERROR);
		return;
	}
	*p1.p.fpnp = -*p1.p.fpnp;
}
static void c_fptn() /* 0x57 */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	if (isnormal(*p1.p.fpnp)) {
		interrupt(INT_ERRORS_ARITHMETIC_ERROR);
		return;
	}
	*p1.p.np = *p1.p.fpnp;
}
static void c_ntfp() /* 0x58 */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	*p1.p.fpnp = *p1.p.np;
}
static void c_uadd() /* 0x59 */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	*p1.p.up += p2.p.u;
}
static void c_usub() /* 0x5A */{
	struct p p1 = param(1, 8);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(0, 8);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	*p1.p.up -= p2.p.u;
}
static void c_umul() /* 0x5B */{
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
}
static void c_udiv() /* 0x5C */{
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
}
static void c_bmul() /* 0x5D */{
	struct p p1 = param(1, 16);
	if (!p1.valid) {
		return;
	}
	struct p p2 = param(1, 16);
	if (!p2.valid) {
		return;
	}
	check_chaged(1, 8)
	*(__int128*) p1.p.pntr = *(__int128*) p1.p.pntr * *(__int128*) p2.p.pntr;
}
