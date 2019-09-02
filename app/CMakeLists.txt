# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.


SET(LIBHELLO_SRC
src/main/cpp/show-lib.cpp
)

add_library(
             show-lib
             SHARED
             ${LIBHELLO_SRC})

#set(lib_src_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})
#add_library(hello_static SHARED IMPORTED)
#set_target_properties(hello_static PROPERTIES IMPORTED_LOCATION
#${lib_src_DIR}/libcstatic.a)

find_library( # Sets the name of the path variable.
              log-lib
              log )

target_link_libraries(
                       show-lib
                       android
                       log
                       openssl-crypto
                       openssl-ssl
                     #  hello_static
                       ${log-lib} )




include_directories(src/main/cpp)

add_library(openssl-crypto STATIC IMPORTED)
set_target_properties(openssl-crypto
  PROPERTIES IMPORTED_LOCATION
  ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libcrypto.so)

#添加加密工具(md5, base64, des, aes, asa, rsa, pem, x509, x509v3) des加密 part-2
add_library(openssl-ssl STATIC IMPORTED)
set_target_properties(openssl-ssl
  PROPERTIES IMPORTED_LOCATION
  ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libssl.so)
