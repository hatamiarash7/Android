package ir.adad.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import ir.adad.client.AdListener;
import ir.adad.client.AdView;
import ir.adad.client.Adad;

public class MainActivity extends Activity{

    private AdListener mAdListener = new AdListener() {

        @Override
        public void onAdLoaded() {
            Toast.makeText(getApplicationContext(), "Banner Ad loaded", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad() {
            Toast.makeText(getApplicationContext(),"Banner ad failed to load", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMessageReceive(JSONObject message) {

        }

        @Override
        public void onRemoveAdsRequested() {
            Toast.makeText(getApplicationContext(), "User requested to remove Banner ads from app", Toast.LENGTH_SHORT).show();
            //Move your user to shopping center of your app
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //***** Adad initialize should get called before setContent if you have bannerAd in this activity.
        Adad.initialize(getApplicationContext());


        setContentView(R.layout.activity_main);

        findViewById(R.id.button_start_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });

        ((AdView) findViewById(R.id.banner_ad_view)).setAdListener(mAdListener);
    }
}
