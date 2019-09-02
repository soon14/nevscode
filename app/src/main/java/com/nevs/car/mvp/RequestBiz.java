package com.nevs.car.mvp;

import android.content.Context;

/**
 * Created by mac on 2018/4/5.
 */

public interface RequestBiz {
    /**
     *  登录逻辑处理
     * *@param username   用户名
     * *@param password   密码
     * *@param callBack   结果回调
     */
    void requestForData(Context context,String tjson, OnRequestListener listener);
}
