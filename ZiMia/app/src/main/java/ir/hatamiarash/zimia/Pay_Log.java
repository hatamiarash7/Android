/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import helper.FontHelper;
import helper.SQLiteHandlerItem;
import helper.TypefaceSpan;
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
        pay_code = i.getStringExtra(Config_TAG.TAG_PAY_STATUS);    // getting product id from intent
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
                MakeToast("دانلود فاکتور");
            }
        });
    }

    public void MakeToast(String Message) { // build and show notification with custom typeface
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }
}
