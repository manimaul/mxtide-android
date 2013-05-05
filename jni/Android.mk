LOCAL_PATH := $(call my-dir)

include jni/tcd.mk
include jni/xtide.mk

include $(CLEAR_VARS)

LOCAL_MODULE    := AndXTideLib
LOCAL_SRC_FILES := AndXTideLib.cpp
LOCAL_STATIC_LIBRARIES := libxtide tcd

include $(BUILD_SHARED_LIBRARY)
