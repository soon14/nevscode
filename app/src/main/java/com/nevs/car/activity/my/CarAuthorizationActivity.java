package com.nevs.car.activity.my;

import android.app.Activity;
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
import com.nevs.car.adapter.CarAuthorizationAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarAuthorizationActivity extends BaseActivity {
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String auvin = "";
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.xrefresh_view)
    XRefreshView xRefreshView;
    @BindView(R.id.public404)
    LinearLayout public404;
    private BaseQuickAdapter myAdapter;
    private int mLoadCount = 0;
    private List<HashMap<String, Object>> list = new ArrayList<>();

    @Override
    public int getContentViewResId() {
        return R.layout.activity_car_authorization;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        auvin = getIntent().getStringExtra("AUVIN");
        MLog.e("auvin:" + auvin);
        initRecyclyView();
        // getTsp10();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MLog.e("bkkjonRestart" + auvin);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MLog.e("bkkjonResume" + auvin);
        getTsp10();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.add})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.add:
                startActivity(new Intent(this, CarAuthorizationAddActivity.class)
                        .putExtra("AUVIN", auvin)
                );
                break;

        }
    }

    private void getTsp10() {
        /**
         *{"items":[{"bindingId":"ea2d093e-0077-4a22-8a40-dc949a6a509c","targetUserAccount":"String","startTime":0,"endTime":0,"permissions":[0]}],"resultMessage":"","resultDescription":""}
         * */
        list.clear();
        DialogUtils.loading(mContext, true);
        TspRxUtils.getAuthorizeusers(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(CarAuthorizationActivity.this).get(Constant.ACCESSTOKENS, "")},
                auvin,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "授权车辆列表", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        public404.setVisibility(View.GONE);
                        xRefreshView.stopRefresh();
                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (list.size() == 0) {
                            // public404.setVisibility(View.VISIBLE);
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.unauth));
                            myAdapter.notifyDataSetChanged();
                        } else {
                            myAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        xRefreshView.stopRefresh();
                        //  public404.setVisibility(View.VISIBLE);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.unauth));
                        myAdapter.notifyDataSetChanged();
                        MyUtils.upLogTSO(mContext, "授权车辆列表", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(list.size()==0){
                    return;
                }
//
                startActivity(new Intent(CarAuthorizationActivity.this, CarAuthorizationEnterActivity.class)
                        .putExtra("AUVIN", auvin)
                        .putExtra("startTime", list.get(position).get("startTime").toString())
                        .putExtra("endTime", list.get(position).get("endTime").toString())
                        .putExtra("targetUserAccount", list.get(position).get("targetUserAccount").toString())
                        .putExtra("permissions", (Serializable) list.get(position).get("permissions"))
                        .putExtra("bindingId", list.get(position).get("bindingId").toString())
                        .putExtra("nickName", list.get(position).get("nickName").toString())
                );


            }
        });
        //条目长按点击事件
        myAdapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                // MyToast.showToast(NewsActivity.this, "长按点击了" + position);
                return true;
            }
        });
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
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        xRefreshView.stopRefresh();
//                    }
//                }, 500);
                getTsp10();
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


        myAdapter = new CarAuthorizationAdapter(R.layout.item_author, list); //设置适配器
        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
        initOnclickListener();
    }
}
