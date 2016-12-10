package ir.hatamiarash.mysql;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainScreenActivity extends AppCompatActivity {
    Button btnViewResturans;
    Button btnViewFastFoods;
    Button btnViewMarkets;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.main_screen);

        btnViewResturans = (Button) findViewById(R.id.btnViewResturans);
        btnViewFastFoods = (Button) findViewById(R.id.btnViewFastFoods);
        btnViewMarkets = (Button) findViewById(R.id.btnViewMarkets);

        btnViewResturans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AllResturans.class);
                startActivity(i);
            }
        });
        btnViewFastFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AllFastFoods.class);
                startActivity(i);
            }
        });
        btnViewMarkets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AllMarkets.class);
                startActivity(i);
            }
        });
    }
}