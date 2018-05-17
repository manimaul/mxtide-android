#pragma once

#include <jni.h>
#include <StationPrediction.h>

namespace mdr {
    class JniStationPrediction {

    private:
        static jclass stationPredictionFactoryClass;

        static jmethodID factoryCtor;

    public:
        static void registerNative(JNIEnv *env);

        static jobject
        createJniStationPrediction(JNIEnv *env,
                                   TimePoint timePoint,
                                   float value,
                                   std::string timeZone);
    };
}

