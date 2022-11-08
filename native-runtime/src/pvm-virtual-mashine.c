/*
 * pvm-virtual-mashine.c
 *
 *  Created on: Jul 6, 2022
 *      Author: pat
 */
#define PVM
#include "pvm-virtual-mashine.h"

#include <stdlib.h>

union numpntr {
	num num;
	void *pntr;
};

static union {
	struct {
		union numpntr start;
		union numpntr end;
	} *area;
	num *num;
} memory;

static inline num chk(void *pntr) {
	for (int i = 0; memory.area[i].start.num != -1; i++) {
		if (pntr < memory.area[i].start.pntr) {
			return -1;
		} else if (pntr < memory.area[i].end.pntr) {
			return memory.area[i].end.pntr - pntr;
		}
	}
	return -1;
}

static inline void exec() {
	if (chk(pvm.ip.pntr) < 8) {
		int_errors_illegal_memory(CI(INT_ERRORS_ILLEGAL_MEMORY));
		return;
	}
	switch (pvm.ip.pntr->cmd[0]) {
	case MVB:
	case MVW:
	case MVDW:
	case MOV:
	case LEA:
	case MVAD:
	case SWAP:

	case ADD:
	case SUB:
	case MUL:
	case DIV:
	case NEG:
	case ADDC:
	case SUBC:
	case INC:
	case DEC:
	case OR:
	case AND:
	case XOR:
	case NOT:
	case LSH:
	case RASH:
	case RLSH:

	case JMP:
	case JMPEQ:
	case JMPNE:
	case JMPGT:
	case JMPGE:
	case JMPLT:
	case JMPLE:
	case JMPCS:
	case JMPCC:
	case JMPZS:
	case JMPZC:
	case JMPNAN:
	case JMPAN:
	case JMPAB:
	case JMPSB:
	case JMPNB:

	case INT:
	case IRET:
	case CALL:
	case CALO:
	case RET:
	case PUSH:
	case POP:

	case CMP:
	case BCMP:

	case FPCMP:
	case FPCHK:
	case FPADD:
	case FPSUB:
	case FPMUL:
	case FPDIV:
	case FPNEG:
	case FPTN:
	case NTFP:
	case UADD:
	case USUB:
	case UMUL:
	case UDIV:

	default:
		int_errors_unknown_command(CI(INT_ERRORS_UNKNOWN_COMMAND));
	}
}

#ifdef PVM_DEBUG
static inline void d_wait() {
	while (1) {
		switch (state) {
		case running:
			return;
		case waiting:
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

static void int_errors_illegal_interrupt INT_PARAMS {
	abort();
}
static void int_errors_unknown_command INT_PARAMS {
	abort();
}
static void int_errors_illegal_memory INT_PARAMS {
	abort();
}
static void int_errors_arithmetic_error INT_PARAMS {
	abort();
}
static void int_exit INT_PARAMS {
	abort();
}
static void int_memory_alloc INT_PARAMS {
	abort();
}
static void int_memory_realloc INT_PARAMS {
	abort();
}
static void int_memory_free INT_PARAMS {
	abort();
}
static void int_open_stream INT_PARAMS {
	abort();
}
static void int_streams_write INT_PARAMS {
	abort();
}
static void int_streams_read INT_PARAMS {
	abort();
}
static void int_streams_close INT_PARAMS {
	abort();
}
static void int_streams_get_pos INT_PARAMS {
	abort();
}
static void int_streams_seek_set INT_PARAMS {
	abort();
}
static void int_streams_seek_add INT_PARAMS {
	abort();
}
static void int_streams_seek_eof INT_PARAMS {
	abort();
}
static void int_open_element_file INT_PARAMS {
	abort();
}
static void int_open_element_folder INT_PARAMS {
	abort();
}
static void int_open_element_pipe INT_PARAMS {
	abort();
}
static void int_open_element INT_PARAMS {
	abort();
}
static void int_element_open_parent INT_PARAMS {
	abort();
}
static void int_element_get_create INT_PARAMS {
	abort();
}
static void int_element_get_last_mod INT_PARAMS {
	abort();
}
static void int_element_set_create INT_PARAMS {
	abort();
}
static void int_element_set_last_mod INT_PARAMS {
	abort();
}
static void int_element_delete INT_PARAMS {
	abort();
}
static void int_element_move INT_PARAMS {
	abort();
}
static void int_element_get_flags INT_PARAMS {
	abort();
}
static void int_element_mod_flags INT_PARAMS {
	abort();
}
static void int_folder_child_count INT_PARAMS {
	abort();
}
static void int_folder_get_child_of_name INT_PARAMS {
	abort();
}
static void int_folder_add_folder INT_PARAMS {
	abort();
}
static void int_folder_add_file INT_PARAMS {
	abort();
}
static void int_folder_add_link INT_PARAMS {
	abort();
}
static void int_folder_open_iter INT_PARAMS {
	abort();
}
static void int_folder_create_folder INT_PARAMS {
	abort();
}
static void int_folder_create_file INT_PARAMS {
	abort();
}
static void int_folder_create_pipe INT_PARAMS {
	abort();
}
static void int_file_length INT_PARAMS {
	abort();
}
static void int_file_truncate INT_PARAMS {
	abort();
}
static void int_file_open_stream INT_PARAMS {
	abort();
}
static void int_pipe_length INT_PARAMS {
	abort();
}
static void int_pipe_truncate INT_PARAMS {
	abort();
}
static void int_pipe_open_stream INT_PARAMS {
	abort();
}
static void int_time_get INT_PARAMS {
	abort();
}
static void int_time_wait INT_PARAMS {
	abort();
}
static void int_random INT_PARAMS {
	abort();
}
static void int_memory_copy INT_PARAMS {
	abort();
}
static void int_memory_move INT_PARAMS {
	abort();
}
static void int_memory_bset INT_PARAMS {
	abort();
}
static void int_memory_set INT_PARAMS {
	abort();
}
static void int_string_length INT_PARAMS {
	abort();
}
static void int_string_compare INT_PARAMS {
	abort();
}
static void int_number_to_string INT_PARAMS {
	abort();
}
static void int_fpnumber_to_string INT_PARAMS {
	abort();
}
static void int_string_to_number INT_PARAMS {
	abort();
}
static void int_string_to_fpnumber INT_PARAMS {
	abort();
}
static void int_string_to_u16string INT_PARAMS {
	abort();
}
static void int_string_to_u32string INT_PARAMS {
	abort();
}
static void int_u16string_to_string INT_PARAMS {
	abort();
}
static void int_u32string_to_string INT_PARAMS {
	abort();
}
static void int_string_format INT_PARAMS {
	abort();
}
static void int_load_file INT_PARAMS {
	abort();
}
