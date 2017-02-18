/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Test extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);



        /*TextView textView = (TextView) findViewById(R.id.sample);
        final TextView textView2 = (TextView) findViewById(R.id.textView9);
        textView2.setVisibility(View.INVISIBLE);

        YoYo.with(Techniques.ZoomInUp)
                .interpolate(new AccelerateDecelerateInterpolator())
                .duration(2000)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        textView2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                })
                .playOn(textView);*/
    }
}