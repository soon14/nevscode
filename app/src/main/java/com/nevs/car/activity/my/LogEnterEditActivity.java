package com.nevs.car.activity.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogEnterEditActivity extends BaseActivity {

    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.remark)
    EditText remark;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String tripid = "";

    @Override
    public int getContentViewResId() {
        return R.layout.activity_log_enter_edit;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initIntent();
    }

    private void initIntent() {
        if (getIntent().getStringExtra("title") != null) {
            title.setText(getIntent().getStringExtra("title"));
            type.setText(getIntent().getStringExtra("type"));
            remark.setText(getIntent().getStringExtra("remark"));
            tripid = getIntent().getStringExtra("tripid");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.save, R.id.rel_two, R.id.title, R.id.remark})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.save:
                getTsp26();
                break;
            case R.id.rel_two:
                showDialogs();
                break;
            case R.id.title:
                title.setCursorVisible(true);
                break;
            case R.id.remark:
                remark.setCursorVisible(true);
                break;
        }
    }

    private void showDialogs() {
        DialogUtils.showPoalsTwo(this, type);
    }

    private void getTsp26() {
        /**
         *
         * */
        DialogUtils.loading(mContext, true);
        TspRxUtils.getSettag(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(LogEnterEditActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"tripId", "title", "category", "remark"},
                new Object[]{new String[]{tripid}, title.getText().toString().trim(), type.getText().toString(), remark.getText().toString().trim()},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.safesuc));
                        finishSelect();

                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_network));
                    }
                }
        );

    }


    private void finishSelect() {
        Intent data = new Intent();
        data.putExtra("title", title.getText().toString());
        data.putExtra("type", type.getText().toString());
        data.putExtra("text", remark.getText().toString());

        setResult(904, data);
        finish();
    }
}
