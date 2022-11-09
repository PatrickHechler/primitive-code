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

void execute() __attribute__ ((__noreturn__));

typedef int64_t num;
typedef uint64_t unum;
typedef double fpnum;
typedef uint8_t byte;
typedef uint16_t word;
typedef uint32_t double_word;

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
	num xnn[256 - 6]; // reg[6..255] // - 6 because XFA shares its address with the errno register
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

enum param_type {
	_P_BASE = 0x01,
	_P_A_NUM = 0x02,
	_P_A_REG = 0x04,
	_P_B_NO = 0x10,
	_P_B_NUM = 0x20,
	_P_B_REG = 0x40,
	P_NUM = _P_BASE | _P_A_NUM | _P_B_NO,
	P_REG = _P_BASE | _P_A_REG | _P_B_NO,
	P_NUM_NUM = _P_BASE | _P_A_NUM | _P_B_NUM,
	P_REG_NUM = _P_BASE | _P_A_REG | _P_B_NUM,
	P_NUM_REG = _P_BASE | _P_A_NUM | _P_B_REG,
	P_REG_REG = _P_BASE | _P_A_REG | _P_B_REG,
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

#include "pvm-int.h"
#include "pvm-cmd.h"

#endif // PVM

struct memory {
	num start;
	num end;
	void *offset;
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
 *
 * this PVM implementation requires the holes exist (size 1 should work)
 */
#	define ADRESS_HOLE_DEFAULT_SIZE 256
#	define ADRESS_HOLE_MINIMUM_SIZE 32

#	define REGISTER_START 0x0000000000001000
#	define REGISTER_END 0x0000000000001800

// I know that an overflow will occur when the program
// (executes long enough|allocates and frees often enough memory)

static struct memory *memory = NULL;
static num mem_size = 0;
static num next_adress = REGISTER_END + ADRESS_HOLE_DEFAULT_SIZE;

#endif // PVM

#if defined PVM & !defined PVM_DEBUG
#	define PVM_SI_PREFIX static inline
#elif defined PVM | defined PVM_DEBUG
#	define PVM_SI_PREFIX
#endif

#ifdef PVM_SI_PREFIX
PVM_SI_PREFIX struct memory2 alloc_memory(num size);
PVM_SI_PREFIX struct memory* alloc_memory2(void *mem, num size);
PVM_SI_PREFIX struct memory* realloc_memory(num adr, num newsize);
PVM_SI_PREFIX void free_memory(num adr);
#endif

#endif /* SRC_PVM_VIRTUAL_MASHINE_H_ */
