package com.nevs.car.activity.service;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.nevs.car.R;
import com.nevs.car.adapter.MyPagerAdapter;
import com.nevs.car.fragment.son.ProxyFailFragment;
import com.nevs.car.fragment.son.ProxyNewFragment;
import com.nevs.car.fragment.son.ProxySuccessFragment;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MyUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProxyManagerActivity extends BaseActivity {
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.n_view)
    RelativeLayout nView;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_proxy_manager;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initViewPager();
    }

    private void initViewPager() {
        // 创建一个集合,装填Fragment
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 装填
        fragments.add(new ProxyNewFragment());
        fragments.add(new ProxyFailFragment());
        fragments.add(new ProxySuccessFragment());
        // 创建ViewPager适配器
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.setFragments(fragments);
        // 给ViewPager设置适配器
        viewpager.setAdapter(myPagerAdapter);
        // TabLayout 指示器 (记得自己手动创建4个Fragment,注意是 app包下的Fragment 还是 V4包下的 Fragment)
        tablayout.addTab(tablayout.newTab());
        tablayout.addTab(tablayout.newTab());
        // 使用 TabLayout 和 ViewPager 相关联
        tablayout.setupWithViewPager(viewpager);
        // TabLayout指示器添加文本
        tablayout.getTabAt(0).setText(getResources().getString(R.string.newclient));
        tablayout.getTabAt(1).setText(getResources().getString(R.string.proxy_fail));
        tablayout.getTabAt(2).setText(getResources().getString(R.string.proxy_success));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.tv_title})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_title:
                break;
        }
    }
}