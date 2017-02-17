/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import ir.hatamiarash.MyToast.CustomToast;
import ir.hatamiarash.hambazidemo.R;
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
        Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
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
        vibrator.vibrate(50);
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
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
}
