#include "JniString.h"
#include "Jni.h"

jclass classId = nullptr;
jmethodID ctorMethodId = nullptr;
jobject utf8 = nullptr;

std::string mdr::JniString::fromJni(JNIEnv *env, jstring str) {
    const char *string = env->GetStringUTFChars(str, 0);
    auto sstring = std::string {string};
    env->ReleaseStringUTFChars(str, string);
    return sstring;
}

jstring mdr::JniString::toJni(JNIEnv *env, std::string &str) {
    auto size = static_cast<jsize >(str.size());
    jbyteArray jniByteArray = env->NewByteArray(size);
    auto jbytes = reinterpret_cast<const jbyte *>(str.c_str());
    env->SetByteArrayRegion(jniByteArray, 0, size, jbytes);
    jobject jniStr = env->NewObject(classId, ctorMethodId, jniByteArray, utf8);
    Jni::checkException(env, true);
    env->DeleteLocalRef(jniByteArray);
    return static_cast<jstring>(jniStr);
}

void mdr::JniString::registerNative(JNIEnv *env) {
    classId = Jni::findJavaClass(env, "java/lang/String");
    ctorMethodId = env->GetMethodID(classId, "<init>", "([BLjava/lang/String;)V");
    auto localUtf8 = env->NewStringUTF("UTF-8");
    utf8 = env->NewGlobalRef(localUtf8);
    env->DeleteLocalRef(localUtf8);
}


