package com.nevs.car.jnihelp;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mac on 2018/5/10.
 */

public class StringUtils {
    //  十六进制的字符串转换成byte数组
    /**
     * private String mstrRestartSend = "FE FE 68 04 04 68 53 FD 50 00 A0 16";
     private byte[] mRestart = null;
     mRestart = StringUtil.HexCommandtoByte(mstrRestartSend.getBytes());
     * */
    public static byte[] HexCommandtoByte(byte[] data) {
        if (data == null) {
            return null;
        }
        int nLength = data.length;

        String strTemString = new String(data, 0, nLength);
        String[] strings = strTemString.split(" ");
        nLength = strings.length;
        data = new byte[nLength];
        for (int i = 0; i < nLength; i++) {
            if (strings[i].length() != 2) {
                data[i] = 00;
                continue;
            }
            try {
                data[i] = (byte)Integer.parseInt(strings[i], 16);
            } catch (Exception e) {
                data[i] = 00;
                continue;
            }
        }

        return data;
    }



    // 字符串转换成16进制文字列的方法
    public static String toHex(String str) {
        String hexString="0123C456789";
        byte[] bytes=str.getBytes();
        StringBuilder hex=new StringBuilder(bytes.length * 2);
        for(int i=0;i<bytes.length;i++) {
            hex.append(hexString.charAt((bytes[i] & 0xf0) >> 4));  // 作用同 n / 16
            hex.append(hexString.charAt((bytes[i] & 0x0f) >> 0));  // 作用同 n
            hex.append(' ');  //中间用空格隔开
        }
        return hex.toString();
    }


    //byte转16进制
    public void byteToHex(byte inPut[], byte outPut[], int len) {

        int i = len, j = 0, tmp;
        for (i = 0; i < len; i++) {
            tmp = inPut[i];
            switch ((tmp >> 4) & 0x0f) {
                case 0:
                    outPut[j++] = '0';
                    break;
                case 1:
                    outPut[j++] = '1';
                    break;
                case 2:
                    outPut[j++] = '2';
                    break;
                case 3:
                    outPut[j++] = '3';
                    break;
                case 4:
                    outPut[j++] = '4';
                    break;
                case 5:
                    outPut[j++] = '5';
                    break;
                case 6:
                    outPut[j++] = '6';
                    break;
                case 7:
                    outPut[j++] = '7';
                    break;
                case 8:
                    outPut[j++] = '8';
                    break;
                case 9:
                    outPut[j++] = '9';
                    break;
                case 10:
                    outPut[j++] = 'A';
                    break;
                case 11:
                    outPut[j++] = 'B';
                    break;
                case 12:
                    outPut[j++] = 'C';
                    break;
                case 13:
                    outPut[j++] = 'D';
                    break;
                case 14:
                    outPut[j++] = 'E';
                    break;
                case 15:
                    outPut[j++] = 'F';
                    break;
                default:
                    break;
            }

            switch (tmp & 0x0f) {
                case 0:
                    outPut[j++] = '0';
                    break;
                case 1:
                    outPut[j++] = '1';
                    break;
                case 2:
                    outPut[j++] = '2';
                    break;
                case 3:
                    outPut[j++] = '3';
                    break;
                case 4:
                    outPut[j++] = '4';
                    break;
                case 5:
                    outPut[j++] = '5';
                    break;
                case 6:
                    outPut[j++] = '6';
                    break;
                case 7:
                    outPut[j++] = '7';
                    break;
                case 8:
                    outPut[j++] = '8';
                    break;
                case 9:
                    outPut[j++] = '9';
                    break;
                case 10:
                    outPut[j++] = 'A';
                    break;
                case 11:
                    outPut[j++] = 'B';
                    break;
                case 12:
                    outPut[j++] = 'C';
                    break;
                case 13:
                    outPut[j++] = 'D';
                    break;
                case 14:
                    outPut[j++] = 'E';
                    break;
                case 15:
                    outPut[j++] = 'F';
                    break;
                default:
                    break;
            }
        }
    }

    //byte字符串表示的16进制换成byte数组
    public static void hexToByte(byte inPut[],byte outPut[],int len) {

        int i,j=0;
        for(i=0;i<len;i++) {
            //空格的ASC码值为32
            if(inPut[i]!=' ') {
                outPut[j]=(byte) (inPut[i]*16+inPut[++i]);
            }  else {
                j++;
            }
        }
    }

    //将16进制数字解码成字符串,适用于所有字符（包括中文）
    public static String decode(String str,int len) {

        ByteArrayOutputStream baos=new ByteArrayOutputStream(len/2);
        //将每2位16进制整数组装成一个字节
        for(int i=0;i<len;i+=2) {
            baos.write((str.charAt(i))<<4 |(str.charAt(i+1)));
            int a=(str.charAt(i))<<4 |(str.charAt(i+1));
        }
        return new String(baos.toByteArray());
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式
     * 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
     * @param src String
     * @return byte[]
     **/
    public static byte[] HexString2Bytes(String src)  {
        byte[] ret = new byte[8];
        byte[] tmp = src.getBytes();
        for(int i=0; i<8; i++)  {
            ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
        }
        return ret;
    }

    /**
     * 将两个ASCII字符合成一个字节；
     * 如："EF"--> 0xEF
     * @param src0 byte
     * @param src1 byte
     * @return byte
     **/
    public static byte uniteBytes(byte src0, byte src1)  {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte)(_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte)(_b0 ^ _b1);
        return ret;
    }




    ////、、、、、、byteToHexString是把byte数组转化成16进制的数值的字符串。
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    // HexString byte

    public  static  byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123C456789".indexOf(c);
    }

    public static String getAssetsCacheFile(Context context, String fileName)   {
        File cacheFile = new File(context.getCacheDir(), fileName);
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[3072];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cacheFile.getAbsolutePath();
    }

}
