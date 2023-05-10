#if defined SRC_PVM_CMD_CMDS_GEN_H_ | !defined SRC_PVM_CMD_H_
#	error "Multiple includes of pvm-cmd-cmds-gen.h or SRC_PVM_CMD_H_ is not defined!"
#endif
#define SRC_PVM_CMD_CMDS_GEN_H_

#define ILL_1    c_ill,
#define ILL_10   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1
#define ILL_100  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10
#define ILL_1000 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100
static void(*cmds[])() = {
//GENERATED-CODE-START
// this code-block is automatic generated, do not modify
	ILL_1
	c_mvb, /* 0001 */
	c_mvw, /* 0002 */
	c_mvdw, /* 0003 */
	c_mov, /* 0004 */
	c_lea, /* 0005 */
	c_mvad, /* 0006 */
	c_swap, /* 0007 */
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	c_or, /* 0100 */
	c_and, /* 0101 */
	c_xor, /* 0102 */
	c_not, /* 0103 */
	c_lsh, /* 0104 */
	c_rash, /* 0105 */
	c_rlsh, /* 0106 */
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	c_add, /* 0110 */
	c_sub, /* 0111 */
	c_mul, /* 0112 */
	c_div, /* 0113 */
	c_neg, /* 0114 */
	c_addc, /* 0115 */
	c_subc, /* 0116 */
	c_inc, /* 0117 */
	c_dec, /* 0118 */
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	c_addfp, /* 0120 */
	c_subfp, /* 0121 */
	c_mulfp, /* 0122 */
	c_divfp, /* 0123 */
	c_negfp, /* 0124 */
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	c_uadd, /* 0130 */
	c_usub, /* 0131 */
	c_umul, /* 0132 */
	c_udiv, /* 0133 */
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	c_badd, /* 0140 */
	c_bsub, /* 0141 */
	c_bmul, /* 0142 */
	c_bdiv, /* 0143 */
	c_bneg, /* 0144 */
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	c_fptn, /* 0150 */
	c_ntfp, /* 0151 */
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	c_cmp, /* 0200 */
	c_cmpl, /* 0201 */
	c_cmpfp, /* 0202 */
	c_chkfp, /* 0203 */
	c_cmpu, /* 0204 */
	c_cmpb, /* 0205 */
	c_sgn, /* 0206 */
	c_sgnfp, /* 0207 */
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	c_jmperr, /* 0210 */
	c_jmpeq, /* 0211 */
	c_jmpne, /* 0212 */
	c_jmpgt, /* 0213 */
	c_jmpge, /* 0214 */
	c_jmplt, /* 0215 */
	c_jmple, /* 0216 */
	c_jmpcs, /* 0217 */
	c_jmpcc, /* 0218 */
	c_jmpzs, /* 0219 */
	c_jmpzc, /* 021A */
	c_jmpnan, /* 021B */
	c_jmpan, /* 021C */
	c_jmpab, /* 021D */
	c_jmpsb, /* 021E */
	c_jmpnb, /* 021F */
	c_jmp, /* 0220 */
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	c_int, /* 0230 */
	c_iret, /* 0231 */
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	c_call, /* 0300 */
	c_calo, /* 0301 */
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	c_ret, /* 0310 */
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	c_push, /* 0320 */
	c_pop, /* 0321 */
	c_pushblk, /* 0322 */
	c_popblk, /* 0323 */
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_1000
	ILL_100
	ILL_100
	ILL_100
	ILL_100
	ILL_100
	ILL_100
	ILL_100
	ILL_100
	ILL_100
	ILL_100
	ILL_100
	ILL_100
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_10
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1
	ILL_1

// here is the end of the automatic generated code-block
// GENERATED-CODE-END
};
#undef ILL_1
#undef ILL_10
#undef ILL_100
#undef ILL_1000
