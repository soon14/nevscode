package com.nevs.car.model;

import java.io.Serializable;

public class TspInterfaceCall implements Serializable{
    private String InterfaceName;
    private String Result;
    private String CallTime;
    private String ResultTime;
    private String Reason;
    private String CallBy;
    private String Args;

    public TspInterfaceCall(String interfaceName, String result, String callTime, String resultTime, String reason, String callBy, String args) {
        InterfaceName = interfaceName;
        Result = result;
        CallTime = callTime;
        ResultTime = resultTime;
        Reason = reason;
        CallBy = callBy;
        Args = args;
    }

    public String getInterfaceName() {
        return InterfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        InterfaceName = interfaceName;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getCallTime() {
        return CallTime;
    }

    public void setCallTime(String callTime) {
        CallTime = callTime;
    }

    public String getResultTime() {
        return ResultTime;
    }

    public void setResultTime(String resultTime) {
        ResultTime = resultTime;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getCallBy() {
        return CallBy;
    }

    public void setCallBy(String callBy) {
        CallBy = callBy;
    }

    public String getArgs() {
        return Args;
    }

    public void setArgs(String args) {
        Args = args;
    }
}
