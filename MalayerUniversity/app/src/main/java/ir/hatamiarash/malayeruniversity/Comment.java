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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import helper.Helper;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class Comment extends AppCompatActivity {
    private static final String TAG = Comment.class.getSimpleName();
    String[] items = new String[]{"بسیج دانشجویی", "فرهنگی", "علمی پژوهشی", "خوابگاه ها", "تغذیه", "آموزشی", "حراست", "نهاد رهبری"};
    private EditText subject, body;
    Button send;
    private ProgressDialog progressDialog;
    private Spinner inputType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        subject = (EditText) findViewById(R.id.comment_subject);
        body = (EditText) findViewById(R.id.comment_body);
        send = (Button) findViewById(R.id.comment_send);
        inputType = (Spinner) findViewById(R.id.type);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        inputType.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.CheckInternet(Comment.this)) {
                    if (subject.length() > 0 && body.length() > 0) {
                        String msg_subject = subject.getText().toString();
                        String msg_body = body.getText().toString();
                        String type = inputType.getSelectedItem().toString();
                        SendEmail(msg_subject, msg_body, type);
                    } else
                        Helper.MakeToast(Comment.this, "لطفا عنوان و متن پیام را وادر نمایید", Config_TAG.WARNING);
                }
            }
        });
    }

    private void SendEmail(final String subject, final String body, final String type) {
        String string_req = "req_send";
        progressDialog.setMessage("در حال ارسال ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "send Response: " + response);  // log server response
                hideDialog();                              // close dialog
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {                          // Check for error node in json
                        Helper.MakeToast(Comment.this, "پیام شما با موفقیت ارسال شد ، متشکریم !", Config_TAG.SUCCESS);
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    } else
                        Helper.MakeToast(Comment.this, jObj.getString(Config_TAG.ERROR_MSG), Config_TAG.ERROR); // show error message
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Fetch Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    Helper.MakeToast(Comment.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(Comment.this, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() { // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "send_comment");
                params.put(Config_TAG.TYPE, type);
                params.put(Config_TAG.SUBJECT, subject);
                params.put(Config_TAG.BODY, body);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}