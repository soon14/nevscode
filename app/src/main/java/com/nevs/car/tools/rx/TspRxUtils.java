package com.nevs.car.tools.rx;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelpers;
import com.nevs.car.tools.rx.rxandroid.RxjavaUtil;
import com.nevs.car.tools.rx.rxandroid.UITask;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by mac on 2018/5/17.
 */

public class TspRxUtils {
    //
    public static void getTestToken0(Context context,String headKeys[],Object headValues[],
                                  String keys[],Object values[],final TspRxListener tspRxListener) {
        final SharedPHelper sharedPHelper=new SharedPHelper(context);
        Novate n=new Novate.Builder(context)
                .connectTimeout(30)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys,headValues))
                .addLog(true)
                .build();
        n.post(Constant.Text.GETTESTTOKEN,HashmapTojson.getHashMap(keys,values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("getTestTokenff:"+e);
                rxAndroidFail(e.toString(),tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss=new String(responseBody.bytes());
                    MLog.e("getTestTokencc:"+ss);
                    JSONObject jsonObject=new JSONObject(ss);
                    String test_access_token=jsonObject.getString("access_token");
                    sharedPHelper.put(Constant.ACCESSTOKENS,test_access_token);
                    rxAndroidSuccess("",tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getTestToken(Context context,
                                    String keys[],Object values[],final TspRxListener tspRxListener) {
        final SharedPHelper sharedPHelper=new SharedPHelper(context);
        Novate n=new Novate.Builder(context)
                .connectTimeout(30)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addLog(true)
                .build();
        n.json(Constant.Text.GETTESTTOKEN,HashmapTojson.getJson(keys,values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("getTestTokenff:"+e);
                rxAndroidFail(e.toString(),tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss=new String(responseBody.bytes());
                    MLog.e("getTestTokencc:"+ss);
                    JSONObject jsonObject=new JSONObject(ss);
                    String test_access_token=jsonObject.getString("access_token");
                    sharedPHelper.put(Constant.ACCESSTOKENS,test_access_token);
                    rxAndroidSuccess("",tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //RXAndroid
    public  static void rxAndroidSuccess(Object obj, final TspRxListener tspRxListener){
        RxjavaUtil.doInUIThread(new UITask<Object>(obj) {
            @Override
            public void doInUIThread() {
                tspRxListener.onSucc(getT());
            }
        });
    }

    public static void rxAndroidFail(String str, final TspRxListener tspRxListener){
        RxjavaUtil.doInUIThread(new UITask<String>(str) {
            @Override
            public void doInUIThread() {
                tspRxListener.onFial(getT());
            }
        });
    }

    public static List<HashMap<String,Object>> xJson(String json,List<HashMap<String,Object>> list){
        list.clear();
        Gson gson=new Gson();
        JsonParser parser=new JsonParser();
        JsonObject jsonObj=parser.parse(json).getAsJsonObject();
        JsonArray jsonArray=jsonObj.getAsJsonArray("items");
        Type type = new TypeToken<HashMap<String,Object>>() {}.getType();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement el = jsonArray.get(i);
            HashMap<String, Object> tmp = gson.fromJson(el, type);
            list.add(tmp);
            // MLog.e("标题：" + list.get(i).get("newsID"));
        }
        return list;
    }



    //获取车辆状态1
    public static void getState(Context context,String headKeys[],Object headValues[],
                                String vin,final TspRxListener tspRxListener) {
        final SharedPHelper sharedPHelper=new SharedPHelper(context);
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");
        final List<Object> list=new ArrayList<>();
        list.clear();
        Novate n=new Novate.Builder(context)
                .connectTimeout(30)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys,headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.STATUS+vin,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("获取车辆状态ffff:"+e);
                rxAndroidFail(e.toString(),tspRxListener);
            }

            /**
             * {
             "speed": 60.1,
             "remainingBattery": 21,
             "remainingTime": 155,
             "rechargeMileage": 120,
             "leftFrontDoorStatus": false,
             "rightFrontDoorStatus": false,
             "leftRearDoorStatus": false,
             "rightRearDoorStatus": false,
             "tierPressureStatus": "off",
             "vehiclestatus": "Stopped",
             "updateTime": 1531188287,
             "resultMessage": "",
             "resultDescription": ""
             }

             {
             "speed": 60.1,
             "remainingBattery": 21,
             "remainingTime": 155,
             "rechargeMileage": 120,
             "windowStatus": "off",
             "doorStatus": "off",
             "tierPressureStatus": "off",
             "vehiclestatus": "Stopped",
             "updateTime": 1530771343,
             "resultMessage": "",
             "resultDescription": ""
             }
             * */


//            获取车辆状态cccc:最新
//
//      3.1      {
//                "speed": 0.0,
//                    "remainingBattery": 0.0,
//                    "remainingTime": 0,
//                    "rechargeMileage": 0,
//                    "leftFrontDoorStatus": false,
//                    "rightFrontDoorStatus": false,
//                    "leftRearDoorStatus": false,
//                    "rightRearDoorStatus": false,
//                    "tierPressureStatus": null,
//                    "lockStatus": 0,
//                    "vehiclestatus": 3,
//                    "updateTime": 1545036771,
//                    "resultMessage": 1000,
//                    "resultDescription": ""
//            }
//     2.9       {
//                "speed": 0.0,
//                    "remainingBattery": 87.1,
//                    "remainingTime": 0,
//                    "rechargeMileage": 261,
//                    "leftFrontDoorStatus": false,
//                    "rightFrontDoorStatus": false,
//                    "leftRearDoorStatus": false,
//                    "rightRearDoorStatus": false,
//                    "tierPressureStatus": "0",
//                    "lockStatus": 1,
//                    "vehiclestatus": 1,
//                    "updateTime": 1551407526,
//                    "resultMessage": 1000,
//                    "resultDescription": ""
//            }
//
//            Output 参数名称	类型	备注
//
//            resultMessage	STRING	有错误的时候，返回的错误代码，成功为1000
//            resultDescription	STRING	有错误的时候，返回的详细信息；默认为空
//            speed	FLOAT	车速(公里/小时)
//            remainingBattery	FLOAT	剩余电量
//            remainingTime	INT	剩余充电时间(分钟)
//            rechargeMileage	INT	续航里程
//            leftFrontDoorStatus	BOOLEAN	TRUE: 打开 FALSE:关闭
//            rightFrontDoorStatus	BOOLEAN	TRUE: 打开 FALSE:关闭
//            leftRearDoorStatus	BOOLEAN	TRUE: 打开 FALSE:关闭
//            rightRearDoorStatus	BOOLEAN	TRUE: 打开 FALSE:关闭
//            tierPressureStatus	STRING	胎压状态
//            lockStatus	INT	0:Unlocked 1:Locked
//            vehiclestatus	INT	0:Stopped 1:Running  2:Charging
//            updateTime	LONG	时间戳，从1970-1-1 0:0:0开始的秒数
            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss=new String(responseBody.bytes());
                    MLog.e("获取车辆状态cccc:"+ss);
                    JSONObject jsonObject=new JSONObject(ss);
                    list.add(0,jsonObject.get("speed"));//车速(公里/小时)
                    list.add(1,jsonObject.get("remainingBattery"));//剩余电量
                    list.add(2,jsonObject.get("remainingTime"));//剩余充电时间(分钟)
                    list.add(3,jsonObject.get("rechargeMileage"));//续航里程
                    list.add(4,jsonObject.get("vehiclestatus")+"");//0:Stopped 1:Running  2:Charging 3:Unkown
//
//                   // MLog.e("sdfvsdgvsdgv"+jsonObject.opt("leftFrontDoorStatus").toString());
                    list.add(5,jsonObject.get("leftFrontDoorStatus"));//左前门
                    list.add(6,jsonObject.get("leftRearDoorStatus"));//左后门
                    list.add(7,jsonObject.get("rightFrontDoorStatus"));//右前门
                    list.add(8,jsonObject.get("rightRearDoorStatus"));//右后门
                    list.add(9,jsonObject.get("lockStatus")+"");//锁定状态状态
//                    list.add(9,jsonObject.get("vehiclestatus"));//Running, Stopped, Start, Charging
//                    list.add(10,jsonObject.get("updateTime"));//时间戳，从1970-1-1 0:0:0开始的秒数

                    sharedPHelper.put("vehiclestatus",jsonObject.get("vehiclestatus")+"");
                    sharedPHelper.put("speed",jsonObject.get("speed")+"");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(list,tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e("e:"+e);

                }
            }
        });
    }

    //获取车辆位置信息2
    public static void getLocation(Context context,String headKeys[],Object headValues[],String vin, final TspRxListener tspRxListener) {
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n=new Novate.Builder(context)
                .connectTimeout(30)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys,headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.LOCATION+vin,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("获取车辆位置信息ffff:"+e);
                rxAndroidFail(e.toString(),tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss=new String(responseBody.bytes());
                    MLog.e("获取车辆位置信息cccc:"+ss);
                    JSONObject jsonObject=new JSONObject(ss);
                    list.add(0,jsonObject.get("longitide"));//经度
                    list.add(1,jsonObject.get("latitude"));//维度
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(list,tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //保存POI 3
    public static void getSavePoi(Context context,String headKeys[],Object headValues[],
                                  String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"save");
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n=new Novate.Builder(context)
                .connectTimeout(30)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys,headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.SAVEPOI,HashmapTojson.getJson(keys,values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:"+e);
                rxAndroidFail(e.toString(),tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss=new String(responseBody.bytes());
                    MLog.e("cccc:"+ss);
                    JSONObject jsonObject=new JSONObject(ss);
                   // String str=jsonObject.getString("d");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(ss,tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取POI列表 4
    public static void getPoi(Context context,String headKeys[],Object headValues[],
                                  int PageIndex,int PageSize,final TspRxListener tspRxListener) {
        final List<HashMap<String,Object>> list=new ArrayList<>();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(30)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.POI+"?PageIndex="+PageIndex+"&PageSize="+PageSize,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("获取POI列表ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("获取POI列表cccc:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(TspRxUtils.xJson(ss,list), tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //保存POI 5
    public static void getDeletePoi(Context context,String headKeys[],Object headValues[],
                                  String keys[],Object values[],final TspRxListener tspRxListener) {
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(30)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.DELETEPOI, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("cccc:" + ss);
//                    JSONObject jsonObject = new JSONObject(ss);
//                    String str = jsonObject.getString("d");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取车辆列表 6
    public static void getUservehicleList(Context context,String headKeys[],Object headValues[], final TspRxListener tspRxListener) {
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n=new Novate.Builder(context)
                .connectTimeout(30)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys,headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.USERVEHICLE,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("获取车辆列表ffff:"+e);
                rxAndroidFail(e.toString(),tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                List<HashMap<String,Object>> list=new ArrayList<>();
                String ss=null;
                try {
                    ss= new String(responseBody.bytes());
                    MLog.e("获取车辆列表cccc:"+ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(TspRxUtils.xJson(ss,list), tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取空调状态 7
    public static void getAirconditionstatus(Context context,String headKeys[],Object headValues[],String vin, final TspRxListener tspRxListener) {
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n=new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys,headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.AIRCONDITIONSTATUS+vin,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("获取空调状态ffff:"+e);
                rxAndroidFail(e.toString(),tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss=new String(responseBody.bytes());
                    MLog.e("获取空调状态cccc:"+ss);
                    JSONObject jsonObject=new JSONObject(ss);
                    list.add(0,jsonObject.get("interiorTemperature"));
                    list.add(1,jsonObject.get("exteriorTemperature"));
                    list.add(2,jsonObject.getInt("airconditionStatus")+"");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(list,tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //绑定车辆 8
    public static void getBind(Context context,String headKeys[],Object headValues[],
                                    String keys[],Object values[],final TspRxListener tspRxListener) {
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        MLog.e("绑定车辆参数"+HashmapTojson.getJson(keys,values));
        n.json(Constant.TSP.BIND, HashmapTojson.getJson(keys,values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("cccc:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //授权车辆 生成数字钥匙 9
    public static void getAuthorize(Context context,String headKeys[],Object headValues[],
                                    String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"authorize");
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        MLog.e("授权车辆"+HashmapTojson.getJson(keys, values));
        n.json(Constant.TSP.AUTHORIZE, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("cccc:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //授权车辆列表 10
    public static void getAuthorizeusers(Context context,String headKeys[],Object headValues[],
                                    String vin,final TspRxListener tspRxListener) {
        final List<HashMap<String,Object>> list=new ArrayList<>();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(30)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.AUTHORIZEUSERS+vin,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("cccc:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(xJson(ss,list), tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //解除授权车辆 11
    public static void getRemoke(Context context,String headKeys[],Object headValues[],
                                    String keys[],Object values[],final TspRxListener tspRxListener) {
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(30)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.REVOKE, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //车辆健康 12
    public static void getHealth(Context context,String headKeys[],Object headValues[],
                                    String vin,final TspRxListener tspRxListener) {
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.HEALTH+vin,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("车辆健康ffff:" + e);
            //    MyUtils.upLogTSO(context,"车辆健康",e+"",MyUtils.getTimeNow(),MyUtils.getTimeNow(),"",MyUtils.timeStampNow()+"");
                rxAndroidFail(e.toString(), tspRxListener);
                  }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("车辆健康cccc:" + ss);
                 //   MyUtils.upLogTSO(context,"车辆健康",ss,MyUtils.getTimeNow(),MyUtils.getTimeNow(),"",MyUtils.timeStampNow()+"");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(ss, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //锁定车辆 13
    public static void getLock(Context context,String headKeys[],Object headValues[],
                                 String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"lock");
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));
        MLog.e("锁定车辆参数："+HashmapTojson.getJson(keys,values));
        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .readTimeout(3000)
                .addCache(false)
                .addCookie(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.LOCK, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("锁定车辆ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            //锁定车辆cccc:{"commandId":"c0d25e3e01b64524a7131ceb5913fdc3","commandStatus":"Success","resultMessage":1000,"resultDescription":""}
            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("锁定车辆cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    String str = jsonObject.getString("commandId");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(str, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //限速 14
    public static void getVehiclelimiter(Context context,String headKeys[],Object headValues[],
                                 String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"vehiclelimiter");
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        MLog.e("限速："+HashmapTojson.getJson(keys,values));
        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .readTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.VEHICLELIMITER, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("限速ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("限速cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    String str = jsonObject.getString("commandId");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(str, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //闪灯 15
    public static void getFlash(Context context,String headKeys[],Object headValues[],
                                         String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"flash");
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.FLASH, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("闪灯ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("闪灯cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    String str = jsonObject.getString("commandId");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(str, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //开起空调16
    public static void getAircondition(Context context,String headKeys[],Object headValues[],
                                         String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"airconditionon");
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .readTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.AIRCONDITION, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("开起空调ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("开起空调cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    String str = jsonObject.getString("commandId");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(str, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //关闭空调 17
    public static void getAirconditionoff(Context context,String headKeys[],Object headValues[],
                                          String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"airconditinoff");
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .readTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.AIRCONDITIOFF, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("关闭空调ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("关闭空调cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    String str = jsonObject.getString("commandId");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(str, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //空调定时 18
    public static void getScheduleairconditioner(Context context,String headKeys[],Object headValues[],
                                       String keys[],Object values[],final TspRxListener tspRxListener) {
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.SCHEDULEAIRCONDITIONER, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("空调定时ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("空调定时cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                   //String str = jsonObject.getString("commandId");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //设置电子围栏 19
    public static void getSet(Context context,String headKeys[],Object headValues[],
                                                 String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"set");
        final List<Object> list=new ArrayList<>();
        list.clear();
        MLog.e("电子围栏："+HashmapTojson.getJson(keys, values));
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.SET, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                  //  String str = jsonObject.getString("d");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取围栏信息 20
    public static void getGeofence(Context context,String headKeys[],Object headValues[],
                                                 String vin,final TspRxListener tspRxListener) {
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.GEOFENCE+vin,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    String geoFenceType=jsonObject.getString("GeoFenceType");
                    boolean enable=jsonObject.getBoolean("Enabled");

//                    {"ResultMessage":"","ResultDescription":"","Vin":"LTPSB1413J1000041","Enabled":true,"GeoFenceType":"1","Circle":null}
                    switch (geoFenceType){
                        case "1":
                            JSONObject str;
                            try {
                                 str = jsonObject.getJSONObject("Circle");
                            }catch (JSONException e){
                                str = null;
                            }
                            if(str==null) {
                                list.add(0,enable);
                                rxAndroidSuccess(list, tspRxListener);
                            }else {
                                list.add(0, str.get("CenterPointLongitude"));
                                list.add(1, str.get("CenterPointLatitude"));
                                list.add(2, str.get("Radius"));
                                list.add(3,enable);
                                sharedPHelpers.put(Constant.GETPAMAS,ss);

                                rxAndroidSuccess(list, tspRxListener);
                            }
                            break;
                        case "2":
                            JSONObject regin = jsonObject.getJSONObject("Region");
                            list.add(0,regin.get("AdCode"));
                            list.add(1,enable);
                            rxAndroidSuccess(list, tspRxListener);
                            break;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    ActivityUtil.showToast(context,context.getResources().getString(R.string.toast_errorsevice));
                    DialogUtils.hidding((Activity) context);
                }
            }
        });
    }

    //获取围栏信息 20
    public static void getGeofenceIs(Context context,String headKeys[],Object headValues[],
                                   String vin,final TspRxListener tspRxListener) {
                final List<Object>  list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.GEOFENCE+vin,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("获取围栏信息ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("获取围栏信息cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    boolean Enabled=jsonObject.getBoolean("Enabled");
                                        String GeoFenceType=jsonObject.getString("GeoFenceType");
                    list.add(0,Enabled);
                    list.add(1,GeoFenceType);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(list, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });
    }

    //获取commid 21  还没有上
    public static void getCommid(Context context,String headKeys[],Object headValues[],
                                   String vin,final TspRxListener tspRxListener) {
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(false)
                .addCache(false)
                .build();
        n.get(Constant.TSP.REPORT+vin,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    String str = jsonObject.getString("d");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(str, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取行程记录开启状态 22
    public static void getSetting(Context context,String headKeys[],Object headValues[],
                                 String vin,final TspRxListener tspRxListener) {
//        final List<Object>  list=new ArrayList<>();
//        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.SETTING+vin,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("获取行程记录开启状态ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("获取行程记录开启状态cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                   boolean enabled=jsonObject.getBoolean("enabled");
//                    String GeoFenceType=jsonObject.getString("GeoFenceType");
//                    list.add(0,enabled);
//                    list.add(1,GeoFenceType);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(enabled, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 23  行程记录开启和关闭
    public static void getSettingset(Context context,String headKeys[],Object headValues[],
                              String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"settingset");
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.SETTINGSET, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("行程记录开启和关闭ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("行程记录开启和关闭cccc:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取行程列表 24  +"&StartTime="+values[1]+"&EndTime="+values[2]+
   // "&PageIndex="+values[3]+"&PageSize="+values[4]

    public static void getHistory(Context context,String headKeys[],Object headValues[],
                                  Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"history");
        final List<HashMap<String,Object>> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
       // {Vin}&PageIndex=0&PageSize=10&BeginTime=&EndTime
        n.get(Constant.TSP.HISTORY+
                values[0]
                ,new HashMap<String,Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("获取行程列表ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("获取行程列表cccc:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(xJson(ss,list), tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    public static void getHistory(Context context,String headKeys[],Object headValues[],
//                                  Object values[],final TspRxListener tspRxListener) {
//        final List<HashMap<String,Object>> list=new ArrayList<>();
//        list.clear();
//        Novate n = new Novate.Builder(context)
//                .connectTimeout(30)
//                .addCache(false)
//                .baseUrl(Constant.TSP.BASE_URL)
//                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
//                .addLog(true)
//                .build();
//        // {Vin}&PageIndex=0&PageSize=10&BeginTime=&EndTime
//        n.rxGet(Constant.TSP.HISTORY +
//                        values[0]
//                , new HashMap<String, Object>(), new RxStringCallback() {
//                    @Override
//                    public void onNext(Object tag, String response) {
//                        MLog.e("onNext:"+tag+";"+response);
//                    }
//
//                    @Override
//                    public void onError(Object tag, Throwable e) {
//                        MLog.e("onError:"+tag+";"+e);
//                    }
//
//                    @Override
//                    public void onCancel(Object tag, Throwable e) {
//                        MLog.e("onCancel:"+tag+";"+e);
//                    }
//                });
//    }





    //   //?Vin=1&StartTime=111&EndTime=111&Category=%E8%87%AA%E5%AE%9A%E4%B9%89
    public static void getHistorys(Context context,String headKeys[],Object headValues[],
                     String vin,String a,String b,String c,final TspRxListener tspRxListener) {
        final List<HashMap<String,Object>> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        // {Vin}&PageIndex=0&PageSize=10&BeginTime=&EndTime
        n.get(Constant.TSP.HISTORY+vin+"&StartTime="+a+"&EndTime="+b+"&Category="+c
                ,new HashMap<String,Object>(), new BaseSubscriber<ResponseBody>() {
                    @Override
                    public void onError(Throwable e) {
                        MLog.e("获取行程列表ffff:" + e);
                        rxAndroidFail(e.toString(), tspRxListener);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String ss = new String(responseBody.bytes());
                            MLog.e("获取行程列表cccc:" + ss);
                            sharedPHelpers.put(Constant.GETPAMAS,ss);

                            rxAndroidSuccess(xJson(ss,list), tspRxListener);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //获取行程详情 25
    public static void getDetail(Context context,String headKeys[],Object headValues[],
                                  String tripid,final TspRxListener tspRxListener) {
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.SETTTRIPID+tripid,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("获取行程详情ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("获取行程详情cccc:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(ss,tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 26  行程标记
    public static void getSettag(Context context,String headKeys[],Object headValues[],
                                     String keys[],Object values[],final TspRxListener tspRxListener) {
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.SETTAG, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("行程标记ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("行程标记cccc:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 27  删除行程
    public static void getDeletetrip(Context context,String headKeys[],Object headValues[],
                                     String keys[],Object values[],final TspRxListener tspRxListener) {
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.DELETETRIP, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("删除行程ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("删除行程cccc:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(ss, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // TSP推送 28
    public static void getRegistration(Context context,String headKeys[],Object headValues[],
                                     String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"registration");
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.REGISTRATION, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //  29  开启和关闭推送
    public static void getAlerset(Context context,String headKeys[],Object headValues[],
                                       String keys[],Object values[],final TspRxListener tspRxListener) {
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.ALERTSET, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //  30 url是否拼接
    public static void getNotification(Context context,String headKeys[],Object headValues[],
                                  String keys[],Object values[],final TspRxListener tspRxListener) {
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(30)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.NOTIFICATION, HashmapTojson.getHashMap(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    String str = jsonObject.getString("d");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(str, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取 31爱车通知详情
    public static void getNotificationid(Context context,String headKeys[],Object headValues[],
                                 String notificationId,final TspRxListener tspRxListener) {
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.NOTIFICATIONID+notificationId,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("cccc爱车通知详情:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(ss, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //  32 DigitalKey申请commandId
    public static void getApply(Context context,String headKeys[],Object headValues[],
                                  String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"apply");
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.APPLY, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("DigitalKey申请commandId，ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("DigitalKey申请commandId，cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    String str = jsonObject.getString("commandId");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(str, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //  33 DigitalKey下载证书
    public static void getDownload(Context context,String headKeys[],Object headValues[],
                                  String commandid,final TspRxListener tspRxListener) {
        final List<String> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpersss=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpersss.put(Constant.UPPAMAS,"GET");

        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.CSRHEAD+new SharedPHelper(context).get("TSPVIN", "0"));
        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.DOWNLOAD+commandid,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("DigitalKey下载证书，ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("DigitalKey下载证书，cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    list.add(0,jsonObject.getString("bleAddress"));
                    list.add(1,jsonObject.getString("tBoxCertificateContent"));
                    list.add(2,jsonObject.getString("mobileCertificateContent"));
                    sharedPHelpers.put(Constant.TSPRXbleAddress,jsonObject.getString("bleAddress"));
                   // sharedPHelpers.put(Constant.TSPRXtBoxCertificateContent,jsonObject.getString("tBoxCertificateContent"));
                    //sharedPHelpers.put(Constant.TSPRXmobileCertificateContent,"-----BEGIN CERTIFICATE-----\n"+jsonObject.getString("mobileCertificateContent")+"\n-----END CERTIFICATE-----");
                  //73  sharedPHelpers.put(Constant.TSPRXmobileCertificateContent,jsonObject.getString("mobileCertificateContent"));
                    sharedPHelpers.put(Constant.TSPRXmobileCertificateContent,"mobileCertificateContent");


                    sharedPHelpersss.put(Constant.GETPAMAS,"NULL");

                    rxAndroidSuccess(list, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e("下载DEGIKEY异常");
                }
            }
        });
    }


//    //  34 返回了一个 true Success 车辆控制轮询
//    public static void getCommandresult(Context context,String headKeys[],Object headValues[],
//                                   String keys[],Object values[],final TspRxListener tspRxListener) {
//        final List<Object> list=new ArrayList<>();
//        list.clear();
//        Novate n = new Novate.Builder(context)
//                .connectTimeout(30)
//                .addCache(false)
//                .baseUrl(Constant.TSP.BASE_URL)
//                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
//                .addLog(true)
//                .build();
//        n.json(Constant.TSP.COMMANDRESULT, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
//            @Override
//            public void onError(Throwable e) {
//                MLog.e("车辆控制轮询ffff:" + e);
//                rxAndroidFail(e.toString(), tspRxListener);
//            }
//
//            @Override
//            public void onNext(ResponseBody responseBody) {
//                try {
//                    String ss = new String(responseBody.bytes());
//                    MLog.e("车辆控制轮询cccc:" + ss);
//                    rxAndroidSuccess("", tspRxListener);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    //  34 返回了一个 true Success 车辆控制轮询
    public static void getCommandresult(Context context,String headKeys[],Object headValues[],
                                        String comid,final TspRxListener tspRxListener) {
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");


        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .readTimeout(3000)
                .addCache(false)
                .addCookie(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.COMMANDRESULT+comid,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {

                MLog.e("车辆控制轮询ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }
            //{"commandStatus":"Success","commandDetail":"{\"items\":[{\"applicationId\":\"OpenAPI\",\"commandMessageId\":\"1c3963a4829d4d9b89b503559f36967d\",\"messageId\":\"c0d25e3e01b64524a7131ceb5913fdc3\",\"vin\":\"LTPSBSIMULATOR199\",\"commandType\":\"Remote_Lock\",\"status\":\"Waiting\",\"createTime\":\"2019-02-27T12:48:38.3650089+08:00\"}],\"resultCode\":\"\",\"resultDescription\":\"\"}","resultMessage":1000,"resultDescription":""}
//            {
//                commandId = d023afe573804e3a976e71092e1d8c21;
//                commandStatus = Completed;
//                executingStatus = flashlightcontrolsuccess;
//                reason = unknow;
//                resultDescription = "";
//                resultMessage = 1000;
//            }
//            车辆控制轮询cccc:{"commandId":"8a3e3b51f8814b17adaf8d1d5ea13122","commandStatus":"Accepted","reason":"","executingStatus":"","resultMessage":1000,"resultDescription":""}
//            commandStatus状态：Accepted
            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("车辆控制轮询cccc:" + ss);
                    JSONObject jsonObject=new JSONObject(ss);
                    String commandStatus=jsonObject.getString("commandStatus");
                    MLog.e("commandStatus状态："+commandStatus);
                    String executingStatus=jsonObject.getString("executingStatus");
                    String reason=jsonObject.getString("reason");
//                    if(commandStatus.equals("Success")){
//                        rxAndroidSuccess("Success", tspRxListener);
//                    }else {
//                        String commandDetail=jsonObject.getString("commandDetail");
//                        JSONObject jsonObject1=new JSONObject(commandDetail);
//                        JSONArray jsonArray=jsonObject1.getJSONArray("items");
//                        JSONObject jsonObject2=jsonArray.getJSONObject(0);
//                        String status=jsonObject2.getString("status");
//                        MLog.e("status:"+status);
//                        if(status.equals("Rejected ")){
//                            rxAndroidFail("Rejected", tspRxListener);
//                        }else {
//                            rxAndroidFail("", tspRxListener);
//                        }
//                    }

                    //{"commandId":"d0928d2123474072a631382340532cdf","commandStatus":"Completed","reason":"LockingSuccess","executingStatus":"Completed","resultMessage":1000,"resultDescription":""}
                    //{"commandId":"58b5c8bb688d42c2bc067f1a9fc4399c","commandStatus":"Completed","reason":"flashlightcontrolsucceed","executingStatus":"Completed","resultMessage":1000,"resultDescription":""}
                    //{"commandId":"d4afe48ee9064809a8c036468e61cd23","commandStatus":"Completed","reason":"drivelinecontrolsucceed","executingStatus":"Completed","resultMessage":1000,"resultDescription":""}

//                    if(commandStatus.equals("Rejected")){
//                        BToast.showToast(context,context.getResources().getString(R.string.n_car_ing),true);
//                    }
//                    if(commandStatus.equals("Accepted")){
//                        BToast.showToast(context,context.getResources().getString(R.string.n_car_next),true);
//                    }
//                    if(commandStatus.equals("Interrupted")){
//                        BToast.showToast(context,context.getResources().getString(R.string.n_car_sheep),true);
//                    }


                   if(commandStatus.equals("Completed")){//成功时
                       if(executingStatus.equals("Completed")){
                           if(reason.equals("flashlightcontrolsucceed")||reason.equals("drivelinecontrolsucceed")||reason.equals("acccontrolsucceed")) {
                               rxAndroidSuccess("Success", tspRxListener);
                           }else if(reason.equals("LockingSuccess")&&reason.length()<=16){
                               rxAndroidSuccess("Success", tspRxListener);
                       }else {
                               rxAndroidFail(reason, tspRxListener);
                           }
                       }

                   }else {
                       rxAndroidFail(commandStatus, tspRxListener);
                   }


                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e("dddddddd:"+e);
                }
            }
        });
    }

    //爱车通知列表 35
    public static void getNohistory(Context context,String headKeys[],Object headValues[],
                              String keys[],Object values[],final TspRxListener tspRxListener) {
        final List<HashMap<String,Object>> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        MLog.e("爱车通知列表："+HashmapTojson.getJson(keys, values));
        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(false)
                .addCookie(false)
                .build();
        n.json(Constant.TSP.NOTIFIHISTORY, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("爱车通知列表ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    //{"items":[{"notificationId":"68628","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551155546,"isRead":true},{"notificationId":"68627","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551155474,"isRead":true},{"notificationId":"68626","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551155404,"isRead":true},{"notificationId":"68615","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551154333,"isRead":true},{"notificationId":"68614","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551154273,"isRead":true},{"notificationId":"68613","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551154203,"isRead":true},{"notificationId":"68607","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551153752,"isRead":true},{"notificationId":"68606","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551153685,"isRead":false},{"notificationId":"68605","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551153612,"isRead":false},{"notificationId":"68599","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551153129,"isRead":false},{"notificationId":"68598","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551153069,"isRead":false},{"notificationId":"68597","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551152999,"isRead":false},{"notificationId":"68591","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551152523,"isRead":false},{"notificationId":"68590","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551152463,"isRead":false},{"notificationId":"68589","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551152403,"isRead":false},{"notificationId":"68583","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551151884,"isRead":false},{"notificationId":"68582","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551151814,"isRead":false},{"notificationId":"68581","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551151748,"isRead":false},{"notificationId":"68571","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551151467,"isRead":false},{"notificationId":"68570","category":"Common","description":"{\"aps\":{\"alert\":\"您的车辆已超出设置的电子围栏范围，请注意！！\"}}","pushTime":1551151398,"isRead":false}],"resultMessage":1000,"resultDescription":""}
                    String ss = new String(responseBody.bytes());
                    MLog.e("爱车通知列表cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    //  String str = jsonObject.getString("d");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(xJson(ss,list), tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //删除地址围栏 36
    public static void getDeleteGeo(Context context,String headKeys[],Object headValues[],
                                    String keys[],Object values[],final TspRxListener tspRxListener) {
        MLog.e("删除地址围栏："+HashmapTojson.getJson(keys, values));
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.DELETEGEOFENCE, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("删除地址围栏ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("删除地址围栏cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    //  String str = jsonObject.getString("d");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //空调定时列表 37
    public static void getScheduleairconditioners(Context context,String headKeys[],Object headValues[],
                                   String vin,final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"scheduleairconditioner");
        final List<HashMap<String,Object>> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.SCHEDULEAIRCONDITIONER+vin,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("空调定时列表:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("空调定时列表:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(xJson(ss,list), tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //删除空调定时列表 39
    public static void getAirDelete(Context context,String headKeys[],Object headValues[],
                                    String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"delete");
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.AIRDELETE,HashmapTojson.getJson(keys,values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("删除空调定时列表FFF:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("删除空调定时列表CCC:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //充电服务列表 40
    public static void getStation(Context context,String headKeys[],Object headValues[],
                                          String lat,String lon, String radiu,final TspRxListener tspRxListener) {
        final List<HashMap<String,Object>> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        //station?Longitude=1&Langitude=1&Radius=9999   station?Longitude=1&Langitude=1&Radius=9999
        n.get(Constant.TSP.STATION+"?Longitude="+lon+"&Langitude="+lat+"&Radius="+radiu,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("充电服务列表:" + e);
                MLog.e("Okhttp","充电服务列表:" + e.getMessage());
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("充电服务列表:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(xJson(ss,list), tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //充电服务列表 40
    public static void getStationSreen(Context context,String headKeys[],Object headValues[],
                                  String lat,String lon, String radiu,String urls,final TspRxListener tspRxListener) {
        final List<HashMap<String,Object>> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        //station?Longitude=1&Langitude=1&Radius=9999   station?Longitude=1&Langitude=1&Radius=9999
        n.get(Constant.TSP.STATION+"?Longitude="+lon+"&Langitude="+lat+"&Radius="+radiu+urls,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("充电服务列表:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("充电服务列表:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(xJson(ss,list), tspRxListener);




                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    //充电服务详情 41
    public static void getStationId(Context context,String headKeys[],Object headValues[],
                                  String stationId,       final TspRxListener tspRxListener) {
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.STATIONID+stationId,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("充电服务详情:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("充电服务详情:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(ss, tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    //获取限速值 42
    public static void getvehiclelimitervalue(Context context,String headKeys[],Object headValues[],
                                    String vin,       final TspRxListener tspRxListener) {
        final List<Object>   list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,"GET");

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.get(Constant.TSP.GETVEHICLE+vin,new HashMap<String, Object>(), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("获取限速值:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("获取限速值:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    String speed = jsonObject.getString("speed");
                    boolean enabled=jsonObject.getBoolean("enabled");
                    list.add(0,enabled);
                    list.add(1,speed);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess(list,tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //默认车 43
    public static void getDefault(Context context,String headKeys[],Object headValues[],
                                    String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"default");
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.DEFALT,HashmapTojson.getJson(keys,values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("默认车FFF:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("默认车CCC:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //空调定时单个更新 44
    public static void getScheduleairconditionerUp(Context context,String headKeys[],Object headValues[],
                                                 String keys[],Object values[],final TspRxListener tspRxListener) {
        MobclickAgent.onEvent(context,"update");
        final List<Object> list=new ArrayList<>();
        list.clear();
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        n.json(Constant.TSP.UPAIR, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("空调定时单个更新ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("空调定时单个更新cccc:" + ss);
                    JSONObject jsonObject = new JSONObject(ss);
                    //String str = jsonObject.getString("commandId");
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //删除爱车通知 45
    public static void deleteNotify(Context context,String headKeys[],Object headValues[],
                                                   String keys[],Object values[],final TspRxListener tspRxListener) {
        final SharedPHelpers sharedPHelpers=new SharedPHelpers(context,Constant.UPLOGFILENAME);
        sharedPHelpers.put(Constant.UPPAMAS,HashmapTojson.getJson(keys, values));

        Novate n = new Novate.Builder(context)
                .connectTimeout(3000)
                .addCache(false)
                .baseUrl(Constant.TSP.BASE_URL)
                .addHeader(HashmapTojson.getHashMap(headKeys, headValues))
                .addLog(true)
                .build();
        MLog.e("删除爱车通知参数："+HashmapTojson.getJson(keys, values));
        n.json(Constant.TSP.NOTIFYDELETE, HashmapTojson.getJson(keys, values), new BaseSubscriber<ResponseBody>() {
            @Override
            public void onError(Throwable e) {
                MLog.e("删除爱车通知ffff:" + e);
                rxAndroidFail(e.toString(), tspRxListener);
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String ss = new String(responseBody.bytes());
                    MLog.e("删除爱车通知cccc:" + ss);
                    sharedPHelpers.put(Constant.GETPAMAS,ss);

                    rxAndroidSuccess("", tspRxListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
