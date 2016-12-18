package ir.hatamiarash.mysql;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;


public class MainScreenActivity extends AppCompatActivity {
    Button btnViewResturans;
    Button btnViewFastFoods;
    Button btnViewMarkets;

    Button btnClosePopup;
    private PopupWindow pwindo;
    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pwindo.dismiss();

        }
    };

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            initiatePopupWindow(id);
            return true;
        }
        if (id == R.id.action_help) {
            initiatePopupWindow(id);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initiatePopupWindow(int id) {
        try {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) MainScreenActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.about, (ViewGroup) findViewById(R.id.popup_about));
            if (id == R.id.action_about) {
                layout = inflater.inflate(R.layout.about, (ViewGroup) findViewById(R.id.popup_about));
            }
            if (id == R.id.action_help) {
                layout = inflater.inflate(R.layout.help, (ViewGroup) findViewById(R.id.popup_help));
            }
            pwindo = new PopupWindow(layout, width - 300, height - 300, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
            btnClosePopup.setOnClickListener(cancel_button_click_listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}