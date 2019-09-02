package com.nevs.car.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nevs.car.R;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.ChooseCarAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MyUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class ChooseCarActivity extends BaseActivity {
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.xrefresh)
    XRefreshView xRefreshView;
    @BindView(R.id.public404)
    LinearLayout public404;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private BaseQuickAdapter myAdapter;
    private int mLoadCount = 0;
    private List<HashMap<String, Object>> list = new ArrayList<>();
    private List<HashMap<String, Object>> listCarOwer = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_choose_car;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initRecyclyView();
        getTsp6();


    }

    //    private void getTsp6() {
//        /**
//         * {"resultMessage":null,"resultDescription":null,"items":[{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPSBSIMULATOR002","relationType":"车主"},{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"车主"}]}
//         * 获取车辆列表cccc:{"items":[{"bindingId":"685a0ea8b1274cba8e50cfc7a4099ce1","vin":"LTPSBSIMULATOR001","iccid":"89860317352002707001","msisdn":"17317229263","bleAddress":"ble","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null},{"bindingId":"c084b4962c194f8abade11dce9819b09","vin":"LTPCHINATELE00123","iccid":"89860317352002707004","msisdn":"14918158628","bleAddress":"","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null}],"resultMessage":"","resultDescription":""}
//
//         * */
//        DialogUtils.loading(ChooseCarActivity.this, true);
//        list.clear();
//        TspRxUtils.getUservehicleList(getContext(),
//                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
//                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(ChooseCarActivity.this).get(Constant.ACCESSTOKENS, "")},
//                new TspRxListener() {
//                    @Override
//                    public void onSucc(Object obj) {
//                        xRefreshView.stopRefresh();
//                        public404.setVisibility(View.GONE);
//                        DialogUtils.hidding(ChooseCarActivity.this);
//                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);
//                        if (list.size() > 0) {
//                            myAdapter = new ChooseCarAdapter(R.layout.item_choosecar,list); //设置适配器
//                            myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
//                            mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
//                            initOnclickListener();
//                        } else {
//
//                        }
//                    }
//
//                    @Override
//                    public void onFial(String str) {
//                        xRefreshView.stopRefresh();
//                        ActivityUtil.showToast(ChooseCarActivity.this, str);
//                        DialogUtils.hidding(ChooseCarActivity.this);
//                        public404.setVisibility(View.VISIBLE);
//                    }
//                }
//        );
//
//    }
    private void getTsp6() {
        /**
         * {"resultMessage":null,"resultDescription":null,"items":[{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPSBSIMULATOR002","relationType":"车主"},{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"车主"}]}
         * 获取车辆列表cccc:{"items":[{"bindingId":"685a0ea8b1274cba8e50cfc7a4099ce1","vin":"LTPSBSIMULATOR001","iccid":"89860317352002707001","msisdn":"17317229263","bleAddress":"ble","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null},{"bindingId":"c084b4962c194f8abade11dce9819b09","vin":"LTPCHINATELE00123","iccid":"89860317352002707004","msisdn":"14918158628","bleAddress":"","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null}],"resultMessage":"","resultDescription":""}
         * */
        DialogUtils.loading(mContext, true);
        list.clear();
        listCarOwer.clear();
        HttpRxUtils.getCarList(mContext,
                new String[]{"appType", "accessToken", "nevsAccessToken"},
                new Object[]{"Android", new SharedPHelper(mContext).get(Constant.ACCESSTOKEN, "").toString(), new SharedPHelper(mContext).get(Constant.REGISTCENACCESSTOKEN, "").toString()},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        xRefreshView.stopRefresh();
                        public404.setVisibility(View.GONE);
                        DialogUtils.hidding(ChooseCarActivity.this);
                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);

                        if (list.size() > 0) {


                            for (int i = 0; i < list.size(); i++) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                if ((list.get(i).get("relationType") + "").equals("车主")) {
                                    map.put("relationType", list.get(i).get("relationType") + "");
                                    map.put("vin", list.get(i).get("vin") + "");
                                    map.put("licenseNo", list.get(i).get("licenseNo"));
                                    listCarOwer.add((HashMap<String, Object>) map);
                                }

                            }


                            myAdapter = new ChooseCarAdapter(R.layout.item_choosecar, listCarOwer); //设置适配器
                            myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
                            mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
                            initOnclickListener();
                        } else {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_null));
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        xRefreshView.stopRefresh();
                        if (str.contains("401")) {
                            MyUtils.exitToLongin(mContext);
                        } else {
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
                }
        );

    }

    private void initOnclickListener() {
        //条目点击事件
        myAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //  MyToast.showToast(ChooseCarMainActivity.this, "点击了" + position);
                //   startActivity(new Intent(ChooseCarActivity.this, FixEnterActivity.class));
                // new SharedPHelper(ChooseCarActivity.this).put("TSPVIN",list.get(position).get("vin"));
                finishSelect(listCarOwer.get(position).get("vin").toString());

            }
        });
        //条目长按点击事件
        myAdapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                //   MyToast.showToast(ChooseCarMainActivity.this, "长按点击了" + position);
                return true;
            }
        });
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
                getTsp6();
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


    }

    @OnClick({R.id.back_choosecar, R.id.rl_title, R.id.refresh})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back_choosecar:
                finish();
                break;
            case R.id.rl_title:
                break;
            case R.id.refresh:
                getTsp6();
                break;
        }
    }


    private void finishSelect(String s) {
//        Intent data = getIntent();
//        data.putExtra("intentvin", s);
//        setResult(901, data);
//        finish();

        //将计算的结果回传给第一个Activity
        Intent reReturnIntent = new Intent();
        reReturnIntent.putExtra("intentvin", s);
        setResult(904, reReturnIntent);
        //退出第二个Activity
        this.finish();
    }
}
