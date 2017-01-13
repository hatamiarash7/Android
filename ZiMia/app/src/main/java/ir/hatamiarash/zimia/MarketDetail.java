/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

public class MarketDetail extends ListActivity {
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MARKET = "markets";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_OPENHOUR = "open_hour";
    private static final String TAG_CLOSEHOUR = "close_hour";
    private static final String TAG_ADDRESS = "address";

    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PRODUCT_PID = "id";
    private static final String TAG_PRODUCT_NAME = "name";
    private static final String TAG_PRODUCT_PRICE = "price";
    private static final String TAG_PRODUCT_PICTURE = "picture";
    TextView marketname;
    TextView marketopenhour;
    TextView marketclosehour;
    TextView marketaddress;
    String pid;
    JSONParser jsonParser = new JSONParser();
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> productList;
    JSONArray products = null;
    Boolean is_open = false;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_detail);
        marketname = (TextView) findViewById(R.id.MarketName);
        marketopenhour = (TextView) findViewById(R.id.MarketOpenHour);
        marketclosehour = (TextView) findViewById(R.id.MarketCloseHour);
        marketaddress = (TextView) findViewById(R.id.MarketAddress);
        Intent i = getIntent();
        pid = i.getStringExtra(TAG_PID);
        new GetMarketDetails().execute();
        productList = new ArrayList<HashMap<String, String>>();
        new MarketDetail.LoadAllProducts().execute();
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (is_open) {
                    String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                    Intent in = new Intent(getApplicationContext(), ItemDetail.class);
                    in.putExtra(TAG_PID, pid);
                    in.putExtra("item_type", "Market_Products");
                    startActivityForResult(in, 100);
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

    class GetMarketDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MarketDetail.this);
            pDialog.setMessage("لطفا منتظر بمانید ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                public void run() {
                    int success;
                    try {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("id", pid));
                        JSONObject json = jsonParser.makeHttpRequest(Config_URL.url_market_detials, "GET", params);
                        Log.d("Single Market Details", json.toString());
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            JSONArray productObj = json.getJSONArray(TAG_MARKET);
                            JSONObject product = productObj.getJSONObject(0);
                            String openh = product.getString(TAG_OPENHOUR);
                            String closeh = product.getString(TAG_CLOSEHOUR);
                            Calendar time = Calendar.getInstance();
                            int current_hour = time.get(Calendar.HOUR_OF_DAY);
                            is_open = current_hour > Integer.parseInt(openh) && current_hour < Integer.parseInt(closeh);
                            String text = "فروشگاه " + product.getString(TAG_NAME);
                            marketname.setText(text);
                            marketopenhour.setText(openh);
                            marketclosehour.setText(closeh);
                            text = "آدرس : " + product.getString(TAG_ADDRESS);
                            marketaddress.setText(text);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", pid));
            JSONObject jso = jParser.makeHttpRequest(Config_URL.url_all_market_products, "GET", params);
            Log.d("All Products: ", jso.toString());
            try {
                int success = jso.getInt(TAG_SUCCESS);
                if (success == 1) {
                    products = jso.getJSONArray(TAG_PRODUCTS);
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
                        String id = c.getString(TAG_PRODUCT_PID);
                        String name = c.getString(TAG_PRODUCT_NAME);
                        String price = c.getString(TAG_PRODUCT_PRICE);
                        price += " تومان";
                        int picture = c.getInt(TAG_PRODUCT_PICTURE);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_PRODUCT_PID, id);
                        map.put(TAG_PRODUCT_NAME, name);
                        map.put(TAG_PRODUCT_PRICE, price);
                        String add = "i" + String.valueOf(picture);
                        int pic = getResources().getIdentifier(add, "drawable", getPackageName());
                        map.put(TAG_PRODUCT_PICTURE, String.valueOf(pic));
                        productList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            MarketDetail.this, productList,
                            R.layout.list_item, new String[]{
                            TAG_PRODUCT_PID,
                            TAG_PRODUCT_NAME,
                            TAG_PRODUCT_PRICE,
                            TAG_PRODUCT_PICTURE
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
            pDialog.dismiss();
        }
    }
}