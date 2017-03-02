/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package ir.hatamiarash.malayeruniversity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import helper.FontHelper;
import helper.Helper;
import helper.SQLiteHandler;
import helper.SessionManager;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class EditUser extends Activity {
    private static final String TAG = EditUser.class.getSimpleName();
    private ProgressDialog pDialog;
    private EditText txtName, txtEmail, txtJob, txtPassword, txtPassword2;
    SessionManager session;
    Button btnConfirm, btnChangePassword;
    SQLiteHandler db;
    HashMap<String, String> user;
    private String username;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit);
        
        txtName = (EditText) findViewById(R.id.name);                  // name
        txtEmail = (EditText) findViewById(R.id.email);                // address
        txtJob = (EditText) findViewById(R.id.job);                    // phone number
        txtPassword = (EditText) findViewById(R.id.password);          // password
        txtPassword2 = (EditText) findViewById(R.id.password2);        // repeat password
        btnConfirm = (Button) findViewById(R.id.btnConfirm);           // detail change button
        btnChangePassword = (Button) findViewById(R.id.btnConfirm2);   // password change button

        Intent i = getIntent();
        final String id = i.getStringExtra("id");
        final String uid = i.getStringExtra("uid");

        session = new SessionManager(getApplicationContext());         // user session
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());
        user = db.getUserDetails();
        username = user.get(Config_TAG.USERNAME);
        Log.d(TAG, "db : " + username);

        GetUser(id, uid);                                              // get person detail from server
        
        btnConfirm.setOnClickListener(new View.OnClickListener() {     // confirm button's event
            @Override
            public void onClick(View v) {
                String name = txtName.getText().toString();
                String email = txtEmail.getText().toString();
                String job = txtJob.getText().toString();
                if (Helper.CheckInternet(EditUser.this))
                    if (!name.isEmpty() && !job.isEmpty())
                        UpdateUser(id, uid, name, email, job);
                    else
                        Helper.MakeToast(EditUser.this, "تمامی کادر ها را پر نمایید", Config_TAG.WARNING);
            }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {         // confirm button's event
            @Override
            public void onClick(View v) {
                String pass = txtPassword.getText().toString();
                String pass2 = txtPassword2.getText().toString();
                if (Helper.CheckInternet(EditUser.this))
                    if (pass.length() >= 8)
                        if (pass.equals(pass2))
                            UpdatePassword(id, uid, pass);
                        else
                            Helper.MakeToast(EditUser.this, "کلمه عبور تطابق ندارد", Config_TAG.WARNING);
                    else
                        Helper.MakeToast(EditUser.this, "کلمه عبور کوتاه است", Config_TAG.WARNING);
            }
        });
    }
    
    private void GetUser(final String id, final String uid) {      // check login request from server
        String string_req = "req_get";                 // Tag used to cancel the request
        pDialog.setMessage(FontHelper.getSpannedString(this, "در حال به روزرسانی ..."));
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Fetch Response: " + response); // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {                          // Check for error node in json
                        JSONObject user = jObj.getJSONObject(Config_TAG.USER);
                        txtName.setText(user.getString(Config_TAG.NAME));
                        if (!user.getString(Config_TAG.EMAIL).equals("NULL"))
                            txtEmail.setText(user.getString(Config_TAG.EMAIL));
                        txtJob.setText(user.getString(Config_TAG.JOB));
                    } else {
                        // Get the error message
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        Helper.MakeToast(EditUser.this, errorMsg, Config_TAG.ERROR); // show error message
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Fetch Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    Helper.MakeToast(EditUser.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(EditUser.this, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() { // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_get_detail");
                params.put(Config_TAG.ID, id);
                params.put(Config_TAG.UID, uid);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }
    
    private void UpdateUser(final String id, final String uid, final String name, final String email, final String job) {
        // Tag used to cancel the request
        String string_req = "req_register";
        pDialog.setMessage(FontHelper.getSpannedString(this, "در حال به روزرسانی ..."));
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        Helper.MakeToast(EditUser.this, "اطلاعات کاربر به روزرسانی شد", Config_TAG.SUCCESS);
                        Intent i = new Intent(getApplicationContext(), Manager.class);
                        i.putExtra(Config_TAG.USERNAME, username);
                        i.putExtra(Config_TAG.TYPE, "users");
                        startActivity(i);
                    } else {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        Helper.MakeToast(EditUser.this, errorMsg, Config_TAG.ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    Helper.MakeToast(EditUser.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(EditUser.this, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_update_details");
                params.put(Config_TAG.ID, id);
                params.put(Config_TAG.UID, uid);
                params.put(Config_TAG.NAME, name);
                params.put(Config_TAG.EMAIL, email);
                params.put(Config_TAG.JOB, job);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }
    
    private void UpdatePassword(final String id, final String uid, final String pass) {
        // Tag used to cancel the request
        String string_req = "req_update";
        pDialog.setMessage(FontHelper.getSpannedString(this, "در حال به روزرسانی ..."));
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        Helper.MakeToast(EditUser.this, "اطلاعات کاربر به روزرسانی شد", Config_TAG.SUCCESS);
                        Intent i = new Intent(getApplicationContext(), Manager.class);
                        i.putExtra(Config_TAG.USERNAME, username);
                        i.putExtra(Config_TAG.TYPE, "users");
                        startActivity(i);
                    } else {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        Helper.MakeToast(EditUser.this, errorMsg, Config_TAG.ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    Helper.MakeToast(EditUser.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(EditUser.this, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_update_password");
                params.put(Config_TAG.PASSWORD, pass);
                params.put(Config_TAG.ID, id);
                params.put(Config_TAG.UID, uid);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
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