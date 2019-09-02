package com.nevs.car.z_start;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
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
import com.nevs.car.web.WebHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("JavascriptInterface")
public class WebBookActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.share)
    TextView share;
    @BindView(R.id.n_view)
    RelativeLayout nView;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private ValueCallback<Uri> uploadMessage;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private Dialog mDialog = null;


    private String url = null;
    private String title = "";
    private String carType = MyApp.getInstance().getResources().getString(R.string.nevs_cartypes);
    private String newS = MyApp.getInstance().getResources().getString(R.string.servce_news);
    private WXShare wxShare;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_web_book;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MyUtils.setPadding(nView,mContext);
        initProgressDialog();
        initWebview();
        initUrl();
        initShare();


    }

    private void initProgressDialog() {
        mDialog = DialogUtils.createLoadingDialog(WebBookActivity.this, getResources().getString(R.string.loading));
    }

    private void initWebview() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        WebHelper.setDefaultWebviewSettings(mWebView);
        WebHelper.setAppCacheWebviewSettings(getApplicationContext(), mWebView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); //设置https连接可以加载http资源
        }

        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());


    }

    private void initUrl() {
        url = getIntent().getStringExtra("URL");
        title = getIntent().getStringExtra("TITLE");
        MLog.e("URL:" + url);
        MLog.e("TITLE:" + title);
        mWebView.loadUrl(url);
        if (title != null) {
            tvTitle.setText(title);
            initRight();
        }
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

    private void initRight() {
        if (getIntent().getStringExtra("TITLES") != null) {
            title = newS;
        }
        if (title.equals(carType) || title.equals(newS)) {
            share.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.tv_title})
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
        }
    }


    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            if (newProgress == 100) {
                if (WebBookActivity.this.isFinishing())
                    return;
                // pd.hide();//隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。
                DialogUtils.webhidding(WebBookActivity.this, mDialog);
                MLog.e("隐藏");

            } else {
                if (WebBookActivity.this.isFinishing())
                    return;
                //pd.show();//显示进度对话框
                DialogUtils.webloading(WebBookActivity.this, true, mDialog);
                MLog.e("显示");
            }
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        //For Android  >= 4.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            openImageChooserActivity();
            return true;
        }
    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (url.startsWith("tel:")) {//拦截打电话
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                return true;
//            }else {
//                view.loadUrl(url);
//                return true;
//            }
            //Android8.0以下的需要返回true 并且需要loadUrl；8.0之后效果相反
            if (Build.VERSION.SDK_INT < 26) {
                view.loadUrl(url);
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // id_noweb_hint.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        wxShare.register();
    }

    @Override
    protected void onDestroy() {
        wxShare.unregister();
        super.onDestroy();
        if (mWebView != null) {
            mWebView.clearCache(true);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();
            cookieManager.removeAllCookie();

        }
    }
}
