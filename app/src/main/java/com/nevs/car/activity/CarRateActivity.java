package com.nevs.car.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.Pickers;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.PickerScrollView;
import com.nevs.car.tools.view.PickerScrollView.onSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarRateActivity extends BaseActivity {

    @BindView(R.id.pickerscrlllview)
    PickerScrollView pickerscrlllview;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private List<Pickers> list; // 滚动选择器数据
    private String[] id;
    private String[] name;
    private String rote = null;
    public static AnimationDrawable animationDrawable;
    private String conmondid = null;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_car_rate;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initLinstener();
        initData();
    }

    private void initData() {
        list = new ArrayList<Pickers>();
        id = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                "13", "14", "15", "16", "17", "18", "19"
        };
        name = new String[]{"50", "55", "60",
                "65", "70", "75", "80", "85", "90", "95", "100", "105", "110", "115", "120", "125", "130", "135", "140"
        };
        for (int i = 0; i < name.length; i++) {
            list.add(new Pickers(name[i], id[i]));
        }
        // 设置数据，默认选择第一条
        pickerscrlllview.setData(list);

        String o = new SharedPHelper(this).get("rote", "0").toString();
        if (o.equals("0")) {
            rote = "80";
            for (int i = 0; i < name.length; i++) {
                if (rote.equals(name[i])) {
                    pickerscrlllview.setSelected(i);
                }
            }
        } else {
            rote = new SharedPHelper(this).get("rote", "0").toString();
            for (int i = 0; i < name.length; i++) {
                if (rote.equals(name[i])) {
                    pickerscrlllview.setSelected(i);
                }
            }

        }

    }

    private void initLinstener() {
        pickerscrlllview.setOnSelectListener(new onSelectListener() {
            @Override
            public void onSelect(Pickers pickers) {
                MLog.e("选择：" + pickers.getShowId() + "--"
                        + pickers.getShowConetnt());
                rote = pickers.getShowConetnt();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.backs, R.id.start})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.backs:
                // getTsp14(false,"取消成功");
                String o = (String) new SharedPHelper(this).get("rote", "0");
                if (o.equals("0")) {
                    // ActivityUtil.showToast(this,getResources().getString(R.string.toast_nocancle));
                    finish();
                } else {
                    Intent intent = new Intent(this, LongControlActivity.class);
                    intent.putExtra("ISRATE", false);
                    intent.putExtra("rates", "0");
                    setResult(902, intent);
                    //退出第二个Activity
                    this.finish();
                }


                break;
            case R.id.start:
                //getTsp14(true,"限速成功");
                Intent reReturnIntent = new Intent(this, LongControlActivity.class);
                reReturnIntent.putExtra("ISRATE", true);
                reReturnIntent.putExtra("rates", rote);
                setResult(902, reReturnIntent);
                //退出第二个Activity
                this.finish();
                break;
        }

    }

    private void getTsp14(boolean flag, final String s) {
        /**
         *cccc:{"commandId":"358cf798a46541a4b2906d662f380088","commandStatus":"Success","resultMessage":"","resultDescription":""}
         * 空调定时cccc:{"resultMessage":"Parameter error","resultDescription":"Invalid time format!"}
         * */
        TspRxUtils.getVehiclelimiter(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "enabled", "speed", "unit"},
                new Object[]{new SharedPHelper(CarRateActivity.this).get("TSPVIN", "0").toString(), flag, 0, 0},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        conmondid = String.valueOf(obj);
                        MLog.e("comondid:" + conmondid);
                        getCommandresult(s);
                    }

                    @Override
                    public void onFial(String str) {
                        ActivityUtil.showToast(CarRateActivity.this, str);
                    }
                }
        );

    }

    private void getCommandresult(final String s) {
        TspRxUtils.getCommandresult(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(this).get(Constant.ACCESSTOKENS, "")},
                conmondid,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        controllHint(CarRateActivity.this, s);
                    }

                    @Override
                    public void onFial(String str) {
                        ActivityUtil.showToast(CarRateActivity.this, str);
                    }
                }
        );
    }

    private void controllHint(final Context context, final String str) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        alertDialog.setCancelable(false);//点击背景是对话框不会消失
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_controll_hint);//加载自定义的布局
        WindowManager.LayoutParams wm = window.getAttributes();
        wm.width = 600;//设置对话框的宽
        wm.height = 500;//设置对话框的高
        wm.alpha = 0.5f;//设置对话框的背景透明度
        wm.dimAmount = 0.6f;//遮罩层亮度
        window.setAttributes(wm);
        final ImageView imageView = (ImageView) window.findViewById(R.id.progress_bar);
        final TextView textView = (TextView) window.findViewById(R.id.text);
        final LinearLayout linearLayout = (LinearLayout) window.findViewById(R.id.dialogq);
        imageView.setBackground(context.getResources().getDrawable(R.drawable.frame));
        animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationDrawable.stop();
                imageView.setBackground(context.getResources().getDrawable(R.mipmap.finish));
                linearLayout.setBackgroundResource(R.color.main_top);
                textView.setText(str);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                        new SharedPHelper(CarRateActivity.this).put("rote", rote);
                        finish();
                    }
                }, 1100);// 延迟关闭

            }
        }, 5000);
    }
}
