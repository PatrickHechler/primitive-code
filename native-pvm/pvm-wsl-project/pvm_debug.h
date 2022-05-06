/*
 * pvm_debug.h
 *
 *  Created on: 14.01.2022
 *      Author: Patrick
 */

#ifndef PVM_DEBUG_H_
#define PVM_DEBUG_H_

void pvm_debug_start(int port);

void pvm_debug_notify();

//void pvm_debug_end();

enum debug {
	/*
	 * exit the program
	 * this will not respond, instead the process will terminate with the exit-code 0
	 */
	pvm_debug_exit = 1,
	/*
	 * pause the program
	 */
	pvm_debug_pause,
	/*
	 * run the program
	 * with the current settings
	 */
	pvm_debug_run,
	/*
	 * make a snapshot and send it to the attached debugger
	 * the pvm responses with:
	 *  the snapshot
	 * snapshot includes:
	 *  64-bit: the AX register
	 *  64-bit: the BX register
	 *  64-bit: the CX register
	 *  64-bit: the DX register
	 *  64-bit: the stack pointer
	 *  64-bit: the instruction pointer
	 *  64-bit: the status register
	 *  64-bit: the interrupt count register
	 *  64-bit: the interrupt pointer
	 */
	pvm_debug_get_snapshot,
	/*
	 * receive a snapshot from the attached debugger and overwrite the current state with it
	 * snapshot includes:
	 *  64-bit: the AX register
	 *  64-bit: the BX register
	 *  64-bit: the CX register
	 *  64-bit: the DX register
	 *  64-bit: the stack pointer
	 *  64-bit: the instruction pointer
	 *  64-bit: the status register
	 *  64-bit: the interrupt count register
	 *  64-bit: the interrupt pointer
	 */
	pvm_debug_set_snapshot,
	/*
	 * read some parts of the memory
	 * the attached debugger sends:
	 *  64-bit: the address
	 *  64-bit: the length
	 * the pvm responses with:
	 *  the array
	 */
	pvm_debug_get_memory,
	/*
	 * read some parts of the memory
	 * the attached debugger sends:
	 *  64-bit: the address
	 *  64-bit: the length
	 *  the array
	 */
	pvm_debug_set_memory,
	/*
	 * send all current breakpoints to the attached debugger
	 * the pvm responses with:
	 *  the breakpoint-list
	 * the breakpoint-list:
	 *  the list of 64-bit breakpoints
	 *  a -1/0xFFFFFFFFFFFFFFFF address at the end
	 */
	pvm_debug_get_breakpoints,
	/*
	 * receive new breakpoints to be added to the current breakpoints
	 * doubled breakpoints will be ignored
	 * the breakpoint-list:
	 *  see pvm_debug_get_breakpoints
	 */
	pvm_debug_add_breakpoints,
	/*
	 * receive a breakpoint-list to be added to the current breakpoints
	 * doubled breakpoints will be ignored
	 * the breakpoint-list:
	 *  see pvm_debug_get_breakpoints
	 */
	pvm_debug_remove_breakpoints,
	/*
	 * the pvm first responses with:
	 *   0x01: if breakpoints are enabled
	 *   0x00: if breakpoints are disabled
	 * then the pvm sends the pvm_debug_executed_command
	 */
	pvm_debug_get_ignore_breakpoints,
	/*
	 * if all breakpoints should be ignored or not.
	 * ignoring the breakpoints does not delete the breakpoints, and adding breakpoints will not change this property.
	 * receive:
	 *   0x01: if breakpoints should not be ignored
	 *   0x00: if breakpoints should be ignored
	 */
	pvm_debug_set_ignore_breakpoints,
	/*
	 * executes the next command
	 */
	pvm_debug_execute_next,
	/*
	 * allocate memory:
	 *   64-bit: size
	 *  the pvm responses with:
	 *   64-bit: the PNTR
	 */
	pvm_debug_allocmem,
	/*
	 * reallocate memory:
	 *   64-bit: PNTR
	 *   64-bit: newsize
	 *  the pvm responses with:
	 *   64-bit: the new PNTR
	 */
	pvm_debug_reallocmem,
	/*
	 * free memory:
	 *   64-bit: PNTR
	 */
	pvm_debug_freemem,
	/*
	 * executes the pvm and stops imidatly before the last command is executed (the exit interrupt), an other error occures or a breakpoint triggers
	 */
	pvm_debug_executeUntilErrorOrEndCall,
	/*
	 * executes the pvm and stops imidatly before the last command is executed (the exit interrupt)
	 */
	pvm_debug_executeUntilExit,
	/*
	 * adds a breakpoint for the default interrupt
	 * recieve:
	 *   64-bit: the number of the interrupt
	 */
	pvm_debug_addDefaultInterruptBreak,
	/*
	 * removes a breakpoint for the default interrupt
	 * recieve:
	 *   64-bit: the number of the interrupt
	 */
	pvm_debug_remDefaultInterruptBreak,
	/*
	 * returns a list of all default interrupt breakpoints
	 * the def-interrupt-breakpoint-list:
	 *  the list of 64-bit interrupt numbers, which are registered as a default-interrupt-breakpoint
	 *  a -1/0xFFFFFFFFFFFFFFFF num at the end
	 */
	pvm_debug_getDefaultInterruptBreaks,
	/*
	 * if not other specified the pvm will response with this message
	 */
	pvm_debug_executed_command = 0x7F,
};

#endif /* PVM_DEBUG_H_ */
