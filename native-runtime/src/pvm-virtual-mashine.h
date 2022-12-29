/*
 * pvm-virtual-mashine.h
 *
 *  Created on: Nov 2, 2022
 *      Author: pat
 */

#ifndef SRC_PVM_VIRTUAL_MASHINE_H_
#define SRC_PVM_VIRTUAL_MASHINE_H_

#include <stddef.h>
#include <stdint.h>

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
	union {
		num num;
		unum unum;
	} status; // regs[5]
	num x[256 - 6]; // reg[6..255] // - 6 because XFA shares its address with the errno register
	num errno; // regs[255]
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

#ifdef PVM

#	ifdef PVM_DEBUG
static volatile enum {
	running,

	stepping,

	waiting,
} state;

EXT num depth;
#	endif // PVM_DEBUG

#endif // PVM

#define MEM_NO_RESIZE       0x00000001u
#define MEM_NO_FREE         0x00000002u
#define MEM_AUTO_GROW       0x00000004U
// TODO implement auto grow
#define MEM_AUTO_GROW_BITS  0xFF000000U
#define MEM_AUTO_GROW_SHIFT 24

struct memory {
	num start;
	num end;
	void *offset;
	unsigned flags;
	unsigned grow_size;
	num *end_pntr;
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
 * which has already been freed or mooved away
 *
 * this PVM implementation requires the holes exist (size 1 should work)
 */
#	define ADRESS_HOLE_DEFAULT_SIZE 1024
#	define ADRESS_HOLE_MINIMUM_SIZE 32

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
PVM_SI_PREFIX struct memory* realloc_memory(num adr, num newsize);
PVM_SI_PREFIX void free_memory(num adr);
#endif /* defined PVM | defined PVM_DEBUG */

#endif /* SRC_PVM_VIRTUAL_MASHINE_H_ */
