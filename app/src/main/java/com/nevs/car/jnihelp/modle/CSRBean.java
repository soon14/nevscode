package com.nevs.car.jnihelp.modle;

import java.io.Serializable;

public class CSRBean implements Serializable{
    private String vin;
    private String username;
    private String starttime;
    private String endtime;
    private String pin;
    private String mobiledevicepubkey;
    private String role;
    private String bookingid;
    private String userid;

    public CSRBean(String vin, String username, String starttime, String endtime, String pin, String mobiledevicepubkey, String role, String bookingid, String userid) {
        this.vin = vin;
        this.username = username;
        this.starttime = starttime;
        this.endtime = endtime;
        this.pin = pin;
        this.mobiledevicepubkey = mobiledevicepubkey;
        this.role = role;
        this.bookingid = bookingid;
        this.userid = userid;
    }
}
