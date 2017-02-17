/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.hambazidemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import helper.FontHelper;

public class Rules extends AppCompatActivity {
    WebView page_content;
    TextView page_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webpage);
        page_content = (WebView) findViewById(R.id.page_content);
        WebSettings webSettings = page_content.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);
        page_content.loadUrl("file:///android_asset/rules.html");
    }
}