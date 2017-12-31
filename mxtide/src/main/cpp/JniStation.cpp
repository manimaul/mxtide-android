#include <jni.h>
#include "JniStation.h"
#include "../Station.h"
#include "JniString.h"
#include "JniArrayList.h"
#include "JniStationPrediction.h"

extern "C" {


JNIEXPORT jdouble JNICALL
Java_com_mxmariner_mxtide_internal_Station_latitude(JNIEnv *env,
                                                    jclass obj,
                                                    jlong ptr) {
    mdr::Station *station = reinterpret_cast<mdr::Station *>(ptr);
    return station->getLatitude();
}

JNIEXPORT jdouble JNICALL
Java_com_mxmariner_mxtide_internal_Station_longitude(JNIEnv *env,
                                                     jclass obj,
                                                     jlong ptr) {
    mdr::Station *station = reinterpret_cast<mdr::Station *>(ptr);
    return station->getLongitude();
}

JNIEXPORT jstring JNICALL
Java_com_mxmariner_mxtide_internal_Station_timeZone(JNIEnv *env,
                                                    jclass obj,
                                                    jlong ptr) {
    mdr::Station *station = reinterpret_cast<mdr::Station *>(ptr);
    auto timeZone = station->timeZone();
    return mdr::JniString::toJni(env, timeZone);
}

JNIEXPORT jstring JNICALL
Java_com_mxmariner_mxtide_internal_Station_name(JNIEnv *env,
                                                jclass obj,
                                                jlong ptr) {
    mdr::Station *station = reinterpret_cast<mdr::Station *>(ptr);
    auto name = station->name();
    return mdr::JniString::toJni(env, name);
}

JNIEXPORT jstring JNICALL
Java_com_mxmariner_mxtide_internal_Station_type(JNIEnv *env,
                                                jclass obj,
                                                jlong ptr) {
    mdr::Station *station = reinterpret_cast<mdr::Station *>(ptr);
    auto value = station->type().toString();
    return mdr::JniString::toJni(env, value);
}

JNIEXPORT jobject JNICALL
Java_com_mxmariner_mxtide_internal_Station_getPredictionRaw(JNIEnv *env,
                                                            jclass obj,
                                                            jlong ptr,
                                                            jlong epoch,
                                                            jlong duration,
                                                            jstring units) {
    auto station = reinterpret_cast<mdr::Station *>(ptr);
    auto dur = DurationSeconds(duration);
    TimePoint timePoint { DurationSeconds(epoch) };
    auto unitsStr = mdr::JniString::fromJni(env, units);
    auto measureUnit = mdr::MeasureUnitFromString(unitsStr);
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

JNIEXPORT jobject JNICALL
Java_com_mxmariner_mxtide_internal_Station_getPredictionPlain(JNIEnv *env,
                                                              jclass obj,
                                                              jlong ptr,
                                                              jlong epoch,
                                                              jlong duration,
                                                              jstring units) {
    auto station = reinterpret_cast<mdr::Station *>(ptr);
    auto dur = DurationSeconds(duration);
    TimePoint timePoint { DurationSeconds(epoch) };
    auto unitsStr = mdr::JniString::fromJni(env, units);
    auto measureUnit = mdr::MeasureUnitFromString(unitsStr);
    auto prediction = station->getPredictionPlain(timePoint, dur, measureUnit);
    auto jniPredictionList = mdr::JniArrayList(env, prediction.size());
    auto tz = station->timeZone();
    auto lambda = [&jniPredictionList, &tz, env](mdr::StationPrediction<std::string> &each) {
        jobject jniPrediction = mdr::JniStationPrediction::createJniStationPrediction(env,
                                                                                      each.timePoint,
                                                                                      each.value,
                                                                                      tz);
        jniPredictionList.add(env, jniPrediction);
    };
    std::for_each(prediction.begin(), prediction.end(), lambda);
    return jniPredictionList.getArrayList();
}

JNIEXPORT jobject JNICALL
Java_com_mxmariner_mxtide_internal_Station_getPredictionClockSVG(JNIEnv *env,
                                                                 jclass obj,
                                                                 jlong ptr,
                                                                 jlong epoch,
                                                                 jlong duration,
                                                                 jstring units) {
    auto station = reinterpret_cast<mdr::Station *>(ptr);
    auto dur = DurationSeconds(duration);
    TimePoint timePoint { DurationSeconds(epoch) };
    auto unitsStr = mdr::JniString::fromJni(env, units);
    auto measureUnit = mdr::MeasureUnitFromString(unitsStr);
    auto svg = station->getPredictionClockSVG(timePoint, dur, measureUnit);
    return mdr::JniString::toJni(env, svg);
}

JNIEXPORT void JNICALL
Java_com_mxmariner_mxtide_internal_Station_deleteStation(JNIEnv *env,
                                                         jclass obj,
                                                         jlong ptr) {
    auto station = reinterpret_cast<mdr::Station *>(ptr);
    //todo:
    delete station;
}

}