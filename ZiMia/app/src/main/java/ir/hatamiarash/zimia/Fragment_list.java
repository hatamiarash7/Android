package ir.hatamiarash.zimia;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import helper.ProductAdapter;
import ir.hatamiarash.MyToast.CustomToast;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class Fragment_list extends Fragment {
    private static final String TAG = Fragment_list.class.getSimpleName();
    private ListView listView;
    private ArrayList<HashMap<String, String>> ProductsList;
    static private AlertDialog progressDialog;
    private View view;
    private String SellerType;
    private String PID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        ProductsList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.seller_list);
        SellerType = getArguments().getString("SellerType");
        PID = getArguments().getString("PID");
        FetchSellerProducts();
        return view;
    }

    public static Fragment newInstance(String type, String pid, AlertDialog pDialog) {
        Fragment_list fragment_list = new Fragment_list();
        Bundle bundle = new Bundle();
        bundle.putString("SellerType", type);
        bundle.putString("PID", pid);
        fragment_list.setArguments(bundle);
        progressDialog = pDialog;
        return fragment_list;
    }

    private void FetchSellerProducts() {
        // Tag used to cancel the request
        String string_req = "req_fetch";
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Products Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Products List fetched from server
                        JSONArray product = jObj.getJSONArray("products");
                        for (int i = 0; i < product.length(); i++) {
                            JSONObject products = product.getJSONObject(i);
                            String id = products.getString(Config_TAG.ID);
                            String name = products.getString(Config_TAG.NAME);
                            String price = products.getString(Config_TAG.PRICE);
                            String specification = products.getString(Config_TAG.SPECIFICATION);
                            int picture = products.getInt(Config_TAG.PICTURE);
                            int price_off = products.getInt(Config_TAG.PRICE_OFF);
                            int web3d = products.getInt(Config_TAG.WEB_3D);
                            //String type = products.getString(Config_TAG.TYPE);
                            price += " تومان";
                            HashMap<String, String> map = new HashMap<>();
                            map.put(Config_TAG.ID, id);
                            map.put(Config_TAG.NAME, name);
                            map.put(Config_TAG.PRICE, price);
                            map.put(Config_TAG.SPECIFICATION, specification);
                            map.put(Config_TAG.PRICE_OFF, String.valueOf(price_off));
                            map.put(Config_TAG.WEB_3D, String.valueOf(web3d));
                            String add = "i" + String.valueOf(picture);
                            int pic = getResources().getIdentifier(add, "drawable", Seller_Detail.pointer.getPackageName());
                            map.put(Config_TAG.PICTURE, String.valueOf(pic));
                            ProductsList.add(map);
                            ProductAdapter adapter = new ProductAdapter(ProductsList, Seller_Detail.pointer);
                            listView.setAdapter(adapter);
                        }
                    } else {
                        // Error occurred
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        MakeToast(errorMsg, Config_TAG.ERROR);
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
                    MakeToast(error.getMessage(), Config_TAG.ERROR);
                } else
                    MakeToast("خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "seller_products");
                params.put(Config_TAG.TYPE, Convert(SellerType));
                params.put(Config_TAG.ID, PID);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    private void MakeToast(String Message, String TAG) {
        if (TAG.equals(Config_TAG.WARNING))
            CustomToast.custom(Seller_Detail.pointer, Message, R.drawable.ic_alert, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
        if (TAG.equals(Config_TAG.SUCCESS))
            CustomToast.custom(Seller_Detail.pointer, Message, R.drawable.ic_success, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
        if (TAG.equals(Config_TAG.ERROR))
            CustomToast.custom(Seller_Detail.pointer, Message, R.drawable.ic_error, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private String Convert(String seller) {
        String converted = null;
        switch (seller) {
            case "Restaurants":
                converted = seller.substring(0, seller.length() - 1);
                converted += "_Foods";
                break;
            case "FastFoods":
                converted = seller.substring(0, seller.length() - 1);
                converted += "_Foods";
                break;
            case "Markets":
                converted = seller.substring(0, seller.length() - 1);
                converted += "_Products";
                break;
        }
        return converted;
    }
}
