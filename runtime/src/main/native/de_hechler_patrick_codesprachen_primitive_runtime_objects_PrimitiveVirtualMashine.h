/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine */

#ifndef _Included_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
#define _Included_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    create
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_create
  (JNIEnv *, jobject);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    openfile
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_openfile
  (JNIEnv *, jobject, jstring);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    filelen
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_filelen
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    filepos
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_filepos
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setfilepos
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setfilepos
  (JNIEnv *, jobject, jlong, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    readfile
 * Signature: (JJJ)V
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_readfile
  (JNIEnv *, jobject, jlong, jlong, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    closefile
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_closefile
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    malloc
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_malloc
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    realloc
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_realloc
  (JNIEnv *, jobject, jlong, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_free
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    execute
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_execute
  (JNIEnv *, jobject);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setInstructionPointer
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setInstructionPointer
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    setStackPointer
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_setStackPointer
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    push
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_push
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    pop
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_pop
  (JNIEnv *, jobject);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    get
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_get
  (JNIEnv *, jobject, jlong);

/*
 * Class:     de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine
 * Method:    set
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_de_hechler_patrick_codesprachen_primitive_runtime_objects_PrimitiveVirtualMashine_set
  (JNIEnv *, jobject, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif
