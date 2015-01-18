LOCAL_PATH := $(call my-dir)

include jni/xtide.mk

include $(CLEAR_VARS)

LOCAL_MODULE    := AndXTideLib
LOCAL_SRC_FILES := AndXTideLib.cpp 

LOCAL_CPP_EXTENSION    := .cc .cpp
LOCAL_CFLAGS           := -O2 -Ijni/xtide -Wno-missing-field-initializers
LOCAL_CPPFLAGS         += -std=gnu++11
LOCAL_STATIC_LIBRARIES += xtide

include $(BUILD_SHARED_LIBRARY)