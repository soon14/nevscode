package com.nevs.car.model;

/**
 * Created by mac on 2018/7/17.
 *
 * 参数名称	类型	是否必填	说明
 sysID	字符串	是	系统ID
 accessToken	字符串	是	国能访问令牌userCenterAccessToken
 noticeType	字符串	否	通知方式SmsCode = 短信,EMail = 邮件
 userInfo	对象	是
 userInfo结构：		是
 nickName	字符串	否	昵称,不更新则省略该字段
 surName	字符串	否	姓,不更新则省略该字段
 name	字符串	否	名,不更新则省略该字段
 sex	字符串	否	W = 女 M = 男,不更新则省略该字段
 eMail	字符串	否	邮箱,不更新则省略该字段
 phoneNo	字符串	否	电话,不更新则省略该字段
 birthday	日期	否	出生日期,不更新则省略该字段

 extend	object/字典	否	扩展属性字典,存在则更新，不存在则添加扩展属性 不更新则省略该字段

 */

public class UserInfoBean {
    private String nickName;
    private String surName;
    private String name;
    private String sex;
    private String eMail;

    public UserInfoBean(String nickName, String surName, String name, String sex, String eMail) {
        this.nickName = nickName;
        this.surName = surName;
        this.name = name;
        this.sex = sex;
        this.eMail = eMail;
    }
}
