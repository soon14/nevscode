package com.nevs.car.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.adapter.MyAdapter;
import com.nevs.car.adapter.xrefreshview.utils.Utils;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.interfaces.OnCheckedChangeListener;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.BToast;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.RecycleViewDivider;
import com.nevs.car.tools.view.SwipeLayout;
import com.nevs.car.z_start.WebActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class AirOrderActivity extends BaseActivity {

    @BindView(R.id.recycleview)
    RecyclerView mRecyclerView;
    @BindView(R.id.car_temperature)
    TextView carTemperature;
    @BindView(R.id.car_state)
    TextView carState;
    @BindView(R.id.car_out)
    TextView carOut;
    @BindView(R.id.air_order)
    ImageView airOrder;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.text_un)
    TextView textUn;
    @BindView(R.id.animation_top_left1)
    LinearLayout animationTopLeft1;
    @BindView(R.id.animation_top_right1)
    LinearLayout animationTopRight1;
    @BindView(R.id.animation_top_left2)
    LinearLayout animationTopLeft2;
    @BindView(R.id.animation_top_right2)
    LinearLayout animationTopRight2;
    @BindView(R.id.pro2)
    RelativeLayout pro2;
    @BindView(R.id.pro1)
    RelativeLayout pro1;
    @BindView(R.id.air_onbg)
    LinearLayout airOnbg;
    @BindView(R.id.activity_service_order)
    RelativeLayout activityServiceOrder;
    @BindView(R.id.air_close)
    TextView airClose;
    @BindView(R.id.air_start)
    TextView airStart;
    @BindView(R.id.n_air)
    LinearLayout nAir;
    private LinearLayoutManager manager;
    private Context mContext;
    private MyAdapter mAdapter;
    private int position;
    private int count1 = 0;
    private int count2 = 0;
    private String commandId = null;
    private String commandIdoff = null;
    private List<HashMap<String, Object>> nameList = new ArrayList<>();      //定时列表
    private List<HashMap<String, Object>> nameLists = new ArrayList<>();      //定时列表
    private boolean isOpen = false;
    private boolean isCanClick = true;//开启和关闭按钮是否可以点击
    private List<Object> listState = new ArrayList<>();

    private List<Object> list = new ArrayList<>();       //空调状态
    private OnCheckedChangeListener listener = new OnCheckedChangeListener() {
        @Override
        public void OnCheckedChangeListener(View v, Object o) {
            switch (v.getId()) {
                case R.id.id_switchd:
                    //在这里处理Item的点击事件即可
                    if ((boolean) o) {
                        JPushInterface.resumePush(getApplicationContext());
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.nevs_starting));
                    } else {
                        JPushInterface.stopPush(getApplicationContext());
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_stopped));
                    }
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_service_order;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mContext = this;
        MyUtils.setPadding(activityServiceOrder, mContext);
        new SharedPHelper(mContext).put(Constant.ISCLICKBLE, "1");
        // initData();
        initView();
        initListener();
        initProssBar1();
        initProssBar2();
        getTsp7();
    }

    @OnClick({R.id.back, R.id.air_start, R.id.air_close, R.id.air_order, R.id.statecar})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.statecar:
                Intent i = new Intent(AirOrderActivity.this, WebActivity.class);
                i.putExtra("URL", Constant.HTTP.BANNERURL + "web/appmanual/appmanual_004.jpg");
                i.putExtra("TITLE", getResources().getString(R.string.questions4));
                startActivity(i);
                break;
            case R.id.air_start:
                int viewId= Utils.toInt(view.getTag());
                if(viewId==R.string.nevs_startnow){
                    startAir();
                }else{
                    closeAir();
                }
//                R.string.nevs_startnow
//                if (!isCanClick) {
//                    return;
//                }
//                startAir();
                break;
            case R.id.air_close:
//                if (!isCanClick) {
//                    return;
//                }
//                closeAir();

                break;
            case R.id.air_order:
                if (new SharedPHelper(mContext).get(Constant.TSPISCAROWER, "0").toString().equals("YES")) {
                    startActivityForResult(new Intent(this, AirOrderEnterActivity.class).putExtra("airtype", "1"), 808);
                } else {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.n_airopen));
                    return;
                }
                break;
        }
    }

    private void startAir() {

            /**
             * {"speed":0.0,"remainingBattery":97,"remainingTime":0,"rechargeMileage":0,"leftFrontDoorStatus":false,
             * "rightFrontDoorStatus":false,"leftRearDoorStatus":false,"rightRearDoorStatus":false,"tierPressureStatus":null,
             * "vehiclestatus":"Stopped","updateTime":1526894079,"resultMessage":"","resultDescription":""}
             * */
            listState.clear();
            DialogUtils.loading(mContext, true);
            TspRxUtils.getState(mContext,
                    new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                    new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                    new SharedPHelper(mContext).get("TSPVIN", "0") + "",
                    new TspRxListener() {
                        @Override
                        public void onSucc(Object obj) {
                            DialogUtils.hidding(AirOrderActivity.this);
                            listState.addAll((Collection<?>) obj);
                            if(String.valueOf(listState.get(4)).equals("1")){
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.n_running_air));
                            }else {

                                if (false) {//isOpen
                                    ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_bluec));
                                } else {
                                    if (new SharedPHelper(mContext).get(Constant.ISCLICKBLE, "0").equals("1")) {
                                        getTsp16();
                                    }
                                }
                            }


                        }

                        @Override
                        public void onFial(String str) {
                            DialogUtils.hidding(AirOrderActivity.this);
                            MLog.e("9");
                            if (str.contains("400") || str.contains("无效的请求")) {
                                //  ActivityUtil.showToast(getContext(), getResources().getString(R.string.zundatas));
                            } else if (str.contains("500")) {
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                            } else if (str.contains("未授权的请求")) {
                                MyUtils.exitToLongin(mContext);
                            } else if (str.contains("401")) {
                                MyUtils.exitToLongin401(mContext);
                            } else if (str.contains("连接超时")) {
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.timeout));
                            } else {
                                //  ActivityUtil.showToast(getContext(), str);
                            }
                        }
                    }
            );
        }


    private void closeAir() {
            /**
             * {"speed":0.0,"remainingBattery":97,"remainingTime":0,"rechargeMileage":0,"leftFrontDoorStatus":false,
             * "rightFrontDoorStatus":false,"leftRearDoorStatus":false,"rightRearDoorStatus":false,"tierPressureStatus":null,
             * "vehiclestatus":"Stopped","updateTime":1526894079,"resultMessage":"","resultDescription":""}
             * */
            listState.clear();
            DialogUtils.loading(mContext, true);
            TspRxUtils.getState(mContext,
                    new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                    new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                    new SharedPHelper(mContext).get("TSPVIN", "0") + "",
                    new TspRxListener() {
                        @Override
                        public void onSucc(Object obj) {
                            DialogUtils.hidding(AirOrderActivity.this);
                            listState.addAll((Collection<?>) obj);

                            if(String.valueOf(listState.get(4)).equals("1")){
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.n_running_air));
                            }else {

                                if (true) {//isOpen
                                    if (new SharedPHelper(mContext).get(Constant.ISCLICKBLE, "0").equals("1")) {
                                        getTsp17();
                                    }
                                } else {
                                    ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_bluef));
                                }
                            }
                        }

                        @Override
                        public void onFial(String str) {
                            DialogUtils.hidding(AirOrderActivity.this);
                            MLog.e("9");
                            if (str.contains("400") || str.contains("无效的请求")) {
                                //  ActivityUtil.showToast(getContext(), getResources().getString(R.string.zundatas));
                            } else if (str.contains("500")) {
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                            } else if (str.contains("未授权的请求")) {
                                MyUtils.exitToLongin(mContext);
                            } else if (str.contains("401")) {
                                MyUtils.exitToLongin401(mContext);
                            } else if (str.contains("连接超时")) {
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.timeout));
                            } else {
                                //  ActivityUtil.showToast(getContext(), str);
                            }
                        }
                    }
            );
        }


    private void getTsp16() {
        /**
         * cccc:{"commandId":"8b109f86613a48bfb1c8ce8cfff5f50a","commandStatus":"Success","resultMessage":"","resultDescription":""}
         * */
        isCanClick = false;
        DialogUtils.controllHint1(AirOrderActivity.this, true, getResources().getString(R.string.hint_airopen));
        // initProssBar1();
        TspRxUtils.getAircondition(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin"},
                new Object[]{new SharedPHelper(AirOrderActivity.this).get("TSPVIN", "0")},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.controllHint1(AirOrderActivity.this, false, "");
                        commandId = String.valueOf(obj);

                        pro1.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getCommandresult(getResources().getString(R.string.hint_bluecd), commandId, 1);

                            }
                        }, Constant.TIMESLUNXUN);
                    }

                    @Override
                    public void onFial(String str) {
                        ActivityUtil.showToast(AirOrderActivity.this, getResources().getString(R.string.hint_bluefd));
                        DialogUtils.controllHint1(AirOrderActivity.this, false, "");
                    }
                }
        );

    }

    private void getTsp17() {
        /**
         * cccc:{"commandId":"83957ba5081240d1896b2a304443c7cb","commandStatus":"Success","resultMessage":"","resultDescription":""}
         * */
        isCanClick = false;
        DialogUtils.controllHint1(AirOrderActivity.this, true, getResources().getString(R.string.hint_airclosed));
        TspRxUtils.getAirconditionoff(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin"},
                new Object[]{new SharedPHelper(AirOrderActivity.this).get("TSPVIN", "0").toString()},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.controllHint1(AirOrderActivity.this, false, "");
                        commandIdoff = String.valueOf(obj);

                        pro2.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getCommandresult(getResources().getString(R.string.hint_bcd), commandIdoff, 2);

                            }
                        }, Constant.TIMESLUNXUN);

                    }

                    @Override
                    public void onFial(String str) {
                        ActivityUtil.showToast(AirOrderActivity.this, getResources().getString(R.string.hint_bfd));
                        DialogUtils.controllHint1(AirOrderActivity.this, false, "");
                    }
                }
        );

    }


    private void initData() {
//        for (int i = 0; i < 1; i++) {
//            NameList.add(getResources().getString(R.string.nevs_weeks) + i);
//        }
    }


    private void initView() {
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        int mColor = ContextCompat.getColor(mContext, R.color.light_gray);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL, 2, mColor));
        mAdapter = new MyAdapter(mContext, nameList);
        mAdapter.addOnCheckedChangeListener(listener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    SwipeLayout preLayout = mAdapter.getPreLayout();
                    if (preLayout != null) {
                        preLayout.close();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                // ToastUtil.showToast(mContext, "打开");
            }

            @Override
            public void onClose(SwipeLayout layout) {
                //  ToastUtil.showToast(mContext, "关闭");

            }

            @Override
            public void onSwiping(SwipeLayout layout) {
                //ToastUtil.showToast(mContext, "正在移动");

            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                //  ToastUtil.showToast(mContext, "开始打开");

            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                //  ToastUtil.showToast(mContext, "开始关闭");

            }

            @Override
            public void onpLacedTop(int position) {
                //  ToastUtil.showToast(mContext, "置顶"+NameList.get(position));
            }

            @Override
            public void onNoRead(int position) {
                //   ToastUtil.showToast(mContext, "标记未读"+NameList.get(position));
            }

            @Override
            public void onDelete(int position) {
//                ToastUtil.showToast(mContext, getResources().getString(R.string.delete) + NameList.get(position));
//                NameList.remove(position);
//                mAdapter.notifyDataSetChanged();

                getDelete(position);

            }

            @Override
            public void onItemClick(int position) {
                //  ToastUtil.showToast(mContext, NameList.get(position));
                try {
                    startActivityForResult(new Intent(AirOrderActivity.this, AirOrderEnterActivity.class).putExtra("airtype", "2").putExtra("type", nameList.get(position).get("scheduleType") + "").putExtra("text", nameList.get(position).get("scheduleValue") + "").putExtra("size", nameList.get(position).get("runDuration") + "").putExtra("scheduleId", nameList.get(position).get("scheduleId") + ""), 808);
                } catch (Exception e) {
                    MLog.e("条目点击异常");
                }

            }
        });
    }
    private void airOpenClose(boolean isOpen){
        if(isOpen){
            airStart.setBackgroundResource(R.drawable.air_open);
            airStart.setText(R.string.nevs_startnow);
            airStart.setTag(R.string.nevs_startnow);
        }else {
            airStart.setBackgroundResource(R.drawable.air_close);
            airStart.setText(R.string.nevs_closenow);
            airStart.setTag(R.string.nevs_closenow);
        }

    }

    private void getCommandresult(final String str, String com, final int id) {
//        switch (id) {
//            case 1:
//                pro1.setVisibility(View.VISIBLE);
//                break;
//            case 2:
//                pro2.setVisibility(View.VISIBLE);
//                break;
//        }
        TspRxUtils.getCommandresult(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                com,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        isCanClick = true;
                        switch (id) {
                            case 1:
                                MyUtils.upLogTSO(mContext, "开启空调", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                                removeProgress1();
                                //imageView.setBackgroundResource(R.mipmap.kt_lf);
                                airOnbg.setVisibility(View.VISIBLE);

                                airClose.setVisibility(View.VISIBLE);
//                                nAir.setBackgroundResource(R.mipmap.n_air_off);
                                airOpenClose(false);
                               // carState.setText(getResources().getString(R.string.hint_open));
                                carState.setText(getResources().getString(R.string.hint_opens));
                                isOpen = true;

                                // DialogUtils.controllHint2(AirOrderActivity.this,str);
                                EventBus.getDefault().post(str);//发送
                                break;
                            case 2:
                                MyUtils.upLogTSO(mContext, "关闭空调", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                                removeProgress2();

                                // imageView.setBackgroundResource(R.mipmap.kt_wf);
                                airOnbg.setVisibility(View.GONE);
                                airClose.setVisibility(View.GONE);
//                                nAir.setBackgroundResource(R.mipmap.n_air_on);
                                airOpenClose(true);
                                carState.setText(getResources().getString(R.string.hint_closeds));
                                isOpen = false;
                                // DialogUtils.controllHint2(AirOrderActivity.this,str);
                                EventBus.getDefault().post(str);//发送
                                break;
                        }

                    }

                    @Override
                    public void onFial(final String str) {
                        final String status = String.valueOf(str);

                        if(status.equals("Rejected")){
                            BToast.showToast(mContext,getResources().getString(R.string.n_car_ing),true);
                        }
                        if(status.equals("Accepted")){
                            BToast.showToast(mContext,getResources().getString(R.string.n_car_next),true);
                        }
                        if(status.equals("Interrupted")){
                            BToast.showToast(mContext,getResources().getString(R.string.n_car_sheep),true);
                        }

                        switch (id) {
                            case 1:
                                MyUtils.upLogTSO(mContext, "开启空调", String.valueOf(status), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        isCanClick = true;
                                        removeProgress1();
                                        //DialogUtils.controllHint2(AirOrderActivity.this,str);
                                        //526  EventBus.getDefault().post(str);//发送
                                        showAir(status);
                                    }
                                }, Constant.TIMESLUNXUNF);
                                break;
                            case 2:
                                MyUtils.upLogTSO(mContext, "关闭空调", String.valueOf(status), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        isCanClick = true;
                                        removeProgress2();
                                        // DialogUtils.controllHint2(AirOrderActivity.this,str);
                                        //526      EventBus.getDefault().post(str);//发送
                                        showAir(status);
                                    }
                                }, Constant.TIMESLUNXUNF);
                                break;
                        }



//                        if (status.equals("Rejected")) {
//                            switch (id) {
//                                case 1:
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            isCanClick = true;
//                                            removeProgress1();
//                                            //DialogUtils.controllHint2(AirOrderActivity.this,str);
//                                            EventBus.getDefault().post(getResources().getString(R.string.refusecontrol));//发送
//
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                                case 2:
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            isCanClick = true;
//                                            removeProgress2();
//                                            // DialogUtils.controllHint2(AirOrderActivity.this,str);
//                                            EventBus.getDefault().post(getResources().getString(R.string.refusecontrol));//发送
//
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                            }
//
//                        } else if (status.equals("Waiting") || status.equals("Accepted") || status.equals("Send")) {
//                            switch (id) {
//                                case 1:
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            isCanClick = true;
//                                            removeProgress1();
//                                            //DialogUtils.controllHint2(AirOrderActivity.this,str);
//                                            EventBus.getDefault().post(getResources().getString(R.string.carnext));//发送
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                                case 2:
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            isCanClick = true;
//                                            removeProgress2();
//                                            // DialogUtils.controllHint2(AirOrderActivity.this,str);
//                                            EventBus.getDefault().post(getResources().getString(R.string.carnext));//发送
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                            }
//                        } else {
//
//                            switch (id) {
//                                case 1:
//                                    MyUtils.upLogTSO(mContext, "开启空调", String.valueOf(status), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");
//
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            isCanClick = true;
//                                            removeProgress1();
//                                            //DialogUtils.controllHint2(AirOrderActivity.this,str);
//                                            //526  EventBus.getDefault().post(str);//发送
//                                            showAir(status);
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                                case 2:
//                                    MyUtils.upLogTSO(mContext, "关闭空调", String.valueOf(status), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");
//
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            isCanClick = true;
//                                            removeProgress2();
//                                            // DialogUtils.controllHint2(AirOrderActivity.this,str);
//                                            //526      EventBus.getDefault().post(str);//发送
//                                            showAir(status);
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                            }
//                        }

                    }
                }
        );
    }

    private void showAir(String reason) {
        String reason0=reason;
        try {
            reason0=reason.split(",BuildDate")[0];
        }catch (Exception e){

        }
        switch (reason0) {
            case "SendAccToMcufailed":
                EventBus.getDefault().post(getResources().getString(R.string.SendAccToMcufailed));//发送
                break;
            case "totsp:preconditionfailedfortimeout":
                EventBus.getDefault().post(getResources().getString(R.string.totsp_preconditionfailedfortimeout));//发送
                break;
            case "acccontrolfailed":
                EventBus.getDefault().post(getResources().getString(R.string.acccontrolfailed));//发送
                break;
            default:
                EventBus.getDefault().post(getResources().getString(R.string.acccontrolfailed));//发送
                break;
        }
    }

    private void initProssBar1() {
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.animalone);
        animationTopLeft1.startAnimation(anim);
        Animation anim2 = AnimationUtils.loadAnimation(mContext, R.anim.animaltwo);
        animationTopRight1.startAnimation(anim2);
    }

    private void initProssBar2() {
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.animalone);
        animationTopLeft2.startAnimation(anim);
        Animation anim2 = AnimationUtils.loadAnimation(mContext, R.anim.animaltwo);
        animationTopRight2.startAnimation(anim2);
    }

    private void removeProgress1() {
        count1 = 0;
        if (isFinishing())
            return;
        pro1.setVisibility(View.GONE);
        MLog.e("1GONE");
    }

    private void removeProgress2() {
        count2 = 0;
        if (isFinishing())
            return;
        pro2.setVisibility(View.GONE);
        MLog.e("2GONE");
    }

    private void getTsp7() {
        /**
         * {"interiorTemperature":0.0,"exteriorTemperature":0.0,"airconditionStatus":null,"resultMessage":"","resultDescription":""}
         * */
        DialogUtils.loading(mContext, true);
        list.clear();
        TspRxUtils.getAirconditionstatus(this,
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new SharedPHelper(this).get("TSPVIN", "0").toString(),
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "获取空调状态", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        list.addAll((Collection<?>) obj);
                        upCarState();
                        getList();

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        getList();
                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else if (str.contains("500") || str.contains("无效的网址") || str.contains("服务器地址未找到")) {
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
                        MyUtils.upLogTSO(mContext, "获取空调状态", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");


                    }
                }
        );
    }

    private void upCarState() {
//        MLog.e("list.get(2):" + list.get(2));
//        if (list.get(2) == null) {
//            imageView.setBackgroundResource(R.mipmap.kt_wf);
//            carState.setText("off");
//        } else {
//            try {
//                carState.setText(list.get(2).toString());
//                carTemperature.setText(getResources().getString(R.string.intemperature) + (int) Float.parseFloat(list.get(0) + "") + "°C");
//                carOut.setText(getResources().getString(R.string.outtemperature) + (int) Float.parseFloat(list.get(1) + "") + "°C");
//
//            } catch (Exception e) {
//                carState.setText(list.get(2).toString());
//                carTemperature.setText(getResources().getString(R.string.intemperature) + list.get(0) + "°C");
//                carOut.setText(getResources().getString(R.string.outtemperature) + list.get(1) + "°C");
//                MLog.e("获取空调状态温度获取异常");
//            }
//
//        }


        MLog.e("list.get(2):" + list.get(2));

        try {

            if (list.get(2).equals("1") || list.get(2).equals("2")) {//1是开
                //  imageView.setBackgroundResource(R.mipmap.kt_lf);
                airOnbg.setVisibility(View.VISIBLE);
                carState.setText(getResources().getString(R.string.hint_opens));
                airClose.setVisibility(View.VISIBLE);
                airOpenClose(false);
                // carTemperature.setText(getResources().getString(R.string.intemperature) + (int) Float.parseFloat(list.get(0) + "") + "°C");
                carTemperature.setText((int) Float.parseFloat(list.get(0) + "") + "");
                carOut.setText(getResources().getString(R.string.outtemperature) + (int) Float.parseFloat(list.get(1) + "") + "°C");
                isOpen = true;
            } else {
                // imageView.setBackgroundResource(R.mipmap.kt_wf);
                airOnbg.setVisibility(View.GONE);
                carState.setText(getResources().getString(R.string.hint_closeds));
                airClose.setVisibility(View.GONE);
                airOpenClose(true);
                // carTemperature.setText(getResources().getString(R.string.intemperature) + (int) Float.parseFloat(list.get(0) + "") + "°C");
                carTemperature.setText((int) Float.parseFloat(list.get(0) + "") + "");
                carOut.setText(getResources().getString(R.string.outtemperature) + (int) Float.parseFloat(list.get(1) + "") + "°C");
                isOpen = false;
            }


        } catch (Exception e) {
//            if (list.get(2) == null) {
//                imageView.setBackgroundResource(R.mipmap.kt_wf);
//                carState.setText("off");
//            }
//            carTemperature.setText(getResources().getString(R.string.intemperature) + list.get(0) + "°C");
//            carOut.setText(getResources().getString(R.string.outtemperature) + list.get(1) + "°C");
            MLog.e("获取空调状态温度获取异常");
        }


    }


    private void getList() {
        /**
         *{"items":[{"scheduleId":"50","vin":"LTPCHINATELE00123","scheduleType":2,"scheduleValue":"1531677268","runDuration":5,"createTime":1531274492}],"resultMessage":"","resultDescription":""}
         * */
        nameList.clear();
        // nameLists.clear();
        TspRxUtils.getScheduleairconditioners(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new SharedPHelper(AirOrderActivity.this).get("TSPVIN", "0") + "",
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        nameList.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        MyUtils.upLogTSO(mContext, "空调定时列表", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        //{"scheduleId":"63","vin":"LTPSBSIMULATOR001","scheduleType":1,"scheduleValue":"07/30/2018 23:15:00","runDuration":5,"createTime":1531991930},
                        if (nameList.size() > 0) {
//                            Map<String, Object> map = new HashMap<String, Object>();
//                            for (int i = 0; i < nameList.size(); i++) {
//                                if (new SharedPHelper(mContext).get("TSPVIN", "0").toString().equals(nameList.get(i).get("vin"))) {
//                                    map.put("scheduleValue", nameList.get(i).get("scheduleValue"));
//                                    map.put("scheduleType", String.valueOf(nameList.get(i).get("scheduleType")));
//                                    map.put("runDuration", String.valueOf(nameList.get(i).get("runDuration")));
//                                }
//                            }
//                            nameLists.add((HashMap<String, Object>) map);
                            mAdapter.notifyDataSetChanged();

                        }

                        if (nameList.size() > 0) {
                            textUn.setVisibility(View.GONE);
                            if (nameList.size() >= 1) {//3 715
                                airOrder.setVisibility(View.GONE);//tjh
                            } else {
                                airOrder.setVisibility(View.VISIBLE);//tjh
                            }
                        } else {
                            airOrder.setVisibility(View.VISIBLE);//tjh
                            textUn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        MyUtils.upLogTSO(mContext, "空调定时列表", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        airOrder.setVisibility(View.VISIBLE);  //tjh
                        textUn.setVisibility(View.VISIBLE);


                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else if (str.contains("500") || str.contains("无效的网址") || str.contains("服务器地址未找到")) {
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


    private void getDelete(final int position) {
        DialogUtils.loading(mContext, true);
        TspRxUtils.getAirDelete(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"scheduleId"},
                new Object[]{new String[]{nameList.get(position).get("scheduleId") + ""}},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "删除空调定时列表", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        ActivityUtil.showToast(AirOrderActivity.this, getResources().getString(R.string.toast_deletesuccess));
//                        //nameList.remove(position);
//                        nameList.clear();
//                        //nameLists.clear();
//                        mAdapter.notifyDataSetChanged();
//                        airOrder.setVisibility(View.VISIBLE);
//                        textUn.setVisibility(View.VISIBLE);
                        getList();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(AirOrderActivity.this, getResources().getString(R.string.deletefail));
                        MyUtils.upLogTSO(mContext, "删除空调定时列表", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 808 && resultCode == 200) {
            getList();
        } else if (requestCode == 808 && resultCode == 201) {
//            airOrder.setVisibility(View.VISIBLE);
//            textUn.setVisibility(View.VISIBLE);
//            nameList.clear();
//            //nameLists.clear();
//            mAdapter.notifyDataSetChanged();
            getList();
        }
    }
}
