/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ir.hatamiarash.MyToast.CustomToast;
import helper.SQLiteHandlerItem;
import volley.Config_TAG;

public class Pay_Log extends Activity {
    String pay_code;
    Button download;
    TextView pay_title, pay_msg;
    SQLiteHandlerItem db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_log);
        db = new SQLiteHandlerItem(getApplicationContext()); // items database
        download = (Button) findViewById(R.id.pay_log_download);
        pay_title = (TextView) findViewById(R.id.pay_title);
        pay_msg = (TextView) findViewById(R.id.pay_msg);
        download.setVisibility(View.INVISIBLE);
        Intent i = getIntent();
        pay_code = i.getStringExtra(Config_TAG.PAY_STATUS);    // getting product id from intent
        if (pay_code.equals("error")) {
            pay_title.setText("پرداخت با خطا مواجه شد");
            pay_title.setTextColor(Color.RED);
            pay_msg.setText("لطفا مجددا تلاش نمایید. اطلاعات شما ثبت شده است و جهت پیگیری مشکل خود می توانید با ما تماس بگیرید");
        } else if (pay_code.equals("ok")) {
            pay_title.setText("پرداخت موفقیت آمیز بود");
            pay_title.setTextColor(Color.GREEN);
            int price = db.TotalPrice();
            db.deleteItems();
            String msg;
            msg = "شما در مجموع " + price + " تومان پرداخت نموده اید. جهت دانلود فاکتور خود روی دکمه ی زیر کلیک نمایید.";
            pay_msg.setText(msg);
            download.setVisibility(View.VISIBLE);
        }
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MakeToast("دانلود فاکتور", Config_TAG.WARNING);
            }
        });
    }

    private void MakeToast(String Message, String TAG) {
        if (TAG.equals(Config_TAG.WARNING))
            CustomToast.custom(this, Message, R.drawable.ic_alert, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
        if (TAG.equals(Config_TAG.SUCCESS))
            CustomToast.custom(this, Message, R.drawable.ic_success, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
        if (TAG.equals(Config_TAG.ERROR))
            CustomToast.custom(this, Message, R.drawable.ic_error, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
    }

    private boolean CheckInternet() { // check network connection for run from possible exceptions
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        PackageManager PM = getPackageManager();
        if (PM.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                return true;
        } else {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                return true;
        }
        MakeToast("اتصال به اینترنت را بررسی نمایید", Config_TAG.WARNING);
        return false;
    }
}
