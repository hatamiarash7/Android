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
import android.widget.RadioGroup;
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

public class Register extends Activity {
    private static final String TAG = Register.class.getSimpleName();
    Button btnRegister;
    Button btnLinkToLogin;
    SessionManager session;
    RadioGroup RadioGroup;
    String UserType = "null";
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPassword2;
    private EditText inputAddress;
    private EditText inputPhone;
    private ProgressDialog pDialog;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPassword2 = (EditText) findViewById(R.id.password2);
        inputAddress = (EditText) findViewById(R.id.address);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        RadioGroup = (RadioGroup) findViewById(R.id.TypeRadioGroup);
        inputPhone = inputEmail;
        inputEmail.setError("همانند نمونه 09123456789");
        inputPassword.setError("حداقل 8 حرف");
        pDialog = new ProgressDialog(this);                   // Progress dialog
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());// Session manager
        db = new SQLiteHandler(getApplicationContext());      // SQLite database handler
        if (session.isLoggedIn()) { // Check if user is already logged in or not
            // User is already logged in. Take him to main activity
            Intent i = new Intent(getApplicationContext(), UserProfile.class);
            startActivity(i);
            finish();
        }
        // User Select Radio Buttons - find by RadioButton id
        RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.UserRadioButton) {          // user button
                    UserType = "User";
                } else if (checkedId == R.id.SellerRadioButton) { // seller button
                    UserType = "Seller";
                }
            }
        });
        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String password2 = inputPassword2.getText().toString();
                String address = inputAddress.getText().toString();
                String phone = inputPhone.getText().toString();
                if (CheckInternet())
                    if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !password2.isEmpty() && !address.isEmpty() && !phone.isEmpty())
                        if (phone.startsWith("09") && phone.length() == 11)
                            if (password.length() >= 8)
                                if (password.equals(password2))
                                    registerUser(name, email, password, address, phone, UserType);
                                else
                                    MakeToast("کلمه عبور تطابق ندارد");
                            else
                                MakeToast("کلمه عبور تطابق ندارد");
                        else
                            MakeToast("شماره موبایل را بررسی نمایید");
                    else
                        MakeToast("تمامی کادر ها را پر نمایید");
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

    private void registerUser(final String name, final String email, final String password, final String address, final String phone, final String type) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        pDialog.setMessage("در حال ثبت ...");
        showDialog();
        StringRequest strReq = new StringRequest(Method.POST, Config_URL.url_register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.TAG_ERROR);
                    if (!error) {
                        // User successfully stored in MySQL , Now store the user in SQLite
                        String uid = jObj.getString(Config_TAG.TAG_UID);
                        JSONObject user = jObj.getJSONObject(Config_TAG.TAG_USER);
                        String name = user.getString(Config_TAG.TAG_NAME);
                        String email = user.getString(Config_TAG.TAG_EMAIL);
                        String address = user.getString(Config_TAG.TAG_ADDRESS);
                        String phone = user.getString(Config_TAG.TAG_PHONE);
                        String created_at = user.getString(Config_TAG.TAG_CREATED_AT);
                        // Inserting row in users table
                        db.addUser(name, email, address, phone, uid, created_at, type);
                        MakeQuestion("ثبت نام انجام شد", "نام کاربری شما تلفن همراهتان می باشد");
                        Intent intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
                        finish();
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                MakeToast(error.getMessage());
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_register");
                params.put(Config_TAG.TAG_NAME, name);
                params.put(Config_TAG.TAG_EMAIL, email);
                params.put(Config_TAG.TAG_ADDRESS, address);
                params.put(Config_TAG.TAG_PHONE, phone);
                params.put(Config_TAG.TAG_PASSWORD, password);
                params.put(Config_TAG.TAG_TYPE, type);
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

    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else
            MakeToast("اتصال به اینترنت را بررسی نمایید");
        return false;
    }

    public void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }

    public void MakeQuestion(String Title, String Message) {                     // build and show an confirm window
        AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this);
        dialog.setTitle(Title);                                                  // set title
        dialog.setMessage(Message);                                              // set message
        dialog.setIcon(R.drawable.ic_confirm);                                   // set icon
        dialog.setNegativeButton("تایید", new DialogInterface.OnClickListener() { // negative answer
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss(); // close dialog
            }
        });
        AlertDialog alert = dialog.create(); // create dialog
        alert.show();                        // show dialog
    }
}