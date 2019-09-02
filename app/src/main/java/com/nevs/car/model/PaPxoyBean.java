package com.nevs.car.model;

import java.io.Serializable;

/**
 * Created by mac on 2018/4/26.
 */

public class PaPxoyBean implements Serializable {
    //"sex","local","address","certification","orgCode","accessToken"
    private String sex;
    private String local;
    private String address;
    private String certification;
    private String orgCode;
    private String accessToken;

    public PaPxoyBean(String sex, String local, String address, String certification, String orgCode, String accessToken) {
        this.sex = sex;
        this.local = local;
        this.address = address;
        this.certification = certification;
        this.orgCode = orgCode;
        this.accessToken = accessToken;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
}
