/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import helper.FontHelper;
import helper.SQLiteHandler;
import helper.SQLiteHandlerSetup;
import helper.SessionManager;
import helper.TypefaceSpan;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

import static java.lang.Integer.parseInt;

public class ItemDetail extends Activity {
    private static final String TAG = ItemDetail.class.getSimpleName();
    private EditText counter;
    private Button inc, dec, add, view_web;
    private String pid, item_type;
    private TextView ItemName;
    private TextView ItemDesc;
    private TextView ItemPrice;
    private TextView ItemType;
    private ImageView ItemImage;
    private String ItemName_Backup, ItemPrice_Backup; // values for save data in local database
    private int count = 0, support_web3d = 0;
    private String final_price;                       // for calculating final price after count changed
    SQLiteHandler db;                         // database for users
    private SQLiteHandlerSetup db2;                   // database for setups
    private SessionManager session;                   // session for check user logged
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);
        counter = (EditText) findViewById(R.id.count);
        ItemName = (TextView) findViewById(R.id.ItemName);
        ItemDesc = (TextView) findViewById(R.id.ItemDesc);
        ItemPrice = (TextView) findViewById(R.id.ItemPrice);
        ItemType = (TextView) findViewById(R.id.ItemType);
        ItemImage = (ImageView) findViewById(R.id.ItemImage);
        view_web = (Button) findViewById(R.id.web3d_button);
        db = new SQLiteHandler(getApplicationContext());
        db2 = new SQLiteHandlerSetup(getApplicationContext());
        session = new SessionManager(getApplicationContext());  // session manager for check user logged
        Intent i = getIntent();
        pid = i.getStringExtra(Config_TAG.TAG_ID);              // get id from previous intent
        item_type = i.getStringExtra(Config_TAG.TAG_TYPE_ITEM); // get item type from previous intent
        pDialog = new ProgressDialog(this);                     // Progress dialog
        pDialog.setCancelable(false);
        inc = (Button) findViewById(R.id.inc);
        dec = (Button) findViewById(R.id.dec);
        add = (Button) findViewById(R.id.add);
        ItemName.setVisibility(View.INVISIBLE);
        ItemDesc.setVisibility(View.INVISIBLE);
        ItemPrice.setVisibility(View.INVISIBLE);
        ItemType.setVisibility(View.INVISIBLE);
        ItemImage.setVisibility(View.INVISIBLE);
        counter.setVisibility(View.INVISIBLE);
        inc.setVisibility(View.INVISIBLE);
        dec.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        view_web.setVisibility(View.INVISIBLE);
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
                            count = parseInt(counter.getText().toString());
                            final_price = String.valueOf(parseInt(ItemPrice_Backup) * count);
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
        view_web.setOnClickListener(new View.OnClickListener() {              // increment button
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Web3d.class);
                // start login activity for login before add item to card
                i.putExtra(Config_TAG.TAG_NAME, "item_name");
                startActivity(i);
            }
        });
        FetchItemDetails(); // get item's details from server
    }

    private void MakeToast(String Message) { // build and show toast with custom typeface
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }

    private void AddToCard(String name, String price, int count) { // add or update item
        MainScreenActivity.db.addItem(name, price, String.valueOf(count));
        MakeToast("اضافه شد");
    }

    private void FetchItemDetails() {
        // Tag used to cancel the request
        String tag_string_req = "req_fetch";
        pDialog.setMessage("لطفا منتظر بمانید ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Item Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.TAG_ERROR);
                    if (!error) {
                        JSONObject item = jObj.getJSONObject("item");
                        ItemName_Backup = item.getString(Config_TAG.TAG_NAME);          // get item's name
                        ItemName.setText(ItemName_Backup);
                        ItemDesc.setText(item.getString(Config_TAG.TAG_SPECIFICATION)); // get item's specification
                        ItemPrice_Backup = item.getString(Config_TAG.TAG_PRICE);        // get item's price
                        String text = ItemPrice_Backup + " تومان";
                        ItemPrice.setText(text);
                        ItemType.setText(item.getString(Config_TAG.TAG_TYPE_ITEM));     // get item's type
                        int pic_id = item.getInt(Config_TAG.TAG_PICTURE);               // get item's picture
                        String pic_name = "i" + String.valueOf(pic_id);
                        int pic = getResources().getIdentifier(pic_name, "drawable", getPackageName());
                        ItemImage.setImageResource(pic);
                        support_web3d = item.getInt(Config_TAG.TAG_WEB_3D);             // get item's web3d support
                        HashMap<String, String> property = db2.GetProperties();         // get setup data
                        if (support_web3d == 1 && Integer.parseInt(property.get("web")) == 1)
                            view_web.setVisibility(View.VISIBLE);
                        ItemName.setVisibility(View.VISIBLE);
                        ItemDesc.setVisibility(View.VISIBLE);
                        ItemPrice.setVisibility(View.VISIBLE);
                        ItemType.setVisibility(View.VISIBLE);
                        ItemImage.setVisibility(View.VISIBLE);
                        counter.setVisibility(View.VISIBLE);
                        inc.setVisibility(View.VISIBLE);
                        dec.setVisibility(View.VISIBLE);
                        add.setVisibility(View.VISIBLE);
                    } else {
                        // Error occurred
                        String errorMsg = jObj.getString(Config_TAG.TAG_ERROR_MSG);
                        MakeToast(errorMsg);
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
                    MakeToast(error.getMessage());
                } else
                    MakeToast("خطایی رخ داده است");
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "item_details");
                params.put(Config_TAG.TAG_TYPE, item_type);
                params.put(Config_TAG.TAG_ID, pid);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}