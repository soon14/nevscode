package com.nevs.car.mvp;

/**
 * Created by mac on 2018/4/5.
 */

public interface OnRequestListener {

    void onSuccess(String data);
    void onFailed(String error);

}
