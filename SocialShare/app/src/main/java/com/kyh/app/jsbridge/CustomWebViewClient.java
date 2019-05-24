package com.kyh.app.jsbridge;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebViewClient extends WebViewClient {
    private Context mContext;

    public CustomWebViewClient(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d("pageFinish", "onPageFinished: ");
        super.onPageFinished(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d("override", "shouldOverrideUrlLoading: ");
        if(!url.contains("gzh.dkyapp.com")) {
            Intent intent= new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            mContext.startActivity(intent);
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }
}
