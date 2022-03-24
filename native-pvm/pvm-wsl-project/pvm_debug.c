/*
 * pvm_debug.c
 *
 *  Created on: 16 Mar 2022
 *      Author: Patrick
 */

#include "pvm_defs.h"
#include "pvm_debug.h"
#include "pvm_virtual_mashine.h"
#include "hashset.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <setjmp.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <pthread.h>

#ifndef NULL
#define NULL 0
#endif

static struct timespec debug_wait_time;

static int waiting = 1;

extern int pvm_runState;

extern int errorState;

static int breakpointsEnabled = 1;
static struct hashset breakoints;

extern jmp_buf debug_state;

pthread_mutex_t debug_mutex = PTHREAD_MUTEX_INITIALIZER;

extern struct pvm pvm;

extern enum pvm_debugState pvm_ds;
extern enum pvm_runConditon pvm_rc;

static int breakpointEqual();
static int breakpointHash();

#define MAX_SERVER_QUEUE 4

static void* pvm_debug_start_server(void *portPNTR);
static void* pvm_debug_run_debugger(void *sokPNTR);

static void safeWrite(int fd, const void *buf, num len);
static int safeRead0(int fd, void *buf, num len);
#ifdef DEBUG_LOG
#define safeReadEOS fputs("[unknown-debugger-thread]: reached end of stream, stop now this debugger (not the pvm)\n", stderr);
#else // DEBUG_LOG
#define safeReadEOS
#endif // DEBUG_LOG
#define safeRead(fd,buf,len) if (safeRead0(fd,buf,len)) { \
	close(fd); \
	pthread_mutex_unlock(&debug_mutex); \
	safeReadEOS \
	return NULL; \
}
#define safeIOError exit(4);

static void changeState(enum pvm_runConditon newState);
static void waitUntil(int waitState);

#define writeSingleNum(value) buffer[0].num = value; safeWrite(sok, buffer, sizeof(num));
#define writeSingleByte(value) buffer[0].cmds[0] = value; safeWrite(sok, buffer, 1);
#define writeFinishedCmd writeSingleByte(pvm_debug_executed_command)

void pvm_debug_start(int port) {
#ifdef DEBUG_LOG
	atexit(log_end);
	printf("[pvm]: debug start (port=%d)\n", port);
	fflush(stdout);
#endif // DEBUG_LOG
	debug_wait_time.tv_sec = 0;
	debug_wait_time.tv_nsec = 10000000; //10 millis
	breakoints.entries = NULL;
	breakoints.entrycount = 0;
	breakoints.setsize = 0;
	breakoints.equalizer = breakpointEqual;
	breakoints.hashmaker = breakpointHash;
	pthread_t th;
	int *portPNTR = malloc(sizeof(int));
	*portPNTR = port;
#ifdef DEBUG_LOG
	printf("[pvm]: start debug start server (%p->port=%d) in new thread\n", portPNTR, port);
	fflush(stdout);
#endif // DEBUG_LOG
	pthread_create(&th, NULL, pvm_debug_start_server, portPNTR);
}

//https://www.vs.inf.ethz.ch/edu/WS0405/VS/Vorl.VertSys04_05-5.pdf
static void* pvm_debug_start_server(void *portPNTR) {
	int port = *(int*) portPNTR;
#ifdef DEBUG_LOG
	printf("[d-start]: debug start0 (%p->port=%d)\n", portPNTR, port);
	fflush(stdout);
#endif // DEBUG_LOG
	free(portPNTR);
	int sok = socket(AF_INET, SOCK_STREAM, 0);
	struct sockaddr_in sokadr;
	sokadr.sin_family = AF_INET;
	sokadr.sin_addr.s_addr = INADDR_ANY;
	sokadr.sin_port = htons((uint16_t) port);
	int ok = bind(sok, (const struct sockaddr*) &sokadr, sizeof(struct sockaddr_in));
	if (ok != 0) {
		fputs("\n[ABORT]: abort now magic=6", stderr);
		fflush(NULL);
		abort();
	}
	ok = listen(sok, MAX_SERVER_QUEUE);
	if (ok != 0) {
		fputs("\n[ABORT]: abort now magic=7", stderr);
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
		int *sokPNTR = malloc(sizeof(int));
		*sokPNTR = newsok;
		pthread_create(&th, NULL, (void* (*)(void*)) pvm_debug_run_debugger, sokPNTR);
	}
}

void* pvm_debug_run_debugger(void *sokPNTR) {//TODO: use [waitUntil, waiting, pvm_runState] correct
#ifdef DEBUG_LOG
	static int id = 0;
	const int thisID = id++;
	printf("[d-run-%d]: debug run: _sok=%p\n", thisID, _sok);
	fflush(stdout);
#endif // DEBUG_LOG
	int sok = *(int*) sokPNTR;
#ifdef DEBUG_LOG
	printf("[d-run-%d]: debug run: sok=%d\n", thisID, sok);
	fflush(stdout);
#endif // DEBUG_LOG
	free(sokPNTR);
	union pvm_command_union buffer[2];
	while (1) {
		safeRead(sok, &buffer, 1);
		pthread_mutex_lock(&debug_mutex);
		switch (buffer[0].cmds[0]) {
		case pvm_debug_exit:
			changeState(PVM_RC_EXIT);
			pthread_mutex_unlock(&debug_mutex);
			sleep(5);
#ifdef DEBUG_LOG
			fprintf(stderr, "[d-run-%d]: did not exit, will now terminate\n", thisID, sok);
			fflush(stderr);
#endif // DEBUG_LOG
			exit(3);
		case pvm_debug_pause:
			changeState(PVM_RC_STOP);
			pthread_mutex_unlock(&debug_mutex);
			waitUntil(1);
			writeFinishedCmd
			pthread_mutex_lock(&debug_mutex);
			break;
		case pvm_debug_run:
			changeState(PVM_RC_DEBUG);
			pthread_mutex_unlock(&debug_mutex);
			waitUntil(0);
			writeFinishedCmd
			pthread_mutex_lock(&debug_mutex);
			break;
		case pvm_debug_get_snapshot:
			safeWrite(sok, &pvm, sizeof(struct pvm));
			break;
		case pvm_debug_set_snapshot:
			safeRead(sok, &pvm, sizeof(struct pvm))
			;
			writeFinishedCmd
			break;
		case pvm_debug_get_memory: {
			safeRead(sok, buffer, sizeof(num) * 2);
			volatile void *buf0 = NULL;
			int val = setjmp(debug_state);
			if (val) {
				if (buf0) {
					free((void*) buf0);
				}
				writeSingleByte(0);
				break;
			}
			void *buf = malloc(buffer[1].num);
			buf0 = buf;
			if (buf == NULL) {
				writeSingleByte(0);
				break;
			}
			memcpy(buf, (const void*) buffer[0].num, buffer[1].num);
			writeSingleByte(1);
			safeWrite(sok, buf, buffer[0].num);
			free(buf);
			break;
		}
		case pvm_debug_set_memory: {
			safeRead(sok, buffer, sizeof(num) * 2);
			void *buf = NULL;
			int val = setjmp(debug_state);
			if (val) {
				break;
			}
			buf = malloc(buffer[1].num);
			if (buf == NULL) {
				writeSingleByte(0);
				break;
			}
			safeRead(sok, buf, buffer[1].num);
			memcpy((void*) buffer[0].num, buf, buffer[1].num);
			writeFinishedCmd
			free(buf);
			break;
		}
		case pvm_debug_get_breakpoints: {
			for (int i = 0; i < breakoints.setsize; i++) {
				if (breakoints.entries[i]) {
					safeWrite(sok, breakoints.entries + i, sizeof(void*));
				}
				writeSingleNum(-1L);
			}
			break;
		}
		case pvm_debug_add_breakpoints: {
			safeRead(sok, buffer, sizeof(num));
			while (buffer[0].num != -1L) {
				hashset_put(&breakoints, (int) buffer[0].num, (void*) buffer[0].num);
				safeRead(sok, buffer, sizeof(num));
			}
			writeFinishedCmd
			break;
		}
		case pvm_debug_remove_breakpoints: {
			safeRead(sok, buffer, sizeof(num));
			while (buffer[0].num != -1L) {
				hashset_remove(&breakoints, (int) buffer[0].num, (void*) buffer[0].num);
				safeRead(sok, buffer, sizeof(num));
			}
			writeFinishedCmd
			break;
		}
		case pvm_debug_get_ignore_breakpoints:
			writeSingleByte(breakpointsEnabled)
			;
			break;
		case pvm_debug_set_ignore_breakpoints:
			safeRead(sok, buffer, 1)
			;
			breakpointsEnabled = buffer[0].cmds[0];
			writeFinishedCmd
			break;
		case pvm_debug_execute_next:
			changeState(PVM_RC_JUST_NEXT);
			waiting = 0;
			pthread_mutex_unlock(&debug_mutex);
			waitUntil(1);
			writeFinishedCmd
			pthread_mutex_lock(&debug_mutex);
			break;
		case pvm_debug_allocmem: {
			safeRead(sok, buffer, sizeof(void*));
			void *buf = malloc(buffer[0].num);
			if (buf) {
				writeSingleNum((num ) buf)
			} else {
				writeSingleNum(-1L)
			}
			break;
		}
		case pvm_debug_reallocmem: {
			safeRead(sok, buffer, sizeof(void*) * 2);
			void *buf = realloc((void*) buffer[0].num, buffer[1].num);
			if (buf) {
				writeSingleNum((num ) buf)
			} else {
				writeSingleNum(-1L)
			}
			break;
		}
		case pvm_debug_freemem: {
			safeRead(sok, buffer, sizeof(void*));
			void *buf = malloc(buffer[0].num);
			free(buf);
			writeFinishedCmd
			break;
		}
		case pvm_debug_executeUntilErrorOrEndCall:
			changeState(PVM_RC_DEBUG);
			pthread_mutex_unlock(&debug_mutex);
			waitUntil(1);
			waitUntil(0);
			writeFinishedCmd
			pthread_mutex_lock(&debug_mutex);
			break;
		case pvm_debug_executeUntilExit:
			changeState(PVM_RC_DEBUG);
			pthread_mutex_unlock(&debug_mutex);
			waitUntil(1);
			waitUntil(0);
			writeFinishedCmd
			pthread_mutex_lock(&debug_mutex);
			break;
		default: {
			int len;
			ioctl(sok, FIONREAD, &len);
			void *buf = malloc(len);
			safeRead(sok, buf, len);
#ifdef DEBUG_LOG
			printf("[d-run-%d]: debug run: sok=%d, illegal message!\n", thisID, sok);
			fflush(stdout);
#endif // DEBUG_LOG
			free(buf);
		}
		}
		pthread_mutex_unlock(&debug_mutex);
	}
}

void pvm_debug_notify() {
	switch (pvm_rc) {
	case PVM_RC_EXIT:
		exit(0);
	case PVM_RC_DEBUG:
		if (breakpointsEnabled) {
			if (hashset_get(&breakoints, (int)(num)pvm.ip, (void*)pvm.ip) != NULL) {
				break;
			}
		}
		if (errorState) {
			errorState = 0;
			break;
		}
	case PVM_RC_JUST_NEXT:
	case PVM_RC_STOP:
		break;
	case PVM_RC_ALWAYS_RUN:
	default:
		fputs("\n[ABORT]: abort now magic=8", stderr);
		fflush(NULL);
		abort();
	}
	waiting = 1;
	pvm_runState = 0;
	pvm_rc = PVM_RC_STOP;
	while (1) {
		nanosleep(&debug_wait_time, NULL);
		pthread_mutex_lock(&debug_mutex);
		if (pvm_rc == PVM_RC_STOP) {
			return;
		}
		pthread_mutex_unlock(&debug_mutex);
	}
}

static inline void waitUntil(int waitState
#ifdef DEBUG_LOG
		, int threadNum
#endif // DEBUG_LOG
		) {
#ifdef DEBUG_LOG
			printf("[d-run-%d]: change state newState=%d\n", threadNum, newState);
			fflush(stdout);
#endif // DEBUG_LOG
	while (1) {
		nanosleep(&debug_wait_time, NULL);
		pthread_mutex_lock(&debug_mutex);
		if (waiting == waitState) {
			return;
		}
		pthread_mutex_unlock(&debug_mutex);
	}
}
static inline void changeState(enum pvm_runConditon newState
#ifdef DEBUG_LOG
		, int threadNum
#endif // DEBUG_LOG
		) {
#ifdef DEBUG_LOG
			printf("[d-run-%d]: change state newState=%d\n", threadNum, newState);
			fflush(stdout);
#endif // DEBUG_LOG
	pvm_rc = newState;

}

static inline void safeWrite(int fd, const void *buf, num len) {
	for (num remain = len, zw; remain > 0L; remain -= zw, buf += zw) {
		zw = write(fd, buf, remain);
		if (zw == -1L) {
			safeIOError
		}
	}
}

static inline int safeRead0(int fd, void *buf, num len) {
	for (num remain = len, zw; remain > 0L; remain -= zw, buf += zw) {
		zw = read(fd, buf, remain);
		if (zw == -1L) {
			safeIOError
		}
		if (zw == 0L) {
			return 1;
		}
	}
	return 0;
}

static int breakpointEqual(void *breakA, void *breakB) {
	return breakA == breakB;
}

static int breakpointHash(void *breakA) {
	return (int) (long) breakA;
}
