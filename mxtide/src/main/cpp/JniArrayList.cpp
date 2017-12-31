#include "JniArrayList.h"
#include "Jni.h"

jclass mdr::JniArrayList::arrayListClass = nullptr;
jmethodID mdr::JniArrayList::arrayListCtor = nullptr;
jmethodID mdr::JniArrayList::arrayListMethodAdd = nullptr;

void mdr::JniArrayList::registerNative(JNIEnv *env) {
    arrayListClass = mdr::Jni::findJavaClass(env, "java/util/ArrayList");
    arrayListCtor = env->GetMethodID(arrayListClass, "<init>", "(I)V");
    arrayListMethodAdd = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
}

mdr::JniArrayList::JniArrayList(JNIEnv *env, size_t size) {
    arrayList = env->NewObject(arrayListClass, arrayListCtor, size);
}

bool mdr::JniArrayList::add(JNIEnv *env, jobject object) {
    return env->CallBooleanMethod(arrayList, arrayListMethodAdd, object);
}

const jobject mdr::JniArrayList::getArrayList() const {
    return arrayList;
}

