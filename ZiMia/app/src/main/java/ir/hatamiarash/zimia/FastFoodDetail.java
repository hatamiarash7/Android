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

public class FastFoodDetail extends ListActivity {
    // shop detail tags
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FASTFOOD = "fastfoods";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_OPENHOUR = "open_hour";
    private static final String TAG_CLOSEHOUR = "close_hour";
    private static final String TAG_ADDRESS = "address";
    // shop products tags
    private static final String TAG_FOODS = "foods";
    private static final String TAG_FOOD_PID = "id";
    private static final String TAG_FOOD_NAME = "name";
    private static final String TAG_FOOD_PRICE = "price";
    private static final String TAG_FOOD_PICTURE = "picture";
    TextView fastfoodname;
    TextView fastfoodopenhour;
    TextView fastfoodclosehour;
    TextView fastfoodaddress;
    String pid;
    int picture;
    Boolean is_open = false;
    JSONParser jsonParser = new JSONParser(); // JSON parser class
    JSONParser jParser = new JSONParser();    // JSON parser class
    ArrayList<HashMap<String, String>> foodList;
    JSONArray foods = null;
    private ProgressDialog pDialog;           // Progress Dialog

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fastfood_detail);
        fastfoodname = (TextView) findViewById(R.id.FastFoodName);
        fastfoodopenhour = (TextView) findViewById(R.id.FastFoodOpenHour);
        fastfoodclosehour = (TextView) findViewById(R.id.FastFoodCloseHour);
        fastfoodaddress = (TextView) findViewById(R.id.FastFoodAddress);
        Intent i = getIntent();
        pid = i.getStringExtra(TAG_PID);    // getting product id from intent
        new GetFastFoodDetails().execute(); // Getting shop details
        foodList = new ArrayList<HashMap<String, String>>();
        new LoadAllFoods().execute();       // getting shop products
        ListView lv = getListView();
        // on selecting single product launching Product Screen
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (is_open) { // check that shop is open or not
                    String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();
                    Intent in = new Intent(getApplicationContext(), ItemDetail.class);
                    in.putExtra(TAG_PID, pid);                  // sending pid to next activity
                    in.putExtra("item_type", "FastFood_Foods"); // sending type to next activity
                    startActivityForResult(in, 100);
                } else
                    MakeToast("این فروشگاه در حال حاضر قادر به خدمت رسانی نمی باشد");
            }
        });
    }

    public void MakeToast(String Message) { // build and show toast with custom typeface
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

    class GetFastFoodDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FastFoodDetail.this);
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
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("id", pid));
                        JSONObject json = jsonParser.makeHttpRequest(Config_URL.url_fastfood_detials, "GET", params);
                        Log.d("Single FastFood Details", json.toString());
                        success = json.getInt(TAG_SUCCESS); // json success tag
                        if (success == 1) {                 // successfully received product details
                            JSONArray productObj = json.getJSONArray(TAG_FASTFOOD);
                            JSONObject product = productObj.getJSONObject(0);
                            String openh = product.getString(TAG_OPENHOUR);
                            String closeh = product.getString(TAG_CLOSEHOUR);
                            Calendar time = Calendar.getInstance();
                            int current_hour = time.get(Calendar.HOUR_OF_DAY);      // get current device time
                            is_open = current_hour > Integer.parseInt(openh) && current_hour < Integer.parseInt(closeh);
                            String text = "فست فود " + product.getString(TAG_NAME); // add shop type to first or it's name
                            fastfoodname.setText(text);
                            fastfoodopenhour.setText(openh);
                            fastfoodclosehour.setText(closeh);
                            text = "آدرس : " + product.getString(TAG_ADDRESS);
                            fastfoodaddress.setText(text);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }

    class LoadAllFoods extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", pid));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(Config_URL.url_all_fastfood_foods, "GET", params);
            Log.d("All Foods: ", json.toString());
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    foods = json.getJSONArray(TAG_FOODS);      // Getting Array of Products
                    for (int i = 0; i < foods.length(); i++) { // looping through All Products
                        JSONObject c = foods.getJSONObject(i);
                        String id = c.getString(TAG_FOOD_PID);
                        String name = c.getString(TAG_FOOD_NAME);
                        String price = c.getString(TAG_FOOD_PRICE);
                        price += " تومان";
                        picture = c.getInt(TAG_FOOD_PICTURE);
                        HashMap<String, String> map = new HashMap<String, String>();
                        // adding each child node to HashMap key => value
                        map.put(TAG_FOOD_PID, id);
                        map.put(TAG_FOOD_NAME, name);
                        map.put(TAG_FOOD_PRICE, price);
                        String add = "i" + String.valueOf(picture);
                        int pic = getResources().getIdentifier(add, "drawable", getPackageName()); // get drawable by name
                        map.put(TAG_FOOD_PICTURE, String.valueOf(pic));
                        foodList.add(map);
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
                            FastFoodDetail.this, foodList,
                            R.layout.list_item, new String[]{
                            TAG_FOOD_PID,
                            TAG_FOOD_NAME,
                            TAG_FOOD_PRICE,
                            TAG_FOOD_PICTURE
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