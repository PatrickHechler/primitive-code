/*
 * pvm_virtual_mashine.h
 *
 *  Created on: 12.01.2022
 *      Author: Patrick
 */

#ifndef PVM_VIRTUAL_MASHINE_H_
#define PVM_VIRTUAL_MASHINE_H_

#include <stdint.h>

typedef double fpnum;
typedef int64_t num;
typedef uint64_t unum;

enum pvm_runConditon {
	PVM_RC_ALWAYS_RUN, PVM_RC_JUST_NEXT, PVM_RC_STOP, PVM_RC_EXIT, PVM_RC_DEBUG,
};

//256 * 64-bit registers
struct pvm {
	num regs[0]; // to access the registers
	num *ip;     // the instruction pointer      ||| pvm.regs[0]      ||| IP
	num *sp;     // the stack pointer            ||| pvm.regs[1]      ||| SP
	num status;  // the status register          ||| pvm.regs[2]      ||| STATUS
	num intcnt;  // the interrupt count register ||| pvm.regs[3]      ||| INTCNT
	num *intp;   // the interrupt pointer        ||| pvm.regs[4]      ||| INTP
	num x[256 - 5];   // the 'normal' registers       ||| pvm.regs[5..255] ||| X[00..FA]
};

#define IP_REGISTER_NUM 0

_Static_assert(sizeof(struct pvm) == (sizeof(num) * 256), "the struct for the primitive-virtual-machine has not the expected size");

union pvm_command_union {
	num num;
	fpnum fpn;
	uint8_t cmds[8];
};

void pvm_vm_execute();

#ifndef PVM_VM_EXTERN
#define PVM_VM_EXTERN extern
#endif //PVM_VM_EXTERN

static inline int getConstantParam(int cmd, int *off1, int *off2, int *len, union pvm_command_union *result);

static inline int getNoConstantParam(int cmd, int *off1, int *off2, int *len, union pvm_command_union **result);

enum pvm_ommand {
	CMD_MOV = 0x01,
	CMD_ADD = 0x02,
	CMD_SUB = 0x03,
	CMD_MUL = 0x04,
	CMD_DIV = 0x05,
	CMD_AND = 0x06,
	CMD_OR = 0x07,
	CMD_XOR = 0x08,
	CMD_NOT = 0x09,
	CMD_NEG = 0x0A,
	CMD_LSH = 0x0B,
	CMD_RLSH = 0x0C,
	CMD_RASH = 0x0D,
	CMD_DEC = 0x0E,
	CMD_INC = 0x0F,
	CMD_JMP = 0x10,
	CMD_JMPEQ = 0x11,
	CMD_JMPNE = 0x12,
	CMD_JMPGT = 0x13,
	CMD_JMPGE = 0x14,
	CMD_JMPLT = 0x15,
	CMD_JMPLE = 0x16,
	CMD_JMPCS = 0x17,
	CMD_JMPCC = 0x18,
	CMD_JMPZS = 0x19,
	CMD_JMPZC = 0x1A,
	CMD_JMPNAN = 0x1B,
	CMD_JMPAN = 0x1C,
	CMD_CALL = 0x20,
	CMD_CMP = 0x21,
	CMD_RET = 0x22,
	CMD_INT = 0x23,
	CMD_PUSH = 0x24,
	CMD_POP = 0x25,
	CMD_IRET = 0x26,
	CMD_SWAP = 0x27,
	CMD_LEA = 0x28,
	CMD_MVAD = 0x29,
	CMD_CALO = 0x2A,
	CMD_CMPFP = 0x2B,
	CMD_ADDC = 0x30,
	CMD_SUBC = 0x31,
	CMD_ADDFP = 0x32,
	CMD_SUBFP = 0x33,
	CMD_MULFP = 0x34,
	CMD_DIVFP = 0x35,
	CMD_NTFP = 0x36,
	CMD_FPTN = 0x37,
	CMD_CHKFP = 0x38,
	CMD_UMUL = 0x39,
	CMD_UDIV = 0x3A,
};

enum pvm_command_param {
	ART___BASE = 0x01, ART___A_NUM = 0x00, ART___A_SR = 0x02, ART___NO_B = 0x00, ART___B_REG = 0x04, ART___B_NUM = 0x08, ART___B_SR = 0x0C,

	ART_ANUM = ART___BASE | ART___A_NUM | ART___NO_B,

	ART_ASR = ART___BASE | ART___A_SR | ART___NO_B,

	ART_ANUM_BREG = ART___BASE | ART___A_NUM | ART___B_REG,

	ART_ASR_BREG = ART___BASE | ART___A_SR | ART___B_REG,

	ART_ANUM_BNUM = ART___BASE | ART___A_NUM | ART___B_NUM,

	ART_ASR_BNUM = ART___BASE | ART___A_SR | ART___B_NUM,

	ART_ANUM_BSR = ART___BASE | ART___A_NUM | ART___B_SR,

	ART_ASR_BSR = ART___BASE | ART___A_SR | ART___B_SR,

};

#define STATUS_LOWER     0x0000000000000001L
#define STATUS_GREATHER  0x0000000000000002L
#define STATUS_EQUAL     0x0000000000000004L
#define STATUS_CARRY     0x0000000000000008L
#define STATUS_ZERO      0x0000000000000010L
#define STATUS_NAN       0x0000000000000020L

#define DEF_INT_ERRORS_ILLEGAL_INTERRUPT  0
#define DEF_INT_ERRORS_UNKNOWN_COMMAND    1
#define DEF_INT_ERRORS_ILLEGAL_MEMORY     2
#define DEF_INT_ERRORS_ARITHMETIC_ERROR   3
#define DEF_INT_EXIT                      4
#define DEF_INT_MEMORY_ALLOC              5
#define DEF_INT_MEMORY_REALLOC            6
#define DEF_INT_MEMORY_FREE               7
#define DEF_INT_STREAMS_NEW_IN            8
#define DEF_INT_STREAMS_NEW_OUT           9
#define DEF_INT_STREAMS_NEW_APPEND        10
#define DEF_INT_STREAMS_NEW_IN_OUT        11
#define DEF_INT_STREAMS_NEW_APPEND_IN_OUT 12
#define DEF_INT_STREAMS_WRITE             13
#define DEF_INT_STREAMS_READ              14
#define DEF_INT_STREAMS_SYNC_STREAM       15
#define DEF_INT_STREAMS_CLOSE_STREAM      16
#define DEF_INT_STREAMS_GET_POS           17
#define DEF_INT_STREAMS_SET_POS           18
#define DEF_INT_STREAMS_SET_POS_TO_END    19
#define DEF_INT_FS_REM                    20
#define DEF_INT_FS_MK_DIR                 21
#define DEF_INT_FS_REM_DIR                22
#define DEF_INT_TIME_GET                  23
#define DEF_INT_TIME_WAIT                 24
#define DEF_INT_SOCKET_CLIENT_CREATE      25
#define DEF_INT_SOCKET_CLIENT_CONNECT     26
#define DEF_INT_SOCKET_SERVER_CREATE      27
#define DEF_INT_SOCKET_SERVER_LISTEN      28
#define DEF_INT_SOCKET_SERVER_ACCEPT      29
#define DEF_INT_RANDOM                    30
#define DEF_INT_MEMORY_COPY               31
#define DEF_INT_MEMORY_MOVE               32
#define DEF_INT_MEMORY_BSET               33
#define DEF_INT_MEMORY_SET                34
#define DEF_INT_STRING_LENGTH             35
#define DEF_INT_NUMBER_TO_STRING          36
#define DEF_INT_FPNUMBER_TO_STRING        37
#define DEF_INT_STRING_TO_NUMBER          38
#define DEF_INT_STRING_TO_FPNUMBER        39
#define DEF_INT_STRING_FORMAT             40
#define DEF_INT_LOAD_FILE                 41
#define ALL_INTS_INTCNT                   42

#define DEF_MAX_VALUE 0x7FFFFFFFFFFFFFFFL
#define DEF_MIN_VALUE -0x8000000000000000L

enum dmode {
	pvm_dmode_noDebug, pvm_dmode_justBreakpoints, pvm_dmode_executeUntilErrorOrEndCall, pvm_dmode_executeUntilExit,
};

#define EXIT_NUM_ILLEGAL_INTERRUPT_ADD 64
#define EXIT_NUM_EXIT_IS_ILLEGAL (EXIT_NUM_ILLEGAL_INTERRUPT_ADD + DEF_INT_EXIT)
#define EXIT_NUM_ILLEGAL_INTERRUPT_IS_ILLEGAL (EXIT_NUM_ILLEGAL_INTERRUPT_ADD + DEF_INT_ERRORS_ILLEGAL_INTERRUPT)
#define EXIT_NUM_UNKNOWN_COMMAND 62
#define EXIT_NUM_ILLEGAL_MEMORY 61
#define EXIT_NUM_ARITHMETIC_ERROR 60

#ifdef PVM_VM
struct pvm pvm;

static void int_errors_illegal_interrupt(int);
static void int_errors_unknown_command(int);
static void int_errors_illegal_memory(int);
static void int_errors_arithmetic_error(int);
static void int_exit(int);
static void int_memory_alloc(int);
static void int_memory_realloc(int);
static void int_memory_free(int);
static void int_streams_new_in(int);
static void int_streams_new_out(int);
static void int_streams_new_append(int);
static void int_streams_new_in_out(int);
static void int_streams_new_append_in_out(int);
static void int_streams_write(int);
static void int_streams_read(int);
static void int_streams_sync_stream(int);
static void int_streams_close_stream(int);
static void int_streams_get_pos(int);
static void int_streams_set_pos(int);
static void int_streams_set_pos_to_end(int);
static void int_fs_rem(int);
static void int_fs_mk_dir(int);
static void int_fs_rem_dir(int);
static void int_time_get(int);
static void int_time_wait(int);
static void int_socket_client_create(int);
static void int_socket_client_connect(int);
static void int_socket_server_create(int);
static void int_socket_server_listen(int);
static void int_socket_server_accept(int);
static void int_random(int);
static void int_memory_copy(int);
static void int_memory_move(int);
static void int_memory_bset(int);
static void int_memory_set(int);
static void int_string_length(int);
static void int_number_to_string(int);
static void int_fpnumber_to_string(int);
static void int_string_to_number(int);
static void int_string_to_fpnumber(int);
static void int_string_format(int);
static void int_load_file(int);

void (*defaultinterrupts[])(int) = {
	int_errors_illegal_interrupt,
	int_errors_unknown_command,
	int_errors_illegal_memory,
	int_errors_arithmetic_error,
	int_exit,
	int_memory_alloc,
	int_memory_realloc,
	int_memory_free,
	int_streams_new_in,
	int_streams_new_out,
	int_streams_new_append,
	int_streams_new_in_out,
	int_streams_new_append_in_out,
	int_streams_write,
	int_streams_read,
	int_streams_sync_stream,
	int_streams_close_stream,
	int_streams_get_pos,
	int_streams_set_pos,
	int_streams_set_pos_to_end,
	int_fs_rem,
	int_fs_mk_dir,
	int_fs_rem_dir,
	int_time_get,
	int_time_wait,
	int_socket_client_create,
	int_socket_client_connect,
	int_socket_server_create,
	int_socket_server_listen,
	int_socket_server_accept,
	int_random,
	int_memory_copy,
	int_memory_move,
	int_memory_bset,
	int_memory_set,
	int_string_length,
	int_number_to_string,
	int_fpnumber_to_string,
	int_string_to_number,
	int_string_to_fpnumber,
	int_string_format,
	int_load_file,
};
_Static_assert(sizeof(defaultinterrupts) == (sizeof(void*) * ALL_INTS_INTCNT), "default interrupt array does not have the expected size");
#else
extern struct pvm pvm;
#endif

enum pvm_debugState {
	pvm_ds_none, pvm_ds_untilError, pvm_ds_untilExit,
};

static inline int getNoConstantParam(int cmd, int *off1, int *off2, int *len, union pvm_command_union **result) {
	union pvm_command_union ic;
	ic.num = *pvm.ip;
	switch (ic.cmds[cmd]) {
	case ART_ASR:
		*result = (union pvm_command_union*) &pvm.regs[ic.cmds[7 - *off2]];
//		*len += 0;
		*off2 += 1;
		break;
	case ART_ANUM_BREG:
		*result = (union pvm_command_union*) pvm.ip[1 + *off1];
		*len += 1;
		*off1 += 1;
		break;
	case ART_ASR_BREG:
		*result = (union pvm_command_union*) pvm.regs[ic.cmds[7 - *off2]];
//		*len += 0;
		*off2 += 1;
		break;
	case ART_ANUM_BNUM:
		*result = (union pvm_command_union*) (pvm.ip[1 + *off1] + pvm.ip[2 + *off1]);
		*len += 2;
		*off1 += 2;
		break;
	case ART_ASR_BNUM:
		*result = (union pvm_command_union*) (pvm.regs[ic.cmds[7 - *off2]] + pvm.ip[1 + *off1]);
		*len += 1;
		*off1 += 1;
		*off2 += 1;
		break;
	case ART_ANUM_BSR:
		*result = (union pvm_command_union*) (pvm.ip[1 + *off1] + pvm.regs[ic.cmds[7 - *off2]]);
		*len += 1;
		*off1 += 1;
		*off2 += 1;
		break;
	case ART_ASR_BSR:
		*result = (union pvm_command_union*) (pvm.regs[ic.cmds[7 - *off2]] + pvm.regs[ic.cmds[6 - *off2]]);
//		*len += 0;
		*off2 += 2;
		break;
	default:
		return 1;
	}
	return 0;
}

static inline int getConstantParam(int cmd, int *off1, int *off2, int *len, union pvm_command_union *result) {
	union pvm_command_union ic;
	ic.num = *pvm.ip;
	switch (ic.cmds[cmd]) {
	case ART_ANUM:
		result->num = pvm.ip[1 + *off1];
		*len += 1;
		*off1 += 1;
		break;
	case ART_ASR:
		result->num = pvm.regs[ic.cmds[7 - *off2]];
//		*len += 0;
		*off2 += 1;
		break;
	case ART_ANUM_BREG:
		result->num = pvm.ip[1 + *off1];
		*len += 1;
		*off1 += 1;
		break;
	case ART_ASR_BREG:
		result->num = *(num*) pvm.regs[ic.cmds[7 - *off2]];
//		*len += 0;
		*off2 += 1;
		break;
	case ART_ANUM_BNUM:
		result->num = *(num*) (pvm.ip[1 + *off1] + pvm.ip[2 + *off1]);
		*len += 2;
		*off1 += 2;
		break;
	case ART_ASR_BNUM:
		result->num = *(num*) (pvm.regs[ic.cmds[7 - *off2]] + pvm.ip[1 + *off1]);
		*len += 1;
		*off1 += 1;
		*off2 += 1;
		break;
	case ART_ANUM_BSR:
		result->num = *(num*) (pvm.ip[1 + *off1] + pvm.regs[ic.cmds[7 - *off2]]);
		*len += 1;
		*off1 += 1;
		*off2 += 1;
		break;
	case ART_ASR_BSR:
		result->num = *(num*) (pvm.regs[ic.cmds[7 - *off2]] + pvm.regs[ic.cmds[6 - *off2]]);
//		*len += 0;
		*off2 += 2;
		break;
	default:
		return 1;
	}
	return 0;
}

#endif /* PVM_VIRTUAL_MASHINE_H_ */
