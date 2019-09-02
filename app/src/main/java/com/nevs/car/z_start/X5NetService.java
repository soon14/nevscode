package com.nevs.car.z_start;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.nevs.car.tools.util.MLog;
import com.tencent.smtt.sdk.QbSdk;

public class X5NetService extends IntentService {

    public static final String TAG = "x5webview";
    public X5NetService(){
        super(TAG);
    }
    public X5NetService(String name) {
        super(TAG);
    }

    @Override
    public void onHandleIntent(@Nullable Intent intent) {
        // initX5Web();
        initX52();
    }
    public void initX5Web() {
        if (!QbSdk.isTbsCoreInited()) {
            // 设置X5初始化完成的回调接口
            QbSdk.preInit(getApplicationContext(), null);
        }
        QbSdk.initX5Environment(getApplicationContext(), cb);
        MLog.e("X5NetService");
    }

    QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
        @Override
        public void onViewInitFinished(boolean arg0) {
            // TODO Auto-generated method stubCheckNetUtil
            //MLog.e(" X5内核初始化onViewInitFinished is " + arg0);
            if(arg0){
                MLog.e("X5内核初始化成功");
            }else {
                MLog.e("X5内核初始化失败");
                //initX5Web();
            }
        }
        @Override
        public void onCoreInitFinished() {
            // TODO Auto-generated method stub
        }
    };

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




