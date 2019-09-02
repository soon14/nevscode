package com.nevs.car.model;

import java.io.Serializable;

/**
 * Created by mac on 2018/6/29.
 * [{"name":"小吃","phone":"15899999999","sex":"10021003","province_id":"2001","province_name":"广东省",
 * "city_id":"2041","city_name":"深圳市","create_by":"20064","org_name":"聂经","vmodel":"X9-3",
 * "address":"小路和平社区","status":"30251001","budget":"100","purpose":"家用"},
 */

public class ProxyallBean implements Serializable{
    private int id;
    private String name;
    private String phone;
    private String sex;
    private String province_name;
    private String city_name;
    private String vmodel;
    private String address;
    private String purpose;
    private String budget;

    public ProxyallBean(int id,String name, String phone, String sex, String province_name, String city_name, String vmodel, String address, String purpose, String budget) {
        this.id=id;
        this.name = name;
        this.phone = phone;
        this.sex = sex;
        this.province_name = province_name;
        this.city_name = city_name;
        this.vmodel = vmodel;
        this.address = address;
        this.purpose = purpose;
        this.budget = budget;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getVmodel() {
        return vmodel;
    }

    public void setVmodel(String vmodel) {
        this.vmodel = vmodel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }
}
