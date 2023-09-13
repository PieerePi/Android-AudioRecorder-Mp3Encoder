#include "../include/com_phuket_tour_studio_LameMp3Encoder.h"
#include "../include/lame_mp3_encoder.h"
//#include "../../common/CommonTools.h"
#include <android/log.h>

#define LOG_TAG "LameMp3Encoder"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

LameMp3Encoder *encoder = NULL;

JNIEXPORT jint JNICALL Java_com_phuket_tour_studio_LameMp3Encoder_init
        (JNIEnv *env, jobject obj, jstring pcmPathParam, jint channels, jint bitRate,
         jint sampleRate, jstring mp3PathParam) {
    const char *pcmPath = env->GetStringUTFChars(pcmPathParam, NULL);
    const char *mp3Path = env->GetStringUTFChars(mp3PathParam, NULL);
    LOGI("mp3Path is %s...", mp3Path);
    encoder = new LameMp3Encoder();
    encoder->Init(pcmPath, mp3Path, sampleRate, channels, bitRate);
    env->ReleaseStringUTFChars(mp3PathParam, mp3Path);
    env->ReleaseStringUTFChars(pcmPathParam, pcmPath);
    return 0;
}

JNIEXPORT void JNICALL Java_com_phuket_tour_studio_LameMp3Encoder_encode(JNIEnv *env, jobject obj) {
    if (NULL != encoder) {
        encoder->Encode();
    }
}

JNIEXPORT void JNICALL
Java_com_phuket_tour_studio_LameMp3Encoder_destroy(JNIEnv *env, jobject obj) {
    if (NULL != encoder) {
        encoder->Destroy();
        delete encoder;
        encoder = NULL;
    }
}
