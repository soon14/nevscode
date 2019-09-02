package com.nevs.car.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.adapter.xrefreshview.utils.Utils;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.Des3Util;
import com.nevs.car.tools.util.DeviceUtils;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HintUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.web.AndroidUtil;
import com.nevs.car.z_start.MainActivity;
import com.nevs.car.z_start.WebIdActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SmrzIdActivity extends BaseActivity {

    @BindView(R.id.edit_idtype)
    TextView editIdtype;
    @BindView(R.id.edit_id)
    EditText editId;
    @BindView(R.id.edit_family)
    EditText editFamily;
    @BindView(R.id.backs)
    TextView backs;
    @BindView(R.id.back)
    RelativeLayout back;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String url = "";
    private List<HashMap<String, Object>> listId = new ArrayList<>();
    private String iccId = "";
    @BindView(R.id.edit_code)
    EditText editCode;
    @BindView(R.id.btn_code)
    TextView btnCode;
    private TimeCount timeCount;
    private String smsToken = "";

    @Override
    public int getContentViewResId() {
        return R.layout.activity_smrz_id;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        timeCount = new TimeCount(60000, 1000);
        initView();
        initIntent();
    }

    private void initIntent() {
        if (getIntent().getStringExtra("iccId") != null) {
            iccId = getIntent().getStringExtra("iccId");
            if (iccId.length() > 19) {
                try {
                    iccId = iccId.substring(0, 19);
                } catch (Exception e) {
                    MLog.e("iccidcd:" + iccId.length());
                }

            }
        }
        MLog.e("iccId=="+ iccId);
        if (getIntent().getStringExtra("isbindto") != null) {
            backs.setVisibility(View.VISIBLE);
            back.setVisibility(View.GONE);
        } else {
            backs.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        HintUtil.setHintSize(SmrzIdActivity.this, getResources().getString(R.string.hint_name), editFamily);
        HintUtil.setHintSize(SmrzIdActivity.this, getResources().getString(R.string.hint_idnumber), editId);
    }


    @OnClick({R.id.back, R.id.rel_idtype, R.id.edit_family, R.id.edit_id, R.id.confirm, R.id.edit_idtype, R.id.btn_code, R.id.edit_code, R.id.backs})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.rel_idtype:
                // getIdtype();
                break;
            case R.id.edit_idtype:
                //   getIdtype();
                break;
            case R.id.edit_family:
                editFamily.setCursorVisible(true);
                break;
            case R.id.edit_id:
                editId.setCursorVisible(true);
                break;
            case R.id.confirm:
                if (editFamily.getText().toString().length() == 0 || editId.getText().toString().length() == 0
                        || editIdtype.getText().toString().length() == 0 || editCode.getText().toString().trim().length() == 0
                ) {
                    ActivityUtil.showToast(this, getResources().getString(R.string.toast_allnext));
                }else if(editId.getText().toString().trim().length()<16){
                    ActivityUtil.showToast(this, getResources().getString(R.string.n_idcorrect));
                } else {
                    //startActivity(new Intent(this,SmrzCodeActivity.class));
                    getPwdReset();

                }
                break;


            case R.id.btn_code:
                getCode();
                break;
            case R.id.edit_code:
                editCode.setClickable(true);
                editCode.setCursorVisible(true);
                break;

            case R.id.backs:
                startActivity(new Intent(SmrzIdActivity.this, MainActivity.class));
                finish();
                break;
        }
    }

    private void getCode() {
        timeCount.start();
        //网路请求 短信验证
        getMessageCode();
    }

    private void IntentHtml() {
        try {
//            String aa = Des3Util.encode("13317108921");
//            String bb = Des3Util.encode("zz256331");
            String aa= Des3Util.encode("17151152666");
            String bb = Des3Util.encode("Nevs@123");
            url = Constant.INTENTHTML + "userCode=" + aa + "&passWord=" + bb;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getIdtype() {
        listId.clear();
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getIdtype(mContext,
                new String[]{"accessToken"},
                new Object[]{new SharedPHelper(mContext).get(Constant.ACCESSTOKEN, "")},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        listId.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (listId.size() == 0) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_network));
                        } else {
                            String id[] = new String[listId.size()];
                            for (int i = 0; i < listId.size(); i++) {
                                id[i] = listId.get(i).get("codeName").toString();
                            }
                            DialogUtils.showPoal(SmrzIdActivity.this, id, editIdtype);
                        }
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


    private void getMessageCode() {
        HttpRxUtils.getMessageCode(
                SmrzIdActivity.this,
                new String[]{"phone", "appType", "deviceID"},
                new Object[]{new SharedPHelper(mContext).get(Constant.LOGINNAME, ""),
                        "Android",
                        DeviceUtils.getUniqueId(SmrzIdActivity.this)
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        List<Object> list = new ArrayList<>();
                        list = (List<Object>) s;
                        smsToken = String.valueOf(list.get(0));

                    }

                    @Override
                    public void onFial(String str) {
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_message_fail));
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

    private void getPwdReset() {//接口2
        IntentHtml();
//                        MLog.e("url=="+url);
//                        MLog.e("certName=="+editFamily.getText().toString());
//                        MLog.e("url=="+url);
//                        MLog.e("url=="+url);
//                        MLog.e("url=="+url);
//                        MLog.e("url=="+url);
//                        MLog.e("url=="+url);
        String defPhone =AndroidUtil.getPhone(SmrzIdActivity.this,(String)new SharedPHelper(mContext).get(Constant.LOGINNAME, ""));
        startActivity(new Intent(SmrzIdActivity.this, WebIdActivity.class).putExtra("URL", url)
                        .putExtra("certName", editFamily.getText().toString())
                        .putExtra("certPhone",defPhone)
                        .putExtra("certNum", editId.getText().toString().trim())
                        .putExtra("iccId", iccId)
                        .putExtra("pwd","Nevs@123"));
//        DialogUtils.loading(mContext, true);
//        HttpRxUtils.getUserPwdToken(SmrzIdActivity.this,
//                new String[]{"phone", "appType", "deviceID", "smsCode", "smsToken"},
//                new Object[]{new SharedPHelper(mContext).get(Constant.LOGINNAME, "") + "", "Android",
//                        DeviceUtils.getUniqueId(SmrzIdActivity.this),
//                        editCode.getText().toString(), smsToken},
//                new HttpRxListener() {
//                    @Override
//                    public void onSucc(Object value) {
//                        DialogUtils.hidding((Activity) mContext);
//                        IntentHtml();
//                        MLog.e("url=="+url);
//                        MLog.e("certName=="+editFamily.getText().toString());
//                        MLog.e("url=="+url);
//                        MLog.e("url=="+url);
//                        MLog.e("url=="+url);
//                        MLog.e("url=="+url);
//                        MLog.e("url=="+url);
//                        String defPhone =AndroidUtil.getPhone(SmrzIdActivity.this,(String)new SharedPHelper(mContext).get(Constant.LOGINNAME, ""));
//                        startActivity(new Intent(SmrzIdActivity.this, WebIdActivity.class).putExtra("URL", url)
//                                .putExtra("certName", editFamily.getText().toString())
//                                .putExtra("certPhone",defPhone)
//                                .putExtra("certNum", editId.getText().toString().trim())
//                                .putExtra("iccId", iccId)
//                                .putExtra("pwd","Nevs@123")
//                        );
//
//                    }
//
//                    @Override
//                    public void onFial(String str) {
//                        DialogUtils.hidding((Activity) mContext);
//                        switch (str) {
//                            case Constant.HTTP.HTTPFAIL:
//                                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_network));
//                                break;
//                            case Constant.HTTP.HTTPFAILEXIT:
//                                MyUtils.exitToLongin(mContext);
//                                break;
//                            case Constant.HTTP.HTTPFAILEXITS:
//                                MyUtils.exitToLongin(mContext);
//                                break;
//                            default:
//                                ActivityUtil.showToast(mContext, str);
//                        }
//                    }
//                }
//        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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
