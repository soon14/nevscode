package com.nevs.car.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindCarOcrActivity extends BaseActivity {

    @BindView(R.id.car_vin)
    TextView carVin;
    @BindView(R.id.car_charge)
    TextView carCharge;
    @BindView(R.id.car_number)
    TextView carNumber;
    @BindView(R.id.car_image)
    ImageView carImage;
    @BindView(R.id.n_view)
    RelativeLayout nView;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_bind_car_ocr;
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

    @OnClick({R.id.back, R.id.retake, R.id.confirm})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.retake:
                break;
            case R.id.confirm:
                break;
        }
    }
}
