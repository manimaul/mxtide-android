#include "JniFloat.h"
#include "Jni.h"

jclass mdr::JniFloat::classId = nullptr;
jmethodID mdr::JniFloat::valueOfMethodId = nullptr;

jobject mdr::JniFloat::toJni(JNIEnv *env, float number) {
    jfloat jNumber = static_cast<jfloat>(number);
    jobject retVal = env->CallStaticObjectMethod(classId, valueOfMethodId, jNumber);
    Jni::checkExceptionAndClear(env);
    return retVal;
}

void mdr::JniFloat::registerNative(JNIEnv *env) {
    // https://developer.android.com/training/articles/perf-jni
    auto cId = Jni::findJavaClass(env, "java/lang/Float");
    classId = reinterpret_cast<jclass>(env->NewGlobalRef(cId));
    valueOfMethodId = env->GetStaticMethodID(classId, "valueOf", "(F)Ljava/lang/Float;");
}
