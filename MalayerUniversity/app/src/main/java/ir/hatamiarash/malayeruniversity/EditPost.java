/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package ir.hatamiarash.malayeruniversity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import helper.Helper;
import helper.SessionManager;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class EditPost extends AppCompatActivity {
    private static final String TAG = EditPost.class.getSimpleName();
    EditText post_subject, post_body;
    Button send;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_edit);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());

        Intent i = getIntent();
        final String news_id = i.getStringExtra("nid");
        final String news_uid = i.getStringExtra("uid");
        final String news_cid = i.getStringExtra("cid");

        Log.d(TAG, news_id + " " + news_uid + " " + news_cid);

        post_body = (EditText) findViewById(R.id.body);
        post_subject = (EditText) findViewById(R.id.subject);
        send = (Button) findViewById(R.id.send);

        GetNews(news_id, news_cid, news_uid);

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Helper.CheckInternet(EditPost.this))
                    if (session.isLoggedIn())
                        if (post_subject.length() <= 100)
                            if (post_subject.length() > 0 && post_body.length() > 0)
                                UpdateNews(news_id, news_cid, news_uid, post_subject.getText().toString(), post_body.getText().toString());
                            else
                                Helper.MakeToast(EditPost.this, "عنوان و متن خبر را تایپ کنید", Config_TAG.ERROR);
                        else
                            Helper.MakeToast(EditPost.this, "عنوان خبر طولانی است", Config_TAG.ERROR);
                    else {
                        Helper.MakeToast(EditPost.this, "شما وارد نشده اید", Config_TAG.ERROR);
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
            }
        });
    }

    private void UpdateNews(final String nid, final String cid, final String uid, final String title, final String content) {
        pDialog.setMessage("در حال ارسال ...");
        showDialog();
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Post Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        Intent i = new Intent(EditPost.this, MainActivity.class);
                        MainActivity.pointer.finish();
                        startActivity(i);
                        finish();
                    } else
                        Helper.MakeToast(EditPost.this, jObj.getString(Config_TAG.ERROR_MSG), Config_TAG.ERROR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Post Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    Helper.MakeToast(EditPost.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(EditPost.this, "خطایی رخ داده است ، اتصال به اینترنت را بررسی کنید", Config_TAG.ERROR);
                hideDialog();
            }
        });
        smr.addStringParam(Config_TAG.TAG, "update_news");
        smr.addStringParam("title", title);
        smr.addStringParam("content", content);
        smr.addStringParam("nid", nid);
        smr.addStringParam("uid", uid);
        smr.addStringParam("cid", cid);
        AppController.getInstance().addToRequestQueue(smr);
    }


    private void GetNews(final String nid, final String cid, final String uid) {          // check login request from server
        String tag_string_req = "req_get";               // Tag used to cancel the request
        pDialog.setMessage("لطفا منتظر بمانید ...");
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
                        JSONObject _new = jObj.getJSONObject("news");
                        String title = _new.getString("title");
                        String content = _new.getString("content");
                        post_body.setText(content);
                        post_subject.setText(title);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Helper.MakeToast(EditPost.this, errorMsg, Config_TAG.ERROR);
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
                    Helper.MakeToast(EditPost.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(EditPost.this, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() { // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<>();
                params.put("tag", "get_news");
                params.put("nid", nid);
                params.put("uid", uid);
                params.put("cid", cid);
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
}