package com.nevs.car.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nevs.car.R;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.DataSearchAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataSearchActivity extends BaseActivity {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.xrefresh_view)
    XRefreshView xRefreshView;
    @BindView(R.id.public404)
    LinearLayout public404;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private BaseQuickAdapter myAdapter;
    private int mLoadCount = 0;
    private List<HashMap<String, Object>> listDatas = new ArrayList<>();
    private List<HashMap<String, Object>> listDatar = new ArrayList<>();

    @Override
    public int getContentViewResId() {
        return R.layout.activity_data_search;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initRecyclyView();
        initOnclickChildListener();
        getList();
    }
    private void getList() {
        MLog.e("time===>DataSearchActivity");
        DialogUtils.loading(this, true);
        listDatas.clear();
        listDatar.clear();
        HttpRxUtils.getUsageData(
                mContext,
                new String[]{"accessToken", "imsi", "type", "QtyType"},
                new Object[]{new SharedPHelper(mContext).get(Constant.REGISTCENACCESSTOKEN, ""),
                        new SharedPHelper(mContext).get(Constant.imsi, ""),
                        "Month",
                       "APP"
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(DataSearchActivity.this);
                        listDatas.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (listDatas.size() == 0) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else {
                            Map<String, Object> map0 = new HashMap<String, Object>();
                            map0.put("time", getResources().getString(R.string.item_time));
                            map0.put("data", getResources().getString(R.string.item_data));
                            map0.put("possion", "0");
                            listDatar.add((HashMap<String, Object>) map0);
                            for (int i = 0; i < listDatas.size(); i++) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("time", listDatas.get(i).get("year_month") + "");
                                map.put("data", listDatas.get(i).get("total_data") + "");
                                map.put("possion", "1");
                                listDatar.add((HashMap<String, Object>) map);
                            }

                            myAdapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(DataSearchActivity.this);
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

    private void getList0() {
        MLog.e("time===>DataSearchActivity");
        DialogUtils.loading(this, true);
        listDatas.clear();
        listDatar.clear();
        HttpRxUtils.getUsageData(
                mContext,
                new String[]{"accessToken", "month", "deviceID", "type", "msisdn", "appType"},
                new Object[]{new SharedPHelper(mContext).get(Constant.REGISTCENACCESSTOKEN, ""),
                        HashmapTojson.getTime1("yyyy-MM"),
                        DeviceUtils.getUniqueId(DataSearchActivity.this),
                        "Month",
                        //new SharedPHelper(DataSearchActivity.this).get(Constant.MISISDN, ""),
                        "8614928270027",
                        "Android",
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(DataSearchActivity.this);
                        listDatas.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (listDatas.size() == 0) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else {
                            Map<String, Object> map0 = new HashMap<String, Object>();
                            map0.put("time", getResources().getString(R.string.item_time));
                            map0.put("data", getResources().getString(R.string.item_data));
                            map0.put("possion", "0");
                            listDatar.add((HashMap<String, Object>) map0);
                            for (int i = 0; i < listDatas.size(); i++) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("time", listDatas.get(i).get("month") + "");
                                map.put("data", listDatas.get(i).get("total_data") + "");
                                map.put("possion", "1");
                                listDatar.add((HashMap<String, Object>) map);
                            }

                            myAdapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(DataSearchActivity.this);
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

    @OnClick({R.id.back, R.id.tv_title})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_title:
                break;
        }
    }

    private void initRecyclyView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));  //设置RecyclerView的显示模式
        //设置刷新完成以后，headerview固定的时间
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setAutoLoadMore(false);
        // myAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        xRefreshView.enableReleaseToLoadMore(true);
        xRefreshView.enableRecyclerViewPullUp(true);
        xRefreshView.enablePullUpWhenLoadCompleted(true);
        //设置静默加载时提前加载的item个数
//        xRefreshView1.setPreLoadCount(4);

        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        xRefreshView.stopRefresh();
                    }
                }, 500);
                //getList();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
//                        for (int i = 0; i < 6; i++) {
//                            recyclerviewAdapter.insert(new Person("More ", mLoadCount + "21"),
//                                    recyclerviewAdapter.getAdapterItemCount());
//                        }
                        mLoadCount++;
                        if (mLoadCount >= 3) {//模拟没有更多数据的情况
                            xRefreshView.setLoadComplete(true);
                        } else {
                            // 刷新完成必须调用此方法停止加载
                            xRefreshView.stopLoadMore(false);
                            //当数据加载失败 不需要隐藏footerview时，可以调用以下方法，传入false，不传默认为true
                            // 同时在Footerview的onStateFinish(boolean hideFooter)，可以在hideFooter为false时，显示数据加载失败的ui
//                            xRefreshView1.stopLoadMore(false);
                        }
                    }
                }, 1000);
            }
        });

        myAdapter = new DataSearchAdapter(R.layout.item_datasearch, listDatar); //设置适配器
        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;

    }

    private void initOnclickChildListener() {
        myAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (position != 0) {
                    startActivity(new Intent(DataSearchActivity.this, DataSearchEnterActivity.class).putExtra("time", listDatar.get(position).get("time") + ""));
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
}
