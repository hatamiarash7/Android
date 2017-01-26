/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import helper.FontHelper;
import helper.SQLiteHandlerSetup;
import helper.TypefaceSpan;

public class SetupWeb extends Activity {
    private static final String TAG = SetupWeb.class.getSimpleName();
    RadioGroup Check1, Check2;
    private int C1 = 0, C2 = 0;
    Button NextLevel;
    private Boolean selected = false, selected2 = false;
    private static int sessionDepth = 0;
    private SQLiteHandlerSetup db;
    private WebView web3d;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_web);
        Check1 = (RadioGroup) findViewById(R.id.Check1);
        Check2 = (RadioGroup) findViewById(R.id.Check2);
        NextLevel = (Button) findViewById(R.id.Next);
        web3d = (WebView) findViewById(R.id.web_test);
        progressBar = (ProgressBar) findViewById(R.id.pbar);
        progressBar.setVisibility(View.GONE);
        db = new SQLiteHandlerSetup(getApplicationContext());
        String Address = "http://mehrdad.arash-hatami.ir";
        WebSettings webSetting = web3d.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        web3d.setWebViewClient(new WebViewClient());
        web3d.loadUrl(Address);
        Check1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.Yes1:
                        C1 = 1;
                        selected = true;
                        break;
                    case R.id.No1:
                        C1 = 0;
                        selected = true;
                        break;
                }
            }
        });
        Check2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.Yes2:
                        C2 = 1;
                        selected2 = true;
                        break;
                    case R.id.No2:
                        C2 = 0;
                        selected2 = true;
                        break;
                }
            }
        });
        NextLevel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (selected && selected2) {
                    if (C1 + C2 == 2)
                        db.AddProperty(1);
                    else
                        db.AddProperty(0);
                    SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
                    settings.edit().putBoolean("my_first_time", false).commit();
                    Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
                    startActivity(i);
                    finish();
                } else
                    MakeToast("یک گزینه را انتخاب نمایید");
            }
        });
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

    private void MakeToast(String Message) { // build and show toast with custom typeface
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        MakeToast("لطفا تنظیمات را انجام دهید");
    }

    @Override
    protected void onStart() {
        super.onStart();
        sessionDepth++;
    }

    @Override
    protected void onPause() {
        getSharedPreferences("PREFERENCES", MODE_PRIVATE).edit().putBoolean("isFirstRun", true).apply();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sessionDepth > 0)
            sessionDepth--;
        if (sessionDepth == 0)
            getSharedPreferences("PREFERENCES", MODE_PRIVATE).edit().putBoolean("isFirstRun", true).apply();
    }
}