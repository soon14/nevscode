package com.nevs.car.tools.Base;

/**
 * Created by mac on 2018/4/9.
 */

public abstract class BasePresenter<T>{
    public T mView;

    public void attach(T mView) {
        this.mView = mView;
    }

    public void dettach() {
        mView = null;
        System.gc();
    }
}
