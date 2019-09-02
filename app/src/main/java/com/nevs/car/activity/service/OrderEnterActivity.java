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
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyToast;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OrderEnterActivity extends BaseActivity {
    @BindView(R.id.text_time)
    TextView textTime;
    @BindView(R.id.text_one)
    TextView textOne;
    @BindView(R.id.text_two)
    TextView textTwo;
    @BindView(R.id.text_three)
    TextView textThree;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.location)
    EditText location;
    @BindView(R.id.edit_land)
    EditText editLand;
    @BindView(R.id.isvlin)
    LinearLayout isvlin;
    @BindView(R.id.edit_descrep)
    EditText editDescrep;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String bookingNo = null;
    private String ordertype = null;
    private BaseAnimatorSet mBasIn;
    private BaseAnimatorSet mBasOut;
    private List<HashMap<String, Object>> listOrder = new ArrayList<>();
    private static final int REGION_REQUEST_CODE = 888;
    private String provinceCode = null;
    private String cityCode = null;
    private String areaCode = null;
    private String bookingtype = null;
    private String orgCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_order_enter;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        bookingNo = getIntent().getStringExtra(Constant.BOOKINGNO);
        MLog.e("xqbookingno:" + bookingNo);
        ordertype = getIntent().getStringExtra("ordertype");
        MLog.e("ordertype:" + ordertype);
        mBasIn = new BounceTopEnter();
        mBasOut = new SlideBottomExit();
        getDetailHttp();//详情预约
    }

    private void getDetailHttp() {//详情
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getBookingDetails(OrderEnterActivity.this,
                new String[]{"accessToken", "booking_no"},
                new Object[]{new SharedPHelper(OrderEnterActivity.this).get(Constant.ACCESSTOKEN, ""),
                        bookingNo
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        //获取详情显示
                        listOrder.addAll((Collection<? extends HashMap<String, Object>>) s);
                        if (listOrder.size() != 0) {
                            upView();
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

    private void upView() {
        textOne.setText(ordertype);
        textTwo.setText(listOrder.get(0).get("license_no").toString());
        textThree.setText(listOrder.get(0).get("dealer_name").toString());
        textTime.setText(listOrder.get(0).get("booking_time").toString());
        editName.setText(listOrder.get(0).get("deliver").toString());
        editPhone.setText(listOrder.get(0).get("deliver_mobile").toString());
        location.setText(listOrder.get(0).get("province_name").toString() + listOrder.get(0).get("city_name").toString()
                + listOrder.get(0).get("area_name").toString()
        );
        editLand.setText(listOrder.get(0).get("address").toString());
        editDescrep.setText(listOrder.get(0).get("remark").toString());
        provinceCode = listOrder.get(0).get("province").toString();
        cityCode = listOrder.get(0).get("city").toString();
        areaCode = listOrder.get(0).get("area").toString();
        bookingtype = listOrder.get(0).get("booking_type").toString();
        orgCode = listOrder.get(0).get("org_code").toString();

        if (listOrder.get(0).get("address").toString().length() == 0) {
            isvlin.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.back, R.id.tv_cancel, R.id.btn_sublim, R.id.location, R.id.text_time,
            R.id.rel_order_time, R.id.rel_location, R.id.edit_name, R.id.edit_phone, R.id.edit_land
    })
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_cancel:
                showDialogKill();
                break;
            case R.id.btn_sublim:
                if (!PhoneNumberUtils.isMobileNO(editPhone.getText().toString().trim())) {
                    MyToast.showToast(this, getResources().getString(R.string.toast_phonenumber));
                } else {
                    getHttp();//更新预约
                }

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
            case R.id.rel_order_time:
                showDialogTime();
                break;
            case R.id.text_time:
                showDialogTime();
                break;
            case R.id.rel_location:
                startActivityForResult(new Intent(this, SelectCityActivity.class), REGION_REQUEST_CODE);
                break;
            case R.id.location:
                startActivityForResult(new Intent(this, SelectCityActivity.class), REGION_REQUEST_CODE);
                break;
        }
    }

    private void showDialogKill() {
        final NormalDialog dialog = new NormalDialog(OrderEnterActivity.this);
        dialog.content(getResources().getString(R.string.toast_confirmcancle))//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        getHttpCancel();//取消预约
                    }
                });
    }

    private void getHttpCancel() {//取消预约
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getCancelBookingDetails(OrderEnterActivity.this,
                new String[]{"accessToken", "booking_no"},
                new Object[]{new SharedPHelper(OrderEnterActivity.this).get(Constant.ACCESSTOKEN, ""),
                        bookingNo
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(OrderEnterActivity.this, getResources().getString(R.string.toast_cancelsuccess));
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
                            default:
                                ActivityUtil.showToast(mContext, str);
                        }
                    }
                }
        );
    }

    private void getHttp() {//更新预约
        /**
         参数名称	类型	是否必填	说明
         accessToken	字符串	是	访问令牌
         booking_no	字符串	是	预约编号
         book_end_time	字符串	是	预约进厂时间
         deliver	字符串	是	送修人
         deliver_mobile	字符串	是	送修人电话
         area	字符串	是	送修人所在地
         address	字符串	是	送修人地址
         *
         * */
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getUpdateBookingDetails(OrderEnterActivity.this,
                new String[]{"accessToken", "booking_no", "booking_type", "orgcode", "booking_time", "deliver", "deliver_mobile", "province", "city", "area", "address", "remark"},
                new Object[]{new SharedPHelper(OrderEnterActivity.this).get(Constant.ACCESSTOKEN, ""),
                        bookingNo,
                        bookingtype,
                        orgCode,
                        textTime.getText().toString(),
                        editName.getText().toString(),
                        editPhone.getText().toString(),
                        provinceCode,
                        cityCode,
                        areaCode,
                        editLand.getText().toString(),
                        editDescrep.getText().toString()
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(OrderEnterActivity.this, getResources().getString(R.string.toast_submitsuccess));
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
                            default:
                                ActivityUtil.showToast(mContext, str);
                        }
                    }
                }
        );
    }

    private void showDialogTime() {
        DialogUIUtils.showDatePick(OrderEnterActivity.this, Gravity.CENTER, getResources().getString(R.string.toast_choosedate), System.currentTimeMillis() + 60000, DateSelectorWheelView.TYPE_YYYYMMDDHHMM, 0, new DialogUIDateTimeSaveListener() {
            @Override
            public void onSaveSelectedDate(int tag, String selectedDate) {

                // textTime.setText(selectedDate);
                initTime(selectedDate);

            }
        }).show();
    }

    private void initTime(String time) {
        long selecttime = HashmapTojson.getStringToDate(time + ":00", "yyyy-MM-dd HH:mm:ss");
        long nowtime = HashmapTojson.getCurTimeLong();
        MLog.e("selecttime:" + selecttime);
        MLog.e("nowtime:" + nowtime);
//        if ((selecttime - nowtime) >= 24 * 3600 * 1000) {
//            textTime.setText(time);
//        } else {
//            ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_later));
//           // textTime.setText("");
//        }
        if (MyUtils.setTime24(time, mContext)) {
            textTime.setText(time);
        } else {
            //  ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_later));
            // textTime.setText("");
        }
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

        }
    }
}

