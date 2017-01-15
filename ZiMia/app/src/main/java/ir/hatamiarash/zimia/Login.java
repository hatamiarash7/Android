/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helper.FontHelper;
import helper.SQLiteHandler;
import helper.SessionManager;
import helper.TypefaceSpan;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class Login extends Activity {
    private static final String TAG = Login.class.getSimpleName(); // class's tag for log
    Button btnLogin;                                               // login button
    Button btnLinkToRegister;                                      // register activity button
    String name;
    private EditText inputEmail;                                   // email input
    private EditText inputPassword;                                // password input
    private ProgressDialog pDialog;                                // dialog window
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
        pDialog = new ProgressDialog(this);                                      // new dialog
        pDialog.setCancelable(false);                                            // set unacceptable dialog
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
                        pDialog.setMessage("در حال ورود ...");
                        showDialog();                                                // show dialog
                        CheckLogin(email, password);                                 // check user login request from server
                    } else
                        MakeToast("مشخصات را وارد نمایید");
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
    }

    private void CheckLogin(final String email, final String password) { // check login request from server
        String tag_string_req = "req_login";                             // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Method.POST, Config_URL.url_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response); // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.TAG_ERROR);
                    if (!error) {                          // Check for error node in json
                        SetUser(email);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString(Config_TAG.TAG_ERROR_MSG);
                        MakeToast(errorMsg); // show error message
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage()); // log login's error
                MakeToast(error.getMessage());                    // show error in notification
            }
        }) {
            @Override
            protected Map<String, String> getParams() {           // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_login");
                params.put(Config_TAG.TAG_EMAIL, email);
                params.put(Config_TAG.TAG_PASSWORD, password);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() { // show dialog
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() { // close dialog
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public boolean CheckInternet() { // check network connection status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else
            MakeToast("اتصال به اینترنت را بررسی نمایید");
        return false;
    }

    public void MakeToast(String Message) { // show notification with custom typeface
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }

    private void SetUser(final String email) {             // check login request from server
        String tag_string_req = "req_login";               // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Method.POST, Config_URL.url_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response); // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.TAG_ERROR);
                    if (!error) {                          // Check for error node in json
                        session.setLogin(true);            // set login status true
                        String uid = jObj.getString(Config_TAG.TAG_UID);
                        JSONObject user = jObj.getJSONObject(Config_TAG.TAG_USER);
                        String name = user.getString(Config_TAG.TAG_NAME);
                        String email = user.getString(Config_TAG.TAG_EMAIL);
                        String address = user.getString(Config_TAG.TAG_ADDRESS);
                        String phone = user.getString(Config_TAG.TAG_PHONE);
                        String type = user.getString(Config_TAG.TAG_TYPE);
                        String created_at = user.getString(Config_TAG.TAG_CREATED_AT);
                        db.addUser(name, email, address, phone, uid, type, created_at); // save user to local database
                        String msg = "سلام " + name;
                        MakeToast(msg); // show welcome notification
                        Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
                        // finish old main activity
                        MainScreenActivity.pointer.finish();
                        // start new main activity
                        startActivity(i);
                        // finish this one
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString(Config_TAG.TAG_ERROR_MSG);
                        MakeToast(errorMsg); // show error message
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage()); // log login's error
                MakeToast(error.getMessage());                    // show error in notification
            }
        }) {
            @Override
            protected Map<String, String> getParams() {           // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_set");
                params.put(Config_TAG.TAG_EMAIL, email);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}