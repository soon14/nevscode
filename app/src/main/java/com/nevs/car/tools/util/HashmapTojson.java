package com.nevs.car.tools.util;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by mac on 2018/4/8.
 */

public class HashmapTojson {
    public static String getJson(String keys[],Object values[]){
        Map<String,Object> map=new LinkedHashMap<>();
        map.clear();
        for(int i=0;i<keys.length;i++){
            map.put(keys[i],values[i]);
        }
        return new Gson().toJson(map);
    }


    public static String getJsonss(String keys[],Object values[]){
        Map<String,Object> map=new HashMap<>();
        map.clear();
        for(int i=0;i<keys.length;i++){
            map.put(keys[i],values[i]);
        }
        return new Gson().toJson(map);
    }
    public static HashMap<String,Object> getHashMap(String keys[],Object values[]){
        Map<String,Object> map=new HashMap<>();
        map.clear();
        for(int i=0;i<keys.length;i++){
            map.put(keys[i],values[i]);
        }
        return (HashMap<String, Object>) map;
    }

    public static long getTime(){//得到秒数 10
        long timeStampSec = System.currentTimeMillis()/1000;
       // long timeStampSec=new Date().getTime()/1000;
        String timestamp = String.format("%010d", timeStampSec);
        return Long.parseLong(timestamp);
    }

    public static String getTime1(String s){//"yyyy-MM-dd HH:mm:ss"
        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format=new SimpleDateFormat(s);
        Date d1=new Date(time);
        String t1=format.format(d1);
        MLog.e("当前格式化时间："+t1);
        return t1;
    }








    /**
     * 1，日期格式：String dateString = "2017-06-20 10:30:30" 对应的格式：String pattern = "yyyy-MM-dd HH:mm:ss";  yyyy/MM/dd HH:mm:ss

     2，日期格式：String dateString = "2017-06-20" 对应的格式：String pattern = "yyyy-MM-dd";

     3，日期格式：String dateString = "2017年06月20日 10时30分30秒 对应的格式：String pattern = "yyyy年MM月dd日 HH时mm分ss秒";


     4，日期格式：String dateString = "2017年06月20日" 对应的格式：String pattern = "yyyy年MM月dd日";
     * */
    /**
     * 获取系统时间戳
     * @return
     */
    public static long getCurTimeLong(){
        long time=System.currentTimeMillis();
        return time;
    }
    /**
     * 获取当前时间
     * @param pattern
     * @return
     */
    public static String getCurDate(String pattern){
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        return sDateFormat.format(new java.util.Date());
    }

    /**
     * 时间戳转换成字符窜
     * @param milSecond
     * @param pattern
     * @return
     * milSecond 为毫秒
     */
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 将字符串转为时间戳 毫秒
     * @param dateString
     * @param pattern
     * @return
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

    //将字符串转为时间戳得到秒数
    public static long getStringToDates(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        String timestamp =null;
        try{
            date = dateFormat.parse(dateString);
            long timeStampSec=date.getTime()/1000;
            timestamp = String.format("%010d", timeStampSec);
            MLog.e("秒数q："+timestamp);

        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Long.parseLong(timestamp);

    }



    public static long getTimeSecond(long time){//得到秒数 10  //time为毫秒
        long timeStampSec = time/1000;
        // long timeStampSec=new Date().getTime()/1000;
        String timestamp = String.format("%010d", timeStampSec);
        return Long.parseLong(timestamp);
    }

    //"yyyy/MM/dd HH:mm:ss"
    public static String getTimez(String s,String zz){//解决TSP异常返回  1.529999015E9
        String timez="";
        try {
            String a[] = s.split("E");
            long i= (long) (Double.parseDouble(a[0])*1000000000);
            MLog.e("时间戳转化后："+i);
            timez=HashmapTojson.getDateToString(i*1000,zz);
        }catch (Exception e){
            long i=Long.parseLong(s);
            MLog.e("时间戳转化后："+i);
            timez=HashmapTojson.getDateToString(i*1000,zz);
        }

        return timez;
    }


    //CSR时间转换
    /**
     * 时间戳转换成字符窜
     * @param milSecond
     * @return
     * milSecond 为毫秒
     */
    public static String getCSRTime(String milSecond){
        Date date = new Date(Long.parseLong(milSecond));
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }
}
