package com.nevs.car.model;

import java.util.List;

public class JsonMessage
{
    public List<data> data;

    public class data
    {
        public String phone;
        public String appType;
        public String deviceID;

        public String getPhone() {
            return phone;
        }

        public String getAppType() {
            return appType;
        }

        public String getDeviceID() {
            return deviceID;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setAppType(String appType) {
            this.appType = appType;
        }

        public void setDeviceID(String deviceID) {
            this.deviceID = deviceID;
        }
    }

    public List<JsonMessage.data> getData()
    {
        return data;
    }

    public void setData(List<JsonMessage.data> data)
    {
        this.data = data;
    }
}
