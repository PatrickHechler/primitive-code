/*
 * pvm_virtual_mashine.c
 *
 *  Created on: 12.01.2022
 *      Author: Patrick
 */

#define PVM_VM
#include "pvm_defs.h"
#include "pvm_virtual_mashine.h"
#include "pvm_signal_handler.h"

#include <fcntl.h>
#include <string.h>
#include <stdio.h>
#include <netdb.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <pthread.h>
#include <setjmp.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <time.h>
#include <unistd.h>
#include <errno.h>
#include <dirent.h>
#include <math.h>

static const char digits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		'U', 'V', 'W', 'X', 'Y', 'Z' };

extern pthread_mutex_t debug_mutex;

static num interrupt(num intnum);

static void runNextCommand();

static int inline numToStr(num number, char *buffer, int base);

extern jmp_buf state;

extern int asumeRunning;

extern int halt;

enum pvm_debugState pvm_ds = pvm_ds_none;

enum pvm_runConditon pvm_rc;

int pvm_runState = 0;

struct pvm pvm;

int errorState = 0;

#define unknownCommand \
	if (interrupt(DEF_INT_ERRORS_UNKNOWN_COMMAND) == -1) {\
		return;\
		/*goto execNextCmdStart;*/\
	}\
	exit(EXIT_NUM_UNKNOWN_COMMAND);

#define getOneConstParam \
	int len = 1;\
	int off1=0,off2=0;\
	num p1;\
	if (getConstantParam(1, &off1, &off2, &len, (union pvm_command_union*) &p1)) {\
		unknownCommand\
	}

#define getOneConstUnionParam \
	int len = 1;\
	int off1=0,off2=0;\
	union pvm_command_union p1;\
	if (getConstantParam(1, &off1, &off2, &len, &p1)) {\
		unknownCommand\
	}

#define getOneNoConstUnionParam \
	int len = 1;\
	int off1=0, off2=0;\
	union pvm_command_union *p1;\
	if (getNoConstantParam(1, &off1, &off2, &len, &p1)) {\
		unknownCommand\
	}

#define getOneNoConstParam \
	int len = 1;\
	int off1=0, off2=0;\
	num *p1;\
	if (getNoConstantParam(1, &off1, &off2, &len, (union pvm_command_union**) &p1)) {\
		unknownCommand\
	}

#define getTwoConstParam \
	int len = 1;\
	int off1 = 0, off2 = 0;\
	num p1,p2;\
	if (getConstantParam(1, &off1, &off2, &len, (union pvm_command_union*)&p1)) {\
		unknownCommand\
	}\
	if (getConstantParam(2, &off1, &off2, &len, (union pvm_command_union*)&p2)) {\
		unknownCommand\
	}

#define getTwoConstUnionParam \
	int len = 1;\
	int off1 = 0, off2 = 0;\
	union pvm_command_union p1, p2;\
	if (getConstantParam(1, &off1, &off2, &len, &p1)) {\
		unknownCommand\
	}\
	if (getConstantParam(2, &off1, &off2, &len, &p2)) {\
		unknownCommand\
	}

#define getNoConstConstUnionParam \
	int len = 1;\
	int off1 = 0, off2 = 0;\
	union pvm_command_union *p1, p2;\
	if (getNoConstantParam(1, &off1, &off2, &len, &p1)) {\
		unknownCommand\
	}\
	if (getConstantParam(2, &off1, &off2, &len, &p2)) {\
		unknownCommand\
	}

#define getNoConstConstParam \
	int len = 1;\
	int off1 = 0, off2 = 0;\
	num *p1,p2;\
	if (getNoConstantParam(1, &off1, &off2, &len, (union pvm_command_union**) &p1)) {\
		unknownCommand\
	}\
	if (getConstantParam(2, &off1, &off2, &len, (union pvm_command_union*) &p2)) {\
		unknownCommand\
	}

#define getTwoNoConstParam \
	int len = 1;\
	int off1 = 0, off2 = 0;\
	num *p1,*p2;\
	if (getNoConstantParam(1, &off1, &off2, &len, (union pvm_command_union**) &p1)) {\
		unknownCommand\
	}\
	if (getNoConstantParam(2, &off1, &off2, &len, (union pvm_command_union**) &p2)) {\
		unknownCommand\
	}

void pvm_vm_execute() {
#ifdef DEBUG_LOG_ALL
	printf("[pvm]: entered vm-execute\n");
	fflush(stdout);
#endif // DEBUG_LOG_ALL
	int errornum = setjmp(state);
	if (errornum != 0) {
		init_pvm_signal_handler();
		int retOnExit;
		switch (pvm_rc) {
		case PVM_RC_ALWAYS_RUN:
			retOnExit = 0;
			break;
		case PVM_RC_DEBUG:
			switch (pvm_ds) {
			case pvm_ds_untilError:
				errorState = 1;
				return;
			case pvm_ds_untilExit:
				retOnExit = 1;
				break;
			case pvm_ds_none:
				retOnExit = 0;
				break;
			default:
				fprintf(stderr, "\n[ABORT]: abort now magic=1");
				abort();
			}
			break;
		case PVM_RC_JUST_NEXT:
		case PVM_RC_STOP:
		case PVM_RC_EXIT:
			return;
		default:
			fprintf(stderr, "\n[ABORT]: abort now magic=2");
			abort();
		}
		if (interrupt(errornum) != -1) {
			int exitnum;
			switch (errornum) {
			case DEF_INT_ERRORS_ARITHMETIC_ERROR: {
				exitnum = EXIT_NUM_ARITHMETIC_ERROR;
				break;
			}
			case DEF_INT_ERRORS_ILLEGAL_MEMORY: {
				exitnum = EXIT_NUM_ILLEGAL_MEMORY;
				break;
			}
			default:
				fprintf(stderr, "\n[ABORT]: abort now magic=3");
				abort();
			}
			if (interrupt(DEF_INT_EXIT) == -1) {
				pvm.x[0] = exitnum;
			} else if (retOnExit) {
				errorState = 1;
				return;
			} else {
				exit(exitnum);
			}
		}
	}
	if (pvm_runState && !errornum) {
		fprintf(stderr, "\n[ABORT]: abort now magic=4");
		abort();
	}
	pvm_runState = 1;
	if (pvm_rc == PVM_RC_ALWAYS_RUN) {
		asumeRunning = 1;
		while (1) {
			runNextCommand();
		}
	}
	while (1) {
#ifdef DEBUG_LOG_ALL
		printf("[pvm]: new runNextCmd Iteration\n");
		fflush(stdout);
#endif // DEBUG_LOG_ALL
		pthread_mutex_lock(&debug_mutex);
		asumeRunning = 1;
		switch (pvm_rc) {
		case PVM_RC_ALWAYS_RUN:
			fprintf(stderr, "\n[ABORT]: abort now magic=5");
			abort();
		case PVM_RC_DEBUG:
		case PVM_RC_JUST_NEXT:
#ifdef DEBUG_LOG_ALL
			printf("[pvm]: pvm_rc=PVM_RC_JUST_NEXT | pvm_rc=PVM_RC_DEBUG\n");
			fflush(stdout);
#endif // DEBUG_LOG_ALL
			runNextCommand();
#ifdef DEBUG_LOG_ALL
			printf("[pvm]: executed next command\n");
			fflush(stdout);
#endif // DEBUG_LOG_ALL
			asumeRunning = 0;
			pthread_mutex_unlock(&debug_mutex);
			return;
		case PVM_RC_EXIT:
#ifdef DEBUG_LOG
			printf("[pvm]: pvm_rc=PVM_RC_EXIT\n");
			fflush(stdout);
#endif // DEBUG_LOG
//			asumeRunning = 0;
//			pthread_mutex_unlock(&debug_mutex);
			exit(0);
		case PVM_RC_STOP:
#ifdef DEBUG_LOG
			printf("[pvm]: pvm_rc=PVM_RC_STOP\n");
			fflush(stdout);
#endif // DEBUG_LOG
			asumeRunning = 0;
			pthread_mutex_unlock(&debug_mutex);
			return;
		default:
#ifdef DEBUG_LOG
			printf("[pvm]: pvm_rc=unknown: %d\n", pvm_rc);
			fflush(stdout);
#endif // DEBUG_LOG
			asumeRunning = 0;
			pthread_mutex_unlock(&debug_mutex);
			exit(4);
		}
		runNextCommand();
#ifdef DEBUG_LOG_ALL
		printf("[pvm]: executed next command\n");
		fflush(stdout);
#endif // DEBUG_LOG_ALL
		asumeRunning = 0;
		pthread_mutex_unlock(&debug_mutex);
	}
}

static void runNextCommand() {
	execNextCmdStart:
	;
	union pvm_command_union ic;
	ic.num = *pvm.ip;
	switch (ic.cmds[0]) {
	case CMD_MOV: {
		getNoConstConstParam
		*p1 = p2;
		pvm.ip += len;
		break;
	}
	case CMD_MVAD: {
		getNoConstConstParam
		*p1 = p2 + pvm.ip[len];
		pvm.ip += len + 8;
		break;
	}
	case CMD_LEA: {
		getNoConstConstParam
		*p1 = p2 + (num) pvm.ip;
		pvm.ip += len;
		break;
	}
	case CMD_SWAP: {
		getTwoNoConstParam
		num zw = *p1;
		*p1 = *p2;
		*p2 = zw;
		pvm.ip += len;
		break;
	}
	case CMD_ADD: {
		getNoConstConstParam
		num sum = *p1 + p2;
		if (sum < 0) {
			if (*p1 > 0 && p2 > 0) {
				pvm.status |= STATUS_CARRY;
				pvm.status &= ~STATUS_ZERO;
			} else {
				pvm.status &= ~(STATUS_CARRY | STATUS_ZERO);
			}
		} else if (sum > 0) {
			if (*p1 < 0 && p2 < 0) {
				pvm.status |= STATUS_CARRY;
				pvm.status &= ~STATUS_ZERO;
			} else {
				pvm.status &= ~(STATUS_CARRY | STATUS_ZERO);
			}
		} else {
			pvm.status |= STATUS_ZERO;
		}
		*p1 = sum;
		pvm.ip += len;
		break;
	}
	case CMD_ADDC: {
		getNoConstConstParam
		num sum;
		if (pvm.status & STATUS_CARRY) {
			sum = *p1 + p2 + 1;
		} else {
			sum = *p1 + p2;
		}
		if (sum < 0) {
			if (*p1 > 0 && p2 > 0) {
				pvm.status |= STATUS_CARRY;
				pvm.status &= ~STATUS_ZERO;
			} else {
				pvm.status &= ~(STATUS_CARRY | STATUS_ZERO);
			}
		} else if (sum > 0) {
			if (*p1 < 0 && p2 < 0) {
				pvm.status |= STATUS_CARRY;
				pvm.status &= ~STATUS_ZERO;
			} else {
				pvm.status &= ~(STATUS_CARRY | STATUS_ZERO);
			}
		} else {
			pvm.status |= STATUS_ZERO;
		}
		*p1 = sum;
		pvm.ip += len;
		break;
	}
	case CMD_ADDFP: {
		getNoConstConstUnionParam
		fpnum res = p1->fpn + p2.fpn;
		if (res != 0.0) {
			pvm.status &= ~(STATUS_ZERO | STATUS_NAN);
		} else if (isnan(res)) {
			pvm.status &= ~STATUS_ZERO;
			pvm.status |= STATUS_NAN;
		} else {
			pvm.status &= ~STATUS_NAN;
			pvm.status |= STATUS_ZERO;
		}
		p1->fpn = res;
		pvm.ip += len;
		break;
	}
	case CMD_SUB: {
		getNoConstConstParam
		num res = *p1 - p2;
		if (res < 0) {
			if (*p1 > 0 && p2 < 0) {
				pvm.status |= STATUS_CARRY;
				pvm.status &= ~STATUS_ZERO;
			} else {
				pvm.status &= ~(STATUS_CARRY | STATUS_ZERO);
			}
		} else if (res > 0) {
			if (*p1 < 0 && p2 > 0) {
				pvm.status |= STATUS_CARRY;
				pvm.status &= ~STATUS_ZERO;
			} else {
				pvm.status &= ~(STATUS_CARRY | STATUS_ZERO);
			}
		} else {
			pvm.status |= STATUS_ZERO;
		}
		*p1 = res;
		pvm.ip += len;
		break;
	}
	case CMD_SUBC: {
		getNoConstConstParam
		num res;
		if (pvm.status & STATUS_CARRY) {
			res = *p1 - (p2 + 1);
		} else {
			res = *p1 - p2;
		}
		if (res < 0) {
			if (*p1 > 0 && p2 < 0) {
				pvm.status |= STATUS_CARRY;
				pvm.status &= ~STATUS_ZERO;
			} else {
				pvm.status &= ~(STATUS_CARRY | STATUS_ZERO);
			}
		} else if (res > 0) {
			if (*p1 < 0 && p2 > 0) {
				pvm.status |= STATUS_CARRY;
				pvm.status &= ~STATUS_ZERO;
			} else {
				pvm.status &= ~(STATUS_CARRY | STATUS_ZERO);
			}
		} else {
			pvm.status |= STATUS_ZERO;
		}
		*p1 = res;
		pvm.ip += len;
		break;
	}
	case CMD_SUBFP: {
		getNoConstConstUnionParam
		fpnum res = p1->fpn - p2.fpn;
		if (res != 0.0) {
			pvm.status &= ~(STATUS_ZERO | STATUS_NAN);
		} else if (isnan(res)) {
			pvm.status &= ~STATUS_ZERO;
			pvm.status |= STATUS_NAN;
		} else {
			pvm.status &= ~STATUS_NAN;
			pvm.status |= STATUS_ZERO;
		}
		p1->fpn = res;
		pvm.ip += len;
		break;
	}
	case CMD_MUL: {
		getNoConstConstParam
		num res = *p1 * p2;
		if (res != 0) {
			pvm.status &= ~STATUS_ZERO;
		} else {
			pvm.status |= STATUS_ZERO;
		}
		*p1 = res;
		pvm.ip += len;
		break;
	}
	case CMD_UMUL: {
		getNoConstConstParam
		num res = (num) (((unum) *p1) * ((unum) p2));
		if (res != 0) {
			pvm.status &= ~STATUS_ZERO;
		} else {
			pvm.status |= STATUS_ZERO;
		}
		*p1 = res;
		pvm.ip += len;
		break;
	}
	case CMD_MULFP: {
		getNoConstConstUnionParam
		fpnum res = p1->fpn * p2.fpn;
		if (res != 0) {
			pvm.status &= ~STATUS_ZERO;
		} else {
			pvm.status |= STATUS_ZERO;
		}
		p1->fpn = res;
		pvm.ip += len;
		break;
	}
	case CMD_DIV: {
		getTwoNoConstParam
		num div = *p1 / *p2;
		num mod = *p1 % *p2;
		*p1 = div;
		*p2 = mod;
		pvm.ip += len;
		break;
	}
	case CMD_UDIV: {
		getTwoNoConstParam
		unum up1 = *p1, up2 = *p2;
		num div = up1 / up2;
		num mod = up1 % up2;
		*p1 = div;
		*p2 = mod;
		pvm.ip += len;
		break;
	}
	case CMD_DIVFP: {
		getNoConstConstUnionParam
		fpnum res = p1->fpn / p2.fpn;
		if (res == 0.0) {
			pvm.status |= STATUS_ZERO;
			pvm.status &= ~STATUS_NAN;
		} else if (isnan(res)) {
			pvm.status &= ~STATUS_ZERO;
			pvm.status |= STATUS_NAN;
		} else {
			pvm.status &= ~(STATUS_ZERO | STATUS_NAN);
		}
		p1->fpn = res;
		pvm.ip += len;
		break;
	}
	case CMD_AND: {
		getNoConstConstParam
		num res = *p1 & p2;
		*p1 = res;
		if (res == 0) {
			pvm.status |= STATUS_ZERO;
		} else {
			pvm.status &= ~STATUS_ZERO;
		}
		pvm.ip += len;
		break;
	}
	case CMD_OR: {
		getNoConstConstParam
		num res = *p1 | p2;
		*p1 = res;
		if (res == 0) {
			pvm.status |= STATUS_ZERO;
		} else {
			pvm.status &= ~STATUS_ZERO;
		}
		pvm.ip += len;
		break;
	}
	case CMD_XOR: {
		getNoConstConstParam
		num res = *p1 ^ p2;
		*p1 = res;
		if (res == 0) {
			pvm.status |= STATUS_ZERO;
		} else {
			pvm.status &= ~STATUS_ZERO;
		}
		pvm.ip += len;
		break;
	}
	case CMD_LSH: {
		getNoConstConstParam
		num res = *p1 << p2;
		if (res >> p2 == *p1) {
			pvm.status &= ~STATUS_CARRY;
		} else {
			pvm.status |= STATUS_CARRY;
		}
		if (res == 0) {
			pvm.status |= STATUS_ZERO;
		} else {
			pvm.status &= ~STATUS_ZERO;
		}
		*p1 = res;
		pvm.ip += len;
		break;
	}
	case CMD_RLSH: {
		getNoConstConstParam
		unum res = ((unum) *p1) >> ((unum) p2);
		if (res << ((unum) p2) == (unum) *p1) {
			pvm.status &= ~STATUS_CARRY;
		} else {
			pvm.status |= STATUS_CARRY;
		}
		if (res == 0) {
			pvm.status |= STATUS_ZERO;
		} else {
			pvm.status &= ~STATUS_ZERO;
		}
		*p1 = res;
		pvm.ip += len;
		break;
	}
	case CMD_RASH: {
		getNoConstConstParam
		num res = *p1 >> p2;
		if (res << p2 == *p1) {
			pvm.status &= ~STATUS_CARRY;
		} else {
			pvm.status |= STATUS_CARRY;
		}
		if (res == 0) {
			pvm.status |= STATUS_ZERO;
		} else {
			pvm.status &= ~STATUS_ZERO;
		}
		*p1 = res;
		pvm.ip += len;
		break;
	}
	case CMD_NOT: {
		getOneNoConstParam
		num res = ~*p1;
		*p1 = res;
		if (res == 0) {
			pvm.status |= STATUS_ZERO;
		} else {
			pvm.status &= ~STATUS_ZERO;
		}
		pvm.ip += len;
		break;
	}
	case CMD_NEG: {
		getOneNoConstParam
		num res = -*p1;
		if (res == 0) {
			pvm.status |= STATUS_ZERO;
			pvm.status &= ~STATUS_CARRY;
		} else if (res == DEF_MIN_VALUE) {
			pvm.status |= STATUS_CARRY;
		} else {
			pvm.status &= ~(STATUS_ZERO | STATUS_CARRY);
		}
		*p1 = res;
		pvm.ip += len;
		break;
	}
	case CMD_JMP: {
		pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		break;
	}
	case CMD_JMPEQ: {
		if (pvm.status & STATUS_EQUAL) {
			pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		} else {
			pvm.ip += 2;
		}
		break;
	}
	case CMD_JMPNE: {
		if (pvm.status & STATUS_EQUAL) {
			pvm.ip += 2;
		} else {
			pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		}
		break;
	}
	case CMD_JMPGT: {
		if (pvm.status & STATUS_GREATHER) {
			pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		} else {
			pvm.ip += 2;
		}
		break;
	}
	case CMD_JMPGE: {
		if (pvm.status & (STATUS_GREATHER | STATUS_EQUAL)) {
			pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		} else {
			pvm.ip += 2;
		}
		break;
	}
	case CMD_JMPLT: {
		if (pvm.status & STATUS_LOWER) {
			pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		} else {
			pvm.ip += 2;
		}
		break;
	}
	case CMD_JMPLE: {
		if (pvm.status & (STATUS_LOWER | STATUS_EQUAL)) {
			pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		} else {
			pvm.ip += 2;
		}
		break;
	}
	case CMD_JMPCS: {
		if ((pvm.status & STATUS_CARRY) != 0) {
			pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		} else {
			pvm.ip += 2;
		}
		break;
	}
	case CMD_JMPCC: {
		if (pvm.status & STATUS_CARRY) {
			pvm.ip += 2;
		} else {
			pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		}
		break;
	}
	case CMD_JMPZS: {
		if (pvm.status & STATUS_ZERO) {
			pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		} else {
			pvm.ip += 2;
		}
		break;
	}
	case CMD_JMPZC: {
		if (pvm.status & STATUS_ZERO) {
			pvm.ip += 2;
		} else {
			pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		}
		break;
	}
	case CMD_JMPNAN: {
		if (pvm.status & STATUS_NAN) {
			pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		} else {
			pvm.ip += 2;
		}
		break;
	}
	case CMD_JMPAN: {
		if (pvm.status & STATUS_NAN) {
			pvm.ip += 2;
		} else {
			pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		}
		break;
	}
	case CMD_CALL: {
		*pvm.sp = (num) (pvm.ip + 2);
		pvm.sp++;
		pvm.ip = (num*) (((num) pvm.ip) + pvm.ip[1]);
		break;
	}
	case CMD_CALO: {
		getOneConstParam
		*pvm.sp = (num) (pvm.ip + 1 + len);
		pvm.sp++;
		pvm.ip = (num*) (p1 + pvm.ip[len]);
		break;
	}
	case CMD_CMP: {
		getTwoConstParam
		pvm.status &= ~(STATUS_GREATHER | STATUS_LOWER | STATUS_EQUAL);
		if (p1 > p2) {
			pvm.status |= STATUS_GREATHER;
		} else if (p1 < p2) {
			pvm.status |= STATUS_LOWER;
		} else {
			pvm.status |= STATUS_EQUAL;
		}
		pvm.ip += len;
		break;
	}
	case CMD_CMPFP: {
		getTwoConstUnionParam
		pvm.status &= ~(STATUS_GREATHER | STATUS_LOWER | STATUS_EQUAL | STATUS_NAN);
		if (p1.fpn > p2.fpn) {
			pvm.status |= STATUS_GREATHER;
		} else if (p1.fpn < p2.fpn) {
			pvm.status |= STATUS_LOWER;
		} else if (isnan(p1.fpn) || isnan(p2.fpn)) {
			pvm.status |= STATUS_NAN;
		} else {
			pvm.status |= STATUS_EQUAL;
		}
		pvm.ip += len;
		break;
	}
	case CMD_RET: {
		pvm.sp--;
		pvm.ip = (num*) *pvm.sp;
		break;
	}
	case CMD_IRET: {
		num *regpntr = (num*) pvm.regs[15];
		memcpy(&pvm, (void*) pvm.regs[15], sizeof(num) * 16);
		pvm.ip = (num*) pvm.x[2];
		break;
	}
	case CMD_INT: {
		getOneConstParam
		pvm.ip += len;
		p1 = interrupt(p1);
		if (p1 >= 0) {
			if (p1 >= ALL_INTS_INTCNT) {
				num invalid = p1;
				p1 = interrupt(DEF_INT_ERRORS_ILLEGAL_INTERRUPT);
				pvm.x[0] = invalid;
				if (p1 < 0) {
					break;
				}
			}
			defaultinterrupts[p1](p1);
		}
		break;
	}
	case CMD_PUSH: {
		getOneConstParam
		*pvm.sp = p1;
		pvm.sp++;
		pvm.ip += len;
		break;
	}
	case CMD_POP: {
		getOneNoConstParam
		pvm.sp--;
		*p1 = *pvm.sp;
		pvm.ip += len;
		break;
	}
	case CMD_INC: {
		getOneNoConstParam
		if (*p1 == DEF_MAX_VALUE) {
			pvm.status |= STATUS_CARRY;
		} else if (*p1 == -1) {
			pvm.status |= STATUS_ZERO;
		} else {
			pvm.status &= ~(STATUS_CARRY | STATUS_ZERO);
		}
		*p1 += 1;
		pvm.ip += len;
		break;
	}
	case CMD_DEC: {
		getOneNoConstParam
		if (*p1 == DEF_MIN_VALUE) {
			pvm.status |= STATUS_CARRY;
		} else if (*p1 == 1) {
			pvm.status |= STATUS_ZERO;
		} else {
			pvm.status &= ~(STATUS_CARRY | STATUS_ZERO);
		}
		*p1 -= 1;
		pvm.ip += len;
		break;
	}
	case CMD_FPTN: {
		getOneNoConstUnionParam
		p1->num = (num) p1->fpn;
		pvm.ip += len;
		break;
	}
	case CMD_NTFP: {
		getOneNoConstUnionParam
		p1->fpn = (fpnum) p1->num;
		pvm.ip += len;
		break;
	}
	case CMD_CHKFP: {
		getOneConstUnionParam
		pvm.status &= ~(STATUS_GREATHER | STATUS_LOWER | STATUS_ZERO | STATUS_NAN);
		//do not use isinf makro!
		//isinf makro return 1 for positive and negative infinity!
		int inf = (isinf)(p1.fpn);
		if (inf == 0) {
			if (isnan(p1.fpn)) {
				pvm.status |= STATUS_NAN;
			} else {
				pvm.status |= STATUS_ZERO;
			}
		} else if (inf > 0) {
			pvm.status |= STATUS_GREATHER;
		} else {
			pvm.status |= STATUS_LOWER;
		}
		pvm.ip += len;
		break;
	}
	default:
		unknownCommand
	}
}

/*
 * returns intnum when the default interrupt has to be executed
 * and -1 if the default was overwritten and -2 when it is overwritten, but not the original interrupt
 */

static num interrupt(num intnum) {
	if (pvm.intp == NULL) { //always default interrupt
		if (pvm.intcnt <= intnum || intnum < 0) { //illegal interrupt
			if (pvm.intcnt <= DEF_INT_ERRORS_ILLEGAL_INTERRUPT) { //illegal interrupt interrupt is illegal
				exit(EXIT_NUM_ILLEGAL_INTERRUPT_IS_ILLEGAL);
			}
			//call illegal interrupt interrupt
			pvm.x[0] = intnum;
			return DEF_INT_ERRORS_ILLEGAL_INTERRUPT;
		}
		//legal interrupt
		return intnum;
	}
	//Maybe overwritten interrupt
	if (pvm.intcnt <= intnum || intnum < 0) { //illegal interrupt
		if (pvm.intcnt <= DEF_INT_ERRORS_ILLEGAL_INTERRUPT) { //illegal interrupt interrupt is also illegal
			exit(EXIT_NUM_ILLEGAL_INTERRUPT_IS_ILLEGAL);
		}
		//call illegal interrupt interrupt
		int res = interrupt(DEF_INT_ERRORS_ILLEGAL_INTERRUPT);
		pvm.x[0] = intnum;
		if (res == -1) {
			return -2;
		}
		return res;
	}
	num newip = pvm.intp[intnum];
	if (newip == -1) { //this interrupt is a legal default interrupt
		return intnum;
	}
	//this interrupt is a legal overwritten interrupt
	struct pvm *pvmsn = malloc(sizeof(num) * 16);
	memcpy(pvmsn, &pvm, sizeof(num) * 16);
	pvm.regs[15] = (num) pvmsn;
	pvm.ip = (num*) newip;
	return -1;
}

static int inline numToStr(num number, char *buffer, int base) {
	if (number == 0L) {
		buffer[0] = '0';
		buffer[1] = '\0';
		return 1;
	}
	int len = 0;
	int addlen = 0;
	if (number < 0L) {
		buffer[0] = '-';
		addlen++;
		buffer++;
		num mod = -(number % base);
		number = -(number / base);
		buffer[len++] = digits[mod];
	}
	while (number) {
		num mod = number % base;
		number = number / base;
		buffer[len++] = digits[mod];
	}
	buffer[len] = '\0';
	int i = 0;
	for (const int end = len / 2; i < end; i++) {
		char zw = buffer[i];
		buffer[i] = buffer[len - i - 1];
		buffer[len - i - 1] = zw;
	}
	return len + addlen;
}

static void int_errors_illegal_interrupt(int intnum) {
	/*
	 * exit(EXIT_NUM_ILLEGAL_INTERRUPT_IS_ILLEGAL) is
	 * called in the interrupt(num) method this happens
	 * when this interrupt is forbidden and a forbidden
	 * interrupt is called
	 */
	if (pvm.x[0] == DEF_INT_EXIT) {
		exit(EXIT_NUM_EXIT_IS_ILLEGAL);
	}
	num p1 = interrupt(DEF_INT_EXIT);
	if (p1 != DEF_INT_ERRORS_ILLEGAL_INTERRUPT && p1 != -2) {
		pvm.x[0] += EXIT_NUM_ILLEGAL_INTERRUPT_ADD;
	}
	if (p1 >= 0) {
		defaultinterrupts[p1](p1);
	}
}

static void int_errors_unknown_command(int intnum) {
	num p1 = interrupt(DEF_INT_EXIT);
	if (p1 != DEF_INT_ERRORS_ILLEGAL_INTERRUPT && p1 != -2) {
		pvm.x[0] = EXIT_NUM_UNKNOWN_COMMAND;
	}
	if (p1 >= 0) {
		defaultinterrupts[p1](p1);
	}
}

static void int_errors_illegal_memory(int intnum) {
	num p1 = interrupt(DEF_INT_EXIT);
	if (p1 != DEF_INT_ERRORS_ILLEGAL_INTERRUPT && p1 != -2) {
		pvm.x[0] = EXIT_NUM_ILLEGAL_MEMORY;
	}
	if (p1 >= 0) {
		defaultinterrupts[p1](p1);
	}
}

static void int_errors_arithmetic_error(int intnum) {
	num p1 = interrupt(DEF_INT_EXIT);
	if (p1 != DEF_INT_ERRORS_ILLEGAL_INTERRUPT && p1 != -2) {
		pvm.x[0] = EXIT_NUM_ARITHMETIC_ERROR;
	}
	if (p1 >= 0) {
		defaultinterrupts[p1](p1);
	}
}

static void int_exit(int intnum) {
	exit(pvm.x[0]);
}

static void int_memory_alloc(int intnum) {
	void *pntr = malloc(pvm.x[0]);
	if (pntr == NULL) {
		pvm.x[0] = -1L;
	} else {
		pvm.x[0] = (num) pntr;
	}
}

static void int_memory_realloc(int intnum) {
	void *pntr = realloc((void*) pvm.x[0], pvm.x[1]);
	if (pntr == NULL) {
		pvm.x[1] = -1L;
	} else {
		pvm.x[1] = (num) pntr;
	}
}

static void int_memory_free(int intnum) {
	free((void*) pvm.x[0]);
}

static void int_streams_new_in(int intnum) {
	const char *cs = (const char*) pvm.x[0];
	pvm.x[0] = open64(cs, O_LARGEFILE | O_RDONLY);
}

static void int_streams_new_out(int intnum) {
	const char *cs = (const char*) pvm.x[0];
	pvm.x[0] = open64(cs, O_LARGEFILE | O_WRONLY);
}

static void int_streams_new_append(int intnum) {
	const char *cs = (const char*) pvm.x[0];
	pvm.x[0] = open64(cs, O_LARGEFILE | O_WRONLY | O_APPEND);
}

static void int_streams_new_in_out(int intnum) {
	const char *cs = (const char*) pvm.x[0];
	pvm.x[0] = open64(cs, O_LARGEFILE | O_RDWR);
}

static void int_streams_new_append_in_out(int intnum) {
	const char *cs = (const char*) pvm.x[0];
	pvm.x[0] = open64(cs, O_LARGEFILE | O_RDWR | O_APPEND);
}

static void int_streams_write(int intnum) {
	pvm.x[1] = write(pvm.x[0], (void*) pvm.x[2], pvm.x[1]);
}

static void int_streams_read(int intnum) {
	num zw = read(pvm.x[0], (void*) pvm.x[2], pvm.x[1]);
	pvm.x[1] = zw;
}

static void int_streams_sync_stream(int intnum) {
	int fd = pvm.x[0];
	if (fd == -1L) {
		sync(); //sync is always successful
		pvm.x[0] = 1;
	} else {
		pvm.x[0] = fsync(fd) == 0;
	}
}

static void int_streams_close_stream(int intnum) {
	if (close(pvm.x[0]) == 0) {
		pvm.x[0] = 1;
	} else {
		pvm.x[0] = 0;
	}
}

static void int_streams_get_pos(int intnum) {
	pvm.x[1] = lseek64(pvm.x[0], 0, SEEK_CUR);
}

static void int_streams_set_pos(int intnum) {
	pvm.x[1] = lseek64(pvm.x[0], pvm.x[1], SEEK_SET);
}

static void int_streams_set_pos_to_end(int intnum) {
	pvm.x[1] = lseek64(pvm.x[0], 0, SEEK_END);
}

static void int_fs_rem(int intnum) {
	const char *cs = (const char*) pvm.x[0];
	if (unlink(cs) == 0) {
		pvm.x[0] = 1;
	} else {
		pvm.x[0] = 0;
	}
}

static void int_fs_mk_dir(int intnum) {
	const char *cs = (const char*) pvm.x[0];
	if (mkdir(cs, S_IRWXU) == 0) {
		pvm.x[0] = 1;
	} else {
		pvm.x[0] = 0;
	}
}

static void int_fs_rem_dir(int intnum) {
	const char *cs = (const char*) pvm.x[0];
	if (rmdir(cs) == 0) {
		pvm.x[0] = 1;
	} else {
		pvm.x[0] = 0;
	}
}

static void int_time_get(int intnum) {
	pvm.x[0] = time(NULL);
}

static void int_time_wait(int intnum) {
	struct timespec time;
	struct timespec remain;
	time.tv_sec = pvm.x[0];
	time.tv_nsec = pvm.x[1];
	if (nanosleep(&time, &remain) == 0) {
		pvm.x[0] = 0;
		pvm.x[1] = 0;
		pvm.x[2] = 1;
	} else {
		pvm.x[0] = remain.tv_nsec;
		pvm.x[1] = remain.tv_sec;
		pvm.x[2] = 0;
	}
}

static void int_socket_client_create(int intnum) {
	pvm.x[0] = socket(AF_INET, SOCK_STREAM, 0);
}

static void int_socket_client_connect(int intnum) {
	struct hostent *hp;
	const char *cs = (const char*) pvm.x[1];
	hp = gethostbyname(cs);
	if (hp == NULL) {
		pvm.x[1] = -1;
		return;
	}
	struct sockaddr_in server;
	server.sin_family = AF_INET;
	server.sin_port = htons(pvm.x[2]);
	memcpy(&server.sin_addr, hp->h_addr, hp->h_length);
	if (connect(pvm.x[0], (const struct sockaddr*) &server, sizeof(struct sockaddr_in)) != 0) {
		pvm.x[1] = 0;
	} else {
		pvm.x[1] = 1;
	}
}

static void int_socket_server_create(int intnum) {
	struct sockaddr_in sokadr;
	sokadr.sin_family = AF_INET;
	sokadr.sin_addr.s_addr = INADDR_ANY;
	sokadr.sin_port = htons(pvm.x[0]);
	pvm.x[0] = socket(AF_INET, SOCK_STREAM, 0);
	bind(pvm.x[0], (const struct sockaddr*) &sokadr, sizeof(struct sockaddr_in));
}

static void int_socket_server_listen(int intnum) {
	if (listen(pvm.x[0], pvm.x[1]) == 0) {
		pvm.x[1] = 1;
	} else {
		pvm.x[1] = 0;
	}
}

static void int_socket_server_accept(int intnum) {
	pvm.x[1] = accept(pvm.x[0], 0, 0);
}

static void int_random(int intnum) {
	pvm.x[0] = ((unum) rand()) | (((unum) rand()) << 31) | (((unum) rand()) << 62);
}

static void int_memory_copy(int intnum) {
	memcpy((void*) pvm.x[0], (void*) pvm.x[1], pvm.x[2]);
}

static void int_memory_move(int intnum) {
	memmove((void*) pvm.x[0], (void*) pvm.x[1], pvm.x[2]);
}

static void int_memory_bset(int intnum) {
	memset((void*) pvm.x[0], pvm.x[1], pvm.x[2]);
}

static void int_memory_set(int intnum) {
	num *arr = (num*) pvm.x[0];
	num val = pvm.x[1];
	num len = pvm.x[2];
	while (len--) {
		arr[len] = val;
	}
}

static void int_string_length(int intnum) {
	pvm.x[0] = strlen((const char*) pvm.x[0]);
}

static void int_string_to_number(int intnum) {
	pvm.x[0] = strtol((const char*) pvm.x[0], (char**) pvm.x[1], pvm.x[1]);
}

static void int_string_to_fpnumber(int intnum) {
	pvm.x[0] = strtod((const char*) pvm.x[0], (char**) pvm.x[1]);
}

static void int_number_to_string(int intnum) {
	num number = pvm.x[0];
	char *buffer = (char*) pvm.x[1];
	const num base = pvm.x[2];
	pvm.x[0] = numToStr(number, buffer, base);
}

static void int_fpnumber_to_string(int intnum) {
	{
		char *ignore = gcvt(((union pvm_command_union) pvm.x[0]).fpn, pvm.x[1], (char*) pvm.x[2]);
	}
}

#define ensureLength(ti) \
	if ((ti) >= tsize) {\
		tsize = (ti) + tsize / 2;\
		void* ntarget_str = realloc(target_str, tsize);\
		if (ntarget_str == NULL) {\
			free(target_str);\
			target_str = NULL;\
			pvm.x[1] = -1L;\
			return;\
		}\
		target_str = ntarget_str;\
	}

static void int_string_format(int intnum) {
	char *source = (char*) pvm.x[0];
	num tsize;
	char *target_str;
	if (pvm.x[1] == -1L) {
		num sourcelen = strlen(source);
		tsize = sourcelen + sourcelen / 2;
		target_str = malloc(tsize);
	} else {
		target_str = (char*) pvm.x[1];
		tsize = DEF_MAX_VALUE;
	}
	num varArgI = 2;
	num ti = 0;
	for (num si = 0;; si++) {
		char sc = source[si];
		switch (sc) {
		case '\0':
//					ensureLength(ti)
			if (tsize == ti) {
				target_str = realloc(target_str, tsize += 1);
			}
			target_str[ti++] = '\0';
			goto strFormatEnd;
		case '%':
			sc = source[++si];
			switch (sc) {
			case 's': {
				const char *str = (const char*) pvm.x[varArgI++];
				int len = strlen(str);
				ensureLength(ti)
				memcpy(target_str + ti, str, len);
				ti += len;
				break;
			}
			case 'c': {
				ensureLength(ti + 8)
				//0b0....... -> one byte
				//0b10...... -> invalid, treated as one byte
				//0b110..... -> two bytes
				//0b1110.... -> three bytes
				//0b11110... -> four bytes
				//0b111110.. -> invalid, treated as five bytes
				//0b1111110. -> invalid, treated as six bytes
				//0b11111110 -> invalid, treated as seven bytes
				//0b11111111 -> invalid, treated as eight bytes
				int leadingBits;
				num n = pvm.x[varArgI];
				for (leadingBits = 0; (n & DEF_MIN_VALUE) != 0; leadingBits++) {
					n <<= 1;
				}
				const char *pntr = (const char*) &pvm.x[varArgI++];
				int i = 0;
				do {
					target_str[ti++] = pntr[i++];
				} while (--leadingBits > 0);
				break;
			}
			case 'B': {
				target_str[ti++] = (char) pvm.x[varArgI++];
				break;
			}
			case 'd': {
				ensureLength(ti + 65)
				num number = pvm.x[varArgI++];
				ti += numToStr(number, (target_str + ti), 10);
				break;
			}
			case 'p': {
				ensureLength(ti + 65)
				union pvm_command_union number;
				number.num = pvm.x[varArgI++];
				if (number.num == -1) {
					ensureLength(ti + 4);
					target_str[ti++] = '-';
					target_str[ti++] = '-';
					target_str[ti++] = '-';
				} else {
					ensureLength(ti + 19);
					target_str[ti++] = 'p';
					target_str[ti++] = '-';
					for (int i = 7; i >= 0; i--) {
						int tnc = number.cmds[i] & 0x0F;
						if (tnc < 0x0A) {
							target_str[ti++] = '0' + tnc;
						} else {
							target_str[ti++] = 'A' - 0x0A + tnc;
						}
						tnc = number.cmds[i] & 0xF0;
						tnc = tnc >> 4;
						if (tnc < 0x0A) {
							target_str[ti++] = '0' + tnc;
						} else {
							target_str[ti++] = 'A' - 0x0A + tnc;
						}
					}
				}
				break;
			}
			case 'f': {
				ensureLength(ti + 128)
				union pvm_command_union fpnum;
				fpnum.num = pvm.x[varArgI++];
				char *zw = target_str + ti;
				//128-2, if the '.' and the '-' does not count (I don't know)
				//In most cases it will be ignored, because it is too high
				{
					char *ignore = gcvt(fpnum.fpn, 126, zw);
				}
				ti += strlen(zw);
				break;
			}
			case 'h': {
				ensureLength(ti + 17)
				num number = pvm.x[varArgI++];
				ti += numToStr(number, (target_str + ti), 16);
				break;
			}
			case 'b': {
				ensureLength(ti + 65)
				num number = pvm.x[varArgI++];
				ti += numToStr(number, (target_str + ti), 2);
				break;
			}
			case 'o': {
				ensureLength(ti + 33)
				num number = pvm.x[varArgI++];
				ti += numToStr(number, target_str + ti, 8);
				break;
			}
			default:
				ensureLength(ti + 2)
				target_str[ti++] = '%';
				target_str[ti++] = sc;
			}
			break;
		default:
			ensureLength(ti + 1)
			target_str[ti++] = sc;
		}
	}
	strFormatEnd:
	if (ti < tsize) {
		//since ti is smaller than the allocated size, that can not lead to an error
		target_str = realloc(target_str, ti);
	}
	pvm.x[0] = ti;
	pvm.x[1] = (num) target_str;
}

static void int_load_file(int intnum) {
	const char *cs = (const char*) pvm.x[0];
	int fd = open64(cs, O_LARGEFILE | O_RDONLY);
	if (fd == -1) {
		pvm.x[0] = -1L;
		return;
	}
	num len = lseek64(pvm.x[0], 0, SEEK_END);
	void *data = malloc(len);
	lseek64(pvm.x[0], 0, SEEK_SET);
	const num origlen = len;
	while (len > 0) {
		int r = read(fd, data, len);
		len -= r;
	}
	close(fd);
	pvm.x[0] = (num) data;
	pvm.x[1] = origlen;
}
