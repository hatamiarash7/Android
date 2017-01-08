package ir.hatamiarash.zimia;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import helper.JSONParser;
import helper.SQLiteHandler;
import helper.SessionManager;
import volley.Config_URL;

public class Profile extends Activity {
    private static final String TAG = Profile.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PERSON = "persons";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_PHONE = "phone";
    String email;
    JSONParser jsonParser = new JSONParser();
    Button btnLogout;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtAddress;
    private TextView txtPhone;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        txtName = (TextView) findViewById(R.id.profile_name);
        txtEmail = (TextView) findViewById(R.id.profile_email);
        txtAddress = (TextView) findViewById(R.id.profile_address);
        txtPhone = (TextView) findViewById(R.id.profile_phone);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        HashMap<String, String> user = db.getUserDetails();       // get user detial from local database
        email = user.get("email");
        Log.d(email, "db : " + email);
        new GetPersonDetails().execute();                         // get person detail from server
        btnLogout.setOnClickListener(new View.OnClickListener() { // logout button's event
            @Override
            public void onClick(View v) {
                logoutUser(); // logout user
            }
        });
    }

    public void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();                 // delete user from local database
        new DelPersonDetails().execute(); // delete user from server
        Toast.makeText(getApplicationContext(), "خروج موفقیت آمیز بود", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
        MainScreenActivity.pointer.finish();
        // finish old activity and start again for refresh
        startActivity(i);
        finish();
    }

    class GetPersonDetails extends AsyncTask<String, String, String> {
        protected String doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("email", email));
                        JSONObject json = jsonParser.makeHttpRequest(Config_URL.url_get_person_detials, "GET", params);
                        Log.d("Single Person Details", json.toString());
                        if (json.getInt(TAG_SUCCESS) == 1) {
                            JSONArray productObj = json.getJSONArray(TAG_PERSON);
                            JSONObject person = productObj.getJSONObject(0);
                            txtName.setText(person.getString(TAG_NAME));
                            txtEmail.setText(person.getString(TAG_EMAIL));
                            txtAddress.setText(person.getString(TAG_ADDRESS));
                            txtPhone.setText(person.getString(TAG_PHONE));
                        } else {
                            Log.d(TAG, "Error !");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }

    class DelPersonDetails extends AsyncTask<String, String, String> {
        protected String doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("email", email));
                        JSONObject json = jsonParser.makeHttpRequest(Config_URL.url_delete_person, "GET", params);
                        if (json.getInt(TAG_SUCCESS) == 1) {
                            Log.d(TAG, "Done !");
                        } else {
                            Log.d(TAG, "Error !");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }
}