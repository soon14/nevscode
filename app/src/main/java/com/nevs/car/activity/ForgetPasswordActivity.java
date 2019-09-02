package com.nevs.car.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DeviceUtils;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyToast;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ForgetPasswordActivity extends BaseActivity {
    @BindView(R.id.edit_phonenumber)
    EditText editPhonenumber;
    @BindView(R.id.edit_code)
    EditText editCode;
    @BindView(R.id.btn_code)
    TextView btnCode;
    @BindView(R.id.edit_password)
    EditText editPassword;
    @BindView(R.id.edit_repassword)
    EditText editRepassword;
    @BindView(R.id.btn_create)
    TextView btnCreate;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private TimeCount timeCount;
    private String smsToken = null;//保存短信令牌
    private String userPwdToken = null;//保存userPwdToken

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_forget_password;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        timeCount = new TimeCount(60000, 1000);
    }

    @OnClick({R.id.back, R.id.edit_phonenumber, R.id.edit_code, R.id.btn_code,
            R.id.edit_password, R.id.edit_repassword, R.id.btn_create
    })
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back://取消
                finish();
                break;
            case R.id.edit_phonenumber://输入手机号
                editPhonenumber.setCursorVisible(true);
                break;
            case R.id.edit_code://输入验证码
                editCode.setCursorVisible(true);
                break;
            case R.id.btn_code://获取验证码
                getCode();//获取验证码网路请求，手机号合法性验证
                break;
            case R.id.edit_password://输入密码
                editPassword.setCursorVisible(true);
                break;
            case R.id.edit_repassword://确认密码
                editRepassword.setCursorVisible(true);
                break;
            case R.id.btn_create://创建密码
                confirm();
                break;
        }
    }

    private void getCode() {
        if(editPhonenumber.getText().toString().length()==0){
            MyToast.showToast(this, getResources().getString(R.string.edit2));
            return;
        }
        if (!PhoneNumberUtils.isMobileNO(editPhonenumber.getText().toString())) {
            MyToast.showToast(this, getResources().getString(R.string.toast_phonenumber));
        } else {
            timeCount.start();
            //网路请求
            getMessageCode();
        }
    }

    private void getMessageCode() { //接口1
        HttpRxUtils.getMessageCode(
                ForgetPasswordActivity.this,
                new String[]{"phone", "appType", "deviceID"},
                new Object[]{editPhonenumber.getText().toString(),
                        "Android",
                        DeviceUtils.getUniqueId(ForgetPasswordActivity.this)
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        List<Object> list = new ArrayList<>();
                        list = (List<Object>) s;
                        smsToken = String.valueOf(list.get(0));
                        MLog.e("短信验证码获取成功:" + smsToken);
                    }

                    @Override
                    public void onFial(String str) {
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.forget_pwd_phone));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            default:
                                ActivityUtil.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.forget_pwd_phone));
                        }
                    }
                }
        );
    }

    private void confirm() {


        if (editPhonenumber.getText().toString().length() == 0 || editCode.getText().toString().length() == 0
                || editPassword.getText().toString().length() == 0 || editRepassword.getText().toString().length() == 0
        ) {
            MyToast.showToast(this, getResources().getString(R.string.toast_create));
        } else if (!editPassword.getText().toString().equals(editRepassword.getText().toString())) {
            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordone));
        } else if (!MyUtils.isCEnglish(editPassword.getText().toString())) {
            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordthree));
        } else if (editPassword.getText().toString().length() < 6) {
            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordtwo));
        } else {
            if (!MyUtils.isCEnglish(editPassword.getText().toString().trim())) {
                MyToast.showToast(this, getResources().getString(R.string.toast_repasswordthree));
                return;
            }

            if(!MyUtils.isContainsNum(editPassword.getText().toString().trim())){
                MyToast.showToast(this, getResources().getString(R.string.toast_repasswordnumber));
                return;
            }
            //网络请求
            getPwdReset();//获取userPwdToken，后再调用ForgetPassWord()，相当于总共有三个接口
        }
    }

    private void getPwdReset() {//接口2
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getUserPwdToken(ForgetPasswordActivity.this,
                new String[]{"phone", "appType", "deviceID", "smsCode", "smsToken"},
                new Object[]{editPhonenumber.getText().toString(), "Android",
                        DeviceUtils.getUniqueId(ForgetPasswordActivity.this),
                        editCode.getText().toString(), smsToken},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        userPwdToken = (String) s;
                        MLog.e("userPwdToken:" + s);
                        forgetPassWord();//最后的确认请求
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case Constant.HTTP.HTTPMSG:
                                ActivityUtil.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.forget_pwd_msg));
                                break;
                            default:
                                ActivityUtil.showToast(ForgetPasswordActivity.this, str);
                        }
                    }
                }
        );
    }

    private void forgetPassWord() {//接口3
        HttpRxUtils.getUpDateUserPwd(ForgetPasswordActivity.this,
                new String[]{"userPwdToken", "newPwd"},
                new Object[]{userPwdToken, editRepassword.getText().toString(),},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.toast_forgetsuccess));
                        finish();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case Constant.HTTP.HTTPMSG:
                                ActivityUtil.showToast(ForgetPasswordActivity.this, getResources().getString(R.string.forget_pwd_msg));
                                break;
                            default:
                                ActivityUtil.showToast(ForgetPasswordActivity.this, str);
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
