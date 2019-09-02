package com.nevs.car.z_start;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.amap.api.location.AMapLocationClient;
import com.lsxiao.apollo.core.Apollo;
import com.nevs.car.tools.ClassUtil;
import com.nevs.car.tools.SharedPreferencesUtil.ShareUtil;
import com.nevs.car.tools.util.ActivityManagerUtils;
import com.nevs.car.tools.util.GetLanguageUtil;
import com.nevs.car.tools.util.MLog;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.tamic.novate.config.ConfigLoader.getContext;

/**
 * Created by mac on 2018/4/2.
 */

public class MyApp extends Application {
    private static MyApp context;
    public static MyApp getInstance() {
        return context;
    }
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.ACCESS_FINE_LOCATION"};
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initYouMeng();//初始化友盟
        initJpush();//初始化极光推送
        Apollo.init(AndroidSchedulers.mainThread(), this, true);//使用RXANDROID2.0.1
        // localpemiss();
      //  getLanguage();//设置APP语言
        initX52();
        ClassUtil.closeAndroidPDialog();
    }

    private void initX5() {
        //预加载x5内核
        Intent intent = new Intent(this, X5NetService.class);
        startService(intent);
    }

    private void initYouMeng() {
        /*
         * 如果在注册清单里 声明APP KEY,就可以使用这个初始化方法
         *
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:APP_KEY  如果设置为null
         * 参数3:Channel  如果设置为null
         * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数5:Push推送业务的secret 需要集成Push功能必须传入的sercet,否则为空
         */
        UMConfigure.init(getApplicationContext(), null, null, UMConfigure.DEVICE_TYPE_PHONE, null);
        //当应用在后台运行超过30秒（默认）再回到前端，将被认为是两个独立的session(启动)，例如用户回到home，或进入其他程序，经过一段时间后再返回之前的应用。可通过接口：
//        MobclickAgent.setSessionContinueMillis(30*1000);//来自定义这个间隔（参数单位为毫秒）。

    }


    private void initJpush() {
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }

    private void getLanguage() {

        //判断用户是否是第一次登陆，第一次登陆使用系统的设置，如果不设置还是跟谁系统，如果设置了以后都用应用自己设置的语言。

        //获取是否第一次进入APP的状态值
        String isFisrst = ShareUtil.readIsFirst(context,"Isfirst","counst");
        MLog.e("第"+isFisrst+"次进入APP");
        //获取系统语言
        String able = GetLanguageUtil.getLanguage();

        if(isFisrst.equals("1")){
            //将APP的进入状态改为"2"
            ShareUtil.storeIsFirst(context,"2","Isfirst","counst");
            switch (able){//"zh"为中文，"cn"为英文
                case "zh":
                    GetLanguageUtil.zh(context);
                    break;
                case "cn":
                    GetLanguageUtil.cn(context);
                    break;
            }
        }else if (isFisrst.equals("2")){
            //获取用户设置语言状态,没有更改还是根据系统设置语言，如果改了就设为用户之前设置的语言
            String isSetting=ShareUtil.readSettingLanguage(context,"issettings", "issetting");
            //此时设置成哪种语言了需要在APP语言的设置界面写入缓存
            switch (isSetting){//""为未设置,"zh"为中文"cn"为英文
                case "":
                    MLog.e("11");
                    switch (able){//"zh"为中文，"cn"为英文
                        case "zh":
                            MLog.e("5511");
                            GetLanguageUtil.zh(context);
                            break;
                        case "cn":
                            MLog.e("6611");
                            GetLanguageUtil.cn(context);
                            break;
                    }
                    break;
                case "zh":
                    MLog.e("3311");
                    GetLanguageUtil.zh(context);
                    break;
                case "cn":
                    MLog.e("3311");
                    GetLanguageUtil.cn(context);
                    break;
            }
        }
    }

    private void localpemiss() {
        //SDK在Android 6.0下需要进行运行检测的权限如下：
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.READ_PHONE_STATE

        //这里以ACCESS_COARSE_LOCATION为例
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE
            },
                   1);//自定义的code
        }


        AMapLocationClient mLocationClient = new AMapLocationClient(getContext());
        mLocationClient.startLocation();
    }


    public Activity getTopActivity()
    {
        return ActivityManagerUtils.getInstance().getTopActivity();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    private void initX52() {
        //如果没有这个内核，允许在WIFI情况下去下载内核
        QbSdk.setDownloadWithoutWifi(true);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                MLog.e(" onViewInitFinished is " + arg0);
                if(arg0){
                    MLog.e("X5内核初始化成功");
                }else {
                    MLog.e("X5内核初始化失败");
                    //initX5Web();
                }
            }
            @Override
            public void onCoreInitFinished() {

            }
        };
        //x5内核初始化接口
        try {
            QbSdk.initX5Environment(getApplicationContext(),  cb);
        }catch (Exception e){
            MLog.e("X5内核初始化异常");
        }

    }
}
