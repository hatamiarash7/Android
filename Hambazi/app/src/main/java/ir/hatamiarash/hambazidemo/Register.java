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
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import ir.hatamiarash.MyToast.CustomToast;
import helper.FontHelper;
import helper.Helper;
import helper.SQLiteHandler;
import helper.SessionManager;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class Register extends Activity {
    private static final String TAG = Register.class.getSimpleName();
    Button btnRegister;
    Button btnLinkToLogin;
    SessionManager session;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPassword2;
    private EditText inputAddress;
    private EditText inputPhone;
    private EditText inputResetEmail;
    private AlertDialog progressDialog;
    private SQLiteHandler db;
    private Helper CheckEmail, CheckPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPassword2 = (EditText) findViewById(R.id.password2);
        inputAddress = (EditText) findViewById(R.id.address);
        inputResetEmail = (EditText) findViewById(R.id.reset_email);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        inputPhone = inputEmail;
        inputEmail.setError("همانند نمونه 09123456789");
        inputPassword.setError("حداقل 8 حرف");
        progressDialog = new SpotsDialog(this, R.style.CustomDialog);
        progressDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());// Session manager
        db = new SQLiteHandler(getApplicationContext());      // SQLite database handler
        if (session.isLoggedIn()) { // Check if user is already logged in or not
            // User is already logged in. Take him to main activity
            Intent i = new Intent(getApplicationContext(), UserProfile.class);
            startActivity(i);
            finish();
        }
        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String password2 = inputPassword2.getText().toString();
                String address = inputAddress.getText().toString();
                String phone = inputPhone.getText().toString();
                String reset_email = inputResetEmail.getText().toString();
                CheckEmail = new Helper("email", reset_email);
                CheckPhone = new Helper("phone", phone);
                if (CheckInternet())
                    if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !password2.isEmpty() && !address.isEmpty() && !phone.isEmpty())
                        if (CheckPhone.isValidPhone())
                            if (CheckEmail.isValidEmail())
                                if (password.length() >= 8)
                                    if (password.equals(password2))
                                        registerUser(name, email, password, address, phone, "User", reset_email);
                                    else
                                        MakeToast("کلمه عبور تطابق ندارد", Config_TAG.WARNING);
                                else
                                    MakeToast("کلمه عبور کوتاه است", Config_TAG.WARNING);
                            else
                                MakeToast("ایمیل را بررسی نمایید", Config_TAG.WARNING);
                        else
                            MakeToast("شماره موبایل را بررسی نمایید", Config_TAG.WARNING);
                    else
                        MakeToast("تمامی کادر ها را پر نمایید", Config_TAG.WARNING);
            }
        });
        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void registerUser(final String name, final String email, final String password, final String address, final String phone, final String type, final String reset_email) {
        // Tag used to cancel the request
        String string_req = "req_register";
        showDialog();
        StringRequest strReq = new StringRequest(Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // User successfully stored in MySQL , Now store the user in SQLite
                        String uid = jObj.getString(Config_TAG.UID);
                        JSONObject user = jObj.getJSONObject(Config_TAG.USER);
                        String name = user.getString(Config_TAG.NAME);
                        String email = user.getString(Config_TAG.EMAIL);
                        String address = user.getString(Config_TAG.ADDRESS);
                        String phone = user.getString(Config_TAG.PHONE);
                        String created_at = user.getString(Config_TAG.CREATED_AT);
                        // Inserting row in users table
                        db.addUser(name, email, address, phone, uid, created_at, type);
                        MakeDialog("ثبت نام انجام شد", "نام کاربری شما تلفن همراهتان می باشد");
                    } else {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        MakeToast(errorMsg, Config_TAG.ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    MakeToast(error.getMessage(), Config_TAG.ERROR);
                } else
                    MakeToast("خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_register");
                params.put(Config_TAG.NAME, name);
                params.put(Config_TAG.EMAIL, email);
                params.put(Config_TAG.ADDRESS, address);
                params.put(Config_TAG.PHONE, phone);
                params.put(Config_TAG.PASSWORD, password);
                params.put(Config_TAG.TYPE, type);
                params.put(Config_TAG.RESET_EMAIL, reset_email);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
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

    private void MakeDialog(String Title, String Message) {
        new MaterialStyledDialog.Builder(this)
                .setTitle(FontHelper.getSpannedString(this, Title))
                .setDescription(FontHelper.getSpannedString(this, Message))
                .setStyle(Style.HEADER_WITH_TITLE)
                .withDarkerOverlay(true)
                .withDialogAnimation(true)
                .setCancelable(true)
                .setPositiveText("s")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}