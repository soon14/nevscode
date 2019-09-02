package com.nevs.car.tools.util;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by mac on 2018/4/12.
 */

public class ToastUtils {
    private ViewGroup.LayoutParams layoutParams;

    private ToastUtils()
    {
		/* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration)
    {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration)
    {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }




    //自定义cc
    private static Toast toast;
    /**
     * 强大的可以连续弹的吐司
     * @param text
     */
    public static void showToast(String text,Context context){
        if(toast==null){
            //创建吐司对象
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        }else {
            //说明吐司已经存在了，那么则只需要更改当前吐司的文字内容
            toast.setText(text);
        }
        //最后你再show
        toast.show();
    }


    /**
     *
     * Toast工具类，解决多个Toast时长问题
     *
     */

    private static Toast toastOne;

    public synchronized static void showToastOne(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }
}
