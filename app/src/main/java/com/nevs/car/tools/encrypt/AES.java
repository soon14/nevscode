package com.nevs.car.tools.encrypt;

/**
 * Created by mac on 2018/5/7.
 */

import com.nevs.car.R;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelpers;
import com.nevs.car.z_start.MyApp;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author ngh
 * AES128 算法
 *
 * CBC 模式
 *
 * PKCS7Padding 填充模式
 *
 * CBC模式需要添加一个参数iv
 *
 * 介于java 不支持PKCS7Padding，只支持PKCS5Padding 但是PKCS7Padding 和 PKCS5Padding 没有什么区别
 * 要实现在java端用PKCS7Padding填充，需要用到bouncycastle组件来实现
 */
public class AES {
    // 算法名称
    final String KEY_ALGORITHM = "AES";
    // 加解密算法/模式/填充方式
    final String algorithmStr = "AES/CBC/PKCS7Padding";
    //
   // public String key0= MyApp.getInstance().getResources().getString(R.string.clear_or_fill_datak);
    public String key0= String.valueOf(new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0").toString()).get(MyApp.getInstance().getResources().getString(R.string.SymmEnccc),""));
    private Key key;//CC注意倒包
    private Cipher cipher;
    boolean isInited = false;

   // byte[] iv = { 0x30, 0x31, 0x30, 0x32, 0x30, 0x33, 0x30, 0x34, 0x30, 0x35, 0x30, 0x36, 0x30, 0x37, 0x30, 0x38 };

    //protected String iv0=MyApp.getInstance().getResources().getString(R.string.clear_or_fill_datav);
    protected String iv0=String.valueOf(new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0").toString()).get(MyApp.getInstance().getResources().getString(R.string.InitVector),""));
    byte[] iv=iv0.getBytes();
    public void init(byte[] keyBytes) {

        // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
        int base = 16;
        if (keyBytes.length % base != 0) {
            int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        }
        // 初始化
        Security.addProvider(new BouncyCastleProvider());
        // 转化成JAVA的密钥格式
        key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        try {
            // 初始化cipher
            cipher = Cipher.getInstance(algorithmStr, "BC");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 加密方法
     *
     * @param content
     *            要加密的字符串
     * @param keyBytes
     *            加密密钥
     * @return
     */
    public byte[] encrypt(byte[] content, byte[] keyBytes) {
        byte[] encryptedText = null;
        init(keyBytes);
        System.out.println("IV：" + new String(iv));
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(content);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encryptedText;
    }
    /**
     * 解密方法
     *
     * @param encryptedData
     *            要解密的字符串
     * @param keyBytes
     *            解密密钥
     * @return
     */
    public byte[] decrypt(byte[] encryptedData, byte[] keyBytes) {
        byte[] encryptedText = null;
        init(keyBytes);
        System.out.println("IV：" + new String(iv));
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(encryptedData);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encryptedText;
    }
}



/**
 * public class FTest {
 public static void main(String[] args) {
 AES aes = new AES();
 //   加解密 密钥
 byte[] keybytes = { 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38 };
 String content = "1";
 // 加密字符串
 System.out.println("加密前的：" + content);
 System.out.println("加密密钥：" + new String(keybytes));
 // 加密方法
 byte[] enc = aes.encrypt(content.getBytes(), keybytes);
 System.out.println("加密后的内容：" + new String(Hex.encode(enc)));
 // 解密方法
 byte[] dec = aes.decrypt(enc, keybytes);
 System.out.println("解密后的内容：" + new String(dec));
 }


 *
 * */

/**
 * 测试结果：

 加密前的：1
 加密密钥：12345678
 IV：0102030405060708
 加密后的内容：b59227d86200d7fedfb8418a59a8eea9
 IV：0102030405060708
 解密后的内容：1


 * */