/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package ir.hatamiarash.malayeruniversity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helper.FontHelper;
import helper.Helper;
import helper.SQLiteHandler;
import helper.SessionManager;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    Button btnLogin;
    Button btnLinkToResetPassword;
    private EditText inputUsername;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToResetPassword = (Button) findViewById(R.id.btnLinkToResetPassword);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());
        db.CreateTable();
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                if (Helper.CheckInternet(LoginActivity.this))
                    if (username.trim().length() > 0 && password.trim().length() > 0) {
                        CheckLogin(username, password);
                    } else
                        Helper.MakeToast(LoginActivity.this, "مشخصات را وارد نمایید", Config_TAG.ERROR);
            }
        });
        btnLinkToResetPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new MaterialStyledDialog.Builder(LoginActivity.this)
                        .setTitle(FontHelper.getSpannedString(LoginActivity.this, "با سلام"))
                        .setDescription(FontHelper.getSpannedString(LoginActivity.this, "کاربر گرامی ، جهت دریافت رمز عبور جدید لطفا با مسئول مربوطه تماس حاصل فرمایید"))
                        .setStyle(Style.HEADER_WITH_TITLE)
                        .withDarkerOverlay(true)
                        .withDialogAnimation(true)
                        .setCancelable(false)
                        .setPositiveText(FontHelper.getSpannedString(LoginActivity.this, "باشه"))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Log.d(TAG, "DialogPositive");
                            }
                        })
                        .show();
            }
        });
    }

    private void CheckLogin(final String email, final String password) {
        String tag_string_req = "req_login";
        pDialog.setMessage(FontHelper.getSpannedString(this, "در حال ورود ..."));
        showDialog();
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
                        new MaterialStyledDialog.Builder(LoginActivity.this)
                                .setTitle(FontHelper.getSpannedString(LoginActivity.this, msg))
                                .setDescription(FontHelper.getSpannedString(LoginActivity.this, "مدیر گرامی ، به علت این که این برنامه هم اکنون در مراحل آزمایشی قرار دارد لطفا مشکلات برنامه ، نظرات و پیشنهادات خود را جهت هرچه بهتر شدن امور به مسئول مربوطه انتقال دهید.\nبا تشکر ، حاتمی"))
                                .setStyle(Style.HEADER_WITH_TITLE)
                                .withDarkerOverlay(true)
                                .withDialogAnimation(true)
                                .setCancelable(false)
                                .setPositiveText(FontHelper.getSpannedString(LoginActivity.this, "باشه"))
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Log.d(TAG, "DialogPositive");
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        MainActivity.pointer.finish();
                                        startActivity(i);
                                        finish();
                                    }
                                })
                                .show();
                    } else {
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        Helper.MakeToast(LoginActivity.this, errorMsg, Config_TAG.ERROR);
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
                    Helper.MakeToast(LoginActivity.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(LoginActivity.this, "خطایی رخ داده است ، اتصال به اینترنت را بررسی کنید", Config_TAG.ERROR);
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
}