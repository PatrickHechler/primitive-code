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
 * pvm-cmd.h
 *
 *  CreateD on: Nov 9, 2022
 *      Author: pat
 */

#ifdef SRC_PVM_CMD_H_
#	ifdef PVM
#		error "Multiple includes of pvm-cmd.h and PVM is defined!"
#	endif
#else // SRC_PVM_CMD_H_
#define SRC_PVM_CMD_H_

#ifdef PVM

static void c_ill();

// GENERATED-CODE-START
// this code-block is automatic generated, do not modify

/* 00.. : data */
/* 000. : move data */
	/* 0000 */ static void c_extern();
	/* 0001 */ static void c_mvb();
	/* 0002 */ static void c_mvw();
	/* 0003 */ static void c_mvdw();
	/* 0004 */ static void c_mov();
	/* 0005 */ static void c_lea();
	/* 0006 */ static void c_mvad();
	/* 0007 */ static void c_swap();

/* 00.. : math */
/* 001. : logic */
	/* 0100 */ static void c_or();
	/* 0101 */ static void c_and();
	/* 0102 */ static void c_xor();
	/* 0103 */ static void c_not();
	/* 0104 */ static void c_lsh();
	/* 0105 */ static void c_rash();
	/* 0106 */ static void c_rlsh();
/* 001. : simple arithmetic */
	/* 0110 */ static void c_add();
	/* 0111 */ static void c_sub();
	/* 0112 */ static void c_mul();
	/* 0113 */ static void c_div();
	/* 0114 */ static void c_neg();
	/* 0115 */ static void c_addc();
	/* 0116 */ static void c_subc();
	/* 0117 */ static void c_inc();
	/* 0118 */ static void c_dec();
/* 001. : floating-point arithmetic */
	/* 0120 */ static void c_addfp();
	/* 0121 */ static void c_subfp();
	/* 0122 */ static void c_mulfp();
	/* 0123 */ static void c_divfp();
	/* 0124 */ static void c_negfp();
/* 001. : unsigned arithmetic */
	/* 0130 */ static void c_uadd();
	/* 0131 */ static void c_usub();
	/* 0132 */ static void c_umul();
	/* 0133 */ static void c_udiv();
/* 001. : big arithmetic */
	/* 0140 */ static void c_badd();
	/* 0141 */ static void c_bsub();
	/* 0142 */ static void c_bmul();
	/* 0143 */ static void c_bdiv();
	/* 0144 */ static void c_bneg();
/* 001. : convert number types */
	/* 0150 */ static void c_fptn();
	/* 0151 */ static void c_ntfp();

/* 00.. : program control */
/* 002. : compare/check */
	/* 0200 */ static void c_cmp();
	/* 0201 */ static void c_cmpl();
	/* 0202 */ static void c_cmpfp();
	/* 0203 */ static void c_chkfp();
	/* 0204 */ static void c_cmpu();
	/* 0205 */ static void c_cmpb();
	/* 0206 */ static void c_sgn();
	/* 0207 */ static void c_sgnfp();
/* 002. : conditional jump */
	/* 0210 */ static void c_jmperr();
	/* 0211 */ static void c_jmpeq();
	/* 0212 */ static void c_jmpne();
	/* 0213 */ static void c_jmpgt();
	/* 0214 */ static void c_jmpge();
	/* 0215 */ static void c_jmplt();
	/* 0216 */ static void c_jmple();
	/* 0217 */ static void c_jmpcs();
	/* 0218 */ static void c_jmpcc();
	/* 0219 */ static void c_jmpzs();
	/* 021A */ static void c_jmpzc();
	/* 021B */ static void c_jmpnan();
	/* 021C */ static void c_jmpan();
	/* 021D */ static void c_jmpab();
	/* 021E */ static void c_jmpsb();
	/* 021F */ static void c_jmpnb();
/* 002. : unconditional jump */
	/* 0220 */ static void c_jmp();
/* 002. : interrupt */
	/* 0230 */ static void c_int();
	/* 0231 */ static void c_iret();

/* 00.. : stack */
/* 003. : call */
#define CALL_COMMANDS_START 0x0300
	/* 0300 */ static void c_call();
	/* 0301 */ static void c_calo();
#define CALL_COMMANDS_COUNT 2
/* 003. : return */
#define RETURN_COMMANDS_START 0x0310
	/* 0310 */ static void c_ret();
#define RETURN_COMMANDS_COUNT 1
/* 003. : push/pop */
	/* 0320 */ static void c_push();
	/* 0321 */ static void c_pop();
	/* 0322 */ static void c_pushblk();
	/* 0323 */ static void c_popblk();

// here is the end of the automatic generated code-block
// GENERATED-CODE-END

#endif // PVM

#include "pvm-cmd-cmds-gen.h"

#endif // SRC_PVM_CMD_H_