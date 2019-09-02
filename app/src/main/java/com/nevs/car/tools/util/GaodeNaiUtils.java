package com.nevs.car.tools.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.nevs.car.R;

import java.io.File;

/**
 * Created by mac on 2018/5/25.
 */

public class GaodeNaiUtils {
    public static boolean isInstallPackage() {
        return new File("/data/data/" + "com.autonavi.minimap").exists();
    }

//    public static void openGaoDeMap(Context context,double lon, double lat, String title, String describle) {
//        try {
//            double[] gd_lat_lon = bdToGaoDe(lon, lat);
//            StringBuilder loc = new StringBuilder();
//            loc.append("androidamap://viewMap?sourceApplication=XX");
//            loc.append("&poiname=");
//            loc.append(describle);
//            loc.append("&lat=");
//            loc.append(gd_lat_lon[0]);
//            loc.append("&lon=");
//            loc.append(gd_lat_lon[1]);
//            loc.append("&dev=0");
//            Intent intent = Intent.getIntent(loc.toString());
//            context.startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


//    /**
//     * 启动高德App进行导航
//     *述是官方的方法，但是在部分手机上出现崩溃，方案：将uri开头的“amapuri”改为“androidamap”即可！
//     * @param sourceApplication 必填 第三方调用应用名称。如 amap
//     * @param dname             非必填 目的地名称
//     * @param dlat              必填 终点纬度
//     * @param dlon              必填 终点经度
//     * @param dev               必填 是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
//     * @param style             必填 预设的导航方式 t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车）
//     */
//    public static void goToNaviActivity(String sourceApplication, String poiname, double lat, double lon, String dev, String style) {
//        //启动路径规划页面
//        String uri = "amapuri://route/plan/?dlat="+ dlat+"&dlon="+dlon+"&dname="+ dname+"&dev=1&t=0";
//        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(uri));
//        intent.setPackage("com.autonavi.minimap");
//        context.startActivity(intent);
//    }


//    public static void openGaoDeMap(Context context, double lon, double lat, String title, String describle) {
//        try {
//            double[] gd_lat_lon = bdToGaoDe(lon, lat);
//            StringBuilder loc = new StringBuilder();
//            loc.append("androidamap://viewMap?sourceApplication=XX");
//            loc.append("&poiname=");
//            loc.append(describle);
//            loc.append("&lat=");
//            loc.append(lat);
//            loc.append("&lon=");
//            loc.append(lon);
//            loc.append("&dev=0");
//            Intent intent = new Intent();
//            intent.setData(Uri.parse(loc.toString()));
//            context.startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
public static void openGaoDeMap(Context context, double lon, double lat, String title, String describle) {
//    try {
//        double[] gd_lat_lon = bdToGaoDe(lon, lat);
//        StringBuilder loc = new StringBuilder();
//        loc.append("androidamap://viewMap?sourceApplication=").append(R.string.app_name);
//        loc.append("&poiname=").append("");
//        loc.append("&lat=").append(lat);
//        loc.append("&lon=").append(lon);
//        loc.append("&dev=0&m=0&t=0&showType=1");
//        Intent intent = new Intent();
//        intent.setData(Uri.parse(loc.toString()));
//        context.startActivity(intent);
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
    MLog.e("lat=="+lat+"----------->lon=="+lon);

    if(lat>lon){
        double tmpLon= lat;
        lat = lon;
        lon= tmpLon;

    }
    try {
        double[] gd_lat_lon = bdToGaoDe(lon, lat);
        StringBuilder loc = new StringBuilder();
        loc.append("androidamap://navi?sourceApplication=").append(R.string.app_name);
        loc.append("&poiname=").append("");
        loc.append("&lat=").append(lat);
        loc.append("&lon=").append(lon);
        loc.append("&dev=0&m=0&t=0&style=0");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.autonavi.minimap");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse(loc.toString()));
        context.startActivity(intent);
    } catch (Exception e) {
        e.printStackTrace();
    }


}

    //GCJ-02 == BD-09 地图坐标系互转
    public static double[] bdToGaoDe(double bd_lat, double bd_lon) {
        double[] gd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        gd_lat_lon[0] = z * Math.cos(theta);
        gd_lat_lon[1] = z * Math.sin(theta);
        return gd_lat_lon;
    }

    public static double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }

}
