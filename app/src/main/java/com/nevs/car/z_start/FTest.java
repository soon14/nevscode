package com.nevs.car.z_start;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FTest {
    public static void main(String args[]) {

        long time = getStringToDates("2019-04-28" + ":00:00", "yyyy-MM-dd HH:mm:ss");
        System.out.println(time);
    }
    public static long getStringToDates(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        String timestamp =null;
        try{
            date = dateFormat.parse(dateString);
            long timeStampSec=date.getTime()/1000;
            timestamp = String.format("%010d", timeStampSec);

        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Long.parseLong(timestamp);

    }
}
