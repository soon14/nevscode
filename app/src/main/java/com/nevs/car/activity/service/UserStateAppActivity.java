package com.nevs.car.activity.service;

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
import com.nevs.car.adapter.UserStateAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.z_start.WebActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserStateAppActivity extends BaseActivity {
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
    private List<HashMap<String, Object>> listNews = new ArrayList<>();
    private String URLS[] = new String[9];
    private String TITLES[] = new String[9];


    @Override
    public int getContentViewResId() {
        return R.layout.activity_user_state_app;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initRecyclyView();
        // getList();
        initAdapter();
    }

    private void initAdapter() {
        URLS = new String[]{
                Constant.HTTP.BANNERURL + "web/appmanual/appmanual_001.jpg",
                Constant.HTTP.BANNERURL + "web/appmanual/appmanual_002.jpg",
                Constant.HTTP.BANNERURL + "web/appmanual/appmanual_003.jpg",
                Constant.HTTP.BANNERURL + "web/appmanual/appmanual_004.jpg",
                Constant.HTTP.BANNERURL + "web/appmanual/appmanual_005.jpg",
                Constant.HTTP.BANNERURL + "web/appmanual/appmanual_006.jpg",
                Constant.HTTP.BANNERURL + "web/appmanual/appmanual_007.jpg",
                Constant.HTTP.BANNERURL + "web/appmanual/appmanual_008.jpg",
                Constant.HTTP.BANNERURL + "web/appmanual/appmanual_009.jpg"
        };
        TITLES = new String[]{
                getResources().getString(R.string.questions1),
                getResources().getString(R.string.questions2),
                getResources().getString(R.string.questions3),
                getResources().getString(R.string.questions4),
                getResources().getString(R.string.questions5),
                getResources().getString(R.string.questions6),
                getResources().getString(R.string.questions7),
                getResources().getString(R.string.questions8),
                getResources().getString(R.string.questions9)
        };
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map2 = new HashMap<>();
        Map<String, Object> map3 = new HashMap<>();
        Map<String, Object> map4 = new HashMap<>();
        Map<String, Object> map5 = new HashMap<>();
        Map<String, Object> map6 = new HashMap<>();
        Map<String, Object> map7 = new HashMap<>();
        Map<String, Object> map8 = new HashMap<>();
        Map<String, Object> map9 = new HashMap<>();

        map1.put("question", getResources().getString(R.string.questions1));
        map2.put("question", getResources().getString(R.string.questions2));
        map3.put("question", getResources().getString(R.string.questions3));
        map4.put("question", getResources().getString(R.string.questions4));
        map5.put("question", getResources().getString(R.string.questions5));
        map6.put("question", getResources().getString(R.string.questions6));
        map7.put("question", getResources().getString(R.string.questions7));
        map8.put("question", getResources().getString(R.string.questions8));
        map9.put("question", getResources().getString(R.string.questions9));
        listNews.add((HashMap<String, Object>) map1);
        listNews.add((HashMap<String, Object>) map2);
        listNews.add((HashMap<String, Object>) map3);
        listNews.add((HashMap<String, Object>) map4);
        listNews.add((HashMap<String, Object>) map5);
        listNews.add((HashMap<String, Object>) map6);
        listNews.add((HashMap<String, Object>) map7);
        listNews.add((HashMap<String, Object>) map8);
        listNews.add((HashMap<String, Object>) map9);
        myAdapter = new UserStateAdapter(R.layout.item_userstate, listNews); //设置适配器
        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
        initOnclickListener();
    }


    private void getList() {
        //{"question":"问题1","answer":"http://www.baidu.com","status":"90051001","order":"4","CreationBy":"1",
        // "CreateTime":"2018-06-14T19:50:13","LastUpdatedBy":"1","LastUpdateTime":"2018-06-30T18:30:36","Version":1,"IsEnabled":0}
        DialogUtils.loading(this, true);
        listNews.clear();
        HttpRxUtils.getVehicleHandbook(
                UserStateAppActivity.this,
                new String[]{"accessToken"},
                new Object[]{new SharedPHelper(UserStateAppActivity.this).get(Constant.ACCESSTOKEN, ""),
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        xRefreshView.stopRefresh();
                        DialogUtils.hidding(UserStateAppActivity.this);
                        public404.setVisibility(View.GONE);
                        listNews.addAll((Collection<? extends HashMap<String, Object>>) list);
                        if (listNews.size() == 0) {
                            public404.setVisibility(View.VISIBLE);
                        } else {
                            myAdapter = new UserStateAdapter(R.layout.item_userstate, listNews); //设置适配器
                            myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
                            mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
                            initOnclickListener();
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        xRefreshView.stopRefresh();
                        DialogUtils.hidding(UserStateAppActivity.this);
                        // public404.setVisibility(View.VISIBLE);
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

    @OnClick({R.id.back, R.id.refresh})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.refresh:
                //getList();
                break;

        }
    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent i = new Intent(UserStateAppActivity.this, WebActivity.class);
                i.putExtra("URL", URLS[position]);
                i.putExtra("TITLE", TITLES[position]);
                startActivity(i);


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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        xRefreshView.stopRefresh();
                    }
                }, 500);
                // getList();
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}

