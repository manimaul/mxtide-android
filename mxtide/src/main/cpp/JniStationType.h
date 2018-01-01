#pragma once

#include <jni.h>
#include <StationType.h>
#include "JniString.h"

namespace mdr {
    static StationType stationTypeFromJavaString(JNIEnv *env, jstring str) {
        if (mdr::JniString::fromJni(env, str) == "tide") {
            return stationTypeTide;
        } else {
            return stationTypeCurrent;
        }
    }
}

