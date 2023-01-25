/*
 * pvm-virtual-mashine.h
 *
 *  Created on: Nov 2, 2022
 *      Author: pat
 */

#ifndef SRC_PVM_VIRTUAL_MASHINE_H_
#define SRC_PVM_VIRTUAL_MASHINE_H_

#include <stdint.h>
#include <stdlib.h>
#include <stddef.h>

#ifdef PVM
#	define EXT
#else
#	define EXT extern
#endif

typedef int64_t num;
typedef uint64_t unum;
typedef double fpnum;
typedef uint8_t byte;
typedef uint16_t word;
typedef uint32_t double_word;

void execute() __attribute__ ((__noreturn__));

void pvm_init(char **argv, num argc_ount, void *exe, num exe_size);

#ifdef PVM_DEBUG
void pvm_debug_init(int input, _Bool input_is_pipe, _Bool wait);
#endif // PVM_DEBUG

_Static_assert(sizeof(num) == 8, "Error!");
_Static_assert(sizeof(num) == sizeof(unum), "Error!");
_Static_assert(sizeof(fpnum) == sizeof(unum), "Error!");
_Static_assert(sizeof(void*) == sizeof(unum), "Error!");

EXT struct pvm {
	num regs[0]; // array size set to zero, because this is no union
	num ip; // regs[0]
	num sp; // regs[1]
	num intp; // regs[2]
	num intcnt; // regs[3]
	unum status; // regs[5]
	num err; // regs[6]
	fpnum fpx[0]; // reg[7..255]
	num x[256 - 6]; // reg[7..255]
} pvm;

_Static_assert((sizeof(num) * 256) == sizeof(struct pvm), "Error!");

#define S_LOWER     0x0000000000000001UL
#define S_GREATHER  0x0000000000000002UL
#define S_EQUAL     0x0000000000000004UL
#define S_OVERFLOW  0x0000000000000008UL
#define S_ZERO      0x0000000000000010UL
#define S_NAN       0x0000000000000020UL
#define S_ALL_BITS  0x0000000000000040UL
#define S_SOME_BITS 0x0000000000000080UL
#define S_NONE_BITS 0x0000000000000100UL

#define __P_BASE  0x01
#define __P_A_NUM 0x02
#define __P_A_REG 0x04
#define __P_B_NO  0x10
#define __P_B_NUM 0x20
#define __P_B_REG 0x40

enum param_type {
	P_NUM = __P_BASE | __P_A_NUM | __P_B_NO,
	P_REG = __P_BASE | __P_A_REG | __P_B_NO,
	P_NUM_NUM = __P_BASE | __P_A_NUM | __P_B_NUM,
	P_REG_NUM = __P_BASE | __P_A_REG | __P_B_NUM,
	P_NUM_REG = __P_BASE | __P_A_NUM | __P_B_REG,
	P_REG_REG = __P_BASE | __P_A_REG | __P_B_REG,
};

#define NUM_MAX_VALUE 0x7FFFFFFFFFFFFFFF
#define NUM_MIN_VALUE 0x8000000000000000

#ifdef PVM

#	ifdef PVM_DEBUG

static int pvm_same_address(const void *a, const void *b);
static unsigned int pvm_address_hash(const void *a);

static struct hashset breakpoints = {
		.entries = NULL,
		.entrycount = 0,
		.equalizer = pvm_same_address,
		.hashmaker = pvm_address_hash,
		.setsize = 0,
};
static enum pvm_db_state {
	pvm_ds_running,

	pvm_ds_new_running,

	pvm_ds_stepping,

	pvm_ds_waiting,

	pvm_ds_new_stepping,

	pvm_ds_init = pvm_ds_waiting,
} pvm_state, pvm_next_state;

static int pvm_depth;

#	endif // PVM_DEBUG

#endif // PVM

#define MEM_NO_RESIZE       0x00000001u
#define MEM_NO_FREE         0x00000002u
#define MEM_AUTO_GROW       0x00000004U
#define MEM_AUTO_GROW_BITS  0xFF000000U
#define MEM_AUTO_GROW_SHIFT 24
#define MEM_INT             0x00000008U
#define MEM_LIB             0x00000010U

struct memory {
	num start;
	num end;
	void *offset;
	unsigned flags;
	unsigned grow_size;
	num *change_pntr;
};

struct memory2 {
	struct memory *mem;
	void *adr;
};

#ifdef PVM

/*
 * the address holes are a security feature.
 * the program should not rely on such holes
 *
 * since memory can grow and shrink memory previously used by a
 * memory block can be 'moved' to be inside of an other memory block
 * which has already been freed or moved away
 *
 * this value should at least be 256, if lower the automatic grow range
 * of a automatic grow memory block could overlap with an other memory block
 */
#	define ADRESS_HOLE_DEFAULT_SIZE 4096
#	define ADRESS_HOLE_MINIMUM_SIZE 512

#	define REGISTER_START 0x0000000000001000
#	define REGISTER_END   0x0000000000001800
#	define REGISTER_SIZE  0x0000000000000800

// I know that an overflow will occur when the program
// (executes long enough/allocates (and frees) often enough memory)

static struct memory *memory = NULL;
static num mem_size = 0;
static num next_adress = REGISTER_START;

#endif // PVM

#if defined PVM | defined PVM_DEBUG
#	if defined PVM & !defined PVM_DEBUG
#		define PVM_SI_PREFIX static inline
#	else
#		define PVM_SI_PREFIX
#	endif

PVM_SI_PREFIX struct memory2 alloc_memory(num size, unsigned flags);
PVM_SI_PREFIX struct memory* alloc_memory2(void *mem, num size, unsigned flags);
PVM_SI_PREFIX struct memory* realloc_memory(num adr, num newsize, _Bool auto_growing);
PVM_SI_PREFIX void free_memory(num adr);
#endif /* defined PVM | defined PVM_DEBUG */

#endif /* SRC_PVM_VIRTUAL_MASHINE_H_ */
