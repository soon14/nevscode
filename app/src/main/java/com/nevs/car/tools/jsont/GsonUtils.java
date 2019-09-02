package com.nevs.car.tools.jsont;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by mac on 2018/4/24.
 * GSON解析工具类
 * 在Http请求成功返回数据之后，首先判断泛型类型type是不是Class类型，如果是则判断type的名字是否为String或者Object
 * ，是则直接在成功回调中传入返回的json字符串。如果不是则用Gson将json数据解析出来，实参就是type。
 */

public class GsonUtils {
    private static Gson mGson;

    public static void getRequest(String url, Map<String, String> params, HttpCallBack callBack) {
        if (callBack == null) {
            return;
        }

        if (mGson == null) {
            mGson = new Gson();
        }

        boolean returnJson = false;
        Type type = callBack.getType();

        if (type instanceof Class) {
            switch (((Class) type).getSimpleName()) {
                case "Object":
                case "String":
                    returnJson = true;
                    break;
                default:
                    break;
            }
        }

        if (returnJson) {
            try {
                callBack.onResolve(url);
            } catch (Exception e) {
                callBack.onFailed("N", e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                callBack.onResolve(mGson.fromJson(url, type));
            } catch (Exception e) {
                callBack.onFailed("N", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
