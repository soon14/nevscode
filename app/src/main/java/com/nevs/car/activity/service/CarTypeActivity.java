package com.nevs.car.activity.service;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MyUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class CarTypeActivity extends BaseActivity {

    @BindView(R.id.n_view)
    RelativeLayout nView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_car_type;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
    }

    @OnClick({R.id.back})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
