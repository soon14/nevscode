package com.nevs.car.model;

/**
 * Created by mac on 2018/5/25.
 * set 接口里面的参数名，设置电子围栏
 */

public class CircleBean {
    private float centerPointLongitude;
    private float centerPointLatitude;
    private int radius;

    public CircleBean(float centerPointLongitude, float centerPointLatitude, int radius) {
        this.centerPointLongitude = centerPointLongitude;
        this.centerPointLatitude = centerPointLatitude;
        this.radius = radius;
    }
}
