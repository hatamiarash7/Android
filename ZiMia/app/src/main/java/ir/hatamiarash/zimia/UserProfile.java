/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import volley.Config_URL;

public class UserProfile extends Activity {
    private static final String TAG = UserProfile.class.getSimpleName();
    String email;
    Button btnLogout;
    Button btnEdit;
    Button btnCharge;
    private TextView txtName;
    private TextView txtAddress;
    private TextView txtPhone;
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;                                // dialog window

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        txtName = (TextView) findViewById(R.id.profile_name);
        txtAddress = (TextView) findViewById(R.id.profile_address);
        txtPhone = (TextView) findViewById(R.id.profile_phone);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnCharge = (Button) findViewById(R.id.btnCharge);
        txtName.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);                                      // new dialog
        pDialog.setCancelable(false);                                            // set unacceptable dialog
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        HashMap<String, String> user = db.getUserDetails();       // get user detail from local database
        email = user.get("email");
        Log.d(email, "db : " + email);
        pDialog.setMessage("لطفا منتظر بمانید ...");
        showDialog();
        GetUser(email);
        btnLogout.setOnClickListener(new View.OnClickListener() { // logout button's event
            @Override
            public void onClick(View v) {
                logoutUser(); // logout user
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() { // logout button's event
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(i);
                finish();
            }
        });
        btnCharge.setOnClickListener(new View.OnClickListener() { // logout button's event
            @Override
            public void onClick(View v) {
                MakeToast("انتقال به صفحه شارژ");
            }
        });
    }

    public void MakeToast(String Message) { // build and show notification with custom typeface
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
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
                        txtPhone.setText(user.getString("phone"));
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
                java.util.Map<String, String> params = new HashMap<>();
                params.put("tag", "user_get");
                params.put("email", email);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
                java.util.Map<String, String> params = new HashMap<>();
                params.put("tag", "user_delete");
                params.put("email", email);
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
}