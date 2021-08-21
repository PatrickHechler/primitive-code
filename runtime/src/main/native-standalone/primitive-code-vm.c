#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <errno.h>

#define LLIS sizeof(int64_t)

#define STATUS_LOWER      0x0000000000000001LL
#define STATUS_GREATHER   0x0000000000000002LL
#define STATUS_CARRY      0x0000000000000004LL

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

struct pvm {
	int64_t regs[4];
	int64_t *ip;
	int64_t *sp;
	int64_t status;
	int64_t *ints;
};

void help(FILE *stream) {
	fprintf(stream, "<--help>\n");
	fprintf(stream, "\tto print this message on the standard out stream\n");
	fprintf(stream, "<-stack> [NUMBER]\n");
	fprintf(stream, "\tto init the stack with a maximum size of NUMBER\n");
	fprintf(stream, "\tnote that the stack will still be empty\n");
	fprintf(stream, "<-bin> [FILE]\n");
	fprintf(stream, "\tto set the binary FILE for of the primitive-vm to execute\n");
}

#define ARG_START 1 /*the first parameter is the file itself (primitive-code-vm.exe for example)*/

int main(int argc, char **argv) {
	struct pvm *pvm = malloc(sizeof(sizeof pvm));
	struct pvm p = pvm[0];
	p.ip = NULL;
	p.ints = NULL;
	int helped = 0;
	{
		if (argc <= ARG_START) {
			fprintf(stderr, "I need args! argcnt=%d\n", argc);
			help(stderr);
			return -3;
		}
		for (int i = ARG_START; i < argc; i++) {
			char *arg = argv[i];
			if (arg[0] != '-') {
				fprintf(stderr, "this argument is unknown: '%s' (arg[%d])\n", arg, i);
				fprintf(stderr, "arg len=%d\n", argc);
				help(stderr);
				return -3;
			}
			if (arg[1] == '-') {
				switch (arg[2]) {
				case 'h':
					if (strcmp(arg + 3, "elp") != 0) {
						fprintf(stderr, "this argument is unknown: '%s' (arg[%d])\n", arg, i);
						help(stderr);
						return -3;
					}
					help(stdout);
					helped = 1;
					break;
				default:
					fprintf(stderr, "this argument is unknown: '%s' (arg[%d])\n", arg, i);
					help(stderr);
					return -3;
				}
			} else {
				switch (arg[1]) {
				case 's': {
					if (strcmp(arg + 2, "ack") != 0) {
						fprintf(stderr, "this argument is unknown: '%s' (arg[%d])\n", arg, i);
						help(stderr);
						return -3;
					}
					if (argc <= i) {
						fprintf(stderr, "too les arguments on arg <-stack>");
						help(stderr);
						return -3;
					}
					int64_t ss = atoll(argv[++i]);
					p.sp = malloc(ss * LLIS + LLIS - 1);
					if (p.sp == NULL) {
						fprintf(stderr, "could not allocate the needed memory for the stack: (%I64d)\n", ss);
						help(stderr);
						return -3;
					}
					int64_t mod = ((int64_t) p.sp) % 8;
					if (mod) {
						p.sp = (int64_t*) (((int64_t) p.sp) + (8 - mod));
					}
					break;
				}
				case 'b': {
					if (strcmp(arg + 2, "in") != 0) {
						fprintf(stderr, "this argument is unknown: '%s' (arg[%d])\n", arg, i);
						help(stderr);
						return -3;
					}
					if (argc <= i) {
						fprintf(stderr, "too les arguments on arg <-bin>");
						help(stderr);
						return -3;
					}
					FILE *bin = fopen64(argv[++i], "rb");
					fseeko64(bin, 0, SEEK_END);
					uint64_t len;
					if (!fgetpos64(bin, &len)) {
						fprintf(stderr, "failed to read the binary file (could not get the length) <-bin> errno=%d", errno);
						help(stderr);
						return -3;
					}
					p.ip = malloc(len * LLIS + LLIS - 1);
					if (p.ip == NULL) {
						fprintf(stderr, "could not allocate the needed memory to read the binary: (%I64d)\n", len);
						help(stderr);
						return -3;
					}
					int64_t mod = ((int64_t) p.ip) % 8;
					if (mod) {
						p.ip = (int64_t*) (((int64_t) p.ip) + (8 - mod));
					}
					fread(p.ip, len, LLIS, bin);
					uint64_t check;
					if (!fgetpos64(bin, &check)) {
						fprintf(stderr, "failed to read the binary file (could not get the position) <-bin> errno=%d", errno);
						help(stderr);
						return -3;
					}
					if (len != check) {
						fprintf(stderr, "failed to read the binary file (position{&I64u} != length{%I64u}) <-bin> errno=%d", check, len, errno);
						help(stderr);
						return -3;
					}
					break;
				}
				default:
					fprintf(stderr, "this argument is unknown: '%s' (arg[%d])\n", arg, i);
					help(stderr);
					return -3;
				}
			}
		}
	}
	if (!p.ip) {
		if (helped) {
			return 0;
		} else {
			fprintf(stderr, "I need a binary to execute (<-bin>) argcount=%d\n", argc);
			for(int i = ARG_START; i < argc; i ++) {
				fprintf(stderr, "\targ[%d]='%s'\n", i, argv[i]);
			}
			help(stderr);
			return -3;
		}
	}
	fprintf(stderr, "this is not yet implemented!\n");
	return -3;
}
