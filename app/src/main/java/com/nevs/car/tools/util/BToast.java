package com.nevs.car.tools.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nevs.car.R;

public class BToast extends Toast {
    /**
     * Toast单例
     */
    private static BToast toast;

    /**
     * 构造
     *
     * @param context
     */
    public BToast(Context context) {
        super(context);
    }


    /**
     * 隐藏当前Toast
     */
    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
            toast=null;
        }
    }

    public void cancel() {
        try {
            super.cancel();
        } catch (Exception e) {

        }
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {

        }
    }
    /**
     * 初始化Toast
     *
     * @param context 上下文
     * @param text    显示的文本
     */
    private static void initToast(Context context, CharSequence text) {
        try {
            cancelToast();
            toast = new BToast(context);

            // 获取LayoutInflater对象
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 由layout文件创建一个View对象
            View layout = inflater.inflate(R.layout.toast_layout, null);

            // 吐司上的图片
//            toast_img = (ImageView) layout.findViewById(R.id.toast_img);

            // 吐司上的文字
            TextView toast_text = (TextView) layout.findViewById(R.id.text);
            toast_text.setText(text);
            toast.setView(layout);
           // toast.setGravity(Gravity.CENTER, 0, 70);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void showToast(Context context, CharSequence text,boolean time) {
        // 初始化一个新的Toast对象
        initToast(context, text);

        // 设置显示时长
        if (time) {
            toast.setDuration(Toast.LENGTH_LONG);
        } else {
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        // 显示Toast
        toast.show();
    }
}