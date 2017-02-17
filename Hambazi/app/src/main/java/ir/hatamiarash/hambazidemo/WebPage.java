/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.hambazidemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import volley.Config_TAG;

public class WebPage extends AppCompatActivity {
    WebView page_content;
    TextView page_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webpage);
        Intent i = getIntent();
        String address = i.getStringExtra(Config_TAG.NAME);
        page_content = (WebView) findViewById(R.id.page_content);
        WebSettings webSettings = page_content.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        page_content.loadUrl("http://hambazi.tv/?video_type=" + address);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        page_content.destroy();
    }
}