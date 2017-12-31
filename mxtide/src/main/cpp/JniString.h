#pragma once

#include <string>
#include <jni.h>

namespace mdr {
    class JniString {

    public:
        static void registerNative(JNIEnv *env);

        static std::string fromJni(JNIEnv *env, jstring str);

        static jstring toJni(JNIEnv *env, std::string &str);

    };
}
