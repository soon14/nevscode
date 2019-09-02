package com.nevs.car.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DeviceUtils;
import com.nevs.car.tools.util.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SmrzCodeActivity extends BaseActivity {

    @BindView(R.id.edit_code)
    EditText editCode;
    @BindView(R.id.btn_code)
    TextView btnCode;
    @BindView(R.id.n_view)
    RelativeLayout nView;

    private TimeCount timeCount;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_smrz_code;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        timeCount = new TimeCount(60000, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.btn_code, R.id.btn_create, R.id.edit_code})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_code:
                getCode();
                break;
            case R.id.btn_create:

                break;
            case R.id.edit_code:
                editCode.setClickable(true);
                break;
        }
    }

    private void getCode() {
        timeCount.start();
        //网路请求 短信验证
        getMessageCode();
    }

    private void getMessageCode() {
        HttpRxUtils.getMessageCode(
                SmrzCodeActivity.this,
                new String[]{"phone", "appType", "deviceID"},
                new Object[]{new SharedPHelper(mContext).get(Constant.LOGINNAME, ""),
                        "Android",
                        DeviceUtils.getUniqueId(SmrzCodeActivity.this)
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {

                    }

                    @Override
                    public void onFial(String str) {
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(SmrzCodeActivity.this, getResources().getString(R.string.codefail));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            default:
                                ActivityUtil.showToast(SmrzCodeActivity.this, getResources().getString(R.string.codefail));
                        }
                    }
                }
        );
    }


    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (btnCode != null) {
                btnCode.setClickable(false);
                btnCode.setBackgroundResource(R.drawable.bg_cicle_rected_code);
                btnCode.setText(millisUntilFinished / 1000 + getResources().getString(R.string.toast_seconds));
            }
        }

        @Override
        public void onFinish() {
            if (btnCode != null) {
                btnCode.setText(getResources().getString(R.string.toast_resent));
                btnCode.setBackgroundResource(R.drawable.bg_circle_rect_code);
                btnCode.setClickable(true);
            }
        }
    }
}
