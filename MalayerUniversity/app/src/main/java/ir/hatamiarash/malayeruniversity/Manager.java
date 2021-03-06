/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package ir.hatamiarash.malayeruniversity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import helper.FontHelper;
import helper.Helper;
import helper.NewsAdapter;
import helper.SQLiteHandler;
import helper.UsersAdapter;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class Manager extends AppCompatActivity {
    private static final String TAG = Manager.class.getSimpleName();
    public static Manager pointer;
    ListView listView;
    String type, username;
    private ArrayList<HashMap<String, String>> OutputList;
    private ProgressDialog progressDialog;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listlayout);
        pointer = this;

        Intent i = getIntent();
        username = i.getStringExtra(Config_TAG.USERNAME);
        type = i.getStringExtra(Config_TAG.TYPE);

        listView = (ListView) findViewById(R.id.mylist);

        db = new SQLiteHandler(getApplicationContext());
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        OutputList = new ArrayList<>();

        String uid = db.getUserDetails().get("uid");
        String username = db.getUserDetails().get("username");
        Log.d(TAG, "User ID : " + uid);
        if (!username.equals("hatamiarash7") || !username.equals("admin")) {
            if (type.equals("news"))
                FetchAllNews(uid);
            else
                FetchAllUsers();
        } else {
            if (type.equals("news"))
                FetchAllNews();
            else
                FetchAllUsers();
        }
    }

    private void FetchAllNews() {
        // Tag used to cancel the request
        String tag_string_req = "req_fetch";
        progressDialog.setMessage(FontHelper.getSpannedString(Manager.this, "در حال به روزرسانی ..."));
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Admins Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Restaurants List fetched from server
                        JSONArray news = jObj.getJSONArray("news");
                        for (int i = 0; i < news.length(); i++) {
                            HashMap<String, String> map = new HashMap<>();
                            JSONObject _new = news.getJSONObject(i);
                            String title = _new.getString("title");
                            String image = _new.getString("image");
                            String content = _new.getString("content");
                            String created_at = _new.getString("created_at");
                            String updated_at = _new.getString("updated_at");
                            int cid = _new.getInt("cid");
                            int id = _new.getInt("id");
                            String uid = _new.getString("uid");
                            map.put(Config_TAG.TITLE, title);
                            map.put(Config_TAG.BODY, content);
                            map.put(Config_TAG.PICTURE, image);
                            map.put(Config_TAG.CREATED_AT, created_at);
                            map.put(Config_TAG.UPDATED_AT, updated_at);
                            map.put(Config_TAG.CID, String.valueOf(cid));
                            map.put(Config_TAG.UID, String.valueOf(uid));
                            map.put(Config_TAG.ID, String.valueOf(id));
                            OutputList.add(map);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    NewsAdapter adapter = new NewsAdapter(OutputList, Manager.this);
                                    listView.setAdapter(adapter);
                                }
                            });
                        }
                    } else {
                        // Error occurred
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        Helper.MakeToast(Manager.this, errorMsg, Config_TAG.ERROR);
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
                    Helper.MakeToast(Manager.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(Manager.this, "خطایی رخ داده است ، اتصال به اینترنت را بررسی کنید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "all_news");
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void FetchAllNews(final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_fetch";
        progressDialog.setMessage(FontHelper.getSpannedString(Manager.this, "در حال به روزرسانی ..."));
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Normal Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Restaurants List fetched from server
                        JSONArray news = jObj.getJSONArray("news");
                        for (int i = 0; i < news.length(); i++) {
                            HashMap<String, String> map = new HashMap<>();
                            JSONObject _new = news.getJSONObject(i);
                            String title = _new.getString("title");
                            String image = _new.getString("image");
                            String content = _new.getString("content");
                            String created_at = _new.getString("created_at");
                            String updated_at = _new.getString("updated_at");
                            int cid = _new.getInt("cid");
                            int id = _new.getInt("id");
                            String uid = _new.getString("uid");
                            map.put(Config_TAG.TITLE, title);
                            map.put(Config_TAG.BODY, content);
                            map.put(Config_TAG.PICTURE, image);
                            map.put(Config_TAG.CREATED_AT, created_at);
                            map.put(Config_TAG.UPDATED_AT, updated_at);
                            map.put(Config_TAG.CID, String.valueOf(cid));
                            map.put(Config_TAG.UID, String.valueOf(uid));
                            map.put(Config_TAG.ID, String.valueOf(id));
                            OutputList.add(map);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    NewsAdapter adapter = new NewsAdapter(OutputList, Manager.this);
                                    listView.setAdapter(adapter);
                                }
                            });
                        }
                    } else {
                        // Error occurred
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        Helper.MakeToast(Manager.this, errorMsg, Config_TAG.ERROR);
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
                    Helper.MakeToast(Manager.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(Manager.this, "خطایی رخ داده است ، اتصال به اینترنت را بررسی کنید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "all_news_filter");
                params.put(Config_TAG.USERNAME, uid);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void FetchAllUsers() {
        // Tag used to cancel the request
        String tag_string_req = "req_fetch";
        progressDialog.setMessage(FontHelper.getSpannedString(Manager.this, "در حال به روزرسانی ..."));
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Restaurants List fetched from server
                        JSONArray users = jObj.getJSONArray("users");
                        for (int i = 0; i < users.length(); i++) {
                            HashMap<String, String> map = new HashMap<>();
                            JSONObject _user = users.getJSONObject(i);
                            String name = _user.getString("name");
                            String username = _user.getString("username");
                            String email = _user.getString("email");
                            //String image = _user.getString("image");
                            String type = _user.getString("type");
                            String job = _user.getString("job");
                            String created_at = _user.getString("created_at");
                            String updated_at = _user.getString("updated_at");
                            String uid = _user.getString("unique_id");
                            int id = _user.getInt("id");
                            if (!username.equals("hatamiarash7")) {
                                map.put(Config_TAG.NAME, name);
                                map.put(Config_TAG.EMAIL, email);
                                map.put(Config_TAG.USERNAME, username);
                                map.put(Config_TAG.TYPE, type);
                                map.put(Config_TAG.JOB, job);
                                map.put(Config_TAG.CREATED_AT, created_at);
                                map.put(Config_TAG.UPDATED_AT, updated_at);
                                map.put(Config_TAG.UID, uid);
                                map.put(Config_TAG.ID, String.valueOf(id));
                                OutputList.add(map);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        UsersAdapter adapter = new UsersAdapter(OutputList, Manager.this);
                                        listView.setAdapter(adapter);
                                    }
                                });
                            }
                        }
                    } else {
                        // Error occurred
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        Helper.MakeToast(Manager.this, errorMsg, Config_TAG.ERROR);
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
                    Helper.MakeToast(Manager.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(Manager.this, "خطایی رخ داده است ، اتصال به اینترنت را بررسی کنید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "all_users");
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