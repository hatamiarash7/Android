/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.hambazidemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import ir.hatamiarash.MyToast.CustomToast;
import helper.SQLiteHandler;
import helper.SessionManager;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class EditProfile extends Activity {
    private static final String TAG = EditProfile.class.getSimpleName();
    private boolean isPhoneChanged = false;
    private AlertDialog progressDialog;
    private EditText txtName;
    private EditText txtAddress;
    private EditText txtPhone;
    EditText txtPassword;
    EditText txtPassword2;
    private SQLiteHandler db;
    private SessionManager session;
    private String Backup_Phone, uid, email;
    Button btnConfirm, btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        txtName = (EditText) findViewById(R.id.new_name);                  // name
        txtAddress = (EditText) findViewById(R.id.new_address);            // address
        txtPhone = (EditText) findViewById(R.id.new_phone);                // phone number
        txtPassword = (EditText) findViewById(R.id.new_password);          // password
        txtPassword2 = (EditText) findViewById(R.id.new_password2);        // repeat password
        btnConfirm = (Button) findViewById(R.id.btnConfirm);               // detail change button
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword); // password change button
        db = new SQLiteHandler(getApplicationContext());                   // user local database
        session = new SessionManager(getApplicationContext());             // user session
        progressDialog = new SpotsDialog(this, R.style.CustomDialog);      // dialog
        progressDialog.setCancelable(false);
        HashMap<String, String> user = db.getUserDetails();                // get user detail from local database
        email = user.get(Config_TAG.EMAIL);                                // get user's email
        showDialog();
        GetUser(email);                                                    // get person detail from server
        btnConfirm.setOnClickListener(new View.OnClickListener() {         // confirm button's event
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
                            MakeToast("شماره موبایل را بررسی نمایید", Config_TAG.WARNING);
                    else
                        MakeToast("تمامی کادر ها را پر نمایید", Config_TAG.WARNING);
            }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {         // confirm button's event
            @Override
            public void onClick(View v) {
                String pass = txtPassword.getText().toString();
                String pass2 = txtPassword2.getText().toString();
                if (CheckInternet())
                    if (pass.length() >= 8)
                        if (pass.equals(pass2))
                            UpdatePassword(pass, email);
                        else
                            MakeToast("کلمه عبور تطابق ندارد", Config_TAG.WARNING);
                    else
                        MakeToast("کلمه عبور کوتاه است", Config_TAG.WARNING);
            }
        });
    }

    private void GetUser(final String email) {             // check login request from server
        String string_req = "req_get";                 // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response); // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {                          // Check for error node in json
                        JSONObject user = jObj.getJSONObject(Config_TAG.USER);
                        txtName.setText(user.getString(Config_TAG.NAME));
                        txtAddress.setText(user.getString(Config_TAG.ADDRESS));
                        Backup_Phone = user.getString(Config_TAG.PHONE);
                        txtPhone.setText(Backup_Phone);
                        uid = jObj.getString(Config_TAG.UID);
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
                params.put(Config_TAG.TAG, "user_get");
                params.put(Config_TAG.EMAIL, email);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    private void UpdateUser(final String name, final String address, final String phone, final String email, final String uid) {
        // Tag used to cancel the request
        String string_req = "req_register";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Update User In SQLite
                        JSONObject user = jObj.getJSONObject(Config_TAG.USER);
                        String name = user.getString(Config_TAG.NAME);
                        String email = user.getString(Config_TAG.EMAIL);
                        String address = user.getString(Config_TAG.ADDRESS);
                        String phone = user.getString(Config_TAG.PHONE);
                        // Inserting row in users table
                        //db.updateUser(name, email, phone);
                        MakeToast("اطلاعات شما به روزرسانی شد", Config_TAG.SUCCESS);
                        if (!isPhoneChanged) {
                            Intent intent = new Intent(EditProfile.this, MainScreenActivity.class);
                            startActivity(intent);
                            finish();
                        }
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
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_update_details");
                params.put(Config_TAG.NAME, name);
                params.put(Config_TAG.EMAIL, email);
                params.put(Config_TAG.UID, uid);
                params.put(Config_TAG.ADDRESS, address);
                params.put(Config_TAG.PHONE, phone);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    private void UpdatePassword(final String pass, final String email) {
        // Tag used to cancel the request
        String string_req = "req_update";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Updating Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        MakeToast("اطلاعات شما به روزرسانی شد", Config_TAG.SUCCESS);
                        logoutUser();
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
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_update_password");
                params.put(Config_TAG.PASSWORD, pass);
                params.put(Config_TAG.EMAIL, email);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    private void DeleteUser(final String email) {          // check login request from server
        String string_req = "req_delete";              // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response); // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {                          // Check for error node in json
                        Log.d(TAG, "Done !");
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
                params.put(Config_TAG.TAG, "user_delete");
                params.put(Config_TAG.EMAIL, email);
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

    private void MakeQuestion(String Title, String Message, final String name, final String address, final String phone, final String Backup_Phone, final String uid) { // build and show an confirm window
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
                dialog.dismiss();            // close dialog
            }
        });
        AlertDialog alert = dialog.create(); // create dialog
        alert.show();                        // show dialog
    }

    private void logoutUser() {
        showDialog();
        session.setLogin(false);
        db.deleteUsers(); // delete user from local database
        DeleteUser(email);
        Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
        MainScreenActivity.pointer.finish();
        // finish old activity and start again for refresh
        startActivity(i);
        finish();
    }
}