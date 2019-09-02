package com.nevs.car.tools.rx;

/**
 * Created by mac on 2018/5/17.
 */

public interface TspRxListener {
    void onSucc(Object obj);//正确输出
    void onFial(String str);//与服务器连接异常/错误输出
}
