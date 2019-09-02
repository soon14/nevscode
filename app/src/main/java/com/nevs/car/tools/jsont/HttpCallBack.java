package com.nevs.car.tools.jsont;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by mac on 2018/4/24.
 * 回调的抽象基类
 *
 * Type：Java中所有类型的公共高级接口。包括原始类型(对应Class)、参数化类型(对应ParameterizedType)、
 * 数组类型(对应GenericArrayType)、类型变量(对应TypeVariable)和基本类型(对应Class)。
 getGenericSuperclass()：获得带有泛型的父类。
 ParameterizedType：参数化类型，即泛型。
 getActualTypeArguments()：获取参数化类型的数组，泛型参数可能有多个。
 getActualTypeArguments()[0]得到泛型的第一个参数T的类型，赋值给当前泛型类型的成员变量genericSuperclass。
 如果不是参数化类型，比如在接收服务器回调的地方没有加泛型类型，那么我们就给genericSuperclass赋值Object的Class对象。

 其中的抽象方法：

 onResolve()：用来解析接口返回的json数据，在子类中需要重写它来进行具体解析。
 onFailed()：错误回调。
 */

public abstract class HttpCallBack<T> {
    private Type mGenericSuperclass;

    public HttpCallBack() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            mGenericSuperclass = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        } else {
            mGenericSuperclass = Object.class;
        }
    }

    public abstract void onResolve(T t);

    public abstract void onFailed(String code, String msg);

    public Type getType() {
        return mGenericSuperclass;
    }
}
