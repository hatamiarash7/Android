package com.example.androidhive;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditMarkets extends Activity {

    EditText txtName;
    EditText txtOpenHour;
    EditText txtCloseHour;
    EditText txtAddress;
    EditText txtPicture;
    EditText txtCreatedAt;
    Button btnSaveMarket;
    Button btnDeleteMarket;

    String pid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single product url
    private static final String url_market_detials = "http://zimia.ir/get_market_details.php";

    // url to update product
    private static final String url_update_market = "http://zimia.ir/update_market.php";

    // url to delete product
    private static final String url_delete_market = "http://zimia.ir/delete_market.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MARKET = "market";
    private static final String TAG_PID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_OPENHOUR = "open_hour";
    private static final String TAG_CLOSEHOUR = "close_hour";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_PICTURE = "picture";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_market);

        // save button
        btnSaveMarket = (Button) findViewById(R.id.btnSaveMarket);
        btnDeleteMarket = (Button) findViewById(R.id.btnDeleteMarket);

        // getting product details from intent
        Intent i = getIntent();

        // getting product id (pid) from intent
        pid = i.getStringExtra(TAG_PID);

        // Getting complete product details in background thread
        new GetMarketDetails().execute();

        // save button click event
        btnSaveMarket.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update product
                new SaveMarketDetails().execute();
            }
        });

        // Delete button click event
        btnDeleteMarket.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // deleting product in background thread
                new DeleteMarket().execute();
            }
        });

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
            pDialog = new ProgressDialog(EditMarkets.this);
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
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_market_detials, "GET", params);

                        // check your log for json response
                        Log.d("Single Market Details", json.toString());

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray(TAG_MARKET); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            // product with this pid found
                            // Edit Text
                            txtName = (EditText) findViewById(R.id.inputNameMarket);
                            txtOpenHour = (EditText) findViewById(R.id.inputOpenHourMarket);
                            txtCloseHour = (EditText) findViewById(R.id.inputCloseHourMarket);
                            txtAddress = (EditText) findViewById(R.id.inputAddressMarket);
                            txtPicture = (EditText) findViewById(R.id.inputPictureMarket);

                            // display product data in EditText
                            txtName.setText(product.getString(TAG_NAME));
                            txtOpenHour.setText(product.getString(TAG_OPENHOUR));
                            txtCloseHour.setText(product.getString(TAG_CLOSEHOUR));
                            txtPicture.setText(product.getString(TAG_PICTURE));
                            txtAddress.setText(product.getString(TAG_ADDRESS));

                        } else {
                            // product with pid not found
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
     * Background Async Task to  Save product Details
     */
    class SaveMarketDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditMarkets.this);
            pDialog.setMessage("Saving Market ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving product
         */
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String name = txtName.getText().toString();
            String open_hour = txtOpenHour.getText().toString();
            String close_hour = txtCloseHour.getText().toString();
            String address = txtAddress.getText().toString();
            String picture = txtPicture.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PID, pid));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_OPENHOUR, open_hour));
            params.add(new BasicNameValuePair(TAG_CLOSEHOUR, close_hour));
            params.add(new BasicNameValuePair(TAG_ADDRESS, address));
            params.add(new BasicNameValuePair(TAG_PICTURE, picture));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_market,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update product
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
            // dismiss the dialog once product uupdated
            pDialog.dismiss();
        }
    }

    /*****************************************************************
     * Background Async Task to Delete Product
     */
    class DeleteMarket extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditMarkets.this);
            pDialog.setMessage("Deleting Market ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting product
         */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", pid));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_market, "POST", params);

                // check your log for json response
                Log.d("Delete Market", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about product deletion
                    setResult(100, i);
                    finish();
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
            // dismiss the dialog once product deleted
            pDialog.dismiss();

        }

    }
}
