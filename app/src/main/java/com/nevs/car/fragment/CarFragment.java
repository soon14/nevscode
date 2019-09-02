package com.nevs.car.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lsxiao.apollo.core.Apollo;
import com.nevs.car.R;
import com.nevs.car.activity.AirOrderActivity;
import com.nevs.car.activity.CarHealthActivity;
import com.nevs.car.activity.ChargeMainActivity;
import com.nevs.car.activity.ChooseCarMainActivity;
import com.nevs.car.activity.LongControlActivity;
import com.nevs.car.activity.MyCarEnter2Activity;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.jnihelp.BlueMessUtil;
import com.nevs.car.jnihelp.DigitalUtils;
import com.nevs.car.jnihelp.JniHelper;
import com.nevs.car.tools.Base.BaseFragment;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelpers;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DeviceUtils;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.FingerprintUtil;
import com.nevs.car.tools.util.HashmapTojson;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/2/28.
 */

public class CarFragment extends BaseFragment implements BlueMessUtil.ReDownZSListener {

    @BindView(R.id.pow_bar)
    ImageView powBar;
    KeyBoardDialog keyboard;
    protected LoadingDialog loadingDialog;
    @BindView(R.id.longcontrol)
    TextView longcontrol;
    @BindView(R.id.airorder)
    TextView airorder;
    @BindView(R.id.chargemanager)
    TextView chargemanager;
    @BindView(R.id.carhealth)
    TextView carhealth;
    @BindView(R.id.timenext)
    TextView timenext;
    @BindView(R.id.battery)
    TextView battery;
    @BindView(R.id.mileage)
    TextView mileage;
    @BindView(R.id.car_type)
    TextView carType;
    @BindView(R.id.image_type)
    ImageView imageType;
    @BindView(R.id.iamge_car)
    ImageView iamgeCar;
    Unbinder unbinder;
    @BindView(R.id.xrefresh_view)
    XRefreshView xRefreshView;
    @BindView(R.id.bar_view)
    LinearLayout barView;
    @BindView(R.id.logBlue)
    TextView logBlue;
    private int mLoadCount = 0;
    private String tips = "请点击忘记密码进行找回或重试";
    private String total = "";
    private List<Object> listState = new ArrayList<>();
    private int id = 0;
    private SharedPHelpers sharedPHelpers;
    private SharedPHelper sharedPHelper;
    private int count = 0;//记录错误输入PIN码的次数，三次输入错误退出
    private int countFinger = 0;//记录错误输入指纹的次数，三次输入错误跳到PIN码
    private boolean isKill = true;//默认失效
    private boolean isKillFinger = true;//默认失效
    private boolean isFinger = false;//默认未开启
    private List<HashMap<String, Object>> list = new ArrayList<>();
    private boolean is = true;
    private List<HashMap<String, Object>> listJson = new ArrayList<>();
    private boolean isCancle = true;
    private boolean isTwo = true;//是否二次设置pin
    private JniHelper jniHelper;
    private String a = null;
    private String b = null;
    private String username = null;
    private String vin = null;
    private String starttime = null;
    private String endtime = null;
    private String pin = null;
    private String mobiledevicepubkey = null;
    private String role = null;
    private String bookingid = null;
    private String userid = null;
    private byte cs[] = null;
    private String csr0 = null;
    private String csr = null;
    private String priKey = null;

    private int countB = 1;
    private boolean isClick=true;

    public static CarFragment newInstance() {
        Bundle args = new Bundle();
        CarFragment fragment = new CarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CloseBleListener reDownZSListener;

    public static void setgotoClose(CloseBleListener reDownZSListeners) {
        reDownZSListener = reDownZSListeners;
    }

    public static void gotoClose(int i) {
        reDownZSListener.closeBle(i);
    }


    public interface CloseBleListener {
        void closeBle(int i);
    }

    private myreceiver recevier;
    private IntentFilter intentFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if(null!=recevier)
         getActivity().unregisterReceiver(recevier);
        if(null!=handler){
            handler.removeMessages(handler.obtainMessage().what);
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }


    public class myreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //在这里写上相关的处理代码，一般来说，不要此添加过多的逻辑或者是进行任何的耗时操作
            //因为广播接收器中是不允许开启多线程的，过久的操作就会出现报错
            //因此广播接收器更多的是car_type扮演一种打开程序其他组件的角色，比如创建一条状态栏通知，或者启动某个服务
            MLog.e("收到广播：" + intent.getStringExtra("intentvin"));
            String intentvin = intent.getStringExtra("intentvin");

            String gro=new SharedPHelper(getContext()).get(Constant.groupCode,"")+"";
            if(gro.equals("")||gro.equals("null")){
                logBlue.setText("NEVS");
            }else {
                logBlue.setText(gro);
            }

            getTsp(intentvin);
            //判断是否需要生成CSR
            countB = 1;
            isDownZS();

        }
    }

    @Override
    public int getLayoutResId() {
        //绑定布局
        return R.layout.fragment_car;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        try {
            jniHelper = new JniHelper();
        } catch (Exception e) {
            MLog.e("初始化JNIYC");
        }
        MyUtils.setPadding(barView, getContext());
        total = getResources().getString(R.string.pay_title);
        sharedPHelpers = new SharedPHelpers(getContext(), "c" + new SharedPHelper(getContext()).get(Constant.LOGINNAME, ""));
        sharedPHelper = new SharedPHelper(getContext());
        initRecevier();

        //初始化操作
        //cc     getTestToken();


        // getTsp();

        //getTsp6();

        initxRefreshView();


        MLog.e("car init");
//        if(new SharedPHelper(getContext()).get("TSPVIN","0").equals("0")){
//            //getTsp(new SharedPHelper(getContext()).get("TSPVIN","0").toString());
//            getTsp6();
//            MLog.e("111");
//        }else {
//           getTsp(new SharedPHelper(getContext()).get("TSPVIN","0")+"");
//            MLog.e("222");
//        }

        new SharedPHelper(getContext()).put(Constant.ISCONFORM, "1");

        BlueMessUtil.setgotoDown(this);

    }

    private void initRecevier() {
        recevier = new myreceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("MAINACTIVITY.INITENT.VIN");
        //当网络发生变化的时候，系统广播会发出值为android.net.conn.CONNECTIVITY_CHANGE这样的一条广播
        getActivity().registerReceiver(recevier, intentFilter);
    }


    @Override
    public void onStart() {
        super.onStart();
        MLog.e("onStartcar");


        if (new SharedPHelper(getContext()).get(Constant.ISCONFORM, "0").equals("1")) {


            if (new SharedPHelper(getContext()).get("TSPVIN", "0").equals("0")) {
                //getTsp(new SharedPHelper(getContext()).get("TSPVIN","0").toString());
                getTsp6();
                MLog.e("33");
            } else {

                getTsp(new SharedPHelper(getContext()).get("TSPVIN", "0") + "");//车辆状态
                MLog.e("44");

            }


        }


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        MLog.e("CarFragmentonHiddenChangedcar1");
        if (hidden) {
            MLog.e("CarFragmentonHiddenChangedcarture");
        } else {
            MLog.e("CarFragmentonHiddenChangedcar2");
            if (new SharedPHelper(getContext()).get("TSPVIN", "0").equals("0")) {
                //getTsp(new SharedPHelper(getContext()).get("TSPVIN","0").toString());
                getTsp6();


            } else {
                getTsp(new SharedPHelper(getContext()).get("TSPVIN", "0") + "");//车辆状态
            }

            String gro=new SharedPHelper(getContext()).get(Constant.groupCode,"")+"";
            if(gro.equals("")||gro.equals("null")){
                logBlue.setText("NEVS");
            }else {
                logBlue.setText(gro);
            }
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        MLog.e("CarFragmentonResume");
      //  Apollo.emit("isbinddialog", "1");
        isClick=true;
    }

    private void getTestToken() {
        TspRxUtils.getTestToken(getContext(),
                new String[]{"userAccount", "password"},
                new Object[]{"testopenapi", "nevs@dev123"},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        getTsp6();
                    }

                    @Override
                    public void onFial(String str) {

                        ActivityUtil.showToast(getContext(), str);
                    }
                }
        );
    }

    private void getTestToken0() {
        TspRxUtils.getTestToken0(getContext(),
                new String[]{"Content-Type"},
                new Object[]{"application/x-www-form-urlencoded"},
                new String[]{"resource", "client_id", "grant_type", "username", "password", "scope", "client_secret"},
                new Object[]{"https://nevstelematics.partner.onmschina.cn/nevs-cvp-dev-openapi",
                        "9bf84837-a2c1-4d36-b2be-9a37f746c3c5",
                        "password",
                        "testopenapi@nevstelematics.partner.onmschina.cn",
                        "nevs@dev123",
                        "openid",
                        "vcDvOo7RKh/wHf9yOKVmB20N2GRCt2FW9dhvZLE0tw0="},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        getTsp6();
                    }

                    @Override
                    public void onFial(String str) {

                        ActivityUtil.showToast(getContext(), str);
                    }
                }
        );
    }

    private void callSafe(String ss) {
        DialogUtils.call(getContext(), false, ss);
    }

    @OnClick({R.id.longcontrol, R.id.airorder, R.id.chargemanager, R.id.logBlue,
            R.id.carhealth, R.id.pow_bar, R.id.right_image, R.id.iamge_car, R.id.phone12x, R.id.phone22x,
            R.id.n_one, R.id.n_two, R.id.n_three, R.id.n_four
    })
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            MLog.e("ClickUtil.isFastClick");
            return;
        }
//        if (AntiShake.check(view.getId())) {    //判断是否多次点击
//            MLog.e("ddddddd222");
//            return;
//        }
        long lastTime = (long) new SharedPHelper(getContext()).get(Constant.SAFETY,0L);
        long currentTime=System.currentTimeMillis();

        MLog.e("current=="+currentTime+"----lastTime=="+lastTime);
        if(currentTime-lastTime>Constant.SAFETYTIME){
            sharedPHelper.put(Constant.PINISKILL, true);
        }
        switch (view.getId()) {
            case R.id.logBlue:
                // startActivity(new Intent(getContext(),LogBlueActivity.class));
                break;
            case R.id.phone12x: //客服
                callSafe(sharedPHelper.get(Constant.LOGINHOTLINE, "") + "");
                break;
            case R.id.phone22x://救援
                callSafe(sharedPHelper.get(Constant.LOGINRESCUE, "") + "");
                break;
            case R.id.n_one://longcontrol
                id = 1;
                isKill = (boolean) sharedPHelper.get(Constant.PINISKILL, true);
                isFinger = (boolean) sharedPHelper.get(Constant.ISFINGER, false);
                isKillFinger = (boolean) sharedPHelper.get(Constant.PINISKILLFINGER, true);
//                //3
//                if (isKillFinger) {
//                    MLog.e("f1");
//                    //2
//                    if (isFinger) {
//                        MLog.e("f2");
//                        showFinger();
//                    } else {
//                        //1
//                        if (isKill) {
//                            MLog.e("f3");
//                            showDialogPin();
//                        } else {
//                            MLog.e("f4");
//                            startActivity(new Intent(getContext(), LongControlActivity.class));
//                        }
//                        //1
//                    }
//                    //2
//                } else {
//                    MLog.e("f5");
//                    startActivity(new Intent(getContext(), LongControlActivity.class));
//                }


                //2
                if (isFinger) {
                    if (isKillFinger) {
                        MLog.e("f1");
                        showFinger();
                    } else {
                        MLog.e("f5");
                            startActivityForResult(new Intent(getContext(), LongControlActivity.class), 100);
                    }
                } else {
                   if (isKill) {
                        MLog.e("f3");
                        showDialogPin();
                    } else {
                           MLog.e("f4");
                           startActivityForResult(new Intent(getContext(), LongControlActivity.class), 100);

                    }
                    //1
                }
                //2


                break;
            case R.id.n_two://airorder
                if (new SharedPHelper(getContext()).get(Constant.TSPISCAROWER, "0").toString().equals("YES")) {
                } else {
                    if (MyUtils.getPermissions("4", getContext())) {

                    } else {
                        ActivityUtil.showToast(getContext(), getResources().getString(R.string.hint_author_not));
                        return;
                    }
                }

                id = 2;
                isKill = (boolean) sharedPHelper.get(Constant.PINISKILL, true);
                isFinger = (boolean) sharedPHelper.get(Constant.ISFINGER, false);
                isKillFinger = (boolean) sharedPHelper.get(Constant.PINISKILLFINGER, true);
                //2

             if (isFinger) {
                    if (isKillFinger) {
                        MLog.e("f1");
                        showFinger();
                    } else {

                            MLog.e("f5");
                            startActivity(new Intent(getContext(), AirOrderActivity.class));
                    }
                } else {
                    //1
                    if (isKill) {
                        MLog.e("f3");
                        showDialogPin();
                    } else {

                            MLog.e("f4");
                            startActivity(new Intent(getContext(), AirOrderActivity.class));

                    }
                    //1
                }
                //2

                break;
            case R.id.n_three://chargemanager
                //ActivityUtil.showToast(getContext(), getResources().getString(R.string.empty));
                startActivity(new Intent(getContext(), ChargeMainActivity.class));
                break;
            case R.id.n_four://carhealth
                startActivity(new Intent(getContext(), CarHealthActivity.class));
                break;
            case R.id.pow_bar:

                break;
            case R.id.right_image:
                // ActivityUtil.showToast(getContext(), getResources().getString(R.string.empty));
                // startActivity(new Intent(getContext(), ChooseCarMainActivity.class));
                getActivity().startActivityForResult(new Intent(getContext(), ChooseCarMainActivity.class), 801);
                break;
            case R.id.iamge_car:
                MLog.e("ttttt");
//                if(isClick) {
//                    startActivity(new Intent(getContext(), MyCarEnter2Activity.class));
//                    isClick=false;
//                }
                startActivity(new Intent(getContext(), MyCarEnter2Activity.class));
                break;
        }
    }

    private void showFinger() {
        FingerprintUtil.callFingerPrint(new FingerprintUtil.OnCallBackListenr() {
            AlertDialog dialog;

            @Override
            public void onSupportFailed() {
                showToast(getContext().getResources().getString(R.string.toast_nofinger));
            }

            @Override
            public void onInsecurity() {
                showToast(getContext().getResources().getString(R.string.toast_nofingerno));
            }

            @Override
            public void onEnrollFailed() {
                showToast(getContext().getResources().getString(R.string.toast_nofingersetting));
            }

            @Override
            public void onAuthenticationStart() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_fingerprint, null);
                initView(view);
                builder.setView(view);
                builder.setCancelable(false);
                builder.setNeutralButton(getContext().getResources().getString(R.string.toast_pin), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dialog.dismiss();
                        handler.removeMessages(0);
                        FingerprintUtil.cancel();
                        showDialogPin();//跳到安全码
                    }
                });
                builder.setNegativeButton(getContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
                    MLog.e("指纹弹框消失");
                    dialog.dismiss();
                    handler.removeMessages(0);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                showToast(getContext().getResources().getString(R.string.toast_unlockfail));
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
                showToast(getContext().getResources().getString(R.string.toast_unlocksuc));
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    handler.removeMessages(0);
                    handler.sendEmptyMessage(1);
                }

            }
        });
    }

//
//    private void getTsp6() {
//        /**
//         * {"resultMessage":null,"resultDescription":null,"items":[{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPSBSIMULATOR002","relationType":"车主"},{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"车主"}]}
//         * */
//        DialogUtils.loading(getContext(), true);
//
//        list.clear();
//        TspRxUtils.getUservehicleList(getContext(),
//                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
//                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + sharedPHelper.get(Constant.ACCESSTOKENS, "")},
//                new TspRxListener() {
//                    @Override
//                    public void onSucc(Object obj) {
//                        MLog.e("3");
//                        DialogUtils.hidding(getActivity());
//                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);
//                        MLog.e(list.size() + "辆车");
//                        if (list.size() > 0) {
//                            // Apollo.emit("isbinddialog","1");//模拟测试绑车提示，正式注释掉
//                            MLog.e("6: " + new SharedPHelper(getContext()).get("TSPVIN", "0"));
//                            if ((new SharedPHelper(getContext()).get("TSPVIN", "0")).equals("0")) {
//                                getTsp(list.get(0).get("vin").toString());
//                                new SharedPHelper(getContext()).put("TSPVIN", list.get(0).get("vin").toString());
//                                MLog.e("4 首次加载" + new SharedPHelper(getContext()).get("TSPVIN", "0"));
//                                initJson(list.get(0).get("vin").toString());
//                            } else {
//                                getTsp(new SharedPHelper(getContext()).get("TSPVIN", "0").toString());
//                                MLog.e("5 缓存加载");
//                            }
//                            Apollo.emit("isbinddialog", "pin");//是否设置PIN
//                        } else {
//                            Apollo.emit("isbinddialog", "0");
//                        }
//                    }
//
//                    @Override
//                    public void onFial(String str) {
//                      //  ActivityUtil.showToast(getContext(), str);
//                        DialogUtils.hidding(getActivity());
//                        if(str.contains("400")) {
//                            new SharedPHelper(getContext()).put("TSPVIN","0");
//                            Apollo.emit("isbinddialog", "1");
//                        }
//                        MLog.e("7");
//                    }
//                }
//        );
//
//    }

    private void getTsp6() {
        /**
         * {"resultMessage":null,"resultDescription":null,"items":[{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPSBSIMULATOR002","relationType":"车主"},{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"车主"}]}
         * 获取车辆列表cccc:{"items":[{"bindingId":"685a0ea8b1274cba8e50cfc7a4099ce1","vin":"LTPSBSIMULATOR001","iccid":"89860317352002707001","msisdn":"17317229263","bleAddress":"ble","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null},{"bindingId":"c084b4962c194f8abade11dce9819b09","vin":"LTPCHINATELE00123","iccid":"89860317352002707004","msisdn":"14918158628","bleAddress":"","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null}],"resultMessage":"","resultDescription":""}
         * */
        DialogUtils.loading(getContext(), false);
        list.clear();
        HttpRxUtils.getCarList(getContext(),
                new String[]{"appType", "accessToken", "nevsAccessToken"},
                new Object[]{"Android", new SharedPHelper(getContext()).get(Constant.ACCESSTOKEN, "").toString(), new SharedPHelper(getContext()).get(Constant.REGISTCENACCESSTOKEN, "").toString()},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        MLog.e("3");
                        DialogUtils.hidding(getActivity());
                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        MLog.e(list.size() + "辆车");
                        if (list.size() > 0) {
                            // Apollo.emit("isbinddialog","1");//模拟测试绑车提示，正式注释掉
                            MLog.e("6: " + new SharedPHelper(getContext()).get("TSPVIN", "0"));
                            if ((new SharedPHelper(getContext()).get("TSPVIN", "0")).equals("0")) {
                                getTsp(list.get(0).get("vin").toString());
                                new SharedPHelper(getContext()).put("TSPVIN", list.get(0).get("vin") + "");
                                new SharedPHelper(getContext()).put(Constant.MISISDN, list.get(0).get("msisdn") + "");
                                new SharedPHelper(getContext()).put(Constant.imsi, list.get(0).get("imsi") + "");
                                new SharedPHelper(getContext()).put(Constant.groupCode, list.get(0).get("groupCode") + "");
                                new SharedPHelper(getContext()).put(Constant.isAuthenticated, list.get(0).get("isAuthenticated") + "");
                                if(list.get(0).get("CARALIAS")==null){
                                    new SharedPHelper(getContext()).put(Constant.CARALIAS,"");
                                }else {
                                    new SharedPHelper(getContext()).put(Constant.CARALIAS, list.get(0).get("CARALIAS") + "");
                                }


                                if(list.get(0).get("groupCode")!=null||!(list.get(0).get("groupCode")+"").equals("null")){
                                    logBlue.setText(list.get(0).get("groupCode") + "");
                                }else {
                                    logBlue.setText("NEVS");
                                }

                                if ((String.valueOf(list.get(0).get("relationType"))).equals("车主")) {
                                    new SharedPHelper(getContext()).put(Constant.TSPISCAROWER, "YES");
                                } else {
                                    new SharedPHelper(getContext()).put(Constant.TSPISCAROWER, "NO");//
                                }
                                MyUtils.savaPermissions((List<String>) list.get(0).get("permissions"), getContext());
                                if (list.get(0).get("nickName") != null) {
                                    new SharedPHelper(getContext()).put(Constant.CARALIAS, String.valueOf(list.get(0).get("nickName")));
                                }
                                MLog.e("4 首次加载" + new SharedPHelper(getContext()).get("TSPVIN", "0"));
                                initJson(list.get(0).get("vin").toString());

                                if (String.valueOf(list.get(0).get("relationType")).equals("车主")) {
                                }
                                //缓存车辆列表对应车辆信息
                                putShareCSRs();
                                // 是否下载证书
                                //  isDownZS();

                            } else {
                                getTsp(new SharedPHelper(getContext()).get("TSPVIN", "0").toString());
                                MLog.e("5 缓存加载");
                            }
//                            Apollo.emit("isbinddialog", "pin");//是否设置PIN
//                            isTwo=false;


                        } else {
                            // Apollo.emit("isbinddialog", "0");
                            new SharedPHelper(getContext()).put("TSPVIN", "0");
                            new SharedPHelper(getContext()).put(Constant.MISISDN, "0");
                            Apollo.emit("isbinddialog", "1");
                            MLog.e("7");
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(getActivity());
                        if (str.contains("未授权的请求")) {
                            MyUtils.exitToLongin(getContext());
                        } else {
                            switch (str) {
                                case Constant.HTTP.HTTPFAIL:
                                    ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_network));
                                    break;
                                case Constant.HTTP.HTTPFAILEXIT:
                                    MyUtils.exitToLongin(getContext());
                                    break;
                                case Constant.HTTP.HTTPFAILEXITS:
                                    MyUtils.exitToLongin(getContext());
                                    break;
                                case "No bound vechile":
                                    new SharedPHelper(getContext()).put("TSPVIN", "0");
                                    new SharedPHelper(getContext()).put(Constant.MISISDN, "0");
                                    Apollo.emit("isbinddialog", "1");
                                    break;
                                default:
                                    //  ActivityUtil.showToast(getContext(), str);
                            }
                        }

                    }
                }
        );

    }


    private void initJson(String vin) {
        MyUtils.xJson(new SharedPHelper(getContext()).get("LOGINJSONSSCAR", "") + "", listJson);
        if (list.size() != 0) {
            MLog.e("长度：" + listJson.size() + ": " + listJson.get(0).get("vin"));
            for (int i = 0; i < listJson.size(); i++) {
                if (vin.equals(listJson.get(i).get("vin"))) {
                    if (listJson.get(i).get("nickName") != null) {
                        new SharedPHelper(getContext()).put(Constant.CARALIAS, String.valueOf(listJson.get(i).get("nickName")));
                    }
                }
            }
        }
    }

    private void getTsp(final String vin) {
        /**
         * {"speed":0.0,"remainingBattery":97,"remainingTime":0,"rechargeMileage":0,"leftFrontDoorStatus":false,
         * "rightFrontDoorStatus":false,"leftRearDoorStatus":false,"rightRearDoorStatus":false,"tierPressureStatus":null,
         * "vehiclestatus":"Stopped","updateTime":1526894079,"resultMessage":"","resultDescription":""}
         * */
        listState.clear();
        //  DialogUtils.loading(getContext(),true);
        TspRxUtils.getState(getContext(),
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + sharedPHelper.get(Constant.ACCESSTOKENS, "")},
                vin,
                new TspRxListener() {
                    @Override
                    public void onSucc(final Object obj) {
                        xRefreshView.stopRefresh();
                        isDownZS();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MyUtils.getTsp6(getContext());

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyUtils.upLogTSO(getContext(), "获取车辆状态", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");
                                    }
                                }, 500);


                            }
                        }, 500);

                        MLog.e("8");
                        //  DialogUtils.hidding(getActivity());
                        listState.addAll((Collection<?>) obj);
                        MLog.e("充电剩余时间：" + listState.get(2));
                        upView();
//                        if(isTwo) {
                        try {
                            Apollo.emit("isbinddialog", "pin");//是否设置PIN
                        } catch (Exception e) {
                            MLog.e("Apolloyic");
                        }


                        //}


                    }

                    @Override
                    public void onFial(String str) {
                        xRefreshView.stopRefresh();
                        MLog.e("9");
                        Apollo.emit("isbinddialog", "pin");//是否设置PIN有车却没有状态的情况
                        //    DialogUtils.hidding(getActivity());
                        if (str.contains("400") || str.contains("无效的请求")) {
                            //  ActivityUtil.showToast(getContext(), getResources().getString(R.string.zundatas));
                        } else if (str.contains("500")) {
                            ActivityUtil.showToast(getContext(), getResources().getString(R.string.neterror));
                        } else if (str.contains("未授权的请求")) {
                            MyUtils.exitToLongin(getContext());
                        } else if (str.contains("401")) {
                            MyUtils.exitToLongin401(getContext());
                        } else if (str.contains("连接超时")) {
                            ActivityUtil.showToast(getContext(), getResources().getString(R.string.timeout));
                        } else {
                            //  ActivityUtil.showToast(getContext(), str);
                        }

                        isDownZS();//77
                        MyUtils.upLogTSO(getContext(), "获取车辆状态", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }


    private void upView() {
        new SharedPHelper(getContext()).put("speed", listState.get(0));
        // battery.setText(String.valueOf(listState.get(1)).split("[.]")[0] + "%");
        battery.setText(MyUtils.getInt(Double.parseDouble(String.valueOf(listState.get(1)))) + "%");
        mileage.setText(listState.get(3) + "km");
        String vehiclestatus = String.valueOf(listState.get(4));
        MLog.e("vehiclestatus" + vehiclestatus);
        //车辆状态返回值0:Stopped 1:Running  2:Charging 3:Unkown
        switch (vehiclestatus) {
            case "1":
                timenext.setVisibility(View.INVISIBLE);
                // carType.setText(getResources().getString(R.string.running));
                carType.setText(getResources().getString(R.string.running));
                imageType.setBackgroundResource(R.mipmap.clzy_type);
                iamgeCar.setBackgroundResource(R.mipmap.n_running);
                new SharedPHelper(getContext()).put("vehiclestatus", "1");
                break;
            case "0":
                timenext.setVisibility(View.INVISIBLE);
                // carType.setText(getResources().getString(R.string.stopping));
                carType.setText(getResources().getString(R.string.stopping));
                imageType.setBackgroundResource(R.mipmap.clzy_type);
                //imageType.setColorFilter(getResources().getColor(R.color.grey));
                iamgeCar.setBackgroundResource(R.mipmap.n_home);
                new SharedPHelper(getContext()).put("vehiclestatus", "0");
                break;
//            case "Start":
//                timenext.setVisibility(View.INVISIBLE);
//                //carType.setText(R.string.starting);
//                carType.setText("Start");
//                imageType.setBackgroundResource(R.mipmap.clzy_type);
//                iamgeCar.setBackgroundResource(R.mipmap.clxz_car);
//                break;
            case "2":
                timenext.setVisibility(View.VISIBLE);
                timenext.setText(getResources().getString(R.string.nevs_chagen) + listState.get(2) + getResources().getString(R.string.nevs_chageminute));
                //  carType.setText(getResources().getString(R.string.nevs_charging));
                carType.setText(getResources().getString(R.string.nevs_charging));
                imageType.setBackgroundResource(R.mipmap.clzy_type);
                iamgeCar.setBackgroundResource(R.mipmap.carcharge);
                new SharedPHelper(getContext()).put("vehiclestatus", "2");
                break;
            case "3":
                //ToastUtil.showToast(getContext(),getResources().getString(R.string.neterrorhint));
                timenext.setVisibility(View.INVISIBLE);
                // carType.setText(getResources().getString(R.string.stopping));
                carType.setText(getResources().getString(R.string.offline));
                imageType.setBackgroundResource(R.mipmap.zc_zht);
                //imageType.setColorFilter(getResources().getColor(R.color.grey));
                iamgeCar.setBackgroundResource(R.mipmap.n_noline);
                new SharedPHelper(getContext()).put("vehiclestatus", "3");

//                battery.setText("--" + "%");
//                mileage.setText("--" + "km");
                break;
        }
    }


    protected View getDecorViewDialog() {
        //1表示不隐藏取消按钮
        return PayPasswordView.getInstance(1, total, getContext(), new PayPasswordView.OnPayListener() {

            @Override
            public void onSurePay(final String password) {// 这里调用验证密码是否正确的请求

                // TODO Auto-generated method stub
                if(keyboard!=null){
                keyboard.dismiss();
                keyboard = null;
                }
                initProgressDialog();
                loadingDialog.setCanceledOnTouchOutside(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        dismissProgressDialog();
                        if (password.equals(new SharedPHelpers(getContext(), "c" + new SharedPHelper(getContext()).get(Constant.LOGINNAME, "")).get("pin", "abcdef") + "")) {
                            startAlarm();//开启后台任务5分钟失效

                            switch (id) {
                                case 1:
                                    startActivity(new Intent(getContext(), LongControlActivity.class));
                                    break;
                                case 2:
                                    startActivity(new Intent(getContext(), AirOrderActivity.class));
                                    break;
                            }
                            //   ToastUtils.showShortToast(getContext(), "交易成功");
                        } else {
                            count++;
                            if (count == 3) {
                                exitToLongin();
                            } else {

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
                                ActivityUtil.showToast(getContext(), getContext().getResources().getString(R.string.toast_pinerrorr));
                                keyboard = new KeyBoardDialog(getActivity(), getDecorViewDialog());
                                keyboard.show();
                            }
                        }

                    }
                }, 500);

            }

            @Override
            public void onCancelPay() {
                // TODO Auto-generated method stub
                try {
                    keyboard.dismiss();
                    keyboard = null;
                    //ToastUtils.showShortToast(getContext(), "交易已取消");
                }catch (Exception e){

                }
            }
        }).getView();
    }

    private void startAlarm() {
        MLog.e("开启计时服务");
        sharedPHelper.put(Constant.PINISKILL, false);
//        Intent i = new Intent(getContext(), LongRunningService.class);
////        getContext().startService(i);
        new SharedPHelper(getContext()).put(Constant.SAFETY,System.currentTimeMillis());
    }

    private void startAlarmFinger() {
        MLog.e("开启计时服务");
        sharedPHelper.put(Constant.PINISKILLFINGER, false);
        Intent i = new Intent(getContext(), LongRunningService.class);
        getContext().startService(i);
    }

    public void initProgressDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getContext(), R.style.loading_dialogone);
            loadingDialog.setText(getContext().getResources().getString(R.string.loading));
        }
        if (!getActivity().isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog = new LoadingDialog(getContext(), R.style.loading_dialogone);
            loadingDialog.setText(getContext().getResources().getString(R.string.loading));
            loadingDialog.show();
        }
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
    }

    public void dismissProgressDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public void showDialogPin() {
        keyboard = new KeyBoardDialog(getActivity(), getDecorViewDialog());
        // keyboard.setCancelable(true);//按框外面和BACK键都不响应
        //dialog.setCanceledOnTouchOutside(false);//只有BACK键响应
        // keyboard.setCanceledOnTouchOutside(true);
        keyboard.show();
    }

    private void exitToLongin() {
        new SharedPHelpers(getContext(), "c" + new SharedPHelper(getContext()).get(Constant.LOGINNAME, "")).put("pin", "abcdef");
//        Intent intent = new Intent(getContext(), LoginActivity.class);
//        ComponentName cn = intent.getComponent();
//        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);//ComponentInfo{包名+类名}
//        startActivity(mainIntent);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(getContext(), LoginActivity.class);
        startActivity(intent);
        ActivityUtil.showToast(getContext(), getContext().getResources().getString(R.string.toast_threerror));
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
                    switch (id) {
                        case 1:
                            startActivity(new Intent(getContext(), LongControlActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(getContext(), AirOrderActivity.class));
                            break;
                    }
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
        Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
    }


    private void initxRefreshView() {

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
                getTsp(new SharedPHelper(getContext()).get("TSPVIN", "0") + "");//车辆状态
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                xRefreshView.stopLoadMore();
            }
        });
    }

    //缓存CSR下载所需参数
    private void putShareCSRs() {
        new SharedPHelpers(getContext(), Constant.CSRHEAD + list.get(0).get("vin")).put("bindingId", list.get(0).get("bindingId"));
        new SharedPHelpers(getContext(), Constant.CSRHEAD + list.get(0).get("vin")).put("bleAddress", list.get(0).get("bleAddress"));
        new SharedPHelpers(getContext(), Constant.CSRHEAD + list.get(0).get("vin")).put("startTime", HashmapTojson.getCSRTime(String.valueOf(list.get(0).get("startTime"))));
        new SharedPHelpers(getContext(), Constant.CSRHEAD + list.get(0).get("vin")).put("endTime", HashmapTojson.getCSRTime(String.valueOf(list.get(0).get("endTime"))));
        MLog.e("CSR开始时间：" + new SharedPHelpers(getContext(), Constant.CSRHEAD + list.get(0).get("vin")).get("startTime", ""));
        MLog.e("CSR结束时间：" + new SharedPHelpers(getContext(), Constant.CSRHEAD + list.get(0).get("vin")).get("endTime", ""));

    }

    //生成CSR
    private void isDownZS() {
        //蓝牙暂时适配到6.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MLog.e("此手机安卓版本：" + Build.VERSION.SDK_INT);
        } else {
            MLog.e("此手机安卓版本：" + Build.VERSION.SDK_INT);
            // return;
        }

        if (new SharedPHelper(getContext()).get(Constant.TSPISCAROWER, "0").toString().equals("YES")) {
        } else {//车主才能使用蓝牙
            return;
        }
        String renIsSucc = new SharedPHelpers(getContext(), "rz" + new SharedPHelper(getContext()).get("TSPVIN", "0")).get(Constant.ISRENZHENG, "0") + "";//1为认证成功
        if (renIsSucc.equals("1")) {
            MLog.e("此车认证成功不用下载证书认证");
            if (countB == 1) {
                gotoClose(2);
                countB = 2;
            }
            return;
        } else {
            String zsapp0 = new SharedPHelpers(getContext(), Constant.CSRHEAD + new SharedPHelper(getContext()).get("TSPVIN", "0")).get(Constant.TSPRXmobileCertificateContent, "") + "";
            if (!zsapp0.equals("")) {
                MLog.e("证书已经下载无需下载");
                MLog.e("fttttt");
                if (countB == 1) {
                    gotoClose(2);
                    countB = 2;
                }
                MLog.e("fttttt222");
                return;
            }

        }


        //MLog.e("证书是否为空:"+(new SharedPHelpers(getContext(),Constant.CSRHEAD+new SharedPHelper(getContext()).get("TSPVIN","0")).get(Constant.CSRDIGI,"")+"").length());
        // if((new SharedPHelpers(getContext(),Constant.CSRHEAD+new SharedPHelper(getContext()).get("TSPVIN","0")).get(Constant.CSRDIGI,"")+"").equals("")){
        MLog.e("开始生成CSR");
        try {
//            final String  a= "/C=CN/ST=Shanghai/L=Shanghai/O=/OU=/CN=20180829.20180829.LTPCHINATELE00123";
//            final String b = "DNS.1:BindingId=80cb4fe0a2dd486cb0dd0065c1372f27,DNS.2:MobileId=6bf39b29906b75e43ad03232a21ac2d6,DNS.3:UserAccount=bfd75ef4961b49c69fdbe793633e56cb";
            a = "/C=CN/ST=Shanghai/L=Shanghai/O=/OU=/CN=" + new SharedPHelpers(getContext(), Constant.CSRHEAD + new SharedPHelper(getContext()).get("TSPVIN", "0")).get("startTime", "") + "." + new SharedPHelpers(getContext(), Constant.CSRHEAD + new SharedPHelper(getContext()).get("TSPVIN", "0")).get("endTime", "") + "." + new SharedPHelper(getContext()).get("TSPVIN", "0");
            b = "DNS.1:BindingId=" + new SharedPHelpers(getContext(), Constant.CSRHEAD + new SharedPHelper(getContext()).get("TSPVIN", "0")).get("bindingId", "") + ",DNS.2:MobileId=" + DeviceUtils.getUniqueId(getContext()) + ",DNS.3:UserAccount=" + new SharedPHelper(getContext()).get(Constant.REGISTNEVSUSERID, "");

            MLog.e("aaa");
            // byte cs[]=jniHelper.CSRb(a,b);
            username = new SharedPHelper(getContext()).get(Constant.LOGINNAME, "") + "";
            vin = new SharedPHelper(getContext()).get("TSPVIN", "0") + "";
            starttime = new SharedPHelpers(getContext(), Constant.CSRHEAD + vin).get("startTime", "") + "";
            endtime = new SharedPHelpers(getContext(), Constant.CSRHEAD + vin).get("endTime", "") + "";
            pin = "";
            if (vin.length() > 8) {
                pin = vin.substring(vin.length() - 6);
                MLog.e("csrpin:" + pin);
            } else {
                MLog.e("获取VIN码异常");
            }
            mobiledevicepubkey = "";
            role = "owner";
            bookingid = vin + "001";
            userid = new SharedPHelper(getContext()).get(Constant.REGISTNEVSUSERID, "") + "";
            //byte cs[]=jniHelper.CSRg(a,b,a.length(),new CSRBean(vin,username,starttime,endtime,pin,mobiledevicepubkey,role,bookingid,userid));
            cs = null;
            cs = jniHelper.csrGai(a, b, a.length(), vin, username, starttime, endtime, pin, mobiledevicepubkey, role, bookingid, userid);
            MLog.e("bbb" + cs.length);
            MLog.e("cs-->\n" + new String(cs, "UTF-8"));
            MLog.e("cs长度-->" + cs.length);
            csr0 = new String(cs, "UTF-8").split(";")[0];
            csr = "-----" + csr0.substring(csr0.indexOf("BEGIN CERTIFICATE REQUEST-----"));
            priKey = "-----BEGIN RSA PRIVATE KEY-----" + new String(cs, "UTF-8").split(";")[1] + "-----END RSA PRIVATE KEY-----";
            MLog.e("pri:\n" + priKey);
            //new SharedPHelpers(getContext(),Constant.CSRHEAD+new SharedPHelper(getContext()).get("TSPVIN", "0")).put(Constant.TSPPRIKEY,priKey);
            // 存入文件
            String fileName = Constant.CSRHEAD + new SharedPHelper(getContext()).get("TSPVIN", "0") + Constant.FILEPRIKey;
            MyUtils.initData(MyUtils.getStoragePath(getContext(), false) + Constant.MYNEVSCAR, fileName, priKey);
            //Log.e("tag","CSR:\n"+csr);
            MLog.e("CSR长度:" + csr.length());
            MLog.e("PRIVATE长度:" + priKey.length());
            cs = null;
            csr0 = null;
            priKey = null;
            //下载digirkey
            downLoadDigirkey(csr);
        } catch (Exception e) {
            e.printStackTrace();
            MLog.e("CSRYIC:" + e);
        }


//        }else {
//            MLog.e("此车有证书无需下载");
//            setCsrListener(csrListener1);
//        }

    }

    //下载证书
    private void downLoadDigirkey(String csr) {
        DigitalUtils.getApply(getActivity(), csr, new SharedPHelpers(getContext(), Constant.CSRHEAD + new SharedPHelper(getContext()).get("TSPVIN", "0")).get("bindingId", "") + "");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 101) {
            //重连失败重新认证
            MLog.e("重连失败重新认证carfragment");
            new SharedPHelpers(getContext(), "rz" + new SharedPHelper(getContext()).get("TSPVIN", "0")).put(Constant.ISRENZHENG, "0");//1为认证成功
            gotoClose(1);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isDownZS();
                }
            }, 500);

        }
    }

    @Override
    public void reDownZS() {
        isDownZS();
    }

}
