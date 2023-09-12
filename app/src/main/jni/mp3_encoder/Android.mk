LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

#LOCAL_ARM_MODE := arm
#LOCAL_C_INCLUDES := $(LOCAL_PATH)/../3rdparty/lame/include
LOCAL_CFLAGS := -DHAVE_CONFIG_H -DFPM_ARM -ffast-math -O3

LOCAL_SRC_FILES := ./src/mp3_encoder.cpp

LOCAL_STATIC_LIBRARIES := mp3lame

LOCAL_MODULE := mp3_encoder
include $(BUILD_STATIC_LIBRARY)