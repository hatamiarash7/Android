/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
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

import helper.FontHelper;
import helper.Helper;
import helper.TypefaceSpan;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class ResetPassword extends Activity {
    private static final String TAG = ResetPassword.class.getSimpleName();
    private EditText inputEmail;
    private EditText inputPhone;
    private ProgressDialog pDialog;
    Helper helper;
    Button btnSet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPhone = (EditText) findViewById(R.id.phone);
        btnSet = (Button) findViewById(R.id.btnSet);
        pDialog = new ProgressDialog(this);                    // Progress dialog
        pDialog.setCancelable(false);
        helper = new Helper(inputEmail.getText().toString());
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternet())
                    //if (helper.isValidEmail())
                    ResetUserPassword(inputEmail.getText().toString(), inputPhone.getText().toString());
                //else
                //  MakeToast("ایمیل وارد شده ایراد دارد");
            }
        });
    }

    private void ResetUserPassword(final String email, final String phone) {
        // Tag used to cancel the request
        String tag_string_req = "req_reset";
        pDialog.setMessage("در حال ثبت ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Reset Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.TAG_ERROR);
                    if (!error) {
                        MakeQuestion("کلمه عبور", "کلمه عبور جدید به ایمیل شما ارسال شد");
                    } else {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString(Config_TAG.TAG_ERROR_MSG);
                        MakeToast(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Reset Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    MakeToast(error.getMessage());
                } else
                    MakeToast("خطایی رخ داده است");
                hideDialog();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_reset_password");
                params.put(Config_TAG.TAG_RESET_EMAIL, email);
                params.put(Config_TAG.TAG_EMAIL, phone);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else
            MakeToast("اتصال به اینترنت را بررسی نمایید");
        return false;
    }

    private void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }

    private void MakeQuestion(String Title, String Message) {                     // build and show an confirm window
        AlertDialog.Builder dialog = new AlertDialog.Builder(ResetPassword.this);
        dialog.setTitle(Title);                                                   // set title
        dialog.setMessage(Message);                                               // set message
        dialog.setIcon(R.drawable.ic_confirm);                                    // set icon
        dialog.setPositiveButton("تایید", new DialogInterface.OnClickListener() { // negative answer
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss(); // close dialog
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                finish();
            }
        });
        AlertDialog alert = dialog.create(); // create dialog
        alert.show();                        // show dialog
    }
}
