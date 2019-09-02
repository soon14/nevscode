package com.nevs.car.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.nevs.car.R;
import com.nevs.car.activity.ChooseCarActivity;
import com.nevs.car.adapter.MyPagerAdapter;
import com.nevs.car.fragment.son.FixFragment;
import com.nevs.car.fragment.son.KeepFragment;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class MyFixActivity extends BaseActivity {
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private int mCurrentPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_my_service;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initViewPager(); // 初始化ViewPager
        initClick();

        new SharedPHelper(mContext).put("CHANGETSPVIN", "0");
    }

    private void initClick() {
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 把当前显示的position传递出去
                mCurrentPosition = position;
                MLog.e("当前位置：" + mCurrentPosition);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.back, R.id.tv_type})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_type:
                startActivityForResult(new Intent(this, ChooseCarActivity.class), 809);
                // startActivity(new Intent(this, KeepEnterActivity.class));
                break;
        }
    }

    private void initViewPager() {
        // 创建一个集合,装填Fragment
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 装填
        fragments.add(new KeepFragment());
        fragments.add(new FixFragment());
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
        tablayout.getTabAt(0).setText(getResources().getString(R.string.keep));
        tablayout.getTabAt(1).setText(getResources().getString(R.string.service));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 809 && resultCode == 904) {
            String vin = data.getStringExtra("intentvin");
            new SharedPHelper(mContext).put("CHANGETSPVIN", vin);
            Intent intent = new Intent();
            intent.setAction("sendBroadcastviewpager" + mCurrentPosition);
            intent.putExtra("vin", vin);
            sendBroadcast(intent);

        }
    }


}
