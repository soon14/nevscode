package com.nevs.car.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MyUtils;

import butterknife.BindView;
import butterknife.OnClick;

/***
 * 添加车辆
 */
public class AddBindGetActivity extends BaseActivity {
    @BindView(R.id.bind_rel)
    RelativeLayout bindRel;
    @BindView(R.id.get_rel)
    RelativeLayout getRel;
    @BindView(R.id.n_view)
    RelativeLayout nView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_add_bind_get;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
    }

    @OnClick({R.id.back, R.id.bind_rel, R.id.get_rel})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.bind_rel:
                startActivity(new Intent(this, BindCarActivity.class));
                break;
            case R.id.get_rel:
                startActivity(new Intent(this, GetCarActivity.class));
                break;
        }
    }
}
