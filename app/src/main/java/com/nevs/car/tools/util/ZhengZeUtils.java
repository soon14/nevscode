package com.nevs.car.tools.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mac on 2018/7/5.
 */

public class ZhengZeUtils {
    /**
     * 手机号号段校验，
     * 第1位：1；
     * 第2位：{3、4、5、6、7、8}任意数字；
     * 第3—11位：0—9任意数字
     *
     * @param value
     * @return
     */
    public static boolean isTelPhoneNumber(String value) {
        if (value != null && value.length() == 11) {
            Pattern pattern = Pattern.compile("^1[3|4|5|6|7|8][0-9]\\d{8}$");
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        }
        return false;
    }

    /**
     * 验证输入的名字是否为“中文”或者是否包含“·”
     * 这里验证姓名，用户可以在输入框内输入任何东西，但是在点击验证的按钮时，会调这个方法。

     验证规则是：姓名由汉字或汉字加“•”、"·"组成，而且，“点”只能有一个，“点”的位置不能在首位也不能在末尾，只有在汉字之间才会验证通过。
     */
    public static boolean isLegalName(String name) {
        if (name.contains("·") || name.contains("•")) {
            if (name.matches("^[\\u4e00-\\u9fa5]+[·•][\\u4e00-\\u9fa5]+$")) {
                return true;
            } else {
                return false;
            }
        } else {
            if (name.matches("^[\\u4e00-\\u9fa5]+$")) {
                return true;
            } else {
                return false;
            }
        }
    }


    /**
     * 验证输入的身份证号是否合法
     * 规则是：由15位数字或18位数字（17位数字加“x”）组成，15位纯数字没什么好说的，18位的话，可以是18位纯数字，或者17位数字加“x”
     */
    public static boolean isLegalId(String id){
        if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")){
            return true;
        }else {
            return false;
        }
    }


    //邮箱验证
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }
}