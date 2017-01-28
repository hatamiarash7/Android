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
import helper.SQLiteHandler;
import helper.SessionManager;
import helper.TypefaceSpan;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class EditProfile extends Activity {
    private static final String TAG = EditProfile.class.getSimpleName();
    private boolean isPhoneChanged = false;
    private ProgressDialog pDialog;
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
        pDialog = new ProgressDialog(this);                                // dialog
        pDialog.setCancelable(false);
        HashMap<String, String> user = db.getUserDetails();                // get user detail from local database
        email = user.get(Config_TAG.TAG_EMAIL);                            // get user's email
        pDialog.setMessage("لطفا منتظر بمانید ...");
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
                            MakeToast("شماره موبایل را بررسی نمایید");
                    else
                        MakeToast("تمامی کادر ها را پر نمایید");
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
                            MakeToast("کلمه عبور تطابق ندارد");
                    else
                        MakeToast("کلمه عبور کوتاه است");
            }
        });
    }

    private void GetUser(final String email) {             // check login request from server
        String tag_string_req = "req_get";                 // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response); // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.TAG_ERROR);
                    if (!error) {                          // Check for error node in json
                        JSONObject user = jObj.getJSONObject(Config_TAG.TAG_USER);
                        txtName.setText(user.getString(Config_TAG.TAG_NAME));
                        txtAddress.setText(user.getString(Config_TAG.TAG_ADDRESS));
                        Backup_Phone = user.getString(Config_TAG.TAG_PHONE);
                        txtPhone.setText(Backup_Phone);
                        uid = jObj.getString(Config_TAG.TAG_UID);
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
            protected java.util.Map<String, String> getParams() { // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_get");
                params.put(Config_TAG.TAG_EMAIL, email);
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
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.TAG_ERROR);
                    if (!error) {
                        // Update User In SQLite
                        JSONObject user = jObj.getJSONObject(Config_TAG.TAG_USER);
                        String name = user.getString(Config_TAG.TAG_NAME);
                        String email = user.getString(Config_TAG.TAG_EMAIL);
                        String address = user.getString(Config_TAG.TAG_ADDRESS);
                        String phone = user.getString(Config_TAG.TAG_PHONE);
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
                Log.e(TAG, "Updating Error: " + error.getMessage());
                MakeToast(error.getMessage());
                hideDialog();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_update_details");
                params.put(Config_TAG.TAG_NAME, name);
                params.put(Config_TAG.TAG_EMAIL, email);
                params.put(Config_TAG.TAG_UID, uid);
                params.put(Config_TAG.TAG_ADDRESS, address);
                params.put(Config_TAG.TAG_PHONE, phone);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void UpdatePassword(final String pass, final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_update";
        pDialog.setMessage("در حال ثبت ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Updating Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.TAG_ERROR);
                    if (!error) {
                        MakeToast("اطلاعات شما به روزرسانی شد");
                        logoutUser();
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
                Log.e(TAG, "Updating Error: " + error.getMessage());
                MakeToast(error.getMessage());
                hideDialog();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_update_password");
                params.put(Config_TAG.TAG_PASSWORD, pass);
                params.put(Config_TAG.TAG_EMAIL, email);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void DeleteUser(final String email) {          // check login request from server
        String tag_string_req = "req_delete";              // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response); // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.TAG_ERROR);
                    if (!error) {                          // Check for error node in json
                        Log.d(TAG, "Done !");
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
            protected java.util.Map<String, String> getParams() { // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_delete");
                params.put(Config_TAG.TAG_EMAIL, email);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
        pDialog.setMessage("در حال خروج ...");
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