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

enum int0memory {
	/*
	 * AX: 1
	 * BX: len
	 */
	INT_0_MALLOC = 1,
	/*
	 * AX: 2
	 * BX: old pointer
	 * CX: new len
	 */
	INT_0_REALLOC = 2,
	/*
	 * AX: 3
	 * BX: old pointer
	 */
	INT_0_FREE = 2,
};

enum commands {
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
	CMD_BMOV = 0x0B,
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

#define twoParamsP1NoConst(pointeraction, sractiont) switch(cmd.bytes[0]){/*TODO*/}

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
		switch(cmd.bytes[0]){
			//TODO

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

