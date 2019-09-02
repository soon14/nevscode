package com.nevs.car.model;

import java.io.Serializable;

public class AdCodeBean implements Serializable {
private String adcode;
private String name;

    public AdCodeBean(String adcode, String name) {
        this.adcode = adcode;
        this.name = name;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
