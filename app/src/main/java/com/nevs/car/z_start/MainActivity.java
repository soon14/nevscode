package com.nevs.car.z_start;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lsxiao.apollo.core.annotations.Receive;
import com.nevs.car.R;
import com.nevs.car.activity.BindCarActivity;
import com.nevs.car.constant.Constant;
import com.nevs.car.fragment.CarFragment;
import com.nevs.car.fragment.GuideFragment;
import com.nevs.car.fragment.MyFragment;
import com.nevs.car.fragment.ServeFragment;
import com.nevs.car.jnihelp.BleService;
import com.nevs.car.jnihelp.BlueBrocastRecever;
import com.nevs.car.jnihelp.BlueListener;
import com.nevs.car.jnihelp.BlueMessUtil;
import com.nevs.car.jnihelp.CSRListener;
import com.nevs.car.jnihelp.DigitalUtils;
import com.nevs.car.jnihelp.JniHelper;
import com.nevs.car.jnihelp.LogBlueLin;
import com.nevs.car.jnihelp.RzStateListener;
import com.nevs.car.jnihelp.modle.DigitalKeyBean;
import com.nevs.car.jnihelp.modle.QBean;
import com.nevs.car.jnihelp.modle.Qstatbooking;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelpers;
import com.nevs.car.tools.ToastUtil;
import com.nevs.car.tools.encrypt.AES;
import com.nevs.car.tools.encrypt.Sha256Util;
import com.nevs.car.tools.interfaces.PermissionInterface;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.InterfaceUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.PermissionHelper;
import com.nevs.car.tools.view.safecode.KeyBoardDialog;
import com.nevs.car.tools.view.safecode.LoadingDialog;
import com.nevs.car.tools.view.safecode.PayPasswordView;
import com.nevs.car.tools.view.safecode.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

import static com.nevs.car.jnihelp.BlueMessUtil.getAESDe;
import static com.nevs.car.jnihelp.BlueMessUtil.getAck;
import static com.nevs.car.jnihelp.BlueMessUtil.getUint8;
import static com.nevs.car.tools.util.MyUtils.split_bytes;
import static org.bouncycastle.asn1.gnu.GNUObjectIdentifiers.CRC;

public class MainActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionInterface,BlueListener,CSRListener,CarFragment.CloseBleListener, InterfaceUtils.CSRListeners {
    @BindView(R.id.rbCar)
    RadioButton rbCar;
    @BindView(R.id.rbGuide)
    RadioButton rbGuide;
    @BindView(R.id.rbServe)
    RadioButton rbServe;
    @BindView(R.id.rbMy)
    RadioButton rbMy;
    @BindView(R.id.image_guide)
    ImageView imageGuide;
    private CarFragment carFragment;
    private GuideFragment guideFragment;
    private ServeFragment serveFragment;
    private MyFragment myFragment;
    private Boolean isExit = false;
    private String isHaveCar = "0";//0表示没有车
    private KeyBoardDialog keyboard = null;
    protected LoadingDialog loadingDialog;
    private String total = "";
    private int count = 0;//记录安全码输入正确的次数两次设置成功
    private Context context = MainActivity.this;
    //private SharedPHelpers sharedPHelpers;
    private BaseAnimatorSet mBasIns = new BounceTopEnter();
    private BaseAnimatorSet mBasOuts = new SlideBottomExit();
    private  int guide=2;
    private boolean getLanguage=true;
    private NormalDialog dialog=null;
    //蓝牙
    private PermissionHelper mPermissionHelper;
    private BleService mBleService;
    private Vibrator vibrator;
    private String mAddress="";
    private String mName="";
    private BluetoothAdapter mBluetoothAdapter;
    private JniHelper jniHelper;
    String base64="";
    private Intent gattServiceIntent=null;
    private BlueBrocastRecever blueBrocastRecever;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 530;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();
    private List<byte[]> listBytes=new ArrayList();
    private List<byte[]> listBytesRenz=new ArrayList();
    private List<byte[]> listBytesRenzSplit=new ArrayList();//RSA分段加密后保存
    private List<byte[]> listrenzSplit=new ArrayList();//AES分段加密后保存
    private byte[][] bytesR=null;
    // private byte[][] bytesRSplit=null;//RSA分段加密合并后再分割
    private byte[][] bytesrenzSplit=null;//AES分段加密合并后再分割
    private List<byte[]> listAccept=new ArrayList<>();
    private List<byte[]> listAcceptdersa=new ArrayList<>();//RSA解密后
    private List<byte[]> listAccept2=new ArrayList<>();//认证2接收有序
    public static long startrenzheng;//认证开始时间戳
    public static RzStateListener csrListener1;
    public static void setRzListener(RzStateListener csrListener){
        csrListener1=csrListener;
    }
    public static void toSend(){
        if(csrListener1!=null){
            csrListener1.getRzState();
        }
    }
    public static LogBlueLin callback2;
    public static void setLog(LogBlueLin blueListener){
        callback2=blueListener;
    }
    public static void toLog(int i,String str){
        if(callback2!=null) {
            callback2.getLog(i,str);
        }
    }
    private final ServiceConnection mServiceConnection = new ServiceConnection() {//2

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBleService = ((BleService.LocalBinder) service)
                    .getService();
            if((new SharedPHelper(MyApp.getInstance()).get(Constant.BLUECONSTATE,"0")+"").equals("0")) {

                if (!mBleService.initialize()) {//3
                    Log.e("tag", "Unable to initialize Bluetooth");
                    finish();
                } else {
                    Log.e("tag", "能初始化");
                }


                // 自动连接to the device upon successful start-up
                // 初始化.
                // mBleService.connect(mAddress);//4
                initConnect();

            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rbServe.setText(R.string.tab_car2);
                rbCar.setText(R.string.tab_car2);
        rbGuide.setText(R.string.tab_guide);
                rbMy.setText(R.string.tab_my);

    }

    @Override
    public int getContentViewResId() {
        //绑定布局
        return R.layout.activity_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
       // sharedPHelpers = new SharedPHelpers(mContext,"c"+new SharedPHelper(mContext).get(Constant.LOGINNAME,""));
       // new SharedPHelper(mContext).put(Constant.ISCONFORM,"0");
        //初始化
        total = getResources().getString(R.string.toast_setpin);
        initFragment();//默认加载CarFragment
        //hintBindCar();//车辆绑定提示
        dialog = new NormalDialog(context);
        initIntentService();

      //  MLog.e("dddddd\n"+new SharedPHelpers(mContext,Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.TSPRXmobileCertificateContent,"")+"");
        //蓝牙
        new SharedPHelper(MyApp.getInstance()).put(Constant.BLUECONSTATE,"0");
        new SharedPHelpers(MyApp.getInstance(),"rerz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.ISDISCON,"1");//1为断开过,0为连接状态中
        blueBrocastRecever=BlueBrocastRecever.getInstance();
        DigitalUtils.setCsrListener(this);
        BlueBrocastRecever.setCallback(this);
        initBle();
        registerReceiver(blueBrocastRecever, BlueBrocastRecever.makeGattUpdateIntentFilterM());//8cc430
//77        mAddress=(new SharedPHelpers(mContext,Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN","0")).get("bleAddress", "")+"").toUpperCase();
//        if(!mAddress.equals("")) {
//            initPession();//初始化权限
//        }

        CarFragment.setgotoClose(this);
        InterfaceUtils.setCsrListeners(this);

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if((new SharedPHelper(mContext).get(Constant.ISCONFORM,"0")+"").equals("1")){
//
//                hintBindCar();//车辆绑定提示
//
//        }
//
//    }

    private void initGuide() {
        imageGuide.setVisibility(View.VISIBLE);
        imageGuide.setBackgroundResource(R.mipmap.qhcl1);
    }

    private void initGuideEnglish() {
        imageGuide.setVisibility(View.VISIBLE);
        imageGuide.setBackgroundResource(R.mipmap.qhe1);
    }
    private void startGuide() {

        switch (guide){
            case 2:
                imageGuide.setBackgroundResource(R.mipmap.qhcl2);
                break;
            case 3:
                imageGuide.setBackgroundResource(R.mipmap.qhcl3);
                break;
            case 4:
                imageGuide.setBackgroundResource(R.mipmap.qlch4);
                break;
            case 5:
                imageGuide.setBackgroundResource(R.mipmap.qlch5);
                break;
            case 6:
                imageGuide.setBackgroundResource(R.mipmap.qhcl6);
                break;

        }


    }
    private void startGuideEnglish() {

        switch (guide){
            case 2:
                imageGuide.setBackgroundResource(R.mipmap.qhe2);
                break;
            case 3:
                imageGuide.setBackgroundResource(R.mipmap.qhe3);
                break;
            case 4:
                imageGuide.setBackgroundResource(R.mipmap.qhe4);
                break;
            case 5:
                imageGuide.setBackgroundResource(R.mipmap.qhe5);
                break;
            case 6:
                imageGuide.setBackgroundResource(R.mipmap.qhe6);
                break;

        }


    }
    private void initIntentService() {//服务预约界面跳转过来到服务页面
        if (getIntent().getStringExtra("isservice") != null) {
            showServeFragment();
        }

    }

    private void initFragment() {
        showCarFragment();
    }

    @Receive("isbinddialog")
    public void onEvent(String message) {
        if (message.equals("pin")) {
            MLog.e("是否设置PIN");
            pinDialog();//是否设置PIN
        }else {
            MLog.e("车辆绑定提示");
            isHaveCar = "0";
            hintBindCar();//车辆绑定提示
        }
    }


    private void hintBindCar() {
        //isHaveCar= String.valueOf(new SharedPHelper(MainActivity.this).get("ishavecar","0"));
        //isHaveCar="1";//模拟有车，真实情况要调接口后缓存获取
        if (isHaveCar.equals("0")) {//0没有车
            //DialogUtils.NormalDialogStyleTwo(MainActivity.this,false);
            if(dialog!=null) {
                NormalDialogStyleTwo(MainActivity.this, false, MainActivity.this);
            }
        } else {
            pinDialog();//是否设置PIN
        }


    }

    private void pinDialog() {
        String name="c"+new SharedPHelper(mContext).get(Constant.LOGINNAME,"");
       String isHavePin = String.valueOf(new SharedPHelpers(mContext,name).get("pin","abcdef"));
        if(isHavePin.equals("abcdef")){
            MLog.e("没有pin");
        }else {
            MLog.e("有pin");
        }
        if (isHavePin.equals("abcdef")) {//没有pin就显示
            if (keyboard == null) {
                keyboard = new KeyBoardDialog((Activity) context, getDecorViewDialog());
                keyboard.show();
            }
        }
    }


    @OnClick({R.id.rbCar, R.id.rbGuide, R.id.rbServe, R.id.rbMy,R.id.image_guide})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbCar:
                showCarFragment();
                break;
            case R.id.rbGuide:
                showGuideFragment();
                break;
            case R.id.rbServe:
                showServeFragment();
                break;
            case R.id.rbMy:
                showMyFragment();
                break;
            case R.id.image_guide:
                if(guide==7){
                    imageGuide.setVisibility(View.GONE);
                    guide=2;
                }else {
                    if(getLanguage){
                        startGuide();
                    }else {
                        startGuideEnglish();
                    }
                    guide++;
                }
                break;
        }
    }


    private void showCarFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(fragmentTransaction);
        if (carFragment == null) {
            carFragment = CarFragment.newInstance();
            fragmentTransaction.add(R.id.fragments, carFragment);
        }
        commitShowFragment(fragmentTransaction, carFragment);
    }

    public void showGuideFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(fragmentTransaction);
        if (guideFragment == null) {
            guideFragment = GuideFragment.newInstance();
            fragmentTransaction.add(R.id.fragments, guideFragment);
        }
        commitShowFragment(fragmentTransaction, guideFragment);
    }

    public void showServeFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        hideAllFragment(fragmentTransaction);
        if (serveFragment == null) {
            serveFragment = ServeFragment.newInstance();
            fragmentTransaction.add(R.id.fragments, serveFragment);
        }
        commitShowFragment(fragmentTransaction, serveFragment);
    }

    public void showMyFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(fragmentTransaction);
        if (myFragment == null) {
            myFragment = MyFragment.newInstance();
            fragmentTransaction.add(R.id.fragments, myFragment);
        }
        commitShowFragment(fragmentTransaction, myFragment);
    }

    public void commitShowFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    public void hideAllFragment(FragmentTransaction fragmentTransaction) {
        hideFragment(fragmentTransaction, carFragment);
        hideFragment(fragmentTransaction, guideFragment);
        hideFragment(fragmentTransaction, serveFragment);
        hideFragment(fragmentTransaction, myFragment);
    }

    private void hideFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        if (fragment != null) {
            fragmentTransaction.hide(fragment);
        }
    }

    @Override//监听BACK键，连续点击两次退出程序
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (isExit == false) {
                    isExit = true;
                    Toast.makeText(this, getResources().getString(R.string.reback), Toast.LENGTH_SHORT).show();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isExit = false;
                        }
                    }, 1500);
                } else {
                    finish();
                }

                break;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
// 通过 onActivityResult的方法获取 扫描回来的 值   扫一扫回调
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MLog.e("1跳转返回");
        if (requestCode == 801 && resultCode == 901) {
            String intentvin = data.getStringExtra("intentvin");
            MLog.e("跳转返回" + intentvin);

            Intent intent = new Intent("MAINACTIVITY.INITENT.VIN");
            intent.putExtra("intentvin", intentvin);
            //也可以像注释这样写
            sendBroadcast(intent);//发送标准广播

        }else if(requestCode == 1001 && resultCode == 1002){
            Intent intent = new Intent("MAINACTIVITY.INITENT.USER");
            //也可以像注释这样写
            sendBroadcast(intent);//发送标准广播
        }else if(requestCode == 3005 && resultCode == 3006){
            hintBindCar();
        }

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
               // Toast.makeText(this, "蓝牙已经开启", Toast.LENGTH_SHORT).show();
                startConnect();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getResources().getString(R.string.hintpermissionBlue), Toast.LENGTH_SHORT).show();
              //  finish();

            }
        }

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, getResources().getString(R.string.toast_empty), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.toast_zsuc), Toast.LENGTH_LONG).show();
                // ScanResult 为 获取到的字符串
                String ScanResult = intentResult.getContents();
                Toast.makeText(this, ScanResult, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }


    protected View getDecorViewDialog() {

        //0表示隐藏取消按钮
        return PayPasswordView.getInstance(0, total, context, new PayPasswordView.OnPayListener() {

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
                        if (count == 0) {
                            if (password.length() == 6) {
                                //count++;
                                count=1;
                                new SharedPHelpers(mContext,"c"+new SharedPHelper(mContext).get(Constant.LOGINNAME,"")).put("pinisone", password);
                                total = getResources().getString(R.string.toast_confirmpin);
                                keyboard = new KeyBoardDialog(MainActivity.this, getDecorViewDialog());
                                keyboard.show();

                            }
                        } else if (count == 1) {
                            if (password.equals(String.valueOf(new SharedPHelpers(mContext,"c"+new SharedPHelper(mContext).get(Constant.LOGINNAME,"")).get("pinisone", "abcdef")))) {
                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pinsuccess));
                                new SharedPHelpers(mContext,"c"+new SharedPHelper(mContext).get(Constant.LOGINNAME,"")).put("pin", password);
                                new SharedPHelpers(mContext,"c"+new SharedPHelper(mContext).get(Constant.LOGINNAME,"")).put("pinisone", "abcdef");
                                startAlarm();
                                count = 0;
                                isGuide();
                                new SharedPHelper(mContext).put(Constant.SAFETY,System.currentTimeMillis());

                            } else {
//                                count++;
//                                keyboard = new KeyBoardDialog(MainActivity.this, getDecorViewDialog());
//                                keyboard.show();
//                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pininout));
                                total=getResources().getString(R.string.toast_setpin);
                                keyboard = new KeyBoardDialog(MainActivity.this, getDecorViewDialog());
                                keyboard.show();
                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pininout));
                                count=0;
                            }

                        } else {//count>=2时的情况
                            if (password.equals(String.valueOf(new SharedPHelpers(mContext,"c"+new SharedPHelper(mContext).get(Constant.LOGINNAME,"")).get("pinisone", "abcdef")))) {
                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pinsuccess));
                                new SharedPHelpers(mContext,"c"+new SharedPHelper(mContext).get(Constant.LOGINNAME,"")).put("pin", password);
                                new SharedPHelpers(mContext,"c"+new SharedPHelper(mContext).get(Constant.LOGINNAME,"")).put("pinisone", "abcdef");
                                startAlarm();
                                count = 0;
                            } else {
                                keyboard = new KeyBoardDialog(MainActivity.this, getDecorViewDialog());
                                keyboard.show();
                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pininout));
                            }
                        }
                    }


                }, 500);//500

            }

            @Override
            public void onCancelPay() {
                // TODO Auto-generated method stub
                if(null!=keyboard&&keyboard.isShowing()) {
                    keyboard.dismiss();
                    keyboard = null;
                }
                count = 0;
                ToastUtils.showShortToast(context, "");
            }
        }).getView();
    }



    private void startAlarm() {
        MLog.e("开启计时服务");
        new SharedPHelper(mContext).put(Constant.PINISKILL, false);
        Intent i = new Intent(mContext, LongRunningService.class);
        mContext.startService(i);
    }

    public void initProgressDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context, R.style.loading_dialogone);
            loadingDialog.setText(getResources().getString(R.string.loading));
        }
        if (!MainActivity.this.isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog = new LoadingDialog(context, R.style.loading_dialogone);
            loadingDialog.setText(getResources().getString(R.string.loading));
            loadingDialog.show();
        }
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissProgressDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }


    public void NormalDialogStyleTwo(final Context context, boolean flag, final Activity activity) {
       // final NormalDialog dialog = new NormalDialog(context);
        dialog.content(context.getResources().getString(R.string.main_bind_dialod))//
                .style(NormalDialog.STYLE_TWO)//
                .btnNum(2)
                .btnText(context.getResources().getString(R.string.cancle),context.getResources().getString(R.string.for_confirm))
                .titleTextSize(18)//
                .titleTextColor(context.getResources().getColor(R.color.dialog_title))
                .title(context.getResources().getString(R.string.dialog_title))
                .contentTextColor(context.getResources().getColor(R.color.main_textss))
                .contentTextSize(16)
                .showAnim(mBasIns)//
                .dismissAnim(mBasOuts)//
                .show();



        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        new SharedPHelper(context).put(Constant.ISCONFORM, "0");
                        rbServe.post(new Runnable(){
                            @Override
                            public void run() {
                                rbServe.performClick();
                            }
                        });
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        new SharedPHelper(context).put(Constant.ISCONFORM, "1");
                        // context.startActivity(new Intent(context,AddBindGetActivity.class));
                        startActivityForResult(new Intent(context, BindCarActivity.class).putExtra("dialogtoBind","dialogtoBind"),3005);
                       //activity.startActivityForResult(new Intent(context, BindCarActivity.class), 3001);
                    }
                });
        dialog.setCanceledOnTouchOutside(flag);// 设置点击屏幕Dialog不消失时为false
        dialog.setCancelable(false);

    }

    private void isGuide() {
         getLanguage= MyUtils.getLanguage(mContext);
        if(SharedPHelper.get(Constant.ISGUIDEFIRST,"0").toString().equals("0")) {
            if(getLanguage){
                initGuide();//发送引导页指令
            }else {
               // initGuideEnglish();
                initGuide();
            }

        }
        SharedPHelper.put(Constant.ISGUIDEFIRST,"1");
    }

    //蓝牙

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

    //1111111111111111111111111111111111111111111111111111111start
    //认证流程
    private  void renOnef() {//认证第一步的发送指令
        new SharedPHelpers(mContext,"rz"+new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.BLUESTEP,"1");
        /**
         * A,数据源
         */
        String mdCer="-----BEGIN CERTIFICATE-----\n" +
                "MIIGnDCCBYSgAwIBAgIBDDANBgkqhkiG9w0BAQsFADB7MQswCQYDVQQGEwJDTjEL\n" +
                "MAkGA1UECAwCR0QxCzAJBgNVBAcMAkhaMQswCQYDVQQKDAJTVjEMMAoGA1UECwwD\n" +
                "SURBMQ8wDQYDVQQDDAZqYXNwZXIxJjAkBgkqhkiG9w0BCQEWF3poYW5nempAc29s\n" +
                "b21vLWluZm8uY29tMB4XDTE4MDkyMTEwMjU1NFoXDTE5MDkyMTEwMjU1NFowYjEL\n" +
                "MAkGA1UEBhMCQ04xCzAJBgNVBAgMAkdEMQswCQYDVQQKDAJTVjEMMAoGA1UECwwD\n" +
                "SURBMQ8wDQYDVQQDDAZqYXNwZXIxGjAYBgkqhkiG9w0BCQEWCzEyM0AxNjMuY29t\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn8/F1qdWPlmaqSWuyeCN\n" +
                "klgfgnPmDEJLpZLZSLu11k31bM1HkNtZY/zogsIZeG8PygEtKh3ihdE6dPP5UZxR\n" +
                "CS2cY5kD46y+33soF65oQmSD9AhImOPKagb7W46S1hhwhwiEUu8LO8LX1FcDGw+o\n" +
                "JiusRAB0ZeAe0fiLrsYhcNrd9fxc5dgLXeFwTatCTSKGS4wSCSGrjR4mWQwSOsdS\n" +
                "xF2JHueNQuwKzhfd6CYCVdor1OjXnpqCy4r3ZAglr3IltaJi+FkfM9YBkciWMJHM\n" +
                "RAcWIHk/PC7e+qJWCFNF4K/2QhpnF8N+VFO57+ZBXEKEvJE+q/Tob0Rt1hhW+i3w\n" +
                "6QIDAQABo4IDQjCCAz4wJQYIKgMEBQGD/3oEGQwXIGJsZSBtYWMgYWRkclttYWMg\n" +
                "YWRkcl0wLAYIKgMEBQGD/3sEIAweIHZlaGljbGUgaWRbQUFBQUFBQUFBQTEyMzQ1\n" +
                "NjddMCIGCCoDBAUBg/98BBYMFCB1c3IgbmFtZVt4aWFvIG1pbmddMC4GCCoDBAUB\n" +
                "g/99BCIMICBzdGFydCB0aW1lWzIwMTgtMDUtMDcgMTU6MDA6MDBdMCwGCCoDBAUB\n" +
                "g/9+BCAMHiBlbmQgdGltZVsyMDE5LTA1LTA3IDE1OjAwOjAwXTAfBggqAwQFAYP/\n" +
                "fwQTDBEgcGluIGNvZGVbMjM0NTY3XTCCAeYGCCoDBAUBhIAABIIB2AyCAdQgdGJv\n" +
                "eCBwdWJsaWMga2V5Wy0tLS0tQkVHSU4gUFVCTElDIEtFWS0tLS0tCk1JSUJJakFO\n" +
                "QmdrcWhraUc5dzBCQVFFRkFBT0NBUThBTUlJQkNnS0NBUUVBcjVXMWJ3WGJKRVVn\n" +
                "UHg1UmZFVkMKd2RtdEo1amJHQ2FPcXFoeXdJQTR5MFF3U0NWeU5GRGNuRFN6TXo3\n" +
                "cS9vZ2MyZzJSQXZUYmFwbmFOaU85RjNGVApBcXNrVE0vNVBLR3EwcFRNTHVYV3pT\n" +
                "YzA4VFora1M4ckFwMmhLS0g3RnVqWlNZZWg0QWYvOUtaeC9aeDBNbFBmCmF5NzdH\n" +
                "T2tHWlQ4RVlCaHEyRHEzcTMzaU5JV09CQjdiWVVKbkZUMjhGaTQ4NkVDa0dzV0dI\n" +
                "OG5aVCtzSlZDN0kKOElYblY3WFNucmZzVjFTLzFoeklHZ3ZjQlpyTitGK3pOUUQ3\n" +
                "bnRSVTgrZHk2NjFUdFBTSzNQdi9mVHV4UTMvSgpnVUltRzZ1Y3hsTFlLdzdaVjVk\n" +
                "SkFqdDk4eHhsY3NmNkJvYVEvQ2xnUHV5cGRkNHU2UDJudFBvMmp5L2dESGtYCjB3\n" +
                "SURBUUFCCi0tLS0tRU5EIFBVQkxJQyBLRVktLS0tLV0wHgYIKgMEBQGEgAEEEgwQ\n" +
                "IHVzciByb2xlW293bmVyXTAeBggqAwQFAYSAAgQSDBAgYm9va2luZyBpZFswMDFd\n" +
                "MBoGCCoDBAUBhIADBA4MDCB1c3IgaWRbMDAxXTANBgkqhkiG9w0BAQsFAAOCAQEA\n" +
                "d5OjSmJcf7KwmVvGY+RpemUH24xXYdjzleROaqjK3IoLS+duwM1NHlbfrDZ6PUt3\n" +
                "9ozCPHYz8g7KhhwCPqefvcFaNlZh2lKR8HLTwqgZ3rY6jfVHDe1Zg01XrSENIe4m\n" +
                "2dsQDv5dpB+KuhehUhxMT05rowh8J2hmAJ7cGfYJ44qLaq4bWpDgNjOWddZDCj3V\n" +
                "xDL1c+xYJwXrbnmHPnrUvckDEsfnHdXLBNwpx4bDn7B+KMOeQE97D8CL4CUsKt+P\n" +
                "0y7Qjlabt7wU/BF8oWmOHLIrlH1MUueA/o8V/XiTqowRBa5m+PbdPBPVuX1thJ2e\n" +
                "rs8i/avVlp8Ec7pD7MxePw==\n" +
                "-----END CERTIFICATE-----";

//        String jsonr=HashmapTojson.getJson(
//                new String[]{"id","t","q"},
//                new Object[]{12345678,"AuthReq",new QBean(12345678,
//                       // new DigitalKeyBean("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpNSUlHakRDQ0JYU2dBd0lCQWdJQkNqQU5CZ2txaGtpRzl3MEJBUXNGQURCN01Rc3dDUVlEVlFRR0V3SkRUakVMDQpNQWtHQTFVRUNBd0NSMFF4Q3pBSkJnTlZCQWNNQWtoYU1Rc3dDUVlEVlFRS0RBSlRWakVNTUFvR0ExVUVDd3dEDQpTVVJCTVE4d0RRWURWUVFEREFacVlYTndaWEl4SmpBa0Jna3Foa2lHOXcwQkNRRVdGM3BvWVc1bmVtcEFjMjlzDQpiMjF2TFdsdVptOHVZMjl0TUI0WERURTRNRFV4TURBMU5EUXpObG9YRFRFNU1EVXhNREExTkRRek5sb3diakVMDQpNQWtHQTFVRUJoTUNRMDR4Q3pBSkJnTlZCQWdNQWtkRU1Rc3dDUVlEVlFRS0RBSlRWakVNTUFvR0ExVUVDd3dEDQpTVVJCTVE4d0RRWURWUVFEREFacVlYTndaWEl4SmpBa0Jna3Foa2lHOXcwQkNRRVdGM3BvWVc1bmVtcEFjMjlzDQpiMjF2TFdsdVptOHVZMjl0TUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUE1S2lsDQpJQ1hQejB6RkhETyt1RFMwck11Vi9GTjZSRWZRWnh1RVZsbGxjNEJVZ2FGcm1sUEZvdWh2bW5FcDRiMVlVdkRZDQpxV2FlS2RuTElsVm9pZjQ2elRJZHhIb1lMdTZaU2o2eFRSQVhldmkrb0ZXdlpsTHY3YzFBdkFTdTJWNTFYWis2DQp2TXd3ZkxRek02bjhqV1VaMThTTjRVMTA1YkF6MjJqdkRrdzVDcjVPR0ZXcVNwNmhVTllYSDM4b0pYZ0FhYkE0DQpsL3NYZExEcVJWa2tFNUlwQ2ttSEpaaG15UWowcExEWlpxSFhQeVRKNWdDcWEyZ0JXZGJQaEVld0JhWEcyUEdGDQp1UUo3M3VzZkdwL2YrMjR4RjlRVDZVTXFEdFBrY1Q1WXdZUEVoWXlycGp2dHZPWlpaVVBZRHg1a3I3cEI4Z3Y3DQptVFRpbFVySHVjZzk3U21Tb1FJREFRQUJvNElESmpDQ0F5SXdIZ1lJS2dNRUJRR0VnQUlFRWd3UUlHSnZiMnRwDQpibWNnYVdSYk1EQXhYVEFlQmdncUF3UUZBWVNBQVFRU0RCQWdkWE55SUhKdmJHVmJiM2R1WlhKZE1DNEdDQ29EDQpCQVVCZy85OUJDSU1JQ0J6ZEdGeWRDQjBhVzFsV3pJd01UZ3RNRFV0TURjZ01UVTZNREE2TURCZE1Dd0dDQ29EDQpCQVVCZy85K0JDQU1IaUJsYm1RZ2RHbHRaVnN5TURFNUxUQTFMVEEzSURFMU9qQXdPakF3WFRBaUJnZ3FBd1FGDQpBWVAvZkFRV0RCUWdkWE55SUc1aGJXVmJlR2xoYnlCdGFXNW5YVEFzQmdncUF3UUZBWVAvZXdRZ0RCNGdkbVZvDQphV05zWlNCcFpGdEJRVUZCUVVGQlFVRkJNVEl6TkRVMk4xMHdnZ0htQmdncUF3UUZBWVNBQUFTQ0FkZ01nZ0hVDQpJSFJpYjNnZ2NIVmliR2xqSUd0bGVWc3RMUzB0TFVKRlIwbE9JRkJWUWt4SlF5QkxSVmt0TFMwdExRcE5TVWxDDQpTV3BCVGtKbmEzRm9hMmxIT1hjd1FrRlJSVVpCUVU5RFFWRTRRVTFKU1VKRFowdERRVkZGUVhJMVZ6RmlkMWhpDQpTa1ZWWjFCNE5WSm1SVlpEQ25ka2JYUktOV3BpUjBOaFQzRnhhSGwzU1VFMGVUQlJkMU5EVm5sT1JrUmpia1JUDQplazE2TjNFdmIyZGpNbWN5VWtGMlZHSmhjRzVoVG1sUE9VWXpSbFFLUVhGemExUk5MelZRUzBkeE1IQlVUVXgxDQpXRmQ2VTJNd09GUmFLMnRUT0hKQmNESm9TMHRJTjBaMWFscFRXV1ZvTkVGbUx6bExXbmd2V25nd1RXeFFaZ3BoDQplVGMzUjA5clIxcFVPRVZaUW1oeE1rUnhNM0V6TTJsT1NWZFBRa0kzWWxsVlNtNUdWREk0Um1rME9EWkZRMnRIDQpjMWRIU0RodVdsUXJjMHBXUXpkSkNqaEpXRzVXTjFoVGJuSm1jMVl4VXk4eGFIcEpSMmQyWTBKYWNrNHJSaXQ2DQpUbEZFTjI1MFVsVTRLMlI1TmpZeFZIUlFVMHN6VUhZdlpsUjFlRkV6TDBvS1oxVkpiVWMyZFdONGJFeFpTM2MzDQpXbFkxWkVwQmFuUTVPSGg0YkdOelpqWkNiMkZSTDBOc1oxQjFlWEJrWkRSMU5sQXliblJRYnpKcWVTOW5SRWhyDQpXQW93ZDBsRVFWRkJRZ290TFMwdExVVk9SQ0JRVlVKTVNVTWdTMFZaTFMwdExTMWRNQjhHQ0NvREJBVUJnLzkvDQpCQk1NRVNCd2FXNGdZMjlrWlZzeU16UTFOamRkTUNVR0NDb0RCQVVCZy85NkJCa01GeUJpYkdVZ2JXRmpJR0ZrDQpaSEpiYldGaklHRmtaSEpkTUEwR0NTcUdTSWIzRFFFQkN3VUFBNElCQVFCQjJTMXQxejd0Y0MzYkJQdWttZGU5DQpJT3BBOVRaOTNaRVFzTTc0bHh4dlBnNzVSME1EV21sNTBDM2t3TVFGd1BWVmgwM3h2N2dtWG90ZnR5QldoVUplDQp0Tk1lWk1SVm50U2dmK1krYlVCWFU5SnB1SkVtdXJWSERUbGVIa1hHTzlzVUhYZVdRN3k3ZEY0VjFUOVJZemVQDQpGcEpCTG5NdHh6SllaMTV5YlIyRzFWaUIrVHg1UTM1V24vRjRiVXJLRzd5REVRaFFJWVBmalQxZlJOZTJwUm94DQptc1c3OG9EMzZEb09MMWJSSDJ5UXhsN1puRjFkWWVSeXhiNnYyNFg0MkVsVjNmczYyYWw1SnNzeVNKU2dCQlJ5DQpJZUVUQzBuZkY4QVU2dXJkMGdXQWFUVjhjREtveGtkNEZoR1F2R1Q2NDJiTEEyZ3NvQzc2MDg4aUZzeE82dzBlDQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0t")
//                        new DigitalKeyBean(com.nevs.car.tools.encrypt.Base64.encode(mdCer.getBytes()))
//                )});
       // String zsapp0=DigitalUtils.getFile(MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR+Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEAPPZS);
      //73  String zsapp0=new SharedPHelpers(mContext,Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.TSPRXmobileCertificateContent,"")+"";
       // String zsapp="-----BEGIN CERTIFICATE-----\n"+DigitalUtils.getPem(zsapp0)+"\n-----END CERTIFICATE-----";
      //73  String zsapp="-----BEGIN CERTIFICATE-----\n"+zsapp0+"\n-----END CERTIFICATE-----";
        String zsapp= null;
        try {
            zsapp = DigitalUtils.readExternal(mContext, MyUtils.getStoragePath(mContext,false)+ Constant.MYNEVSCAR+Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEAPPZS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MLog.e("zsapp:\n"+zsapp);
        String jsonr= null;
        try {
            jsonr = HashmapTojson.getJson(
                    new String[]{"id","t","q"},
                    new Object[]{12345678,"AuthReq",new QBean(12345678,
                            // new DigitalKeyBean("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpNSUlHakRDQ0JYU2dBd0lCQWdJQkNqQU5CZ2txaGtpRzl3MEJBUXNGQURCN01Rc3dDUVlEVlFRR0V3SkRUakVMDQpNQWtHQTFVRUNBd0NSMFF4Q3pBSkJnTlZCQWNNQWtoYU1Rc3dDUVlEVlFRS0RBSlRWakVNTUFvR0ExVUVDd3dEDQpTVVJCTVE4d0RRWURWUVFEREFacVlYTndaWEl4SmpBa0Jna3Foa2lHOXcwQkNRRVdGM3BvWVc1bmVtcEFjMjlzDQpiMjF2TFdsdVptOHVZMjl0TUI0WERURTRNRFV4TURBMU5EUXpObG9YRFRFNU1EVXhNREExTkRRek5sb3diakVMDQpNQWtHQTFVRUJoTUNRMDR4Q3pBSkJnTlZCQWdNQWtkRU1Rc3dDUVlEVlFRS0RBSlRWakVNTUFvR0ExVUVDd3dEDQpTVVJCTVE4d0RRWURWUVFEREFacVlYTndaWEl4SmpBa0Jna3Foa2lHOXcwQkNRRVdGM3BvWVc1bmVtcEFjMjlzDQpiMjF2TFdsdVptOHVZMjl0TUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUE1S2lsDQpJQ1hQejB6RkhETyt1RFMwck11Vi9GTjZSRWZRWnh1RVZsbGxjNEJVZ2FGcm1sUEZvdWh2bW5FcDRiMVlVdkRZDQpxV2FlS2RuTElsVm9pZjQ2elRJZHhIb1lMdTZaU2o2eFRSQVhldmkrb0ZXdlpsTHY3YzFBdkFTdTJWNTFYWis2DQp2TXd3ZkxRek02bjhqV1VaMThTTjRVMTA1YkF6MjJqdkRrdzVDcjVPR0ZXcVNwNmhVTllYSDM4b0pYZ0FhYkE0DQpsL3NYZExEcVJWa2tFNUlwQ2ttSEpaaG15UWowcExEWlpxSFhQeVRKNWdDcWEyZ0JXZGJQaEVld0JhWEcyUEdGDQp1UUo3M3VzZkdwL2YrMjR4RjlRVDZVTXFEdFBrY1Q1WXdZUEVoWXlycGp2dHZPWlpaVVBZRHg1a3I3cEI4Z3Y3DQptVFRpbFVySHVjZzk3U21Tb1FJREFRQUJvNElESmpDQ0F5SXdIZ1lJS2dNRUJRR0VnQUlFRWd3UUlHSnZiMnRwDQpibWNnYVdSYk1EQXhYVEFlQmdncUF3UUZBWVNBQVFRU0RCQWdkWE55SUhKdmJHVmJiM2R1WlhKZE1DNEdDQ29EDQpCQVVCZy85OUJDSU1JQ0J6ZEdGeWRDQjBhVzFsV3pJd01UZ3RNRFV0TURjZ01UVTZNREE2TURCZE1Dd0dDQ29EDQpCQVVCZy85K0JDQU1IaUJsYm1RZ2RHbHRaVnN5TURFNUxUQTFMVEEzSURFMU9qQXdPakF3WFRBaUJnZ3FBd1FGDQpBWVAvZkFRV0RCUWdkWE55SUc1aGJXVmJlR2xoYnlCdGFXNW5YVEFzQmdncUF3UUZBWVAvZXdRZ0RCNGdkbVZvDQphV05zWlNCcFpGdEJRVUZCUVVGQlFVRkJNVEl6TkRVMk4xMHdnZ0htQmdncUF3UUZBWVNBQUFTQ0FkZ01nZ0hVDQpJSFJpYjNnZ2NIVmliR2xqSUd0bGVWc3RMUzB0TFVKRlIwbE9JRkJWUWt4SlF5QkxSVmt0TFMwdExRcE5TVWxDDQpTV3BCVGtKbmEzRm9hMmxIT1hjd1FrRlJSVVpCUVU5RFFWRTRRVTFKU1VKRFowdERRVkZGUVhJMVZ6RmlkMWhpDQpTa1ZWWjFCNE5WSm1SVlpEQ25ka2JYUktOV3BpUjBOaFQzRnhhSGwzU1VFMGVUQlJkMU5EVm5sT1JrUmpia1JUDQplazE2TjNFdmIyZGpNbWN5VWtGMlZHSmhjRzVoVG1sUE9VWXpSbFFLUVhGemExUk5MelZRUzBkeE1IQlVUVXgxDQpXRmQ2VTJNd09GUmFLMnRUT0hKQmNESm9TMHRJTjBaMWFscFRXV1ZvTkVGbUx6bExXbmd2V25nd1RXeFFaZ3BoDQplVGMzUjA5clIxcFVPRVZaUW1oeE1rUnhNM0V6TTJsT1NWZFBRa0kzWWxsVlNtNUdWREk0Um1rME9EWkZRMnRIDQpjMWRIU0RodVdsUXJjMHBXUXpkSkNqaEpXRzVXTjFoVGJuSm1jMVl4VXk4eGFIcEpSMmQyWTBKYWNrNHJSaXQ2DQpUbEZFTjI1MFVsVTRLMlI1TmpZeFZIUlFVMHN6VUhZdlpsUjFlRkV6TDBvS1oxVkpiVWMyZFdONGJFeFpTM2MzDQpXbFkxWkVwQmFuUTVPSGg0YkdOelpqWkNiMkZSTDBOc1oxQjFlWEJrWkRSMU5sQXliblJRYnpKcWVTOW5SRWhyDQpXQW93ZDBsRVFWRkJRZ290TFMwdExVVk9SQ0JRVlVKTVNVTWdTMFZaTFMwdExTMWRNQjhHQ0NvREJBVUJnLzkvDQpCQk1NRVNCd2FXNGdZMjlrWlZzeU16UTFOamRkTUNVR0NDb0RCQVVCZy85NkJCa01GeUJpYkdVZ2JXRmpJR0ZrDQpaSEpiYldGaklHRmtaSEpkTUEwR0NTcUdTSWIzRFFFQkN3VUFBNElCQVFCQjJTMXQxejd0Y0MzYkJQdWttZGU5DQpJT3BBOVRaOTNaRVFzTTc0bHh4dlBnNzVSME1EV21sNTBDM2t3TVFGd1BWVmgwM3h2N2dtWG90ZnR5QldoVUplDQp0Tk1lWk1SVm50U2dmK1krYlVCWFU5SnB1SkVtdXJWSERUbGVIa1hHTzlzVUhYZVdRN3k3ZEY0VjFUOVJZemVQDQpGcEpCTG5NdHh6SllaMTV5YlIyRzFWaUIrVHg1UTM1V24vRjRiVXJLRzd5REVRaFFJWVBmalQxZlJOZTJwUm94DQptc1c3OG9EMzZEb09MMWJSSDJ5UXhsN1puRjFkWWVSeXhiNnYyNFg0MkVsVjNmczYyYWw1SnNzeVNKU2dCQlJ5DQpJZUVUQzBuZkY4QVU2dXJkMGdXQWFUVjhjREtveGtkNEZoR1F2R1Q2NDJiTEEyZ3NvQzc2MDg4aUZzeE82dzBlDQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0t")
                            new DigitalKeyBean(com.nevs.car.tools.encrypt.Base64.encode(zsapp.getBytes("UTF-8")))
                    )});
        } catch (Exception e) {
            e.printStackTrace();
        }
        MLog.e("renzJSON-->"+jsonr);


//        String zsNumber= null;
//        try {
//            zsNumber = new String(jniHelper.getCsrNumber(""),"UTF-8");
//        } catch (Exception e) {
//           MLog.e("zsNumber获取异常"+e);
//        }
//        String jsonre=HashmapTojson.getJson(
//                new String[]{"id","t","q"},
//                new Object[]{12345678,"AuthReconn",new BookingCertSNBean(zsNumber)});
//        MLog.e("renzJSON-->"+jsonr);


        listBytesRenzSplit.clear();
        listAccept.clear();

        /**
         * B,分段加密
         */
        byte[] mRestart= new byte[0];//1 json转化为byte数组
        try {
            mRestart = jsonr.getBytes("UTF-8");
            MLog.e("mRestart长度:"+mRestart.length);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //按180个分成一组加密
        bytesR = split_bytes(mRestart,180);
        MLog.e("分段长度："+bytesR.length);

        for(int i=0;i<bytesR.length;i++){
            listBytesRenzSplit.add(getRsa(bytesR[i]));
        }
        MLog.e("listBytesRenzSplit长度："+listBytesRenzSplit.size());


        /**
         * C,分段加密后合并成新大数组
         */
        int ii=0;
        byte[] bytehe=new byte[(listBytesRenzSplit.size())*256];
        for(int i=0;i<listBytesRenzSplit.size();i++){
            for(int j=0;j<256;j++){
                bytehe[ii]=listBytesRenzSplit.get(i)[j];
                ii++;
            }
        }
        MLog.e("bytehe长度："+bytehe.length);
        MLog.e("---------------------------");


        /**
         * D,按120有效数据位分成新数组
         */

        byte[][] bytesRSplit = split_bytes(bytehe,80);  //120分包大小
        MLog.e("bytesRSplit.length:分包个数："+bytesRSplit.length);

        MLog.e("\r\n\r\n==========================================\r\n");

        MLog.e(bytesRSplit[0][0]+"");

        /**
         * E,计算效验和并重新组包后放入集合保存
         */
        initCRCRenOnef(bytesRSplit);

//        Looper.prepare();
//        ToastUtil.showToast(mContext,getResources().getString(R.string.rzstart));
//        Looper.loop();
        /**
         * F,开始分包发送
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //cz时间250
                    Thread.sleep(250);
                    startrenzheng=MyUtils.timeStampNow();
                    MLog.e("开始时间："+HashmapTojson.getDateToString(MyUtils.timeStampNow()*1000,"yyyy-MM-dd HH:mm:ss")  );
                    mBleService.sendOrders(listBytesRenz.get(0), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e("发送异常"+e);
                }

            }
        }).start();
    }
    public  void initCRCRenOnef(byte[][] bytesRSplit) {
        listBytesRenz.clear();
        byte[] byteSend = new byte[0];
        //vv一
        for (int i=0;i<bytesRSplit.length;i++) {
            byte[] byted = new byte[bytesRSplit[i].length];
            byteSend = new byte[byted.length + 5];

            //计算效验和vv
            short sum=0;
            for (int j=0;j<bytesRSplit[i].length;j++){
                sum+=bytesRSplit[i][j];
            }
            //   short CRC= (short) ((i+1)+bytesRSplit[i].length+sum);
            short CRC= (short) ((i+1)+bytesRSplit[i].length+sum);

            if(i==bytesRSplit.length-1){
                CRC= (short) ((-1)+bytesRSplit[i].length+sum);
            }

//            MLog.e("crcshort:"+CRC);
//            MLog.e("uint8crc:"+getUint8(CRC));
//            MLog.e("uint8crct:"+getUint8((short) 300));
//            MLog.e("CRC:"+(byte)CRC+"  "+i);
            //^^

            byteSend[0]= (byte) 0xFF;
            byteSend[1]= (byte) 0xA5;
            byteSend[2]= (byte) ((i+1) & 0xFF);

            if(i==bytesRSplit.length-1){
                byteSend[2]= (byte)0xFF;
            }

            MLog.e("byteSend[2]"+byteSend[2]);
            byteSend[3]= (byte) (byted.length & 0xFF);
            for(int k=4;k<byteSend.length-1;k++){
                byteSend[k]= bytesRSplit[i][k-4];
            }
            byteSend[byteSend.length-1]= (byte) (getUint8(CRC)&0xFF);
            // MLog.e("(byte) CRC："+(byte) CRC);
            listBytesRenz.add(byteSend);
        }
        //^^一
        MLog.e("包的个数："+listBytesRenz.size());
    }
    private void getCrcRenzOnej(byte[] value2) {
        //计算效验和vv
        short sum=0;//有效数据位和
        for (int j=4;j<value2.length-1;j++){
            sum+=value2[j];
        }
        short CRCs= (short) (value2[2]+value2[3]+sum);
        byte CRCb=(byte) (getUint8(CRCs)&0xFF);
        MLog.e("crcshort:"+CRC);
        if(CRCb==value2[value2.length-1]){
            MLog.e("效验成功");

            byte []bytey =new byte[value2.length-5];
            int ii=0;
            for(int k=4;k<value2.length-1;k++){
                bytey[ii]=value2[k];
                ii++;
            }
            listAccept.add(bytey);
//            try {
//                byte[] ack=getAck(value2);
//                Thread.sleep(30);
//                mBleService.sendOrders(ack,ack[2]);
//            } catch (Exception e) {
//                e.printStackTrace();
//                MLog.e("发送异常"+e);
//            }
            getAck(value2,mBleService);
            if(value2[2]==-1){
                MLog.e("认证接收的包的个数："+listAccept.size());

                getRsaDe();//rsa分段解密

            }else {

            }

        }else {
            MLog.e("效验失败");
        }
    }
    private void getRsaDe() {//RSA分段解密
        //将集合里面的有效数据位的数组拿出来组成新数组
        int leng=0;
        for(int e=0;e<listAccept.size();e++){
            leng+=listAccept.get(e).length;
        }
        MLog.e("有效数据位的总长度"+leng);
        int kk=0;
        byte[] Acceptdersaend=new byte[leng];
        for (int i=0;i<listAccept.size();i++){
            for(int j=0;j<listAccept.get(i).length;j++){
                Acceptdersaend[kk]=listAccept.get(i)[j];
                kk++;
            }
        }
        MLog.e("Acceptdersaend字节的长度："+Acceptdersaend.length);
        MLog.e("base64jieguo:"+ com.nevs.car.tools.encrypt.Base64.encode(Acceptdersaend));


        //分割数组
        //按256个分成一组
        byte[][] bytesRSplitren = split_bytes(Acceptdersaend,256);
        MLog.e("bytesRSplit.length:分包个数："+bytesRSplitren.length);

        MLog.e("\r\n\r\n==========================================\r\n");


        //解密后合成
        List<byte[]> list=new ArrayList();
        try {
            for(int k=0;k<bytesRSplitren.length;k++){
                list.add(rsaDe(bytesRSplitren[k]));
            }
        }catch (Exception e){
            MLog.e("解密失败");
            return;
        }


        int lengs=0;
        for(int z=0;z<list.size();z++){
            lengs+=list.get(z).length;
        }


        int ii=0;
        byte[] bytehej=new byte[lengs];
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.get(i).length;j++){
                bytehej[ii]=list.get(i)[j];
                ii++;
            }
        }
        MLog.e("bytehe字节长度："+bytehej.length);
        MLog.e("---------------------------");

        //{"id":12345678,"t":"AuthReq","r":{"ErrCode":0,"ErrInf":"Succeed","SymmEncKey":"Sym7V94hd8FgZj34","AES_InitVector":"wl6WIW4Qs065ps3o","MD_nonce":12345678,"TU_nonce":220392469}}
        String s= null;
        String SymmEnccc="";
        String InitVector="";
        String ErrInf="";
        try {
            s = new String(bytehej,"UTF-8");
            JSONObject jsonObject = new JSONObject(s);
            JSONObject r = jsonObject.getJSONObject("r");
            SymmEnccc=r.getString("SymmEncKey");
            InitVector=r.getString("AES_InitVector");
            ErrInf=r.getString("ErrInf");
        } catch (Exception e) {
            e.printStackTrace();
            MLog.e("认证解密后的数据异常");

        }
        //CZTJ
        MLog.e("认证解密后的数据："+s);

        try {
            if((new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).get(Constant.ISRENZHENG,"0")+"").equals("0")){
                toLog(111,"认证解密后的数据："+s);
                }

        }catch (Exception e){
            MLog.e("yy"+e);
        }


        MLog.e("SymmEnccc:"+SymmEnccc);
        MLog.e("InitVector:"+InitVector);

        if(!ErrInf.equals("Succeed")){
            MLog.e("认证解密后的数据,证书不对");
            return;
        }
        new SharedPHelpers(mContext,"rz"+new SharedPHelper(MainActivity.this).get("TSPVIN", "0").toString()).put(getResources().getString(R.string.SymmEnccc),SymmEnccc);
        new SharedPHelpers(mContext,"rz"+new SharedPHelper(MainActivity.this).get("TSPVIN", "0").toString()).put(getResources().getString(R.string.InitVector),InitVector);
        startrenz2(s);
    }
    private void startrenz2(String s) {//获取认证2AES参数
        long TU_nonce=0;
        try {
            JSONObject js=new JSONObject(s);
            JSONObject r=js.getJSONObject("r");
            TU_nonce=r.getLong("TU_nonce");
            Log.e("tag","TU_nonce:"+TU_nonce);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String json=HashmapTojson.getJson(
                new String[]{"id","t","q"},
                new Object[]{12345678,"AuthConfirm",
                        new Qstatbooking(TU_nonce)
                }
        );
        MLog.e("startrenz2:"+json);
        byte aa[]=new byte[0];
        try {
            aa=json.getBytes("UTF-8");
            MLog.e("startrenz2aa"+aa.length);
            getaes(aa);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }
    private void getaes(byte[] aa) {

        // String name="q";
        //String key0=getResources().getString(R.string.car_healtho);
        String key0= String.valueOf(new SharedPHelpers(mContext,"rz"+new SharedPHelper(MainActivity.this).get("TSPVIN", "0").toString()).get(getResources().getString(R.string.SymmEnccc),""));
        AES aes = new AES();
        byte[] enc=aes.encrypt(aa,key0.getBytes());
//        Log.e("tag","加密后的内容：" + new String(Hex.encode(enc)));
//        Log.e("tag","加密后的长度：" + enc.length);
//        Log.e("tag","加密后的内容base64：" + com.nevs.bluetooth.encrypt.Base64.encode(enc));


        //对AES加密后的内容进行SHAMAC256
        byte[] sha256= Sha256Util.HmacSHA256(enc);
        // MLog.e("HMACK256:"+ com.nevs.bluetooth.encrypt.Base64.encode(sha256));
        //取前16个字节拼到enc后组成新的数组
        byte[] byte16=new byte[16];
        for(int i=0;i<16;i++){
            byte16[i]=sha256[i];
        }

        byte[] aeshe=new byte[enc.length+16];

        int kk=0;
        List<byte[]> list=new ArrayList<>();
        list.add(enc);
        list.add(byte16);
        for(int n=0;n<list.size();n++){
            for(int m=0;m<list.get(n).length;m++){
                aeshe[kk]=list.get(n)[m];
                kk++;
            }
        }
        MLog.e("AES完整加密后的字节个数："+aeshe.length);
        MLog.e("AES完整加密后的："+ com.nevs.car.tools.encrypt.Base64.encode(aeshe));


        //分组后发送
        getSplit(aeshe);
    }
    private void getSplit(byte[] aeshe) {
        //按120个分成一组
        bytesrenzSplit = split_bytes(aeshe,120);//120分包大小
        MLog.e("bytesrenzSplit.length:分包个数："+bytesrenzSplit.length);

        MLog.e("\r\n\r\n==========================================\r\n");

        //组包
        initCRCRenzss();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(250);
                    MLog.e("整个数据包："+ com.nevs.car.tools.encrypt.Base64.encode(listrenzSplit.get(0)));
                    mBleService.sendOrders(listrenzSplit.get(0), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e("发送异常"+e);
                }

            }
        }).start();

    }
    private byte[] getRsa(byte[] byteSplit) {//RSA加密
        // String s=MyUtils.getStoragePath(LongControlActivity.this,false)+"/data/zxc41.pem";
        String s=MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR+Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEPUBKey;

        //    File hh=new File(s);

//        File image = getFileStreamPath("zxc41.pem");
//        File second = getFileStreamPath("ic_launcher.png");
//        Log.e("tag", "onCreate: =path=" + image.getAbsolutePath());
//         Log.e("tag", "onCreate: =paht="+second.getAbsolutePath());
//        File absoluteFile = image.getAbsoluteFile();
//        if (hh.exists()) {
//            Log.e("tag", "onCreate: =文件存在=");
//        } else {
//            Log.e("tag", "onCreate: =文件不存在=");
//        }


        //  String ddd="/storage/emulated/0/data/zxc41.pem";
        //String ddd="/storage/emulated/0/data/zxc41.pem";

        String ddd= com.nevs.car.jnihelp.StringUtils.getAssetsCacheFile(mContext,"zxc41.pem");
        //tj String ddd=MyUtils.getStoragePath(mContext,false)+"/datamy/"+Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEPUBKey;

        Log.e("tag","SD内置路径："+MyUtils.getStoragePath(MainActivity.this,false));
        //  Log.e("tag","pem内置路径："+StringUtils.getAssetsCacheFile(mContext,"zxc41.pem"));
        Log.e("tag","pubkey："+s);
        byte ou[]=new byte[256];


        Log.e("tag","结果："+jniHelper.RSA(s,byteSplit,byteSplit.length,ou,256)+"");

        Log.e("tag",ou[0]+"tttttt");

        Log.e("tag",ou[0]+" ouchangde0");
        Log.e("tag","结果0："+ Base64.encodeToString(ou,Base64.NO_WRAP));
        Log.e("tag","结果1："+ com.nevs.car.tools.encrypt.Base64.encode(ou));

        base64= com.nevs.car.tools.util.Base64.encode(ou);
        Log.e("tag","结果2："+  com.nevs.car.jnihelp.StringUtils.bytesToHexString(ou));

        Log.e("tag","地址："+ou);

        return ou;

    }
    public byte[] rsaDe(byte[] bb){//RSA解密
        //String ddd="/storage/emulated/0/data/mdPri.pem";
         //String ddd=com.nevs.car.jnihelp.StringUtils.getAssetsCacheFile(mContext,"mdPri.pem");
        String ddd=MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR+Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEPRIKey;

        Log.e("tag","JM内置路径："+ddd);

        //String base64="EA4gKZ20Zd+JiQ007Ci8+IT2yG6SoHCEJ0o1A3ok8OVnYf0qn8mNemNVx8orO6ChqzgH6ljT5FZii2kxSRZicoikLWArnVbH+KF5xAr35yTuNsp76lbP+t+kfvVyADpUqHDcA2rSM0FslfCRNXrtOS2+ijn/NZ8lL6BWcX6fBHvBJKhXytfdjnawOrWBM1SQHMGlMRfKoThk4m6xOWgVhmarKvQ/fLnzIEQQb769tWh+MGLt9LCUn8Q7KEh1b2YcXr3q5pkOxmckJU25XOHvSc9cWkzWUh1ebbMLSWb4xq+EuqRW/07KmNLvIYAwpX0GRzZAIxBXFovhodSq+rJNzw==";

        // String base64="hvV4/CDgayHENyqSeiqH6euFeEMfaBEuDq/gmBKhu7lW5hT2l/X5EpVnIRptV6rtdpKv/lPJGDx3Zn8jpQBeJ4jTYc7l+bbA0RiDGbOXpxvQ8Dr5RCbxBI8DUgoYvfId4YNKUosvtzqUv31En93CyJIAp0BjHF9RwmQgPXgLnHrQS3JXRnDf2L9NGgt5xoC7Dzj7JZnb83aqKl6XvgWrlkk3X5wVVo7FPctHat2dt1atFS2QczE71GQqpEanOaSPeR/V0ZcLETdVZo9aYjZUP2n49M2F+GMBzIIUFf/qwucMjfgBQApgsRJKGGPBHO4d8orAyz0ZLF2ElSG2Dhs+Jw==";
//        byte[] dede=new byte[256];
//        for(int h=0;h<bb.length;h++){
//            dede[h]=bb[h];
//        }
        int delen = 256;
        byte de[]=new byte[256];
        byte []dejni=null;
        try {
            int aaa=jniHelper.RSADECO(ddd,bb,bb.length,de,delen);
            Log.e("tag","解密长度："+aaa);
           // Log.e("tag",de[0]+"解密0");



            dejni=new byte[aaa];
            for(int i=0;i<aaa;i++){
                dejni[i]=de[i];
            }

            String res = new String(dejni,"UTF-8");
            Log.e("tag","解密结果："+res);
            Log.e("tag","delen"+aaa);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return dejni;
    }
    public  void initCRCRenzss() {

        byte[] byteSend = new byte[0];
        //vv一
        for (int i=0;i<bytesrenzSplit.length;i++) {
            byte[] byted = new byte[bytesrenzSplit[i].length];
            byteSend = new byte[byted.length + 5];

            //计算效验和vv
            short sum=0;
            for (int j=0;j<bytesrenzSplit[i].length;j++){
                sum+=bytesrenzSplit[i][j];
            }
            //   short CRC= (short) ((i+1)+bytesRSplit[i].length+sum);
            short CRC= (short) ((i+1)+bytesrenzSplit[i].length+sum);

            if(i==bytesrenzSplit.length-1){
                CRC= (short) ((-1)+bytesrenzSplit[i].length+sum);
            }

            byteSend[0]= (byte) 0xFF;
            byteSend[1]= (byte) 0xA5;
            byteSend[2]= (byte) ((i+1) & 0xFF);

            if(i==bytesrenzSplit.length-1){
                byteSend[2]= (byte)0xFF;
            }

            MLog.e("byteSend[2]"+byteSend[2]);
            byteSend[3]= (byte) (byted.length & 0xFF);
            for(int k=4;k<byteSend.length-1;k++){
                byteSend[k]= bytesrenzSplit[i][k-4];
            }
            byteSend[byteSend.length-1]= (byte) (getUint8(CRC)&0xFF);
            // MLog.e("(byte) CRC："+(byte) CRC);
            listrenzSplit.add(byteSend);
        }
        //^^一
        MLog.e("aes包的个数："+listrenzSplit.size());
    }

    //认证成功的消息接收 最后的一个环节
    private void getCrc2(byte[] value2) {
        //计算效验和vv
        short sum=0;//有效数据位和
        for (int j=4;j<value2.length-1;j++){
            sum+=value2[j];
        }
        short CRCs= (short) (value2[2]+value2[3]+sum);
        byte CRCb=(byte) (getUint8(CRCs)&0xFF);
        MLog.e("crcshort:"+CRC);
        if(CRCb==value2[value2.length-1]){
            MLog.e("效验成功");

            byte []bytey =new byte[value2.length-5];
            int ii=0;
            for(int k=4;k<value2.length-1;k++){
                bytey[ii]=value2[k];
                ii++;
            }
            listAccept2.add(bytey);

            getAck(value2,mBleService);
            if(value2[2]==-1){
                MLog.e("认证接收的包的个数："+listAccept2.size());
                String json= getAESDe(listAccept2);//AES解密
                jsonJie(json);
            }

        }else {
            MLog.e("效验失败");
        }
    }
    private void jsonJie(String json) {
        try {
            JSONObject js=new JSONObject(json);
            JSONObject r=js.getJSONObject("r");
            String ErrInf=r.getString("ErrInf");
            if(ErrInf.equals("Succeed")){
                MLog.e("整个流程走完，认证成功@@@");
                new SharedPHelpers(mContext,"rz"+new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISRENZHENG,"1");
                new SharedPHelpers(mContext,"rerz"+new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISDISCON,"0");//1为断开过,0为连接状态中
                ToastUtil.showLongToast(mContext,getResources().getString(R.string.n_renzsucc));
               // unregisterReceiver(blueBrocastRecever);

                new SharedPHelpers(mContext, "rz" + new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISSTARTBOOKING, "0");//重走startbooking 0重走
                new SharedPHelpers(mContext, "rerz" + new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISDISCONNING, "1");
                toSend();


                toLog(112,"\n------------认证成功@@@");

            }else {
                MLog.e("整个流程走完，认证失败~~~");
                ToastUtil.showToast(mContext,getResources().getString(R.string.rzfail));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //11111111111111111111111111111111111111111111111111111111end

    protected void onResume() {
        super.onResume();

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        try {
//            unregisterReceiver(blueBrocastRecever);
//        }catch (Exception e){
//
//        }
//
//    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initConnect(){
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
                            startConnect();
                        } else {
                            MLog.e("蓝牙开启中");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mBluetoothAdapter.isEnabled()) {
                                        MLog.e("蓝牙开启成功");
                                        startConnect();
                                    }
                                }
                            }, 2000);
                        }
                    }
                };
                handler.postDelayed(runnable, 3000);

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
        }else {
            MLog.e("权限都有开始连接蓝牙");
            startConnect();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startConnect(){//开始连接蓝牙
         //cc   registerReceiver(blueBrocastRecever, BlueBrocastRecever.makeGattUpdateIntentFilterM());//8
        if (mBleService != null) {
            final boolean result = mBleService.connect(mAddress);
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
    private void initService() {
        gattServiceIntent = new Intent(MainActivity.this, BleService.class);
        boolean bll = bindService(gattServiceIntent, mServiceConnection,//2
                BIND_AUTO_CREATE);
        if (bll) {
            Log.e("tag", "绑定成功");
        } else {
            Log.e("tag", "绑定失败");
        }

        //连接
        //  service.connectBle(this,mAddress);
        //initConnect();

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
//            //判断是否有权限
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
    /**
     * 权限申请
     * */
    private void initPession() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //做一些处理
//            //初始化并发起权限申请
//            mPermissionHelper = new PermissionHelper(this, this);
//            mPermissionHelper.requestPermissions();
//
//        } else{
//            //在版本低于此的时候，做一些处理
//        }
        //做一些处理
        //初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionHelper==null){
            return;
        }
        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            //权限请求结果，并已经处理了该回调
            try {
                MLog.e("requestCode1:"+requestCode+":"+grantResults+":"+permissions);
            }catch (Exception e){

            }


            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

      //  MLog.e("requestCode2:"+requestCode+":"+grantResults+":"+permissions);

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

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initViews(){
        //已经拥有所需权限，可以放心操作任何东西了
        //初始化蓝牙
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //mAddress ="0A:5B:00:6B:3C:59";
        //mAddress="01:A5:A5:A5:A5:A5";
        //mAddress="74:AC:5F:76:7D:D5";
        mAddress=(new SharedPHelpers(mContext,Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN","0")).get("bleAddress", "")+"").toUpperCase();
        mAddress=MyUtils.toBacks(mAddress);
      //  mAddress="01:A5:A5:A5:A5:A5";//73
        MLog.e("需要连的蓝牙地址："+mAddress);
        mName = "NEVS Sample";
        jniHelper=new JniHelper();

        if(null!=mBleService){
            initConnect();
        }else {
            initService();
        }
    }
    @Override
    public void postTo() {
        MLog.e("开始获取权限并连接蓝牙");
        initPession();//初始化权限
    }
    @Override
    public void getBlueReceiver(int i,int id, byte[] bytes) {
        switch (i){
            case 1:
                ToastUtil.showLongToast(mContext,getResources().getString(R.string.rzstart));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        renOnef();//开始认证
                    }
                },8000);



//                String zsapp0=new SharedPHelpers(mContext,Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")).get(Constant.TSPRXmobileCertificateContent,"")+"";
//                if(!zsapp0.equals("")){
//                    ToastUtil.showLongToast(mContext,getResources().getString(R.string.rzstart));
//                    renOnef();//开始认证
//                }else {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            ToastUtil.showLongToast(mContext,getResources().getString(R.string.rzstart));
//                            renOnef();//延时开始认证
//                        }
//                    },3000);
//                }

                break;
            case 2:
                // 搜索需要的uuid
                initGattCharacteristics(mBleService
                        .getSupportedGattServices());
                // writeDate(true);
                //  Toast.makeText(MainControllActivity.this, "发现新services", Toast.LENGTH_SHORT).show();
                MLog.e("发现新services");
                break;
            case 3:
                MLog.e("mainA 3");
                mBleService.sendOrders(listBytesRenz.get(id), id);
                break;
            case 4:
                MLog.e("mainA 4");
                getCrcRenzOnej(bytes);
                break;
            case 5:
                mBleService.sendOrders(listrenzSplit.get(bytes[0]), bytes[0]);
                break;
            case 6:
                getCrc2(bytes);
                break;
            case 7:

                break;
            case 8:

                break;


            case 300:
                ToastUtil.showLongToast(mContext,getResources().getString(R.string.reconnecrsuccing));
                new SharedPHelpers(mContext, "rerz" + new SharedPHelper(mContext).get("TSPVIN", "0")).put(Constant.ISDISCONNING, "0");//0为进行中
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BlueMessUtil.renOnef(jniHelper, mContext, mBleService);
                    }
                },8000);
                break;
            case 33:
                MLog.e("MainA 33");
                mBleService.sendOrders(BlueMessUtil.listBytesRenz.get(id), id);
                break;
            case 34:
                MLog.e("MainA 34");
                BlueMessUtil.getCrcRenzOnej(bytes, mBleService, mContext, jniHelper);
                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLog.e("Mainactivity onDestroy");
        try {
            //PIN
            SharedPHelper sharedPHelper = new SharedPHelper(context);
            sharedPHelper.put(Constant.PINISKILL,true);//表示PIN已经失效
            sharedPHelper.put(Constant.PINISKILLFINGER,true);//表示指纹已经失效
            MLog.e("MAINA失效");


            new SharedPHelper(MyApp.getInstance()).put(Constant.BLUECONSTATE,"0");//1为连接，0为断开
            new SharedPHelpers(MyApp.getInstance(),"rerz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0")).put(Constant.ISDISCON,"1");//1为断开过,0为连接状态中

        }catch (Exception e){
            MLog.e("Mainactivity onDestroy  yic");
        }
//            if(mBluetoothAdapter!=null){
//                mBluetoothAdapter.disable();
//            }

            // mBluetoothAdapter.disable();

        try {
            unregisterReceiver(blueBrocastRecever);
        }catch (Exception e){
            MLog.e("MainActivity1:"+e);
        }
        try {
            invalidateOptionsMenu();
        }catch (Exception e){
            MLog.e("MainActivity2:"+e);
        }
        try {
            mBleService.close();
        }catch (Exception e){
            MLog.e("MainActivity3:"+e);
        }
        try {
            this.getApplicationContext().unbindService(mServiceConnection);
        }catch (Exception e){
            MLog.e("MainActivity4:"+e);
        }
        try {
            mBleService = null;
        }catch (Exception e){
            MLog.e("MainActivity5:"+e);
        }
        try {
            //PIN
            Intent i = new Intent(mContext, LongRunningService.class);
            mContext.stopService(i);//停止服务
        }catch (Exception e){
            MLog.e("MainActivity6:"+e);
        }

//            mBleService.stopSelf();
//            this.getApplicationContext().stopService(gattServiceIntent);


        try {
           // mBluetoothAdapter.disable();
        }catch (Exception e){
            MLog.e("MainActivity7关闭蓝牙异常");
        }

    }

    @Override
    public void closeBle(int i) {
        switch (i){
            case 1:
                try {
                    mBluetoothAdapter.disable();
                }catch (Exception e){
                    MLog.e("关闭蓝牙异常");
                }
                break;

            case 2:
                String zsapp= "";
                try {
                    zsapp = DigitalUtils.readExternal(mContext, MyUtils.getStoragePath(mContext,false)+ Constant.MYNEVSCAR+Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEAPPZS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(!zsapp.equals("")&&!zsapp.equals("null")&&zsapp!=null) {
                    MLog.e("ffffffeeeeeeeeeee："+zsapp);
                    mAddress = (new SharedPHelpers(mContext, Constant.CSRHEAD + new SharedPHelper(mContext).get("TSPVIN", "0")).get("bleAddress", "") + "").toUpperCase();
                    if (!mAddress.equals("")) {
                        MLog.e("ffffff");
                        initPession();//初始化权限
                    }
                }
                break;
        }





    }

    @Override
    public void connect() {
//        if(mBleService==null){
//            return;
//        }
//        if(mBleService.mConnectionState==2){
//            return;
//        }
        String zsapp= null;
        try {
            zsapp = DigitalUtils.readExternal(mContext, MyUtils.getStoragePath(mContext,false)+ Constant.MYNEVSCAR+Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEAPPZS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(zsapp!=null||zsapp.length()!=0){
            MLog.e("ffffffeeeeeeeeeee");
            mAddress=(new SharedPHelpers(mContext,Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN","0")).get("bleAddress", "")+"").toUpperCase();
        if(!mAddress.equals("")) {
            MLog.e("ffffff");
            initPession();//初始化权限
        }

        }
    }
}

