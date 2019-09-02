package com.nevs.car.jnihelp.modle;

import java.io.Serializable;

/**
 * Created by mac on 2018/8/13.
 */

public class QBean implements Serializable{
private long MD_nonce;
private DigitalKeyBean DigitalKey;

    public QBean(long MD_nonce, DigitalKeyBean digitalKey) {
        this.MD_nonce = MD_nonce;
        DigitalKey = digitalKey;
    }

    public long getMD_nonce() {
        return MD_nonce;
    }

    public void setMD_nonce(long MD_nonce) {
        this.MD_nonce = MD_nonce;
    }

    public DigitalKeyBean getDigitalKey() {
        return DigitalKey;
    }

    public void setDigitalKey(DigitalKeyBean digitalKey) {
        DigitalKey = digitalKey;
    }
}
