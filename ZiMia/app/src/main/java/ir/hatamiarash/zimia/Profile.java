package ir.hatamiarash.zimia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import helper.SQLiteHandler;
import helper.SessionManager;

public class Profile extends Activity {
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtAddress;
    private TextView txtPhone;
    private Button btnLogout;
    private SQLiteHandler db;
    private SessionManager session;
    private static final String url_person_detials = "http://zimia.ir/users/include/Get_User_Detail.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PERSON = "persons";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_PHONE = "phone";
    String pid,email;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        txtName = (TextView) findViewById(R.id.profile_name);
        txtEmail = (TextView) findViewById(R.id.profile_email);
        txtAddress = (TextView) findViewById(R.id.profile_address);
        txtPhone = (TextView) findViewById(R.id.profile_phone);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        HashMap<String, String> user = db.getUserDetails();
        email = user.get("email");
        Log.d(email,"db : " + email);
        //Intent i = getIntent();
        //pid = i.getStringExtra(TAG_EMAIL);
        new GetPersonDetails().execute();
        /*
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");
        Log.d(name,"Name : "+ name);
        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);*/

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
        startActivity(i);
        finish();
    }

    class GetPersonDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Profile.this);
            pDialog.setMessage("Loading Profile. Please wait...");
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
                        params.add(new BasicNameValuePair("email", email));
                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(url_person_detials, "GET", params);
                        // check your log for json response
                        Log.d("Single Person Details", json.toString());
                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json.getJSONArray(TAG_PERSON); // JSON Array
                            // get first product object from JSON Array
                            JSONObject person = productObj.getJSONObject(0);
                            // product with this pid found
                            // Edit Text
                            txtName.setText(person.getString(TAG_NAME));
                            txtEmail.setText(person.getString(TAG_EMAIL));
                            txtAddress.setText(person.getString(TAG_ADDRESS));
                            txtPhone.setText(person.getString(TAG_PHONE));
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
}