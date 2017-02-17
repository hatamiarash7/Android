/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.hambazidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ImageView loading = (ImageView) findViewById(R.id.loading);
        loading.setImageDrawable(getResources().getDrawable(R.drawable.loading));
        loading.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));
        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        Thread timerThread = new Thread() {
            public void run() {
                // wait some milliseconds for loading all parts of app
                try {
                    sleep(500);
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
        super.onPause();
        finish();
    }
}