cmake_minimum_required(VERSION 3.6) # 指定CMake的最低要求版本号

set(target audioencoder) # 设置环境变量的名称（target）及其取值（common）
project(${target}) # 指定项目的名称

include_directories(3rdparty/lame/include)
file(GLOB srcs mp3_encoder/src/*.cpp) # 把所有cpp文件和c文件都放入名称为srcs的集合中
file(GLOB hdrs mp3_encoder/include/*.h) # 把所有hpp文件和h文件都放入名称为hdrs的集合中

#if(${CMAKE_ANDROID_ARCH_ABI} MATCHES armeabi-v7a)
#    message("do it for armeabi-v7a")
#endif()
#if(${CMAKE_ANDROID_ARCH_ABI} MATCHES arm64-v8a)
#    message("do it for arm64-v8a")
#endif()
#message("do it 2 for ${CMAKE_ANDROID_ARCH_ABI}")
#message("${CMAKE_SOURCE_DIR}/3rdparty/lame/lib/${CMAKE_ANDROID_ARCH_ABI}/libmp3lame.a")

#add_library(${target} STATIC ${srcs} ${hdrs}) # 生成静态库（库文件的扩展名为a）
add_library(${target} SHARED ${srcs} ${hdrs}) # 生成动态库（共享库）（库文件的扩展名为so）
target_link_libraries(${target} log ${CMAKE_SOURCE_DIR}/3rdparty/lame/lib/${CMAKE_ANDROID_ARCH_ABI}/libmp3lame.a) # 指定要链接哪些库。log表示日志库