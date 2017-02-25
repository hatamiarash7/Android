/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package ir.hatamiarash.malayeruniversity;

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
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

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

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName(); // class's tag for log
    Button btnLogin;                                               // login button
    Button btnLinkToResetPassword;
    private EditText inputUsername;                                   // email input
    private EditText inputPassword;                                // password input
    private ProgressDialog pDialog;                                // dialog window
    private SessionManager session;                                // session for check user logged status
    private SQLiteHandler db;                                      // users database

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        inputUsername = (EditText) findViewById(R.id.username);                        // email text input
        inputPassword = (EditText) findViewById(R.id.password);                  // password text input
        btnLogin = (Button) findViewById(R.id.btnLogin);

        pDialog = new ProgressDialog(this);                                      // new dialog
        pDialog.setCancelable(false);                                            // set unacceptable dialog
        db = new SQLiteHandler(getApplicationContext());                         // users database
        db.CreateTable();                                                        // create users table
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {                                              // Check if user is already logged in or not
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);                                                    // start main activity
            finish();                                                            // close this activity
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {                     // login button's event
            public void onClick(View view) {
                String username = inputUsername.getText().toString();                      // get email from text input
                String password = inputPassword.getText().toString();                // get password from text input
                if (CheckInternet())                                                 // check network connection status
                    if (username.trim().length() > 0 && password.trim().length() > 0) { // check empty fields
                        pDialog.setMessage("در حال ورود ...");
                        showDialog();                                                // show dialog
                        CheckLogin(username, password);                                 // check user login request from server
                    } else
                        MakeToast("مشخصات را وارد نمایید");
            }
        });
    }

    private void CheckLogin(final String email, final String password) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        session.setLogin(true);
                        String id = jObj.getString(Config_TAG.UID);
                        JSONObject user = jObj.getJSONObject(Config_TAG.USER);
                        String name = user.getString(Config_TAG.NAME);
                        String email = user.getString(Config_TAG.EMAIL);
                        String username = user.getString(Config_TAG.USERNAME);
                        String type = user.getString(Config_TAG.TYPE);
                        db.addUser(id, name, email, username, type);
                        String msg = "سلام " + name;
                        MakeToast(msg);
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        MainActivity.pointer.finish();
                        startActivity(i);
                        finish();
                    } else {
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        MakeToast(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    MakeToast(error.getMessage());
                } else
                    MakeToast("خطایی رخ داده است ، اتصال به اینترنت را بررسی کنید");
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_login");
                params.put("username", email);
                params.put("password", password);
                return params;
            }
        };
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

    public boolean CheckInternet() {
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
}