package com.nevs.car.tools.util;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by SoBan on 2017/3/23.
 * Describe: 事件统计（计数事件）
 */

public class UmengEventUtils {
    private static final String DEVICEID = "deviceid";
    private static final String IMSI = "imsi";
    private static final String MAC = "mac";
    private static final String PHONEMODEL = "phone_model";
    private static final String USERID = "userid";
    private static final String PHONE = "phone";
    private static final String GOODSID = "goodsid";
    private static final String GOODSNAME = "goodsname";
    private static final String SHOPID = "shopid";
    private static final String SHOPNAME = "shopname";
    private static final String PRICE = "price";
    private static final String PAYWAY = "payway";

    /**
     * 首次安装
     *
     * @param ctx
     */
    public static void toInstallClick(Context ctx) {
        HashMap<String, String> map = getInstallMap(ctx);
        MobclickAgent.onEvent(ctx, "install", map);
    }

    /**
     * 登录
     *
     * @param ctx
     * @param userId
     * @param phone
     */
    public static void toLoginClick(Context ctx, String userId, String phone) {
        HashMap<String, String> map = getInstallMap(ctx);
        map.put(USERID, userId);
        map.put(PHONE, phone);
        MobclickAgent.onEvent(ctx, "login", map);
    }

    /**
     * 退出
     *
     * @param ctx
     * @param userId
     */
    public static void toLogoutClick(Context ctx, String userId) {
        HashMap<String, String> map = getInstallMap(ctx);
        map.put(USERID, userId);
        MobclickAgent.onEvent(ctx, "logout", map);
    }

    /**
     * 充值
     *
     * @param ctx
     * @param userId
     * @param goodsId
     * @param goodsName
     * @param price
     * @param payway
     */
    public static void toRechargeClick(Context ctx, String userId, String phone,
                                       String goodsId, String goodsName, int price, String payway) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(USERID, userId);
        map.put(PHONE, phone);
        map.put(GOODSID, goodsId);
        map.put(GOODSNAME, goodsName);
        map.put(PRICE, String.valueOf(price));
        map.put(PAYWAY, payway);
        MobclickAgent.onEvent(ctx, "recharge", map);
    }

    /**
     * 扫码app支付
     *
     * @param ctx
     * @param userId
     * @param phone
     * @param shopId
     * @param shopName
     * @param price
     * @param payway
     */
    public static void toSweepPaymentClick(Context ctx, String userId, String phone,
                                           String shopId, String shopName, int price, String payway) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(USERID, userId);
        map.put(PHONE, phone);
        map.put(SHOPID, shopId);
        map.put(SHOPNAME, shopName);
        map.put(PRICE, String.valueOf(price));
        map.put(PAYWAY, payway);
        MobclickAgent.onEvent(ctx, "sweeppayment", map);
    }

    private static HashMap<String, String> getInstallMap(Context ctx) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(PHONEMODEL, CommonUtils.getPhoneModel());
        map.put(DEVICEID, CommonUtils.getDeviceId(ctx));
        map.put(IMSI, CommonUtils.getIMSI(ctx));
        map.put(MAC, CommonUtils.getLocalMacAddressFromWifiInfo(ctx));
        return map;
    }
}