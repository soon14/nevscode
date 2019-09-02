package com.nevs.car.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nevs.car.R;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.jnihelp.BleService;
import com.nevs.car.jnihelp.BlueBrocastRecever;
import com.nevs.car.jnihelp.BlueListener;
import com.nevs.car.jnihelp.BlueMessUtil;
import com.nevs.car.jnihelp.BlueReListener;
import com.nevs.car.jnihelp.JniHelper;
import com.nevs.car.jnihelp.RzStateListener;
import com.nevs.car.jnihelp.modle.BookingStarted;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelpers;
import com.nevs.car.tools.ToastUtil;
import com.nevs.car.tools.encrypt.AES;
import com.nevs.car.tools.encrypt.Base64;
import com.nevs.car.tools.encrypt.Sha256Util;
import com.nevs.car.tools.interfaces.PermissionInterface;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.BToast;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.PermissionHelper;
import com.nevs.car.tools.view.FinancialIOSTipsDialog;
import com.nevs.car.z_start.MainActivity;
import com.nevs.car.z_start.MyApp;
import com.nevs.car.z_start.WebActivity;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.nevs.car.jnihelp.BlueMessUtil.getUint8;
import static com.nevs.car.tools.util.MyUtils.split_bytes;
import static org.bouncycastle.asn1.gnu.GNUObjectIdentifiers.CRC;

public class LongControlActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionInterface, BlueListener, BlueReListener, RzStateListener {

    @BindView(R.id.batteryy)
    TextView battery;
    @BindView(R.id.mileagee)
    TextView mileage;
    @BindView(R.id.rel_bottom)
    RelativeLayout relBottom;
    @BindView(R.id.iamge_car)
    ImageView iamgeCar;
    @BindView(R.id.lockicon)
    ImageView lockicon;
    @BindView(R.id.animation_top_left1)
    LinearLayout animationTopLeft1;
    @BindView(R.id.animation_top_right1)
    LinearLayout animationTopRight1;
    @BindView(R.id.pro1)
    RelativeLayout pro1;
    @BindView(R.id.animation_top_left2)
    LinearLayout animationTopLeft2;
    @BindView(R.id.animation_top_right2)
    LinearLayout animationTopRight2;
    @BindView(R.id.pro2)
    RelativeLayout pro2;
    @BindView(R.id.animation_top_left3)
    LinearLayout animationTopLeft3;
    @BindView(R.id.animation_top_right3)
    LinearLayout animationTopRight3;
    @BindView(R.id.pro3)
    RelativeLayout pro3;
    @BindView(R.id.xrefresh_view)
    XRefreshView xRefreshView;
    @BindView(R.id.n_view)
    LinearLayout nView;
    @BindView(R.id.img_leftq)
    ImageView imgLeftq;
    @BindView(R.id.img_lefth)
    TextView imgLefth;
    @BindView(R.id.img_rightq)
    TextView imgRightq;
    @BindView(R.id.img_righth)
    TextView imgRighth;
    @BindView(R.id.t_leftq)
    TextView tLeftq;
    @BindView(R.id.t_lefth)
    TextView tLefth;
    @BindView(R.id.t_rightq)
    TextView tRightq;
    @BindView(R.id.t_righth)
    TextView tRighth;
    @BindView(R.id.line_state)
    TextView lineState;
    private String rote = null;
    private boolean endle = false;//是否开启限速
    private List<Object> listRote = new ArrayList<>();
    private int count1 = 0;
    private int count2 = 0;
    private int count3 = 0;
    private String commandId = null;
    private String commandIdfash = null;
    private String commandIdrate = null;
    private AnimationDrawable frameAnim;
    private boolean isLock = false;
    private List<Object> listState = new ArrayList<>();

    private PermissionHelper mPermissionHelper;
    private boolean isRunning=false;


    //蓝牙
    private BleService mBleService;
    private Vibrator vibrator;
    private String mAddress = "";
    private String mName = "";
    private BluetoothAdapter mBluetoothAdapter;
    private JniHelper jniHelper;

    private boolean reState = false;//蓝牙连接状态
    private boolean isLonCon = false;//是否是本界面连接 true为是
    private FinancialIOSTipsDialog tipsDialog;
    private BlueBrocastRecever blueBrocastRecever;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();

    private List<byte[]> listStartbooking = new ArrayList();//startbookingAES分段加密后保存
    private List<byte[]> listByteUnLock = new ArrayList();//listUnLockgAES分段加密后保存
    private List<byte[]> listbookingstarted = new ArrayList();//startbookingAES分段加密后保存
    private List<byte[]> listbookingstartedpin = new ArrayList();//startbookinpinAES分段加密后保存

    private byte[][] bytestartbooking = null;//startbookingAES分段加密合并后再分割
    private byte[][] bytebookingstarted = null;//bookingstartedAES分段加密合并后再分割
    private byte[][] bytebookingstartedpin = null;//bookingstartedpinAES分段加密合并后再分割


    private List<byte[]> liststart = new ArrayList<>();//startbooking接收
    private List<byte[]> liststartok = new ArrayList<>();//输入OK后接收
    private List<byte[]> listPinend = new ArrayList<>();//输入PIN后接收
    private List<byte[]> listPinend2 = new ArrayList<>();//输入PIN后接收
    private List<byte[]> listUNLOCK = new ArrayList<>();//UNLOCK接收
    private List<byte[]> listUNLOCK2 = new ArrayList<>();//UNLOCK接收


    private final ServiceConnection mServiceConnection = new ServiceConnection() {//2

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBleService = ((BleService.LocalBinder) service)
                    .getService();
//            if(!reState){
//                MLog.e("lonA 可以连接");
//                if (!mBleService.initialize()) {//3
//                    Log.e("tag", "Unable to initialize Bluetooth");
//                    finish();
//                } else {
//                    Log.e("tag", "能初始化");
//                }
//
//                // 自动连接to the device upon successful start-up
//                // 初始化.
//               // mBleService.connect(mAddress);//4
            if (mBleService.mConnectionState == 1 || mBleService.mConnectionState == 2) {

            } else {
                //605 initConnect();
            }
            //           }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public int getContentViewResId() {
        return R.layout.activity_long_control;
    }

    // @RequiresApi(api = M)
    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView, mContext);
        new SharedPHelper(mContext).put(Constant.ISCLICKBLE, "1");
        MyUtils.showHint(mContext);
        //  new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"1");//除初始化 SERVICE步骤，实现统一管理

        initView();
        // initIntent();
        initProssBar1();
        initProssBar2();
        initProssBar3();

        initBle();
        getRzResult();//获取认证结果


        // getList();//获取限速值
        getTsp(new SharedPHelper(mContext).get("TSPVIN", "0") + "");


        //模拟
        // renOnef();

        initxRefreshView();


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
                getTsp(new SharedPHelper(mContext).get("TSPVIN", "0") + "");
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                xRefreshView.stopLoadMore();
            }
        });
    }

    private void getRzResult() {
        String renIsSucc = new SharedPHelpers(mContext, "rz" + new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.ISRENZHENG, "0") + "";//1为认证成功
        if (renIsSucc.equals("0")) {
            MLog.e("此车认证未成功或等待中");
            return;
        }
        initBleConnectAll();
    }

    private void initBleConnectAll() {
        MainActivity.setRzListener(this);

        String state = new SharedPHelper(mContext).get(Constant.BLUECONSTATE, "0") + "";//1为连接，0为断开
        MLog.e("state:" + state);
        if (state.equals("0")) {
            reState = false;
        } else if (state.equals("1")) {
            reState = true;
        }
        blueBrocastRecever = BlueBrocastRecever.getInstance();
        registerReceiver(blueBrocastRecever, BlueBrocastRecever.makeGattUpdateIntentFilterL());//8


        // registerReceiver(BlueBrocastRecever.getInstance(), BlueBrocastRecever.makeGattUpdateIntentFilter());//8
        BlueBrocastRecever.setCallback(this);
        BlueMessUtil.setCallback(this);
//        if(!mBluetoothAdapter.isEnabled()){
//            mBluetoothAdapter.enable();  //打开蓝牙，需要BLUETOOTH_ADMIN权限
//        }

        initPession();//初始化权限
    }

    private void getTsp(final String vin) {
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
                vin,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        xRefreshView.stopRefresh();
                        DialogUtils.hidding(LongControlActivity.this);
                        MLog.e("8");
                        //  DialogUtils.hidding(getActivity());
                        listState.addAll((Collection<?>) obj);
                        MLog.e("充电剩余时间：" + listState.get(2));
                        upCarState();
                        upView();
                        getList();//获取限速值
                    }

                    @Override
                    public void onFial(String str) {
                        xRefreshView.stopRefresh();
                        DialogUtils.hidding(LongControlActivity.this);
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

    private void initService() {
        Intent gattServiceIntent = new Intent(LongControlActivity.this, BleService.class);
        boolean bll = bindService(gattServiceIntent, mServiceConnection,//2
                BIND_AUTO_CREATE);
        if (bll) {
            Log.e("tag", "绑定成功");
        } else {
            Log.e("tag", "绑定失败");
        }

        //连接
        //  service.connectBle(this,mAddress);


    }

    // @RequiresApi(api = M)  //此处注意这个是API23
    private void initBle() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "您的设备不支持蓝牙BLE，将关闭", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.READ_CONTACTS)) {
//                showMessageOKCancel("你必须允许这个权限", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                         initBle();
//                        ActivityCompat.requestPermissions(LongControlActivity.this,
//                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
//                    }
//                });
//                return;
//            }
//            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
//                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        //  }


//            if(Build.VERSION.SDK_INT>=23){
//            //判断是否有权
//            if (ContextCompat.checkSelfPermission(LongControlActivity.this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
//                //请求权限
//                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                        7788);
////向用户解释，为什么要申请该权限
//                if(ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        Manifest.permission.READ_CONTACTS)) {
//                    Toast.makeText(LongControlActivity.this,"shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
    }

//   @Override
//    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
//        // TODO Auto-generated method stub
//        if (requestCode == 7788) {
//            if (permissions[0] .equals(Manifest.permission.ACCESS_COARSE_LOCATION)
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 用户同意使用该权限
//                MLog.e("同意");
//
//
//                //初始化蓝牙
//                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//                mAddress ="0A:5B:00:6B:3C:59";
//                mName = "NEVS Sample";
//                jniHelper=new JniHelper();
//
//                initService();
//
//
//            } else {
//                // 用户不同意，向用户展示该权限作用
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                    //showTipDialog("用来扫描附件蓝牙设备的权限，请手动开启！");
//                    MLog.e("不同意");
//                    return;
//                }
//            }
//        }
//    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    private void getList() {
        listRote.clear();
        //获取限速值:{"enabled":true,"speed":75,"unit":1,"limitChannel":1,"resultMessage":"","resultDescription":""}
        // DialogUtils.loading(mContext, true);
        TspRxUtils.getvehiclelimitervalue(this,
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                new SharedPHelper(mContext).get("TSPVIN", "0").toString(),
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        // DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "获取限速值", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        listRote.addAll((Collection<?>) obj);
                        endle = (boolean) listRote.get(0);
                        rote = String.valueOf(listRote.get(1));
                        if (endle) {
                            upRoteView();
                        } else {
                            //  relBottom.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        //  DialogUtils.hidding((Activity) mContext);
                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
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
                        MyUtils.upLogTSO(mContext, "获取限速值", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }

    //  获取车辆的锁定状态
    private void upCarState() {
        //lockStatus	INT	0:Unlocked 1:Locked
        String lockStatus = listState.get(9) + "";
        if (lockStatus.equals("0")) {
            //79iamgeCar.setBackgroundResource(R.drawable.alpha1);
            lockicon.setBackgroundResource(R.mipmap.sdunlock);
            isLock = false;
        } else if (lockStatus.equals("1")) {
            //79 iamgeCar.setBackgroundResource(R.drawable.alpha1);
            lockicon.setBackgroundResource(R.mipmap.sdlock);
            isLock = true;
        }

        String vehiclestatus = String.valueOf(listState.get(4));
        MLog.e("vehiclestatus：" + vehiclestatus);
        //车辆状态返回值0:Stopped 1:Running  2:Charging 3:Unkown
        switch (vehiclestatus) {
            case "1":
                isRunning=true;
                break;
            case "0":
                isRunning=false;
                break;
            case "2":
                isRunning=false;
                break;
            case "3":
                isRunning=false;
                break;
        }
    }

    //获取车门状态
    private void upView() {
//        "leftFrontDoorStatus": false, BOOLEAN	TRUE: 打开 FALSE:关闭   5
//                "leftRearDoorStatus": false,  6
        //                "rightFrontDoorStatus": false,  7
//                "rightRearDoorStatus": false,   8
        Boolean leftFrontDoorStatus = Boolean.parseBoolean(String.valueOf(listState.get(5)));
        Boolean leftRearDoorStatus = Boolean.parseBoolean(String.valueOf(listState.get(6)));
        Boolean rightFrontDoorStatus = Boolean.parseBoolean(String.valueOf(listState.get(7)));
        Boolean rightRearDoorStatus = Boolean.parseBoolean(String.valueOf(listState.get(8)));
// 79       String doorName = "";
//        if (leftFrontDoorStatus == true) {
//            doorName += "lf";//左前
//        }
//        if (leftRearDoorStatus == true) {
//            doorName += "lr";//左后
//        }
//        if (rightFrontDoorStatus == true) {
//            doorName += "rf";//右前
//        }
//        if (rightRearDoorStatus == true) {
//            doorName += "rr";//右后
//        }
//        MLog.e("doorName:" + doorName);
//        int doorID = MyUtils.getResourceID(doorName, mContext);
//        MLog.e("doorID:" + doorID);
//        if (doorID != 0) {
//            iamgeCar.setBackgroundResource(doorID);
//        }

        imgLeftq.setVisibility(View.INVISIBLE);
        tLeftq.setVisibility(View.INVISIBLE);
        imgLefth.setVisibility(View.INVISIBLE);
        tLefth.setVisibility(View.INVISIBLE);
        imgRightq.setVisibility(View.INVISIBLE);
        tRightq.setVisibility(View.INVISIBLE);
        imgRighth.setVisibility(View.INVISIBLE);
        tRighth.setVisibility(View.INVISIBLE);
//        imgLeftq.setVisibility(View.VISIBLE);
//        tLeftq.setVisibility(View.VISIBLE);
//        imgLefth.setVisibility(View.VISIBLE);
//        tLefth.setVisibility(View.VISIBLE);
//        imgRightq.setVisibility(View.VISIBLE);
//        tRightq.setVisibility(View.VISIBLE);
//        imgRighth.setVisibility(View.VISIBLE);
//        tRighth.setVisibility(View.VISIBLE);
        String doorName = "";
        if (leftFrontDoorStatus == true) {
            doorName += "1";//左前
            imgLeftq.setVisibility(View.VISIBLE);
            tLeftq.setVisibility(View.VISIBLE);
        }
        if (leftRearDoorStatus == true) {
            doorName += "2";//左后
            imgLefth.setVisibility(View.VISIBLE);
            tLefth.setVisibility(View.VISIBLE);
        }
        if (rightFrontDoorStatus == true) {
            doorName += "3";//右前
            imgRightq.setVisibility(View.VISIBLE);
            tRightq.setVisibility(View.VISIBLE);
        }
        if (rightRearDoorStatus == true) {
            doorName += "4";//右后
            imgRighth.setVisibility(View.VISIBLE);
            tRighth.setVisibility(View.VISIBLE);
        }
        MLog.e("doorName:" + doorName);

    }

    //更新限速状态
    private void upRoteView() {
        // relBottom.setVisibility(View.VISIBLE);
        battery.setText(getResources().getString(R.string.nevs_starting));
        mileage.setText(rote + "km/h");
        lineState.setBackgroundColor(getResources().getColor(R.color.n_79CBBD));
        new SharedPHelper(LongControlActivity.this).put("rote", rote);
        if (rote.equals("0")) {
            battery.setText(getResources().getString(R.string.unstart));
            lineState.setBackgroundColor(getResources().getColor(R.color.n_DFDFDF));
        }

    }

    //解锁成功更新UI
    private void upViewc() {
        try {
            isLock = false;
            //79 iamgeCar.setBackgroundResource(R.drawable.alpha1);
            lockicon.setBackgroundResource(R.mipmap.sdunlock);
            ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_unlocksuc));
        } catch (Exception e) {
            MLog.e("UI异常");
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

    private void initProssBar3() {
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.animalone);
        animationTopLeft3.startAnimation(anim);
        Animation anim2 = AnimationUtils.loadAnimation(mContext, R.anim.animaltwo);
        animationTopRight3.startAnimation(anim2);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
////        //将线程销毁掉
////        count1 = 0;
////        handler.removeCallbacks(runnable1);
////        count2 = 0;
////        handler.removeCallbacks(runnable2);
////        count3 = 0;
////        handler.removeCallbacks(runnable3);
//    }

    private void initView() {
//        battery.setText("暂无");
//        mileage.setText("暂无");
//        new SharedPHelper(this).put("rote", "0");
    }

    private void initIntent() {
        rote = (String) new SharedPHelper(this).get("rote", "0");
        MLog.e("rote:" + rote);
        if (rote.equals("0")) {
            // relBottom.setVisibility(View.GONE);
        } else {
            // relBottom.setVisibility(View.VISIBLE);
            battery.setText(getResources().getString(R.string.nevs_starting));
            mileage.setText(rote + "km/h");
        }

    }

    @OnClick({R.id.back, R.id.blutooth_open, R.id.look, R.id.flash, R.id.clock, R.id.statecar})
    public void onVClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.statecar:
                Intent i = new Intent(LongControlActivity.this, WebActivity.class);
                i.putExtra("URL", Constant.HTTP.BANNERURL + "web/appmanual/appmanual_004.jpg");
                i.putExtra("TITLE", getResources().getString(R.string.questions4));
                startActivity(i);
                break;
            case R.id.blutooth_open:
                MobclickAgent.onEvent(mContext,"UnlockCar");
//                if(isRunning){
//                    ActivityUtil.showToast(mContext, getResources().getString(R.string.n_running));
//                    return;
//                }


                if (new SharedPHelper(mContext).get(Constant.TSPISCAROWER, "0").toString().equals("YES")) {
                } else {
//                    if (MyUtils.getPermissions("1", mContext)) {
//
//                    } else {
//                        ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_author_not));
//                        return;
//                    }
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.n_dooropen));
                    return;
                }

                getTsp2(new SharedPHelper(mContext).get("TSPVIN", "0") + "");
                break;
            case R.id.look://锁定
                if (new SharedPHelper(mContext).get(Constant.TSPISCAROWER, "0").toString().equals("YES")) {
                } else {
                    if (MyUtils.getPermissions("2", mContext)) {

                    } else {
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_author_not));
                        return;
                    }
                }

                if (new SharedPHelper(mContext).get(Constant.ISCLICKBLE, "0").equals("1")) {
//                    if (isLock) {
//                        ActivityUtil.showToast(mContext, getResources().getString(R.string.carislock));
//                    } else {
//                        getTsp13();
//                    }
                    getTsp13();
                }

                break;
            case R.id.flash://寻车
                if (new SharedPHelper(mContext).get(Constant.TSPISCAROWER, "0").toString().equals("YES")) {
                } else {
                    if (MyUtils.getPermissions("3", mContext)) {

                    } else {
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_author_not));
                        return;
                    }
                }

                if (new SharedPHelper(mContext).get(Constant.ISCLICKBLE, "0").equals("1")) {
                    getTsp15();
                }

                break;
            case R.id.clock://限速
                if (!MyUtils.isCarowern(mContext)) {//被授权的车辆无法使用限速功能
                    ActivityUtil.showUiToast(getResources().getString(R.string.nodoline));
                    return;
                }
                if (new SharedPHelper(mContext).get(Constant.ISCLICKBLE, "0").equals("1")) {
                    startActivityForResult(new Intent(this, CarRateActivity.class), 802);
                }

                break;

        }
    }

    private void showDialogBle() {
        tipsDialog = new FinancialIOSTipsDialog(this, getResources().getString(R.string.hint_blue), "", 2, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();
                startActivity(new
                        Intent(Settings.ACTION_BLUETOOTH_SETTINGS));

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();
            }
        });
        tipsDialog.show();
        tipsDialog.setCanceledOnTouchOutside(false);
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

    private void removeProgress3() {
        count3 = 0;
        if (isFinishing())
            return;
        pro3.setVisibility(View.GONE);
        MLog.e("3GONE");
    }

    private void getTsp13() {
        /**
         *{"commandId":"fd1cc708fe584a0c887b01859a8e58d1","commandStatus":"Success","resultMessage":"","resultDescription":""}
         * */
        DialogUtils.controllHint1(LongControlActivity.this, true, getResources().getString(R.string.hint_close));
        initProssBar1();
        TspRxUtils.getLock(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "lockType"},
                new Object[]{new SharedPHelper(LongControlActivity.this).get("TSPVIN", "0").toString(), "ForceLockCarFromCostumer"},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.controllHint1(LongControlActivity.this, false, "");
                        commandId = String.valueOf(obj);
                        // ActivityUtil.showToast(LongControlActivity.this, "锁定成功");

                        pro1.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getCommandresult(getResources().getString(R.string.toast_locksuc), 1, commandId);
                            }
                        }, Constant.TIMESLUNXUN);
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.controllHint1(LongControlActivity.this, false, "");
                        ActivityUtil.showToast(LongControlActivity.this, getResources().getString(R.string.toast_lockfail));
                    }
                }
        );

    }

    private void getTsp15() {
        /**
         *cccc:{"commandId":"aaa253740ed54e448cb3393e05dcb0ad","commandStatus":"Success","resultMessage":"","resultDescription":""}
         * */
        DialogUtils.controllHint1(LongControlActivity.this, true, getResources().getString(R.string.hint_look));
        initProssBar2();
        MLog.e("ACCESSTOKENS:"+"Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, ""));
        TspRxUtils.getFlash(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin"},
                new Object[]{new SharedPHelper(LongControlActivity.this).get("TSPVIN", "0").toString()},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.controllHint1(LongControlActivity.this, false, "");
                        commandIdfash = String.valueOf(obj);
                        MLog.e("coommandid:" + commandIdfash);
                        //  ActivityUtil.showToast(LongControlActivity.this, "闪灯成功");


                        pro2.setVisibility(View.VISIBLE);


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getCommandresult(getResources().getString(R.string.toast_flashsuc), 2, commandIdfash);
                            }
                        },Constant.TIMESLUNXUN);//
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.controllHint1(LongControlActivity.this, false, "");
                        ActivityUtil.showToast(LongControlActivity.this, getResources().getString(R.string.toast_flashfail));

                        //测试
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                EventBus.getDefault().post("nihhhj");//发送
//                            }
//                        },Constant.TIMESLUNXUN);


                    }
                }
        );


    }

    private void getCommandresult(final String str, final int id, String com) {
//        switch (id) {
//            case 1:
//                pro1.setVisibility(View.VISIBLE);
//                break;
//            case 2:
//                pro2.setVisibility(View.VISIBLE);
//                break;
//            case 3:
//                pro3.setVisibility(View.VISIBLE);
//                break;
//        }

        //"2018-06-16T03:24:50.877Z"
//        TspRxUtils.getCommandresult(this,
//                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
//                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
//                new String[]{"messageId", "commandStatus", "collectTime"},
//                new Object[]{com, "Success", HashmapTojson.getTime1()},
//                new TspRxListener() {
//                    @Override
//                    public void onSucc(Object obj) {
//                        switch (id) {
//                            case 1:
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        removeProgress1();
//                                        // DialogUtils.controllHint2(LongControlActivity.this,str);
//                                        EventBus.getDefault().post(str);//发送
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                iamgeCar.setBackgroundResource(R.mipmap.c0001);
//                                            }
//                                        });
//                                    }
//                                }, Constant.TIMESLUNXUN);
//                                break;
//                            case 2:
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        removeProgress2();
//                                        //  DialogUtils.controllHint2(LongControlActivity.this,str);
//                                        EventBus.getDefault().post(str);//发送
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                iamgeCar.setBackgroundResource(R.mipmap.clkz_caright_3);
//                                            }
//                                        });
//                                    }
//                                }, Constant.TIMESLUNXUN);
//                                break;
//                            case 3:
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        removeProgress3();
//                                        // DialogUtils.controllHint2(LongControlActivity.this,str);
//                                        EventBus.getDefault().post(str);//发送
//                                        new SharedPHelper(LongControlActivity.this).put("rote", rote);
//                                        MLog.e("限速成功" + new SharedPHelper(LongControlActivity.this).get("rote", "0"));
//                                        initIntent();
//                                    }
//                                }, Constant.TIMESLUNXUN);
//                                break;
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onFial(final String str) {
//                        switch (id) {
//                            case 1:
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        removeProgress1();
//                                        // DialogUtils.controllHint2(LongControlActivity.this,str);
//                                        EventBus.getDefault().post(str);//发送
//                                    }
//                                }, Constant.TIMESLUNXUN);
//                                break;
//                            case 2:
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        removeProgress2();
//                                        // DialogUtils.controllHint2(LongControlActivity.this,str);
//                                        EventBus.getDefault().post(str);//发送
//                                    }
//                                }, Constant.TIMESLUNXUN);
//                                break;
//                            case 3:
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        removeProgress3();
//                                        // DialogUtils.controllHint2(LongControlActivity.this,str);
//                                        EventBus.getDefault().post(str);//发送
//                                    }
//                                }, Constant.TIMESLUNXUN);
//                                break;
//                        }
//
//
//                    }
//                }
//        );
//    }

        TspRxUtils.getCommandresult(this,
//                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
//                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                com,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                        switch (id) {
                            case 1:
                                MyUtils.upLogTSO(mContext, "锁定车辆", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                                removeProgress1();
                                // DialogUtils.controllHint2(LongControlActivity.this,str);
                               // EventBus.getDefault().post(str);//发送
                                BToast.showToast(mContext,getResources().getString(R.string.n_succdor),true);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            //79  iamgeCar.setBackgroundResource(R.drawable.alpha1);
                                            lockicon.setBackgroundResource(R.mipmap.sdlock);
                                            isLock = true;
                                        } catch (Exception e) {
                                            MLog.e("iamgeCar异常");
                                        }

                                    }
                                });

                                break;
                            case 2:
                                MyUtils.upLogTSO(mContext, "寻车", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                                removeProgress2();
                                //  DialogUtils.controllHint2(LongControlActivity.this,str);
                                EventBus.getDefault().post(str);//发送
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // iamgeCar.setBackgroundResource(R.mipmap.clkz_caright_3);
                                        frameAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_list);
                                        // 把AnimationDrawable设置为ImageView的背景
                                        //79iamgeCar.setBackgroundDrawable(frameAnim);
                                        start();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                /**
                                                 *要执行的操作
                                                 */
                                                stop();
                                                //79  iamgeCar.setBackgroundResource(R.drawable.alpha1);
                                            }
                                        }, 3000);//3秒后执行Runnable中的run方法
                                    }
                                });

                                break;
                            case 3:
                                MyUtils.upLogTSO(mContext, "限速", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                                removeProgress3();
                                // DialogUtils.controllHint2(LongControlActivity.this,str);
                                EventBus.getDefault().post(str);//发送
                                new SharedPHelper(LongControlActivity.this).put("rote", rote);
                                MLog.e("限速成功" + new SharedPHelper(LongControlActivity.this).get("rote", "0"));


                                if (str.equals(getResources().getString(R.string.toast_ratesuc))) {
                                    //cc604   initIntent();
                                    getList();//获取限速值
                                } else {
                                    battery.setText(getResources().getString(R.string.unstart));
                                    mileage.setText(0 + "km/h");
                                    lineState.setBackgroundColor(getResources().getColor(R.color.n_DFDFDF));
                                }

                                break;
                        }


                    }

                    @Override
                    public void onFial(final String str) {
                        final String status = String.valueOf(str);

//                        if(str.contains("400")){
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    getCommandresult(getResources().getString(R.string.toast_flashsuc), 2, commandIdfash);
//                                }
//                            }, Constant.TIMESLUNXUN);
//                            return;
//                        }

                        MLog.e("车辆控制失败时：" + str);
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
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyUtils.upLogTSO(mContext, "锁定车辆", String.valueOf(status), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                                            removeProgress1();

                                            showLock(status);
                                        }
                                    }, Constant.TIMESLUNXUNF);
                                    break;
                                case 2:
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyUtils.upLogTSO(mContext, "寻车", String.valueOf(status), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                                            removeProgress2();
                                            // DialogUtils.controllHint2(LongControlActivity.this,str);

                                            showFlash(status);
                                        }
                                    }, Constant.TIMESLUNXUNF);
                                    break;
                                case 3:
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            MyUtils.upLogTSO(mContext, "限速", String.valueOf(status), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                                            removeProgress3();
                                            // DialogUtils.controllHint2(LongControlActivity.this,str);

                                            showRate(status);
                                        }
                                    }, Constant.TIMESLUNXUNF);
                                    break;
                            }



//                        if (status.equals("Rejected")) {
//                            switch (id) {
//                                case 1:
//
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            removeProgress1();
//                                            // DialogUtils.controllHint2(LongControlActivity.this,str);
//                                            EventBus.getDefault().post(getResources().getString(R.string.refusecontrol));//发送
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                                case 2:
//
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            removeProgress2();
//                                            // DialogUtils.controllHint2(LongControlActivity.this,str);
//                                            EventBus.getDefault().post(getResources().getString(R.string.refusecontrol));//发送
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                                case 3:
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            removeProgress3();
//                                            // DialogUtils.controllHint2(LongControlActivity.this,str);
//                                            EventBus.getDefault().post(getResources().getString(R.string.refusecontrol));//发送
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
//                                            removeProgress1();
//                                            // DialogUtils.controllHint2(LongControlActivity.this,str);
//                                            EventBus.getDefault().post(getResources().getString(R.string.carnext));//发送
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                                case 2:
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            removeProgress2();
//                                            // DialogUtils.controllHint2(LongControlActivity.this,str);
//                                            EventBus.getDefault().post(getResources().getString(R.string.carnext));//发送
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                                case 3:
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            removeProgress3();
//                                            // DialogUtils.controllHint2(LongControlActivity.this,str);
//                                            EventBus.getDefault().post(getResources().getString(R.string.carnext));//发送
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                            }
//                        } else {
////                            switch (id) {
////                                case 1:
////                                    new Handler().postDelayed(new Runnable() {
////                                        @Override
////                                        public void run() {
////                                            removeProgress1();
////                                            // DialogUtils.controllHint2(LongControlActivity.this,str);
////                                            EventBus.getDefault().post(getResources().getString(R.string.toast_lockfail));//发送
////                                        }
////                                    }, Constant.TIMESLUNXUN);
////                                    break;
////                                case 2:
////                                    new Handler().postDelayed(new Runnable() {
////                                        @Override
////                                        public void run() {
////                                            removeProgress2();
////                                            // DialogUtils.controllHint2(LongControlActivity.this,str);
////                                            EventBus.getDefault().post(getResources().getString(R.string.toast_flashfail));//发送
////                                        }
////                                    }, Constant.TIMESLUNXUN);
////                                    break;
////                                case 3:
////                                    new Handler().postDelayed(new Runnable() {
////                                        @Override
////                                        public void run() {
////                                            removeProgress3();
////                                            // DialogUtils.controllHint2(LongControlActivity.this,str);
////                                            EventBus.getDefault().post(getResources().getString(R.string.ratefail));//发送
////                                        }
////                                    }, Constant.TIMESLUNXUN);
////                                    break;
////                            }
//                            switch (id) {
//                                case 1:
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            MyUtils.upLogTSO(mContext, "锁定车辆", String.valueOf(status), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");
//
//                                            removeProgress1();
//
//                                            showLock(status);
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                                case 2:
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            MyUtils.upLogTSO(mContext, "寻车", String.valueOf(status), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");
//
//                                            removeProgress2();
//                                            // DialogUtils.controllHint2(LongControlActivity.this,str);
//
//                                            showFlash(status);
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                                case 3:
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            MyUtils.upLogTSO(mContext, "限速", String.valueOf(status), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");
//
//                                            removeProgress3();
//                                            // DialogUtils.controllHint2(LongControlActivity.this,str);
//
//                                            showRate(status);
//                                        }
//                                    }, Constant.TIMESLUNXUN);
//                                    break;
//                            }
//
//                        }

                    }
                }
        );
    }

    private void showLock(String reason) {
        String reason0=reason;
        try {
             reason0=reason.split(",BuildDate")[0];
        }catch (Exception e){
            MLog.e("BuildDate不存在");
        }
        switch (reason0) {
            case "LockingFailcarrunning":
                EventBus.getDefault().post(getResources().getString(R.string.LockingFailcarrunning));//发送
                break;
            case "LockingSuccessKeystillactiveandnotincar":
                EventBus.getDefault().post(getResources().getString(R.string.LockingSuccessKeystillactiveandnotincar));//发送
                break;
            case "LockingSuccessKeystillative":
                EventBus.getDefault().post(getResources().getString(R.string.LockingSuccessKeystillative));//发送
                break;
            case "LockingSuccesskeyisnotincar":
                EventBus.getDefault().post(getResources().getString(R.string.LockingSuccesskeyisnotincar));//发送
                break;
            case "LockingSuccesskeynotinpossition":
                EventBus.getDefault().post(getResources().getString(R.string.LockingSuccesskeynotinpossition));//发送
                break;
            case "LockingSuccessKeystillactiveandnotinposition":
                EventBus.getDefault().post(getResources().getString(R.string.LockingSuccessKeystillactiveandnotinposition));//发送
                break;
            case "LockingfaildKeystillactive":
                EventBus.getDefault().post(getResources().getString(R.string.LockingfaildKeystillactive));//发送
                break;
            case "Lockingfaildkeydeactivaed":
                EventBus.getDefault().post(getResources().getString(R.string.Lockingfaildkeydeactivaed));//发送
                break;
            case "Lockingfaildkeyisdeactivatedbutnotincar":
                EventBus.getDefault().post(getResources().getString(R.string.Lockingfaildkeyisdeactivatedbutnotincar));//发送
                break;
            case "LockingfaildKeystillactiveandnotincar":
                EventBus.getDefault().post(getResources().getString(R.string.LockingfaildKeystillactiveandnotincar));//发送
                break;
            case "Lockingfaildkeynotinpossition":
                EventBus.getDefault().post(getResources().getString(R.string.Lockingfaildkeynotinpossition));//发送
                break;
            case "LockingfaildKeystillactiveandnotinpossition":
                EventBus.getDefault().post(getResources().getString(R.string.LockingfaildKeystillactiveandnotinpossition));//发送
                break;
            case "LockingSuccess,butsomedoorcanstillbeopen":
                EventBus.getDefault().post(getResources().getString(R.string.LockingSuccessbutsomedoorcanstillbeopen));//发送
                break;
            default:
                EventBus.getDefault().post(getResources().getString(R.string.toast_lockfail));//发送
                break;
        }
    }

    private void showFlash(String reason) {
        String reason0=reason;
        try {
            reason0=reason.split(",BuildDate")[0];
        }catch (Exception e){
            MLog.e("BuildDate不存在");
        }
        switch (reason0) {
            case "failedforcarinuse":
                EventBus.getDefault().post(getResources().getString(R.string.failedforcarinuse));//发送
                break;
            case "failedforgettingEvModetimeout":
                EventBus.getDefault().post(getResources().getString(R.string.failedforgettingEvModetimeout));//发送
                break;
            case "failedforheadlampson":
                EventBus.getDefault().post(getResources().getString(R.string.failedforheadlampson));//发送
                break;
            case "failedforgettingpositionlampstatustimeout":
                EventBus.getDefault().post(getResources().getString(R.string.failedforgettingpositionlampstatustimeout));//发送
                break;
            case "failedforlightoncmdrespondisfalse":
                EventBus.getDefault().post(getResources().getString(R.string.failedforlightoncmdrespondisfalse));//发送
                break;
            case "failedforheadlightctrlcmdtimeout":
                EventBus.getDefault().post(getResources().getString(R.string.failedforheadlightctrlcmdtimeout));//发送
                break;
            case "failedforstopflashlightcmdsendfailed":
                EventBus.getDefault().post(getResources().getString(R.string.failedforstopflashlightcmdsendfailed));//发送
                break;
            default:
                EventBus.getDefault().post(getResources().getString(R.string.toast_flashfail));//发送
                break;
        }
    }

    private void showRate(String reason) {
        String reason0=reason;
        try {
            reason0=reason.split(",BuildDate")[0];
        }catch (Exception e){
            MLog.e("BuildDate不存在");
        }
        switch (reason0) {
            case "SendEpcmToMcufailed":
                EventBus.getDefault().post(getResources().getString(R.string.SendEpcmToMcufailed));//发送
                break;
            case "totsp:drivelinefailedfortimeout":
                EventBus.getDefault().post(getResources().getString(R.string.totsp_drivelinefailedfortimeout));//发送
                break;
            case "drivelinecontrolfailed":
                EventBus.getDefault().post(getResources().getString(R.string.drivelinecontrolfailed));//发送
                break;
            default:
                EventBus.getDefault().post(getResources().getString(R.string.ratefail));//发送
                break;
        }
    }

    private void getTsp14(boolean flag, final String s) {
        /**
         *cccc:{"commandId":"358cf798a46541a4b2906d662f380088","commandStatus":"Success","resultMessage":"","resultDescription":""}
         * cccc:{"resultMessage":"Parameter error","resultDescription":"Invalid time format!"}
         * */
        if (flag) {
            DialogUtils.controllHint1(LongControlActivity.this, true, getResources().getString(R.string.hint_rate));
        } else {
            DialogUtils.controllHint1(LongControlActivity.this, true, getResources().getString(R.string.n_cancespeed));
        }

        initProssBar3();
        TspRxUtils.getVehiclelimiter(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "enabled", "speed", "unit"},
                new Object[]{new SharedPHelper(LongControlActivity.this).get("TSPVIN", "0").toString(), flag, Integer.parseInt(rote), 1},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.controllHint1(LongControlActivity.this, false, "");
                        commandIdrate = String.valueOf(obj);
                        MLog.e("coommandid:" + commandIdrate);

                        pro3.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getCommandresult(s, 3, commandIdrate);
                            }
                        }, Constant.TIMESLUNXUN);
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.controllHint1(LongControlActivity.this, false, "");
                        if (str.contains("400") || str.contains("无效的请求")) {
                            // ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 902 && requestCode == 802) {
            boolean f = data.getBooleanExtra("ISRATE", true);
            String rotee = data.getStringExtra("rates");
            rote = rotee;
            MLog.e("回传" + rote);
            if (f) {
                getTsp14(true, getResources().getString(R.string.toast_ratesuc));
            } else {
                getTsp14(false, getResources().getString(R.string.toast_cancleratesuc));
            }


        }
    }


    protected void start() {
        if (frameAnim != null && !frameAnim.isRunning()) {
            frameAnim.start();
            //  Toast.makeText(mContext, "开始播放", 0).show();
            MLog.e("index 为5的帧持续时间为：" + frameAnim.getDuration(5) + "毫秒");
            MLog.e("当前AnimationDrawable一共有" + frameAnim.getNumberOfFrames() + "帧");
        }
    }

    protected void stop() {
        if (frameAnim != null && frameAnim.isRunning()) {
            frameAnim.stop();
            // Toast.makeText(mContext, "停止播放", 0).show();
            MLog.e("停止播放");
        }
    }


    //蓝牙功能


    //    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();//接收广播
//            Log.e("tag", "action:" + action);
//            if (BleService.NOTIFYRENZ.equals(action)) {//通知开启
//
//                renOnef();//开始认证
//
//            } else if (BleService.ACTION_GATT_CONNECTED.equals(action)) {//连接状态
////                connectFlag = CONNECTED;
////                proDialog.dismiss();
////                state.setText("已连接");
////                finis.setText("断开");
////                flag=false;
////                ToastU.showShort(getApplicationContext(), "连接成功");
//                //  writeDate(true);
//                MLog.e("连接成功");
//
//               // renOnef();//开始认证
//
//                invalidateOptionsMenu();
//            } else if (BleService.ACTION_GATT_DISCONNECTED
//                    .equals(action)) {//未连接
//                //加入是否可以点击
////                proDialog.dismiss();
////                state.setText("未连接");
////                finis.setText("连接");
////                flag=true;
////                ToastU.showShort(getApplicationContext(), "连接失败");
////                Log.e("tag", isClickDisconnect + "isClickDisconnect");
////                if (!isClickDisconnect) {
////                    //    vibrator.vibrate(new long[]{100, 2000, 500, 2500}, -1);
////                    //  mp.start();
////                }
////                closeUi();
//                MLog.e("未连接");
//            } else if (BleService.ACTION_GATT_SERVICES_DISCOVERED
//                    .equals(action)) {
//                // 搜索需要的uuid
//                initGattCharacteristics(mBleService
//                        .getSupportedGattServices());
//                // writeDate(true);
//              //  Toast.makeText(MainControllActivity.this, "发现新services", Toast.LENGTH_SHORT).show();
//                MLog.e("发现新services");
//            } else if (BleService.ACTION_DATA_AVAILABLE.equals(action)) {
//
//            } else if (BleService.READ_RSSI.equals(action)) {
//               // Log.e("tag", "rssi:" + rssi);
////                checkRssi(rssi);
//              //  iss.setText("信号：" + Math.abs(rssi));
//            } else if (BleService.SER.equals(action)) {
////                SUUIDW = intent.getStringExtra("suuid");
////                nonono.append(SUUIDW+"\n");
//
//
//            } else if (BleService.WRITE.equals(action)) {
////                UUIDW = intent.getStringExtra("uuids");
////                // MLog.e("UUIDs:" + UUIDW);
////                nonono.append(UUIDW+"\n");
//            }else if (BleService.TOAST.equals(action)) {
//                // nonono.setText("");
////                nonono.append("写入成功"+"\n");
////
////                if(intent.getStringExtra("Hex")==null){
////                    return;
////                }
////                nonono.append(intent.getStringExtra("Hex")+"\n");
//
//                MLog.e("写入成功\"+\"\\n");
//                MLog.e(intent.getStringExtra("Hex")+"\n");
//            }else if (BleService.ACCEPT.equals(action)) {//开始认证
//                //nonono.setText("");
//              //  nonono.append("收到数据"+"\n");
//
//                if(intent.getStringExtra("AHex")==null){
//                    return;
//                }
//              //  nonono.append(intent.getStringExtra("AHex")+"\n");
//                frameid= Integer.parseInt(intent.getStringExtra("FRAMID"));
//                if(shou){
//
//                    MLog.e("接收流程");
//
//                }else {
//                    if(frameid==-1){
//                        MLog.e("认证结束时间 耗时："+ (MyUtils.timeStampNow()-startrenzheng)+"s");
//                        shou=true;
//                    }else {
//                        try {
//                            //控制异常  40
//                            Thread.sleep(40);
//                            mBleService.sendOrders(listBytesRenz.get(frameid), frameid);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//
////                if(frameid==37){
////                    MLog.e("收到37帧ID");
////                    try {
////                        Thread.sleep(50);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                    mBleService.sendOrders(listBytesRenz.get(frameid), frameid);
////                }else if(frameid==38){
////                    MLog.e("收到38帧ID");
////                    try {
////                        Thread.sleep(50);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                    mBleService.sendOrders(listBytesRenz.get(frameid), frameid);
////                }else {
////                    try {
////                        Thread.sleep(50);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                    mBleService.sendOrders(listBytesRenz.get(frameid), frameid);
////                }
//
//            }else if (BleService.SETMTU.equals(action)) {
//             //   btnOpen.setText("");
//                if(intent.getStringExtra("MTU")==null){
//                   return;
//                }
//               // renOnef();//开始认证
//              //  btnOpen.append(intent.getStringExtra("MTU")+"\n");
//            }else if(BleService.BYTEACCEPT.equals(action)){//认证接收
//                byte[] acc=intent.getByteArrayExtra("byteacept2");
//                getCrcRenzOnej(acc);
//            }else if(BleService.BYTEACCEPTSS.equals(action)){//认证接收后AES发送
//                byte[] acc=intent.getByteArrayExtra("byteacept3");
//
//                if(shou2){
//                    MLog.e("renz2接收流程");
//                }else {
//                    if(acc[2]==-1){
//                        MLog.e("rezAES发送结束时间："+MyUtils.getTimeNow());
//                        shou=true;
//
//                    }else {
//                        if(acc[2]==0){
//                            MLog.e("收到心跳接收的广播里面");
//                        }else {
//                            try {
//                                Thread.sleep(40);
//                                // mBleService.sendOrders(listrenzSplit.get(acc[2]), acc[2]);
//                                mBleService.sendOrders(listrenzSplit.get(acc[2]), acc[2]);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                MLog.e("rezAES发送结束时间后异常:"+e);
//                            }
//                        }
//
//
//                    }
//                }
//
//            }else if(BleService.BYTEACCEPT2.equals(action)){//认证2接收
//                byte[] acc=intent.getByteArrayExtra("byteacept4");
//                getCrc2(acc);
//            }else if(BleService.BYTEACCEPTSTART.equals(action)){//START发送
//                byte[] acc=intent.getByteArrayExtra("byteacept5");
//
//
//                if(acc[2]==-1){
//                    MLog.e("start发送结束时间："+MyUtils.getTimeNow());
//
//
//                }else {
//                    try {
//                        Thread.sleep(40);
//                        mBleService.sendOrders(listStartbooking.get(acc[2]), acc[2]);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        MLog.e("发送异常"+e);
//                    }
//
//                }
//
//
//            }else if(BleService.BYTESTARTJIE.equals(action)){//startbooking接收
//                byte[] acc=intent.getByteArrayExtra("byteacept6");
//                if(acc!=null) {
//                    getCrcStartbookingj(acc);
//                }
//            }else if(BleService.BYTEBOOKINGSTART.equals(action)){//STARTED ok发送
//                byte[] acc=intent.getByteArrayExtra("byteacept7");
//
//
//                if(acc[2]==-1){
//                    MLog.e("startED ok发送结束时间："+MyUtils.getTimeNow());
//
//                    //     new SharedPHelper(MainControllActivity.this).put("step8","1");
//
//                }else {
//                    MLog.e("ok第二次发送");
//                    try {
//                        Thread.sleep(40);
//                        mBleService.sendOrders(listbookingstarted.get(acc[2]), acc[2]);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        MLog.e("发送异常"+e);
//                    }
//
//                }
//
//            }else if(BleService.BYTESTARTJIEPIN.equals(action)){//接收手动发ENDPIN的指令
//                byte[] acc=intent.getByteArrayExtra("byteacept8");
//                if(acc!=null) {
//                    getCrc8(acc);
//                }
//            }else if(BleService.ENTERPIN.equals(action)){//STARTED发送
//                byte[] acc=intent.getByteArrayExtra("byteacept9");
//
//                if(acc[2]==-1){
//                    MLog.e("PIN发送结束时间："+MyUtils.getTimeNow());
//
//                }else {
//                    try {
//                        Thread.sleep(40);
//                        mBleService.sendOrders(listbookingstartedpin.get(acc[2]), acc[2]);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        MLog.e("发送异常"+e);
//                    }
//
//                }
//
//            }else if(BleService.BYTEPINEND.equals(action)){//接收手动发ENDPIN的指令
//                byte[] acc=intent.getByteArrayExtra("byteacept10");
//                if(acc!=null) {
//                    getCrcPINEND(acc);
//                }
//            }else if(BleService.BYTEPINEND2.equals(action)){//接收手动发ENDPIN的指令
//                byte[] acc=intent.getByteArrayExtra("byteacept20");
//                if(acc!=null) {
//                    getCrcPINEND2(acc);
//                }
//            }
//
//            else if(BleService.BYTEUNLOCKF.equals(action)){//UNLOCK发送
//                byte[] acc=intent.getByteArrayExtra("byteacept11");
//
//
//                if(acc[2]==-1){
//                    MLog.e("UNLOCK发送结束时间："+MyUtils.getTimeNow());
//
//
//                }else {
//                    try {
//                        Thread.sleep(40);
//                        mBleService.sendOrders(listByteUnLock.get(acc[2]), acc[2]);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        MLog.e("发送异常"+e);
//                    }
//
//                }
//
//
//            }else if(BleService.BYTEUNLOCKJ.equals(action)){//接收UNLOCK
//                byte[] acc=intent.getByteArrayExtra("byteacept12");
//                if(acc!=null) {
//                    getCrcUNLOCK(acc);
//                }
//            }else if(BleService.BYTEUNLOCKJ2.equals(action)){//接收UNLOCK2
//                byte[] acc=intent.getByteArrayExtra("byteacept13");
//                if(acc!=null) {
//                    getCrcUNLOCK2(acc);
//                }
//            }
//        }
//    };
    private void initGattCharacteristics(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            return;
        }
        mGattCharacteristics = new ArrayList<>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
            }
            mGattCharacteristics.add(charas);
        }
    }


    // ------------------------------------------------public  start
    private void getAck(byte[] value2) {
        byte[] ack = new byte[5];
        ack[0] = (byte) 0xFF;
        ack[1] = (byte) 0xA5;
        ack[2] = value2[2];
        ack[3] = 0x00;
        ack[4] = value2[2];
        try {
            Thread.sleep(30);
            mBleService.sendOrders(ack, ack[2]);
        } catch (Exception e) {
            e.printStackTrace();
            MLog.e("发送异常" + e);
        }

    }

    //将后16未去掉,重组后解密
    private String getAESDe(List<byte[]> listAES) {
        String json = "";
        try {
            //1获取字节总长度
            int lengs = 0;
            for (int n = 0; n < listAES.size(); n++) {
                lengs += listAES.get(n).length;
            }
            MLog.e("AES有效数据位总长度:" + lengs);
            //2合并数组成一个新的数组
            byte[] aeshe = new byte[lengs];
            int hh = 0;
            for (int i = 0; i < listAES.size(); i++) {
                for (int j = 0; j < listAES.get(i).length; j++) {
                    aeshe[hh] = listAES.get(i)[j];
                    hh++;
                }
            }
            //2去掉后16位
            byte[] aesend = new byte[aeshe.length - 16];
            for (int p = 0; p < aeshe.length - 16; p++) {
                aesend[p] = aeshe[p];
            }

            //3AES解密
            AES aes = new AES();
            // 解密方法
            byte[] dec = aes.decrypt(aesend, aes.key0.getBytes());
            json = new String(dec, "UTF-8");
            Log.e("tag", "aes解密后的内容：" + json);
        } catch (Exception e) {
            e.printStackTrace();
            MLog.e("UNLOCKaes解密后异常");
        }
        return json;
    }

    //-------------------------------------------------public   end


    //22222222222222222222222222222222222222222222222222222222  start
    //Startbooking 整个流程
    public void startbooking() {//点击开始startbooking
        new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "5");
        /**
         * A数据源
         * */
        String json = HashmapTojson.getJson(
                new String[]{"id", "t"},
                new Object[]{12345678, "StartBooking"}
        );

        MLog.e("startbooking:" + json);
        byte aa[] = new byte[0];
        try {
            aa = json.getBytes("UTF-8");
            MLog.e("startrenz2aa" + aa.length);

        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * B  AES加密，然后SHA256取前16拼在AES加密后的后面
         * */
        //String key0=getResources().getString(R.string.car_healtho);
        String key0 = String.valueOf(new SharedPHelpers(mContext, "rz" + new SharedPHelper(LongControlActivity.this).get("TSPVIN", "0").toString()).get(getResources().getString(R.string.SymmEnccc), ""));
        AES aes = new AES();
        byte[] enc = aes.encrypt(aa, key0.getBytes());
//        Log.e("tag","加密后的内容：" + new String(Hex.encode(enc)));
//        Log.e("tag","加密后的长度：" + enc.length);
//        Log.e("tag","加密后的内容base64：" + com.nevs.bluetooth.encrypt.Base64.encode(enc));

        //对AES加密后的内容进行SHAMAC256
        byte[] sha256 = Sha256Util.HmacSHA256(enc);
        // MLog.e("HMACK256:"+ com.nevs.bluetooth.encrypt.Base64.encode(sha256));
        //取前16个字节拼到enc后组成新的数组
        byte[] byte16 = new byte[16];
        for (int i = 0; i < 16; i++) {
            byte16[i] = sha256[i];
        }

        byte[] aeshe = new byte[enc.length + 16];

        int kk = 0;
        List<byte[]> list = new ArrayList<>();
        list.add(enc);
        list.add(byte16);
        for (int n = 0; n < list.size(); n++) {
            for (int m = 0; m < list.get(n).length; m++) {
                aeshe[kk] = list.get(n)[m];
                kk++;
            }
        }
        MLog.e("AES完整加密后的字节个数：" + aeshe.length);


        /**
         * C分组后发送
         * */
        //按120个分成一组
        bytestartbooking = null;
        bytestartbooking = split_bytes(aeshe, 120);  //120分包大小
        MLog.e("bytestartbooking.length:分包个数：" + bytestartbooking.length);

        MLog.e("\r\n\r\n==========================================\r\n");

        //组包
        initCRCRenzssstartbooking();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MLog.e("整个数据包：" + Base64.encode(listStartbooking.get(0)));
                try {
                    mBleService.sendOrders(listStartbooking.get(0), 0);
                } catch (Exception e) {
                    MLog.e("蓝牙为连接发送了startbooking" + e);
                }

            }
        }).start();

    }

    public void initCRCRenzssstartbooking() {

        listStartbooking.clear();
        byte[] byteSend = new byte[0];
        //vv一
        for (int i = 0; i < bytestartbooking.length; i++) {
            byte[] byted = new byte[bytestartbooking[i].length];
            byteSend = new byte[byted.length + 5];

            //计算效验和vv
            short sum = 0;
            for (int j = 0; j < bytestartbooking[i].length; j++) {
                sum += bytestartbooking[i][j];
            }
            //   short CRC= (short) ((i+1)+bytesRSplit[i].length+sum);
            short CRC = (short) ((i + 1) + bytestartbooking[i].length + sum);

            if (i == bytestartbooking.length - 1) {
                CRC = (short) ((-1) + bytestartbooking[i].length + sum);
            }

            byteSend[0] = (byte) 0xFF;
            byteSend[1] = (byte) 0xA5;
            byteSend[2] = (byte) ((i + 1) & 0xFF);

            if (i == bytestartbooking.length - 1) {
                byteSend[2] = (byte) 0xFF;
            }

            MLog.e("byteSend[2]" + byteSend[2]);
            byteSend[3] = (byte) (byted.length & 0xFF);
            for (int k = 4; k < byteSend.length - 1; k++) {
                byteSend[k] = bytestartbooking[i][k - 4];
            }
            byteSend[byteSend.length - 1] = (byte) (getUint8(CRC) & 0xFF);
            // MLog.e("(byte) CRC："+(byte) CRC);
            listStartbooking.add(byteSend);
        }
        //^^一
        MLog.e("listStartbookingaes包的个数：" + listStartbooking.size());
    }

    private void getCrcStartbookingj(byte[] value2) {
        //计算效验和vv
        short sum = 0;//有效数据位和
        for (int j = 4; j < value2.length - 1; j++) {
            sum += value2[j];
        }
        short CRCs = (short) (value2[2] + value2[3] + sum);
        byte CRCb = (byte) (getUint8(CRCs) & 0xFF);
        MLog.e("crcshort:" + CRC);
        if (CRCb == value2[value2.length - 1]) {
            MLog.e("效验成功");

            byte[] bytey = new byte[value2.length - 5];
            int ii = 0;
            for (int k = 4; k < value2.length - 1; k++) {
                bytey[ii] = value2[k];
                ii++;
            }
            liststart.add(bytey);

            getAck(value2);
            if (value2[2] == -1) {
                MLog.e("认证接收的包的个数：" + liststart.size());

                String json = getAESDe(liststart);//AES解密
                jsonStartbookingJ(json);

            } else {

            }

        } else {
            MLog.e("效验失败");
        }
    }

    private void jsonStartbookingJ(String json) {
        //{"id":0,"t":"BookingStarted"}
        String t = "";
        liststart.clear();
        try {
            JSONObject js = new JSONObject(json);
            t = js.getString("t");
            MLog.e("t startbooking接收:" + t);
            if (t.equals("BookingStarted")) {
                alertDialog(LongControlActivity.this, true);
            }

//            else if(t.equals("EnterPIN")){
//                enterPINDialog(MainControllActivity.this,false);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //发OK过去，此过程不会接收到消息，等待(现在暂时手动)发送过来的ENDPIN指令
    public void alertDialog(final Context context, boolean flag) {//发送OK  弹框OK
//        android.app.AlertDialog aldialod=new android.app.AlertDialog.Builder(context)
//                .setTitle(context.getResources().getString(R.string.notifyTitle))
//                .setMessage("是否OK")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        //进行发送的流程：
//                        getJsonokF();
//                    }
//                })
//                .setNegativeButton(context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
//        aldialod.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false


        tipsDialog = new FinancialIOSTipsDialog(this, getResources().getString(R.string.isconfirmopen), "", 2, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();
                //进行发送的流程：
                getJsonokF();

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();
            }
        });
        tipsDialog.show();
        tipsDialog.setCancelable(false);
        tipsDialog.setCanceledOnTouchOutside(false);
    }

    private void getJsonokF() {
        String json = HashmapTojson.getJson(
                new String[]{"id", "t", "r"},
                new Object[]{12345678, "BookingStarted", new BookingStarted("Ok", 0, "Succeed")}
        );

        MLog.e("BookingStartedOK:" + json);
        byte aa[] = new byte[0];
        try {
            aa = json.getBytes("UTF-8");
            MLog.e("BookingStartedaa" + aa.length);
            getaesBookingstarted(aa);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getaesBookingstarted(byte[] aa) {

        // String name="q";
        // String key0=getResources().getString(R.string.car_healtho);
        String key0 = String.valueOf(new SharedPHelpers(mContext, "rz" + new SharedPHelper(LongControlActivity.this).get("TSPVIN", "0").toString()).get(getResources().getString(R.string.SymmEnccc), ""));
        AES aes = new AES();
        byte[] enc = aes.encrypt(aa, key0.getBytes());
//        Log.e("tag","加密后的内容：" + new String(Hex.encode(enc)));
//        Log.e("tag","加密后的长度：" + enc.length);
//        Log.e("tag","加密后的内容base64：" + com.nevs.bluetooth.encrypt.Base64.encode(enc));


        //对AES加密后的内容进行SHAMAC256
        byte[] sha256 = Sha256Util.HmacSHA256(enc);
        // MLog.e("HMACK256:"+ com.nevs.bluetooth.encrypt.Base64.encode(sha256));
        //取前16个字节拼到enc后组成新的数组
        byte[] byte16 = new byte[16];
        for (int i = 0; i < 16; i++) {
            byte16[i] = sha256[i];
        }

        byte[] aeshe = new byte[enc.length + 16];

        int kk = 0;
        List<byte[]> list = new ArrayList<>();
        list.add(enc);
        list.add(byte16);
        for (int n = 0; n < list.size(); n++) {
            for (int m = 0; m < list.get(n).length; m++) {
                aeshe[kk] = list.get(n)[m];
                kk++;
            }
        }
        MLog.e("发OK AES完整加密后的字节个数：" + aeshe.length);

        //分组后发送
        getBookingstared(aeshe);
    }

    private void getBookingstared(byte[] aeshe) {
        //按120个分成一组
        bytebookingstarted = null;
        bytebookingstarted = split_bytes(aeshe, 120);  //120分包大小
        MLog.e("bytebookingstarted.length:分包个数：" + bytebookingstarted.length);

        MLog.e("\r\n\r\n==========================================\r\n");
        //组包
        initbookingstarted();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //test 250
                    Thread.sleep(150);
                    MLog.e("整个数据包：" + Base64.encode(listStartbooking.get(0)));
                    mBleService.sendOrders(listbookingstarted.get(0), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e("发送异常" + e);
                }

            }
        }).start();
    }

    private void initbookingstarted() {
        byte[] byteSend = new byte[0];
        listbookingstarted.clear();
        //vv一
        for (int i = 0; i < bytebookingstarted.length; i++) {
            byte[] byted = new byte[bytebookingstarted[i].length];
            byteSend = new byte[byted.length + 5];

            //计算效验和vv
            short sum = 0;
            for (int j = 0; j < bytebookingstarted[i].length; j++) {
                sum += bytebookingstarted[i][j];
            }
            //   short CRC= (short) ((i+1)+bytesRSplit[i].length+sum);
            short CRC = (short) ((i + 1) + bytebookingstarted[i].length + sum);

            if (i == bytebookingstarted.length - 1) {
                CRC = (short) ((-1) + bytebookingstarted[i].length + sum);
            }

            byteSend[0] = (byte) 0xFF;
            byteSend[1] = (byte) 0xA5;
            byteSend[2] = (byte) ((i + 1) & 0xFF);

            if (i == bytebookingstarted.length - 1) {
                byteSend[2] = (byte) 0xFF;
            }

            MLog.e("byteSend[2]" + byteSend[2]);
            byteSend[3] = (byte) (byted.length & 0xFF);
            for (int k = 4; k < byteSend.length - 1; k++) {
                byteSend[k] = bytebookingstarted[i][k - 4];
            }
            byteSend[byteSend.length - 1] = (byte) (getUint8(CRC) & 0xFF);
            // MLog.e("(byte) CRC："+(byte) CRC);
            listbookingstarted.add(byteSend);
        }
        //^^一
        MLog.e("listbookingstartedaes包的个数：" + listbookingstarted.size());
    }

    //TU-->APP (现在暂时手动发) ENDPIN 的指令  接收流程
    private void getCrc8(byte[] value2) {
        //计算效验和vv
        short sum = 0;//有效数据位和
        for (int j = 4; j < value2.length - 1; j++) {
            sum += value2[j];
        }
        short CRCs = (short) (value2[2] + value2[3] + sum);
        byte CRCb = (byte) (getUint8(CRCs) & 0xFF);
        MLog.e("crcshort:" + CRC);
        if (CRCb == value2[value2.length - 1]) {
            MLog.e("效验成功");

            byte[] bytey = new byte[value2.length - 5];
            int ii = 0;
            for (int k = 4; k < value2.length - 1; k++) {
                bytey[ii] = value2[k];
                ii++;
            }
            liststartok.add(bytey);


            getAck(value2);
            if (value2[2] == -1) {
                MLog.e("认证接收的包的个数：" + liststartok.size());

                String json = getAESDe(liststartok);//AES解密
                jsonJie8(json);


            } else {

            }

        } else {
            MLog.e("效验失败");
        }
    }

    private void jsonJie8(String json) {
        //{//TU->MD inform enter pin
//        “id”:12345678,//command ID
//        “t”:”EnterPIN”//command type
//    }
        String t = "";
        liststartok.clear();
        try {
            JSONObject js = new JSONObject(json);
            t = js.getString("t");
            MLog.e("t EnterPIN:" + t);
            if (t.equals("EnterPIN")) {
                enterPINDialog(this, false);
            } else if (t.equals("AuthFailed")) {
                ActivityUtil.showUiToast("AuthFailed");
                getJsonPINFAuthFailed("AuthFailed", "Ok");
            } else if (t.equals("UnlockingFailed")) {
                ActivityUtil.showUiToast("UnlockingFailed");
                getJsonPINFAuthFailed("UnlockingFailed", "Ok");
            } else if (t.equals("KeyMissing")) {
                ActivityUtil.showUiToast("KeyMissing");
                getJsonPINFAuthFailed("KeyMissing", "Ok");
            } else if (t.equals("Helpdesk")) {
                MLog.e("if21  Helpdesk");
                getJsonPINFAuthFailed("Helpdesk", "Ok");

            }

        } catch (Exception e) {
            e.printStackTrace();
            MLog.e("EnterPINYIC:" + e);
        }
    }

    //最后一个环节发送PIN码过去  发送流程
    private void enterPINDialog(Context context, boolean flag) {//PIN弹框
//        android.app.AlertDialog aldialod=new android.app.AlertDialog.Builder(context)
//                .setTitle(context.getResources().getString(R.string.notifyTitle))
//                .setMessage("请输入PIN码")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        //进行发送PIN的流程：
//                        getJsonPINF();
//                    }
//                })
//                .setNegativeButton(context.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
//        aldialod.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false


        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_iosedit, null);//获取自定义布局
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(view);//设置自定义样式布局到对话框
        final android.app.AlertDialog dialog = builder.create();//获取dialog
        dialog.show();//显示对话框
        dialog.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false
        dialog.setCancelable(false);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_tv_contentedit);
        TextView left = (TextView) view.findViewById(R.id.dialog_left_tv);//右边
        TextView right = (TextView) view.findViewById(R.id.dialog_right_tv);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//右边


                String pin = editText.getText().toString().trim();
                MLog.e("dsd:" + editText.getText().toString());

                if (pin.length() == 0) {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.enterpindialog));
                } else {
                    dialog.dismiss();
                    //进行发送PIN的流程：234567
                    getJsonPINF(pin);
                }

            }
        });


        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //取消发送PIN码
                enJsonPINF("EnterPIN", "Error", 1, "Cancel sending");
            }
        });
    }

    //最后一个环节发送PIN码过去 错误 的弹框   重新发 送流程
    private void enterPINDialogRE(Context context, boolean flag) {//PIN弹框
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_iosedit_re, null);//获取自定义布局
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(view);//设置自定义样式布局到对话框
        final android.app.AlertDialog dialog = builder.create();//获取dialog
        dialog.show();//显示对话框
        dialog.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false
        dialog.setCancelable(false);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_tv_contentedit);
        TextView left = (TextView) view.findViewById(R.id.dialog_left_tv);//右边
        TextView right = (TextView) view.findViewById(R.id.dialog_right_tv);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//右边


                String pin = editText.getText().toString().trim();
                MLog.e("dsd:" + editText.getText().toString());

                if (pin.length() == 0) {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.enterpindialog));
                } else {
                    dialog.dismiss();
                    //进行发送PIN的流程：234567
                    getJsonPINF(pin);
                }

            }
        });


        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                //取消发送PIN码
                enJsonPINF("EnterPIN", "Error", 1, "Cancel sending");
            }
        });
    }

    private void enterPINDialogERR1(Context context, boolean flag) {//PIN弹框
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_iosedit_err1, null);//获取自定义布局
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(view);//设置自定义样式布局到对话框
        final android.app.AlertDialog dialog = builder.create();//获取dialog
        dialog.show();//显示对话框
        dialog.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false
        dialog.setCancelable(false);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_tv_contentedit);
        TextView left = (TextView) view.findViewById(R.id.dialog_left_tv);//右边
        TextView right = (TextView) view.findViewById(R.id.dialog_right_tv);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//右边
                dialog.dismiss();
                //进行发送是否取消的指令  重试
                getJsonPINFERR1("CorrectVehicle", "Retry");


            }
        });


        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //进行发送是否取消的指令   取消
                getJsonPINFERR1("CorrectVehicle", "Abort");
            }
        });
    }

    private void enterPINDialogERR2(Context context, boolean flag) {//PIN弹框
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_iosedit_err1, null);//获取自定义布局
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(view);//设置自定义样式布局到对话框
        final android.app.AlertDialog dialog = builder.create();//获取dialog
        dialog.show();//显示对话框
        dialog.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false
        dialog.setCancelable(false);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_tv_contentedit);
        TextView left = (TextView) view.findViewById(R.id.dialog_left_tv);//右边
        TextView right = (TextView) view.findViewById(R.id.dialog_right_tv);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//右边
                dialog.dismiss();
                //进行发送是否取消的指令  重试
                getJsonPINFERR1("Disclamer", "Retry");


            }
        });


        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //进行发送是否取消的指令   取消
                getJsonPINFERR1("Disclamer", "Abort");
            }
        });
    }

    private void getJsonPINF(String pin) {
        String json = HashmapTojson.getJson(
                new String[]{"id", "t", "r"},
                new Object[]{12345678, "EnterPIN", new BookingStarted(pin, 0, "Succeed")}
        );

        MLog.e("BookingStartedPIN:" + json);
        byte aa[] = new byte[0];
        try {
            aa = json.getBytes("UTF-8");
            MLog.e("BookingStartedaa" + aa.length);
            getaesBookingstartedpin(aa);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enJsonPINF(String t, String Response, int ErrCode, String ErrInf) {
        String json = HashmapTojson.getJson(
                new String[]{"id", "t", "r"},
                new Object[]{12345678, t, new BookingStarted(Response, ErrCode, ErrInf)}
        );

        MLog.e("enBookingStartedPIN:" + json);
        byte aa[] = new byte[0];
        try {
            aa = json.getBytes("UTF-8");
            MLog.e("enBookingStartedaa" + aa.length);
            getaesBookingstartedpin(aa);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getJsonPINFAuthFailed(String t, String Response) {
        String json = HashmapTojson.getJson(
                new String[]{"id", "t", "r"},
                new Object[]{12345678, t, new BookingStarted(Response, 0, "Succeed")}
        );

        MLog.e("BookingStartedPIN:" + json);
        byte aa[] = new byte[0];
        try {
            aa = json.getBytes("UTF-8");
            MLog.e("BookingStartedaa" + aa.length);
            getaesBookingstartedpin(aa);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getJsonPINFERR1(String t, String Response) {
        String json = HashmapTojson.getJson(
                new String[]{"id", "t", "r"},
                new Object[]{12345678, t, new BookingStarted(Response, 0, "Succeed")}
        );

        MLog.e("BookingStartedPIN:" + json);
        byte aa[] = new byte[0];
        try {
            aa = json.getBytes("UTF-8");
            MLog.e("BookingStartedaa" + aa.length);
            getaesBookingstartedpin(aa);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getaesBookingstartedpin(byte[] aa) {
        // String name="q";
        // String key0=getResources().getString(R.string.car_healtho);
        String key0 = String.valueOf(new SharedPHelpers(mContext, "rz" + new SharedPHelper(LongControlActivity.this).get("TSPVIN", "0").toString()).get(getResources().getString(R.string.SymmEnccc), ""));
        AES aes = new AES();
        byte[] enc = aes.encrypt(aa, key0.getBytes());
//        Log.e("tag","加密后的内容：" + new String(Hex.encode(enc)));
//        Log.e("tag","加密后的长度：" + enc.length);
//        Log.e("tag","加密后的内容base64：" + com.nevs.bluetooth.encrypt.Base64.encode(enc));


        //对AES加密后的内容进行SHAMAC256
        byte[] sha256 = Sha256Util.HmacSHA256(enc);
        // MLog.e("HMACK256:"+ com.nevs.bluetooth.encrypt.Base64.encode(sha256));
        //取前16个字节拼到enc后组成新的数组
        byte[] byte16 = new byte[16];
        for (int i = 0; i < 16; i++) {
            byte16[i] = sha256[i];
        }

        byte[] aeshe = new byte[enc.length + 16];

        int kk = 0;
        List<byte[]> list = new ArrayList<>();
        list.add(enc);
        list.add(byte16);
        for (int n = 0; n < list.size(); n++) {
            for (int m = 0; m < list.get(n).length; m++) {
                aeshe[kk] = list.get(n)[m];
                kk++;
            }
        }
        MLog.e("AES完整加密后的字节个数：" + aeshe.length);

        //分组后发送
        getBookingstaredpin(aeshe);
    }

    private void getBookingstaredpin(byte[] aeshe) {
        bytebookingstartedpin = null;
        //按120个分成一组
        bytebookingstartedpin = split_bytes(aeshe, 120); //120分包大小
        MLog.e("bytebookingstartedpin.length:分包个数：" + bytebookingstartedpin.length);

        MLog.e("\r\n\r\n==========================================\r\n");

        //组包
        initbookingstartedpin();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(150);
                    MLog.e("整个数据包：" + Base64.encode(listStartbooking.get(0)));
                    mBleService.sendOrders(listbookingstartedpin.get(0), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e("发送异常" + e);
                }

            }
        }).start();
    }

    private void initbookingstartedpin() {
        listbookingstartedpin.clear();
        byte[] byteSend = new byte[0];
        //vv一
        for (int i = 0; i < bytebookingstartedpin.length; i++) {
            byte[] byted = new byte[bytebookingstartedpin[i].length];
            byteSend = new byte[byted.length + 5];

            //计算效验和vv
            short sum = 0;
            for (int j = 0; j < bytebookingstartedpin[i].length; j++) {
                sum += bytebookingstartedpin[i][j];
            }
            //   short CRC= (short) ((i+1)+bytesRSplit[i].length+sum);
            short CRC = (short) ((i + 1) + bytebookingstartedpin[i].length + sum);

            if (i == bytebookingstartedpin.length - 1) {
                CRC = (short) ((-1) + bytebookingstartedpin[i].length + sum);
            }

            byteSend[0] = (byte) 0xFF;
            byteSend[1] = (byte) 0xA5;
            byteSend[2] = (byte) ((i + 1) & 0xFF);

            if (i == bytebookingstartedpin.length - 1) {
                byteSend[2] = (byte) 0xFF;
            }

            MLog.e("byteSend[2]" + byteSend[2]);
            byteSend[3] = (byte) (byted.length & 0xFF);
            for (int k = 4; k < byteSend.length - 1; k++) {
                byteSend[k] = bytebookingstartedpin[i][k - 4];
            }
            byteSend[byteSend.length - 1] = (byte) (getUint8(CRC) & 0xFF);
            // MLog.e("(byte) CRC："+(byte) CRC);
            listbookingstartedpin.add(byteSend);
        }
        //^^一
        MLog.e("listbookingstartedpinaes包的个数：" + listbookingstartedpin.size());
    }

    //最后一个环节发送PIN码成功后接收返回的消息    接收流程
    private void getCrcPINEND(byte[] value2) {
        //计算效验和vv
        short sum = 0;//有效数据位和
        for (int j = 4; j < value2.length - 1; j++) {
            sum += value2[j];
        }
        short CRCs = (short) (value2[2] + value2[3] + sum);
        byte CRCb = (byte) (getUint8(CRCs) & 0xFF);
        MLog.e("crcshort:" + CRC);
        if (CRCb == value2[value2.length - 1]) {
            MLog.e("效验成功");

            byte[] bytey = new byte[value2.length - 5];
            int ii = 0;
            for (int k = 4; k < value2.length - 1; k++) {
                bytey[ii] = value2[k];
                ii++;
            }
            listPinend.add(bytey);


            getAck(value2);
            if (value2[2] == -1) {
                MLog.e("输入PIN后接收的包的个数：" + listPinend.size());

                String json = getAESDe(listPinend);//AES解密

                MLog.e("----" + json);
                jsonJiePINEND(json);


            } else {

            }

        } else {
            MLog.e("效验失败");
        }
    }

    private void jsonJiePINEND(String json) {
//{"id":15,"t":"ReadyToStart"}
        String t = "";
        listPinend.clear();//清空接收集合以便下次接收
        try {
            JSONObject js = new JSONObject(json);
            t = js.getString("t");
            MLog.e("t STABOOK结束111@@@------------" + t);
//            if(t.equals("EnterPIN")){
//                enterPINDialog(this,false);
//            }

            if (t.equals("ReadyToStart")) {
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "9");
                getJsonPINFERR1("ReadyToStart", "Ok");
                MLog.e("startbooking整个流程走完啦，祝您好运@@@111");
            } else if (t.equals("StartBooking")) {  //{"id":12345678,"t":"StartBooking","r":{"ErrCode":0,"ErrInf":"Succeed"}}
                JSONObject r = js.getJSONObject("r");
                String ErrInf = r.getString("ErrInf");
                if (ErrInf.equals("Succeed")) {
                    MLog.e("startbooking整个流程走完啦，祝您好运@@@111112222");
                    isLonCon = false;
                    new SharedPHelpers(mContext, "rerz" + new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISDISCON, "0");//1为断开过,0为连接状态中
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(mContext, getResources().getString(R.string.n_startbookc));
                        }
                    });

                    new SharedPHelpers(mContext, "rz" + new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISSTARTBOOKING, "1");

                    // ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_unlocksuc));
                }
            }
            //PIN输入错误重新输入
            if (t.equals("WrongPIN")) {
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "9");
                enterPINDialogRE(mContext, false);
            } else if (t.equals("CorrectVehicle")) {
                //PIN码输入3次错误弹框询问是否继续
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "9");
                enterPINDialogERR1(mContext, false);
            } else if (t.equals("Disclamer")) {
                //取消重试后收到的消息
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "9");
                enterPINDialogERR2(mContext, false);
            } else if (t.equals("EnterPIN")) {
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "9");
                enterPINDialog(mContext, false);
            } else if (t.equals("KeyActivationFailed")) {
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "9");
                getJsonPINFERR1("KeyActivationFailed", "Ok");
            } else if (t.equals("AuthFailed")) {
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "9");
                getJsonPINFERR1("AuthFailed", "Ok");
            } else if (t.equals("Helpdesk")) {
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "9");
                MLog.e("if22  Helpdesk");
                getJsonPINFERR1("Helpdesk", "Ok");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //最后一个环节发送PIN码成功后接收返回的消息    接收流程2
    private void getCrcPINEND2(byte[] value2) {
        //计算效验和vv
        short sum = 0;//有效数据位和
        for (int j = 4; j < value2.length - 1; j++) {
            sum += value2[j];
        }
        short CRCs = (short) (value2[2] + value2[3] + sum);
        byte CRCb = (byte) (getUint8(CRCs) & 0xFF);
        MLog.e("crcshort:" + CRC);
        if (CRCb == value2[value2.length - 1]) {
            MLog.e("效验成功");

            byte[] bytey = new byte[value2.length - 5];
            int ii = 0;
            for (int k = 4; k < value2.length - 1; k++) {
                bytey[ii] = value2[k];
                ii++;
            }
            listPinend2.add(bytey);


            getAck(value2);
            if (value2[2] == -1) {
                MLog.e("2输入PIN后接收的包的个数：" + listPinend2.size());

                String json = getAESDe(listPinend2);//AES解密

                MLog.e("2----" + json);
                jsonJiePINEND2(json);


            } else {

            }

        } else {
            MLog.e("效验失败");
        }
    }

    private void jsonJiePINEND2(String json) {
//{"id":12345678,"t":"StartBooking","r":{"ErrCode":0,"ErrInf":"Succeed"}}

        MLog.e("jsonJiePINEND2");
        listPinend2.clear();

        try {
            JSONObject js = new JSONObject(json);
            String t = js.getString("t");
            if (t.equals("Helpdesk")) {
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "9");
                MLog.e("if22  Helpdesk");
                getJsonPINFERR1("Helpdesk", "Ok");
            } else {
                JSONObject r = js.getJSONObject("r");
                String ErrInf = r.getString("ErrInf");
                if (ErrInf.equals("Succeed")) {
                    MLog.e("startbooking整个流程走完啦，祝您好运@@@2222");
                    isLonCon = false;

                    new SharedPHelpers(mContext, "rz" + new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISSTARTBOOKING, "1");
                    new SharedPHelpers(mContext, "rerz" + new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISDISCON, "0");//1为断开过,0为连接状态中
                    // ActivityUtil.showToast(mContext,getResources().getString(R.string.toast_unlocksuc));

                    // Thread.sleep(150);
                    // unlock();//开始解锁车门

                } else {
                    MLog.e("startbooking整个流程走完啦，失败~~~2");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //22222222222222222222222222222222222222222222222222222222  end


    //33333333333333333333333333333333333333333333333333333333  start
    //UNLOCK
    //发送指令
    public void unlock() {
        listByteUnLock.clear();
        new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "11");

        /**
         * A数据源
         * */
        String json = HashmapTojson.getJson(
                new String[]{"id", "t"},
                new Object[]{12345678, "UnlockCar"}
        );

        MLog.e("UNLOCK:" + json);
        byte aa[] = new byte[0];
        try {
            aa = json.getBytes("UTF-8");
            MLog.e("UNLOCKaa" + aa.length);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        /**
         * B  AES加密，然后SHA256取前16拼在AES加密后的后面
         * */
        //String key0=getResources().getString(R.string.car_healtho);
        String key0 = String.valueOf(new SharedPHelpers(mContext, "rz" + new SharedPHelper(LongControlActivity.this).get("TSPVIN", "0").toString()).get(getResources().getString(R.string.SymmEnccc), ""));
        AES aes = new AES();
        byte[] enc = aes.encrypt(aa, key0.getBytes());
//        Log.e("tag","加密后的内容：" + new String(Hex.encode(enc)));
//        Log.e("tag","加密后的长度：" + enc.length);
//        Log.e("tag","加密后的内容base64：" + com.nevs.bluetooth.encrypt.Base64.encode(enc));

        //对AES加密后的内容进行SHAMAC256
        byte[] sha256 = Sha256Util.HmacSHA256(enc);
        // MLog.e("HMACK256:"+ com.nevs.bluetooth.encrypt.Base64.encode(sha256));
        //取前16个字节拼到enc后组成新的数组
        byte[] byte16 = new byte[16];
        for (int i = 0; i < 16; i++) {
            byte16[i] = sha256[i];
        }

        byte[] aeshe = new byte[enc.length + 16];

        int kk = 0;
        List<byte[]> list = new ArrayList<>();
        list.add(enc);
        list.add(byte16);
        for (int n = 0; n < list.size(); n++) {
            for (int m = 0; m < list.get(n).length; m++) {
                aeshe[kk] = list.get(n)[m];
                kk++;
            }
        }
        MLog.e("AES完整加密后的字节个数：" + aeshe.length);


        /**
         * C分组后发送
         * */
        //按120个分成一组
        byte[][] byteunLock = split_bytes(aeshe, 120);  //120分包大小
        MLog.e("bytestartbooking.length:分包个数：" + byteunLock.length);

        MLog.e("\r\n\r\n==========================================\r\n");

        //组包
        initCRCUnlock(byteunLock);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MLog.e("整个数据包：" + Base64.encode(listByteUnLock.get(0)));
                try {
                    mBleService.sendOrders(listByteUnLock.get(0), 0);
                } catch (Exception e) {
                    MLog.e("蓝牙为连接发送了unlock" + e);
                }

            }
        }).start();

    }

    private void getJsonPINFAuthFailed2(String t, String Response) {
        listByteUnLock.clear();
        String json = HashmapTojson.getJson(
                new String[]{"id", "t", "r"},
                new Object[]{12345678, t, new BookingStarted(Response, 0, "Succeed")}
        );

        MLog.e("BookingStartedPIN:" + json);


        byte aa[] = new byte[0];
        try {
            aa = json.getBytes("UTF-8");
            MLog.e("UNLOCKaa" + aa.length);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * B  AES加密，然后SHA256取前16拼在AES加密后的后面
         * */
        //String key0=getResources().getString(R.string.car_healtho);
        String key0 = String.valueOf(new SharedPHelpers(mContext, "rz" + new SharedPHelper(LongControlActivity.this).get("TSPVIN", "0").toString()).get(getResources().getString(R.string.SymmEnccc), ""));
        AES aes = new AES();
        byte[] enc = aes.encrypt(aa, key0.getBytes());
//        Log.e("tag","加密后的内容：" + new String(Hex.encode(enc)));
//        Log.e("tag","加密后的长度：" + enc.length);
//        Log.e("tag","加密后的内容base64：" + com.nevs.bluetooth.encrypt.Base64.encode(enc));

        //对AES加密后的内容进行SHAMAC256
        byte[] sha256 = Sha256Util.HmacSHA256(enc);
        // MLog.e("HMACK256:"+ com.nevs.bluetooth.encrypt.Base64.encode(sha256));
        //取前16个字节拼到enc后组成新的数组
        byte[] byte16 = new byte[16];
        for (int i = 0; i < 16; i++) {
            byte16[i] = sha256[i];
        }

        byte[] aeshe = new byte[enc.length + 16];

        int kk = 0;
        List<byte[]> list = new ArrayList<>();
        list.add(enc);
        list.add(byte16);
        for (int n = 0; n < list.size(); n++) {
            for (int m = 0; m < list.get(n).length; m++) {
                aeshe[kk] = list.get(n)[m];
                kk++;
            }
        }
        MLog.e("AES完整加密后的字节个数：" + aeshe.length);


        /**
         * C分组后发送
         * */
        //按120个分成一组
        byte[][] byteunLock = split_bytes(aeshe, 120);  //120分包大小
        MLog.e("bytestartbooking.length:分包个数：" + byteunLock.length);

        MLog.e("\r\n\r\n==========================================\r\n");

        //组包
        initCRCUnlock(byteunLock);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MLog.e("整个数据包：" + Base64.encode(listByteUnLock.get(0)));
                try {
                    mBleService.sendOrders(listByteUnLock.get(0), 0);
                } catch (Exception e) {
                    MLog.e("蓝牙为连接发送了unlock" + e);
                }

            }
        }).start();

    }

    public void initCRCUnlock(byte[][] bytestartbooking) {

        byte[] byteSend = new byte[0];
        //vv一
        for (int i = 0; i < bytestartbooking.length; i++) {
            byte[] byted = new byte[bytestartbooking[i].length];
            byteSend = new byte[byted.length + 5];

            //计算效验和vv
            short sum = 0;
            for (int j = 0; j < bytestartbooking[i].length; j++) {
                sum += bytestartbooking[i][j];
            }
            //   short CRC= (short) ((i+1)+bytesRSplit[i].length+sum);
            short CRC = (short) ((i + 1) + bytestartbooking[i].length + sum);

            if (i == bytestartbooking.length - 1) {
                CRC = (short) ((-1) + bytestartbooking[i].length + sum);
            }

            byteSend[0] = (byte) 0xFF;
            byteSend[1] = (byte) 0xA5;
            byteSend[2] = (byte) ((i + 1) & 0xFF);

            if (i == bytestartbooking.length - 1) {
                byteSend[2] = (byte) 0xFF;
            }

            MLog.e("byteSend[2]" + byteSend[2]);
            byteSend[3] = (byte) (byted.length & 0xFF);
            for (int k = 4; k < byteSend.length - 1; k++) {
                byteSend[k] = bytestartbooking[i][k - 4];
            }
            byteSend[byteSend.length - 1] = (byte) (getUint8(CRC) & 0xFF);
            // MLog.e("(byte) CRC："+(byte) CRC);
            listByteUnLock.add(byteSend);
        }
        //^^一
        MLog.e("listByteUnLock包的个数：" + listByteUnLock.size());
    }

    //接收流程
    private void getCrcUNLOCK(byte[] value2) {
        //计算效验和vv
        short sum = 0;//有效数据位和
        for (int j = 4; j < value2.length - 1; j++) {
            sum += value2[j];
        }
        short CRCs = (short) (value2[2] + value2[3] + sum);
        byte CRCb = (byte) (getUint8(CRCs) & 0xFF);
        MLog.e("crcshort:" + CRC);
        if (CRCb == value2[value2.length - 1]) {
            MLog.e("效验成功");

            byte[] bytey = new byte[value2.length - 5];
            int ii = 0;
            for (int k = 4; k < value2.length - 1; k++) {
                bytey[ii] = value2[k];
                ii++;
            }
            listUNLOCK.add(bytey);

            getAck(value2);
            if (value2[2] == -1) {
                MLog.e("getCrcUNLOCK接收的包的个数：" + listUNLOCK.size());

                String json = getAESDe(listUNLOCK);//AES解密

                MLog.e("----" + json);
                jsonJieUNLOCK(json);


            } else {

            }

        } else {
            MLog.e("效验失败");
        }
    }

    private void jsonJieUNLOCK(String json) {
        listUNLOCK.clear();
        String t = "";
        try {
//            JSONObject js=new JSONObject(json);
//            JSONObject r=js.getJSONObject("r");
//            String ErrInf=r.getString("ErrInf");
//            if(ErrInf.equals("Succeed")){
//                MLog.e("车门解锁成功，祝您好运@@@1");
//            }else {
//                MLog.e("车门解锁失败，从头再来~~~1");
//            }

            JSONObject js = new JSONObject(json);
            t = js.getString("t");
            MLog.e("tUNLOCK接收------------" + t);
            if (t.equals("AuthFailed")) {
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "11");
                ActivityUtil.showUiToast("AuthFailed");
                getJsonPINFAuthFailed2("AuthFailed", "Ok");

            } else if (t.equals("UnlockingFailed")) {
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "11");
                ActivityUtil.showUiToast("UnlockingFailed");
                getJsonPINFAuthFailed2("UnlockingFailed", "Ok");

            } else if (t.equals("KeyActivationFailed")) {
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "11");
                ActivityUtil.showUiToast("KeyActivationFailed");
                getJsonPINFAuthFailed2("KeyActivationFailed", "Ok");

            } else if (t.equals("ReadyToStart")) {
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "11");
                // ActivityUtil.showUiToast("ReadyToStart");
                getJsonPINFAuthFailed2("ReadyToStart", "Ok");

            } else if (t.equals("Helpdesk")) {
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "11");
                getJsonPINFAuthFailed2("Helpdesk", "Ok");

            } else if (t.equals("UnlockCar")) {//{"id":12345678,"t":"UnlockCar","r":{"ErrCode":0,"ErrInf":"Succeed"}}
                JSONObject r = js.getJSONObject("r");
                String ErrInf = r.getString("ErrInf");
                if (ErrInf.equals("Succeed")) {
                    MLog.e("车门解锁成功，祝您好运@@@11122222");
                    new SharedPHelpers(mContext, "rerz" + new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISDISCON, "0");//1为断开过,0为连接状态中
                    upViewc();
                } else {
                    MLog.e("车门解锁失败，从头再来~~~11112222");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //接收流程2
    private void getCrcUNLOCK2(byte[] value2) {
        //计算效验和vv
        short sum = 0;//有效数据位和
        for (int j = 4; j < value2.length - 1; j++) {
            sum += value2[j];
        }
        short CRCs = (short) (value2[2] + value2[3] + sum);
        byte CRCb = (byte) (getUint8(CRCs) & 0xFF);
        MLog.e("crcshort:" + CRC);
        if (CRCb == value2[value2.length - 1]) {
            MLog.e("效验成功");

            byte[] bytey = new byte[value2.length - 5];
            int ii = 0;
            for (int k = 4; k < value2.length - 1; k++) {
                bytey[ii] = value2[k];
                ii++;
            }
            listUNLOCK2.add(bytey);


            getAck(value2);
            if (value2[2] == -1) {
                MLog.e("getCrcUNLOCK2接收的包的个数：" + listUNLOCK2.size());

                String json = getAESDe(listUNLOCK2);//AES解密

                MLog.e("----" + json);
                jsonJieUNLOCK2(json);


            } else {

            }

        } else {
            MLog.e("效验失败");
        }
    }

    private void jsonJieUNLOCK2(String json) {
        // {"id":12345678,"t":"UnlockCar","r":{"ErrCode":0,"ErrInf":"Succeed"}}
        listUNLOCK2.clear();
        try {
            JSONObject js = new JSONObject(json);
            String t = js.getString("t");
            if (t.equals("Helpdesk")) {
                MLog.e("if32  Helpdesk");
                new SharedPHelpers(MyApp.getInstance(), "rz" + new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP, "11");
                getJsonPINFAuthFailed2("Helpdesk", "Ok");


            } else {
                JSONObject r = js.getJSONObject("r");
                String ErrInf = r.getString("ErrInf");
                if (ErrInf.equals("Succeed")) {
                    MLog.e("2车门解锁成功，祝您好运@@@2");
                    new SharedPHelpers(mContext, "rerz" + new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISDISCON, "0");//1为断开过,0为连接状态中
                    upViewc();
                } else {
                    MLog.e("2车门解锁失败，从头再来~~~22");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //33333333333333333333333333333333333333333333333333333333  end

    private void initConnect() {
        if (!mBluetoothAdapter.isEnabled() || mBluetoothAdapter == null) {
            // if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            MLog.e("询问打开蓝牙开始权限请求");
            boolean result = mBluetoothAdapter.enable();//强制打开蓝牙，需要BLUETOOTH_ADMIN权限
            if (result) {
                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (mBluetoothAdapter.isEnabled()) {
                            MLog.e("蓝牙开启成功");
                            isLonCon = true;
                            startConnect();
                        } else {
                            MLog.e("蓝牙开启中");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mBluetoothAdapter.isEnabled()) {
                                        MLog.e("蓝牙开启成功");
                                        isLonCon = true;
                                        startConnect();
                                    }
                                }
                            }, 2000);
                        }
                    }
                };
                handler.postDelayed(runnable, 2000);

            } else {
                Toast.makeText(this, getResources().getString(R.string.hintpermissionBlue), Toast.LENGTH_SHORT).show();
            }
            //强制打开蓝牙
//            Intent enableBtIntent = new Intent(
//                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, 1);

//        }else if(!mBluetoothAdapter.isEnabled() || mBluetoothAdapter == null){
//            MLog.e("开始连接");
//            startConnect();
//        }
        } else {
            MLog.e("权限都有开始连接蓝牙");
            startConnect();
        }
    }

    private void startConnect() {//开始连接蓝牙
        //  registerReceiver(blueBrocastRecever, BlueBrocastRecever.makeGattUpdateIntentFilter());//8

        if (mBleService != null) {
            boolean result = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                result = mBleService.connect(mAddress);
            }
            if (result) {
                // connectFlag = CONNECTED;
            } else {
                // connectFlag = DISCONNECTED;
            }
            Log.e("tag", "连接结果" + result);
        } else {
            Log.e("tag", "mBluetoothLeService为空");
        }
    }

    /**
     * 权限申请
     */
    private void initPession() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //做一些处理
            //初始化并发起权限申请
            mPermissionHelper = new PermissionHelper(this, this);
            mPermissionHelper.requestPermissions();

        } else {
            //在版本低于此的时候，做一些处理
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public int getPermissionsRequestCode() {
        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。
        return 10000;
    }


    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

    }

    @Override
    public void requestPermissionsSuccess() {
        //权限请求用户已经全部允许
        initViews();

    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
        // finish();
        // ActivityUtil.showToast(mContext,getResources().getString(R.string.about));
        MLog.e("权限请求不被用户允许");
    }


    private void initViews() {
        jniHelper = new JniHelper();
        //已经拥有所需权限，可以放心操作任何东西了
        //初始化蓝牙
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //mAddress ="0A:5B:00:6B:3C:59";
        //mAddress="01:A5:A5:A5:A5:A5";
        //mAddress="74:AC:5F:76:7D:D5";
        mAddress = (new SharedPHelpers(mContext, Constant.CSRHEAD + new SharedPHelper(mContext).get("TSPVIN", "0")).get("bleAddress", "") + "").toUpperCase();
        mAddress = MyUtils.toBacks(mAddress);
        MLog.e("需要连的蓝牙地址：" + mAddress);
        mName = "NEVS Sample";

        initService();

    }

    @Override
    public void getBlueReceiver(int i, int id, byte[] bytes) {
        switch (i) {
//            case 33:
//                MLog.e("LonconA 33");
//                mBleService.sendOrders(BlueMessUtil.listBytesRenz.get(id), id);
//                break;
//            case 34:
//                MLog.e("LonconA 34");
//                BlueMessUtil.getCrcRenzOnej(bytes, mBleService, mContext, jniHelper);
//                break;
            case 21:
                mBleService.sendOrders(listStartbooking.get(bytes[0]), bytes[0]);
                break;
            case 22:
                getCrcStartbookingj(bytes);
                break;
            case 23:
                mBleService.sendOrders(listbookingstarted.get(bytes[0]), bytes[0]);
                break;
            case 24:
                getCrc8(bytes);
                break;
            case 25:
                mBleService.sendOrders(listbookingstartedpin.get(bytes[0]), bytes[0]);
                break;
            case 26:
                getCrcPINEND(bytes);
                break;
            case 27:
                getCrcPINEND2(bytes);
                break;
            case 28:
                mBleService.sendOrders(listByteUnLock.get(bytes[0]), bytes[0]);
                break;
            case 29:
                getCrcUNLOCK(bytes);
                break;
            case 30:
                getCrcUNLOCK2(bytes);
                break;
            case 31:

                break;
        }
    }

    protected void onResume() {
        super.onResume();
//        if (!mBluetoothAdapter.isEnabled() || mBluetoothAdapter == null) {
//            //强制打开蓝牙
////            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//
//            MLog.e("onResume开始权限请求");
//            mBluetoothAdapter.enable();  //强制打开蓝牙，需要BLUETOOTH_ADMIN权限
//
//
//        }
//        //7
//        registerReceiver(blueBrocastRecever, BlueBrocastRecever.makeGattUpdateIntentFilter());//8
//
//        if (mBleService != null) {
//            final boolean result = mBleService.connect(mAddress);
//            if (result) {
//               // connectFlag = CONNECTED;
//            } else {
//               // connectFlag = DISCONNECTED;
//            }
//            Log.e("tag", "连接结果" + result);
//        } else {
//            Log.e("tag", "mBluetoothLeService为空");
//        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        try {
//            unregisterReceiver(mGattUpdateReceiver);
//        }catch (Exception e){
//
//        }
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(blueBrocastRecever);
        } catch (Exception e) {
            MLog.e("onDestroy longA yic");
        }
        try {
            String renIsSucc = new SharedPHelpers(mContext, "rz" + new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.ISRENZHENG, "0") + "";//1为认证成功
            if (renIsSucc.equals("0")) {
                MLog.e("此车认证未成功或等待中");
                return;
            }
        } catch (Exception e) {
            MLog.e("onDestroy longA1");
        }
        try {
            mBleService = null;
        } catch (Exception e) {
            MLog.e("onDestroy longA2");
        }
        try {
            this.getApplicationContext().unbindService(mServiceConnection);
        } catch (Exception e) {
            MLog.e("onDestroy longA3");
        }
        try {
            invalidateOptionsMenu();
        } catch (Exception e) {
            MLog.e("onDestroy longA4");
        }


    }

    @Override
    public void startBooking() {
        startbooking();
    }

    @Override
    public void unclok() {
        unlock();
    }

    @Override
    public void getRzState() {//获取是否认证成功的回调
        initBleConnectAll();
    }

    private void myFinish() {
        //将计算的结果回传给第一个Activity
        Intent reReturnIntent = new Intent(this, MainActivity.class);
        setResult(101, reReturnIntent);
        //退出第二个Activity
        this.finish();
    }

//    @Override
//    public void reDownZS() {
//        myFinish();
//    }


    private void getTsp2(final String vin) {//new SharedPHelper(mContext).get("TSPVIN", "0") + ""
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
                vin,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(LongControlActivity.this);
                        listState.addAll((Collection<?>) obj);
                        String vehiclestatus = String.valueOf(listState.get(4));
                        MLog.e("vehiclestatus:" + vehiclestatus);
                        //车辆状态返回值0:Stopped 1:Running  2:Charging 3:Unkown
                     if(vehiclestatus.equals("1")){
                             ActivityUtil.showToast(mContext, getResources().getString(R.string.n_running));

                     }else {
                         startOpen();
                     }

                    }

                    @Override
                    public void onFial(String str) {
                        xRefreshView.stopRefresh();
                        DialogUtils.hidding(LongControlActivity.this);
                        startOpen();
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


    private void startOpen(){
        //第一次点击的时候走startbooking按OK后就可以解锁车门,成功以后，再以后点击都直接走UNLOCK流程
        MLog.e("blu标志："+new SharedPHelpers(mContext, "rerz" + new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.ISDISCONNING, "0"));

        if (new SharedPHelpers(mContext, "rz" + new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.ISRENZHENG, "0").equals("0")) {//是否认证
            ActivityUtil.showToast(mContext, getResources().getString(R.string.unappove));
            MLog.e("认证未通过");
        } else {
            if (new SharedPHelpers(mContext, "rerz" + new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.ISDISCONNING, "0").equals("0")) {//0为进行中
                ActivityUtil.showToast(mContext, getResources().getString(R.string.reconnecrsuccing));
                MLog.e("重连中");
                return;
            }

            if (mBleService == null) {
                MLog.e("mBleService == null");
                return;
            }
            if (mBleService.mConnectionState == 0 || mBleService.mConnectionState == 1) {
                MLog.e("蓝牙连接中");
                ToastUtil.showToast(mContext, getResources().getString(R.string.bluethoothcon));
                return;
            }

            if (new SharedPHelpers(mContext, "rz" + new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.ISSTARTBOOKING, "0").equals("0")) {//是否走过startbooking 0没有走  0fffff
                //是否走重连
//                        String isDisCon = new SharedPHelpers(mContext, "rerz" + new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.ISDISCON, "1") + "";//1为断开过
//                        if (isDisCon.equals("0")) {
//                            ToastUtil.showToast(mContext, "startbooking");
//                            MLog.e("startbooking()流程");
//                            if (isLonCon) {
//                                BlueMessUtil.renOnef(jniHelper, mContext, mBleService);
//                            } else {
//                                startbooking();
//                            }
//
//
//                        } else {
//                            ToastUtil.showToast(mContext, "startbooking");
//                          if(new SharedPHelpers(mContext,"rz"+new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.ISRECONINGSUC,"0").equals("0")){
//                              MLog.e("startbooking()重连流程");
//                              BlueMessUtil.renOnef(jniHelper, mContext, mBleService);
//                          }else {
//                              startbooking();
//                          }
//
//
//                        }

                //  ToastUtil.showToast(mContext, "startbooking");
                MLog.e("startbooking()流程");
                startbooking();

            } else {
                ToastUtil.showToast(mContext, "解锁中");
                MLog.e("unlock()流程");
                unlock();


// //                           new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.BLUESTEP,"11");

                //是否走重连
                //是否走重连
//                        String isDisCon = new SharedPHelpers(mContext, "rerz" + new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.ISDISCON, "1") + "";//1为断开过
//                        if (isDisCon.equals("0")) {
//                            ToastUtil.showToast(mContext, "解锁中");
//                            MLog.e("unlock()流程");
//                            if (isLonCon) {
//                                BlueMessUtil.renOnef(jniHelper, mContext, mBleService);
//                            } else {
//                                unlock();
//                            }
//                        } else {
//                            ToastUtil.showToast(mContext, "解锁中");
//                            if(new SharedPHelpers(mContext,"rz"+new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.ISRECONINGSUC,"0").equals("0")){
//                                MLog.e("unlock()重连流程");
//                                BlueMessUtil.renOnef(jniHelper, mContext, mBleService);
//                            }else {
//                                unlock();
//                            }
//
//
//                        }

            }


        }

    }
}

