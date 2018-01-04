#pragma once

#include <jni.h>

namespace mdr {

    class JniLong {
    private:
        static jclass classId;
        static jmethodID valueOfMethodId;

    public:
        static void registerNative(JNIEnv *env);

        static jobject toJni(JNIEnv *env, long number);
    };

}
