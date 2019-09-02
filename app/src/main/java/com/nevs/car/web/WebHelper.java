package com.nevs.car.web;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import java.net.MalformedURLException;
import java.net.URL;

public class WebHelper {
	
	public static String buildDefaultWebpageUrl(Context context, String originalWebappUrl) {
        return attachParamsToUrl(originalWebappUrl, 
            new String[]{"lang", Global.getLanguage(context) }, 
            new String[]{"clientVersion", "" + Config.VERSION }, 
            new String[]{"udid", "" + AndroidUtil.getUdid(context) }, 
            new String[]{"osVersion", Build.VERSION.SDK_INT + ""},
            new String[]{"os", "android"});
	}
	
	
	//@SuppressLint("SetJavaScriptEnabled")
    public static void setDefaultWebviewSettings(WebView webView) {
        WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);













		//支持手势缩放
		settings.setBuiltInZoomControls(true);

		//不显示webview缩放按钮
		settings.setDisplayZoomControls(false);
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


		settings.setDisplayZoomControls(false); //是否显示缩放控件
		settings.setSupportZoom(false);  //是否支持缩放
		//wb_img.setInitialScale(145); //第一次显示的缩放比例，例子是120%;
		//注释掉 wb_img.setInitialScale(145); 使用下面代码，则自适应屏幕居中显示
		//----------------------------------------
				//settings.setUseWideViewPort(true);
				// settings.setLoadWithOverviewMode(true);
		//		---------------------------------------------
				//		wb_img.getSettings().setJavaScriptEnabled(true);//启用js
		settings.setBlockNetworkImage(false);//解决图片不显示



		settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
	}
	
	public static void clearWebViewAbsolutely(WebView webView){
	    webView.clearCache(true);
	    webView.clearHistory();
	    webView.clearView();
	    webView.clearSslPreferences();
	    webView.clearDisappearingChildren();
	    webView.clearFocus();
	    webView.clearFormData();
	    webView.clearMatches();
	}
	
	public static void setAppCacheWebviewSettings(Context context, WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);
        
        String databasePath = ""; 
                //context.getDatabasePath(ChatProvider.DATABASE_NAME).getParent();
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(databasePath);
        
        settings.setGeolocationEnabled(true);
        settings.setGeolocationDatabasePath(databasePath);
        
        String appCachePath = context.getCacheDir().getAbsolutePath();
        settings.setAppCacheEnabled(true);
        settings.setAppCacheMaxSize(1024*1024*2);
        settings.setAppCachePath(appCachePath);
        
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
	}
	
	public static String attachParamsToUrl(String urlStr, String[]... kv) {
		if (TextUtils.isEmpty(urlStr)) return null;
		if (null == kv) return urlStr;
		
		try {
			URL url = new URL(urlStr);
			int port = url.getPort();
			String sPort = (port > 0 ? ":" + port  : "");
			
			// no query yet
			String newUrl = url.getProtocol() + "://" + url.getHost() + sPort + url.getPath();

			// add new params
			int len = kv.length;
			String[] params = new String[len];
			for (int i = 0; i < len; i++) {
				params[i] = kv[i][0] + "=" + StringUtils.emptyStringIfNull(kv[i][1]);
			}
			
			String sQuery = (TextUtils.isEmpty(url.getQuery()) ? "" : "?" + url.getQuery());
			if (TextUtils.isEmpty(sQuery)) {
				sQuery = "?" + TextUtils.join("&", params);
			} else {
				sQuery = sQuery + "&" + TextUtils.join("&", params);
			}
			
			return newUrl + sQuery;
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	
}
