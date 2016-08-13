//
// Created by Timothy McCarthy on 2/08/2016.
//

#include <jni.h>
#include <stdio.h>

#include "au_id_tmm_senatedb_countengine_CountEngineInterface__.h"

JNIEXPORT void JNICALL Java_au_id_tmm_senatedb_countengine_CountEngineInterface_00024_count(JNIEnv *env, jobject thisObj, jint numCandidates , jintArray preferences, jintArray ballotIds) {
    printf("Hello World!\n");
    return;
}
