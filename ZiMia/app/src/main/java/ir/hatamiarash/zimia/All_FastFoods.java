/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import helper.FontHelper;
import helper.TypefaceSpan;
import volley.AppController;
import volley.Config_URL;

public class All_FastFoods extends ListActivity {
    private static final String TAG = All_FastFoods.class.getSimpleName();
    private static final String TAG_TYPE = "fastfoods";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_STATUS_PICTURE = "status";
    private static final String TAG_OPENHOUR = "open_hour";
    private static final String TAG_CLOSEHOUR = "close_hour";
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> fastfoodList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_fastfoods);
        fastfoodList = new ArrayList<HashMap<String, String>>();
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                Intent in = new Intent(getApplicationContext(), FastFoodDetail.class);
                in.putExtra(TAG_PID, pid);
                startActivityForResult(in, 100);
            }
        });
        FetchAllFastFoods();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    private void FetchAllFastFoods() {
        // Tag used to cancel the request
        String tag_string_req = "req_fetch";
        pDialog.setMessage("لطفا منتظر بمانید ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.url_register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "FastFoods Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // FastFoods List fetched from server
                        JSONArray fastfoods = jObj.getJSONArray(TAG_TYPE);
                        for (int i = 0; i < fastfoods.length(); i++) {
                            JSONObject fastfood = fastfoods.getJSONObject(i);
                            String id = fastfood.getString(TAG_PID);
                            String name = fastfood.getString(TAG_NAME);
                            int picture = fastfood.getInt(TAG_PICTURE);
                            int open_hour = fastfood.getInt(TAG_OPENHOUR);
                            int close_hour = fastfood.getInt(TAG_CLOSEHOUR);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(TAG_PID, id);
                            map.put(TAG_NAME, name);
                            String add = "i" + String.valueOf(picture);
                            int pic = getResources().getIdentifier(add, "drawable", getPackageName());
                            map.put(TAG_PICTURE, String.valueOf(pic));
                            Calendar time = Calendar.getInstance();
                            int current_hour = time.get(Calendar.HOUR_OF_DAY);
                            Log.d("Time: ", String.valueOf(current_hour));
                            if (current_hour > open_hour && current_hour < close_hour) {
                                pic = getResources().getIdentifier("open", "drawable", getPackageName());
                                map.put(TAG_STATUS_PICTURE, String.valueOf(pic));
                            } else {
                                pic = getResources().getIdentifier("close", "drawable", getPackageName());
                                map.put(TAG_STATUS_PICTURE, String.valueOf(pic));
                            }
                            fastfoodList.add(map);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ListAdapter adapter = new SimpleAdapter(
                                            All_FastFoods.this, fastfoodList,
                                            R.layout.list_item, new String[]{
                                            TAG_PID,
                                            TAG_NAME,
                                            TAG_PICTURE,
                                            "",
                                            TAG_STATUS_PICTURE
                                    },
                                            new int[]{
                                                    R.id.pid,
                                                    R.id.name,
                                                    R.id.img,
                                                    R.id.price,
                                                    R.id.img2
                                            });
                                    setListAdapter(adapter);
                                }
                            });
                        }
                    } else {
                        // Error occurred
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
                Log.e(TAG, "Fetch Error: " + error.getMessage());
                MakeToast(error.getMessage());
                hideDialog();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "seller_fastfoods");
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

    public void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }
}