package com.nevs.car.activity.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.LocationAdapter;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
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

public class SelectCityActivity extends BaseActivity implements BaseQuickAdapter.OnRecyclerViewItemClickListener {
    public static final String REGION_PROVINCE = "region_province";
    public static final String REGION_CITY = "region_city";
    public static final String REGION_AREA = "region_area";
    private static final int RESULT_CODE_SUCCESS = 200;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.list)
    RecyclerView mRecyclerView;
    @BindView(R.id.n_view)
    RelativeLayout nView;

    private LocationAdapter mAdapter;


    private List<HashMap<String, Object>> mList = new ArrayList<>();

    private List<HashMap<String, Object>> mProvinceList = new ArrayList<>();
    private List<HashMap<String, Object>> mCityList = new ArrayList<>();
    private List<HashMap<String, Object>> mAreaList = new ArrayList<>();
    private int state = 0;

    private String mProvince = "";
    private String mCity = "";
    private String mArea = "";
    private String mProvinceCode = "";
    private String mCityCode = "";
    private String mAreaCode = "";

    private int isf = 0;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_select_city;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initView();
        getProvinceHttp();
    }

    private void initView() {
        mAdapter = new LocationAdapter(mList);
        mAdapter.setOnRecyclerViewItemClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void getProvinceHttp() {
        /**
         *
         参数名称	类型	是否必填	说明
         accessToken	字符串	是	访问令牌
         type	字符串	是	"说明获取的省市区的类型：
         province：省
         city：市
         area：区"
         language	选择语言	否	未实现
         parentid	字符串	是	0表示查询省列表，其他的填其父id
         * */
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getPositionList(SelectCityActivity.this,
                new String[]{"accessToken", "type", "language", "parentid"},
                new Object[]{new SharedPHelper(SelectCityActivity.this).get(Constant.ACCESSTOKEN, ""),
                        "province", "", "0"
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        DialogUtils.hidding((Activity) mContext);
                        mProvinceList.addAll((Collection<? extends HashMap<String, Object>>) list);
                        mRecyclerView.setAdapter(mAdapter);
                        tvTitle.setText(getResources().getString(R.string.select_location));
                        mAdapter.addData(mProvinceList);
                        isf = 0;
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
                });
    }

    private void getCityHttp(String parentid) {
        DialogUtils.loading((Activity) mContext, true);
        HttpRxUtils.getPositionList(SelectCityActivity.this,
                new String[]{"accessToken", "type", "language", "parentid"},
                new Object[]{new SharedPHelper(SelectCityActivity.this).get(Constant.ACCESSTOKEN, ""),
                        "city", "", parentid
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        DialogUtils.hidding((Activity) mContext);
                        mCityList.addAll((Collection<? extends HashMap<String, Object>>) list);
                        mRecyclerView.setAdapter(mAdapter);
                        tvTitle.setText(getResources().getString(R.string.toast_choosecity));
                        mList.clear();
                        mAdapter.addData(mCityList);
                        state++;
                        isf = 1;
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
                });
    }

    private void getAreaHttp(String pid) {
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getPositionList(SelectCityActivity.this,
                new String[]{"accessToken", "type", "language", "parentid"},
                new Object[]{new SharedPHelper(SelectCityActivity.this).get(Constant.ACCESSTOKEN, ""),
                        "area", "", pid
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        DialogUtils.hidding((Activity) mContext);
                        mAreaList.addAll((Collection<? extends HashMap<String, Object>>) list);
                        MLog.e("mAreaList" + mAreaList.size() + "");
                        if (mAreaList.size() == 0) {
                            //防止有的城市没有县级
                            finishSelect();
                        } else {
                            mRecyclerView.setAdapter(mAdapter);
                            tvTitle.setText(getResources().getString(R.string.toast_choosearea));
                            mList.clear();
                            mAdapter.addData(mAreaList);
                            state++;
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
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        HashMap<String, Object> map = mAdapter.getItem(position);
        if (state == 0) {
            tvTitle.setText(getResources().getString(R.string.toast_choosecity));
            if (mProvinceList.size() != 0) {
                getCityHttp(mProvinceList.get(position).get("regionid").toString());
            }
            mProvince = map.get("name").toString();
            try {
                mProvinceCode = map.get("regionid").toString();
            } catch (Exception e) {

            }

        } else if (state == 1) {
//            tvTitle.setText("选择地区");
//            if(mCityList.size()!=0){
//                getAreaHttp(mCityList.get(position).get("regionid").toString());
//            }
            mCity = map.get("name").toString();
            try {
                mCityCode = map.get("regionid").toString();
            } catch (Exception e) {

            }
            state++;
            finishSelect();
        } else if (state == 2) {
//            mArea = map.get("name").toString();
//            state++;
//            finishSelect();
        }
    }


    /**
     * 完成
     */
    private void finishSelect() {
        Intent data = new Intent();
        data.putExtra(REGION_PROVINCE, mProvince);
        data.putExtra(REGION_CITY, mCity);
        data.putExtra(REGION_AREA, mArea);

        data.putExtra("PROVICECODE", mProvinceCode);
        data.putExtra("CITYCODE", mCityCode);
        data.putExtra("AREACODE", mAreaCode);
        setResult(RESULT_CODE_SUCCESS, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (state == 0) {
            super.onBackPressed();
        }
        if (state == 1) {
            tvTitle.setText(getResources().getString(R.string.select_location));
            mList.clear();
            mAdapter.addData(mProvinceList);
            state--;
        } else if (state == 2) {
            tvTitle.setText(getResources().getString(R.string.toast_choosecity));
            mList.clear();
            mAdapter.addData(mCityList);
            state--;
        }
    }

    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                if (isf == 0) {
                    finish();
                } else {
                    state = 0;
                    mProvinceList.clear();
                    mCityList.clear();
                    mList.clear();
                    getProvinceHttp();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
