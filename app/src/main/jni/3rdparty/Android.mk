LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := mp3lame

LOCAL_SRC_FILES := ./lame/lib/libmp3lame.a

include $(PREBUILT_STATIC_LIBRARY)