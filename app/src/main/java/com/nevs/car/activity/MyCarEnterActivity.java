package com.nevs.car.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class MyCarEnterActivity extends BaseActivity {

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
    @BindView(R.id.switch_buttong)
    TextView switchButtong;
    private String vin = "";
    private ArrayList<HashMap<String, Object>> list;
    private TimeCount timeCount;
    private int ii = 0;
    private String alias = "";
    private String msisdn = "";
    private String imsi = "";
    private String groupCode = "";
    private String isAuthenticated = "";
    private String relationType = "";
    private String nickName = null;
    private List<String> permissions = new ArrayList<>();


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
        initIntent();
        initSwitch();
        initJson();
    }

    private void initIntent() {
        vin = getIntent().getStringExtra("vin");
        MLog.e("跳转:" + vin);
    }


    private void initJson() {
        //   MyUtils.xJson(new SharedPHelper(mContext).get("LOGINJSONSSCAR", "") + "", list);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ArrayList lista = bundle.getParcelableArrayList("list");
        list = (ArrayList<HashMap<String, Object>>) lista.get(0);
        MLog.e("bbb:" + list.size());

        if (list.size() != 0) {
            MLog.e("长度：" + list.size() + ": " + list.get(0).get("vin"));
            for (int i = 0; i < list.size(); i++) {
                if (vin.equals(list.get(i).get("vin"))) {
                    upView(i);
                }
            }
        }
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
            nickName = list.get(i).get("nickName") + "";
        }

        if (list.get(i).get("msisdn") != null) {
            msisdn = list.get(i).get("msisdn") + "";
        }
        if (list.get(i).get("imsi") != null) {
            msisdn = list.get(i).get("imsi") + "";
        }
        if (list.get(i).get("groupCode") != null) {
            groupCode = list.get(i).get("groupCode") + "";
        }
        if (list.get(i).get("isAuthenticated") != null) {
            isAuthenticated = list.get(i).get("isAuthenticated") + "";
        }
        if (list.get(i).get("relationType") != null) {
            msisdn = list.get(i).get("relationType") + "";
        }
        if (list.get(i).get("permissions") != null) {
            permissions.addAll((Collection<? extends String>) list.get(i).get("permissions"));
        }


        if (list.get(i).get("relationType") != null) {
            if (list.get(i).get("relationType").equals("车主")) {
                tvUncar.setVisibility(View.VISIBLE);
            } else {
                tvUncar.setVisibility(View.GONE);
                //confirm.setVisibility(View.INVISIBLE);
                // switchButton.setClickable(false);
               // editAlias.setEnabled(false);
                if (editAlias.getText().toString().length() == 0) {
                    editAlias.setText("");
                }

            }
        }
    }


    private void initSwitch() {
        if (getIntent().getStringExtra("isdefault").equals("1")) {//是默认车
            switchButton.setChecked(true);
            switchButton.setEnabled(false);
            switchButtong.setVisibility(View.VISIBLE);
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
                        new SharedPHelper(mContext).put("TSPVIN", vin);
                        MyUtils.upLogTSO(mContext, "默认车", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        switchButton.setEnabled(false);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_settingsuc));
// cc                       if(list.get(poss).get("nickName")!=null) {
//                            new SharedPHelper(mContext).put(Constant.CARALIAS, String.valueOf(list.get(poss).get("nickName")));
//                        }


                        new SharedPHelper(MyCarEnterActivity.this).put(Constant.MISISDN, msisdn);
                        new SharedPHelper(MyCarEnterActivity.this).put(Constant.imsi, imsi);
                        new SharedPHelper(MyCarEnterActivity.this).put(Constant.groupCode, groupCode);//0
                        new SharedPHelper(MyCarEnterActivity.this).put(Constant.isAuthenticated, isAuthenticated);


                        String iscarOwer = relationType;
                        if (iscarOwer.equals("车主")) {//授权
                            new SharedPHelper(MyCarEnterActivity.this).put(Constant.TSPISCAROWER, "YES");//
                        } else {
                            new SharedPHelper(MyCarEnterActivity.this).put(Constant.TSPISCAROWER, "NO");//
                        }

                        MyUtils.savaPermissions(permissions, mContext);


                        //initJson(list.get(position).get("vin")+"");
                        if (nickName != null) {
                            new SharedPHelper(mContext).put(Constant.CARALIAS, nickName);
                        } else {
                            new SharedPHelper(mContext).put(Constant.CARALIAS, "");
                        }


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
                    ActivityUtil.showToast(MyCarEnterActivity.this, getResources().getString(R.string.hoast_caralis));
                } else {
                    getSet();
                }
                break;
            case R.id.tv_uncar:
                // getUn();
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
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(MyCarEnterActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "mobile"},
                new Object[]{vin,
                        new SharedPHelper(MyCarEnterActivity.this).get(Constant.LOGINNAME, "")
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
        DialogUtils.loading(MyCarEnterActivity.this, true);
        HttpRxUtils.getUnBind(MyCarEnterActivity.this,
                new String[]{"tsp_token", "accessToken", "appType", "vin"},
                new Object[]{
                        "Bearer" + " " + new SharedPHelper(MyCarEnterActivity.this).get(Constant.ACCESSTOKENS, ""),
                        new SharedPHelper(MyCarEnterActivity.this).get(Constant.ACCESSTOKEN, ""),
                        "Android",
                        vin
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding(MyCarEnterActivity.this);
                        ActivityUtil.showToast(MyCarEnterActivity.this, getResources().getString(R.string.hint_unbindcarsucc));
                        finishSelect();

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(MyCarEnterActivity.this);
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
        DialogUtils.loading(MyCarEnterActivity.this, true);
        HttpRxUtils.getSetCarNick(MyCarEnterActivity.this,
                new String[]{"vin", "accessToken", "nick_name"},
                new Object[]{vin, new SharedPHelper(MyCarEnterActivity.this).get(Constant.ACCESSTOKEN, ""),
                        editAlias.getText().toString()
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding(MyCarEnterActivity.this);
                        ActivityUtil.showToast(MyCarEnterActivity.this, getResources().getString(R.string.safesuc));
                        if ((new SharedPHelper(mContext).get("TSPVIN", "0")).equals(vin)) {
                            new SharedPHelper(mContext).put(Constant.CARALIAS, editAlias.getText().toString());
                        }
                        finishSelect();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(MyCarEnterActivity.this);
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
