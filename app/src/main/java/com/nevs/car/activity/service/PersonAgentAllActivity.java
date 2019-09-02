package com.nevs.car.activity.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.nevs.car.R;
import com.nevs.car.activity.UploadPictureActivity;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.PaPxoyBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ActivityManager;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.z_start.MyApp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PersonAgentAllActivity extends BaseActivity {

    private static final int REGION_REQUEST_CODE = 888;
    @BindView(R.id.text_sex)
    TextView textSex;
    @BindView(R.id.text_location)
    TextView textLocation;
    @BindView(R.id.text_goal)
    TextView textGoal;
    @BindView(R.id.edit_family)
    TextView editFamily;
    @BindView(R.id.edit_phone)
    TextView editPhone;
    @BindView(R.id.edit_land)
    EditText editLand;
    @BindView(R.id.edit_idnumber)
    EditText editIdnumber;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String accessToken = null;
    private List<PaPxoyBean> list = new ArrayList<>();
    private String[] mStringItems = {MyApp.getInstance().getResources().getString(R.string.nevs_boy), MyApp.getInstance().getResources().getString(R.string.nevs_girl)};
    private List<HashMap<String, Object>> listPoal = new ArrayList<>();
    private String proviceCode = "";
    private String cityCode = "";
    private String sexCode = "";
    private List<String> orgcode = new ArrayList<>();
    private String org = "";
    private String orgcodess = "";
    private String cityname = null;
    private String citynames = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_person_agent_all;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        ActivityManager.addActivity(PersonAgentAllActivity.this);
        accessToken = new SharedPHelper(PersonAgentAllActivity.this).get(Constant.ACCESSTOKEN, "").toString();
        initView();
    }

    private void initView() {
        editFamily.setText(new SharedPHelper(PersonAgentAllActivity.this).get(Constant.LOGINFAMILYNAME, "").toString() +
                new SharedPHelper(PersonAgentAllActivity.this).get(Constant.LOGINGIVENNAMME, "").toString()
        );
        editPhone.setText(new SharedPHelper(PersonAgentAllActivity.this).get(Constant.LOGINNAME, "").toString());
    }

    @OnClick({R.id.back, R.id.next, R.id.edit_location, R.id.edit_goal, R.id.choose_sex, R.id.edit_land, R.id.edit_idnumber})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.next:
//                if (textSex.getText().toString().length() == 0 || textLocation.getText().toString().length() == 0
//                        || editLand.getText().toString().length() == 0 || editIdnumber.getText().toString().length() == 0
//                        || textGoal.getText().toString().length() == 0) {
//                    ActivityUtil.showToast(this,getResources().getString(R.string.toast_enterall));
//                }else {
//                    if(ZhengZeUtils.isLegalId(editIdnumber.getText().toString().trim())){
//                        passList();
//                    }else {
//                       ActivityUtil.showToast(mContext,getResources().getString(R.string.toast_idz));
//                    }
//
//                }
                passList();
                break;
            case R.id.choose_sex:
                showSex();
                break;
            case R.id.edit_location:
                startActivityForResult(new Intent(this, SelectCityActivity.class), REGION_REQUEST_CODE);
                break;
            case R.id.edit_goal:
                //   startActivityForResult(new Intent(this, SelectCityActivity.class),800);
//                if(textLocation.getText().toString().trim().length()==0){
//                 ActivityUtil.showToast(mContext,getResources().getString(R.string.toast_chooselocation));
//                }else {
//                    getDealer();
//                }
                startActivityForResult(new Intent(this, ChooseDetailActivity.class).putExtra("cityname", cityname).putExtra("citynames", citynames), 805);
                break;
            case R.id.edit_land:
                editLand.setCursorVisible(true);
                break;
            case R.id.edit_idnumber:
                editIdnumber.setCursorVisible(true);
                break;

        }
    }

    private void showSex() {
        final NormalListDialog dialog = new NormalListDialog(PersonAgentAllActivity.this, mStringItems);
        dialog.title(getResources().getString(R.string.toast_choose))//
                .layoutAnimation(null)
                .titleBgColor(getResources().getColor(R.color.text_default))
                .show(R.style.myDialogAnim);
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                textSex.setText(mStringItems[position]);
                if (mStringItems[position].equals(getResources().getString(R.string.nevs_boy))) {
                    sexCode = "10021001";//男
                } else {
                    sexCode = "10021002";
                }
                dialog.dismiss();
            }
        });
    }

    private void getDealer() {
        /**
         * 参数名称	类型	是否必填	说明
         accessToken	字符串	是	访问令牌
         type	字符串	是	"说明获取的经销商服务站的类型：
         dealer：经销商
         service：服务站"
         pid	字符串	否	省id
         cid	字符串	是	市id//需要回传

         data":[{"name":"12312","fullname":"21321","ename":"","fullename":"","orgcode":"SX213123","orgid":"1000034","address":"123123","latitude":"","provinceid":"2012","cityid":"2194","like_phone":""}]}
         * */
        listPoal.clear();
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getDealerList(PersonAgentAllActivity.this,
                new String[]{"accessToken", "type", "pid", "cid"},
                new Object[]{new SharedPHelper(PersonAgentAllActivity.this).get(Constant.ACCESSTOKEN, ""),
                        "dealer", proviceCode, cityCode
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        listPoal.addAll((Collection<? extends HashMap<String, Object>>) obj);
                        if (listPoal.size() != 0) {
                            showPoal();
                        } else {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_nopro));
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        ActivityUtil.showToast(mContext, getResources().getString(R.string.toast_message_fail));
                    }
                }
        );
    }

    private void showPoal() {
        final String[] stringItems = new String[listPoal.size()];
        for (int i = 0; i < listPoal.size(); i++) {
            stringItems[i] = listPoal.get(i).get("name").toString();
            orgcode.add(listPoal.get(i).get("orgcode").toString());
            MLog.e("name:" + stringItems[i]);
        }
        final ActionSheetDialog dialog = new ActionSheetDialog(PersonAgentAllActivity.this, stringItems, null);
        dialog.isTitleShow(false).show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                textGoal.setText(stringItems[position]);
                org = orgcode.get(position);
                MLog.e("orgcode:" + org);
                dialog.dismiss();
            }
        });
    }

    private void passList() {
//        String key[]={"sex","local","address","certification","orgCode","accessToken"}; M = 男  W = 女
        list.add(new PaPxoyBean(sexCode, textLocation.getText().toString(),
                editLand.getText().toString().trim(), editIdnumber.getText().toString().trim(),
                orgcodess,
                accessToken));
        Intent i = new Intent(PersonAgentAllActivity.this, UploadPictureActivity.class);
        i.putExtra("papoxyList", (Serializable) list);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGION_REQUEST_CODE && resultCode == 200) {
            String province = data.getStringExtra(SelectProvinceActivity.REGION_PROVINCE);
            String city = data.getStringExtra(SelectProvinceActivity.REGION_CITY);
            String area = data.getStringExtra(SelectProvinceActivity.REGION_AREA);
            textLocation.setText(province + " " + city + " " + area);
            proviceCode = data.getStringExtra("PROVICECODE");
            cityCode = data.getStringExtra("CITYCODE");
            cityname = cityCode;
            citynames = city;
        } else if (requestCode == 800 && resultCode == 200) {
//            proviceCode=data.getStringExtra("PROVICECODE");
//            cityCode=data.getStringExtra("CITYCODE");
//            MLog.e("proviceCode"+proviceCode);
//            MLog.e("cityCode"+cityCode);
//            getDealer();//获取经销商列表--
        } else if (requestCode == 805 && resultCode == 905) {
            String name = data.getStringExtra("stopname");
            textGoal.setText(name);
            orgcodess = data.getStringExtra("orgcode");

        }
    }


}
