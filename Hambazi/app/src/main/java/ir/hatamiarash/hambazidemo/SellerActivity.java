package ir.hatamiarash.hambazidemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import helper.Adapter_Product;
import helper.Helper;
import helper.LoadImageTask;
import helper.Product;
import helper.SQLiteHandler;
import jp.wasabeef.blurry.Blurry;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class SellerActivity extends AppCompatActivity implements LoadImageTask.Listener {
    private static final String TAG = SellerActivity.class.getSimpleName();
    private RecyclerView list;
    private List<Product> productList;
    private ProgressDialog progressDialog;
    private SQLiteHandler db;
    private TextView seller_name;
    private ImageView logo, blur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        list = (RecyclerView) findViewById(R.id.list);
        seller_name = (TextView) findViewById(R.id.name);
        logo = (ImageView) findViewById(R.id.logo);
        blur = (ImageView) findViewById(R.id.blur);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        list.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        productList = new ArrayList<>();
        db = new SQLiteHandler(getApplicationContext());
        Intent i = getIntent();

        FetchSellerDetails(i.getStringExtra("uid"));
    }

    private void FetchSellerDetails(final String uid) {
        // Tag used to cancel the request
        String string_req = "req_fetch";
        progressDialog.setMessage("لطفا منتظر بمانید");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "products Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        JSONObject seller = jObj.getJSONObject("seller");
                        String name = seller.getString("name");
                        String description = seller.getString("description");
                        String point = seller.getString("point");
                        String image = seller.getString("image");

                        seller_name.setText(name);

                        if (!image.equals("null"))
                            new LoadImageTask(SellerActivity.this).execute(Config_URL.image_URL + image);
                        else
                            logo.setImageDrawable(getResources().getDrawable(R.drawable.nnull));

                        FetchAllProducts(uid);
                    } else {
                        // Error occurred
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        Helper.MakeToast(SellerActivity.this, errorMsg, Config_TAG.ERROR);
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
                    Helper.MakeToast(SellerActivity.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(SellerActivity.this, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "seller_details");
                params.put(Config_TAG.UID, uid);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }
    
    private void FetchAllProducts(final String uid) {
        // Tag used to cancel the request
        String string_req = "req_fetch";
        progressDialog.setMessage("لطفا منتظر بمانید");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "products Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Restaurants List fetched from server
                        JSONArray sellers = jObj.getJSONArray("products");
                        for (int i = 0; i < sellers.length(); i++) {
                            JSONObject seller = sellers.getJSONObject(i);
                            String uid = seller.getString("uid");
                            String name = seller.getString("name");
                            String image = seller.getString("image");
                            String price = seller.getString("price");
                            int price_off = seller.getInt("price_off");
                            productList.add(new Product(uid, name, image, price, Helper.CalculatePrice(price, price_off)));
                        }
                        Adapter_Product adapter = new Adapter_Product(SellerActivity.this, productList);
                        list.setAdapter(adapter);
                    } else {
                        // Error occurred
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        Helper.MakeToast(SellerActivity.this, errorMsg, Config_TAG.ERROR);
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
                    Helper.MakeToast(SellerActivity.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(SellerActivity.this, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "all_products");
                params.put(Config_TAG.UID, uid);
                params.put(Config_TAG.TYPE, db.getUserDetails().get("type"));
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    @Override
    public void onImageLoaded(Bitmap bitmap) {
        logo.setImageBitmap(bitmap);
        Blurry.with(SellerActivity.this)
                .radius(10)
                .sampling(3)
                .async()
                .animate(2500)
                .from(bitmap)
                .into(blur);
    }

    @Override
    public void onError() {
        Log.d(TAG, "مشکل در بارگزاری تصویر");
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
