package com.nevs.car.tools.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by mac on 2018/5/25.
 */

public class BaiduNaiUtils {
    /**
     * 根据包名检测某个APP是否安装
     * <h3>Version</h3> 1.0
     * <h3>CreateTime</h3> 2016/6/27,13:02
     * <h3>UpdateTime</h3> 2016/6/27,13:02
     * <h3>CreateAuthor</h3>
     * <h3>UpdateAuthor</h3>
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     * @return true 安装 false 没有安装
     */
    public static boolean isInstalled() {
        return new File("/data/data/com.baidu.BaiduMap").exists();
    }

    /**
     * 通过经纬度导航
     * (此处输入方法执行任务.)
     * <h3>Version</h3> 1.0
     * <h3>CreateTime</h3> 2017/11/9,15:31
     * <h3>UpdateTime</h3> 2017/11/9,15:31
     * <h3>CreateAuthor</h3>
     * <h3>UpdateAuthor</h3>
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     * @param context 上下文
     * @param coord_type coord_type 可选 坐标类型，可选参数，默认为bd09经纬度坐标
     * @param src  必选 调用来源，规则：companyName|appName。
     * @param location 经纬度 例如：39.9761,116.3282
     */
    public static  void invokeNavi0(Context context, String coord_type , String src, String location){
        StringBuffer stringBuffer  = new StringBuffer("baidumap://map/navi?");
        if (!TextUtils.isEmpty(coord_type)){
            stringBuffer.append("coord_type=").append(coord_type);
        }
        stringBuffer.append("&src=").append(src);
        stringBuffer.append("&location=").append(location);
        Intent intent = new Intent();
        intent.setData(Uri.parse(stringBuffer.toString()));
        context.startActivity(intent);
    }

    /**
     * 通过关键字导航
     * (此处输入方法执行任务.)
     * <h3>Version</h3> 1.0
     * <h3>CreateTime</h3> 2017/11/9,15:31
     * <h3>UpdateTime</h3> 2017/11/9,15:31
     * <h3>CreateAuthor</h3>
     * <h3>UpdateAuthor</h3>
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     * @param context 上下文
     * @param coord_type coord_type 可选 坐标类型，可选参数，默认为bd09经纬度坐标
     * @param src  必选 调用来源，规则：companyName|appName。
     * @param query 关键字 例如：故宫
     */
    public static  void invokeNavi2(Context context, String coord_type ,String src,String query){
        StringBuffer stringBuffer  = new StringBuffer("baidumap://map/navi?");
        if (!TextUtils.isEmpty(coord_type)){
            stringBuffer.append("coord_type=").append(coord_type);
        }
        stringBuffer.append("&src=").append(src);
        stringBuffer.append("&query=").append(query);
        Intent intent = new Intent();
        intent.setData(Uri.parse(stringBuffer.toString()));
        context.startActivity(intent);
    }

    //百度 如果已安装跳转安装的程序 如果没有安装跳转网页版百度地图
//    private void openBaiduMap(double lon, double lat, String describle) {
//        try {
//            StringBuilder loc = new StringBuilder();
//            loc.append("intent://map/direction?origin=latlng:");
//            loc.append(latitude);
//            loc.append(",");
//            loc.append(longitude);
//            loc.append("|name:");
//            loc.append("我的位置");
//            loc.append("&destination=latlng:");
//            loc.append(lat);
//            loc.append(",");
//            loc.append(lon);
//            loc.append("|name:");
//            loc.append(describle);
//            loc.append("&mode=driving");
//            loc.append("&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
//            Intent intent = Intent.getIntent(loc.toString());
//            if (isInstallPackage("com.baidu.BaiduMap")) {
//                startActivity(intent); //启动调用
//                Log.e("GasStation", "百度地图客户端已经安装");
//            } else {
//                LatLng ptMine = new LatLng(latitude, longitude);
//                LatLng ptPosition = new LatLng(lat, lon);
//
//                NaviParaOption para = new NaviParaOption()
//                        .startPoint(ptMine)
//                        .endPoint(ptPosition);
//                BaiduMapNavigation.openWebBaiduMapNavi(para, getApplicationContext());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



//    WGS84坐标系：即地球坐标系，国际上通用的坐标系。但是国家规定不能直接使用WGS84地理坐标系。
//    GCJ02坐标系：即火星坐标系，WGS84坐标系经加密后的坐标系。高德 腾讯采用的就是这个。
//    BD09坐标系：即百度坐标系，GCJ02坐标系经加密后的坐标系。
//
//    /**
//     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 将 GCJ-02 坐标转换成 BD-09 坐标
//     *
//     * @param lat
//     * @param lon
//     */
//    public static double[] gcj02_To_Bd09(double lat, double lon) {
//        double x = lon, y = lat;
//        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
//        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
//        double tempLat = z * Math.sin(theta) + 0.006;
//        double tempLon = z * Math.cos(theta) + 0.0065;
//        double[] gps = {tempLat, tempLon};
//        return gps;
//    }
//



    /**
     * @param destinationLat 目的地维度
     * @param destinationLng 目的地经度
     * @param coord_type     坐标类型  允许的值为bd09ll、bd09mc、gcj02、wgs84。
     *                       bd09ll表示百度经纬度坐标，bd09mc表示百度墨卡托坐标，gcj02表示经过国测局加密的坐标，wgs84表示gps获取的坐标
     * @param mode           导航类型导航模式
     *                       可选transit（公交）、 driving（驾车）、 walking（步行）和riding（骑行）.
     * @param src            必选参数，格式为：appName  不传此参数，不保证服务
     */
    public static void invokeNavi(Context context, String coord_type, String src, String destinationLat, String destinationLng, String mode) {
        Intent i1 = new Intent();
        i1.setData(Uri.parse("baidumap://map/direction?destination=" +
                destinationLat + "," + destinationLng + "&coord_type=" + coord_type +
                "&mode=" + mode + "&src=" + src + "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end"));

        context.startActivity(i1);
    }
}
