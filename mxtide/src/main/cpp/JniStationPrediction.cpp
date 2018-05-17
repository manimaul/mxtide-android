#include "JniStationPrediction.h"
#include "JniString.h"
#include "Jni.h"
#include "JniFloat.h"

jclass mdr::JniStationPrediction::stationPredictionFactoryClass = nullptr;
jmethodID mdr::JniStationPrediction::factoryCtor = nullptr;

void mdr::JniStationPrediction::registerNative(JNIEnv *env) {
    const char *companionClassName = "com/mxmariner/mxtide/internal/StationPredictionFactory";
    stationPredictionFactoryClass = mdr::Jni::findJavaClass(env, companionClassName);
    mdr::Jni::checkException(env, true);
    const char *factorySignature = "(JLjava/lang/String;Ljava/lang/Object;)Lcom/mxmariner/mxtide/internal/StationPrediction;";
    factoryCtor = env->GetStaticMethodID(stationPredictionFactoryClass, "createPrediction",
                                   factorySignature);
    mdr::Jni::checkException(env, true);
}

jobject mdr::JniStationPrediction::createJniStationPrediction(JNIEnv *env,
                                                              TimePoint timePoint,
                                                              float value,
                                                              std::string timeZone) {
    auto duration = timePoint.time_since_epoch();
    auto epoch = std::chrono::duration_cast<std::chrono::milliseconds>(duration).count();
    jlong jniEpoch = static_cast<jlong>(epoch);
    jobject jniValue = mdr::JniFloat::toJni(env, value);
    jstring tz = mdr::JniString::toJni(env, timeZone);
    auto retVal = env->CallStaticObjectMethod(stationPredictionFactoryClass, factoryCtor, jniEpoch,
                                              tz, jniValue);
    mdr::Jni::checkException(env, true);
    return retVal;
}
