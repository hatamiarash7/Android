package ir.hatamiarash.zimia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        WebView web = (WebView) findViewById(R.id.gif2);
        web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        web.getSettings().setLoadsImagesAutomatically(true);
        web.setBackgroundColor(Color.TRANSPARENT);
        web.loadDataWithBaseURL("file:///android_asset/"
                , "<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} img{width:100%25;}</style></head><body><img src='loading.gif'/><body><html>"
                , "text/html"
                , "UTF-8"
                , "");
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashScreen.this, MainScreenActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}