package com.nevs.car.z_start;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nevs.car.tools.util.WXShare;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by mac on 2018/7/3.
 */

public class AppRegister extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final IWXAPI api = WXAPIFactory.createWXAPI(context, null);

        // 将该app注册到微信
        api.registerApp(WXShare.APP_ID);
    }
}

