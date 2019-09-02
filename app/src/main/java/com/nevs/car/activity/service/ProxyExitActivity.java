package com.nevs.car.activity.service;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProxyExitActivity extends BaseActivity {


    @BindView(R.id.text_reason)
    EditText textReason;
    @BindView(R.id.n_view)
    RelativeLayout nView;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_proxy_exit;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.next})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.next:
                if (textReason.getText().toString().trim().length() == 0) {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_writeall));
                } else {
                    getHttp();
                }
                break;
        }
    }

    private void getHttp() {
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getPaUnPorxy(ProxyExitActivity.this,
                new String[]{"accessToken", "reason"},
                new Object[]{new SharedPHelper(ProxyExitActivity.this).get(Constant.ACCESSTOKEN, ""),
                        textReason.getText().toString()
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        //  ActivityUtil.showToast(UserSpeakActivity.this,getResources().getString(R.string.toast_submitsuccess));
                        new SharedPHelper(mContext).put(Constant.LOGINISPA, "Und");
                        DialogUtils.NormalDialogOneBtnHintnoEixt(ProxyExitActivity.this, getResources().getString(R.string.toast_pleasesuc));

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(ProxyExitActivity.this, getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(mContext);
                                break;
                            default:
                                if (str.contains("RepeatApplicationPA")) {
                                    ActivityUtil.showToast(ProxyExitActivity.this, getResources().getString(R.string.toast_unrepeat));
                                } else {
                                    ActivityUtil.showToast(ProxyExitActivity.this, str);
                                }
                        }
                    }
                }
        );
    }
}
