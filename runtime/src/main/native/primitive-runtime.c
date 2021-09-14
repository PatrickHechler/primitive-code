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
#include <sys/stat.h>
#include <sys/time.h>
#include <windows.h>

#include "de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine.h"

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

jfieldID values = NULL;

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

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    create
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_create(JNIEnv *env, jclass cls) {
	puts("[N-LOG]: enter create");
	int64_t *p = malloc(PVM_SIZE);
	if (p == NULL) {
		jclass ecls = (*env)->FindClass(env, "java/lang/OutOfMemoryError");
		jint c = (*env)->ThrowNew(env, ecls, "not enugh memory to allocate the struct pvm (three pointers)");
		printf("[N-ERR]: continued after exception, will now return with %d\n", c);
		return c;
	}
	p[OFFSET_INTERUPT_POINTER] = -1LL;
	if (!values) {
		values = (*env)->GetFieldID(env, cls, "values", "J");
	}
	printf("[N-LOG]: pvm =%I64d\n", p);
	printf("[N-LOG]: AX  =%I64d\n", p[0]);
	printf("[N-LOG]: BX  =%I64d\n", p[1]);
	printf("[N-LOG]: CX  =%I64d\n", p[2]);
	printf("[N-LOG]: DX  =%I64d\n", p[3]);
	printf("[N-LOG]: IP  =%I64d\n", p[OFFSET_INSTRUCTION_POINTER]);
	printf("[N-LOG]: SP  =%I64d\n", p[OFFSET_STACK_POINTER]);
	printf("[N-LOG]: INTP=%I64d\n", p[OFFSET_INTERUPT_POINTER]);
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
	puts("[N-LOG]: enter filelen");
	FILE *file = (FILE*) pntr;
	if (!fseeko64(file, 0, SEEK_END)) {
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
	puts("[N-LOG]: enter filepos");
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
	puts("[N-LOG]: enter setfilepos");
	FILE *file = (FILE*) pntr;
	int64_t *fpos;
	if (!fgetpos64(file, fpos)) {
		jclass ecls = (*env)->FindClass(env, "java/io/IOException");
		char *msg = malloc(16 + 44);
		strcpy(msg, "error on geting the pos of the file errno=0x");
		itoa(errno, msg + 44, 16);
		jint c = (*env)->ThrowNew(env, ecls, msg);
		printf("[N-ERR]: continued after exception, will now return ThrowNew returned with %I64d\n", c);
		return;
	}
	fpos[0] = pos * LLIS;
	if (!fsetpos64(file, fpos)) {
		jclass ecls = (*env)->FindClass(env, "java/io/IOException");
		char *msg = malloc(16 + 44);
		strcpy(msg, "error by seting the pos of the file errno=0x");
		itoa(errno, msg + 44, 16);
		jint c = (*env)->ThrowNew(env, ecls, msg);
		printf("[N-ERR]: continued after exception, will now return ThrowNew returned with %I64d\n", c);
		return;
	}
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    readfile
 * Signature: (JJJ)V
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_readfile(JNIEnv *env, jobject caller, jlong pntr, jlong len, jlong jdest) {
	puts("[N-LOG]: enter readfile");
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
	puts("[N-LOG]: enter closefile");
	if (!fclose((FILE*) pntr)) {
		jclass ecls = (*env)->FindClass(env, "java/io/IOException");
		char *msg = malloc(16 + 45);
		strcpy(msg, "error by closing the pos of the file errno=0x");
		itoa(errno, msg + 45, 16);
		jint c = (*env)->ThrowNew(env, ecls, msg);
		printf("[N-ERR]: continued after exception, will now return ThrowNew returned with %I64d\n", c);
		return;
	}
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    malloc
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_malloc(JNIEnv *env, jobject caller, jlong len) {
	puts("[N-LOG]: enter malloc");
	void *ret = malloc(len * LLIS + LLIS - 1);
	int64_t r = (int64_t) ret;
	int64_t mod = r % LLIS;
	printf("[N-LOG]: orig pointer: %I64d\n", r);
	r = r / LLIS;
	if (mod) {
		r++;
	}
	printf("[N-LOG]: return:       %I64d\n", r);
	printf("[N-LOG]: mod=%I64d\n", mod);
	puts("[N-LOG]: exit malloc");
	return r;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    realloc
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_realloc(JNIEnv *env, jobject caller, jlong pntr, jlong len) {
	puts("[N-LOG]: enter realloc");
	void *ret = realloc((void*) (pntr * LLIS), len * LLIS + LLIS - 1);
	int64_t r = (int64_t) ret;
	int64_t mod = r % LLIS;
	printf("[N-LOG]: old pointer: %I64d\n", pntr * LLIS);
	printf("[N-LOG]: new pointer: %I64d\n", r);
	r = r / LLIS;
	if (mod) {
		r++;
	}
	printf("[N-LOG]: return:      %I64d\n", r);
	printf("[N-LOG]: mod=%I64d\n", mod);
	puts("[N-LOG]: exit realloc");
	return r;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_free(JNIEnv *env, jobject caller, jlong pntr) {
	puts("[N-LOG]: enter free");
	free((void*) (((long long) pntr) * LLIS));
}

//#define unknownCommandReturn return -2;
#define unknownCommand if (p[OFFSET_INTERUPT_POINTER] != -1LL) { int64_t inter = ((int64_t*) p[OFFSET_INTERUPT_POINTER])[DEF_INT_ERRORS]; if (inter != -1LL) { inter *= LLIS; p[0] = DEF_INT_ERRORS_UNKNOWN_COMMAND; p[OFFSET_STACK_POINTER] += LLIS; ((int64_t*)p[OFFSET_STACK_POINTER])[0] = p[OFFSET_INSTRUCTION_POINTER]; p[OFFSET_INSTRUCTION_POINTER] = inter; } else { return -2; } } else { return -2; }

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
 * 		int64_t* param = (int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] * LLIS);
 * 		int64_t len = 2;
 * 		pntraction
 * 		break;
 * 	}
 * 	case ART_ASR_BREG: {
 * 		int64_t* param = (int64_t*) (p[cmd.bytes[7]] * LLIS);
 * 		int64_t len = 1;
 * 		pntraction
 * 		break;
 * 	}
 * 	case ART_ANUM_BNUM: {
 * 		int64_t* param = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[2]) * LLIS);
 * 		int64_t len = 3;
 * 		pntraction
 * 		break;
 * 	}
 * 	case ART_ASR_BNUM: {
 * 		int64_t* param = *((int64_t*) ((p[cmd.bytes[7]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]) * LLIS);
 * 		int64_t len = 2;
 * 		pntraction
 * 		break;
 * 	}
 * 	case ART_ANUM_BSR: {
 * 		int64_t* param = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + p[cmd.bytes[7]]) * LLIS);
 * 		int64_t len = 2;
 * 		pntraction
 * 		break;
 * 	}
 * 	case ART_ASR_BSR: {
 * 		int64_t* param = (int64_t*) ((p[cmd.bytes[7]] + p[cmd.bytes[6]]) * LLIS);
 * 		int64_t len = 2;
 * 		pntraction
 * 		break;
 * 	}
 * 	default:
 * 		unknownCommandReturn
 * 	}
 * @formatter:on
 */
#define oneParamAllowNoConst(pntraction, sraction) switch (cmd.bytes[1]) { case ART_ASR: { int64_t param = cmd.bytes[7]; int64_t len = 1; sraction break; } case ART_ANUM_BREG: { int64_t* param = (int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] * LLIS); int64_t len = 2; pntraction break; } case ART_ASR_BREG: { int64_t* param = (int64_t*) (p[cmd.bytes[7]] * LLIS); int64_t len = 1; pntraction break; } case ART_ANUM_BNUM: { int64_t* param = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[2]) * LLIS); int64_t len = 3; pntraction break; } case ART_ASR_BNUM: { int64_t* param = (int64_t*) ((p[cmd.bytes[7]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]) * LLIS); int64_t len = 2; pntraction break; } case ART_ANUM_BSR: { int64_t* param = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + p[cmd.bytes[7]]) * LLIS); int64_t len = 2; pntraction break; } case ART_ASR_BSR: { int64_t* param = (int64_t*) ((p[cmd.bytes[7]] + p[cmd.bytes[6]]) * LLIS); int64_t len = 2; pntraction break; } default: unknownCommand break; }
/*
 * @formatter:off
 * 	switch (cmd.bytes[1]) {
 * 	case ART_ANUM: {
 * 		int64_t param = ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1];
 * 		int64_t len = 2;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ASR: {
 * 		int64_t param = p[cmd.bytes[7]];
 * 		int64_t len = 1;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ANUM_BREG: {
 * 		int64_t param = *((int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] * LLIS));
 * 		int64_t len = 2;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ASR_BREG: {
 * 		int64_t param = *((int64_t*) (p[cmd.bytes[7]] * LLIS));
 * 		int64_t len = 1;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ANUM_BNUM: {
 * 		int64_t param = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[2]) * LLIS));
 * 		int64_t len = 3;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ASR_BNUM: {
 * 		int64_t param = *((int64_t*) ((p[cmd.bytes[7]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]) * LLIS));
 * 		int64_t len = 2;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ANUM_BSR: {
 * 		int64_t param = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + p[cmd.bytes[7]]) * LLIS));
 * 		int64_t len = 2;
 * 		action
 * 		break;
 * 	}
 * 	case ART_ASR_BSR: {
 * 		int64_t param = *((int64_t*) ((p[cmd.bytes[7]] + p[cmd.bytes[6]]) * LLIS));
 * 		int64_t len = 2;
 * 		action
 * 		break;
 * 	}
 * 	default:
 * 		unknownCommandReturn
 * 	}
 * @formatter:on
 */
#define oneParamAllowConst(action) switch (cmd.bytes[1]) { case ART_ANUM: { int64_t param = ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]; int64_t len = 2; action break; } case ART_ASR: { int64_t param = p[cmd.bytes[7]]; int64_t len = 1; action break; } case ART_ANUM_BREG: { int64_t param = *( (int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] * LLIS)); int64_t len = 2; action break; } case ART_ASR_BREG: { int64_t param = *( (int64_t*) (p[cmd.bytes[7]] * LLIS) ); int64_t len = 1; action break; } case ART_ANUM_BNUM: { int64_t param = *( (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[2]) * LLIS)); int64_t len = 3; action break; } case ART_ASR_BNUM: { int64_t param = *( (int64_t*) ((p[cmd.bytes[7]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]) * LLIS)); int64_t len = 2; action break; } case ART_ANUM_BSR: { int64_t param = *( (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + p[cmd.bytes[7]]) * LLIS)); int64_t len = 2; action break; } case ART_ASR_BSR: { int64_t param = *( (int64_t*) ((p[cmd.bytes[7]] + p[cmd.bytes[6]]) * LLIS)); int64_t len = 2; action break; } default: unknownCommand }
#define getTwoParamsConsts int64_t param1; int64_t len; int64_t _bytesi; switch (cmd.bytes[1]) { case ART_ANUM: param1 = ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]; len = 2; _bytesi = 7; break; case ART_ANUM_BNUM: param1 = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[2]) * LLIS)); len = 3; _bytesi = 7; break; case ART_ANUM_BREG: param1 = *((int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] * LLIS)); len = 2; _bytesi = 7; break; case ART_ANUM_BSR: param1 = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + p[cmd.bytes[7]]) * LLIS)); len = 2; _bytesi = 6; break; case ART_ASR: param1 = p[cmd.bytes[7]]; len = 1; _bytesi = 6; break; case ART_ASR_BNUM: param1 = *((int64_t*) ((p[cmd.bytes[7]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]) * LLIS)); len = 2; _bytesi = 6; break; case ART_ASR_BREG: param1 = *((int64_t*) (p[cmd.bytes[7]] * LLIS)); len = 1; _bytesi = 6; break; case ART_ASR_BSR: param1 = *((int64_t*) ((p[cmd.bytes[7]] + p[cmd.bytes[6]]) * LLIS)); len = 1; _bytesi = 5; break; default: unknownCommand } int64_t param2; switch (cmd.bytes[2]) { case ART_ANUM: param2 = ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len]; len += 1; break; case ART_ANUM_BNUM: param2 = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len + 1]) * LLIS)); len += 2; break; case ART_ANUM_BREG: param2 = *((int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] * LLIS)); len += 1; break; case ART_ANUM_BSR: param2 = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] + p[cmd.bytes[_bytesi]]) * LLIS)); len += 1; break; case ART_ASR: param2 = p[cmd.bytes[_bytesi]]; break; case ART_ASR_BNUM: param2 = *((int64_t*) ((p[cmd.bytes[_bytesi]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len]) * LLIS)); len += 1; break; case ART_ASR_BREG: param2 = *((int64_t*) (p[cmd.bytes[_bytesi]] * LLIS)); break; case ART_ASR_BSR: param2 = *((int64_t*) ((p[cmd.bytes[_bytesi]] + p[cmd.bytes[_bytesi - 1]]) * LLIS)); break; default: unknownCommand } //			getTwoParamsConsts
#define getTwoParamP1NoConstP2Const int64_t* param1; int64_t len; int64_t _bytesi; switch (cmd.bytes[1]) { case ART_ANUM_BNUM: param1 = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[2]) * LLIS); len = 3; _bytesi = 7; break; case ART_ANUM_BREG: param1 = (int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] * LLIS); len = 2; _bytesi = 7; break; case ART_ANUM_BSR: param1 = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + p[cmd.bytes[7]]) * LLIS); len = 2; _bytesi = 6; break; case ART_ASR: param1 = &p[cmd.bytes[7]]; len = 1; _bytesi = 6; break; case ART_ASR_BNUM: param1 = (int64_t*) ((p[cmd.bytes[7]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]) * LLIS); len = 2; _bytesi = 6; break; case ART_ASR_BREG: param1 = (int64_t*) (p[cmd.bytes[7]] * LLIS); len = 1; _bytesi = 6; break; case ART_ASR_BSR: param1 = (int64_t*) ((p[cmd.bytes[7]] + p[cmd.bytes[6]]) * LLIS); len = 1; _bytesi = 5; break; default: unknownCommand } int64_t param2; switch (cmd.bytes[2]) { case ART_ANUM: param2 = ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len]; len += 1; break; case ART_ANUM_BNUM: param2 = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len + 1]) * LLIS)); len += 2; break; case ART_ANUM_BREG: param2 = *((int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] * LLIS)); len += 1; break; case ART_ANUM_BSR: param2 = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] + p[cmd.bytes[_bytesi]]) * LLIS)); len += 1; break; case ART_ASR: param2 = p[cmd.bytes[_bytesi]]; break; case ART_ASR_BNUM: param2 = *((int64_t*) ((p[cmd.bytes[_bytesi]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len]) * LLIS)); len += 1; break; case ART_ASR_BREG: param2 = *((int64_t*) (p[cmd.bytes[_bytesi]] * LLIS)); break; case ART_ASR_BSR: param2 = *((int64_t*) ((p[cmd.bytes[_bytesi]] + p[cmd.bytes[_bytesi - 1]]) * LLIS)); break; default: unknownCommand }
#define getTwoParamNoConsts int64_t* param1; int64_t len; int64_t _bytesi; switch (cmd.bytes[1]) { case ART_ANUM_BNUM: param1 = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[2]) * LLIS); len = 3; _bytesi = 7; break; case ART_ANUM_BREG: param1 = (int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] * LLIS); len = 2; _bytesi = 7; break; case ART_ANUM_BSR: param1 = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + p[cmd.bytes[7]]) * LLIS); len = 2; _bytesi = 6; break; case ART_ASR: param1 = &p[cmd.bytes[7]]; len = 1; _bytesi = 6; break; case ART_ASR_BNUM: param1 = (int64_t*) ((p[cmd.bytes[7]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]) * LLIS); len = 2; _bytesi = 6; break; case ART_ASR_BREG: param1 = (int64_t*) (p[cmd.bytes[7]] * LLIS); len = 1; _bytesi = 6; break; case ART_ASR_BSR: param1 = (int64_t*) ((p[cmd.bytes[7]] + p[cmd.bytes[6]]) * LLIS); len = 1; _bytesi = 5; break; default: unknownCommand } int64_t* param2; switch (cmd.bytes[2]) { case ART_ANUM_BNUM: param2 = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len + 1]) * LLIS); len += 2; break; case ART_ANUM_BREG: param2 = (int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] * LLIS); len += 1; break; case ART_ANUM_BSR: param2 = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] + p[cmd.bytes[_bytesi]]) * LLIS); len += 1; break; case ART_ASR: param2 = &p[cmd.bytes[_bytesi]]; break; case ART_ASR_BNUM: param2 = (int64_t*) ((p[cmd.bytes[_bytesi]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len]) * LLIS); len += 1; break; case ART_ASR_BREG: param2 = (int64_t*) (p[cmd.bytes[_bytesi]] * LLIS); break; case ART_ASR_BSR: param2 = (int64_t*) ((p[cmd.bytes[_bytesi]] + p[cmd.bytes[_bytesi - 1]]) * LLIS); break; default: unknownCommand }

#define stringToChars wstr ++; char* str = malloc(wstr[-1LL] + 1); int zw; printf("[N-LOG]: stringToChars len=%I64d\n", wstr[-1LL]);for (int i = 0; i < wstr[-1LL] * 2; i ++) { printf("[N-LOG]: bytes[%d]=%d\n", i, ((char*) wstr)[i]); } for (int len = 0, z = 0; len < wstr[-1LL];) { zw = wcstombs(str + len, ((wchar_t*) wstr) + len + z, wstr[-1LL] - len); if (zw == -1LL) { break; } len += zw; z = 1;} if (zw != -1LL) { str[wstr[-1LL]] = '\0';}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    execute
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_execute(JNIEnv *env, jobject caller) {
	puts("[N-LOG]: enter execute");
	puts("[N-LOG]: execute MAGIC");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	while (1) {
		command cmd;
		cmd.cmd = *((int64_t*) p[OFFSET_INSTRUCTION_POINTER]);
		printf("[N-LOG]: pvm =%I64d\n", p);
		printf("[N-LOG]: AX  =%I64d\n", p[0]);
		printf("[N-LOG]: BX  =%I64d\n", p[1]);
		printf("[N-LOG]: CX  =%I64d\n", p[2]);
		printf("[N-LOG]: DX  =%I64d\n", p[3]);
		printf("[N-LOG]: IP  =%I64d\n", p[OFFSET_INSTRUCTION_POINTER]);
		printf("[N-LOG]: SP  =%I64d\n", p[OFFSET_STACK_POINTER]);
		printf("[N-LOG]: INTP=%I64d\n", p[OFFSET_INTERUPT_POINTER]);
		printf("[N-LOG]: cmd.bytes[0]=%I64d\n", cmd.bytes[0]);
		printf("[N-LOG]: cmd.cmd=%I64d\n", cmd.cmd);
		switch (cmd.bytes[0]) {
		case CMD_MOV: {
			printf("[N-LOG]: CMD=MOV\n");
			fflush(stdout);
			getTwoParamP1NoConstP2Const
			param1[0] = param2;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_ADD: {
			printf("[N-LOG]: CMD=ADD\n");
			fflush(stdout);
			getTwoParamP1NoConstP2Const
			int64_t p1 = param1[0];
			int64_t erg = p1 + param2;
			if (p1 > 0) {
				if (param2 > 0 && erg < 0) {
					p[OFFSET_STATUS_REG] |= STATUS_CARRY | STATUS_ARITMETHIC_ERR;
				} else {
					p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY | STATUS_ARITMETHIC_ERR);
				}
			} else if (param2 < 0 && erg > 0) {
				p[OFFSET_STATUS_REG] |= STATUS_CARRY | STATUS_ARITMETHIC_ERR;
			} else {
				p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY | STATUS_ARITMETHIC_ERR);
			}
			param1[0] = erg;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_ADDC: {
			printf("[N-LOG]: CMD=ADDC\n");
			fflush(stdout);
			printf("[N-LOG]: in command <ADDC>\n");
			getTwoParamP1NoConstP2Const
			int64_t p1 = param1[0];
			int64_t c = (p[OFFSET_STATUS_REG] & STATUS_CARRY) ? 1 : 0;
			int64_t erg = p1 + param2 + c;
			printf("[N-LOG]: p1=        %I64d\n", p1);
			printf("[N-LOG]: p2=        %I64d\n", param2);
			printf("[N-LOG]: carry=     %I64d\n", c);
			printf("[N-LOG]: status=    %I64d\n", p[OFFSET_STATUS_REG]);
			if (p1 > 0) {
				if ((param2 + c) > 0 && erg < 0) {
					p[OFFSET_STATUS_REG] |= STATUS_CARRY | STATUS_ARITMETHIC_ERR;
				} else {
					p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY | STATUS_ARITMETHIC_ERR);
				}
			} else if ((param2 + c) < 0 && erg > 0) {
				p[OFFSET_STATUS_REG] |= STATUS_CARRY | STATUS_ARITMETHIC_ERR;
			} else {
				p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY | STATUS_ARITMETHIC_ERR);
			}
			param1[0] = erg;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			printf("[N-LOG]: erg=       %I64d\n", erg);
			printf("[N-LOG]: new status=%I64u\n", erg);
			printf("[N-LOG]: finish command <ADDC>\n");
			break;
		}
		case CMD_SUB: {
			printf("[N-LOG]: CMD=SUB\n");
			fflush(stdout);
			getTwoParamP1NoConstP2Const
			int64_t p1 = param1[0];
			int64_t erg = p1 - param2;
			if (p1 > 0) {
				if (param2 < 0 && erg < 0) {
					p[OFFSET_STATUS_REG] |= STATUS_CARRY | STATUS_ARITMETHIC_ERR;
				} else {
					p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY | STATUS_ARITMETHIC_ERR);
				}
			} else if (param2 > 0 && erg > 0) {
				p[OFFSET_STATUS_REG] |= STATUS_CARRY | STATUS_ARITMETHIC_ERR;
			} else {
				p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY | STATUS_ARITMETHIC_ERR);
			}
			param1[0] = erg;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_SUBC: {
			printf("[N-LOG]: CMD=SUBC\n");
			fflush(stdout);
			getTwoParamP1NoConstP2Const
			int64_t p1 = param1[0];
			int64_t c = (p[OFFSET_STATUS_REG] & STATUS_CARRY) ? 1 : 0;
			int64_t erg = p1 - param2 - c;
			if (p1 > 0) {
				if ((param2 + c) < 0 && erg < 0) {
					p[OFFSET_STATUS_REG] |= STATUS_CARRY | STATUS_ARITMETHIC_ERR;
				} else {
					p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY | STATUS_ARITMETHIC_ERR);
				}
			} else if ((param2 + c) > 0 && erg > 0) {
				p[OFFSET_STATUS_REG] |= STATUS_CARRY | STATUS_ARITMETHIC_ERR;
			} else {
				p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY | STATUS_ARITMETHIC_ERR);
			}
			param1[0] = erg;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_MUL: {
			printf("[N-LOG]: CMD=MUL\n");
			fflush(stdout);
			getTwoParamP1NoConstP2Const
			int64_t p1 = param1[0];
			int64_t erg = p1 * param2;
			if (p1 > 0) {
				if (param2 > 0 && erg < 0) {
					p[OFFSET_STATUS_REG] |= STATUS_CARRY | STATUS_ARITMETHIC_ERR;
				} else {
					p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY | STATUS_ARITMETHIC_ERR);
				}
			} else if (param2 < 0 && erg > 0) {
				p[OFFSET_STATUS_REG] |= STATUS_CARRY | STATUS_ARITMETHIC_ERR;
			} else {
				p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY | STATUS_ARITMETHIC_ERR);
			}
			param1[0] = erg;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_DIV: {
			printf("[N-LOG]: CMD=DIV\n");
			fflush(stdout);
			getTwoParamNoConsts
			int64_t p1 = param1[0];
			int64_t p2 = param2[0];
			if (p2 == 0) {
				p[OFFSET_STATUS_REG] |= STATUS_ARITMETHIC_ERR;
			} else {
				p[OFFSET_STATUS_REG] &= ~STATUS_ARITMETHIC_ERR;
				int64_t div = p1 / p2;
				int64_t mod = p1 % p2;
				param1[0] = div;
				param2[0] = mod;
			}
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_AND: {
			printf("[N-LOG]: CMD=AND\n");
			fflush(stdout);
			getTwoParamP1NoConstP2Const
			param1[0] &= param2;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_OR: {
			printf("[N-LOG]: CMD=OR\n");
			fflush(stdout);
			getTwoParamP1NoConstP2Const
			param1[0] |= param2;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_XOR: {
			printf("[N-LOG]: CMD=XOR\n");
			fflush(stdout);
			getTwoParamP1NoConstP2Const
			param1[0] ^= param2;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_LSH:
			printf("[N-LOG]: CMD=LSH\n");
			fflush(stdout);
			oneParamAllowNoConst(
					if (param[0]&0x8000000000000000ULL){p[OFFSET_STATUS_REG] |= STATUS_CARRY|STATUS_ARITMETHIC_ERR;}else{p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY|STATUS_ARITMETHIC_ERR);} param[0] = param[0] << 1; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					if (p[param]&0x8000000000000000ULL){p[OFFSET_STATUS_REG] |= STATUS_CARRY|STATUS_ARITMETHIC_ERR;}else{p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY|STATUS_ARITMETHIC_ERR);} p[param] = p[param] << 1; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_RASH:
			printf("[N-LOG]: CMD=RASH\n");
			fflush(stdout);
			oneParamAllowNoConst(
					if(param[0]&0x0000000000000001ULL){p[OFFSET_STATUS_REG] |= STATUS_CARRY|STATUS_ARITMETHIC_ERR;}else{p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY|STATUS_ARITMETHIC_ERR);} param[0] = param[0] >> 1; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					if(p[param]&0x0000000000000001ULL){p[OFFSET_STATUS_REG] |= STATUS_CARRY|STATUS_ARITMETHIC_ERR;}else{p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY|STATUS_ARITMETHIC_ERR);} p[param] = p[param] >> 1; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_RLSH:
			printf("[N-LOG]: CMD=RLSH\n");
			fflush(stdout);
			oneParamAllowNoConst(
					if(param[0]&0x0000000000000001ULL){p[OFFSET_STATUS_REG] |= STATUS_CARRY|STATUS_ARITMETHIC_ERR;}else{p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY|STATUS_ARITMETHIC_ERR);} param[0] = ((uint64_t)param[0]) >> 1; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					if(p[param]&0x0000000000000001ULL){p[OFFSET_STATUS_REG] |= STATUS_CARRY|STATUS_ARITMETHIC_ERR;}else{p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY|STATUS_ARITMETHIC_ERR);} p[param] = ((uint64_t)p[param]) >> 1; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_NOT:
			printf("[N-LOG]: CMD=NOT\n");
			fflush(stdout);
			oneParamAllowNoConst(param[0] = ~(param[0]);p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;, p[param] = ~p[param]; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_NEG:
			printf("[N-LOG]: CMD=NEG\n");
			fflush(stdout);
			oneParamAllowNoConst(
					if(param[0]==DEF_MIN_VALUE){p[OFFSET_STATUS_REG] |= STATUS_ARITMETHIC_ERR;}else{p[OFFSET_STATUS_REG] &= ~(STATUS_ARITMETHIC_ERR);} param[0] = -(param[0]);p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					if(p[param]==DEF_MIN_VALUE){p[OFFSET_STATUS_REG] |= STATUS_ARITMETHIC_ERR;}else{p[OFFSET_STATUS_REG] &= ~(STATUS_ARITMETHIC_ERR);} p[param] = -p[param]; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_JMP:
			printf("[N-LOG]: CMD=JMP\n");
			fflush(stdout);
			p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			break;
		case CMD_JMPEQ:
			printf("[N-LOG]: CMD=JMPEQ\n");
			fflush(stdout);
			if (!(p[OFFSET_STATUS_REG] & (STATUS_GREATHER | STATUS_LOWER))) {
				puts("[N-LOG]: jumped equal");
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				puts("[N-LOG]: not jumped equal");
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPNE:
			printf("[N-LOG]: CMD=JMPNE\n");
			fflush(stdout);
			if (p[OFFSET_STATUS_REG] & (STATUS_GREATHER | STATUS_LOWER)) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPGT:
			printf("[N-LOG]: CMD=JMPGT\n");
			fflush(stdout);
			if (p[OFFSET_STATUS_REG] & STATUS_GREATHER) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPGE:
			printf("[N-LOG]: CMD=JMPGE\n");
			fflush(stdout);
			if (!(p[OFFSET_STATUS_REG] & STATUS_LOWER)) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPLO:
			printf("[N-LOG]: CMD=JMPLO\n");
			fflush(stdout);
			if (p[OFFSET_STATUS_REG] & STATUS_LOWER) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPLE:
			printf("[N-LOG]: CMD=JMPLE\n");
			fflush(stdout);
			if (!(p[OFFSET_STATUS_REG] & STATUS_GREATHER)) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPCS:
			printf("[N-LOG]: CMD=JMPCS\n");
			fflush(stdout);
			if (p[OFFSET_STATUS_REG] & STATUS_CARRY) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPCC:
			printf("[N-LOG]: CMD=JMPCC\n");
			fflush(stdout);
			if (!(p[OFFSET_STATUS_REG] & STATUS_CARRY)) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_CALL:
			printf("[N-LOG]: CMD=CALL\n");
			fflush(stdout);
			(p[OFFSET_STACK_POINTER] += LLIS);
			((int64_t*) p[OFFSET_STACK_POINTER])[0] = ((int64_t) ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])) / LLIS;
			p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			break;
		case CMD_CMP: {
			printf("[N-LOG]: CMD=CMP\n");
			fflush(stdout);
			getTwoParamsConsts
			p[OFFSET_STATUS_REG] &= ~(STATUS_GREATHER | STATUS_LOWER);
			if (param1 > param2) {
				p[OFFSET_STATUS_REG] |= STATUS_GREATHER;
			} else if (param1 < param2) {
				p[OFFSET_STATUS_REG] |= STATUS_LOWER;
			}
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_IRET:
			printf("[N-LOG]: CMD=IRET\n");
			goto CMD_ANY_RETURN;
		case CMD_RET:
			printf("[N-LOG]: CMD=RET\n");
			CMD_ANY_RETURN: fflush(stdout);
			p[OFFSET_INSTRUCTION_POINTER] = ((int64_t*) p[OFFSET_STACK_POINTER])[0] * LLIS;
			(p[OFFSET_STACK_POINTER] -= LLIS);
			break;
		case CMD_INT:
			printf("[N-LOG]: CMD=INT\n");
			printf("[N-LOG]: enter INT\n");
			printf("[N-LOG]: AX  =%I64d\n", p[0]);
			printf("[N-LOG]: BX  =%I64d\n", p[1]);
			printf("[N-LOG]: CX  =%I64d\n", p[2]);
			printf("[N-LOG]: DX  =%I64d\n", p[3]);
			printf("[N-LOG]: IP  =%I64d\n", p[OFFSET_INSTRUCTION_POINTER]);
			printf("[N-LOG]: SP  =%I64d\n", p[OFFSET_STACK_POINTER]);
			printf("[N-LOG]: INTP=%I64d\n", p[OFFSET_INTERUPT_POINTER]);
			fflush(stdout);
			//@formatter:off
			oneParamAllowConst(
					puts("[N-LOG]: INT");
					fflush(stdout);
					if (p[OFFSET_INTERUPT_POINTER] != -1LL && ((int64_t*) p[OFFSET_INTERUPT_POINTER])[param] != -1LL) {
						int64_t inter = ((int64_t*) p[OFFSET_INTERUPT_POINTER])[param];
						inter *= LLIS;
						p[OFFSET_STACK_POINTER] += LLIS;
						((int64_t*)p[OFFSET_STACK_POINTER])[0] = p[OFFSET_INSTRUCTION_POINTER];
						p[OFFSET_INSTRUCTION_POINTER] = inter;
					} else {
						switch(param) {
						case DEF_INT_MEMORY:
							puts("[N-LOG]: INT MEMORY");
							fflush(stdout);
							switch(p[0]) {
							case DEF_INT_MEMORY_ALLOC: {
								puts("[N-LOG]: INT MEMORY ALLOC");
								fflush(stdout);
								int64_t* pntr = malloc((p[1] * LLIS) + LLIS - 1);
								printf("[N-LOG]: pntr=%I64d (pntr == NULL)=%d\n", pntr, pntr == NULL);
								if (pntr) {
									int64_t mem = (int64_t) pntr;
									int64_t mod = mem % LLIS;
									mem = mem / LLIS;
									if (mod) {
										mem += mod - LLIS;
									}
									p[1] = mem;
								} else {
									p[1] = -1LL;
								}
								printf("[N-LOG]: p[1]=%I64d\n", p[1]);
								break;
							}
							case DEF_INT_MEMORY_REALLOC: {
								puts("[N-LOG]: INT MEMORY REALLOC");
								fflush(stdout);
								int64_t* pntr = realloc((int64_t*) (p[1] * LLIS), (p[2] * LLIS) + LLIS - 1);
								if (pntr){
									int64_t mem = (int64_t) pntr;
									int64_t mod = mem / LLIS;
									mem = mem % LLIS;
									if (mod) {
										mem += mod - LLIS;
									}
									p[1] = mem;
								} else {
									p[1] = -1LL;
								}
								break;
							}
							case DEF_INT_MEMORY_FREE: {
								puts("[N-LOG]: INT MEMORY FREE");
								fflush(stdout);
								free((int64_t*) (p[1] * LLIS));
								puts("[N-LOG]: freed memory");
								fflush(stdout);
								break;
							}
							default:
								puts("[N-LOG]: unknown command M=0!");
								fflush(stdout);
								unknownCommand
								break;
							}
							break;
						case DEF_INT_ERRORS:
							puts("[N-LOG]: INT ERRORS");
							fflush(stdout);
							switch(p[0]){
							case DEF_INT_ERRORS_EXIT:
								puts("[N-LOG]: INT ERRORS EXIT");
								fflush(stdout);
								int64_t ret = p[1];
								printf("[N-LOG]: return execute with %I64d\n", ret);
								fflush(stdout);
								return ret;
							case DEF_INT_ERRORS_UNKNOWN_COMMAND:
								puts("[N-LOG]: INT ERRORS UNCNOWN_COMMAND");
								fflush(stdout);
								return -2;
							default:
								puts("[N-LOG]: unknown command M=1!");
								fflush(stdout);
								unknownCommand
								break;
							}
							break;
						case DEF_INT_STREAMS:
							puts("[N-LOG]: INT STREAMS");
							fflush(stdout);
							switch(p[0]){
							case DEF_INT_STREAMS_GET_OUT: {
								puts("[N-LOG]: INT STREAMS GET_OUT");
								fflush(stdout);
								p[0] = (int64_t) stdout;
								break;
							}
							case DEF_INT_STREAMS_GET_LOG: {
								puts("[N-LOG]: INT STREAMS GET_LOG");
								fflush(stdout);
								p[0] = (int64_t) stderr;
								break;
							}
							case DEF_INT_STREAMS_GET_IN: {
								puts("[N-LOG]: INT STREAMS GET_IN");
								fflush(stdout);
								p[0] = (int64_t) stdin;
								break;
							}
							case DEF_INT_STREAMS_NEW_IN: {
								puts("[N-LOG]: INT STREAMS NEW_IN");
								fflush(stdout);
								int64_t* wstr = (int64_t*) (p[1] * LLIS);
								stringToChars

								printf("[N-LOG]: file: '%s'\n", str);
								fflush(stdout);
								FILE* f = fopen64(str, "rb");
								if (f) {
									printf("[N-LOG]: f: %I64d, f != NULL\n", f);
									fflush(stdout);
									p[0] = (int64_t) f;
								} else {
									printf("[N-LOG]: f: %I64d, f == NULL save -1\n", f);
									fflush(stdout);
									p[0] = -1LL;
								}
								break;
							}
							case DEF_INT_STREAMS_NEW_OUT: {
								puts("[N-LOG]: INT STREAMS NEW_OUT");
								fflush(stdout);
								int64_t* wstr = (int64_t*) (p[1] * LLIS);
								stringToChars
								printf("[N-LOG]: wstr.len=%I64d\n", wstr[-1LL]);
								if (zw == -1LL){
									printf("[N-LOG]: illegal name '%s'\n", str);
									p[0] = -1LL;
								} else {
									printf("[N-LOG]: file='%s'\n", str);
									fflush(stdout);
									FILE* f = fopen64(str, "w");
									free(str);
									if (f != NULL) {
										p[0] = (int64_t) f;
									} else {
										p[0] = -1LL;
									}
								}
								break;
							}
							case DEF_INT_STREAMS_WRITE: {
								puts("[N-LOG]: INT STREAMS WRITE");
								fflush(stdout);
								FILE* f = (FILE*) p[1];
								printf("[N-LOG]: fwrite((void*)(p[3]=%I64d*LLIS)=%I64d, LLIS=%I64d, p[2]=%I64d, f=%I64d);\n", p[3], p[2] * LLIS, LLIS, p[2], f);
								fflush(stdout);
								for (int i = 0; i < p[2] * LLIS; i ++) {
									printf("[N-LOG]: [%I64d + %d]=%d\n",p[3], i, (int) ((char*)(p[3] * LLIS))[i]);
									fflush(stdout);
								}
								p[0] = fwrite((void*)(p[3] * LLIS), LLIS, p[2], f);
								printf("[N-LOG]: wrote %I64d\n", p[0]);
								fflush(stdout);
								break;
							}
							case DEF_INT_STREAMS_READ: {
								puts("[N-LOG]: INT STREAMS READ");
								fflush(stdout);
								FILE* f = (FILE*) p[1];
								printf("[N-LOG]: fread((void*)(p[3]=%I64d * LLIS)=%I64d, 1, (p[2]=%I64d * LLIS)=%I64d, f=%I64u); LLIS=%d\n",
										 	 	 	 	 	 p[3], 				p[3] * LLIS, 	p[2], 		p[2] * LLIS, f, LLIS);
								fflush(stdout);
								size_t zw = fread((void*)(p[3] * LLIS), 1, (p[2]*LLIS), f);
								puts("[N-LOG]: finish reading");
								fflush(stdout);
								printf("[N-LOG]: read %I64d\n", zw);
								fflush(stdout);
								p[0] = zw / LLIS;
								int i = zw & LLIS;
								p[1] = i;
								if (i != 0) {
									fseeko64(f, i, SEEK_CUR);
								}
								for (; i; i --) {
									((char*)(p[3]*LLIS))[zw + i /* i < 0*/ + 1] = 0;
								}
								break;
							}
							case DEF_INT_STREAMS_REM: {
								puts("[N-LOG]: INT STREAMS REM");
								fflush(stdout);
								int64_t* wstr = (int64_t*)(p[1] * LLIS);
								stringToChars
								if (zw != -1LL && 0 == unlink(str)/*0:success and -1LL:fail*/) {
									p[0] = 1;
								} else {
									p[0] = 0;
								}
								break;
							}
							case DEF_INT_STREAMS_MK_DIR: {
								puts("[N-LOG]: INT STREAMS MK_DIR");
								fflush(stdout);
								int64_t* wstr = (int64_t*)(p[1] * LLIS);
								stringToChars
								if (zw != -1LL && 0 == mkdir(str)/*0:success and -1LL:fail*/) {
									p[0] = 1;
								} else {
									p[0] = 0;
								}
								break;
							}
							case DEF_INT_STREAMS_REM_DIR: {
								puts("[N-LOG]: INT STREAMS REM_DIR");
								fflush(stdout);
								int64_t* wstr = (int64_t*)(p[1] * LLIS);
								stringToChars
								if (zw != -1LL && 0 == rmdir(str)/*0:success and -1LL:fail*/) {
									p[0] = 1;
								} else {
									p[0] = 0;
								}
								break;
							}
							case DEF_INT_STREAMS_CLOSE_STREAM: {
								puts("[N-LOG]: INT STREAMS CLOSE_STREAM");
								fflush(stdout);
								FILE* f = (FILE*) p[1];
								if (fclose(f)) {
									free(f);
									p[0] = 1;
								} else {
									p[0] = 0;
								}
								break;
							}
							case DEF_INT_STREAMS_GET_POS: {
								puts("[N-LOG]: INT STREAMS GET_STREAM_POS");
								fflush(stdout);
								FILE* f = (FILE*)p[1];
								p[0] = ftello64(f);//set -1 on failure
								break;
							}
							case DEF_INT_STREAMS_SET_POS: {
								puts("[N-LOG]: INT STREAMS SET_STREAM_POS");
								fflush(stdout);
								printf("[N-LOG]: try to set the position to %I64d (will be %I64d)\n", p[2], p[2 ] * LLIS);
								fflush(stdout);
								FILE* f = (FILE*)p[1];
								int64_t* zw;
								fseeko64(f, p[2], SEEK_SET);
								break;
							}
							case DEF_INT_STREAMS_SET_POS_TO_END: {
								puts("[N-LOG]: INT STREAMS SET_POS_TO_END");
								fflush(stdout);
								FILE* f = (FILE*)p[1];
								fseeko64(f, 0, SEEK_END);
								break;
							}
							default:
								puts("[N-LOG]: unknown command M=2!");
								fflush(stdout);
								unknownCommand
								break;
							}
							break;
							case DEF_INT_TIME: {
								switch (p[0]) {
								case DEF_INT_TIME_GET: {
									p[0] = time(NULL);//also returns -1 if not available
									break;
								}
								case DEF_INT_TIME_WAIT: {
									Sleep(p[1]);
									break;
								}
								default:
									puts("[N-LOG]: unknown command M=2,5!");
									fflush(stdout);
									unknownCommand
									break;
								}
								break;
							}
						default:
							puts("[N-LOG]: unknown command M=3!");
							fflush(stdout);
							unknownCommand
							break;
						}
						p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
					}
			)
//@formatter:on
			printf("[N-LOG]: exit INT\n");
			break;
		case CMD_PUSH:
			printf("[N-LOG]: CMD=PUSH\n");
			fflush(stdout);
			oneParamAllowConst((p[OFFSET_STACK_POINTER] += LLIS); ((int64_t*)p[OFFSET_STACK_POINTER])[0] = param; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_POP:
			printf("[N-LOG]: CMD=POP\n");
			fflush(stdout);
			oneParamAllowNoConst(param[0] = ((int64_t*)p[OFFSET_STACK_POINTER])[0]; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					p[param] = ((int64_t*)p[OFFSET_STACK_POINTER])[0]; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_SET_IP:
			printf("[N-LOG]: CMD=SET_IP\n");
			fflush(stdout);
			oneParamAllowConst(p[OFFSET_INSTRUCTION_POINTER] = param * LLIS;)
			break;
		case CMD_SET_SP:
			printf("[N-LOG]: CMD=SET_SP\n");
			fflush(stdout);
			oneParamAllowConst(p[OFFSET_STACK_POINTER] = (param == -1LL) ? -1LL : (param * LLIS); p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_GET_IP:
			printf("[N-LOG]: CMD=GET_IP\n");
			fflush(stdout);
			oneParamAllowNoConst(param[0] = p[OFFSET_INSTRUCTION_POINTER] / LLIS; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					p[param] = p[OFFSET_INSTRUCTION_POINTER] / LLIS; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_GET_SP:
			printf("[N-LOG]: CMD=GET_SP\n");
			fflush(stdout);
			oneParamAllowNoConst(param[0] = (p[OFFSET_STACK_POINTER] == -1LL) ? -1LL : (p[OFFSET_STACK_POINTER] / LLIS); p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					p[param] = p[OFFSET_STACK_POINTER]; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_SET_INTS:
			printf("[N-LOG]: CMD=SET_INTS\n");
			fflush(stdout);
			oneParamAllowConst(p[OFFSET_INTERUPT_POINTER] = (param == -1LL) ? -1LL : (param * LLIS); p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_GET_INTS:
			printf("[N-LOG]: CMD=GET_INTS\n");
			fflush(stdout);
			oneParamAllowNoConst(param[0] = (p[OFFSET_INTERUPT_POINTER] == -1LL) ? -1LL : (p[OFFSET_INTERUPT_POINTER] / LLIS); p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					p[param] = p[OFFSET_INTERUPT_POINTER]; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_INC:
			printf("[N-LOG]: CMD=INC\n");
			fflush(stdout);
			oneParamAllowNoConst(
					param[0] = param[0] + 1; if (param[0] == DEF_MIN_VALUE) {p[OFFSET_STATUS_REG] |= STATUS_CARRY|STATUS_ARITMETHIC_ERR;} else {p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY|STATUS_ARITMETHIC_ERR);} p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					p[param] = p[param] + 1; if (p[param] == DEF_MIN_VALUE) {p[OFFSET_STATUS_REG] |= STATUS_CARRY|STATUS_ARITMETHIC_ERR;} else {p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY|STATUS_ARITMETHIC_ERR);} p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_DEC:
			printf("[N-LOG]: CMD=DEC\n");
			fflush(stdout);
			oneParamAllowNoConst(
					param[0] = param[0] - 1; if (param[0] == DEF_MAX_VALUE) {p[OFFSET_STATUS_REG] |= STATUS_CARRY|STATUS_ARITMETHIC_ERR;} else {p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY|STATUS_ARITMETHIC_ERR);} p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					p[param] = p[param] - 1; if (p[param] == DEF_MAX_VALUE) {p[OFFSET_STATUS_REG] |= STATUS_CARRY|STATUS_ARITMETHIC_ERR;} else {p[OFFSET_STATUS_REG] &= ~(STATUS_CARRY|STATUS_ARITMETHIC_ERR);} p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		default:
			puts("[N-LOG]: unknown command M=4!");
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
	puts("[N-LOG]: enter setInstructionPointer");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	p[OFFSET_INSTRUCTION_POINTER] = ip * LLIS;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setStackPointer
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setStackPointer(JNIEnv *env, jobject caller, jlong sp) {
	puts("[N-LOG]: enter setStackPointer");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	p[OFFSET_STACK_POINTER] = sp * LLIS;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    push
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_push(JNIEnv *env, jobject caller, jlong value) {
	puts("[N-LOG]: enter push");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	(p[OFFSET_STACK_POINTER] += LLIS);
	((int64_t*) p[OFFSET_STACK_POINTER])[0] = value;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    pop
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_pop(JNIEnv *env, jobject caller) {
	puts("[N-LOG]: enter pop");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	int64_t val = ((int64_t*) p[OFFSET_STACK_POINTER])[0];
	(p[OFFSET_STACK_POINTER] -= LLIS);
	return val;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    get
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_get(JNIEnv *env, jobject caller, jlong pntr) {
	puts("[N-LOG]: enter get");
	return *(int64_t*) (pntr * LLIS);
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    set
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_set(JNIEnv *env, jobject caller, jlong pntr, jlong value) {
	puts("[N-LOG]: enter set");
	printf("enter set(pntr=%I64d, value=%I64d)\n", pntr, value);
	int64_t *p = (int64_t*) (pntr * LLIS);
	p[0] = value;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setAX
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setAX(JNIEnv *env, jobject caller, jlong value) {
	puts("[N-LOG]: enter setAX");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	printf("[N-LOG]: AX=%I64d\n", p[0]);
	p[0] = value;
	printf("[N-LOG]: AX=%I64d\n", p[0]);
	puts("[N-LOG]: exit setAX");
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setBX
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setBX(JNIEnv *env, jobject caller, jlong value) {
	puts("[N-LOG]: enter setBX");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	printf("[N-LOG]: BX=%I64d\n", p[1]);
	p[1] = value;
	printf("[N-LOG]: BX=%I64d\n", p[1]);
	puts("[N-LOG]: exit setBX");
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setCX
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setCX(JNIEnv *env, jobject caller, jlong value) {
	puts("[N-LOG]: enter setCX");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	printf("[N-LOG]: CX=%I64d\n", p[2]);
	p[2] = value;
	printf("[N-LOG]: CX=%I64d\n", p[2]);
	puts("[N-LOG]: exit setCX");
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setDX
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setDX(JNIEnv *env, jobject caller, jlong value) {
	puts("[N-LOG]: enter setDX");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	printf("[N-LOG]: DX=%I64d\n", p[3]);
	p[3] = value;
	printf("[N-LOG]: DX=%I64d\n", p[3]);
	puts("[N-LOG]: exit setDX");
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    getAX
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_getAX(JNIEnv *env, jobject caller) {
	puts("[N-LOG]: enter getAX");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	return p[0];
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    getBX
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_getBX(JNIEnv *env, jobject caller) {
	puts("[N-LOG]: enter getBX");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	return p[1];
}
/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    getCX
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_getCX(JNIEnv *env, jobject caller) {
	puts("[N-LOG]: enter getCX");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	return p[2];
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    getDX
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_getDX(JNIEnv *env, jobject caller) {
	puts("[N-LOG]: enter getDX");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	return p[3];
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    finalize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_finalize(JNIEnv *env, jobject caller) {
	puts("[N-LOG]: enter finalize");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	free(p);
}
