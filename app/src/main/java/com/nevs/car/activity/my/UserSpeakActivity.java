package com.nevs.car.activity.my;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.ShareUtil;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.GetLanguageUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class UserSpeakActivity extends BaseActivity {

    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    @BindView(R.id.txt_size)
    TextView txt_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_user_speak;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView, mContext);
        if(isCn()){
            txt_size.setText("1-240");
            content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(240){}});
        }else{
            txt_size.setText("1-200");
            content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1-200){}});

        }
    }

    @OnClick({R.id.back, R.id.btn_createspeak, R.id.content})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_createspeak:
                if (content.getText().toString().trim().length() == 0) {
                    ActivityUtil.showToast(UserSpeakActivity.this, getResources().getString(R.string.toast_writeall));
                } else {
                    getHttp();
                }

                break;
            case R.id.content:
                content.setCursorVisible(true);
                break;
        }
    }

    private void getHttp() {
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getUserFeedBack(UserSpeakActivity.this,
                new String[]{"accessToken", "usercode", "content"},
                new Object[]{new SharedPHelper(UserSpeakActivity.this).get(Constant.ACCESSTOKEN, ""),
                        new SharedPHelper(UserSpeakActivity.this).get(Constant.LOGINNAME, ""), content.getText().toString(),
                        content.getText().toString()
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        //  ActivityUtil.showToast(UserSpeakActivity.this,getResources().getString(R.string.toast_submitsuccess));
                        DialogUtils.NormalDialogOneBtnHint(UserSpeakActivity.this, getResources().getString(R.string.toast_speaksuc), content, UserSpeakActivity.this);

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(mContext);
                                break;
                            default:
                                ActivityUtil.showToast(mContext, str);
                        }
                    }
                }
        );
    }


    private boolean isCn() {

        //判断用户是否是第一次登陆，第一次登陆使用系统的设置，如果不设置还是跟谁系统，如果设置了以后都用应用自己设置的语言。

        //获取是否第一次进入APP的状态值
        String isFisrst = ShareUtil.readIsFirst(mContext, "Isfirst", "counst");
        MLog.e("第" + isFisrst + "次进入APP");
        //获取系统语言
        String able = GetLanguageUtil.getLanguage();
        boolean isCn = false;
        if (isFisrst.equals("1")) {
            //将APP的进入状态改为"2"
            ShareUtil.storeIsFirst(mContext, "2", "Isfirst", "counst");
            switch (able){//"zh"为中文，"cn"为英文
                case "zh":
                    isCn=false;
                    break;
                case "cn":
                    isCn=true;
                    break;
            }

        } else if (isFisrst.equals("2")) {
            //获取用户设置语言状态,没有更改还是根据系统设置语言，如果改了就设为用户之前设置的语言
            String isSetting = ShareUtil.readSettingLanguage(mContext, "issettings", "issetting");
            //此时设置成哪种语言了需要在APP语言的设置界面写入缓存
            switch (isSetting) {//""为未设置,"zh"为中文"cn"为英文
                case "":
                    MLog.e("11");
                    switch (able) {//"zh"为中文，"cn"为英文
                        case "zh":
                            isCn= false;
                        break;
                        case "cn":
                            isCn=true;
                        break;
                    }
                    break;
                case "zh":
                    isCn=false;
                break;
                case "cn":
                    isCn=true;
                break;
            }
        }
        return isCn;
    }
}
