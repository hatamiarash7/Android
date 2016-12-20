package ir.hatamiarash.zimia;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AllMarkets extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> marketsList;

    // url to get all products list
    private static String url_all_markets = "http://zimia.ir/get_all_markets.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MARKETS = "markets";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_STATUS_PICTURE = "status";
    private static final String TAG_OPENHOUR = "open_hour";
    private static final String TAG_CLOSEHOUR = "close_hour";

    // products JSONArray
    JSONArray markets = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_markets);

        // Hashmap for ListView
        marketsList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadAllProducts().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(), MarketDetail.class);
                // sending pid to next activity
                in.putExtra(TAG_PID, pid);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
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
     * Background Async Task to Load all product by making HTTP Request
     */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllMarkets.this);
            pDialog.setMessage("Loading Markets. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_markets, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Markets: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    markets = json.getJSONArray(TAG_MARKETS);

                    // looping through All Products
                    for (int i = 0; i < markets.length(); i++) {
                        JSONObject c = markets.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        int picture = c.getInt(TAG_PICTURE);
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_NAME, name);
                        String add = "i"+String.valueOf(picture);
                        int pic = getResources().getIdentifier(add, "drawable", getPackageName());
                        int open_hour = c.getInt(TAG_OPENHOUR);
                        int close_hour = c.getInt(TAG_CLOSEHOUR);
                        map.put(TAG_PICTURE, String.valueOf(pic));
                        Calendar time = Calendar.getInstance();
                        int current_hour = time.get(Calendar.HOUR_OF_DAY);
                        if (current_hour > open_hour && current_hour < close_hour) {
                            pic = getResources().getIdentifier("open", "drawable", getPackageName());
                            map.put(TAG_STATUS_PICTURE, String.valueOf(pic));
                        }
                        else {
                            pic = getResources().getIdentifier("close", "drawable", getPackageName());
                            map.put(TAG_STATUS_PICTURE, String.valueOf(pic));
                        }
                        // adding HashList to ArrayList
                        marketsList.add(map);
                    }
                } else {
                    // no products found

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
                            AllMarkets.this, marketsList,
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
                    // updating listview
                    setListAdapter(adapter);
                }
            });
        }
    }
}