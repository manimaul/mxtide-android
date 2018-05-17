#include <exception>
#include "Jni.h"
#include "JniArrayList.h"
#include "JniString.h"
#include "JniStationPrediction.h"
#include "JniFloat.h"

void mdr::Jni::checkException(JNIEnv *env, bool terminal) {
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        if (terminal) {
            std::terminate();
        }
    }
}

void mdr::Jni::checkExceptionAndClear(JNIEnv *env) {
    checkException(env, false);
    env->ExceptionClear();
}

jclass mdr::Jni::findJavaClass(JNIEnv *env, const char *name) {
    jclass localRef = env->FindClass(name);
    jclass globalRef = (jclass) env->NewGlobalRef(localRef);
    env->DeleteLocalRef(localRef);
    return globalRef;
}

extern "C" {

JNIEXPORT jint
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    mdr::JniArrayList::registerNative(env);
    mdr::JniString::registerNative(env);
    mdr::JniStationPrediction::registerNative(env);
    mdr::JniFloat::registerNative(env);
    return JNI_VERSION_1_6;
}

}
