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
		pvm.status.unum = (pvm.status.unum & ~(S_ZERO)) | S_OVERFLOW;
	} else if (*p1.p.np) {
		pvm.status.unum = (pvm.status.unum & ~(S_OVERFLOW | S_ZERO));
	} else {
		pvm.status.unum = (pvm.status.unum & ~(S_OVERFLOW)) | S_ZERO;
	}
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
		pvm.status.unum = (pvm.status.unum & ~(S_ZERO)) | S_OVERFLOW;
	} else if (*p1.p.np) {
		pvm.status.unum = (pvm.status.unum & ~(S_OVERFLOW | S_ZERO));
	} else {
		pvm.status.unum = (pvm.status.unum & ~(S_OVERFLOW)) | S_ZERO;
	}
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
		pvm.status.unum &= ~S_ZERO;
	} else {
		pvm.status.unum |= S_ZERO;
	}
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
	abort();
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
}
static void c_inc() /* 0x17 */{
	abort();
}
static void c_dec() /* 0x18 */{
	abort();
}
static void c_or() /* 0x19 */{
	abort();
}
static void c_and() /* 0x1a */{
	abort();
}
static void c_xor() /* 0x1b */{
	abort();
}
static void c_not() /* 0x1c */{
	abort();
}
static void c_lsh() /* 0x1d */{
	abort();
}
static void c_rash() /* 0x1e */{
	abort();
}
static void c_rlsh() /* 0x1f */{
	abort();
}

static void c_jmp() /* 0x20 */{
	abort();
}
static void c_jmpeq() /* 0x21 */{
	abort();
}
static void c_jmpne() /* 0x22 */{
	abort();
}
static void c_jmpgt() /* 0x23 */{
	abort();
}
static void c_jmpge() /* 0x24 */{
	abort();
}
static void c_jmplt() /* 0x25 */{
	abort();
}
static void c_jmple() /* 0x26 */{
	abort();
}
static void c_jmpcs() /* 0x27 */{
	abort();
}
static void c_jmpcc() /* 0x28 */{
	abort();
}
static void c_jmpzs() /* 0x29 */{
	abort();
}
static void c_jmpzc() /* 0x2a */{
	abort();
}
static void c_jmpnan() /* 0x2b */{
	abort();
}
static void c_jmpan() /* 0x2c */{
	abort();
}
static void c_jmpab() /* 0x2d */{
	abort();
}
static void c_jmpsb() /* 0x2e */{
	abort();
}
static void c_jmpnb() /* 0x2f */{
	abort();
}

static void c_int() /* 0x30 */{
	abort();
}
static void c_iret() /* 0x31 */{
	abort();
}
static void c_call() /* 0x32 */{
	abort();
}
static void c_calo() /* 0x33 */{
	abort();
}
static void c_ret() /* 0x34 */{
	abort();
}
static void c_push() /* 0x35 */{
	abort();
}
static void c_pop() /* 0x36 */{
	abort();
}

static void c_cmp() /* 0x40 */{
	abort();
}
static void c_bcmp() /* 0x41 */{
	abort();
}

static void c_fpcmp() /* 0x50 */{
	abort();
}
static void c_fpchk() /* 0x51 */{
	abort();
}
static void c_fpadd() /* 0x52 */{
	abort();
}
static void c_fpsub() /* 0x53 */{
	abort();
}
static void c_fpmul() /* 0x54 */{
	abort();
}
static void c_fpdiv() /* 0x55 */{
	abort();
}
static void c_fpneg() /* 0x56 */{
	abort();
}
static void c_fptn() /* 0x57 */{
	abort();
}
static void c_ntfp() /* 0x58 */{
	abort();
}
static void c_uadd() /* 0x59 */{
	abort();
}
static void c_usub() /* 0x5a */{
	abort();
}
static void c_umul() /* 0x5b */{
	abort();
}
static void c_udiv() /* 0x5c */{
	abort();
}

