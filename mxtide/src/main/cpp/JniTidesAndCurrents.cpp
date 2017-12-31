#include <jni.h>
#include <codecvt>
#include <locale>
#include "JniArrayList.h"
#include "JniString.h"
#include "../TidesAndCurrents.h"

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_create(JNIEnv *env,
                                                           jclass obj) {
    return reinterpret_cast<jlong>(new mdr::TidesAndCurrents());
}

JNIEXPORT void JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_delete(JNIEnv *env,
                                                           jclass obj,
                                                           jlong ptr) {
    delete reinterpret_cast<mdr::TidesAndCurrents *>(ptr);
}

JNIEXPORT void JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_addHarmonicsFile(JNIEnv *env,
                                                                     jclass type,
                                                                     jlong ptr,
                                                                     jstring pPath) {
    const char *path = env->GetStringUTFChars(pPath, NULL);
    mdr::TidesAndCurrents *tidesAndCurrents = reinterpret_cast<mdr::TidesAndCurrents *>(ptr);
    tidesAndCurrents->addHarmonicsFile(path);
    env->ReleaseStringUTFChars(pPath, path);
}

JNIEXPORT jint JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_stationCount(JNIEnv *env,
                                                                 jclass type,
                                                                 jlong ptr) {
    mdr::TidesAndCurrents *tidesAndCurrents = reinterpret_cast<mdr::TidesAndCurrents *>(ptr);
    return static_cast<jint>(tidesAndCurrents->stationCount());
}

JNIEXPORT jobject JNICALL
Java_com_mxmariner_mxtide_internal_TidesAndCurrents_stationNames(JNIEnv *env,
                                                                 jclass type,
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
                                                                      jclass type,
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

}