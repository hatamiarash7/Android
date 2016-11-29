package com.example.androidhive;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewMarket extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputName;
    EditText inputOpenHour;
    EditText inputCloseHour;
    EditText inputAddress;
    EditText inputPicture;

    // url to create new product
    private static String url_create_market = "http://zimia.ir/create_market.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_market);

        // Edit Text
        inputName = (EditText) findViewById(R.id.NewNameMarket);
        inputOpenHour = (EditText) findViewById(R.id.NewOpenHourMarket);
        inputCloseHour = (EditText) findViewById(R.id.NewCloseHourMarket);
        inputAddress = (EditText) findViewById(R.id.NewAddressMarket);
        inputPicture = (EditText) findViewById(R.id.NewPictureMarket);

        // Create button
        Button btnCreateMarket = (Button) findViewById(R.id.btnCreateMarket);

        // button click event
        btnCreateMarket.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new product in background thread
                new CreateNewMarket().execute();
            }
        });
    }

    /**
     * Background Async Task to Create new product
     */
    class CreateNewMarket extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewMarket.this);
            pDialog.setMessage("Creating Market ..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {
            String name = inputName.getText().toString();
            String open_hour = inputOpenHour.getText().toString();
            String close_hour = inputCloseHour.getText().toString();
            String address = inputAddress.getText().toString();
            String picture = inputPicture.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("open_hour", open_hour));
            params.add(new BasicNameValuePair("close_hour", close_hour));
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("picture", picture));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_market,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), AllMarkets.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
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
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
}
