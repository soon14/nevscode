package com.nevs.car.mvp;

import android.content.Context;
import android.util.Log;

import com.nevs.car.constant.Constant;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.util.MLog;
import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;

import org.json.JSONObject;

import okhttp3.ResponseBody;

/**
 * Created by mac on 2018/4/5.
 *
 * model层业务逻辑处理
 * *@param username   用户名
 * *@param password   密码
 * *@param callBack   结果回调
 */


    public class RequestBizIml implements RequestBiz {

    //private Handler handler=new Handler(Looper.getMainLooper());//主线程handler一步处理

    private static RequestBizIml instance = new RequestBizIml();//单例

    public static RequestBizIml getInstance() {
        return instance;
    }


    @Override
        public void requestForData(Context context,String toJson,final OnRequestListener listener) {
        //三方框架网络请求
            Novate novate=new Novate.Builder(context)
                    .connectTimeout(30)
                    .baseUrl(Constant.HTTP.BASE_URL)
                    .addLog(true)
                    .build();
            novate.json(Constant.HTTP.USERLOGIN, toJson, new BaseSubscriber<ResponseBody>() {
                @Override
                public void onError(Throwable e) {
                    listener.onFailed("onError:"+e);
                }
                @Override
                public void onNext(ResponseBody responseBody) {

                    String json= null;
                    try {
                        json = new String(responseBody.bytes());
                        Log.e("TAG","获取登录："+json);
                        JSONObject jsonObject=new JSONObject(json);
                        String isSuccess=jsonObject.getString("isSuccess");
                        String reason=jsonObject.getString("reason");
                        JSONObject data=jsonObject.getJSONObject("data");
                        String accessToken=data.getString("accessToken");
                        //写入缓存
                        SharedPHelper sharedPHelper=new SharedPHelper(context);
                        sharedPHelper.put(Constant.ACCESSTOKEN,accessToken);
                        MLog.e("accessToken:"+sharedPHelper.get(Constant.ACCESSTOKEN,""));
                        switch (isSuccess){
                            case "Y":
                                listener.onSuccess("Y");
//                                //写入缓存
//                                SharedPHelper sharedPHelper=new SharedPHelper(context);
//                                sharedPHelper.put(Constant.ACCESSTOKEN,accessToken);
//                                MLog.e("accessToken:"+sharedPHelper.get(Constant.ACCESSTOKEN,""));
                                break;
                            case "N":
                                listener.onSuccess(reason);
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

}

