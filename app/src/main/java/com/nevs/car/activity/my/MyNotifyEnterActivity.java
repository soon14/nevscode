package com.nevs.car.activity.my;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.ListUnrBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MyUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyNotifyEnterActivity extends BaseActivity {


    @BindView(R.id.lin_all)
    LinearLayout linAll;
    @BindView(R.id.imageone)
    ImageView imageone;
    @BindView(R.id.imagetwo)
    ImageView imagetwo;
    @BindView(R.id.imagethree)
    ImageView imagethree;
    @BindView(R.id.text_one)
    TextView textOne;
    @BindView(R.id.text_two)
    TextView textTwo;
    @BindView(R.id.text_three)
    TextView textThree;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String publics = "";
    private String messages = "";

    @Override
    public int getContentViewResId() {
        return R.layout.activity_my_notify_enter;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initIntent();
    }

    private void initIntent() {
        if (getIntent().getStringExtra("messageone") != null) {
            publics = getIntent().getStringExtra("messageone");
            if (!publics.equals("0")) {
                textOne.setVisibility(View.VISIBLE);
                textOne.setText(publics);
            }
        }
        if (getIntent().getStringExtra("messagetwo") != null) {
            messages = getIntent().getStringExtra("messagetwo");
            if (!messages.equals("0")) {
                textTwo.setVisibility(View.VISIBLE);
                textTwo.setText(messages);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.text_all, R.id.textViewKill, R.id.nor_one, R.id.nor_two,
            R.id.nor_three, R.id.back})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.text_all:
//                textOne.setVisibility(View.INVISIBLE);
//                textTwo.setVisibility(View.INVISIBLE);
//                textThree.setVisibility(View.INVISIBLE);
                getIsAll();
                break;
            case R.id.textViewKill:
                // linAll.setVisibility(View.GONE);
                getKill();
                break;
            case R.id.nor_one:
                if (imageone.getVisibility() == View.VISIBLE) {
                    imageone.setVisibility(View.INVISIBLE);
                } else {
                    imageone.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.nor_two:
                if (imagetwo.getVisibility() == View.VISIBLE) {
                    imagetwo.setVisibility(View.INVISIBLE);
                } else {
                    imagetwo.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.nor_three:
                if (imagethree.getVisibility() == View.VISIBLE) {
                    imagethree.setVisibility(View.INVISIBLE);
                } else {
                    imagethree.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void getIsAll() {
        List<ListUnrBean> listUn = new ArrayList<>();
//        listUn.add(new ListUnrBean(String.valueOf(lisNotice.get(position).get("un_id")),
//                "YES",
//                "NO",
//                String.valueOf(lisNotice.get(position).get("n_id"))
//        ));
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getUserNotifRe(mContext,
                new String[]{"accessToken", "list_unr"},
                new Object[]{new SharedPHelper(MyNotifyEnterActivity.this).get(Constant.ACCESSTOKEN, ""),
                        listUn
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_network));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(mContext);
                                break;
                            default:
                                ActivityUtil.showToast(mContext, str);
                        }
                    }
                }
        );
    }

    private void getKill() {
    }


}
