package com.nevs.car.activity.my;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.animation.Attention.Swing;
import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nevs.car.R;
import com.nevs.car.adapter.BaseQuickAdapter;
import com.nevs.car.adapter.FixEnterAdapter;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.ServiceEalution;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.CustomLinearLayoutManager;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class FixEnterActivity extends BaseActivity {
    @BindView(R.id.text_car)
    TextView textCar;
    @BindView(R.id.text_carnumber)
    TextView textCarnumber;
    @BindView(R.id.text_type)
    TextView textType;
    @BindView(R.id.text_person)
    TextView textPerson;
    @BindView(R.id.text_phone)
    TextView textPhone;
    @BindView(R.id.text_start)
    TextView textStart;
    @BindView(R.id.text_finish)
    TextView textFinish;
    @BindView(R.id.text_guide)
    TextView textGuide;
    @BindView(R.id.recyclerone)
    RecyclerView recyclerone;
    @BindView(R.id.recyclertwo)
    RecyclerView recyclertwo;
    @BindView(R.id.recyclerthree)
    RecyclerView recyclerthree;
    @BindView(R.id.moneyalls)
    TextView moneyalls;
    @BindView(R.id.moneyshould)
    TextView moneyshould;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String ro_no = null;
    private String se_score = null;
    private String se_evalution = null;
    private CustomDialog dialog;
    private List<String> list = new ArrayList<>();
    private List<String> listPing = new ArrayList<>();
    private BaseQuickAdapter myAdapterOne;
    private BaseQuickAdapter myAdapterTwo;
    private BaseQuickAdapter myAdapterThree;
    private List<HashMap<String, Object>> listOne = new ArrayList<>();
    private List<HashMap<String, Object>> listTwo = new ArrayList<>();
    private List<HashMap<String, Object>> listThree = new ArrayList<>();
    private boolean invsone = true;
    private boolean invstwo = true;
    private boolean invsthree = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_choose_car_enter;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        ro_no = getIntent().getStringExtra("ro_no");
        MLog.e("xqro_no:" + ro_no);
        initRv();
        getHttp();
    }

    private void initRv() {
        CustomLinearLayoutManager linearLayoutManagerone = new CustomLinearLayoutManager(mContext);
        linearLayoutManagerone.setScrollEnabled(false);
        CustomLinearLayoutManager linearLayoutManagertwo = new CustomLinearLayoutManager(mContext);
        linearLayoutManagertwo.setScrollEnabled(false);
        CustomLinearLayoutManager linearLayoutManagerthree = new CustomLinearLayoutManager(mContext);
        linearLayoutManagerthree.setScrollEnabled(false);
        recyclerone.setLayoutManager(linearLayoutManagerone);
        recyclertwo.setLayoutManager(linearLayoutManagertwo);
        recyclerthree.setLayoutManager(linearLayoutManagerthree);
        myAdapterOne = new FixEnterAdapter(R.layout.item_fixenter, listOne); //设置适配器
        myAdapterOne.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        recyclerone.setAdapter(myAdapterOne);
        myAdapterTwo = new FixEnterAdapter(R.layout.item_fixenter, listTwo); //设置适配器
        myAdapterTwo.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        recyclertwo.setAdapter(myAdapterTwo);
        myAdapterThree = new FixEnterAdapter(R.layout.item_fixenter, listThree); //设置适配器
        myAdapterThree.openLoadAnimation(BaseQuickAdapter.SCALEIN);//设置加载动画
        recyclerthree.setAdapter(myAdapterThree);
    }

    public static List<HashMap<String, Object>> xJson(String json, List<HashMap<String, Object>> list, String datas) {
        list.clear();
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(json).getAsJsonObject();
        JsonObject data = jsonObj.getAsJsonObject("data");
        JsonArray jsonArray = data.getAsJsonArray(datas);
        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement el = jsonArray.get(i);
            HashMap<String, Object> tmp = gson.fromJson(el, type);
            list.add(tmp);
            // MLog.e("标题：" + list.get(i).get("newsID"));
        }
        return list;
    }

    private void getHttp() {
        DialogUtils.loading(FixEnterActivity.this, true);
        HttpRxUtils.getRepairDetails(FixEnterActivity.this,
                new String[]{"accessToken", "ro_no"},
                new Object[]{new SharedPHelper(FixEnterActivity.this).get(Constant.ACCESSTOKEN, ""),
                        ro_no
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding(FixEnterActivity.this);
                        String json = (String) s;
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            JSONObject jsondata = jsonObject.getJSONObject("data");
                            list.add(0, jsondata.getString("vin"));
                            list.add(1, jsondata.getString("license_no"));
                            list.add(2, jsondata.getString("ro_type_name"));
                            list.add(3, jsondata.getString("deliver"));
                            list.add(4, jsondata.getString("deliver_mobile"));
                            list.add(5, jsondata.getString("delivery_time"));
                            list.add(6, jsondata.getString("assign_time"));
                            list.add(7, jsondata.getString("order_by_name"));
                            list.add(8, jsondata.getString("order_by_phone"));
                            list.add(9, jsondata.getString("id"));
                            list.add(10, jsondata.getString("order_by"));
                            list.add(11, jsondata.getString("settle_amount"));
                            list.add(12, jsondata.getString("c_total_amount"));
                            MLog.e(list.size() + "CHA");

                            xJson(json, listOne, "work_houts");
                            xJson(json, listTwo, "part");
                            xJson(json, listThree, "addtion_item");

                        } catch (Exception e) {
                        }

                        //获取详情显示
                        initView();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(FixEnterActivity.this);
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
        );
    }

    private void initView() {
        textCar.setText(list.get(0));
        textCarnumber.setText(list.get(1));
        textType.setText(list.get(2));
        textPerson.setText(list.get(3));
        textPhone.setText(list.get(4));
        textStart.setText(list.get(6));
        textFinish.setText(list.get(5));
        textGuide.setText(list.get(7));
        moneyalls.setText(list.get(11) + getResources().getString(R.string.enter_money));
        moneyshould.setText(list.get(12) + getResources().getString(R.string.enter_money));
        myAdapterOne.notifyDataSetChanged();
        myAdapterTwo.notifyDataSetChanged();
        myAdapterThree.notifyDataSetChanged();
    }

    @OnClick({R.id.back, R.id.tv_mark, R.id.btn_create, R.id.rel_fixs, R.id.rel_changes, R.id.rel_rels})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_mark:
                getPing();
                break;
            case R.id.btn_create:
                DialogUtils.call(this, false, list.get(8));
                break;
            case R.id.rel_fixs:
                if (invsone) {
                    recyclerone.setVisibility(View.VISIBLE);
                    invsone = false;
                } else {
                    recyclerone.setVisibility(View.GONE);
                    invsone = true;
                }
                break;
            case R.id.rel_changes:
                if (invstwo) {
                    recyclertwo.setVisibility(View.VISIBLE);
                    invstwo = false;
                } else {
                    recyclertwo.setVisibility(View.GONE);
                    invstwo = true;
                }
                break;
            case R.id.rel_rels:
                if (invsthree) {
                    recyclerthree.setVisibility(View.VISIBLE);
                    invsthree = false;
                } else {
                    recyclerthree.setVisibility(View.GONE);
                    invsthree = true;
                }
                break;
        }
    }

    private void getPing() {
        DialogUtils.loading(FixEnterActivity.this, true);
        listPing.clear();
        HttpRxUtils.getGetServiceEalution(FixEnterActivity.this,
                new String[]{"accessToken", "ro_id", "ro_no"},
                new Object[]{new SharedPHelper(FixEnterActivity.this).get(Constant.ACCESSTOKEN, ""),
                        list.get(9), ro_no
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding(FixEnterActivity.this);
                        listPing.addAll((Collection<? extends String>) s);
                        showDialog();//弹框选择评分
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding(FixEnterActivity.this);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(FixEnterActivity.this, getResources().getString(R.string.toast_network));
                                break;
                            default:
                                ActivityUtil.showToast(FixEnterActivity.this, str);
                        }
                    }
                }
        );
    }

    private void showDialog() {
        dialog = new CustomDialog(FixEnterActivity.this);
        dialog.show();
    }

    private void getSpeak() {
        /**
         * 	ServiceEalution对象
         参数名称	类型	是否必填	说明
         u_code	字符串	是	用户ID
         se_score	字符串	是	用户评分
         se_evalution	字符串	是	用户评价
         ro_id	字符串	是	维修单ID
         order_by	字符串	是	服务经理ID
         order_by_name	字符串	是	服务经理
         ro_no	字符串	是	维修工单号
         * */
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getServiceEalution(FixEnterActivity.this,
                new String[]{"accessToken", "service_ealution"},
                new Object[]{new SharedPHelper(FixEnterActivity.this).get(Constant.ACCESSTOKEN, ""),
                        new ServiceEalution((String) new SharedPHelper(FixEnterActivity.this).get(Constant.LOGINNAME, ""),
                                se_score, se_evalution, list.get(9), list.get(10), list.get(7), ro_no)
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(FixEnterActivity.this, getResources().getString(R.string.toast_submitsuccess));
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(FixEnterActivity.this, getResources().getString(R.string.toast_submitfail));
                                break;
                            default:
                                ActivityUtil.showToast(FixEnterActivity.this, str);
                        }
                    }
                }
        );
    }

    class CustomDialog extends BottomBaseDialog {
        private RatingBar ratingBar;
        private EditText editText;
        private TextView textView;
        private TextView nevsResetting;
        private String ra = null;

        public CustomDialog(Context context) {
            super(context);
        }

        @Override
        public View onCreateView() {
            widthScale(0.85f);
            showAnim(new Swing());

            // dismissAnim(this, new ZoomOutExit());
            View inflate = View.inflate(getContext(), R.layout.dialog_custom_base, null);
            ratingBar = (RatingBar) inflate.findViewById(R.id.rabar);
            editText = (EditText) inflate.findViewById(R.id.edcontent);
            textView = (TextView) inflate.findViewById(R.id.submit);
            nevsResetting = (TextView) inflate.findViewById(R.id.nevs_resetting);
            inflate.setBackgroundDrawable(
                    CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));

            return inflate;
        }

        @Override
        public void setUiBeforShow() {
            if (listPing.size() == 2) {
                editText.setText(listPing.get(1));
                MLog.e("评分:" + listPing.get(0));
                ratingBar.setRating(Float.parseFloat(listPing.get(0)));
                textView.setVisibility(View.INVISIBLE);
                nevsResetting.setVisibility(View.INVISIBLE);
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ra == null) {
                        ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_scored));
                    } else if (editText.getText().toString().trim().length() <= 0) {
                        ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_speaks));
                    } else if (ra == null && editText.getText().toString().trim().length() <= 0) {
                        ActivityUtil.showToast(getContext(), getResources().getString(R.string.toast_scored));
                    } else {
                        se_score = ra;
                        se_evalution = editText.getText().toString().trim();
                        getSpeak();
                    }
                }
            });
            nevsResetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText.setText("");
                    ratingBar.setRating(0f);
                }
            });
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ra = Float.toString(rating);//5.0
                }
            });
        }
    }
}
