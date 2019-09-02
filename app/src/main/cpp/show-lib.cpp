#include <jni.h>
#include <string>
#include "openssl/rsa.h"
#include "openssl/pem.h"
#include <openssl/err.h>
#include <openssl/md5.h>
#include <openssl/sha.h>
#include <openssl/evp.h>

#include <stdio.h>
#include <string.h>
#include <openssl/x509.h>
#include <openssl/x509v3.h>


#pragma comment(lib, "libeay32.lib")


#include <android/log.h>
//日志标签，随意定义
#define LOG_TAG "TAG"
//Debug等级
#define LOGD(...)__android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
//Info等级
#define LOGI(...)__android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
//Error等级
#define LOGE(...)__android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#ifdef __cplusplus

extern "C" {

#endif
//-------生成CRS封装方法---------------------------------------------------vvv
typedef struct X509_extension_parameter {
    char * vin;
    char * username;
    char * starttime;
    char * endtime;
    char * pin;
    char * mobiledevicepubkey;
    char * role;
    char * bookingid;
    char * userid;
} X509_EXTENSION_PARAMETER;

/*
 * subject is expected to be in the format /type0=value0/type1=value1/type2=...
 * where characters may be escaped by \
 */
X509_NAME *parse_name(char *subject, long chtype, int multirdn)
{
    size_t buflen = strlen(subject)+1; /* to copy the types and values into. due to escaping, the copy can only become shorter */
    char *buf = static_cast<char *>(OPENSSL_malloc(buflen));
    size_t max_ne = buflen / 2 + 1; /* maximum number of name elements */
    char **ne_types = static_cast<char **>(OPENSSL_malloc(max_ne * sizeof (char *)));
    char **ne_values = static_cast<char **>(OPENSSL_malloc(max_ne * sizeof (char *)));
    int *mval = static_cast<int *>(OPENSSL_malloc (max_ne * sizeof (int)));

    char *sp = subject, *bp = buf;
    int i, ne_num = 0;

    X509_NAME *n = NULL;
    int nid;

    if (!buf || !ne_types || !ne_values || !mval)
    {
        //BIO_printf(bio_err, "malloc error\n");
        goto error;
    }

    if (*subject != '/')
    {
        //BIO_printf(bio_err, "Subject does not start with '/'.\n");
        goto error;
    }
    sp++; /* skip leading / */

    /* no multivalued RDN by default */
    mval[ne_num] = 0;

    while (*sp)
    {
        /* collect type */
        ne_types[ne_num] = bp;
        while (*sp)
        {
            if (*sp == '\\') /* is there anything to escape in the type...? */
            {
                if (*++sp)
                    *bp++ = *sp++;
                else
                {
                    //BIO_printf(bio_err, "escape character at end of string\n");
                    goto error;
                }
            }
            else if (*sp == '=')
            {
                sp++;
                *bp++ = '\0';
                break;
            }
            else
                *bp++ = *sp++;
        }
        if (!*sp)
        {
            //BIO_printf(bio_err, "end of string encountered while processing type of subject name element #%d\n", ne_num);
            goto error;
        }
        ne_values[ne_num] = bp;
        while (*sp)
        {
            if (*sp == '\\')
            {
                if (*++sp)
                    *bp++ = *sp++;
                else
                {
                    //BIO_printf(bio_err, "escape character at end of string\n");
                    goto error;
                }
            }
            else if (*sp == '/')
            {
                sp++;
                /* no multivalued RDN by default */
                mval[ne_num+1] = 0;
                break;
            }
            else if (*sp == '+' && multirdn)
            {
                /* a not escaped + signals a mutlivalued RDN */
                sp++;
                mval[ne_num+1] = -1;
                break;
            }
            else
                *bp++ = *sp++;
        }
        *bp++ = '\0';
        ne_num++;
    }

    if (!(n = X509_NAME_new()))
        goto error;

    for (i = 0; i < ne_num; i++)
    {
        if ((nid=OBJ_txt2nid(ne_types[i])) == NID_undef)
        {
            //BIO_printf(bio_err, "Subject Attribute %s has no known NID, skipped\n", ne_types[i]);
            continue;
        }

        if (!*ne_values[i])
        {
            //BIO_printf(bio_err, "No value provided for Subject Attribute %s, skipped\n", ne_types[i]);
            continue;
        }

        if (!X509_NAME_add_entry_by_NID(n, nid, (int)chtype, (unsigned char*)ne_values[i], -1,-1,mval[i]))
            goto error;
    }

    OPENSSL_free(ne_values);
    OPENSSL_free(ne_types);
    OPENSSL_free(buf);
    OPENSSL_free(mval);
    return n;

    error:
    X509_NAME_free(n);
    if (ne_values)
        OPENSSL_free(ne_values);
    if (ne_types)
        OPENSSL_free(ne_types);
    if (mval)
        OPENSSL_free(mval);
    if (buf)
        OPENSSL_free(buf);
    return NULL;
}

X509_NAME *CreateDN(char *pbEmail, char *pbCN, char *pbOU, char *pbO, char *pbL, char *pbST, char *pbC)
{
    X509_NAME *pX509Name = NULL;
    if(pbCN == NULL)
    {
        return NULL;
    }

    if (!(pX509Name = X509_NAME_new()))
    {
        return NULL;
    }
    X509_NAME_add_entry_by_txt(pX509Name, "emailAddress", V_ASN1_UTF8STRING,
                               reinterpret_cast<const unsigned char *>(pbEmail), -1, -1, 0);
    X509_NAME_add_entry_by_txt(pX509Name, "CN", V_ASN1_UTF8STRING,
                               reinterpret_cast<const unsigned char *>(pbCN), -1, -1, 0);
    X509_NAME_add_entry_by_txt(pX509Name, "OU", V_ASN1_UTF8STRING,
                               reinterpret_cast<const unsigned char *>(pbOU), -1, -1, 0);
    X509_NAME_add_entry_by_txt(pX509Name, "O", V_ASN1_UTF8STRING,
                               reinterpret_cast<const unsigned char *>(pbO), -1, -1, 0);
    X509_NAME_add_entry_by_txt(pX509Name, "L", V_ASN1_UTF8STRING,
                               reinterpret_cast<const unsigned char *>(pbL), -1, -1, 0);
    X509_NAME_add_entry_by_txt(pX509Name, "ST", V_ASN1_UTF8STRING,
                               reinterpret_cast<const unsigned char *>(pbST), -1, -1, 0);
    X509_NAME_add_entry_by_txt(pX509Name, "C", V_ASN1_UTF8STRING,
                               reinterpret_cast<const unsigned char *>(pbC), -1, -1, 0);
    return pX509Name;
}

// base64 编码
char * base64Encode(const char *buffer, int length, int newLine)
{
    BIO *bmem = NULL;
    BIO *b64 = NULL;
    BUF_MEM *bptr;

    b64 = BIO_new(BIO_f_base64());
    if (!newLine) {
        BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL);
    }
    bmem = BIO_new(BIO_s_mem());
    b64 = BIO_push(b64, bmem);
    BIO_write(b64, buffer, length);
    BIO_flush(b64);
    BIO_get_mem_ptr(b64, &bptr);
    BIO_set_close(b64, BIO_NOCLOSE);

    char *buff = (char *)malloc(bptr->length + 1);
    memcpy(buff, bptr->data, bptr->length);
    buff[bptr->length] = 0;
    BIO_free_all(b64);
    return buff;
}

void RSA_GET_PUBKEY_PRIKEY(RSA * rsa, char ** pubKey, char ** priKey) {

    int len = i2d_RSAPrivateKey(rsa,NULL);
    unsigned char* pribuf = (unsigned char*)malloc(len+1);
    unsigned char* pt2 = pribuf;
    len = i2d_RSAPrivateKey(rsa,&pt2);
    char * pri = base64Encode((const char *)pribuf, len, 1);
    *priKey = pri;

    len = i2d_RSA_PUBKEY(rsa, NULL);
    unsigned char* pubbuf = (unsigned char*)malloc(len+1);
    unsigned char* pt3 = pubbuf;
    len = i2d_RSA_PUBKEY(rsa, &pt3);

    char * pub = base64Encode((const char *)pubbuf, len, 1);
    *pubKey = pub;
}

RSA *RSA_generate_key1(int bits, unsigned long e_value,
                      void (*callback) (int, int, void *), void *cb_arg)
{
    BN_GENCB cb;
    int i;
    RSA *rsa = RSA_new();
    BIGNUM *e = BN_new();

    if (!rsa || !e)
        goto err;

    /*
     * The problem is when building with 8, 16, or 32 BN_ULONG, unsigned long
     * can be larger
     */
    for (i = 0; i < (int)sizeof(unsigned long) * 8; i++) {
        if (e_value & (1UL << i))
            if (BN_set_bit(e, i) == 0)
                goto err;
    }

    BN_GENCB_set_old(&cb, callback, cb_arg);

    if (RSA_generate_key_ex(rsa, bits, e, &cb)) {
        BN_free(e);
        return rsa;
    }
    err:
    if (e)
        BN_free(e);
    if (rsa)
        RSA_free(rsa);
    return 0;
}

//-------生成CSR封装方法---------------------------------------------------^^^







JNIEXPORT jboolean JNICALL  //RSA加密------------------------------------------------------------------
Java_com_nevs_car_jnihelp_JniHelper_RSA(JNIEnv *env, jobject instance, jstring pubkeyStr_,
                                        jbyteArray inBytes_, jint inLen, jbyteArray outBytes_,
                                        jint outlen) {
    const char *pubkeyStr = env->GetStringUTFChars(pubkeyStr_, 0);
    jbyte *inBytes = env->GetByteArrayElements(inBytes_, NULL);
    jbyte *outBytes = env->GetByteArrayElements(outBytes_, NULL);

    // TODO

    FILE *file = fopen(pubkeyStr, "rb");
    if (!file) {
        LOGE("RSA 找不到文件");
        return false;
    }
    EVP_PKEY *rsa_pub = NULL;

    PEM_read_PUBKEY(file, &rsa_pub, NULL, NULL);
    fclose(file);


    EVP_PKEY_CTX *ctx = NULL;
    ctx = EVP_PKEY_CTX_new(rsa_pub, NULL);

    if (NULL == ctx) {

        LOGE("EVP_PKEY_CTX_new failed\n");
        EVP_PKEY_CTX_free(ctx);
        return false;
    }

    if (EVP_PKEY_encrypt_init(ctx) <= 0) {

        LOGE("EVP_PKEY_encrypt_init failed\n");
        EVP_PKEY_CTX_free(ctx);
        return false;
    }
    // LOGE("fffffffff%d", sizeof(ctx));
    if (EVP_PKEY_CTX_set_rsa_padding(ctx, RSA_PKCS1_OAEP_PADDING) <= 0) {

        LOGE("EVP_PKEY_CTX_set_rsa_padding failed\n");
        EVP_PKEY_CTX_free(ctx);
        return false;
    }

    if (EVP_PKEY_CTX_set_rsa_oaep_md(ctx, EVP_sha256()) <= 0) {

        LOGE("EVP_PKEY_CTX_set_rsa_oaep_md failed\n");
        EVP_PKEY_CTX_free(ctx);
        return false;
    }

    if (EVP_PKEY_CTX_set_rsa_mgf1_md(ctx, EVP_sha1()) <= 0) {

        LOGE("EVP_PKEY_CTX_set_rsa_mgf1_md failed\n");

        return false;
    }

    if (EVP_PKEY_CTX_set0_rsa_oaep_label(ctx, NULL, 0) <= 0) {

        LOGE("EVP_PKEY_CTX_set0_rsa_oaep_label failed\n");

        return false;

    }


//    size_t minRstLen = 0;
//
//    if(EVP_PKEY_encrypt(ctx, NULL, &minRstLen, inBytes, *inLen) <= 0){
//
//        printf("EVP_PKEY_encrypt get result size failed\n");
//
//        return false;
//
//    }

//    if(*inLen < minRstLen){
//
//        printf("inLen[%zu] < minRstLen[%zu]\n", *inLen, minRstLen);
//
//        return false;
//
//    }

    if (EVP_PKEY_encrypt(ctx, (unsigned char *) outBytes, (size_t *) &outlen,
                         (const unsigned char *) inBytes, inLen) <= 0) {

        LOGE("EVP_PKEY_encrypt failed\n");
        EVP_PKEY_CTX_free(ctx);
        return false;
    }
    LOGE("iiiii%p", outBytes);
    LOGE("0位置的值iiiii%d", outBytes[0]);

// for 循环执行
//    for( int a = 0; a < 256; a++ )
//    {
//        LOGE("%d", outBytes[a]);
//    }


//    jclass my_class=env->GetObjectClass(instance);
//    //jclass my_class=evn->FindClass("com/example/jni_test");也可以获取Class
//    //获取java的score属性的FieldID
//    jfieldID score_id=env->GetFieldID(my_class,"score","F");//（类,属性名.签名)
//    //根据FieldID获取属性
//    jfloat score=env->GetFloatField(instance,score_id);//(实例对象,属性ID)
//    //修改java的temp的score的值
//    env->SetFloatField(instance,score_id,88.8);

    env->ReleaseStringUTFChars(pubkeyStr_, pubkeyStr);
    env->ReleaseByteArrayElements(inBytes_, inBytes, 0);
    env->ReleaseByteArrayElements(outBytes_, outBytes, 0);


    EVP_PKEY_CTX_free(ctx);

    LOGE("加密中");
    return true;
}


JNIEXPORT jint JNICALL    //RSA解密=--------------------------------------------------------------------
Java_com_nevs_car_jnihelp_JniHelper_RSADECO(JNIEnv *env, jobject instance, jstring pubkeyStr_,
                                            jbyteArray inBytes_, jint inLen, jbyteArray outBytes_,
                                            jint outlen) {
    const char *pubkeyStr = env->GetStringUTFChars(pubkeyStr_, 0);
    jbyte *inBytes = env->GetByteArrayElements(inBytes_, NULL);
    jbyte *outBytes = env->GetByteArrayElements(outBytes_, NULL);

    // TODO

    LOGE("进入解密");
    FILE *file = fopen(pubkeyStr, "rb");
    if (!file) {
        return 0;
        LOGE("11");
    }
    EVP_PKEY *rsa_pri = NULL;

    PEM_read_PrivateKey(file, &rsa_pri, NULL, NULL);
    fclose(file);

    EVP_PKEY_CTX *ctx = NULL;

    ctx = EVP_PKEY_CTX_new(rsa_pri, NULL);

    if (NULL == ctx) {

        LOGE("EVP_PKEY_CTX_new failed\n");
        EVP_PKEY_CTX_free(ctx);
        return 0;

    }

    if (EVP_PKEY_decrypt_init(ctx) <= 0) {

        LOGE("EVP_PKEY_encrypt_init failed\n");
        EVP_PKEY_CTX_free(ctx);
        return 0;
    }


    if (EVP_PKEY_CTX_set_rsa_padding(ctx, RSA_PKCS1_OAEP_PADDING) <= 0) {

        LOGE("EVP_PKEY_CTX_set_rsa_padding failed\n");
        EVP_PKEY_CTX_free(ctx);
        return 0;
    }

    if (EVP_PKEY_CTX_set_rsa_oaep_md(ctx, EVP_sha256()) <= 0) {

        LOGE("EVP_PKEY_CTX_set_rsa_oaep_md failed\n");
        EVP_PKEY_CTX_free(ctx);
        return 0;
    }


    if (EVP_PKEY_CTX_set_rsa_mgf1_md(ctx, EVP_sha1()) <= 0) {

        LOGE("EVP_PKEY_CTX_set_rsa_mgf1_md failed\n");

        return 0;
    }

    if (EVP_PKEY_CTX_set0_rsa_oaep_label(ctx, NULL, 0) <= 0) {

        LOGE("EVP_PKEY_CTX_set0_rsa_oaep_label failed\n");

        return 0;

    }


    size_t minRstLen = 0;

    LOGE("22");
    if (EVP_PKEY_decrypt(ctx, NULL, &minRstLen, (const unsigned char *) inBytes, inLen) <= 0) {

        LOGE("EVP_PKEY_encrypt get result size failed\n");

        return 0;

    }
    //    if(*inLen < minRstLen){
    //
    //        printf("inLen[%zu] < minRstLen[%zu]\n", *inLen, minRstLen);
    //
    //        return false;
    //
    //    }
    if (EVP_PKEY_decrypt(ctx, (unsigned char *) outBytes, (size_t *) &outlen,
                         (const unsigned char *) inBytes, inLen) <= 0) {

        LOGE("EVP_PKEY_decrypt failed\n");

        EVP_PKEY_CTX_free(ctx);

        return 0;
    }


    LOGE("aa%d", outlen);

    env->ReleaseStringUTFChars(pubkeyStr_, pubkeyStr);
    env->ReleaseByteArrayElements(inBytes_, inBytes, 0);
    env->ReleaseByteArrayElements(outBytes_, outBytes, 0);
    EVP_PKEY_CTX_free(ctx);
    LOGE("解密中");
    return outlen;
}


JNIEXPORT jbyteArray JNICALL //生成CSR----------------------------------------------------------------
Java_com_nevs_car_jnihelp_JniHelper_CSRb(JNIEnv *env, jobject instance, jstring chDN_,
                                         jstring san_) {
    const char *pbDN = env->GetStringUTFChars(chDN_, 0);
    const char *SAN = env->GetStringUTFChars(san_, 0);

    // TODO


    //    char BindingId[] = "e3e22df0-39de-4233-bdbb-c9f2c9f9766c";
//    char MobileId[] = "7dc98aaa-9d29-46ef-9bc7-f8281e61fc82";
//    char UserAccount[] = "bfd75ef4961b49c69fdbe793633e56cb";
//    char szAltName[] = "DNS:www.example.com,DNS:www2.example.com";
//    char szComment[] = "20180101.20180810.LTPCHINATELE00123";
//    char szKeyUsage[] = "digitalSignature, nonRepudiation";
//    char szExKeyUsage[] = "serverAuth, clientAuth";

    X509_REQ *pX509Req = NULL;
    int iRV = 0;
    long lVer = 3;
    X509_NAME *pX509DN = NULL;
    EVP_PKEY *pEVPKey = NULL;
    RSA *pRSA = NULL;
    X509_NAME_ENTRY *pX509Entry = NULL;
    char szBuf[255] = {0};
    char mdout[20];
    int nLen, nModLen;
    int bits = 2048;
    unsigned long E = RSA_F4;
    unsigned char *pDer = NULL;
    unsigned char *p = NULL;
    FILE *fp = NULL;
    const EVP_MD *md = NULL;
    X509 *pX509 = NULL;
    BIO *pBIO = NULL;
    BIO *pPemBIO = NULL;
    BUF_MEM *pBMem = NULL;

    char pCSR[2048] = {0};
    size_t nCSRSize = 2048;

    //STACK_OF(X509_EXTENSION) *pX509Ext;

    if (pbDN == NULL) {
        return (jbyteArray) -1;
    }
    pX509DN = parse_name((char *) pbDN, V_ASN1_UTF8STRING, 0);

    pX509Req = X509_REQ_new();

    iRV = X509_REQ_set_version(pX509Req, lVer);
    // subject pX509Name
    iRV = X509_REQ_set_subject_name(pX509Req, pX509DN);

    /* pub key */
    pEVPKey = EVP_PKEY_new();
    pRSA = RSA_generate_key1(bits, E, NULL, NULL);
    //pRSA = reinterpret_cast<RSA *>(RSA_generate_key_ex(reinterpret_cast<RSA *>(bits), E, NULL, NULL));


    char *pubKey = NULL;
    char *priKey = NULL;
    RSA_GET_PUBKEY_PRIKEY(pRSA, &pubKey, &priKey);

    EVP_PKEY_assign_RSA(pEVPKey, pRSA);
    iRV = X509_REQ_set_pubkey(pX509Req, pEVPKey);

    /**
     * 添加多DNS
     */
    X509_EXTENSION *ext;
    STACK_OF(X509_EXTENSION) *extlist;
    char const *name = "subjectAltName";
    char const *value = (char *) SAN;

    extlist = sk_X509_EXTENSION_new_null();
    {
        if (!(ext = X509V3_EXT_conf(NULL, NULL, const_cast<char *>(name), const_cast<char *>(value))))
            LOGE("Error creating subjectAltName extension");
        sk_X509_EXTENSION_push(extlist, ext);
        if (!X509_REQ_add_extensions(pX509Req, extlist))
            LOGE("Error adding subjectAltName to the request");
    }

    //cc 好像没有什么影响     sk_X509_EXTENSION_pop_free(extlist, X509_EXTENSION_free);


    md = EVP_sha256();
    iRV = X509_REQ_digest(pX509Req, md, (unsigned char *) mdout, (unsigned int *) &nModLen);
    iRV = X509_REQ_sign(pX509Req, pEVPKey, md);

    if (!iRV) {
        LOGE("sign err!\n");
        X509_REQ_free(pX509Req);
        return NULL;
    }

    // 写入文件PEM格式
    //     pBIO = BIO_new_file("certreq.txt", "w");
    //     PEM_write_bio_X509_REQ(pBIO, pX509Req, NULL, NULL);
    //     BIO_free(pBIO);

    LOGE("7");
    //返回PEM字符
    pPemBIO = BIO_new(BIO_s_mem());
    LOGE("8");
    PEM_write_bio_X509_REQ(pPemBIO, pX509Req);
//    PEM_write_bio_X509_REQ(pPemBIO, pX509Req, NULL, NULL);
//    PEM_write_bio_X509_REQ(pPemBIO, pX509Req, NULL, NULL);
    BIO_get_mem_ptr(pPemBIO, &pBMem);
    LOGE("9");
    if (pBMem->length <= nCSRSize) {
        memcpy(pCSR, pBMem->data, pBMem->length);
    }
    LOGE("10");
    BIO_free(pPemBIO);

    /* DER编码 */
    //nLen = i2d_X509_REQ(pX509Req, NULL);
    //pDer = (unsigned char *)malloc(nLen);
    //p = pDer;
    //nLen = i2d_X509_REQ(pX509Req, &p);
    //free(pDer);

    LOGE("1");
    //    验证CSR
    OpenSSL_add_all_algorithms();
    LOGE("2");
    iRV = X509_REQ_verify(pX509Req, pEVPKey);
    LOGE("3");
    if (iRV < 0) {
        LOGE("verify err.\n");
    }
    LOGE("4");
    X509_REQ_free(pX509Req);
    LOGE("5");
    size_t resultSize = strlen(pCSR) + strlen(priKey) + strlen(pubKey) + 2;
    //CC   LOGE("6%s",pubKey);
    char *result = (char *) malloc(resultSize);
    LOGE("15");
    strcat(result, pCSR);
    LOGE("16");
    strcat(result, ";");
    LOGE("17");
    strcat(result, priKey);
    LOGE("18");
    strcat(result, ";");
    LOGE("19");
    strcat(result, pubKey);

   // LOGE("20%lu", strlen(result));


    //CC  LOGE("dd\n%s",result+2048);


//    //定义java String类 strClass
//    jclass strClass = (env)->FindClass("Ljava/lang/String;");
//    //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
//    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    //建立byte数组
    jbyteArray bytes = (env)->NewByteArray(strlen(result));
    //将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, strlen(result), (jbyte *) result);
//    // 设置String, 保存语言类型,用于byte数组转换至String时的参数
//    jstring encoding = (env)->NewStringUTF("GB2312");
//    //将byte数组转换为java String,并输出


    env->ReleaseStringUTFChars(chDN_, pbDN);
    env->ReleaseStringUTFChars(san_, SAN);
    LOGE("21");


    return bytes;
}

//生成CSR改版
JNIEXPORT jbyteArray JNICALL
Java_com_nevs_car_jnihelp_JniHelper_CSRg(JNIEnv *env, jobject instance, jstring pbDN_, jstring SAN_,
                                         jint nDNLen, jobject csrBean) {
    const char *pbDN = env->GetStringUTFChars(pbDN_, 0);
    const char *SAN = env->GetStringUTFChars(SAN_, 0);

    // TODO
    X509_REQ        *pX509Req = NULL;
    int                iRV = 0;
    long            lVer = 3;
    X509_NAME        *pX509DN = NULL;
    EVP_PKEY        *pEVPKey = NULL;
    RSA                *pRSA = NULL;
    X509_NAME_ENTRY    *pX509Entry = NULL;
    char            szBuf[255] = {0};
    char            mdout[20];
    int                nLen, nModLen;
    int                bits = 2048;
    unsigned long    E = RSA_F4;
    unsigned char    *pDer = NULL;
    unsigned char    *p = NULL;
    FILE            *fp = NULL;
    const EVP_MD    *md = NULL;
    X509            *pX509 = NULL;
    BIO                *pBIO = NULL;
    BIO                *pPemBIO = NULL;
    BUF_MEM            *pBMem = NULL;

    size_t nCSRSize = 2048;
    char pCSR[2048] = {0};
    char * pubKey = NULL;
    char * priKey = NULL;
    //STACK_OF(X509_EXTENSION) *pX509Ext;

//  //--------ccs
//    X509_EXTENSION_PARAMETER stuInfo;
//    LOGI("====setStu===1====");
//    //获取jclass的实例
//    //jclass jcs = env->FindClass("com/nevs/car/jnihelp/modle/CSRBean");
//    jclass jcs=env->GetObjectClass(csrBean);
//    //获取StuInfo的字段ID
//    jfieldID vinId = env->GetFieldID(jcs, "vin", "Ljava/lang/String;");
//    jfieldID usernameId = env->GetFieldID(jcs, "username", "Ljava/lang/String;");
//    jfieldID starttimeId = env->GetFieldID(jcs, "starttime", "Ljava/lang/String;");
//    jfieldID endtimeId = env->GetFieldID(jcs, "endtime", "Ljava/lang/String;");
//    jfieldID pinId = env->GetFieldID(jcs, "pin", "Ljava/lang/String;");
//    jfieldID mobiledevicepubkeyId = env->GetFieldID(jcs, "mobiledevicepubkey", "Ljava/lang/String;");
//    jfieldID roleId = env->GetFieldID(jcs, "role", "Ljava/lang/String;");
//    jfieldID bookingidId = env->GetFieldID(jcs, "bookingid", "Ljava/lang/String;");
//    jfieldID useridId = env->GetFieldID(jcs, "vuserid", "Ljava/lang/String;");
//
//    //把字段Id设置到结构体中
//    jstring vinStr = (jstring) env->GetObjectField(csrBean, vinId);
//    const char *locstr1 = env->GetStringUTFChars(vinStr, 0);
//    strcpy(stuInfo.vin, locstr1);
//
//    jstring usernameStr = (jstring) env->GetObjectField(csrBean, usernameId);
//    const char *locstr2 = env->GetStringUTFChars(usernameStr, 0);
//    strcpy(stuInfo.username, locstr2);
//
//    jstring starttimeStr = (jstring) env->GetObjectField(csrBean, starttimeId);
//    const char *locstr3 = env->GetStringUTFChars(vinStr, 0);
//    strcpy(stuInfo.starttime, locstr3);
//
//    jstring endtimeStr = (jstring) env->GetObjectField(csrBean, endtimeId);
//    const char *locstr4 = env->GetStringUTFChars(endtimeStr, 0);
//    strcpy(stuInfo.endtime, locstr4);
//
//    jstring pinStr = (jstring) env->GetObjectField(csrBean, pinId);
//    const char *locstr5 = env->GetStringUTFChars(pinStr, 0);
//    strcpy(stuInfo.pin, locstr5);
//
//    jstring mobiledevicepubkeyStr = (jstring) env->GetObjectField(csrBean, mobiledevicepubkeyId);
//    const char *locstr6 = env->GetStringUTFChars(mobiledevicepubkeyStr, 0);
//    strcpy(stuInfo.mobiledevicepubkey, locstr6);
//
//    jstring roleStr = (jstring) env->GetObjectField(csrBean, roleId);
//    const char *locstr7 = env->GetStringUTFChars(roleStr, 0);
//    strcpy(stuInfo.role, locstr7);
//
//    jstring bookingidStr = (jstring) env->GetObjectField(csrBean, bookingidId);
//    const char *locstr8 = env->GetStringUTFChars(bookingidStr, 0);
//    strcpy(stuInfo.bookingid, locstr8);
//
//    jstring useridStr = (jstring) env->GetObjectField(csrBean, useridId);
//    const char *locstr9 = env->GetStringUTFChars(useridStr, 0);
//    strcpy(stuInfo.userid, locstr9);
//    LOGE("====setStu===4====");
//
//    //-------cce
//
//
//    if(pbDN == NULL)
//    {
//        return reinterpret_cast<jbyteArray>(-1);
//    }
//    pX509DN = parse_name(const_cast<char *>(pbDN), V_ASN1_UTF8STRING, 0);
//
//    pX509Req = X509_REQ_new();
//
//    iRV = X509_REQ_set_version(pX509Req, lVer);
//    // subject pX509Name
//    iRV = X509_REQ_set_subject_name(pX509Req, pX509DN);
//    /* pub key */
//    pEVPKey = EVP_PKEY_new();
//    pRSA = RSA_generate_key(bits, E, NULL, NULL);
//
//    RSA_GET_PUBKEY_PRIKEY(pRSA, &pubKey, &priKey);
//
//    EVP_PKEY_assign_RSA(pEVPKey, pRSA);
//    iRV = X509_REQ_set_pubkey(pX509Req, pEVPKey);
//
//    /**
//     * 添加扩展
//     */
//    X509_EXTENSION           *ext;
//    STACK_OF(X509_EXTENSION) *extlist;
//    char                     *name = "subjectAltName";
//    char                     *value = const_cast<char *>(SAN);
//    extlist = sk_X509_EXTENSION_new_null();
//    {
//        //        if (!(ext = X509V3_EXT_conf(NULL, NULL, name, value)))
//        //            printf("Error creating subjectAltName extension");
//        //
//        //        sk_X509_EXTENSION_push(extlist, ext);
//        {
//            X509_EXTENSION           *ext2 = NULL;
//            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
//            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(stuInfo.vin), strlen(stuInfo.vin));
//            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65531", 0);
//            ext2 = X509_EXTENSION_new();
//            X509_EXTENSION_set_data(ext2, str);
//            X509_EXTENSION_set_object(ext2, obj);
//            sk_X509_EXTENSION_push(extlist, ext2);
//        }
//
//        {
//            X509_EXTENSION           *ext2 = NULL;
//            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
//            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(stuInfo.username), strlen(stuInfo.username));
//            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65532", 0);
//            ext2 = X509_EXTENSION_new();
//            X509_EXTENSION_set_data(ext2, str);
//            X509_EXTENSION_set_object(ext2, obj);
//            sk_X509_EXTENSION_push(extlist, ext2);
//        }
//
//        {
//            X509_EXTENSION           *ext2 = NULL;
//            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
//            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(stuInfo.starttime), strlen(stuInfo.starttime));
//            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65533", 0);
//            ext2 = X509_EXTENSION_new();
//            X509_EXTENSION_set_data(ext2, str);
//            X509_EXTENSION_set_object(ext2, obj);
//            sk_X509_EXTENSION_push(extlist, ext2);
//        }
//
//        {
//            X509_EXTENSION           *ext2 = NULL;
//            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
//            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(stuInfo.endtime), strlen(stuInfo.endtime));
//            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65534", 0);
//            ext2 = X509_EXTENSION_new();
//            X509_EXTENSION_set_data(ext2, str);
//            X509_EXTENSION_set_object(ext2, obj);
//            sk_X509_EXTENSION_push(extlist, ext2);
//        }
//
//        {
//            X509_EXTENSION           *ext2 = NULL;
//            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
//            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(stuInfo.pin), strlen(stuInfo.pin));
//            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65535", 0);
//            ext2 = X509_EXTENSION_new();
//            X509_EXTENSION_set_data(ext2, str);
//            X509_EXTENSION_set_object(ext2, obj);
//            sk_X509_EXTENSION_push(extlist, ext2);
//        }
//
//        {
//            char * pubHeader = "-----BEGIN PUBLIC KEY-----\n";
//            char * pubFooter = "-----END PUBLIC KEY-----";
//            unsigned long len = strlen(pubKey) + strlen(pubHeader) + strlen(pubFooter);
//            char pub[len];
//            //            strncpy(pub, *pubKey, len);
//            sprintf(pub, "%s%s%s", pubHeader, pubKey, pubFooter);
//            X509_EXTENSION           *ext2 = NULL;
//            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
//            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(pub), strlen(pub));
//            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65536", 0);
//            ext2 = X509_EXTENSION_new();
//            X509_EXTENSION_set_data(ext2, str);
//            X509_EXTENSION_set_object(ext2, obj);
//            sk_X509_EXTENSION_push(extlist, ext2);
//        }
//
//        {
//            X509_EXTENSION           *ext2 = NULL;
//            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
//            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(stuInfo.role), strlen(stuInfo.role));
//            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65537", 0);
//            ext2 = X509_EXTENSION_new();
//            X509_EXTENSION_set_data(ext2, str);
//            X509_EXTENSION_set_object(ext2, obj);
//            sk_X509_EXTENSION_push(extlist, ext2);
//        }
//
//        {
//            X509_EXTENSION           *ext2 = NULL;
//            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
//            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(stuInfo.bookingid), strlen(stuInfo.bookingid));
//            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65538", 0);
//            ext2 = X509_EXTENSION_new();
//            X509_EXTENSION_set_data(ext2, str);
//            X509_EXTENSION_set_object(ext2, obj);
//            sk_X509_EXTENSION_push(extlist, ext2);
//        }
//
//        {
//            X509_EXTENSION           *ext2 = NULL;
//            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
//            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(stuInfo.userid), strlen(stuInfo.userid));
//            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65539", 0);
//            ext2 = X509_EXTENSION_new();
//            X509_EXTENSION_set_data(ext2, str);
//            X509_EXTENSION_set_object(ext2, obj);
//            sk_X509_EXTENSION_push(extlist, ext2);
//        }
//
//
//        if (!X509_REQ_add_extensions(pX509Req, extlist))
//            printf("Error adding subjectAltName to the request");
//    }
//    //cc 好像没有什么影响
//    sk_X509_EXTENSION_pop_free(extlist, X509_EXTENSION_free);
//
//    md = EVP_sha256();
//    iRV = X509_REQ_digest(pX509Req, md, reinterpret_cast<unsigned char *>(mdout),
//                          reinterpret_cast<unsigned int *>(&nModLen));
//    iRV = X509_REQ_sign(pX509Req, pEVPKey, md);
//
//    if(!iRV)
//    {
//        printf("sign err!\n");
//        X509_REQ_free(pX509Req);
//        return reinterpret_cast<jbyteArray>(-1);
//    }
//
//    // 写入文件PEM格式
//    //     pBIO = BIO_new_file("certreq.txt", "w");
//    //     PEM_write_bio_X509_REQ(pBIO, pX509Req, NULL, NULL);
//    //     BIO_free(pBIO);
//
//    //返回PEM字符
//    pPemBIO = BIO_new(BIO_s_mem());
//    PEM_write_bio_X509_REQ(pPemBIO, pX509Req);
//    //    PEM_write_bio_X509_REQ(pPemBIO, pX509Req, NULL, NULL);
//    //    PEM_write_bio_X509_REQ(pPemBIO, pX509Req, NULL, NULL);
//    BIO_get_mem_ptr(pPemBIO,&pBMem);
//    if(pBMem->length <= nCSRSize)
//    {
//        memcpy(pCSR, pBMem->data, pBMem->length);
//    }
//    BIO_free(pPemBIO);
//
//    /* DER编码 */
//    //nLen = i2d_X509_REQ(pX509Req, NULL);
//    //pDer = (unsigned char *)malloc(nLen);
//    //p = pDer;
//    //nLen = i2d_X509_REQ(pX509Req, &p);
//    //free(pDer);
//
//    //    验证CSR
//    OpenSSL_add_all_algorithms();
//    iRV = X509_REQ_verify(pX509Req, pEVPKey);
//    if(iRV<0)
//    {
//        LOGE("verify err.\n");
//    }
//
//    X509_REQ_free(pX509Req);
//    size_t resultSize = strlen(pCSR) + strlen(pubKey) + strlen(pubKey) + 2;
//    char * result = static_cast<char *>(malloc(resultSize));
//    strcat(result, pCSR);
//    strcat(result, ";");
//    strcat(result, priKey);
//    strcat(result, ";");
//    strcat(result, pubKey);
//    LOGE("20%lu", strlen(result));
//
//
//    //CC  LOGE("dd\n%s",result+2048);
//



    char * result;
//    //定义java String类 strClass
//    jclass strClass = (env)->FindClass("Ljava/lang/String;");
//    //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
//    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    //建立byte数组
    jbyteArray bytes = (env)->NewByteArray(strlen(result));
    //将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, strlen(result), (jbyte*) result);
//    // 设置String, 保存语言类型,用于byte数组转换至String时的参数
//    jstring encoding = (env)->NewStringUTF("GB2312");
//    //将byte数组转换为java String,并输出


    env->ReleaseStringUTFChars(pbDN_, pbDN);
    env->ReleaseStringUTFChars(SAN_, SAN);
    LOGE("21");


    return bytes;



}

/////  生成CSR 传STRING
JNIEXPORT jbyteArray JNICALL
Java_com_nevs_car_jnihelp_JniHelper_csrGai(JNIEnv *env, jobject instance, jstring pbDN_,
                                           jstring SAN_, jint nDNLen, jstring vin_,
                                           jstring username_, jstring starttime_, jstring endtime_, jstring pin_,
                                           jstring mobiledevicepubkey_, jstring role_,
                                           jstring bookingid_, jstring userid_) {
    const char *pbDN = env->GetStringUTFChars(pbDN_, 0);
    const char *SAN = env->GetStringUTFChars(SAN_, 0);
    const char *vin = env->GetStringUTFChars(vin_, 0);
    const char *username = env->GetStringUTFChars(username_, 0);
    const char *starttime = env->GetStringUTFChars(starttime_, 0);
    const char *endtime = env->GetStringUTFChars(endtime_, 0);
    const char *pin = env->GetStringUTFChars(pin_, 0);
    const char *mobiledevicepubkey = env->GetStringUTFChars(mobiledevicepubkey_, 0);
    const char *role = env->GetStringUTFChars(role_, 0);
    const char *bookingid = env->GetStringUTFChars(bookingid_, 0);
    const char *userid = env->GetStringUTFChars(userid_, 0);

    // TODO
    X509_REQ        *pX509Req = NULL;
    int                iRV = 0;
    long            lVer = 3;
    X509_NAME        *pX509DN = NULL;
    EVP_PKEY        *pEVPKey = NULL;
    RSA                *pRSA = NULL;
    X509_NAME_ENTRY    *pX509Entry = NULL;
    char            szBuf[255] = {0};
    char            mdout[20];
    int                nLen, nModLen;
    int                bits = 2048;
    unsigned long    E = RSA_F4;
    unsigned char    *pDer = NULL;
    unsigned char    *p = NULL;
    FILE            *fp = NULL;
    const EVP_MD    *md = NULL;
    X509            *pX509 = NULL;
    BIO                *pBIO = NULL;
    BIO                *pPemBIO = NULL;
    BUF_MEM            *pBMem = NULL;

    size_t nCSRSize = 2048;
    char pCSR[2048] = {0};
    char * pubKey = NULL;
    char * priKey = NULL;
    //STACK_OF(X509_EXTENSION) *pX509Ext;


    if(pbDN == NULL)
    {
        return reinterpret_cast<jbyteArray>(-1);
    }
    pX509DN = parse_name(const_cast<char *>(pbDN), V_ASN1_UTF8STRING, 0);

    pX509Req = X509_REQ_new();

    iRV = X509_REQ_set_version(pX509Req, lVer);
    // subject pX509Name
    iRV = X509_REQ_set_subject_name(pX509Req, pX509DN);
    /* pub key */
    pEVPKey = EVP_PKEY_new();
    pRSA = RSA_generate_key1(bits, E, NULL, NULL);
    //pRSA = reinterpret_cast<RSA *>(RSA_generate_key_ex(reinterpret_cast<RSA *>(bits), E, NULL, NULL));

    RSA_GET_PUBKEY_PRIKEY(pRSA, &pubKey, &priKey);

    EVP_PKEY_assign_RSA(pEVPKey, pRSA);
    iRV = X509_REQ_set_pubkey(pX509Req, pEVPKey);

    /**
     * 添加扩展
     */
    X509_EXTENSION           *ext;
    STACK_OF(X509_EXTENSION) *extlist;
    char const                    *name="subjectAltName";
    char const                    *value = const_cast<char *>(SAN);
    extlist = sk_X509_EXTENSION_new_null();
    {
        //        if (!(ext = X509V3_EXT_conf(NULL, NULL, name, value)))
        //            printf("Error creating subjectAltName extension");
        //
        //        sk_X509_EXTENSION_push(extlist, ext);
        {
            X509_EXTENSION           *ext2 = NULL;
            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(vin), strlen(vin));
            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65531", 0);
            ext2 = X509_EXTENSION_new();
            X509_EXTENSION_set_data(ext2, str);
            X509_EXTENSION_set_object(ext2, obj);
            sk_X509_EXTENSION_push(extlist, ext2);
        }

        {
            X509_EXTENSION           *ext2 = NULL;
            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(username), strlen(username));
            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65532", 0);
            ext2 = X509_EXTENSION_new();
            X509_EXTENSION_set_data(ext2, str);
            X509_EXTENSION_set_object(ext2, obj);
            sk_X509_EXTENSION_push(extlist, ext2);
        }

        {
            X509_EXTENSION           *ext2 = NULL;
            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(starttime), strlen(starttime));
            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65533", 0);
            ext2 = X509_EXTENSION_new();
            X509_EXTENSION_set_data(ext2, str);
            X509_EXTENSION_set_object(ext2, obj);
            sk_X509_EXTENSION_push(extlist, ext2);
        }

        {
            X509_EXTENSION           *ext2 = NULL;
            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(endtime), strlen(endtime));
            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65534", 0);
            ext2 = X509_EXTENSION_new();
            X509_EXTENSION_set_data(ext2, str);
            X509_EXTENSION_set_object(ext2, obj);
            sk_X509_EXTENSION_push(extlist, ext2);
        }

        {
            X509_EXTENSION           *ext2 = NULL;
            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(pin), strlen(pin));
            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65535", 0);
            ext2 = X509_EXTENSION_new();
            X509_EXTENSION_set_data(ext2, str);
            X509_EXTENSION_set_object(ext2, obj);
            sk_X509_EXTENSION_push(extlist, ext2);
        }

        {
            char const  *pubHeader = "-----BEGIN PUBLIC KEY-----\n";
            char const  *pubFooter = "-----END PUBLIC KEY-----";
            unsigned long len = strlen(pubKey) + strlen(reinterpret_cast<const char *>(pubHeader)) + strlen(
                    reinterpret_cast<const char *>(pubFooter));
            char pub[len];
            //            strncpy(pub, *pubKey, len);
            //sprintf(pub, "%s%s%s", pubHeader, pubKey, pubFooter);
            X509_EXTENSION           *ext2 = NULL;
            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(pub), strlen(pub));
            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65536", 0);
            ext2 = X509_EXTENSION_new();
            X509_EXTENSION_set_data(ext2, str);
            X509_EXTENSION_set_object(ext2, obj);
            sk_X509_EXTENSION_push(extlist, ext2);
        }

        {
            X509_EXTENSION           *ext2 = NULL;
            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(role), strlen(role));
            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65537", 0);
            ext2 = X509_EXTENSION_new();
            X509_EXTENSION_set_data(ext2, str);
            X509_EXTENSION_set_object(ext2, obj);
            sk_X509_EXTENSION_push(extlist, ext2);
        }

        {
            X509_EXTENSION           *ext2 = NULL;
            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(bookingid), strlen(bookingid));
            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65538", 0);
            ext2 = X509_EXTENSION_new();
            X509_EXTENSION_set_data(ext2, str);
            X509_EXTENSION_set_object(ext2, obj);
            sk_X509_EXTENSION_push(extlist, ext2);
        }

        {
            X509_EXTENSION           *ext2 = NULL;
            ASN1_OCTET_STRING * str = ASN1_OCTET_STRING_new();
            ASN1_OCTET_STRING_set(str, reinterpret_cast<const unsigned char *>(userid), strlen(userid));
            ASN1_OBJECT * obj = OBJ_txt2obj("1.2.3.4.5.1.65539", 0);
            ext2 = X509_EXTENSION_new();
            X509_EXTENSION_set_data(ext2, str);
            X509_EXTENSION_set_object(ext2, obj);
            sk_X509_EXTENSION_push(extlist, ext2);
        }


        if (!X509_REQ_add_extensions(pX509Req, extlist))
            printf("Error adding subjectAltName to the request");
    }
    //cc 好像没有什么影响
   // sk_X509_EXTENSION_pop_free(extlist, X509_EXTENSION_free);
    free(extlist);
    md = EVP_sha256();
    iRV = X509_REQ_digest(pX509Req, md, reinterpret_cast<unsigned char *>(mdout),
                          reinterpret_cast<unsigned int *>(&nModLen));
    iRV = X509_REQ_sign(pX509Req, pEVPKey, md);

    if(!iRV)
    {
        printf("sign err!\n");
        X509_REQ_free(pX509Req);
        return reinterpret_cast<jbyteArray>(-1);
    }

    // 写入文件PEM格式
    //     pBIO = BIO_new_file("certreq.txt", "w");
    //     PEM_write_bio_X509_REQ(pBIO, pX509Req, NULL, NULL);
    //     BIO_free(pBIO);

    //返回PEM字符
    pPemBIO = BIO_new(BIO_s_mem());
    PEM_write_bio_X509_REQ(pPemBIO, pX509Req);
    //    PEM_write_bio_X509_REQ(pPemBIO, pX509Req, NULL, NULL);
    //    PEM_write_bio_X509_REQ(pPemBIO, pX509Req, NULL, NULL);
    BIO_get_mem_ptr(pPemBIO,&pBMem);
    if(pBMem->length <= nCSRSize)
    {
        memcpy(pCSR, pBMem->data, pBMem->length);
    }
    BIO_free(pPemBIO);

    /* DER编码 */
    //nLen = i2d_X509_REQ(pX509Req, NULL);
    //pDer = (unsigned char *)malloc(nLen);
    //p = pDer;
    //nLen = i2d_X509_REQ(pX509Req, &p);
    //free(pDer);

    //    验证CSR
    OpenSSL_add_all_algorithms();
    iRV = X509_REQ_verify(pX509Req, pEVPKey);
    if(iRV<0)
    {
        LOGE("verify err.\n");
    }

    X509_REQ_free(pX509Req);

    //cc
    jclass String_clazz = env->FindClass("java/lang/String");
    jmethodID concat_methodID = env->GetMethodID(String_clazz, "concat", "(Ljava/lang/String;)Ljava/lang/String;");
    //需要在后面拼接的字符串...
    const char * fg= ";\n";
    jstring fgj=env->NewStringUTF(fg);
    jstring str = env->NewStringUTF(priKey);
    jstring jstring1=env->NewStringUTF(pCSR);
    jobject str1 = env->CallObjectMethod(jstring1, concat_methodID, fgj);
    jobject str2 = env->CallObjectMethod(str1, concat_methodID, str);
    const char *chars = env->GetStringUTFChars((jstring)str2, 0);

   // LOGE("拼接后字符 ===> %s ", chars);



   //ccc size_t resultSize = strlen(pCSR) + strlen(pubKey) + strlen(pubKey) + 2;

//    size_t resultSize = strlen(pCSR) + strlen(priKey) + 1;
//    char * result = NULL;
//    result = static_cast<char *>(malloc(resultSize));
//    strcat(result, pCSR);
//    strcat(result, ";");
//    strcat(result, priKey);

//ccc    strcat(result, ";");
//ccc    strcat(result, pubKey);
   // LOGE("20%lu", strlen(result));


    //CC  LOGE("dd\n%s",result+2048);


//    //定义java String类 strClass
//    jclass strClass = (env)->FindClass("Ljava/lang/String;");
//    //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
//    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");

//    //建立byte数组
    jbyteArray bytes = NULL;
    bytes = (env)->NewByteArray(strlen(chars));
    //将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, strlen(chars), reinterpret_cast<const jbyte *>(chars));

//    // 设置String, 保存语言类型,用于byte数组转换至String时的参数
//    jstring encoding = (env)->NewStringUTF("GB2312");
//    //将byte数组转换为java String,并输出



   // jstring bytes = (env)->NewStringUTF(reinterpret_cast<const char *>(result));


    env->ReleaseStringUTFChars(pbDN_, pbDN);
    env->ReleaseStringUTFChars(SAN_, SAN);
    env->ReleaseStringUTFChars(vin_, vin);
    env->ReleaseStringUTFChars(username_, username);
    env->ReleaseStringUTFChars(starttime_, starttime);
    env->ReleaseStringUTFChars(endtime_, endtime);
    env->ReleaseStringUTFChars(pin_, pin);
    env->ReleaseStringUTFChars(mobiledevicepubkey_, mobiledevicepubkey);
    env->ReleaseStringUTFChars(role_, role);
    env->ReleaseStringUTFChars(bookingid_, bookingid);
    env->ReleaseStringUTFChars(userid_, userid);
    LOGE("21");
    //释放内存
    env->DeleteLocalRef(str);
    env->DeleteLocalRef(jstring1);
    env->DeleteLocalRef(fgj);
    env->DeleteLocalRef(str1);
    env->ReleaseStringUTFChars((jstring)str2,chars);
    //释放result
//    if(result) {
//        free(result);
//        LOGE("sf1");
//    }
    if(priKey) {
        free(priKey);
        LOGE("sf2");
    }
    if(pubKey) {
        free(pubKey);
        LOGE("sf3");
    }
    return bytes;
}


//JNIEXPORT jbyteArray JNICALL
//Java_com_nevs_car_jnihelp_JniHelper_getCsrNumber(JNIEnv *env, jobject instance, jstring cPath_) {
//    const char *certFile = env->GetStringUTFChars(cPath_, 0);
//
//    // TODO
//    X509 *x509 = NULL;
//    x509 = GetX509Cert(certFile);
//    if(NULL == x509){
//        LogE("GetX509Cert failed, certFile = %s\n", certFile.c_str());
//        return "";
//    }
//
//    ASN1_INTEGER *sn = X509_get_serialNumber(x509);
//    BIGNUM *bn = ASN1_INTEGER_to_BN(sn, NULL);
//    if(NULL == bn){
//        LogE("unable to convert ASN1INTEGER to BN, certFile = %s\n", certFile.c_str());
//        X509_free(x509);
//        return "";
//    }
//
//    gchar *tmp = BN_bn2dec(bn);
//    if(NULL == tmp){
//        LogE("unable to convert BN to decimal string, certFile = %s\n", certFile.c_str());
//        BN_free(bn);
//        X509_free(x509);
//        return "";
//    }
//
//    std::string strSn = tmp;
//    BN_free(bn);
//    OPENSSL_free(tmp);
//    X509_free(x509);
//    return strSn;
//
//
//
//
////    unsigned char * number = numObj->data;
//    LOGE("number:%s\n", number);
//
//    //建立byte数组
//    jbyteArray bytes = NULL;
//    bytes = (env)->NewByteArray(strlen(number));
//    //将char* 转换为byte数组
//    (env)->SetByteArrayRegion(bytes, 0, strlen(number), (jbyte*) number);
//    env->ReleaseStringUTFChars(cPath_, cPath);
//    //释放number
//    if(number) {
//        free(number);
//    }
//
//    return bytes;
//}
JNIEXPORT jbyteArray JNICALL
Java_com_nevs_car_jnihelp_JniHelper_getCsrNumber(JNIEnv *env, jobject instance, jstring cPath_) {
    const char *cPath = env->GetStringUTFChars(cPath_, 0);

    // TODO
    X509 *x509 = NULL;
    FILE* file = fopen(cPath, "rb");
    if (!file) {
        return (jbyteArray) "0";
    }
    x509 = PEM_read_X509(file, NULL, NULL, NULL);
    if(NULL == x509){
        return (jbyteArray) "0";
    }

    ASN1_INTEGER * numObj = X509_get_serialNumber(x509);
    BIGNUM * bn = ASN1_INTEGER_to_BN(numObj, NULL);
    char * number = BN_bn2dec(bn);



//    unsigned char * number = numObj->data;
    LOGE("number:%s\n", number);

    //建立byte数组
    jbyteArray bytes = NULL;
    bytes = (env)->NewByteArray(strlen(number));
    //将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, strlen(number), (jbyte*) number);
    env->ReleaseStringUTFChars(cPath_, cPath);
    //释放number
    if(number) {
        free(number);
    }

    return bytes;
}

#ifdef __cplusplus

}

#endif


