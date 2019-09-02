package com.nevs.car.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataOrderActivity extends BaseActivity {
    @BindView(R.id.n_view)
    RelativeLayout nView;
//    @BindView(R.id.single)
//    TextView single;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_data_order;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        // initSingle();
//           single.setVisibility(View.VISIBLE);
//        new SharedPHelper(mContext).put(Constant.SINGLEONE, "1");
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(MyUtils.isForeground(DataOrderActivity.this)) {
//                    single.setVisibility(View.GONE);
//                    new SharedPHelper(mContext).put(Constant.SINGLEONE, "0");
//                }
//            }
//        },10*1000);
    }

    private void initSingle() {

//        if(new SharedPHelper(mContext).get(Constant.SINGLEONE,"0").toString().equals("0")){
//            single.setVisibility(View.GONE);
//            new SharedPHelper(mContext).put(Constant.SINGLEONE,"0");
//        }else {
//            single.setVisibility(View.VISIBLE);
//            new SharedPHelper(mContext).put(Constant.SINGLEONE,"1");
//        }
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
