#!/bin/bash
#NDK目录
NDK=/d/Android/Sdk/ndk/21.4.7075529

#工具路径
HOST_TAG=windows-x86_64
TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/$HOST_TAG

#安装目录
ANDROID_LIB_PATH="$(pwd)/install.dir"

ANDROID_API_VERSION=21

#https://github.com/moorle/lame-android-build/blob/master/build-lame.sh
TARGET_ARCH_ABI="armeabi-v7a arm64-v8a"

function build_android_arm
{
echo "build for android $CPU"
./configure \
--host=$HOST \
--disable-shared \
--disable-frontend \
--enable-static \
--prefix="$ANDROID_LIB_PATH/$ABI"
make clean
make -j8
make install
echo "building for android $CPU completed"
}

for ABI in $TARGET_ARCH_ABI
do
	if [ $ABI = "armeabi-v7a" ]; then
		CPU=armv7-a
		HOST=arm-linux-androideabi
		HOST2=armv7a-linux-androideabi
	elif [ $ABI = "arm64-v8a" ]; then
		CPU=armv8-a
		HOST=aarch64-linux-android
		HOST2=aarch64-linux-android
	fi
	export AR=$TOOLCHAIN/bin/$HOST-ar
	export AS=$TOOLCHAIN/bin/$HOST-as
	export LD=$TOOLCHAIN/bin/$HOST-ld
	export RANLIB=$TOOLCHAIN/bin/$HOST-ranlib
	export STRIP=$TOOLCHAIN/bin/$HOST-strip
	export CC=$TOOLCHAIN/bin/$HOST2$ANDROID_API_VERSION-clang
	export CXX=$TOOLCHAIN/bin/$HOS2T$ANDROID_API_VERSION-clang++
	OPTIMIZE_CFLAGS="-march=$CPU"
	export CFLAGS="-Os -fpic -fdeclspec $OPTIMIZE_CFLAGS -D__ANDROID_API__=$ANDROID_API_VERSION"
	export CPPFLAGS="-Os -fpic -fdeclspec $OPTIMIZE_CFLAGS -D__ANDROID_API__=$ANDROID_API_VERSION"
	build_android_arm
done
