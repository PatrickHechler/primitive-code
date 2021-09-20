/*
 * pvm.c
 *
 *  Created on: 16.07.2021
 *      Author: Patrick
 */
#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <winsock2.h>
#include <windows.h>
#include <winsock.h>
#pragma comment (lib, "Ws2_32.lib")

#define LLIS sizeof(int64_t)

#define STATUS_LOWER          0x0000000000000001LL
#define STATUS_GREATHER       0x0000000000000002LL
#define STATUS_CARRY          0x0000000000000004LL
#define STATUS_ARITMETHIC_ERR 0x0000000000000008LL

#define DEF_INT_MEMORY 0
#define DEF_INT_ERRORS 1
#define DEF_INT_STREAMS 2
#define DEF_INT_TIME 3

#define DEF_INT_MEMORY_ALLOC 0
#define DEF_INT_MEMORY_REALLOC 1
#define DEF_INT_MEMORY_FREE 2

#define DEF_INT_ERRORS_EXIT 0
#define DEF_INT_ERRORS_UNKNOWN_COMMAND 1

#define DEF_INT_STREAMS_GET_OUT 0
#define DEF_INT_STREAMS_GET_LOG 1
#define DEF_INT_STREAMS_GET_IN 2
#define DEF_INT_STREAMS_NEW_IN 3
#define DEF_INT_STREAMS_NEW_OUT 4
#define DEF_INT_STREAMS_WRITE 5
#define DEF_INT_STREAMS_READ 6
#define DEF_INT_STREAMS_REM 7
#define DEF_INT_STREAMS_MK_DIR 8
#define DEF_INT_STREAMS_REM_DIR 9
#define DEF_INT_STREAMS_CLOSE_STREAM 10
#define DEF_INT_STREAMS_GET_POS 11
#define DEF_INT_STREAMS_SET_POS 12
#define DEF_INT_STREAMS_SET_POS_TO_END 13

#define DEF_INT_TIME_GET 0
#define DEF_INT_TIME_WAIT 1

#define DEF_MAX_VALUE 0x7FFFFFFFFFFFFFFFLL
#define DEF_MIN_VALUE -0x8000000000000000LL

#define OFFSET_STACK_POINTER 4
#define OFFSET_INSTRUCTION_POINTER 5
#define OFFSET_STATUS_REG 6
#define OFFSET_INTERUPT_POINTER 7
#define PVM_SIZE (LLIS * 8)

enum {
	CMD_MOV = 0x01,
	CMD_ADD = 0x02,
	CMD_SUB = 0x03,
	CMD_MUL = 0x04,
	CMD_DIV = 0x05,
	CMD_AND = 0x06,
	CMD_OR = 0x07,
	CMD_XOR = 0x08,
	CMD_NOT = 0x09,
	CMD_NEG = 0x0A,
	CMD_LSH = 0x0B,
	CMD_RLSH = 0x0C,
	CMD_RASH = 0x0D,
	CMD_DEC = 0x0E,
	CMD_INC = 0x0F,
	CMD_JMP = 0x10,
	CMD_JMPEQ = 0x11,
	CMD_JMPNE = 0x12,
	CMD_JMPGT = 0x13,
	CMD_JMPGE = 0x14,
	CMD_JMPLO = 0x15,
	CMD_JMPLE = 0x16,
	CMD_JMPCS = 0x17,
	CMD_JMPCC = 0x18,
	CMD_CALL = 0x20,
	CMD_CMP = 0x21,
	CMD_RET = 0x22,
	CMD_INT = 0x23,
	CMD_PUSH = 0x24,
	CMD_POP = 0x25,
	CMD_SET_IP = 0x26,
	CMD_SET_SP = 0x27,
	CMD_GET_IP = 0x28,
	CMD_GET_SP = 0x29,
	CMD_GET_INTS = 0x2A,
	CMD_SET_INTS = 0x2B,
	CMD_IRET = 0x2C,
	CMD_ADDC = 0x30,
	CMD_SUBC = 0x31,
};

enum {
	ART___BASE = 0x01, ART___A_NUM = 0x00, ART___A_SR = 0x02, ART___NO_B = 0x00, ART___B_REG = 0x04, ART___B_NUM = 0x08, ART___B_SR = 0x0C,

	ART_ANUM = ART___BASE | ART___A_NUM | ART___NO_B,

	ART_ASR = ART___BASE | ART___A_SR | ART___NO_B,

	ART_ANUM_BREG = ART___BASE | ART___A_NUM | ART___B_REG,

	ART_ASR_BREG = ART___BASE | ART___A_SR | ART___B_REG,

	ART_ANUM_BNUM = ART___BASE | ART___A_NUM | ART___B_NUM,

	ART_ASR_BNUM = ART___BASE | ART___A_SR | ART___B_NUM,

	ART_ANUM_BSR = ART___BASE | ART___A_NUM | ART___B_SR,

	ART_ASR_BSR = ART___BASE | ART___A_SR | ART___B_SR,

};

typedef union {
	int64_t cmd;
	uint8_t bytes[8];
} command;

void help(FILE*, int, char**);

DWORD WINAPI threadFunc(void*);

enum state {
	state_end = 0, state_wait = 1, state_run = 2, state_step = 3,
};

volatile int end = 1;
volatile int isended = 0;

volatile enum state mystate = state_run;
volatile enum state mystate0 = state_run;

#define NOTHING_DATA_MAGIC 0
#define READ_DATA_MAGIC 1
#define WRITE_DATA_MAGIC 2

volatile void *mydata = NULL;
volatile int64_t datamagic = NOTHING_DATA_MAGIC;

int main(int argc, char **argv) {
	int64_t *pvm = malloc(PVM_SIZE);
	pvm[OFFSET_INSTRUCTION_POINTER] = -1;
	pvm[OFFSET_INTERUPT_POINTER] = -1;
	pvm[OFFSET_STACK_POINTER] = -1;
	SOCKET sok = 0;


	for (int i = 1; i < argc; i++) { //TODO
		if (argv[i][0] != '-') {
			fprintf(stderr, "unknown argument: '%s' index=%d\n", argv[i], i);
			help(stderr, argc, argv);
			return 1;
		}
		switch (argv[i][1]) {
		case '-':
			switch (argv[i][2]) {
			case 'h':
				if (strcmp("elp", argv[i] + 3)) {
					help(stdout, 0, NULL);
				} else {
					fprintf(stderr, "unknown argument: '%s' index=%d\n", argv[i], i);
					help(stderr, argc, argv);
					return 1;
				}
				break;
			case 'l':
				if (strcmp("isten", argv[i] + 3)) {
					sok = socket(AF_UNIX, SOCK_STREAM, 0);
					end = 0;
				} else {
					fprintf(stderr, "unknown argument: '%s' index=%d\n", argv[i], i);
					help(stderr, argc, argv);
					return 1;
				}
				break;
			case 'w':
				if (strcmp("ait", argv[i] + 3)) {
					mystate = state_wait;
				} else {
					fprintf(stderr, "unknown argument: '%s' index=%d\n", argv[i], i);
					help(stderr, argc, argv);
					return 1;
				}
				break;
			default:
				fprintf(stderr, "unknown argument: '%s' index=%d\n", argv[i], i);
				help(stderr, argc, argv);
				return 1;
			}
			break;
		case 's':
			if (strcmp("tack", argv[i] + 3)) {
				i++;
				if (i >= argc) {
					fprintf(stderr, "not enugh args for: '%s' index=%d\n", argv[i - 1], i - 1);
					help(stderr, argc, argv);
					return 1;
				}
				int64_t s = atoll(argv[i]);
				void *m = malloc(s);
				pvm[OFFSET_STACK_POINTER] = m ? (int64_t) m : -1;
			} else {
				fprintf(stderr, "unknown argument: '%s' index=%d\n", argv[i], i);
				help(stderr, argc, argv);
				return 1;
			}
			break;
		case 'p':
			if (strcmp("mc", argv[i] + 3)) {
				if (i >= argc) {
					fprintf(stderr, "not enugh args for: '%s' index=%d\n", argv[i - 1], i - 1);
					help(stderr, argc, argv);
					return 1;
				}
				FILE* f = fopen64(argv[++i], "rb");
				fseeko64(f, 0, SEEK_END);
				int64_t len = ftello64(f);
				fseeko64(f, 0, SEEK_SET);
				void* programm = malloc(len);
				fread(programm, 1, len, f);
				pvm[OFFSET_INSTRUCTION_POINTER] = (int64_t) programm;
				fclose(f);
				free(f);
			} else {
				fprintf(stderr, "unknown argument: '%s' index=%d\n", argv[i], i);
				help(stderr, argc, argv);
				return 1;
			}
			break;
		default:
			fprintf(stderr, "unknown argument: '%s' index=%d\n", argv[i], i);
			help(stderr, argc, argv);
			return 1;
		}
	}
	HANDLE thread = CreateThread(NULL, 0, threadFunc, &sok, 0, NULL);
	while (mystate == state_run) {
		RUN: ;
		//TODO
	}
	CHECK_MYSTATE:
	switch (mystate) {
	case state_end:
		mystate0 = state_end;
		end = 1;
		while (!isended) {
			Sleep(0);
		}
		break;
	case state_run:
		mystate0 = state_run;
		goto RUN;
	case state_step:
		if (mystate0 == state_step) {
			goto WAIT;
		} else {
			mystate0 = state_step;
			goto RUN;
		}
	case state_wait:
		WAIT:
		mystate0 = state_wait;
		while(mystate == state_wait) {
			Sleep(0);
		}
		goto CHECK_MYSTATE;
	default:
		end = 1;
		mystate0 = state_end;
		fprintf(stderr, "unknown state=%d state0=%d\n", mystate, mystate0);
		closesocket(sok);
		Sleep(1000);
		if (!isended) {
			fprintf(stderr, "socket thread did not end after a second, kill prosecc now\n");
		}
		exit(1);
	}
	return 0;
}

void help(FILE *f, int argc, char **argv) {
	if (argc) {
		fprintf(f, "argc=%d\n", argc);
		for (int i = 0; i < argc; i++) {
			fprintf(f, "arg[%d]='%s'\n", i, argv[i]);
		}
	}
	fputs("--help", f);
	fputs("    to print this", f);
	fputs("--listen [NUMBER]", f);
	fputs("    use the socket [NUMBER] to listen to signals", f);
	fputs("--wait", f);
	fputs("    waits for a start signal", f);
	fputs("-stack [NUMBER]", f);
	fputs("    initializes the stack with a size of [NUMBER]", f);
	fputs("-pmc [FILE] [...]", f);
	fputs("    the [FILE] to be executed with the arguments [...]", f);
}

enum {
	SIGNAL_KILL = 1,
	SIGNAL_HALT = 2,
	SIGNAL_STEP = 3,
	SIGNAL_RUN = 4,
	SIGNAL_STATE = 5,
	SIGNAL_WRITE_SNAPSHOT = 6,
	SIGNAL_READ_SNAPSHOT = 7,
	SIGNAL_MEMORY_READ = 8,
	SIGNAL_MEMORY_WRITE = 9,
	SIGNAL_MEMORY_ALLOCATE = 10,
	SIGNAL_MEMORY_REALLOCATE = 11,
	SIGNAL_MEMORY_FREE = 12,
	SIGNAL_ILLEGAL = 255
};

#define GROW_SIZE 256

DWORD WINAPI threadFunc(void *data) {
	SOCKET origsok = ((SOCKET*) data)[0];
	if (end) {
		closesocket(origsok);
		isended = 1;
		return 0;
	}
	struct sockaddr addr;
	int addrlen = sizeof(struct sockaddr);
	SOCKET sok = accept(origsok, &addr, &addrlen);
	size_t size = GROW_SIZE;
	char *c = malloc(size);
	while (!end) {
		recv(sok, c, 1, 0);
		switch (0xFF & ((int) c[0])) {
		case SIGNAL_KILL: {
			mystate = state_end;
			while (mystate0 != state_end) {
				Sleep(0);
			}
			c[0] = SIGNAL_KILL;
			send(sok, c, 1, 0);
			break;
		}
		case SIGNAL_HALT: {
			mystate = state_wait;
			while (mystate0 != state_wait) {
				Sleep(0);
			}
			c[0] = SIGNAL_HALT;
			send(sok, c, 1, 0);
			break;
		}
		case SIGNAL_STEP: {
			mystate = state_step;
			while (mystate0 != state_step) {
				Sleep(0);
			}
			mystate0 = state_wait;
			c[0] = SIGNAL_STEP;
			send(sok, c, 1, 0);
			break;
		}
		case SIGNAL_RUN: {
			mystate = state_run;
			while (mystate0 != state_run) {
				Sleep(0);
			}
			c[0] = SIGNAL_RUN;
			send(sok, c, 1, 0);
			break;
		}
		case SIGNAL_STATE: {
			switch (mystate0) {
			case state_run:
				c[0] = SIGNAL_RUN;
				break;
			case state_step:
				c[0] = SIGNAL_RUN;
				break;
			case state_wait:
				c[0] = SIGNAL_HALT;
				break;
			default:
				c[0] = SIGNAL_ILLEGAL;
				break;
			}
			send(sok, c, 1, 0);
			break;
		}
		case SIGNAL_WRITE_SNAPSHOT: {
			recv(sok, c, 8 * 8, 0);
			if (mystate0 != state_wait) {
				c[0] = SIGNAL_ILLEGAL;
			} else {
				mydata = c;
				datamagic = WRITE_DATA_MAGIC;
				while (datamagic != NOTHING_DATA_MAGIC) {
					Sleep(0);
				}
				c[0] = SIGNAL_READ_SNAPSHOT;
			}
			send(sok, c, 1, 0);
			break;
		}
		case SIGNAL_READ_SNAPSHOT: {
			if (mystate0 != state_wait) {
				c[0] = SIGNAL_ILLEGAL;
				send(sok, c, 1, 0);
			} else {
				mydata = c;
				datamagic = READ_DATA_MAGIC;
				while (datamagic != NOTHING_DATA_MAGIC) {
					Sleep(0);
				}
				c = data;
				send(sok, c, LLIS * 8, 0);
			}
			break;
		}
		case SIGNAL_MEMORY_READ: {
			recv(sok, c, 8 * 2, 0);
			if (mystate0 != state_wait) {
				c[0] = SIGNAL_ILLEGAL;
				send(sok, c, 1, 0);
			} else {
				int64_t *pntr = (int64_t*) ((int64_t*) c)[0];
				int64_t len = ((int64_t*) c)[1];
				while (len * LLIS > size) {
					size += GROW_SIZE;
					c = realloc(c, size);
				}
				for (int i = 0; i < len; i++) {
					((int64_t*) c)[i] = pntr[i];
				}
				send(sok, c, len * LLIS, 0);
			}
			break;
		}
		case SIGNAL_MEMORY_WRITE: {
			recv(sok, c, LLIS * 2, 0);
			if (mystate0 != state_wait) {
				c[0] = SIGNAL_ILLEGAL;
				send(sok, c, 1, 0);
			} else {
				int64_t *pntr = (int64_t*) ((int64_t*) c)[0];
				int64_t len = ((int64_t*) c)[1];
				while (len * LLIS > size) {
					size += GROW_SIZE;
					c = realloc(c, size);
				}
				recv(sok, c, LLIS * len, 0);
				for (int i = 0; i < len; i++) {
					pntr[i] = ((int64_t*) c)[i];
				}
			}
			break;
		}
		case SIGNAL_MEMORY_ALLOCATE: {
			recv(sok, c, LLIS, 0);
			if (mystate0 != state_wait) {
				c[0] = SIGNAL_ILLEGAL;
				send(sok, c, 1, 0);
			} else {
				int64_t len = ((int64_t*) c)[0];
				((int64_t*) c)[0] = (int64_t) malloc(len);
				send(sok, c, LLIS, 0);
			}
			break;
		}
		case SIGNAL_MEMORY_REALLOCATE: {
			recv(sok, c, LLIS * 2, 0);
			if (mystate0 != state_wait) {
				c[0] = SIGNAL_ILLEGAL;
				send(sok, c, 1, 0);
			} else {
				void* pntr = (void*) ((int64_t*) c)[0];
				int64_t len = ((int64_t*) c)[1];
				pntr = realloc(pntr, len);
				if (pntr == NULL) {
					((int64_t*) c)[0] = -1;
				} else {
					((int64_t*) c)[0] = (int64_t) pntr;
				}
				send(sok, c, LLIS, 0);
			}
			break;
		}
		case SIGNAL_MEMORY_FREE: {
			recv(sok, c, LLIS * 2, 0);
			if (mystate0 != state_wait) {
				c[0] = SIGNAL_ILLEGAL;
				send(sok, c, 1, 0);
			} else {
				int64_t pntr = ((int64_t*) c)[0];
				free((void*) pntr);
				c[0] = SIGNAL_MEMORY_FREE;
				send(sok, c, 1, 0);
			}
			break;
		}
		default: {
			c[0] = SIGNAL_ILLEGAL;
			send(sok, c, 1, 0);
			break;
		}
		}

	}
	free(c);
	isended = 1;
	closesocket(sok);
	closesocket(origsok);
	return 0;
}
