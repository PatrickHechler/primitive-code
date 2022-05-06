/*
 * pvm_signal_handler.c
 *
 *  Created on: 12.01.2022
 *      Author: Patrick
 */

#include "pvm_defs.h"
#include "pvm_signal_handler.h"
#include "pvm_virtual_mashine.h"

#include <signal.h>
#include <stdlib.h>
#include <stdio.h>
#include <setjmp.h>
#include <unistd.h>

extern int pvm_runState;

jmp_buf state;

jmp_buf debug_state;

int asumeRunning;

static void intern_pvm_signal_handler(int signum);

void init_pvm_signal_handler() {
	signal(SIGFPE, intern_pvm_signal_handler);
	signal(SIGSEGV, intern_pvm_signal_handler);
}

static void intern_pvm_signal_handler(int signum) {
#ifdef DEBUG_LOG
	fprintf(stderr, "[unknown-Thread]: recieved signal: %d\n", signum);
	fflush(stderr);
#endif // DEBUG_LOG
	if (!asumeRunning) {
		longjmp(debug_state, 1);
	}
	switch(signum) {
	case SIGFPE:
		longjmp(state, DEF_INT_ERRORS_ARITHMETIC_ERROR);
	case SIGSEGV:
		longjmp(state, DEF_INT_ERRORS_ILLEGAL_MEMORY);
	default:
		abort();
	}
}
