/*
 * primitive-runtime.c
 *
 *  Created on: 16.07.2021
 *      Author: Patrick
 */
#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>
#include "de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine.h"

#define LLIS sizeof(int64_t)

#define STATUS_LOWER      0x0000000000000001LL
#define STATUS_GREATHER   0x0000000000000002LL

jfieldID values = NULL;

typedef struct {
	int64_t sr[4];
	int64_t *sp;
	int64_t *ip;
	int64_t status;
	int64_t *ints[5];
} pvm;

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
	CMD_JMP = 0x10,
	CMD_JMPEQ = 0x11,
	CMD_JMPNE = 0x12,
	CMD_JMPGT = 0x13,
	CMD_JMPGE = 0x14,
	CMD_JMPLO = 0x15,
	CMD_JMPLE = 0x16,
	CMD_CALL = 0x17,
	CMD_CALLEQ = 0x18,
	CMD_CALLNE = 0x19,
	CMD_CALLGT = 0x1A,
	CMD_CALLGE = 0x1B,
	CMD_CALLLO = 0x1C,
	CMD_CALLLE = 0x1D,
	CMD_CMP = 0x20,
	CMD_RET = 0x21,
	CMD_INT = 0x22,
	CMD_PUSH = 0x23,
	CMD_POP = 0x24,
	CMD_SET_IP = 0x25,
	CMD_SET_SP = 0x26,
};

enum {
	__BASE = 0x01, __A_NUM = 0x00, __A_SR = 0x02, __NO_B = 0x00, __B_REG = 0x04, __B_NUM = 0x08, __B_SR = 0x0C,

	ART_ANUM = __BASE | __A_NUM | __NO_B,

	ART_ASR = __BASE | __A_SR | __NO_B,

	ART_ANUM_BREG = __BASE | __A_NUM | __B_REG,

	ART_ASR_BREG = __BASE | __A_SR | __B_REG,

	ART_ANUM_BNUM = __BASE | __A_NUM | __B_NUM,

	ART_ASR_BNUM = __BASE | __A_SR | __B_NUM,

	ART_ANUM_BSR = __BASE | __A_NUM | __B_SR,

	ART_ASR_BSR = __BASE | __A_SR | __B_SR,

};

typedef union {
	int64_t cmd;
	uint8_t bytes[8];
} command;

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    create
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_create(JNIEnv *env, jclass cls) {
	puts("[N-LOG]: enter create");
	pvm *p = malloc(sizeof(pvm));
	if (p == NULL) {
		jclass ecls = (*env)->FindClass(env, "java/lang/OutOfMemoryError");
		jint c = (*env)->ThrowNew(env, ecls, "not enugh memory to allocate the struct pvm (three pointers)");
		printf("[N-ERR]: continued after exception, will now return with %d\n", c);
		return c;
	}
	if (p->sr == NULL) {
		free(p);
		jclass ecls = (*env)->FindClass(env, "java/lang/OutOfMemoryError");
		jint c = (*env)->ThrowNew(env, ecls, "not enugh memory to allocate the struct pvm (three pointers) and the four registers [A-D]X (int_64)");
		printf("[N-ERR]: continued after exception, will now return with %d\n", c);
		return c;
	}
	if (!values) {
		values = (*env)->GetFieldID(env, cls, "values", "J");
	}
	puts("[N-LOG]: exit create");
	return (jlong) p;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    openfile
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_openfile(JNIEnv *env, jclass cls, jstring file) {
	puts("[N-LOG]: enter openfile");
	const char *filename = (*env)->GetStringUTFChars(env, file, JNI_FALSE);
	FILE *f = fopen64(filename, "rb");
	if (f == NULL) {
		jclass ecls = (*env)->FindClass(env, "java/io/IOException");
		int fl = strlen(filename);
		char *msg = malloc(23 + fl);
		strcpy(msg, "could not open file '");
		strcpy(msg + 21, filename);
		msg[21 + fl] = '\'';
		msg[22 + fl] = '\0';
		jint c = (*env)->ThrowNew(env, ecls, msg);
		printf("[N-ERR]: continued after exception, will now return with %d\n", c);
		return c;
	}
	puts("[N-LOG]: exit openfile");
	return (jlong) f;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    filelen
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_filelen(JNIEnv *env, jobject caller, jlong pntr) {
	FILE *file = (FILE*) pntr;
	if (!fseek(file, 0, SEEK_END)) {
		jclass ecls = (*env)->FindClass(env, "java/io/IOException");
		char *msg = malloc(16 + 45);
		strcpy(msg, "error on seeking the end of the file errno=0x");
		itoa(errno, msg + 45, 16);
		jint c = (*env)->ThrowNew(env, ecls, msg);
		printf("[N-ERR]: continued after exception, will now return with %d\n", c);
		return c;
	}
	int64_t fpos;
	if (!fgetpos64(file, &fpos)) {
		jclass ecls = (*env)->FindClass(env, "java/io/IOException");
		char *msg = malloc(16 + 45);
		strcpy(msg, "error on seeking the pos of the file errno=0x");
		itoa(errno, msg + 45, 16);
		jint c = (*env)->ThrowNew(env, ecls, msg);
		printf("[N-ERR]: continued after exception, will now return with %d\n", c);
		return c;
	}
	return fpos;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    filepos
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_filepos(JNIEnv *env, jobject caller, jlong pntr) {
	FILE *file = (FILE*) pntr;
	int64_t fpos;
	if (!fgetpos64(file, &fpos)) {
		jclass ecls = (*env)->FindClass(env, "java/io/IOException");
		char *msg = malloc(16 + 44);
		strcpy(msg, "error on geting the pos of the file errno=0x");
		itoa(errno, msg + 44, 16);
		jint c = (*env)->ThrowNew(env, ecls, msg);
		printf("[N-ERR]: continued after exception, will now return with %d\n", c);
		return c;
	}
	return fpos;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setfilepos
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setfilepos(JNIEnv *env, jobject caller, jlong pntr, jlong pos) {
	FILE *file = (FILE*) pntr;
	int64_t *fpos;
	if (!fgetpos64(file, fpos)) {
		jclass ecls = (*env)->FindClass(env, "java/io/IOException");
		char *msg = malloc(16 + 44);
		strcpy(msg, "error on geting the pos of the file errno=0x");
		itoa(errno, msg + 44, 16);
		jint c = (*env)->ThrowNew(env, ecls, msg);
		printf("[N-ERR]: continued after exception, will now return ThrowNew returned with %d\n", c);
		return;
	}
	fpos[0] = pos * LLIS;
	if (!fsetpos64(file, fpos)) {
		jclass ecls = (*env)->FindClass(env, "java/io/IOException");
		char *msg = malloc(16 + 44);
		strcpy(msg, "error by seting the pos of the file errno=0x");
		itoa(errno, msg + 44, 16);
		jint c = (*env)->ThrowNew(env, ecls, msg);
		printf("[N-ERR]: continued after exception, will now return ThrowNew returned with %d\n", c);
		return;
	}
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    readfile
 * Signature: (JJJ)V
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_readfile(JNIEnv *env, jobject caller, jlong pntr, jlong len, jlong jdest) {
	FILE *file = (FILE*) pntr;
	int64_t *dest = (int64_t*) jdest;
	return fread(dest, LLIS, len, file);
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    closefile
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_closefile(JNIEnv *env, jobject caller, jlong pntr) {
	if (!fclose((FILE*) pntr)) {
		jclass ecls = (*env)->FindClass(env, "java/io/IOException");
		char *msg = malloc(16 + 45);
		strcpy(msg, "error by closing the pos of the file errno=0x");
		itoa(errno, msg + 45, 16);
		jint c = (*env)->ThrowNew(env, ecls, msg);
		printf("[N-ERR]: continued after exception, will now return ThrowNew returned with %d\n", c);
		return;
	}
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    malloc
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_malloc(JNIEnv *env, jobject caller, jlong len) {
	puts("[N-LOG]: enter method malloc");
	void *ret = malloc(len * LLIS + LLIS - 1);
	int64_t r = (int64_t) ret;
	int64_t mod = r % LLIS;
	printf("[N-LOG]: orig pointer: %d\n", r);
	r = r / LLIS;
	if (mod) {
		r++;
	}
	printf("[N-LOG]: return:       %d\n", r);
	printf("[N-LOG]: mod=%d\n", mod);
	puts("[N-LOG]: exit malloc");
	return r;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    realloc
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_realloc(JNIEnv *env, jobject caller, jlong pntr, jlong len) {
	puts("[N-LOG]: enter method realloc");
	void *ret = realloc((void*) (pntr * LLIS), len * LLIS + LLIS - 1);
	int64_t r = (int64_t) ret;
	int64_t mod = r % LLIS;
	printf("[N-LOG]: old pointer: %d\n", pntr * LLIS);
	printf("[N-LOG]: new pointer: %d\n", r);
	r = r / LLIS;
	if (mod) {
		r++;
	}
	printf("[N-LOG]: return:      %d\n", r);
	printf("[N-LOG]: mod=%d\n", mod);
	puts("[N-LOG]: exit realloc");
	return r;
//	void *ret = realloc((void*) (pntr * I64S), len * I64S + I64S - 1);
//	int64_t r = (int64_t) ret;
//	int64_t mod = r % I64S;
//	r = r / I64S;
//	if (mod) {
//		r++;
//	}
//	return r;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_free(JNIEnv *env, jobject caller, jlong pntr) {
	free((void*) (((long long) pntr) * LLIS));
}

#define unknownCommand exit(-2);/*TODO: link to unknown command interupt so that later the interupt can be overwritten*/
#define twoParamsP1NoConst(pointeraction, sractiont) switch(cmd.bytes[1]){/*TODO*/}
/*
 * @formatter:off
 * 	switch (cmd.bytes[1]) {
 * 	case ART_ASR: {
 * 		int64_t param = cmd.bytes[7];
 * 		int64_t len = 1;
 * 		sraction
 * 		break;
 * 	}
 * 	case ART_ANUM_BREG: {
 * 		int64_t* param = (int64_t*) (p.ip[1] * LLIS);
 * 		int64_t len = 2;
 * 		pntraction
 * 		break;
 * 	}
 * 	case ART_ASR_BREG: {
 * 		int64_t* param = (int64_t*) (p.sr[cmd.bytes[7]] * LLIS);
 * 		int64_t len = 1;
 * 		pntraction
 * 		break;
 * 	}
 * 	case ART_ANUM_BNUM: {
 * 		int64_t* param = (int64_t*) ((p.ip[1] + p.ip[2]) * LLIS);
 * 		int64_t len = 3;
 * 		pntraction
 * 		break;
 * 	}
 * 	case ART_ASR_BNUM: {
 * 		int64_t* param = *((int64_t*) ((p.sr[cmd.bytes[7]] + p.ip[1]) * LLIS);
 * 		int64_t len = 2;
 * 		pntraction
 * 		break;
 * 	}
 * 	case ART_ANUM_BSR: {
 * 		int64_t* param = *((int64_t*) ((p.ip[1] + p.sr[cmd.bytes[7]]) * LLIS);
 * 		int64_t len = 2;
 * 		pntraction
 * 		break;
 * 	}
 * 	case ART_ASR_BSR: {
 * 		int64_t* param = (int64_t*) ((p.sr[cmd.bytes[7]] + p.sr[cmd.bytes[6]]) * LLIS);
 * 		int64_t len = 2;
 * 		pntraction
 * 		break;
 * 	}
 * 	default:
 * 		unknownCommand
 * 	}
 * @formatter:on
 */
#define oneParamAllowNoConst(pntraction, sraction) switch (cmd.bytes[1]) { case ART_ASR: { int64_t param = cmd.bytes[7]; int64_t len = 1; sraction break; } case ART_ANUM_BREG: { int64_t* param = (int64_t*) (p.ip[1] * LLIS); int64_t len = 2; pntraction break; } case ART_ASR_BREG: { int64_t* param = (int64_t*) (p.sr[cmd.bytes[7]] * LLIS); int64_t len = 1; pntraction break; } case ART_ANUM_BNUM: { int64_t* param = (int64_t*) ((p.ip[1] + p.ip[2]) * LLIS); int64_t len = 3; pntraction break; } case ART_ASR_BNUM: { int64_t* param = (int64_t*) ((p.sr[cmd.bytes[7]] + p.ip[1]) * LLIS); int64_t len = 2; pntraction break; } case ART_ANUM_BSR: { int64_t* param = (int64_t*) ((p.ip[1] + p.sr[cmd.bytes[7]]) * LLIS); int64_t len = 2; pntraction break; } case ART_ASR_BSR: { int64_t* param = (int64_t*) ((p.sr[cmd.bytes[7]] + p.sr[cmd.bytes[6]]) * LLIS); int64_t len = 2; pntraction break; } default: unknownCommand }
/*
 * @formatter:off
 * 	switch (cmd.bytes[1]) {
 * 	case ART_ANUM: {
 * 		int64_t param = p.ip[1];
 * 		int64_t len = 2;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ASR: {
 * 		int64_t param = p.sr[cmd.bytes[7]];
 * 		int64_t len = 1;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ANUM_BREG: {
 * 		int64_t param = *((int64_t*) (p.ip[1] * LLIS));
 * 		int64_t len = 2;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ASR_BREG: {
 * 		int64_t param = *((int64_t*) (p.sr[cmd.bytes[7]] * LLIS));
 * 		int64_t len = 1;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ANUM_BNUM: {
 * 		int64_t param = *((int64_t*) ((p.ip[1] + p.ip[2]) * LLIS));
 * 		int64_t len = 3;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ASR_BNUM: {
 * 		int64_t param = *((int64_t*) ((p.sr[cmd.bytes[7]] + p.ip[1]) * LLIS));
 * 		int64_t len = 2;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ANUM_BSR: {
 * 		int64_t param = *((int64_t*) ((p.ip[1] + p.sr[cmd.bytes[7]]) * LLIS));
 * 		int64_t len = 2;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ASR_BSR: {
 * 		int64_t param = *((int64_t*) ((p.sr[cmd.bytes[7]] + p.sr[cmd.bytes[6]]) * LLIS));
 * 		int64_t len = 2;
 * 		action
 * 		break;
 * 	}
 * 	default:
 * 		unknownCommand
 * 	}
 * @formatter:on
 */
#define oneParamAllowConst(action) switch (cmd.bytes[1]) { case ART_ANUM: { int64_t param = p.ip[1]; int64_t len = 2; action break; } case ART_ASR: { int64_t param = p.sr[cmd.bytes[7]]; int64_t len = 1; action break; } case ART_ANUM_BREG: { int64_t param = *( (int64_t*) (p.ip[1] * LLIS)); int64_t len = 2; action break; } case ART_ASR_BREG: { int64_t param = *( (int64_t*) (p.sr[cmd.bytes[7]] * LLIS) ); int64_t len = 1; action break; } case ART_ANUM_BNUM: { int64_t param = *( (int64_t*) ((p.ip[1] + p.ip[2]) * LLIS)); int64_t len = 3; action break; } case ART_ASR_BNUM: { int64_t param = *( (int64_t*) ((p.sr[cmd.bytes[7]] + p.ip[1]) * LLIS)); int64_t len = 2; action break; } case ART_ANUM_BSR: { int64_t param = *( (int64_t*) ((p.ip[1] + p.sr[cmd.bytes[7]]) * LLIS)); int64_t len = 2; action break; } case ART_ASR_BSR: { int64_t param = *( (int64_t*) ((p.sr[cmd.bytes[7]] + p.sr[cmd.bytes[6]]) * LLIS)); int64_t len = 2; action break; } default: unknownCommand }

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    execute
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_execute(JNIEnv *env, jobject caller) {
	pvm p = *((pvm*) (*env)->GetLongField(env, caller, values));
	while (1) {
		command cmd;
		cmd.cmd = *p.ip;
		switch (cmd.bytes[0]) {
		case CMD_MOV: /*TODO*/
			break;
		case CMD_ADD: /*TODO*/
			break;
		case CMD_SUB: /*TODO*/
			break;
		case CMD_MUL: /*TODO*/
			break;
		case CMD_DIV: /*TODO*/
			break;
		case CMD_AND: /*TODO*/
			break;
		case CMD_OR: /*TODO*/
			break;
		case CMD_XOR: /*TODO*/
			break;
		case CMD_NOT:
			oneParamAllowNoConst(*param = ~(*param);p.ip += len;, p.sr[param] = ~p.sr[param]; p.ip += len;)
			break;
		case CMD_NEG:
			oneParamAllowNoConst(*param = -(*param);p.ip += len;, p.sr[param] = -p.sr[param]; p.ip += len;)
			break;
		case CMD_JMP:
			p.ip += p.ip[1];
			break;
		case CMD_JMPEQ:
			if (!(p.status & (STATUS_GREATHER | STATUS_LOWER))) {
				p.ip += p.ip[1];
			} else {
				p.ip += 2;
			}
			break;
		case CMD_JMPNE:
			if (p.status & (STATUS_GREATHER | STATUS_LOWER)) {
				p.ip += p.ip[1];
			} else {
				p.ip += 2;
			}
			break;
		case CMD_JMPGT:
			if (p.status & STATUS_GREATHER) {
				p.ip += p.ip[1];
			} else {
				p.ip += 2;
			}
			break;
		case CMD_JMPGE:
			if (!(p.status & STATUS_LOWER)) {
				p.ip += p.ip[1];
			} else {
				p.ip += 2;
			}
			break;
		case CMD_JMPLO:
			if (p.status & STATUS_LOWER) {
				p.ip += p.ip[1];
			} else {
				p.ip += 2;
			}
			break;
		case CMD_JMPLE:
			if (!(p.status & STATUS_GREATHER)) {
				p.ip += p.ip[1];
			} else {
				p.ip += 2;
			}
			break;
		case CMD_CALL:
			p.sp++;
			*p.sp = ((int64_t) p.ip) / LLIS;
			p.ip += p.ip[1];
			break;
		case CMD_CALLEQ:
			if (!(p.status & (STATUS_GREATHER | STATUS_LOWER))) {
				p.sp++;
				*p.sp = ((int64_t) p.ip) / LLIS;
				p.ip += p.ip[1];
			} else {
				p.ip += 2;
			}
			break;
		case CMD_CALLNE:
			if (p.status & (STATUS_GREATHER | STATUS_LOWER)) {
				p.sp++;
				*p.sp = ((int64_t) p.ip) / LLIS;
				p.ip += p.ip[1];
			} else {
				p.ip += 2;
			}
			break;
		case CMD_CALLGT:
			if (p.status & STATUS_GREATHER) {
				p.sp++;
				*p.sp = ((int64_t) p.ip) / LLIS;
				p.ip += p.ip[1];
			} else {
				p.ip += 2;
			}
			break;
		case CMD_CALLGE:
			if (!(p.status & STATUS_LOWER)) {
				p.sp++;
				*p.sp = ((int64_t) p.ip) / LLIS;
				p.ip += p.ip[1];
			} else {
				p.ip += 2;
			}
			break;
		case CMD_CALLLO:
			if (p.status & STATUS_LOWER) {
				p.sp++;
				*p.sp = ((int64_t) p.ip) / LLIS;
				p.ip += p.ip[1];
			} else {
				p.ip += 2;
			}
			break;
		case CMD_CALLLE:
			if (!(p.status & STATUS_GREATHER)) {
				p.sp++;
				*p.sp = ((int64_t) p.ip) / LLIS;
				p.ip += p.ip[1];
			} else {
				p.ip += 2;
			}
			break;
		case CMD_CMP:
			/*TODO*/
			break;
		case CMD_RET:
			p.ip = (int64_t*) *p.sp;
			p.sp--;
			break;
		case CMD_INT:
			oneParamAllowConst(
					switch(param) { case 0: switch(p.sr[0]) { case 1: { int64_t* pntr = malloc((p.sr[1] * LLIS) + LLIS - 1); if (pntr) { int64_t mem = (int64_t) pntr; int64_t mod = mem / LLIS; mem = mem % LLIS; if (mod) { mem += mod - LLIS; } p.sr[1] = mem; } else { p.sr[1] = -1; } break; } case 2: { int64_t* pntr = realloc((int64_t*) (p.sr[1] * LLIS), (p.sr[2] * LLIS) + LLIS - 1); if (pntr){ int64_t mem = (int64_t) pntr; int64_t mod = mem / LLIS; mem = mem % LLIS; if (mod) { mem += mod - LLIS; } p.sr[1] = mem; } else { p.sr[1] = -1; } break; } case 3: free((int64_t*) (p.sr[1] * LLIS)); break; default: unknownCommand } case 1: switch(p.sr[0]){ case 1: exit(p.sr[1]); case 2: //unknown command
					exit(-2); default: unknownCommand } default: unknownCommand } p.ip += len;)
			break;
		case CMD_PUSH:
			oneParamAllowConst(p.sp ++; *p.sp = param; p.ip += len;)
			break;
		case CMD_POP:
			oneParamAllowNoConst(*param = *p.sp; p.ip += len;, p.sr[param] = *p.sp; p.ip += len;)
			break;
		case CMD_SET_IP:
			oneParamAllowConst(p.ip = (int64_t*) (param * LLIS);)
			break;
		case CMD_SET_SP:
			oneParamAllowConst(p.sp = (int64_t*) (param * LLIS); p.ip += len;)
			break;
		default:
			unknownCommand
			break;
		}
	}
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setInstructionPointer
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setInstructionPointer(JNIEnv *env, jobject caller, jlong ip) {
	pvm *p = (pvm*) (*env)->GetLongField(env, caller, values);
	p->ip = (int64_t*) (ip * LLIS);
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setStackPointer
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setStackPointer(JNIEnv *env, jobject caller, jlong sp) {
	pvm *p = (pvm*) (*env)->GetLongField(env, caller, values);
	p->sp = (int64_t*) (sp * LLIS);
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    push
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_push(JNIEnv *env, jobject caller, jlong value) {
	pvm p = *(pvm*) (*env)->GetLongField(env, caller, values);
	p.sp++;
	p.sp[0] = value;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    pop
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_pop(JNIEnv *env, jobject caller) {
	pvm p = *(pvm*) (*env)->GetLongField(env, caller, values);
	int64_t val = *p.sp;
	p.sp--;
	return val;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    get
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_get(JNIEnv *env, jobject caller, jlong pntr) {
	return *(int64_t*) pntr;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    set
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_set(JNIEnv *env, jobject caller, jlong pntr, jlong value) {
	int64_t *p = (int64_t*) pntr;
	p[0] = value;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    finalize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_finalize(JNIEnv *env, jobject caller) {
	pvm *p = (pvm*) (*env)->GetLongField(env, caller, values);
	free(p->sr);
	free(p);
}
