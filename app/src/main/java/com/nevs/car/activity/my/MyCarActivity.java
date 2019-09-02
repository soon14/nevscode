package com.nevs.car.activity.my;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nevs.car.R;
import com.nevs.car.activity.BindCarActivity;
import com.nevs.car.activity.MyCarEnterActivity;
import com.nevs.car.activity.SmrzIdActivity;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.MyCarAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelpers;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.FingerprintUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.safecode.KeyBoardDialog;
import com.nevs.car.tools.view.safecode.LoadingDialog;
import com.nevs.car.tools.view.safecode.PayPasswordView;
import com.nevs.car.z_start.LoginActivity;
import com.nevs.car.z_start.LongRunningService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class MyCarActivity extends BaseActivity {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.xrefresh)
    XRefreshView xRefreshView;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private BaseQuickAdapter myAdapter;
    private int mLoadCount = 0;
    @BindView(R.id.public404)
    LinearLayout public404;
    private List<HashMap<String, Object>> list = new ArrayList<>();
    private String total = "";
    private int id = 0;
    private SharedPHelper sharedPHelper;
    private int count = 0;//记录错误输入PIN码的次数，三次输入错误退出
    private int countFinger = 0;//记录错误输入指纹的次数，三次输入错误跳到PIN码
    private boolean isKill = true;//默认失效
    private boolean isKillFinger = true;//默认失效
    private boolean isFinger = false;//默认未开启
    private KeyBoardDialog keyboard;
    protected LoadingDialog loadingDialog;
    private int item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_my_car;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        total = getResources().getString(R.string.pay_title);
        sharedPHelper = new SharedPHelper(mContext);
        initRecyclyView();
       // getTsp6();
    }


    @Override
    protected void onStart() {
        super.onStart();
        MLog.e("onstart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MLog.e("onResume");
        getTsp6();
    }

    @OnClick({R.id.back, R.id.tv_addcar})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_addcar:
                    startActivityForResult(new Intent(MyCarActivity.this, BindCarActivity.class), 8005);
                break;
        }
    }

//    private void getTsp6() {
//        /**
//         * {"resultMessage":null,"resultDescription":null,"items":[{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPSBSIMULATOR002","relationType":"车主"},{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"车主"}]}
//         * 获取车辆列表cccc:{"items":[{"bindingId":"685a0ea8b1274cba8e50cfc7a4099ce1","vin":"LTPSBSIMULATOR001","iccid":"89860317352002707001","msisdn":"17317229263","bleAddress":"ble","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null},{"bindingId":"c084b4962c194f8abade11dce9819b09","vin":"LTPCHINATELE00123","iccid":"89860317352002707004","msisdn":"14918158628","bleAddress":"","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null}],"resultMessage":"","resultDescription":""}
//
//         * */
//        DialogUtils.loading(MyCarActivity.this, true);
//        list.clear();
//        TspRxUtils.getUservehicleList(mContext,
//                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
//                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(MyCarActivity.this).get(Constant.ACCESSTOKENS, "")},
//                new TspRxListener() {
//                    @Override
//                    public void onSucc(Object obj) {
//                        public404.setVisibility(View.GONE);
//                        DialogUtils.hidding(MyCarActivity.this);
//                        xRefreshView.stopRefresh();
//                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);
//                        if (list.size() > 0) {
//                            myAdapter = new MyCarAdapter(R.layout.item_mycar,list); //设置适配器
//                            myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
//                            mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
//                            initOnclickListener();
//                            initOnclickChildListener();
//                        } else {
//
//                        }
//                    }
//
//                    @Override
//                    public void onFial(String str) {
//                        ActivityUtil.showToast(MyCarActivity.this, str);
//                        DialogUtils.hidding(MyCarActivity.this);
//                        public404.setVisibility(View.VISIBLE);
//                        xRefreshView.stopRefresh();
//                    }
//                }
//        );
//
//    }

    private void getTsp6() {
        /**
         * {"resultMessage":null,"resultDescription":null,"items":[{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPSBSIMULATOR002","relationType":"车主"},{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"车主"}]}
         * 获取车辆列表cccc:{"items":[{"bindingId":"685a0ea8b1274cba8e50cfc7a4099ce1","vin":"LTPSBSIMULATOR001","iccid":"89860317352002707001","msisdn":"17317229263","bleAddress":"ble","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null},{"bindingId":"c084b4962c194f8abade11dce9819b09","vin":"LTPCHINATELE00123","iccid":"89860317352002707004","msisdn":"14918158628","bleAddress":"","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null}],"resultMessage":"","resultDescription":""}
         {
         "isSuccess": "Y",
         "reason": "",
         "data": [{
         "bindingId": "5414ae90ca954e63b279c063b29d0e00",
         "vin": "AAAAAAAAAA1234FFF",
         "iccid": "89860318342003202904",
         "msisdn": "14928270052",
         "bleAddress": "01:a5:a5:a5:a5:a5",
         "relationType": "车主",
         "startTime": "1552283547",
         "endTime": "1552283547",
         "permissions": null
         }, {
         "bindingId": "dde09541cd5c43878a7978ef9112e112",
         "vin": "LTPSB1413J1000041",
         "iccid": "89860318342003203118",
         "msisdn": "14928270063",
         "bleAddress": "01:a5:a5:a5:a5:a5",
         "relationType": "授权",
         "startTime": "1551240000",
         "endTime": "1551319200",
         "permissions": ["1", "4", "5", "6"],
         "carType": "Yes",
         "digitalKey": "",
         "color": "F",
         "groupName": "9-3-滴滴订制",
         "groupEnName": "NISSAN",
         "isDefault": "No",
         "groupCode": "X9-3",
         "licDate": "",
         "licTelecontrol": "",
         "licDoorcontrol": "",
         "licSearchcar": "",
         "licAccontrol": "",
         "nickName": "一定",
         "licenseNo": "001",
         "invoiceDate": "2019/3/11 0:00:00",
         "custMobile": "13752315657"
         }]
         }
         * */
        DialogUtils.loading(mContext, true);
        list.clear();
        HttpRxUtils.getCarList(mContext,
                new String[]{"appType", "accessToken", "nevsAccessToken"},
                new Object[]{"Android", new SharedPHelper(mContext).get(Constant.ACCESSTOKEN, "").toString(), new SharedPHelper(mContext).get(Constant.REGISTCENACCESSTOKEN, "").toString()},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        public404.setVisibility(View.GONE);
                        DialogUtils.hidding(MyCarActivity.this);
                        xRefreshView.stopRefresh();
                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (list.size() > 0) {
                            myAdapter = new MyCarAdapter(R.layout.item_mycar, list); //设置适配器
                            myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
                            mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
                            initOnclickListener();
                            initOnclickChildListener();
                        } else {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_un7));
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        xRefreshView.stopRefresh();
                        DialogUtils.hidding((Activity) mContext);
                        if (str.contains("401")) {
                            MyUtils.exitToLongin(mContext);
                        } else {
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
                                    ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                            }
                        }
                    }
                }
        );

    }


    private void initOnclickChildListener() {
        myAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (!ClickUtil.isFastClick()) {
                    return;
                }
                if(list.size()==0){
                    return;
                }
                switch (view.getId()) {
                    case R.id.state:
                        if (list.get(position).get("relationType").equals("车主")) {
                            try {
                                if (list.get(position).get("isAuthenticated").equals("False")) {
                                    startActivity(new Intent(MyCarActivity.this, SmrzIdActivity.class).putExtra("iccId", list.get(position).get("iccid") + ""));
                                } else {//已认证不需要
                                    MLog.e("已认证");
                                }
                            } catch (Exception e) {
                                MLog.e("没有carType字段");
                                startActivity(new Intent(MyCarActivity.this, SmrzIdActivity.class).putExtra("iccId", list.get(position).get("iccid") + ""));
                            }


                        }
                        break;
                    case R.id.rel_type:
                        if (list.get(position).get("relationType").equals("车主")) {
                            long lastTime = (long) sharedPHelper.get(Constant.SAFETY,0L);
                            long currentTime=System.currentTimeMillis();
                            if(currentTime-lastTime>Constant.SAFETYTIME){
                                sharedPHelper.put(Constant.PINISKILL, true);
                            }
                            item = position;
                            isKill = (boolean) sharedPHelper.get(Constant.PINISKILL, true);
                            isFinger = (boolean) sharedPHelper.get(Constant.ISFINGER, false);
                            isKillFinger = (boolean) sharedPHelper.get(Constant.PINISKILLFINGER, true);

                            //3
                          if (isKillFinger) {
                                //2
                                if (isFinger) {
                                    showFinger();
                                } else {
                                    //1
                                    if (isKill) {
                                        showDialogPin();
                                    } else {
                                        startActivity(new Intent(MyCarActivity.this, CarAuthorizationActivity.class)
                                                .putExtra("AUVIN", list.get(position).get("vin").toString()));
                                    }
                                    //1
                                }
                                //2
                            } else {
                                startActivity(new Intent(MyCarActivity.this, CarAuthorizationActivity.class)
                                        .putExtra("AUVIN", list.get(position).get("vin").toString()));
                            }


                        } else {
                            MLog.e("不是车主不能授权");
                        }


                        break;
                }
            }
        });
    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!ClickUtil.isFastClick()) {
                    return;
                }
                if(list.size()==0){
                    return;
                }
                //   MyToast.showToast(MyCarActivity.this, "点击了" + position);
                if (list.get(position).get("relationType").equals("车主")) {
                    if (position == 0) {
                        Intent intent = new Intent(MyCarActivity.this, MyCarEnterActivity.class);
                        Bundle bundle = new Bundle();
                        ArrayList bundlelist = new ArrayList();
                        bundlelist.add(list);
                        bundle.putParcelableArrayList("list", bundlelist);
                        intent.putExtras(bundle);
                        intent.putExtra("vin", list.get(position).get("vin") + "");
                        intent.putExtra("isdefault", "1");
                        //  startActivityForResult(intent,8003);
                        startActivity(intent);

                    } else {
                        //  startActivityForResult(new Intent(MyCarActivity.this, MyCarEnterActivity.class).putExtra("vin", list.get(position).get("vin") + "").putExtra("isdefault", "0"), 8003);
                        Intent intent = new Intent(MyCarActivity.this, MyCarEnterActivity.class);
                        Bundle bundle = new Bundle();
                        ArrayList bundlelist = new ArrayList();
                        bundlelist.add(list);
                        bundle.putParcelableArrayList("list", bundlelist);
                        intent.putExtras(bundle);
                        intent.putExtra("vin", list.get(position).get("vin") + "");
                        intent.putExtra("isdefault", "0");
                        //startActivityForResult(intent,8003);
                        startActivity(intent);
                    }

                } else {//授权
                    if (position == 0) {
                        Intent intent = new Intent(MyCarActivity.this, MyCarEnterActivity.class);
                        Bundle bundle = new Bundle();
                        ArrayList bundlelist = new ArrayList();
                        bundlelist.add(list);
                        bundle.putParcelableArrayList("list", bundlelist);
                        intent.putExtras(bundle);
                        intent.putExtra("vin", list.get(position).get("vin") + "");
                        intent.putExtra("isdefault", "1");
                        //  startActivityForResult(intent,8003);
                        startActivity(intent);

                    } else {
                        //  startActivityForResult(new Intent(MyCarActivity.this, MyCarEnterActivity.class).putExtra("vin", list.get(position).get("vin") + "").putExtra("isdefault", "0"), 8003);
                        Intent intent = new Intent(MyCarActivity.this, MyCarEnterActivity.class);
                        Bundle bundle = new Bundle();
                        ArrayList bundlelist = new ArrayList();
                        bundlelist.add(list);
                        bundle.putParcelableArrayList("list", bundlelist);
                        intent.putExtras(bundle);
                        intent.putExtra("vin", list.get(position).get("vin") + "");
                        intent.putExtra("isdefault", "0");
                        //startActivityForResult(intent,8003);
                        startActivity(intent);
                    }

                }

            }
        });
        //条目长按点击事件
        myAdapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                //   MyToast.showToast(MyCarActivity.this, "长按点击了" + position);
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
                getTsp6();
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


    private void showFinger() {
        FingerprintUtil.callFingerPrint(new FingerprintUtil.OnCallBackListenr() {
            AlertDialog dialog;

            @Override
            public void onSupportFailed() {
                showToast(getResources().getString(R.string.toast_nofinger));
            }

            @Override
            public void onInsecurity() {
                showToast(getResources().getString(R.string.toast_nofingerno));
            }

            @Override
            public void onEnrollFailed() {
                showToast(MyCarActivity.this.getResources().getString(R.string.toast_nofingersetting));
            }

            @Override
            public void onAuthenticationStart() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCarActivity.this);
                View view = LayoutInflater.from(MyCarActivity.this).inflate(R.layout.layout_fingerprint, null);
                initView(view);
                builder.setView(view);
                builder.setCancelable(false);
                builder.setNeutralButton(MyCarActivity.this.getResources().getString(R.string.toast_pin), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showDialogPin();//跳到安全码
                    }
                });
                builder.setNegativeButton(MyCarActivity.this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.removeMessages(0);
                        FingerprintUtil.cancel();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                // showToast(errString.toString());
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    handler.removeMessages(0);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                showToast(MyCarActivity.this.getResources().getString(R.string.toast_unlockfail));
                countFinger++;
                if (countFinger == 3) { //三次错误跳到PIN输入
                    dialog.dismiss();
                    handler.sendEmptyMessage(2);
                }
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                // showToast(helpString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                showToast(MyCarActivity.this.getResources().getString(R.string.toast_unlocksuc));
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    handler.removeMessages(0);
                    handler.sendEmptyMessage(1);
                }

            }
        });
    }

    private void exitToLongin() {
        new SharedPHelpers(mContext, "c" + new SharedPHelper(mContext).get(Constant.LOGINNAME, "")).put("pin", "abcdef");
//        Intent intent = new Intent(MyCarActivity.this, LoginActivity.class);
//        ComponentName cn = intent.getComponent();
//        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);//ComponentInfo{包名+类名}
//        startActivity(mainIntent);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(MyCarActivity.this, LoginActivity.class);
        startActivity(intent);
        ActivityUtil.showToast(MyCarActivity.this, getResources().getString(R.string.toast_threerror));
    }


    //指纹
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int i = postion % 5;
                    if (i == 0) {
                        tv[4].setBackground(null);
                        tv[i].setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        tv[i].setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        tv[i - 1].setBackground(null);
                    }
                    postion++;
                    handler.sendEmptyMessageDelayed(0, 100);
                    break;
                case 1:
                    startAlarmFinger();
                    startActivity(new Intent(MyCarActivity.this, CarAuthorizationActivity.class)
                            .putExtra("AUVIN", list.get(item).get("vin").toString()));
                    break;
                case 2:
                    showDialogPin();//三次指纹录入错误跳到PIN输入
                    break;
            }
        }
    };

    TextView[] tv = new TextView[5];
    private int postion = 0;

    private void initView(View view) {
        postion = 0;
        tv[0] = (TextView) view.findViewById(R.id.tv_1);
        tv[1] = (TextView) view.findViewById(R.id.tv_2);
        tv[2] = (TextView) view.findViewById(R.id.tv_3);
        tv[3] = (TextView) view.findViewById(R.id.tv_4);
        tv[4] = (TextView) view.findViewById(R.id.tv_5);
        handler.sendEmptyMessageDelayed(0, 100);
    }

    public void showToast(String name) {
        Toast.makeText(MyCarActivity.this, name, Toast.LENGTH_SHORT).show();
    }

    public void showDialogPin() {
        keyboard = new KeyBoardDialog((Activity) mContext, getDecorViewDialog());
        // keyboard.setCancelable(true);//按框外面和BACK键都不响应
        //dialog.setCanceledOnTouchOutside(false);//只有BACK键响应
        // keyboard.setCanceledOnTouchOutside(true);
        keyboard.show();
    }

    private void startAlarm() {
        MLog.e("开启计时服务");
        sharedPHelper.put(Constant.PINISKILL, false);
//        Intent i = new Intent(MyCarActivity.this, LongRunningService.class);
//        MyCarActivity.this.startService(i);
        sharedPHelper.put(Constant.SAFETY,System.currentTimeMillis());
    }

    private void startAlarmFinger() {
        MLog.e("开启计时服务");
        sharedPHelper.put(Constant.PINISKILLFINGER, false);
        Intent i = new Intent(MyCarActivity.this, LongRunningService.class);
        MyCarActivity.this.startService(i);
    }

    protected View getDecorViewDialog() {

        //1表示不隐藏取消按钮
        return PayPasswordView.getInstance(1, total, MyCarActivity.this, new PayPasswordView.OnPayListener() {

            @Override
            public void onSurePay(final String password) {// 这里调用验证密码是否正确的请求

                // TODO Auto-generated method stub
                keyboard.dismiss();
                keyboard = null;

                initProgressDialog();
                loadingDialog.setCanceledOnTouchOutside(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        dismissProgressDialog();
                        if (password.equals(String.valueOf(new SharedPHelpers(mContext, "c" + new SharedPHelper(mContext).get(Constant.LOGINNAME, "")).get("pin", "abcdef")))) {
                            startAlarm();//开启后台任务5分钟失效

                            startActivity(new Intent(MyCarActivity.this, CarAuthorizationActivity.class)
                                    .putExtra("AUVIN", list.get(item).get("vin").toString()));
                            //   ToastUtils.showShortToast(getContext(), "交易成功");
                        } else {
                            count++;
                            if (count == 3) {
                                exitToLongin();
                            }
//                            final NotiDialog dialog = new NotiDialog(getContext(), tips);
//                            dialog.show();
//                            dialog.setTitleStr("密码错误");
//                            dialog.setOkButtonText("忘记密码");
//                            dialog.setCancelButtonText("重试");
//                            dialog.setPositiveListener(new View.OnClickListener() {// 忘记密码操作
//                                @Override
//                                public void onClick(View v) {
//
//                                    ToastUtils.showShortToast(getContext(), "再好好想想");
//                                }
//                            }).setNegativeListener(new View.OnClickListener() {// 重试操作
//
//                                @Override
//                                public void onClick(View v) {
//                                    // TODO Auto-generated method stub
//                                    keyboard = new KeyBoardDialog(getActivity(), getDecorViewDialog());
//                                    keyboard.show();
//                                }
//                            });
                            ActivityUtil.showToast(MyCarActivity.this, MyCarActivity.this.getResources().getString(R.string.toast_pinerrorr));
                            keyboard = new KeyBoardDialog(MyCarActivity.this, getDecorViewDialog());
                            keyboard.show();
                        }

                    }
                }, 200);

            }

            @Override
            public void onCancelPay() {
                // TODO Auto-generated method stub
                keyboard.dismiss();
                keyboard = null;
                //ToastUtils.showShortToast(getContext(), "交易已取消");
            }
        }).getView();
    }

    public void initProgressDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(mContext, R.style.loading_dialogone);
            loadingDialog.setText(getResources().getString(R.string.loading));
        }
        if (!MyCarActivity.this.isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog = new LoadingDialog(mContext, R.style.loading_dialogone);
            loadingDialog.setText(mContext.getResources().getString(R.string.loading));
            loadingDialog.show();
        }
        loadingDialog.setCanceledOnTouchOutside(true);
    }

    public void dismissProgressDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==8003&&resultCode==8004){
//            getTsp6();
//        }
//        if(requestCode==8005&&resultCode==8006){
//            getTsp6();
//        }
    }
}
