/*
 * de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime.h
 *
 *  Created on: 07.07.2021
 *      Author: Patrick
 */

#ifndef SRC_DE_HECHLER_PATRICK_CODESPRACHEN_PRIMITIVE_RUNTIME_PRIMITIVERUNTIME_H_
#define SRC_DE_HECHLER_PATRICK_CODESPRACHEN_PRIMITIVE_RUNTIME_PRIMITIVERUNTIME_H_

#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime
 * Method:    create
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_create
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime
 * Method:    read
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_read
  (JNIEnv *, jobject, jstring);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime
 * Method:    setRegisterLen
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_setRegistersLen
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime
 * Method:    getRegisterLen
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_getRegistersLen
  (JNIEnv *, jobject);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime
 * Method:    setRegister
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_setRegister
  (JNIEnv *, jobject, jlong, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime
 * Method:    getRegister
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_getRegister
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime
 * Method:    setStackMaxSize
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_setStackMaxSize
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime
 * Method:    getStackMaxSize
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_getStackMaxSize
  (JNIEnv *, jobject);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime
 * Method:    getStackmaxSize
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_getStackSize
  (JNIEnv *, jobject);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime
 * Method:    getStackmaxSize
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_push
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime
 * Method:    getStackmaxSize
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_pop
  (JNIEnv *, jobject);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime
 * Method:    finalize
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_PrimitiveRuntime_finalize
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif


#endif /* SRC_DE_HECHLER_PATRICK_CODESPRACHEN_PRIMITIVE_RUNTIME_PRIMITIVERUNTIME_H_ */
