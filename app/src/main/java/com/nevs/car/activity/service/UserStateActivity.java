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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class UserStateActivity extends BaseActivity {
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
        return R.layout.activity_user_state;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initRecyclyView();
        // getList();
        initAdapter();

    }

//    private void initAdapter() {
//        Map<String,Object> map1=new HashMap<>();
//        Map<String,Object> map2=new HashMap<>();
//        Map<String,Object> map3=new HashMap<>();
//        Map<String,Object> map4=new HashMap<>();
//
//        map1.put("question",getResources().getString(R.string.question1));
//        map2.put("question",getResources().getString(R.string.question2));
//        map3.put("question",getResources().getString(R.string.question3));
//        map4.put("question",getResources().getString(R.string.question4));
//        listNews.add((HashMap<String, Object>) map1);
//        listNews.add((HashMap<String, Object>) map2);
//        listNews.add((HashMap<String, Object>) map3);
//        listNews.add((HashMap<String, Object>) map4);
//        myAdapter = new UserStateAdapter(R.layout.item_userstate, listNews); //设置适配器
//        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
//        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
//        initOnclickListener();
//    }

    private void initAdapter() {
        Map<String, Object> map1 = new HashMap<>();
        Map<String, Object> map4 = new HashMap<>();

        map1.put("question", getResources().getString(R.string.question1));
        map4.put("question", getResources().getString(R.string.question4));
        listNews.add((HashMap<String, Object>) map1);
        listNews.add((HashMap<String, Object>) map4);
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
                UserStateActivity.this,
                new String[]{"accessToken"},
                new Object[]{new SharedPHelper(UserStateActivity.this).get(Constant.ACCESSTOKEN, ""),
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        xRefreshView.stopRefresh();
                        DialogUtils.hidding(UserStateActivity.this);
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
                        DialogUtils.hidding(UserStateActivity.this);
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


//                Intent i = new Intent(UserStateActivity.this, WebActivity.class);
//                try {//服务器获取数据为20.   http://220.249.93.210:8602/web/APPVer1.0.html
//                    // i.putExtra("URL","http://ispr.frpgz1.idcfengye.com/web/image.html");
//
//                    if(listNews.get(position).get("answer").toString().contains("http")){
//                        i.putExtra("URL",listNews.get(position).get("answer").toString());
//                        // MLog.e("新闻进入："+Constant.HTTP.BANNERURL+Constant.HTTP.NEWSCENTER+listNews.get(position).get("Content"));
//                        i.putExtra("TITLE",getResources().getString(R.string.servce_state));
//                    }else {
//                        i.putExtra("URL",Constant.HTTP.BANNERURL+Constant.HTTP.NEWSCENTER+listNews.get(position).get("answer").toString());
//                        // MLog.e("新闻进入："+Constant.HTTP.BANNERURL+Constant.HTTP.NEWSCENTER+listNews.get(position).get("Content"));
//                        i.putExtra("TITLE",getResources().getString(R.string.servce_state));
//                    }
//
//
//                } catch (Exception e) {
//
//                }
//                startActivity(i);
                switch (position) {
                    case 0:
                        Intent i = new Intent(UserStateActivity.this, UserStateAppActivity.class);
//                        i.putExtra("URL","http://220.249.93.210:8602/web/APPVer1.0.html");
//                        i.putExtra("TITLE",listNews.get(1).get("question")+"");
                        startActivity(i);
                        break;
                    case 1:
                        gotoCarDescripActivity();//调整整车说明
                        break;
                }

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

    private void gotoCarDescripActivity() {
        //https://docs.google.com/viewer?embedded=true&url=
        String url = "";
        String name = "";
        String urle = "http://219.150.92.5:2021/web/60001666_UM_9-3_CN-EN_MY19_2019-02.pdf";//英文
        String urlc = "http://219.150.92.5:2021/web/60004073_UM_9-3_CN-CN_MY19_2019-02.pdf";
        if (MyUtils.isChina(mContext)) {
            url = urlc;
            name = Constant.MYNEVSCARPDFCH;
        } else {
            url = urle;
            name = Constant.MYNEVSCARPDFEN;
        }

        Intent i = new Intent(UserStateActivity.this, CarDescripActivity.class);
        i.putExtra("url", url);
        i.putExtra("name", name);
        startActivity(i);


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
}
