#include <jni.h>
#include "JniStation.h"
#include <Station.h>
#include "JniString.h"
#include "JniArrayList.h"
#include "JniStationPrediction.h"

static mdr::MeasureUnit measureUnitFromString(std::string &str) {
    if (str == "METRIC") {
        return mdr::MeasureUnit::metric;
    } else if (str == "STATUTE") {
        return mdr::MeasureUnit::statute;
    } else {
        return mdr::MeasureUnit::nautical;
    }
}

extern "C" {

JNIEXPORT jdouble JNICALL
Java_com_mxmariner_mxtide_internal_Station_latitude(JNIEnv *env,
                                                    jclass clazz,
                                                    jlong ptr) {
    mdr::Station *station = reinterpret_cast<mdr::Station *>(ptr);
    return station->getLatitude();
}

JNIEXPORT jdouble JNICALL
Java_com_mxmariner_mxtide_internal_Station_longitude(JNIEnv *env,
                                                     jclass clazz,
                                                     jlong ptr) {
    mdr::Station *station = reinterpret_cast<mdr::Station *>(ptr);
    return station->getLongitude();
}

JNIEXPORT jstring JNICALL
Java_com_mxmariner_mxtide_internal_Station_timeZone(JNIEnv *env,
                                                    jclass clazz,
                                                    jlong ptr) {
    mdr::Station *station = reinterpret_cast<mdr::Station *>(ptr);
    auto timeZone = station->timeZone();
    return mdr::JniString::toJni(env, timeZone);
}

JNIEXPORT jstring JNICALL
Java_com_mxmariner_mxtide_internal_Station_name(JNIEnv *env,
                                                jclass clazz,
                                                jlong ptr) {
    mdr::Station *station = reinterpret_cast<mdr::Station *>(ptr);
    auto name = station->name();
    return mdr::JniString::toJni(env, name);
}

JNIEXPORT jstring JNICALL
Java_com_mxmariner_mxtide_internal_Station_type(JNIEnv *env,
                                                jclass clazz,
                                                jlong ptr) {
    mdr::Station *station = reinterpret_cast<mdr::Station *>(ptr);
    auto value = station->type().toString();
    return mdr::JniString::toJni(env, value);
}

JNIEXPORT jobject JNICALL
Java_com_mxmariner_mxtide_internal_Station_getPredictionRaw(JNIEnv *env,
                                                            jclass clazz,
                                                            jlong ptr,
                                                            jlong epoch,
                                                            jlong duration,
                                                            jstring units) {
    auto station = reinterpret_cast<mdr::Station *>(ptr);
    auto dur = DurationSeconds(duration);
    TimePoint timePoint{DurationSeconds(epoch)};
    auto unitsStr = mdr::JniString::fromJni(env, units);
    auto measureUnit = measureUnitFromString(unitsStr);
    auto prediction = station->getPredictionRaw(timePoint, dur, measureUnit);
    auto jniPredictionList = mdr::JniArrayList(env, prediction.size());
    auto tz = station->timeZone();
    auto lambda = [&jniPredictionList, &tz, env](mdr::StationPrediction<float> &each) {
        jobject jniPrediction = mdr::JniStationPrediction::createJniStationPrediction(env,
                                                                                      each.timePoint,
                                                                                      each.value,
                                                                                      tz);
        jniPredictionList.add(env, jniPrediction);
    };
    std::for_each(prediction.begin(), prediction.end(), lambda);
    return jniPredictionList.getArrayList();
}

JNIEXPORT void JNICALL
Java_com_mxmariner_mxtide_internal_Station_deleteStation(JNIEnv *env,
                                                         jclass clazz,
                                                         jlong ptr) {
    auto station = reinterpret_cast<mdr::Station *>(ptr);
    //todo:
    delete station;
}

}
