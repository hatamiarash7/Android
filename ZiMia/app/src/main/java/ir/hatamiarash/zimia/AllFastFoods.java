/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import helper.FontHelper;
import helper.JSONParser;
import helper.TypefaceSpan;
import volley.Config_URL;

public class AllFastFoods extends ListActivity {
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FASTFOODS = "fastfoods";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_STATUS_PICTURE = "status";
    private static final String TAG_OPENHOUR = "open_hour";
    private static final String TAG_CLOSEHOUR = "close_hour";
    JSONParser jParser = new JSONParser(); // JSON Parser object
    ArrayList<HashMap<String, String>> fastfoodsList;
    JSONArray fastfoods = null;            // products JSONArray
    private ProgressDialog pDialog;        // Progress Dialog

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_fastfoods);
        fastfoodsList = new ArrayList<HashMap<String, String>>();
        new LoadAllProducts().execute(); // Loading products in Background Thread
        ListView lv = getListView();     // Get listview
        // on seleting single product launching Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                Intent in = new Intent(getApplicationContext(), FastFoodDetail.class);
                in.putExtra(TAG_PID, pid); // sending pid to next activity
                startActivityForResult(in, 100);
            }
        });
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

    public void MakeToast(String Message) { // build and show toast with custom typeface
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllFastFoods.this);
            pDialog.setMessage("لطفا منتظر بمانید ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = jParser.makeHttpRequest(Config_URL.url_all_fastfoods, "GET", params);
            Log.d("All FastFoods: ", json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    fastfoods = json.getJSONArray(TAG_FASTFOODS);
                    for (int i = 0; i < fastfoods.length(); i++) { // get all products from json array
                        JSONObject c = fastfoods.getJSONObject(i);
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        int picture = c.getInt(TAG_PICTURE);
                        int open_hour = c.getInt(TAG_OPENHOUR);
                        int close_hour = c.getInt(TAG_CLOSEHOUR);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_PID, id);
                        map.put(TAG_NAME, name);
                        String add = "i" + String.valueOf(picture); // put an i in first of picture name (drawables should start with alphabetic )
                        int pic = getResources().getIdentifier(add, "drawable", getPackageName()); // get picture name from drawables by value
                        map.put(TAG_PICTURE, String.valueOf(pic));
                        Calendar time = Calendar.getInstance();
                        int current_hour = time.get(Calendar.HOUR_OF_DAY);           // get current device time
                        if (current_hour > open_hour && current_hour < close_hour) { // if shop is open
                            pic = getResources().getIdentifier("open", "drawable", getPackageName());
                            map.put(TAG_STATUS_PICTURE, String.valueOf(pic));        // put open picture
                        } else {                                                     // if shop is close
                            pic = getResources().getIdentifier("close", "drawable", getPackageName());
                            map.put(TAG_STATUS_PICTURE, String.valueOf(pic));        // put close picture
                        }
                        fastfoodsList.add(map);
                    }
                } else {
                    MakeToast("فروشنده ای وجود ندارد");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            AllFastFoods.this, fastfoodsList,
                            R.layout.list_item, new String[]{
                            TAG_PID,
                            TAG_NAME,
                            TAG_PICTURE,
                            "", // send empty text ( we don't need price in list )
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
    }
}