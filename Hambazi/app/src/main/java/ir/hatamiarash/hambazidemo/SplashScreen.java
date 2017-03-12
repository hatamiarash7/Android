/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.hambazidemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.Wave;

public class SplashScreen extends AppCompatActivity {
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //ImageView loading = (ImageView) findViewById(R.id.loading);
        ImageView logo = (ImageView) findViewById(R.id.logo);
        spinner = (ProgressBar) findViewById(R.id.spinner);

        Wave animation = new Wave();
        spinner.setIndeterminateDrawable(animation);

        //loading.setImageDrawable(getResources().getDrawable(R.drawable.loading));
        //loading.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));
        logo.setImageDrawable(getResources().getDrawable(R.drawable.logo));

        Thread timerThread = new Thread() {
            public void run() {
                // wait some milliseconds for loading all parts of app
                try {
                    sleep(3000);
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