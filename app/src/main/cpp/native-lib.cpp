#include <jni.h>
#include <string>




extern "C"
{
jstring
Java_com_nevs_1b_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT jstring JNICALL
Java_com_nevs_1b_MainActivity_stringFromJNI2(JNIEnv *env, jobject instance) {

    // TODO

    return env->NewStringUTF("Hello from C++");

}


//u_int8_t  blueCheckSum(uint8_t * frame, uint16_t len){
//    uint8_t sum=0;
//    for(uint16_t i=0;i<len;++i){
//        sum+= *(frame+i);
//    }
//    return sum;
//}

}





