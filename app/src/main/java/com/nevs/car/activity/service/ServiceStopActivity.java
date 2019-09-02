package com.nevs.car.activity.service;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.nevs.car.R;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.ServiceStopAdapter;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nevs.car.R.id.map;

public class ServiceStopActivity extends BaseActivity implements LocationSource, AMapLocationListener {

    @BindView(map)
    MapView mapView;
    @BindView(R.id.recycleview)
    RecyclerView mRecyclerView;
    @BindView(R.id.lin_do)
    LinearLayout linDo;//列表
    @BindView(R.id.poi_rel)
    RelativeLayout poiRel;//单个
    @BindView(R.id.poi_name)
    TextView poiName;
    @BindView(R.id.poi_distance)
    TextView poiDistance;
    @BindView(R.id.poi_loation)
    TextView poiLoation;
    @BindView(R.id.cityname)
    TextView cityname;
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
    private String city = "";//地图加载时显示的区域边界(值为districtId)
    private LatLng latLngLong = null;
    Marker[] marker = new Marker[10];
    int totalMarker = 0;
    private LatLng latLng;
    private int mLoadCount = 0;
    private List<HashMap<String, Object>> listen = new ArrayList<>();
    private List<HashMap<String, Object>> listPoal = new ArrayList<>();
    String dis = null;
    private String name = null;
    private String text = null;
    private double lon;
    private double lat;
    private CameraUpdate cameraUpdate;
    private BaseQuickAdapter myAdapter;
    private Marker markers = null;
    private String pid = "";
    private String cid = "";
    private List<Marker> markerstop = new ArrayList<>();
    List<Marker> list = new ArrayList<>();
    List<Marker> list2 = new ArrayList<>();
    private int i = 0;
    private Marker lastMark = null;
    private List<Marker> listchang = new ArrayList<>();
    private String stopname = null;
    private String orgcode = null;
    private String type = null;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_service_stop;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView, mContext);
        initView(savedInstanceState);
        // initList();
        initRecyclyView();
    }


    private void getService(String s) {

        /**
         * 参数名称	类型	是否必填	说明
         accessToken	字符串	是	访问令牌
         type	字符串	是	"说明获取的经销商服务站的类型：
         dealer：经销商
         service：服务站"
         pid	字符串	否	省id
         cid	字符串	是	市id//需要回传

         {"isSuccess":"Y","reason":"","data":[{"province_name":"北京市","city_name":"北京市","KeyID":null,"name":"前途经销商01","fullname":"前途经销商","ename":"","fullename":"","orgcode":"QIANTUDLR01","orgid":"1001041","address":"中航XXXX","latitude":"104.141896,35.25307","provinceid":"2025","cityid":"2282","serviceType":"","like_phone":"14707980903"}]}
         * */
        listPoal.clear();
        listen.clear();
        HttpRxUtils.getDealerList(ServiceStopActivity.this,
                new String[]{"accessToken", "type", "pid", "cid"},
                new Object[]{new SharedPHelper(ServiceStopActivity.this).get(Constant.ACCESSTOKEN, ""),
                        "service", "", s
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        listPoal.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (listPoal.size() != 0) {

                            try {

                                for (int j = 0; j < listPoal.size(); j++) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    String str0 = listPoal.get(j).get("latitude") + "";

                                    try {
                                        if (str0.equals("") || str0.equals(",")) {
                                            map.put("distants", "");
                                        } else {
                                            String[] strarray0 = str0.split("[,]");
                                            map.put("distants", getDiatance(Double.parseDouble(strarray0[1]), Double.parseDouble(strarray0[0])));
                                        }
                                    } catch (Exception e) {
                                        map.put("distants", "");
                                    }


                                    map.put("title", listPoal.get(j).get("name"));
                                    map.put("text", listPoal.get(j).get("address"));
                                    map.put("like_phone", listPoal.get(j).get("like_phone"));
                                    listen.add(map);
                                }
                                linDo.setVisibility(View.VISIBLE);
                                myAdapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                MLog.e("算距异常");
                                //  ActivityUtil.showToast(mContext,getResources().getString(R.string.toast_message_fail));
                            }

                            if (listPoal.size() != 0) {
                                for (int i = 0; i < listPoal.size(); i++) {
                                    String str = listPoal.get(i).get("latitude") + "";
                                    if (str.equals("")) {

                                    } else {
                                        String[] strarray = str.split("[,]");
                                        makepoint("", Double.parseDouble(strarray[1]), Double.parseDouble(strarray[0]), listPoal.get(i).get("name").toString(), "", listPoal.get(i).get("address").toString());
                                    }

                                }
                            }

                        } else {
                            linDo.setVisibility(View.GONE);
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.unstop));
                        }


                    }

                    @Override
                    public void onFial(String str) {
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(mContext);
                                break;
                            default:
                                ActivityUtil.showToast(mContext, str);
                        }
                    }
                }
        );
    }


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

    private void initList() {
        HashMap<String, Object> h = new HashMap<>();
        h.put("a", "a");
        listen.add(h);
        HashMap<String, Object> h1 = new HashMap<>();
        h.put("a", "a");
        listen.add(h1);
        HashMap<String, Object> h2 = new HashMap<>();
        h.put("a", "a");
        listen.add(h2);
        HashMap<String, Object> h3 = new HashMap<>();
        h.put("a", "a");
        listen.add(h3);
        HashMap<String, Object> h4 = new HashMap<>();
        h.put("a", "a");
        listen.add(h4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.start_local, R.id.cityname, R.id.poi_close, R.id.btn_guides, R.id.poi_conllect})
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
            case R.id.cityname:
                startActivityForResult(new Intent(this, SelectStopActivity.class), 803);
                break;
            case R.id.poi_close:
                linDo.setVisibility(View.VISIBLE);
                poiRel.setVisibility(View.GONE);
                break;
            case R.id.btn_guides:
                showGuides();
                break;
            case R.id.poi_conllect:
                //  getTsp3(); 取消了收藏，直接跳到预约界面
                Intent reReturnIntent = new Intent(ServiceStopActivity.this, ServiceOrderActivity.class);
                reReturnIntent.putExtra("stopname", stopname);
                reReturnIntent.putExtra("orgcode", orgcode);
                reReturnIntent.putExtra("type", type);
                startActivity(reReturnIntent);
                break;
        }

    }

    private void showGuides() {
        final String[] stringItems = {getResources().getString(R.string.gaodemap),
                getResources().getString(R.string.baidumap)
        };
        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
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
                            ActivityUtil.showToast(ServiceStopActivity.this, getResources().getString(R.string.toast_installgao));
                        }
                        break;
                    case 1:
                        //百度地图
                        if (BaiduNaiUtils.isInstalled()) {
                            starBaidu();
                        } else {
                            ActivityUtil.showToast(ServiceStopActivity.this, "请先安装百度地图客户端");
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

    private void moveTo() {
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
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
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
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
                    //  ActivityUtil.showToast(getContext(), buffer.toString());
                    city = aMapLocation.getCity();
                    MLog.e("当前城市：" + city);
                    if (!city.equals("")) {
                        isFirstLoc = false;
                        cityname.setText(city);
                        getService(city);
                    }
                }

                latLng = new LatLng(aMapLocation.getLatitude(),
                        aMapLocation.getLongitude());//取出经纬度


            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("TAG", "location Error, ErrCode:"
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

                try {
                    String str1 = listPoal.get(position).get("latitude") + "";
                    String[] strarray1 = str1.split("[,]");
                    stopname = listPoal.get(position).get("name") + "";
                    orgcode = listPoal.get(position).get("orgcode") + "";
                    type = listPoal.get(position).get("type") + "";
                    //改变可视区域为指定位置
                    //CameraPosition4个参数分别为位置，缩放级别，目标可视区域倾斜度，可视区域指向方向（正北逆时针算起，0-360）
                    cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(Double.parseDouble(strarray1[1]), Double.parseDouble(strarray1[0])), 13, 0, 30));
                    aMap.moveCamera(cameraUpdate);//地图移向指定区域

                    //将地图移动到显示MARK位置
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(Double.parseDouble(strarray1[1]), Double.parseDouble(strarray1[0])
                    )));

                    name = listen.get(position).get("title") + "";
                    text = listen.get(position).get("text") + "";
                    dis = listen.get(position).get("distants") + "";
                    lon = Double.parseDouble(strarray1[0]);
                    lat = Double.parseDouble(strarray1[1]);

                    linDo.setVisibility(View.GONE);
                    poiRel.setVisibility(View.VISIBLE);
                    initBottomState();

                    makepointRelpace("", lat, lon, name, dis, text);
                } catch (Exception e) {
                    MLog.e("异常");
                }


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

    private void initOnItemChildClickListener() {
        myAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                MLog.e("点击了子:" + position);
                DialogUtils.call(ServiceStopActivity.this, false, listPoal.get(position).get("like_phone") + "");
            }
        });

    }

    private void initRecyclyView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));  //设置RecyclerView的显示模式
        myAdapter = new ServiceStopAdapter(R.layout.item_service_stop, listen); //设置适配器
        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
        initOnclickListener();
        initOnItemChildClickListener();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 803 && resultCode == 200) {
            String province = data.getStringExtra(SelectCityActivity.REGION_PROVINCE);
            String city = data.getStringExtra(SelectCityActivity.REGION_CITY);
            String area = data.getStringExtra(SelectCityActivity.REGION_AREA);

            String s = data.getStringExtra("CITYCODE");
            cityname.setText(city);

            for (int i = 0; i < markerstop.size(); i++) {
                if (markerstop.size() != 0) {
                    markerstop.get(i).remove();
                }
            }
            getService(s);
        }
    }


    ////
    //根据地址绘制需要显示的点
    public void makepoint(String s, final double lat, final double lon, final String title, String distants, String text) {

//        if(markers!=null){
//            markers.remove();
//        }

        Log.e("tag", "开始绘图");
        //北纬39.22，东经116.39，为负则表示相反方向
        LatLng latLngg = new LatLng(lat, lon);
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
        CameraUpdate cu = CameraUpdateFactory.newLatLng(ll);
        aMap.animateCamera(cu);
        markers = aMap.addMarker(markerOptions);

        markerstop.add(markers);


        // Marker markerchnanges=aMap.addMarker(markerOptions);

        listchang.add(markers);


        //改变可视区域为指定位置  MACK点击移到中间位置
        //CameraPosition4个参数分别为位置，缩放级别，目标可视区域倾斜度，可视区域指向方向（正北逆时针算起，0-360）
        cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLngg, 11, 0, 30));
        aMap.moveCamera(cameraUpdate);//地图移向指定区域

        //将地图移动到显示车辆位置
        // aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lat, lon)));


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

                if (marker.getPosition().longitude == latLng.longitude) {
                    MLog.e("判断MARK的点击事件");
                } else {

                    if (!marker.isInfoWindowShown()) {
                        marker.showInfoWindow();
                        linDo.setVisibility(View.GONE);
                        poiRel.setVisibility(View.VISIBLE);
                        getMark(marker);
                        initBottomState();

                        //   marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.dh_more));

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


                    } else {
                        marker.hideInfoWindow();

                    }

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

        for(int i=0;i<listPoal.size();i++){
            if(name.equals(listPoal.get(i).get("name"))){
                stopname = listPoal.get(i).get("name") + "";
                orgcode = listPoal.get(i).get("orgcode") + "";
                type = listPoal.get(i).get("type") + "";
                return;
            }
        }

    }

    private void initBottomState() {
        poiName.setText(name);
        poiLoation.setText(text);
        poiDistance.setText(dis);
    }

    public String getDiatance(double lat, double lon) {//计算距离
        String str = "";
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

    ////
    private void getTsp3() {
        float d = (float) 116.382248;
        MLog.e("float" + d);
        DialogUtils.loading(ServiceStopActivity.this, true);
        TspRxUtils.getSavePoi(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(ServiceStopActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"poiId", "poiName", "longitude", "latitude", "address"},
                new Object[]{new SharedPHelper(ServiceStopActivity.this).get(Constant.LOGINNAME, "") + String.valueOf(lat) + String.valueOf(lon),
                        name,
                        lon,
                        lat,
                        text},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(ServiceStopActivity.this);
                        ActivityUtil.showToast(ServiceStopActivity.this, getResources().getString(R.string.toast_collectsuc));
                        MyUtils.upLogTSO(mContext, "保存POI", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(ServiceStopActivity.this);
                        if (str.contains("400")) {
                            ActivityUtil.showToast(ServiceStopActivity.this, getResources().getString(R.string.toast_exists));
                        } else {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_collectfail));
                        }
                        MyUtils.upLogTSO(mContext, "保存POI", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

    }
}
