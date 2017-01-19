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

public class RestaurantDetail extends ListActivity {
    private static final String TAG = RestaurantDetail.class.getSimpleName();
    ArrayList<HashMap<String, String>> ProductsList;
    private ProgressDialog pDialog;
    TextView restaurant_name;
    TextView restaurant_open_hour;
    TextView restaurant_close_hour;
    TextView restaurant_address;
    Boolean is_open = false;
    String pid;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_detail);
        restaurant_name = (TextView) findViewById(R.id.RestaurantName);
        restaurant_open_hour = (TextView) findViewById(R.id.RestaurantOpenHour);
        restaurant_close_hour = (TextView) findViewById(R.id.RestaurantCloseHour);
        restaurant_address = (TextView) findViewById(R.id.RestaurantAddress);
        restaurant_name.setText(null);
        restaurant_open_hour.setText(null);
        restaurant_close_hour.setText(null);
        restaurant_address.setText(null);
        pDialog = new ProgressDialog(this);           // Progress dialog
        pDialog.setCancelable(false);
        Intent i = getIntent();
        pid = i.getStringExtra(Config_TAG.TAG_ID);    // get param from top level
        FetchRestaurantDetails();                     // start to fetching data from server
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (is_open) {
                    String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                    Intent i = new Intent(getApplicationContext(), ItemDetail.class);
                    i.putExtra(Config_TAG.TAG_ID, pid);
                    i.putExtra("item_type", "Restaurant_Foods");
                    startActivityForResult(i, 100);
                } else
                    MakeToast("این فروشگاه در حال حاضر قادر به خدمت رسانی نمی باشد");
            }
        });
    }
    
    public void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
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

    private void FetchRestaurantDetails() {
        // Tag used to cancel the request
        String tag_string_req = "req_fetch";
        pDialog.setMessage("لطفا منتظر بمانید ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.url_register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Restaurants Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.TAG_ERROR);
                    if (!error) {
                        JSONObject seller = jObj.getJSONObject("seller");
                        String open_hour = seller.getString(Config_TAG.TAG_OPEN_HOUR);
                        String close_hour = seller.getString(Config_TAG.TAG_CLOSE_HOUR);
                        String name = "رستوران " + seller.getString(Config_TAG.TAG_NAME);
                        String address = "آدرس : " + seller.getString(Config_TAG.TAG_ADDRESS);
                        restaurant_name.setText(name);
                        restaurant_open_hour.setText(open_hour);
                        restaurant_close_hour.setText(close_hour);
                        restaurant_address.setText(address);
                        Calendar time = Calendar.getInstance();
                        int current_hour = time.get(Calendar.HOUR_OF_DAY);
                        is_open = current_hour > Integer.parseInt(open_hour) && current_hour < Integer.parseInt(close_hour);
                        FetchRestaurantProducts();
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
                MakeToast(error.getMessage());
                hideDialog();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "seller_details");
                params.put(Config_TAG.TAG_TYPE, "Restaurants");
                params.put(Config_TAG.TAG_ID, pid);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void FetchRestaurantProducts() {
        // Tag used to cancel the request
        String tag_string_req = "req_fetch";
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.url_register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Products Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.TAG_ERROR);
                    if (!error) {
                        // Products List fetched from server
                        JSONArray product = jObj.getJSONArray("products");
                        for (int i = 0; i < product.length(); i++) {
                            JSONObject products = product.getJSONObject(i);
                            String id = products.getString(Config_TAG.TAG_ID);
                            String name = products.getString(Config_TAG.TAG_NAME);
                            String price = products.getString(Config_TAG.TAG_PRICE);
                            String specification = products.getString(Config_TAG.TAG_SPECIFICATION);
                            int picture = products.getInt(Config_TAG.TAG_PICTURE);
                            String type = products.getString(Config_TAG.TAG_TYPE);
                            price += " تومان";
                            HashMap<String, String> map = new HashMap<>();
                            map.put(Config_TAG.TAG_ID, id);
                            map.put(Config_TAG.TAG_NAME, name);
                            map.put(Config_TAG.TAG_PRICE, price);
                            String add = "i" + String.valueOf(picture);
                            int pic = getResources().getIdentifier(add, "drawable", getPackageName());
                            map.put(Config_TAG.TAG_PICTURE, String.valueOf(pic));
                            ProductsList.add(map);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ListAdapter adapter = new SimpleAdapter(
                                            RestaurantDetail.this, ProductsList,
                                            R.layout.list_item, new String[]{
                                            Config_TAG.TAG_ID,
                                            Config_TAG.TAG_NAME,
                                            Config_TAG.TAG_PRICE,
                                            Config_TAG.TAG_PICTURE
                                    },
                                            new int[]{
                                                    R.id.pid,
                                                    R.id.name,
                                                    R.id.price,
                                                    R.id.img
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
                MakeToast(error.getMessage());
                hideDialog();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "seller_products");
                params.put(Config_TAG.TAG_TYPE, "Restaurants_Foods");
                params.put(Config_TAG.TAG_ID, pid);
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