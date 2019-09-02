package com.nevs.car.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.nevs.car.R;
import com.nevs.car.activity.gmap.ChooseBookActivity;
import com.nevs.car.activity.gmap.CircleRailActivity;
import com.nevs.car.activity.gmap.GaoDeNaviActivity;
import com.nevs.car.activity.gmap.PoisActivity;
import com.nevs.car.activity.gmap.ShearchPoiActivity;
import com.nevs.car.activity.gmap.ThirdMapActivity;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.interfaces.DialogTwoListener;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.AndPermissionUtil;
import com.nevs.car.tools.util.BaiduNaiUtils;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.GaodeNaiUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by mac on 2018/4/2.
 */

public class GuideFragment extends Fragment implements LocationSource, AMapLocationListener,
        AMap.OnMapClickListener, AMap.OnMyLocationChangeListener, AMap.OnMapTouchListener {
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.poi_text)
    TextView poiText;
    @BindView(R.id.btn_local)
    ImageView btnLocal;
    @BindView(R.id.guideCar)
    TextView guideCar;
    Unbinder unbinder;
    @BindView(R.id.state_car)
    LinearLayout stateCar;

    @BindView(R.id.car_name)
    TextView carName;
    @BindView(R.id.car_location)
    TextView carLocation;
    @BindView(R.id.car_distans)
    TextView carDistans;
    @BindView(R.id.car_hink)
    TextView carHink;
    @BindView(R.id.car_speed)
    TextView carSpeed;
    @BindView(R.id.carLocation)
    ImageButton carLocationbutton;
    @BindView(R.id.btn_books)
    ImageButton btnBooks;
    @BindView(R.id.imageone)
    ImageButton imageone;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.carLocation_n)
    LinearLayout carLocationN;
    @BindView(R.id.btn_books_n)
    LinearLayout btnBooksN;
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
    //判断车是否定位成功，定位失显示人，成功显示车
    private boolean iscar = false;
    public static final int REQUEST_CODE_START = 0;
    private MyLocationStyle myLocationStyle;

    private String addressName = "";
    private GeocodeSearch geocoderSearch;
    private LatLonPoint latLonPoint;
    private Marker regeoMarker;
    private LatLonPoint centerLatLng;//行政区域的中心点坐标
    private String addrThree = "武汉市";//地图加载时显示的区域边界(值为districtId)
    private PolygonOptions pOption;
    private LatLng markerLatLng;//用于传递给CompanyInfoActivity页面的经纬度

    private LatLng latLng;
    private String qlat = null;
    private String qlon = null;
    private double latt;
    private double lonn;
    private LatLng latLngCar;
    private List<Object> listLocation = new ArrayList<>();//保存经纬度
    private CameraUpdate cameraUpdate;
    private BaseAnimatorSet mBasIn;
    private BaseAnimatorSet mBasOut;
    private boolean flag = true;
    private boolean fc = true;

    private Marker[] marker = new Marker[10];
    private boolean landnow = true;
    private int totalMarker;
    private String city = null;
    private List<Object> list = new ArrayList<>();
    private String s = "";
    private String dis = "";
    private boolean isSucc = false;//是否车辆信息获取成功，定位成功
    private boolean ishavacar = false;//是否有车

    int count = 0;
    private boolean isTouch = false;
    private MarkerOptions markerOptions;
    private Marker markerlast;

    /**
     * 需要进行检测的权限数组
     */
//    protected String[] needPermissions = {
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.READ_PHONE_STATE
//    };
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;


    public static GuideFragment newInstance() {
        Bundle args = new Bundle();
        GuideFragment fragment = new GuideFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view, savedInstanceState);
        MyUtils.setPadding(rlTitle, getContext());
        initNoCar();


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        MLog.e("onHiddenChangedguide1");
        if (hidden) {
            MLog.e("onHiddenChangedguideture");
        } else {
            MLog.e("onHiddenChangedguide2");
            if (new SharedPHelper(getContext()).get("TSPVIN", "0").equals("0")) {
                ishavacar = false;
                carLocationbutton.setVisibility(View.GONE);
                carLocationN.setVisibility(View.GONE);
                btnBooks.setVisibility(View.GONE);
                btnBooksN.setVisibility(View.GONE);
            } else {
                ishavacar = true;
                carLocationbutton.setVisibility(View.VISIBLE);
                carLocationN.setVisibility(View.VISIBLE);
                btnBooks.setVisibility(View.VISIBLE);
                btnBooksN.setVisibility(View.VISIBLE);
                if (new SharedPHelper(getContext()).get(Constant.TSPISCAROWER, "").equals("YES")) {

                } else {
                    btnBooks.setVisibility(View.GONE);
                    btnBooksN.setVisibility(View.GONE);
                }
                getTsp();
            }
        }


    }

    private void initNoCar() {
        if (new SharedPHelper(getContext()).get("TSPVIN", "0").equals("0")) {
            carLocationbutton.setVisibility(View.GONE);
            carLocationN.setVisibility(View.GONE);
            btnBooks.setVisibility(View.GONE);
            btnBooksN.setVisibility(View.GONE);

        } else {
            carLocationbutton.setVisibility(View.VISIBLE);
            carLocationN.setVisibility(View.VISIBLE);
            btnBooks.setVisibility(View.VISIBLE);
            btnBooksN.setVisibility(View.VISIBLE);
            ishavacar = true;
            if (new SharedPHelper(getContext()).get(Constant.TSPISCAROWER, "").equals("YES")) {

            } else {
                btnBooks.setVisibility(View.GONE);
                btnBooksN.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.map, R.id.poi_text, R.id.btn_books, R.id.btn_local, R.id.start_local,
            R.id.carLocation, R.id.guideCar, R.id.btn_guides, R.id.imageone, R.id.pub})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.pub:
                startActivity(new Intent(getContext(), PoisActivity.class).putExtra("city", city));
                break;
            case R.id.map:
                break;
            case R.id.poi_text:

                break;
            case R.id.btn_books:
                getTsp20();
                //    startActivity(new Intent(getContext(), ChooseBookActivity.class));
                break;
            case R.id.btn_local:
                startActivity(new Intent(getContext(), ShearchPoiActivity.class));
                break;
            case R.id.start_local:
                moveTo();//定位到人
// 725               if (stateCar.getVisibility() != View.GONE) {
//                    stateCar.setVisibility(View.GONE);
//                }
                break;
            case R.id.carLocation:
                getTsp();
//                if (iscar) {
//                    if (isSucc) {
//                        moveToCar();//定位到车
//                        stateCar.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    getTsp();
//                }

                break;
            case R.id.guideCar:
                getPermis();
// 622              if(getDiatance2(latt, lonn)>1000){//1公里内可以步行导航
//                    ActivityUtil.showToast(getContext(),getResources().getString(R.string.hint_walk));
//                }else {
//                    showGuide(latt, lonn);
//                }
//

                //showGuide(30.552245,114.204678);
                break;
            case R.id.btn_guides:
                //  showGuides();
                break;
            case R.id.imageone:
                if (landnow) {
                    aMap.setTrafficEnabled(true);//开始实时交通
                    aMap.setMapType(AMap.MAP_TYPE_NORMAL);//有普通，卫星，夜间模式
                    ActivityUtil.showToast(getContext(), getContext().getResources().getString(R.string.toast_traficopen));
                    imageone.setImageDrawable(getResources().getDrawable(R.mipmap.n_landon));
                    landnow = false;
                } else {
                    aMap.setTrafficEnabled(false);//关闭实时交通
                    aMap.setMapType(AMap.MAP_TYPE_NORMAL);//有普通，卫星，夜间模式
                    ActivityUtil.showToast(getContext(), getContext().getResources().getString(R.string.toast_traficclose));
                    imageone.setImageDrawable(getResources().getDrawable(R.mipmap.n_landoff));
                    landnow = true;
                }

                break;
        }
    }

    private void showGuides0() {
//        final String[] stringItems = {getResources().getString(R.string.gaodemap),
//                getResources().getString(R.string.baidumap)
//        };
//        final ActionSheetDialog dialog = new ActionSheetDialog(getContext(), stringItems, null);
//        dialog.isTitleShow(false)
//                .show();
//        dialog.setOnOperItemClickL(new OnOperItemClickL() {
//            @Override
//            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    default:
//                    case 0:
//                        //高德地图
//                        if (GaodeNaiUtils.isInstallPackage()) {
//                            startGaode();
//                        } else {
//                            ActivityUtil.showToast(getContext(),getContext().getResources().getString(R.string.toast_installgao));
//                        }
//                        break;
//                    case 1:
//                        //百度地图
//                        if (BaiduNaiUtils.isInstalled()) {
//                            starBaidu();
//                        } else {
//                            ActivityUtil.showToast(getContext(),getContext().getResources().getString(R.string.toast_baidu));
//                        }
//                        break;
//                }
//                if (dialog != null) {
//                    dialog.dismiss();
//                }
//            }
//        });

        final String[] stringItems = {getResources().getString(R.string.gaodemap),
        };
        final ActionSheetDialog dialog = new ActionSheetDialog(getContext(), stringItems, null);
        dialog.isTitleShow(false).cancelText(getResources().getString(R.string.cancle))
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    default:
                    case 0:
                        //高德地图
                        if (GaodeNaiUtils.isInstallPackage()) {
                            startGaode();
                        } else {
                            ActivityUtil.showToast(getContext(), getContext().getResources().getString(R.string.toast_installgao));
                        }
                        break;

                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void startGaode0() {

        GaodeNaiUtils.openGaoDeMap(getContext(), lonn, latt, "", "");
    }

    private void starBaidu0() {
        // BaiduNaiUtils.invokeNavi(getContext(), "db09", "国能汽车", "39.9761,116.3282");
    }

    private void moveToCar() {
        //aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLngCar));
        showTwo();
    }

    private void moveTo() {
        //  aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        // showTwo();

//        改变可视区域为指定位置  MACK点击移到中间位置
//        CameraPosition4个参数分别为位置，缩放级别，目标可视区域倾斜度，可视区域指向方向（正北逆时针算起，0-360）
// 605       cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 15, 0, 30));
//        aMap.moveCamera(cameraUpdate);//地图移向指定区域

        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
    }


    private void showTwo() {
        //自动缩放
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
        boundsBuilder.include(latLng);//把所有点都include进去（LatLng类型）
        boundsBuilder.include(latLngCar);
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 300));//第二个参数为四周留空宽度
    }

    private void init(View view, Bundle savedInstanceState) {
        mBasIn = new BounceTopEnter();
        mBasOut = new SlideBottomExit();
        mapView.onCreate(savedInstanceState);//创建地图
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        if (aMap == null) {
            aMap = mapView.getMap();//初始化地图控制器对象

            //设置语言
//            if(MyUtils.getLanguage(getContext())){
//                aMap.setMapLanguage(AMap.CHINESE);
//            }else {
//                aMap.setMapLanguage(AMap.ENGLISH);
//            }

            setUpMap();//自定义定位蓝点

            //设置显示定位按钮 并且可以点击
            UiSettings settings = aMap.getUiSettings();
            aMap.setLocationSource(this);//设置了定位的监听设置定位资源。如果不设置此定位资源则定位按钮不可点击。并且实现activate激活定位,停止定位的回调方法
            // 是否显示定位按钮  cc
            settings.setMyLocationButtonEnabled(false);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
            //去掉右下角隐藏的缩放按钮
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.setOnMapClickListener(this);
            aMap.setOnMyLocationChangeListener(this);

        }

        //开始定位
        location();

        //  inits();
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//                .fromResource(R.mipmap.dh_man));// 设置小蓝点的图标   605

        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
    }

    private void getTsp() {
        TspRxUtils.getLocation(getContext(),
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(getContext()).get(Constant.ACCESSTOKENS, "")},
                new SharedPHelper(getContext()).get("TSPVIN", "0") + "",
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        MyUtils.upLogTSO(getContext(), "获取车辆位置", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        fc = false;
                        iscar = true;
                        listLocation.addAll((Collection<?>) obj);
                        MLog.e("T经纬度：" + listLocation.get(0) + "," + listLocation.get(1));
                        // addMarker();
                        latLngCar = new LatLng(Double.parseDouble(String.valueOf(listLocation.get(1))), Double.parseDouble(String.valueOf(listLocation.get(0))));
                        getAddress(Double.parseDouble(String.valueOf(listLocation.get(1))), Double.parseDouble(String.valueOf(listLocation.get(0))));
                        latt = Double.parseDouble(String.valueOf(listLocation.get(1)));
                        lonn = Double.parseDouble(String.valueOf(listLocation.get(0)));
                        stateCar.setVisibility(View.VISIBLE);

                        moveToCar();


                        upView();
                        isSucc = true;

                    }

                    @Override
                    public void onFial(String str) {
                        // ActivityUtil.showToast(getContext(), getContext().getResources().getString(R.string.toast_locationfail));
                        iscar = false;
                        if (str.contains("未授权的请求")) {
                            MyUtils.exitToLongin(getContext());
                        } else if (str.contains("401")) {
                            MyUtils.exitToLongin401(getContext());
                        }
                        MyUtils.upLogTSO(getContext(), "获取车辆位置", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }

    private void upView() {
        MLog.e("111");

        String sp = (new SharedPHelper(getContext()).get("speed", "").toString()).split("[.]")[0];
        carSpeed.setText(sp + "km/h");
        MLog.e("444" + new SharedPHelper(getContext()).get("speed", "").toString() + "km/h");
        carName.setText(String.valueOf(new SharedPHelper(getContext()).get(Constant.CARALIAS, "")));
        MLog.e("555" + String.valueOf(new SharedPHelper(getContext()).get(Constant.CARALIAS, "")));
        getAddress(latt, lonn);
        carLocation.setText(s);
        MLog.e("666" + s);
        carDistans.setText(getDiatance(latt, lonn));
        MLog.e("777" + getDiatance(latt, lonn));

        String dd = new SharedPHelper(getContext()).get("vehiclestatus", "") + "";
        switch (dd) {
            case "1":
                carHink.setText(getResources().getString(R.string.running));
                MLog.e("333" + new SharedPHelper(getContext()).get("vehiclestatus", "").toString());
                break;
            case "0":
                carHink.setText(getResources().getString(R.string.stopping));
                MLog.e("333" + new SharedPHelper(getContext()).get("vehiclestatus", "").toString());

                break;
            case "2":
                carHink.setText(getResources().getString(R.string.nevs_charging));
                MLog.e("333" + new SharedPHelper(getContext()).get("vehiclestatus", "") + "");

                break;
            case "3":
                carHink.setText(getResources().getString(R.string.offline));
                break;
        }

    }

    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getContext().getApplicationContext());
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

        //  mLocationOption.setLocationCacheEnable(false);    // 定位的缓存结果设置选项

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
        //停止定位后，本地定位服务并不会被销毁
        mLocationClient.stopLocation();
        //销毁定位客户端，同时销毁本地定位服务。
        mLocationClient.onDestroy();
        deactivate();
        try {
            unbinder.unbind();
        } catch (Exception E) {
            MLog.e(" guidefragment unbinder" + "异常");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (isNeedCheck) {
            checkPermissions(needPermissions);
            isNeedCheck = false;
        }


        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
        if (fc) {
            getTsp();
        }
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
        //  getTsp();// 获取车辆位置信息

//        if (mListener != null && aMapLocation != null) {
//            mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
//        }

        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                if (flag) {
                    if (ishavacar) {
                        getTsp();// 获取车辆位置信息
                    }
                }
                flag = false;
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
                    //设置缩放级别 缩放级别为4-20级）

                    aMap.moveCamera(CameraUpdateFactory.zoomTo(15));


//  tjcc                  //将地图移动到定位点 先显示车辆位置，因此先不调用

                    if (iscar) {
                    } else {
                        // 将地图移动到定位点
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    }


                    // 设置显示的焦点，即当前地图显示为当前位置
                    //  aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), 13));
                    //点击定位按钮 能够将地图的中心移动到定位点,显示系统小蓝点
                    //605    mListener.onLocationChanged(aMapLocation);

                    isFirstLoc = false;
                }

//                if(!isTouch){
//                    mListener.onLocationChanged(aMapLocation);
//
//                }


                if (true) {// tjfff isFirstLoc
                    //设置缩放级别 缩放级别为4-20级）

                    //tjfff   aMap.moveCamera(CameraUpdateFactory.zoomTo(13));


                    //CC getAddress(aMapLocation.getLatitude()*1.0009,aMapLocation.getLongitude());

                    // addMarker(aMapLocation.getLatitude()*1.0009,aMapLocation.getLongitude());

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
                    // ActivityUtil.showToast(getContext(), ç);
                    MLog.e("当前地址：" + buffer.toString());


                    city = aMapLocation.getCity();//获取所在城市信息
                    MLog.e("当前城市：" + city);

                    latLng = new LatLng(aMapLocation.getLatitude(),
                            aMapLocation.getLongitude());//取出经纬度
                    qlat = aMapLocation.getLatitude() + "";
                    qlon = aMapLocation.getLongitude() + "";
                    MLog.e("纬度和经度：" + aMapLocation.getLatitude() + "  "
                            + aMapLocation.getLongitude());
                    //点击定位按钮 能够将地图的中心移动到定位点,显示系统小蓝点
                    // mListener.onLocationChanged(aMapLocation);

                    showLocation(latLng);

                }


                //30.546291  114.196607
                //圆形地理围栏
//                mapView.getMap().addCircle(new CircleOptions().center(latLng).radius(2000));
//                fillColor(Color.argb(progress, 1, 1, 1)).
//                        strokeColor(Color.argb(progress, 1, 1, 1)).
//                        strokeWidth(15));


            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                //   cc   Toast.makeText(getContext(), "定位失败", Toast.LENGTH_LONG).show();
                MLog.e("定位失败");
            }
        }

    }

    private void getAddress(final double lat, final double lon) {
        GeocodeSearch geocodeSearch = new GeocodeSearch(getContext());//地址查询器

        //设置查询参数,
        //三个参数依次为坐标，范围多少米，坐标系
        RegeocodeQuery regeocodeQuery = new RegeocodeQuery(new LatLonPoint(lat, lon), 200, GeocodeSearch.AMAP);

        //设置查询结果监听
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            //根据坐标获取地址信息调用
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                s = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                Log.e("tag", "获得请求结果" + s);
                makepoint(s, lat, lon);
            }

            //根据地址获取坐标信息是调用
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
            }
        });

        geocodeSearch.getFromLocationAsyn(regeocodeQuery);//发起异步查询请求
    }

    //根据地址绘制需要显示的点
    public void makepoint(String s, final double lat, final double lon) {
        Log.e("tag", "开始绘图");
        //北纬39.22，东经116.39，为负则表示相反方向
        LatLng latLngg = new LatLng(lat, lon);
        Log.e("tag", "地址:" + s);

        //使用默认点标记
        //Marker maker=aMap.addMarker(new MarkerOptions().position(latLng).title("地点:").snippet(s));


        //自定义点标记
        MarkerOptions markerOptions = new MarkerOptions();
        // markerOptions.position(new LatLng(lat, lon)).title("地点").snippet(s);
        markerOptions.position(new LatLng(lat, lon));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.n_car_l)));//设置图标csxt1  dh_car
        markerOptions.visible(true);
        markerOptions.zIndex(-1);//Marker就会显示在小蓝点下面
        aMap.addMarker(markerOptions);

        //改变可视区域为指定位置  MACK点击移到中间位置
        //CameraPosition4个参数分别为位置，缩放级别，目标可视区域倾斜度，可视区域指向方向（正北逆时针算起，0-360）
//        cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLngg, 17, 0, 30));
//        aMap.moveCamera(cameraUpdate);//地图移向指定区域

        //将地图移动到显示车辆位置
        //  aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lat, lon)));
        showTwo();


        //位置坐标的点击事件
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.setInfoWindowEnable(false);
                //Toast.makeText(MainActivity.this,"点击指定位置",Toast.LENGTH_SHORT).show();
                //  return false;

                if (marker.getPosition().longitude != latLng.longitude) {
                    MLog.e("点击车辆图标");
                    if (stateCar.getVisibility() == View.GONE) {
                        stateCar.setVisibility(View.VISIBLE);
                    }

                }
                if (!marker.isInfoWindowShown()) {
                    marker.showInfoWindow();

                } else {
                    marker.hideInfoWindow();
                }
                return true;

            }
        });
        //位置上面信息窗口的点击事件
        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //Toast.makeText(getContext(),"点击了我的地点",Toast.LENGTH_SHORT).show();
                showGuide(lat, lon);
            }
        });
    }

    private void showGuide(final double lat, final double lon) {
        final NormalDialog dialog = new NormalDialog(getActivity());
        dialog.content(getContext().getResources().getString(R.string.toast_isguides))//
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
                        guide(lat, lon);
                    }
                });
    }

    private void guide(final double lat, final double lon) {
        Intent intent = new Intent(getContext(), GaoDeNaviActivity.class);
        intent.putExtra("qlat", qlat);
        intent.putExtra("qlon", qlon);
        intent.putExtra("zlat", lat + "");
        intent.putExtra("zlon", lon + "");

        startActivity(intent);
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
    public void onMapClick(LatLng latLng) {//地图点击监听
        if (stateCar.getVisibility() != View.GONE) {
            stateCar.setVisibility(View.GONE);
        }

        //cc  showPoi(latLng);


//        aMap.clear();//清除所有的地图上覆盖物
//        Polygon polygon = aMap.addPolygon(pOption.strokeColor(Color.BLUE).fillColor(Color.argb(0, 0, 0, 0)));//重画区域边界
//        boolean flag = polygon.contains(latLng);
//
//        if (flag) {       //是否在区域内
//            //点击地图，显示标注
//            final Marker marker = aMap.addMarker(new MarkerOptions().
//                    position(latLng).
//                    title("详细地址").
//                    snippet("DefaultMarker"));
//            //根据latLng编译成地理描述
//            latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
//          //  getAddress(latLonPoint);
//            markerLatLng = latLng;
//        } else {
//            Toast.makeText(getContext(), "请在区域内点击", Toast.LENGTH_SHORT).show();
//        }

    }

    /**
     * 简单的自定义MARK图标
     */
    public MarkerOptions myMark(float dis, LatLng latLng) {
        Resources res = getContext().getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.mipmap.dh_mbwz);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("")
                .snippet("直线距离：" + dis + "m")
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        return markerOptions;
    }

    private void showPoi(final LatLng latLng) {
        aMap.clear(true);
        LatLng mylatlng;
        mylatlng = new LatLng(aMap.getMyLocation().getLatitude(), aMap.getMyLocation().getLongitude());
        float dis = AMapUtils.calculateLineDistance(mylatlng, latLng);
        marker[totalMarker] = aMap.addMarker(myMark(dis, latLng));
        final GeocodeSearch geocodeSearch = new GeocodeSearch(getContext().getApplicationContext());
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
        try {
            LatLng mylatlng;
            mylatlng = new LatLng(location.getLatitude(), location.getLongitude());
// cc           for (int i = 0; i <= totalMarker; i++) {
//                float dis = AMapUtils.calculateLineDistance(mylatlng, marker[i].getPosition());
//                marker[i].setSnippet("直线距离：" + String.valueOf(dis));
//            }
        } catch (Exception e) {
            MLog.e("获取距离异常" + e);
        }
    }
//    @Override
//    public void onDistrictSearched(DistrictResult districtResult) {
////在回调函数中解析districtResult获取行政区划信息
////在districtResult.getAMapException().getErrorCode()=1000时调用districtResult.getDistrict()方法
////获取查询行政区的结果，详细信息可以参考DistrictItem类。
//
//        if (districtResult == null || districtResult.getDistrict() == null) {
//            return;
//        }
//        final DistrictItem item = districtResult.getDistrict().get(0);
//
//
//        if (item == null) {
//            return;
//        }
//        centerLatLng = item.getCenter();//得到行政中心点坐标
//        if (centerLatLng != null) {  //地图加载时就显示行政区域
//            aMap.moveCamera(
//                    CameraUpdateFactory.newLatLngZoom(new LatLng(centerLatLng.getLatitude(), centerLatLng.getLongitude()), 13));//13为缩放级别
//        }
//
//
//        new Thread() {
//            private PolylineOptions polylineOption;
//
//
//            public void run() {
//
//                String[] polyStr = item.districtBoundary();
//                if (polyStr == null || polyStr.length == 0) {
//                    return;
//                }
//                for (String str : polyStr) {
//                    String[] lat = str.split(";");
//                    polylineOption = new PolylineOptions();
//                    boolean isFirst = true;
//                    LatLng firstLatLng = null;
//                    for (String latstr : lat) {
//                        String[] lats = latstr.split(",");
//                        if (isFirst) {
//                            isFirst = false;
//                            firstLatLng = new LatLng(Double
//                                    .parseDouble(lats[1]), Double
//                                    .parseDouble(lats[0]));
//                        }
//                        polylineOption.add(new LatLng(Double
//                                .parseDouble(lats[1]), Double
//                                .parseDouble(lats[0])));
//                    }
//                    if (firstLatLng != null) {
//                        polylineOption.add(firstLatLng);
//                    }
//
//                    polylineOption.width(6).color(Color.BLUE);
//                    Message message = handler.obtainMessage();
//                    message.obj = polylineOption;
//                    handler.sendMessage(message);
//
//
//                }
//                pOption = new PolygonOptions();
//                pOption.addAll(polylineOption.getPoints());//转换成PolygonOptions类型，为了判断marker是否在区域内
//
//            }
//        }.start();
//
//    }
//
//    /**
//     * 逆地理编码回调
//     */
//    @Override
//    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
//
//
//        if (rCode == 1000) {
//
//            if (result != null && result.getRegeocodeAddress() != null
//                    && result.getRegeocodeAddress().getFormatAddress() != null) {
//
//                addressName = result.getRegeocodeAddress().getFormatAddress();
//
//                //     txt_map_detailaddr.setText(addressName);
//
//            } else {
//
//            }
//        } else {
//
//        }
//    }
//
//
//    @Override
//    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
//
//    }
//
//
//
//
//
//    /**
//     * 响应逆地理编码
//     */
//    public void getAddress(final LatLonPoint latLonPoint) {
//        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
//                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
//    }
//
//    private void inits() {
//        if (aMap == null) {
//            aMap = mapView.getMap();
//        }
//        aMap.clear();
//
//        DistrictSearch search = new DistrictSearch(getContext());
//        DistrictSearchQuery query = new DistrictSearchQuery();
//        query.setKeywords(addrThree);//传入关键字
//        query.setShowBoundary(true);//是否返回边界值
//       // query.setShowChild(false);//不显示子区域边界
//        search.setQuery(query);
//        search.setOnDistrictSearchListener(this);//绑定监听器
//        search.searchDistrictAsyn();//开始异步搜索
//        //返回地址详细信息代码
//        geocoderSearch = new GeocodeSearch(getContext());
//        geocoderSearch.setOnGeocodeSearchListener(this);
//
//
////        Intent intent = new Intent();
////        intent.putExtra("detailAddr", txt_map_detailaddr.getText().toString());
////        intent.putExtra("markerLatLng", markerLatLng);
////        setResult(1, intent);
////        finish();
//
//    }
//
//
//
//    //主线程在地图上添加边界线
//    private Handler handler = new Handler(Looper.getMainLooper()) {
//        public void handleMessage(Message msg) {
//            PolylineOptions polylineOption = (PolylineOptions) msg.obj;
//            aMap.addPolyline(polylineOption);
//
//        }
//    };
//

    //添加marker
    public void addMarker(double lat, double lon) {
        //new LatLng(lat, lon) 39.055861,117.214284
        //aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(String.valueOf(listLocation.get(1))),Double.parseDouble(String.valueOf(listLocation.get(1)))), 19));
        // aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.055861,117.214284),17));


        MarkerOptions markerOptions = new MarkerOptions();
        // markerOptions.position(new LatLng(Double.parseDouble(String.valueOf(listLocation.get(1))),Double.parseDouble(String.valueOf(listLocation.get(1)))));
        // 117.214284,39.055861
        markerOptions.position(new LatLng(lat, lon));
        markerOptions.title("YOUR CAR");
        markerOptions.visible(true);
        markerOptions.zIndex(-1);//Marker就会显示在小蓝点下面
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.csxt1));
        markerOptions.icon(bitmapDescriptor);
        aMap.addMarker(markerOptions);

        //将地图移动到显示车辆位置
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lat, lon)));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getTsp20() {
        /**
         * cccc:{"ResultMessage":"","ResultDescription":"","Vin":"LTPSBSIMULATOR001","Enabled":false,"GeoFenceType":"1","Circle":null}
         *
         *
         *cccc:{"resultMessage":"","resultDescription":"","vin":"LTPSBSIMULATOR001","enabled":true,
         * "geoFenceType":"string","circle":{"centerPointLongitude":35.0333,"centerPointLatitude":116.7469,"radius":10},"region":{"adCode":"A003"},"polygon":[{"longitude":0.0,"latitude":0.0}]}
         * */
        list.clear();
        DialogUtils.loading(getContext(), true);
        TspRxUtils.getGeofence(getContext(),
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(getContext()).get(Constant.ACCESSTOKENS, "")},
                new SharedPHelper(getContext()).get("TSPVIN", "0").toString(),
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(getActivity());
                        MyUtils.upLogTSO(getContext(), "获取围栏信息", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        list.addAll((Collection<?>) obj);
                        if (list.size() == 0) {
                            startActivity(new Intent(getContext(), ChooseBookActivity.class));
                        } else if (list.size() == 3) {
                            startActivity(new Intent(getContext(), CircleRailActivity.class)
                                    .putExtra("lon", list.get(0) + "")
                                    .putExtra("lat", list.get(1) + "")
                                    .putExtra("radius", list.get(2) + "")

                            );
                        } else if (list.size() == 1) {
                            startActivity(new Intent(getContext(), ThirdMapActivity.class)
                                    .putExtra("AdCode", list.get(0) + ""));
                        }

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(getActivity());
                        MyUtils.upLogTSO(getContext(), "获取围栏信息", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        MLog.e("失败，数据有NULL");
                        // ActivityUtil.showToast(getContext(),str);
                        startActivity(new Intent(getContext(), ChooseBookActivity.class));
                    }
                }
        );

    }


    public String getDiatance(double lat, double lon) {//计算距离
        String str = null;
        try {
            dis = String.valueOf(AMapUtils.calculateLineDistance(latLng, new LatLng(lat, lon)));
            MLog.e("距离：M" + dis);
            if (Double.parseDouble(dis) >= 1000) {
                String diss = String.valueOf(Double.parseDouble(dis) / 1000);
                MLog.e("距离：km" + diss);
                DecimalFormat myformat = new DecimalFormat("0.00");
                str = myformat.format(Double.parseDouble(diss)) + "km";
            } else {
                str = (int) Double.parseDouble(dis) + "m";
            }
        } catch (Exception e) {
            MLog.e("算距异常");
        }
        return str;
    }

    public long getDiatance2(double lat, double lon) {//计算距离
        long str = 0;
        try {
            dis = String.valueOf(AMapUtils.calculateLineDistance(latLng, new LatLng(lat, lon)));
            MLog.e("距离：M" + dis);
            str = (long) Double.parseDouble(dis);
        } catch (Exception e) {
            MLog.e("算距异常");
        }
        return str;
    }


    //----------以下动态获取权限---------


    /**
     * 检查权限
     *
     * @param
     * @since 2.5.0
     */
    private void checkPermissions(String... permissions) {
        //获取权限列表
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            //list.toarray将集合转化为数组
            ActivityCompat.requestPermissions(getActivity(),
                    needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }


    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        //for (循环变量类型 循环变量名称 : 要被遍历的对象)
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(), perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {      //没有授权
                showMissingPermissionDialog();              //显示提示信息
                isNeedCheck = false;
            }
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.notifyTitle);
//        builder.setMessage(R.string.notifyMsg);
//
//        // 拒绝, 退出应用
//        builder.setNegativeButton(R.string.cancel,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //finish();
//                       // dialog.dismiss();
//                        MLog.e("用户点击取消");
//                    }
//                });
//
//        builder.setPositiveButton(R.string.setting,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startAppSettings();
//                    }
//                });
//
//        builder.setCancelable(false);
//
//        builder.show();


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.notifyTitle))
                .setMessage(getResources().getString(R.string.notifyMsg))
                .setPositiveButton(getResources().getString(R.string.setting), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startAppSettings();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //dialog.dismiss();
                        MLog.e("用户点击取消");
                    }
                });
        builder.create().show();

    }


    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getContext().getPackageName()));
        startActivity(intent);
    }


    @Override
    public void onTouch(MotionEvent motionEvent) {
        MLog.e("onTouch");
        isTouch = true;
    }

    private void showLocation(LatLng latLngs) {
        if (markerlast != null) {
            markerlast.remove();
        }
        //自定义点标记
        if (markerOptions == null) {
            markerOptions = new MarkerOptions();
        }
        // markerOptions.position(new LatLng(lat, lon)).title("地点").snippet(s);
        markerOptions.position(latLngs);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.gps_point)));//设置图标csxt1  gprs_zdy
        markerOptions.visible(true);
        markerOptions.zIndex(-1);//Marker就会显示在小蓝点下面
        markerlast = aMap.addMarker(markerOptions);

    }


    public void getPermis() {
        new AndPermissionUtil().applyPermissonLocation(getContext(), new DialogTwoListener() {
            @Override
            public void confirm() {
//                if (getDiatance2(latt, lonn) > 1000) {//1公里内可以步行导航
//                    ActivityUtil.showToast(getContext(), getResources().getString(R.string.hint_walk));
//                } else {
//                    // showGuide(latt, lonn);
//                    showGuides();
//                }
                showGuides();
            }

            @Override
            public void cancel() {
                MLog.e("拒绝了定位权限");
                showMissingPermissionDialog();              //显示提示信息
            }
        });
        // PermissionUtil.checkPermission(LoginActivity.this);
    }


    private void showGuides() {
        final String[] stringItems = {getResources().getString(R.string.gaodemap),
                getResources().getString(R.string.baidumap)
        };
        final ActionSheetDialog dialog = new ActionSheetDialog(getContext(), stringItems, null);
        dialog.isTitleShow(false).cancelText(getResources().getString(R.string.cancel))
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    default:
                    case 0:
                        //高德地图
                        if (GaodeNaiUtils.isInstallPackage()) {
                            startGaode();
                        } else {
                            ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_installgao));
                        }
                        break;
                    case 1:
                        //百度地图
                        if (BaiduNaiUtils.isInstalled()) {
                            starBaidu();
                        } else {
                            ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_installbai));
                        }
                        break;
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void startGaode() {
//        GaodeNaiUtils.openGaoDeMap(getContext(), lonn, latt, "", "");
//        GaodeNaiUtils.openGaoDeMap(getContext(), latLng.longitude, latLng.latitude, "",lonn ,latt,s);
        GaodeNaiUtils.openGaoDeMap(getContext(), lonn, latt, "", s);
//        GaodeNaiUtils.openGaoDeMap(getContext(), , , s);
    }

    private void starBaidu() {
        BaiduNaiUtils.invokeNavi(getContext(), "gcj02", "国能汽车", String.valueOf(latt), String.valueOf(lonn), "driving");
    }
}

