#include "pvm_defs.h"
#include "hashset.h"
#include "pvm_debug.h"
#include "pvm_virtual_mashine.h"

#include <string.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <setjmp.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>

#define MAX_QUEUE 1

static const num zero = 0;
static const num one = 1;
static const num minusone = -1;
static const int debugExecuted = pvm_debug_executed_command;

static void* debbugger_run(int*);

static void* pvm_debug_start0(int*)__attribute__ ((__noreturn__));

static int breakpointEqual(const void*, const void*);

static int breakpointHash(const void*);

extern jmp_buf debug_state;

extern struct pvm pvm;

extern enum pvm_runConditon pvm_rc;

extern int pvm_runState;

static int continueRun = 0;

static int breakpointsEnabled = 1;

static struct hashset breakoints;

static struct timespec debug_wait_time;

enum dmode debugMode = pvm_dmode_noDebug;

pthread_mutex_t debug_mutex = PTHREAD_MUTEX_INITIALIZER;

#define safeWriteError exit(4);
#define safeReadError exit(4);
#define safeReadEOF return NULL;

static inline void safeWrite(int fd, const void* buf, num len) {
	for (num remain = len, zw; remain > 0L; remain -= zw, buf += zw) {
		zw = write(fd, buf, remain);
		if (zw == -1L) {
			safeWriteError
		}
	}
}

static inline int safeRead0(int fd, void* buf, num len) {
	for (num remain = len, zw; remain > 0L; remain -= zw, buf += zw) {
		zw = read(fd, buf, remain);
		if (zw == -1L) {
			safeReadError
		}
		if (zw == 0L) {
			return 0;
		}
	}
	return 1;
}

//#define safeRead(fd, buf, len) \
//	if (!safeRead0(fd, buf, len)) {\
//		safeReadEOF\
//	}

void sleep_2() {
#ifdef DEBUG_LOG_ALL
	printf("[pvm]: sleep time * 2:\n"
			"[pvm]:   secs=%ld\n"
			"[pvm]:   nanosecs=%ld\n", debug_wait_time.tv_sec,
			debug_wait_time.tv_nsec);
	printf("[pvm]: wait_1\n");
	fflush(stdout);
#endif // DEBUG_LOG_ALL
	nanosleep(&debug_wait_time, NULL);
#ifdef DEBUG_LOG_ALL
	printf("[pvm]: wait_2\n");
	fflush(stdout);
#endif // DEBUG_LOG_ALL
	nanosleep(&debug_wait_time, NULL);
#ifdef DEBUG_LOG_ALL
	printf("[pvm]: finished sleeping\n");
#endif // DEBUG_LOG_ALL
}

void pvm_debug_notify() {
#ifdef DEBUG_LOG_ALL
	printf("[pvm]: entered debug-notify\n");
#endif // DEBUG_LOG_ALL
	sleep_2();
	pthread_mutex_lock(&debug_mutex);
	pvm_runState = 0;
#ifdef DEBUG_LOG_ALL
	printf("[pvm]: cleared pvm_runState\n");
	fflush(stdout);
#endif // DEBUG_LOG_ALL
	pthread_mutex_unlock(&debug_mutex);
	sleep_2();
	pthread_mutex_lock(&debug_mutex);
#ifdef DEBUG_LOG_ALL
	printf("[pvm]: state:\n"
			"[pvm]:   debugMode=%d\n"
			"[pvm]:   continueRun=%d\n"
			"[pvm]:   pvm_rc=%d\n"
			"[pvm]:   pvm_runState=%d\n", debugMode, continueRun, pvm_rc,
			pvm_runState);
	fflush(stdout);
#endif // DEBUG_LOG_ALL
	switch (pvm_rc) {
	case PVM_RC_ALWAYS_RUN:
		fputs("\n[ABORT]: abort now\n", stderr);
		fflush(NULL);
		abort();
	case PVM_RC_EXIT:
		exit(0);
	case PVM_RC_JUST_NEXT:
	case PVM_RC_STOP:
		wait_label_WITH_DEBUG_LOCK: while (!continueRun) {
			pthread_mutex_unlock(&debug_mutex);
#ifdef DEBUG_LOG_ALL
			printf("[pvm]: waiting ...\n"
					"[pvm]:   secs=%ld\n"
					"[pvm]:   nanosecs=%ld\n", debug_wait_time.tv_sec,
					debug_wait_time.tv_nsec);
			fflush(stdout);
#endif // DEBUG_LOG_ALL
			nanosleep(&debug_wait_time, NULL);
			pthread_mutex_lock(&debug_mutex);
		}
		continueRun = 0;
		pthread_mutex_unlock(&debug_mutex);
		break;
	case PVM_RC_DEBUG:
		switch (debugMode) {
		default:
		case pvm_dmode_noDebug:
			fputs("\n[ABORT]: abort now\n", stderr);
			fflush(NULL);
			abort();
		case pvm_dmode_justBreakpoints:
			if (breakpointsEnabled) {
				if (hashset_get((struct hashset*) &breakoints,
						(int) (long) pvm.ip, pvm.ip) != NULL) {
					debugMode = pvm_dmode_noDebug;
					goto wait_label_WITH_DEBUG_LOCK;
				}
			}
			break;
		case pvm_dmode_executeUntilErrorOrEndCall: {
			if (breakpointsEnabled) {
				if (hashset_get((struct hashset*) &breakoints,
						(int) (long) pvm.ip, pvm.ip) != NULL) {
					debugMode = pvm_dmode_noDebug;
					goto wait_label_WITH_DEBUG_LOCK;
				}
			}
			union pvm_command_union ic;
			ic.num = *pvm.ip;
			if (ic.cmds[0] == CMD_INT) {
				int off1 = 0, off2 = 0, len = 1;
				num intnum;
				if (getConstantParam(1, &off1, &off2, &len,
						(union pvm_command_union*) &intnum)) {
					debugMode = pvm_dmode_noDebug;
					goto wait_label_WITH_DEBUG_LOCK;
				}
				if (intnum <= DEF_INT_EXIT) {
					debugMode = pvm_dmode_noDebug;
					goto wait_label_WITH_DEBUG_LOCK;
				}
			}
			break;
		}
		case pvm_dmode_executeUntilExit: {
			if (breakpointsEnabled) {
				if (hashset_get((struct hashset*) &breakoints,
						(int) (long) pvm.ip, pvm.ip) != NULL) {
					debugMode = pvm_dmode_noDebug;
					goto wait_label_WITH_DEBUG_LOCK;
				}
			}
			union pvm_command_union ic;
			ic.num = *pvm.ip;
			if (ic.cmds[0] == CMD_INT) {
				int off1 = 0, off2 = 0, len = 1;
				num intnum;
				if (getConstantParam(1, &off1, &off2, &len,
						(union pvm_command_union*) &intnum)) {
					pthread_mutex_unlock(&debug_mutex);
					return;
				}
				if (intnum <= DEF_INT_EXIT) {//only error and exit interrupts can directly lead to an exit of the pvm
					if (pvm.intp == NULL) { //if always default it will exit
						debugMode = pvm_dmode_noDebug;
						goto wait_label_WITH_DEBUG_LOCK;
					}
					if (pvm.intcnt <= intnum) { //if this interrupt is illegal
						//because this interrupt is at most exit and illegal exit is illegal
						if (pvm.intcnt <= DEF_INT_ERRORS_ILLEGAL_INTERRUPT) { //and illegal interrupt is illegal it will exit
							debugMode = pvm_dmode_noDebug;
							goto wait_label_WITH_DEBUG_LOCK;
						}
						if (pvm.intp[DEF_INT_ERRORS_ILLEGAL_INTERRUPT] == -1L) { //orAnd illegal interrupt is default it will exit
							debugMode = pvm_dmode_noDebug;
							goto wait_label_WITH_DEBUG_LOCK;
						}
					}
					if (pvm.intp[intnum] == -1L) { //if this is default
						if (pvm.intp[DEF_INT_EXIT] == -1L) { //and exit is default will lead to exit
							debugMode = pvm_dmode_noDebug;
							goto wait_label_WITH_DEBUG_LOCK;
						}
						if (pvm.intcnt <= DEF_INT_EXIT) { //and exit is illegal
							if (pvm.intcnt <= DEF_INT_ERRORS_ILLEGAL_INTERRUPT) { //and illegal interrupt is illegal it will exit
								debugMode = pvm_dmode_noDebug;
								goto wait_label_WITH_DEBUG_LOCK;
							}
							if (pvm.intp[DEF_INT_ERRORS_ILLEGAL_INTERRUPT] == -1L) { //orAnd illegal interrupt is default it will exit
								debugMode = pvm_dmode_noDebug;
								goto wait_label_WITH_DEBUG_LOCK;
							}
						}
					}
				}
			}
		}
		}
		break;
	default:
		fputs("\n[ABORT]: abort now\n", stderr);
		fflush(NULL);
		abort();
	}
	pthread_mutex_unlock(&debug_mutex);
#ifdef DEBUG_LOG_ALL
	printf("[pvm]: return from debug-notify\n");
	fflush(stdout);
#endif // DEBUG_LOG_ALL
}

#ifdef DEBUG_LOG
void log_end() {
	fputs("[EXIT]: at exit\n", stderr);
	fflush(NULL);
}
#endif //DEBUG_LOG

//https://www.vs.inf.ethz.ch/edu/WS0405/VS/Vorl.VertSys04_05-5.pdf
void pvm_debug_start(int port) {
#ifdef DEBUG_LOG
	atexit(log_end);
	printf("[pvm]: debug start (port=%d)\n", port);
	fflush(stdout);
#endif // DEBUG_LOG
	debug_wait_time.tv_sec = 0;
//	debug_wait_time.tv_nsec = 999999999;
	debug_wait_time.tv_nsec = 10000000;
	setsid();
	breakoints.entries = NULL;
	breakoints.entrycount = 0;
	breakoints.setsize = 0;
	breakoints.equalizer = breakpointEqual;
	breakoints.hashmaker = breakpointHash;
	pthread_t th;
	int *portPNTR = malloc(sizeof(int));
	*portPNTR = port;
#ifdef DEBUG_LOG
	printf("[pvm]: call debug start0 (%p->port=%d)\n", portPNTR, port);
	fflush(stdout);
#endif // DEBUG_LOG
	pthread_create(&th, NULL, (void* (*)(void*)) pvm_debug_start0, portPNTR);
}

static void* pvm_debug_start0(int *portPNTR) {
	int port = *portPNTR;
#ifdef DEBUG_LOG
	printf("[d-start]: debug start0 (%p->port=%d)\n", portPNTR, port);
	fflush(stdout);
#endif // DEBUG_LOG
	free(portPNTR);
	int sok = socket(AF_INET, SOCK_STREAM, 0);
	struct sockaddr_in sokadr;
	sokadr.sin_family = AF_INET;
	sokadr.sin_addr.s_addr = INADDR_ANY;
	sokadr.sin_port = htons((uint16_t)port);
	int ok = bind(sok, (const struct sockaddr*) &sokadr,
			sizeof(struct sockaddr_in));
	if (ok != 0) {
		fputs("\n[ABORT]: abort now\n", stderr);
		fflush(NULL);
		abort();
	}
	ok = listen(sok, MAX_QUEUE);
	if (ok != 0) {
		fputs("\n[ABORT]: abort now\n", stderr);
		fflush(NULL);
		abort();
	}
	while (1) {
#ifdef DEBUG_LOG
		printf("[d-start]: debug accept (sok=%d, port=%d)\n", sok, port);
		fflush(stdout);
#endif // DEBUG_LOG
		int newsok = accept(sok, 0, 0); //https://stackoverflow.com/questions/66338549/wsl2-network-unreachable/66340554#66340554?newreg=8080ce8f323b46bea1ca1fdd73cbb112
#ifdef DEBUG_LOG
		printf("[d-start]: accepted: %d\n", newsok);
		fflush(stdout);
#endif // DEBUG_LOG
		pthread_t th;
		int *sPNTR = malloc(sizeof(int));
		*sPNTR = newsok;
		pthread_create(&th, NULL, (void* (*)(void*)) debbugger_run, sPNTR);
	}
}

static void* debbugger_run(int *_sok) {
#ifdef DEBUG_LOG
	static int id = 0;
	const int thisID = id++;
	printf("[d-run-%d]: debug run: _sok=%p\n", thisID, _sok);
	fflush(stdout);
#endif // DEBUG_LOG
	int sok = *_sok;
#ifdef DEBUG_LOG
	printf("[d-run-%d]: debug run: sok=%d\n", thisID, sok);
	fflush(stdout);
#endif // DEBUG_LOG
	free(_sok);
	union pvm_command_union buffer[2];
	while (1) {
		safeRead(sok, &buffer, 1)
		pthread_mutex_lock(&debug_mutex);
		switch (buffer[0].cmds[0]) {
		/*
		 * exit the program
		 */
		case pvm_debug_exit: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: exit\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG
			pvm_rc = PVM_RC_EXIT;
			continueRun = 1;
			pthread_mutex_unlock(&debug_mutex);
#ifndef DEBUG_LOG
			const int thisID = -1;
#endif // DEBUG_LOG
			sleep(5);
			fprintf(stderr,
					"[d-run-%d]: needs over 5 secs for quiting, I will exit in 5 seconds if it is still not finished\n",
					thisID);
			fflush(stderr);
			sleep(5);
			fprintf(stderr,
					"[d-run-%d]: needs over 10 secs for quiting, I will exit now\n",
					thisID);
			fflush(NULL);
			exit(1);
		}
			/*
			 * pause the program
			 */
		case pvm_debug_pause: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: pause\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG

			pvm_rc = PVM_RC_STOP;
			while (pvm_runState) {
				pthread_mutex_unlock(&debug_mutex);
#ifdef DEBUG_LOG_ALL
				printf("[d-run-%d]: wait:\n"
						"[d-run-%d]:   secs=%ld"
						"[d-run-%d]:   nanosecs=%ld", thisID, thisID,
						debug_wait_time.tv_sec, thisID,
						debug_wait_time.tv_nsec);
				fflush(stdout);
#endif // DEBUG_LOG_ALL
				nanosleep(&debug_wait_time, NULL);
				pthread_mutex_lock(&debug_mutex);
			}
			pthread_mutex_unlock(&debug_mutex);
			safeWrite(sok, &debugExecuted, 1);
			break;
		}
			/*
			 * run the program
			 * with the debug setting
			 */
		case pvm_debug_run: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: run\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG
			pvm_rc = PVM_RC_DEBUG;
			continueRun = 1;
			debugMode = pvm_dmode_justBreakpoints;
			while (!pvm_runState) {
				pthread_mutex_unlock(&debug_mutex);
				nanosleep(&debug_wait_time, NULL);
				pthread_mutex_lock(&debug_mutex);
			}
			while (pvm_runState) {
				pthread_mutex_unlock(&debug_mutex);
				nanosleep(&debug_wait_time, NULL);
				pthread_mutex_lock(&debug_mutex);
			}
			pthread_mutex_unlock(&debug_mutex);
			safeWrite(sok, &debugExecuted, 1);
			break;
		}
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
		case pvm_debug_get_snapshot: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: get_snapshot\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG
			void* zwbuf = malloc(sizeof(struct pvm));
			if (zwbuf == NULL) {
				zwbuf = &pvm;
			} else {
				memcpy(zwbuf, &pvm, sizeof(struct pvm));
			}
			safeWrite(sok, &pvm, sizeof(struct pvm));
			pthread_mutex_unlock(&debug_mutex);
			if (zwbuf != &pvm) {
				free(zwbuf);
			}
			break;
		}
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
			 * it is not save to execute this operation while the pvm is executing
			 */
		case pvm_debug_set_snapshot: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: set_snapshot\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG

			safeRead(sok, &pvm, sizeof(struct pvm));
			pthread_mutex_unlock(&debug_mutex);
			safeWrite(sok, &debugExecuted, 1);
			break;
		}
			/*
			 * read some parts of the memory
			 * the attached debugger sends:
			 *  64-bit: the address
			 *  64-bit: the length
			 * the pvm responses with:
			 *  8bit:
			 *    1, and than the array
			 *    0: operation failed
			 */
		case pvm_debug_get_memory: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: get_memory\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG
			safeRead(sok, &buffer, sizeof(num) * 2);
			void *zwbuf = malloc(buffer[1].num);
#ifdef DEBUG_LOG
			printf("[d-run%d]: read:\n"
					"[d-run%d]:  fromPNTR=%p=%ld\n"
					"[d-run%d]:  len=%ld\n"
					"[d-run%d]: allocated memory:\n"
					"[d-run%d]:  bufPNTR=%p=%ld\n"
					"[d-run%d]:  len=%ld\n", thisID, thisID,
					(const void*) buffer[0].num, buffer[0].num, thisID,
					buffer[1].num, thisID, thisID, zwbuf, (num) zwbuf, thisID,
					buffer[1].num);
			fflush(stdout);
#endif // DEBUG_LOG
			int sj = setjmp(debug_state);
			if (zwbuf == NULL || sj) {
#ifdef DEBUG_LOG
				printf("[d-run%d]: zwbuf=%p == NULL || setjmp(debug_state)=%d\n", thisID, zwbuf, sj);
				fflush(stdout);
#endif // DEBUG_LOG
				pthread_mutex_unlock(&debug_mutex);
				safeWrite(sok, &zero, 1);
				break;
			}
#ifdef DEBUG_LOG
			printf("[d-run-%d]: get memory:\n"
					"[d-run-%d]:   PNTR=%ld:\n"
					"[d-run-%d]:   len=%ld:\n", thisID, thisID, buffer[0].num,
					thisID, buffer[1].num);
			fflush(stdout);
#endif // DEBUG_LOG
			memcpy(zwbuf, (const void*) buffer[0].num, buffer[1].num);
			pthread_mutex_unlock(&debug_mutex);
			safeWrite(sok, &one, 1);
			safeWrite(sok, zwbuf, buffer[1].num);
			free(zwbuf);
			break;
		}
			/*
			 * read some parts of the memory
			 * the attached debugger sends:
			 *  64-bit: the address
			 *  64-bit: the length
			 *  the array
			 * the pvm responses with:
			 * 	1: success
			 * 	0: fail
			 *    on a fail, the operation may have been executed partly
			 */
		case pvm_debug_set_memory: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: set_memory\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG
			safeRead(sok, &buffer, sizeof(num) * 2);
			void *zwbuf = malloc(buffer[1].num);
			void *zwbuf2 = NULL;
			if (zwbuf == NULL || setjmp(debug_state)) {
				if (zwbuf != NULL) {
					zwbuf2 = zwbuf;
					zwbuf = NULL;
					memcpy(zwbuf2, (const void*) buffer[0].num, buffer[1].num);
				} else if (zwbuf2 != NULL) {
					free(zwbuf2);
				}
				pthread_mutex_unlock(&debug_mutex);
				safeWrite(sok, &zero, 1);
				break;
			}
			memcpy(zwbuf, (const void*) buffer[0].num, buffer[1].num);
			memset((void*) buffer[0].num, 0, buffer[1].num);
			free(zwbuf);
			zwbuf = NULL;
			pthread_mutex_unlock(&debug_mutex);
			safeRead(sok, (void*) buffer[0].num, buffer[1].num);
			safeWrite(sok, &one, 1);
			break;
		}
			/*
			 * send all current breakpoints to the attached debugger
			 * the pvm responses with:
			 *  the breakpoint-list
			 * the breakpoint-list:
			 *  the list of 64-bit breakpoints
			 *  a -1/0xFFFFFFFFFFFFFFFF address at the end
			 */
		case pvm_debug_get_breakpoints: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: get_breakpoints\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG
			for (int i = 0; i < breakoints.setsize; i++) {
				if (breakoints.entries[i]) {
					safeWrite(sok, breakoints.entries + i, sizeof(num));
				}
			}
			pthread_mutex_unlock(&debug_mutex);
			safeWrite(sok, &minusone, sizeof(void*));
			break;
		}
			/*
			 * receive new breakpoints to be added to the current breakpoints
			 * doubled breakpoints will be ignored
			 * the breakpoint-list:
			 *  see pvm_debug_get_breakpoints
			 */
		case pvm_debug_add_breakpoints: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: add_breakpoints\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG
			num add;
			while (1) {
				safeRead(sok, &add, sizeof(num));
				if (add == -1L) {
					break;
				}
				hashset_put((struct hashset*) &breakoints, (int) add,
						(void*) add);
			}
			pthread_mutex_unlock(&debug_mutex);
			safeWrite(sok, &debugExecuted, 1);
			break;
		}
			/*
			 * receive a breakpoint-list to be added to the current breakpoints
			 * doubled breakpoints will be ignored
			 * the breakpoint-list:
			 *  see pvm_debug_get_breakpoints
			 */
		case pvm_debug_remove_breakpoints: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: remove_breakpoints\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG

			num rem;
			while (1) {
				safeRead(sok, &rem, sizeof(num));
				if (rem == -1L) {
					break;
				}
				hashset_remove((struct hashset*) &breakoints, (int) rem,
						(void*) rem);
			}
			pthread_mutex_unlock(&debug_mutex);
			safeWrite(sok, &debugExecuted, 1);
			break;
		}
			/*
			 * the pvm first responses with:
			 *   0x01: if breakpoints are enabled
			 *   0x00: if breakpoints are disabled
			 * then the pvm sends the pvm_debug_executed_command
			 */
		case pvm_debug_get_ignore_breakpoints: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: get_ignore_breakpoints\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG

			buffer[0].cmds[0] = breakpointsEnabled;
			pthread_mutex_unlock(&debug_mutex);
			safeWrite(sok, &buffer, 1);
			break;
		}
			/*
			 * if all breakpoints should be ignored or not.
			 * ignoring the breakpoints does not delete the breakpoints: and adding breakpoints will not change this property.
			 * receive:
			 *   0x01: breakpoints should not be ignored
			 *   0x00: if breakpoints should be ignored
			 */
		case pvm_debug_set_ignore_breakpoints: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: set_ignore_breakpoints\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG

			safeRead(sok, &buffer, 1);
			breakpointsEnabled = buffer[0].cmds[0];
			pthread_mutex_unlock(&debug_mutex);
			safeWrite(sok, &debugExecuted, 1);
			break;
		}
		case pvm_debug_execute_next:
			pvm_rc = PVM_RC_JUST_NEXT;
			continueRun = 1;
#ifdef DEBUG_LOG
			printf("[d-run-%d]: set continueRun\n", thisID);
			fflush(stdout);
#endif //DEBUG_LOG
			do {
#ifdef DEBUG_LOG_ALL
				printf("[d-run-%d]: wait ...\n"
						"[d-run-%d]:   secs=%ld\n"
						"[d-run-%d]:   nanosecs=%ld\n", thisID, thisID,
						debug_wait_time.tv_sec, thisID,
						debug_wait_time.tv_nsec);
				fflush(stdout);
#endif //DEBUG_LOG_ALL
				pthread_mutex_unlock(&debug_mutex);
				nanosleep(&debug_wait_time, NULL);
				pthread_mutex_lock(&debug_mutex);
			} while (!pvm_runState);
#ifdef DEBUG_LOG
			printf("[d-run-%d]: pvm_runState is set\n", thisID);
			fflush(stdout);
#endif //DEBUG_LOG
			do {
#ifdef DEBUG_LOG_ALL
				printf("[d-run-%d]: wait:\n"
						"[d-run-%d]:   secs=%ld\n"
						"[d-run-%d]:   nanosecs=%ld\n", thisID, thisID,
						debug_wait_time.tv_sec, thisID,
						debug_wait_time.tv_nsec);
				fflush(stdout);
#endif //DEBUG_LOG_ALL
				pthread_mutex_unlock(&debug_mutex);
				nanosleep(&debug_wait_time, NULL);
				pthread_mutex_lock(&debug_mutex);
			} while (pvm_runState);
#ifdef DEBUG_LOG
			printf("[d-run-%d]: pvm_runState is cleared\n", thisID);
			fflush(stdout);
#endif //DEBUG_LOG
			pthread_mutex_unlock(&debug_mutex);
#ifdef DEBUG_LOG
			printf("[d-run-%d]: write(sok=%d, &(debugExecuted=%d), 1=1);\n",
					thisID, sok, debugExecuted);
			fflush(stdout);
#endif //DEBUG_LOG
			safeWrite(sok, &debugExecuted, 1);
#ifdef DEBUG_LOG
			printf("[d-run-%d]: executed next command\n", thisID);
			fflush(stdout);
#endif //DEBUG_LOG
			break;
		case pvm_debug_allocmem: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: allocmem\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG

			safeRead(sok, &buffer, 8);
			void *pntr = malloc(buffer[0].num);
			pthread_mutex_unlock(&debug_mutex);
			if (pntr == NULL) {
				safeWrite(sok, &minusone, 8);
			} else {
				safeWrite(sok, &pntr, 8);
			}
			break;
		}
		case pvm_debug_reallocmem: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: reallocmem\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG

			safeRead(sok, &buffer, 16);
			void *pntr = realloc((void*) buffer[0].num, buffer[1].num);
			pthread_mutex_unlock(&debug_mutex);
			if (pntr == NULL) {
				safeWrite(sok, &minusone, 8);
			} else {
				safeWrite(sok, &pntr, 8);
			}
			break;
		}
		case pvm_debug_freemem: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: freemem\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG

			safeRead(sok, &buffer, 8);
			free((void*) buffer[0].num);
			pthread_mutex_unlock(&debug_mutex);
			safeWrite(sok, &debugExecuted, 1);
			break;
		}
		case pvm_debug_executeUntilErrorOrEndCall: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: executeUntilErrorOrEndCall\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG
			pvm_rc = PVM_RC_DEBUG;
			debugMode = pvm_dmode_executeUntilErrorOrEndCall;
			continueRun = 1;
			do {
				pthread_mutex_unlock(&debug_mutex);
				nanosleep(&debug_wait_time, NULL);
				pthread_mutex_lock(&debug_mutex);
			} while (debugMode != pvm_dmode_noDebug);
			pthread_mutex_unlock(&debug_mutex);
			safeWrite(sok, &debugExecuted, 1);
			break;
		}
		case pvm_debug_executeUntilExit: {
#ifdef DEBUG_LOG
			printf("[d-run%d]: received: executeUntilExit\n", thisID);
			fflush(stdout);
#endif // DEBUG_LOG
			pvm_rc = PVM_RC_DEBUG;
			debugMode = pvm_dmode_executeUntilExit;
			continueRun = 1;
			do {
				pthread_mutex_unlock(&debug_mutex);
				nanosleep(&debug_wait_time, NULL);
				pthread_mutex_lock(&debug_mutex);
			} while (debugMode != pvm_dmode_noDebug);
			pthread_mutex_unlock(&debug_mutex);
			safeWrite(sok, &debugExecuted, 1);
			break;
		}
		default:
			fputs("\n[ABORT]: abort now\n", stderr);
			fflush(NULL);
			abort();
		}
	}
}

static int breakpointEqual(const void *bpa, const void *bpb) {
	return bpa == bpb;
}

static int breakpointHash(const void *bp) {
	return (int) (long) bp;
}
