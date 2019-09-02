package com.nevs.car.z_start;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.RelativeLayout;

import com.nevs.car.R;
import com.nevs.car.constant.Constant;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.SharedPreferencesUtil.SharedPHelper;
import com.nevs.car.tools.rx.HttpRxListener;
import com.nevs.car.tools.rx.HttpRxUtils;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebIdActivity extends BaseActivity {


    @BindView(R.id.webview)
    WebView wvTask;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String url = null;
    private Dialog mDialog = null;
    private String title = "";
    private String certName = "";
    private String certPhone = "";
    private String certNum = "";
    private String iccId = "";
    private String pwd = "";

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {//定义一个Handler，用于处理下载线程与UI间通讯
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
                        if (WebIdActivity.this.isFinishing())
                            return;
                        //pd.show();//显示进度对话框
                        DialogUtils.webloading(WebIdActivity.this, true, mDialog);
                        MLog.e("显示");
                        break;
                    case 1:
                        if (WebIdActivity.this.isFinishing())
                            return;
                        // pd.hide();//隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。
                        DialogUtils.webhidding(WebIdActivity.this, mDialog);
                        MLog.e("隐藏");
                        insertValue();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    private void insertValue() {
        String name[] = {"certName", "certPhone", "certNum", "iccId"};
        String value[] = {certName, certPhone, certNum, iccId};
        //"var text = document.getElementById('certName'); \n text.value = '%@';"
        for (int i = 0; i < name.length; i++) {
            wvTask.evaluateJavascript("var text = document.getElementById('" + name[i] + "'); \n text.value = '" + value[i] + "';", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    MLog.e("value" + value);
                }
            });
        }
    }


    @Override
    public int getContentViewResId() {
        return R.layout.activity_web_id;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initProgressDialog();
        initUrl();
        initWebview();


    }


    private void initUrl() {
        url = getIntent().getStringExtra("URL");
        MLog.e("URL:" + url);
        certName = getIntent().getStringExtra("certName");
        certPhone = getIntent().getStringExtra("certPhone");
        certNum = getIntent().getStringExtra("certNum");
        iccId = getIntent().getStringExtra("iccId");
        pwd=getIntent().getStringExtra("pwd");
        //   loadurl(wv, url);
    }

    private void initWebview() {
        wvTask.getSettings().setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        wvTask.getSettings().setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        wvTask.getSettings().setDisplayZoomControls(true); //隐藏原生的缩放控件
        wvTask.getSettings().setBlockNetworkImage(false);//解决图片不显示
        wvTask.getSettings().setLoadsImagesAutomatically(true); //支持自动加载图片
        wvTask.getSettings().setDefaultTextEncodingName("utf-8");//设置编码格式
        wvTask.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置允许JS弹窗
        wvTask.getSettings().setJavaScriptEnabled(true);
        wvTask.getSettings().setAllowContentAccess(true);
        wvTask.getSettings().setAppCacheEnabled(false);
        wvTask.getSettings().setLoadWithOverviewMode(true);
        wvTask.getSettings().setUseWideViewPort(true);
        wvTask.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wvTask.getSettings().setAllowFileAccess(true);
        handler.sendEmptyMessage(0);
        wvTask.loadUrl(url);
        MLog.e("监控界面加载的url为: " + url);

        loadWeb();

    }

    private void loadWeb() {
        //该界面打开更多链接
        wvTask.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                MLog.e("WebViewClient：s="+s);
                webView.loadUrl(s);
                return false;
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                webView.getSettings().setJavaScriptEnabled(true);
                super.onPageFinished(webView, url);
            }
        });
        //监听网页的加载进度
        wvTask.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                try {
                    if (i < 100) {
//                    tvTaskProgress.setVisibility(View.VISIBLE);
                        webView.setVisibility(View.GONE);
                    } else {
//                        tvTaskProgress.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessage(1);
                    }
                } catch (Exception e) {
                    MLog.e("yicc");
                }

            }
        });

    }

    public void initProgressDialog() {
        mDialog = DialogUtils.createLoadingDialog(WebIdActivity.this, getResources().getString(R.string.loading));
    }

    public void loadurl(WebView view, String url) {
        handler.sendEmptyMessage(0);
        view.loadUrl(url);

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onResume() {
        super.onResume();
        wvTask.onResume();
        wvTask.getSettings().setJavaScriptEnabled(true);
    }


    @Override
    public void onPause() {
        super.onPause();
        wvTask.onPause();
        wvTask.getSettings().setLightTouchEnabled(false);
    }


    @Override
    protected void onDestroy() {
        if (this.wvTask != null) {
            wvTask.destroy();
        }
        super.onDestroy();

        //清空所有Cookie
//        CookieSyncManager.createInstance(MyApp.getInstance());  //Create a singleton CookieSyncManager within a context
//        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
//        cookieManager.removeAllCookie();// Removes all cookies.
//        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now

    }


    //点击返回上一页面而不是退出浏览器
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回键
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wvTask.canGoBack()) {
            wvTask.goBack();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            getIsysCertifByIccId();
            WebIdActivity.this.finish();//按了返回键，但已经不能返回，则执行退出确认
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                getIsysCertifByIccId();
                finish();
                break;
        }
    }

    final class DemoJavaScriptInterface {

        DemoJavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        @JavascriptInterface
        public void clickOnAndroid(String test) {
            MLog.e("test:" + test);
        }

        @JavascriptInterface
        public String GetLat() {
            return "123";
        }

    }


//    private void insertValue() {
//        String name[] = {"certName", "certPhone", "certNum", "iccId"};
//        String value[] = {certName, certPhone, certNum, iccId};
//        //"var text = document.getElementById('certName'); \n text.value = '%@';"
//        for (int i = 0; i < name.length; i++) {
//            wv.evaluateJavascript("var text = document.getElementById('" + name[i] + "'); \n text.value = '" + value[i] + "';", new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    MLog.e("value" + value);
//                }
//            });
//        }
//    }
//
//
//    @SuppressLint("JavascriptInterface")
//    @Override
//    public int getContentViewResId() {
//        return R.layout.activity_web_id;
//    }
//
//    @Override
//    public void init(Bundle savedInstanceState) {
//        initProgressDialog();
//        initWebview();
//        initUrl();
//
//    }
//
//
//    private void initUrl() {
//        url = getIntent().getStringExtra("URL");
//        MLog.e("URL:" + url);
//        certName = getIntent().getStringExtra("certName");
//        certPhone = getIntent().getStringExtra("certPhone");
//        certNum = getIntent().getStringExtra("certNum");
//        iccId = getIntent().getStringExtra("iccId");
//
//        loadurl(wv, url);
//    }
//
//    private void initWebview() {
//        WebSettings settings = wv.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
//
//        settings.setSavePassword(false);
//        settings.setSaveFormData(false);
//        settings.setSupportZoom(false);
//
//        // wv.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
//
//        //设置https连接可以加载http资源
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            wv.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        // wv.setScrollBarStyle(0);//滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
//
//        loadWeb();
//
//    }
//
//    private void loadWeb() {
//        //处理通知，请求事件
//        wv.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                loadurl(view, url);
//                return true;
//            }
//
//            //步骤1：写一个html文件（error_handle.html），用于出错时展示给用户看的提示页面
////步骤2：将该html文件放置到代码根目录的assets文件夹下
//            //步骤3：复写WebViewClient的onRecievedError方法
////该方法传回了错误码，根据错误类型可以进行不同的错误分类处理
////            @Override
////            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
////                switch(errorCode)
////                {
////                    case 404:
////                        view.loadUrl("file:///android_assets/error_handle.html");
////                        break;
////                }
////            }
//        });
//        //辅助WEBVIEW处理JS的对话框，网站图标，网站title,加载进度等
//        wv.setWebChromeClient(new WebChromeClient() {
//            public void onProgressChanged(WebView view, int progress) {//载入进度改变而触发
//                MLog.e("进度" + progress + "");
//                if (progress == 100) {
//                    handler.sendEmptyMessage(1);//如果全部载入,隐藏进度对话框
//                }
//                super.onProgressChanged(view, progress);
//            }
//
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                MLog.e("message:" + message);
//                result.confirm();
//                return true;
//
//            }
//        });
//    }
//
//    public void initProgressDialog() {
//        mDialog = DialogUtils.createLoadingDialog(WebIdActivity.this, getResources().getString(R.string.loading));
//    }
//
//    public void loadurl(WebView view, String url) {
//        handler.sendEmptyMessage(0);
//        view.loadUrl(url);
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (wv != null) {
//            wv.onResume(); //通知内核尝试停止所有处理，如动画和地理位置，但是不能停止Js，如果想全局停止Js，可以调用pauseTimers()全局停止Js，调用onResume()恢复。
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (wv != null) {
//            wv.onPause(); //通知内核尝试停止所有处理，如动画和地理位置，但是不能停止Js，如果想全局停止Js，可以调用pauseTimers()全局停止Js，调用onResume()恢复。
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
////        if (wv != null) {
////            wv.clearCache(true);
////        }
//        if (wv != null) {
//            wv.clearCache(true); //清空缓存
//            wv.getSettings().setJavaScriptEnabled(false);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                if (webViewLayout != null) {
////                    webViewLayout.removeView(wv);
////                }
//                wv.removeAllViews();
//                wv.destroy();
//            } else {
//                wv.removeAllViews();
//                wv.destroy();
////                if (webViewLayout != null) {
////                    webViewLayout.removeView(wv);
//            }
//        }
//        wv = null;
//
//        //清空所有Cookie
//        CookieSyncManager.createInstance(MyApp.getInstance());  //Create a singleton CookieSyncManager within a context
//        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
//        cookieManager.removeAllCookie();// Removes all cookies.
//        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now
//
//
//    }
//
//
//    //点击返回上一页面而不是退出浏览器
//    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回键
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
//            wv.goBack();
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
//            WebIdActivity.this.finish();//按了返回键，但已经不能返回，则执行退出确认
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // TODO: add setContentView(...) invocation
//        ButterKnife.bind(this);
//    }
//
//    @OnClick({R.id.back})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.back:
//                finish();
//                break;
//        }
//    }
//
//    final class DemoJavaScriptInterface {
//
//        DemoJavaScriptInterface() {
//        }
//
//        /**
//         * This is not called on the UI thread. Post a runnable to invoke
//         * loadUrl on the UI thread.
//         */
//        @JavascriptInterface
//        public void clickOnAndroid(String test) {
//            MLog.e("test:" + test);
//        }
//
//        @JavascriptInterface
//        public String GetLat() {
//            return "123";
//        }
//
//    }


    private void getIsysCertifByIccId() {
        HttpRxUtils.getIsysCertifByIccId(
                mContext,
                new String[]{"accessToken", "userCode","pwd","ICCID","userAccount","IMSI","VIN","userName","identityType","identityNo"},
                new Object[]{new SharedPHelper(mContext).get(Constant.REGISTCENACCESSTOKEN, ""),
                        certPhone,
                        pwd,
                        iccId,
                        certPhone,
                        new SharedPHelper(mContext).get(Constant.imsi, ""),
                        new SharedPHelper(WebIdActivity.this).get("TSPVIN", "0")+"",
                        certName,
                        "身份证",
                        certNum

                },
                new HttpRxListener() {
                    @Override
                    public void onSucc(Object obj) {
                    }
                    @Override
                    public void onFial(String str) {
                    }
                });
    }


}