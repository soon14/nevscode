package com.nevs.car.jnihelp;

import com.nevs.car.jnihelp.modle.CSRBean;
import com.nevs.car.tools.util.MLog;

/**
 * Created by mac on 2018/8/19.
 */

public class JniHelper {
    static {
        try {
            System.loadLibrary("show-lib");
        }catch (Exception e){
            MLog.e("JniHelperyic");
        }

    }

    //RSA加密
    public native boolean RSA(String pubkeyStr, byte[] inBytes, int inLen, byte[] outBytes, int outlen);

    //RSA解密
    public native int RSADECO(String pubkeyStr, byte[] inBytes, int inLen, byte[] outBytes, int outlen);


    //生成CSR
    public native byte[] CSRb(String chDN, String san);

    //生成CSR改版  传对象未成功
    public native byte[] CSRg(String pbDN, String SAN,int nDNLen, CSRBean csrBean);

    //  生成CSR改版 传的STRING成功
    public native byte[] csrGai(String pbDN, String SAN,int nDNLen,String vin,String username,String starttime,String endtime,String pin,String mobiledevicepubkey,String role,String bookingid,String userid);

    // 获取证书编号
    public native byte[] getCsrNumber(String cPath);


}