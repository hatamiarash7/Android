/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PERSON = "persons";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_ID = "id";
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
    private SessionManager session;
    String Backup_Phone, uid;

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
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());
//        if (!session.isLoggedIn()) {
//
//        }
        HashMap<String, String> user = db.getUserDetails();        // get user detail from local database
        email = user.get("email");
        Log.d(email, "db : " + email);
        new EditProfile.GetPersonDetails().execute();              // get person detail from server
        btnConfirm.setOnClickListener(new View.OnClickListener() { // confirm button's event
            @Override
            public void onClick(View v) {
                String name = txtName.getText().toString();
                String address = txtAddress.getText().toString();
                String phone = txtPhone.getText().toString();
                if (CheckInternet())
                    if (!name.isEmpty() && !address.isEmpty() && !phone.isEmpty())
                        if (phone.startsWith("09") && phone.length() == 11)
                            updateUser(name, address, phone, Backup_Phone, uid);
                        else
                            MakeToast("شماره موبایل را بررسی نمایید");
                    else
                        MakeToast("تمامی کادر ها را پر نمایید");
            }
        });
    }

    class GetPersonDetails extends AsyncTask<String, String, String> {
        protected String doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("email", email));
                        JSONObject json = jsonParser.makeHttpRequest(Config_URL.url_get_person_detials, "GET", params);
                        Log.d("Single Person Details", json.toString());
                        if (json.getInt(TAG_SUCCESS) == 1) {
                            JSONArray productObj = json.getJSONArray(TAG_PERSON);
                            JSONObject person = productObj.getJSONObject(0);
                            txtName.setText(person.getString(TAG_NAME));
                            txtAddress.setText(person.getString(TAG_ADDRESS));
                            Backup_Phone = person.getString(TAG_PHONE);
                            txtPhone.setText(Backup_Phone);
                            uid = person.getString(TAG_ID);
                        } else {
                            Log.d(TAG, "Error !");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }

    private void updateUser(final String name, final String address, final String phone, final String email, final String uid) {
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
                        Intent intent = new Intent(EditProfile.this, MainScreenActivity.class);
                        startActivity(intent);
                        finish();
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
                params.put("tag", "update");
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
}
