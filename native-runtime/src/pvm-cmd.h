/*
 * pvm-cmd.h
 *
 *  CreateD on: Nov 9, 2022
 *      Author: pat
 */

#if defined SRC_PVM_CMD_H_ | !defined PVM
#error "multpilE includes oF pvm-cmd.h or PVM is not defined!"
#endif
#define SRC_PVM_CMD_H_

static void c_ill(); /* --- */

static void c_mvb(); /* 0x01 */
static void c_mvw(); /* 0x02 */
static void c_mvdw(); /* 0x03 */
static void c_mov(); /* 0x04 */
static void c_lea(); /* 0x05 */
static void c_mvad(); /* 0x06 */
static void c_swap(); /* 0x07 */

static void c_add(); /* 0x10 */
static void c_sub(); /* 0x11 */
static void c_mul(); /* 0x12 */
static void c_div(); /* 0x13 */
static void c_neg(); /* 0x14 */
static void c_addc(); /* 0x15 */
static void c_subc(); /* 0x16 */
static void c_inc(); /* 0x17 */
static void c_dec(); /* 0x18 */
static void c_or(); /* 0x19 */
static void c_and(); /* 0x1A */
static void c_xor(); /* 0x1B */
static void c_not(); /* 0x1C */
static void c_lsh(); /* 0x1D */
static void c_rash(); /* 0x1E */
static void c_rlsh(); /* 0x1F */

static void c_jmp(); /* 0x20 */
static void c_jmpeq(); /* 0x21 */
static void c_jmpne(); /* 0x22 */
static void c_jmpgt(); /* 0x23 */
static void c_jmpge(); /* 0x24 */
static void c_jmplt(); /* 0x25 */
static void c_jmple(); /* 0x26 */
static void c_jmpcs(); /* 0x27 */
static void c_jmpcc(); /* 0x28 */
static void c_jmpzs(); /* 0x29 */
static void c_jmpzc(); /* 0x2A */
static void c_jmpnan(); /* 0x2B */
static void c_jmpan(); /* 0x2C */
static void c_jmpab(); /* 0x2D */
static void c_jmpsb(); /* 0x2E */
static void c_jmpnb(); /* 0x2F */

static void c_int(); /* 0x30 */
static void c_iret(); /* 0x31 */
static void c_call(); /* 0x32 */
static void c_calo(); /* 0x33 */
static void c_ret(); /* 0x34 */
static void c_push(); /* 0x35 */
static void c_pop(); /* 0x36 */

static void c_cmp(); /* 0x40 */
static void c_bcmp(); /* 0x41 */

static void c_fpcmp(); /* 0x50 */
static void c_fpchk(); /* 0x51 */
static void c_fpadd(); /* 0x52 */
static void c_fpsub(); /* 0x53 */
static void c_fpmul(); /* 0x54 */
static void c_fpdiv(); /* 0x55 */
static void c_fpneg(); /* 0x56 */
static void c_fptn(); /* 0x57 */
static void c_ntfp(); /* 0x58 */
static void c_uadd(); /* 0x59 */
static void c_usub(); /* 0x5A */
static void c_umul(); /* 0x5B */
static void c_udiv(); /* 0x5C */

static void (*cmds[])() = {
		// 0x0.
		c_ill,   c_mvb,   c_mvw,   c_mvdw,   c_mov,   c_lea,   c_mvad,  c_swap,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

		// 0x1.
		c_add,   c_sub,   c_mul,   c_div,    c_neg,   c_addc,  c_subc,  c_inc,
		c_dec,   c_or,    c_and,   c_xor,    c_not,   c_lsh,   c_rash,  c_rlsh,

		// 0x2.
		c_jmp,   c_jmpeq, c_jmpne, c_jmpgt,  c_jmpge, c_jmplt, c_jmple, c_jmpcs,
		c_jmpcc, c_jmpzs, c_jmpzc, c_jmpnan, c_jmpan, c_jmpab, c_jmpsb, c_jmpnb,

		// 0x3.
		c_int,   c_iret,  c_call,  c_calo,   c_ret,   c_push,  c_pop,   c_ill,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

		// 0x4.
		c_cmp,   c_bcmp,  c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

		// 0x5.
		c_fpcmp, c_fpchk, c_fpadd, c_fpsub,  c_fpmul, c_fpdiv, c_fpneg, c_fptn,
		c_ntfp,  c_uadd,  c_usub,  c_umul,   c_udiv,  c_ill,   c_ill,   c_ill,

		// 0x6.
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

		// 0x7.
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

		// 0x8.
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

		// 0x9.
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

		// 0xA.
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

		// 0xB.
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

		// 0xC.
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

		// 0xD.
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

		// 0xE.
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

		// 0xF.
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,
		c_ill,   c_ill,   c_ill,   c_ill,    c_ill,   c_ill,   c_ill,   c_ill,

};
