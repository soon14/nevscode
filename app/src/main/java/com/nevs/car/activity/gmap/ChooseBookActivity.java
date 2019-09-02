package com.nevs.car.activity.gmap;

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

public class ChooseBookActivity extends BaseActivity {

    @BindView(R.id.cercle_rel)
    RelativeLayout cercleRel;
    @BindView(R.id.land_rel)
    RelativeLayout landRel;
    @BindView(R.id.n_view)
    RelativeLayout nView;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_choose_book;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        ActivityManager.getInstance().addActivity(this);
    }

    @OnClick({R.id.back, R.id.cercle_rel, R.id.land_rel, R.id.notice, R.id.speak})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.cercle_rel:
                startActivity(new Intent(ChooseBookActivity.this, CircleRailActivity.class));
                break;
            case R.id.land_rel:
                startActivity(new Intent(ChooseBookActivity.this, ThirdMapActivity.class));
                break;
            case R.id.notice:
                break;
            //http://220.249.93.210:8602/web/ElectronicFence.png,http://220.249.93.210:8602/web/12345.html

            case R.id.speak:
//                Intent i = new Intent(ChooseBookActivity.this, WebBookActivity.class);
//                    i.putExtra("URL", Constant.HTTP.BANNERURL+"web/ElectronicFence.jpg");
//                    i.putExtra("TITLE",getResources().getString(R.string.geospeak));
//                startActivity(i);
                Intent i = new Intent(ChooseBookActivity.this, BookLookActivity.class);
                startActivity(i);
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
