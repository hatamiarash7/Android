/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

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
import android.widget.ImageView;
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
import helper.SQLiteHandler;
import helper.SessionManager;
import helper.TypefaceSpan;
import volley.Config_URL;

public class ItemDetail extends Activity {
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ITEM = "items";
    private static final String TAG_NAME = "name";
    private static final String TAG_PID = "id";
    private static final String TAG_PRICE = "price";
    private static final String TAG_DESC = "specification";
    private static final String TAG_PICTURE = "picture";
    private static final String TAG_TYPE = "item_type";
    private static final String TAG_ID = "item_id";
    EditText counter;
    Button inc, dec, add;
    String pid, item_type;
    TextView ItemName;
    TextView ItemDesc;
    TextView ItemPrice;
    TextView ItemType;
    ImageView ItemImage;
    String ItemName_Backup, ItemPrice_Backup; // values for save data in local database
    int count = 0;                            // item's count
    JSONParser jsonParser = new JSONParser();
    String final_price;                       // for calculating final price after count changed
    SQLiteHandler db;
    SessionManager session; // session for check user logged
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);
        counter = (EditText) findViewById(R.id.count);
        Intent i = getIntent();
        ItemName = (TextView) findViewById(R.id.ItemName);
        ItemDesc = (TextView) findViewById(R.id.ItemDesc);
        ItemPrice = (TextView) findViewById(R.id.ItemPrice);
        ItemType = (TextView) findViewById(R.id.ItemType);
        ItemImage = (ImageView) findViewById(R.id.ItemImage);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext()); // session manager for check user logged
        // get parameters from last activity
        pid = i.getStringExtra(TAG_PID);
        item_type = i.getStringExtra(TAG_TYPE);
        // buttons
        inc = (Button) findViewById(R.id.inc);
        dec = (Button) findViewById(R.id.dec);
        add = (Button) findViewById(R.id.add);
        inc.setOnClickListener(new View.OnClickListener() { // increment button
            @Override
            public void onClick(View view) {
                count++;
                counter.setText(String.valueOf(count));
            }
        });
        dec.setOnClickListener(new View.OnClickListener() { // decrement button
            @Override
            public void onClick(View view) {
                if (count > 1) {
                    count--;
                    counter.setText(String.valueOf(count));
                } else
                    MakeToast("حداقل انتخاب یک عدد می باشد");
            }
        });
        add.setOnClickListener(new View.OnClickListener() { // add button
            @Override
            public void onClick(View view) {
                if (!ItemPrice_Backup.isEmpty() && !ItemName_Backup.isEmpty())
                    if (session.isLoggedIn()) // check for logged status
                        if (count >= 1) {
                            count = Integer.parseInt(counter.getText().toString());
                            final_price = String.valueOf(Integer.parseInt(ItemPrice_Backup) * count);
                            AddToCard(ItemName_Backup, final_price, count);
                            finish();
                        } else
                            MakeToast("حداقل انتخاب یک عدد می باشد");
                    else {
                        MakeToast("شما وارد نشده اید");
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        // start login activity for login before add item to card
                        startActivity(i);
                        finish();
                    }
                else
                    MakeToast("خطا در دریافت اطلاعات از سرور");
            }
        });
        new GetItemDetails().execute(); // get item's details from server
    }

    public void MakeToast(String Message) { // build and show toast with custom typeface
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }

    protected void AddToCard(String name, String price, int count) { // add or update item
        MainScreenActivity.db.addItem(name, price, String.valueOf(count));
        MakeToast("اضافه شد");
    }

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
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("id", pid));
                        params.add(new BasicNameValuePair("type", item_type));
                        // getting product details by making HTTP request
                        JSONObject json = jsonParser.makeHttpRequest(Config_URL.url_item_detials, "GET", params);
                        Log.d("Single Item Details", json.toString());
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) { // successfully received product details
                            JSONArray ItemObj = json.getJSONArray(TAG_ITEM);
                            JSONObject Item = ItemObj.getJSONObject(0);
                            ItemName_Backup = Item.getString(TAG_NAME);
                            ItemName.setText(ItemName_Backup);
                            ItemDesc.setText(Item.getString(TAG_DESC));
                            ItemPrice_Backup = Item.getString(TAG_PRICE);
                            ItemPrice.setText(ItemPrice_Backup);
                            ItemType.setText(Item.getString(TAG_TYPE));
                            int pic_id = Item.getInt(TAG_PICTURE);
                            String pic_name = "i" + String.valueOf(pic_id);
                            int pic = getResources().getIdentifier(pic_name, "drawable", getPackageName()); // get drawable by name
                            ItemImage.setImageResource(pic);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
    }
}