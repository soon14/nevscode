package com.nevs.car.tools.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.widget.EditText;

import com.nevs.car.R;

/**
 * Created by mac on 2018/6/5.
 */

public class HintUtil {
    public static void setHintSize(Context context,String hintStr, EditText editText){
        SpannableString ss =  new SpannableString(hintStr);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);//是否使用DP
        editText.setHintTextColor(context.getResources().getColor(R.color.line));
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannedString(ss));
    }
}
