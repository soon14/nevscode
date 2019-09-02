package com.nevs.car.model;

/**
 * Created by mac on 2018/6/29.
 */

public class ClueBean {
    private String name;
    private String phone;
    private String sex;
    private String intention_car;
    private String province_id;
    private String city_id;
    private String address;
    private String purpose;
    private String budget;
    private String source;
    private String dealer_id;

    public ClueBean(String name, String phone, String sex, String intention_car, String province_id, String city_id,String address, String purpose, String budget, String source, String dealer_id) {
        this.name = name;
        this.phone = phone;
        this.sex = sex;
        this.intention_car = intention_car;
        this.province_id = province_id;
        this.address = address;
        this.city_id = city_id;
        this.purpose = purpose;
        this.budget = budget;
        this.source = source;
        this.dealer_id = dealer_id;
    }
}
