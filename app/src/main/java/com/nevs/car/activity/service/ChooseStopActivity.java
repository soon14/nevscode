package com.nevs.car.activity.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.nevs.car.R;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.ChooseStopAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChooseStopActivity extends BaseActivity implements AMapLocationListener {

    @BindView(R.id.cityname)
    TextView cityname;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.xrefresh_view)
    XRefreshView xRefreshView;
    @BindView(R.id.public404)
    LinearLayout public404;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private BaseQuickAdapter myAdapter;
    private int mLoadCount = 0;
    private List<HashMap<String, Object>> listPoal = new ArrayList<>();
    private List<HashMap<String, Object>> listPoals = new ArrayList<>();
    private boolean istwo = true;
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private LatLng latLng;
    private String dis = "";
    private String city = "";

    @Override
    public int getContentViewResId() {
        return R.layout.activity_choose_stop;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initRecyclyView();
        initLoacation();//定位
        DialogUtils.loading(mContext, true);
    }

    private void initLoacation() {
        mlocationClient = new AMapLocationClient(mContext);
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
        mlocationClient.startLocation();
    }

    private void initRecyclyView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));  //设置RecyclerView的显示模式
        //设置刷新完成以后，headerview固定的时间
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setAutoLoadMore(false);
        // myAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        xRefreshView.enableReleaseToLoadMore(true);
        xRefreshView.enableRecyclerViewPullUp(true);
        xRefreshView.enablePullUpWhenLoadCompleted(true);
        //设置静默加载时提前加载的item个数
//        xRefreshView1.setPreLoadCount(4);

        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        xRefreshView.stopRefresh();
//                    }
//                }, 500);

                if (cityname.getText().toString().trim().length() != 0) {
                    getService(cityname.getText().toString());
                }

            }

            @Override
            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
//                        for (int i = 0; i < 6; i++) {
//                            recyclerviewAdapter.insert(new Person("More ", mLoadCount + "21"),
//                                    recyclerviewAdapter.getAdapterItemCount());
//                        }
                        mLoadCount++;
                        if (mLoadCount >= 3) {//模拟没有更多数据的情况
                            xRefreshView.setLoadComplete(true);
                        } else {
                            // 刷新完成必须调用此方法停止加载
                            xRefreshView.stopLoadMore(false);
                            //当数据加载失败 不需要隐藏footerview时，可以调用以下方法，传入false，不传默认为true
                            // 同时在Footerview的onStateFinish(boolean hideFooter)，可以在hideFooter为false时，显示数据加载失败的ui
//                            xRefreshView1.stopLoadMore(false);
                        }
                    }
                }, 1000);
            }
        });

        myAdapter = new ChooseStopAdapter(R.layout.item_choosestop, listPoals); //设置适配器
        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
        initOnclickListener();
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
        listPoals.clear();
       // DialogUtils.loading(this, true);
        HttpRxUtils.getDealerList(ChooseStopActivity.this,
                new String[]{"accessToken", "type", "pid", "cid"},
                new Object[]{new SharedPHelper(ChooseStopActivity.this).get(Constant.ACCESSTOKEN, ""),
                        "service", "", s
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(ChooseStopActivity.this);
                        public404.setVisibility(View.GONE);
                        xRefreshView.stopRefresh();
                        listPoal.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        MLog.e("listPoal长度：" + listPoal.size());
                        if (listPoal.size() == 0) {
                            // public404.setVisibility(View.VISIBLE);
                            ActivityUtil.showToast(ChooseStopActivity.this, getResources().getString(R.string.toast_unstop));
                            myAdapter.notifyDataSetChanged();
                        } else {
                            MLog.e("循环");
                            for (int i = 0; i < listPoal.size(); i++) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("name", listPoal.get(i).get("name") + "");
                                map.put("address", listPoal.get(i).get("address") + "");
                                map.put("type", listPoal.get(i).get("type") + "");
                                String latitude = listPoal.get(i).get("latitude") + "";
                                try {
                                    if (latitude.equals("")||latitude.equals(",")) {
                                        map.put("dis", "0");
                                    } else {
                                        String[] strarray = latitude.split("[,]");
                                        map.put("dis", getDiatance(Double.parseDouble(strarray[1]), Double.parseDouble(strarray[0])));
                                        MLog.e("str:" + getDiatance(Double.parseDouble(strarray[1]), Double.parseDouble(strarray[0])));
                                    }
                                }catch (Exception e){
                                    map.put("dis", "0");
                                }

                                listPoals.add((HashMap<String, Object>) map);
                            }

                            MLog.e("刷新适配器");
                            myAdapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onFial(String str) {
                        xRefreshView.stopRefresh();
                        DialogUtils.hidding(ChooseStopActivity.this);
                        //public404.setVisibility(View.VISIBLE);
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
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        }
                    }
                }
        );
    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                finishSelect(listPoal.get(position).get("name")+"", listPoal.get(position).get("orgcode")+"", listPoal.get(position).get("type") + "");
                if ((listPoal.get(position).get("type")+"").equals("30031005")) {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.hintyidong));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.cityname, R.id.refresh})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.cityname:
                startActivityForResult(new Intent(this, SelectStopActivity.class), 805);
                break;
            case R.id.refresh:
                getService(cityname.getText().toString());
                break;
        }
    }

    private void finishSelect(String s, String orgcode, String type) {
        //将计算的结果回传给第一个Activity
        Intent reReturnIntent = new Intent(this, ServiceOrderActivity.class);
        reReturnIntent.putExtra("stopname", s);
        reReturnIntent.putExtra("orgcode", orgcode);
        reReturnIntent.putExtra("type", type);
        setResult(905, reReturnIntent);
        //退出第二个Activity
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 805 && resultCode == 200) {
            String province = data.getStringExtra(SelectStopActivity.REGION_PROVINCE);
            String city = data.getStringExtra(SelectStopActivity.REGION_CITY);
            String area = data.getStringExtra(SelectProvinceActivity.REGION_AREA);

            String s = data.getStringExtra("CITYCODE");
            cityname.setText(city);
            getService(s);
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                if (istwo) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    city = amapLocation.getCity();
                    MLog.e("当前城市：" + city);

                    if(city==null||city.equals("")){

                    }else {
                        latLng = new LatLng(amapLocation.getLatitude(),
                                amapLocation.getLongitude());//取出经纬度

                        cityname.setText(city);
                        getService(city);

                        istwo = false;//定位成功以后不再定位
                    }


                }

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

//    public String getDiatance(double lat,double lon){//计算距离
//        String str=null;
//        try {
//            dis = String.valueOf(AMapUtils.calculateLineDistance(latLng,new LatLng(lat,lon)));
//            String diss=String.valueOf(Double.parseDouble(dis)/1000);
//            MLog.e("距离："+dis);
//            java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.0");
//            str = myformat.format(Double.parseDouble(diss));
//        }catch (Exception e){
//            MLog.e("算距异常");
//        }
//        return str;
//    }

    public String getDiatance(double lat, double lon) {//计算距离
        String str = "0";
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
}
