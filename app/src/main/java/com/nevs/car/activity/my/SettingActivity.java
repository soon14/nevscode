package com.nevs.car.activity.my;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.activity.gmap.ChooseBookActivity;
import com.nevs.car.adapter.xrefreshview.utils.Utils;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelpers;
import com.nevs.car.tools.rx.TspRxListener;
import com.nevs.car.tools.rx.TspRxUtils;
import com.nevs.car.tools.util.ActivityUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.view.FinancialIOSTipsDialog;
import com.nevs.car.tools.view.SwitchButton;
import com.nevs.car.tools.view.safecode.KeyBoardDialog;
import com.nevs.car.tools.view.safecode.LoadingDialog;
import com.nevs.car.tools.view.safecode.PayPasswordView;
import com.nevs.car.tools.view.safecode.StringUtils;
import com.nevs.car.tools.view.safecode.ToastUtils;
import com.nevs.car.z_start.LoginActivity;
import com.nevs.car.z_start.MyApp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.switch_two)
    SwitchButton switchTwo;
    @BindView(R.id.switch_three)
    SwitchButton switchThree;
    @BindView(R.id.switch_four)
    SwitchButton switchFour;
    @BindView(R.id.push_state)
    TextView pushState;
    @BindView(R.id.settinggeo)
    RelativeLayout settinggeo;
    @BindView(R.id.isv_trip)
    RelativeLayout isvTrip;
    @BindView(R.id.isv_geo)
    RelativeLayout isvGeo;
    @BindView(R.id.isv_lin)
    RelativeLayout isvLin;
    @BindView(R.id.isv_lin2)
    RelativeLayout isvLin2;
    @BindView(R.id.sefe_setting)
    RelativeLayout sefeSetting;
    @BindView(R.id.chang_line)
    RelativeLayout changLine;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    @BindView(R.id.setting_push)
    TextView settingPush;
    private AnimationDrawable animationDrawable;
    private FinancialIOSTipsDialog tipsDialog;
    private KeyBoardDialog keyboard;
    protected LoadingDialog loadingDialog;
    private String tips = "请点击忘记密码进行找回或重试";
    private String total = "";
    private int count = 0;//记录新安全码输入正确的次数两次设置成功
    private Context context = SettingActivity.this;
    private SharedPHelpers sharedPHelpers;
    private SharedPHelper sharedPHelper;
    private boolean flagone = false;//判断消息推送
    private boolean flagtwo = false;//判断行程记录
    private boolean flagFinger = false;//判断指纹识别是否开启
    private boolean isChe = true;
    private boolean enabled = false;
    private boolean isOne = true;//行程进来的时候不调接口只获取状态
    private int trip = 0;
    private boolean isFail = true;//是否失败，如果失败就保存原状不调接口设置
    private int ii = 1;//记录安全码输入的错误次数
    private boolean isEnable = false;//判断是否开启电子围栏
    private int bb = 0;//判断是否开电子围栏接口
    private String geoFenceType = "";
    private List<Object> objectList = new ArrayList<>();
    private boolean iss = false;
    private String total0 = MyApp.getInstance().getResources().getString(R.string.toast_setpin);
    private int count0 = 0;//记录安全码输入正确的次数两次设置成功
    private List<Object> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_setting;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        total = mContext.getResources().getString(R.string.pay_title);
        MyUtils.setPadding(nView, mContext);
        sharedPHelpers = new SharedPHelpers(mContext, "c" + new SharedPHelper(mContext).get(Constant.LOGINNAME, ""));
        sharedPHelper = new SharedPHelper(mContext);
        setText();
        initNoCar();
        // sharedPHelper.put("pin","000000");


        initFinger();//初始化指纹识别


    }

    private void initNoCar() {
        if (new SharedPHelper(mContext).get("TSPVIN", "0").equals("0")) {
            isvTrip.setVisibility(View.GONE);
            isvGeo.setVisibility(View.GONE);
            isvLin.setVisibility(View.GONE);
            isvLin2.setVisibility(View.GONE);

            sefeSetting.setVisibility(View.GONE);
            changLine.setVisibility(View.GONE);

        } else {
            initDay();//初始化行程记录

            if (new SharedPHelper(mContext).get(Constant.TSPISCAROWER, "").equals("YES")) {

                initGeo();//初始化地址围栏
                iss = true;
            } else {

                isvGeo.setVisibility(View.GONE);
                isvLin2.setVisibility(View.GONE);
            }

            getTsp22();//初始化行程记录
        }
    }

    private void initGeo() {
        switchThree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (bb == 0) {
                    MLog.e("保持原状");
                    bb++;
                } else {
                    getTsp19(isChecked);
                }
            }
        });
    }


    private void initPush() {
        //   boolean flag = isNotificationEnable(mContext);
        boolean flag = isNoEnable();
        if (flag) {
            pushState.setText(getResources().getString(R.string.pushstate));
//            try {
//            Object service=getSystemService(NOTIFICATION_SERVICE);
//            if(service!=null){
//                Method expand=service.getClass().getMethod("expand");
//                expand.invoke(service);
//            }
//        } catch (Exception e) {
//        }
            // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
//            Intent intent = new Intent();
//            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            Uri uri = Uri.fromParts("package", MyApp.getInstance().getPackageName(), null);
//            intent.setData(uri);
//            startActivity(intent);

        } else {
            pushState.setText(getResources().getString(R.string.toast_stopped));
        }
    }

    private boolean isNoEnable() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(MyApp.getInstance());
        return manager.areNotificationsEnabled();
    }


    /*
     * 判断通知权限是否打开
     * AppOpsManager这个类是api 19以上才添加的，所以android4.3以下这个方法就失效了。
     */
    @SuppressLint("NewApi")
    private boolean isNotificationEnable(Context context) {
        AppOpsManager mAppOps = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAppOps = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);
        }
        ApplicationInfo appInfo = context.getApplicationInfo();

        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null; /* Context.APP_OPS_MANAGER */

        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
            int value = (int) opPostNotificationValue.get(Integer.class);
            return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    private void getTsp22() {
        /**
         * cccc:{"resultMessage":"","resultDescription":""}
         * */
        DialogUtils.loading(mContext, true);
        TspRxUtils.getSetting(this,
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(SettingActivity.this).get(Constant.ACCESSTOKENS, "")},
                new SharedPHelper(SettingActivity.this).get("TSPVIN", "0").toString(),
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "获取行程记录开启状态", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        enabled = (boolean) obj;
                        switchTwo.setChecked(enabled);
                        if (enabled == false) {
                            isOne = false;
                        }
                        if (iss) {
                            getTsp20();
                        }


                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        //switchTwo.setChecked(false);
                        isOne = false;
                        if (iss) {
                            getTsp20();
                        }

                    }
                }
        );

    }

    private void getTsp23(final boolean flag) {
        /**
         * cccc:{"resultMessage":"","resultDescription":""}
         * */
        DialogUtils.loading(mContext, true);
        TspRxUtils.getSettingset(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(SettingActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "enabled"},
                new Object[]{new SharedPHelper(SettingActivity.this).get("TSPVIN", "0").toString(), flag},
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "设置行程记录", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        isFail = true;
                        if (flag) {
                            if (trip == 0) {
                                trip++;
                            } else {
                                ActivityUtil.showToast(SettingActivity.this, getResources().getString(R.string.trip_opens));
                            }

                            //  switchTwo.setChecked(true);
                        } else {
                            if (trip == 0) {
                                trip++;
                            } else {
                                ActivityUtil.showToast(SettingActivity.this, getResources().getString(R.string.trip_closes));
                            }

                            // switchTwo.setChecked(false);
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "设置行程记录", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        isFail = false;
                        if (flag) {
                            if (trip == 0) {
                                trip++;
                            } else {
                                // ActivityUtil.showToast(SettingActivity.this, getResources().getString(R.string.trip_openf));
                                ActivityUtil.showToast(SettingActivity.this, getResources().getString(R.string.hint_trip_setfail));
                                return;
                            }

                            //  switchTwo.setChecked(false);
                        } else {
                            if (trip == 0) {
                                trip++;
                            } else {
                                // ActivityUtil.showToast(SettingActivity.this, getResources().getString(R.string.trip_closef));
                                ActivityUtil.showToast(SettingActivity.this, getResources().getString(R.string.hint_trip_setfail));
                                return;
                            }
                            // switchTwo.setChecked(true);
                        }


                        ActivityUtil.showToast(SettingActivity.this, getResources().getString(R.string.hint_trip_setfail));
                    }
                }
        );

    }

//    private void getTsp29(final boolean flag) {
//        /**
//         * cccc:{"resultMessage":"","resultDescription":""}
//         * */
//        DialogUtils.loading(mContext,true);
//        TspRxUtils.getAlerset(this,
//                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
//                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer"+" "+new SharedPHelper(SettingActivity.this).get(Constant.ACCESSTOKENS,"")},
//                new String[]{"enabled"},
//                new Object[]{flag},
//                new TspRxListener() {
//                    @Override
//                    public void onSucc(Object obj) {
//                        DialogUtils.hidding((Activity) mContext);
//                       if(flag){
//                           ActivityUtil.showToast(SettingActivity.this,getResources().getString(R.string.toast_openpush));
//                           isChe=true;
//                           id_switch.setChecked(true);
//                           sharedPHelper.put("swichone",flagone);
//                       }else {
//                           ActivityUtil.showToast(SettingActivity.this,getResources().getString(R.string.toast_closepush));
//                           isChe=true;
//                           id_switch.setChecked(false);
//                       }
//                    }
//
//                    @Override
//                    public void onFial(String str) {
//                        DialogUtils.hidding((Activity) mContext);
//                        if(flag){
//                            ActivityUtil.showToast(SettingActivity.this,getResources().getString(R.string.toast_openpushf));
//                            isChe=false;
//                            id_switch.setChecked(false);
//                        }else {
//                            ActivityUtil.showToast(SettingActivity.this,getResources().getString(R.string.toast_closepushf));
//                            isChe=false;
//                            id_switch.setChecked(true);
//                        }
//                    }
//                }
//        );
//
//    }


    private void initFinger() {
        flagFinger = (boolean) sharedPHelper.get(Constant.ISFINGER, false);
        if (flagFinger) {
            switchFour.setChecked(true);
        } else {
            switchFour.setChecked(false);
        }
        switchFour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    flagFinger = true;
                } else {
                    flagFinger = false;
                }
                sharedPHelper.put(Constant.ISFINGER, flagFinger);

            }
        });
    }
//    private void initPush() {
//        flagone= (boolean) sharedPHelper.get("swichone",false);
//        if(flagone){
//            id_switch.setChecked(true);
//        }else {
//            id_switch.setChecked(false);
//        }
//
//    }

    private void initDay() {
        switchTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

//                if (isFail) {
//                    getTsp23(isChecked);
//                } else {
//                    MLog.e("修改失败保持原状");
//                }

                if (isOne) {
                    isOne = false;
                } else {
                    getTsp23(isChecked);
                }


            }
        });
    }


    @OnClick({R.id.back, R.id.language, R.id.password_setting, R.id.sefe_setting,
            R.id.kill, R.id.exit, R.id.setting_push, R.id.settinggeo})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.language:
                startActivity(new Intent(SettingActivity.this, LanguageActivity.class));
                break;
            case R.id.password_setting:
                startActivity(new Intent(SettingActivity.this, FixPasswordActivity.class));
                break;
            case R.id.sefe_setting:
//                if (sharedPHelpers.get("pin", "abcdef").equals("abcdef")) {//设置
//                    keyboard = new KeyBoardDialog((Activity) context, getDecorViewDialogSetting());
//                    keyboard.show();
//
//                } else {//重设
//                    keyboard = new KeyBoardDialog((Activity) context, getDecorViewDialog());
//                    keyboard.setCancelable(true);//false按框外面和BACK键都不响应
//                    keyboard.setCanceledOnTouchOutside(true);
//                    keyboard.show();
//                }


                sharedPHelpers.put("pinone", "abcdef");
                keyboard = new KeyBoardDialog((Activity) context, getDecorViewDialog(1));
                keyboard.setCancelable(true);//false按框外面和BACK键都不响应
                keyboard.setCanceledOnTouchOutside(true);
                keyboard.show();


                break;
            case R.id.kill:

                showdialogkill();

                break;
            case R.id.exit:

                showdialogexit();

                break;
            case R.id.setting_push:
                // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限

//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("package", MyApp.getInstance().getPackageName(), null);
//                intent.setData(uri);
//                startActivity(intent);

//                Intent intent = new Intent();
//                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
//                intent.putExtra("app_package", mContext.getPackageName());
//                intent.putExtra("app_uid", mContext.getApplicationInfo().uid);
//                startActivity(intent);

                MyUtils.gotoNotificationSetting(SettingActivity.this);
                break;
            case R.id.settinggeo:
                startActivity(new Intent(this, ChooseBookActivity.class));
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initPush();//初始化消息推送
    }

    private void showdialogexit() {
//        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(SettingActivity.this);
//        builder1.setTitle(getResources().getString(R.string.toast_confirmexit))
//                .setPositiveButton(getResources().getString(R.string.nevs_lfor_confirm), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        sharedPHelper.put("TSPVIN", "0");
//                        sharedPHelper.put(Constant.ISPLASHTO,"0");//是否成功登录过一次
//                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
//                        ComponentName cn = intent.getComponent();
//                        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);//ComponentInfo{包名+类名}
//                        startActivity(mainIntent);
//                    }
//                })
//                .setNegativeButton(getResources().getString(R.string.comm_cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();

        tipsDialog = new FinancialIOSTipsDialog(this, getResources().getString(R.string.toast_confirmexit), "", 2, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();
                sharedPHelper.put("TSPVIN", "0");
                sharedPHelper.put(Constant.ISPLASHTO, "0");//是否成功登录过一次

                //      BitmapUtil.deletefile(Constant.ICONBITMAPNAME);

//                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
//                ComponentName cn = intent.getComponent();
//                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);//ComponentInfo{包名+类名}
//                startActivity(mainIntent);
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setClass(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();
            }
        });
        tipsDialog.show();
        tipsDialog.setCancelable(false);


    }

    private void showdialogkill() {
        tipsDialog = new FinancialIOSTipsDialog(this, getResources().getString(R.string.toast_isdelete), "", 2, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();

                MyUtils.clearGlide(mContext);//清除图片缓存

                final AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this).create();
                alertDialog.show();
                alertDialog.setCancelable(false);//点击背景是对话框不会消失
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.alertzdy);//加载自定义的布局
                WindowManager.LayoutParams wm = window.getAttributes();
                wm.width = 600;//设置对话框的宽
                wm.height = 500;//设置对话框的高
                wm.alpha = 0.5f;//设置对话框的背景透明度
                wm.dimAmount = 0.6f;//遮罩层亮度
                window.setAttributes(wm);
                final ImageView imageView = (ImageView) window.findViewById(R.id.progress_bar);
                final TextView textView = (TextView) window.findViewById(R.id.text);
                final LinearLayout linearLayout = (LinearLayout) window.findViewById(R.id.dialogq);
                imageView.setBackground(getResources().getDrawable(R.drawable.frame));
                animationDrawable = (AnimationDrawable) imageView.getBackground();
                animationDrawable.start();
                clearPdf();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animationDrawable.stop();
                        imageView.setBackground(getResources().getDrawable(R.mipmap.finish));
                        linearLayout.setBackgroundResource(R.color.black_40);
                        textView.setText(getResources().getString(R.string.toast_deletesuc));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 1100);// 延迟关闭

                    }
                }, 2000);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();
            }
        });
        tipsDialog.show();
    }

    private void clearPdf() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyUtils.clearPdf();
            }
        }).start();
    }


    protected View getDecorViewDialog(int i) {


        //1表示不隐藏取消按钮
        return PayPasswordView.getInstance(i, total, context, new PayPasswordView.OnPayListener() {

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
                            MLog.e("dd1");
                            if (password.equals(new SharedPHelpers(mContext, "c" + new SharedPHelper(mContext).get(Constant.LOGINNAME, "")).get("pin", "abcdef") + "")) {
                                MLog.e("dd2");
                                count++;
                                total = getResources().getString(R.string.toast_newpin);
                                keyboard = new KeyBoardDialog(SettingActivity.this, getDecorViewDialog(0));
                                keyboard.show();
                            } else {
                                MLog.e("dd3");
//                            final NotiDialog dialog = new NotiDialog(context, tips);
//                            dialog.show();
//                            dialog.setTitleStr("密码错误");
//                            dialog.setOkButtonText("忘记密码");
//                            dialog.setCancelButtonText("重试");
//                            dialog.setPositiveListener(new View.OnClickListener() {// 忘记密码操作
//                                @Override
//                                public void onClick(View v) {
//
//                                    ToastUtils.showShortToast(context, "再好好想想");
//                                }
//                            }).setNegativeListener(new View.OnClickListener() {// 重试操作
//
//                                @Override
//                                public void onClick(View v) {
//                                    // TODO Auto-generated method stub
//                                    keyboard = new KeyBoardDialog((Activity) context, getDecorViewDialog());
//                                    keyboard.show();
//                                }
//                            });

                                if (ii == 3) {
                                    exitToLongin();
                                } else {
                                    MLog.e("dd4");
                                    ii++;
                                    ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pinerrorr));
                                    keyboard = new KeyBoardDialog(SettingActivity.this, getDecorViewDialog(0));
                                    keyboard.show();
                                    MLog.e("dd5");
                                }

                            }
                        } else if (count == 1) {
                            if(password.equals(new SharedPHelpers(mContext, "c" + new SharedPHelper(mContext).get(Constant.LOGINNAME, "")).get("pin", "abcdef") + "")){
                                total = getResources().getString(R.string.toast_newpin);
                                ToastUtils.showShortToast(context, getResources().getString(R.string.n_unpinsame));
                                keyboard = new KeyBoardDialog(SettingActivity.this, getDecorViewDialog(0));
                                keyboard.show();
                            }else {
                                MLog.e("dd6");
                                count++;
                                ii = 0;
                                sharedPHelpers.put("pinone", password);
                                total = getResources().getString(R.string.toast_confirmpin);
                                keyboard = new KeyBoardDialog(SettingActivity.this, getDecorViewDialog(0));
                                keyboard.show();
                                MLog.e("dd7");
                            }




                        } else if (count == 2) {
                            MLog.e("dd8");
                            if (password.equals(String.valueOf(sharedPHelpers.get("pinone", "abcdef")))) {
                                MLog.e("dd9");
                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pinreset));
                                sharedPHelpers.put("pin", password);
                                sharedPHelpers.put("pinone", "abcdef");
                                total = MyApp.getInstance().getResources().getString(R.string.pay_title);
                                count = 0;
                            } else {
                                MLog.e("dd10");
//                                count++;
//                                keyboard = new KeyBoardDialog(SettingActivity.this, getDecorViewDialog(0));
//                                keyboard.show();
//                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pininout));
                                //total=getResources().getString(R.string.toast_setpin);
                                total = getResources().getString(R.string.toast_newpin);
                                ii = 0;
                                keyboard = new KeyBoardDialog(SettingActivity.this, getDecorViewDialog(0));
                                keyboard.show();
                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pininout));
                                count = 1;//ccccc
                                MLog.e("dd11");
                            }

                        } else if (count == 3) {//count>=3时的情况
                            MLog.e("dd12");
                            if (password.equals(String.valueOf(sharedPHelpers.get("pinone", "abcdef")))) {
                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pinreset));
                                sharedPHelpers.put("pin", password);
                                sharedPHelpers.put("pinone", "abcdef");
                                count = 0;
                                total = MyApp.getInstance().getResources().getString(R.string.pay_title);
                                MLog.e("dd13");
                            } else {
                                MLog.e("dd14");
                                ii = 0;
                                total = getResources().getString(R.string.toast_newpin);
                                keyboard = new KeyBoardDialog(SettingActivity.this, getDecorViewDialog(0));
                                keyboard.show();
                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pininout));
                                count = 4;
                                MLog.e("dd15");
                            }
                        } else {
                            MLog.e("dd16");
                            count = 3;
                            sharedPHelpers.put("pinone", password);
                            total = getResources().getString(R.string.toast_confirmpin);
                            keyboard = new KeyBoardDialog(SettingActivity.this, getDecorViewDialog(0));
                            keyboard.show();
                            MLog.e("dd17");
                        }
                    }


                }, 500);

            }

            @Override
            public void onCancelPay() {
                // TODO Auto-generated method stub
                keyboard.dismiss();
                keyboard = null;
                count = 0;
                ToastUtils.showShortToast(context, "");
            }
        }).getView();
    }

    private void exitToLongin() {//PIN码输入错误退出，不是登录
        new SharedPHelpers(mContext, "c" + new SharedPHelper(mContext).get(Constant.LOGINNAME, "")).put("pin", "abcdef");


//        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
//        ComponentName cn = intent.getComponent();
//        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);//ComponentInfo{包名+类名}
//        startActivity(mainIntent);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(SettingActivity.this, LoginActivity.class);
        startActivity(intent);
        ActivityUtil.showToast(SettingActivity.this, getResources().getString(R.string.toast_threerror));
    }

    public void initProgressDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context, R.style.loading_dialogone);
            loadingDialog.setText(getResources().getString(R.string.loading));
        }
        if (!SettingActivity.this.isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog = new LoadingDialog(context, R.style.loading_dialogone);
            loadingDialog.setText(getResources().getString(R.string.loading));
            loadingDialog.show();
        }
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
    }

    public void dismissProgressDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }


    private void getTsp20() {//获取围栏信息
        /**
         * cccc:{"ResultMessage":"","ResultDescription":"","Vin":"LTPSBSIMULATOR001","Enabled":false,"GeoFenceType":"1","Circle":null}
         *
         *
         *cccc:{"resultMessage":"","resultDescription":"","vin":"LTPSBSIMULATOR001","enabled":true,
         * "geoFenceType":"string","circle":{"centerPointLongitude":35.0333,"centerPointLatitude":116.7469,"radius":10},"region":{"adCode":"A003"},"polygon":[{"longitude":0.0,"latitude":0.0}]}
         * */

        TspRxUtils.getGeofenceIs(mContext,
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                new SharedPHelper(mContext).get("TSPVIN", "0").toString(),
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        MyUtils.upLogTSO(mContext, "电子围栏是否开启", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        MLog.e("1");
                        objectList.addAll((Collection<?>) obj);

                        isEnable = (boolean) objectList.get(0);
                        geoFenceType = String.valueOf(objectList.get(1));
                        switchThree.setChecked(isEnable);
                        getTsp21();
                        if (isEnable) {
                           // settinggeo.setVisibility(View.GONE);
                            MLog.e("2");
                        } else {
                            bb = 1;
                        }


                    }

                    @Override
                    public void onFial(String str) {
                        MLog.e("3");
                        MLog.e("失败，数据有NULL");
                        settinggeo.setVisibility(View.GONE);
                        MyUtils.upLogTSO(mContext, "电子围栏是否开启", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        // ActivityUtil.showToast(getContext(),str);
                        //bb=1;
                        switchThree.setChecked((boolean) new SharedPHelper(mContext).get("geosettingstate", false));

                        if ((boolean) new SharedPHelper(mContext).get("geosettingstate", false)) {
//                            settinggeo.setVisibility(View.VISIBLE);
                            MLog.e("4");
                        } else {
                            bb = 1;
                            MLog.e("5");
                        }


                    }
                }
        );

    }

    private void getTsp19(final boolean flag) {//设置开启和关闭
        /**
         *cccc:{"resultMessage":"","resultDescription":""}
         * */
        DialogUtils.loading(mContext, true);
        TspRxUtils.getSet(this,
                new String[]{Constant.TSP.CONTENTTYPE, Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.CONTENTTYPEVALUE, Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(SettingActivity.this).get(Constant.ACCESSTOKENS, "")},
                new String[]{"vin", "enabled", "geoFenceType"},
                new Object[]{new SharedPHelper(SettingActivity.this).get("TSPVIN", "0"),
                        flag,
                        geoFenceType
                },
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "设置电子围栏开启关闭", String.valueOf(obj), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");

                        // ActivityUtil.showToast(SettingActivity.this, getResources().getString(R.string.toast_settingsuc));
                        if (flag) {
                            settinggeo.setVisibility(View.VISIBLE);
                        } else {
                            settinggeo.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFial(String str) {
                        DialogUtils.hidding((Activity) mContext);
                        MyUtils.upLogTSO(mContext, "设置电子围栏开启关闭", String.valueOf(str), MyUtils.getTimeNow(), MyUtils.getTimeNow(), "", MyUtils.timeStampNow() + "");
                        settinggeo.setVisibility(View.GONE);
                        if (str.contains("400")) {
                            // ActivityUtil.showToast(SettingActivity.this, getResources().getString(R.string.toast_settingsuc));
                            bb = 1;
                            if (flag) {
//                                settinggeo.setVisibility(View.VISIBLE);
                                new SharedPHelper(mContext).put("geosettingstate", true);
                            } else {
//                                settinggeo.setVisibility(View.GONE);
                                new SharedPHelper(mContext).put("geosettingstate", false);
                            }
                        } else {
                            ActivityUtil.showToast(SettingActivity.this, getResources().getString(R.string.unnetwort));
                        }
                    }
                }
        );

    }


    protected View getDecorViewDialogSetting() {

        //0表示隐藏取消按钮
        return PayPasswordView.getInstance(0, total0, context, new PayPasswordView.OnPayListener() {

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

                        if (count0 == 0) {
                            if (password.length() == 6) {
                                count0++;
                                sharedPHelpers.put("pinisone0", password);
                                total0 = getResources().getString(R.string.toast_confirmpin);
                                keyboard = new KeyBoardDialog(SettingActivity.this, getDecorViewDialogSetting());
                                keyboard.show();
                            }
                        } else if (count0 == 1) {
                            if (StringUtils.isEquals(password, String.valueOf(sharedPHelpers.get("pinisone0", "abcdef")))) {
                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pinsuccess));
                                sharedPHelpers.put("pin", password);
                                sharedPHelpers.put("pinisone0", "abcdef");
                                //  startAlarm();
                                count0 = 0;
                                sharedPHelper.put(Constant.SAFETY,System.currentTimeMillis());
                            } else {
                                count0++;
                                keyboard = new KeyBoardDialog(SettingActivity.this, getDecorViewDialogSetting());
                                keyboard.show();
                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pininout));
                            }

                        } else {//count0>=2时的情况
                            if (StringUtils.isEquals(password, String.valueOf(sharedPHelpers.get("pinisone0", "abcdef")))) {
                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pinsuccess));
                                sharedPHelpers.put("pin", password);
                                sharedPHelpers.put("pinisone0", "abcdef");
                                //   startAlarm();
                                count0 = 0;
                            } else {
                                keyboard = new KeyBoardDialog(SettingActivity.this, getDecorViewDialogSetting());
                                keyboard.show();
                                ToastUtils.showShortToast(context, getResources().getString(R.string.toast_pininout));
                            }
                        }
                    }


                }, 200);

            }

            @Override
            public void onCancelPay() {
                // TODO Auto-generated method stub
                keyboard.dismiss();
                keyboard = null;
                count = 0;
                ToastUtils.showShortToast(context, "");
            }
        }).getView();
    }

    private void setText() {
        if (MyUtils.getLanguage(mContext)) {//如需关闭或开启本应用新消息通知，请点击这里设置。
            String content = "如需关闭或开启本应用新消息通知，请" + "<u>"
                    + "点击这里"
                    + "</u>"
                    + "设置。";
            settingPush.setText(Html.fromHtml(content));
        } else {//To turn off or turn on the new message notification for this app, click here to set it up.
            String content = "To turn off or turn on the new message notification for this app, " + "<u>"
                    + "click here"
                    + "</u>"
                    + "to set it up.";
            settingPush.setText(Html.fromHtml(content));
        }


    }

    private void getTsp21() {
        /**
         * cccc:{"ResultMessage":"","ResultDescription":"","Vin":"LTPSBSIMULATOR001","Enabled":false,"GeoFenceType":"1","Circle":null}
         *
         *
         *cccc:{"resultMessage":"","resultDescription":"","vin":"LTPSBSIMULATOR001","enabled":true,
         * "geoFenceType":"string","circle":{"centerPointLongitude":35.0333,"centerPointLatitude":116.7469,"radius":10},"region":{"adCode":"A003"},"polygon":[{"longitude":0.0,"latitude":0.0}]}
         * */
        list.clear();
        TspRxUtils.getGeofence(mContext,
                new String[]{Constant.TSP.ACCEPT, Constant.TSP.AUTHORIZATION},
                new Object[]{Constant.TSP.ACCEPTVALUE, "Bearer" + " " + new SharedPHelper(mContext).get(Constant.ACCESSTOKENS, "")},
                new SharedPHelper(mContext).get("TSPVIN", "0").toString(),
                new TspRxListener() {
                    @Override
                    public void onSucc(Object obj) {
//                        settinggeo.setVisibility(View.VISIBLE);
                        list.addAll((Collection<?>) obj);
                        if (list.size() == 0) {
                            settinggeo.setVisibility(View.GONE);
                            new SharedPHelper(mContext).put("geosettingstate", true);
                        }else if(list.size()==1){
                            boolean enable =Utils.toBoolean(list.get(0));
                            if(!enable){
                                settinggeo.setVisibility(View.GONE);
                                new SharedPHelper(mContext).put("geosettingstate", true);
                                return;
                            }
                            settinggeo.setVisibility(View.VISIBLE);
                            new SharedPHelper(mContext).put("geosettingstate", false);
                        } else if (list.size() == 4) {
                            boolean enable =Utils.toBoolean(list.get(3));
                            if(!enable){
                                settinggeo.setVisibility(View.GONE);
                                new SharedPHelper(mContext).put("geosettingstate", true);
                                return;
                            }
                            int ruadius = Utils.toInt(list.get(2));
                            if(ruadius>0){
                                settinggeo.setVisibility(View.GONE);
                            }else{
                                settinggeo.setVisibility(View.VISIBLE);

                            }
                            new SharedPHelper(mContext).put("geosettingstate", false);

                        } else if (list.size() == 2) {
                            String adCode = String.valueOf(list.get(0));
                            boolean enable =Utils.toBoolean(list.get(1));
                            if(!enable){
                                settinggeo.setVisibility(View.GONE);
                                new SharedPHelper(mContext).put("geosettingstate", true);
                                return;
                            }
                            if(TextUtils.isEmpty(adCode)){
                                settinggeo.setVisibility(View.VISIBLE);

                            }else {
                                settinggeo.setVisibility(View.GONE);
                            }
                                new SharedPHelper(mContext).put("geosettingstate", false);
                        }

                    }

                    @Override
                    public void onFial(String str) {
                    }
                }
        );

    }
}
