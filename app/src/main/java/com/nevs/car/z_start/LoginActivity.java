package com.nevs.car.z_start;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nevs.car.R;
import com.nevs.car.activity.ForgetPasswordActivity;
import com.nevs.car.activity.PrimaryActivity;
import com.nevs.car.activity.RegisterActivity;
import com.nevs.car.constant.Constant;
import com.nevs.car.model.CircleBean;
import com.nevs.car.model.PolygonBean;
import com.nevs.car.model.ReginBean;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.ShareUtil;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.AndPermissionUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DeviceUtils;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.HashmapTojson;
import com.nevs.car.tools.util.JpushUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyToast;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.PhoneNumberUtils;
import com.nevs.car.tools.view.FinancialIOSTipsDialog2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nevs.car.jnihelp.DigitalUtils.getKeyFromCRT;

/**
 * create by cc on 2018.3.3
 */
public class LoginActivity extends BaseActivity {
    @BindView(R.id.line_username)
    View lineUsername;
    @BindView(R.id.edit_username)
    EditText editUsername;
    @BindView(R.id.line_password)
    View linePassword;
    @BindView(R.id.edit_password)
    EditText editPassword;
    @BindView(R.id.text_register)
    TextView textRegister;
    @BindView(R.id.text_forget)
    TextView textForget;
    @BindView(R.id.btn_login)
    TextView btnLogin;
    @BindView(R.id.loading_lin)
    LinearLayout loadingLin;
    @BindView(R.id.privacy)
    TextView privacy;
    private FinancialIOSTipsDialog2 tipsDialog;
    private boolean needAlarm =true;//界面防劫持
    private HomeWatcherReceiver mHomeWatcherReceiver = null;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_login;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        needAlarm=true;

        getPermis();//动态获取权限
        initHint();//打开通知的提示，只提示一次
        initHome();


        initPravacy();
       // initL();//调试模式，正式环境删掉

        MLog.e("手机唯一标识：" + DeviceUtils.getUniqueId(LoginActivity.this));
        //测试TSP接口
        // getTsp28();
        MLog.e(HashmapTojson.getTime() + "");


        MLog.e("LoginActivity init");


    }


    private void  testFile(){

        final String zs="-----BEGIN CERTIFICATE-----\n" +
                "MIID8TCCAtmgAwIBAgITcAAAAPg3U7K/bVaNEQAAAAAA+DANBgkqhkiG9w0BAQsFADAPMQ0wCwYDVQQDEwRORVZTMB4XDTE5MDIyNzA1MjcyOFoXDTIwMDIyNzA1MzcyOFowSjELMAkGA1UEBhMCQ04xDTALBgNVBAoTBE5FVlMxDTALBgNVBAsTBE5FVlMxHTAbBgNVBAMTFDg5ODYwMzE4MzQyMDAzMjAzMTE4MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1yPTm5qoCoXBgFb0b7IE7fL5a5P29aSJH74Vtbwsr6IWYuH7+XK3MF1qviCsaV6AfhP8ja911CtM4WOIduaLHTJrdve7vMSRoAZ9kquiwol/Im6BdlVLv8IpC1kM60ny+XAvLg8Ec+dy3EBLL1YQtD2p9kqgd8la3PbINJDxMvJMkpkaWR/b6igjUdxTF3uyA5UTjfQkrHD2tmt1emW73CCOJPfBl5Dj+NB7vOg0WOy7qSRwIr8A4YSJvlWgzZGShO6IDZk3xLi8BxJLk8Q08k02LtE5mjmu9HI+WUJ6lteBhCVL++JTc1INySdu2uQmBGfAEp+oLIlMUdoz8SVqTwIDAQABo4IBCTCCAQUwHQYDVR0OBBYEFHOHqQsMvZlGAJY9J16Cs5fHW8kMMB8GA1UdIwQYMBaAFNgVNKguzPzi25FeE/FYUTqQHx15MDYGA1UdHwQvMC0wK6ApoCeGJWZpbGU6Ly8vL0NBU2VydmVyL0NlcnRFbnJvbGwvTkVWUy5jcmwwSgYIKwYBBQUHAQEEPjA8MDoGCCsGAQUFBzAChi5maWxlOi8vLy9DQVNlcnZlci9DZXJ0RW5yb2xsL0NBU2VydmVyX05FVlMuY3J0MD8GCSsGAQQBgjcUAgQyHjAASQBQAFMARQBDAEkAbgB0AGUAcgBtAGUAZABpAGEAdABlAE8AZgBmAGwAaQBuAGUwDQYJKoZIhvcNAQELBQADggEBAHd9vmdjQnt+CnM7dJ55FEfZFcdFgyGVGmhYd6WRhqsAuIh2EGVKNEvLj2Of0CGuuM3roSLYiLm14N9ehztkiAf+41Oye9m9qjgKYlSzElccbtK4AdzKxgVInJLFs4mDlxrmhYUnLLIsfe4oJ/VC2suKEII83XEloYQNXFkP6wbXMMHKZc2FCCcztQR4OwLks9TaCJ9a7Zs+Iyr+cmpImZxcZsAFdd5oOLokGKzIbPEE+n9E9Ay+n8Ft3zzsUszBJMc/fKZPkALpBEzoc2Xe4Eh6jnj44Vmhl30zixHmkz2nEfPRT0+sHh6ilqFjlqtxpTyJXlwVz+OyKJiW0guYhus=\n" +
                "-----END CERTIFICATE-----";
        new Thread(new Runnable() {
            @Override
            public void run() {
                //解密证书
                String fileName=Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "L01")+Constant.FILEPUBZS;
                MyUtils.initData(MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR,fileName,zs);
                String pubKey="-----BEGIN PUBLIC KEY-----\n"+getKeyFromCRT(mContext,MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR+fileName)+"\n-----END PUBLIC KEY-----";
                MLog.e("pubKey回调"+pubKey);
                // 存入文件
                MyUtils.initData(MyUtils.getStoragePath(mContext,false)+Constant.MYNEVSCAR,Constant.CSRHEAD+new SharedPHelper(mContext).get("TSPVIN", "0")+Constant.FILEPUBKey,pubKey);
            }
        }).start();
    }

    private void initHome() {
        mHomeWatcherReceiver = new HomeWatcherReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeWatcherReceiver, filter);
        if(tipsDialog!=null){
            tipsDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_HOME && event.getRepeatCount() == 0 && tipsDialog.isShowing()){
                        // TODO dialog弹出期间用户点击了 home 键
                        // Toast.makeText(this,"点击 Home 键",Toast.LENGTH_SHORT).show();
                        needAlarm=false;
                        MLog.e("点击HOMEd");
                        return true;

                    }
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Toast.makeText(MainActivity.this, "返回键无效", Toast.LENGTH_SHORT).show();
            needAlarm=false;
            MLog.e("点击BACK");
          //  return false;//return true;拦截事件传递,从而屏蔽back键。
        }

        return super.onKeyDown(keyCode, event);
    }

    public void getPermis() {
       new AndPermissionUtil().applyPermisson(LoginActivity.this);
       // PermissionUtil.checkPermission(LoginActivity.this);
    }

    public class HomeWatcherReceiver extends BroadcastReceiver {
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (TextUtils.equals(intentAction, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (TextUtils.equals(SYSTEM_DIALOG_REASON_HOME_KEY, reason)) {
                    //TODO 用户点击了 home 键
                   // Toast.makeText(this,"点击 Home 键",Toast.LENGTH_SHORT).show();
                    needAlarm=false;
                    MLog.e("点击home");
                }else if(TextUtils.equals("recentapps", reason)){
                    needAlarm=false;
                    MLog.e("长按home");
                }
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        needAlarm=true;
    }

    @Override
    protected void onPause() {
// 若程序进入后台不是用户自身造成的，则需要弹出警示
        if (needAlarm) {
// 弹出警示信息
            ActivityUtil.showLongToast(getApplicationContext(),
                    getResources().getString(R.string.safeui));
        }
        super.onPause();

    }



    private void initHint() {
        String isFisrst = new SharedPHelper(mContext).get("isonehint","0")+"";

        if(isFisrst.equals("0")){
            showDialogKill();
        }else {

        }
       // showDialogKill();
    }
    private void showDialogKill() {
         tipsDialog = new FinancialIOSTipsDialog2(this, getResources().getString(R.string.notifyhint), "", 2, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();

                new SharedPHelper(mContext).put("isonehint","1");

                needAlarm=false;
                //跳到系统开启通知
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", MyApp.getInstance().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();
                new SharedPHelper(mContext).put("isonehint","1");
            }
        });
        tipsDialog.show();
        tipsDialog.setCancelable(false);


    }

    private void initPravacy() {
        privacy.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
        privacy.getPaint().setAntiAlias(true);//抗锯齿
      //  editUsername.setText(String.valueOf(new SharedPHelper(LoginActivity.this).get(Constant.LOGINNAME,"")));

    }
    private void initL() {
//        editUsername.setText("13555555555");13317108921 c123456
//        editPassword.setText("C1234567");//此c大写
     //   editUsername.setText("13109093390");
     //  editPassword.setText("qwerty");
//        editUsername.setText("13476059095");
//        editPassword.setText("g123456");
//        editUsername.setText("13554093709");
//        editPassword.setText("c123456");

        editUsername.setText("17671642181");
        editPassword.setText("qwerty");

//        editUsername.setText("13720156539");
//        editPassword.setText("a123456");
    }


    @OnClick({R.id.edit_username, R.id.edit_password, R.id.text_register, R.id.text_forget, R.id.btn_login,R.id.privacy})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.edit_username://用户名
                editUsername.setCursorVisible(true);
                break;
            case R.id.edit_password://密码
                editPassword.setCursorVisible(true);
                break;
            case R.id.text_register://注册
                needAlarm=false;
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.text_forget://忘记密码
                needAlarm=false;
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                break;
            case R.id.btn_login://登录按钮
              click_btn_login();
               //startActivity(new Intent(this,CarAuthorizationAddActivity.class));

//                startActivity(new Intent(this,MainActivity.class));
//                this.finish();
                break;
            case R.id.privacy:
                needAlarm=false;
                startActivity(new Intent(this,PrimaryActivity.class));

                break;
        }
    }

    private void click_btn_login() {
        if (editUsername.getText().length() <= 0) {
            MyToast.showToast(this,getResources().getString(R.string.toast_enterusername));
        } else if (editPassword.getText().length() <= 0) {
            MyToast.showToast(this,getResources().getString(R.string.toast_enterpassword));
        } else if (editUsername.getText().length() <= 0 || editPassword.getText().length() <= 0) {
            MyToast.showToast(this,getResources().getString(R.string.toast_enterusername));
        } else {
            //网路请求
            login();
        }
    }
    private void click_btn_login0() {
        if (editUsername.getText().length() <= 0) {
            MyToast.showToast(this,getResources().getString(R.string.toast_enterusername));
        } else if (editPassword.getText().length() <= 0) {
            MyToast.showToast(this,getResources().getString(R.string.toast_enterpassword));
        } else if (editUsername.getText().length() <= 0 || editPassword.getText().length() <= 0) {
            MyToast.showToast(this,getResources().getString(R.string.toast_enterusername));
        } else if (editPassword.getText().length() < 6) {
            MyToast.showToast(this,getResources().getString(R.string.toast_entererrpassword));
        } else if (!Character.isLetter(editPassword.getText().toString().charAt(0))) {
            //用char包装类中的判断字母的方法判断每一个字符
            MyToast.showToast(this,getResources().getString(R.string.toast_entererrpassword));
        } else if (!PhoneNumberUtils.isMobileNO(editUsername.getText().toString())) {
            MyToast.showToast(this,getResources().getString(R.string.toast_entererr));
        } else {
            //网路请求
            login();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPHelper sharedPHelper = new SharedPHelper(LoginActivity.this);
        sharedPHelper.put(Constant.PINISKILL, true);//在登录界面设置PIN失效，防止密码输入错误后进入不用输入PIN
        sharedPHelper.put(Constant.PINISKILLFINGER, true);//在登录界面设置指纹失效，防止录入错误后进入不用录入指纹
    }

    @Override
    protected void onDestroy() {
        hideLoding();
        if (mHomeWatcherReceiver != null) {
            try {
                unregisterReceiver(mHomeWatcherReceiver);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void login() {
            final  SharedPHelper sharedPHelper=new SharedPHelper(LoginActivity.this);
             long tmpTime = System.currentTimeMillis();
             long errorTime= (long) sharedPHelper.get(Constant.LOGINERRORTIME,0L);
              int erroFrequency= (int) sharedPHelper.get(Constant.LOGINERRORNUMBER,0);
              if(erroFrequency>3){
                  if(tmpTime-errorTime<3* 60 * 1000){
                      ActivityUtil.showToast(LoginActivity.this, getResources().getString(R.string.login_limit_prompt));
                      return;
                  }else{
                      sharedPHelper.put(Constant.LOGINERRORTIME,0L);
                      sharedPHelper.put(Constant.LOGINERRORNUMBER,0);
                  }
              }


        showLoding();
        HttpRxUtils.getLogin(
                LoginActivity.this,
                new String[]{"loginName", "pwd", "appType", "deviceID"},
                new Object[]{
                        editUsername.getText().toString().trim(),
                        editPassword.getText().toString().trim(),
                        "Android",
                        DeviceUtils.getUniqueId(LoginActivity.this)
                       },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object s) {
                      //  JpushUtil.toJpush(LoginActivity.this, String.valueOf(new SharedPHelper(LoginActivity.this).get(Constant.LOGINNAME, "")));//极光推送给个人

                        ShareUtil.storett(mContext,editPassword.getText().toString().trim(),Constant.LONGINTTS,Constant.LONGINTT);
                        //MLog.e("mima:"+ShareUtil.readtt(LoginActivity.this,Constant.LONGINTTS,Constant.LONGINTT));
                        new SharedPHelper(mContext).put(Constant.ISPLASHTO,"2");//是否成功登录过一次


                        isDelePin(String.valueOf(s));

                        needAlarm=false;
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        LoginActivity.this.finish();

                        getTsp28();//TSP消息
                        JpushUtil.setTags(mContext);//极光推送
                    }

                    @Override
                    public void onFial(String str) {
                        hideLoding();
                        if(str.contains("超时")){
                            ActivityUtil.showToast(LoginActivity.this, getResources().getString(R.string.loginout));
                            return;
                        }
                        if(str.contains("连接失败")){
                            ActivityUtil.showToast(LoginActivity.this, getResources().getString(R.string.neterrhint));
                            return;
                        }
                        sharedPHelper.put(Constant.LOGINERRORTIME,System.currentTimeMillis());
                        int erroFrequency= (int) sharedPHelper.get(Constant.LOGINERRORNUMBER,0)+1;
                        sharedPHelper.put(Constant.LOGINERRORNUMBER,erroFrequency);
                        switch (str) {
                            case Constant.HTTP.HTTPFAIL:
                                ActivityUtil.showToast(LoginActivity.this, getResources().getString(R.string.toast_loginfail));
                                break;
                            case Constant.HTTP.userNameOrPwdError:
                                ActivityUtil.showToast(LoginActivity.this, getResources().getString(R.string.usernameorpassworde));
                                break;
                            default:
                                //ActivityUtil.showToast(LoginActivity.this, str);
                                ActivityUtil.showToast(LoginActivity.this, getResources().getString(R.string.toast_loginfail));
                        }

                    }
                });
    }

    private void isDelePin(String s) {
        String json=s;
        try {
            JSONObject js=new JSONObject(json);
            JSONObject data=js.getJSONObject("data");
            String loginName=data.getString("loginName");
            if(new SharedPHelper(mContext).get(Constant.LONGINLASTNAME,"kong").toString().equals(editUsername.getText().toString())){

            }else {
                new SharedPHelper(mContext).put("TSPVIN","0");

              //  new SharedPHelper(mContext).put("pin", "abcdef");
                new SharedPHelper(mContext).put(Constant.LONGINLASTNAME,loginName);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getRegistNotify() {

    }

    public void showLoding() {
        // loadingLin.setVisibility(View.VISIBLE);
        DialogUtils.loading(this, false);
    }

    public void hideLoding() {
//        if(!LoginActivity.this.isFinishing()) {
//            loadingLin.setVisibility(View.GONE);
//        }
        DialogUtils.hidding(LoginActivity.this);
    }


    //////////////
    private void getTsp3() {
        float d = (float) 116.382248;

        MLog.e("float" + d);
        TspRxUtils.getSavePoi(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"poiId", "poiName", "longitude", "latitude", "address"},
                new Object[]{"A10", "北京", 39.941711, 116.382248, "中关村"},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        MyUtils.upLogTSO(mContext,"保存POI",String.valueOf(obj),MyUtils.getTimeNow(),MyUtils.getTimeNow(),"",MyUtils.timeStampNow()+"");

                    }

                    @Override
                    public void onFial(String str) {
                        MyUtils.upLogTSO(mContext,"保存POI",String.valueOf(str),MyUtils.getTimeNow(),MyUtils.getTimeNow(),"",MyUtils.timeStampNow()+"");

                    }
                }
        );

    }

    private void getTsp4() {
        TspRxUtils.getPoi(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE,"Bearer"+" "+new SharedPHelper(LoginActivity.this).get(Constant.ACCESSTOKENS,"")},
                0,20,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp5() {
        TspRxUtils.getDeletePoi(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"items", "poiId"},
                new Object[]{new String[]{}, "A20"},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {


                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp6() {
        /**
         * {"resultMessage":null,"resultDescription":null,"items":[{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPSBSIMULATOR002","relationType":"车主"},{"vin":"LTPSBSIMULATOR001","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPCHINATELE00123","relationType":"车主"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"授权"},{"vin":"LTPSBSIMULATOR003","relationType":"车主"}]}
         * */
        final List<HashMap<String, Object>> list = new ArrayList<>();
        TspRxUtils.getUservehicleList(this,
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        list.addAll((Collection<? extends HashMap<String, Object>>) obj);
                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }


    private void getTsp7() {
        /**
         * {"interiorTemperature":null,"exteriorTemperature":null,"airconditionStatus":null,"resultMessage":"","resultDescription":""}

         * */
        final List<Object> list = new ArrayList<>();
        TspRxUtils.getAirconditionstatus(this,
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                Constant.TSP.VIN,
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        list.addAll((Collection<?>) obj);
//                        MLog.e("充电剩余时间：" + listState.get(2));
//                        upView();
                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );
    }

    private void getTsp8() {
        TspRxUtils.getBind(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"vin"},
                new Object[]{"LTPSBSIMULATOR008"},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp9() {
        /**
         * {"resultMessage":"Service Success","resultDescription":"AuthorizeUser success"}
         * */
        TspRxUtils.getAuthorize(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"vin", "targetUserAccount", "startTime", "endTime", "permissions"},
                new Object[]{"LTPSBSIMULATOR009", "String", 0, 0, new int[]{0}},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

//    private void getTsp10() {
//        /**
//         *{"items":[{"bindingId":"ea2d093e-0077-4a22-8a40-dc949a6a509c","targetUserAccount":"String","startTime":0,"endTime":0,"permissions":[0]}],"resultMessage":"","resultDescription":""}
//         * */
//        TspRxUtils.getAuthorizeusers(this,
//                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
//                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
//                new TspRxListener() {
//                    @Override
//                    public void onSucc(Object obj) {
//
//                    }
//
//                    @Override
//                    public void onFial(String str) {
//
//                    }
//                }
//        );
//
//    }

    private void getTsp11() {
        /**
         *{"resultMessage":"Service success","resultDescription":"Revoke success"}
         * */
        TspRxUtils.getRemoke(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"items"},
                new Object[]{new String[]{"ea2d093e-0077-4a22-8a40-dc949a6a509c"}},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp12() {//
        /**
         *
         * */
        TspRxUtils.getHealth(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                "LTPSBSIMULATOR001",
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp13() {
        /**
         *{"commandId":"fd1cc708fe584a0c887b01859a8e58d1","commandStatus":"Success","resultMessage":"","resultDescription":""}
         * */
        TspRxUtils.getLock(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"vin"},
                new Object[]{"LTPSBSIMULATOR001"},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp14() {
        /**
         *cccc:{"commandId":"358cf798a46541a4b2906d662f380088","commandStatus":"Success","resultMessage":"","resultDescription":""}
         * */
        TspRxUtils.getVehiclelimiter(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"vin", "enabled", "speed", "unit"},
                new Object[]{"LTPSBSIMULATOR001", true, 0, 0},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp15() {
        /**
         *cccc:{"commandId":"aaa253740ed54e448cb3393e05dcb0ad","commandStatus":"Success","resultMessage":"","resultDescription":""}
         * */
        TspRxUtils.getFlash(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"vin"},
                new Object[]{"LTPSBSIMULATOR001"},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp16() {
        /**
         * cccc:{"commandId":"8b109f86613a48bfb1c8ce8cfff5f50a","commandStatus":"Success","resultMessage":"","resultDescription":""}
         * */
        TspRxUtils.getAircondition(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"vin"},
                new Object[]{"LTPSBSIMULATOR001"},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp17() {
        /**
         * cccc:{"commandId":"83957ba5081240d1896b2a304443c7cb","commandStatus":"Success","resultMessage":"","resultDescription":""}
         * */
        TspRxUtils.getAirconditionoff(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"vin"},
                new Object[]{"LTPSBSIMULATOR001"},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp18() {
        /**
         *cccc:{"resultMessage":"","resultDescription":""}
         * */
        TspRxUtils.getScheduleairconditioner(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"vin", "runDuration", "scheduleValue", "scheduleType"},
                new Object[]{"LTPSBSIMULATOR001", 5, "1527080556", 1},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp19() {
        /**
         *cccc:{"resultMessage":"","resultDescription":""}
         * */
        TspRxUtils.getSet(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"vin", "enabled", "geoFenceType", "circle", "region", "polygon"},
                new Object[]{"LTPSBSIMULATOR001", true, "string", new CircleBean(35.0333f, 116.7469f, 10),
                        new ReginBean("A003"), new Object[]{new PolygonBean(0, 0)}
                },
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp20() {
        /**
         *cccc:{"resultMessage":"","resultDescription":"","vin":"LTPSBSIMULATOR001","enabled":true,
         * "geoFenceType":"string","circle":{"centerPointLongitude":35.0333,"centerPointLatitude":116.7469,"radius":10},"region":{"adCode":"A003"},"polygon":[{"longitude":0.0,"latitude":0.0}]}
         * */
        TspRxUtils.getGeofence(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                "LTPSBSIMULATOR001",
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp22() {
        /**
         * cccc:{"resultMessage":"","resultDescription":""}
         * */
        TspRxUtils.getSetting(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                "LTPCHINATELE00123",
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp23() {
        /**
         * cccc:{"resultMessage":"","resultDescription":""}
         * */
        TspRxUtils.getSettingset(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"vin", "enabled"},
                new Object[]{"LTPCHINATELE00123", true},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }


    private void getTsp24() {
        /**
         *
         * */
        TspRxUtils.getHistory(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new Object[]{"LTPSBSIMULATOR001", 1l, 2l, 0, 20},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp25() {
        /**
         *500
         * */
        TspRxUtils.getDetail(this,
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                "tripid行程Id",
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp26() {
        /**
         *
         * */
        TspRxUtils.getSettag(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"tripId", "title", "category", "remark"},
                new Object[]{new String[]{"string", "string"}, "标题", "分类", "说明"},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp27() {
        /**
         *500  参数问题
         * */
        TspRxUtils.getDeletetrip(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"tripId"},
                new Object[]{new String[]{"string", "string"}},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp28() {
        /**
         *
         * */
        TspRxUtils.getRegistration(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(LoginActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"deviceId", "deviceType", "handler"},
                new Object[]{DeviceUtils.getUniqueId(LoginActivity.this),1,
                        new SharedPHelper(mContext).get("baiduchannelId","")+"-"+new SharedPHelper(mContext).get("baiduuserId","")},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        MLog.e("TSP推送注册成功");
                    }

                    @Override
                    public void onFial(String str) {
                        MLog.e("TSP推送注册失败");
                    }
                }
        );

    }

    private void getTsp29() {
        /**
         * cccc:{"resultMessage":"","resultDescription":""}
         * */
        TspRxUtils.getAlerset(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
                new String[]{"enabled"},
                new Object[]{true},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {

                    }

                    @Override
                    public void onFial(String str) {

                    }
                }
        );

    }

    private void getTsp34() {
        /**
         *返回了一个  ture
         * */
//               TspRxUtils.getCommandresult(this,
//                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
//                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, Constant.TSP.AUTHORIZATIONVALUE},
//                new String[]{"messageId", "messageId", "messageId"},
//                new Object[]{"string", "string", "2018-05-25T07:32:48.345Z"},
//                new TspRxListener() {
//                    @Override
//                    public void onSucc(Object obj) {
//
//                    }
//
//                    @Override
//                    public void onFial(String str) {
//
//                    }
//                }
//        );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //同意申请权限
                MLog.e("同意调起文件读写权限");
            } else
            {
                // 用户拒绝申请权限
                Toast.makeText(LoginActivity.this,"请同意调起文件读写权限", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
