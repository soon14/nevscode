package com.nevs.car.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.activity.service.ServiceOrderActivity;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.CarHealthAdapter;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityManager;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.CustomLinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.nevs.car.R.drawable.bg_circle_rect_code;
import static com.nevs.car.R.drawable.health_yello;

public class CarHealthActivity extends BaseActivity {

    @BindView(R.id.image_one)
    ImageView imageOne;
    @BindView(R.id.isv_one)
    RelativeLayout isvOne;
    @BindView(R.id.iamge_two)
    ImageView iamgeTwo;
    @BindView(R.id.isv_two)
    RelativeLayout isvTwo;
    @BindView(R.id.iamge_three)
    ImageView iamgeThree;
    @BindView(R.id.isv_three)
    RelativeLayout isvThree;
    @BindView(R.id.iamge_four)
    ImageView iamgeFour;
    @BindView(R.id.isv_four)
    RelativeLayout isvFour;
    @BindView(R.id.image_five)
    ImageView imageFive;
    @BindView(R.id.isv_five)
    RelativeLayout isvFive;
    @BindView(R.id.image_six)
    ImageView imageSix;
    @BindView(R.id.isv_six)
    RelativeLayout isvSix;
    @BindView(R.id.image_seven)
    ImageView imageSeven;
    @BindView(R.id.isv_seven)
    RelativeLayout isvSeven;
    @BindView(R.id.text_one)
    TextView textOne;
    @BindView(R.id.text_two)
    TextView textTwo;
    @BindView(R.id.text_three)
    TextView textThree;
    @BindView(R.id.text_four)
    TextView textFour;
    @BindView(R.id.recyclerone)
    RecyclerView recyclerone;
    @BindView(R.id.recyclertwo)
    RecyclerView recyclertwo;
    @BindView(R.id.recyclerthree)
    RecyclerView recyclerthree;
    @BindView(R.id.recyclerfour)
    RecyclerView recyclerfour;
    @BindView(R.id.recyclerfive)
    RecyclerView recyclerfive;
    @BindView(R.id.recyclersix)
    RecyclerView recyclersix;
    @BindView(R.id.recyclerseven)
    RecyclerView recyclerseven;
    @BindView(R.id.isv_sro)
    ScrollView isvSro;
    @BindView(R.id.endtime)
    TextView endtime;
    @BindView(R.id.lin_sro)
    LinearLayout linSro;
    @BindView(R.id.hint_one)
    TextView hintOne;
    @BindView(R.id.hint_two)
    TextView hintTwo;
    @BindView(R.id.hint_three)
    TextView hintThree;
    @BindView(R.id.hint_four)
    TextView hintFour;
    @BindView(R.id.hint_five)
    TextView hintFive;
    @BindView(R.id.hint_six)
    TextView hintSix;
    @BindView(R.id.hint_seven)
    TextView hintSeven;
    @BindView(R.id.one1)
    TextView one1;
    @BindView(R.id.one2)
    TextView one2;
    @BindView(R.id.two1)
    TextView two1;
    @BindView(R.id.three1)
    TextView three1;
    @BindView(R.id.four1)
    TextView four1;
    @BindView(R.id.four2)
    TextView four2;
    @BindView(R.id.four3)
    TextView four3;
    @BindView(R.id.five1)
    TextView five1;
    @BindView(R.id.six1)
    TextView six1;
    @BindView(R.id.seven1)
    TextView seven1;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private boolean isOne = true;
    private boolean isTwo = true;
    private boolean isThree = true;
    private boolean isFour = true;
    private boolean isFive = true;
    private boolean isSix = true;
    private boolean isSeven = true;
    private String json = "";
    private BaseQuickAdapter myAdapterOne;
    private BaseQuickAdapter myAdapterTwo;
    private BaseQuickAdapter myAdapterThree;
    private BaseQuickAdapter myAdapterFour;
    private BaseQuickAdapter myAdapterFive;
    private BaseQuickAdapter myAdapterSix;
    private BaseQuickAdapter myAdapterSeven;
    private List<HashMap<String, Object>> listOne = new ArrayList<>();
    private List<HashMap<String, Object>> listTwo = new ArrayList<>();
    private List<HashMap<String, Object>> listThree = new ArrayList<>();
    private List<HashMap<String, Object>> listFour = new ArrayList<>();
    private List<HashMap<String, Object>> listFive = new ArrayList<>();
    private List<HashMap<String, Object>> listSix = new ArrayList<>();
    private List<HashMap<String, Object>> listSeven = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_car_health;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        ActivityManager.getInstance().addActivity(CarHealthActivity.this);
        //initRv();
        //  initClick();
        getTsp12();

    }


    @OnClick({R.id.back, R.id.right_image, R.id.rel_one, R.id.rel_two, R.id.rel_three,
            R.id.rel_four, R.id.rel_five, R.id.rel_six, R.id.rel_seven, R.id.btn_draw,
            R.id.isv_ones, R.id.isv_twos, R.id.isv_threes, R.id.isv_fours, R.id.isv_fives,
            R.id.isv_sixs, R.id.isv_sevens, R.id.refresh
    })
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_image:
                getTsp12();
                break;
            case R.id.rel_one:
                isVis(isOne, 1, imageOne, isvOne);
                break;
            case R.id.rel_two:
                isVis(isTwo, 2, iamgeTwo, isvTwo);
                break;
            case R.id.rel_three:
                isVis(isThree, 3, iamgeThree, isvThree);
                break;
            case R.id.rel_four:
                isVis(isFour, 4, iamgeFour, isvFour);
                break;
            case R.id.rel_five:
                isVis(isFive, 5, imageFive, isvFive);
                break;
            case R.id.rel_six:
                isVis(isSix, 6, imageSix, isvSix);
                break;
            case R.id.rel_seven:
                isVis(isSeven, 7, imageSeven, isvSeven);
                break;
            case R.id.btn_draw:
                getTsp12();
                break;
            case R.id.refresh:
                getTsp12();
                break;
            case R.id.isv_ones:
                if (hintOne.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
                break;
            case R.id.isv_twos:
                if (hintTwo.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
                break;
            case R.id.isv_threes:
                if (hintThree.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
                break;
            case R.id.isv_fours:
                if (hintFour.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
                break;
            case R.id.isv_fives:
                if (hintFive.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
                break;
            case R.id.isv_sixs:
                if (hintSix.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
                break;
            case R.id.isv_sevens:
                if (hintSeven.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
                break;

        }
    }

    private void isVis(boolean flag, int id, ImageView imageView, RelativeLayout rel) {
        if (flag) {
            switch (id) {
                case 1:
                    isOne = false;
                    rel.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    isTwo = false;
                    rel.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    isThree = false;
                    rel.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    isFour = false;
                    rel.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    isFive = false;
                    rel.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    isSix = false;
                    rel.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    isSeven = false;
                    rel.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            switch (id) {
                case 1:
                    isOne = true;
                    rel.setVisibility(View.GONE);
                    break;
                case 2:
                    isTwo = true;
                    rel.setVisibility(View.GONE);
                    break;
                case 3:
                    isThree = true;
                    rel.setVisibility(View.GONE);
                    break;
                case 4:
                    isFour = true;
                    rel.setVisibility(View.GONE);
                    break;
                case 5:
                    isFive = true;
                    rel.setVisibility(View.GONE);
                    break;
                case 6:
                    isSix = true;
                    rel.setVisibility(View.GONE);
                    break;
                case 7:
                    isSeven = true;
                    rel.setVisibility(View.GONE);
                    break;
            }
        }
    }


    private void initRv() {
        CustomLinearLayoutManager linearLayoutManagerone = new CustomLinearLayoutManager(mContext);
        linearLayoutManagerone.setScrollEnabled(false);
        CustomLinearLayoutManager linearLayoutManagertwo = new CustomLinearLayoutManager(mContext);
        linearLayoutManagertwo.setScrollEnabled(false);
        CustomLinearLayoutManager linearLayoutManagerthree = new CustomLinearLayoutManager(mContext);
        linearLayoutManagerthree.setScrollEnabled(false);
        CustomLinearLayoutManager linearLayoutManagerfour = new CustomLinearLayoutManager(mContext);
        linearLayoutManagerthree.setScrollEnabled(false);
        CustomLinearLayoutManager linearLayoutManagerfive = new CustomLinearLayoutManager(mContext);
        linearLayoutManagerthree.setScrollEnabled(false);
        CustomLinearLayoutManager linearLayoutManagersix = new CustomLinearLayoutManager(mContext);
        linearLayoutManagerthree.setScrollEnabled(false);
        CustomLinearLayoutManager linearLayoutManagerseven = new CustomLinearLayoutManager(mContext);
        linearLayoutManagerthree.setScrollEnabled(false);
        recyclerone.setLayoutManager(linearLayoutManagerone);
        recyclertwo.setLayoutManager(linearLayoutManagertwo);
        recyclerthree.setLayoutManager(linearLayoutManagerthree);
        recyclerfour.setLayoutManager(linearLayoutManagerfour);
        recyclerfive.setLayoutManager(linearLayoutManagerfive);
        recyclersix.setLayoutManager(linearLayoutManagersix);
        recyclerseven.setLayoutManager(linearLayoutManagerseven);

        myAdapterOne = new CarHealthAdapter(R.layout.item_health, listOne); //设置适配器
        myAdapterOne.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        recyclerone.setAdapter(myAdapterOne);
        myAdapterTwo = new CarHealthAdapter(R.layout.item_health, listTwo); //设置适配器
        myAdapterTwo.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        recyclertwo.setAdapter(myAdapterTwo);
        myAdapterThree = new CarHealthAdapter(R.layout.item_health, listThree); //设置适配器
        myAdapterThree.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        recyclerthree.setAdapter(myAdapterThree);
        myAdapterFour = new CarHealthAdapter(R.layout.item_health, listFour); //设置适配器
        myAdapterFour.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        recyclerfour.setAdapter(myAdapterFour);
        myAdapterFive = new CarHealthAdapter(R.layout.item_health, listFive); //设置适配器
        myAdapterFive.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        recyclerfive.setAdapter(myAdapterFive);
        myAdapterSix = new CarHealthAdapter(R.layout.item_health, listSix); //设置适配器
        myAdapterSix.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        recyclersix.setAdapter(myAdapterSix);
        myAdapterSeven = new CarHealthAdapter(R.layout.item_health, listSeven); //设置适配器
        myAdapterSeven.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        recyclerseven.setAdapter(myAdapterSeven);

    }

    private void initClick() {
        myAdapterOne.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (hintOne.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
            }
        });
        myAdapterTwo.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (hintTwo.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
            }
        });
        myAdapterThree.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (hintThree.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
            }
        });
        myAdapterFour.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (hintFour.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
            }
        });
        myAdapterFive.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (hintFive.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
            }
        });
        myAdapterSix.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (hintSix.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
            }
        });

        myAdapterSeven.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (hintSeven.getVisibility() == View.VISIBLE) {
                    startActivity(new Intent(mContext, ServiceOrderActivity.class).putExtra("ishealth", "yes"));
                }
            }
        });
    }

    private void getTsp12() {//
        /**
         *车辆健康cccc:{"vin":"LTPSBSIMULATOR001","alarmLevelL1":{"turnIndicatorFailure":false,"
         * washerFluidLevelLow":false,"immobilizerFailure":false},"alarmLevelL2":
         * {"antilckBrakeMalfnctn":false,"tiredeflation":false,"tirePressurSysFailure":false,"airbagMalfunction":false},"
         * alarmLevelL3":{"brakeFluidLevelLow":false,"brakeFailure":false,"internalFaultStatus":false},"
         * updateTime":1528961753,"resultMessage":"","resultDescription":""}
         * */
        MLog.e("车辆体检" + new SharedPHelper(CarHealthActivity.this).get("TSPVIN", "0").toString());
        clearList();
        DialogUtils.loading(mContext, true);
        TspRxUtils.getHealth(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new SharedPHelper(this).get("TSPVIN", "0") + "",
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "车辆体检", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");


                        json = String.valueOf(obj);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_healthend));
                        linSro.setVisibility(View.VISIBLE);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            upView(jsonObject);
                            setAdapter();
                            // MyUtils.upLogTSO(mContext,"车辆健康","","","","");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        isvSro.setVisibility(View.INVISIBLE);
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
                        MyUtils.upLogTSO(mContext, "车辆体检", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

    }

    private void setAdapter() {
        myAdapterOne.notifyDataSetChanged();
        myAdapterTwo.notifyDataSetChanged();
        myAdapterThree.notifyDataSetChanged();
        myAdapterFour.notifyDataSetChanged();
        myAdapterFive.notifyDataSetChanged();
        myAdapterSix.notifyDataSetChanged();
        myAdapterSeven.notifyDataSetChanged();
    }

    private void clearList() {
        listOne.clear();
        listTwo.clear();
        listThree.clear();
        listFour.clear();
        listFive.clear();
        listSix.clear();
        listSeven.clear();
    }

    private void upView(JSONObject jsonObject) {
        try {//yyyy-MM-dd hh:mm:ss 十二小时制
            textOne.setText(jsonObject.getString("averageEnergyConsumption") + "kW");
            // textTwo.setText((int) (Double.parseDouble(jsonObject.get("averageSpeed") + "")) + "km/h");
            textTwo.setText((jsonObject.get("averageSpeed") + "") + "km/h");
            textThree.setText(jsonObject.getLong("totalMileage") + "km");
            textFour.setText(jsonObject.getLong("nextMaintenanceMileage") + "km");
         //   endtime.setText(getResources().getString(R.string.health_endtime) + HashmapTojson.getTime1("yyyy-MM-dd HH:mm:ss"));
            endtime.setText(getResources().getString(R.string.health_endtime) + HashmapTojson.getDateToString(jsonObject.getLong("updateTime")*1000,"yyyy-MM-dd HH:mm:ss"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        upViewItem(jsonObject);
    }


    private void upBackgroundno(TextView textView) {
        textView.setBackgroundResource(health_yello);
        textView.setTextColor(getResources().getColor(R.color.white));
        Drawable drawable = getResources().getDrawable(R.drawable.jgx);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        textView.setCompoundDrawables(drawable, null, null, null);//画在左边  左上右下
    }

    private void upBackgroundyes(TextView textView) {
        textView.setBackgroundResource(bg_circle_rect_code);
        textView.setTextColor(getResources().getColor(R.color.text_default));
        textView.setCompoundDrawables(null, null, null, null);//画在左边  左上右下
    }

    private void upViewItem(JSONObject jsonObject) {
        try {
            //false 就是没有故障
            JSONObject j1 = jsonObject.getJSONObject("tirePressurSystem");
            JSONObject j2 = jsonObject.getJSONObject("batterySystem");
            JSONObject j3 = jsonObject.getJSONObject("chargingSystem");
            JSONObject j4 = jsonObject.getJSONObject("brakeSystem");
            JSONObject j5 = jsonObject.getJSONObject("washerFluidSystem");
            JSONObject j6 = jsonObject.getJSONObject("bodySystem");
            JSONObject j7 = jsonObject.getJSONObject("turnIndicatorSystem");

            //1
            boolean j11 = j1.getBoolean("tirePressurSysFailure");
            boolean j12 = j1.getBoolean("tireDeflation");
            if (j11) {
                j11 = false;
            } else {
                j11 = true;
            }
            if (j12) {
                j12 = false;
            } else {
                j12 = true;
            }
            if (j11 == true && j12 == true) {
                imageOne.setBackgroundResource(R.mipmap.cltj_type_true);
                hintOne.setVisibility(View.GONE);
                upBackgroundyes(one1);
                upBackgroundyes(one2);


            } else if (j11 == false || j12 == false) {
                imageOne.setBackgroundResource(R.mipmap.cltj_type_false);
                hintOne.setVisibility(View.VISIBLE);
                if (j11 == false) {
                    upBackgroundno(one1);
                } else {
                    upBackgroundyes(one1);
                }

                if (j12 == false) {
                    upBackgroundno(one2);
                } else {
                    upBackgroundyes(one2);
                }


            }

            //2
            boolean j21 = j2.getBoolean("internalFaultStatus");
            if (j21) {
                j21 = false;
            } else {
                j21 = true;
            }
            if (j21 == true) {
                iamgeTwo.setBackgroundResource(R.mipmap.cltj_type_true);
                hintTwo.setVisibility(View.GONE);
                upBackgroundyes(two1);
            } else {
                iamgeTwo.setBackgroundResource(R.mipmap.cltj_type_false);
                hintTwo.setVisibility(View.VISIBLE);
                upBackgroundno(two1);
            }


            //3
            boolean j31 = j3.getBoolean("chargingFault");
            if (j31) {
                j31 = false;
            } else {
                j31 = true;
            }
            if (j31 == true) {
                iamgeThree.setBackgroundResource(R.mipmap.cltj_type_true);
                hintThree.setVisibility(View.GONE);
                upBackgroundyes(three1);
            } else {
                iamgeThree.setBackgroundResource(R.mipmap.cltj_type_false);
                hintThree.setVisibility(View.VISIBLE);
                upBackgroundno(three1);
            }


            //4
            boolean j41 = j4.getBoolean("brakeFailure");
            boolean j42 = j4.getBoolean("antilckBrakeMalfnctn");
            boolean j43 = j4.getBoolean("brakeFluidLevelLow");
            if (j41) {
                j41 = false;
            } else {
                j41 = true;
            }
            if (j42) {
                j42 = false;
            } else {
                j42 = true;
            }
            if (j43) {
                j43 = false;
            } else {
                j43 = true;
            }
            if (j41 == true && j42 == true && j43 == true) {
                iamgeFour.setBackgroundResource(R.mipmap.cltj_type_true);
                hintFour.setVisibility(View.GONE);
                upBackgroundyes(four1);
                upBackgroundyes(four2);
                upBackgroundyes(four3);
            } else {
                iamgeFour.setBackgroundResource(R.mipmap.cltj_type_false);
                hintFour.setVisibility(View.VISIBLE);

                if (j41 == true) {
                    upBackgroundyes(four1);
                } else {
                    upBackgroundno(four1);
                }
                if (j42 == true) {
                    upBackgroundyes(four2);
                } else {
                    upBackgroundno(four2);
                }
                if (j43 == true) {
                    upBackgroundyes(four3);
                } else {
                    upBackgroundno(four3);
                }
            }

            //5
            boolean j51 = j5.getBoolean("washerFluidLevelLow");
            if (j51) {
                j51 = false;
            } else {
                j51 = true;
            }
            if (j51 == true) {
                imageFive.setBackgroundResource(R.mipmap.cltj_type_true);
                hintFive.setVisibility(View.GONE);
                upBackgroundyes(five1);
            } else {
                imageFive.setBackgroundResource(R.mipmap.cltj_type_false);
                hintFive.setVisibility(View.VISIBLE);
                upBackgroundno(five1);
            }


            //6
            boolean j61 = j6.getBoolean("airBagMalfunction");
            if (j61) {
                j61 = false;
            } else {
                j61 = true;
            }
            if (j61 == true) {
                imageSix.setBackgroundResource(R.mipmap.cltj_type_true);
                hintSix.setVisibility(View.GONE);
                upBackgroundyes(six1);
            } else {
                imageSix.setBackgroundResource(R.mipmap.cltj_type_false);
                hintSix.setVisibility(View.VISIBLE);
                upBackgroundno(six1);
            }

            //7
            boolean j71 = j7.getBoolean("turnIndicatorFailure");
            if (j71) {
                j71 = false;
            } else {
                j71 = true;
            }
            if (j71 == true) {
                imageSeven.setBackgroundResource(R.mipmap.cltj_type_true);
                hintSeven.setVisibility(View.GONE);
                upBackgroundyes(seven1);
            } else {
                imageSeven.setBackgroundResource(R.mipmap.cltj_type_false);
                hintSeven.setVisibility(View.VISIBLE);
                upBackgroundno(seven1);
            }


//            //1
//            boolean j11 = j1.getBoolean("tirePressurSysFailure");
//            boolean j12 = j1.getBoolean("tireDeflation");
//            if (j11 == true && j12 == true) {
//                imageOne.setBackgroundResource(R.mipmap.cltj_type_true);
//                hintOne.setVisibility(View.GONE);
//                Map<String, Object> map1 = new HashMap();
//                map1.put("name", "胎压系统正常");
//                map1.put("number", "1");
//                Map<String, Object> map2 = new HashMap();
//                map2.put("name", "轮胎压力正常");
//                map2.put("number", "2");
//                listOne.add((HashMap<String, Object>) map1);
//                listOne.add((HashMap<String, Object>) map2);
//            } else if (j11 == false || j12 == false) {
//                imageOne.setBackgroundResource(R.mipmap.cltj_type_false);
//                hintOne.setVisibility(View.VISIBLE);
//                Map<String, Object> map1 = new HashMap();
//                Map<String, Object> map2 = new HashMap();
//                if (j11 == false) {
//                    map1.put("name", "胎压系统故障");
//                    map1.put("number", "1");
//                } else {
//                    map1.put("name", "胎压系统正常");
//                    map1.put("number", "1");
//                }
//
//                if (j12 == false) {
//                    map2.put("name", "轮胎压力低");
//                    map2.put("number", "2");
//                } else {
//                    map2.put("name", "轮胎压力正常");
//                    map2.put("number", "2");
//                }
//
//                listOne.add((HashMap<String, Object>) map1);
//                listOne.add((HashMap<String, Object>) map2);
//
//            }
//
//            //2
//            boolean j21=j2.getBoolean("internalFaultStatus");
//            Map<String, Object> map3 = new HashMap();
//            if(j21==true){
//                iamgeTwo.setBackgroundResource(R.mipmap.cltj_type_true);
//                hintTwo.setVisibility(View.GONE);
//                map3.put("name", "电池系统正常");
//                map3.put("number", "1");
//            }else {
//                iamgeTwo.setBackgroundResource(R.mipmap.cltj_type_false);
//                hintTwo.setVisibility(View.VISIBLE);
//                map3.put("name", "电池系统故障");
//                map3.put("number", "1");
//            }
//            listTwo.add((HashMap<String, Object>) map3);
//
//            //3
//            boolean j31=j3.getBoolean("chargingFault");
//            Map<String, Object> map4 = new HashMap();
//            if(j31==true){
//                iamgeThree.setBackgroundResource(R.mipmap.cltj_type_true);
//                hintThree.setVisibility(View.GONE);
//                map4.put("name", "充电正常");
//                map4.put("number", "1");
//            }else {
//                iamgeThree.setBackgroundResource(R.mipmap.cltj_type_false);
//                hintThree.setVisibility(View.VISIBLE);
//                map4.put("name", "有充电故障");
//                map4.put("number", "1");
//            }
//            listThree.add((HashMap<String, Object>) map4);
//
//            //4
//            boolean j41=j4.getBoolean("brakeFailure");
//            boolean j42=j4.getBoolean("antilckBrakeMalfnctn");
//            boolean j43=j4.getBoolean("brakeFluidLevelLow");
//            if(j41==true&&j42==true&&j43==true){
//                iamgeFour.setBackgroundResource(R.mipmap.cltj_type_true);
//                hintFour.setVisibility(View.GONE);
//            }else {
//                iamgeFour.setBackgroundResource(R.mipmap.cltj_type_false);
//                hintFour.setVisibility(View.VISIBLE);
//
//                Map<String, Object> map41 = new HashMap();
//                Map<String, Object> map42 = new HashMap();
//                Map<String, Object> map43 = new HashMap();
//                if(j41==true){
//                    map41.put("name", "刹车系统正常");
//                    map41.put("number", "1");
//                }else {
//                    map41.put("name", "刹车系统故障");
//                    map41.put("number", "1");
//                }
//                if(j42==true){
//                    map42.put("name", "ABS系统正常");
//                    map42.put("number", "2");
//                }else {
//                    map42.put("name", "ABS系统故障");
//                    map42.put("number", "2");
//                }
//                if(j43==true){
//                    map43.put("name", "制动液zc");
//                    map43.put("number", "3");
//                }else {
//                    map43.put("name", "制动液位低");
//                    map43.put("number", "3");
//                }
//                listFour.add((HashMap<String, Object>) map41);
//                listFour.add((HashMap<String, Object>) map42);
//                listFour.add((HashMap<String, Object>) map43);
//            }
//
//            //5
//            boolean j51=j5.getBoolean("washerFluidLevelLow");
//            Map<String, Object> map51 = new HashMap();
//            if(j51==true){
//                imageFive.setBackgroundResource(R.mipmap.cltj_type_true);
//                hintFive.setVisibility(View.GONE);
//                map51.put("name", "清洗液位正常");
//                map51.put("number", "1");
//            }else {
//                imageFive.setBackgroundResource(R.mipmap.cltj_type_false);
//                hintFive.setVisibility(View.VISIBLE);
//                map51.put("name", "清洗液位低");
//                map51.put("number", "1");
//            }
//            listFive.add((HashMap<String, Object>) map51);
//
//            //6
//            boolean j61=j6.getBoolean("airBagMalfunction");
//            Map<String, Object> map61 = new HashMap();
//            if(j61==true){
//                imageSix.setBackgroundResource(R.mipmap.cltj_type_true);
//                hintSix.setVisibility(View.GONE);
//                map61.put("name", "安全气囊正常");
//                map61.put("number", "1");
//            }else {
//                imageSix.setBackgroundResource(R.mipmap.cltj_type_false);
//                hintSix.setVisibility(View.VISIBLE);
//                map61.put("name", "安全气囊故障");
//                map61.put("number", "1");
//            }
//            listSix.add((HashMap<String, Object>) map61);
//
//            //7
//            boolean j71=j7.getBoolean("turnIndicatorFailure");
//            Map<String, Object> map71 = new HashMap();
//            if(j71==true){
//                imageSeven.setBackgroundResource(R.mipmap.cltj_type_true);
//                hintSeven.setVisibility(View.GONE);
//                map71.put("name", "转向灯正常");
//                map71.put("number", "1");
//            }else {
//                imageSeven.setBackgroundResource(R.mipmap.cltj_type_false);
//                hintSeven.setVisibility(View.VISIBLE);
//                map71.put("name", "转向灯故障");
//                map71.put("number", "1");
//            }
//            listSeven.add((HashMap<String, Object>) map71);

            //getDatas();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDatas() {
        Map<String, Object> map1 = new HashMap();
        map1.put("name", "胎压系统故障");
        map1.put("number", "1");
        Map<String, Object> map2 = new HashMap();
        map2.put("name", "轮胎压力低");
        map2.put("number", "2");
        listOne.add((HashMap<String, Object>) map1);
        listOne.add((HashMap<String, Object>) map2);

        Map<String, Object> map3 = new HashMap();
        map3.put("name", "电池系统故障");
        map3.put("number", "1");
        listTwo.add((HashMap<String, Object>) map3);

        Map<String, Object> map4 = new HashMap();
        map4.put("name", "有充电故障");
        map4.put("number", "1");
        listThree.add((HashMap<String, Object>) map4);

        Map<String, Object> map41 = new HashMap();
        map41.put("name", "刹车系统故障");
        map41.put("number", "1");
        Map<String, Object> map42 = new HashMap();
        map42.put("name", "ABS系统故障");
        map42.put("number", "2");
        Map<String, Object> map43 = new HashMap();
        map43.put("name", "制动液位低");
        map43.put("number", "3");
        listFour.add((HashMap<String, Object>) map41);
        listFour.add((HashMap<String, Object>) map42);
        listFour.add((HashMap<String, Object>) map43);

        Map<String, Object> map51 = new HashMap();
        map51.put("name", "清洗液位低");
        map51.put("number", "1");
        listFive.add((HashMap<String, Object>) map51);

        Map<String, Object> map61 = new HashMap();
        map61.put("name", "安全气囊故障");
        map61.put("number", "1");
        listSix.add((HashMap<String, Object>) map61);

        Map<String, Object> map71 = new HashMap();
        map71.put("name", "转向灯故障");
        map71.put("number", "1");
        listSeven.add((HashMap<String, Object>) map71);
    }

    /**
     * http://m.codes51.com/article/detail_324330.html      加载公用布局
     *   try {
     JSONObject obj= new JSONObject(s);

     Iterator it = obj.keys();
     String vol = "";//值
     String key = null;//键
     while(it.hasNext()){//遍历JSONObject
     key = (String) it.next().toString();
     vol = obj.String(key);


     }

     } catch (JSONException e) {
     e.printStackTrace();
     }
     * */

}
