#include "JniLong.h"
#include "Jni.h"

jclass  mdr::JniLong::classId = nullptr;
jmethodID mdr::JniLong::valueOfMethodId = nullptr;

jobject mdr::JniLong::toJni(JNIEnv *env, long number) {
    jlong jNumber = static_cast<jlong>(number);
    jobject  retVal = env->CallStaticObjectMethod(classId, valueOfMethodId, jNumber);
    Jni::checkExceptionAndClear(env);
    return retVal;
}

void mdr::JniLong::registerNative(JNIEnv *env) {
    classId = Jni::findJavaClass(env, "java/lang/Long");
    valueOfMethodId = env->GetStaticMethodID(classId, "valueOf", "(J)Ljava/lang/Long;");

}
