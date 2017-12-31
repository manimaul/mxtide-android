#include "JniFloat.h"
#include "Jni.h"

jclass mdr::JniFloat::classId = nullptr;
jmethodID mdr::JniFloat::valueOfMethodId = nullptr;

void mdr::JniFloat::registerNative(JNIEnv *env) {
    classId = Jni::findJavaClass(env, "java/lang/Float");
    valueOfMethodId = env->GetStaticMethodID(classId, "valueOf", "(F)Ljava/lang/Float;");
}

jobject mdr::JniFloat::toJni(JNIEnv *env, float number) {
    jfloat jNumber = static_cast<jfloat>(number);
    jobject retVal = env->CallStaticObjectMethod(classId, valueOfMethodId, jNumber);
    Jni::checkExceptionAndClear(env);
    return retVal;
}
