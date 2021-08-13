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
	int64_t ints[5];
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
	pvm *p = malloc(sizeof(pvm));
	if (p == NULL) {
		jclass ecls = (*env)->FindClass(env, "java/lang/OutOfMemoryError");
		jint c = (*env)->ThrowNew(env, ecls, "not enugh memory to allocate the struct pvm (three pointers)");
		printf("[N-ERR]: continued after exception, will now return with %d\n", c);
		return c;
	}
	p->ints[0] = -1;
	p->ints[1] = -1;
	p->ints[2] = -1;
	p->ints[3] = -1;
	p->ints[4] = -1;
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

#define unknownCommandExit exit(-2);/*TODO: link to unknown command interupt so that later the interupt can be overwritten*/
#define unknownCommandReturn return -2;/*TODO: link to unknown command interupt so that later the interupt can be overwritten*/
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
 * 		unknownCommandReturn
 * 	}
 * @formatter:on
 */
#define oneParamAllowNoConst(pntraction, sraction) switch (cmd.bytes[1]) { case ART_ASR: { int64_t param = cmd.bytes[7]; int64_t len = 1; sraction break; } case ART_ANUM_BREG: { int64_t* param = (int64_t*) (p.ip[1] * LLIS); int64_t len = 2; pntraction break; } case ART_ASR_BREG: { int64_t* param = (int64_t*) (p.sr[cmd.bytes[7]] * LLIS); int64_t len = 1; pntraction break; } case ART_ANUM_BNUM: { int64_t* param = (int64_t*) ((p.ip[1] + p.ip[2]) * LLIS); int64_t len = 3; pntraction break; } case ART_ASR_BNUM: { int64_t* param = (int64_t*) ((p.sr[cmd.bytes[7]] + p.ip[1]) * LLIS); int64_t len = 2; pntraction break; } case ART_ANUM_BSR: { int64_t* param = (int64_t*) ((p.ip[1] + p.sr[cmd.bytes[7]]) * LLIS); int64_t len = 2; pntraction break; } case ART_ASR_BSR: { int64_t* param = (int64_t*) ((p.sr[cmd.bytes[7]] + p.sr[cmd.bytes[6]]) * LLIS); int64_t len = 2; pntraction break; } default: unknownCommandReturn }
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
 * 		unknownCommandReturn
 * 	}
 * @formatter:on
 */
#define oneParamAllowConst(action) switch (cmd.bytes[1]) { case ART_ANUM: { int64_t param = p.ip[1]; int64_t len = 2; action break; } case ART_ASR: { int64_t param = p.sr[cmd.bytes[7]]; int64_t len = 1; action break; } case ART_ANUM_BREG: { int64_t param = *( (int64_t*) (p.ip[1] * LLIS)); int64_t len = 2; action break; } case ART_ASR_BREG: { int64_t param = *( (int64_t*) (p.sr[cmd.bytes[7]] * LLIS) ); int64_t len = 1; action break; } case ART_ANUM_BNUM: { int64_t param = *( (int64_t*) ((p.ip[1] + p.ip[2]) * LLIS)); int64_t len = 3; action break; } case ART_ASR_BNUM: { int64_t param = *( (int64_t*) ((p.sr[cmd.bytes[7]] + p.ip[1]) * LLIS)); int64_t len = 2; action break; } case ART_ANUM_BSR: { int64_t param = *( (int64_t*) ((p.ip[1] + p.sr[cmd.bytes[7]]) * LLIS)); int64_t len = 2; action break; } case ART_ASR_BSR: { int64_t param = *( (int64_t*) ((p.sr[cmd.bytes[7]] + p.sr[cmd.bytes[6]]) * LLIS)); int64_t len = 2; action break; } default: unknownCommandReturn }
#define getTwoParamsConsts int64_t param1; int64_t _ipi; int64_t _bytesi; switch (cmd.bytes[1]) { case ART_ANUM: param1 = p.ip[1]; _ipi = 2; _bytesi = 7; break; case ART_ANUM_BNUM: param1 = *((int64_t*) ((p.ip[1] + p.ip[2]) * LLIS)); _ipi = 3; _bytesi = 7; break; case ART_ANUM_BREG: param1 = *((int64_t*) (p.ip[1] * LLIS)); _ipi = 2; _bytesi = 7; break; case ART_ANUM_BSR: param1 = *((int64_t*) ((p.ip[1] + p.sr[cmd.bytes[7]]) * LLIS)); _ipi = 2; _bytesi = 6; break; case ART_ASR: param1 = p.sr[cmd.bytes[7]]; _ipi = 1; _bytesi = 6; break; case ART_ASR_BNUM: param1 = *((int64_t*) ((p.sr[cmd.bytes[7]] + p.ip[1]) * LLIS)); _ipi = 2; _bytesi = 6; break; case ART_ASR_BREG: param1 = *((int64_t*) (p.sr[cmd.bytes[7]] * LLIS)); _ipi = 1; _bytesi = 6; break; case ART_ASR_BSR: param1 = *((int64_t*) ((p.sr[cmd.bytes[7]] + p.sr[cmd.bytes[6]]) * LLIS)); _ipi = 1; _bytesi = 5; break; default: unknownCommandReturn } int64_t param2; switch (cmd.bytes[2]) { case ART_ANUM: param2 = p.ip[_ipi]; break; case ART_ANUM_BNUM: param2 = *((int64_t*) ((p.ip[_ipi] + p.ip[_ipi + 1]) * LLIS)); break; case ART_ANUM_BREG: param2 = *((int64_t*) (p.ip[_ipi] * LLIS)); break; case ART_ANUM_BSR: param2 = *((int64_t*) ((p.ip[_ipi] + p.sr[cmd.bytes[_bytesi]]) * LLIS)); break; case ART_ASR: param2 = p.sr[cmd.bytes[_bytesi]]; break; case ART_ASR_BNUM: param2 = *((int64_t*) ((p.sr[cmd.bytes[_bytesi]] + p.ip[_ipi]) * LLIS)); break; case ART_ASR_BREG: param2 = *((int64_t*) (p.sr[cmd.bytes[_bytesi]] * LLIS)); break; case ART_ASR_BSR: param2 = *((int64_t*) ((p.sr[cmd.bytes[_bytesi]] + p.sr[cmd.bytes[_bytesi - 1]]) * LLIS)); break; default: unknownCommandReturn }
#define getTwoParamP1NoConstP2Const int64_t *param1; int64_t _ipi; int64_t _bytesi; switch (cmd.bytes[1]) { case ART_ANUM_BNUM: param1 = (int64_t*) ((p.ip[1] + p.ip[2]) * LLIS); _ipi = 3; _bytesi = 7; break; case ART_ANUM_BREG: param1 = (int64_t*) (p.ip[1] * LLIS); _ipi = 2; _bytesi = 7; break; case ART_ANUM_BSR: param1 = (int64_t*) ((p.ip[1] + p.sr[cmd.bytes[7]]) * LLIS); _ipi = 2; _bytesi = 6; break; case ART_ASR: param1 = &p.sr[cmd.bytes[7]]; _ipi = 1; _bytesi = 6; break; case ART_ASR_BNUM: param1 = (int64_t*) ((p.sr[cmd.bytes[7]] + p.ip[1]) * LLIS); _ipi = 2; _bytesi = 6; break; case ART_ASR_BREG: param1 = (int64_t*) (p.sr[cmd.bytes[7]] * LLIS); _ipi = 1; _bytesi = 6; break; case ART_ASR_BSR: param1 = (int64_t*) ((p.sr[cmd.bytes[7]] + p.sr[cmd.bytes[6]]) * LLIS); _ipi = 1; _bytesi = 5; break; default: unknownCommandReturn } int64_t param2; switch (cmd.bytes[2]) { case ART_ANUM: param2 = p.ip[_ipi]; break; case ART_ANUM_BNUM: param2 = *((int64_t*) ((p.ip[_ipi] + p.ip[_ipi + 1]) * LLIS)); break; case ART_ANUM_BREG: param2 = *((int64_t*) (p.ip[_ipi] * LLIS)); break; case ART_ANUM_BSR: param2 = *((int64_t*) ((p.ip[_ipi] + p.sr[cmd.bytes[_bytesi]]) * LLIS)); break; case ART_ASR: param2 = p.sr[cmd.bytes[_bytesi]]; break; case ART_ASR_BNUM: param2 = *((int64_t*) ((p.sr[cmd.bytes[_bytesi]] + p.ip[_ipi]) * LLIS)); break; case ART_ASR_BREG: param2 = *((int64_t*) (p.sr[cmd.bytes[_bytesi]] * LLIS)); break; case ART_ASR_BSR: param2 = *((int64_t*) ((p.sr[cmd.bytes[_bytesi]] + p.sr[cmd.bytes[_bytesi - 1]]) * LLIS)); break; default: unknownCommandReturn }
#define getTwoParamNoConsts int64_t *param1; int64_t _ipi; int64_t _bytesi; switch (cmd.bytes[1]) { case ART_ANUM_BNUM: param1 = (int64_t*) ((p.ip[1] + p.ip[2]) * LLIS); _ipi = 3; _bytesi = 7; break; case ART_ANUM_BREG: param1 = (int64_t*) (p.ip[1] * LLIS); _ipi = 2; _bytesi = 7; break; case ART_ANUM_BSR: param1 = (int64_t*) ((p.ip[1] + p.sr[cmd.bytes[7]]) * LLIS); _ipi = 2; _bytesi = 6; break; case ART_ASR: param1 = &p.sr[cmd.bytes[7]]; _ipi = 1; _bytesi = 6; break; case ART_ASR_BNUM: param1 = (int64_t*) ((p.sr[cmd.bytes[7]] + p.ip[1]) * LLIS); _ipi = 2; _bytesi = 6; break; case ART_ASR_BREG: param1 = (int64_t*) (p.sr[cmd.bytes[7]] * LLIS); _ipi = 1; _bytesi = 6; break; case ART_ASR_BSR: param1 = (int64_t*) ((p.sr[cmd.bytes[7]] + p.sr[cmd.bytes[6]]) * LLIS); _ipi = 1; _bytesi = 5; break; default: unknownCommandReturn } int64_t *param2; switch (cmd.bytes[2]) { case ART_ANUM_BNUM: param2 = (int64_t*) ((p.ip[_ipi] + p.ip[_ipi + 1]) * LLIS); break; case ART_ANUM_BREG: param2 = (int64_t*) (p.ip[_ipi] * LLIS); break; case ART_ANUM_BSR: param2 = (int64_t*) ((p.ip[_ipi] + p.sr[cmd.bytes[_bytesi]]) * LLIS); break; case ART_ASR: param2 = &p.sr[cmd.bytes[_bytesi]]; break; case ART_ASR_BNUM: param2 = (int64_t*) ((p.sr[cmd.bytes[_bytesi]] + p.ip[_ipi]) * LLIS); break; case ART_ASR_BREG: param2 = (int64_t*) (p.sr[cmd.bytes[_bytesi]] * LLIS); break; case ART_ASR_BSR: param2 = (int64_t*) ((p.sr[cmd.bytes[_bytesi]] + p.sr[cmd.bytes[_bytesi - 1]]) * LLIS); break; default: unknownCommandReturn }

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    execute
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_execute(JNIEnv *env, jobject caller) {
	puts("[N-LOG]: enter execute");
	pvm p = *((pvm*) (*env)->GetLongField(env, caller, values));
	while (1) {
		command cmd;
		cmd.cmd = *p.ip;
		printf("my command=%d", cmd.cmd);
		switch (cmd.bytes[0]) {
		case CMD_MOV: {
			getTwoParamP1NoConstP2Const
			*param1 = param2;
			break;
		}
		case CMD_ADD: {
			getTwoParamP1NoConstP2Const
			*param1 &= param2;
			break;
		}
		case CMD_SUB: {
			getTwoParamP1NoConstP2Const
			*param1 -= param2;
			break;
		}
		case CMD_MUL: {
			getTwoParamP1NoConstP2Const
			*param1 *= param2;
			break;
		}
		case CMD_DIV: {
			getTwoParamNoConsts
			int64_t div = (*param1) / (*param2);
			int64_t mod = (*param1) % (*param2);
			*param1 = div;
			*param2 = mod;
			break;
		}
		case CMD_AND: {
			getTwoParamP1NoConstP2Const
			*param1 ^= param2;
			break;
		}
		case CMD_OR: {
			getTwoParamP1NoConstP2Const
			*param1 |= param2;
			break;
		}
		case CMD_XOR: {
			getTwoParamP1NoConstP2Const
			*param1 ^= param2;
			break;
		}
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
		case CMD_CMP: {
			getTwoParamsConsts
			p.status &= STATUS_GREATHER | STATUS_LOWER;
			if (param1 > param2) {
				p.status |= STATUS_GREATHER;
			} else if (param1 < param2) {
				p.status |= STATUS_LOWER;
			}
			break;
		}
		case CMD_RET:
			p.ip = (int64_t*) *p.sp;
			p.sp--;
			break;
		case CMD_INT:
			//@formatter:off
			oneParamAllowConst(
					switch(param) {
					case 0:
						switch(p.sr[0]) {
						case 1: {
							int64_t* pntr = malloc((p.sr[1] * LLIS) + LLIS - 1);
							if (pntr) {
								int64_t mem = (int64_t) pntr;
								int64_t mod = mem / LLIS;
								mem = mem % LLIS;
								if (mod) {
									mem += mod - LLIS;
								}
								p.sr[1] = mem;
							} else {
								p.sr[1] = -1;
							}
							break;
						}
						case 2: {
							int64_t* pntr = realloc((int64_t*) (p.sr[1] * LLIS), (p.sr[2] * LLIS) + LLIS - 1);
							if (pntr){
								int64_t mem = (int64_t) pntr;
								int64_t mod = mem / LLIS;
								mem = mem % LLIS;
								if (mod) {
									mem += mod - LLIS;
								}
								p.sr[1] = mem;
							} else {
								p.sr[1] = -1;
							}
							break;
						}
						case 3:
							free((int64_t*) (p.sr[1] * LLIS));
							break;
						default:
							unknownCommandReturn
						}
						break;
					case 1:
						switch(p.sr[0]){
						case 1: return p.sr[1];
						case 2: /*unknown command*/
							return -2;
						default:
							unknownCommandReturn
						}
						/*break;*/
					default:
						unknownCommandReturn
					}
				p.ip += len;
			)
//@formatter:on
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
	return *(int64_t*) (pntr * LLIS);
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    set
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_set(JNIEnv *env, jobject caller, jlong pntr, jlong value) {
	printf("enter set(pntr=0x%16x, value=0x%16x)\n", pntr, value);
	int64_t *p = (int64_t*) (pntr * LLIS);
	p[0] = value;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setAX
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setAX(JNIEnv *env, jobject caller, jlong value) {
	pvm p = *(pvm*) (*env)->GetLongField(env, caller, values);
	p.sr[0] = value;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setBX
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setBX(JNIEnv *env, jobject caller, jlong value) {
	pvm p = *(pvm*) (*env)->GetLongField(env, caller, values);
	p.sr[1] = value;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setCX
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setCX(JNIEnv *env, jobject caller, jlong value) {
	pvm p = *(pvm*) (*env)->GetLongField(env, caller, values);
	p.sr[2] = value;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setDX
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setDX(JNIEnv *env, jobject caller, jlong value) {
	pvm p = *(pvm*) (*env)->GetLongField(env, caller, values);
	p.sr[3] = value;
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    getAX
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_getAX(JNIEnv *env, jobject caller) {
	pvm p = *(pvm*) (*env)->GetLongField(env, caller, values);
	return p.sr[0];
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    getBX
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_getBX(JNIEnv *env, jobject caller) {
	pvm p = *(pvm*) (*env)->GetLongField(env, caller, values);
	return p.sr[1];
}
/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    getCX
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_getCX(JNIEnv *env, jobject caller) {
	pvm p = *(pvm*) (*env)->GetLongField(env, caller, values);
	return p.sr[2];
}

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    getDX
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_getDX(JNIEnv *env, jobject caller) {
	pvm p = *(pvm*) (*env)->GetLongField(env, caller, values);
	return p.sr[3];
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
