package com.nevs.car.web;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import java.lang.ref.WeakReference;

public class TalkWebViewCallback implements JavascriptCallback {
    private static final String TAG = "TalkWebViewCallback";
    private final WeakReference<Activity> mActivity;
    private Handler mCallBackHandler;
    
    public static final int MSG_UPDATE_CHANNEL = 10010;
    public static final int MSG_UPDATE_USERNAME = 10011;
    
    public TalkWebViewCallback(Context context) {
        this.mActivity = new WeakReference<Activity>((Activity) context);
    }
    
    public void setCallBackHandler(Handler mCallBackHandler){
        this.mCallBackHandler = mCallBackHandler;
    }

    public void toast(String toast) {
    }
    
    public void close() {
        mActivity.get().finish();
    }
    
    public void cmd(String command, String param1, String param2, String param3) {

        if (null == mActivity || null == mActivity.get()) {
            return;
        }
    }
    
    public void channelUpdated() {
        if (null != mCallBackHandler) {
            mCallBackHandler.sendEmptyMessage(MSG_UPDATE_CHANNEL);
        }
    }
    
    public void usernameUpdated() {
        if (null != mCallBackHandler) {
            mCallBackHandler.sendEmptyMessage(MSG_UPDATE_USERNAME);
        }
    }
    

}
