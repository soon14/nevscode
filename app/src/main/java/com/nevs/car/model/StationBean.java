package com.nevs.car.model;

/**
 * Created by mac on 2018/8/3.
 */

public class StationBean {
    private String ParkFee;//停车收费
    private boolean EquipmentIsFree;//true表示只看空闲；false：表示全看
    private int StationType;//0：私人 1：专用 2：公用 3：其他 int
    private String Payment;//支付方式

    public StationBean(String parkFee, boolean equipmentIsFree, int stationType, String payment) {
        ParkFee = parkFee;
        EquipmentIsFree = equipmentIsFree;
        StationType = stationType;
        Payment = payment;
    }

    public String getParkFee() {
        return ParkFee;
    }

    public void setParkFee(String parkFee) {
        ParkFee = parkFee;
    }

    public boolean isEquipmentIsFree() {
        return EquipmentIsFree;
    }

    public void setEquipmentIsFree(boolean equipmentIsFree) {
        EquipmentIsFree = equipmentIsFree;
    }

    public int getStationType() {
        return StationType;
    }

    public void setStationType(int stationType) {
        StationType = stationType;
    }

    public String getPayment() {
        return Payment;
    }

    public void setPayment(String payment) {
        Payment = payment;
    }
}
