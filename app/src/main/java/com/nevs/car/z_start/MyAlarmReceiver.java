package com.nevs.car.z_start;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nevs.car.constant.Constant;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.util.MLog;

/**
 * Created by mac on 2018/5/30.
 */

public class MyAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPHelper sharedPHelper = new SharedPHelper(context);
        sharedPHelper.put(Constant.PINISKILL,true);//表示PIN已经失效
        sharedPHelper.put(Constant.PINISKILLFINGER,true);//表示指纹已经失效
        MLog.e("失效");
//        Intent i = new Intent(context, LongRunningService.class);
//        context.startService(i); //循环服务
        Intent i = new Intent(context, LongRunningService.class);
        context.stopService(i);//停止服务


    }
}
