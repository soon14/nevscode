package com.nevs.car.tools.jsont;

import com.nevs.car.tools.util.MLog;

/**
 * Created by mac on 2018/4/24.
 * 不解析返回数据的抽象子类
 *
 * 此泛型类用于直接返回Http返回的数据。有些时候我们不需要对Http返回的数据进行解析，
 * 这时我们就可以使用此种解析方式来解析。
 */

public abstract class OnCommonCallBack<T> extends HttpCallBack<T> {
    @Override
    public void onResolve(T t) {
        onSuccess(t);
    }

    @Override
    public void onFailed(String code, String msg) {
        if (enableShowToast()) {
            //ToastUtil.showText(msg);
            MLog.e(msg);
        } else {
            onFailure(code, msg);
        }
    }

    public abstract void onSuccess(T data);

    public abstract void onFailure(String code, String msg);

    public boolean enableShowToast() {
        return false;
    }
}
