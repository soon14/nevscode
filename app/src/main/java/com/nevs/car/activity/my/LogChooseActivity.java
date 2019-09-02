package com.nevs.car.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ispr.uilibrary.DialogUIUtils;
import com.ispr.uilibrary.listener.DialogUIDateTimeSaveListener;
import com.ispr.uilibrary.widget.DateSelectorWheelView;
import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.SwitchButton;

import java.net.URLDecoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogChooseActivity extends BaseActivity {

    @BindView(R.id.star_date)
    TextView starDate;
    @BindView(R.id.end_date)
    TextView endDate;
    @BindView(R.id.id_switch)
    SwitchButton idSwitch;
    @BindView(R.id.switch_two)
    SwitchButton switchTwo;
    @BindView(R.id.switch_three)
    SwitchButton switchThree;
    String type1 = "";
    String type2 = "";
    String type3 = "";
    ArrayList<String> list = new ArrayList<>();
    @BindView(R.id.n_view)
    RelativeLayout nView;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_log_choose;
    }
    private String beginTime,endTime;
    @Override
    public void init(Bundle savedInstanceState) {
        beginTime=getIntent().getStringExtra("beginTime");
        endTime=getIntent().getStringExtra("endTime");
        MyUtils.setPadding(nView,mContext);
        initSwich();
        initTime();
    }

    private void initTime() {
        if(TextUtils.isEmpty(beginTime))
             starDate.setText("2018-01-01");
        else
            starDate.setText(beginTime);
        if(TextUtils.isEmpty(endTime))
            endDate.setText(HashmapTojson.getTime1("yyyy-MM-dd"));
        else
            endDate.setText(endTime);
    }

    private void initSwich() {
        idSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type1 = getResources().getString(R.string.nevs_official);
                } else {
                    type1 = "";
                }
            }
        });
        switchTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type2 = getResources().getString(R.string.nevs_private);
                } else {
                    type2 = "";
                }
            }
        });
        switchThree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type3 = getResources().getString(R.string.nevs_custom);
                } else {
                    type3 = "";
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.setting_log, R.id.start_setting, R.id.end_setting, R.id.btn_create})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.setting_log://确认
//cc暂时不用这功能               if (type1.equals("") && type2.equals("") && type3.equals("")) {
//                    ActivityUtil.showToast(mContext,getResources().getString(R.string.toast_syszhuan));
//                }  else {
//                      finishSelect();
//                }

                finishSelect();


                break;
            case R.id.start_setting:
                showDialogs(1,beginTime);
                break;
            case R.id.end_setting:
                showDialogs(2,endTime);
                break;
            case R.id.btn_create:
                reView();
                break;
        }
    }

    private void reView() {
        idSwitch.setChecked(false);
        switchTwo.setChecked(false);
        switchThree.setChecked(false);
        beginTime = "2018-01-01";
        endTime =  HashmapTojson.getTime1("yyyy-MM-dd");
        starDate.setText(beginTime);
        endDate.setText(endTime);
    }

    private void showDialogs(final int a,String time) {
        long t = 0L;
        if(TextUtils.isEmpty(time))
                t=System.currentTimeMillis();
            else
                t = HashmapTojson.getStringToDates(time + " 00:00:00", "yyyy-MM-dd HH:mm:ss")*1000;
        DialogUIUtils.showDatePick(mContext, Gravity.CENTER, getResources().getString(R.string.toast_choosedate), t, DateSelectorWheelView.TYPE_YYYYMMDD, 0, new DialogUIDateTimeSaveListener() {
            @Override
            public void onSaveSelectedDate(int tag, String selectedDate) {

                long startTime = 0L,endTime=0L;
                switch (a) {
                    case 1:
                         endTime = HashmapTojson.getStringToDates(endDate.getText().toString() , "yyyy-MM-dd");
                         startTime = HashmapTojson.getStringToDates(selectedDate , "yyyy-MM-dd");
                        if(endTime-startTime>0)
                            starDate.setText(selectedDate);
                            else
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_data_not));

                        break;
                    case 2:

                        endTime = HashmapTojson.getStringToDates(selectedDate, "yyyy-MM-dd");
                        startTime = HashmapTojson.getStringToDates(starDate.getText().toString(), "yyyy-MM-dd");
                        if(endTime-startTime>0)
                            endDate.setText(selectedDate);
                        else
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_data_not));
                        break;
                }
            }
        }).show();
    }

    private void finishSelect() {
        if (HashmapTojson.getStringToDates(starDate.getText().toString(), "yyyy-MM-dd") > HashmapTojson.getStringToDates(endDate.getText().toString(), "yyyy-MM-dd")) {
            ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_data_not));
            return;
        }
        try {
            if (type1.length() > 1) {
                list.add(URLDecoder.decode(type1, "UTF-8"));
            }
            if (type2.length() > 1) {
                list.add(URLDecoder.decode(type2, "UTF-8"));
            }
            if (type3.length() > 1) {
                list.add(URLDecoder.decode(type3, "UTF-8"));
            }
        } catch (Exception e) {

        }

        Intent data = new Intent();
        data.putExtra("BeginTime", starDate.getText().toString());
        data.putExtra("EndTime", endDate.getText().toString());
        data.putStringArrayListExtra("Category", list);
        setResult(902, data);
        finish();
    }
}
