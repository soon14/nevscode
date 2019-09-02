package com.nevs.car.z_start;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.SharedPreferencesUtil.ShareUtil;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.DeviceUtils;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.SystemBarTintManager;

/**
 * 闪屏activity，保证点击桌面应用图标后无延时响应
 * */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 取消显示标题栏
       //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置背景NO全屏
        //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // StatusBarCompat.setStatusBarColor(this, Color.parseColor("#d9e1ed"));

        setThemes();
        super.onCreate(savedInstanceState);

        //默认打开蓝牙
      //  initBluetoolth();


        DialogUtils.loading0(SplashActivity.this,false);
        initNumber();
//        MLog.e("测试数据KEY:"+String.valueOf(new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0").toString()).get(MyApp.getInstance().getResources().getString(R.string.SymmEnccc),"")));
//        MLog.e("测试数据IV:"+String.valueOf(new SharedPHelpers(MyApp.getInstance(),"rz"+new SharedPHelper(MyApp.getInstance()).get("TSPVIN", "0").toString()).get(MyApp.getInstance().getResources().getString(R.string.InitVector),"")));

        //setH5();

    }
    private void setH5() {
        // 获取uri参数
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        Uri uri = intent.getData();
    }
    private void setThemes() {
        //设置沉浸式状态栏
        MyUtils.setWindow(SplashActivity.this);
        boolean lang=MyUtils.getLanguage(SplashActivity.this);
        MLog.e("lang:"+lang);
        if(lang){
           // setTheme(R.style.StartTheme);
            getWindow().getDecorView().setBackgroundResource(R.mipmap.splashz);
        }else {
           // setTheme(R.style.StartThemeEnglish);
            getWindow().getDecorView().setBackgroundResource(R.mipmap.splashz);
        }
    }
    private void initBluetoolth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Toast.makeText(this,"本地蓝牙不可用",Toast.LENGTH_SHORT).show();
            finish();   //退出应用
        }

        if(!bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();  //打开蓝牙，需要BLUETOOTH_ADMIN权限
        }
    }

    private void initNumber() {
        String isto=new SharedPHelper(this).get(Constant.ISPLASHTO,"0")+"";
        if(isto.equals("0")){
            timeker();
            MLog.e("00000");
        }else {
            timeker2();
            MLog.e("22222");
        }

    }

    private void setSys() {
        SystemBarTintManager tintManager;
        //判断当前系统版本是否>=Andoird4.4
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            // 通知标题栏所需颜色
            tintManager.setStatusBarTintColor(Color.parseColor("#353535"));
            //设置状态栏背景状态
            //true：表明当前Android系统版本>=4.4
            tintManager.setStatusBarTintEnabled(true);
        }
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void timeker() {
        //设置背景全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogUtils.hidding0(SplashActivity.this);
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            }
        }, 1000);
    }

    private void timeker2() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                login();
            }
        },500);
    }

    private void login() {
        HttpRxUtils.getLogin(
                SplashActivity.this,
                new String[]{"loginName", "pwd", "appType", "deviceID"},
                new Object[]{
                        new SharedPHelper(SplashActivity.this).get(Constant.LOGINNAME,"tt")+"",
                        ShareUtil.readtt(SplashActivity.this,Constant.LONGINTTS,Constant.LONGINTT),
        "Android",
                        DeviceUtils.getUniqueId(SplashActivity.this)},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding0(SplashActivity.this);
                        MLog.e("登录中");
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding0(SplashActivity.this);
                        startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                        finish();
                    }
                });
    }




    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }

}
