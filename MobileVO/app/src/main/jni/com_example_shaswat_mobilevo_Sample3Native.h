//
// Created by shaswat on 2/5/16.
//
#include <jni.h>

#ifndef MOBILEVO_COM_EXAMPLE_SHASWAT_MOBILEVO_SAMPLE3NATIVE_H
#define MOBILEVO_COM_EXAMPLE_SHASWAT_MOBILEVO_SAMPLE3NATIVE_H

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_shaswat_mobilevo_LibVisodo
 * Method:    start
 * Signature: (JJI)Ljava/lang/String;
 */
JNIEXPORT void JNICALL Java_com_example_shaswat_mobilevo_Sample3Native_FindFeatures(JNIEnv*, jobject, jlong im1, jlong im2, jlong im3, jint no_images);

#ifdef __cplusplus
}
#endif
#endif


