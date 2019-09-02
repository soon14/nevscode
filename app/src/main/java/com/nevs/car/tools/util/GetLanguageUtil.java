package com.nevs.car.tools.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by mac on 2018/4/20.
 */

public class GetLanguageUtil {
    /**
     * 获取系统语言
     * "zh"为中文，"cn"为英文
     * */
    public static String getLanguage(){
        Locale locale=Locale.getDefault();
        String language=locale.getLanguage();
        MLog.e("此时的系统语言为："+language);
        return language;
    }

    /**
     * 设置英文
     *
     * *@param v
     */
    public static void cn(Context context) {
        Resources resources = context.getResources();// 获得res资源对象
        Configuration config = resources.getConfiguration();// 获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.locale = Locale.ENGLISH; // 英文
        resources.updateConfiguration(config, dm);
    }

    /**
     * 设置中文
     *
     *
     */
    public static void zh(Context context) {
        Resources resources = context.getResources();// 获得res资源对象
        Configuration config = resources.getConfiguration();// 获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.locale = Locale.CHINA; // 中文
        resources.updateConfiguration(config, dm);
    }
}
