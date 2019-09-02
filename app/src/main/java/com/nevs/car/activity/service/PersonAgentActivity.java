package com.nevs.car.activity.service;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
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

import butterknife.BindView;
import butterknife.OnClick;

public class PersonAgentActivity extends BaseActivity {


    @BindView(R.id.please_enter)
    TextView pleaseEnter;
    @BindView(R.id.webview)
    WebView wv;
    @BindView(R.id.please_enters)
    LinearLayout pleaseEnters;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private Dialog mDialog = null;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {//定义一个Handler，用于处理下载线程与UI间通讯
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
                        if (PersonAgentActivity.this.isFinishing())
                            return;
                        //pd.show();//显示进度对话框
                        DialogUtils.webloading(PersonAgentActivity.this, true, mDialog);
                        MLog.e("显示");
                        break;
                    case 1:
                        if (PersonAgentActivity.this.isFinishing())
                            return;
                        // pd.hide();//隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。
                        DialogUtils.webhidding(PersonAgentActivity.this, mDialog);
                        MLog.e("隐藏");
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_person_agent;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        ActivityManager.addActivity(PersonAgentActivity.this);
        getHttp();
    }

    private void getHttp() {
        //http://220.249.93.210:8602/web/yhxy.html
//    90071003	个人代理协议
//    90071002	个人代理介绍
        HttpRxUtils.getUrlProxy(mContext, "90071002",
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                        initProgressDialog();
                        initWebview();
                        initUrl(String.valueOf(obj));
                    }

                    @Override
                    public void onFial(String str) {
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
    protected void onStart() {
        super.onStart();
        initisV();
    }

    private void initisV() {
        String isPa = new SharedPHelper(mContext).get(Constant.LOGINISPA, "").toString();
        // isPa="ddd";  //测试
        if (isPa.equals("Yes") || isPa.equals("Und")) {
            pleaseEnters.setVisibility(View.GONE);
        }

    }

    @OnClick({R.id.back, R.id.please_enter})
    public void onClick(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.please_enter:
                startActivity(new Intent(this, ClauseActivity.class));
                break;
        }
    }


    private void initUrl(String s) {
        loadurl(wv, Constant.HTTP.BANNERURL + Constant.HTTP.NEWSCENTER + s);
    }


    private void initWebview() {
        WebSettings settings = wv.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);


//支持javascript
        settings.setJavaScriptEnabled(true);
// 设置可以支持缩放
        settings.setSupportZoom(true);
// 设置出现缩放工具
        //    settings.setBuiltInZoomControls(true);
        //android自带的五种字体大小：
        //  SMALLEST(50%),
        //       SMALLER(75%),          NORMAL(100%),          LARGER(150%),          LARGEST(200%);
        settings.setTextSize(WebSettings.TextSize.LARGEST);
//扩大比例的缩放
        settings.setUseWideViewPort(true);
//自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);


        //设置https连接可以加载http资源
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wv.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // wv.setScrollBarStyle(0);//滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上

        loadWeb();

    }

    private void loadWeb() {
        //处理通知，请求事件
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadurl(view, url);
                return true;
            }

            //步骤1：写一个html文件（error_handle.html），用于出错时展示给用户看的提示页面
//步骤2：将该html文件放置到代码根目录的assets文件夹下
            //步骤3：复写WebViewClient的onRecievedError方法
//该方法传回了错误码，根据错误类型可以进行不同的错误分类处理
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
//                switch(errorCode)
//                {
//                    case 404:
//                        view.loadUrl("file:///android_assets/error_handle.html");
//                        break;
//                }
//            }
        });
        //辅助WEBVIEW处理JS的对话框，网站图标，网站title,加载进度等
        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {//载入进度改变而触发
                MLog.e("进度" + progress + "");
                if (progress == 100) {
                    handler.sendEmptyMessage(1);//如果全部载入,隐藏进度对话框
                }
                super.onProgressChanged(view, progress);
            }
        });
    }

    private void initProgressDialog() {
        mDialog = DialogUtils.createLoadingDialog(PersonAgentActivity.this, getResources().getString(R.string.loading));
    }

    public void loadurl(WebView view, String url) {
        handler.sendEmptyMessage(0);
        view.loadUrl(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wv != null) {
            wv.onResume(); //通知内核尝试停止所有处理，如动画和地理位置，但是不能停止Js，如果想全局停止Js，可以调用pauseTimers()全局停止Js，调用onResume()恢复。
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wv != null) {
            wv.onPause(); //通知内核尝试停止所有处理，如动画和地理位置，但是不能停止Js，如果想全局停止Js，可以调用pauseTimers()全局停止Js，调用onResume()恢复。
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (wv != null) {
//            wv.clearCache(true);
//        }
        if (wv != null) {
            wv.clearCache(true); //清空缓存
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                if (webViewLayout != null) {
//                    webViewLayout.removeView(wv);
//                }
                wv.removeAllViews();
                wv.destroy();
            } else {
                wv.removeAllViews();
                wv.destroy();
//                if (webViewLayout != null) {
//                    webViewLayout.removeView(wv);
            }
        }
        wv = null;
    }


    //点击返回上一页面而不是退出浏览器
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回键
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
//            wv.goBack();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);

        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
            wv.goBack();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            PersonAgentActivity.this.finish();//按了返回键，但已经不能返回，则执行退出确认
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
