package com.nevs.car.activity.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.activity.LogSettingActivity;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.CarCopyAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CarCopyActivity extends BaseActivity {
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.xrefresh)
    XRefreshView xRefreshView;
    @BindView(R.id.rel_top)
    RelativeLayout relTop;
    @BindView(R.id.person_land)
    TextView personLand;
    @BindView(R.id.public_land)
    TextView publicLand;
    @BindView(R.id.all_land)
    TextView allLand;
    @BindView(R.id.rel_bottom)
    RelativeLayout relBottom;
    @BindView(R.id.time_to)
    TextView timeTo;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private BaseQuickAdapter myAdapter;
    private int mLoadCount = 0;
    private List<HashMap<String, Object>> list = new ArrayList<>();
    private String BeginTime = "";
    private String EndTime = "";
    private String Category = "";
    private boolean is = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_car_copy;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initView();
        initRecyclyView();
        initOnclickListener();
        getTsp24();
    }

    private void initView() {
        BeginTime = "2018-01-01";
        EndTime =  HashmapTojson.getTime1("yyyy-MM-dd");
        timeTo.setText(BeginTime + "/" + EndTime);
    }

    @OnClick({R.id.back, R.id.setting_log, R.id.choose_log_rel})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.setting_log:
                startActivity(new Intent(CarCopyActivity.this, LogSettingActivity.class));
                break;
            case R.id.choose_log_rel://筛选
                Intent intent = new Intent(this, LogChooseActivity.class);
                intent.putExtra("beginTime",BeginTime);
                intent.putExtra("endTime",EndTime);
                startActivityForResult(intent, 901);
                break;
        }
    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //  MyToast.showToast(CarCopyActivity.this, "点击了" + position);
                startActivity(new Intent(CarCopyActivity.this, CarCopyEnterActivity.class)
                        .putExtra("tripid", list.get(position).get("tripId").toString())
                        .putExtra("totalMileage", MyUtils.getZ(list.get(position).get("totalMileage") + ""))
                        .putExtra("totalDuration", MyUtils.getZ(list.get(position).get("totalDuration") + ""))
                );
                //finish();

            }
        });
        //条目长按点击事件
        myAdapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                //   MyToast.showToast(CarCopyActivity.this, "长按点击了" + position);
                return true;
            }
        });
    }

    private void initRecyclyView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));  //设置RecyclerView的显示模式
        myAdapter = new CarCopyAdapter(R.layout.item_carcopy, list); //设置适配器
        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
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

    private void getTsp24() {
        /**
         *{
         "items": [
         {
         "tripId": "fbdb798c-a30a-42d4-acde-05d7a0d248d9",
         "title": "德语",
         "category": "公务",
         "remark": "不",
         "beginTime": 1524045809,
         "endTime": 1524045870,
         "beginLongitude": 116.309931,
         "beginLatitude": 39.984586,
         "endLongitude": 116.310764,
         "endLatitude": 39.985175,
         "totalDuration": 1,
         "totalMileage": 2,
         "status": 2
         }
         ],
         "resultMessage": "",
         "resultDescription": ""
         }  new Object[]{new SharedPHelper(CarCopyActivity.this).get("TSPVIN", "0"), 1l, 2l, 0, 20},
         * */
        list.clear();
        DialogUtils.loading(mContext, false);
        TspRxUtils.getHistory(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(CarCopyActivity.this).get(Constant.ACCESSTOKENS, "")},
                new Object[]{new SharedPHelper(CarCopyActivity.this).get("TSPVIN", "0")},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        MyUtils.upLogTSO(mContext, "获取行程记录列表", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");
                        MLog.e("数据长度====》"+((Collection<? extends HashMap<String, Object>>) obj).size());
                        DialogUtils.hidding((Activity) mContext);
                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (list.size() == 0) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_null));
                        } else {
                            initBottom();
                        }
                        myAdapter.notifyData(list);
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "获取行程记录列表", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");
                        myAdapter.notifyData(list);
                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_un6));
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
                }
        );

    }


    private void initBottom() {

        long person = 0;
        long publics = 0;
        long alls = 0;
        long allss = 0;
        MLog.e("listchangdu:" + list.size());
        for (int i = 0; i < list.size(); i++) {

            allss += Long.parseLong(MyUtils.getZ(list.get(i).get("totalMileage") + ""));
            MLog.e("allss" + allss + "  " + i);


            if (list.get(i).get("category") != null) {
                if (list.get(i).get("category").equals("私人")) {
                    MLog.e("person0" + list.get(i).get("totalMileage"));
                    person += Long.parseLong(MyUtils.getZ(list.get(i).get("totalMileage") + ""));
                } else if (list.get(i).get("category").equals("公务")) {
                    publics += Long.parseLong(MyUtils.getZ(list.get(i).get("totalMileage") + ""));
                } else if (list.get(i).get("category").equals("自定义")) {
                    alls += Long.parseLong(MyUtils.getZ(list.get(i).get("totalMileage") + ""));
                }

            }


        }
        MLog.e("person" + person);
        personLand.setText(person + "km");
        publicLand.setText(publics + "km");
        // allss = person + publics + alls;
        allLand.setText(allss + "km");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 901 && resultCode == 902) {
            MLog.e("onActivityResult");
            is = false;
            BeginTime = data.getStringExtra("BeginTime");
            EndTime = data.getStringExtra("EndTime");

            timeTo.setText(BeginTime + "/" + EndTime);

            try {
                ArrayList<String> infoList = new ArrayList<String>();
                if (getIntent().getStringArrayListExtra("Category") != null) {
                    infoList = getIntent().getStringArrayListExtra("Category");
                    if (infoList.size() == 1) {
                        Category = infoList.get(0);
                    } else if (infoList.size() == 2) {
                        Category = infoList.get(0) + "," + infoList.get(1);
                    } else if (infoList.size() == 3) {
                        Category = infoList.get(0) + "," + infoList.get(1) + "," + infoList.get(2);
                    } else if (infoList.size() == 0) {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            getTsp();
        }
    }

    private void getTsp() {
        list.clear();
        DialogUtils.loading(mContext, false);
        TspRxUtils.getHistorys(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(CarCopyActivity.this).get(Constant.ACCESSTOKENS, "")},
                new SharedPHelper(CarCopyActivity.this).get("TSPVIN", "0").toString(),
                HashmapTojson.getStringToDates(BeginTime, "yyyy-MM-dd") + "",
                HashmapTojson.getStringToDates(EndTime, "yyyy-MM-dd") + "",
                Category,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "行程记录", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (list.size() == 0) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_null));
                        } else {
                            initBottom();
                            timeTo.setText(BeginTime + "/" + EndTime);
                        }
                        myAdapter.notifyData(list);
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "行程记录", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");
                        myAdapter.notifyData(list);
                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else if (str.contains("500") || str.contains("无效的网址")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                        } else if (str.contains("401")) {
                            MyUtils.exitToLongin(mContext);
                        } else if (str.contains("连接超时")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.timeout));
                        } else {
                            ActivityUtil.showToast(mContext, str);
                        }

                    }
                }
        );
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        MLog.e("onRestart");
        if (is) {
            //626  getTsp24();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        is = true;
    }
}
