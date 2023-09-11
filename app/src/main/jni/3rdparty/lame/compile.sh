
#!/bin/bash
#NDK目录
NDK=/d/Android/Sdk/ndk/21.4.7075529

HOST_TAG=windows-x86_64
TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/$HOST_TAG
#安装目录
ANDROID_LIB_PATH="$(pwd)/install_arm64-v8a"

API=21

function build_android_arm
{
echo "build for android $CPU"
./configure \
--host=$HOST \
--target=android \
--disable-shared \
--enable-static \
--prefix="$ANDROID_LIB_PATH/$CPU"
make clean
make -j8
make install
echo "building for android $CPU completed"
}



CPU=armv8-a
HOST=aarch64-linux-android
export AR=$TOOLCHAIN/bin/aarch64-linux-android-ar
export AS=$TOOLCHAIN/bin/aarch64-linux-android-as
export LD=$TOOLCHAIN/bin/aarch64-linux-android-ld
export RANLIB=$TOOLCHAIN/bin/aarch64-linux-android-ranlib
export STRIP=$TOOLCHAIN/bin/aarch64-linux-android-strip
export CC=$TOOLCHAIN/bin/aarch64-linux-android$API-clang
export CXX=$TOOLCHAIN/bin/aarch64-linux-android$API-clang++
OPTIMIZE_CFLAGS="-march=$CPU"
export CFLAGS="-Os -fpic -fdeclspec $OPTIMIZE_CFLAGS"
export CPPFLAGS="-Os -fpic -fdeclspec $OPTIMIZE_CFLAGS"
build_android_arm
