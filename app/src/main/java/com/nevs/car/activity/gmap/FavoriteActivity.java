package com.nevs.car.activity.gmap;

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
import com.nevs.car.adapter.FavoriteAdapter;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.OnClick;

public class FavoriteActivity extends BaseActivity {
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
    private List<HashMap<String, Object>> listPoi = new ArrayList<>();
    private List<HashMap<String, Object>> listPois = new ArrayList<>();
    private Set<Integer> set = new TreeSet<>();
    private List<Integer> list = new ArrayList<>();
    private Map<Integer, Integer> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_favorite;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initRecyclyView();
        getTsp4();

    }

    @OnClick({R.id.back, R.id.clear})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.clear:
                if (listPoi.size() == 0)
                    return;
                deleteAll();
                break;
        }
    }

    private void deleteAll() {
        DialogUtils.loading(this, true);
        TspRxUtils.getDeletePoi(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(FavoriteActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"items"},
                new Object[]{getArry()},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(FavoriteActivity.this);
                        ActivityUtil.showToast(FavoriteActivity.this, getResources().getString(R.string.toast_clearsuccess));
//                        for (int v : map.values()) {
//                            listPois.remove(v);
//                            map.remove(v);
//                        }
//                        myAdapter.notifyDataSetChanged();
                        getTsp4();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(FavoriteActivity.this);
                        if (str.contains("400") || str.contains("无效的请求")) {
                            //   ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else if (str.contains("500")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                        } else if (str.contains("未授权的请求")) {
                            MyUtils.exitToLongin(mContext);
                        } else if (str.contains("401")) {
                            MyUtils.exitToLongin401(mContext);
                        } else if (str.contains("连接超时")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.timeout));
                        } else {
                            //  ActivityUtil.showToast(mContext, str);
                        }
                        MyUtils.upLogTSO(mContext, "删除POI", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }

    private String[] getArry() {
        //      String items[]=new String[listPoi.size()];
//        for(int i=0;i<listPoi.size();i++){
//            items[i]=listPoi.get(i).get("poiId").toString();
//        }

        List<Integer> list = new ArrayList<>();
        List<String> listString = new ArrayList<>();
        for (int v : map.values()) {
            list.add(v);
        }
        MLog.e("选择的个数" + list.size());
        for (int k = 0; k < list.size(); k++) {
            listString.add(listPoi.get(list.get(k)).get("poiId") + "");
        }
        String items[] = new String[listString.size()];
        for (int i = 0; i < listString.size(); i++) {
            items[i] = listString.get(i).toString();
        }

        return items;
    }

    private void getTsp4() {
        DialogUtils.loading(this, true);
        listPoi.clear();
        listPois.clear();
        set.clear();
        map.clear();
        TspRxUtils.getPoi(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(FavoriteActivity.this).get(Constant.ACCESSTOKENS, "")},
                0, 20,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(FavoriteActivity.this);
                        MyUtils.upLogTSO(mContext, "获取POI", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        public404.setVisibility(View.GONE);
                        xRefreshView.stopRefresh();
                        listPoi.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (listPoi.size() == 0) {
                            // public404.setVisibility(View.VISIBLE);
                            myAdapter.notifyDataSetChanged();
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_null));
                        } else {
                            for (int i = 0; i < listPoi.size(); i++) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("poiId", listPoi.get(i).get("poiId"));
                                map.put("poiName", listPoi.get(i).get("poiName"));
                                map.put("isCheck", "0");
                                listPois.add((HashMap<String, Object>) map);
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(FavoriteActivity.this);
                        xRefreshView.stopRefresh();
                        // public404.setVisibility(View.VISIBLE);
                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else if (str.contains("500")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                        } else if (str.contains("未授权的请求")) {
                            MyUtils.exitToLongin(mContext);
                        } else if (str.contains("401")) {
                            MyUtils.exitToLongin401(mContext);
                        } else if (str.contains("连接超时")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.timeout));
                        } else {
                            //  ActivityUtil.showToast(mContext, str);
                        }
                        MyUtils.upLogTSO(mContext, "获取POI", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                });
    }

    private void getTsp5(final int possion) {
        DialogUtils.loading(this, true);
        TspRxUtils.getDeletePoi(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(FavoriteActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"items"},
                new Object[]{new String[]{listPoi.get(possion).get("poiId").toString()}},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(FavoriteActivity.this);
                        ActivityUtil.showToast(FavoriteActivity.this, getResources().getString(R.string.toast_deletesuccess));
                        listPoi.remove(possion);
                        myAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(FavoriteActivity.this);
                        ActivityUtil.showToast(FavoriteActivity.this, str);
                    }
                }
        );

    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!ClickUtil.isFastClick()) {
                    return;
                }
                startActivity(new Intent(FavoriteActivity.this, SerchGuideActivity.class)
                        .putExtra("address", listPoi.get(position).get("address") + "")
                        .putExtra("poiName", listPoi.get(position).get("poiName") + "")
                        .putExtra("latitude", listPoi.get(position).get("latitude") + "")
                        .putExtra("longitude", listPoi.get(position).get("longitude") + "")
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

    private void initOnItemChildClickListener() {
        myAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                MLog.e("点击了子:" + position);
                //  getTsp5(position);

//                list.clear();
//
//                if(set.size()==0){//第一次点击
//                    listPois.get(position).put("isCheck","1");
//                    set.add(position);
//                    myAdapter.notifyDataSetChanged();
//                }else {//非第一次
//                    Iterator<Integer> iterator=set.iterator();
//                    while(iterator.hasNext()){
//                       // list.add(iterator.next());
//                        int pp=iterator.next();
//                        if(pp==position){
//                            iterator.remove();
//                            listPois.get(position).put("isCheck","0");
//                            myAdapter.notifyDataSetChanged();
//                        }else {
//                            listPois.get(position).put("isCheck","1");
//                            myAdapter.notifyDataSetChanged();
//                            set.add(position);
//                        }
//                    }
//
//                  for(int j=0;j<list.size();j++){
//                      if(list.get(j)==position){
//                          listPois.get(position).put("isCheck","0");
//                          myAdapter.notifyDataSetChanged();
//                          set.remove(j);
//                      }else {
//                          listPois.get(position).put("isCheck","1");
//                          myAdapter.notifyDataSetChanged();
//                          set.add(position);
//                      }
//                  }


                //  }


//                if(map.size()==0){
//                                        listPois.get(position).put("isCheck","1");
//                    map.put(position,position);
//                    myAdapter.notifyDataSetChanged();
//                    MLog.e("1");
//                }else {
//                    MLog.e("2");
//                    for (int v : map.values()) {
//                        MLog.e("3");
//                       if(v==position){//重复点击取消
//                           MLog.e("4");
//                           listPois.get(position).put("isCheck","0");
//                           myAdapter.notifyDataSetChanged();
//                           map.remove(position);
//                           break;
//                       }else {//新加入
//                           MLog.e("5");
//                           listPois.get(position).put("isCheck","1");
//                          myAdapter.notifyDataSetChanged();
//                          map.put(position,position);
//                           break;
//                       }
//                    }
//                }


                if (map.size() == 0) {
                    listPois.get(position).put("isCheck", "1");
                    map.put(position, position);
                    myAdapter.notifyDataSetChanged();
                    MLog.e("1");
                } else {
                    MLog.e("2");

                    Set set = map.entrySet();

                    Iterator iterator = set.iterator();

                    List<Integer> list = new ArrayList<Integer>();
                    list.clear();
                    while (iterator.hasNext()) {
                        Map.Entry mapentry = (Map.Entry) iterator.next();
                        MLog.e("遍历：" + (int) mapentry.getValue());
                        list.add((int) mapentry.getValue());
                    }

                    if (list != null && list.contains(position)) {//重复点击取消
                        MLog.e("isis");
                        MLog.e("4");
                        listPois.get(position).put("isCheck", "0");
                        myAdapter.notifyDataSetChanged();
                        map.remove(position);
                    } else {//新加入
                        MLog.e("5");
                        listPois.get(position).put("isCheck", "1");
                        myAdapter.notifyDataSetChanged();
                        map.put(position, position);

                    }

                }
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
                getTsp4();
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


        myAdapter = new FavoriteAdapter(R.layout.item_favipois, listPois); //设置适配器
        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
        initOnclickListener();
        initOnItemChildClickListener();
    }

}