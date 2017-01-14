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
import android.os.AsyncTask;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import helper.FontHelper;
import helper.JSONParser;
import helper.SQLiteHandler;
import helper.SessionManager;
import helper.TypefaceSpan;
import volley.AppController;
import volley.Config_URL;

public class EditProfile extends Activity {
    private static final String TAG = UserProfile.class.getSimpleName();
    String email;
    JSONParser jsonParser = new JSONParser();
    Button btnConfirm, btnChangePassword;
    private ProgressDialog pDialog;
    private EditText txtName;
    private EditText txtAddress;
    private EditText txtPhone;
    private EditText txtPassword;
    private EditText txtPassword2;
    private SQLiteHandler db;
    String Backup_Phone, uid;
    private SessionManager session;
    private boolean isPhoneChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        txtName = (EditText) findViewById(R.id.new_name);
        txtAddress = (EditText) findViewById(R.id.new_address);
        txtPhone = (EditText) findViewById(R.id.new_phone);
        txtPassword = (EditText) findViewById(R.id.new_password);
        txtPassword2 = (EditText) findViewById(R.id.new_password2);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        HashMap<String, String> user = db.getUserDetails();        // get user detail from local database
        email = user.get("email");
        Log.d(email, "db : " + email);
        pDialog.setMessage("لطفا منتظر بمانید ...");
        showDialog();
        GetUser(email);                                            // get person detail from server
        btnConfirm.setOnClickListener(new View.OnClickListener() { // confirm button's event
            @Override
            public void onClick(View v) {
                String name = txtName.getText().toString();
                String address = txtAddress.getText().toString();
                String phone = txtPhone.getText().toString();
                if (CheckInternet())
                    if (!name.isEmpty() && !address.isEmpty() && !phone.isEmpty())
                        if (phone.startsWith("09") && phone.length() == 11 && phone.matches("\\d+(?:\\.\\d+)?"))
                            if (!phone.equals(Backup_Phone))
                                MakeQuestion("تغییر شماره تلفن", "اطلاعات ورود شما نیز تغییر خواهد کرد", name, address, phone, Backup_Phone, uid);
                            else
                                UpdateUser(name, address, phone, Backup_Phone, uid);
                        else
                            MakeToast("شماره موبایل را بررسی نمایید");
                    else
                        MakeToast("تمامی کادر ها را پر نمایید");
            }
        });
    }


    private void GetUser(final String email) {          // check login request from server
        String tag_string_req = "req_get";               // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.url_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response); // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {                          // Check for error node in json
                        JSONObject user = jObj.getJSONObject("user");
                        txtName.setText(user.getString("name"));
                        txtAddress.setText(user.getString("address"));
                        Backup_Phone = user.getString("phone");
                        txtPhone.setText(Backup_Phone);
                        uid = jObj.getString("uid");
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
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
            protected java.util.Map<String, String> getParams() { // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "user_get");
                params.put("email", email);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void UpdateUser(final String name, final String address, final String phone, final String email, final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        pDialog.setMessage("در حال ثبت ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.url_register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL , Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String address = user.getString("address");
                        String phone = user.getString("phone");
                        // Inserting row in users table
                        db.updateUser(name, email, address, phone);
                        MakeToast("اطلاعات شما به روزرسانی شد");
                        if (!isPhoneChanged) {
                            Intent intent = new Intent(EditProfile.this, MainScreenActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        MakeToast(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Updating Error: " + error.getMessage());
                MakeToast(error.getMessage());
                hideDialog();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "user_update");
                params.put("name", name);
                params.put("email", email);
                params.put("uid", uid);
                params.put("address", address);
                params.put("phone", phone);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void MakeQuestion(String Title, String Message, final String name, final String address, final String phone, final String Backup_Phone, final String uid) {                     // build and show an confirm window
        AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
        dialog.setTitle(Title);                                                     // set title
        dialog.setMessage(Message);                                                 // set message
        dialog.setIcon(R.drawable.ic_alert);                                        // set icon
        dialog.setPositiveButton("باشه !", new DialogInterface.OnClickListener() {  // positive answer
            public void onClick(DialogInterface dialog, int id) {
                isPhoneChanged = true;
                UpdateUser(name, address, phone, Backup_Phone, uid);
                logoutUser();
            }
        });
        dialog.setNegativeButton("بیخیال", new DialogInterface.OnClickListener() { // negative answer
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss(); // close dialog
            }
        });
        AlertDialog alert = dialog.create(); // create dialog
        alert.show();                        // show dialog
    }

    public void logoutUser() {
        pDialog.setMessage("در حال خروج ...");
        showDialog();
        session.setLogin(false);
        db.deleteUsers();                 // delete user from local database
        DeleteUser(email);
        Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
        MainScreenActivity.pointer.finish();
        // finish old activity and start again for refresh
        startActivity(i);
        finish();
    }

    private void DeleteUser(final String email) {          // check login request from server
        String tag_string_req = "req_delete";               // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.url_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response); // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {                          // Check for error node in json
                        Log.d(TAG, "Done !");
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
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
            protected java.util.Map<String, String> getParams() { // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "user_delete");
                params.put("email", email);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}