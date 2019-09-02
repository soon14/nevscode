package com.nevs.car.tools.jsont;

/**
 * Created by mac on 2018/4/24.
 * 创建接口返回实体类
 * 以下面服务器返回数据格式为例：

 {
 "code":200,
 "msg":"成功",
 "data":{
 "userName":"小明",
 "nickName":"一花一世界"
 }
 }
 {"isSuccess":"Y",
 "reason":"",
 "data":{"smsToken":"eaa775a32bc24058cc6d7666b","msg":"134"}}
 */

public class HttpResult<T> {
    private String isSuccess;
    private String reason;
    private T data;

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ServerCallBackModel{" +
                "isSuccess='" + isSuccess + '\'' +
                ", reason='" + reason + '\'' +
                ", data=" + data +
                '}';
    }
}
