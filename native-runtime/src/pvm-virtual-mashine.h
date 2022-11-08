/*
 * pvm-virtual-mashine.h
 *
 *  Created on: Nov 2, 2022
 *      Author: pat
 */

#ifndef SRC_PVM_VIRTUAL_MASHINE_H_
#define SRC_PVM_VIRTUAL_MASHINE_H_

#include <stdint.h>

#ifdef PVM
#define EXT
#else
#define EXT extern
#endif

void execute() __attribute__ ((__noreturn__));

typedef int64_t num;
typedef uint64_t unum;
typedef double fpnum;
typedef int8_t byte;
typedef int16_t word;
typedef int32_t double_word;

_Static_assert(sizeof(num) == sizeof(unum), "Error!");
_Static_assert(sizeof(fpnum) == sizeof(unum), "Error!");
_Static_assert(sizeof(void*) == sizeof(unum), "Error!");

EXT struct pvm {
	num regs[0]; // array size set to zero, because this is no union
	union {
		num num;
		union {
			num num;
			byte cmd[8];
		} *pntr;
	} ip; // regs[0]
	union {
		num num;
		num *pntr;
	} sp; // regs[1]
	union {
		num num;
		union {
			num num;
			byte cmd[8];
		} *pntr;
	} intp; // regs[2]
	num intcnt; // regs[3]
	union {
		num num;
		unum unum;
	} status; // regs[5]
	num xnn[256 - 6]; // reg[6..255] // - 6 because XFA shares its address with the errno register
	num errno; // regs[255]
} pvm;

_Static_assert((sizeof(num) * 256) == sizeof(struct pvm), "Error!");

enum cmd {
	MVB = 0x01,
	MVW = 0x02,
	MVDW = 0x03,
	MOV = 0x04,
	LEA = 0x05,
	MVAD = 0x06,
	SWAP = 0x07,

	ADD = 0x10,
	SUB = 0x11,
	MUL = 0x12,
	DIV = 0x13,
	NEG = 0x14,
	ADDC = 0x15,
	SUBC = 0x16,
	INC = 0x17,
	DEC = 0x18,
	OR = 0x19,
	AND = 0x1A,
	XOR = 0x1B,
	NOT = 0x1C,
	LSH = 0x1D,
	RASH = 0x1E,
	RLSH = 0x1F,

	JMP = 0x20,
	JMPEQ = 0x21,
	JMPNE = 0x22,
	JMPGT = 0x23,
	JMPGE = 0x24,
	JMPLT = 0x25,
	JMPLE = 0x26,
	JMPCS = 0x27,
	JMPCC = 0x28,
	JMPZS = 0x29,
	JMPZC = 0x2A,
	JMPNAN = 0x2B,
	JMPAN = 0x2C,
	JMPAB = 0x2D,
	JMPSB = 0x2E,
	JMPNB = 0x2F,

	INT = 0x30,
	IRET = 0x31,
	CALL = 0x32,
	CALO = 0x33,
	RET = 0x34,
	PUSH = 0x35,
	POP = 0x36,

	CMP = 0x40,
	BCMP = 0x41,

	FPCMP = 0x50,
	FPCHK = 0x51,
	FPADD = 0x52,
	FPSUB = 0x53,
	FPMUL = 0x54,
	FPDIV = 0x55,
	FPNEG = 0x56,
	FPTN = 0x57,
	NTFP = 0x58,
	UADD = 0x59,
	USUB = 0x5A,
	UMUL = 0x5B,
	UDIV = 0x5C,
};

#ifdef PVM

#ifdef PVM_DEBUG
static volatile enum {
	running,

	stepping,

	waiting,
} state;

EXT num depth;
#endif // PVM_DEBUG

#ifdef PVM_DEBUG
#define INT_PARAMS (num int_num)
#define CI(params) (params)
#else
#define INT_PARAMS ()
#define CI(params) (params)
#endif

static void int_errors_illegal_interrupt          INT_PARAMS; /* 0 */
static void int_errors_unknown_command            INT_PARAMS; /* 1 */
static void int_errors_illegal_memory             INT_PARAMS; /* 2 */
static void int_errors_arithmetic_error           INT_PARAMS; /* 3 */
static void int_exit                              INT_PARAMS; /* 4 */
static void int_memory_alloc                      INT_PARAMS; /* 5 */
static void int_memory_realloc                    INT_PARAMS; /* 6 */
static void int_memory_free                       INT_PARAMS; /* 7 */
static void int_open_stream                       INT_PARAMS; /* 8 */
static void int_streams_write                     INT_PARAMS; /* 9 */
static void int_streams_read                      INT_PARAMS; /* 10 */
static void int_streams_close                     INT_PARAMS; /* 11 */
static void int_streams_get_pos                   INT_PARAMS; /* 12 */
static void int_streams_seek_set                  INT_PARAMS; /* 13 */
static void int_streams_seek_add                  INT_PARAMS; /* 14 */
static void int_streams_seek_eof                  INT_PARAMS; /* 15 */
static void int_open_element_file                 INT_PARAMS; /* 16 */
static void int_open_element_folder               INT_PARAMS; /* 17 */
static void int_open_element_pipe                 INT_PARAMS; /* 18 */
static void int_open_element                      INT_PARAMS; /* 19 */
static void int_element_open_parent               INT_PARAMS; /* 20 */
static void int_element_get_create                INT_PARAMS; /* 21 */
static void int_element_get_last_mod              INT_PARAMS; /* 22 */
static void int_element_set_create                INT_PARAMS; /* 23 */
static void int_element_set_last_mod              INT_PARAMS; /* 24 */
static void int_element_delete                    INT_PARAMS; /* 25 */
static void int_element_move                      INT_PARAMS; /* 26 */
static void int_element_get_flags                 INT_PARAMS; /* 27 */
static void int_element_mod_flags                 INT_PARAMS; /* 28 */
static void int_folder_child_count                INT_PARAMS; /* 29 */
static void int_folder_get_child_of_name          INT_PARAMS; /* 30 */
static void int_folder_add_folder                 INT_PARAMS; /* 31 */
static void int_folder_add_file                   INT_PARAMS; /* 32 */
static void int_folder_add_link                   INT_PARAMS; /* 33 */
static void int_folder_open_iter                  INT_PARAMS; /* 34 */
static void int_folder_create_folder              INT_PARAMS; /* 35 */
static void int_folder_create_file                INT_PARAMS; /* 36 */
static void int_folder_create_pipe                INT_PARAMS; /* 37 */
static void int_file_length                       INT_PARAMS; /* 38 */
static void int_file_truncate                     INT_PARAMS; /* 39 */
static void int_file_open_stream                  INT_PARAMS; /* 40 */
static void int_pipe_length                       INT_PARAMS; /* 41 */
static void int_pipe_truncate                     INT_PARAMS; /* 42 */
static void int_pipe_open_stream                  INT_PARAMS; /* 43 */
static void int_time_get                          INT_PARAMS; /* 44 */
static void int_time_wait                         INT_PARAMS; /* 45 */
static void int_random                            INT_PARAMS; /* 46 */
static void int_memory_copy                       INT_PARAMS; /* 47 */
static void int_memory_move                       INT_PARAMS; /* 48 */
static void int_memory_bset                       INT_PARAMS; /* 49 */
static void int_memory_set                        INT_PARAMS; /* 50 */
static void int_string_length                     INT_PARAMS; /* 51 */
static void int_string_compare                    INT_PARAMS; /* 52 */
static void int_number_to_string                  INT_PARAMS; /* 53 */
static void int_fpnumber_to_string                INT_PARAMS; /* 54 */
static void int_string_to_number                  INT_PARAMS; /* 55 */
static void int_string_to_fpnumber                INT_PARAMS; /* 56 */
static void int_string_to_u16string               INT_PARAMS; /* 57 */
static void int_string_to_u32string               INT_PARAMS; /* 58 */
static void int_u16string_to_string               INT_PARAMS; /* 59 */
static void int_u32string_to_string               INT_PARAMS; /* 60 */
static void int_string_format                     INT_PARAMS; /* 61 */
static void int_load_file                         INT_PARAMS; /* 62 */

static void (*ints[])INT_PARAMS = {
	    int_errors_illegal_interrupt      ,
#define INT_ERRORS_ILLEGAL_INTERRUPT      0
	    int_errors_unknown_command        ,
#define INT_ERRORS_UNKNOWN_COMMAND        1
	    int_errors_illegal_memory         ,
#define INT_ERRORS_ILLEGAL_MEMORY         2
	    int_errors_arithmetic_error       ,
#define INT_ERRORS_ARITHMETIC_ERROR       3
	    int_exit                          ,
#define INT_EXIT                          4
	    int_memory_alloc                  ,
#define INT_MEMORY_ALLOC                  5
	    int_memory_realloc                ,
#define INT_MEMORY_REALLOC                6
	    int_memory_free                   ,
#define INT_MEMORY_FREE                   7
	    int_open_stream                   ,
#define INT_OPEN_STREAM                   8
	    int_streams_write                 ,
#define INT_STREAMS_WRITE                 9
	    int_streams_read                  ,
#define INT_STREAMS_READ                  10
	    int_streams_close                 ,
#define INT_STREAMS_CLOSE                 11
	    int_streams_get_pos               ,
#define INT_STREAMS_GET_POS               12
	    int_streams_seek_set              ,
#define INT_STREAMS_SEEK_SET              13
	    int_streams_seek_add              ,
#define INT_STREAMS_SEEK_ADD              14
	    int_streams_seek_eof              ,
#define INT_STREAMS_SEEK_EOF              15
	    int_open_element_file             ,
#define INT_OPEN_ELEMENT_FILE             16
	    int_open_element_folder           ,
#define INT_OPEN_ELEMENT_FOLDER           17
	    int_open_element_pipe             ,
#define INT_OPEN_ELEMENT_PIPE             18
	    int_open_element                  ,
#define INT_OPEN_ELEMENT                  19
	    int_element_open_parent           ,
#define INT_ELEMENT_OPEN_PARENT           20
	    int_element_get_create            ,
#define INT_ELEMENT_GET_CREATE            21
	    int_element_get_last_mod          ,
#define INT_ELEMENT_GET_LAST_MOD          22
	    int_element_set_create            ,
#define INT_ELEMENT_SET_CREATE            23
	    int_element_set_last_mod          ,
#define INT_ELEMENT_SET_LAST_MOD          24
	    int_element_delete                ,
#define INT_ELEMENT_DELETE                25
	    int_element_move                  ,
#define INT_ELEMENT_MOVE                  26
	    int_element_get_flags             ,
#define INT_ELEMENT_GET_FLAGS             27
	    int_element_mod_flags             ,
#define INT_ELEMENT_MOD_FLAGS             28
	    int_folder_child_count            ,
#define INT_FOLDER_CHILD_COUNT            29
	    int_folder_get_child_of_name      ,
#define INT_FOLDER_GET_CHILD_OF_NAME      30
	    int_folder_add_folder             ,
#define INT_FOLDER_ADD_FOLDER             31
	    int_folder_add_file               ,
#define INT_FOLDER_ADD_FILE               32
	    int_folder_add_link               ,
#define INT_FOLDER_ADD_LINK               33
	    int_folder_open_iter              ,
#define INT_FOLDER_OPEN_ITER              34
	    int_folder_create_folder          ,
#define INT_FOLDER_CREATE_FOLDER          35
	    int_folder_create_file            ,
#define INT_FOLDER_CREATE_FILE            36
	    int_folder_create_pipe            ,
#define INT_FOLDER_CREATE_PIPE            37
	    int_file_length                   ,
#define INT_FILE_LENGTH                   38
	    int_file_truncate                 ,
#define INT_FILE_TRUNCATE                 39
	    int_file_open_stream              ,
#define INT_FILE_OPEN_STREAM              40
	    int_pipe_length                   ,
#define INT_PIPE_LENGTH                   41
	    int_pipe_truncate                 ,
#define INT_PIPE_TRUNCATE                 42
	    int_pipe_open_stream              ,
#define INT_PIPE_OPEN_STREAM              43
	    int_time_get                      ,
#define INT_TIME_GET                      44
	    int_time_wait                     ,
#define INT_TIME_WAIT                     45
	    int_random                        ,
#define INT_RANDOM                        46
	    int_memory_copy                   ,
#define INT_MEMORY_COPY                   47
	    int_memory_move                   ,
#define INT_MEMORY_MOVE                   48
	    int_memory_bset                   ,
#define INT_MEMORY_BSET                   49
	    int_memory_set                    ,
#define INT_MEMORY_SET                    50
	    int_string_length                 ,
#define INT_STRING_LENGTH                 51
	    int_string_compare                ,
#define INT_STRING_COMPARE                52
	    int_number_to_string              ,
#define INT_NUMBER_TO_STRING              53
	    int_fpnumber_to_string            ,
#define INT_FPNUMBER_TO_STRING            54
	    int_string_to_number              ,
#define INT_STRING_TO_NUMBER              55
	    int_string_to_fpnumber            ,
#define INT_STRING_TO_FPNUMBER            56
	    int_string_to_u16string           ,
#define INT_STRING_TO_U16STRING           57
	    int_string_to_u32string           ,
#define INT_STRING_TO_U32STRING           58
	    int_u16string_to_string           ,
#define INT_U16STRING_TO_STRING           59
	    int_u32string_to_string           ,
#define INT_U32STRING_TO_STRING           60
	    int_string_format                 ,
#define INT_STRING_FORMAT                 61
	    int_load_file                     ,
#define INT_LOAD_FILE                     62
};
#define INTERRUPT_COUNT                   63

_Static_assert((sizeof(void (*)INT_PARAMS) * INTERRUPT_COUNT) == sizeof(ints), "Error!");

#endif // PVM

#endif /* SRC_PVM_VIRTUAL_MASHINE_H_ */
