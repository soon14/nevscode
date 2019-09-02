package com.nevs.car.model;

/**
 * Created by mac on 2018/4/18.
 */

public class MyCarItem {
    private String imageUrl;
    private String state;
    private String name;
    private String type;

    public MyCarItem(String imageUrl, String state, String name,String type) {
        this.imageUrl = imageUrl;
        this.state = state;
        this.type = type;
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
