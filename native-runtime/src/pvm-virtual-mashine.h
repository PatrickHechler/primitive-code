/*
 * pvm-virtual-mashine.h
 *
 *  Created on: Nov 2, 2022
 *      Author: pat
 */

#ifndef SRC_PVM_VIRTUAL_MASHINE_H_
#define SRC_PVM_VIRTUAL_MASHINE_H_

#include <stdint.h>

#ifdef PVM_DEBUG

void d_wait();

#endif // PVM_DEBUG

void execute() __attribute__ ((__noreturn__));

typedef int64_t num;
typedef uint64_t unum;
typedef double fpnum;
typedef int8_t byte;
typedef int16_t word;
typedef int32_t double_word;

_Static_assert(sizeof(fpnum) == sizeof(unum), "Error!");
_Static_assert(sizeof(void*) == sizeof(unum), "Error!");

#ifndef PVM
extern
#endif
struct pvm {
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
		unum unum;
	} status; // regs[2]
	num errno; // regs[3]
	num xnn[256 - 4]; // reg[4..255]
} pvm;

_Static_assert((sizeof(num) * 256) == sizeof(struct pvm), "Error!");

#endif /* SRC_PVM_VIRTUAL_MASHINE_H_ */
