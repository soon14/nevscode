package com.nevs.car.model;

import java.io.Serializable;
import java.util.List;

public class ProCityBean implements Serializable {
    private List<ProInBean> list;

    public ProCityBean(List<ProInBean> list) {
        this.list = list;
    }

    public List<ProInBean> getList() {
        return list;
    }

    public void setList(List<ProInBean> list) {
        this.list = list;
    }
}
