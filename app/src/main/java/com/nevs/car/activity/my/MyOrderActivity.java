package com.nevs.car.activity.my;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.nevs.car.R;
import com.nevs.car.adapter.MyPagerAdapter;
import com.nevs.car.fragment.son.HistoryOrderFragment;
import com.nevs.car.fragment.son.MyOrderFragment;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MyUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class MyOrderActivity extends BaseActivity {

    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.n_view)
    RelativeLayout nView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_my_order;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initViewPager(); // 初始化ViewPager
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


    private void initViewPager() {
        // 创建一个集合,装填Fragment
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 装填
        fragments.add(new MyOrderFragment());
        fragments.add(new HistoryOrderFragment());
        // 创建ViewPager适配器
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.setFragments(fragments);
        // 给ViewPager设置适配器
        viewpager.setAdapter(myPagerAdapter);
        // TabLayout 指示器 (记得自己手动创建2个Fragment,注意是 app包下的Fragment 还是 V4包下的 Fragment)
        tablayout.addTab(tablayout.newTab());
        tablayout.addTab(tablayout.newTab());
        // 使用 TabLayout 和 ViewPager 相关联
        tablayout.setupWithViewPager(viewpager);
        // TabLayout指示器添加文本
        tablayout.getTabAt(0).setText(getResources().getString(R.string.my_order));
        tablayout.getTabAt(1).setText(getResources().getString(R.string.history_order));

    }
}
