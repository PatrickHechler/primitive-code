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

#ifdef SRC_PVM_CMD_CMDS_GEN_H_
#	ifdef PVM
#		error "Multiple includes of pvm-cmd-cmds-gen.h and PVM is defined!"
#	endif
#else // SRC_PVM_CMD_CMDS_GEN_H_
#define SRC_PVM_CMD_CMDS_GEN_H_

#define ILL_1    c_ill,
#define ILL_10   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1   ILL_1
#define ILL_100  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10  ILL_10
#define ILL_1000 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100 ILL_100
static void(*cmds[])()
#ifdef PVM
= {
// GENERATED-CODE-START
// this code-block is automatic generated, do not modify
	c_extern, /* 0000 */
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
	c_modfp, /* 0125 */
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
	c_addqfp, /* 0130 */
	c_subqfp, /* 0131 */
	c_mulqfp, /* 0132 */
	c_divqfp, /* 0133 */
	c_negqfp, /* 0134 */
	c_modqfp, /* 0135 */
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
	c_addsfp, /* 0140 */
	c_subsfp, /* 0141 */
	c_mulsfp, /* 0142 */
	c_divsfp, /* 0143 */
	c_negsfp, /* 0144 */
	c_modsfp, /* 0145 */
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
	c_uadd, /* 0150 */
	c_usub, /* 0151 */
	c_umul, /* 0152 */
	c_udiv, /* 0153 */
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
	c_badd, /* 0160 */
	c_bsub, /* 0161 */
	c_bmul, /* 0162 */
	c_bdiv, /* 0163 */
	c_bneg, /* 0164 */
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
	c_fptn, /* 0170 */
	c_ntfp, /* 0171 */
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
	c_jmpo, /* 0221 */
	c_jmpno, /* 0222 */
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
	c_calno, /* 0302 */
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
}
#endif // PVM
;
#undef ILL_1
#undef ILL_10
#undef ILL_100
#undef ILL_1000

#ifdef PVM
_Static_assert((sizeof(void(*)()) * (1 << 16)) == sizeof(cmds), "Error!");
#endif // PVM

#endif // SRC_PVM_CMD_CMDS_GEN_H_
