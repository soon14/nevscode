package com.nevs.car.activity.gmap;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.MLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShearchPoiActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener{
    private MapView mMapView = null;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    //poiSearch相关
    private PoiSearch poiSearch;
    private PoiSearch.Query query;
    boolean isPoiSearched = false; //是否进行poi搜索
    //listview
    private ListView ll;
    ArrayList<PoiItem> arrayList;
    MyAdpter adapter;
    MyHandler myHandler;
    //字体
    Typeface tf;
    //搜索栏
    FrameLayout frameLayout;
    ImageView searchIv;
    EditText searchEt;
    TextView title;
    Button btn;
    ImageView success;
    boolean onSearch = false; //是否打开搜索栏
    ImageView back;
    private double mCurrentLat;
    private double mCurrentLng;
    Map<String, String> currentInfo = new HashMap<>();
    int selectIndex = -1;
    ImageView currentSelectItem = null;
    @Override
    public int getContentViewResId() {
        return R.layout.activity_shearch_poi;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        findAllView();
        setAllViewOnclickLinster();
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        initAMap();
    }

    /**
     * 获取view对象，初始化一些对象
     */
    void findAllView() {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        frameLayout = (FrameLayout) findViewById(R.id.searchLayout);
        searchEt = (EditText) findViewById(R.id.search_input);
        searchIv = (ImageView) findViewById(R.id.search);
        btn = (Button) findViewById(R.id.search_go_btn);
        success = (ImageView) findViewById(R.id.success);
        back = (ImageView) findViewById(R.id.back);
        //初始化listview
        ll = (ListView) findViewById(R.id.ll);
        arrayList = new ArrayList<>();
        adapter = new MyAdpter();
        ll.setAdapter(adapter);
        //设置标题字体
//        tf = Typeface.createFromAsset(getAssets(), "font/f1.ttf");
//        (title = (TextView) findViewById(R.id.title)).setTypeface(tf);
        title= (TextView) findViewById(R.id.title);
        myHandler = new MyHandler();
    }
    /**
     * 设置点击事件
     */
    void setAllViewOnclickLinster() {
        //当搜索图标点击时，切换显示效果
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getVisibility() == View.VISIBLE) {
                    hideTitle();
                } else if (title.getVisibility() == View.GONE) {
                    showTitle();
                }
            }
        });
        //点击搜索按钮时，搜索关键字
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = searchEt.getText().toString();
                if (!key.trim().isEmpty()) {
                    if (currentSelectItem != null) {
                        currentSelectItem.setVisibility(View.INVISIBLE);
                    }
                    searchPoi(key, 0, currentInfo.get("cityCode"), false);
                }
            }
        });
        //使editText监听回车事件，进行搜索，效果同上
        searchEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String key = searchEt.getText().toString();
                    if (!key.trim().isEmpty()) {
                        if (currentSelectItem != null) {
                            currentSelectItem.setVisibility(View.INVISIBLE);
                        }
                        searchPoi(key, 0, currentInfo.get("cityCode"), false);
                    }
                    return true;
                }
                return false;
            }
        });
        //返回处理事件
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSearch) {
                    showTitle();
                } else {
                    finish();
                }
            }
        });
        //完成事件
        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击ok的处理事件
                //获取数据并返回上一个activity即可
            }
        });
        //listview点击事件
        ll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MLog.e( i + "");
                PoiItem item = arrayList.get(i);
                MLog.e(item.getLatLonPoint().toString());
                MLog.e(item.toString());
                MLog.e( item.getAdName());
                //在地图上添加一个marker，并将地图中移动至此处
                MarkerOptions mk = new MarkerOptions();
                mk.icon(BitmapDescriptorFactory.defaultMarker());
                mk.title(item.getAdName());
                LatLng ll = new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());
                mk.position(ll);
                //清除所有marker等，保留自身
                aMap.clear(true);
                CameraUpdate cu = CameraUpdateFactory.newLatLng(ll);
                aMap.animateCamera(cu);
                aMap.addMarker(mk);
                //存储当前点击位置
                selectIndex = i;
                //存储当前点击view，并修改view和上一个选中view的定位图标
                ImageView iv = (ImageView) view.findViewById(R.id.yes);
                iv.setVisibility(View.VISIBLE);
                if (currentSelectItem != null) {
                    currentSelectItem.setVisibility(View.INVISIBLE);
                }
                currentSelectItem = iv;
                if (onSearch) {
                    //退出搜索模式，显示地图
                    showTitle();
                }
            }
        });
    }
    /**
     * 初始化高德地图
     */
    void initAMap() {
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        //地图加载监听器
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                //aMap.setMapType();
                aMap.setMyLocationEnabled(true);
                aMap.animateCamera(CameraUpdateFactory.zoomTo(aMap.getMaxZoomLevel() - 1));
            }
        });
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.radiusFillColor(0x70f3ff);
        myLocationStyle.strokeColor(0xe3f9fd);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMaxZoomLevel(aMap.getMaxZoomLevel());
        //地址监听事件
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (!isPoiSearched) {
                    MLog.e(location.toString());
                    MLog.e(location.getProvider());
                    MLog.e(location.getLatitude() + ":" + location.getLongitude());
                    //存储定位数据
                    mCurrentLat = location.getLatitude();
                    mCurrentLng = location.getLongitude();
                    String[] args = location.toString().split("#");
                    for (String arg : args) {
                        String[] data = arg.split("=");
                        if (data.length >= 2)
                            currentInfo.put(data[0], data[1]);
                    }
                    //搜索poi
                    searchPoi("", 0, currentInfo.get("cityCode"), true);
                    //latitude=41.652146#longitude=123.427205#province=辽宁省#city=沈阳市#district=浑南区#cityCode=024#adCode=210112#address=辽宁省沈阳市浑南区创新一路靠近东北大学浑南校区#country=中国#road=创新一路#poiName=东北大学浑南校区#street=创新一路#streetNum=193号#aoiName=东北大学浑南校区#poiid=#floor=#errorCode=0#errorInfo=success#locationDetail=24 #csid:1cce9508143d493182a8da7745eb07b3#locationType=5
                }
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //点击返回键时，将浏览器后退
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onSearch) {
                showTitle();
                return true;
            } else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x001:
                    //加载listview中数据
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
    /**
     * 自定义adpter
     */
    class MyAdpter extends BaseAdapter {
        @Override
        public int getCount() {
            return arrayList.size();
        }
        @Override
        public Object getItem(int i) {
            return arrayList.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //布局加载器
            LayoutInflater inflater = LayoutInflater.from(ShearchPoiActivity.this);
            //加载location_item布局
            View view1 = inflater.inflate(R.layout.location_item, null);
            //修改文字和字体
            TextView v1 = (TextView) view1.findViewById(R.id.name);
            TextView v2 = (TextView) view1.findViewById(R.id.sub);
            ImageView iv = (ImageView) view1.findViewById(R.id.yes);
            v1.setText(arrayList.get(i).getTitle());
            v1.setTypeface(tf);
            v2.setText(arrayList.get(i).getSnippet());
            v2.setTypeface(tf);
            if (selectIndex == i) {
                iv.setVisibility(View.VISIBLE);
                currentSelectItem = iv;
            } else {
                iv.setVisibility(View.INVISIBLE);
            }
            return view1;
        }
    }
    /**
     * 搜索poi
     *
     * @param key  关键字
     * @param pageNum 页码
     * @param cityCode 城市代码，或者城市名称
     * @param nearby 是否搜索周边
     */
    void searchPoi(String key, int pageNum, String cityCode, boolean nearby) {
        MLog.e(key);
        isPoiSearched = true;
        query = new PoiSearch.Query(key, "", cityCode);
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
        if (nearby)
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(mCurrentLat,
                    mCurrentLng), 1500));//设置周边搜索的中心点以及半径
        poiSearch.searchPOIAsyn();
    }
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        int index = 0;
        //填充数据，并更新listview
        ArrayList<PoiItem> result = poiResult.getPois();
        if (result.size() > 0) {
            arrayList.clear();
            selectIndex = -1;
            arrayList.addAll(result);
            myHandler.sendEmptyMessage(0x001);
        }
        for (PoiItem item : poiResult.getPois()) {
           MLog.e(item.toString());
            MLog.e(item.getDirection());
            MLog.e(item.getAdName());
        }
    }
    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
    /**
     * 显示标题栏，即默认状态
     */
    void showTitle() {
        //显示标题栏
        title.setVisibility(View.VISIBLE);
        success.setVisibility(View.VISIBLE);
        searchEt.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);
        frameLayout.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5));
        mMapView.setVisibility(View.VISIBLE);
        onSearch = false;
        closeKeyboard(this);
    }
    /**
     * 隐藏标题栏，即进行搜索
     */
    void hideTitle() {
        //显示搜索框
        title.setVisibility(View.GONE);
        success.setVisibility(View.GONE);
        searchEt.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
        frameLayout.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        50));
        mMapView.setVisibility(View.GONE);
        onSearch = true;
    }
    /**
     * 强制关闭软键盘
     */
    public void closeKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            //如果开启
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            //关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
        }
    }

}
