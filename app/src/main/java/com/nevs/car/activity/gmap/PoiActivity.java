package com.nevs.car.activity.gmap;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PoiActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener,TextWatcher,Inputtips.InputtipsListener {


    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.text_content)
    AutoCompleteTextView textContent;
    @BindView(R.id.btn_search)
    ImageButton btnSearch;
    @BindView(R.id.dialog_search_recyclerview)
    RecyclerView dialogSearchRecyclerview;
    public static final String IS_PICK_END = "is_pick_end";
    public static final int RESULT_CODE = 1;
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

    //当前城市 默认是吉林
    private String cityText = "吉林";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_poi;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        textContent.addTextChangedListener(this);

    }

    @OnClick({R.id.btn_back, R.id.text_content, R.id.btn_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.text_content:
                break;
            case R.id.btn_search:
                break;
        }
    }
    //开始poi检索
    protected void doSearchQuery() {
        showProgressDialog();
        // 显示进度框
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", cityText);
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);
        // 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);
        // 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }
    //显示进度条
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + keyWord);
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
            Toast.makeText(this, "错误代码:" + rCode, Toast.LENGTH_SHORT).show();
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
            InputtipsQuery inputquery = new InputtipsQuery(str, cityText);
            Inputtips inputTips = new Inputtips(PoiActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        // 正确返回
        if (i == 1000) {
            List<String> listString = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
                listString.add(list.get(j).getName());
                Log.e("text:", list.get(j).getName());
            }
            ArrayAdapter<String> aAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1, listString);
            textContent.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "网络错误：" + i, Toast.LENGTH_SHORT).show();
        }

    }
}
