package ir.hatamiarash.hambazidemo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import helper.Adapter_SellerList;
import helper.Helper;
import helper.SQLiteHandler;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class SellersActivity extends AppCompatActivity {
    private static final String TAG = SellersActivity.class.getSimpleName();
    public static SellersActivity pointer;
    private ProgressDialog progressDialog;
    private ArrayList<HashMap<String, String>> SellersList;
    private ListView listView;
    private Vibrator vibrator;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = (ListView) findViewById(R.id.my_list);
        
        pointer = this;
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        SellersList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                Intent i = new Intent(getApplicationContext(), Seller_Detail.class);
                i.putExtra(Config_TAG.TYPE, SellerType);
                i.putExtra(Config_TAG.ID, pid);
                startActivity(i);*/
            }
        });
        FetchAllRestaurants();
    }
    
    private void FetchAllRestaurants() {
        // Tag used to cancel the request
        String string_req = "req_fetch";
        progressDialog.setMessage("لطفا منتظر بمانید");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Sellers Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Restaurants List fetched from server
                        JSONArray sellers = jObj.getJSONArray("sellers");
                        for (int i = 0; i < sellers.length(); i++) {
                            JSONObject seller = sellers.getJSONObject(i);
                            String uid = seller.getString("uid");
                            String name = seller.getString("name");
                            String image = seller.getString("image");
                            HashMap<String, String> map = new HashMap<>();
                            map.put("uid",uid);
                            map.put("name",name);
                            map.put("image",image);
                            SellersList.add(map);

                            Adapter_SellerList adapter = new Adapter_SellerList(SellersList, SellersActivity.this);
                            listView.setAdapter(adapter);
                        }
                    } else {
                        // Error occurred
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        Helper.MakeToast(SellersActivity.this, errorMsg, Config_TAG.ERROR);
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
                    Helper.MakeToast(SellersActivity.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(SellersActivity.this, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "all_sellers");
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