package com.nevs.car.activity.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.ClueBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MyToast;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.PhoneNumberUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nevs.car.R.id.location;

public class NewThreadActivity extends BaseActivity {

    @BindView(R.id.edit_sex)
    TextView editSex;
    @BindView(R.id.edit_think)
    TextView editThink;
    @BindView(R.id.edit_land)
    TextView editLand;
    private static final int REGION_REQUEST_CODE = 888;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_adress)
    EditText editAdress;
    @BindView(R.id.edit_goal)
    EditText editGoal;
    @BindView(R.id.edit_budget)
    EditText editBudget;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String sex = "";
    private String proviceid = "";
    private String cityid = "";

    @Override
    public int getContentViewResId() {
        return R.layout.activity_new_thread;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        editPhone.setInputType(EditorInfo.TYPE_CLASS_PHONE);
    }

    @OnClick({R.id.back, R.id.sex, R.id.car_type, location, R.id.next,
            R.id.edit_name, R.id.edit_phone, R.id.edit_adress, R.id.edit_goal, R.id.edit_budget
    })
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.sex:
                DialogUtils.showSex(this, editSex);
                if (editSex.getText().toString().equals(getResources().getString(R.string.nevs_boy))) {
                    sex = "M";//男
                } else {
                    sex = "W";
                }
                break;
            case R.id.car_type:
                startActivityForResult(new Intent(this, ChooseThinkCarActivity.class), 806);
                break;
            case location:
                startActivityForResult(new Intent(this, SelectCityActivity.class), REGION_REQUEST_CODE);
                break;
            case R.id.next:
                if (editName.getText().toString().trim().length() == 0 ||
                        editPhone.getText().toString().trim().length() == 0 ||
                        editSex.getText().toString().trim().length() == 0 ||
                        editThink.getText().toString().trim().length() == 0 ||
                        editLand.getText().toString().trim().length() == 0 ||
                        editAdress.getText().toString().trim().length() == 0 ||
                        editGoal.getText().toString().trim().length() == 0 ||
                        editBudget.getText().toString().trim().length() == 0
                ) {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_enterall));
                } else if (!PhoneNumberUtils.isMobileNO(editPhone.getText().toString())) {
                    MyToast.showToast(this, getResources().getString(R.string.toast_entererr));
                } else {
                    getHttp();
                }
                break;
            case R.id.edit_name:
                editName.setCursorVisible(true);
                break;
            case R.id.edit_phone:
                editPhone.setCursorVisible(true);
                break;
            case R.id.edit_adress:
                editAdress.setCursorVisible(true);
                break;
            case R.id.edit_goal:
                editGoal.setCursorVisible(true);
                break;
            case R.id.edit_budget:
                editBudget.setCursorVisible(true);
                break;
        }
    }

    private void getHttp() {
        /**
         参数名称	类型	是否必填	说明
         accessToken	字符串	是	访问令牌
         booking_no	字符串	是	预约编号
         book_end_time	字符串	是	预约进厂时间
         deliver	字符串	是	送修人
         deliver_mobile	字符串	是	送修人电话
         area	字符串	是	送修人所在地
         address	字符串	是	送修人地址
         *
         * */
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getNewClue(NewThreadActivity.this,
                new String[]{"accessToken", "clue"},
                new Object[]{new SharedPHelper(NewThreadActivity.this).get(Constant.ACCESSTOKEN, ""),
                        new ClueBean(
                                editName.getText().toString().trim(),
                                editPhone.getText().toString().trim(),
                                sex,
                                editThink.getText().toString().trim(),
                                proviceid,
                                cityid,
                                editAdress.getText().toString().trim(),
                                editGoal.getText().toString().trim(),
                                editBudget.getText().toString().trim(),
                                new SharedPHelper(NewThreadActivity.this).get(Constant.LOGINORGCODE, "").toString(),
                                new SharedPHelper(NewThreadActivity.this).get(Constant.LOGINORGCODE, "").toString()
                        )
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(NewThreadActivity.this, getResources().getString(R.string.toast_submitsuccess));
                        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGION_REQUEST_CODE && resultCode == 200) {
            String province = data.getStringExtra(SelectCityActivity.REGION_PROVINCE);
            String city = data.getStringExtra(SelectCityActivity.REGION_CITY);
            String area = data.getStringExtra(SelectCityActivity.REGION_AREA);
            editLand.setText(province + " " + city + " " + area);
            proviceid = data.getStringExtra("PROVICECODE");
            cityid = data.getStringExtra("CITYCODE");
        } else if (requestCode == 806 && resultCode == 906) {
            String thinkcar = data.getStringExtra("thinkcar");
            editThink.setText(thinkcar);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

}
