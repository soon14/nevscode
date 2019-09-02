package com.nevs.car.activity.my;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LookTripActivity extends BaseActivity implements LocationSource, AMapLocationListener {
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private List<HashMap<String, Object>> listLines = new ArrayList<>();
    @BindView(R.id.mapViewC)
    MapView mapView;
    //AMap是地图对象
    private AMap aMap;
    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    private OnLocationChangedListener mListener = null;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_look_trip;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initView(savedInstanceState);
        initIntent();
    }

    private void initIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        listLines = (List<HashMap<String, Object>>) bundle.getSerializable("listLines");
        MLog.e("跳转点长度" + listLines.size());
        try {
            drawLines();
        } catch (Exception e) {
            MLog.e("崩溃");
        }


    }

    private void drawLines() {
        try {
            MLog.e("跳转度度：" + listLines.get(0).get("longitude"));
        } catch (Exception e) {
            MLog.e("跳转度度：\"+listLines.get(0).get(\"longitude\")" + "空");
        }

        final List<LatLng> latLngs = new ArrayList<LatLng>();
        for (int i = 0; i < listLines.size(); i++) {
            latLngs.add(new LatLng(Double.parseDouble(listLines.get(i).get("latitude") + ""), Double.parseDouble(listLines.get(i).get("longitude") + "")));
        }
//        Polyline polyline =aMap.addPolyline(new PolylineOptions().
//                addAll(latLngs).width(10).color(Color.argb(255,255,20,147)));
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(Double.parseDouble(listLines.get(0).get("latitude")+""),Double.parseDouble(listLines.get(0).get("longitude")+"")),7, 0, 30));
//        aMap.moveCamera(cameraUpdate);//地图移向指定区域


//        mPolylineOptions = new PolylineOptions();
//        mPolylineOptions.setDottedLine(true);//设置是否为虚线
//        mPolylineOptions.geodesic(false);//是否为大地曲线
//        mPolylineOptions.visible(true);//线段是否可见
//        mPolylineOptions.useGradient(false);//设置线段是否使用渐变色
//        //设置线颜色，宽度
//        mPolylineOptions.color(getWalkColor()).width(getRouteWidth());

        //起点位置和  地图界面大小控制
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 13));
        aMap.setMapTextZIndex(2);


//        aMap.addPolyline((new PolylineOptions())
//                //手动数据测试
//                //.add(new LatLng(26.57, 106.71),new LatLng(26.14,105.55),new LatLng(26.58, 104.82), new LatLng(30.67, 104.06))
//                //集合数据
//                .addAll(latLngs)
//                //线的宽度
//                .width(19).setDottedLine(false).geodesic(true)
//                //颜色
//                .color(getResources().getColor(R.color.tripgray)));


        makepoint(latLngs.get(0), 1);//绘制起点
        makepoint(latLngs.get(latLngs.size() - 1), 2);//绘制终点

        new Thread(new Runnable() {
            @Override
            public void run() {
                //绘制纹理
                for (int i = 0; i < latLngs.size() - 1; ++i) {

                    aMap.addPolyline(GetPolylineOptions().add(
                            latLngs.get(i), latLngs.get(i + 1)
                    ));

                }
            }
        }).start();


    }


    /**
     * Created by adminZPH on 2017/4/14.
     * 设置线条中的纹理的方法
     *
     * @return PolylineOptions
     */
    public static PolylineOptions GetPolylineOptions() {
        //添加纹理图片
        List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
        BitmapDescriptor mRedTexture = BitmapDescriptorFactory
                .fromResource(R.mipmap.zxczxc);
        textureList.add(mRedTexture);

        // 添加纹理图片对应的顺序
        List<Integer> textureIndexs = new ArrayList<Integer>();
        textureIndexs.add(0);
        PolylineOptions polylienOptions = new PolylineOptions();
        polylienOptions.setCustomTextureIndex(textureIndexs);
        polylienOptions.setCustomTextureList(textureList);

        polylienOptions.setUseTexture(true);
        polylienOptions.width(220.0f);
        return polylienOptions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

        }
    }


    private void initView(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);//创建地图
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        if (aMap == null) {
            aMap = mapView.getMap();//初始化地图控制器对象
            setUpMap();
            //设置显示定位按钮 并且可以点击
            UiSettings settings = aMap.getUiSettings();
            aMap.setLocationSource(this);//设置了定位的监听
            // 是否显示定位按钮  cc
            settings.setMyLocationButtonEnabled(false);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
            //去掉右下角隐藏的缩放按钮
            aMap.getUiSettings().setZoomControlsEnabled(false);
        }

        //开始定位
        location();

    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//                .fromResource(R.mipmap.dh_man));// 设置小蓝点的图标

        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
    }

    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(mContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mapView != null) {
            mapView.onDestroy();
        }
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
                    //将地图移动到定位点
                    // aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    //mListener.onLocationChanged(aMapLocation);
                    //添加图钉
                    //  aMap.addMarker(getMarkerOptions(amapLocation));
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(aMapLocation.getCountry() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getCity() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getDistrict() + ""
                            + aMapLocation.getStreet() + ""
                            + aMapLocation.getStreetNum());
                    // Toast.makeText(getContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    // ActivityUtil.showToast(getContext(), buffer.toString());
                    isFirstLoc = false;
                    MLog.e("定位成功");

                }


            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("tag", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                //   cc   Toast.makeText(getContext(), "定位失败", Toast.LENGTH_LONG).show();
                MLog.e("定位失败");
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }

    //根据地址绘制需要显示的点
    public void makepoint(LatLng latLng, int mipmap) {
        Log.e("tag", "开始绘图");
        //自定义点标记
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("mark");
        if (mipmap == 1) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.tripstart)));//设置图标csxt1
        } else {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.tripend)));//设置图标csxt1
        }
        markerOptions.visible(true);
        markerOptions.zIndex(-1);//Marker就会显示在小蓝点下面
        LatLng ll = latLng;
        markerOptions.position(ll);
        CameraUpdate cu = CameraUpdateFactory.newLatLng(ll);
        aMap.animateCamera(cu);
        aMap.addMarker(markerOptions);
    }
}
