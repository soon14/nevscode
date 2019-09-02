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
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.PublicNoticeAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.ListUnrBean;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicNoticeActivity extends BaseActivity {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.xrefresh_view)
    XRefreshView xRefreshView;
    @BindView(R.id.public404)
    LinearLayout public404;
    @BindView(R.id.tv_do)
    TextView tvDo;
    @BindView(R.id.back)
    RelativeLayout back;
    @BindView(R.id.backq)
    TextView backq;
    @BindView(R.id.rel_visi)
    RelativeLayout relVisi;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private BaseQuickAdapter myAdapter;
    private int mLoadCount = 0;
    private List<HashMap<String, Object>> lisNotice = new ArrayList<>();
    private List<HashMap<String, Object>> lisNoticeOne = new ArrayList<>();
    private List<HashMap<String, Object>> lisNoticeTwo = new ArrayList<>();
    private List<ListUnrBean> listUn = new ArrayList<>();
    private boolean isEdit = false;
    private Map<Integer, Integer> map = new HashMap<>();
    private boolean flagall = true;//全选状态

    @Override
    public int getContentViewResId() {
        return R.layout.activity_public_notice;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initRecyclyView();
        getList();//获取公告通知列表
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getList();
    }

    private void getArryDelete() {
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
            // listString.add(lisNoticeOne.get(list.get(k)).get("notificationId")+"");
            listUn.add(new ListUnrBean(String.valueOf(lisNoticeOne.get(k).get("un_id")), "YES", "YES", String.valueOf(lisNoticeOne.get(k).get("n_id"))));
        }
    }

    private void getArryAll() {
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
        for (int n = 0; n < list.size(); n++) {
            listUn.add(new ListUnrBean(String.valueOf(lisNoticeOne.get(n).get("un_id")), "YES", "NO", String.valueOf(lisNoticeOne.get(n).get("n_id"))));
        }
    }

    private void getList() {
        DialogUtils.loading(this, true);
        lisNotice.clear();
        lisNoticeOne.clear();
        lisNoticeTwo.clear();
        map.clear();
        listUn.clear();
        HttpRxUtils.getAnnouncement(
                PublicNoticeActivity.this,
                new String[]{"accessToken", "type"},
                new Object[]{new SharedPHelper(PublicNoticeActivity.this).get(Constant.ACCESSTOKEN, ""),
                        "90111003"
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        DialogUtils.hidding(PublicNoticeActivity.this);
                        public404.setVisibility(View.GONE);
                        xRefreshView.stopRefresh();
                        lisNotice.addAll((Collection<? extends HashMap<String, Object>>) list);
                        if (lisNotice.size() == 0) {
                            // public404.setVisibility(View.VISIBLE);
                            ActivityUtil.showToast(PublicNoticeActivity.this, getResources().getString(R.string.hint_un1));
                            myAdapter.notifyDataSetChanged();
                        } else {

                            for (int i = 0; i < lisNotice.size(); i++) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                Map<String, Object> map2 = new HashMap<String, Object>();
                                if (lisNotice.get(i).get("is_read").equals("NO")) {
                                    map.put("title", lisNotice.get(i).get("title"));
                                    map.put("pub_time", lisNotice.get(i).get("pub_time"));
                                    map.put("is_read", lisNotice.get(i).get("is_read"));
                                    map.put("content", lisNotice.get(i).get("content"));
                                    map.put("un_id", lisNotice.get(i).get("un_id"));
                                    map.put("n_id", lisNotice.get(i).get("n_id"));
                                    if (isEdit) {
                                        map.put("isCheck", "0");
                                    } else {
                                        map.put("isCheck", "2");
                                    }
                                    lisNoticeOne.add((HashMap<String, Object>) map);
                                } else {
                                    map2.put("title", lisNotice.get(i).get("title"));
                                    map2.put("pub_time", lisNotice.get(i).get("pub_time"));
                                    map2.put("is_read", lisNotice.get(i).get("is_read"));
                                    map2.put("content", lisNotice.get(i).get("content"));
                                    map2.put("un_id", lisNotice.get(i).get("un_id"));
                                    map2.put("n_id", lisNotice.get(i).get("n_id"));
                                    if (isEdit) {
                                        map2.put("isCheck", "0");
                                    } else {
                                        map2.put("isCheck", "2");
                                    }
                                    lisNoticeTwo.add((HashMap<String, Object>) map2);
                                }
                            }
                            lisNoticeOne.addAll(lisNoticeTwo);

                            myAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(PublicNoticeActivity.this);
                        // public404.setVisibility(View.VISIBLE);
                        xRefreshView.stopRefresh();
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

    @OnClick({R.id.back, R.id.refresh, R.id.tv_do, R.id.text_all, R.id.textViewKill, R.id.backq})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.backq:
                chooseAll();
                break;
            case R.id.refresh:
                getList();
                break;
            case R.id.tv_do:
                if (isEdit) {
                    getEditCancle();
                } else {
                    getEdit();
                }
                break;
            case R.id.text_all:
                if (lisNoticeOne.size() != 0) {
                    getIsAll();
                } else {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_un1));
                }
                break;
            case R.id.textViewKill:
                if (lisNoticeOne.size() != 0) {
                    getKill();
                } else {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_un1));
                }
                break;
        }
    }

    private void getKill() {
        listUn.clear();
//        for (int i = 0; i < lisNoticeOne.size(); i++) {
//            listUn.add(new ListUnrBean(String.valueOf(lisNoticeOne.get(i).get("un_id")), "YES", "YES", String.valueOf(lisNoticeOne.get(i).get("n_id"))));
//        }
        getArryDelete();
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getUserNotifRe(mContext,
                new String[]{"accessToken", "list_unr"},
                new Object[]{new SharedPHelper(mContext).get(Constant.ACCESSTOKEN, ""),
                        listUn
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
//                       lisNoticeOne.clear();
//                        myAdapter.notifyDataSetChanged();
                        getList();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(mContext, str);
                    }
                }
        );
    }

    private void getIsAll() {
        listUn.clear();
//        for (int i = 0; i < lisNoticeOne.size(); i++) {
//            listUn.add(new ListUnrBean(String.valueOf(lisNoticeOne.get(i).get("un_id")), "YES", "NO", String.valueOf(lisNoticeOne.get(i).get("n_id"))));
//        }
        getArryAll();
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getUserNotifRe(mContext,
                new String[]{"accessToken", "list_unr"},
                new Object[]{new SharedPHelper(mContext).get(Constant.ACCESSTOKEN, ""),
                        listUn
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        getList();
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

    private void getEditCancle() {
        tvDo.setText(getResources().getString(R.string.editor));
        backq.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        isEdit = false;
        relVisi.setVisibility(View.GONE);
        for (int i = 0; i < lisNoticeOne.size(); i++) {
            lisNoticeOne.get(i).put("isCheck", "2");
        }
        myAdapter.notifyDataSetChanged();
    }

    private void getEdit() {
        tvDo.setText(getResources().getString(R.string.cancle));
        backq.setVisibility(View.VISIBLE);
        backq.setText(getResources().getString(R.string.chooseall));
        flagall = true;
        back.setVisibility(View.GONE);
        isEdit = true;
        relVisi.setVisibility(View.VISIBLE);
        for (int i = 0; i < lisNoticeOne.size(); i++) {
            lisNoticeOne.get(i).put("isCheck", "0");
        }
        myAdapter.notifyDataSetChanged();
    }

    private void chooseAll() {
        if (flagall) {
            MLog.e("取消全选状态");
            for (int i = 0; i < lisNoticeOne.size(); i++) {
                lisNoticeOne.get(i).put("isCheck", "1");
            }
            enbuttonall();
            myAdapter.notifyDataSetChanged();
            for (int i = 0; i < lisNoticeOne.size(); i++) {
                map.put(i, i);
            }
        } else {
            MLog.e("全选状态");
            for (int i = 0; i < lisNoticeOne.size(); i++) {
                lisNoticeOne.get(i).put("isCheck", "0");
            }
            buttonall();
            myAdapter.notifyDataSetChanged();
            map.clear();
        }

    }

    private void buttonall() {//全选状态
        backq.setText(getResources().getString(R.string.chooseall));
        flagall = true;
    }

    private void enbuttonall() {//取消全选状态
        backq.setText(getResources().getString(R.string.enchooseall));
        flagall = false;
    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isEdit) {//编辑状态
                    if (map.size() == 0) {
                        lisNoticeOne.get(position).put("isCheck", "1");
                        map.put(position, position);
                        myAdapter.notifyDataSetChanged();
                        MLog.e("1");
                        if (lisNoticeOne.size() == 1) {
                            enbuttonall();
                        } else {
                            buttonall();
                        }

                    } else {
                        if (map.size() == lisNoticeOne.size()) {
                            buttonall();
                        }
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
                            lisNoticeOne.get(position).put("isCheck", "0");
                            myAdapter.notifyDataSetChanged();
                            map.remove(position);
                        } else {//新加入
                            MLog.e("5");
                            lisNoticeOne.get(position).put("isCheck", "1");
                            myAdapter.notifyDataSetChanged();
                            map.put(position, position);
                            if (map.size() == lisNoticeOne.size()) {
                                enbuttonall();
                            }
                        }

                    }


                } else {
                    if (lisNoticeOne.get(position).get("is_read").equals("YES")) {
                        startActivity(new Intent(PublicNoticeActivity.this, PublicNoticeEnterActivity.class).putExtra("title",
                                lisNoticeOne.get(position).get("title").toString()
                        ).putExtra("content", lisNoticeOne.get(position).get("content").toString()).putExtra("time",
                                lisNoticeOne.get(position).get("pub_time").toString()
                        ).putExtra("bigtitle", getResources().getString(R.string.nevs_notice)));
                    } else {
                        getIsRead(position);
                    }
                }


            }
        });
        //条目长按点击事件
        myAdapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                // MyToast.showToast(NewsActivity.this, "长按点击了" + position);
                //  getDelete(position);
                return true;
            }
        });
    }

    private void getDelete(final int position) {
        List<ListUnrBean> listUn = new ArrayList<>();
        listUn.clear();
        listUn.add(new ListUnrBean(String.valueOf(lisNoticeOne.get(position).get("un_id")),
                "YES",
                "YES",
                String.valueOf(lisNoticeOne.get(position).get("n_id"))
        ));
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getUserNotifRe(mContext,
                new String[]{"accessToken", "list_unr"},
                new Object[]{new SharedPHelper(PublicNoticeActivity.this).get(Constant.ACCESSTOKEN, ""),
                        listUn
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        lisNoticeOne.remove(position);
                        myAdapter.notifyDataSetChanged();
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

    private void getIsRead(final int position) {
        List<ListUnrBean> listUn = new ArrayList<>();
        listUn.clear();
        listUn.add(new ListUnrBean(String.valueOf(lisNoticeOne.get(position).get("un_id")),
                "YES",
                "NO",
                String.valueOf(lisNoticeOne.get(position).get("n_id"))
        ));
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getUserNotifRe(mContext,
                new String[]{"accessToken", "list_unr"},
                new Object[]{new SharedPHelper(PublicNoticeActivity.this).get(Constant.ACCESSTOKEN, ""),
                        listUn
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        startActivity(new Intent(PublicNoticeActivity.this, PublicNoticeEnterActivity.class).putExtra("title",
                                lisNoticeOne.get(position).get("title").toString()
                        ).putExtra("content", lisNoticeOne.get(position).get("content").toString()).putExtra("time",
                                lisNoticeOne.get(position).get("pub_time").toString()
                        ).putExtra("bigtitle", getResources().getString(R.string.nevs_notice)));
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

        myAdapter = new PublicNoticeAdapter(R.layout.item_publicnotice, lisNoticeOne); //设置适配器
        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
        initOnclickListener();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
