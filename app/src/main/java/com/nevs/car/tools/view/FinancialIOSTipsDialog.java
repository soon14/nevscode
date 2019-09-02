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


/**
 * Created by Administrator on 2015/12/11.
 */
public class FinancialIOSTipsDialog extends Dialog {
    private String mTitle = "";
    private int mBtnCount = 2;
    private String mBtnTxt = "";
    private View.OnClickListener mListenerLeft;
    private View.OnClickListener mListenerRight;
    private  Context mcontect;


    public FinancialIOSTipsDialog(Context context) {
        super(context);
    }

    public FinancialIOSTipsDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public FinancialIOSTipsDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public FinancialIOSTipsDialog(Context context, String title, String btnTxt, int btnCount, View.OnClickListener listenerLeft, View.OnClickListener listenerRight) {
        super(context, R.style.selectorDialog);//R.style.selectorDialog
        this.mBtnCount = btnCount;
        this.mListenerLeft = listenerLeft;
        this.mListenerRight = listenerRight;
        this.mTitle = title;
        this.mBtnTxt = btnTxt;
        this.mcontect=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ios);
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
                    left.setText(mcontect.getResources().getString(R.string.for_confirm));
                }
                if (mListenerLeft != null) {
                    left.setOnClickListener(mListenerLeft);
                }

                break;
            case 2:
                left.setText(mcontect.getResources().getString(R.string.for_confirm));
                right.setText(mcontect.getResources().getString(R.string.cancel));
                if (mListenerLeft != null && mListenerRight != null) {
                    left.setOnClickListener(mListenerLeft);
                    right.setOnClickListener(mListenerRight);
                }

                break;
        }


    }
}
