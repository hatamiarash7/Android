/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package ir.hatamiarash.malayeruniversity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import helper.Helper;

public class Contact extends AppCompatActivity {
    ImageView back;
    private LinearLayout telegram, instagram;
    TextView name, job, instagram_name, telegram_name, count;
    TextView copyright;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        Intent i = getIntent();
        String device_count = i.getStringExtra("devices");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        back = (ImageView) findViewById(R.id.back);
        telegram = (LinearLayout) findViewById(R.id.telegram);
        instagram = (LinearLayout) findViewById(R.id.instagram);
        name = (TextView) findViewById(R.id.name);
        job = (TextView) findViewById(R.id.job);
        copyright = (TextView) findViewById(R.id.copyright);
        instagram_name = (TextView) findViewById(R.id.instagram_name);
        telegram_name = (TextView) findViewById(R.id.telegram_name);
        count = (TextView) findViewById(R.id.count);

        name.setVisibility(View.INVISIBLE);
        job.setVisibility(View.INVISIBLE);
        telegram.setVisibility(View.INVISIBLE);
        instagram.setVisibility(View.INVISIBLE);
        instagram_name.addTextChangedListener(Helper.TextAutoResize(this, instagram_name, 14, 20));
        telegram_name.addTextChangedListener(Helper.TextAutoResize(this, telegram_name, 14, 20));
        count.setText("Total Installed : " + device_count);

        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            copyright.setText("2017 - v" + version);
        } catch (PackageManager.NameNotFoundException e) {
            copyright.setText("2017 - MalayerUniversity");
        }

        YoYo.with(Techniques.SlideInLeft)
                .interpolate(new AccelerateDecelerateInterpolator())
                .duration(900)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        name.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.SlideInDown)
                                .interpolate(new AccelerateDecelerateInterpolator())
                                .duration(800)
                                .withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        job.setVisibility(View.VISIBLE);
                                        YoYo.with(Techniques.SlideInDown)
                                                .interpolate(new AccelerateDecelerateInterpolator())
                                                .duration(800)
                                                .withListener(new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {
                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        telegram.setVisibility(View.VISIBLE);
                                                        YoYo.with(Techniques.SlideInLeft)
                                                                .interpolate(new AccelerateDecelerateInterpolator())
                                                                .duration(800)
                                                                .withListener(new Animator.AnimatorListener() {
                                                                    @Override
                                                                    public void onAnimationStart(Animator animation) {
                                                                    }

                                                                    @Override
                                                                    public void onAnimationEnd(Animator animation) {
                                                                        instagram.setVisibility(View.VISIBLE);
                                                                        YoYo.with(Techniques.SlideInRight)
                                                                                .interpolate(new AccelerateDecelerateInterpolator())
                                                                                .duration(800)
                                                                                .playOn(instagram);
                                                                    }

                                                                    @Override
                                                                    public void onAnimationCancel(Animator animation) {
                                                                    }

                                                                    @Override
                                                                    public void onAnimationRepeat(Animator animation) {
                                                                    }
                                                                })
                                                                .playOn(telegram);
                                                    }

                                                    @Override
                                                    public void onAnimationCancel(Animator animation) {
                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animator animation) {
                                                    }
                                                })
                                                .playOn(job);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {
                                    }
                                })
                                .playOn(name);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                })
                .playOn(back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.CheckInternet(Contact.this)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("tg://resolve?domain=hatamiarash7"));
                    startActivity(i);
                }
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.CheckInternet(Contact.this)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/hatamiarash7"));
                    intent.setPackage("com.instagram.android");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/hatamiarash7")));
                    }
                }
            }
        });
    }
}