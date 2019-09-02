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
import com.nevs.car.adapter.NewsListAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
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
import com.nevs.car.z_start.WebActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class NewsActivity extends BaseActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_news;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initRecyclyView();
        getList();//获取新闻列表
    }

    private void getList() {
        DialogUtils.loading(this, true);
        listNews.clear();
        HttpRxUtils.getNewsList(
                NewsActivity.this,
                new String[]{"accessToken", "title", "pageIndex", "pageSize"},
                new Object[]{new SharedPHelper(NewsActivity.this).get(Constant.ACCESSTOKEN, ""),
                        "",
                        1,
                        10
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        xRefreshView.stopRefresh();
                        DialogUtils.hidding(NewsActivity.this);
                        public404.setVisibility(View.GONE);
                        listNews.addAll((Collection<? extends HashMap<String, Object>>) list);
                        if (listNews.size() == 0) {
                            // public404.setVisibility(View.VISIBLE);
                        } else {
                            myAdapter = new NewsListAdapter(R.layout.item_newslist, listNews); //设置适配器
                            myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
                            mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
                            initOnclickListener();
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        xRefreshView.stopRefresh();
                        DialogUtils.hidding(NewsActivity.this);
                        //public404.setVisibility(View.VISIBLE);
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
                                //  ActivityUtil.showToast(mContext, str);
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                                break;
                        }
                    }
                });
    }

    @OnClick({R.id.back, R.id.refresh, R.id.new_Search})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.refresh:
                getList();
                break;
            case R.id.new_Search:
                startActivity(new Intent(this, NewsSearchActivity.class));
                break;

        }
    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                //MyToast.showToast(NewsActivity.this, "点击了" + position);
//                Intent i = new Intent(NewsActivity.this, WebActivity.class);
//                try {//服务器获取数据为20.0
//                    i.putExtra("URL", Constant.HTTP.BASE_URL + Cons tant.HTTP.NEWS +
//                            listNews.get(position).get("newsID").toString().substring(0, listNews.get(position).get("newsID").toString().indexOf(".")));
//                } catch (Exception e) {
//                    i.putExtra("URL", Constant.HTTP.BASE_URL + Constant.HTTP.NEWS +
//                            listNews.get(position).get("newsID").toString());
//                }
//                startActivity(i);


                Intent i = new Intent(NewsActivity.this, WebActivity.class);
                try {//服务器获取数据为20.
                    // i.putExtra("URL","http://ispr.frpgz1.idcfengye.com/web/image.html");
                    i.putExtra("URL", Constant.HTTP.BANNERURL + Constant.HTTP.NEWSCENTER + listNews.get(position).get("Content"));
                    MLog.e("新闻进入：" + Constant.HTTP.BANNERURL + Constant.HTTP.NEWSCENTER + listNews.get(position).get("Content"));
                    i.putExtra("TITLE", listNews.get(position).get("title") + "");
                    i.putExtra("TITLES", "1");
                } catch (Exception e) {

                }
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
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        xRefreshView.stopRefresh();
//                    }
//                }, 500);
                getList();
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
}
