package com.nevs.car.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.UpdateContentList;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.DatePickerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AirOrderEnterActivity extends BaseActivity {

    @BindView(R.id.text_date)
    TextView textDate;
    @BindView(R.id.text_times)
    TextView textTimes;
    @BindView(R.id.picker_hour)
    DatePickerView pickerHour;
    @BindView(R.id.picker_minute)
    DatePickerView pickerMinute;
    @BindView(R.id.tv_order)
    TextView tvOrder;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String hour = "";
    private String minute = "";
    public static AnimationDrawable animationDrawable;
    private String commandId = null;
    private String time1 = "";
    private String time2 = "";
    private String chooseTime = "";
    private String chooseTimes = "";
    private List<Integer> listData = new ArrayList();
    private String cronss = "";
    private String scheduleId = "";
    private String airtype = "";

    private List<UpdateContentList> listUp = new ArrayList<>();


    @Override
    public int getContentViewResId() {
        return R.layout.activity_air_order_enter;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initIntent();
        //getTimes1();

        initTime();

    }

    private void initIntent() {
        airtype = getIntent().getStringExtra("airtype");
        if (airtype.equals("2")) {//详情
            upView();
        } else if (airtype.equals("1")) {//添加
            tvOrder.setVisibility(View.GONE);
            hour = HashmapTojson.getTime1("HH");
            minute = HashmapTojson.getTime1("mm");
            MLog.e("当前minute" + minute + "  当前hour" + hour);
        }
    }

    private void upView() {
        tvOrder.setVisibility(View.VISIBLE);
        scheduleId = getIntent().getStringExtra("scheduleId");
        MLog.e("scheduleId" + scheduleId);
        String scheduleType = getIntent().getStringExtra("type");
        String scheduleValue = getIntent().getStringExtra("text");
        String runDuration = getIntent().getStringExtra("size");
        textTimes.setText(MyUtils.getZ(runDuration));


        if (scheduleType.equals("3.0")) {
            String bb[] = scheduleValue.split(" ");
            hour = bb[2];
            minute = bb[1];
            String times = bb[5];
            cronss = times;
            String timeOnes[] = times.split(",");
            MLog.e(timeOnes.length + "长度");
            if (timeOnes.length == 7) {
                textDate.setText(mContext.getResources().getString(R.string.everyday));
                MLog.e(timeOnes.length + "长度");
            } else {
                String ss = "";
                for (int i = 0; i < timeOnes.length; i++) {
                    MLog.e("测试" + i + ":" + timeOnes[i]);
                    switch (timeOnes[i]) {
                        case "2":
                            ss += mContext.getResources().getString(R.string.toast_monday);//星期一
                            break;
                        case "3":
                            ss += mContext.getResources().getString(R.string.toast_tuesday);//星期二
                            break;
                        case "4":
                            ss += mContext.getResources().getString(R.string.toast_wednesday);
                            break;
                        case "5":
                            ss += mContext.getResources().getString(R.string.toast_thursday);
                            break;
                        case "6":
                            ss += mContext.getResources().getString(R.string.toast_friday);
                            break;
                        case "7":
                            ss += mContext.getResources().getString(R.string.toast_saturday);
                            break;
                        case "1":
                            ss += mContext.getResources().getString(R.string.toast_sunday);//星期天
                            break;
                    }
                }
                textDate.setText(ss);
                if (ss.equals(getResources().getString(R.string.toast_monday) +
                        getResources().getString(R.string.toast_tuesday) +
                        getResources().getString(R.string.toast_wednesday) +
                        getResources().getString(R.string.toast_thursday) +
                        getResources().getString(R.string.toast_friday))) {
                    textDate.setText(getResources().getString(R.string.week));
                }
            }

        } else if (scheduleType.equals("1.0")) {
            textDate.setText(getResources().getString(R.string.nevs_never));
            String aa[] = scheduleValue.split(" ");
            String s = aa[1];
            MLog.e("获取时间" + s);
            String cc[] = s.split(":");
            hour = cc[0];
            minute = cc[1];
        }
    }

    private void initTime() {


        List hours = new ArrayList();
        List minutes = new ArrayList();
        for (int i = 0; i < 25; i++) {
            //  hours.add(i < 10 ? "0" + i : "" + i);
            hours.add(i + "");
        }
        for (int i = 0; i < 60; i++) {
            //  minutes.add(i < 10 ? "0" + i : "" + i);
            minutes.add(i + "");
        }
        pickerMinute.setData(minutes);
        for (int j = 0; j < minutes.size(); j++) {
            if (minute.equals(minutes.get(j))) {
                pickerMinute.setSelected(j);
            }
        }
        pickerMinute.setOnSelectListener(new DatePickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                MLog.e("选择了 " + text + " 分");
                minute = text;
            }
        });
        pickerHour.setData(hours);
        for (int k = 0; k < hours.size(); k++) {
            if (hour.equals(hours.get(k))) {
                pickerHour.setSelected(k);
            }
        }
        pickerHour.setOnSelectListener(new DatePickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                MLog.e("选择了 " + text + " 时");
                hour = text;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.choose_date, R.id.edit_times, R.id.config, R.id.tv_order})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.choose_date:
                listData.clear();
                showChooses();
                break;
            case R.id.edit_times:
                showChoose();
                break;
            case R.id.config:
                /**
                 * Input	参数名称	校验规则	备注

                 vin	STRING	车辆VIN码
                 scheduleType  	INT	1: 某个时间点执行一次 2: 每隔一段时间执行一次 3: Cron表达式执行
                 scheduleValue	STRING	"根据scheduleType设置不同的值
                 如果是1: 标准格式时间 YYYY-MM-dd HH:mm:ss
                 如果是2:则是间隔时间，单位为分钟
                 如果是3: 则是cron表达式"
                 runDuration	INT	空调运行多久，单位为分钟
                 * */


                if (airtype.equals("1")) {//添加

                    if (textDate.getText().toString().equals(getResources().getString(R.string.nevs_never))) {//用1
                        getTimes1();
                        getTsp18();
                    } else {//yong3
                        if (listData.size() == 0) {
                            getTimes1ss();
                            getTsp18s();
                        } else {
                            getTimes1s();
                            getTsp18s();
                        }

                    }

                } else {//修改

                    if (textDate.getText().toString().equals(getResources().getString(R.string.nevs_never))) {//用1
                        getTimes1();
                        getTspUp();
                    } else {//yong3
                        if (listData.size() == 0) {
                            getTimes1ss();
                            getTspUps();
                        } else {
                            getTimes1s();
                            getTspUps();
                        }

                    }

                }


                break;
            case R.id.tv_order:
                getDelete();
                break;
        }
    }


    private void getDelete() {
        DialogUtils.loading(mContext, true);
        TspRxUtils.getAirDelete(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"scheduleId"},
                new Object[]{new String[]{
                        scheduleId
                }},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(AirOrderEnterActivity.this, getResources().getString(R.string.toast_deletesuccess));
                        finishSelect1();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.deletefail));
                    }
                }
        );

    }


    private void getTimes1ss() {
        chooseTimes = "0 " + minute + " " + hour + " ? * " + cronss;

    }

    private void getTsp18s() { // "0 30 15 ? * 2,3,5,6,"
        DialogUtils.loading(mContext, true);
        TspRxUtils.getScheduleairconditioner(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "runDuration", "scheduleValue", "scheduleType"},
                new Object[]{new SharedPHelper(AirOrderEnterActivity.this).get("TSPVIN", "0").toString(), textTimes.getText().toString(), chooseTimes, 3},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        //  controllHint(AirOrderEnterActivity.this,getResources().getString(R.string.toast_servicesuc));
                        // getCommandresult();
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "空调预约", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        ActivityUtil.showToast(AirOrderEnterActivity.this, getResources().getString(R.string.toast_servicesuc));
                        finishSelect();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.unsefail));
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
                        MyUtils.upLogTSO(mContext, "空调预约", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }

    private void getTimes1s() {
        //"0 0 12 ? * 7,6,1,"
        if (textDate.equals(getResources().getString(R.string.everyday))) {
            chooseTimes = "0 " + minute + " " + hour + " ? * " + "1,2,3,4,5,6,7,";
        } else {
            String end = "";
            int dd[] = new int[listData.size()];
            for (int i = 0; i < listData.size(); i++) {
                // end+=listData.get(i)+",";
                dd[i] = listData.get(i) + 1;
                if ((listData.get(i) + "").equals("7")) {
                    dd[i] = 1;
                }
            }
            for (int j = 0; j < dd.length; j++) {
                end += dd[j] + ",";
            }
            MLog.e("end" + end);
            chooseTimes = "0 " + minute + " " + hour + " ? * " + end;
        }

    }

    private void getTimes1() {
        //yyyy-MM-dd HH:mm   07/31/2018 22:0:00
        String nowTime = HashmapTojson.getTime1("yyyy");
        String nowTime2 = HashmapTojson.getTime1("MM");
        String nowTime3 = HashmapTojson.getTime1("dd");
        String nowHour = HashmapTojson.getTime1("HH");
        String nowMinute = HashmapTojson.getTime1("mm");
        MLog.e("nowTime" + nowTime);
        MLog.e("nowTime2" + nowTime2);
        MLog.e("nowTime3" + nowTime3);


        try {
            if (Integer.parseInt(hour) > Integer.parseInt(nowHour)) {
                chooseTime = nowTime2 + "/" + nowTime3 + "/" + nowTime + " " + hour + ":" + minute + ":00";
            } else if (Integer.parseInt(hour) == Integer.parseInt(nowHour)) {
                if (Integer.parseInt(minute) > Integer.parseInt(nowMinute)) {
                    chooseTime = nowTime2 + "/" + nowTime3 + "/" + nowTime + " " + hour + ":" + minute + ":00";
                } else {
                    String hourz = Integer.parseInt(hour) < 10 ? "0" + hour : "" + hour;
                    String minutez = Integer.parseInt(minute) < 10 ? "0" + minute : "" + minute;
                    String chooseTimetext = nowTime + "-" + nowTime2 + "-" + nowTime3 + " " + hourz + ":" + minutez;
                    long l = HashmapTojson.getStringToDates(chooseTimetext, "yyyy-MM-dd HH:mm");
                    long ll = l + (3600 * 24);
                    String timez = HashmapTojson.getDateToString(ll * 1000, "dd");
                    String nowTime2s = HashmapTojson.getDateToString(ll * 1000, "MM");
                    String nowTimes = HashmapTojson.getDateToString(ll * 1000, "yyyy");
                    MLog.e("timez:" + timez);
                    chooseTime = nowTime2s + "/" + timez + "/" + nowTimes + " " + hour + ":" + minute + ":00";
                }
            } else {
                String hourz = Integer.parseInt(hour) < 10 ? "0" + hour : "" + hour;
                String minutez = Integer.parseInt(minute) < 10 ? "0" + minute : "" + minute;
                String chooseTimetext = nowTime + "-" + nowTime2 + "-" + nowTime3 + " " + hourz + ":" + minutez;
                long l = HashmapTojson.getStringToDates(chooseTimetext, "yyyy-MM-dd HH:mm");
                long ll = l + (3600 * 24);
                String timez = HashmapTojson.getDateToString(ll * 1000, "dd");
                String nowTime2s = HashmapTojson.getDateToString(ll * 1000, "MM");
                String nowTimes = HashmapTojson.getDateToString(ll * 1000, "yyyy");
                MLog.e("timez:" + timez);
                chooseTime = nowTime2s + "/" + timez + "/" + nowTimes + " " + hour + ":" + minute + ":00";

            }

        } catch (Exception S) {
            MLog.e("if (Integer.parseInt(hour) > Integer.parseInt(nowHour))数子转化异常");
        }


    }

    private void getTsp18() {
        /**
         *cccc:{"resultMessage":"","resultDescription":""}
         * 空调定时cccc:{"resultMessage":"Parameter error","resultDescription":"Invalid time format!"}
         * */
        DialogUtils.loading(mContext, true);
        TspRxUtils.getScheduleairconditioner(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "runDuration", "scheduleValue", "scheduleType"},
                new Object[]{new SharedPHelper(AirOrderEnterActivity.this).get("TSPVIN", "0").toString(), textTimes.getText().toString(), chooseTime, 1},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        //  controllHint(AirOrderEnterActivity.this,getResources().getString(R.string.toast_servicesuc));
                        // getCommandresult();
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "空调预约", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        ActivityUtil.showToast(AirOrderEnterActivity.this, getResources().getString(R.string.toast_servicesuc));
                        finishSelect();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.unsefail));
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
                        MyUtils.upLogTSO(mContext, "空调预约", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

    }

    private void showChoose() {
        final String item[] = new String[]{"5", "10", "15", "20"};
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.toast_choose))
                .setSingleChoiceItems(item, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(this, items[which], Toast.LENGTH_SHORT).show();
                        textTimes.setText(item[which]);
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void showChooses() {
        final String items[] = new String[]{getResources().getString(R.string.toast_monday),
                getResources().getString(R.string.toast_tuesday),
                getResources().getString(R.string.toast_wednesday),
                getResources().getString(R.string.toast_thursday),
                getResources().getString(R.string.toast_friday),
                getResources().getString(R.string.toast_saturday),
                getResources().getString(R.string.toast_sunday)};
        final boolean isCheck[] = new boolean[]{false, false, false, false, false, false, false,};
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.toast_choose))
                .setNegativeButton(getResources().getString(R.string.nevs_never), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        textDate.setText(getResources().getString(R.string.nevs_never));

                        dialog.dismiss();

                    }
                })

//                .setNeutralButton(getResources().getString(R.string.week), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        textDate.setText(getResources().getString(R.string.week));
//                        dialog.dismiss();
//                    }
//                })
                .setPositiveButton(getResources().getString(R.string.for_confirm), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String text = "";
                        for (int i = 0; i < items.length; i++) {
                            if (isCheck[i]) {
                                text += items[i];
                                listData.add(i + 1);
                            }
                        }

                        for (int h = 0; h < listData.size(); h++) {
                            MLog.e("选择的星期：" + listData.get(h));
                        }


                        textDate.setText(text);
                        if (text.equals(getResources().getString(R.string.toast_monday) +
                                getResources().getString(R.string.toast_tuesday) +
                                getResources().getString(R.string.toast_wednesday) +
                                getResources().getString(R.string.toast_thursday) +
                                getResources().getString(R.string.toast_friday) +
                                getResources().getString(R.string.toast_saturday) +
                                getResources().getString(R.string.toast_sunday)
                        )) {
                            textDate.setText(getResources().getString(R.string.everyday));

                        } else if (text.equals(getResources().getString(R.string.toast_monday) +
                                getResources().getString(R.string.toast_tuesday) +
                                getResources().getString(R.string.toast_wednesday) +
                                getResources().getString(R.string.toast_thursday) +
                                getResources().getString(R.string.toast_friday))) {
                            textDate.setText(getResources().getString(R.string.week));
                        } else if (text.equals("")) {
                            textDate.setText(getResources().getString(R.string.nevs_never));
                        }
                        //Toast.makeText(AirOrderEnterActivity.this, text, 0).show();
                        dialog.dismiss();
                    }
                })
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        isCheck[which] = isChecked;
                    }
                }).create();
        dialog.show();
    }

    private void controllHint(final Context context, final String str) {
        final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
        alertDialog.show();
        alertDialog.setCancelable(false);//点击背景是对话框不会消失
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_controll_hint);//加载自定义的布局
        WindowManager.LayoutParams wm = window.getAttributes();
        wm.width = 600;//设置对话框的宽
        wm.height = 500;//设置对话框的高
        wm.alpha = 0.5f;//设置对话框的背景透明度
        wm.dimAmount = 0.6f;//遮罩层亮度
        window.setAttributes(wm);
        final ImageView imageView = (ImageView) window.findViewById(R.id.progress_bar);
        final TextView textView = (TextView) window.findViewById(R.id.text);
        final LinearLayout linearLayout = (LinearLayout) window.findViewById(R.id.dialogq);
        imageView.setBackground(context.getResources().getDrawable(R.drawable.frame));
        animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationDrawable.stop();
                imageView.setBackground(context.getResources().getDrawable(R.mipmap.finish));
                linearLayout.setBackgroundResource(R.color.main_top);
                textView.setText(str);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();

                        finish();
                    }
                }, 1100);// 延迟关闭

            }
        }, 3000);
    }


    private void getTspUp() {
        /**
         *cccc:{"resultMessage":"","resultDescription":""}
         * 空调定时cccc:{"resultMessage":"Parameter error","resultDescription":"Invalid time format!"}
         * */
        DialogUtils.loading(mContext, true);
        listUp.clear();
        listUp.add(new UpdateContentList(scheduleId, Integer.parseInt(textTimes.getText().toString()), chooseTime, 1));
        TspRxUtils.getScheduleairconditionerUp(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"updateContentList"},
                new Object[]{listUp},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        MyUtils.upLogTSO(mContext, "空调定时更新", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        //  controllHint(AirOrderEnterActivity.this,getResources().getString(R.string.toast_servicesuc));
                        // getCommandresult();
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(AirOrderEnterActivity.this, getResources().getString(R.string.toast_servicesuc));
                        finishSelect();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        if (str.contains("400")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else if (str.contains("500")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                        } else {
                            ActivityUtil.showToast(mContext, str);
                        }
                        MyUtils.upLogTSO(mContext, "空调定时更新", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

    }

    private void getTspUps() { // "0 30 15 ? * 2,3,5,6,"
        DialogUtils.loading(mContext, true);
        listUp.clear();
        listUp.add(new UpdateContentList(scheduleId, Integer.parseInt(textTimes.getText().toString()), chooseTimes, 3));
        TspRxUtils.getScheduleairconditionerUp(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"updateContentList"},
                new Object[]{listUp},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        //  controllHint(AirOrderEnterActivity.this,getResources().getString(R.string.toast_servicesuc));
                        // getCommandresult();
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "空调定时更新", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        ActivityUtil.showToast(AirOrderEnterActivity.this, getResources().getString(R.string.toast_servicesuc));
                        finishSelect();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        if (str.contains("400")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else if (str.contains("500")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                        } else {
                            ActivityUtil.showToast(mContext, str);
                        }
                        MyUtils.upLogTSO(mContext, "空调定时更新", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }


    /**
     * 完成
     */
    private void finishSelect() {
        Intent data = new Intent();
        setResult(200, data);
        finish();
    }

    private void finishSelect1() {//删除
        Intent data = new Intent();
        setResult(201, data);
        finish();
    }
}
