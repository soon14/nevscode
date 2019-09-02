package com.nevs.car.activity.gmap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.nevs.car.R;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.SerchGuideAdapter;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.AddressBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.BaiduNaiUtils;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.GaodeNaiUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nevs.car.R.id.poi_rel;
import static com.tamic.novate.config.ConfigLoader.getContext;

public class SerchGuideActivity extends BaseActivity implements LocationSource, AMapLocationListener,
        PoiSearch.OnPoiSearchListener {
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.recycleview)
    RecyclerView mRecyclerView;
    @BindView(R.id.lin_do)
    LinearLayout linDo;//列表
    @BindView(poi_rel)
    RelativeLayout poiRel;//单个
    @BindView(R.id.poi_name)
    TextView poiName;
    @BindView(R.id.poi_distance)
    TextView poiDistance;
    @BindView(R.id.poi_loation)
    TextView poiLoation;
    @BindView(R.id.poi_close)
    ImageView poiClose;
    @BindView(R.id.poi_conllect)
    TextView poiConllect;
    @BindView(R.id.btn_guides)
    TextView btnGuides;
    @BindView(R.id.tv_title)
    TextView tvTitle;
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
    private LatLng latLng;//当前人的经纬度
    private BaseQuickAdapter myAdapter;
    private int mLoadCount = 0;
    private List<HashMap<String, Object>> listen = new ArrayList<>();
    private List<PoiItem> arrayList = new ArrayList<>();
    ;
    private String intentLon = null;
    private String intentLat = null;
    private String name = null;
    private String text = null;
    private String dis = null;//距离
    private double mCurrentLat;
    private double mCurrentLng;
    Map<String, String> currentInfo = new HashMap<>();
    int selectIndex = -1;
    //poiSearch相关
    private PoiSearch poiSearch;
    private PoiSearch.Query query;
    boolean isPoiSearched = false; //是否进行poi搜索
    private List<AddressBean> data = new ArrayList<>();
    private List<HashMap<String, Object>> dataRecycle = new ArrayList<>();
    private boolean isF = true;
    private CameraUpdate cameraUpdate;
    private Marker markers = null;
    private String key = null;
    private MyHandler myHandler = new MyHandler();
    private double lon;
    private double lat;
    private Marker markergone;
    private boolean isOne = false;
    private String address = null;
    private String poiNames = null;
    private String latitude = null;
    private String longitude = null;

    List<Marker> list = new ArrayList<>();
    List<Marker> list2 = new ArrayList<>();
    private int i = 0;
    private Marker lastMark = null;
    private List<Marker> listchang = new ArrayList<>();

    @Override
    public int getContentViewResId() {
        return R.layout.activity_serch_guide;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initPoi();

        initView(savedInstanceState);
        //initGeocodeSearch();

//        initList();
        initRecyclyView();
        initOnclickListener();


    }


    private void initBottomState() {
        poiName.setText(name);
        poiLoation.setText(text);
        poiDistance.setText(dis);
    }

    private void initGeocodeSearch() {

        //构造 GeocodeSearch 对象，并设置监听
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        // geocoderSearch.setOnGeocodeSearchListener(this);
        //通过 GeocodeQuery(java.lang.String locationName, java.lang.String city) 设置查询参数，调用 GeocodeSearch 的 getFromLocationNameAsyn(GeocodeQuery geocodeQuery) 方法发起请求
        GeocodeQuery query = new GeocodeQuery("地址", "武汉");
        //发起请求
        geocoderSearch.getFromLocationNameAsyn(query);
    }

    private void initPoi() {
//
//        arrayList=new ArrayList<>();
//        PoiItem item =intentPoi;
//        //在地图上添加一个marker，并将地图中移动至此处
//        MarkerOptions mk = new MarkerOptions();
//        mk.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                .decodeResource(getResources(), R.mipmap.dh_car)));
//        mk.title(item.getAdName());
//        mk.visible(true);
//        mk.zIndex(-1);//Marker就会显示在小蓝点下面
//        LatLng ll = new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());
//        mk.position(ll);
//        //清除所有marker等，保留自身
//        aMap.clear(true);
//        CameraUpdate cu = CameraUpdateFactory.newLatLng(ll);
//        aMap.animateCamera(cu);
//        aMap.addMarker(mk);

//        intentLon = getIntent().getStringExtra("poilon");
//        intentLat = getIntent().getStringExtra("poilat");
//        name = getIntent().getStringExtra("name");
//        text = getIntent().getStringExtra("text");
//        data = (List<AddressBean>) getIntent().getSerializableExtra("AddressBean");
//        MLog.e("当前目的地经纬度：" + intentLon + "   " + intentLat);
//
//        if(intentLon!=null&&intentLat!=null){
//            getAddress(Double.parseDouble(intentLat), Double.parseDouble(intentLon));
//        }

        key = getIntent().getStringExtra("kuanjiazi");
        MLog.e("kuanjiazi：" + key);
        if (key != null) {
            tvTitle.setText(getIntent().getStringExtra("kuanjiazi"));
        }
        address = getIntent().getStringExtra("address");
        poiNames = getIntent().getStringExtra("poiName");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        MLog.e("address：" + address);
        MLog.e("latitude：" + latitude);
        MLog.e("longitude：" + longitude);
        if (address != null) {
            isOne = true;
            tvTitle.setText(poiNames);
            poiClose.setVisibility(View.GONE);
            // poiConllect.setVisibility(View.GONE);
            //btnGuides.setVisibility(View.GONE);
            // btnGuidess.setVisibility(View.VISIBLE);
            poiConllect.setEnabled(false);
            poiConllect.setBackgroundResource(R.drawable.btn_press_login_gray);

        }
    }

    @OnClick({R.id.back, R.id.start_local, R.id.poi_conllect, R.id.poi_close, R.id.btn_guides, R.id.tv_title})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.start_local:
                moveTo();//定位到人
                break;
            case R.id.poi_close:
                linDo.setVisibility(View.VISIBLE);
                poiRel.setVisibility(View.GONE);
                break;
            case R.id.poi_conllect:
                getTsp3();
                break;
            case R.id.btn_guides:
                showGuides();
                break;
//            case R.id.btn_guidess:
//                showGuides();
//                break;
            case R.id.tv_title:
                //  aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),15));
                break;
        }

    }

    private void initList() {
        HashMap<String, Object> h = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            h.put("name", data.get(i).getTitle());
            h.put("discript", data.get(i).getText());
            listen.add(h);
        }
        if (list.size() > 0) {
            poiRel.setVisibility(View.VISIBLE);
        }

    }

    private void moveTo() {
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
    }

    private void initView(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);//创建地图
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        if (aMap == null) {
            aMap = mapView.getMap();//初始化地图控制器对象

            setUpMap();//自定义定位蓝点

            //设置显示定位按钮 并且可以点击
            UiSettings settings = aMap.getUiSettings();
            aMap.setLocationSource(this);//设置了定位的监听设置定位资源。如果不设置此定位资源则定位按钮不可点击。并且实现activate激活定位,停止定位的回调方法
            // 是否显示定位按钮  cc
            settings.setMyLocationButtonEnabled(false);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
            //去掉右下角隐藏的缩放按钮
            aMap.getUiSettings().setZoomControlsEnabled(false);

        }


        //开始定位
        location();


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
                    MLog.e("定位成功");
//                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
//                    //将地图移动到定位点
//                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
//                    if (intentLat != null && intentLon != null) {
//                        //将地图移动到显示车辆位置
//                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(Double.parseDouble(intentLat), Double.parseDouble(intentLon))));
//                        MLog.e("intentLat");
//                    }
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
                    // ActivityUtil.showToast(getContext(), buffer.toString());

                    isFirstLoc = false;
                    latLng = new LatLng(aMapLocation.getLatitude(),
                            aMapLocation.getLongitude());//取出经纬度

                    if (isOne) {
                        MLog.e("sstrue");
                        getMarks();
                        makepointtwo("", Double.parseDouble(latitude), Double.parseDouble(longitude), poiNames, "", address);
                        poiRel.setVisibility(View.VISIBLE);
                        initBottomState();
                    } else {
                        if (key != null) {
                            MLog.e("s1");
                            searchPoi(key, 0, aMapLocation.getCityCode(), false, aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        }
                    }


                }


//                //根据经纬度算距离
//                if(isF){
//                    try {
//                        dis=getDiatance(Double.parseDouble(intentLat),Double.parseDouble(intentLon));
//                        MLog.e("距离："+dis);
//                        //initBottomState();//更新UI
//
//                        for(int i=0;i<data.size();i++){
//                            HashMap<String,Object> map=new HashMap<>();
//                            map.put("distants",getDiatance(data.get(i).getLatitude(),data.get(i).getLongitude()));
//                            MLog.e(i +getDiatance(data.get(i).getLatitude(),data.get(i).getLongitude()));
//                            map.put("title",data.get(i).getTitle());
//                            map.put("text",data.get(i).getText());
//                            dataRecycle.add(map);
//                        }
//                        isF=false;
//
//                    }catch (Exception e){
//
//                    }
//
//                }

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
                Log.e("AmapError", "location Error, ErrCode:"
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

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//               getAddress(data.get(position).getLatitude(),data.get(position).getLongitude());
//                name=data.get(position).getTitle();
//                text=data.get(position).getText();
//                dis=dataRecycle.get(position).get("distants").toString();
//                intentLat= String.valueOf(data.get(position).getLatitude());
//                        intentLon= String.valueOf(data.get(position).getLongitude());


                //改变可视区域为指定位置
                //CameraPosition4个参数分别为位置，缩放级别，目标可视区域倾斜度，可视区域指向方向（正北逆时针算起，0-360）
                cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(arrayList.get(position).getLatLonPoint().getLatitude(),
                        arrayList.get(position).getLatLonPoint().getLongitude()), 17, 0, 30));
                aMap.moveCamera(cameraUpdate);//地图移向指定区域

                //将地图移动到显示MARK位置
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(arrayList.get(position).getLatLonPoint().getLatitude(),
                        arrayList.get(position).getLatLonPoint().getLongitude()
                )));

                name = dataRecycle.get(position).get("title").toString();
                text = dataRecycle.get(position).get("text").toString();
                dis = dataRecycle.get(position).get("distants").toString();
                lon = arrayList.get(position).getLatLonPoint().getLongitude();
                lat = arrayList.get(position).getLatLonPoint().getLatitude();

                linDo.setVisibility(View.GONE);
                poiRel.setVisibility(View.VISIBLE);
                initBottomState();

                makepointRelpace("", lat, lon, name, dis, text);
            }
        });
        //条目长按点击事件
        myAdapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                // MyToast.showToast(NewsActivity.this, "长按点击了" + position);
                return true;
            }
        });
    }

    //根据地址绘制需要显示的点
    public void makepointRelpace(String s, final double lat, final double lon, final String title, String distants, String text) {

        for (int i = 0; i < listchang.size(); i++) {
            if (listchang.get(i).getTitle().equals(title)) {

                if (lastMark != listchang.get(i) && lastMark != null) {
                    if (lastMark.getTitle().equals(listchang.get(i).getTitle())) {
                        MLog.e("相等");
                    } else {
                        changeMark(listchang.get(i));
                        MLog.e("不相等");
                    }
                } else if (lastMark == null) {
                    MLog.e("lastMark==null");
                    changeMark(listchang.get(i));
                }

                lastMark = listchang.get(i);

            }
        }

////        //改变可视区域为指定位置  MACK点击移到中间位置
//        //CameraPosition4个参数分别为位置，缩放级别，目标可视区域倾斜度，可视区域指向方向（正北逆时针算起，0-360）
//        cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLngg, 17, 0, 30));
//        aMap.moveCamera(cameraUpdate);//地图移向指定区域


    }

    private void initRecyclyView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));  //设置RecyclerView的显示模式
        myAdapter = new SerchGuideAdapter(R.layout.item_serch_guide, dataRecycle); //设置适配器
        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;

    }

    @Override//pOI收缩结果2
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

//    @Override//根据地名获取经纬度1  ,GeocodeSearch.OnGeocodeSearchListener
//    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
//
//    }
//
//    @Override//根据地名获取经纬度2  通过GeocodeResult获取到我们需要的经纬度
//    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
////判断请求是否成功(1000为成功，其他为失败)
//        if (i == 1000) {
//            if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
//                    && geocodeResult.getGeocodeAddressList().size() > 0) {
//                GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
//                Log.e("tag", "经纬度值:" + address.getLatLonPoint() + "位置描述:"
//                        + address.getFormatAddress());
//               getAddress(address.getLatLonPoint().getLatitude(),address.getLatLonPoint().getLongitude());
//            }
//        }else{
//        MLog.e("获取目的地经纬度失败");
//        }
//    }

    /**
     * 简单的自定义MARK图标
     */
    public MarkerOptions myMark(float dis, LatLng latLng) {
        Resources res = this.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.mipmap.dh_mbwz01);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("")
                .snippet("直线距离：" + dis + "m")
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        return markerOptions;
    }

    private void showPoi(final LatLng latLng) {
        //aMap.clear(true);
        LatLng mylatlng;
        mylatlng = new LatLng(aMap.getMyLocation().getLatitude(), aMap.getMyLocation().getLongitude());
        float dis = AMapUtils.calculateLineDistance(mylatlng, latLng);
        marker[totalMarker] = aMap.addMarker(myMark(dis, latLng));
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
                String s = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                Log.e("tag", "获得请求结果");
                makepoint(s, lat, lon);
                linDo.setVisibility(View.VISIBLE);
                myAdapter.notifyDataSetChanged();
            }

            //根据地址获取坐标信息是调用
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
            }
        });

        geocodeSearch.getFromLocationAsyn(regeocodeQuery);//发起异步查询请求
    }

    //    //删除指定Marker
//    private void clearMarkers() {
//        //获取地图上所有Marker
//        List<Marker> mapScreenMarkers = aMap.getMapScreenMarkers();
//        for (int i = 0; i < mapScreenMarkers.size(); i++) {
//            Marker marker = mapScreenMarkers.get(i);
//            if (marker.getObject() instanceof) {
//                marker.remove();//移除当前Marker
//            }
//        }
//    }
    //根据地址绘制需要显示的点
    public void makepoint(String s, final double lat, final double lon) {
        //aMap.clear(true);
        if (markers != null) {
            markers.remove();
        }
        Log.e("tag", "开始绘图");
        //北纬39.22，东经116.39，为负则表示相反方向
        LatLng latLngs = new LatLng(lat, lon);
        Log.e("tag", "地址:" + s);

        //使用默认点标记
        //Marker maker=aMap.addMarker(new MarkerOptions().position(latLng).title("地点:").snippet(s));


        //自定义点标记
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lon));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.dh_mbwz)));//设置图标csxt1
        markerOptions.visible(true);
        markerOptions.zIndex(-1);//Marker就会显示在小蓝点下面
        markers = aMap.addMarker(markerOptions);


//        //改变可视区域为指定位置
//        //CameraPosition4个参数分别为位置，缩放级别，目标可视区域倾斜度，可视区域指向方向（正北逆时针算起，0-360）
//        cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLngs, 17, 0, 30));
//        aMap.moveCamera(cameraUpdate);//地图移向指定区域
//
//        //将地图移动到显示MARK位置
//        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLngs));

        //位置坐标的点击事件
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Toast.makeText(MainActivity.this,"点击指定位置",Toast.LENGTH_SHORT).show();
                //  return false;

//                if (!marker.isInfoWindowShown()) {
//                    marker.showInfoWindow();
//                    linDo.setVisibility(View.GONE);
//                    poiRel.setVisibility(View.VISIBLE);
//                    dis=getDiatance(marker.getPosition().latitude,marker.getPosition().longitude);
//                    initBottomState();
//                } else {
//                    marker.hideInfoWindow();
//               }
                linDo.setVisibility(View.GONE);
                poiRel.setVisibility(View.VISIBLE);
                dis = getDiatance(marker.getPosition().latitude, marker.getPosition().longitude);
                initBottomState();

                // marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.dh_more));
                return false;

            }
        });
        //位置上面信息窗口的点击事件
        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //Toast.makeText(getContext(),"点击了我的地点",Toast.LENGTH_SHORT).show();
                // showGuide(lat, lon);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.dh_man));// 设置小蓝点的图标

        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
    }

    private void showGuides() {
        final String[] stringItems = {getResources().getString(R.string.gaodemap),
                getResources().getString(R.string.baidumap)
        };
        // getResources().getString(R.string.baidumap)
        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.isTitleShow(false)
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
                            ActivityUtil.showToast(SerchGuideActivity.this, getResources().getString(R.string.toast_installgao));
                        }
                        break;
                    case 1:
                        //百度地图
                        if (BaiduNaiUtils.isInstalled()) {
                            starBaidu();
                        } else {
                            ActivityUtil.showToast(SerchGuideActivity.this, "请先安装百度地图客户端");
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
        GaodeNaiUtils.openGaoDeMap(this, lon, lat, "", "");
    }

    private void starBaidu() {
        BaiduNaiUtils.invokeNavi(this, "gcj02", "国能汽车", String.valueOf(lat), String.valueOf(lon), "driving");
    }

    //    public com.baidu.mapapi.model.LatLng getDB09(){
//        LatLng sourceLatLng=new LatLng(Double.parseDouble(intentLat),Double.parseDouble(intentLat));
//        //LatLng
//        // 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
//        CoordinateConverter converter  = new CoordinateConverter();
//        converter.from(CoordinateConverter.CoordType.COMMON);
//// sourceLatLng待转换坐标
//        converter.coord(sourceLatLng);
//        com.baidu.mapapi.model.LatLng desLatLngone = converter.convert();
//        return desLatLngone;
//
////// 将GPS设备采集的原始GPS坐标转换成百度坐标
////        CoordinateConverter converter2  = new CoordinateConverter();
////        converter2.from(CoordinateConverter.CoordType.GPS);
////// sourceLatLng待转换坐标
////        converter.coord(sourceLatLng);
////        com.baidu.mapapi.model.LatLng desLatLngtwo = converter2.convert();
//    }
    public String getDiatance(double lat, double lon) {//计算距离
        String str = null;
        try {
            dis = String.valueOf(AMapUtils.calculateLineDistance(latLng, new LatLng(lat, lon)));
            MLog.e("距离：M" + dis);
            if (Double.parseDouble(dis) >= 1000) {
                String diss = String.valueOf(Double.parseDouble(dis) / 1000);
                MLog.e("距离：KM" + diss);
                DecimalFormat myformat = new DecimalFormat("0.0");
                str = myformat.format(Double.parseDouble(diss)) + "km";
            } else {
                str = (int) Double.parseDouble(dis) + "m";
            }


        } catch (Exception e) {

        }
        return str;
    }


    /**关键字收索*/
    /**
     * 搜索poi
     *
     * @param key      关键字
     * @param pageNum  页码
     * @param cityCode 城市代码，或者城市名称
     * @param nearby   是否搜索周边
     */
    void searchPoi(String key, int pageNum, String cityCode, boolean nearby, double lat, double lon) {
        MLog.e(key);
        isPoiSearched = true;
        query = new PoiSearch.Query(key, "", cityCode);
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，
        //POI搜索类型共分为以下20种：汽车服务|汽车销售|
        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
        //住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
        //金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(50);// 设置每页最多返回多少条poiitem
        query.setPageNum(pageNum);//设置查询页码
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        if (nearby) {//nearby表示是否是附近
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat,
                    lon), 20000, true));//设置周边搜索的中心点以及半径
        }
        poiSearch.searchPOIAsyn();// 异步搜索

    }

    @Override//pOI收缩结果1
    public void onPoiSearched(PoiResult poiResult, int i) {
        MLog.e("s5");
        int index = 0;
        //填充数据，并更新listview
        ArrayList<PoiItem> result = poiResult.getPois();
        MLog.e("s6:" + result.size());
        if (result.size() > 0) {
            arrayList.clear();
            selectIndex = -1;
            arrayList.addAll(result);

            try {

                for (int j = 0; j < arrayList.size(); j++) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("distants", getDiatance(arrayList.get(j).getLatLonPoint().getLatitude(), arrayList.get(j).getLatLonPoint().getLongitude()));
                    // MLog.e(i +getDiatance(data.get(i).getLatitude(),data.get(i).getLongitude()));
                    map.put("title", arrayList.get(j).getTitle());
                    map.put("text", arrayList.get(j).getSnippet());
                    dataRecycle.add(map);
                }
                MLog.e("s2");
                myHandler.sendEmptyMessage(0x001);
            } catch (Exception e) {
                MLog.e("算距异常");
            }


        }
        for (PoiItem item : poiResult.getPois()) {
            MLog.e(item.toString());
            MLog.e(item.getDirection());
            MLog.e(item.getAdName());
        }
    }


    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x001:
                    //加载listview中数据
                    linDo.setVisibility(View.VISIBLE);
                    myAdapter.notifyDataSetChanged();
                    // idfresh=true;
                    drawbleMarks();
                    // showPoint();
                    break;
                case 0x009:
                    MLog.e("0x009");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15));
                            // showPoint2();
                        }
                    }, 500);

                    break;
            }
        }
    }

    private void drawbleMarks() {
        if (arrayList.size() != 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                getAddresstwo(arrayList.get(i).getLatLonPoint().getLatitude(), arrayList.get(i).getLatLonPoint().getLongitude(),
                        dataRecycle.get(i).get("title").toString(), dataRecycle.get(i).get("distants").toString(), dataRecycle.get(i).get("text").toString()
                );
            }
        }

    }

    private void getAddresstwo(final double lat, final double lon, final String title, final String distants, final String text) {
        GeocodeSearch geocodeSearch = new GeocodeSearch(mContext);//地址查询器

        //设置查询参数,
        //三个参数依次为坐标，范围多少米，坐标系
        RegeocodeQuery regeocodeQuery = new RegeocodeQuery(new LatLonPoint(lat, lon), 200, GeocodeSearch.AMAP);

        //设置查询结果监听
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            //根据坐标获取地址信息调用
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                String s = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                Log.e("tag", "获得请求结果");
                makepointtwo(s, lat, lon, title, distants, text);
                MLog.e("s3");
            }

            //根据地址获取坐标信息是调用
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
            }
        });

        geocodeSearch.getFromLocationAsyn(regeocodeQuery);//发起异步查询请求
    }

    //根据地址绘制需要显示的点
    public void makepointtwo(String s, final double lat, final double lon, final String title, String distants, String text) {
        Log.e("tag", "开始绘图");
        //北纬39.22，东经116.39，为负则表示相反方向
        final LatLng latLngg = new LatLng(lat, lon);
        Log.e("tag", "地址:" + s);

        //使用默认点标记
        //Marker maker=aMap.addMarker(new MarkerOptions().position(latLng).title("地点:").snippet(s));


        //自定义点标记
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lon)).title(title).snippet(text);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.dh_mbwz01)));//设置图标csxt1
        markerOptions.visible(true);
        markerOptions.zIndex(-1);//Marker就会显示在小蓝点下面
        LatLng ll = new LatLng(lat, lon);
        markerOptions.position(ll);
//        CameraUpdate cu = CameraUpdateFactory.newLatLng(ll);
//       aMap.animateCamera(cu);
        Marker markerchnanges = aMap.addMarker(markerOptions);

        listchang.add(markerchnanges);


        if (isOne) {

            //showPoint2();
            MLog.e("yige");
            //改变可视区域为指定位置  MACK点击移到中间位置
            //CameraPosition4个参数分别为位置，缩放级别，目标可视区域倾斜度，可视区域指向方向（正北逆时针算起，0-360）
//                    cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lat,lon), 15, 0, 30));
//                    aMap.moveCamera(cameraUpdate);//地图移向指定区域
//                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lat,lon)));
            //   aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),15));
            myHandler.sendEmptyMessage(0x009);
        } else {
            showPoint();
        }


        MLog.e("s4");
        //将地图移动到显示车辆位置
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lat, lon)));


        //位置坐标的点击事件
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.setInfoWindowEnable(false);
                //Toast.makeText(MainActivity.this,"点击指定位置",Toast.LENGTH_SHORT).show();
                //  return false;
//                if (stateCar.getVisibility() == View.GONE) {
//                    stateCar.setVisibility(View.VISIBLE);
//                }
//                if (!marker.isInfoWindowShown()) {
//                    marker.showInfoWindow();
//                    linDo.setVisibility(View.GONE);
//                    poiRel.setVisibility(View.VISIBLE);
//                    getMark(marker);
//                    initBottomState();
//                } else {
//                    marker.hideInfoWindow();
//
//                }
//                markergone=marker;
//                markergone.hideInfoWindow();
//                if (aMap != null && markergone != null) {
//                    if (markergone.isInfoWindowShown()){
//                        markergone.hideInfoWindow();
//                    }else {
//                        markergone.hideInfoWindow();
//                    }
//                }

                if (marker.getPosition().longitude == latLng.longitude) {
                    MLog.e("判断MARK的点击事件");
                } else {

                    linDo.setVisibility(View.GONE);
                    poiRel.setVisibility(View.VISIBLE);
                    getMark(marker);
                    initBottomState();


                    if (lastMark != marker && lastMark != null) {
                        if (lastMark.getTitle().equals(marker.getTitle())) {
                            MLog.e("相等");
                        } else {
                            changeMark(marker);
                            MLog.e("不相等");
                        }
                    } else if (lastMark == null) {
                        MLog.e("lastMark==null");
                        changeMark(marker);
                    }

                    lastMark = marker;


                }
                return true;

            }
        });
        //位置上面信息窗口的点击事件
        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //Toast.makeText(getContext(),"点击了我的地点",Toast.LENGTH_SHORT).show();
                //  showGuide(lat, lon);
            }
        });
    }

    private void changeMark(Marker marker) {
        //                    lastLat=marker.getPosition().latitude;
//                    lastLon=marker.getPosition().longitude;
        list.add(i, marker);

        marker.setAlpha(0);
//marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//        .decodeResource(getResources(), R.mipmap.dh_mbwz)));
        Marker marker2 = aMap.addMarker(new MarkerOptions().position(new LatLng(marker.getPosition().latitude,
                marker.getPosition().longitude)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.dh_mbwz))).title(marker.getTitle()).snippet(marker.getSnippet()));
        list2.add(i, marker2);
        if (i != 0) {
//                        aMap.addMarker(new MarkerOptions().position(new LatLng(list.get(i-1).getPosition().latitude,
//                                list.get(i-1).getPosition().longitude)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                                .decodeResource(getResources(), R.mipmap.dh_p))));
//                        list.get(i-1).setAlpha(0);
            list.get(i - 1).setAlpha(1);
            list2.get(i - 1).remove();
        }
        i++;

    }

    private void getMarks() {
        name = poiNames;
        text = address;
        dis = getDiatance(Double.parseDouble(latitude), Double.parseDouble(longitude));
        lon = Double.parseDouble(longitude);
        lat = Double.parseDouble(latitude);
    }

    private void getMark(Marker marker) {
        MLog.e("1" + marker.getTitle());
        MLog.e("2" + marker.getObject());
        MLog.e("3" + marker.getSnippet());
        MLog.e("4" + marker.getPosition());
        name = marker.getTitle();
        text = marker.getSnippet();
        dis = getDiatance(marker.getPosition().latitude, marker.getPosition().longitude);
        lon = marker.getPosition().longitude;
        lat = marker.getPosition().latitude;

    }

    /**
     * 关键字收索
     */


    private void getTsp3() {
        float d = (float) 116.382248;
        MLog.e("float" + d);
        DialogUtils.loading(SerchGuideActivity.this, true);
        TspRxUtils.getSavePoi(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(SerchGuideActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"poiId", "poiName", "longitude", "latitude", "address"},
                new Object[]{new SharedPHelper(SerchGuideActivity.this).get(Constant.LOGINNAME, "") + String.valueOf(lat) + String.valueOf(lon),
                        name,
                        lon,
                        lat,
                        text},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(SerchGuideActivity.this);
                        ActivityUtil.showToast(SerchGuideActivity.this, getResources().getString(R.string.toast_collectsuc));
                        MyUtils.upLogTSO(mContext, "保存POI", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(SerchGuideActivity.this);
                        if (str.contains("400")) {
                            ActivityUtil.showToast(SerchGuideActivity.this, getResources().getString(R.string.toast_exists));
                        } else {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_collectfail));
                        }
                        MyUtils.upLogTSO(mContext, "保存POI", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

    }


    private void showPoint() {
        //自动缩放
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
        for (int i = 0; i < arrayList.size(); i++) {
            boundsBuilder.include(new LatLng(arrayList.get(i).getLatLonPoint().getLatitude(), arrayList.get(i).getLatLonPoint().getLongitude()));//把所有点都include进去（LatLng类型）
        }

        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 180));//第二个参数为四周留空宽度
    }

    private void showPoint2() {
        //自动缩放
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度

        boundsBuilder.include(new LatLng(lat, lon));//把所有点都include进去（LatLng类型）

        boundsBuilder.include(latLng);//把所有点都include进去（LatLng类型）
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 180));//第二个参数为四周留空宽度
    }
}
