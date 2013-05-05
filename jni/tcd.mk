LOCAL_PATH := $(call my-dir)

LIBTCD_SRC= \
	tcd/tide_db.c \
	tcd/bit_pack.c 

include $(CLEAR_VARS)

LOCAL_MODULE    := libtcd
LOCAL_CFLAGS    := -O2 -Ijni/tcd -Wno-missing-field-initializers
LOCAL_SRC_FILES := $(LIBTCD_SRC)
LOCAL_STATIC_LIBRARIES := cpufeatures

include $(BUILD_STATIC_LIBRARY)