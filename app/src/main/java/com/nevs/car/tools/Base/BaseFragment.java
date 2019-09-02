package com.nevs.car.tools.Base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lsxiao.apollo.core.Apollo;
import com.lsxiao.apollo.core.contract.ApolloBinder;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.UmengUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

;

/**
 * Created by mac on 2018/4/2.
 */

public abstract class BaseFragment extends Fragment{
    public static final String TAG = BaseFragment.class.getSimpleName();
    protected View mRootView;
    protected Unbinder mUnbinder;
    private ApolloBinder aBinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutResId(), container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        aBinder = Apollo.bind(this);
        init(savedInstanceState);
        return mRootView;
    }

    public abstract int getLayoutResId();

    public abstract void init(Bundle savedInstanceState);

    @Override
    public void onResume() {
        super.onResume();
        MLog.e("BASEfrag onResume");
        UmengUtils.onResumeToFragment(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        MLog.e("BASEfrag onPause");
        UmengUtils.onPauseToFragment(getContext());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        aBinder.unbind();
    }

}
