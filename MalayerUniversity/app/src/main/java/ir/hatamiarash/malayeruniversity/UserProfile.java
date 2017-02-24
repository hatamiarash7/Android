/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.malayeruniversity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import helper.SQLiteHandler;
import helper.SessionManager;
import ir.hatamiarash.MyToast.CustomToast;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class UserProfile extends AppCompatActivity {
    private static final String TAG = UserProfile.class.getSimpleName();
    String username;
    Button btnLogout, btnEdit, btnNewUser, btnManageUsers, btnPostNews, btnManageNews;
    private TextView txtName, txtEmail, txtJob;
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog progressDialog;
    HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        txtName = (TextView) findViewById(R.id.profile_name);
        txtEmail = (TextView) findViewById(R.id.profile_email);
        txtJob = (TextView) findViewById(R.id.profile_type);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnNewUser = (Button) findViewById(R.id.new_user);
        btnManageNews = (Button) findViewById(R.id.manage_news);
        btnPostNews = (Button) findViewById(R.id.post_news);
        btnManageUsers = (Button) findViewById(R.id.manage_users);

        txtName.setText("");
        txtJob.setText("");
        btnNewUser.setVisibility(View.INVISIBLE);
        btnManageUsers.setVisibility(View.INVISIBLE);
        btnManageNews.setVisibility(View.INVISIBLE);
        btnPostNews.setVisibility(View.INVISIBLE);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        progressDialog = new ProgressDialog(this);                                      // new dialog
        progressDialog.setCancelable(false);
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        user = db.getUserDetails();       // get user detail from local database
        username = user.get(Config_TAG.USERNAME);
        Log.d(TAG, "db : " + username);
        GetUser(username);
        btnLogout.setOnClickListener(new View.OnClickListener() { // logout button's event
            @Override
            public void onClick(View v) {
                logoutUser(); // logout user
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() { // logout button's event
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(i);
                finish();*/
            }
        });
        btnManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Manager.class);
                i.putExtra(Config_TAG.USERNAME, username);
                i.putExtra(Config_TAG.TYPE, "users");
                startActivity(i);
            }
        });
        btnManageNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Manager.class);
                i.putExtra(Config_TAG.USERNAME, username);
                i.putExtra(Config_TAG.TYPE, "news");
                startActivity(i);
            }
        });
        btnPostNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PostNews.class);
                startActivity(i);
                finish();
            }
        });
        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void logoutUser() {
        progressDialog.setMessage("در حال خروج ...");
        showDialog();
        session.setLogin(false);
        db.deleteUsers();                 // delete user from local database
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        MainActivity.pointer.finish();
        // finish old activity and start again for refresh
        startActivity(i);
        finish();
    }

    private void GetUser(final String username) {          // check login request from server
        String tag_string_req = "req_get";               // Tag used to cancel the request
        progressDialog.setMessage("لطفا منتظر بمانید ...");
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
                        txtName.setText(user.getString("name"));
                        txtEmail.setText(user.getString("email"));
                        txtJob.setText(user.getString("type") + " - " + user.getString("job"));
                        if (username.equals("admin")) {
                            btnNewUser.setVisibility(View.VISIBLE);
                            btnManageUsers.setVisibility(View.VISIBLE);
                            btnManageNews.setVisibility(View.VISIBLE);
                            btnPostNews.setVisibility(View.VISIBLE);
                        } else {
                            btnManageNews.setVisibility(View.VISIBLE);
                            btnPostNews.setVisibility(View.VISIBLE);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnPostNews.getLayoutParams();
                            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                            btnPostNews.setLayoutParams(params);
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
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
                params.put("tag", "user_get");
                params.put("username", username);
                Log.d(TAG, "user : " + username);
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
}