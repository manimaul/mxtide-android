#include <jni.h>
#include <codecvt>
#include <locale>
#include "JniArrayList.h"
#include "JniString.h"
#include "JniStationType.h"
#include "Jni.h"
#include <TidesAndCurrents.h>
#include <algorithm>

jlongArray pointers(JNIEnv *env, std::vector<mdr::Station> &ptrs, jint limit) {
    static_cast<size_t >(limit);
    size_t size;
    if (limit > 0) {
        size = std::min(ptrs.size(), static_cast<size_t >(limit));
    } else {
        size = ptrs.size();
    }
    std::vector<jlong> javaLongs;
    size_t diff = ptrs.size() - size;

    auto end = limit > 0 ? ptrs.end() - diff : ptrs.end();
    std::transform(ptrs.begin(), end, std::back_inserter(javaLongs), [](mdr::Station &stn) -> jlong {
        mdr::Station *station = new mdr::Station(stn);
        return reinterpret_cast<jlong>(station);
    });

    jsize javaSize = static_cast<jsize>(size);
    jlongArray retVal = env->NewLongArray(javaSize);
    env->SetLongArrayRegion(retVal, 0, javaSize, javaLongs.data());
    return retVal;
}

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

JNIEXPORT jlongArray JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_findNearestStations(JNIEnv *env,
                                                                        jclass clazz,
                                                                        jlong ptr,
                                                                        jdouble lat,
                                                                        jdouble lng,
                                                                        jstring type,
                                                                        jint limit) {
    mdr::TidesAndCurrents *tidesAndCurrents = reinterpret_cast<mdr::TidesAndCurrents *>(ptr);
    auto stationType = mdr::stationTypeFromJavaString(env, type);
    auto stations = tidesAndCurrents->findNearestStations(static_cast<double>(lat),
                                                          static_cast<double>(lng),
                                                          stationType);
    return pointers(env, stations, limit);
}

JNIEXPORT jlongArray JNICALL
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
    return pointers(env, values, 0);
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
    return pointers(env, values, 0);
}

}