package com.nevs.car.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.ShareUtil;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DeviceUtils;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.JpushUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyToast;
import com.nevs.car.tools.util.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

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
    @BindView(R.id.edit_family)
    EditText editFamily;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.btn_create)
    TextView btnCreate;
    @BindView(R.id.iamgeis)
    ImageView iamgeis;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private TimeCount timeCount;
    private String smsToken = null;//保存短信令牌
    private String msg = "";
    private String phone = "";
    private boolean isagree = true;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_register;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        timeCount = new TimeCount(60000, 1000);

        iamgeis.setBackgroundResource(R.mipmap.fw_male);
        isagree = false;
        btnCreate.setEnabled(false);
        btnCreate.setBackgroundResource(R.drawable.btn_unpress);

    }

    @OnClick({R.id.back, R.id.edit_phonenumber, R.id.edit_code, R.id.btn_code, R.id.edit_password, R.id.centerc,
            R.id.edit_repassword, R.id.edit_family, R.id.edit_name, R.id.btn_create, R.id.privacy, R.id.iamgeis,
    })
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back://取消
                finish();
                break;
            case R.id.centerc:
                if (isagree) {
                    iamgeis.setBackgroundResource(R.mipmap.fw_male);
                    isagree = false;
                    btnCreate.setEnabled(false);
                    btnCreate.setBackgroundResource(R.drawable.btn_unpress);
                } else {
                    iamgeis.setBackgroundResource(R.mipmap.cltj_type_true);
                    isagree = true;
                    btnCreate.setEnabled(true);
                    btnCreate.setBackgroundResource(R.drawable.btn_press);
                }
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
            case R.id.edit_family://输入姓氏
                editFamily.setCursorVisible(true);
                break;
            case R.id.edit_name://输入名字
                editName.setCursorVisible(true);
                break;
            case R.id.btn_create://创建帐号
                create();
                break;
            case R.id.privacy:
                startActivity(new Intent(this, PrimaryActivity.class));
                break;
            case R.id.iamgeis:
                if (isagree) {
                    iamgeis.setBackgroundResource(R.mipmap.fw_male);
                    isagree = false;
                    btnCreate.setEnabled(false);
                    btnCreate.setBackgroundResource(R.drawable.btn_unpress);
                } else {
                    iamgeis.setBackgroundResource(R.mipmap.cltj_type_true);
                    isagree = true;
                    btnCreate.setEnabled(true);
                    btnCreate.setBackgroundResource(R.drawable.btn_press);
                }
                break;
        }
    }

    private void getCode() {
       // if (!PhoneNumberUtils.isMobileNO(editPhonenumber.getText().toString())) {
        if (editPhonenumber.getText().toString().trim().length()!=11) {
            MyToast.showToast(this, getResources().getString(R.string.toast_phonenumber));
        } else {
            timeCount.start();
            //网路请求 短信验证
            getMessageCode();
        }
    }

    private void getMessageCode() {
        list.clear();
        HttpRxUtils.getMessageCode(
                RegisterActivity.this,
                new String[]{"phone", "appType", "deviceID"},
                new Object[]{editPhonenumber.getText().toString(),
                        "Android",
                        DeviceUtils.getUniqueId(RegisterActivity.this)
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        list.addAll((Collection<? extends String>) s);
                        smsToken = list.get(0);
                        msg = list.get(1);
                        phone = editPhonenumber.getText().toString().trim();
                        MLog.e("短信验证码获取成功:" + smsToken);
                    }

                    @Override
                    public void onFial(String str) {
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(RegisterActivity.this, getResources().getString(R.string.codefail));
                                break;
                            default:
                                ActivityUtil.showToast(RegisterActivity.this, getResources().getString(R.string.codefail));
                        }
                    }
                }
        );
    }

    private void create() {


//    } else if (!Character.isLetter(editPassword.getText().toString().charAt(0))) {
//        MyToast.showToast(this, getResources().getString(R.string.toast_repasswordthree));
//else if (!MyUtils.isCEnglish(editPassword.getText().toString())) {
//            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordthree));
//        }

//else if (!MyUtils.getResult(newPassword.getText().toString(), mContext) &&
//                newPassword.getText().toString().length() != 0
//        ) {
//            // MyToast.showToast(this,getResources().getString(R.string.toast_repasswordthree));
//        }

        if (editPhonenumber.getText().toString().trim().length()!=11) {
            MyToast.showToast(this, getResources().getString(R.string.toast_phonenumber));
            return;
        }

        if (!MyUtils.isCEnglish(editPassword.getText().toString().trim())) {
            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordthree));
            return;
        }

        if(!MyUtils.isContainsNum(editPassword.getText().toString().trim())){
            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordnumber));
            return;
        }

        if (editPhonenumber.getText().toString().length() == 0 || editCode.getText().toString().length() == 0
                || editPassword.getText().toString().length() == 0 || editRepassword.getText().toString().length() == 0
                || editFamily.getText().toString().length() == 0 || editName.getText().toString().length() == 0
        ) {
            MyToast.showToast(this, getResources().getString(R.string.toast_create));
        } else if (!editPassword.getText().toString().equals(editRepassword.getText().toString())) {
            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordone));
        }  else if (editPassword.getText().toString().length() < 6) {
            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordtwo));
        } else {


            if (!phone.equals(editPhonenumber.getText().toString().trim())) {
                ActivityUtil.showToast(mContext, getResources().getString(R.string.pleasecode));
            } else if (!editCode.getText().toString().trim().equals(msg)) {
                ActivityUtil.showToast(mContext, getResources().getString(R.string.pleasecode));
            } else {
                //网络请求 注册
                getRegister();
            }


        }
    }

    private void create0() {


//    } else if (!Character.isLetter(editPassword.getText().toString().charAt(0))) {
//        MyToast.showToast(this, getResources().getString(R.string.toast_repasswordthree));

        if (editPhonenumber.getText().toString().length() == 0 || editCode.getText().toString().length() == 0
                || editPassword.getText().toString().length() == 0 || editRepassword.getText().toString().length() == 0
                || editFamily.getText().toString().length() == 0 || editName.getText().toString().length() == 0
        ) {
            MyToast.showToast(this, getResources().getString(R.string.toast_create));
        } else if (!editPassword.getText().toString().equals(editRepassword.getText().toString())) {
            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordone));
        } else if (!MyUtils.isCEnglish(editPassword.getText().toString())) {
            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordthree));
        } else if (editPassword.getText().toString().length() < 6) {
            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordtwo));
        } else {


            if (!phone.equals(editPhonenumber.getText().toString().trim())) {
                ActivityUtil.showToast(mContext, getResources().getString(R.string.pleasecode));
            } else if (!editCode.getText().toString().trim().equals(msg)) {
                ActivityUtil.showToast(mContext, getResources().getString(R.string.pleasecode));
            } else {
                //网络请求 注册
                getRegister();
            }


        }
    }

    private void getRegister() {
        /**
         * {"isSuccess":"Y","reason":"","data":{"accessToken":"8e6f7d32827c47909a539732e7dd51e9","expir":"60","nevsUserID":"b9404e8507174827ad256f1fc84150ea"}}
         * */
        DialogUtils.loading(mContext, false);
        HttpRxUtils.getRegister(
                RegisterActivity.this,
                new String[]{"loginName", "pwd", "appType", "deviceID", "familyName", "givenName", "smsCode", "smsToken"},
                new Object[]{
                        editPhonenumber.getText().toString(),
                        editPassword.getText().toString(),
                        "Android",
                        DeviceUtils.getUniqueId(RegisterActivity.this),
                        editFamily.getText().toString(),
                        editName.getText().toString(),
                        editCode.getText().toString(),
                        smsToken},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        ActivityUtil.showToast(RegisterActivity.this, getResources().getString(R.string.toast_regist_success));
                        // finish();//此处的逻辑需要确认后修改，直接登录还是---
                        login();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(RegisterActivity.this, getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.userNameExisted:
                                ActivityUtil.showToast(RegisterActivity.this, getResources().getString(R.string.toast_submitfailexisted));
                                break;
                            default:
                                ActivityUtil.showToast(RegisterActivity.this, str);
                        }
                    }
                }
        );
    }

    private void login() {

        HttpRxUtils.getLogin(
                mContext,
                new String[]{"loginName", "pwd", "appType", "deviceID"},
                new Object[]{
                        editPhonenumber.getText().toString().trim(),
                        editPassword.getText().toString(),
                        "Android",
                        DeviceUtils.getUniqueId(mContext)},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        //  JpushUtil.toJpush(LoginActivity.this, String.valueOf(new SharedPHelper(LoginActivity.this).get(Constant.LOGINNAME, "")));//极光推送给个人

                        DialogUtils.hidding((Activity) mContext);
                        ShareUtil.storett(mContext, editPassword.getText().toString(), Constant.LONGINTTS, Constant.LONGINTT);

                        new SharedPHelper(mContext).put(Constant.ISPLASHTO, "2");//是否成功登录过一次

                        new SharedPHelper(mContext).put("pin", "abcdef");

                        new SharedPHelper(mContext).put("TSPVIN", "0");

                        isDelePin(String.valueOf(s));

                        startActivity(new Intent(mContext, BindCarActivity.class).putExtra("registitent", "registitent"));
                        finish();

                        getTsp28();//TSP消息
                        JpushUtil.setTags(mContext);//极光推送
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_loginfail));
                                break;
                            default:
                                ActivityUtil.showToast(mContext, str);
                        }

                    }
                });
    }


    private void isDelePin(String s) {
        String json = s;
        try {
            JSONObject js = new JSONObject(json);
            JSONObject data = js.getJSONObject("data");
            String loginName = data.getString("loginName");
            if (new SharedPHelper(mContext).get(Constant.LONGINLASTNAME, "kong").toString().equals(editPhonenumber.getText().toString())) {

            } else {
                new SharedPHelper(mContext).put("pin", "abcdef");
                new SharedPHelper(mContext).put(Constant.LONGINLASTNAME, loginName);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getTsp28() {
        /**
         *
         * */
        TspRxUtils.getRegistration(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                new String[]{"deviceId", "deviceType", "handler"},
                new Object[]{DeviceUtils.getUniqueId(mContext), 1,
                        new SharedPHelper(mContext).get("baiduchannelId", "") + "-" + new SharedPHelper(mContext).get("baiduuserId", "")},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        MyUtils.upLogTSO(mContext, "TSP推送注册", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        MLog.e("TSP推送注册成功");
                    }

                    @Override
                    public void onFial(String str) {
                        MLog.e("TSP推送注册失败");
                        MyUtils.upLogTSO(mContext, "TSP推送注册", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

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
