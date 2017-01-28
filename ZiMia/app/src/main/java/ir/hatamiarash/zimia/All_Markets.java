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
import volley.Config_TAG;
import volley.Config_URL;

public class All_Markets extends ListActivity {
    private static final String TAG = All_Markets.class.getSimpleName();
    private ProgressDialog pDialog;
    private ArrayList<HashMap<String, String>> MarketList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_sellers);
        MarketList = new ArrayList<>();
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                Intent i = new Intent(getApplicationContext(), MarketDetail.class);
                i.putExtra(Config_TAG.TAG_ID, pid);
                startActivity(i);
            }
        });
        FetchAllMarkets();
    }

    private void FetchAllMarkets() {
        // Tag used to cancel the request
        String tag_string_req = "req_fetch";
        pDialog.setMessage("لطفا منتظر بمانید ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Markets Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // Markets List fetched from server
                        JSONArray markets = jObj.getJSONArray("seller");
                        for (int i = 0; i < markets.length(); i++) {
                            JSONObject market = markets.getJSONObject(i);
                            String id = market.getString(Config_TAG.TAG_ID);
                            String name = market.getString(Config_TAG.TAG_NAME);
                            int picture = market.getInt(Config_TAG.TAG_PICTURE);
                            int open_hour = market.getInt(Config_TAG.TAG_OPEN_HOUR);
                            int close_hour = market.getInt(Config_TAG.TAG_CLOSE_HOUR);
                            HashMap<String, String> map = new HashMap<>();
                            map.put(Config_TAG.TAG_ID, id);
                            map.put(Config_TAG.TAG_NAME, name);
                            String add = "i" + String.valueOf(picture);
                            int pic = getResources().getIdentifier(add, "drawable", getPackageName());
                            map.put(Config_TAG.TAG_PICTURE, String.valueOf(pic));
                            Calendar time = Calendar.getInstance();
                            int current_hour = time.get(Calendar.HOUR_OF_DAY);
                            if (current_hour > open_hour && current_hour < close_hour) {
                                pic = getResources().getIdentifier("open", "drawable", getPackageName());
                                map.put(Config_TAG.TAG_STATUS_PICTURE, String.valueOf(pic));
                            } else {
                                pic = getResources().getIdentifier("close", "drawable", getPackageName());
                                map.put(Config_TAG.TAG_STATUS_PICTURE, String.valueOf(pic));
                            }
                            MarketList.add(map);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ListAdapter adapter = new SimpleAdapter(
                                            All_Markets.this, MarketList,
                                            R.layout.list_item, new String[]{
                                            Config_TAG.TAG_ID,
                                            Config_TAG.TAG_NAME,
                                            Config_TAG.TAG_PICTURE,
                                            "",
                                            Config_TAG.TAG_STATUS_PICTURE
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
                Log.e(TAG, "Fetch Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    MakeToast(error.getMessage());
                } else
                    MakeToast("خطایی رخ داده است");
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "sellers");
                params.put(Config_TAG.TAG_TYPE, "Markets");
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

    private void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }
}