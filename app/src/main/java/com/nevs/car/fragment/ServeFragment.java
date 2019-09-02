package com.nevs.car.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.bumptech.glide.Glide;
import com.nevs.car.R;
import com.nevs.car.activity.service.CarExamineActivity;
import com.nevs.car.activity.service.NewsActivity;
import com.nevs.car.activity.service.PersonAgentActivity;
import com.nevs.car.activity.service.ProxyMainActivity;
import com.nevs.car.activity.service.ServiceOrderActivity;
import com.nevs.car.activity.service.ServiceStopActivity;
import com.nevs.car.activity.service.UserStateActivity;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseFragment;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyToast;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.EasyBanner;
import com.nevs.car.tools.view.GlideImageLoader;
import com.nevs.car.z_start.WebActivity;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by mac on 2018/4/2.
 */

public class ServeFragment extends BaseFragment implements WeatherSearch.OnWeatherSearchListener
        , AMapLocationListener, OnBannerListener {
    @BindView(R.id.eb_banner)
    EasyBanner mBanner;
    @BindView(R.id.service_order)
    TextView serviceOrder;
    @BindView(R.id.person_agent)
    TextView personAgent;
    @BindView(R.id.user_state)
    TextView userstate;
    @BindView(R.id.news)
    TextView news;
    Unbinder unbinder1;
    @BindView(R.id.car_examine)
    TextView carExamine;
    @BindView(R.id.city_address)
    TextView cityAddress;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.state)
    TextView state;
    Unbinder unbinder;
    @BindView(R.id.big)
    TextView big;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.airs)
    TextView airs;
    @BindView(R.id.washs)
    TextView washs;
    @BindView(R.id.pem2)
    LinearLayout pem2;
    @BindView(R.id.pem1)
    LinearLayout pem1;
    @BindView(R.id.view_bar_lin)
    LinearLayout viewBarLin;
    private List<HashMap<String, Object>> listI = new ArrayList<>();//保存回调的LIST
    private List<String> imageList = new ArrayList<>();//保存图片的的LIST
    private List<String> contentList = new ArrayList<>();//保存标题的LIST
    private List<String> urlList = new ArrayList<>();//保存点击进个的的LIST
    private List<String> imageLists = new ArrayList<>();//
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private LocalWeatherLive weatherlive;
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private String city = null;
    private boolean istwo = true;
    private String is = "";//是否是车主

    public static ServeFragment newInstance() {
        Bundle args = new Bundle();
        ServeFragment fragment = new ServeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_serve;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(viewBarLin,getContext());
        //626   initVisi();
        is = new SharedPHelper(getContext()).get(Constant.TSPISCAROWER, "0").toString();
        getHttp();//轮播

        initLoacation();//定位

        MLog.e("service init");
    }

    private void initVisi() {
        String is = new SharedPHelper(getContext()).get(Constant.TSPISCAROWER, "0").toString();
        MLog.e("is" + is);
//        if (is.equals("YES")) {
//            serviceOrder.setVisibility(View.VISIBLE);
//            userstate.setVisibility(View.VISIBLE);
//        } else {
//            serviceOrder.setVisibility(View.GONE);
//            userstate.setVisibility(View.GONE);
//        }
        if (is.equals("YES")) {
            pem1.setVisibility(View.VISIBLE);
            pem2.setVisibility(View.GONE);
        } else {
            pem1.setVisibility(View.GONE);
            pem2.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.service_order, R.id.person_agent, R.id.user_state,
            R.id.car_examine, R.id.news, R.id.call, R.id.stop, R.id.more,
            R.id.person_agent2, R.id.news2, R.id.car_examine2, R.id.stop2,
            R.id.service_order3, R.id.user_state3, R.id.stop3
    })
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.service_order3://预约服务
                if (is.equals("YES")) {
                    startActivity(new Intent(getContext(), ServiceOrderActivity.class));
                } else {
                    if (MyUtils.getPermissions("5", getContext())) {
                        startActivity(new Intent(getContext(), ServiceOrderActivity.class));
                    } else {
                        ActivityUtil.showToast(getContext(), getResources().getString(R.string.hint_author_not));
                    }
                }
                break;
            case R.id.user_state3://用户手册
                startActivity(new Intent(getContext(), UserStateActivity.class));
                // startActivity(new Intent(getContext(), CarTypeActivity.class));
                break;
            case R.id.stop3:
                //ActivityUtil.showToast(getContext(),getResources().getString(R.string.empty));
                startActivity(new Intent(getContext(), ServiceStopActivity.class));
                break;


            case R.id.service_order://预约服务
                startActivity(new Intent(getContext(), ServiceOrderActivity.class));
                break;
            case R.id.person_agent://个人代理
                //  getHttp2();

                MyToast.showToast(getContext(), getResources().getString(R.string.notoline));
//                String isPa=new SharedPHelper(getContext()).get(Constant.LOGINISPA,"").toString();
//                if(isPa.equals("Yes")){//||isPa.equals("Und")
//                    startActivity(new Intent(getContext(), ProxyMainActivity.class));
//                }else {
//                    startActivity(new Intent(getContext(), PersonAgentActivity.class));
//                }

                break;
            case R.id.person_agent2://个人代理
                //tj   getHttp2();

                MyToast.showToast(getContext(), getResources().getString(R.string.notoline));
//                String isPa=new SharedPHelper(getContext()).get(Constant.LOGINISPA,"").toString();
//                if(isPa.equals("Yes")){//||isPa.equals("Und")
//                    startActivity(new Intent(getContext(), ProxyMainActivity.class));
//                }else {
//                    startActivity(new Intent(getContext(), PersonAgentActivity.class));
//                }

                break;
            case R.id.user_state://用户手册
                startActivity(new Intent(getContext(), UserStateActivity.class));
                // startActivity(new Intent(getContext(), CarTypeActivity.class));
                break;
            case R.id.news://新闻资讯
                startActivity(new Intent(getContext(), NewsActivity.class));
                break;
            case R.id.news2://新闻资讯
                startActivity(new Intent(getContext(), NewsActivity.class));
                break;
            case R.id.car_examine://车型品鉴
                startActivity(new Intent(getContext(), CarExamineActivity.class));
                break;
            case R.id.car_examine2://车型品鉴
                startActivity(new Intent(getContext(), CarExamineActivity.class));
                break;
            case R.id.call://拨打电话
                call();
                break;
            case R.id.stop:
                //ActivityUtil.showToast(getContext(),getResources().getString(R.string.empty));
                startActivity(new Intent(getContext(), ServiceStopActivity.class));
                break;
            case R.id.stop2:
                //ActivityUtil.showToast(getContext(),getResources().getString(R.string.empty));
                startActivity(new Intent(getContext(), ServiceStopActivity.class));
                break;
            case R.id.more:
                //ActivityUtil.showToast(getContext(),getResources().getString(R.string.empty));
                break;
        }
    }

    private void getHttp2() {
        DialogUtils.loading(getContext(), true);
        HttpRxUtils.getGetUserStatus(getContext(),
                new String[]{"accessToken"},
                new Object[]{
                        new SharedPHelper(getContext()).get(Constant.ACCESSTOKEN, "")
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding(getActivity());
                        String isPa = String.valueOf(obj);
                        new SharedPHelper(getContext()).put(Constant.LOGINISPA, isPa);
                        if (isPa.equals("Yes")) {//||isPa.equals("Und")
                            startActivity(new Intent(getContext(), ProxyMainActivity.class));
                        } else {
                            startActivity(new Intent(getContext(), PersonAgentActivity.class));
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(getActivity());
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(getContext());
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(getContext());
                                break;
                            default:
                                ActivityUtil.showToast(getContext(), str);
                        }
                    }
                }

        );
    }

    private void getHttp() {
        listI.clear();
        imageLists.clear();
        HttpRxUtils.getImageList(getContext(),
                new String[]{"accessToken"},
                new Object[]{new SharedPHelper(getContext()).get(Constant.ACCESSTOKEN, "")},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object list) {
                        listI.addAll((Collection<? extends HashMap<String, Object>>) list);
                        // listI.addAll((Collection<? extends HashMap<String, Object>>) list);
                        MLog.e("listI:" + listI.size());
//                        if (listI.size() == 0) {
//                            return;
//                        }
//                        getImageUrlData();
//                        getContentData();
//                        getUrlData();
//
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    internet();
//                                } catch (Exception e) {
//                                    MLog.e("snapp is null");
//                                }
//
//                            }
//                        }).start();
//
//                        if (urlList.size() > 1) {
//                            if (mBanner != null) {
//                                mBanner.start();
//                            }
//                        }

                        initImages();
                        initBanner();
                    }

                    @Override
                    public void onFial(String str) {
                        initBanner();
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(getContext());
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(getContext());
                                break;
                            default:
                                ActivityUtil.showToast(getContext(), str);
                        }
                    }
                }
        );
    }

    private void initImages() {
        for (int i = 0; i < listI.size(); i++) {
            imageLists.add(Constant.HTTP.BANNERURL + Constant.HTTP.NEWSCENTER + listI.get(i).get("imgPath").toString());
            MLog.e(Constant.HTTP.BANNERURL + Constant.HTTP.NEWSCENTER + listI.get(i).get("imgPath").toString());
        }
    }

    private void initBanner() {
        //设置banner样式
        //banner.setBannerStyle(BannerConfig.CENTER);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(imageLists);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
        // banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(2500);
        //设置指示器位置（当banner模式中有指示器时）
        // banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.setOnBannerListener(this);
        banner.start();
    }

    public void internet() {
        //设置图片url和图片标题
        mBanner.initBanner(imageList, contentList);
        //设置图片加载器
        mBanner.setImageLoader(new EasyBanner.ImageLoader() {
            @Override
            public void loadImage(final ImageView imageView, final String url) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getContext())
                                .load(url)
                                .placeholder(R.mipmap.fw_banner_img)//等待中
                                .error(R.mipmap.fw_banner_img)//加载失败
                                .into(imageView);
                    }
                });
            }
        });
        //监听banner的item点击事件
        mBanner.setOnItemClickListener(new EasyBanner.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String title) {
                try {
                    Intent i = new Intent(getContext(), WebActivity.class);
                    i.putExtra("URL", Constant.HTTP.BANNERURL + Constant.HTTP.NEWSCENTER + urlList.get(position));
                    // i.putExtra("TITLE","");
                    startActivity(i);
                    MLog.e("position:" + position);
                } catch (Exception e) {
                    MLog.e("图片加载中，不能点击");
                }

            }
        });
    }

    private void getImageUrlData() {
        for (int i = 0; i < listI.size(); i++) {
            imageList.add(Constant.HTTP.BANNERURL + Constant.HTTP.NEWSCENTER + listI.get(i).get("imgPath").toString());
            MLog.e(Constant.HTTP.BANNERURL + Constant.HTTP.NEWSCENTER + listI.get(i).get("imgPath").toString());
        }
    }

    private void getContentData() {
        for (int j = 0; j < listI.size(); j++) {
            contentList.add(listI.get(j).get("title").toString());
        }
    }

    private void getUrlData() {
        for (int k = 0; k < listI.size(); k++) {
            urlList.add(listI.get(k).get("link").toString());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (urlList.size() > 1) {
//            if (mBanner != null) {
//                mBanner.stop();
//            }
//        }
        //结束轮播
        banner.stopAutoPlay();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(urlList.size()>1) {
//            if (mBanner != null) {
//                mBanner.start();
//            }
//        }
        //开始轮播
        banner.startAutoPlay();


        MLog.e("service onResume");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        MLog.e("onHiddenChanged1service");
        if (hidden) {

        } else {
            MLog.e("onHiddenChanged2service");
            //开始轮播
            banner.startAutoPlay();

            //626  initVisi();
            is = new SharedPHelper(getContext()).get(Constant.TSPISCAROWER, "0").toString();
        }


    }

    public void call() {

//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("是否拨打电话")
//                .setMessage(Constant.PHONENUMBER)
//                .setPositiveButton(getResources().getString(R.string.call_enter), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constant.PHONENUMBER));
//                        startActivity(intent);
//                    }
//                })
//                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//                    }
//                }).show();
        DialogUtils.call(getContext(), false, "15888888888");
    }


    private void initLoacation() {
        mlocationClient = new AMapLocationClient(getContext());
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
        mlocationClient.startLocation();
    }

    private void initWeather() {//检索参数为城市和天气类型，实况天气为WEATHER_TYPE_LIVE、天气预报为WEATHER_TYPE_FORECAST
        mquery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        mweathersearch = new WeatherSearch(getContext());
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == 1000) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                weatherlive = weatherLiveResult.getLiveResult();
                // reporttime1.setText(weatherlive.getReportTime() + "发布");
                state.setText(weatherlive.getWeather());
                // Temperature.setText(weatherlive.getTemperature() + "°");
                number.setText(weatherlive.getTemperature());
                big.setText(weatherlive.getWindDirection() + "风 " + weatherlive.getWindPower() + getResources().getString(R.string.windss));
                cityAddress.setText(city);
                airs.setText(getResources().getString(R.string.nevs_airquality) + weatherlive.getHumidity());
                if (weatherlive.getWeather().contains("晴") || weatherlive.getWeather().contains("多云")) {
                    washs.setText(getResources().getString(R.string.washgood));
                } else {
                    washs.setText(getResources().getString(R.string.washbad));
                }
            } else {
                ActivityUtil.showToast(getContext(), R.string.no_result);
            }
        } else {
            ActivityUtil.showToast(getContext(), rCode);
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

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
                    if(!city.equals("")) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(amapLocation.getTime());
                        df.format(date);//定位时间
                        initWeather();//初始化天气
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void OnBannerClick(int position) {
        try {
            Intent i = new Intent(getContext(), WebActivity.class);
            if ((listI.get(position).get("link") + "").contains("http")) {
                i.putExtra("URL", listI.get(position).get("link") + "");
                i.putExtra("TITLE", getResources().getString(R.string.editenters));
            } else {
                i.putExtra("URL", Constant.HTTP.BANNERURL + Constant.HTTP.NEWSCENTER + listI.get(position).get("link"));
                i.putExtra("TITLE", getResources().getString(R.string.editenters));
            }

            // i.putExtra("TITLE","");
            startActivity(i);
            MLog.e("position:" + position);
        } catch (Exception e) {
            MLog.e("图片加载中，不能点击");
        }
    }
}
