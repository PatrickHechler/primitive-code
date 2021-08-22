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

#define STATUS_LOWER          0x0000000000000001LL
#define STATUS_GREATHER       0x0000000000000002LL
#define STATUS_CARRY          0x0000000000000004LL
#define STATUS_ARITMETHIC_ERR 0x0000000000000008LL

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
		printf("[N-ERR]: continued after exception, will now return with %I64d\n", c);
		return c;
	}
	p[OFFSET_INTERUPT_POINTER] = -1;
	if (!values) {
		values = (*env)->GetFieldID(env, cls, "values", "J");
	}
	printf("[N-LOG]: pvm=%I64d\n", p);
	printf("[N-LOG]: AX=%I64d\n", p[0]);
	printf("[N-LOG]: BX=%I64d\n", p[1]);
	printf("[N-LOG]: CX=%I64d\n", p[2]);
	printf("[N-LOG]: DX=%I64d\n", p[3]);
	printf("[N-LOG]: IP=%I64d\n", p[OFFSET_INSTRUCTION_POINTER]);
	printf("[N-LOG]: SP=%I64d\n", p[OFFSET_STACK_POINTER]);
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
		printf("[N-ERR]: continued after exception, will now return with %I64d\n", c);
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
	if (!fseek(file, 0, SEEK_END)) {
		jclass ecls = (*env)->FindClass(env, "java/io/IOException");
		char *msg = malloc(16 + 45);
		strcpy(msg, "error on seeking the end of the file errno=0x");
		itoa(errno, msg + 45, 16);
		jint c = (*env)->ThrowNew(env, ecls, msg);
		printf("[N-ERR]: continued after exception, will now return with %I64d\n", c);
		return c;
	}
	int64_t fpos;
	if (!fgetpos64(file, &fpos)) {
		jclass ecls = (*env)->FindClass(env, "java/io/IOException");
		char *msg = malloc(16 + 45);
		strcpy(msg, "error on seeking the pos of the file errno=0x");
		itoa(errno, msg + 45, 16);
		jint c = (*env)->ThrowNew(env, ecls, msg);
		printf("[N-ERR]: continued after exception, will now return with %I64d\n", c);
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
		printf("[N-ERR]: continued after exception, will now return with %I64d\n", c);
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

#define unknownCommandReturn return -2;
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
#define oneParamAllowNoConst(pntraction, sraction) switch (cmd.bytes[1]) { case ART_ASR: { int64_t param = cmd.bytes[7]; int64_t len = 1; sraction break; } case ART_ANUM_BREG: { int64_t* param = (int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] * LLIS); int64_t len = 2; pntraction break; } case ART_ASR_BREG: { int64_t* param = (int64_t*) (p[cmd.bytes[7]] * LLIS); int64_t len = 1; pntraction break; } case ART_ANUM_BNUM: { int64_t* param = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[2]) * LLIS); int64_t len = 3; pntraction break; } case ART_ASR_BNUM: { int64_t* param = (int64_t*) ((p[cmd.bytes[7]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]) * LLIS); int64_t len = 2; pntraction break; } case ART_ANUM_BSR: { int64_t* param = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + p[cmd.bytes[7]]) * LLIS); int64_t len = 2; pntraction break; } case ART_ASR_BSR: { int64_t* param = (int64_t*) ((p[cmd.bytes[7]] + p[cmd.bytes[6]]) * LLIS); int64_t len = 2; pntraction break; } default: unknownCommandReturn }
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
#define oneParamAllowConst(action) switch (cmd.bytes[1]) { case ART_ANUM: { int64_t param = ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]; int64_t len = 2; action break; } case ART_ASR: { int64_t param = p[cmd.bytes[7]]; int64_t len = 1; action break; } case ART_ANUM_BREG: { int64_t param = *( (int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] * LLIS)); int64_t len = 2; action break; } case ART_ASR_BREG: { int64_t param = *( (int64_t*) (p[cmd.bytes[7]] * LLIS) ); int64_t len = 1; action break; } case ART_ANUM_BNUM: { int64_t param = *( (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[2]) * LLIS)); int64_t len = 3; action break; } case ART_ASR_BNUM: { int64_t param = *( (int64_t*) ((p[cmd.bytes[7]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]) * LLIS)); int64_t len = 2; action break; } case ART_ANUM_BSR: { int64_t param = *( (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + p[cmd.bytes[7]]) * LLIS)); int64_t len = 2; action break; } case ART_ASR_BSR: { int64_t param = *( (int64_t*) ((p[cmd.bytes[7]] + p[cmd.bytes[6]]) * LLIS)); int64_t len = 2; action break; } default: unknownCommandReturn }
#define getTwoParamsConsts int64_t param1; int64_t len; int64_t _bytesi; switch (cmd.bytes[1]) { case ART_ANUM: param1 = ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]; len = 2; _bytesi = 7; break; case ART_ANUM_BNUM: param1 = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[2]) * LLIS)); len = 3; _bytesi = 7; break; case ART_ANUM_BREG: param1 = *((int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] * LLIS)); len = 2; _bytesi = 7; break; case ART_ANUM_BSR: param1 = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + p[cmd.bytes[7]]) * LLIS)); len = 2; _bytesi = 6; break; case ART_ASR: param1 = p[cmd.bytes[7]]; len = 1; _bytesi = 6; break; case ART_ASR_BNUM: param1 = *((int64_t*) ((p[cmd.bytes[7]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]) * LLIS)); len = 2; _bytesi = 6; break; case ART_ASR_BREG: param1 = *((int64_t*) (p[cmd.bytes[7]] * LLIS)); len = 1; _bytesi = 6; break; case ART_ASR_BSR: param1 = *((int64_t*) ((p[cmd.bytes[7]] + p[cmd.bytes[6]]) * LLIS)); len = 1; _bytesi = 5; break; default: unknownCommandReturn } int64_t param2; switch (cmd.bytes[2]) { case ART_ANUM: param2 = ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len]; len += 1; break; case ART_ANUM_BNUM: param2 = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len + 1]) * LLIS)); len += 2; break; case ART_ANUM_BREG: param2 = *((int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] * LLIS)); len += 1; break; case ART_ANUM_BSR: param2 = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] + p[cmd.bytes[_bytesi]]) * LLIS)); len += 1; break; case ART_ASR: param2 = p[cmd.bytes[_bytesi]]; break; case ART_ASR_BNUM: param2 = *((int64_t*) ((p[cmd.bytes[_bytesi]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len]) * LLIS)); len += 1; break; case ART_ASR_BREG: param2 = *((int64_t*) (p[cmd.bytes[_bytesi]] * LLIS)); break; case ART_ASR_BSR: param2 = *((int64_t*) ((p[cmd.bytes[_bytesi]] + p[cmd.bytes[_bytesi - 1]]) * LLIS)); break; default: unknownCommandReturn } //			getTwoParamsConsts
#define getTwoParamP1NoConstP2Const int64_t* param1; int64_t len; int64_t _bytesi; switch (cmd.bytes[1]) { case ART_ANUM_BNUM: param1 = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[2]) * LLIS); len = 3; _bytesi = 7; break; case ART_ANUM_BREG: param1 = (int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] * LLIS); len = 2; _bytesi = 7; break; case ART_ANUM_BSR: param1 = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + p[cmd.bytes[7]]) * LLIS); len = 2; _bytesi = 6; break; case ART_ASR: param1 = &p[cmd.bytes[7]]; len = 1; _bytesi = 6; break; case ART_ASR_BNUM: param1 = (int64_t*) ((p[cmd.bytes[7]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]) * LLIS); len = 2; _bytesi = 6; break; case ART_ASR_BREG: param1 = (int64_t*) (p[cmd.bytes[7]] * LLIS); len = 1; _bytesi = 6; break; case ART_ASR_BSR: param1 = (int64_t*) ((p[cmd.bytes[7]] + p[cmd.bytes[6]]) * LLIS); len = 1; _bytesi = 5; break; default: unknownCommandReturn } int64_t param2; switch (cmd.bytes[2]) { case ART_ANUM: param2 = ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len]; len += 1; break; case ART_ANUM_BNUM: param2 = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len + 1]) * LLIS)); len += 2; break; case ART_ANUM_BREG: param2 = *((int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] * LLIS)); len += 1; break; case ART_ANUM_BSR: param2 = *((int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] + p[cmd.bytes[_bytesi]]) * LLIS)); len += 1; break; case ART_ASR: param2 = p[cmd.bytes[_bytesi]]; break; case ART_ASR_BNUM: param2 = *((int64_t*) ((p[cmd.bytes[_bytesi]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len]) * LLIS)); len += 1; break; case ART_ASR_BREG: param2 = *((int64_t*) (p[cmd.bytes[_bytesi]] * LLIS)); break; case ART_ASR_BSR: param2 = *((int64_t*) ((p[cmd.bytes[_bytesi]] + p[cmd.bytes[_bytesi - 1]]) * LLIS)); break; default: unknownCommandReturn }
#define getTwoParamNoConsts int64_t* param1; int64_t len; int64_t _bytesi; switch (cmd.bytes[1]) { case ART_ANUM_BNUM: param1 = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[2]) * LLIS); len = 3; _bytesi = 7; break; case ART_ANUM_BREG: param1 = (int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] * LLIS); len = 2; _bytesi = 7; break; case ART_ANUM_BSR: param1 = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1] + p[cmd.bytes[7]]) * LLIS); len = 2; _bytesi = 6; break; case ART_ASR: param1 = &p[cmd.bytes[7]]; len = 1; _bytesi = 6; break; case ART_ASR_BNUM: param1 = (int64_t*) ((p[cmd.bytes[7]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[1]) * LLIS); len = 2; _bytesi = 6; break; case ART_ASR_BREG: param1 = (int64_t*) (p[cmd.bytes[7]] * LLIS); len = 1; _bytesi = 6; break; case ART_ASR_BSR: param1 = (int64_t*) ((p[cmd.bytes[7]] + p[cmd.bytes[6]]) * LLIS); len = 1; _bytesi = 5; break; default: unknownCommandReturn } int64_t* param2; switch (cmd.bytes[2]) { case ART_ANUM_BNUM: param2 = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len + 1]) * LLIS); len += 2; break; case ART_ANUM_BREG: param2 = (int64_t*) (((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] * LLIS); len += 1; break; case ART_ANUM_BSR: param2 = (int64_t*) ((((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len] + p[cmd.bytes[_bytesi]]) * LLIS); len += 1; break; case ART_ASR: param2 = &p[cmd.bytes[_bytesi]]; break; case ART_ASR_BNUM: param2 = (int64_t*) ((p[cmd.bytes[_bytesi]] + ((int64_t*)p[OFFSET_INSTRUCTION_POINTER])[len]) * LLIS); len += 1; break; case ART_ASR_BREG: param2 = (int64_t*) (p[cmd.bytes[_bytesi]] * LLIS); break; case ART_ASR_BSR: param2 = (int64_t*) ((p[cmd.bytes[_bytesi]] + p[cmd.bytes[_bytesi - 1]]) * LLIS); break; default: unknownCommandReturn }

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    execute
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_execute(JNIEnv *env, jobject caller) {
	puts("[N-LOG]: enter execute");
	int64_t *p = (int64_t*) (*env)->GetLongField(env, caller, values);
	while (1) {
		command cmd;
		cmd.cmd = *((int64_t*) p[OFFSET_INSTRUCTION_POINTER]);
		printf("[N-LOG]: pvm=%I64d\n", p);
		printf("[N-LOG]: AX=%I64d\n", p[0]);
		printf("[N-LOG]: BX=%I64d\n", p[1]);
		printf("[N-LOG]: CX=%I64d\n", p[2]);
		printf("[N-LOG]: DX=%I64d\n", p[3]);
		printf("[N-LOG]: IP=%I64d\n", p[OFFSET_INSTRUCTION_POINTER]);
		printf("[N-LOG]: SP=%I64d\n", p[OFFSET_STACK_POINTER]);
		printf("[N-LOG]: cmd.bytes[0]=%I64d\n", cmd.bytes[0]);
		printf("[N-LOG]: cmd.cmd=%I64d\n", cmd.cmd);
		switch (cmd.bytes[0]) {
		case CMD_MOV: {
			getTwoParamP1NoConstP2Const
			param1[0] = param2;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_ADD: {
			getTwoParamP1NoConstP2Const
			int64_t p1 = param1[0];
			int64_t erg = p1 + param2;
			if (p1 > 0) {
				if (param2 > 0 && erg < 0) {
					p[OFFSET_STATUS_REG] |= STATUS_CARRY;
				} else {
					p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;
				}
			} else if (param2 < 0 && erg > 0) {
				p[OFFSET_STATUS_REG] |= STATUS_CARRY;
			} else {
				p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;
			}
			param1[0] = erg;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_SUB: {
			getTwoParamP1NoConstP2Const
			int64_t p1 = param1[0];
			int64_t erg = p1 - param2;
			if (p1 > 0) {
				if (param2 < 0 && erg < 0) {
					p[OFFSET_STATUS_REG] |= STATUS_CARRY;
				} else {
					p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;
				}
			} else if (param2 > 0 && erg > 0) {
				p[OFFSET_STATUS_REG] |= STATUS_CARRY;
			} else {
				p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;
			}
			param1[0] = erg;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_MUL: {
			getTwoParamP1NoConstP2Const
			int64_t p1 = param1[0];
			int64_t erg = p1 * param2;
			if (p1 > 0) {
				if (param2 > 0 && erg < 0) {
					p[OFFSET_STATUS_REG] |= STATUS_CARRY;
				} else {
					p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;
				}
			} else if (param2 < 0 && erg > 0) {
				p[OFFSET_STATUS_REG] |= STATUS_CARRY;
			} else {
				p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;
			}
			param1[0] = erg;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_DIV: {
			getTwoParamNoConsts
			int64_t p1 = param1[0];
			int64_t p2 = param2[0];
			if (p2 == 0){
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
			getTwoParamP1NoConstP2Const
			param1[0] &= param2;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_OR: {
			getTwoParamP1NoConstP2Const
			param1[0] |= param2;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_XOR: {
			getTwoParamP1NoConstP2Const
			param1[0] ^= param2;
			p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			break;
		}
		case CMD_LSH:
			oneParamAllowNoConst(
					if (param[0]&0x8000000000000000ULL){p[OFFSET_STATUS_REG] |= STATUS_CARRY;}else{p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;}param[0] = param[0] << 1;p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					if (p[param]&0x8000000000000000ULL){p[OFFSET_STATUS_REG] |= STATUS_CARRY;}else{p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;} p[param] = p[param] << 1; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_RASH:
			oneParamAllowNoConst(
					if(param[0]&0x0000000000000001ULL){p[OFFSET_STATUS_REG] |= STATUS_CARRY;}else{p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;} param[0] = param[0] >> 1;p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					if(p[param]&0x0000000000000001ULL){p[OFFSET_STATUS_REG] |= STATUS_CARRY;}else{p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;} p[param] = p[param] >> 1; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_RLSH:
			oneParamAllowNoConst(
					if(param[0]&0x0000000000000001ULL){p[OFFSET_STATUS_REG] |= STATUS_CARRY;}else{p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;} param[0] = ((uint64_t)param[0]) >> 1;p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					if(p[param]&0x0000000000000001ULL){p[OFFSET_STATUS_REG] |= STATUS_CARRY;}else{p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;} p[param] = ((uint64_t)p[param]) >> 1; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_NOT:
			oneParamAllowNoConst(param[0] = ~(param[0]);p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;, p[param] = ~p[param]; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_NEG:
			oneParamAllowNoConst(param[0] = -(param[0]);p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;, p[param] = -p[param]; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_JMP:
			p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			break;
		case CMD_JMPEQ:
			if (!(p[OFFSET_STATUS_REG] & (STATUS_GREATHER | STATUS_LOWER))) {
				puts("[N-LOG]: jumped equal");
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				puts("[N-LOG]: not jumped equal");
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPNE:
			if (p[OFFSET_STATUS_REG] & (STATUS_GREATHER | STATUS_LOWER)) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPGT:
			if (p[OFFSET_STATUS_REG] & STATUS_GREATHER) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPGE:
			if (!(p[OFFSET_STATUS_REG] & STATUS_LOWER)) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPLO:
			if (p[OFFSET_STATUS_REG] & STATUS_LOWER) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPLE:
			if (!(p[OFFSET_STATUS_REG] & STATUS_GREATHER)) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPCS:
			if (p[OFFSET_STATUS_REG] & STATUS_CARRY) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_JMPCC:
			if (!(p[OFFSET_STATUS_REG] & STATUS_CARRY)) {
				p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			} else {
				p[OFFSET_INSTRUCTION_POINTER] += 2 * LLIS;
			}
			break;
		case CMD_CALL:
			(p[OFFSET_STACK_POINTER] += LLIS);
			((int64_t*) p[OFFSET_STACK_POINTER])[0] = ((int64_t) ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])) / LLIS;
			p[OFFSET_INSTRUCTION_POINTER] += LLIS * ((int64_t*) p[OFFSET_INSTRUCTION_POINTER])[1];
			break;
		case CMD_CMP: {
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
		case CMD_RET:
			p[OFFSET_INSTRUCTION_POINTER] = ((int64_t*) p[OFFSET_STACK_POINTER])[0] * LLIS;
			(p[OFFSET_STACK_POINTER] -= LLIS);
			break;
		case CMD_INT:
			//@formatter:off
			oneParamAllowConst(
					switch(param) {
					case 0:
						switch(p[0]) {
						case 1: {
							int64_t* pntr = malloc((p[1] * LLIS) + LLIS - 1);
							if (pntr) {
								int64_t mem = (int64_t) pntr;
								int64_t mod = mem / LLIS;
								mem = mem % LLIS;
								if (mod) {
									mem += mod - LLIS;
								}
								p[1] = mem;
							} else {
								p[1] = -1;
							}
							break;
						}
						case 2: {
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
								p[1] = -1;
							}
							break;
						}
						case 3:
							free((int64_t*) (p[1] * LLIS));
							break;
						default:
							unknownCommandReturn
						}
						break;
					case 1:
						switch(p[0]){
						case 1: return p[1];
						case 2: /*unknown command*/
							return -2;
						default:
							unknownCommandReturn
						}
						/*break;*/
					default:
						unknownCommandReturn
					}
				p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;
			)
//@formatter:on
			break;
		case CMD_PUSH:
			oneParamAllowConst((p[OFFSET_STACK_POINTER] += LLIS); ((int64_t*)p[OFFSET_STACK_POINTER])[0] = param * LLIS; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_POP:
			oneParamAllowNoConst(param[0] = ((int64_t*)p[OFFSET_STACK_POINTER])[0]; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					p[param] = ((int64_t*)p[OFFSET_STACK_POINTER])[0]; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_SET_IP:
			oneParamAllowConst(p[OFFSET_INSTRUCTION_POINTER] = param * LLIS;)
			break;
		case CMD_SET_SP:
			oneParamAllowConst(p[OFFSET_STACK_POINTER] = param; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_GET_IP:
			oneParamAllowNoConst(param[0] = p[OFFSET_INSTRUCTION_POINTER] / LLIS; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					p[param] = p[OFFSET_INSTRUCTION_POINTER] / LLIS; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_GET_SP:
			oneParamAllowNoConst(param[0] = p[OFFSET_STACK_POINTER]; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;, p[param] = p[OFFSET_STACK_POINTER]; p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_INC:
			oneParamAllowNoConst(
					param[0] = param[0] + 1; if (param[0] == -0x8000000000000000LL) {p[OFFSET_STATUS_REG] |= STATUS_CARRY;} else {p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;} p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					p[param] = p[param] + 1; if (p[param] == -0x8000000000000000LL) {p[OFFSET_STATUS_REG] |= STATUS_CARRY;} else {p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;} p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		case CMD_DEC:
			oneParamAllowNoConst(
					param[0] = param[0] - 1; if (param[0] == 0x7FFFFFFFFFFFFFFFLL) {p[OFFSET_STATUS_REG] |= STATUS_CARRY;} else {p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;} p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;,
					p[param] = p[param] - 1; if (p[param] == 0x7FFFFFFFFFFFFFFFLL) {p[OFFSET_STATUS_REG] |= STATUS_CARRY;} else {p[OFFSET_STATUS_REG] &= ~STATUS_CARRY;} p[OFFSET_INSTRUCTION_POINTER] += len * LLIS;)
			break;
		default:
			unknownCommandReturn
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
