package com.nevs.car.tools.jsont;

import com.nevs.car.tools.util.MLog;

/**
 * Created by mac on 2018/4/24.
 * 解析返回数据的抽象子类
 *
 * 这里我们定义了一个抽象带两个类型参数的泛型类，第一个参数是Gson映射的外层对象，比如可以传入我们前面定义的HttpResult；
 * 第二个参数就是外层对象中的数据了，比如HttpResult中的data。

 其中enableShowToast()这个方法主要是用于拦截错误返回，用于统一处理所有请求的错误，默认只判断是否需要直接弹Toast显示错误信息。
 如果想直接将错误信息弹窗提示，只需要重写enableShowToast方法，返回true即可。
 */

public abstract class OnServerCallBack<T, V> extends HttpCallBack<T> {
    @Override
    public void onResolve(T t) {
        if (t instanceof HttpResult) {
            HttpResult<V> callbackData = (HttpResult) t;
            V result = callbackData.getData();
            if (callbackData.getIsSuccess().equals("Y")) {
                onSuccess(result);
            } else {
                onFailed(callbackData.getIsSuccess(), callbackData.getReason());
            }
        } else {
            onSuccess((V) t);
        }
    }

    @Override
    public void onFailed(String code, String msg) {
        if (enableShowToast()) {
           // ToastUtil.showText(msg);
            MLog.e(msg);
        } else {
            onFailure(code, msg);
        }
    }

    public abstract void onSuccess(V data);

    public abstract void onFailure(String code, String msg);

    public boolean enableShowToast() {
        return false;
    }
}
