/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package ir.hatamiarash.malayeruniversity;

import android.animation.Animator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
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
    private TextView name, job;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        back = (ImageView) findViewById(R.id.back);
        telegram = (LinearLayout) findViewById(R.id.telegram);
        instagram = (LinearLayout) findViewById(R.id.instagram);
        name = (TextView) findViewById(R.id.name);
        job = (TextView) findViewById(R.id.job);

        name.setVisibility(View.INVISIBLE);
        job.setVisibility(View.INVISIBLE);
        telegram.setVisibility(View.INVISIBLE);
        instagram.setVisibility(View.INVISIBLE);

        YoYo.with(Techniques.SlideInLeft)
                .interpolate(new AccelerateDecelerateInterpolator())
                .duration(500)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        name.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.SlideInDown)
                                .interpolate(new AccelerateDecelerateInterpolator())
                                .duration(1000)
                                .withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        job.setVisibility(View.VISIBLE);
                                        YoYo.with(Techniques.SlideInDown)
                                                .interpolate(new AccelerateDecelerateInterpolator())
                                                .duration(1000)
                                                .withListener(new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {
                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        telegram.setVisibility(View.VISIBLE);
                                                        YoYo.with(Techniques.SlideInLeft)
                                                                .interpolate(new AccelerateDecelerateInterpolator())
                                                                .duration(1000)
                                                                .withListener(new Animator.AnimatorListener() {
                                                                    @Override
                                                                    public void onAnimationStart(Animator animation) {
                                                                    }

                                                                    @Override
                                                                    public void onAnimationEnd(Animator animation) {
                                                                        instagram.setVisibility(View.VISIBLE);
                                                                        YoYo.with(Techniques.SlideInRight)
                                                                                .interpolate(new AccelerateDecelerateInterpolator())
                                                                                .duration(1000)
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