package com.nevs.car.model;

/**
 * Created by mac on 2018/7/3.
 */

public class UiError {
    public int errorCode;
    public String errorMessage;
    public String errorDetail;

    public UiError(int var1, String var2, String var3) {
        this.errorMessage = var2;
        this.errorCode = var1;
        this.errorDetail = var3;
    }
}
