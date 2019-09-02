package com.nevs.car.model;

/**
 * Created by mac on 2018/6/7.
 */

public class ChooseCarItem {
    private String imageUrl;
    private String content;
    private String type;
    private String number;

    public ChooseCarItem(String imageUrl, String content, String type, String number) {
        this.imageUrl = imageUrl;
        this.content = content;
        this.type = type;
        this.number = number;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
