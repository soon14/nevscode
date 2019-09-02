package com.nevs.car.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.ref.WeakReference;

public class TalkWebViewClient extends WebViewClient {
	private static final String TAG = "TalkWebViewClient";

	private final WeakReference<Activity> mActivity;
	
	public TalkWebViewClient(Context context) {
		this.mActivity = new WeakReference<Activity> ((Activity) context);
	}
	
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            url = WebHelper.buildDefaultWebpageUrl(mActivity.get(), url);
            view.loadUrl(url);
            return false;
            
        } else {
            Intent intent = null;
            if(url.startsWith("tel")){
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            }else if(url.startsWith("smsto")){
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            }else if(url.startsWith("mailto")){
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            }
            if(null != intent){
                view.getContext().startActivity(intent);
            }
            return true;
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
    	super.onPageStarted(view, url, favicon);
    }
    
    @Override
    public void onPageFinished(WebView view, String url) {

        super.onPageFinished(view, url);
    }
    
    @Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    	switch (errorCode) {
	    	case WebViewClient.ERROR_BAD_URL:	//1
	    		break;
	    		
        	case WebViewClient.ERROR_CONNECT:	//2
        		showErrorDialog(description);
        		break;
        		
        	case WebViewClient.ERROR_HOST_LOOKUP:	//6
        		break;
        		
        	case WebViewClient.ERROR_IO:			//7
        		showErrorDialog(description);
        		break;
        		
        	case WebViewClient.ERROR_REDIRECT_LOOP:	//9
        		break;
        	case WebViewClient.ERROR_TIMEOUT:	//10
        		break;
        	default:
    	}
    	
    	super.onReceivedError(view, errorCode, description, failingUrl);
    }
    
    private void showErrorDialog(final String description) {
    	if (null == mActivity || null == mActivity.get()) return;
    }
    
    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }
    
}

