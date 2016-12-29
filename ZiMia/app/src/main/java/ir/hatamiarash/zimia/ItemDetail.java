package ir.hatamiarash.zimia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import helper.FontHelper;
import helper.JSONParser;
import helper.TypefaceSpan;
import volley.Config_URL;

public class ItemDetail extends Activity {
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ITEM = "items";
    private static final String TAG_NAME = "name";
    private static final String TAG_PID = "id";
    private static final String TAG_PRICE = "price";
    private static final String TAG_DESC = "desc";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_TYPE = "type";
    EditText counter;
    Button inc;
    Button dec;
    String pid;
    /*TextView ItemName = (TextView) findViewById(R.id.ItemName);
    TextView ItemDesc = (TextView) findViewById(R.id.ItemDesc);
    TextView ItemPrice = (TextView) findViewById(R.id.ItemPrice);
    TextView ItemType = (TextView) findViewById(R.id.ItemType);*/
    int count = 0;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);
        counter = (EditText) findViewById(R.id.count);
        Intent i = getIntent();
        pid = i.getStringExtra(TAG_PID);
        inc = (Button) findViewById(R.id.inc);
        dec = (Button) findViewById(R.id.dec);

        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                counter.setText(String.valueOf(count));
            }
        });
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count > 1) {
                    count--;
                    counter.setText(String.valueOf(count));
                } else
                    MakeToast("حداقل انتخاب یک عدد می باشد");
            }
        });
    }

    public void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_LONG).show();
    }
/*
    class GetItemDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ItemDetail.this);
            pDialog.setMessage("لطفا منتظر بمانید ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
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
                        JSONObject json = jsonParser.makeHttpRequest(Config_URL.url_fastfood_detials, "GET", params);
                        // check your log for json response
                        Log.d("Single FastFood Details", json.toString());
                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json.getJSONArray(TAG_ITEM); // JSON Array
                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);
                            // product with this pid found
                            // Edit Text
                            ItemName.setText("فست فود " + product.getString(TAG_NAME));
                            ItemDesc.setText(product.getString(TAG_DESC));
                            ItemPrice.setText(product.getString(TAG_PRICE));
                            ItemType.setText("آدرس : " + product.getString(TAG_TYPE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }
*/
}