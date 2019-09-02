package com.nevs.car.activity.gmap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nevs.car.R;
import com.nevs.car.activity.ChargeMain2Activity;
import com.nevs.car.activity.service.ServiceStopActivity;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.HistoryAdapter;
import com.nevs.car.adapter.SearchAdapter;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.AddressBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PoiChangeActivity extends BaseActivity implements TextWatcher
        , AdapterView.OnItemClickListener, PoiSearch.OnPoiSearchListener,
        Inputtips.InputtipsListener {
    @BindView(R.id.text_content)
    AutoCompleteTextView textContent;
    @BindView(R.id.lin)
    LinearLayout lin;
    @BindView(R.id.search_list)
    ListView lv;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.linis)
    LinearLayout linis;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    //poi检索
    // poi返回的结果
    private PoiResult poiResult;
    // 当前页面，从0开始计数
    private int currentPage = 0;
    // Poi查询条件类
    private PoiSearch.Query query;
    // POI搜索
    private PoiSearch poiSearch;
    private ProgressDialog progDialog = null;
    //关键字
    private String keyWord = null;

    //当前城市 默认是
    private String cityText = null;
    private SearchAdapter mAdapter;
    private ArrayList<AddressBean> data = new ArrayList<AddressBean>();
    private String lat = null;
    private String lon = null;//经度
    private String name = null;
    private String text = null;
    private boolean isSearch = true;
    private BaseQuickAdapter myAdapter;
    private List<String> listHistory = new ArrayList<>();
    private static SharedPreferences share;
    private static SharedPreferences.Editor editor;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_poi_change;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        getShare(mContext);
        initIntent();
        initAdapter();
        initListHistory();
        initRecyclyView();
        initOnclickListener();
        //initkeyboard();
    }

    public void getShare(Context context) {
        share = context.getSharedPreferences(Constant.HISTORYFILENAMESSS, Context.MODE_PRIVATE);
        editor = share.edit();
    }

    public void clear() {
//        SharedPreferences preferences = context.getSharedPreferences(Constant.HISTORYFILENAMESSS, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private void intent() {
        if (data.size() == 0) {
            MLog.e("搜索操作不执行");
            if (textContent.getText().toString().trim().length() == 0) {
                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_poisearhempty));
            } else {
                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_poisearhone));
            }

        } else {
            MLog.e("搜索操作执行 " + textContent.getText());

            insertShare(textContent.getText().toString().trim());//存入缓存

            startActivity(new Intent(mContext, SerchGuideActivity.class).putExtra("kuanjiazi", textContent.getText().toString()));
        }
    }

    private void initkeyboard() {
        textContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    intent();
                }
                return false;
            }
        });
    }

    private void insertShare(String name) {
        historyInsert(name);
    }

    private void initListHistory() {
        MLog.e("share.historyList()" + historyList().size());
        if (historyList().size() == 0)
            return;
        listHistory.addAll(historyList());
    }

    public void historyInsert(String keyword) {//插入数据
        LinkedHashSet<String> historySet = new LinkedHashSet<>();
        String jsonString = share.getString("historyList", "");
        LinkedHashSet<String> set = new Gson().fromJson(jsonString, new TypeToken<LinkedHashSet<String>>() {
        }.getType());
        if (set != null) {
            historySet.addAll(set);
        }
        if (historySet.size() <= 15) {
            historySet.add(keyword);
        }
        if (historySet.size() == 16) {
            List<String> list = new ArrayList<>();
            list.addAll(historySet);
            Collections.reverse(list);
            historySet.remove(list.get(list.size() - 1));
        }
        share.edit().putString("historyList", new Gson().toJson(historySet)).apply();
    }

    public List<String> historyList() {//查询所有数据并返回LIST
        String jsonString = share.getString("historyList", "");
        LinkedHashSet<String> set = new Gson().fromJson(jsonString, new TypeToken<LinkedHashSet<String>>() {
        }.getType());
        LinkedHashSet<String> historySet = new LinkedHashSet<>();
        if (set != null) {
            historySet.addAll(set);
        }
        List<String> historyList = new ArrayList<>();
        historyList.addAll(historySet);
        Collections.reverse(historyList);
        return historyList;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if (mRecyclerView.getVisibility() == View.GONE) {
//            mRecyclerView.setVisibility(View.VISIBLE);
//        }
//        if (data.size() != 0) {
//            data.clear();
//            lv.setVisibility(View.GONE);
//            lin.setVisibility(View.VISIBLE);
//        }
//        listHistory.clear();
//        initListHistory();
//        myAdapter.notifyDataSetChanged();
//        textContent.setText("");
    }

    private void initRecyclyView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new HistoryAdapter(R.layout.item_poihistory, listHistory); //设置适配器
        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                if(data.size()!=0){
//                    startActivity(new Intent(mContext, ChargeMain2Activity.class).putExtra("search",data).putExtra("possion",position));
//                }
                if (!ClickUtil.isFastClick()) {
                    return;
                }
                MLog.e("历史列表点击");
                lin.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);

                textContent.setText(listHistory.get(position));

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

    private void initIntent() {
        cityText = getIntent().getStringExtra("city");
        MLog.e("cityText:" + cityText);

        if (getIntent().getStringExtra("chargemainintent") != null) {
            if (getIntent().getStringExtra("chargemainintent").equals("1")) {
                linis.setVisibility(View.GONE);
            }
        }
    }

    private void initAdapter() {
        mAdapter = new SearchAdapter(this, data);
        lv.setAdapter(mAdapter);
        textContent.addTextChangedListener(this);
        lv.setOnItemClickListener(this);


    }

    @OnClick({R.id.charge, R.id.stop, R.id.service_stop, R.id.favorite, R.id.clear, R.id.image_serch,
            R.id.search, R.id.text_content, R.id.back})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.search://search
                intent();
                break;
            case R.id.charge:
                startActivity(new Intent(this, ChargeStopActivity.class));
                break;
            case R.id.stop:
                startActivity(new Intent(this, StopActivity.class));
                break;
            case R.id.service_stop:
                startActivity(new Intent(this, ServiceStopActivity.class));
                break;
            case R.id.favorite:
                startActivity(new Intent(this, FavoriteActivity.class));
                break;
            case R.id.clear:
                clear();
                listHistory.clear();
                myAdapter.notifyDataSetChanged();
                mRecyclerView.setVisibility(View.GONE);
                break;
            case R.id.image_serch:
//                if (textContent.getText().toString().trim().length() == 0) {
//                    ActivityUtil.showToast(this, "请输入目的地");
//                } else {
//                    if(lon==null){
//                        ActivityUtil.showToast(this,"请在下面列表中选择一个详细地址后收索");
//                    }else {
//                        startActivity(new Intent(this, SerchGuideActivity.class).putExtra("poilat",lat
//                        ).putExtra("poilon",lon).putExtra("name",name).putExtra("text",text).putExtra("AddressBean",data));
//                    }
//
//                }
                break;
            case R.id.text_content:
                isSearch = true;
                // textContent.setText("");
                lv.setVisibility(View.GONE);
                lin.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    protected void onStart() {

        super.onStart();
        textContent.setText("");

    }

    //显示进度条
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage(getResources().getString(R.string.toast_serching) + "\n" + keyWord);
        progDialog.show();
    }

    //关闭进度条
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        // 隐藏对话框
        dissmissProgressDialog();

        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        //cc  aMap.clear();
                        // 清理之前的图标
//                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
//                        poiOverlay.removeFromMap();
//                        poiOverlay.addToMap();
//                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
//                        showSuggestCity(suggestionCities);
                    } else {
                        Toast.makeText(this, R.string.no_result, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, R.string.no_result, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_errorrcode) + rCode, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String str = charSequence.toString().trim();

        if (!str.equals("")) {
            InputtipsQuery inputtipsQuery = new InputtipsQuery(str, cityText);//初始化一个输入提示搜索对象，并传入参数
            inputtipsQuery.setCityLimit(true);//将获取到的结果进行城市限制筛选TRUE
            Inputtips inputtips = new Inputtips(this, inputtipsQuery);//定义一个输入提示对象，传入当前上下文和搜索对象
            inputtips.setInputtipsListener(this);//设置输入提示查询的监听，实现输入提示的监听方法onGetInputtips()
            inputtips.requestInputtipsAsyn();//输入查询提示的异步接口实现
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        data.clear();
        mAdapter.notifyDataSetChanged();
        // 正确返回
        if (i == 1000) {
            if (isSearch) {
                List<String> listString = new ArrayList<>();
                for (int j = 0; j < list.size(); j++) {
//                listString.add(list.get(j).getName());
//                Log.e("text:", list.get(j).getName());
                    data.add(new AddressBean(
                            list.get(j).getPoint().getLongitude(),
                            list.get(j).getPoint().getLatitude(),
                            list.get(j).getName(),
                            list.get(j).getDistrict()
                    ));
                }
                lv.setVisibility(View.VISIBLE);
                lin.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();

            } else {
            }


        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_errorrnetwork) + i, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
//            textContent.setText(data.get(position).getTitle());
//            lon = data.get(position).getLongitude() + "";
//            lat = data.get(position).getLatitude() + "";
            name = data.get(position).getTitle();
//            text = data.get(position).getText();

            isSearch = false;

            insertShare(name);//存入缓存

//            startActivity(new Intent(this, SerchGuideActivity.class).putExtra("poilat", lat
//            ).putExtra("poilon", lon).putExtra("name", name).putExtra("text", text).putExtra("AddressBean", data));

            //cc   startActivity(new Intent(mContext, SerchGuideActivity.class).putExtra("kuanjiazi", name));
//            lin.setVisibility(View.VISIBLE);
//            lv.setVisibility(View.GONE);
//
//            textContent.setText(name);


            if (data.size() != 0) {
                startActivity(new Intent(mContext, ChargeMain2Activity.class).putExtra("search", data).putExtra("possion", position));
            }


        } catch (Exception E) {

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
