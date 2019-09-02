package com.nevs.car.tools;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * =========================================
 * 版权所有 违法必究
 * 作者: wxj.
 * <p/>
 * 工程: SwipeLayoutz11.
 * <p/>
 * 文件名: ToastUtil.
 * <p/>
 * 时间: 04/05 0005.
 * <p/>
 * 修订历史:
 * <p/>
 * 修订时间:
 * <p/>
 * =========================================
 */
public class ToastUtil {

    private static Toast toast;
    private static Toast toastCus;

    public static void showToast(Context context, String text) {
        if (toast == null)
            toast = Toast.makeText(context, text,Toast.LENGTH_SHORT);
        toast.setText(text);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public static void showLongToast(Context context, String text) {
        if (toast == null)
            toast = Toast.makeText(context, text,Toast.LENGTH_LONG);
        toast.setText(text);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }



}
