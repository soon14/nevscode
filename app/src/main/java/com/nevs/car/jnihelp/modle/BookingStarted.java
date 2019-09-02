package com.nevs.car.jnihelp.modle;

import java.io.Serializable;

/**
 * Created by mac on 2018/8/18.
 */

public class BookingStarted implements Serializable{
    private String Response;
    private int ErrCode;
    private String ErrInf;

    public BookingStarted(String response, int errCode, String errInf) {
        Response = response;
        ErrCode = errCode;
        ErrInf = errInf;
    }
}
