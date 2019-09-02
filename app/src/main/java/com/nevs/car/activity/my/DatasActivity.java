package com.nevs.car.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DeviceUtils;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.MyCircleView;
import com.nevs.car.tools.view.MySinkingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DatasActivity extends BaseActivity {

    @BindView(R.id.sinking)
    MySinkingView mSinkingView;
    @BindView(R.id.circle)
    MyCircleView circle;
    @BindView(R.id.single)
    TextView single;
    @BindView(R.id.date_end)
    TextView dateEnd;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    @BindView(R.id.n_toatle)
    TextView nToatle;
    @BindView(R.id.n_next)
    TextView nNext;
    @BindView(R.id.n_ing)
    TextView nIng;
    @BindView(R.id.n_date)
    TextView nDate;
    private float percent = 0f;
    private float percentHttp = 0f;
    private List<HashMap<String, Object>> list0 = new ArrayList<>();
    private List<HashMap<String, Object>> list = new ArrayList<>();
    private String dataOne = "";
    private String dataTwo = "";
    private double toatles = 1;
    private double nexts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_datas;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // initSingle();
        MyUtils.setPadding(nView, mContext);
//        initMySinkingView();
//        initMyCicleView();
        getList();
    }

    private void upView(String json) {//余量
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject dataBalance = data.getJSONObject("dataBalance");
            JSONObject bucket = dataBalance.getJSONObject("bucket");
            String remainValue = bucket.getString("remainValue");
            String expirationDate=bucket.getString("expirationDate");
            dateEnd.setText(expirationDate.substring(0,10)+" "+expirationDate.substring(11,19));
            nexts = Long.parseLong(remainValue) / 1024;
            int cc= (int) nexts;
            nNext.setText(cc+"M");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void upView2(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            toatles = jsonObject.getDouble("data");
            int dd= (int) toatles;
            nToatle.setText(dd+"M");
            int cc= (int) nexts;
            int bb=dd-cc;
            nIng.setText(bb+"M");
            int aa= (int) (toatles/30);
            nDate.setText(aa+"M");
            initMySinkingView();
            initMyCicleView();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void initSingle() {

        if (new SharedPHelper(mContext).get(Constant.SINGLEONE, "0").toString().equals("0")) {
            single.setVisibility(View.GONE);
        } else {
            single.setVisibility(View.VISIBLE);
        }
    }

    private void initMySinkingView() {
        percentHttp = MyUtils.getTwoPoint(nexts, toatles);
        MLog.e("剩余百分比：" + percentHttp + "");
        test();
    }

    private void initMyCicleView() {
        circle.setArcWidth(10.0f);
        testBig();
    }

    @OnClick({R.id.back, R.id.setting, R.id.data_buy, R.id.data_order, R.id.data_search})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.setting:
                // startActivity(new Intent(DatasActivity.this, DataSettingActivity.class));
                break;
            case R.id.data_buy:
                startActivity(new Intent(DatasActivity.this, DataBuyActivity.class));
                break;
            case R.id.data_order:
                startActivity(new Intent(DatasActivity.this, DataOrderActivity.class));
                break;
            case R.id.data_search:
                startActivity(new Intent(DatasActivity.this, DataSearchActivity.class));
                break;
        }
    }

    private void test() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                percent = 0;
                while (percent < percentHttp+0.01f) {
                    try {
                        mSinkingView.setPercent(percent);
                        percent += 0.01f;

                        Thread.sleep(20);//40
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                MLog.e("percentHttp" + percentHttp);
                //  mSinkingView.setPercent(percentHttp);
//                // mSinkingView.clear();

            }
        });
        thread.start();
    }

    private void testBig() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                float percentbig = 0;
                while (percentbig <percentHttp) {
                    final float finalPercentbig = percentbig;
                    DatasActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                circle.refresh(finalPercentbig);
                            } catch (Exception e) {

                            }
                        }
                    });
                    try {
                        percentbig += 0.01f;
                        Thread.sleep(20);
                    } catch (Exception e) {

                    }

                }

            }
        });
        thread.start();
    }


    private void getList0() {
        MLog.e("time===>DatasActivity");
        DialogUtils.loading(this, true);
        list0.clear();
        HttpRxUtils.getUsageData(
                mContext,
                new String[]{"accessToken", "month", "deviceID", "type", "msisdn", "appType"},
                new Object[]{new SharedPHelper(mContext).get(Constant.REGISTCENACCESSTOKEN, ""),
                        HashmapTojson.getTime1("yyyy-MM"),
                        DeviceUtils.getUniqueId(DatasActivity.this),
                        "Month",
                        "8614928270027",
                        //  new SharedPHelper(DatasActivity.this).get(Constant.MISISDN, ""),
                        "Android",
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(DatasActivity.this);
                        list0.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (list0.size() == 0) {

                        } else {
                            MLog.e("aaa:" + list0.get(0).get("total_data"));
                            // upView(json);
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(DatasActivity.this);
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
                });
    }

    private void getList() {
        DialogUtils.loading(this, true);
        list.clear();
        HttpRxUtils.getUsageRemainData(
                mContext,
                new String[]{"accessToken", "imsi", "version"},
                new Object[]{new SharedPHelper(mContext).get(Constant.REGISTCENACCESSTOKEN, ""),
                        new SharedPHelper(mContext).get(Constant.imsi, ""),
                        "v1"
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(DatasActivity.this);
                        String json = String.valueOf(obj);
                        upView(json);
                        getList2();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(DatasActivity.this);
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
                });
    }

    private void getList2() {
        DialogUtils.loading(this, true);
        HttpRxUtils.getSearchB2CFlow(
                mContext,
                new String[]{"accessToken", "IMSI"},
                new Object[]{new SharedPHelper(mContext).get(Constant.REGISTCENACCESSTOKEN, ""),
                        new SharedPHelper(mContext).get(Constant.imsi, ""),
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(DatasActivity.this);
                        String json = String.valueOf(obj);
                        upView2(json);
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(DatasActivity.this);
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
                });
    }


}
