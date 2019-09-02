package com.nevs.car.activity;

import android.os.Bundle;
import android.view.View;

import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ClickUtil;

import butterknife.OnClick;

public class GetCarActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_get_car;
    }

    @Override
    public void init(Bundle savedInstanceState) {

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
