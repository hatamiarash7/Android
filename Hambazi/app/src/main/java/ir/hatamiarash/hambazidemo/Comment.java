/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.hambazidemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import helper.SQLiteHandler;
import helper.SessionManager;
import ir.hatamiarash.MyToast.CustomToast;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class Comment extends AppCompatActivity {
    private static final String TAG = EditProfile.class.getSimpleName();
    private EditText subject, body;
    Button send, telegram;
    private SessionManager session;
    public static SQLiteHandler db;
    private AlertDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);
        session = new SessionManager(getApplicationContext());
        subject = (EditText) findViewById(R.id.comment_subject);
        body = (EditText) findViewById(R.id.comment_body);
        send = (Button) findViewById(R.id.comment_send);
        telegram = (Button) findViewById(R.id.comment_send_telegram);
        db = new SQLiteHandler(getApplicationContext());
        progressDialog = new SpotsDialog(this, R.style.CustomDialog);
        progressDialog.setCancelable(false);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternet()) {
                    if (session.isLoggedIn()) {
                        if (subject.length() > 0 && body.length() > 0) {
                            String msg_subject = subject.getText().toString();
                            String msg_body = body.getText().toString();
                            HashMap<String, String> user = db.getUserDetails();
                            String email = user.get(Config_TAG.EMAIL);
                            SendEmail(msg_subject, msg_body, email);
                        } else
                            MakeToast("لطفا عنوان و متن پیام را وادر نمایید", Config_TAG.WARNING);
                    } else {
                        MakeToast("لطفا برای ارسال نظر ، وارد شوید", Config_TAG.WARNING);
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternet()) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setPackage("org.telegram.messenger");
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, body.getText().toString());
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "hatamiarash7"));
                }
            }
        });
    }

    private void SendEmail(final String subject, final String body, final String sender) {
        String string_req = "req_send";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "send Response: " + response);  // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {                          // Check for error node in json
                        MakeToast("پیام شما با موفقیت ارسال شد ، متشکریم !", Config_TAG.SUCCESS);
                        Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
                        startActivity(i);
                        finish();
                    } else
                        MakeToast(jObj.getString(Config_TAG.ERROR_MSG), Config_TAG.ERROR); // show error message
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Fetch Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    MakeToast(error.getMessage(), Config_TAG.ERROR);
                } else
                    MakeToast("خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() { // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "send_comment");
                params.put(Config_TAG.EMAIL, sender);
                params.put(Config_TAG.SUBJECT, subject);
                params.put(Config_TAG.BODY, body);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
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

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}