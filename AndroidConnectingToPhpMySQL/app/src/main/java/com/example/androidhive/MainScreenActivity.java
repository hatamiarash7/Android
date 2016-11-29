package com.example.androidhive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;


public class MainScreenActivity extends Activity {

    Button btnViewResturans;
    Button btnViewFastFoods;
    Button btnViewMarkets;
    Button btnNewResturan;
    Button btnNewFastFood;
    Button btnNewMarket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.main_screen);

        // Buttons
        btnNewResturan = (Button) findViewById(R.id.btnCreateResturan);
        btnNewFastFood = (Button) findViewById(R.id.btnCreateFastFood);
        btnNewMarket = (Button) findViewById(R.id.btnCreateMarket);
        btnViewResturans = (Button) findViewById(R.id.btnViewResturans);
        btnViewFastFoods = (Button) findViewById(R.id.btnViewFastFoods);
        btnViewMarkets = (Button) findViewById(R.id.btnViewMarkets);


        // view products click event
        btnNewResturan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), NewResturan.class);
                startActivity(i);
            }
        });
        // view products click event
        btnNewFastFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), NewFastFood.class);
                startActivity(i);
            }
        });
        // view products click event
        btnNewMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), NewMarket.class);
                startActivity(i);
            }
        });
        btnViewResturans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), AllResturans.class);
                startActivity(i);
            }
        });
        btnViewFastFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), AllFastFoods.class);
                startActivity(i);
            }
        });
        btnViewMarkets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), AllMarkets.class);
                startActivity(i);
            }
        });
    }
}