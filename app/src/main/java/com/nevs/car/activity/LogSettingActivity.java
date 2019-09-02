package com.nevs.car.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.nevs.car.R;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.LogSettingA;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.OnClick;

public class LogSettingActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private BaseQuickAdapter myAdapter;
    private List<HashMap<String, Object>> listPoi = new ArrayList<>();
    private List<HashMap<String, Object>> listPois = new ArrayList<>();
    private Set<Integer> set = new TreeSet<>();
    private List<Integer> list = new ArrayList<>();
    private Map<Integer, Integer> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_log_setting;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initRecyclyView();
        getTsp24();
    }

    @OnClick({R.id.back, R.id.tv_title, R.id.mark
            , R.id.markall, R.id.deleteall
    })
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_title:
                break;
            case R.id.mark:
                // DialogUtils.showPoals(this);
                if (map.size() == 0) {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.choosetrip));
                } else {
                    showPoals();
                }

                break;

            case R.id.markall://全部选择
                upAdapter();
                break;
            case R.id.deleteall:
                deleteAll();
                break;

        }
    }

    private void deleteAll() {
        if (map.size() == 0) {
            ActivityUtil.showToast(mContext, getResources().getString(R.string.choosetrip));
        } else {
            getTsp27(getArry());
        }

    }

    private void upAdapter() {
        for (int i = 0; i < listPois.size(); i++) {
            listPois.get(i).put("isCheck", "1");
            map.put(i, i);
        }
        myAdapter.notifyDataSetChanged();
    }

    private String[] getArry() {
        //      String items[]=new String[listPoi.size()];
//        for(int i=0;i<listPoi.size();i++){
//            items[i]=listPoi.get(i).get("poiId").toString();
//        }

        List<Integer> list = new ArrayList<>();
        List<String> listString = new ArrayList<>();
        for (int v : map.values()) {
            list.add(v);
        }
        MLog.e("选择的个数" + list.size());
        for (int k = 0; k < list.size(); k++) {
            listString.add(listPoi.get(list.get(k)).get("tripId") + "");
        }
        String items[] = new String[listString.size()];
        for (int i = 0; i < listString.size(); i++) {
            items[i] = listString.get(i).toString();
        }

        return items;
    }

    private void getTsp27(String[] aa) {
        /**
         *
         * */
        DialogUtils.loading(mContext, true);
        TspRxUtils.getDeletetrip(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                new String[]{"tripId"},
                new Object[]{aa},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_deletesuccess));
                        getTsp24();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.deletefail));
                    }
                }
        );

    }

    private void getTsp24() {
        /**
         *{
         "items": [
         {
         "tripId": "fbdb798c-a30a-42d4-acde-05d7a0d248d9",
         "title": "德语",
         "category": "公务",
         "remark": "不",
         "beginTime": 1524045809,
         "endTime": 1524045870,
         "beginLongitude": 116.309931,
         "beginLatitude": 39.984586,
         "endLongitude": 116.310764,
         "endLatitude": 39.985175,
         "totalDuration": 1,
         "totalMileage": 2,
         "status": 2
         }
         ],
         "resultMessage": "",
         "resultDescription": ""
         }  new Object[]{new SharedPHelper(CarCopyActivity.this).get("TSPVIN", "0"), 1l, 2l, 0, 20},
         * */
        listPoi.clear();
        listPois.clear();
        set.clear();
        map.clear();
        DialogUtils.loading(mContext, true);
        TspRxUtils.getHistory(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(LogSettingActivity.this).get(Constant.ACCESSTOKENS, "")},
                new Object[]{new SharedPHelper(LogSettingActivity.this).get("TSPVIN", "0")},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "获取行程记录列表", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        listPoi.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (listPoi.size() == 0) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_null));
                        } else {
                            for (int i = 0; i < listPoi.size(); i++) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("title", listPoi.get(i).get("title") + "");
                                map.put("tripId", listPoi.get(i).get("tripId") + "");
                                map.put("isCheck", "0");
                                listPois.add((HashMap<String, Object>) map);
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "获取行程记录开启列表", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else if (str.contains("500") || str.contains("无效的网址")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                        } else if (str.contains("未授权的请求")) {
                            MyUtils.exitToLongin(mContext);
                        } else if (str.contains("401")) {
                            MyUtils.exitToLongin401(mContext);
                        } else if (str.contains("连接超时")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.timeout));
                        } else {
                            //  ActivityUtil.showToast(mContext, str);
                        }
                    }
                }
        );

    }

    private void initRecyclyView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));  //设置RecyclerView的显示模式
        myAdapter = new LogSettingA(R.layout.item_favipois, listPois); //设置适配器
        myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
        initOnclickListener();
    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


//                if(map.size()==0){
//                    listPois.get(position).put("isCheck","1");
//                    map.put(position,position);
//                    myAdapter.notifyDataSetChanged();
//                }else {
//
//                    for (int v : map.values()) {
//                        if(v==position){
//                            listPois.get(position).put("isCheck","0");
//                            myAdapter.notifyDataSetChanged();
//                            map.remove(position);
//                            break;
//                        }else {
//                            listPois.get(position).put("isCheck","1");
//                            myAdapter.notifyDataSetChanged();
//                            map.put(position,position);
//                            break;
//                        }
//
//
//                    }
//
//                }


                if (map.size() == 0) {
                    listPois.get(position).put("isCheck", "1");
                    map.put(position, position);
                    myAdapter.notifyDataSetChanged();
                    MLog.e("1");
                } else {
                    MLog.e("2");

                    Set set = map.entrySet();

                    Iterator iterator = set.iterator();

                    List<Integer> list = new ArrayList<Integer>();
                    list.clear();
                    while (iterator.hasNext()) {
                        Map.Entry mapentry = (Map.Entry) iterator.next();
                        MLog.e("遍历：" + (int) mapentry.getValue());
                        list.add((int) mapentry.getValue());
                    }

                    if (list != null && list.contains(position)) {//重复点击取消
                        MLog.e("isis");
                        MLog.e("4");
                        listPois.get(position).put("isCheck", "0");
                        myAdapter.notifyDataSetChanged();
                        map.remove(position);
                    } else {//新加入
                        MLog.e("5");
                        listPois.get(position).put("isCheck", "1");
                        myAdapter.notifyDataSetChanged();
                        map.put(position, position);

                    }

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

    public void showPoals() {
        final String[] stringItems = new String[]{getResources().getString(R.string.nevs_official),
                getResources().getString(R.string.nevs_private),
                getResources().getString(R.string.nevs_custom)};
        final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
        dialog.isTitleShow(false).show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                //textGoal.setText(stringItems[position]);
                // ActivityUtil.showToast(mContext,getResources().getString(R.string.toast_marksuc));
                dialog.dismiss();
                getTsp26(stringItems[position], getArry());
            }
        });
    }

    private void getTsp26(String remark, String[] arry) {
        /**
         *
         * */
        DialogUtils.loading(mContext, true);
        TspRxUtils.getSettag(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                new String[]{"tripId", "title", "category", "remark"},
                new Object[]{arry, "", remark, ""},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_marksuc));

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.mark_fail));
                    }
                }
        );

    }

}
