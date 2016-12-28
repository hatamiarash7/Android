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

import helper.JSONParser;
import helper.TypefaceSpan;
import volley.Config_URL;

public class AllResturans extends ListActivity {
    // url to get all products list
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESTURANS = "resturans";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_STATUS_PICTURE = "status";
    private static final String TAG_OPENHOUR = "open_hour";
    private static final String TAG_CLOSEHOUR = "close_hour";
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> resturanList;
    // products JSONArray
    JSONArray resturans = null;
    // Progress Dialog
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_resturans);
        // Hashmap for ListView
        resturanList = new ArrayList<HashMap<String, String>>();
        // Loading products in Background Thread
        new LoadAllProducts().execute();
        // Get listview
        ListView lv = getListView();
        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                // Starting new intent
                Intent in = new Intent(getApplicationContext(), ResturanDetail.class);
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

    public void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_LONG).show();
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllResturans.this);
            pDialog.setMessage("لطفا منتظر بمانید ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(Config_URL.url_all_resturans, "GET", params);
            // Check your log cat for JSON reponse
            Log.d("All Resturans: ", json.toString());
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    resturans = json.getJSONArray(TAG_RESTURANS);
                    // looping through All Products
                    for (int i = 0; i < resturans.length(); i++) {
                        JSONObject c = resturans.getJSONObject(i);
                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        int picture = c.getInt(TAG_PICTURE);
                        int open_hour = c.getInt(TAG_OPENHOUR);
                        int close_hour = c.getInt(TAG_CLOSEHOUR);
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
                        // adding each child node to HashMap key => value
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
                        // adding HashList to ArrayList
                        resturanList.add(map);
                    }
                } else {
                    // no products found
                    MakeToast("فروشنده ای وجود ندارد");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            AllResturans.this, resturanList,
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