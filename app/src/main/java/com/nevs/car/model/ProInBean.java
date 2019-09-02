package com.nevs.car.model;

import java.io.Serializable;
import java.util.List;

public class ProInBean implements Serializable {
    private String provice;
    private List<AdCodeBean> city;

    public ProInBean(String provice, List<AdCodeBean> city) {
        this.provice = provice;
        this.city = city;
    }

    public String getProvice() {
        return provice;
    }

    public void setProvice(String provice) {
        this.provice = provice;
    }

    public List<AdCodeBean> getCity() {
        return city;
    }

    public void setCity(List<AdCodeBean> city) {
        this.city = city;
    }
}
