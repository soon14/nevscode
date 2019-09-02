package com.nevs.car.activity.my;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.interfaces.OnResponseListener;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.WXShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarCopyEnterActivity extends BaseActivity implements LocationSource, AMapLocationListener, AMap.OnMapClickListener {
    @BindView(R.id.mapViewC)
    MapView mapView;
    @BindView(R.id.text_title)
    EditText textTitle;
    @BindView(R.id.text_type)
    TextView textType;
    @BindView(R.id.text_where)
    EditText textWhere;
    @BindView(R.id.text_time)
    TextView textTime;
    @BindView(R.id.text_lands)
    TextView textLands;
    @BindView(R.id.text_charge)
    TextView textCharge;
    @BindView(R.id.text_start)
    TextView textStart;
    @BindView(R.id.text_end)
    TextView textEnd;
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
    private String addrThree = "";//地图加载时显示的区域边界(值为districtId)
    private LatLng latLngLong = null;
    Marker[] marker = new Marker[10];
    int totalMarker = 0;
    private String tripId = null;
    private String json = "";
    private String title = "";
    private String type = "";
    private String remark = "";
    private WXShare wxShare;
    private List<HashMap<String, Object>> listLines = new ArrayList<>();
    private Thread thread;
    @Override
    public int getContentViewResId() {
        return R.layout.activity_car_copy_enter;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initView(savedInstanceState);
        initIntent();

        getTsp25();

        initShare();
    }

    private void initIntent() {
        if (getIntent().getStringExtra("tripid") != null) {
            tripId = getIntent().getStringExtra("tripid");
            MLog.e("tripId" + tripId);
        }
    }

    private void initShare() {
        wxShare = new WXShare(this);
        wxShare.setListener(new OnResponseListener() {
            @Override
            public void onSuccess() {
                // 分享成功
                MLog.e("sd");
            }

            @Override
            public void onCancel() {
                // 分享取消
                MLog.e("sd");
            }

            @Override
            public void onFail(String message) {
                // 分享失败
                MLog.e("sd");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.setting_log, R.id.edit, R.id.text_title, R.id.re_type, R.id.text_where})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.setting_log:
                showDialog();
                break;
            case R.id.edit:
//                startActivityForResult(new Intent(this, LogEnterEditActivity.class)
//                        .putExtra("title",title)
//                        .putExtra("type",type)
//                        .putExtra("remark",remark)
//                        .putExtra("tripid",tripId),903);

                getTsp26();
                break;
            case R.id.text_title:
                textTitle.setCursorVisible(true);
                break;
            case R.id.re_type:
                showDialogs();
                break;
            case R.id.text_where:
                textWhere.setCursorVisible(true);
                break;
        }
    }

    private void showDialogs() {
        DialogUtils.showPoalsTwo(this, textType);
    }

    private void getTsp26() {
        /**
         *
         * */
        DialogUtils.loading(mContext, true);
        TspRxUtils.getSettag(mContext,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                new String[]{"tripId", "title", "category", "remark"},
                new Object[]{new String[]{tripId}, textTitle.getText().toString().trim(), textType.getText().toString(), textWhere.getText().toString().trim()},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.safesuc));

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_network));
                    }
                }
        );

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 903 && resultCode == 904) {
            title = data.getStringExtra("title");
            type = data.getStringExtra("type");
            remark = data.getStringExtra("text");
            textTitle.setText(data.getStringExtra("title"));
            textType.setText(data.getStringExtra("type"));
            textWhere.setText(data.getStringExtra("text"));

        }
    }

    private void showDialog() {//动画效果
        final String[] stringItems = {getResources().getString(R.string.share), getResources().getString(R.string.delete)};
        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.cancelText(getResources().getText(R.string.cancel).toString());
        dialog.isTitleShow(false)
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    default:
                    case 0:
                        shareTrip();
                        break;
                    case 1:
                        getTsp27();
                        break;
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void shareTrip() {
        if (tripId != null) {
            DialogUtils.shareDiolag(this, wxShare, Constant.HTTP.TRIPSHARE + tripId, title);
        }
    }

    public static void shareDiolag(Context context, final WXShare wxShare, final String url, final String title) {
        final Dialog dialog = new Dialog(context, R.style.Theme_Light_Dialog);
        final View dialogView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.share_pop_window, null);

        LinearLayout id_wxfrend = (LinearLayout) dialogView.findViewById(R.id.id_wxfrend);
        id_wxfrend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wxShare.shareu(SendMessageToWX.Req.WXSceneSession, url, title);

            }
        });

        LinearLayout id_pyquan = (LinearLayout) dialogView.findViewById(R.id.id_pyquan);
        id_pyquan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wxShare.shareu(SendMessageToWX.Req.WXSceneTimeline, url, title);

            }
        });
        //取消
        TextView shareback = (TextView) dialogView.findViewById(R.id.shareback);
        shareback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.popwin_anim_style);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.setContentView(dialogView);
        dialog.show();
    }


    private void getTsp25() {
        /**
         *
         * */
        DialogUtils.loading(mContext, false);
        TspRxUtils.getDetail(this,
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(CarCopyEnterActivity.this).get(Constant.ACCESSTOKENS, "")},
                tripId,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "获取行程记录详情", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        json = (String) obj;
                        upView();
                        try {
                            drawLines();
                        } catch (Exception e) {
                            MLog.e("崩溃");
                        }

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "获取行程记录详情", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

    }

    private void drawLines() {
        TspRxUtils.xJson(json, listLines);
        MLog.e("度度：" + listLines.get(0).get("longitude"));
        final List<LatLng> latLngs = new ArrayList<LatLng>();
        for (int i = 0; i < listLines.size(); i++) {
            latLngs.add(new LatLng(Double.parseDouble(listLines.get(i).get("latitude") + ""), Double.parseDouble(listLines.get(i).get("longitude") + "")));
        }
        //起点位置和  地图界面大小控制
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 11));
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
        getAddress(latLngs.get(0).longitude, latLngs.get(0).latitude);
        getAddress1(latLngs.get(latLngs.size() - 1).longitude, latLngs.get(latLngs.size() - 1).latitude);
//        thread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //绘制纹理
//                for (int i = 0; i < latLngs.size() - 1; ++i) {
//
//                    aMap.addPolyline(GetPolylineOptions().add(
//                            latLngs.get(i), latLngs.get(i + 1)
//                    ));
//
//                }
//            }
//        }).start();

        thread=new Thread(){
            @Override
            public void run() {
                super.run();
                //绘制纹理
                for (int i = 0; i < latLngs.size() - 1; ++i) {

                    aMap.addPolyline(GetPolylineOptions().add(
                            latLngs.get(i), latLngs.get(i + 1)
                    ));

                }
            }
        };

        thread.start();

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
        polylienOptions.setCustomTextureList(textureList);
        polylienOptions.setCustomTextureIndex(textureIndexs);
        polylienOptions.setUseTexture(true);
        polylienOptions.width(220.0f);
        return polylienOptions;
    }

    private void upView() {
        //(Long.parseLong(jsonObject.getString("endTime"))-Long.parseLong(jsonObject.getString("beginTime")))/3600+getResources().getString(R.string.toast_hours)
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            title = jsonObject.getString("title");
            type = jsonObject.getString("category");
            remark = jsonObject.getString("remark");

            if (jsonObject.getString("title") == null || jsonObject.getString("title").equals("null")) {
                textTitle.setText("");
            } else {
                textTitle.setText(jsonObject.getString("title"));
            }
            if (jsonObject.getString("remark") == null || jsonObject.getString("remark").equals("null")) {
                textWhere.setText("");
            } else {
                textWhere.setText(jsonObject.getString("remark"));
            }

            textType.setText(jsonObject.getString("category"));

            textTime.setText(getIntent().getStringExtra("totalDuration") + "min");
            textLands.setText(getIntent().getStringExtra("totalMileage") + "km");
            textCharge.setText(jsonObject.getString("battery") + "kW/h");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getTsp27() {
        /**
         *
         * */
        DialogUtils.loading(mContext, true);
        TspRxUtils.getDeletetrip(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(CarCopyEnterActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"tripId"},
                new Object[]{new String[]{tripId}},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "删除行程记录", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        finish();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "删除行程记录", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

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

        aMap.setOnMapClickListener(this);
//        aMap.setOnMapLongClickListener(this);
//        mapView.getMap().setLocationSource(this);
//        aMap.setOnMyLocationChangeListener(this);
        //开始定位
        location();

        // inits();

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
        wxShare.unregister();
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mapView != null) {
            mapView.onDestroy();
        }
        if(thread!=null){
            thread=null;
        }
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
        wxShare.register();
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
//                    //设置缩放级别
//                    aMap.moveCamera(CameraUpdateFactory.zoomTo(7));
//                    //将地图移动到定位点
//                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
//                    //点击定位按钮 能够将地图的中心移动到定位点
//                    mListener.onLocationChanged(aMapLocation);
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
                    //drawLines();
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


    @Override
    public void onMapClick(LatLng latLng) {
        Intent intent = new Intent(CarCopyEnterActivity.this, LookTripActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("listLines", (Serializable) listLines);
        intent.putExtras(bundle);
        startActivity(intent);

    }


    //根据地址绘制需要显示的点
    public void makepoint(LatLng latLng, int mipmap) {

        Log.e("tag", "开始绘图");
        //自定义点标记
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("");
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

    //坐标转地址
    public void getAddress(double longitude, double latitude) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {

            @Override
            public void onGeocodeSearched(GeocodeResult result, int rCode) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

                String formatAddress = result.getRegeocodeAddress().getFormatAddress();
                textStart.setText(formatAddress);
                MLog.e("formatAddress:" + formatAddress);
                MLog.e("rCode:" + rCode);

            }
        });
        LatLonPoint lp = new LatLonPoint(latitude, longitude);
        RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);

    }

    public void getAddress1(double longitude, double latitude) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {

            @Override
            public void onGeocodeSearched(GeocodeResult result, int rCode) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

                String formatAddress = result.getRegeocodeAddress().getFormatAddress();
                textEnd.setText(formatAddress);
                MLog.e("formatAddress1:" + formatAddress);
                MLog.e("rCode1:" + rCode);

            }
        });
        LatLonPoint lp = new LatLonPoint(latitude, longitude);
        RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);

    }
}
