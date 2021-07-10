/*
 * de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime.cpp
 *
 *  Created on: 07.07.2021
 *      Author: Patrick
 */

#include "de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime.h"
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>

typedef struct {
	int64_t*regs;
	uint64_t regLen;
	int64_t *stack;
	uint64_t stackMaxSize;
	uint64_t stackSize;
	uint8_t *cmds;
	uint64_t ip;
} runtime;

JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_create(
		JNIEnv *env, jobject obj, jlong regs, jlong stack) {
	runtime r = ((runtime*) malloc(sizeof(runtime)))[0];
	r.regs = (long long*) malloc(sizeof(long long*) * (uint64_t) regs);
	r.regs[0] = 7;
	r.regLen = (uint64_t) regs;
	r.stack = (long long*) malloc(sizeof(long long*) * (uint64_t) stack);
	r.stackMaxSize = (uint64_t) stack;
	r.stackSize = 0;
	r.cmds = NULL;
	long long zw = (long long) &r;
	return (jlong) zw;
}

JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_read(
		JNIEnv *env, jobject caller, jstring filename) {
	jclass cls = (*env)->FindClass(env, "de/hechler/patrick/codesprachen/primitive/runtime/PrimitiveRuntime");
	jfieldID fieldID = (*env)->GetFieldID(env, caller,"values","J");
	jlong field = (*env)->GetLongField(env, cls,fieldID);
	runtime r = ((runtime*) field)[0];
	const char *name = (*env)->GetStringUTFChars(env, filename, JNI_FALSE);
	FILE *fpr = fopen(name, "rb");
	fseek(fpr, 0, SEEK_END);
	long long len = ftell(fpr);
	rewind(fpr);
	r.cmds = (uint8_t*) malloc(len);
	fread(r.cmds, 1, len, fpr);
	fclose(fpr);
	r.ip = 0;
}

JNIEXPORT void JNICALL throwOutOfMemory(const char* msg1_2_3, JNIEnv* env, uint64_t len, uint64_t notNewlen) {
	int str1len = strlen(msg1_2_3);
	int str2len = strlen(msg1_2_3 + str1len + 1 + 16);
	int str3len = strlen(msg1_2_3 + str1len + 1 + 32 + 1 + str2len);
	char* msg = (char*) malloc(str1len + str2len);
	strcpy(msg, msg1_2_3);
	int off = str1len;
	lltoa(len, msg + off, 16);
	off += 16;
	strcpy(msg + off, msg1_2_3);
	off += str2len;
	lltoa(notNewlen, msg + off, 16);
	off += 16;
	strcpy(msg + off, msg1_2_3);
	jclass cls = (*env)->FindClass(env,	"java/lang/OutOfMemoryError");
	jmethodID mid = (*env)->GetMethodID(env, cls, "<init>", "V(Ljava/lang/String)");
	jobject exep = (*env)->NewObject(env, cls, mid, "java/lang/IndexOutOfBoundsException");
	(*env)->Throw(env, exep);
}

JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_setRegistersLen(
		JNIEnv *env, jobject caller, jlong len) {
	jclass cls = (*env)->FindClass(env, "de/hechler/patrick/codesprachen/primitive/runtime/PrimitiveRuntime");
	jfieldID fieldID = (*env)->GetFieldID(env, caller,"values","J");
	jlong field = (*env)->GetLongField(env, cls,fieldID);
	runtime r = ((runtime*) field)[0];
	int64_t* zw = (int64_t*) realloc(r.regs, sizeof(int64_t) * (uint64_t) len);
	if (zw == NULL) {
		throwOutOfMemory("registers would need to much memory! beforelen: 0x\0 wantedLen: 0x\0", env, r.regLen, (uint64_t) len);
	}
	r.regs = zw;
	r.regLen = (unsigned long long) len;
}

JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_getRegistersLen(
		JNIEnv *env, jobject caller) {
	jclass cls = (*env)->FindClass(env, "de/hechler/patrick/codesprachen/primitive/runtime/PrimitiveRuntime");
	jfieldID fieldID = (*env)->GetFieldID(env, caller,"values","J");
	jlong field = (*env)->GetLongField(env, cls, fieldID);
	runtime r = ((runtime*) field)[0];
	return r.regLen;
}

JNIEXPORT void JNICALL throwIndexOutOfBounds(uint64_t index, runtime r, JNIEnv *env, uint64_t len) {
	jclass cls = (*env)->FindClass(env,	"java/lang/IndexOutOfBoundsException");
	char *msg1 = "index out of bounds: min=0x00000000 max=0x";//27
	char* msg2 = "index=0x";//8
	char* msg = (char*)malloc(sizeof (char) * 68);//27+8+1+16+16=68strcpy(msg, msg1);
	strcpy(msg, msg1);
	lltoa(index, msg + 25, 16);
	strcpy(msg + 25, msg2);
	lltoa(index, msg + 49, 16); //25+16+2+6=49
	jmethodID mid = (*env)->GetMethodID(env, cls, "<init>", "V(Ljava/lang/String)");
	jobject exep = (*env)->NewObject(env, cls, mid, "java/lang/IndexOutOfBoundsException");
	(*env)->Throw(env, exep);
}

JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_setRegister(
		JNIEnv *env, jobject caller, jlong index, jlong val) {
	jclass cls = (*env)->FindClass(env, "de/hechler/patrick/codesprachen/primitive/runtime/PrimitiveRuntime");
	jfieldID fieldID = (*env)->GetFieldID(env, caller,"values","J");
	jlong zw = (*env)->GetLongField(env, cls,fieldID);
	runtime r = ((runtime*) zw)[0];
	if (((uint64_t)index) > r.regLen) {
		throwIndexOutOfBounds((uint64_t)index, r, env, r.regLen);
	}
	uint64_t i = (uint64_t) index;
	r.regs[i] = val;
}

JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_getRegister(
		JNIEnv *env, jobject caller, jlong index) {
	jclass cls = (*env)->FindClass(env, "de/hechler/patrick/codesprachen/primitive/runtime/PrimitiveRuntime");
	jfieldID fieldID = (*env)->GetFieldID(env, caller,"values","J");
	jlong zw = (*env)->GetLongField(env, cls,fieldID);
	runtime r = ((runtime*) zw)[0];
	if (((uint64_t)index) > r.regLen) {
		throwIndexOutOfBounds((uint64_t)index, r, env, r.regLen);
	}
	return r.regs[(unsigned long long) index];
}

JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_setStackMaxSize(
		JNIEnv *env, jobject caller, jlong size) {
	jclass cls = (*env)->FindClass(env, "de/hechler/patrick/codesprachen/primitive/runtime/PrimitiveRuntime");
	jfieldID fieldID = (*env)->GetFieldID(env, caller,"values","J");
	jlong field = (*env)->GetLongField(env, cls,fieldID);
	runtime r = ((runtime*) field)[0];
	int64_t* zw = realloc(r.stack, sizeof(int64_t) * size);
	if (zw == NULL){
		throwOutOfMemory("stack would need to much memory! beforeMaxSize: 0x\0 wantedMaxSize: 0x\0", env, r.stackMaxSize, (uint64_t) size);
	}
	r.stack = zw;
	r.stackMaxSize = (unsigned long long) size;
	if (r.stackSize > r.stackMaxSize){
		r.stackSize = r.stackMaxSize;
	}
}

JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_getStackMaxSize(
		JNIEnv *env, jobject caller) {
	jclass cls = (*env)->FindClass(env, "de/hechler/patrick/codesprachen/primitive/runtime/PrimitiveRuntime");
	jfieldID fieldID = (*env)->GetFieldID(env, caller,"values","J");
	jlong field = (*env)->GetLongField(env, cls,fieldID);
	runtime r = ((runtime*) field)[0];
	return r.stackMaxSize;
}

JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_getStackSize(
		JNIEnv *env, jobject caller) {
	jclass cls = (*env)->FindClass(env, "de/hechler/patrick/codesprachen/primitive/runtime/PrimitiveRuntime");
	jfieldID fieldID = (*env)->GetFieldID(env, caller,"values","J");
	jlong field = (*env)->GetLongField(env, cls,fieldID);
	runtime r = ((runtime*) field)[0];
	return r.stackSize;
}

JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_push(
		JNIEnv *env, jobject caller, jlong val) {
	jclass cls = (*env)->FindClass(env, "de/hechler/patrick/codesprachen/primitive/runtime/PrimitiveRuntime");
	jfieldID fieldID = (*env)->GetFieldID(env, caller,"values","J");
	jlong field = (*env)->GetLongField(env, cls,fieldID);
	runtime r = ((runtime*) field)[0];
	if (r.stackSize >= r.stackMaxSize){
		throwIndexOutOfBounds(r.stackSize, r, env, val);
	}
	r.stack[r.stackSize++] = val;
}

JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_pop(
		JNIEnv *env, jobject caller) {
	jclass cls = (*env)->FindClass(env, "de/hechler/patrick/codesprachen/primitive/runtime/PrimitiveRuntime");
	jfieldID fieldID = (*env)->GetFieldID(env, caller,"values","J");
	jlong field = (*env)->GetLongField(env, cls,fieldID);
	runtime r = ((runtime*) field)[0];
	if (r.stackSize >= r.stackMaxSize){
		throwIndexOutOfBounds(r.stackSize, r, env, r.stackMaxSize);
	}
	return 0;
}

JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_finalize
  (JNIEnv *env, jobject caller){
	jclass cls = (*env)->FindClass(env, "de/hechler/patrick/codesprachen/primitive/runtime/PrimitiveRuntime");
	jfieldID fieldID = (*env)->GetFieldID(env, caller,"values","J");
	jlong field = (*env)->GetLongField(env, cls,fieldID);
	runtime r = ((runtime*) field)[0];
	free(r.cmds);
	free(r.stack);
	free(r.regs);
	long long ll = (long long) &r;
	free((void*) ll);
}
