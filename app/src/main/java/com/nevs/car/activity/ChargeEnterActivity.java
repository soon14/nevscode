package com.nevs.car.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.google.gson.Gson;
import com.nevs.car.R;
import com.nevs.car.adapter.MsgAdapter;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.JsonRootBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.GaodeNaiUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.CustomLinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChargeEnterActivity extends BaseActivity {

    @BindView(R.id.text_all)
    TextView textAll;
    @BindView(R.id.text_line1)
    TextView textLine1;
    @BindView(R.id.text_equip)
    TextView textEquip;
    @BindView(R.id.text_line2)
    TextView textLine2;
    @BindView(R.id.main_one)
    LinearLayout mainOne;
    @BindView(R.id.main_two)
    LinearLayout mainTwo;
    @BindView(R.id.text_name)
    TextView textName;
    @BindView(R.id.text_quik)
    TextView textQuik;
    @BindView(R.id.text_slow)
    TextView textSlow;
    @BindView(R.id.text_one)
    TextView textOne;
    @BindView(R.id.text_two)
    TextView textTwo;
    @BindView(R.id.text_three)
    TextView textThree;
    @BindView(R.id.text_four)
    TextView textFour;
    @BindView(R.id.text_five)
    TextView textFive;
    @BindView(R.id.text_six)
    TextView textSix;
    @BindView(R.id.text_seven)
    TextView textSeven;
    @BindView(R.id.text_eight)
    TextView textEight;
    @BindView(R.id.recyclerone)
    RecyclerView recyclerone;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private boolean isImage = true;
    private String stationID = "";
    private int possionss;
    private ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
    // private BaseQuickAdapter myAdapterOne;
    private String json = "";
    private List<HashMap<String, Object>> listOne = new ArrayList<>();
    private int lastPossion = -1;
    boolean isVisivi = true;
    private MsgAdapter msgAdapter;
    private double lon;
    private double lat;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_charge_enter;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initRv();
        if (getIntent().getIntExtra("possionss", -1) != -1) {
            possionss = getIntent().getIntExtra("possionss", -1);
            MLog.e("possionss:" + possionss);
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            dataList = (ArrayList<HashMap<String, Object>>) bundle.getSerializable("listss");
            MLog.e("dataList:" + dataList.size());
            stationID = dataList.get(possionss).get("stationID") + "";
            lon = Double.parseDouble(dataList.get(possionss).get("longitude") + "");
            lat = Double.parseDouble(dataList.get(possionss).get("latitude") + "");
            upViewOne();
        }
        MLog.e("ddd");
        getTsp();
    }


    private void initRv() {
        CustomLinearLayoutManager linearLayoutManagerone = new CustomLinearLayoutManager(mContext);
        linearLayoutManagerone.setScrollEnabled(false);
        recyclerone.setLayoutManager(linearLayoutManagerone);
        msgAdapter = new MsgAdapter(mContext); //设置适配器
        recyclerone.setAdapter(msgAdapter);
    }

//    private void initOnclickListener() {
//        //条目点击事件
//        myAdapterOne.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                ImageView up= (ImageView) view.findViewById(R.id.up);
//                LinearLayout visiOnes= (LinearLayout) view.findViewById(R.id.lin_visis);
//
//                visiOnes.setEnabled(false);
//                visiOnes.setClickable(false);
//
////                if(lastPossion==position){
////                    if(visiOnes.getVisibility()==View.VISIBLE){
////                        up.setBackgroundResource(R.mipmap.cdzdetail_slideup_jt);
////                        visiOnes.setVisibility(View.GONE);
////                        myAdapterOne.notifyDataSetChanged();
////                    }else {
////                        up.setBackgroundResource(R.mipmap.cdzdetail_slidedown_jt);
////                        visiOnes.setVisibility(View.VISIBLE);
////                        myAdapterOne.notifyDataSetChanged();
////                    }
////
////                }else if(lastPossion==-1){
////                    up.setBackgroundResource(R.mipmap.cdzdetail_slidedown_jt);
////                    visiOnes.setVisibility(View.VISIBLE);
////                    myAdapterOne.notifyDataSetChanged();
////                }else if(lastPossion!=-1&&lastPossion!=position){
////                    if(visiOnes.getVisibility()==View.VISIBLE){
////                        up.setBackgroundResource(R.mipmap.cdzdetail_slideup_jt);
////                        visiOnes.setVisibility(View.GONE);
////                        myAdapterOne.notifyDataSetChanged();
////                    }else {
////                        up.setBackgroundResource(R.mipmap.cdzdetail_slidedown_jt);
////                        visiOnes.setVisibility(View.VISIBLE);
////                        myAdapterOne.notifyDataSetChanged();
////                    }
////                }
//
//
//
//                    up.setBackgroundResource(R.mipmap.cdzdetail_slidedown_jt);
//                    visiOnes.setVisibility(View.VISIBLE);
//                    myAdapterOne.notifyDataSetChanged();
//
//
//
//
//
//                lastPossion=position;
////                up.setBackgroundResource(R.mipmap.cdzdetail_slidedown_jt);
////                visiOnes.setVisibility(View.VISIBLE);
////                myAdapterOne.notifyDataSetChanged();
//            }
//        });
//    }
//
//    private void initOnItemChildClickListener() {
//
//        myAdapterOne.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                MLog.e("点击了子:"+position);
//                ImageView up= (ImageView) view.findViewById(R.id.up);
//                 LinearLayout visiOnes= (LinearLayout) view.findViewById(R.id.lin_visis);
//
//                if(lastPossion==position){
//                    up.setBackgroundResource(R.mipmap.cdzdetail_slideup_jt);
//                }else if(lastPossion==-1){
//                    up.setBackgroundResource(R.mipmap.cdzdetail_slidedown_jt);
//                    visiOnes.setVisibility(View.VISIBLE);
//                }
//
//               lastPossion=position;
//
////                        if (isVisivi) {
////            up.setBackgroundResource(R.mipmap.cdzdetail_slidedown_jt);
////           // visiOnes.setVisibility(View.VISIBLE);
////            isVisivi = false;
////        } else {
////            up.setBackgroundResource(R.mipmap.cdzdetail_slideup_jt);
////          //  visiOnes.setVisibility(View.GONE);
////            isVisivi = true;
////                  }
////
//           }
//        });
//
//    }

    private void upViewOne() {
        tvTitle.setText(dataList.get(possionss).get("stationName") + "");
        textName.setText(dataList.get(possionss).get("stationName") + "");
        textQuik.setText("空闲0/共0");
        textSlow.setText("空闲0/共0");
        textOne.setText(dataList.get(possionss).get("stationName") + "");
        textTwo.setText(dataList.get(possionss).get("siteGuide") + "");
        try {
            textThree.setText((dataList.get(possionss).get("parkNums") + "").split("\\.")[0]);
        } catch (Exception E) {
            textThree.setText(dataList.get(possionss).get("parkNums") + "");
        }

        textFour.setText(dataList.get(possionss).get("serviceTel") + "");
        textFive.setText(dataList.get(possionss).get("businessHours") + "");


        try {
            textSix.setText(MyUtils.getDisOnePoint((dataList.get(possionss).get("electricityFee") + "").split(":")[4]));
        } catch (Exception E) {
            MLog.e("电费异常");
            textSix.setText(dataList.get(possionss).get("electricityFee") + "");
        }

        textSeven.setText(dataList.get(possionss).get("serviceFee") + "");
        textEight.setText(dataList.get(possionss).get("parkFee") + "");

    }

    private void getTsp() {
        json = "";
        DialogUtils.loading(mContext, true);
        TspRxUtils.getStationId(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(ChargeEnterActivity.this).get(Constant.ACCESSTOKENS, "")},
                stationID,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        json = String.valueOf(obj);
                        MyUtils.upLogTSO(mContext, "充电服务详情", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");


                        JsonRootBean rootBean = new Gson().fromJson(json, JsonRootBean.class);
                        List<JsonRootBean.Items> datas = rootBean.getItems();
                        MLog.e("dddddd:" + datas.get(0).getConnectors().get(0).getConnectorName());
                        msgAdapter.setLists(datas);
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
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
                        MyUtils.upLogTSO(mContext, "充电服务详情", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.text_all, R.id.text_equip, R.id.text_guide})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.text_all:
                upOne();
                break;
            case R.id.text_equip:
                upTwo();
                break;
//            case R.id.rel_one:
//                upImage();
//                break;
            case R.id.text_guide:
                showGuides();
                break;
        }
    }

    private void showGuides() {
        final String[] stringItems = {getResources().getString(R.string.gaodemap),
        };
        //getResources().getString(R.string.baidumap)
        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.isTitleShow(false)
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
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_installgao));
                        }
                        break;
//                    case 1:
//                        //百度地图
//                        if (BaiduNaiUtils.isInstalled()) {
//                            starBaidu();
//                        } else {
//                            ActivityUtil.showToast(ChargeMainActivity.this, "请先安装百度地图客户端");
//                        }
//                        break;
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

    private void upImage() {
//        if (isImage) {
//            up.setBackgroundResource(R.mipmap.cdzdetail_slidedown_jt);
//            visiOne.setVisibility(View.VISIBLE);
//            isImage = false;
//        } else {
//            up.setBackgroundResource(R.mipmap.cdzdetail_slideup_jt);
//            visiOne.setVisibility(View.GONE);
//            isImage = true;
        //  }
    }

    private void upTwo() {
        textLine2.setVisibility(View.VISIBLE);
        textLine1.setVisibility(View.GONE);
        mainOne.setVisibility(View.GONE);
        mainTwo.setVisibility(View.VISIBLE);
        textEquip.setTextColor(getResources().getColor(R.color.text_default));
        textAll.setTextColor(getResources().getColor(R.color.android_defalt));
    }

    private void upOne() {
        textLine1.setVisibility(View.VISIBLE);
        textLine2.setVisibility(View.GONE);
        mainOne.setVisibility(View.VISIBLE);
        mainTwo.setVisibility(View.GONE);
        textAll.setTextColor(getResources().getColor(R.color.text_default));
        textEquip.setTextColor(getResources().getColor(R.color.android_defalt));
    }


}
