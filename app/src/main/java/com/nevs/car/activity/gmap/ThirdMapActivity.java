package com.nevs.car.activity.gmap;

import android.content.Intent;
import android.graphics.Color;
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
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.nevs.car.R;
import com.nevs.car.activity.service.SelectCityActivity;
import com.nevs.car.activity.service.SelectProvinceActivity;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.ReginBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityManager;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.JsonUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.z_start.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nevs.car.R.id.btn_draw;

public class ThirdMapActivity extends BaseActivity implements LocationSource, AMapLocationListener,
        AMap.OnMapClickListener, DistrictSearch.OnDistrictSearchListener, GeocodeSearch.OnGeocodeSearchListener {

    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.edit_electron)
    TextView editElectron;
    @BindView(btn_draw)
    TextView btnDraw;
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
    private LatLonPoint latLonPoint;
    private Marker regeoMarker;
    private LatLonPoint centerLatLng;//行政区域的中心点坐标
    private String addrThree = "武汉市";//地图加载时显示的区域边界(值为districtId)
    private PolygonOptions pOption;
    private LatLng markerLatLng;//用于传递给CompanyInfoActivity页面的经纬度
    private boolean isLoation = true;
    private boolean isDelete = true;//清除按钮是否为删除地址围栏，false为删除，true为重新绘制

    private LatLng latLng;
    // private int isCercleLand=0;
    private static final int REGION_REQUEST_CODE = 888;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_third_map;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initExture();
        initView(savedInstanceState);
        initGeo();
    }

    private void initGeo() {
        if (getIntent().getStringExtra("AdCode") != null) {
            draw(getIntent().getStringExtra("AdCode"));
            editElectron.setText(JsonUtil.getCityName(mContext,getIntent().getStringExtra("AdCode")));
            btnDraw.setText(getResources().getString(R.string.toast_change));
        }
    }

    private void initExture() {
        //1为圆形围栏，2为地理围栏
//       isCercleLand= getIntent().getIntExtra("isCercleLand",0);
//        MLog.e("isCercleLand:"+isCercleLand);

        if (getIntent().getStringExtra("AdCode") != null) {
            isLoation = false;
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

        aMap.setOnMapClickListener(this);
        aMap.setLocationSource(this);


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
                    if (isLoation) {
                        //设置缩放级别
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
                        //将地图移动到定位点
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    }
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(aMapLocation);
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
                    //   ActivityUtil.showToast(getContext(), buffer.toString());
                    isFirstLoc = false;
                }

                //圆形地理围栏
                latLng = new LatLng(aMapLocation.getLatitude(),
                        aMapLocation.getLongitude());//取出经纬度

//                fillColor(Color.argb(progress, 1, 1, 1)).
//                        strokeColor(Color.argb(progress, 1, 1, 1)).
//                        strokeWidth(15));


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
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }


    @Override
    public void onMapClick(LatLng latLng) {
//        aMap.clear();//清除所有的地图上覆盖物
//        Polygon polygon = aMap.addPolygon(pOption.strokeColor(Color.BLUE).fillColor(Color.argb(0, 0, 0, 0)));//重画区域边界
//        boolean flag = polygon.contains(latLng);

//            if (flag) {       //是否在区域内
//                //点击地图，显示标注
//                final Marker marker = aMap.addMarker(new MarkerOptions().
//                        position(latLng).
//                        title("详细地址").
//                        snippet("DefaultMarker"));
//                //根据latLng编译成地理描述
//                latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
//                getAddress(latLonPoint);
//                markerLatLng = latLng;
//            } else {
//                Toast.makeText(getContext(), "请在区域内点击", Toast.LENGTH_SHORT).show();
//            }


    }


    @Override
    public void onDistrictSearched(DistrictResult districtResult) {
//在回调函数中解析districtResult获取行政区划信息
//在districtResult.getAMapException().getErrorCode()=1000时调用districtResult.getDistrict()方法
//获取查询行政区的结果，详细信息可以参考DistrictItem类。


        if (districtResult == null || districtResult.getDistrict() == null) {
            return;
        }
        final DistrictItem item = districtResult.getDistrict().get(0);


        if (item == null) {
            return;
        }
        centerLatLng = item.getCenter();//得到行政中心点坐标
        if (centerLatLng != null) {  //地图加载时就显示行政区域
            aMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(new LatLng(centerLatLng.getLatitude(), centerLatLng.getLongitude()), 13));//13为缩放级别
        }


        new Thread() {
            private PolylineOptions polylineOption;


            public void run() {

                String[] polyStr = item.districtBoundary();
                if (polyStr == null || polyStr.length == 0) {
                    return;
                }
                for (String str : polyStr) {
                    String[] lat = str.split(";");
                    polylineOption = new PolylineOptions();
                    boolean isFirst = true;
                    LatLng firstLatLng = null;
                    for (String latstr : lat) {
                        String[] lats = latstr.split(",");
                        if (isFirst) {
                            isFirst = false;
                            firstLatLng = new LatLng(Double
                                    .parseDouble(lats[1]), Double
                                    .parseDouble(lats[0]));
                        }
                        polylineOption.add(new LatLng(Double
                                .parseDouble(lats[1]), Double
                                .parseDouble(lats[0])));
                    }
                    if (firstLatLng != null) {
                        polylineOption.add(firstLatLng);
                    }

                    polylineOption.width(6).color(Color.BLUE);
                    Message message = handler.obtainMessage();
                    message.obj = polylineOption;
                    handler.sendMessage(message);


                }
                pOption = new PolygonOptions();
                pOption.addAll(polylineOption.getPoints());//转换成PolygonOptions类型，为了判断marker是否在区域内

                //自动缩放
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
                for (int i = 0; i < polylineOption.getPoints().size(); i++) {
                    boundsBuilder.include(polylineOption.getPoints().get(i));//把所有点都include进去（LatLng类型）
                }
                aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 200));//第二个参数为四周留空宽度
            }
        }.start();

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


    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    private void inits() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

//        DistrictSearch search = new DistrictSearch(getContext());
//        DistrictSearchQuery query = new DistrictSearchQuery();
//        query.setKeywords(addrThree);//传入关键字
//        query.setShowBoundary(true);//是否返回边界值
//        // query.setShowChild(false);//不显示子区域边界
//        search.setQuery(query);
//        search.setOnDistrictSearchListener(this);//绑定监听器
//        search.searchDistrictAsyn();//开始异步搜索
//        //返回地址详细信息代码
//        geocoderSearch = new GeocodeSearch(getContext());
//        geocoderSearch.setOnGeocodeSearchListener(this);


//        Intent intent = new Intent();
//        intent.putExtra("detailAddr", txt_map_detailaddr.getText().toString());
//        intent.putExtra("markerLatLng", markerLatLng);
//        setResult(1, intent);
//        finish();

    }


    //主线程在地图上添加边界线
    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            PolylineOptions polylineOption = (PolylineOptions) msg.obj;
            aMap.addPolyline(polylineOption);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(7));
            //  btnDraw.setText(getResources().getString(R.string.submit));//网路请求成功以后变绘制
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({btn_draw, R.id.back, R.id.edit_electron, R.id.btn_clear, R.id.imageble})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case btn_draw:

                if (editElectron.getText().toString().trim().equals(getResources().getString(R.string.choseland)) ||
                        editElectron.getText().toString().trim().equals(getResources().getString(R.string.choseland))
                ) {
                    ActivityUtil.showToast(this, getResources().getString(R.string.toast_administrative));
                } else if (btnDraw.getText().equals(getResources().getString(R.string.toast_change))) {
                    startActivity(new Intent(ThirdMapActivity.this, ChooseBookActivity.class));
                    finish();
                } else {
                    //网络请求
                    getTsp19();
                }

                break;
            case R.id.back:
                finish();
                break;
            case R.id.edit_electron:
                startActivityForResult(new Intent(this, SelectCityActivity.class), REGION_REQUEST_CODE);
                break;
            case R.id.btn_clear:
                if (isDelete) {
                    aMap.clear(true);
                    // btnDraw.setText(getResources().getString(R.string.guide_draw));
                    editElectron.setText(getResources().getString(R.string.choseland));
                } else {
                    getDelete();
                }

                break;
            case R.id.imageble:
                startActivityForResult(new Intent(this, SelectCityActivity.class), REGION_REQUEST_CODE);
                break;
        }
    }

    private void getDelete() {
        DialogUtils.loading(ThirdMapActivity.this, true);
        TspRxUtils.getDeleteGeo(mContext,
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(ThirdMapActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin"},
                new Object[]{new SharedPHelper(ThirdMapActivity.this).get("TSPVIN", "0")},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(ThirdMapActivity.this);
                        MyUtils.upLogTSO(mContext, "删除地理围栏", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        ActivityUtil.showToast(ThirdMapActivity.this, getResources().getString(R.string.toast_deletesuccess));
                        aMap.clear(true);
                        isDelete = true;
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(ThirdMapActivity.this);
                        ActivityUtil.showToast(ThirdMapActivity.this, str);
                        MyUtils.upLogTSO(mContext, "删除地理围栏", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }

    private void draw(String cityname) {
        aMap.clear(true);
        DistrictSearch search = new DistrictSearch(mContext);
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords(cityname);//传入关键字,行政区域地址
        // query.setKeywords("武汉");
        query.setShowBoundary(true);//是否返回边界值
        // query.setShowChild(false);//不显示子区域边界
        search.setQuery(query);
        search.setOnDistrictSearchListener(this);//绑定监听器
        search.searchDistrictAsyn();//开始异步搜索
        //返回地址详细信息代码
        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGION_REQUEST_CODE && resultCode == 200) {
            String province = data.getStringExtra(SelectProvinceActivity.REGION_PROVINCE);
            String city = data.getStringExtra(SelectProvinceActivity.REGION_CITY);
            String area = data.getStringExtra(SelectProvinceActivity.REGION_AREA);


            addressName = city;
            if (city.contains("日喀则")) {
                editElectron.setText("日喀则");
                addressName = "日喀则";
            } else if (city.contains("林芝")) {
                editElectron.setText("林芝");
                addressName = "林芝";
            } else if (city.contains("那曲")) {
                editElectron.setText("那曲");
                addressName = "那曲";
            } else if (city.contains("阿里")) {
                editElectron.setText("阿里");
                addressName = "阿里";
            } else if (city.contains("山南")) {
                editElectron.setText("山南");
                addressName = "山南";
            } else if (city.contains("昌都")) {
                editElectron.setText("昌都");
                addressName = "昌都";
            }

            editElectron.setText(addressName);
            btnDraw.setText(getResources().getString(R.string.submit));
            draw(addressName);
        }
    }

    private void getTsp19() {
        /**
         *cccc:{"resultMessage":"","resultDescription":""}
         * */
        DialogUtils.loading(ThirdMapActivity.this, true);
        TspRxUtils.getSet(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(ThirdMapActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "enabled", "geoFenceType", "region"},
                new Object[]{new SharedPHelper(ThirdMapActivity.this).get("TSPVIN", "0"),
                        true,
                        "2",
                        new ReginBean(JsonUtil.getAdCode(mContext,addressName)),
                },
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(ThirdMapActivity.this);
                        ActivityUtil.showToast(ThirdMapActivity.this, getResources().getString(R.string.toast_submitsuccess));
                        MyUtils.upLogTSO(mContext, "设置电子围栏", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        startActivity(new Intent(ThirdMapActivity.this, MainActivity.class));
                        ActivityManager.getInstance().exit();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(ThirdMapActivity.this);
                        MyUtils.upLogTSO(mContext, "设置电子围栏", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        ActivityUtil.showToast(ThirdMapActivity.this, getResources().getString(R.string.toast_submitfail));
                    }
                }
        );

    }


}
