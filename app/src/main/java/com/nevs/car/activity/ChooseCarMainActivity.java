package com.nevs.car.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.nevs.car.R;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.ChooseCarAdapter;
import com.nevs.car.adapter.xrefreshview.XRefreshView;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelpers;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.z_start.MainActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseCarMainActivity extends BaseActivity {
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.xrefresh)
    XRefreshView xRefreshView;
    @BindView(R.id.public404)
    LinearLayout public404;
    @BindView(R.id.activity_choose_car)
    LinearLayout activityChooseCar;
    private BaseQuickAdapter myAdapter;
    private int mLoadCount = 0;
    private List<HashMap<String, Object>> list = new ArrayList<>();
    private List<HashMap<String, Object>> listJson = new ArrayList<>();

//groupEnName
    @Override
    public int getContentViewResId() {
        return R.layout.activity_choose_car_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(activityChooseCar,mContext);
        initRecyclyView();
        getTsp6();


    }

//    private void getTsp6() {
//        /**
//         * {"resultMessage":null,"resultDescription":null,"items":[{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPSBSIMULATOR002","relationType":"车主"},{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"车主"}]}
//         * 获取车辆列表cccc:{"items":[{"bindingId":"685a0ea8b1274cba8e50cfc7a4099ce1","vin":"LTPSBSIMULATOR001","iccid":"89860317352002707001","msisdn":"17317229263","bleAddress":"ble","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null},{"bindingId":"c084b4962c194f8abade11dce9819b09","vin":"LTPCHINATELE00123","iccid":"89860317352002707004","msisdn":"14918158628","bleAddress":"","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null}],"resultMessage":"","resultDescription":""}
//
//         * */
//        DialogUtils.loading(ChooseCarMainActivity.this, true);
//
//        TspRxUtils.getUservehicleList(getContext(),
//                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
//                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(ChooseCarMainActivity.this).get(Constant.ACCESSTOKENS, "")},
//                new TspRxListener() {
//                    @Override
//                    public void onSucc(Object obj) {
//                        public404.setVisibility(View.GONE);
//                        DialogUtils.hidding(ChooseCarMainActivity.this);
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
//
//                        DialogUtils.hidding(ChooseCarMainActivity.this);
//                        if(str.contains("400")){
//                            ActivityUtil.showToast(ChooseCarMainActivity.this,getResources().getString(R.string.tspnocar));
//                        }else {
//                            ActivityUtil.showToast(ChooseCarMainActivity.this, str);
//                            public404.setVisibility(View.VISIBLE);
//                        }
//
//
//
//                    }
//                }
//        );
//
//    }


    private void getTsp6() {
        /**
         * {"resultMessage":null,"resultDescription":null,"items":[{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPSBSIMULATOR002","relationType":"车主"},{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"车主"}]}
         * 获取车辆列表cccc:{"items":[{"bindingId":"685a0ea8b1274cba8e50cfc7a4099ce1","vin":"LTPSBSIMULATOR001","iccid":"89860317352002707001","msisdn":"17317229263","bleAddress":"ble","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null},{"bindingId":"c084b4962c194f8abade11dce9819b09","vin":"LTPCHINATELE00123","iccid":"89860317352002707004","msisdn":"14918158628","bleAddress":"","relationType":"车主","startTime":1529334769,"endTime":1529334769,"permissions":null}],"resultMessage":"","resultDescription":""}


         6.6
         {
         "isSuccess": "Y",
         "reason": "",
         "data": [{
         "bindingId": "7eb79083fff947748ceb13c405c8be28",
         "isAuthenticated": "False",
         "vin": "LV3SB1411K1000208",
         "iccid": "89860318342003491150",
         "imsi": "460111015938637",
         "msisdn": "14928270146",
         "bleAddress": "6a:6b:6c:6d:6e:6f",
         "relationType": "车主",
         "startTime": "1560145349",
         "endTime": "1560145349",
         "permissions": null
         }, {
         "bindingId": "3741215eaf4f4ca9935d408e2fe183f7",
         "isAuthenticated": "False",
         "vin": "LV3SB1415K1000101",
         "iccid": "89860318342003491564",
         "imsi": "460111015941442",
         "msisdn": "14928270187",
         "bleAddress": "01:a5:a5:a5:a5:a5",
         "relationType": "车主",
         "startTime": "1560145349",
         "endTime": "1560145349",
         "permissions": null
         }, {
         "bindingId": "e9281f3e4bdd4edd8db01fecc15a7d30",
         "isAuthenticated": "False",
         "vin": "LV3SB1414K1000106",
         "iccid": "89860318342003491614",
         "imsi": "460111015942711",
         "msisdn": "14928270192",
         "bleAddress": "01:a5:a5:a5:a5:a5",
         "relationType": "车主",
         "startTime": "1560145349",
         "endTime": "1560145349",
         "permissions": null
         }, {
         "bindingId": "b9c4566efdda48b5a729f315a446ebaa",
         "isAuthenticated": "False",
         "vin": "LV3SB1410K1000104",
         "iccid": "89860318342003490913",
         "imsi": "460111015938613",
         "msisdn": "14928270122",
         "bleAddress": "01:a5:a5:a5:a5:a5",
         "relationType": "授权",
         "startTime": "1558083600",
         "endTime": "1715936400",
         "permissions": ["1", "2", "3", "4", "5", "6"]
         }, {
         "bindingId": "c24b06ceffa74a57a04388409395d61f",
         "isAuthenticated": "True",
         "vin": "LTPSB1413J1000041",
         "iccid": "89860318342003203118",
         "imsi": "460111015940689",
         "msisdn": "14928270063",
         "bleAddress": "01:a5:a5:a5:a5:a5",
         "relationType": "授权",
         "startTime": "1554944400",
         "endTime": "1586566800",
         "permissions": ["1", "2", "3", "4", "5", "6"],
         "carType": "Yes",
         "digitalKey": "",
         "color": "F",
         "groupName": "9-3-滴滴订制",
         "groupEnName": "NISSAN",
         "isDefault": "No",
         "groupCode": "X9-3",
         "licDate": "",
         "licTelecontrol": "",
         "licDoorcontrol": "",
         "licSearchcar": "",
         "licAccontrol": "",
         "nickName": "掉色掉",
         "licenseNo": "京AD88889",
         "invoiceDate": "2019/3/19 0:00:00",
         "custMobile": "13317108921"
         }]
         }
         * */
        DialogUtils.loading(ChooseCarMainActivity.this, true);
        list.clear();
        HttpRxUtils.getCarList(mContext,
                new String[]{"appType", "accessToken", "nevsAccessToken"},
                new Object[]{"Android", new SharedPHelper(mContext).get(Constant.ACCESSTOKEN, "").toString(), new SharedPHelper(mContext).get(Constant.REGISTCENACCESSTOKEN, "").toString()},
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);

                        //   MyUtils.upLogTSO(mContext,"车辆列表",String.valueOf(obj),MyUtils.getTimeNow(),MyUtils.getTimeNow(),"",MyUtils.timeStampNow()+"");


                        if (list.size() > 0) {
                            myAdapter = new ChooseCarAdapter(R.layout.item_choosecar, list); //设置适配器
                            myAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
                            mRecyclerView.setAdapter(myAdapter);//将适配器添加到RecyclerView;
                            initOnclickListener();

                            String start = list.get(0).get("startTime") + "";
                            String end = list.get(0).get("endTime") + "";
                            MLog.e("start:" + HashmapTojson.getDateToString(Long.parseLong(start) * 1000, "yyyyMMdd"));
                            MLog.e("end:" + HashmapTojson.getDateToString(Long.parseLong(end) * 1000, "yyyyMMdd"));

                            //制作缓存
                            createShare();
                        } else {

                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
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
                                    ActivityUtil.showToast(mContext, str);
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
                if(list.size()==0){
                    return;
                }
                getDefaults(position);
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

    /**
     * {
     * "isSuccess": "Y",
     * "reason": "",
     * "data": [{
     * "bindingId": "684f58c97a8546a28d90133174637776",
     * "vin": "LTPSB1413J1000041",
     * "iccid": "89860318342003203118",
     * "msisdn": "14928270063",
     * "bleAddress": "01:a5:a5:a5:a5:a5",
     * "relationType": "授权",
     * "startTime": "1553065200",
     * "endTime": "1584687600",
     * "permissions": ["1", "2", "3", "4", "5", "6"],
     * "carType": "Yes",
     * "digitalKey": "",
     * "color": "F",
     * "groupName": "9-3-滴滴订制",
     * "groupEnName": "NISSAN",
     * "isDefault": "No",
     * "groupCode": "X9-3",
     * "licDate": "",
     * "licTelecontrol": "",
     * "licDoorcontrol": "",
     * "licSearchcar": "",
     * "licAccontrol": "",
     * "nickName": "掉色掉",
     * "licenseNo": "京AD88889",
     * "invoiceDate": "2019/3/19 0:00:00",
     * "custMobile": "13100000001",
     * "IsCertification": "Yes"
     * }, {
     * "bindingId": "7f425b2b11fc4c9c89212b48fd61ced5",
     * "vin": "LTPSB1413J1001214",
     * "iccid": "89860315502187056566",
     * "msisdn": "14918146143",
     * "bleAddress": "24:41:67:18:42:C8",
     * "relationType": "车主",
     * "startTime": "1555918515",
     * "endTime": "1555918515",
     * "permissions": null
     * }, {
     * "bindingId": "7f425b2b12fc4c9c89212b48fd61ced5",
     * "vin": "LTPSBSIMULATOR002",
     * "iccid": "89860317352002707022",
     * "msisdn": "18021029438",
     * "bleAddress": "ble",
     * "relationType": "车主",
     * "startTime": "1555918515",
     * "endTime": "1555918515",
     * "permissions": null
     * }, {
     * "bindingId": "7f445b2c12fc4c9c89212b48fd61ced5",
     * "vin": "LTPSBSIMULATOR003",
     * "iccid": "89860317352002707033",
     * "msisdn": "15502187056",
     * "bleAddress": "84:41:67:11:41:C9",
     * "relationType": "车主",
     * "startTime": "1555918515",
     * "endTime": "1555918515",
     * "permissions": null
     * }, {
     * "bindingId": "3c1abda91411400fa6c0ad4904ff1190",
     * "vin": "LTPSBSIMULATOR199",
     * "iccid": "89860317352002707199",
     * "msisdn": "14928270065",
     * "bleAddress": "84:41:67:11:41:C9",
     * "relationType": "车主",
     * "startTime": "1555918515",
     * "endTime": "1555918515",
     * "permissions": null
     * }]
     * }
     */
    private void getDefaults(final int poss) {
        DialogUtils.loading(mContext, true);

        TspRxUtils.getDefault(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin"},
                new Object[]{list.get(poss).get("vin")},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "默认车", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        //  MyToast.showToast(ChooseCarMainActivity.this, "点击了" + position);
                        //   startActivity(new Intent(ChooseCarActivity.this, FixEnterActivity.class));
                        new SharedPHelper(ChooseCarMainActivity.this).put("TSPVIN", list.get(poss).get("vin") + "");
                        new SharedPHelper(ChooseCarMainActivity.this).put(Constant.MISISDN, list.get(poss).get("msisdn") + "");
                        new SharedPHelper(ChooseCarMainActivity.this).put(Constant.imsi, list.get(poss).get("imsi") + "");//0
                        new SharedPHelper(ChooseCarMainActivity.this).put(Constant.groupCode, list.get(poss).get("groupCode") + "");//
                        new SharedPHelper(ChooseCarMainActivity.this).put(Constant.isAuthenticated, list.get(poss).get("isAuthenticated") + "");//


                        String iscarOwer = list.get(poss).get("relationType") + "";
                        if (iscarOwer.equals("车主")) {//授权
                            new SharedPHelper(ChooseCarMainActivity.this).put(Constant.TSPISCAROWER, "YES");//
                        } else {
                            new SharedPHelper(ChooseCarMainActivity.this).put(Constant.TSPISCAROWER, "NO");//
                        }

                        MyUtils.savaPermissions((List<String>) list.get(poss).get("permissions"), mContext);


                        //initJson(list.get(position).get("vin")+"");
                        if (list.get(poss).get("nickName") != null) {
                            new SharedPHelper(mContext).put(Constant.CARALIAS, String.valueOf(list.get(poss).get("nickName")));
                        }else {
                            new SharedPHelper(mContext).put(Constant.CARALIAS,"");
                        }

                        finishSelect(list.get(poss).get("vin").toString());
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        if (str.contains("400") || str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else if (str.contains("500")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                        } else if (str.contains("401")) {
                            MyUtils.exitToLongin(mContext);
                        } else {
                            ActivityUtil.showToast(mContext, str);

                        }
                        MyUtils.upLogTSO(mContext, "默认车", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }

    private void initJson(String vin) {
        MyUtils.xJson(new SharedPHelper(mContext).get("LOGINJSON", "") + "", listJson);
        if (list.size() != 0) {
            MLog.e("长度：" + listJson.size() + ": " + listJson.get(0).get("vin"));
            for (int i = 0; i < listJson.size(); i++) {
                if (vin.equals(listJson.get(i).get("vin"))) {
                    if (listJson.get(i).get("nickName") != null) {
                        new SharedPHelper(mContext).put(Constant.CARALIAS, String.valueOf(listJson.get(i).get("nickName")));
                    }
                }
            }
        }
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        xRefreshView.stopRefresh();
                    }
                }, 500);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private void finishSelect(String s) {
//        Intent data = getIntent();
//        data.putExtra("intentvin", s);
//        setResult(901, data);
//        finish();

        //将计算的结果回传给第一个Activity
        Intent reReturnIntent = new Intent(this, MainActivity.class);
        reReturnIntent.putExtra("intentvin", s);
        setResult(901, reReturnIntent);
        //退出第二个Activity
        this.finish();
    }


    private void createShare() {
        for (int i = 0; i < list.size(); i++) {
            new SharedPHelpers(mContext, Constant.CSRHEAD + list.get(i).get("vin")).put("bindingId", list.get(i).get("bindingId"));
            new SharedPHelpers(mContext, Constant.CSRHEAD + list.get(i).get("vin")).put("bleAddress", list.get(i).get("bleAddress"));
            new SharedPHelpers(mContext, Constant.CSRHEAD + list.get(i).get("vin")).put("startTime", HashmapTojson.getCSRTime(String.valueOf(list.get(i).get("startTime"))));
            new SharedPHelpers(mContext, Constant.CSRHEAD + list.get(i).get("vin")).put("endTime", HashmapTojson.getCSRTime(String.valueOf(list.get(i).get("endTime"))));
            MLog.e("CSR开始时间：" + new SharedPHelpers(mContext, Constant.CSRHEAD + list.get(i).get("vin")).get("startTime", ""));
            MLog.e("CSR结束时间：" + new SharedPHelpers(mContext, Constant.CSRHEAD + list.get(i).get("vin")).get("endTime", ""));

        }
    }
}
