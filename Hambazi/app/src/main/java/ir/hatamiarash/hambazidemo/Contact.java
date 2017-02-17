/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.hambazidemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import helper.Helper;
import volley.Config_TAG;

public class Contact extends AppCompatActivity {
    TextView PhoneText;
    TextView TelegramText;
    TextView EmailText;
    TextView WebsiteText;
    TextView LocationText;
    TextView DirectText;
    ImageView Phone;
    ImageView Telegram;
    ImageView Email;
    ImageView Website;
    ImageView Location;
    ImageView Direct;

    final static private String PhoneNumber = "+989182180519";
    final static private String WebsiteAddress = "http://zimia.ir";
    final static private String EmailAddress = "info@zimia.ir";
    final static private String ChannelAddress = "zimia_ir";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        PhoneText = (TextView) findViewById(R.id.phone_address);
        TelegramText = (TextView) findViewById(R.id.telegram_address);
        EmailText = (TextView) findViewById(R.id.email_address);
        WebsiteText = (TextView) findViewById(R.id.website_address);
        LocationText = (TextView) findViewById(R.id.location_address);
        DirectText = (TextView) findViewById(R.id.direct_address);

        Phone    = (ImageView) findViewById(R.id.contact_phone);
        Telegram = (ImageView) findViewById(R.id.contact_telegram);
        Email    = (ImageView) findViewById(R.id.contact_email);
        Website = (ImageView) findViewById(R.id.contact_website);
        Location = (ImageView) findViewById(R.id.contact_location);
        Direct = (ImageView) findViewById(R.id.contact_direct);

        PhoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + PhoneNumber));
                if (ActivityCompat.checkSelfPermission(Contact.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Contact.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    startActivity(i);
                }
            }
        });
        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + PhoneNumber));
                if (ActivityCompat.checkSelfPermission(Contact.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Contact.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    startActivity(i);
                }
            }
        });
        TelegramText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.CheckInternet(Contact.this)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("tg://resolve?domain=" + ChannelAddress));
                    startActivity(i);
                }
            }
        });
        Telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.CheckInternet(Contact.this)) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("tg://resolve?domain=" + ChannelAddress));
                    startActivity(i);
                }
            }
        });
        WebsiteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.CheckInternet(Contact.this)) {
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.addCategory(Intent.CATEGORY_BROWSABLE);
                    i.setData(Uri.parse(WebsiteAddress));
                    startActivity(i);
                }
            }
        });
        Website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.CheckInternet(Contact.this)) {
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.addCategory(Intent.CATEGORY_BROWSABLE);
                    i.setData(Uri.parse(WebsiteAddress));
                    startActivity(i);
                }
            }
        });
        EmailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.CheckInternet(Contact.this)) {
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.addCategory(Intent.CATEGORY_BROWSABLE);
                    i.setData(Uri.parse("mailto:" + EmailAddress));
                    startActivity(i);
                }
            }
        });
        Email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.CheckInternet(Contact.this)) {
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.addCategory(Intent.CATEGORY_BROWSABLE);
                    i.setData(Uri.parse("mailto:" + EmailAddress));
                    startActivity(i);
                }
            }
        });
        DirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.CheckInternet(Contact.this)) {
                    Intent i = new Intent(getApplicationContext(), Comment.class);
                    startActivity(i);
                }
            }
        });
        Direct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.CheckInternet(Contact.this)) {
                    Intent i = new Intent(getApplicationContext(), Comment.class);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    return;
                else
                    Helper.MakeToast(this, "دسترسی داده نشد", Config_TAG.ERROR);
                break;
            }
        }
    }
}