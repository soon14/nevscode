package com.nevs.car.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.ToastUtil;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.MyToast;
import com.nevs.car.tools.util.MyUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class FixPasswordActivity extends BaseActivity {

    @BindView(R.id.old_password)
    EditText oldPassword;
    @BindView(R.id.new_password)
    EditText newPassword;
    @BindView(R.id.re_password)
    EditText rePassword;
    @BindView(R.id.n_view)
    RelativeLayout nView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_fix_password;
    }

    @Override
    public void init(Bundle savedInstanceState) {
MyUtils.setPadding(nView,mContext);
    }

    @OnClick({R.id.back, R.id.btn_create, R.id.old_password, R.id.new_password, R.id.re_password})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_create:
                getUpPassWord();
                break;
            case R.id.old_password:
                oldPassword.setCursorVisible(true);
                break;
            case R.id.new_password:
                newPassword.setCursorVisible(true);
                break;
            case R.id.re_password:
                rePassword.setCursorVisible(true);
                break;
        }
    }

    private void getUpPassWord() {
//       if(oldPassword.getText().toString().length()==0&&newPassword.getText().toString().length()==0
//               &&rePassword.getText().toString().length()==0){
//           ActivityUtil.showToast(FixPasswordActivity.this,getResources().getString(R.string.toast_create));
//       }else if(!newPassword.getText().toString().equals(rePassword.getText().toString())){
//           if(newPassword.getText().toString().length()==0){
//               MyToast.showToast(this,getResources().getString(R.string.toast_new));
//           }else if(rePassword.getText().toString().length()==0){
//               MyToast.showToast(this,getResources().getString(R.string.toast_con));
//           }else {
//               MyToast.showToast(this,getResources().getString(R.string.toast_repasswordone));
//           }
//
//       }else if(newPassword.getText().toString().length()!=0){
//           if(!Character.isLetter(newPassword.getText().toString().charAt(0))){
//               MyToast.showToast(this,getResources().getString(R.string.toast_repasswordthree));
//           }
//       }else if(newPassword.getText().toString().length()<6){
//           MyToast.showToast(this,getResources().getString(R.string.toast_repasswordtwo));
//       }else if(oldPassword.getText().toString().length()==0){
//           MyToast.showToast(this,getResources().getString(R.string.toast_old));
//       }
//       else {
//           //网络请求
//           getHttp();
//       }







        if (oldPassword.getText().toString().length() == 0) {
            MyToast.showToast(this, getResources().getString(R.string.toast_old));
        } else if (newPassword.getText().toString().length() == 0) {
            MyToast.showToast(this, getResources().getString(R.string.toast_new));
        } else if (rePassword.getText().toString().length() == 0) {
            MyToast.showToast(this, getResources().getString(R.string.toast_con));
        } else if (!newPassword.getText().toString().equals(rePassword.getText().toString()) &&
                newPassword.getText().toString().length() != 0 && rePassword.getText().toString().length() != 0) {
            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordone));
            // }else if(!MyUtils.isCEnglish(newPassword.getText().toString())&&
        }  else if (newPassword.getText().toString().length() < 6) {
            MyToast.showToast(this, getResources().getString(R.string.toast_repasswordtwo));
        } else  if(oldPassword.getText().toString().trim().equals(newPassword.getText().toString().trim())){
            MyToast.showToast(this, getResources().getString(R.string.n_unsame));
        } else {

            if (!MyUtils.isCEnglish(newPassword.getText().toString().trim())) {
                MyToast.showToast(this, getResources().getString(R.string.toast_repasswordthree));
                return;
            }

            if(!MyUtils.isContainsNum(newPassword.getText().toString().trim())){
                MyToast.showToast(this, getResources().getString(R.string.toast_repasswordnumber));
                return;
            }

            //网络请求
            getHttp();
        }
    }

    private void getHttp() {
        HttpRxUtils.getChangePassword(FixPasswordActivity.this,
                new String[]{"accessToken", "userCenterAccessToken", "nevsUserID", "oldPwd", "newPwd"},
                new Object[]{new SharedPHelper(FixPasswordActivity.this).get(Constant.ACCESSTOKEN, ""),
                        new SharedPHelper(FixPasswordActivity.this).get(Constant.REGISTCENACCESSTOKEN, ""),
                        new SharedPHelper(FixPasswordActivity.this).get(Constant.REGISTNEVSUSERID, ""),
                        oldPassword.getText().toString(),
                        newPassword.getText().toString()
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        ActivityUtil.showToast(FixPasswordActivity.this, getResources().getString(R.string.toast_fixsuccess));
                        FixPasswordActivity.this.finish();
                    }

                    @Override
                    public void onFial(String str) {
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_fixfail));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case Constant.HTTP.HTTPFAILEXITS:
                                MyUtils.exitToLongin(mContext);
                                break;
                            case "wrongOldPwd":
                                ToastUtil.showToast(mContext,getResources().getString(R.string.n_erroldpassword));
                                break;
                            default:
                                ActivityUtil.showToast(mContext, str);
                        }
                    }
                }
        );
    }
}
