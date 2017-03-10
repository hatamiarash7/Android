/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.hambazidemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import helper.Helper;
import helper.SQLiteHandler;
import helper.SessionManager;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class UserProfile extends AppCompatActivity {
    private static final String TAG = UserProfile.class.getSimpleName();
    private String phone;
    Button btnLogout;
    Button btnEdit;
    Button btnCharge;
    ImageView type_badge;
    private TextView txtName;
    private TextView txtType;
    private SQLiteHandler db;
    private SessionManager session;
    private AlertDialog progressDialog;                                // dialog window

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        txtName = (TextView) findViewById(R.id.profile_name);
        txtType = (TextView) findViewById(R.id.profile_type);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnCharge = (Button) findViewById(R.id.btnCharge);
        type_badge = (ImageView) findViewById(R.id.type_badge);

        txtName.setText("");
        txtType.setText("");
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        progressDialog = new SpotsDialog(this, R.style.CustomDialog);                             // new dialog
        progressDialog.setCancelable(false);                                            // set unacceptable dialog
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        HashMap<String, String> user = db.getUserDetails();       // get user detail from local database
        phone = user.get(Config_TAG.PHONE);
        Log.d(phone, "db : " + phone);
        GetUser(phone);
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
                Helper.MakeToast(UserProfile.this, "انتقال به صفحه شارژ", Config_TAG.WARNING);
            }
        });
    }

    private void logoutUser() {
        showDialog();
        session.setLogin(false);
        db.deleteUsers();
        Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
        MainScreenActivity.pointer.finish();
        // finish old activity and start again for refresh
        startActivity(i);
        finish();
    }

    private void GetUser(final String phone) {          // check login request from server
        String tag_string_req = "req_get";               // Tag used to cancel the request
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response); // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {                          // Check for error node in json
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String type = "شما کاربر " + Helper.ConvertTypes(user.getInt("type")) + " هستید";
                        txtName.setText(name);
                        txtType.setText(type);
                        if (user.getInt("type") == 0)
                            type_badge.setImageDrawable(getResources().getDrawable(R.drawable.bronze_badge));
                        if (user.getInt("type") == 1)
                            type_badge.setImageDrawable(getResources().getDrawable(R.drawable.silver_badge));
                        if (user.getInt("type") == 2)
                            type_badge.setImageDrawable(getResources().getDrawable(R.drawable.gold_badge));
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Helper.MakeToast(UserProfile.this, errorMsg, Config_TAG.ERROR); // show error message
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
                    Helper.MakeToast(UserProfile.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(UserProfile.this, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() { // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<>();
                params.put("tag", "user_get");
                params.put("phone", phone);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() { // show dialog
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() { // close dialog
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}