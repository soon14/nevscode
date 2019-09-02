package com.nevs.car.jnihelp.modle;

import java.io.Serializable;

/**
 * Created by mac on 2018/8/17.
 */

public class Qstatbooking implements Serializable{
    private long TU_nonce;

    public Qstatbooking(long TU_nonce) {
        this.TU_nonce = TU_nonce;
    }
}
