package com.nevs.car.tools.util;


import android.util.Log;

public class LogUtil {

    private LogUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug =true;//是否需要打印bug，可以在application的onCreate函数里面初始化

    private static final String TAG = "http_";
    public static void e(String tag,Object obj){
        if(isDebug){
            Log.e(TAG+tag,String.valueOf(obj));
        }
    }
    public static void e(String tag,Object... objs){
        StringBuilder sBuilder= new StringBuilder();
        for (Object obj:objs){
            sBuilder.append(obj);
        }
        if(isDebug){
            Log.e(TAG+tag,sBuilder.toString());
        }
    }
    public static void e(String tag,Throwable e,Object obj){
        if(isDebug){
            Log.e(TAG+tag,String.valueOf(obj),e);
        }
    }
    public static void e(String tag,Throwable e,Object... objs){
        StringBuilder sBuilder= new StringBuilder();
        for (Object obj:objs){
            sBuilder.append(obj);
        }
        if(isDebug){
            Log.e(TAG+tag,sBuilder.toString(),e);
        }
    }

}
