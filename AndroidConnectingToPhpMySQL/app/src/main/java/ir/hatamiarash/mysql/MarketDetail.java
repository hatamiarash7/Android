package ir.hatamiarash.mysql;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarketDetail extends ListActivity {

    // single product url
    private static final String url_market_detials = "http://zimia.ir/get_market_details.php";
    // JSON Node names
    private static final String url_all_market_products = "http://zimia.ir/get_all_market_products.php";

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
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> productList;
    JSONArray products = null;
    // Progress Dialog
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_market);

        marketname = (TextView) findViewById(R.id.MarketName);
        marketopenhour = (TextView) findViewById(R.id.MarketOpenHour);
        marketclosehour = (TextView) findViewById(R.id.MarketCloseHour);
        marketaddress = (TextView) findViewById(R.id.MarketAddress);
        // getting product details from intent
        Intent i = getIntent();
        // getting product id (pid) from intent
        pid = i.getStringExtra(TAG_PID);
        // Getting complete product details in background thread
        new GetMarketDetails().execute();
        // Hashmap for ListView
        productList = new ArrayList<HashMap<String, String>>();
        // Loading products in Background Thread
        new MarketDetail.LoadAllProducts().execute();
        // Get listview
        ListView lv = getListView();
        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting values from selected ListItem
                //String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                // Starting new intent
                //Intent in = new Intent(getApplicationContext(), EditMarkets.class);
                // sending pid to next activity
                //in.putExtra(TAG_PID, pid);
                // starting new activity and expecting some response back
                //startActivityForResult(in, 100);
            }
        });

    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Background Async Task to Get complete product details
     */
    class GetMarketDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MarketDetail.this);
            pDialog.setMessage("Loading Market details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         */
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("id", pid));
                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(url_market_detials, "GET", params);
                        // check your log for json response
                        Log.d("Single Market Details", json.toString());
                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json.getJSONArray(TAG_MARKET); // JSON Array
                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);
                            // product with this pid found
                            // Edit Text
                            marketname.setText("فروشگاه " + product.getString(TAG_NAME));
                            marketopenhour.setText(product.getString(TAG_OPENHOUR));
                            marketclosehour.setText(product.getString(TAG_CLOSEHOUR));
                            marketaddress.setText("آدرس : " + product.getString(TAG_ADDRESS));
                        } else {
                            // product with pid not found
                            //Log.d("pid not found", null);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pDialog = new ProgressDialog(EditMarkets.this);
            //pDialog.setMessage("Loading Products. Please wait...");
            //pDialog.setIndeterminate(false);
            //pDialog.setCancelable(false);
            //pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", pid));
            // getting JSON string from URL
            JSONObject jso = jParser.makeHttpRequest(url_all_market_products, "GET", params);
            // Check your log cat for JSON reponse
            Log.d("All Products: ", jso.toString());
            try {
                // Checking for SUCCESS TAG
                int success = jso.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = jso.getJSONArray(TAG_PRODUCTS);
                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
                        // Storing each json item in variable
                        String id = c.getString(TAG_PRODUCT_PID);
                        String name = c.getString(TAG_PRODUCT_NAME);
                        String price = c.getString(TAG_PRODUCT_PRICE);
                        int picture = c.getInt(TAG_PRODUCT_PICTURE);
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
                        // adding each child node to HashMap key => value
                        map.put(TAG_PRODUCT_PID, id);
                        map.put(TAG_PRODUCT_NAME, name);
                        map.put(TAG_PRODUCT_PRICE, price);
                        String add = "i"+String.valueOf(picture);
                        int pic = getResources().getIdentifier(add, "drawable", getPackageName());
                        map.put(TAG_PRODUCT_PICTURE, String.valueOf(pic));
                        // adding HashList to ArrayList
                        productList.add(map);
                    }
                } else {
                    // no products found
                    //Log.d("no food", null);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
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
                    // updating listview
                    setListAdapter(adapter);
                }
            });
        }
    }

}
