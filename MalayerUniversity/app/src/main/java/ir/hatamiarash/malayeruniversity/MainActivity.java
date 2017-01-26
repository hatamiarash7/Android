package ir.hatamiarash.malayeruniversity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {
    private static ViewPager mPager;
    private static int currentPage = 0 ;
    private static int NUM_PAGES = 0;
    private static final Integer[] IMAGES={R.drawable.one,R.drawable.two,R.drawable.three,R.drawable.five};
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init(){
        for(int i=0;i<IMAGES.length;i++)
            ImagesArray.add(IMAGES[i]);
        mPager=(ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImages_adapter(MainActivity.this, ImagesArray));
        CirclePageIndicator indicator=(CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5*density);
        NUM_PAGES=IMAGES.length;
        final Handler handler=new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
        if(currentPage==NUM_PAGES){
            currentPage=0;
        }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
        handler.post(Update);
            }
        }, 3000, 3000);
        indicator.s
    }
}