/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package helper;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;

import ir.hatamiarash.MyToast.CustomToast;
import ir.hatamiarash.malayeruniversity.R;
import volley.Config_TAG;

public class Helper {
    private String Email_Address;
    private String Phone_Number;

    public Helper(String TAG, String VALUE) {
        switch (TAG) {
            case "email":
                this.Email_Address = VALUE;
                break;
            case "phone":
                this.Phone_Number = VALUE;
                break;
        }
    }

    public boolean isValidEmail() {
        return Email_Address.endsWith("@gmail.com") || Email_Address.endsWith("@yahoo.com") || Email_Address.endsWith("@live.com")
                || Email_Address.endsWith("@outlook.com");
    }

    public boolean isValidPhone() {
        return Phone_Number.startsWith("09") && Phone_Number.length() == 11;
    }

    static String SumString(String val1, String val2) {
        if (val2.equals(""))
            return val1;
        else
            return String.valueOf(Integer.valueOf(val1) + Integer.valueOf(val2));
    }

    public static boolean CheckInternet(Context context) { // check network connection for run from possible exceptions
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        PackageManager PM = context.getPackageManager();
        if (PM.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                return true;
        } else {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                return true;
        }
        Helper.MakeToast(context, "اتصال به اینترنت را بررسی نمایید", Config_TAG.WARNING);
        return false;
    }

    public static void MakeToast(Context context, String Message, String TAG) {
        if (TAG.equals(Config_TAG.WARNING))
            CustomToast.custom(context, Message, R.drawable.ic_alert, context.getResources().getColor(R.color.black), context.getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
        if (TAG.equals(Config_TAG.SUCCESS))
            CustomToast.custom(context, Message, R.drawable.ic_success, context.getResources().getColor(R.color.black), context.getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
        if (TAG.equals(Config_TAG.ERROR))
            CustomToast.custom(context, Message, R.drawable.ic_error, context.getResources().getColor(R.color.black), context.getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
    }

    public static TextWatcher TextAutoResize(final Context context, final TextView view, final int MIN_SP, final int MAX_SP) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                final int widthLimitPixels = view.getWidth() - view.getPaddingRight() - view.getPaddingLeft();
                Paint paint = new Paint();
                float fontSizeSP = PixelsToSP(context, view.getTextSize());
                paint.setTextSize(SPToPixels(context, fontSizeSP));
                String viewText = view.getText().toString();
                float widthPixels = paint.measureText(viewText);
                if (widthPixels < widthLimitPixels) {
                    while (widthPixels < widthLimitPixels && fontSizeSP <= MAX_SP) {
                        ++fontSizeSP;
                        paint.setTextSize(SPToPixels(context, fontSizeSP));
                        widthPixels = paint.measureText(viewText);
                    }
                    --fontSizeSP;
                } else {
                    while (widthPixels > widthLimitPixels || fontSizeSP > MAX_SP) {
                        if (fontSizeSP < MIN_SP) {
                            fontSizeSP = MIN_SP;
                            break;
                        }
                        --fontSizeSP;
                        paint.setTextSize(SPToPixels(context, fontSizeSP));
                        widthPixels = paint.measureText(viewText);
                    }
                }
                view.setTextSize(fontSizeSP);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final int widthLimitPixels = view.getWidth() - view.getPaddingRight() - view.getPaddingLeft();
                Paint paint = new Paint();
                float fontSizeSP = PixelsToSP(context, view.getTextSize());
                paint.setTextSize(SPToPixels(context, fontSizeSP));
                String viewText = view.getText().toString();
                float widthPixels = paint.measureText(viewText);
                if (widthPixels < widthLimitPixels) {
                    while (widthPixels < widthLimitPixels && fontSizeSP <= MAX_SP) {
                        ++fontSizeSP;
                        paint.setTextSize(SPToPixels(context, fontSizeSP));
                        widthPixels = paint.measureText(viewText);
                    }
                    --fontSizeSP;
                } else {
                    while (widthPixels > widthLimitPixels || fontSizeSP > MAX_SP) {
                        if (fontSizeSP < MIN_SP) {
                            fontSizeSP = MIN_SP;
                            break;
                        }
                        --fontSizeSP;
                        paint.setTextSize(SPToPixels(context, fontSizeSP));
                        widthPixels = paint.measureText(viewText);
                    }
                }
                view.setTextSize(fontSizeSP);
            }

            @Override
            public void afterTextChanged(Editable s) {
                final int widthLimitPixels = view.getWidth() - view.getPaddingRight() - view.getPaddingLeft();
                Paint paint = new Paint();
                float fontSizeSP = PixelsToSP(context, view.getTextSize());
                paint.setTextSize(SPToPixels(context, fontSizeSP));
                String viewText = view.getText().toString();
                float widthPixels = paint.measureText(viewText);
                if (widthPixels < widthLimitPixels) {
                    while (widthPixels < widthLimitPixels && fontSizeSP <= MAX_SP) {
                        ++fontSizeSP;
                        paint.setTextSize(SPToPixels(context, fontSizeSP));
                        widthPixels = paint.measureText(viewText);
                    }
                    --fontSizeSP;
                } else {
                    while (widthPixels > widthLimitPixels || fontSizeSP > MAX_SP) {
                        if (fontSizeSP < MIN_SP) {
                            fontSizeSP = MIN_SP;
                            break;
                        }
                        --fontSizeSP;
                        paint.setTextSize(SPToPixels(context, fontSizeSP));
                        widthPixels = paint.measureText(viewText);
                    }
                }
                view.setTextSize(fontSizeSP);
            }
        };
    }

    public static float PixelsToSP(Context context, float PX) {
        float ScaleDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return PX / ScaleDensity;
    }

    public static float SPToPixels(Context context, float PX) {
        float ScaleDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return PX * ScaleDensity;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("HardwareIds")
    public static String GenerateDeviceIdentifier(Context context) {
        String pseudoId = "35" +
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10;
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String btId = "";
        if (bluetoothAdapter != null)
            btId = bluetoothAdapter.getAddress();
        String longId = pseudoId + androidId + btId;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(longId.getBytes(), 0, longId.length());
            byte md5Bytes[] = messageDigest.digest();
            String identifier = "";
            for (byte md5Byte : md5Bytes) {
                int b = (0xFF & md5Byte);
                if (b <= 0xF)
                    identifier += "0";
                identifier += Integer.toHexString(b);
            }
            identifier = identifier.toUpperCase();
            Log.e("Device Identifier", "ID : " + identifier);
            return identifier;
        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }
        return "null";
    }
}
