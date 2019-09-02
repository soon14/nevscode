package com.nevs.car.tools.rx.rxandroid;

import rx.Observable;

/**
 * Created by mac on 2018/4/23.
 */

public abstract class MyOnSubscribe<C> implements Observable.OnSubscribe<C> {
    private C c;

    public MyOnSubscribe(C c) {
        setT(c);
    }

    public C getT() {
        return c;
    }

    public void setT(C c) {
        this.c = c;
    }


}