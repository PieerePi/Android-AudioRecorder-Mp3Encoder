LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include

LOCAL_SRC_FILES := ./lib/$(TARGET_ARCH_ABI)/libmp3lame.a

LOCAL_MODULE := mp3lame
include $(PREBUILT_STATIC_LIBRARY)