package com.nevs.car.tools.util;

import java.security.Key;  

import javax.crypto.Cipher;  
import javax.crypto.SecretKeyFactory;  
import javax.crypto.spec.DESedeKeySpec;  
import javax.crypto.spec.IvParameterSpec; 

/**
 * 
 * 版权所有：2015-美库网
 * 项目名称：mrrck-web   
 *
 * 类描述：3DES加密工具类 
 * 类名称：com.base.util.Des3     
 * 创建人：仲崇生
 * 创建时间：2015-11-12 下午02:14:28   
 * @version V1.0
 */
public class Des3Util {
	
	
    // 密钥  
    private final static String secretKey = "uniteAppStaryea@12345678";
    // 向量  
    private final static String iv = "01234567";  
    // 加解密统一使用的编码方式  
    private final static String encoding = "utf-8";  
  
    /** 
     * 3DES加密 
     *  
     * @param plainText 普通文本 
     * @return 
     * @throws Exception  
     */  
    public static String encode(String plainText) throws Exception {  
        Key deskey = null;  
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());  
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");  
        deskey = keyfactory.generateSecret(spec);  
  
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");  
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());  
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);  
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));  
        return Base64.encode(encryptData);  
    }  
  
    /** 
     * 3DES解密 
     *  
     * @param encryptText 加密文本 
     * @return 
     * @throws Exception 
     */  
    public static String decode(String encryptText) throws Exception {  
        Key deskey = null;  
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());  
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");  
        deskey = keyfactory.generateSecret(spec);  
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");  
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());  
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);  
  
        byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));  
  
        return new String(decryptData, encoding);  
    }
    
    public static void main(String args[]){
        try {
            String arg = Des3Util.encode("{'imei':'发送方式','imsi':'16789'}");
            System.out.println(arg);
            System.out.println(Des3Util.decode("TrtnNK8zyokQuZWy7M4D1C73cPNUZPasQntZdj0n3ZpYRWzkRiuQmjOrCDBJ hQWlyHK3tUu9WXtL2WEyy386GVWgyZzyqvmA1xEHBjTFhqFdff8dYN7i+UTS 0EXfC1l9DpZFslaRJS65FEubO/m0sg=="));
        } catch (Exception e) {
        }
    }
}
