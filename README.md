# 项目练习 - Android录制PCM及转换为MP3

将《音视频开发进展指南-基于Android与iOS平台的实践》书中的[Android-Mp3Encoder](https://github.com/zhanxiaokai/Android-Mp3Encoder)和[Android-AudioRecorder](https://github.com/zhanxiaokai/Android-AudioRecorder)这两个项目结合起来，使用Android Studio Dolphin和MSYS2环境。

有一些修改，具体如下，

- 更新了lame的[build.sh](./app/src/main/jni/3rdparty/lame/build.sh)，支持armeabi-v7a和arm64-v8a两种ABI
  - 需要修改变量NDK、HOST_TAG和ANDROID_API_VERSION来适应你的机器和环境
  - 官方文档
    - [Use the NDK with other build systems](https://developer.android.google.cn/ndk/guides/other_build_systems)

- 使用cmake或ndk-build编译，默认是cmake，并支持自动运行（在[app/build.gradle](./app/build.gradle)中配置）
  - [将 Gradle 关联到您的原生库](https://developer.android.com/studio/projects/gradle-external-native-builds?hl=zh-cn)
  - cmake官方文档
    - [ExternalNativeCmakeOptions](https://developer.android.google.cn/reference/tools/gradle-api/7.1/com/android/build/api/dsl/ExternalNativeCmakeOptions)
    - [CMake](https://developer.android.google.cn/reference/tools/gradle-api/7.1/com/android/build/api/dsl/Cmake)
  - ndk-build官方文档
    - [ExternalNativeNdkBuildOptions](https://developer.android.google.cn/reference/tools/gradle-api/7.1/com/android/build/api/dsl/ExternalNativeNdkBuildOptions)
    - [NdkBuild](https://developer.android.google.cn/reference/tools/gradle-api/7.1/com/android/build/api/dsl/NdkBuild)

- 增加了LameMP3Encoder需要的PCM源为单通道的支持，当然这样转换后的MP3也是单通道的；增加lame_encode_flush处理

- 录制的PCM和转换后的MP3文件在应用专属存储空间/data/data/com.phuket.tour.audiorecorder/files目录下，需要使用Device File Explorer来导出
