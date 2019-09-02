package com.nevs.car.activity.my;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.ispr.uilibrary.DialogUIUtils;
import com.ispr.uilibrary.listener.DialogUIDateTimeSaveListener;
import com.ispr.uilibrary.widget.DateSelectorWheelView;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.CustomDatePicker;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.SwitchButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarAuthorizationAddActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.edit_family)
    EditText editFamily;
    @BindView(R.id.edit_number)
    EditText editNumber;
    @BindView(R.id.edit_data)
    EditText editData;
    @BindView(R.id.edit_datas)
    EditText editDatas;
    @BindView(R.id.id_switch)
    SwitchButton idSwitch;
    @BindView(R.id.switch_two)
    SwitchButton switchTwo;
    @BindView(R.id.switch_three)
    SwitchButton switchThree;
    @BindView(R.id.switch_four)
    SwitchButton switchFour;
    @BindView(R.id.switch_five)
    SwitchButton switchFive;
    @BindView(R.id.switch_six)
    SwitchButton switchSix;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String auvin = "";
    private int a = 0;
    private int b = 0;
    private int c = 0;
    private int d = 0;
    private int e = 0;
    private int f = 0;
    private List<Integer> aa = new ArrayList<>();
    private List<Integer> li = new ArrayList();

    private CustomDatePicker datePicker, timePicker;
    private String time = "";
    private String date = "";
    private int poss = 0;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_car_authorization_add;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initSwichClick();
        auvin = getIntent().getStringExtra("AUVIN");
        initPicker();
    }

    private void initPicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        time = sdf.format(new Date());
        date = time.split(" ")[0];
//        //设置当前显示的日期
//        currentDate.setText(date);
//        //设置当前显示的时间
//        currentTime.setText(time);

        /**
         * 设置年月日
         */
        datePicker = new CustomDatePicker(this, "请选择日期", new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {
                //  currentDate.setText(time.split(" ")[0]);

            }
        }, "2007-01-01 00:00", time);
        datePicker.showSpecificTime(false); //显示时和分
        datePicker.setIsLoop(false);
        datePicker.setDayIsLoop(true);
        datePicker.setMonIsLoop(true);

        timePicker = new CustomDatePicker(this, getResources().getString(R.string.title), new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {
                // currentTime.setText(time);
                showTime(time);
            }
        }, "2007-01-01 00:00", "2027-12-31 23:59");//"2027-12-31 23:59"
        timePicker.showSpecificTime(true);
        timePicker.setIsLoop(true);

//        // 日期格式为yyyy-MM-dd
//        datePicker.show(date);
//        // 日期格式为yyyy-MM-dd HH:mm
//        timePicker.show(time);
    }
    private void showTime(String time) {
        MLog.e("选择的时间：" + time);
        String sub = time.substring(0, time.length() - 3);
        String selectedDate = time.substring(0, time.length() - 6);
        long startTime,endTime;
        switch (poss) {
            case 1:
                //long cc = HashmapTojson.getStringToDates(selectedDate, "yyyy-MM-dd");
                startTime= HashmapTojson.getStringToDates(sub+":59:59", "yyyy-MM-dd HH:mm:ss");
                String endStr=editData.getText().toString();
                if(endStr.length()>0)
                    endTime = HashmapTojson.getStringToDates(endStr+ ":00:00", "yyyy-MM-dd HH:mm:ss");
                else
                    endTime = 0L;

                MLog.e("选择日期的时间戳：" + HashmapTojson.getStringToDates(selectedDate, "yyyy-MM-dd"));
                MLog.e("当天0点的时间戳：" + (MyUtils.getTimesnight() - 24 * 3600));
                MLog.e("当前的时间戳：" + MyUtils.timeStampNow());
                MLog.e("当天24点的时间戳：" + MyUtils.getTimesnight());
               // long nowZero = MyUtils.getTimesnight() - 24 * 3600;

                if (startTime >= MyUtils.timeStampNow()) {

                    if(endTime!=0&&endTime<startTime) {
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.time_hint));
                    }else
                        editDatas.setText(sub + ":00");
                } else {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.pleasebigtiome));
                }
                break;
            case 2:
                String hours = time.substring(0, time.length() - 3) + ":00:00";
                String startStr=editDatas.getText().toString();
                if(startStr.length()>0)
                    startTime = HashmapTojson.getStringToDates(editDatas.getText().toString() + ":00:00", "yyyy-MM-dd HH:mm:ss");
                else
                    startTime = 0L;
                      endTime = HashmapTojson.getStringToDates(hours, "yyyy-MM-dd HH:mm:ss");

                    //if()
                    if (startTime >= MyUtils.timeStampNow()) {

                        if(endTime!=0&&endTime<startTime) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.time_hint));
                        }else
                            editData.setText(sub + ":00");

                    } else {
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.pleasebigtiome));
                    }


                break;
        }

    }

    private void initSwichClick() {
        idSwitch.setOnCheckedChangeListener(this);
        switchTwo.setOnCheckedChangeListener(this);
        switchThree.setOnCheckedChangeListener(this);
        switchFour.setOnCheckedChangeListener(this);
        switchFive.setOnCheckedChangeListener(this);
        switchSix.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.edit_family, R.id.edit_number, R.id.edit_data, R.id.edit_datas, R.id.rel_time, R.id.rel_times, R.id.next})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit_family:
                editFamily.setCursorVisible(true);
                break;
            case R.id.edit_number:
                editNumber.setCursorVisible(true);
                break;
            case R.id.edit_data:
                // showDialogs(2);
                poss = 2;
                timePicker.show(time);
                break;
            case R.id.edit_datas:
                poss = 1;
                // showDialogs(1);
                timePicker.show(time);
                break;
            case R.id.rel_time:
                // showDialogs(2);
                poss = 2;
                timePicker.show(time);

                break;
            case R.id.rel_times:
                // showDialogs(1);
                poss = 1;
                timePicker.show(time);
                break;
            case R.id.next:
                getInt();
                if (editNumber.getText().toString().trim().length() != 0 &&
                        editData.getText().toString().length() != 0 && aa.size() != 0 &&
                        editFamily.getText().toString().trim().length() != 0
                ) {
                    getHttp();
                } else if (aa.size() == 0) {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_settingaut));
                } else {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_enterall));
                }

                break;
        }
    }

    private void getInt() {
        if (a != 0) {
            li.add(a);
        }
        if (b != 0) {
            li.add(b);
        }
        if (c != 0) {
            li.add(c);
        }
        if (d != 0) {
            li.add(d);
        }
        if (e != 0) {
            li.add(e);
        }
        if (f != 0) {
            li.add(f);
        }
        if (li.size() != 0) {
            for (int i = 0; i < li.size(); i++) {
                aa.add(li.get(i));
            }
        }
    }

    private void showDialogs(final int id) {
        DialogUIUtils.showDatePick(mContext, Gravity.CENTER, getResources().getString(R.string.toast_choosedate), System.currentTimeMillis() + 60000, DateSelectorWheelView.TYPE_YYYYMMDD, 0, new DialogUIDateTimeSaveListener() {
            @Override
            public void onSaveSelectedDate(int tag, String selectedDate) {

                switch (id) {
                    case 1:
                        long cc = HashmapTojson.getStringToDates(selectedDate, "yyyy-MM-dd");
                        MLog.e("选择日期的时间戳：" + HashmapTojson.getStringToDates(selectedDate, "yyyy-MM-dd"));
                        MLog.e("当天0点的时间戳：" + (MyUtils.getTimesnight() - 24 * 3600));
                        MLog.e("当前的时间戳：" + MyUtils.timeStampNow());
                        MLog.e("当天24点的时间戳：" + MyUtils.getTimesnight());
                        long nowZero = MyUtils.getTimesnight() - 24 * 3600;
                        if (cc >= nowZero) {
                            editDatas.setText(selectedDate);
                        } else {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.pleasebigtiome));
                        }


                        break;
                    case 2:
                        editData.setText(selectedDate);
                        break;
                }


            }
        }).show();
    }

    private void getHttp() {
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getJudgmentUserExist(mContext,
                new String[]{"user_code", "accessToken"},
                new Object[]{
                        editNumber.getText().toString().trim(),
                        new SharedPHelper(mContext).get(Constant.ACCESSTOKEN, "")
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        MLog.e("判断用户是否存在获取：" + obj);
                        getTsp9(String.valueOf(obj));
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                                break;
                            case "UserNameNotExists":
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.unexist));
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

    private void getTsp9(String data) {
        /**
         * {"resultMessage":"Service Success","resultDescription":"AuthorizeUser success"}
         * */
        //DialogUtils.loading(CarAuthorizationAddActivity.this,true);
        TspRxUtils.getAuthorize(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(CarAuthorizationAddActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "nickName", "targetUserAccount", "startTime", "endTime", "permissions"},
                new Object[]{auvin, editFamily.getText().toString(), data,
                        HashmapTojson.getTimeSecond(HashmapTojson.getStringToDate(editDatas.getText().toString() + ":00", "yyyy-MM-dd HH:mm")),
                        HashmapTojson.getTimeSecond(HashmapTojson.getStringToDate(editData.getText().toString() + ":00", "yyyy-MM-dd HH:mm")),
                        aa},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(CarAuthorizationAddActivity.this);
                        MyUtils.upLogTSO(mContext, "授权车辆", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        ActivityUtil.showToast(CarAuthorizationAddActivity.this, getResources().getString(R.string.safesuc));
                        finish();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
//                       if (str.contains("500")||str.contains("无效的网址")) {
//                            ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
//                        } else if (str.contains("401")||str.contains("未授权的请求")) {
//                            MyUtils.exitToLongin(mContext);
//                        } else if (str.contains("连接超时")) {
//                            ActivityUtil.showToast(mContext, getResources().getString(R.string.timeout));
//                        }else if(str.contains("400") || str.contains("无效的请求")) {
//                            ActivityUtil.showToast(mContext, getResources().getString(R.string.arthorfail));
//                        }
//                        else {
//                            ActivityUtil.showToast(mContext, str);
//                        }


                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.arthorfail));
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
                        MyUtils.upLogTSO(mContext, "授权车辆", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.id_switch:
                if (isChecked) {
                    a = 1;
                } else {
                    a = 0;
                }
                MLog.e("a=" + a);
                break;
            case R.id.switch_two:
                if (isChecked) {
                    b = 2;
                } else {
                    b = 0;
                }
                MLog.e("b=" + b);
                break;
            case R.id.switch_three:
                if (isChecked) {
                    c = 3;
                } else {
                    c = 0;
                }
                MLog.e("c=" + c);
                break;
            case R.id.switch_four:
                if (isChecked) {
                    d = 4;
                } else {
                    d = 0;
                }
                MLog.e("d=" + d);
                break;
            case R.id.switch_five:
                if (isChecked) {
                    e = 5;
                } else {
                    e = 0;
                }
                MLog.e("d=" + d);
                break;
            case R.id.switch_six:
                if (isChecked) {
                    f = 6;
                } else {
                    f = 0;
                }
                MLog.e("d=" + d);
                break;
        }
    }
}
