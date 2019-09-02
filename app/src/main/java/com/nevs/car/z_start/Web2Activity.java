package com.nevs.car.z_start;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevs.car.R;
import com.nevs.car.tools.Base.BaseActivity;
import com.nevs.car.tools.interfaces.OnResponseListener;
import com.nevs.car.tools.util.ClickUtil;
import com.nevs.car.tools.util.DialogUtils;
import com.nevs.car.tools.util.MLog;
import com.nevs.car.tools.util.MyUtils;
import com.nevs.car.tools.util.WXShare;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Web2Activity extends BaseActivity {
    @BindView(R.id.webview)
    WebView wv;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.share)
    TextView share;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private String url = null;
    private ProgressDialog pd;
    private Dialog mDialog = null;
    private String title = "";
    private String carType = MyApp.getInstance().getResources().getString(R.string.nevs_cartypes);
    private String newS = MyApp.getInstance().getResources().getString(R.string.servce_news);
    private WXShare wxShare;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {//定义一个Handler，用于处理下载线程与UI间通讯
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
                        if (Web2Activity.this.isFinishing())
                            return;
                        //pd.show();//显示进度对话框
                        DialogUtils.webloading(Web2Activity.this, true, mDialog);
                        MLog.e("显示");
                        break;
                    case 1:
                        if (Web2Activity.this.isFinishing())
                            return;
                        // pd.hide();//隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。
                        DialogUtils.webhidding(Web2Activity.this, mDialog);
                        MLog.e("隐藏");
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public int getContentViewResId() {
        return R.layout.activity_web;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initProgressDialog();
        initWebview();
        initUrl();
        initShare();
    }

    private void initShare() {
        wxShare = new WXShare(this);
        wxShare.setListener(new OnResponseListener() {
            @Override
            public void onSuccess() {
                // 分享成功
                MLog.e("sd");
            }

            @Override
            public void onCancel() {
                // 分享取消
                MLog.e("sd");
            }

            @Override
            public void onFail(String message) {
                // 分享失败
                MLog.e("sd");
            }
        });
    }

    private void initUrl() {
        url = getIntent().getStringExtra("URL");
        title = getIntent().getStringExtra("TITLE");
        MLog.e("URL:" + url);
        MLog.e("TITLE:" + title);
        loadurl(wv, url);
        if (title != null) {
            tvTitle.setText(title);
            initRight();
        }
    }

    private void initRight() {
        if (title.equals(carType) || title.equals(newS)) {
            share.setVisibility(View.VISIBLE);
        }
    }

    private void initWebview() {
        WebSettings settings = wv.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);


        //wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        wv.setLayerType(View.LAYER_TYPE_HARDWARE, null);//开启硬件加速

        //支持javascript
        settings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        settings.setSupportZoom(true);
        //支持手势缩放
        settings.setBuiltInZoomControls(true);

        settings.setDomStorageEnabled(true);
        //不显示webview缩放按钮
        settings.setDisplayZoomControls(false);


// 设置出现缩放工具
        //    settings.setBuiltInZoomControls(true);
        //android自带的五种字体大小：
        //  SMALLEST(50%),
        //       SMALLER(75%),          NORMAL(100%),          LARGER(150%),          LARGEST(200%);
        settings.setTextSize(WebSettings.TextSize.SMALLER);
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
//cc                view.getSettings().setBlockNetworkImage(true);
//cc                view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                return true;
            }


//cc            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);	 view.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);	 view.getSettings().setBlockNetworkImage(false);
//            }


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
//        pd = new ProgressDialog(WebActivity.this);
//        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pd.setMessage("数据载入中，请稍候！");
        //文字即为显示的内容
        mDialog = DialogUtils.createLoadingDialog(Web2Activity.this, getResources().getString(R.string.loading));
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
        wxShare.register();
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
        wxShare.unregister();
        super.onDestroy();
//        if (wv != null) {
//            wv.clearCache(true);
//        }
        if (wv != null) {
            wv.clearCache(true); //清空缓存
            wv.getSettings().setJavaScriptEnabled(false);
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


        //清空所有Cookie
        CookieSyncManager.createInstance(MyApp.getInstance());  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now


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
            Web2Activity.this.finish();//按了返回键，但已经不能返回，则执行退出确认
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

    @OnClick({R.id.back, R.id.tv_title, R.id.share})
    public void onViewClicked(View view) {
        if (!ClickUtil.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_title:
                break;
            case R.id.share:
                DialogUtils.shareDiolag(Web2Activity.this, wxShare, url, title);
                break;
        }
    }

    //http://slide.fashion.sina.com.cn/s/slide_24_84625_114481.html#p=1
}
