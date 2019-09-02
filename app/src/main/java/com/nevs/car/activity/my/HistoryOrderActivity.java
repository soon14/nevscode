package com.nevs.car.activity.my;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryOrderActivity extends BaseActivity {


    @BindView(R.id.text_one)
    TextView textOne;
    @BindView(R.id.text_two)
    TextView textTwo;
    @BindView(R.id.text_three)
    TextView textThree;
    @BindView(R.id.text_four)
    TextView textFour;
    @BindView(R.id.text_five)
    TextView textFive;
    @BindView(R.id.text_six)
    TextView textSix;
    @BindView(R.id.text_seven)
    TextView textSeven;
    @BindView(R.id.text_eight)
    TextView textEight;
    @BindView(R.id.isvlin)
    LinearLayout isvlin;
    @BindView(R.id.text_descrep)
    TextView textDescrep;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String bookingNo = null;
    private String ordertype = null;
    private List<HashMap<String, Object>> listHistory = new ArrayList<>();

    @Override
    public int getContentViewResId() {
        return R.layout.activity_history_order;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initIntent();
        getDetailHttp();
    }

    private void initIntent() {
        bookingNo = getIntent().getStringExtra(Constant.BOOKINGNO);
        MLog.e("xqbookingno:" + bookingNo);
        ordertype = getIntent().getStringExtra("ordertype");
        MLog.e("ordertype:" + ordertype);
    }

    @OnClick(R.id.back)
    public void onClick() {
        finish();
    }

    private void getDetailHttp() {//详情
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getBookingDetails(HistoryOrderActivity.this,
                new String[]{"accessToken", "booking_no"},
                new Object[]{new SharedPHelper(HistoryOrderActivity.this).get(Constant.ACCESSTOKEN, ""),
                        bookingNo
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        DialogUtils.hidding((Activity) mContext);
                        //获取详情显示
                        listHistory.addAll((Collection<? extends HashMap<String, Object>>) list);
                        if (listHistory.size() != 0) {
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
        textTwo.setText(listHistory.get(0).get("license_no").toString());
        textThree.setText(listHistory.get(0).get("dealer_name").toString());
        textFour.setText(listHistory.get(0).get("booking_time").toString());
        textFive.setText(listHistory.get(0).get("deliver").toString());
        textSix.setText(listHistory.get(0).get("deliver_mobile").toString());
        textSeven.setText(listHistory.get(0).get("province_name").toString() + listHistory.get(0).get("city_name").toString()
                + listHistory.get(0).get("area_name").toString()
        );
        textEight.setText(listHistory.get(0).get("address").toString());
        textDescrep.setText(listHistory.get(0).get("remark").toString());
        if (listHistory.get(0).get("address").toString().length() == 0) {
            isvlin.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
