package com.nevs.car.activity.gmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.CircleBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityManager;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.LastInputEditText;
import com.nevs.car.z_start.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nevs.car.tools.util.DialogUtils.mBasIn;
import static com.nevs.car.tools.util.DialogUtils.mBasOut;

public class CircleRailActivity extends BaseActivity implements LocationSource, AMapLocationListener,
        AMap.OnMapClickListener, DistrictSearch.OnDistrictSearchListener, GeocodeSearch.OnGeocodeSearchListener,
        AMap.OnMapLongClickListener, AMap.OnMyLocationChangeListener {
    @BindView(R.id.mapViewC)
    MapView mapView;
    @BindView(R.id.edit_radiu)
    LastInputEditText editRadiu;
    @BindView(R.id.center_address)
    TextView centerAddress;
    @BindView(R.id.btn_config)
    TextView btnConfig;
    @BindView(R.id.n_view)
    RelativeLayout nView;
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
    public static final int REQUEST_CODE_START = 0;
    private String addressName = "";
    private GeocodeSearch geocoderSearch;
    private String addrThree = "武汉市";//地图加载时显示的区域边界(值为districtId)
    private LatLng latLngLong = null;
    Marker[] marker = new Marker[10];
    int totalMarker = 0;
    private Marker markers = null;
    private String formatAddress = "";
    private double lat;
    private double lon;
    private int radius;
    private double latlast, lonlast;
    private boolean isDing = true;
    private boolean isDelete = true;//清除按钮是否为删除地址围栏，false为删除，true为重新绘制

    @Override
    public int getContentViewResId() {
        return R.layout.activity_circle_rail;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initExture();
        initView(savedInstanceState);
        // initDraw();
    }

    private void initDraw() {
        // 移动地图，所有marker自适应显示。LatLngBounds与地图边缘10像素的填充区域
        // aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
        if (getIntent().getStringExtra("lat") != null) {
            double lattsp = Double.parseDouble(getIntent().getStringExtra("lat"));
            double lontsp = Double.parseDouble(getIntent().getStringExtra("lon"));
            double radius = Double.parseDouble(getIntent().getStringExtra("radius"));

//            //设置缩放级别
//            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
//            mapView.isShown();
//            //将地图移动到坐标点
//            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lattsp,lontsp)));
            //   aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lattsp,lontsp), 15, 0, 0)));

//            mapView.getMap().addCircle(new CircleOptions().center(new LatLng(lattsp,lontsp)).radius(radius* 1000));
//            aMap.moveCamera(
//                    CameraUpdateFactory.newLatLngZoom(new LatLng(lattsp,lontsp), 13));//13为缩放级别

            aMap.addCircle(new CircleOptions().center(new LatLng(lattsp, lontsp)).radius(radius * 1000).strokeColor(getResources().getColor(R.color.n_D1B48B)).fillColor(getResources().getColor(R.color.lightg)));
//            aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
//            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lattsp,lontsp)));

            MLog.e("中的半径：" + (int) radius);
            if ((int) radius <= 20) {
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lattsp, lontsp), 10, 0, 0)));
            } else if ((int) radius <= 45 && (int) radius > 20) {
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lattsp, lontsp), 9, 0, 0)));
            } else if ((int) radius <= 85 && (int) radius > 45) {
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lattsp, lontsp), 8, 0, 0)));
            } else if ((int) radius <= 145 && (int) radius > 85) {
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lattsp, lontsp), 7, 0, 0)));
            } else if ((int) radius <= 300 && (int) radius > 145) {
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lattsp, lontsp), 6, 0, 0)));
            } else {
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lattsp, lontsp), 5, 0, 0)));
            }


            // aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattsp,lontsp), 13f));
            btnConfig.setText(getResources().getString(R.string.toast_change));
            editRadiu.setText(((int) radius) + "");
            editRadiu.setCursorVisible(false);
            editRadiu.setEnabled(false);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.dh_wz);
            // marker[totalMarker] = aMap.addMarker(new MarkerOptions().position(latLng).title("").snippet("直线距离：" + dis + "m"));
            markers = aMap.addMarker(new MarkerOptions().position(new LatLng(lattsp, lontsp)).title("").snippet("直线距离：" + "m").icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

            getAddress(lattsp, lontsp);

        }
    }

    private void initExture() {
        if (getIntent().getStringExtra("lat") != null) {
            // cc   isDing=false;
            isDing = false;
            isDelete = false;
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
            settings.setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
            //去掉右下角隐藏的缩放按钮
            aMap.getUiSettings().setZoomControlsEnabled(false);
        }

        mapView.getMap().setOnMapClickListener(this);
        aMap.setOnMapLongClickListener(this);
        mapView.getMap().setLocationSource(this);
        aMap.setOnMyLocationChangeListener(this);


        //开始定位
        location();

        inits();

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
                    if (isDing) {//当需要展示之前设置的围栏的时候不移动为FALSE;默认为TURE
                        //设置缩放级别
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
                        //将地图移动到定位点
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                        //点击定位按钮 能够将地图的中心移动到定位点
                        mListener.onLocationChanged(aMapLocation);
                    } else {
                        //设置缩放级别
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                        //将地图移动到定位点
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                        //点击定位按钮 能够将地图的中心移动到定位点
                        mListener.onLocationChanged(aMapLocation);
                    }

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
                    //  ActivityUtil.showToast(getContext(), buffer.toString());
                    isFirstLoc = false;

                    if (isDelete) {

                    } else {
                        initDraw();
                    }

                    MLog.e("定位成功");
                    // initDraw();
                }

//                //圆形地理围栏
//                latLng = new LatLng(aMapLocation.getLatitude(),
//                        aMapLocation.getLongitude());//取出经纬度
//
//                    mapView.getMap().addCircle(new CircleOptions().center(latLng).radius(2000));


//                fillColor(Color.argb(progress, 1, 1, 1)).
//                        strokeColor(Color.argb(progress, 1, 1, 1)).
//                        strokeWidth(15));


            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                MLog.e("location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                //   cc   Toast.makeText(getContext(), "定位失败", Toast.LENGTH_LONG).show();
                MLog.e("定位失败");
            }
        }
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }


    @Override
    public void onMapClick(LatLng latLng) {

    }


    @Override
    public void onDistrictSearched(DistrictResult districtResult) {
//在回调函数中解析districtResult获取行政区划信息
//在districtResult.getAMapException().getErrorCode()=1000时调用districtResult.getDistrict()方法
//获取查询行政区的结果，详细信息可以参考DistrictItem类。


    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {


        if (rCode == 1000) {

            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {

                addressName = result.getRegeocodeAddress().getFormatAddress();

                //     txt_map_detailaddr.setText(addressName);

            } else {

            }
        } else {

        }
    }


    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }


    private void inits() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.clear(true);

        DistrictSearch search = new DistrictSearch(mContext);
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords(addrThree);//传入关键字
        query.setShowBoundary(true);//是否返回边界值
        // query.setShowChild(false);//不显示子区域边界
        search.setQuery(query);
        search.setOnDistrictSearchListener(this);//绑定监听器
        search.searchDistrictAsyn();//开始异步搜索
        //返回地址详细信息代码
        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);


    }


    //主线程在地图上添加边界线
    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
        }
    };

    @Override
    public void onMapLongClick(LatLng latLng) {
        showPoi(latLng);
        latLngLong = latLng;
        //设置中心点和缩放比例
        // aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        // aMap.moveCamera(CameraUpdateFactory.zoomTo(11));
        getAddress(latLng.latitude, latLng.longitude);
        lat = latLng.latitude;
        lon = latLng.longitude;
    }

    //放入经纬度就可以了
    public void getAddress(double latitude, double longitude) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {

            @Override
            public void onGeocodeSearched(GeocodeResult result, int rCode) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

                formatAddress = result.getRegeocodeAddress().getFormatAddress();
                Log.e("formatAddress", "formatAddress:" + formatAddress);
                Log.e("formatAddress", "rCode:" + rCode);
                centerAddress.setText(formatAddress);

            }
        });
        LatLonPoint lp = new LatLonPoint(latitude, longitude);
        RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    private void showPoi(final LatLng latLng) {
        if (markers != null) {
            markers.remove();
        }
        LatLng mylatlng;
        mylatlng = new LatLng(aMap.getMyLocation().getLatitude(), aMap.getMyLocation().getLongitude());
        float dis = AMapUtils.calculateLineDistance(mylatlng, latLng);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.dh_wz);
        // marker[totalMarker] = aMap.addMarker(new MarkerOptions().position(latLng).title("").snippet("直线距离：" + dis + "m"));
        markers = aMap.addMarker(new MarkerOptions().position(latLng).title("").snippet("直线距离：" + dis + "m").icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
        final GeocodeSearch geocodeSearch = new GeocodeSearch(mContext.getApplicationContext());
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                if (i == 0) {
                    System.out.println("i=0!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
                } else {
                    List<PoiItem> poiItemList;
                    poiItemList = regeocodeResult.getRegeocodeAddress().getPois();
                    marker[totalMarker].setTitle(regeocodeResult.getRegeocodeAddress().getDistrict() + getNearestName(poiItemList, latLng) + "附近");
                    //System.out.println(regeocodeResult.getRegeocodeAddress().getCity()+"<<<<<<<<<<<<<<<<<<");
                    //System.out.println(regeocodeResult.getRegeocodeAddress().getDistrict()+"<<<<<<<<<<<<<<<<<<<<");
                    totalMarker++;
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
    }


    public String getNearestName(List<PoiItem> poiItemList, LatLng targetLocation) {
        double minDis = 500, nowDis;
        String ret = "";
        for (int i = 0; i <= poiItemList.size() - 1; i++) {
            PoiItem poiItem;
            poiItem = poiItemList.get(i);
            LatLng poilatlng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            nowDis = AMapUtils.calculateLineDistance(targetLocation, poilatlng);
            if (nowDis < minDis) {
                minDis = nowDis;
                ret = poiItem.toString();
            }
        }
        return ret;
    }

    @Override
    public void onMyLocationChange(Location location) {
//        LatLng mylatlng;
//        mylatlng = new LatLng(location.getLatitude(), location.getLongitude());
//        for (int i = 0; i <= totalMarker; i++) {
//            float dis = AMapUtils.calculateLineDistance(mylatlng, marker[i].getPosition());
//            marker[i].setSnippet("直线距离：" + String.valueOf(dis));
//        }
    }


    @OnClick({R.id.back, R.id.btn_draw, R.id.btn_config})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_draw://改为了清除
                if (isDelete) {
                    deleteAll();
                } else {
                    showDialogDelete();
                }

                break;

            case R.id.btn_config:
                if (btnConfig.getText().toString().equals(getResources().getString(R.string.guide_draw))) {
                    if (latLngLong == null) {
                        ActivityUtil.showToast(this, getResources().getString(R.string.toast_longcenter));
                    } else if (editRadiu.getText().toString().trim().length() == 0) {
                        ActivityUtil.showToast(this, getResources().getString(R.string.nevs_hintradius));
                    } else {
                        // aMap.clear();
                        mapView.getMap().addCircle(new CircleOptions().center(latLngLong).radius((Double.parseDouble(editRadiu.getText().toString().trim())) * 1000).strokeColor(getResources().getColor(R.color.n_D1B48B)).fillColor(getResources().getColor(R.color.lightg)));

                        MLog.e("绘制中的半径：" + (int) Double.parseDouble(editRadiu.getText().toString().trim()));

                        if ((int) (Double.parseDouble(editRadiu.getText().toString().trim())) <= 20) {
                            setZoom(latLngLong, 10);
                            MLog.e("111");
                        } else if ((int) (Double.parseDouble(editRadiu.getText().toString().trim())) <= 45 && (int) (Double.parseDouble(editRadiu.getText().toString().trim())) > 20) {
                            setZoom(latLngLong, 9);
                            MLog.e("222");
                        } else if ((int) (Double.parseDouble(editRadiu.getText().toString().trim())) <= 85 && (int) (Double.parseDouble(editRadiu.getText().toString().trim())) > 45) {
                            setZoom(latLngLong, 8);
                            MLog.e("333");
                        } else if ((int) (Double.parseDouble(editRadiu.getText().toString().trim())) <= 145 && (int) (Double.parseDouble(editRadiu.getText().toString().trim())) > 85) {
                            setZoom(latLngLong, 7);
                            MLog.e("444");
                        } else if ((int) radius <= 300 && (int) radius > 145) {
                            setZoom(latLngLong, 6);
                        } else {
                            setZoom(latLngLong, 5);
                            MLog.e("555");
                        }


                        btnConfig.setText(getResources().getString(R.string.submit));
                        //绘制时的
                        radius = Integer.parseInt(editRadiu.getText().toString().trim());
                        lonlast = lon;
                        latlast = lat;
                    }
                } else if (btnConfig.getText().toString().equals(getResources().getString(R.string.toast_change))) {
                    startActivity(new Intent(CircleRailActivity.this, ChooseBookActivity.class));
                    finish();
                } else {
                    //qingqTSP
                    getTsp19();
                }


                break;
        }
    }


    private void deleteAll() {
        aMap.clear(true);
        //aMap.getMapScreenMarkers().clear();


        btnConfig.setText(getResources().getString(R.string.guide_draw));
        centerAddress.setText("");
        editRadiu.setText("");
        editRadiu.setCursorVisible(true);
        editRadiu.setEnabled(true);
    }

    private void showDialogDelete() {
        final NormalDialog dialog = new NormalDialog(CircleRailActivity.this);
        dialog.content(getResources().getString(R.string.dialog_deletegeo))//
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        getDeleteGeo();//取消预约
                    }
                });
    }

    private void getDeleteGeo() {
        DialogUtils.loading(CircleRailActivity.this, true);
        TspRxUtils.getDeleteGeo(mContext,
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(CircleRailActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin"},
                new Object[]{new SharedPHelper(CircleRailActivity.this).get("TSPVIN", "0")},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(CircleRailActivity.this);
                        MyUtils.upLogTSO(mContext, "删除地理围栏", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        ActivityUtil.showToast(CircleRailActivity.this, getResources().getString(R.string.toast_deletesuccess));
                        isDelete = true;
                        deleteAll();
                        isFirstLoc = true;
                        isDing = true;
                        location();

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(CircleRailActivity.this);
                        ActivityUtil.showToast(CircleRailActivity.this, str);
                        MyUtils.upLogTSO(mContext, "删除地理围栏", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }

    private void getTsp19() {
        /**
         *cccc:{"resultMessage":"","resultDescription":""}
         * */
        DialogUtils.loading(CircleRailActivity.this, true);
        TspRxUtils.getSet(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(CircleRailActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "enabled", "geoFenceType", "circle"
                },
                new Object[]{new SharedPHelper(CircleRailActivity.this).get("TSPVIN", "0"),
                        true,
                        "1",
                        new CircleBean(Float.parseFloat(lonlast + ""), Float.parseFloat(latlast + ""), radius),
                },
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(CircleRailActivity.this);
                        ActivityUtil.showToast(CircleRailActivity.this, getResources().getString(R.string.toast_submitsuccess));
                        MyUtils.upLogTSO(mContext, "设置电子围栏", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        startActivity(new Intent(CircleRailActivity.this, MainActivity.class));
                        ActivityManager.getInstance().exit();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(CircleRailActivity.this);
                        ActivityUtil.showToast(CircleRailActivity.this, getResources().getString(R.string.toast_submitfail));
                        MyUtils.upLogTSO(mContext, "设置电子围栏", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

    }


    public void setZoom(LatLng lng, int zoom) {
        // aMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(lng, zoom, 0, 0)));
    }
}