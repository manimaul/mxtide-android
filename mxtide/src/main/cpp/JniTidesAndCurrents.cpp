#include <jni.h>
#include <codecvt>
#include <locale>
#include "JniArrayList.h"
#include "JniString.h"
#include "JniStationType.h"
#include "JniLong.h"
#include <TidesAndCurrents.h>

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_create(JNIEnv *env,
                                                           jclass clazz) {
    return reinterpret_cast<jlong>(new mdr::TidesAndCurrents());
}

JNIEXPORT void JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_delete(JNIEnv *env,
                                                           jclass clazz,
                                                           jlong ptr) {
    delete reinterpret_cast<mdr::TidesAndCurrents *>(ptr);
}

JNIEXPORT void JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_addHarmonicsFile(JNIEnv *env,
                                                                     jclass clazz,
                                                                     jlong ptr,
                                                                     jstring pPath) {
    const char *path = env->GetStringUTFChars(pPath, NULL);
    mdr::TidesAndCurrents *tidesAndCurrents = reinterpret_cast<mdr::TidesAndCurrents *>(ptr);
    tidesAndCurrents->addHarmonicsFile(path);
    env->ReleaseStringUTFChars(pPath, path);
}

JNIEXPORT jint JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_stationCount(JNIEnv *env,
                                                                 jclass clazz,
                                                                 jlong ptr) {
    mdr::TidesAndCurrents *tidesAndCurrents = reinterpret_cast<mdr::TidesAndCurrents *>(ptr);
    return static_cast<jint>(tidesAndCurrents->stationCount());
}

JNIEXPORT jobject JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_stationNames(JNIEnv *env,
                                                                 jclass clazz,
                                                                 jlong ptr) {
    mdr::TidesAndCurrents *tidesAndCurrents = reinterpret_cast<mdr::TidesAndCurrents *>(ptr);
    auto names = tidesAndCurrents->stationNames();
    auto count = names.size();
    auto arrayList = mdr::JniArrayList(env, count);
    for (int i = 0; i < count; ++i) {
        auto javaName = mdr::JniString::toJni(env, names[i]);
        arrayList.add(env, javaName);
        env->DeleteLocalRef(javaName);
    }
    return arrayList.getArrayList();
}

JNIEXPORT jlong JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_findStationByName(JNIEnv *env,
                                                                      jclass clazz,
                                                                      jlong ptr,
                                                                      jstring name) {
    auto stationName = mdr::JniString::fromJni(env, name);
    mdr::TidesAndCurrents *tidesAndCurrents = reinterpret_cast<mdr::TidesAndCurrents *>(ptr);
    auto station = tidesAndCurrents->findStationByName(stationName.c_str());
    jlong retVal = 0;
    station.let([&retVal](mdr::Station &stn) {
        mdr::Station *s = new mdr::Station(stn);
        retVal = reinterpret_cast<jlong>(s);
    });
    return retVal;
}

JNIEXPORT jlong JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_findNearestStation(JNIEnv *env,
                                                                       jclass clazz,
                                                                       jlong ptr,
                                                                       jdouble lat,
                                                                       jdouble lng,
                                                                       jstring type) {
    mdr::TidesAndCurrents *tidesAndCurrents = reinterpret_cast<mdr::TidesAndCurrents *>(ptr);
    auto stationType = mdr::stationTypeFromJavaString(env, type);
    auto station = tidesAndCurrents->findNearestStation(static_cast<double>(lat),
                                                        static_cast<double>(lng),
                                                        stationType);
    jlong retVal = 0;
    station.let([&retVal](mdr::Station &stn) {
        mdr::Station *s = new mdr::Station(stn);
        retVal = reinterpret_cast<jlong>(s);
    });
    return retVal;
}

JNIEXPORT jobject JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_findStationsInCircle(JNIEnv *env,
                                                                         jclass clazz,
                                                                         jlong nativePtr,
                                                                         jdouble lat,
                                                                         jdouble lng,
                                                                         jdouble radius,
                                                                         jstring type) {
    mdr::TidesAndCurrents *tidesAndCurrents = reinterpret_cast<mdr::TidesAndCurrents *>(nativePtr);
    mdr::StationType stationType = mdr::stationTypeFromJavaString(env, type);
    std::vector<mdr::Station> values = tidesAndCurrents->findStationsInCircle(lat, lng, radius, stationType);
    jobject retVal = nullptr;
    if (values.size() > 0) {
        auto jniList = mdr::JniArrayList(env, values.size());
        std::for_each(values.begin(), values.end(), [&jniList, env](mdr::Station &stn) {
            mdr::Station *s = new mdr::Station(stn);
            jobject jLong = mdr::JniLong::toJni(env, reinterpret_cast<long>(s));
            jniList.add(env, jLong);
        });
        retVal = jniList.getArrayList();
    }
    return retVal;
}


JNIEXPORT jobject JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_findStationsInBounds(JNIEnv *env,
                                                                         jclass clazz,
                                                                         jlong ptr,
                                                                         jdouble nLat,
                                                                         jdouble eLng,
                                                                         jdouble sLat,
                                                                         jdouble wLng,
                                                                         jstring type) {
    mdr::TidesAndCurrents *tidesAndCurrents = reinterpret_cast<mdr::TidesAndCurrents *>(ptr);
    auto stationType = mdr::stationTypeFromJavaString(env, type);
    std::vector<mdr::Station> values = tidesAndCurrents->findStationsInBounds(nLat, eLng, sLat, wLng, stationType);
    jobject retVal = nullptr;
    if (values.size() > 0) {
        auto jniList = mdr::JniArrayList(env, values.size());
        std::for_each(values.begin(), values.end(), [&jniList, env](mdr::Station &stn) {
            mdr::Station *s = new mdr::Station(stn);
            jobject jLong = mdr::JniLong::toJni(env, reinterpret_cast<long>(s));
            jniList.add(env, jLong);
        });
        retVal = jniList.getArrayList();
    }
    return retVal;
}

}