package com.nevs.car.tools.rx;

/**
 * Created by mac on 2018/4/25.
 * 网路请求接口回调监听器
 */

public interface HttpRxListener {
    void onSucc(Object obj);//正确输出
    void onFial(String str);//与服务器连接异常/错误输出
}
