package com.nevs.car.activity.service;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ActivityManager;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProxyMainActivity extends BaseActivity {

    @BindView(R.id.n_view)
    RelativeLayout nView;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_proxy_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        ActivityManager.addActivity(ProxyMainActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.proxy_one, R.id.proxy_two, R.id.proxy_three, R.id.proxy_four})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.proxy_one:
                startActivity(new Intent(this, PersonAgentActivity.class));
                break;
            case R.id.proxy_two:
                startActivity(new Intent(this, NewThreadActivity.class));
                break;
            case R.id.proxy_three:
                startActivity(new Intent(this, ProxyManagerActivity.class));
                break;
            case R.id.proxy_four:
                startActivity(new Intent(this, ProxyExitActivity.class));
                break;
        }
    }
}
