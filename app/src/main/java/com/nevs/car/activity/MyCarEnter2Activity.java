package com.nevs.car.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.interfaces.DialogHintListener;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.SwitchButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MyCarEnter2Activity extends BaseActivity {

    @BindView(R.id.text_type)
    TextView textType;
    @BindView(R.id.text_car)
    TextView textCar;
    @BindView(R.id.text_carf)
    TextView textCarf;
    @BindView(R.id.text_stop)
    TextView textStop;
    @BindView(R.id.edit_charges)
    TextView editCharges;
    @BindView(R.id.text_number)
    TextView textNumber;
    @BindView(R.id.edit_alias)
    EditText editAlias;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_uncar)
    TextView tvUncar;
    @BindView(R.id.lin_main)
    LinearLayout linMain;
    @BindView(R.id.lin_three)
    LinearLayout linThree;
    @BindView(R.id.edit_code)
    EditText editCode;
    @BindView(R.id.btn_code)
    TextView btnCode;
    @BindView(R.id.switch_button)
    SwitchButton switchButton;
    @BindView(R.id.confirm)
    TextView confirm;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    @BindView(R.id.setting)
    RelativeLayout setting;
    private String vin = "";
    private List<HashMap<String, Object>> list = new ArrayList<>();
    private TimeCount timeCount;
    private int ii = 0;
    private String alias = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_my_car_enter;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // MyUtils.setPadding(nView,mContext);
        confirm.setVisibility(View.INVISIBLE);
        initIntent();
        initJson();
        //  initSwitch();
    }

    private void initIntent() {
        vin = new SharedPHelper(this).get("TSPVIN", "0") + "";
        MLog.e("跳转:" + vin);
        setting.setVisibility(View.INVISIBLE);
        SpannableString s = new SpannableString("");//这里输入自己想要的提示文字
        editAlias.setHint(s);

    }

    private void initJson() {
        //   MyUtils.xJson(new SharedPHelper(mContext).get("LOGINJSONSSCAR", "") + "", list);
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        ArrayList lista=bundle.getParcelableArrayList("list");
//        list = (ArrayList<HashMap<String, Object>>) lista.get(0);
//        MLog.e("bbb:"+list.size());
        getTsp6();

    }

    private void getTsp6() {
        /**
         * {"resultMessage":null,"resultDescription":null,"items":[{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPSBSIMULATOR002","relationType":"车主"},{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"车主"}]}
         * 获取车辆列表cccc:{"items":[{"bindingId":"685a0ea8b1274cba8e50cfc7a4099ce1","vin":"LTPSBSIMULATOR001","iccid":"89860317352002707001","msisdn":"17317229263","bleAddress":"ble","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null},{"bindingId":"c084b4962c194f8abade11dce9819b09","vin":"LTPCHINATELE00123","iccid":"89860317352002707004","msisdn":"14918158628","bleAddress":"","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null}],"resultMessage":"","resultDescription":""}
         * */
        DialogUtils.loading(mContext, true);
        list.clear();
        HttpRxUtils.getCarList(mContext,
                new String[]{"appType", "accessToken", "nevsAccessToken"},
                new Object[]{"Android", new SharedPHelper(mContext).get(Constant.ACCESSTOKEN, "").toString(), new SharedPHelper(mContext).get(Constant.REGISTCENACCESSTOKEN, "").toString()},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (list.size() != 0) {
                            MLog.e("长度：" + list.size() + ": " + list.get(0).get("vin"));
                            for (int i = 0; i < list.size(); i++) {
                                if (vin.equals(list.get(i).get("vin"))) {
                                    upView(i);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        if (str.contains("401")) {
                            MyUtils.exitToLongin(mContext);
                        } else {
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
                                    ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                            }
                        }
                    }
                }
        );

    }

    private void upView(int i) {

        if (list.get(i).get("vin") == null) {
            textType.setText("");
        } else {
            textType.setText(list.get(i).get("vin") + "");
        }
        if (list.get(i).get("groupCode") == null) {
            textCar.setText("");
        } else {
            textCar.setText(list.get(i).get("groupCode") + "");
        }
        if (list.get(i).get("color") == null) {
            textCarf.setText("");
        } else {
            textCarf.setText(list.get(i).get("color") + "");
        }
        if (list.get(i).get("invoiceDate") == null) {
            textStop.setText("");
        } else {
            String[] words = (list.get(i).get("invoiceDate") + "").split(" ");
            textStop.setText(words[0]);
        }
        if (list.get(i).get("powerNo") == null) {
            editCharges.setText("");
        } else {
            editCharges.setText(list.get(i).get("powerNo") + "");
        }
        if (list.get(i).get("licenseNo") == null) {
            textNumber.setText("");
        } else {
            textNumber.setText(list.get(i).get("licenseNo") + "");
        }
        if (list.get(i).get("nickName") == null) {
            editAlias.setText("");
        } else {
            editAlias.setText(list.get(i).get("nickName") + "");
            // String aa= (String) new SharedPHelper(mContext).get(Constant.CARALIAS,"");
            alias = list.get(i).get("nickName") + "";
        }
        if (list.get(i).get("relationType") != null) {
            if (list.get(i).get("relationType").equals("车主")) {
                tvUncar.setVisibility(View.VISIBLE);
                editAlias.setEnabled(false);
            } else {
                tvUncar.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                editAlias.setEnabled(false);
                if (editAlias.getText().toString().length() == 0) {
                    editAlias.setText("");
                }
            }
        }
    }


    private void initSwitch() {
        if (true) {//是默认车
            switchButton.setChecked(true);
            switchButton.setEnabled(false);
            ii = 0;
        } else {
            ii = 1;
        }
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                MLog.e("0");
//                if(!buttonView.isPressed()) {
//                    MLog.e("1");
//                    return;
//
//                }
                if (ii != 0) {

                    if (isChecked) {
                        MLog.e("2");
                        getDefaults(vin);
                    }
                }
            }
        });
        //  setting.setVisibility(View.INVISIBLE);
    }

    private void getDefaults(final String vin) {
        DialogUtils.loading(mContext, true);
        TspRxUtils.getDefault(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin"},
                new Object[]{vin},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "默认车", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        new SharedPHelper(mContext).put("TSPVIN", vin);

                        switchButton.setEnabled(true);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_settingsuc));
// cc                       if(list.get(poss).get("nickName")!=null) {
//                            new SharedPHelper(mContext).put(Constant.CARALIAS, String.valueOf(list.get(poss).get("nickName")));
//                        }


                        new SharedPHelper(mContext).put(Constant.CARALIAS, alias);

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switchButton.setChecked(false);
                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.unsettingfail));
                        } else if (str.contains("500") || str.contains("无效的网址")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                        } else if (str.contains("未授权的请求")) {
                            MyUtils.exitToLongin(mContext);
                        } else if (str.contains("401")) {
                            MyUtils.exitToLongin401(mContext);
                        } else if (str.contains("连接超时")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.timeout));
                        } else {
                            //  ActivityUtil.showToast(mContext, str);
                        }
                        MyUtils.upLogTSO(mContext, "默认车", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }

    @OnClick({R.id.back, R.id.confirm, R.id.tv_uncar, R.id.btn_un, R.id.edit_code, R.id.btn_code, R.id.edit_alias})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.confirm:
                if (editAlias.getText().toString().trim().length() == 0) {
                    ActivityUtil.showToast(MyCarEnter2Activity.this, getResources().getString(R.string.hoast_caralis));
                } else {
                    getSet();
                }
                break;
            case R.id.tv_uncar:
                //  getUn();
                DialogUtils.hint1(mContext, false, getResources().getString(R.string.iscallservice), new DialogHintListener() {
                    @Override
                    public void callBack() {
                        callSafe(new SharedPHelper(mContext).get(Constant.LOGINHOTLINE, "") + "");
                    }
                });
                break;
            case R.id.btn_un:

//                if(editCode.getText().toString().trim().length()==0){
//                    ActivityUtil.showToast(mContext,getResources().getString(R.string.hint_entercode));
//                }else {
//                    getUn();
//                }
                break;


            case R.id.edit_code:
                editCode.setCursorVisible(true);
                break;
            case R.id.btn_code:
                editCode.setText("");
                getTsp8();
                break;
            case R.id.edit_alias:
                editAlias.setCursorVisible(true);
                break;
        }
    }

    private void callSafe(String ss) {
        DialogUtils.call(mContext, false, ss);
    }

    private void upVis() {
        linThree.setVisibility(View.VISIBLE);
        linMain.setVisibility(View.GONE);
        tvTitle.setText(getResources().getString(R.string.dissolve));
        tvUncar.setVisibility(View.GONE);
        timeCount = new TimeCount(60000, 1000);
        timeCount.start();
    }

    private void getTsp8() {
        DialogUtils.loading(mContext, true);
        TspRxUtils.getBind(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(MyCarEnter2Activity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "mobile"},
                new Object[]{vin,
                        new SharedPHelper(MyCarEnter2Activity.this).get(Constant.LOGINNAME, "")
                },
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);

                        MyUtils.upLogTSO(mContext, "绑定车辆", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        upVis();

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        if (str.contains("400") || str.contains("无效的请求")) {
                            // ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else if (str.contains("500") || str.contains("无效的网址")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                        } else if (str.contains("未授权的请求")) {
                            MyUtils.exitToLongin(mContext);
                        } else if (str.contains("401")) {
                            MyUtils.exitToLongin401(mContext);
                        } else if (str.contains("连接超时")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.timeout));
                        } else {
                            //  ActivityUtil.showToast(mContext, str);
                        }
                        MyUtils.upLogTSO(mContext, "绑定车辆", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

    }

    private void getUn() {
        DialogUtils.loading(MyCarEnter2Activity.this, true);
        HttpRxUtils.getUnBind(MyCarEnter2Activity.this,
                new String[]{"tsp_token", "accessToken", "appType", "vin"},
                new Object[]{
                        "Bearer" + " " + new SharedPHelper(MyCarEnter2Activity.this).get(Constant.ACCESSTOKENS, ""),
                        new SharedPHelper(MyCarEnter2Activity.this).get(Constant.ACCESSTOKEN, ""),
                        "Android",
                        vin
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding(MyCarEnter2Activity.this);
                        ActivityUtil.showToast(MyCarEnter2Activity.this, getResources().getString(R.string.hint_unbindcarsucc));
                        finishSelect();

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(MyCarEnter2Activity.this);
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

    private void getSet() {
        DialogUtils.loading(MyCarEnter2Activity.this, true);
        HttpRxUtils.getSetCarNick(MyCarEnter2Activity.this,
                new String[]{"vin", "accessToken", "nick_name"},
                new Object[]{vin, new SharedPHelper(MyCarEnter2Activity.this).get(Constant.ACCESSTOKEN, ""),
                        editAlias.getText().toString()
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding(MyCarEnter2Activity.this);
                        ActivityUtil.showToast(MyCarEnter2Activity.this, getResources().getString(R.string.safesuc));
                        if ((new SharedPHelper(mContext).get("TSPVIN", "0")).equals(vin)) {
                            new SharedPHelper(mContext).put(Constant.CARALIAS, editAlias.getText().toString());
                        }
                        finishSelect();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(MyCarEnter2Activity.this);
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

    private void finishSelect() {
        Intent data = new Intent();
        setResult(8004, data);
        finish();
    }
}
