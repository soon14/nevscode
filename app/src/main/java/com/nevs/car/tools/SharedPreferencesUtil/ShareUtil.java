package com.nevs.car.tools.SharedPreferencesUtil;

import android.content.Context;
import android.content.SharedPreferences;

import com.nevs.car.tools.encrypt.AES2;
import com.nevs.car.tools.util.MLog;

/**
 * Created by cz on 2017/8/9.
 */

public class ShareUtil {
    //加密读写
    public static void store(Context context,String text,String sharename,String insidename) {
        String afterAESEncrypt = "";
        SharedPreferences mSharedPreferences= context.getSharedPreferences(sharename, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        try{
            afterAESEncrypt = AESEncryptor.encrypt(AESEncryptor.AESkey, text);
        }catch(Exception ex){}
        editor.putString(insidename, afterAESEncrypt);
        editor.commit();
    }
    public static String read (Context context,String sharename,String insidename) {
        String afterAESdecrypt = "";
        SharedPreferences mSharedPreferences= context.getSharedPreferences(sharename, context.MODE_PRIVATE);
        String text= mSharedPreferences.getString(insidename,"");
        try{
            afterAESdecrypt  = AESEncryptor.decrypt(AESEncryptor.AESkey, text);
        }catch(Exception ex){}
        return afterAESdecrypt;
    }

    //记录系统时间
    public static void storetime(Context context,long text,String sharename,String insidename) {
        SharedPreferences mSharedPreferences= context.getSharedPreferences(sharename, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(insidename,text);
        editor.commit();
    }
    public static long readtime (Context context,String sharename,String insidename) {

        SharedPreferences mSharedPreferences= context.getSharedPreferences(sharename, context.MODE_PRIVATE);
        long text= mSharedPreferences.getLong(insidename,1l);
        return text;
    }

    //记录当前系统语言 (context,"languages","language")
    public static void storeLanguage(Context context,String text,String sharename,String fieldname) {
        SharedPreferences mSharedPreferences= context.getSharedPreferences(sharename, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(fieldname,text);
        editor.commit();
    }
    public static String readLanguage(Context context,String sharename,String fieldname) {
        SharedPreferences mSharedPreferences= context.getSharedPreferences(sharename, context.MODE_PRIVATE);
        String text= mSharedPreferences.getString(fieldname,"");
        return text;
    }

    //判断是否第一次进入APP，1为首次, 2为第二次  (context,"Isfirst","counst")
    public static void storeIsFirst(Context context,String text,String sharename,String fieldname) {
        SharedPreferences mSharedPreferences= context.getSharedPreferences(sharename, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(fieldname,text);
        editor.commit();
    }
    public static String readIsFirst(Context context,String sharename,String fieldname) {
        SharedPreferences mSharedPreferences= context.getSharedPreferences(sharename, context.MODE_PRIVATE);
        String text= mSharedPreferences.getString(fieldname,"1");
        return text;
    }

    //记录用户设置语言 //设置此时的语言值(""为未设置,"zh"为中文"cn"为英文)(context,"issettings","issetting")
    public static void storeSettingLanguage(Context context,String text,String sharename,String fieldname) {
        SharedPreferences mSharedPreferences= context.getSharedPreferences(sharename, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(fieldname,text);
        editor.commit();
    }
    public static String readSettingLanguage(Context context,String sharename,String fieldname) {
        SharedPreferences mSharedPreferences= context.getSharedPreferences(sharename, context.MODE_PRIVATE);
        String text= mSharedPreferences.getString(fieldname,"");
        return text;
    }




    //加密读写tt
    public static void storett(Context context,String text,String sharename,String insidename) {
        String afterAESEncrypt = "";
        SharedPreferences mSharedPreferences= context.getSharedPreferences(sharename, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        try{
            afterAESEncrypt = new String(new AES2().encrypt(text.getBytes(),AES2.AESkey.getBytes()));
        }catch(Exception ex){
            MLog.e("storett加密异常");
        }
        editor.putString(insidename, afterAESEncrypt);
        editor.commit();
    }
    public static String readtt(Context context,String sharename,String insidename) {
        String afterAESdecrypt = "";
        SharedPreferences mSharedPreferences= context.getSharedPreferences(sharename, context.MODE_PRIVATE);
        String text= mSharedPreferences.getString(insidename,"");
        try{
            afterAESdecrypt  = new String(new AES2().decrypt(text.getBytes(),AES2.AESkey.getBytes()));
        }catch(Exception ex){
            MLog.e("readtt解密异常");
        }
        return afterAESdecrypt;
    }



}
