LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LIBTCD_SRC= \
    tcd/tide_db.c \
    tcd/bit_pack.c

LOCAL_MODULE           := libtcd
LOCAL_CFLAGS           := -O2 -Ijni/tcd -Wno-missing-field-initializers
LOCAL_SRC_FILES        := $(LIBTCD_SRC)

include $(BUILD_STATIC_LIBRARY)