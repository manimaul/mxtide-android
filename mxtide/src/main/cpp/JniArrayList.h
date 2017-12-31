#pragma once

#include <jni.h>

namespace mdr {
    class JniArrayList {

    private:
        static jclass arrayListClass;
        static jmethodID arrayListCtor;
        static jmethodID arrayListMethodAdd;

        jobject arrayList;

    public:
        JniArrayList(JNIEnv *env, size_t size);

        bool add(JNIEnv *env, jobject object);

        const jobject getArrayList() const;

        static void registerNative(JNIEnv *env);
    };
}
