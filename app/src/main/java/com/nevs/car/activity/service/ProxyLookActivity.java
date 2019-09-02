package com.nevs.car.activity.service;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.model.ProxyallBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProxyLookActivity extends BaseActivity {
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private List<ProxyallBean> list = new ArrayList<>();
    @BindView(R.id.edit_family)
    TextView editFamily;
    @BindView(R.id.edit_phone)
    TextView editPhone;
    @BindView(R.id.edit_sex)
    TextView editSex;
    @BindView(R.id.edit_think)
    TextView editThink;
    @BindView(R.id.edit_land)
    TextView editLand;
    @BindView(R.id.edit_adress)
    TextView editAdress;
    @BindView(R.id.edit_goal)
    TextView editGoal;
    @BindView(R.id.edit_budget)
    TextView editBudget;
    @BindView(R.id.imageis)
    ImageView imageis;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_proxy_look;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initIntent();
        initView();
    }

    private void initIntent() {
        list.addAll((List<ProxyallBean>) getIntent().getSerializableExtra("proxyall"));
        MLog.e(list.size() + "内容：" + list.get(0).getCity_name());
    }

    private void initView() {
        if (list.size() != 0) {
            switch (list.get(0).getId()) {
                case 1://新建
                    imageis.setVisibility(View.GONE);
                    break;
                case 2://战败
                    imageis.setVisibility(View.VISIBLE);
                    imageis.setBackgroundResource(R.mipmap.proxyfail);
                    break;
                case 3://成功
                    imageis.setVisibility(View.VISIBLE);
                    imageis.setBackgroundResource(R.mipmap.fw_succeed);
                    break;
            }
            switch (list.get(0).getSex()) {
                case "10021001"://男
                    editSex.setText(getResources().getString(R.string.nevs_boy));
                    break;
                case "10021002":
                    editSex.setText(getResources().getString(R.string.nevs_girl));
                    break;
                default:
                    editSex.setText("");
            }
            editFamily.setText(list.get(0).getName());
            editPhone.setText(list.get(0).getPhone());
            editThink.setText(list.get(0).getVmodel());
            editLand.setText(list.get(0).getProvince_name() + list.get(0).getCity_name());
            editAdress.setText(list.get(0).getAddress());
            editGoal.setText(list.get(0).getPurpose());
            editBudget.setText(list.get(0).getBudget() + getResources().getString(R.string.nevs_wanyuan));
        }
    }


    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
