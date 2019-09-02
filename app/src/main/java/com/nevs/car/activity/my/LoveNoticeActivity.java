package com.nevs.car.activity.my;

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
import com.nevs.car.adapter.LoveNoticeAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.ToastUtil;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import org.json.JSONObject;

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

public class LoveNoticeActivity extends BaseActivity {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.xrefresh_view)
    XRefreshView xRefreshView;
    @BindView(R.id.public404)
    LinearLayout public404;
    @BindView(R.id.rel_visi)
    RelativeLayout relVisi;
    @BindView(R.id.tv_do)
    TextView tvDo;
    @BindView(R.id.back)
    RelativeLayout back;
    @BindView(R.id.backq)
    TextView backq;
    @BindView(R.id.textViewKill)
    TextView textViewKill;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private BaseQuickAdapter myAdapter;
    private int mLoadCount = 0;
    private List<HashMap<String, Object>> lisNotice = new ArrayList<>();
    private List<HashMap<String, Object>> lisNotices = new ArrayList<>();
    private boolean isEdit = false;
    private List<String> notificationIds = new ArrayList<>();
    private Map<Integer, Integer> map = new HashMap<>();
    //private boolean ischooseall = false;
    private boolean flagall = true;//全选状态

    @Override
    public int getContentViewResId() {
        return R.layout.activity_love_notice;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initRecyclyView();
        // getList();//通知列表
    }

    @Override
    protected void onStart() {
        super.onStart();
        MLog.e("onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        MLog.e("onResume");
        getList();//通知列表
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MLog.e("onRestart");

    }

    private void getList() {
        DialogUtils.loading(this, true);
        lisNotice.clear();
        lisNotices.clear();
        map.clear();
        notificationIds.clear();
        TspRxUtils.getNohistory(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(LoveNoticeActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"beginTime", "endTime", "pageIndex", "pageSize"},
                new Object[]{HashmapTojson.getTime() - 24 * 3600 * 180, HashmapTojson.getTime(), 0, 20},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        DialogUtils.hidding(LoveNoticeActivity.this);
                        MyUtils.upLogTSO(mContext, "爱车通知列表", String.valueOf(list), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        public404.setVisibility(View.GONE);
                        xRefreshView.stopRefresh();
                        lisNotice.addAll((Collection<? extends HashMap<String, Object>>) list);
                        if (lisNotice.size() == 0) {
                            // public404.setVisibility(View.VISIBLE);
                            ActivityUtil.showToast(LoveNoticeActivity.this, getResources().getString(R.string.hint_un3));
                            textViewKill.setEnabled(false);
                        } else {
                            MLog.e("时间戳0：" + lisNotice.get(0).get("pushTime"));
//                            "items": [{
//                                "notificationId": "68628",
//                                        "category": "Common",
//                                        "description": "{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}",
//                                        "pushTime": 1551155546,
//                                        "isRead": true
//                            }, {
//                                "notificationId": "68627",
//                                        "category": "Common",
//                                        "description": "{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}",
//                                        "pushTime": 1551155474,
//                                        "isRead": true
//                            },
                            for (int i = 0; i < lisNotice.size(); i++) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("description", lisNotice.get(i).get("description") + "");
                                map.put("pushTime", lisNotice.get(i).get("pushTime") + "");
                                map.put("notificationId", lisNotice.get(i).get("notificationId") + "");
                                map.put("isRead", lisNotice.get(i).get("isRead"));
                                map.put("title", lisNotice.get(i).get("category"));
                                if (isEdit) {
                                    map.put("isCheck", "0");
                                } else {
                                    map.put("isCheck", "2");
                                }

                                lisNotices.add((HashMap<String, Object>) map);
                            }
                            myAdapter.notifyDataSetChanged();
                            for (int i = 0; i < lisNotice.size(); i++) {
                                notificationIds.add(lisNotice.get(i).get("notificationId") + "");
                            }
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(LoveNoticeActivity.this);
                        MyUtils.upLogTSO(mContext, "爱车通知列表", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_un3));
                        } else if (str.contains("500") || str.contains("无效的网址")) {
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
                        xRefreshView.stopRefresh();


                    }
                }
        );
    }

    @OnClick({R.id.back, R.id.refresh, R.id.tv_do, R.id.textViewKill, R.id.backq})
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
            case R.id.tv_do://编辑
                if (isEdit) {
                    getEditCancle();
                } else {
                    getEdit();
                }
                break;
            case R.id.textViewKill://全部清理
                if (map.size() == 0) {
                    ToastUtil.showToast(mContext,getResources().getString(R.string.n_chosed));
                    return;
                }
                getDelete();
                break;

        }
    }

    private void chooseAll() {
        if (flagall) {
            MLog.e("取消全选状态");
            for (int i = 0; i < lisNotices.size(); i++) {
                lisNotices.get(i).put("isCheck", "1");
            }
            backq.setText(getResources().getString(R.string.enchooseall));
            myAdapter.notifyDataSetChanged();
            flagall = false;
            for (int i = 0; i < lisNotices.size(); i++) {
                map.put(i, i);
            }
        } else {
            MLog.e("全选状态");
            for (int i = 0; i < lisNotices.size(); i++) {
                lisNotices.get(i).put("isCheck", "0");
            }
            backq.setText(getResources().getString(R.string.chooseall));
            myAdapter.notifyDataSetChanged();
            flagall = true;
            map.clear();
        }

    }

    private String[] getArry() {
//        if (ischooseall) {
//            String items[] = new String[lisNotices.size()];
//            for (int i = 0; i < lisNotices.size(); i++) {
//                items[i] = String.valueOf(lisNotices.get(i).get("notificationId") + "");
//            }
//            return items;
//        } else {
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
            listString.add(lisNotices.get(list.get(k)).get("notificationId") + "");
        }
        String items[] = new String[listString.size()];
        for (int i = 0; i < listString.size(); i++) {
            items[i] = String.valueOf(listString.get(i));
        }
        return items;


    }


    private void getDelete() {
        DialogUtils.loading(this, true);
        TspRxUtils.deleteNotify(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(LoveNoticeActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"notificationIds"},
                new Object[]{getArry()},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        DialogUtils.hidding(LoveNoticeActivity.this);
                        MyUtils.upLogTSO(mContext, "删除爱车通知", String.valueOf(list), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        ActivityUtil.showToast(LoveNoticeActivity.this, getResources().getString(R.string.toast_clearsuccess));
//                        if (ischooseall) {
//                            finish();
//                        } else {
//                            getList();
//                        }
                        getList();

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(LoveNoticeActivity.this);

                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_un3));
                        } else if (str.contains("500") || str.contains("无效的网址")) {
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
                        MyUtils.upLogTSO(mContext, "删除爱车通知", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }

    private void getEditCancle() {
        map.clear();
        tvDo.setText(getResources().getString(R.string.editor));
        backq.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        isEdit = false;
        relVisi.setVisibility(View.GONE);
        for (int i = 0; i < lisNotices.size(); i++) {
            lisNotices.get(i).put("isCheck", "2");
        }
        myAdapter.notifyDataSetChanged();
    }

    private void getEdit() {
        tvDo.setText(getResources().getString(R.string.cancle));
        backq.setVisibility(View.VISIBLE);
        backq.setText(getResources().getString(R.string.chooseall));
        flagall = true;
        //ischooseall=true;
        back.setVisibility(View.GONE);
        isEdit = true;
        relVisi.setVisibility(View.VISIBLE);
        for (int i = 0; i < lisNotices.size(); i++) {
            lisNotices.get(i).put("isCheck", "0");
        }
        myAdapter.notifyDataSetChanged();
    }

    private void buttonall() {//全选状态
        backq.setText(getResources().getString(R.string.chooseall));
        //ischooseall = false;
        flagall = true;
    }

    private void enbuttonall() {//取消全选状态
        backq.setText(getResources().getString(R.string.enchooseall));
        //ischooseall = true;
        flagall = false;
    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isEdit) {//编辑状态
                    if (map.size() == 0) {
                        lisNotices.get(position).put("isCheck", "1");
                        MLog.e("=====================1");
                        map.put(position, position);
                        myAdapter.notifyDataSetChanged();
                        if (lisNotices.size() == 1) {
                            enbuttonall();
                        } else {
                            buttonall();
                        }
                        textViewKill.setEnabled(true);
                    } else {
                        if (map.size() == lisNotices.size()) {
                            buttonall();
                        }

                        Set set = map.entrySet();

                        Iterator iterator = set.iterator();
                        MLog.e("=====================2");
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
                            lisNotices.get(position).put("isCheck", "0");
                            myAdapter.notifyDataSetChanged();
                            map.remove(position);
                            if(list.size()<2)
                                textViewKill.setEnabled(false);
                            else
                                textViewKill.setEnabled(true);
                        } else {//新加入
                            textViewKill.setEnabled(true);
                            MLog.e("5");
                            lisNotices.get(position).put("isCheck", "1");
                            myAdapter.notifyDataSetChanged();
                            map.put(position, position);
                            if (map.size() == lisNotices.size()) {
                                enbuttonall();
                            }
                        }

                    }


                } else {
                    getDetail(lisNotice.get(position).get("notificationId") + "");

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
        textViewKill.setEnabled(false);
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


        myAdapter = new LoveNoticeAdapter(R.layout.item_love, lisNotices); //设置适配器
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

    public void getDetail(String NotificationId) {
        DialogUtils.loading(mContext, false);
        TspRxUtils.getNotificationid(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(LoveNoticeActivity.this).get(Constant.ACCESSTOKENS, "")},
                NotificationId,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object ss) {
                        DialogUtils.hidding(LoveNoticeActivity.this);
                        MyUtils.upLogTSO(mContext, "爱车通知详情", String.valueOf(ss), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

//cccc爱车通知详情:{
//	"category": "Common",
//	"description": "{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}",
//	"pushTime": 1552024277,
//	"resultMessage": 1000,
//	"resultDescription": ""
//}
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(ss));
                            String title = jsonObject.getString("category");
                            String description = jsonObject.getString("description");
                            JSONObject jsonObject1 = new JSONObject(description);
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("aps");
                            String pushTime = jsonObject.getString("pushTime");
                            String alert = jsonObject2.getString("alert");
                            startActivity(new Intent(LoveNoticeActivity.this, PublicNoticeEnterActivity.class).putExtra("title",
                                    title
                            ).putExtra("content", alert).putExtra("time",
                                    //  HashmapTojson.getDateToString(Long.parseLong(lisNotice.get(position).get("pushTime").toString()),"yyyy-MM-dd HH:mm:ss")
                                    pushTime
                            ).putExtra("bigtitle", getResources().getString(R.string.nevs_loveinform)).putExtra("timez", "3"));

                        } catch (Exception e) {
                            MLog.e("yic1:" + e);
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(ss));
                            String category = jsonObject.getString("category");
                            String description = jsonObject.getString("description");
                            String pushTime = jsonObject.getString("pushTime");
                            JSONObject jsonObject1 = new JSONObject(description);
                            // JSONObject jsonObject2 = jsonObject1.getJSONObject("aps");
                            String alert = jsonObject1.getString("description");
                            String title = jsonObject1.getString("title");
                            startActivity(new Intent(LoveNoticeActivity.this, PublicNoticeEnterActivity.class).putExtra("title",
                                    title
                            ).putExtra("content", alert).putExtra("time",
                                    //  HashmapTojson.getDateToString(Long.parseLong(lisNotice.get(position).get("pushTime").toString()),"yyyy-MM-dd HH:mm:ss")
                                    pushTime
                            ).putExtra("bigtitle", getResources().getString(R.string.nevs_loveinform)).putExtra("timez", "3"));

                        } catch (Exception e) {
                            MLog.e("yic2:" + e);
                        }


                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(LoveNoticeActivity.this);
                        MyUtils.upLogTSO(mContext, "爱车通知详情", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_un3));
                        } else if (str.contains("500") || str.contains("无效的网址")) {
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
                    }
                });

    }
}
