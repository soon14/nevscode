package com.nevs.car.tools.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.z_start.MyApp;

/**
 * Created by mac on 2018/8/22.
 */

public class FinancialIOSTipsDialog2 extends Dialog {
    private String mTitle = "";
    private int mBtnCount = 2;
    private String mBtnTxt = "";
    private View.OnClickListener mListenerLeft;
    private View.OnClickListener mListenerRight;


    public FinancialIOSTipsDialog2(Context context) {
        super(context);
    }

    public FinancialIOSTipsDialog2(Context context, int themeResId) {
        super(context, themeResId);
    }

    public FinancialIOSTipsDialog2(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public FinancialIOSTipsDialog2(Context context, String title, String btnTxt, int btnCount, View.OnClickListener listenerLeft, View.OnClickListener listenerRight) {
        super(context, R.style.selectorDialog);//R.style.selectorDialog
        this.mBtnCount = btnCount;
        this.mListenerLeft = listenerLeft;
        this.mListenerRight = listenerRight;
        this.mTitle = title;
        this.mBtnTxt = btnTxt;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ios2);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        TextView content = (TextView) findViewById(R.id.dialog_tv_content);
        TextView left = (TextView) findViewById(R.id.dialog_left_tv);
        TextView right = (TextView) findViewById(R.id.dialog_right_tv);
        View line = findViewById(R.id.view_line);

        content.setTextSize(16);
        content.setText(mTitle);
        switch (mBtnCount) {
            case 1:
                right.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(mBtnTxt)) {
                    left.setText(mBtnTxt);
                } else {
                    left.setText(MyApp.getInstance().getResources().getString(R.string.opennotify));
                }
                if (mListenerLeft != null) {
                    left.setOnClickListener(mListenerLeft);
                }

                break;
            case 2:
                left.setText(MyApp.getInstance().getResources().getString(R.string.opennotify));
                right.setText(MyApp.getInstance().getResources().getString(R.string.comm_cancel));
                if (mListenerLeft != null && mListenerRight != null) {
                    left.setOnClickListener(mListenerLeft);
                    right.setOnClickListener(mListenerRight);
                }

                break;
        }


    }
}
