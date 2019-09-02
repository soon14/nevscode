package com.nevs.car.model;

/**
 * Created by mac on 2018/8/21.
 */

public class UpdateContentList {
//    "scheduleId": 0,
//            "runDuration": 0,
//            "scheduleType": 1,
//            "scheduleValue": "string"

    private String scheduleId;
    private int runDuration;
    private String scheduleValue;
    private int scheduleType;


    public UpdateContentList(String scheduleId, int runDuration, String scheduleValue, int scheduleType) {
        this.scheduleId = scheduleId;
        this.runDuration = runDuration;
        this.scheduleValue = scheduleValue;
        this.scheduleType = scheduleType;
    }

}
