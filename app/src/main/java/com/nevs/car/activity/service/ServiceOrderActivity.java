package com.nevs.car.activity.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ispr.uilibrary.DialogUIUtils;
import com.ispr.uilibrary.listener.DialogUIDateTimeSaveListener;
import com.ispr.uilibrary.widget.DateSelectorWheelView;
import com.nevs.car.R;
import com.nevs.car.activity.ChooseCarActivity;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityManager;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyToast;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.PhoneNumberUtils;
import com.nevs.car.z_start.MainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ServiceOrderActivity extends BaseActivity {


    @BindView(R.id.text_fix)
    TextView textFix;
    @BindView(R.id.text_keep)
    TextView textKeep;
    @BindView(R.id.text_choose_car)
    EditText textChooseCar;
    @BindView(R.id.text_stop)
    EditText textStop;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.location)
    EditText location;
    @BindView(R.id.text_time)
    EditText textTime;
    @BindView(R.id.edit_land)
    EditText editLand;
    @BindView(R.id.isvlin)
    LinearLayout isvlin;
    @BindView(R.id.remark)
    EditText remark;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private List<String> list = new ArrayList<String>();
    private String type = null;
    private static final int REGION_REQUEST_CODE = 888;
    private String provinceCode = null;
    private String cityCode = null;
    private String areaCode = null;
    private String orgcode = "";
    private String types = "";
    private String ishealth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_service_order2;
    }

    @Override
    public void init(Bundle savedInstanceState) {
       // MyUtils.setPadding(nView,mContext);
        // initChoiceGroup();
        ActivityManager.getInstance().addActivity(ServiceOrderActivity.this);
        initView();
        initIntentHealth();
        initIntentServiceStop();

    }

    private void initIntentServiceStop(){
        try {
            if(getIntent().getStringExtra("stopname")==null){
                return;
            }
            String name = getIntent().getStringExtra("stopname");
            textStop.setText(name);
            orgcode = getIntent().getStringExtra("orgcode");
            types = getIntent().getStringExtra("type");
            if (types.equals("30031005")) {
                isvlin.setVisibility(View.VISIBLE);
            } else {
                isvlin.setVisibility(View.GONE);
            }
        }catch (Exception e){

        }

    }

    private void initIntentHealth() {
        if (getIntent().getStringExtra("ishealth") != null) {
            ishealth = "yes";
        }
    }

    private void initView() {
        editName.setText(new SharedPHelper(this).get(Constant.LOGINFAMILYNAME, "").toString() +
                new SharedPHelper(this).get(Constant.LOGINGIVENNAMME, "").toString()
        );
        editPhone.setText(new SharedPHelper(this).get(Constant.LOGINNAME, "").toString());
//        if(new SharedPHelper(this).get("servicestopname","").toString().trim().length()!=0){
//            textStop.setText(new SharedPHelper(this).get("servicestopname","").toString());
//        }
//        textChooseCar.setText(new SharedPHelper(this).get("TSPVIN", "").toString());
    }

//    private void initChoiceGroup() {
//        list.add("维修");
//        list.add("保养");
//        choiceGroup.setColumn(2);//设置列数
//        choiceGroup.setValues(list);//设置记录列表
//        choiceGroup.setView(this);//设置视图
//        choiceGroup.setInitChecked(0);//设置最初默认被选按钮
//        String type = choiceGroup.getCurrentValue();//获取当前被选择的按扭值，需要三方获取
//        MLog.e(type);
//    }

    @OnClick({R.id.back, R.id.btn_sublim, R.id.rel_choosecar, R.id.rel_order_time, R.id.rel_stop, R.id.rel_location
            , R.id.text_fix, R.id.text_keep, R.id.edit_name, R.id.edit_phone, R.id.edit_land
            , R.id.text_choose_car, R.id.text_stop, R.id.text_time, R.id.location,R.id.remark
    })
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_sublim:
                if (types.equals("30031005")) {
                    if (type == null) {
                        ActivityUtil.showToast(this, getResources().getString(R.string.toast_choosetype));
                    } else if (textChooseCar.getText().length() == 0 || textStop.getText().length() == 0 ||
                            textTime.getText().length() == 0 || location.getText().length() == 0 ||
                            editName.getText().length() == 0 || editPhone.getText().length() == 0 ||
                            editLand.getText().length() == 0 || remark.getText().length() == 0) {
                        ActivityUtil.showToast(this, getResources().getString(R.string.toast_enterall));
                    } else if (!PhoneNumberUtils.isMobileNO(editPhone.getText().toString().trim())) {
                        MyToast.showToast(this, getResources().getString(R.string.toast_phonenumber));
                    } else {
                        getHttp();
                    }
                } else {

                    if (type == null) {
                        ActivityUtil.showToast(this, getResources().getString(R.string.toast_choosetype));
                    } else if (textChooseCar.getText().length() == 0 || textStop.getText().length() == 0 ||
                            textTime.getText().length() == 0 || remark.getText().length() == 0 ||
                            editName.getText().length() == 0 || editPhone.getText().length() == 0
                    ) {
                        ActivityUtil.showToast(this, getResources().getString(R.string.toast_enterall));
                    } else if (!PhoneNumberUtils.isMobileNO(editPhone.getText().toString().trim())) {
                        MyToast.showToast(this, getResources().getString(R.string.toast_phonenumber));
                    } else {
                        getHttp2();
                    }

                }


                break;
            case R.id.rel_order_time:
                showDialogTime();
                break;
            case R.id.rel_choosecar:
                startActivityForResult(new Intent(this, ChooseCarActivity.class), 804);
                break;
            case R.id.rel_stop:
                startActivityForResult(new Intent(this, ChooseStopActivity.class), 805);
                break;
            case R.id.rel_location:
                startActivityForResult(new Intent(this, SelectCityActivity.class), REGION_REQUEST_CODE);
                break;
            case R.id.text_fix:
                /**
                 * 预约类型
                 保养 40061001
                 维修 40061002
                 保修 40061003
                 * */
                type = "40061002";
                MLog.e(type);
                textFix.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                textKeep.setBackgroundResource(R.drawable.bg_circle_rect_code);
                textFix.setTextColor(getResources().getColor(R.color.white));
                textKeep.setTextColor(getResources().getColor(R.color.text_default));
                break;
            case R.id.text_keep:
                type = "40061001";
                MLog.e(type);
                textKeep.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                textFix.setBackgroundResource(R.drawable.bg_circle_rect_code);
                textKeep.setTextColor(getResources().getColor(R.color.white));
                textFix.setTextColor(getResources().getColor(R.color.text_default));
                break;
            case R.id.edit_name:
                editName.setCursorVisible(true);
                break;
            case R.id.edit_phone:
                editPhone.setCursorVisible(true);
                break;
            case R.id.edit_land:
                editLand.setCursorVisible(true);
                break;
            case R.id.text_choose_car:
                startActivityForResult(new Intent(this, ChooseCarActivity.class), 804);
                break;
            case R.id.text_stop:
                /**
                 * 获取服务站信息的时候添加了字段type，type字段
                 销售展厅	30031001
                 售后服务中心 30031002
                 一体店(2S)	30031003
                 一体店(4S)	30031004
                 移动服务站	30031005
                 线上服务站	30031006
                 移动服务站是30031005，其他的你们做判断。
                 * */
                startActivityForResult(new Intent(this, ChooseStopActivity.class), 805);
                break;
            case R.id.location:
                startActivityForResult(new Intent(this, SelectCityActivity.class), REGION_REQUEST_CODE);
                break;
            case R.id.text_time:
                showDialogTime();
                break;
            case R.id.remark:
                remark.setCursorVisible(true);
                break;
        }
    }

    private void showDialogTime() {
        DialogUIUtils.showDatePick(ServiceOrderActivity.this, Gravity.CENTER, getResources().getString(R.string.toast_choosedate), System.currentTimeMillis() + 60000, DateSelectorWheelView.TYPE_YYYYMMDDHHMM, 0, new DialogUIDateTimeSaveListener() {
            @Override
            public void onSaveSelectedDate(int tag, String selectedDate) {

                //   textTime.setText(selectedDate.substring(0,selectedDate.length()-3));
                MLog.e("selectedDate:" + selectedDate);
                initTime(selectedDate);//预约时间为大于当天晚上24时
            }
        }).show();
    }

    private void initTime(String time) {
        long selecttime = HashmapTojson.getStringToDate(time + ":00", "yyyy-MM-dd HH:mm:ss");
        long nowtime = HashmapTojson.getCurTimeLong();
        MLog.e("selecttime:" + selecttime);
        MLog.e("nowtime:" + nowtime);
        if (MyUtils.setTime24(time, mContext)) {
            textTime.setText(time);
        } else {
            // ActivityUtil.showToast(mContext,getResources().getString(R.string.toast_later));
            //textTime.setText("");
        }
    }

    private void getHttp() {//服务预约
        /**
         * 参数名称	类型	是否必填	说明
         accessToken	字符串	是	访问令牌
         booking_type	字符串	是	预约类型
         vin	字符串	是	预约车辆
         orgcode	字符串		服务站
         booken_time	字符串		预约进厂时间
         deliver	字符串		送修人
         deliver_mobile	字符串		送修人电话
         area	字符串		送修人所在地
         address	字符串		送修人地址
         *
         * */
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getServiceResevation(ServiceOrderActivity.this,
                new String[]{"accessToken", "booking_type", "vin", "orgcode", "booking_time", "deliver", "deliver_mobile", "province", "city", "area", "address", "remark"},
                new Object[]{new SharedPHelper(ServiceOrderActivity.this).get(Constant.ACCESSTOKEN, ""),
                        type,
                        //  "LFBJDBB43WJ000118",//textChooseCar.getText().toString(),测试写死
                        textChooseCar.getText().toString(),
                        orgcode,
                        textTime.getText().toString(),
                        editName.getText().toString(),
                        editPhone.getText().toString(),
                        provinceCode,
                        cityCode,
                        areaCode,
                        editLand.getText().toString(),
                        remark.getText().toString()
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(ServiceOrderActivity.this, getResources().getString(R.string.toast_thankservice));
                        if (ishealth.equals("yes")) {
                            startActivity(new Intent(mContext, MainActivity.class).putExtra("isservice", "yes"));
                            ActivityManager.getInstance().exit();//一键回退到主页的服务界面
                        } else {
                            finish();
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
                            case "BookingOrderIsFull":
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.n_pool));
                                break;
                            case "BookingFaild":
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.n_please));
                                break;
                            default:
                              //  ActivityUtil.showToast(mContext, str);
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.unsefail));
                        }
                    }
                }
        );
    }

    private void getHttp2() {//服务预约
        /**
         * 参数名称	类型	是否必填	说明
         accessToken	字符串	是	访问令牌
         booking_type	字符串	是	预约类型
         vin	字符串	是	预约车辆
         orgcode	字符串		服务站
         booken_time	字符串		预约进厂时间
         deliver	字符串		送修人
         deliver_mobile	字符串		送修人电话
         area	字符串		送修人所在地
         address	字符串		送修人地址
         *
         * */
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getServiceResevation(ServiceOrderActivity.this,
                new String[]{"accessToken", "booking_type", "vin", "orgcode", "booking_time", "deliver", "deliver_mobile", "remark"},
                new Object[]{new SharedPHelper(ServiceOrderActivity.this).get(Constant.ACCESSTOKEN, ""),
                        type,
                        //  "LFBJDBB43WJ000118",//textChooseCar.getText().toString(),测试写死
                        textChooseCar.getText().toString(),
                        orgcode,
                        textTime.getText().toString(),
                        editName.getText().toString(),
                        editPhone.getText().toString(),
                        remark.getText().toString()
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(ServiceOrderActivity.this, getResources().getString(R.string.toast_thankservice));
                        finish();
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
                            case "BookingOrderIsFull":
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.n_pool));
                                break;
                            case "BookingFaild":
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.n_please));
                                break;
                            default:
                              //  ActivityUtil.showToast(mContext, str);
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.unsefail));
                        }
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGION_REQUEST_CODE && resultCode == 200) {
            String province = data.getStringExtra(SelectCityActivity.REGION_PROVINCE);
            String city = data.getStringExtra(SelectCityActivity.REGION_CITY);
            String area = data.getStringExtra(SelectCityActivity.REGION_AREA);
            location.setText(province + " " + city + " " + area);

            provinceCode = data.getStringExtra("PROVICECODE");
            MLog.e("provinceCode" + provinceCode);
            cityCode = data.getStringExtra("CITYCODE");
            MLog.e("cityCode" + cityCode);
            areaCode = data.getStringExtra("AREACODE");
            MLog.e("areaCode" + areaCode);
        } else if (requestCode == 804 && resultCode == 904) {
            String vin = data.getStringExtra("intentvin");
            textChooseCar.setText(vin);
//            if(vin.equals("LTPSB1412H1000889")){
//                textChooseCar.setText("LGWFF4A5XGF153655");
//            }else {
//                textChooseCar.setText("LTPCHINATELE00123");
//            }

            new SharedPHelper(this).put("servicechoosecar", "LGWFF4A5XGF153655");
        } else if (requestCode == 805 && resultCode == 905) {
            String name = data.getStringExtra("stopname");
            textStop.setText(name);
            orgcode = data.getStringExtra("orgcode");
            types = data.getStringExtra("type");
            if (types.equals("30031005")) {
                isvlin.setVisibility(View.VISIBLE);
            } else {
                isvlin.setVisibility(View.GONE);
            }
            // new SharedPHelper(this).put("servicestopname",vin);
        }
    }
}
