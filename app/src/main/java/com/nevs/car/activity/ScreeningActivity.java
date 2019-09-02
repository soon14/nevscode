package com.nevs.car.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nevs.car.R.id.btn_config;

public class ScreeningActivity extends BaseActivity {

    @BindView(R.id.text_one1)
    TextView textOne1;
    @BindView(R.id.text_one2)
    TextView textOne2;
    @BindView(R.id.text_one3)
    TextView textOne3;
    @BindView(R.id.text_one4)
    TextView textOne4;
    @BindView(R.id.text_two1)
    TextView textTwo1;
    @BindView(R.id.text_two2)
    TextView textTwo2;
    @BindView(R.id.text_two3)
    TextView textTwo3;
    @BindView(R.id.text_two4)
    TextView textTwo4;
    @BindView(R.id.text_three1)
    TextView textThree1;
    @BindView(R.id.text_four1)
    TextView textFour1;
    @BindView(R.id.text_five1)
    TextView textFive1;
    @BindView(R.id.text_five2)
    TextView textFive2;
    @BindView(R.id.text_five3)
    TextView textFive3;
    @BindView(R.id.text_five4)
    TextView textFive4;
    @BindView(R.id.text_six1)
    TextView textSix1;
    @BindView(R.id.text_six2)
    TextView textSix2;
    @BindView(R.id.text_six3)
    TextView textSix3;
    @BindView(R.id.text_six4)
    TextView textSix4;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String dataone = "";
    private String datatwo = "";
    private String datathree = "";
    private String datafour = "";
    private String datafive = "";
    private String datasix = "";
    private boolean flagthree = true;
    private boolean flagfour = true;
    private String parkFee = "";
    private boolean equipmentIsFree = false;
    private int stationType = 5;
    private String payment = "";
//0：私人 1：专用 2：公用 3：其他 int

    private boolean one1 = true;
    private boolean one2 = true;
    private boolean one3 = true;
    private boolean one4 = true;
    private boolean two1 = true;
    private boolean two2 = true;
    private boolean two3 = true;
    private boolean two4 = true;
    private boolean five1 = true;
    private boolean five2 = true;
    private boolean five3 = true;
    private boolean five4 = true;
    private boolean six1 = true;
    private boolean six2 = true;
    private boolean six3 = true;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_screening;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        //   getShare();
    }

    private void getShare() {
        if ((new SharedPHelper(mContext).get("sreendataone", "") + "").length() > 0) {
            dataone = new SharedPHelper(mContext).get("sreendataone", "") + "";
            switch (dataone) {
                case "1km":
                    changefour1(textOne1, textOne2, textOne3, textOne4);
                    break;
                case "5km":
                    changefour2(textOne1, textOne2, textOne3, textOne4);
                    break;
                case "10km":
                    changefour3(textOne1, textOne2, textOne3, textOne4);
                    break;
                case "20km":
                    changefour4(textOne1, textOne2, textOne3, textOne4);
                    break;
            }
        }
        if ((new SharedPHelper(mContext).get("sreendatatwo", "") + "").length() > 0) {
            datatwo = new SharedPHelper(mContext).get("sreendatatwo", "") + "";
            switch (datatwo) {
                case "私家乘用车":
                    changefour1(textTwo1, textTwo2, textTwo3, textTwo4);
                    break;
                case "出租车":
                    changefour2(textTwo1, textTwo2, textTwo3, textTwo4);
                    break;
                case "物流车":
                    changefour3(textTwo1, textTwo2, textTwo3, textTwo4);
                    break;
                case "大巴":
                    changefour4(textTwo1, textTwo2, textTwo3, textTwo4);
                    break;
            }

        }

        if ((new SharedPHelper(mContext).get("sreendatathree", "") + "").length() > 0) {
            datathree = new SharedPHelper(mContext).get("sreendatathree", "") + "";
            changeone(flagthree, textThree1, 3);
        }

        if ((new SharedPHelper(mContext).get("sreendatafour", "") + "").length() > 0) {
            datafour = new SharedPHelper(mContext).get("sreendatafour", "") + "";
            changeone(flagfour, textFour1, 4);
        }

        if ((new SharedPHelper(mContext).get("sreendatafive", "") + "").length() > 0) {
            datafive = new SharedPHelper(mContext).get("sreendatafive", "") + "";
            switch (datafive) {
                case "0":
                    changefour1(textFive1, textFive2, textFive3, textFive4);
                    break;
                case "1":
                    changefour2(textFive1, textFive2, textFive3, textFive4);
                    break;
                case "2":
                    changefour3(textFive1, textFive2, textFive3, textFive4);
                    break;
                case "3":
                    changefour4(textFive1, textFive2, textFive3, textFive4);
                    break;
            }
        }

        if ((new SharedPHelper(mContext).get("sreendatasix", "") + "").length() > 0) {
            datasix = new SharedPHelper(mContext).get("sreendatasix", "") + "";
            switch (datasix) {
                case "刷卡":
                    changefour1(textSix1, textSix2, textSix3, textSix4);
                    break;
                case "线上":
                    changefour2(textSix1, textSix2, textSix3, textSix4);
                    break;
                case "现金":
                    changefour3(textSix1, textSix2, textSix3, textSix4);
                    break;
            }
        }
    }

    private void deleteall() {
        textOne1.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textOne1.setTextColor(getResources().getColor(R.color.text_default));
        textOne2.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textOne2.setTextColor(getResources().getColor(R.color.text_default));
        textOne3.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textOne3.setTextColor(getResources().getColor(R.color.text_default));
        textOne4.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textOne4.setTextColor(getResources().getColor(R.color.text_default));
        textTwo1.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textTwo1.setTextColor(getResources().getColor(R.color.text_default));
        textTwo2.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textTwo2.setTextColor(getResources().getColor(R.color.text_default));
        textTwo3.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textTwo3.setTextColor(getResources().getColor(R.color.text_default));
        textTwo4.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textTwo4.setTextColor(getResources().getColor(R.color.text_default));
        textThree1.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textThree1.setTextColor(getResources().getColor(R.color.text_default));
        textFour1.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textFour1.setTextColor(getResources().getColor(R.color.text_default));
        textFive1.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textFive1.setTextColor(getResources().getColor(R.color.text_default));
        textFive2.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textFive2.setTextColor(getResources().getColor(R.color.text_default));
        textFive3.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textFive3.setTextColor(getResources().getColor(R.color.text_default));
        textFive4.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textFive4.setTextColor(getResources().getColor(R.color.text_default));
        textSix1.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textSix1.setTextColor(getResources().getColor(R.color.text_default));
        textSix2.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textSix2.setTextColor(getResources().getColor(R.color.text_default));
        textSix3.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textSix3.setTextColor(getResources().getColor(R.color.text_default));
        textSix4.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textSix4.setTextColor(getResources().getColor(R.color.text_default));
        flagthree = true;
        flagfour = true;
        dataone = "";
        datatwo = "";
        datathree = "";
        datafour = "";
        datafive = "";
        datasix = "";

        one1 = true;
        one2 = true;
        one3 = true;
        one4 = true;
        two1 = true;
        two2 = true;
        two3 = true;
        two4 = true;
        five1 = true;
        five2 = true;
        five3 = true;
        five4 = true;
        six1 = true;
        six2 = true;
        six3 = true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, btn_config, R.id.right_image, R.id.text_one1, R.id.text_one2, R.id.text_one3, R.id.text_one4, R.id.text_two1, R.id.text_two2, R.id.text_two3, R.id.text_two4, R.id.text_three1, R.id.text_four1, R.id.text_five1, R.id.text_five2, R.id.text_five3, R.id.text_five4, R.id.text_six1, R.id.text_six2, R.id.text_six3})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_image:
                deleteall();
                break;
            case btn_config:
                setShare();
                finishSelect();
                break;
            case R.id.text_one1:
                changefour1(textOne1, textOne2, textOne3, textOne4);
                dataone = "1km";
                MLog.e(dataone);
                if (one1) {
                    textOne1.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textOne1.setTextColor(getResources().getColor(R.color.white));
                    one1 = false;
                } else {
                    textOne1.setTextColor(getResources().getColor(R.color.text_default));
                    textOne1.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    one1 = true;
                    dataone = "";
                    MLog.e("data" + dataone);
                }
                break;
            case R.id.text_one2:
                changefour2(textOne1, textOne2, textOne3, textOne4);
                dataone = "5km";
                MLog.e(dataone);
                if (one2) {
                    textOne2.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textOne2.setTextColor(getResources().getColor(R.color.white));
                    one2 = false;
                } else {
                    textOne2.setTextColor(getResources().getColor(R.color.text_default));
                    textOne2.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    one2 = true;
                    dataone = "";
                    MLog.e("data" + dataone);
                }
                break;
            case R.id.text_one3:
                changefour3(textOne1, textOne2, textOne3, textOne4);
                dataone = "10km";
                MLog.e(dataone);
                if (one3) {
                    textOne3.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textOne3.setTextColor(getResources().getColor(R.color.white));
                    one3 = false;
                } else {
                    textOne3.setTextColor(getResources().getColor(R.color.text_default));
                    textOne3.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    one3 = true;
                    dataone = "";
                    MLog.e("data" + dataone);
                }
                break;
            case R.id.text_one4:
                changefour4(textOne1, textOne2, textOne3, textOne4);
                dataone = "20km";
                MLog.e(dataone);
                if (one4) {
                    textOne4.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textOne4.setTextColor(getResources().getColor(R.color.white));
                    one4 = false;
                } else {
                    textOne4.setTextColor(getResources().getColor(R.color.text_default));
                    textOne4.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    one4 = true;
                    dataone = "";
                    MLog.e("data" + dataone);
                }
                break;
            case R.id.text_two1:
                changefour1(textTwo1, textTwo2, textTwo3, textTwo4);
                datatwo = "私家乘用车";
                MLog.e(datatwo);
                if (two1) {
                    textTwo1.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textTwo1.setTextColor(getResources().getColor(R.color.white));
                    two1 = false;
                } else {
                    textTwo1.setTextColor(getResources().getColor(R.color.text_default));
                    textTwo1.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    two1 = true;
                    datatwo = "";
                    MLog.e("data" + datatwo);
                }
                break;
            case R.id.text_two2:
                changefour2(textTwo1, textTwo2, textTwo3, textTwo4);
                datatwo = "出租车";
                MLog.e(datatwo);
                if (two2) {
                    textTwo2.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textTwo2.setTextColor(getResources().getColor(R.color.white));
                    two2 = false;
                } else {
                    textTwo2.setTextColor(getResources().getColor(R.color.text_default));
                    textTwo2.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    two2 = true;
                    datatwo = "";
                    MLog.e("data" + datatwo);
                }
                break;
            case R.id.text_two3:
                changefour3(textTwo1, textTwo2, textTwo3, textTwo4);
                datatwo = "物流车";
                MLog.e(datatwo);
                if (two3) {
                    textTwo3.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textTwo3.setTextColor(getResources().getColor(R.color.white));
                    two3 = false;
                } else {
                    textTwo3.setTextColor(getResources().getColor(R.color.text_default));
                    textTwo3.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    two3 = true;
                    datatwo = "";
                    MLog.e("data" + datatwo);
                }
                break;
            case R.id.text_two4:
                changefour4(textTwo1, textTwo2, textTwo3, textTwo4);
                datatwo = "大巴";
                MLog.e(datatwo);
                if (two4) {
                    textTwo4.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textTwo4.setTextColor(getResources().getColor(R.color.white));
                    two4 = false;
                } else {
                    textTwo4.setTextColor(getResources().getColor(R.color.text_default));
                    textTwo4.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    two4 = true;
                    datatwo = "";
                    MLog.e("data" + datatwo);
                }
                break;
            case R.id.text_three1:
                changeone(flagthree, textThree1, 3);
                break;
            case R.id.text_four1:
                changeone(flagfour, textFour1, 4);
                break;
            case R.id.text_five1:
                changefour1(textFive1, textFive2, textFive3, textFive4);
                datafive = "0";
                MLog.e(datafive);
                if (five1) {
                    textFive1.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textFive1.setTextColor(getResources().getColor(R.color.white));
                    five1 = false;
                } else {
                    textFive1.setTextColor(getResources().getColor(R.color.text_default));
                    textFive1.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    five1 = true;
                    datafive = "";
                    MLog.e("data" + datafive);
                }
                break;
            case R.id.text_five2:
                changefour2(textFive1, textFive2, textFive3, textFive4);
                datafive = "1";
                MLog.e(datafive);
                if (five2) {
                    textFive2.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textFive2.setTextColor(getResources().getColor(R.color.white));
                    five2 = false;
                } else {
                    textFive2.setTextColor(getResources().getColor(R.color.text_default));
                    textFive2.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    five2 = true;
                    datafive = "";
                    MLog.e("data" + datafive);
                }
                break;
            case R.id.text_five3:
                changefour3(textFive1, textFive2, textFive3, textFive4);
                datafive = "2";
                MLog.e(datafive);
                if (five3) {
                    textFive3.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textFive3.setTextColor(getResources().getColor(R.color.white));
                    five3 = false;
                } else {
                    textFive3.setTextColor(getResources().getColor(R.color.text_default));
                    textFive3.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    five3 = true;
                    datafive = "";
                    MLog.e("data" + datafive);
                }
                break;
            case R.id.text_five4:
                changefour4(textFive1, textFive2, textFive3, textFive4);
                datafive = "3";
                MLog.e(datafive);
                if (five4) {
                    textFive4.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textFive4.setTextColor(getResources().getColor(R.color.white));
                    five4 = false;
                } else {
                    textFive4.setTextColor(getResources().getColor(R.color.text_default));
                    textFive4.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    five4 = true;
                    datafive = "";
                    MLog.e("data" + datafive);
                }
                break;
            case R.id.text_six1:
                changefour1(textSix1, textSix2, textSix3, textSix4);
                datasix = "刷卡";
                MLog.e(datasix);
                if (six1) {
                    textSix1.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textSix1.setTextColor(getResources().getColor(R.color.white));
                    six1 = false;
                } else {
                    textSix1.setTextColor(getResources().getColor(R.color.text_default));
                    textSix1.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    six1 = true;
                    datasix = "";
                    MLog.e("data" + datasix);
                }
                break;
            case R.id.text_six2:
                changefour2(textSix1, textSix2, textSix3, textSix4);
                datasix = "线上";
                MLog.e(datasix);
                if (six2) {
                    textSix2.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textSix2.setTextColor(getResources().getColor(R.color.white));
                    six2 = false;
                } else {
                    textSix2.setTextColor(getResources().getColor(R.color.text_default));
                    textSix2.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    six2 = true;
                    datasix = "";
                    MLog.e("data" + datasix);
                }
                break;
            case R.id.text_six3:
                changefour3(textSix1, textSix2, textSix3, textSix4);
                datasix = "现金";
                MLog.e(datasix);
                if (six3) {
                    textSix3.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
                    textSix3.setTextColor(getResources().getColor(R.color.white));
                    six3 = false;
                } else {
                    textSix3.setTextColor(getResources().getColor(R.color.text_default));
                    textSix3.setBackgroundResource(R.drawable.bg_circle_rect_code);
                    six3 = true;
                    datasix = "";
                    MLog.e("data" + datasix);
                }
                break;
        }
    }

    private void setShare() {
        if (dataone.length() > 0) {
            new SharedPHelper(mContext).put("sreendataone", dataone);
        }
        if (datatwo.length() > 0) {
            new SharedPHelper(mContext).put("sreendatatwo", datatwo);
        }
        if (datathree.length() > 0) {
            new SharedPHelper(mContext).put("sreendatathree", datathree);
        }
        if (datafour.length() > 0) {
            new SharedPHelper(mContext).put("sreendatafour", datafour);
        }
        if (datafive.length() > 0) {
            new SharedPHelper(mContext).put("sreendatafive", datafive);
        }
        if (datasix.length() > 0) {
            new SharedPHelper(mContext).put("sreendatasix", datasix);
        }
    }

    private void finishSelect() {
// //station?Longitude=1&Langitude=1&Radius=1&ParkFee=11&EquipmentIsFree=false&StationType=1&Payment=%E4%BD%A0%E8%AF%B4"
        getDatas();
        Intent data = new Intent();
        data.putExtra("equipmentIsFree", equipmentIsFree);
        data.putExtra("stationType", stationType);
        data.putExtra("payment", payment);
        data.putExtra("parkFee", parkFee);
        setResult(8002, data);
        finish();
    }

    private void getDatas() {
        if (datafour.equals("")) {
            equipmentIsFree = false;
        } else {
            equipmentIsFree = true;
        }
        if (datafive.equals("")) {
            stationType = 5;
        } else {
            switch (datafive) {
                case "0":
                    stationType = 0;
                    break;
                case "1":
                    stationType = 1;
                    break;
                case "2":
                    stationType = 2;
                    break;
                case "3":
                    stationType = 3;
                    break;
            }
        }
        if (datathree.equals("")) {
            parkFee = "";
        } else {
            try {
                parkFee = URLDecoder.decode("免费停车", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (datasix.equals("")) {
            payment = "";
        } else {
            try {
                payment = URLDecoder.decode(datasix, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeone(boolean falg, TextView textView, int aa) {
        if (falg) {
            textView.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
            textView.setTextColor(getResources().getColor(R.color.white));
            switch (aa) {
                case 3:
                    datathree = "免费停车";
                    MLog.e(datathree);
                    flagthree = false;
                    break;
                case 4:
                    datafour = "只看空闲";
                    MLog.e(datafour);
                    flagfour = false;
                    break;
            }
        } else {
            textView.setBackgroundResource(R.drawable.bg_circle_rect_code);
            textView.setTextColor(getResources().getColor(R.color.text_default));
            switch (aa) {
                case 3:
                    datathree = "";
                    MLog.e(datathree + "空");
                    flagthree = true;
                    break;
                case 4:
                    datafour = "";
                    MLog.e(datafour + "空");
                    flagfour = true;
                    break;
            }
        }
    }

    private void changefour1(TextView textView1, TextView textView2, TextView textView3, TextView textView4) {
        textView1.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
        textView1.setTextColor(getResources().getColor(R.color.white));
        textView2.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textView2.setTextColor(getResources().getColor(R.color.text_default));
        textView3.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textView3.setTextColor(getResources().getColor(R.color.text_default));
        textView4.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textView4.setTextColor(getResources().getColor(R.color.text_default));
    }

    private void changefour2(TextView textView1, TextView textView2, TextView textView3, TextView textView4) {
        textView2.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
        textView2.setTextColor(getResources().getColor(R.color.white));
        textView1.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textView1.setTextColor(getResources().getColor(R.color.text_default));
        textView3.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textView3.setTextColor(getResources().getColor(R.color.text_default));
        textView4.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textView4.setTextColor(getResources().getColor(R.color.text_default));
    }

    private void changefour3(TextView textView1, TextView textView2, TextView textView3, TextView textView4) {
        textView3.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
        textView3.setTextColor(getResources().getColor(R.color.white));
        textView2.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textView2.setTextColor(getResources().getColor(R.color.text_default));
        textView1.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textView1.setTextColor(getResources().getColor(R.color.text_default));
        textView4.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textView4.setTextColor(getResources().getColor(R.color.text_default));
    }

    private void changefour4(TextView textView1, TextView textView2, TextView textView3, TextView textView4) {
        textView4.setBackgroundResource(R.drawable.bg_cicle_recteds_code);
        textView4.setTextColor(getResources().getColor(R.color.white));
        textView2.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textView2.setTextColor(getResources().getColor(R.color.text_default));
        textView3.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textView3.setTextColor(getResources().getColor(R.color.text_default));
        textView1.setBackgroundResource(R.drawable.bg_circle_rect_code);
        textView1.setTextColor(getResources().getColor(R.color.text_default));
    }


}
