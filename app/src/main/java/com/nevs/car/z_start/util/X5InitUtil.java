package com.nevs.car.z_start.util;

import com.nevs.car.tools.util.MLog;
import com.nevs.car.z_start.MyApp;
import com.tencent.smtt.sdk.QbSdk;

/**
 * created by chenjun on 2019-08-16
 */
public class X5InitUtil {
    public static void initX52() {
        //如果没有这个内核，允许在WIFI情况下去下载内核
        QbSdk.setDownloadWithoutWifi(true);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                MLog.e(" onViewInitFinished is " + arg0);
                if (arg0) {
                    MLog.e("X5内核初始化成功");
                } else {
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
            QbSdk.initX5Environment(MyApp.getInstance(), cb);
        } catch (Exception e) {
            MLog.e("X5内核初始化异常");
        }

    }
}
