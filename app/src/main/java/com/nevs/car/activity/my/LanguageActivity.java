package com.nevs.car.activity.my;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.ShareUtil;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.GetLanguageUtil;
import com.nevs.car.tools.util.LanguageUtil;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.z_start.SplashActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class LanguageActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.chinese)
    RadioButton chinese;
    @BindView(R.id.english)
    RadioButton english;
    @BindView(R.id.rag)
    RadioGroup rag;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String language = null;
    private int isSetting = 0;
    private AnimationDrawable animationDrawable;

    //  EventBus.getDefault().register(this);
    // EventBus.getDefault().unregister(this);
    // EventBus.getDefault().post("showhint");//发送
//    @Subscribe(threadMode = ThreadMode.MAIN)//接收
//    public void messageEventBus(String event){
//
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_language;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        checkButton();
        checkLanguageButton();//语言按钮的默认显示状态
    }

    @OnClick({R.id.back, R.id.set_language})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.set_language:
                //根据变量isSetting来设置语言缓存，并请求服务器传递当前APP的语言环境
                //1为中文,2为英文
                switch (isSetting) {
                    case 1:
                        //正式环境下移动到请求成功里面
                        ShareUtil.storeSettingLanguage(LanguageActivity.this, "zh", "issettings", "issetting");
                        LanguageUtil.updateLocale(this, LanguageUtil.LOCALE_CHINESE);
                        //请求服务器

                        //重置等待中成功以后跳到首页
                        freshView("zh");//重新启动MainActivity
                        break;
                    case 2:
                        //正式环境下移动到请求成功里面
                        ShareUtil.storeSettingLanguage(LanguageActivity.this, "cn", "issettings", "issetting");
                        // ActivityUtil.showToast(LanguageActivity.this,getResources().getString(R.string.tost));
                        LanguageUtil.updateLocale(this, LanguageUtil.LOCALE_ENGLISH);
                        //请求服务器

                        //重置等待中成功以后跳到首页
                        freshView("cn");//重新启动MainActivity
                        break;
                }
                break;
        }
    }


    private void checkLanguageButton() {
        //判断用户是否已经设置，没有设置则设置系统语言，设置了根据缓存设置对应语言
        //""为未设置,"zh"为中文"cn"为英文
        String setting = ShareUtil.readSettingLanguage(LanguageActivity.this, "issettings", "issetting");
        switch (setting) {
            case "":
                switch (GetLanguageUtil.getLanguage()) {
                    case "zh":
                        rag.check(R.id.chinese);
                        break;
                    case "cn":
                        rag.check(R.id.english);
                        break;
                }
                break;
            case "zh":
                rag.check(R.id.chinese);
                break;
            case "cn":
                rag.check(R.id.english);
                break;
        }

    }


    private void checkButton() {
        rag.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (chinese.getId() == checkedId) {
            // ActivityUtil.showToast(LanguageActivity.this, "中文简体");
            MLog.e("设置了中文");
            isSetting = 1;
        } else if (english.getId() == checkedId) {
            //ActivityUtil.showToast(LanguageActivity.this, "English");
            MLog.e("设置了英文");
            isSetting = 2;
        }
    }

    private void freshView(final String lang) {
        final AlertDialog alertDialog = new AlertDialog.Builder(LanguageActivity.this).create();
        alertDialog.show();
        alertDialog.setCancelable(false);//点击背景是对话框不会消失
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.aler_language);//加载自定义的布局
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationDrawable.stop();
                imageView.setBackground(getResources().getDrawable(R.mipmap.finish));
                linearLayout.setBackgroundResource(R.color.black_40);
                textView.setText(getResources().getString(R.string.toast_settingsuc));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();

                        switch (lang) {
                            case "zh":
                                MLog.e("设置了中文");
                                // Apollo.emit("event","zh");
                                //  EventBus.getDefault().post("zh");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        GetLanguageUtil.zh(LanguageActivity
                                                .this);
                                        exitLogin();
                                    }
                                });
                                break;
                            case "cn":
                                MLog.e("设置了英文");
                                // Apollo.emit("event","cn");
                                // EventBus.getDefault().post("cn");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        GetLanguageUtil.cn(LanguageActivity.this);
                                        exitLogin();
                                    }
                                });
                                break;
                        }

                        // finish();

                    }
                }, 1100);// 延迟关闭

            }
        }, 2000);

    }

//    @Receive("event")
//    public void onEvent(String message) {
//        switch (message) {
//            case "zh":
//                GetLanguageUtil.zh(BaseActivity.this);
//                break;
//            case "cn":
//                GetLanguageUtil.cn(BaseActivity.this);
//                break;
//        }
//
//    }
//@Subscribe(threadMode = ThreadMode.MAIN)//接收
//public void messageEventBus(String event){
//    switch (event) {
//        case "zh":
//            GetLanguageUtil.zh(LanguageActivity
//                    .this);
//            exitLogin();
//            break;
//        case "cn":
//            GetLanguageUtil.cn(LanguageActivity.this);
//            exitLogin();
//            break;
//    }
//}

    private void exitLogin() {
//        Intent intent = new Intent(LanguageActivity.this, SplashActivity.class);
//        ComponentName cn = intent.getComponent();
//        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);//ComponentInfo{包名+类名}
//        startActivity(mainIntent);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(LanguageActivity.this, SplashActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if (!EventBus.getDefault().isRegistered(this))
//        {
//            EventBus.getDefault().register(this);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  EventBus.getDefault().unregister(this);
    }
}
