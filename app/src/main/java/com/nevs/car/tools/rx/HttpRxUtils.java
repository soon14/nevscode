package com.nevs.car.tools.rx;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.rxandroid.RxjavaUtil;
import com.nevs.car.tools.rx.rxandroid.UITask;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by mac on 2018/4/23.
 */

public class HttpRxUtils {
    public interface UpTspLogListen{
        void upTsp(String str);
    }
    public static UpTspLogListen callback;
    public static void setUpTspLogListen(UpTspLogListen upTspLogListen){
        callback=upTspLogListen;
    }
    public static void toSend(String str){
        if(callback!=null) {
            callback.upTsp(str);
        }
    }

    //RXAndroid
    public static void rxAndroidSuccess(Object obj, final HttpRxListener httpRxListener){
        RxjavaUtil.doInUIThread(new UITask<Object>(obj) {
            @Override
            public void doInUIThread() {
                httpRxListener.onSucc(getT());
            }
        });
    }

    public static void rxAndroidFail(String str, final HttpRxListener httpRxListener){
        RxjavaUtil.doInUIThread(new UITask<String>(str) {
            @Override
            public void doInUIThread() {
                httpRxListener.onFial(getT());
            }
        });
    }

    public static List<HashMap<String,Object>> xJson(String json,List<HashMap<String,Object>> list){
        list.clear();
        Gson gson=new Gson();
        JsonParser parser=new JsonParser();
        JsonObject jsonObj=parser.parse(json).getAsJsonObject();
        JsonArray jsonArray=jsonObj.getAsJsonArray("data");
        Type type = new TypeToken<HashMap<String,Object>>() {}.getType();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement el = jsonArray.get(i);
            HashMap<String, Object> tmp = gson.fromJson(el, type);
            list.add(tmp);
           // MLog.e("标题：" + list.get(i).get("newsID"));
        }
                 return list;
    }


    /**
     *
     *接口
     *
     * */

    //1登录
    public static void getLogin(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        MobclickAgent.onEvent(context,"UserLogin");
        final SharedPHelper sharedPHelper=new SharedPHelper(context);
        Novate novate = new Novate.Builder(context)
                .connectTimeout(2000)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("登录索参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.USERLOGIN,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(e+"",httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {

                /**
                 * {"isSuccess":"Y","reason":"","data":{"loginName":"13109093390","mobile":"13109093390",
                 * "name":"qwerty","email":"777777777@qq.com","familyName":"qqqq","givenName":"BCT ",
                 * "idnum":"","isPa":"Und","sex":"M","accessToken":"eb5c077143594efcb839f86729a97010","cars":[]
                 *
                 *
                 *
                 * {"isSuccess":"Y","reason":"","data":{"loginName":"13554093709","mobile":"13554093709","name":"","email":"","familyName":"小","givenName":"陈","idnum":"","isPa":"No","sex":"None","isCarOwner":"","accessToken":"ca35814131d14653b92a35b4a1a913a4","userCenterAccessToken":"f0010368dea44f37b74a51453261d09e","expir":"60","nevsUserID":"b9404e8507174827ad256f1fc84150ea","cars":[]}}
                 *
                 *
                 *
                 * {"isSuccess":"Y","reason":"","data":{"loginName":"17671642181","mobile":"17671642181","name":"哈哈","email":"123456789@qq.com","familyName":"tom JAVA","givenName":"tom","idnum":"","isPa":"Yes","sex":"W","isCarOwner":"YES","orgCode":"YTD","accessToken":"58b0648d6b1e4768b640daf758aff6ca","userCenterAccessToken":"8d377627cab249bdb85bbfc361266d79","expir":"60","nevsUserID":"bfd75ef4961b49c69fdbe793633e56cb","cars":[{"bindingId":"a973c93f-58dc-4507-b43b-f31ae88987c6","vin":"LTPSBSIMULATOR001","iccid":"89860317352002707001","msisdn":"15502187056","bleAddress":"ble","relationType":"车主","startTime":"1525658148","endTime":"1525658148","permissions":["1","2"],"nevsUserID":"bfd75ef4961b49c69fdbe793633e56cb"},{"bindingId":"e3e22df0-39de-4233-bdbb-c9f2c9f9766c","vin":"LTPCHINATELE00123","iccid":"8986031735200270704","msisdn":"155021870561","bleAddress":"00-12-23-12-54-37","relationType":"车主","startTime":"1532399187","endTime":"1532399187","permissions":null,"carType":"No","digitalKey":"","color":"F","groupName":"9-3-滴滴订制","groupEnName":"NISSAN","isDefault":"No","groupCode":"X9-3","licDate":"","licTelecontrol":"","licDoorcontrol":"","licSearchcar":"","licAccontrol":"","nevsUserID":"bfd75ef4961b49c69fdbe793633e56cb"},{"bindingId":"e3e22df0-39de-4233-bdbb-c9f2c9f9766c","vin":"LTPCHINATELE00123","iccid":"8986031735200270704","msisdn":"155021870561","bleAddress":"00-12-23-12-54-37","relationType":"车主","startTime":"1532399187","endTime":"1532399187","permissions":null,"carType":"No","digitalKey":"","color":"F","groupName":"9-3-滴滴订制","groupEnName":"NISSAN","isDefault":"No","groupCode":"X9-3","licDate":"","licTelecontrol":"","licDoorcontrol":"","licSearchcar":"","licAccontrol":"","nevsUserID":"bfd75ef4961b49c69fdbe793633e56cb"},{"bindingId":"be78176927a64d3db93d0806cf2fbedd","vin":"LTPCHINATELE00124","iccid":"8986031735200270705","msisdn":"155021870562","bleAddress":"00-12-23-12-54-37","relationType":"车主","startTime":"1532399187","endTime":"1532399187","permissions":null,"nevsUserID":"bfd75ef4961b49c69fdbe793633e56cb"}]}}
                 07-24 10:26:27.560 3579-3579/com.nevs.
                 * */
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            JSONObject data=jsonObject.getJSONObject("data");
                            String accessToken=data.getString("accessToken");
                            String loginName=data.getString("loginName");
                            String mobile=data.getString("mobile");
                            String name=data.getString("name");
                            String email=data.getString("email");
                            String familyName=data.getString("familyName");
                            String givenName=data.getString("givenName");
                            String idnum=data.getString("idnum");
                            String isPa=data.getString("isPa");
                            String sex=data.getString("sex");
                            String isCarOwner=data.getString("isCarOwner");
                            sharedPHelper.put(Constant.ACCESSTOKEN,accessToken);//
                            sharedPHelper.put(Constant.LOGINNAME,loginName);//
                            sharedPHelper.put(Constant.LOGINMOBILE,mobile);//
                            sharedPHelper.put(Constant.NAMES,name);//
                            sharedPHelper.put(Constant.LOGINEMAIL,email);//
                            sharedPHelper.put(Constant.LOGINFAMILYNAME,familyName);//
                            sharedPHelper.put(Constant.LOGINGIVENNAMME,givenName);//
                            sharedPHelper.put(Constant.LOGINIDNUM,idnum);//
                            sharedPHelper.put(Constant.LOGINISPA,isPa);//
                            sharedPHelper.put(Constant.LOGINSEX,sex);//
                            sharedPHelper.put(Constant.LOGINISCAROWNER,isCarOwner);//
                            MLog.e("accessTokenn:"+sharedPHelper.get(Constant.ACCESSTOKEN,""));
                            MLog.e("loginName:"+sharedPHelper.get(Constant.LOGINNAME,""));

                            sharedPHelper.put(Constant.LOGINRESCUE,data.getString("rescue"));
                            sharedPHelper.put(Constant.LOGINHOTLINE,data.getString("hotline"));

                            sharedPHelper.put(Constant.REGISTCENACCESSTOKEN,data.getString("userCenterAccessToken"));
                            sharedPHelper.put(Constant.REGISTNEVSUSERID,data.getString("nevsUserID"));
                            sharedPHelper.put(Constant.REGISTEXPIR,data.getString("expir"));
                            sharedPHelper.put(Constant.LOGINORGCODE,data.getString("orgCode"));
                            MLog.e("LOGINORGCODE:"+sharedPHelper.get(Constant.LOGINORGCODE,""));

                           // sharedPHelper.put("LOGINJSON",json);//
                            rxAndroidSuccess(json,httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e(e+"");
                }
                MLog.e("获取登录-->" + json);
            }
        });
    }



    //2获取短信验证码
    public static void getMessageCode(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        MobclickAgent.onEvent(context,"Verification");
        final List<String> list=new ArrayList();
        list.clear();
        //三方框架网络请求
        Novate novate=new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("获取短信验证码参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.GETMESSAGE,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }
            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("短信验证码获取-->"+json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){//判断是否成功获取，如果成功就解析，如果失败就看返回的原因
                        case "Y":
                            JSONObject data=jsonObject.getJSONObject("data");
                            String smsToken=data.getString("smsToken");
                            String msg=data.getString("msg");
                            list.add(0,smsToken);
                            list.add(1,msg);
                            rxAndroidSuccess(list,httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //3注册
    public static void getRegister(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        MobclickAgent.onEvent(context,"Regist");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("注册参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.REGISTER,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("获取注册-->" + json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    //忘记密码界面4，第二个接口获取userPwdToken
    public static void getUserPwdToken(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        MobclickAgent.onEvent(context,"PwdReset");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("忘记密码界面4，第二个接口获取userPwdToken参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.PWDRESET,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("获取userPwdToken-->" + json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            JSONObject data=jsonObject.getJSONObject("data");
                            String userPwdToken=data.getString("userPwdToken");
                            rxAndroidSuccess(userPwdToken,httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    //忘记密码5
    public static void getUpDateUserPwd(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        MobclickAgent.onEvent(context,"UpDateUserPwd");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("忘记密码参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.UPDATEUSERPWD,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("获取重置密码-->" + json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //服务界面轮播6
    public static void getImageList(Context context,String keys[],Object values[],final HttpRxListener httpRxListener) {
        MobclickAgent.onEvent(context,"ImageList");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("服务界面轮播参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.IMAGELIST,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL, httpRxListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list), httpRxListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason, httpRxListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
               MLog.e("获取轮播-->" + json);
            }
        });
    }

    //新闻列表7
    public static void getNewsList(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"NewsList");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("新闻列表参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.NEWSLIST,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取新闻列表-->" + json);
            }
        });
    }

    //新闻内容8  没有此接口 跳入Webview
    public static void getNews(Context context,String keys[],Object values[],final HttpRxListener httpRxListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("新闻内容参数："+HashmapTojson.getJson(keys,values));
        novate.json("20",HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                Log.e("TAG", "onError-->" + e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {

                String json = null;
                try {
                    json = new String(responseBody.bytes());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取-->" + json);
            }
        });
    }

    //个人代理申请9 传递FORM表单
    public static void getPaProxy(Context context, String keys[], Object values[], List<File> files,final HttpRxListener httpoListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(keys[0],String.valueOf(values[0]))
                .addFormDataPart(keys[1],String.valueOf(values[1]))
                .addFormDataPart(keys[2],String.valueOf(values[2]))
                .addFormDataPart(keys[3],String.valueOf(values[3]))
                .addFormDataPart(keys[4],String.valueOf(values[4]))
                .addFormDataPart(keys[5],String.valueOf(values[5]))
                .addFormDataPart("file", files.get(0).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(0)))
                .addFormDataPart("file", files.get(1).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(1)))
                .addFormDataPart("file", files.get(2).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(2)))
                .build();
        novate.upload(Constant.HTTP.PAPROXY, requestBody, new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获个人代理申请-->" + json);
            }
        });

    }

    //10用户反馈
    public static void getUserFeedBack(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("用户反馈参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.USERFEEDBACK,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("获取用户反馈-->" + json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //11 服务预约
    public static void getServiceResevation(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        MobclickAgent.onEvent(context,"ServiceResevation");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.SERVICERESEVATION,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("获取服务预约-->" + json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //12 更新用户信息
    public static void getUpdateUserInfo(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        MobclickAgent.onEvent(context,"UpdateUserInfo");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("更新用户信息："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.UPDATEUSERINFO,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("获取更新用户信息-->" + json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //13 车辆绑定
    public static void getVehicleBinding(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("车辆绑定参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.VEHICLEBINDING,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("获取车辆绑定-->" + json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //省市区列表14
    public static void getPositionList(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"PositionList");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("省市区列表参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.POSITIONLIST,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                           rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取省市区列表-->" + json);
            }
        });
    }

    //经销商列表15
    public static void getDealerList(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"DealerList");
        final List<HashMap<String,Object>> list=new ArrayList<>();
        list.clear();
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("经销商列表"+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.DEALERLIST,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {

                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取经销商列表-->" + json);
            }
        });
    }

    //16 更改密码
    public static void getChangePassword(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        MobclickAgent.onEvent(context,"ChangePassword");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("更改密码"+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.CHANGEPASSWORD,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("获取更改密码-->" + json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //我的预约 17
    public static void getMyBooking(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"MyBooking");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("我的预约参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.MYBOOKING,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                list.clear();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取我的(历史)预约-->" + json);
            }
        });
    }


    //预约详情 18
    public static void getBookingDetails(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("预约详情参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.BOOKINGDETAILS,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取预约详情-->" + json);
            }
        });
    }

    //更新预约 19
    public static void getUpdateBookingDetails(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("更新预约参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.UPDATEBOOLINGDETAILS,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取更新预约-->" + json);
            }
        });
    }

    //取消预约 20
    public static void getCancelBookingDetails(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("取消预约参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.CANCELBOOKINGDETAILS,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取取消预约-->" + json);
            }
        });
    }

    //维修保养 21
    public static void getRepairMaintain(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"RepairMaintain");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("维修保养参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.REPAIRMAINTAIN,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取维修保养-->" + json);
            }
        });
    }

    //查询维修详情 22
    public static void getRepairDetails(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"RepairDetails");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("查询维修详情参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.REPAIRDETAILS,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<String> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
//                           JSONObject jsondata=jsonObject.getJSONObject("data");
//                            JSONArray jsonArraypart=jsondata.getJSONArray("part");
//                            JSONArray jsonArrayworkhouts=jsondata.getJSONArray("work_houts");
//
//                            list.add(0,jsondata.getString("vin"));
//                            list.add(1,jsondata.getString("license_no"));
//                            list.add(2,jsondata.getString("ro_type_name"));
//                            list.add(3,jsondata.getString("deliver"));
//                            list.add(4,jsondata.getString("deliver_mobile"));
//                            list.add(5,jsondata.getString("delivery_time"));
//                            list.add(6,jsondata.getString("assign_time"));
//                            list.add(7,jsondata.getString("order_by_name"));
//                            list.add(8,jsondata.getString("order_by_phone"));

                            rxAndroidSuccess(json,httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取查询维修详情-->" + json);
            }
        });
    }

    //23 维修评价
    public static void getServiceEalution(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        MobclickAgent.onEvent(context,"ServiceEalution");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("维修评价参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.SERVICEEALUTION,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("获取维修评价-->" + json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //车型品鉴 24
    public static void getCarTypeTastings(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"CarTypeTatsing");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("车型评鉴参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.CARTYPETASTING,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取车型品鉴-->" + json);
            }
        });
    }

    //上传驾照25
    public static void getDriverManage(Context context,String keys[], Object values[],List<File> files,final HttpRxListener httpoListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(keys[0],String.valueOf(values[0]))
                .addFormDataPart(keys[1],String.valueOf(values[1]))
                .addFormDataPart("file", files.get(0).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(0)))
                .addFormDataPart("file", files.get(1).getName(), RequestBody.create(MediaType.parse("image/*"), files.get(1)))
                .build();
        novate.upload(Constant.HTTP.DRIVERMANAGE, requestBody, new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获个上传驾照-->" + json);
            }
        });
    }

    //获取驾照图片 26
    public static void getDriverManageQuery(Context context,String keys[],Object values[],final HttpRxListener httpRxListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("获取驾照参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.DRIVERMANAGEQUERY,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL, httpRxListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<String> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            JSONObject data=jsonObject.getJSONObject("data");
                            String front_pic=data.getString("front_pic");
                            String ob_pic=data.getString("ob_pic");
                            list.add(0,front_pic);
                            list.add(1,ob_pic);
                            rxAndroidSuccess(list, httpRxListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason, httpRxListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取驾照图片-->" + json);
            }
        });
    }

    //27 实名认证
    public static void getCerification(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("实名认证参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.CERIFICATION,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("实名认证-->" + json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //个人代理线索管理 28
    public static void getPaProyQuery(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("个人代理线索管理参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.PAPROXYQUERY,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("个人代理线索管理-->" + json);
            }
        });
    }

    //个人代理新建线索 29
    public static void getNewClue(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("个人代理新建线索参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.NEWCLUE,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("个人代理新建线索-->" + json);
            }
        });
    }

    //获取证件类型 30
    public static void getIdtype(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("获取证件类型参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.IDTYPE,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取证件类型-->" + json);
            }
        });
    }

    //获取公告 31
    public static void getAnnouncement(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"Announcement");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("获取公告参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.ANNOUNCEMENT,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取公告-->" + json);
            }
        });
    }
    //32个人代理取消申请
    public static void getPaUnPorxy(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("个人代理取消申请参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.PAUNPORXY,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("个人代理取消申请-->" + json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //用车手册 33
    public static void getVehicleHandbook(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("用车手册参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.VHICLEHANDBOOK,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("用车手册-->" + json);
            }
        });
    }

    //设置消息已读或删除 34
    public static void getUserNotifRe(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("设置消息已读或删除参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.USERNOTIFRE,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("设置消息已读或删除-->" + json);
            }
        });
    }

    //获取维修评价数量 35
    public static void getGetServiceEalution(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"GetServiceEalution");
        final List<String> list=new ArrayList<>();
        list.clear();
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("获取维修评价数量："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.GETSERVICEEALUTION,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            if(jsonObject.has("data")){
                                JSONObject data=jsonObject.getJSONObject("data");
                                list.add(0,data.getString("se_score"));
                                list.add(1,data.getString("se_evalution"));
                                rxAndroidSuccess(list,httpListListener);
                            }else {
                                list.add("Y");
                                rxAndroidSuccess(list,httpListListener);
                            }
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取维修评价数量-->" + json);
            }
        });
    }

    //设置车昵称 36
    public static void getSetCarNick(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"SetCarNick");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("设置车昵称："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.SETCARNICK,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("Y",httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("设置车昵称-->" + json);
            }
        });
    }

    //判断用户是否存在 37
    public static void getJudgmentUserExist(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"JudgmentUserExist");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("判断用户是否存在："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.JUDGMENTUSEREXIST,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            String data=jsonObject.getString("data");
                            rxAndroidSuccess(data,httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("判断用户是否存在-->" + json);
            }
        });
    }

    //获取用户状态 38
    public static void getGetUserStatus(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"GetUserrStatus");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("获取用户状态："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.GETUSERSTATUS,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            JSONObject data=jsonObject.getJSONObject("data");
                            String IsPa=data.getString("IsPa");
                            String IsCarOwner=data.getString("IsCarOwner");
                            rxAndroidSuccess(IsPa,httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取用户状态-->" + json);
            }
        });
    }

    //个人代理介绍和协议 39
    public static void getUrlProxy(Context context,String url,final HttpRxListener httpListListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        novate.get(Constant.HTTP.NEWS+url,new HashMap<String, Object>(),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            String data=jsonObject.getString("reason");
                            rxAndroidSuccess(data,httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("个人代理介绍和协议-->" + json);
            }
        });
    }



    //获取车辆列表 40
    public static void getCarList(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"GetCarList");
        final SharedPHelper sharedPHelper=new SharedPHelper(context);
        final List<HashMap<String,Object>> list=new ArrayList<>();
        list.clear();
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("获取车辆列表参数-->"+HashmapTojson.getJson(keys, values));
        novate.json(Constant.HTTP.GETCARLIST,HashmapTojson.getJson(keys, values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            sharedPHelper.put("LOGINJSONSSCAR",json);
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取车辆列表-->" + json);
            }
        });
    }


    //车辆绑定新接口 41
    public static void getBindNew(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"bind");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("车辆绑定新接口参数-->"+HashmapTojson.getJsonss(keys, values));
        novate.json(Constant.HTTP.VEHICLEbINDVERIFY,HashmapTojson.getJsonss(keys, values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("",httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("车辆绑定新接口-->" + json);
            }
        });
    }

    //车辆解绑 42
    public static void getUnBind(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("车辆解绑接口参数-->"+HashmapTojson.getJson(keys, values));
        novate.json(Constant.HTTP.VEHICLEUNBIND,HashmapTojson.getJson(keys, values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess("",httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("车辆解绑-->" + json);
            }
        });
    }


    //流量查询43
    public static void getUsageData(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"UsageData");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_DATAURL)
                .addLog(true)
                .build();
        MLog.e("流量查询参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.USAGEDATA,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取流量查询-->" + json);
            }
        });
    }


    //上传TSP接口情况日志 44
    public static void getUpLogTSP(final Context context, final String keys[], final Object values[]) {
        MobclickAgent.onEvent(context,"TspInterfaceCall");
                Novate novate = new Novate.Builder(context)
                        .connectTimeout(2000)
                        .baseUrl(Constant.HTTP.BASE_URL)
                        .addLog(true)
                        .build();
                MLog.e("上传TSP接口情况日志参数-->"+HashmapTojson.getJson(keys, values));
                novate.json(Constant.HTTP.TSPINTERFACECALL,HashmapTojson.getJson(keys, values),new BaseSubscriber<ResponseBody>() {
                    @Override
                    public void onError(Throwable e) {
                     //   rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                        MLog.e("onError-->"+e);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String json = null;
                        try {
                            json = new String(responseBody.bytes());
                            JSONObject jsonObject=new JSONObject(json);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        MLog.e("上传TSP接口情况日志-->" + json);
                    }
                });
            }

    //查询TSP调用日志45
    public static void getDownLogTSP(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_DATAURL)
                .addLog(true)
                .build();
        MLog.e("查询TSP调用日志参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.USAGEDATA,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);
               // toSend(e+"");
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                  //  toSend(json);
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(HttpRxUtils.xJson(json,list),httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取查询TSP调用日志-->" + json);
            }
        });
    }


    //ID转手机号  46
    public static void getIdPhone(Context context,String keys[],Object values[],final HttpRxListener httpoListener) {
        MobclickAgent.onEvent(context,"ID2Phone");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("ID转手机号："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.ID2PHONE,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpoListener);
                MLog.e("onError-->"+e);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("获取ID转手机号-->" + json);
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            String data=jsonObject.getString("data");
                            rxAndroidSuccess(data,httpoListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpoListener);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //用余量查询47
    public static void getUsageRemainData(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        MobclickAgent.onEvent(context,"GetB2CUserData");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("用余量查询参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.GetB2CUserData,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(json,httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取用余量查询-->" + json);
            }
        });
    }

    //用总量查询48
    public static void getSearchB2CFlow(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
       // MobclickAgent.onEvent(context,"GetB2CUserData");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_DATAURL)
                .addLog(true)
                .build();
        MLog.e("用总量查询参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.SearchB2CFlow,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(json,httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取用总量查询-->" + json);
            }
        });
    }

    //H5返回49
    public static void getIsysCertifByIccId(Context context,String keys[],Object values[],final HttpRxListener httpListListener) {
        // MobclickAgent.onEvent(context,"GetB2CUserData");
        Novate novate = new Novate.Builder(context)
                .connectTimeout(30)
                .baseUrl(Constant.HTTP.BASE_URL)
                .addLog(true)
                .build();
        MLog.e("H5返回参数："+HashmapTojson.getJson(keys,values));
        novate.json(Constant.HTTP.IsysCertifByIccId,HashmapTojson.getJson(keys,values),new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                rxAndroidFail(Constant.HTTP.HTTPFAIL,httpListListener);
                MLog.e("onError-->"+e);

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String json = null;
                try {
                    json = new String(responseBody.bytes());
                    JSONObject jsonObject=new JSONObject(json);
                    String isSuccess=jsonObject.getString("isSuccess");
                    switch (isSuccess){
                        case "Y":
                            rxAndroidSuccess(json,httpListListener);
                            break;
                        case "N":
                            String reason=jsonObject.getString("reason");
                            rxAndroidFail(reason,httpListListener);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MLog.e("获取H5返回-->" + json);
            }
        });
    }





    public static void logHttp(Novate novate,String url,final String port,String param,final BaseSubscriber httpListListener) {
        MLog.e("URL = "+url);
        MLog.e("参数："+param);
        novate.json(port,param,new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("port==> "+port);
                MLog.e("error==>"+e);
                httpListListener.onError(e);

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                httpListListener.onNext(responseBody);
                MLog.e("port==> "+port);
                try {
                    String json = new String(responseBody.bytes());
                    MLog.e("error==>"+json);
                } catch (Exception e) {
                    MLog.e("获取流量查询-->" + e);
                    e.printStackTrace();
                }

            }
        });
    }
}
