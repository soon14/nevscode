package com.nevs.car.constant;

/**
 * Created by mac on 2018/4/4.
 */

public final class Constant {
    //public static final String ACCESSTOKENS="test_access_token";//访问令牌测试
    public static final String ACCESSTOKENS="login_userCenterAccessToken";//访问令牌测试

    /**
     * 缓存字段
     *  new SharedPHelper(context).get(Constant.ACCESSTOKEN,"")
     *  new SharedPHelper(context).get(Constant.LOGINNAME,"")
     * */
    public static final String SAFETY ="SAFETY";//安全验证
    public static final long SAFETYTIME=3*60*1000L;//安全验证间隔


    public static final String PEEMISSIONFILENAME="PEEMISSIONFILENAME";//授权权限名
    public static final String PEEMISSIONPARAMA="PEEMISSIONPARAMA";


    public static final int TIMESLUNXUN=6000;//轮询时间
    public static final int TIMESLUNXUNF=1000;//
    public static final String UPLOGFILENAME="UPLOGFILENAME";//
    public static final String UPPAMAS="UPPAMAS";//
    public static final String GETPAMAS="GETPAMAS";//


    public static final String LOGINERRORTIME = "loginErrorTime";
    public static final String LOGINERRORNUMBER = "loginErrorNumber";
    public static final String ACCESSTOKEN="accessToken";//访问令牌
    public static final String LOGINNAME="loginName";//登录名/手机号
    public static final String LOGINMOBILE="login_mobile";//
    public static final String NAMES="names";//姓名
    public static final String LOGINEMAIL="login_email";//
    public static final String LOGINFAMILYNAME="login_familyName";//
    public static final String LOGINGIVENNAMME="login_givenName";//
    public static final String LOGINIDNUM="login_idnum";//
    public static final String LOGINISPA="login_isPa";//
    public static final String LOGINSEX="login_sex";//
    public static final String LOGINISCAROWNER="login_iscarowner";//
    public static final String BOOKINGNO="booking_no";//取消预约带的参数
    public static final String LOGINRESCUE="login_rescue";//救援电话
    public static final String LOGINHOTLINE="login_hotline";//服务热线

    public static final String TSPISCAROWER="tspiscarower";//当前车是车主还是授权，YES是车主，NO是授权

    public static final String REGISTCENACCESSTOKEN="login_userCenterAccessToken";//注册返回
    public static final String REGISTNEVSUSERID="login_nevsUserID";//注册返回
    public static final String REGISTEXPIR="login_expir";//注册返回

    public static final String LOGINORGCODE="login_orgcode";//

    public static final String CARALIAS="CAR_ALIAS";//车昵称

    public static final String ISPLASHTO="ISPLASHTO";//记录启动页跳转的去向

    public static final String INTENTHTML="http://iotapp.iot.189.cn:9090/uapp/certifhtml/certif_entry.html?";//

    public static final String LONGINLASTNAME="LONGINLASTNAME";//保存上一次登录时的登录名

    public static final String ISCONFORM="isconformbind";//是否是点击绑车提示的确定

    public static final String ISRENZHENG="ISRENZHENG";//是否认证
    public static final String ISDISCON="ISDISCONN";//是否断开过
    public static final String ISDISCONNING="ISDISCONNING";//0为重连中，1为重连成功
    public static final String ISRECONINGSUC="ISRECONNINGSUC";//是否是重连成功状态 0为未成功1为成功
    public static final String ISSTARTBOOKING="ISSTARTBOOKING";//是否startbooking
    public static final String BLUESTEP="BLUESTEP";//蓝牙步骤
    public static final String BLUECONSTATE="BLUECONSTATE";//蓝牙连接状态
    public static final String CSRHEAD="CSRHEAD";//证书缓存文件名头部
    public static final String CSRDIGI="CSRDIGI";//CSR证书缓存文件CSRDIGI字段名
    public static final String TSPRXbleAddress="TSPRXbleAddress";//
    public static final String TSPRXtBoxCertificateContent="TSPRXtBoxCertificateContent";//
    public static final String TSPRXmobileCertificateContent="TSPRXmobileCertificateContent";//
    public static final String TSPPRIKEY="TSPPRIKEY";//
    public static final String FILEPUBZS="zx.pem";//TU
    public static final String FILEPUBKey="pb.pem";//
    public static final String FILEPRIKey="pr.pem";//
    public static final String FILEAPPZS="zs.pem";//手机证书
    public static final String MYNEVSCAR="/Android/data/com.nevs.car/files/";//   /mynevscar/files/
    public static final String MYNEVSCARPDF="/Android/data/com.nevs.car/pdfs/";//   pdf文件夹
    public static final String MYNEVSCARPDFCH="yhscpdfch";//   pdf文件名
    public static final String MYNEVSCARPDFEN="yhscpdfen";//   pdf文件名
//    public static final String FILEPUBZS="zx.crt";//
//    public static final String FILEPUBKey="pb.pem";//
//    public static final String FILEPRIKey="pr.pem";//

    public static final String LONGINTT="LONGINTT";//
    public static final String LONGINTTS="LONGINTTS";//

    public static final String SINGLEONE="single_one";// 判断单例控件
    public static final String SINGLETWO="single_two";//
    public static final String SINGLETHREE="single_three";//


    public static final String PHONENUMBER="15888888888";  //客服电话号码

    public static final String ISCLICKBLE="ISCLICKBLE";  //车控判断是否可以点击 0不可以1可以

    public static final String HISTORYFILENAME="HISTORYFILENAME";//历史纪录文件名
    public static final String HISTORYFILENAMESSS="HISTORYFILENAMESSS";//历史纪录文件名充电桩

    public static final String WEIXAPPID = "wx6eebbcf8e0971eb0";
    public static final String ICONBITMAPNAME = "icon";


    public static final String PINISKILL="PINISKILL";
    public static final String PINISKILLFINGER="PINISKILLFINGER";
    public static final String ISFINGER="ISFINGER";//是否开启指纹识别

    public static final String MISISDN="MISISDN";//车辆列表对应msisdn
    public static final String imsi="imsi";//车辆列表对应msisdn
    public static final String groupCode="groupCode";//
    public static final String isAuthenticated="isAuthenticated";//
    public static final String ISGUIDEFIRST="ISGUIDEFIRST";//是否首次显示导航

    public static final String ISCANCLE="iscancle";//绑车是否是返回的状态
    public static boolean isDebug = true;
    /***
     * A
     *
     * 内部接口
     */
    public static final class HTTP {
//        //BASE_URL
//        public static final String BASE_URL="http://192.168.1.12:8602/Service/";
//        //轮播界面拼接前缀
//        public static final String BANNERURL="http://192.168.1.12:8602/";
//        //驾照图片拼接前缀
//        public static final String BANNERURLDRIVE="http://192.168.1.12:8602";

//        //BASE_URL
//        public static final String BASE_URL="http://ispr.frpgz1.idcfengye.com/Service/";
//        //轮播界面拼接前缀
//        public static final String BANNERURL="http://ispr.frpgz1.idcfengye.com/";
//        //驾照图片拼接前缀
//        public static final String BANNERURLDRIVE="http://ispr.frpgz1.idcfengye.com";


// cc       //220.249.93.210:8602   http://220.249.93.210:8602/FacadeService.aspx?m=/
//                //BASE_URL   http://www.ispr.cn:8602/Service/News/90071003
//        public static final String BASE_URL="http://220.249.93.210:8602/Service/";
//        //轮播界面拼接前缀
//        public static final String BANNERURL="http://220.249.93.210:8602/";
//        //驾照图片拼接前缀
//        public static final String BANNERURLDRIVE="http://220.249.93.210:8602";

        public static String BASE_URL;
        public static String BANNERURL;
        public static String BASE_DATAURL;
        static {
            if(isDebug){
                BASE_URL="http://219.150.92.5:2021/Service/";
                //轮播界面拼接前缀
                BANNERURL="http://219.150.92.5:2021/";
                //流量
                BASE_DATAURL="http://219.150.92.5:2023/Service/";
            }else{
                BASE_URL="https://nevs-app-prod-api.nevs.cn:2021/Service/";
                //轮播界面拼接前缀
                BANNERURL="https://nevs-app-prod-api.nevs.cn:2021/";
                //流量
                BASE_DATAURL="https://nevs-app-prod-mno.nevs.cn:2023/Service/";
            }
        }

//       public static final String BASE_URL="http://219.150.92.5:2021/Service/";//旧
//        //82 public static final String BASE_URL="https://nevs-app-prod-api.nevs.cn:2021/Service/";//新
//        //轮播界面拼接前缀
//        public static final String BANNERURL="http://219.150.92.5:2021/";//旧
//        //82  public static final String BANNERURL="https://nevs-app-prod-api.nevs.cn:2021/";//新
//        //流量
//          public static final String BASE_DATAURL="http://219.150.92.5:2023/Service/";//旧
//        //82   public static final String BASE_DATAURL="https://nevs-app-prod-mno.nevs.cn:2023/Service/";//新

        //驾照图片拼接前缀
        public static final String BANNERURLDRIVE="http://219.150.92.5:2021";

        //新闻中部拼接
        public static final String NEWSCENTER="dmsFileStore/files/fs1";

        //行程分享：
        public  static final String TRIPSHARE="www.ispr.cn:8602/web/NEVSTripShares.html?TripId=";

        //连接失败返回参数定义
        public static final String HTTPFAIL="ERRORR";
        public static final String HTTPFAILEXIT="tokenCode Not Exists";
        public static final String HTTPMSG = "SmsTokenMissing";
        public static final String HTTPFAILEXITS="tokenCodeNotExists";
        public static final String userNameOrPwdError="userNameOrPwdError";
        public static final String userNameExisted="userNameExisted";


        public static final String GETMESSAGE="Verification";//短信验证码
        public static final String REGISTER="Register";//注册
        public static final String USERLOGIN="UserLogin";//登录

        public static final String PWDRESET="PwdReset";//调用验证用户名和短信验证码
        public static final String UPDATEUSERPWD="UpDateUserPwd";//忘记密码
        public static final String CHANGEPASSWORD="ChangePassword";//修改密码

        public static final String IMAGELIST="ImageList";//轮播
        public static final String NEWSLIST="NewsList";//新闻列表
        public static final String NEWS="News/";//新闻内容 跳入Webview
        public static final String PAPROXY="PaProxy";//个人代理申请
        public static final String USERFEEDBACK="UserFeedBack";//用户反馈信息
        public static final String UPDATEUSERINFO="UpdateUserInfo";//更新用户信息
        public static final String SERVICERESEVATION="ServiceResevation";//服务预约
        public static final String DEALERLIST="DealerList";//经销商，服务站列表
        public static final String VEHICLEBINDING="VehicleBinding";//车辆绑定
        public static final String POSITIONLIST="PositionList";//省市区列表
        public static final String MYBOOKING="MyBooking";//我的预约
        public static final String BOOKINGDETAILS="BookingDetails";//预约详情
        public static final String UPDATEBOOLINGDETAILS="UpdateBookingDetails";//更新预约
        public static final String CANCELBOOKINGDETAILS="CancelBookingDetails";//取消预约
        public static final String REPAIRMAINTAIN="RepairMaintain";//维修保养
        public static final String REPAIRDETAILS="RepairDetails";//查询维修详情
        public static final String SERVICEEALUTION="ServiceEalution";//维修评价
        public static final String CARTYPETASTING="CarTypeTasting";//车型品鉴
        public static final String DRIVERMANAGE="DriverManage";//上传驾照
        public static final String DRIVERMANAGEQUERY="DriverManageQuery";//获取驾照图片
        public static final String CERIFICATION="Cerification";//实名认证
        public static final String PAPROXYQUERY="PaProxyQuery";//个人代理线索管理
        public static final String NEWCLUE="NewClue";//个人代理新建线索
        public static final String IDTYPE="IDType";//获取证件类型
        public static final String ANNOUNCEMENT="Announcement";//获取公告
        public static final String PAUNPORXY="PaUnPorxy";//个人代理退出申请
        public static final String VHICLEHANDBOOK="VehicleHandbook";//用车手册
        public static final String USERNOTIFRE="UserNotifRe";//设置消息已读或删除
        public static final String GETSERVICEEALUTION="GetServiceEalution";//获取维修评价数量
        public static final String SETCARNICK="SetCarNick";//设置车昵称
        public static final String JUDGMENTUSEREXIST="JudgmentUserExist";//判断用户是否存在
        public static final String GETUSERSTATUS="GetUserStatus";//获取用户状态
        public static final String GETCARLIST="GetCarList";//获取车辆列表
        public static final String VEHICLEbINDVERIFY="Bindverify";//车辆绑定新接口
        public static final String VEHICLEUNBIND="VehicleUnBind";//车辆解绑
        public static final String USAGEDATA="UsageData";//流量查询
        public static final String ID2PHONE="ID2Phone";//ID转手机号
        public static final String GetB2CUserData="GetB2CUserData";//用户总量查询
        public static final String SearchB2CFlow="SearchB2CFlow";//用户余量查询

        public static final String TSPINTERFACECALL="TspInterfaceCall";//写入TSP调用日志
        public static final String IsysCertifByIccId="IsysCertifByIccId";//H5退出
    }




    /**
     * B
     *
     *
     * TSP接口
     * */
    public static final class TSP {
        public static String BASE_URL;
        static {
            if (isDebug) {
                BASE_URL = "https://nevs-cvp-qa-openapi.chinacloudsites.cn/v1/";
            } else {
                BASE_URL="https://nevs-cvp-prod-api.nevs.cn/v1/";
            }
        }
        //BASE_URL
      //  public static final String BASE_URL="https://Nlink.nevs.cn/v1/";
       // public static final String BASE_URL="https://nevs-cvp-qa-openapi.chinacloudsites.cn/v1";
    //    public static final String BASE_URL="https://nevs-cvp-qa-openapi.chinacloudsites.cn/swagger/";

//          public static final String BASE_URL="https://nevs-cvp-qa-openapi.chinacloudsites.cn/v1/";//旧
//        //82   public static final String BASE_URL="https://nevs-cvp-prod-api.nevs.cn/v1/";//新
      //  public static final String BASE_URL="http://nevs-cvp-sit-openapi.chinacloudsites.cn/v1/";

        //头部参数
        public static final String ACCEPT="Accept";
        public static final String AUTHORIZATION="Authorization";
        public static final String ACCEPTVALUE="application/json";
        public static final String AUTHORIZATIONVALUE="Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Im8wcnBUU0lFVzdzNV9NOVlBaWNXQzZaR0hzayIsImtpZCI6Im8wcnBUU0lFVzdzNV9NOVlBaWNXQzZaR0hzayJ9.eyJhdWQiOiJodHRwczovL25ldnN0ZWxlbWF0aWNzLnBhcnRuZXIub25tc2NoaW5hLmNuL25ldnMtY3ZwLWRldi1vcGVuYXBpIiwiaXNzIjoiaHR0cHM6Ly9zdHMuY2hpbmFjbG91ZGFwaS5jbi9kODAwMDdkOC03NDA2LTQ4MTQtYTExNi0wMjA2MGI0M2VjMzgvIiwiaWF0IjoxNTI4MTY3MjUwLCJuYmYiOjE1MjgxNjcyNTAsImV4cCI6MTUyODE3MTE1MCwiYWNyIjoiMSIsImFpbyI6IlkyQmdZTENRUFB6QmVWYU54OWNHbnFnQUtiUFB2aitQNXAyL0hscG9XdEk5YlptdWZ6OEEiLCJhbXIiOlsicHdkIl0sImFwcGlkIjoiOWJmODQ4MzctYTJjMS00ZDM2LWIyYmUtOWEzN2Y3NDZjM2M1IiwiYXBwaWRhY3IiOiIxIiwiaXBhZGRyIjoiNDIuMTU5LjQuNzMiLCJuYW1lIjoidGVzdG9wZW5hcGkiLCJvaWQiOiI5MmM2OGE1NS1mNDg0LTRiMjgtOWEyNC0xNmQ2MTZjNDU1ZGMiLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJzdWIiOiJVekpERVo2SjRwZnFPSng1ZUZ6enZtUXdfd1BUOGlwR3plX29FOWtIVF9jIiwidGlkIjoiZDgwMDA3ZDgtNzQwNi00ODE0LWExMTYtMDIwNjBiNDNlYzM4IiwidW5pcXVlX25hbWUiOiJ0ZXN0b3BlbmFwaUBuZXZzdGVsZW1hdGljcy5wYXJ0bmVyLm9ubXNjaGluYS5jbiIsInVwbiI6InRlc3RvcGVuYXBpQG5ldnN0ZWxlbWF0aWNzLnBhcnRuZXIub25tc2NoaW5hLmNuIiwidXRpIjoiT19selhkaTJqRWkzUWMwNFl4TWdBQSIsInZlciI6IjEuMCJ9.OMPvjLYbJfpjgmwsafZNdrOc2EyUjvQx3rIqG8ihF1PmoRurWpiYDzHD-H1CFRqCfaZ98sv6LRk0DWrnOO9cYCip-hf3fH1JKPYAa92Z1btmL1zwiT0X6HdSOCoGopasVTr6OFnXZm1SQemEryBpvekU1vHEJA79SgGQkKgrhXIdinv1hlA7GC26IhJyLqfdHdGQN5TlRiVEW7zGD92DyAmotD16gQvehJuHvfgbOhXmDaITLRJl_TvD2zWm8fDR5smYZ-XY3KVL6WEnPjl6BkVal03HxaVrZV8nxc5DunQGE0C_p-KpqiXouA8JLXAutr2maDwCRpeDHkIdcgKYMw";
        public static final String VIN="LTPCHINATELE00123";
        public static final String CONTENTTYPE="Content-Type";
        public static final String CONTENTTYPEVALUE="application/json";

        public static final String USERVEHICLE="vehicle/uservehicle";
        public static final String LOCATION="vehicle/location?vin=";
        public static final String STATUS="vehicle/status?vin=";
        public static final String AIRCONDITIONSTATUS="vehicle/airconditionstatus?vin=";
        public static final String BIND="vehicle/bind";
        public static final String AUTHORIZE="vehicle/authorize";
        public static final String AUTHORIZEUSERS="vehicle/authorizeusers?vin=";
        public static final String REVOKE="vehicle/revoke";
        public static final String HEALTH="vehicle/health?vin=";

        public static final String LOCK="remotecontrol/lock";
        public static final String VEHICLELIMITER="remotecontrol/vehiclelimiter";
        public static final String FLASH="remotecontrol/flash";
        public static final String AIRCONDITION="remotecontrol/airconditionon";
        public static final String AIRCONDITIOFF="remotecontrol/airconditionoff";
        public static final String SCHEDULEAIRCONDITIONER="remotecontrol/scheduleairconditioner?Vin=";

        //https://nevs-cvp-qa-openapi.chinacloudsites.cn/v1/remotecontrol/report?CommandId=11
       // public static final String COMMANDRESULT="remotecontrol/commandresult";
        public static final String COMMANDRESULT="remotecontrol/report?CommandId=";


        public static final String AIRDELETE="remotecontrol/scheduleairconditioner/delete";

        public static final String SET="geofence/set";
        public static final String GEOFENCE="geofence?vin=";
        public static final String DELETEGEOFENCE="geofence/delete";
        public static final String REPORT="remotecontrol/report?commandid=";
        public static final String SETTING="trip/setting?vin=";
        public static final String SETTINGSET="trip/settingset";
        public static final String HISTORY="trip/history?Vin=";
        public static final String SETTTRIPID="trip/detail?tripid=";
        public static final String SETTAG="trip/settag";
        public static final String DELETETRIP="trip/deletetrip";
        public static final String REGISTRATION="notification/registration";
        public static final String ALERTSET="notification/alertset";
        public static final String NOTIFICATION="notification";
        public static final String NOTIFICATIONID="notification?NotificationId=";
        public static final String NOTIFIHISTORY="notification/history";
        public static final String APPLY="digitalkey/apply";
        public static final String DOWNLOAD="digitalkey/download?CommandId=";
        public static final String SAVEPOI="poi/save";
        public static final String POI="poi";
        public static final String STATION="station";
        public static final String DELETEPOI="poi/delete";
        public static final String STATIONID="station/equipment?StationId=";
        public static final String GETVEHICLE="remotecontrol/vehiclelimiter?Vin=";
        public static final String DEFALT="vehicle/default";
        public static final String UPAIR="remotecontrol/scheduleairconditioner/update";
        public static final String NOTIFYDELETE="notification/delete";

    }

    public static final class Text{
      //  public static final String BASEURL="https://login.partner.microsoftonline.cn/nevstelematics.partner.onmschina.cn/oauth2/";  原测试TOKEN
        public static final String GETTESTTOKEN="token";
    }
}
