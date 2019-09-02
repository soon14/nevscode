package com.nevs.car.activity.my;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ispr.uilibrary.DialogUIUtils;
import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.DatePickerView;
import com.nevs.car.tools.view.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataSettingActivity extends BaseActivity {

    @BindView(R.id.data_datas)
    TextView dataDatas;
    @BindView(R.id.id_switch)
    SwitchButton idSwitch;
    @BindView(R.id.data_percentage)
    TextView dataPercentage;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String hour = "";

    @Override
    public int getContentViewResId() {
        return R.layout.activity_data_setting;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.rel_per})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.rel_per:
                View rootViewB = View.inflate(mContext, R.layout.dialog_bottom_layout, null);
                final Dialog dialog = DialogUIUtils.showCustomBottomAlert(this, rootViewB, false, false).show();
                dialog.setCancelable(false);
                DatePickerView pickerHour = (DatePickerView) rootViewB.findViewById(R.id.pickerscrlllview);
                initTime(pickerHour);
                cancle(rootViewB, dialog);
                confirm(rootViewB, dialog);
                break;
        }
    }

    private void initTime(DatePickerView pickerHour) {
        List hours = new ArrayList();
        String id[] = new String[]{"10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};
        for (int i = 0; i < id.length; i++) {
            hours.add(id[i]);
        }
        pickerHour.setData(hours);
        pickerHour.setSelected(0);
        hour = id[0];
        pickerHour.setOnSelectListener(new DatePickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                MLog.e("选择了 " + text + "%");
                hour = text;
            }
        });
    }

    private void confirm(View rootViewB, final Dialog dialog) {
        rootViewB.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUIUtils.dismiss(dialog);
                dataPercentage.setText(hour + "%");
            }
        });
    }

    private void cancle(View rootViewB, final Dialog dialog) {
        rootViewB.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUIUtils.dismiss(dialog);
            }
        });
    }
}
