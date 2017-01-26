/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import volley.Config_TAG;

public class Web3d extends Activity {
    private WebView web3d;
    private ProgressBar progressBar;
    String item_name, Address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
        Intent i = getIntent();
        item_name = i.getStringExtra(Config_TAG.TAG_NAME);
        web3d = (WebView) findViewById(R.id.web_view);
        progressBar = (ProgressBar) findViewById(R.id.pbar);
        //Address = "http://web3d.zimia.ir/" + item_name + "/";
        Address = "http://mehrdad.arash-hatami.ir";
        WebSettings webSetting = web3d.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        web3d.setWebViewClient(new WebViewClient());
        web3d.loadUrl(Address);
    }

    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web3d.canGoBack()) {
            web3d.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}