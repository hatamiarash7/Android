/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.hambazidemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import ir.hatamiarash.MyToast.CustomToast;
import helper.SQLiteHandler;
import helper.SessionManager;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class Login extends Activity {
    private static final String TAG = Login.class.getSimpleName(); // class's tag for log
    Button btnLogin;                                               // login button
    Button btnLinkToRegister;                                      // register activity button
    Button btnLinkToResetPassword;
    private EditText inputEmail;                                   // email input
    private EditText inputPassword;                                // password input
    private AlertDialog progressDialog;                                // dialog window
    private SessionManager session;                                // session for check user logged status
    private SQLiteHandler db;                                      // users database

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        inputEmail = (EditText) findViewById(R.id.email);                        // email text input
        inputPassword = (EditText) findViewById(R.id.password);                  // password text input
        btnLogin = (Button) findViewById(R.id.btnLogin);                         // login button
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen); // register activity button
        btnLinkToResetPassword = (Button) findViewById(R.id.btnLinkToResetPassword);
        progressDialog = new SpotsDialog(this, R.style.CustomDialog);                   // new dialog
        progressDialog.setCancelable(false);                                            // set unacceptable dialog
        db = new SQLiteHandler(getApplicationContext());                         // users database
        db.CreateTable();                                                        // create users table
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {                                              // Check if user is already logged in or not
            Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
            startActivity(i);                                                    // start main activity
            finish();                                                            // close this activity
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {                     // login button's event
            public void onClick(View view) {
                String email = inputEmail.getText().toString();                      // get email from text input
                String password = inputPassword.getText().toString();                // get password from text input
                if (CheckInternet())                                                 // check network connection status
                    if (email.trim().length() > 0 && password.trim().length() > 0) { // check empty fields
                        showDialog();                                                // show dialog
                        CheckLogin(email, password);                                 // check user login request from server
                    } else
                        MakeToast("مشخصات را وارد نمایید", Config_TAG.WARNING);
            }
        });
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {            // register button's event
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                // start register activity
                startActivity(i);
                // finish this one
                finish();
            }
        });
        btnLinkToResetPassword.setOnClickListener(new View.OnClickListener() {            // register button's event
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ResetPassword.class);
                // start register activity
                startActivity(i);
                // finish this one
                finish();
            }
        });
    }

    private void CheckLogin(final String email, final String password) { // check login request from server
        String string_req = "req_login";                             // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response); // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {                          // Check for error node in json
                        SetUser(email);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        MakeToast(errorMsg, Config_TAG.ERROR); // show error message
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    MakeToast(error.getMessage(), Config_TAG.ERROR);
                } else
                    MakeToast("خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {           // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_login");
                params.put(Config_TAG.EMAIL, email);
                params.put(Config_TAG.PASSWORD, password);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    private void SetUser(final String email) {             // check login request from server
        String string_req = "req_login";               // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response); // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {                          // Check for error node in json
                        session.setLogin(true);            // set login status true
                        String uid = jObj.getString(Config_TAG.UID);
                        JSONObject user = jObj.getJSONObject(Config_TAG.USER);
                        String name = user.getString(Config_TAG.NAME);
                        String email = user.getString(Config_TAG.EMAIL);
                        String address = user.getString(Config_TAG.ADDRESS);
                        String phone = user.getString(Config_TAG.PHONE);
                        String type = user.getString(Config_TAG.TYPE);
                        String created_at = user.getString(Config_TAG.CREATED_AT);
                        db.addUser(name, email, address, phone, uid, type, created_at); // save user to local database
                        String msg = "سلام " + name;
                        MakeToast(msg, Config_TAG.WARNING); // show welcome notification
                        Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
                        // finish old main activity
                        MainScreenActivity.pointer.finish();
                        // start new main activity
                        startActivity(i);
                        // finish this one
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        MakeToast(errorMsg, Config_TAG.ERROR); // show error message
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    MakeToast(error.getMessage(), Config_TAG.ERROR);
                } else
                    MakeToast("خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {           // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_set");
                params.put(Config_TAG.EMAIL, email);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    private void showDialog() { // show dialog
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() { // close dialog
        if (progressDialog.isShowing())
            progressDialog.dismiss();
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