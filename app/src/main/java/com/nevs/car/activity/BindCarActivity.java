package com.nevs.car.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.OcrRequestParams;
import com.baidu.ocr.sdk.model.OcrResponseResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.ToastUtil;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DeviceUtils;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.FileUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.UpperCaseTransform;
import com.nevs.car.tools.view.safecode.KeyBoardDialog;
import com.nevs.car.tools.view.safecode.LoadingDialog;
import com.nevs.car.tools.view.safecode.PayPasswordView;
import com.nevs.car.tools.view.safecode.ToastUtils;
import com.nevs.car.z_start.MainActivity;
import com.nevs.car.z_start.MyApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class BindCarActivity extends BaseActivity {
    @BindView(R.id.btn_bind)
    TextView btnbind;
    @BindView(R.id.edit_vin)
    EditText editVin;
    private static final int REQUEST_CODE_VEHICLE_LICENSE = 120;
    private static final int REQUEST_CODE_BANKCARD = 100; //
    @BindView(R.id.car_vin)
    TextView carVin;
    @BindView(R.id.car_charge)
    TextView carCharge;
    @BindView(R.id.car_number)
    TextView carNumber;
    @BindView(R.id.car_image)
    ImageView carImage;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lin_one)
    LinearLayout linOne;
    @BindView(R.id.lin_two)
    LinearLayout linTwo;
    @BindView(R.id.lin_three)
    LinearLayout linThree;
    @BindView(R.id.edit_code)
    EditText editCode;
    @BindView(R.id.btn_code)
    TextView btnCode;
    @BindView(R.id.back)
    RelativeLayout back;
    @BindView(R.id.backq)
    RelativeLayout backq;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String vin = "";
    private boolean isShow = false;
    private SharedPHelper sharedPHelper;
    private KeyBoardDialog keyboard;
    protected LoadingDialog loadingDialog;
    private String total = MyApp.getInstance().getResources().getString(R.string.toast_setpin);
    private int count = 0;//记录安全码输入正确的次数两次设置成功
    //百度AI开放平台使用OAuth2.0授权调用开放API，调用API时必须在URL中带上accesss_token参数。AccessToken可用AK/SK或者授权文件的方式获得。
    private boolean hasGotToken = false; //是否已经获取到了Token
    private TimeCount timeCount;
    private String vinEdite = "";
    private String registitent = "";
    private String dialogtoBind = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_bind_car;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        sharedPHelper = new SharedPHelper(mContext);
        sharedPHelper.put(Constant.ISCANCLE, "0");
        initIntent();//是否由注册跳转
        initAccessToken();  //授权文件、安全模式
        editVin.setTransformationMethod(new UpperCaseTransform());//小写转大写
    }

    private void initIntent() {
        if (getIntent().getStringExtra("registitent") != null) {
            registitent = getIntent().getStringExtra("registitent");
        }
        if (getIntent().getStringExtra("dialogtoBind") != null) {
            dialogtoBind = getIntent().getStringExtra("dialogtoBind");
            if (dialogtoBind.equals("dialogtoBind")) {
                back.setVisibility(View.GONE);
                backq.setVisibility(View.VISIBLE);
                new SharedPHelper(mContext).put(Constant.ISCONFORM, "1");//绑车成功改掉
            }
        }
    }

    private void finishto() {
        Intent data = getIntent();
        setResult(3006, data);
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            MLog.e("onKeyDown");
            //此处写退向后台的处理
            if (dialogtoBind.equals("dialogtoBind")) {
                finishto();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //授权文件（安全模式）
    //此种身份验证方案使用授权文件获得AccessToken，缓存在本地。建议有安全考虑的开发者使用此种身份验证方式。
    private void initAccessToken() {
        OCR.getInstance(mContext).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                // 调用成功，返回AccessToken对象
                String token = accessToken.getAccessToken();
                MLog.e("token:-------->" + token);
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                try {
                    MLog.e("onError:licence方式获取token失败---->" + error.getMessage());
                    //  ActivityUtil.showToast(BindCarActivity.this, "licence方式获取token失败  " + error.getMessage());
                } catch (Exception E) {
                    MLog.e("BindCarActivity异常");
                }

            }
        }, getApplicationContext());
    }

    private void scaningLicense() {

//       Intent intent = new Intent(BindCarActivity.this, CameraActivity.class);
//        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
//                FileUtils.getSaveFile(getApplication()).getAbsolutePath());
//        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
//                CameraActivity.CONTENT_TYPE_GENERAL);
//        startActivityForResult(intent, REQUEST_CODE_VEHICLE_LICENSE);

        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtils.getSaveFile(getApplication()).getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_BANK_CARD);
        startActivityForResult(intent, REQUEST_CODE_VEHICLE_LICENSE);


    }

    private void scaningLicense0() {
        Intent intent = new Intent(BindCarActivity.this, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtils.getSaveFile(getApplication()).getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL);
        startActivityForResult(intent, REQUEST_CODE_VEHICLE_LICENSE);
    }

    @OnClick({R.id.back, R.id.backq, R.id.btn_bind, R.id.edit_vin, R.id.baidu_text, R.id.retake, R.id.confirm, R.id.edit_code, R.id.btn_bindss, R.id.btn_code})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                if (registitent.equals("registitent")) {
                    startActivity(new Intent(BindCarActivity.this, MainActivity.class));
                    finish();
                } else {
                    finish();
                }
                break;
            case R.id.backq:
                finishto();
                break;
            case R.id.btn_bind:
                //  startActivity(new Intent(this,RealNameActivity.class));
                if (editVin.getText().toString().length() == 0) {
                    ActivityUtil.showToast(this, getResources().getString(R.string.toast_henterpin));
                } else if (editVin.getText().toString().length() < 17) {
                    ActivityUtil.showToast(this, getResources().getString(R.string.toast_seenterpin));
                } else {
                    getTsp8();
                }
                break;
            case R.id.edit_vin:
                editVin.setCursorVisible(true);
                break;
            case R.id.baidu_text:
                //百度文字识别
                scaningLicense();
                break;
            case R.id.retake:
                scaningLicense();
                break;
            case R.id.confirm:
                linTwo.setVisibility(View.GONE);
                linOne.setVisibility(View.VISIBLE);
                tvTitle.setText(getResources().getString(R.string.car_bind));
                editVin.setText(vin);
                break;

            case R.id.edit_code:
                editCode.setCursorVisible(true);
                break;
            case R.id.btn_bindss:
                if (editCode.getText().toString().trim().length() == 0) {
                    ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_entercode));
                } else {
                    getBindNew();
                }
                break;
            case R.id.btn_code:
                editCode.setText("");
                getTsp8();
                break;
        }
    }


//   private void getHttp() {
//        /**
//         * 	参数名称	类型	是否必填	说明
//         accessToken	字符串	是	访问令牌
//         vin	字符串	是	VIN
//         isDefault	字符串	是	是否默认车辆
//
//         *"LFBJDBB44WJ000115"
//         * */
//        DialogUtils.loading(mContext, true);
//        HttpRxUtils.getVehicleBinding(BindCarActivity.this,
//                new String[]{"accessToken", "vin", "isDefault", "tsp_token"},
//                new Object[]{new SharedPHelper(BindCarActivity.this).get(Constant.ACCESSTOKEN, ""),
//                        editVin.getText().toString(), "Yes", "Bearer" + " " + new SharedPHelper(BindCarActivity.this).get(Constant.ACCESSTOKENS, "")
//                },
//                new HttpRxListener() {
//                    @Override
//                    public void onSucc(Object s) {
//                        DialogUtils.hidding((Activity) mContext);
//                        ActivityUtil.showToast(BindCarActivity.this, getResources().getString(R.string.toast_submitsuccess));
//                        pinDialog();//是否设置PIN
//                    }
//
//                    @Override
//                    public void onFial(String str) {
//                        DialogUtils.hidding((Activity) mContext);
//                        switch (str) {
//                            case Constant.HTTP.HTTPFAIL:
//                                ActivityUtil.showToast(BindCarActivity.this, getResources().getString(R.string.toast_network));
//                                break;
//                            default:
//                                ActivityUtil.showToast(BindCarActivity.this, str);
//                        }
//                    }
//                }
//        );
//    }

    private void getBindNew() {
        /**
         * 	参数名称	类型	是否必填	说明
         accessToken	字符串	是	访问令牌
         vin	字符串	是	VIN
         isDefault	字符串	是	是否默认车辆
         *"LFBJDBB44WJ000115"
         * */
        DialogUtils.loading(mContext, true);
        HttpRxUtils.getBindNew(BindCarActivity.this,
                new String[]{"accessToken", "vin", "verifyCode", "mobile", "tspToken", "isDefault"},
                new Object[]{new SharedPHelper(BindCarActivity.this).get(Constant.ACCESSTOKEN, ""),
                        vinEdite,
                        editCode.getText().toString(),
                        new SharedPHelper(BindCarActivity.this).get(Constant.LOGINNAME, ""),
                        "Bearer" + " " + new SharedPHelper(BindCarActivity.this).get(Constant.ACCESSTOKENS, ""),
                        "Yes"
                },

                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        DialogUtils.hidding((Activity) mContext);
                        sharedPHelper.put(Constant.TSPISCAROWER, "YES");//
                        sharedPHelper.put(Constant.ISCONFORM, "0");//   绑车成功后清零
                        dialogtoBind = "";
                        ActivityUtil.showToast(BindCarActivity.this, getResources().getString(R.string.toast_submitsuccess));

//                        if(registitent.equals("registitent")){
//                            keyboard = new KeyBoardDialog((Activity) mContext, getDecorViewDialog());
//                            keyboard.show();
//                        }else {
//                            pinDialog();//是否设置PIN
//                        }

                        startActivity(new Intent(BindCarActivity.this, SmrzIdActivity.class).putExtra("isbindto", "1"));
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
                            case "Verification code error":
                                ToastUtil.showToast(mContext,getResources().getString(R.string.n_errvincode));
                                break;
                            default:
                                ActivityUtil.showToast(mContext, str);
                        }
                    }
                }
        );
    }

    private void getTsp8() {//调成功以后TSP 会发短信验证码过来
        DialogUtils.loading(mContext, true);
        TspRxUtils.getBind(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(BindCarActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "mobile"},
                new Object[]{editVin.getText().toString().toUpperCase(),
                        new SharedPHelper(BindCarActivity.this).get(Constant.LOGINNAME, "")
                },
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "绑定车辆", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        vinEdite = editVin.getText().toString().toUpperCase();
                        upCode();
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        if (str.contains("无效的请求")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.zundatas));
                        } else if (str.contains("400")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.hint_bind_error));
                        } else if (str.contains("500") || str.contains("无效的网址")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.neterror));
                        } else if (str.contains("未授权的请求")) {
                            MyUtils.exitToLongin(mContext);
                        } else if (str.contains("401")) {
                            MyUtils.exitToLongin401(mContext);
                        } else if (str.contains("连接超时")) {
                            ActivityUtil.showToast(mContext, getResources().getString(R.string.timeout));
                        } else {
                            //  ActivityUtil.showToast(mContext, str);
                        }
                        MyUtils.upLogTSO(mContext, "绑定车辆", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                    }
                }
        );

    }

    private void getMessageCode() { //接口
        HttpRxUtils.getMessageCode(
                BindCarActivity.this,
                new String[]{"phone", "appType", "deviceID"},
                new Object[]{sharedPHelper.get(Constant.LOGINNAME, ""),
                        "Android",
                        DeviceUtils.getUniqueId(BindCarActivity.this)
                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                        List<Object> list = new ArrayList<>();
                        list = (List<Object>) s;
                        String code = String.valueOf(list.get(1));
                        MLog.e("短信验证码获取成功:" + code);
                    }

                    @Override
                    public void onFial(String str) {
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(BindCarActivity.this, getResources().getString(R.string.codefail));
                                break;
                            case Constant.HTTP.HTTPFAILEXIT:
                                MyUtils.exitToLongin(mContext);
                                break;
                            default:
                                ActivityUtil.showToast(BindCarActivity.this, getResources().getString(R.string.codefail));
                        }
                    }
                }
        );
    }

    private void upCode() {
        linOne.setVisibility(View.GONE);
        linThree.setVisibility(View.VISIBLE);
        timeCount = new TimeCount(60000, 1000);
        timeCount.start();
    }


    private void pinDialog() {
        String isHavePin = String.valueOf(new SharedPHelper(mContext).get("pin", "abcdef"));
        if (isHavePin.equals("abcdef")) {//没有pin就显示
            keyboard = new KeyBoardDialog((Activity) mContext, getDecorViewDialog());
            keyboard.show();
        } else {
            finishSelect();
        }
    }

    private void recingLicense(String filePath) {
        final Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        OcrRequestParams param = new OcrRequestParams();
        param.setImageFile(new File(filePath));
        OCR.getInstance(mContext).recognizeVehicleLicense(param, new OnResultListener<OcrResponseResult>() {
            @Override//recognizeVehicleLicense
            public void onResult(OcrResponseResult result) {
                if (result != null) {
                    Log.e("tag", "扫描行驶证成功");
                    Log.e("tag", "onResult: 扫描行驶证成功");
                    String jsonRes = result.getJsonRes();
                    Log.e("tag", "jsonings: " + jsonRes);
                    //  resultTv.setText("行驶证信息:" + jsonRes);
                    try {
                        JSONObject j = new JSONObject(jsonRes);
                        JSONObject ru = j.getJSONObject("words_result");

                        if(!ru.has("车辆识别代号")){
                            DialogUtils.hidding(BindCarActivity.this);
                            ActivityUtil.showToast(BindCarActivity.this, getResources().getString(R.string.n_spleasu));
                            return;
                        }

                        JSONObject numberj = ru.getJSONObject("号牌号码");
                        String number = numberj.getString("words");
                        Log.e("tag", "number:" + number);

                        JSONObject chargej = ru.getJSONObject("发动机号码");
                        String charge = chargej.getString("words");
                        Log.e("tag", "charge:" + charge);

                        JSONObject v = ru.getJSONObject("车辆识别代号");
                        vin = v.getString("words");
                        Log.e("tag", "vin:" + vin);


                        // iamgeCar.setImageBitmap(bitmap);
                        //editVin.setText(vin.trim());
                        carImage.setImageBitmap(bitmap);
                        carVin.setText(vin);
                        carCharge.setText(charge);
                        carNumber.setText(number);
                        DialogUtils.hidding(BindCarActivity.this);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("tag", "sdc" + e);

                    }
                } else {
                    DialogUtils.hidding(BindCarActivity.this);
                    MLog.e("result为空");
                }

            }

            @Override
            public void onError(OCRError error) {
                Log.e("tag", "onError: 扫描行驶证错误  " + error.getMessage());
                DialogUtils.hidding(BindCarActivity.this);
                ActivityUtil.showToast(BindCarActivity.this, getResources().getString(R.string.toast_zxingerror));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 识别成功回调，行驶证识别
        if (requestCode == REQUEST_CODE_VEHICLE_LICENSE && resultCode == Activity.RESULT_OK) {
            isShow = true;
            recingLicense(FileUtils.getSaveFile(getApplicationContext()).getAbsolutePath());
            MLog.e("识别成功");
        } else {
            isShow = false;
            MLog.e("识别失败");

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        if(OCR.getInstance(mContext)==null){
            return;
        }
        OCR.getInstance(mContext).release();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MLog.e("onRestart()");
        if (isShow) {
            DialogUtils.loading(this, true);
            initViews();
        }
    }

    private void initViews() {
        linOne.setVisibility(View.GONE);
        linTwo.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.toast_zxingpicture));
    }


    protected View getDecorViewDialog() {

        //0表示隐藏取消按钮
        return PayPasswordView.getInstance(0, total, mContext, new PayPasswordView.OnPayListener() {

            @Override
            public void onSurePay(final String password) {// 这里调用验证密码是否正确的请求

                // TODO Auto-generated method stub
                keyboard.dismiss();
                keyboard = null;

                initProgressDialog();
                loadingDialog.setCanceledOnTouchOutside(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        dismissProgressDialog();
                        if (count == 0) {
                            if (password.length() == 6) {
                                count++;
                                sharedPHelper.put("pinisone", password);
                                total = getResources().getString(R.string.toast_confirmpin);
                                keyboard = new KeyBoardDialog(BindCarActivity.this, getDecorViewDialog());
                                keyboard.show();

                            }
                        } else if (count == 1) {
                            if (password.equals(String.valueOf(sharedPHelper.get("pinisone", "abcdef")))) {
                                ToastUtils.showShortToast(mContext, getResources().getString(R.string.toast_pinsuccess));
                                sharedPHelper.put("pin", password);
                                sharedPHelper.put("pinisone", "abcdef");
                                new SharedPHelper(mContext).put(Constant.SAFETY,System.currentTimeMillis());
                                count = 0;
                            } else {
                                count++;
                                keyboard = new KeyBoardDialog(BindCarActivity.this, getDecorViewDialog());
                                keyboard.show();
                                ToastUtils.showShortToast(mContext, getResources().getString(R.string.toast_pininout));
                            }

                        } else {//count>=2时的情况
                            if (password.equals(String.valueOf(sharedPHelper.get("pinisone", "abcdef")))) {
                                ToastUtils.showShortToast(mContext, getResources().getString(R.string.toast_pinsuccess));
                                sharedPHelper.put("pin", password);
                                sharedPHelper.put("pinisone", "abcdef");
                                count = 0;
                                if (registitent.equals("registitent")) {
                                    startActivity(new Intent(mContext, MainActivity.class));
                                    finish();
                                } else {
                                    finishSelect();
                                }
                            } else {
                                keyboard = new KeyBoardDialog(BindCarActivity.this, getDecorViewDialog());
                                keyboard.show();
                                ToastUtils.showShortToast(mContext, getResources().getString(R.string.toast_pininout));
                            }
                        }
                    }


                }, 1000);

            }

            @Override
            public void onCancelPay() {
                // TODO Auto-generated method stub
                keyboard.dismiss();
                keyboard = null;
                count = 0;
                ToastUtils.showShortToast(mContext, "");
            }
        }).getView();
    }

    public void initProgressDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(mContext, R.style.loading_dialogone);
            loadingDialog.setText(getResources().getString(R.string.loading));
        }
        if (!BindCarActivity.this.isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog = new LoadingDialog(mContext, R.style.loading_dialogone);
            loadingDialog.setText(getResources().getString(R.string.loading));
            loadingDialog.show();
        }
        loadingDialog.setCanceledOnTouchOutside(true);
    }

    public void dismissProgressDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (btnCode != null) {
                btnCode.setClickable(false);
                btnCode.setBackgroundResource(R.drawable.bg_cicle_rected_code);
                btnCode.setText(millisUntilFinished / 1000 + getResources().getString(R.string.toast_seconds));
            }
        }

        @Override
        public void onFinish() {
            if (btnCode != null) {
                btnCode.setText(getResources().getString(R.string.toast_resent));
                btnCode.setBackgroundResource(R.drawable.bg_circle_rect_code);
                btnCode.setClickable(true);
            }
        }
    }


    private void finishSelect() {
        Intent data = new Intent();
        setResult(8006, data);
        finish();
    }
}
