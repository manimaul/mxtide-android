#pragma once

#include <jni.h>
#include "JniTidesAndCurrents.h"

/*
 * How to find java class,field and method signatures:
 * $unzip /Library/Java/JavaVirtualMachines/jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar
 * $javap -s ./java/lang/Object.class
 *
 * http://docs.oracle.com/javase/7/docs/technotes/guides/jni/spec/types.html
 */
namespace mdr {
    class Jni {

    public:
        static jclass findJavaClass(JNIEnv *env, const char *name);

        static void checkExceptionAndClear(JNIEnv *env);

        static void checkException(JNIEnv *env, bool terminal = true);
    };
}
