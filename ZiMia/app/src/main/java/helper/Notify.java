/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package helper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Toast;

import ir.hatamiarash.zimia.R;

public class Notify extends AppCompatActivity {
    private String TITLE;
    private String MESSAGE;
    private Context CONTEXT;
    private View VIEW;
    private Class CLASS;

    public Notify(String TITLE, String MESSAGE, Class CLASS) {
        this.TITLE = TITLE;
        this.MESSAGE = MESSAGE;
        this.CLASS = CLASS;
    }

    public Notify(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public Notify(String MESSAGE, View VIEW) {
        this.MESSAGE = MESSAGE;
        this.VIEW = VIEW;
    }

    public void MakeNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_alert);
        mBuilder.setContentTitle(this.TITLE);
        mBuilder.setContentText(this.MESSAGE);
        Intent intent = new Intent(this, CLASS);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(CLASS);
        stackBuilder.addNextIntent(intent);
        PendingIntent resault = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resault);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1001, mBuilder.build());
    }

    public void MakeToast() {
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(this.MESSAGE);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }

    public void MakeSnack() {
        Snackbar.make(this.VIEW, this.MESSAGE, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }
}
