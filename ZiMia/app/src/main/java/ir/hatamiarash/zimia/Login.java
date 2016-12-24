package ir.hatamiarash.zimia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import helper.SQLiteHandler;
import helper.SessionManager;
import volley.AppController;
import volley.Config_URL;

public class Login extends Activity {
    private static final String TAG = Login.class.getSimpleName();
    private static final String url_person_detials = "http://zimia.ir/users/include/Set_User_Detail.php";
    Button btnLogin;
    Button btnLinkToRegister;
    JSONParser jsonParser = new JSONParser();
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
            startActivity(i);
            finish();
        }
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                // Check for empty data in the form
                if (CheckInternet())
                    if (email.trim().length() > 0 && password.trim().length() > 0) {
                        pDialog.setMessage("در حال ورود ...");
                        showDialog();
                        checkLogin(email, password);
                        new SetPersonDetails().execute();
                        hideDialog();
                        String msg = "سلام " + name;
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getApplicationContext(), "مشخصات را وارد نمایید", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "اتصال به اینترنت را بررسی نمایید", Toast.LENGTH_LONG).show();
            }
        });
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Method.POST, Config_URL.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        session.setLogin(true);
                        // Launch main activity
                        Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
                        MainScreenActivity.pointer.finish();
                        startActivity(i);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);
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

    class SetPersonDetails extends AsyncTask<String, String, String> {
        protected String doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        String e = inputEmail.getText().toString();
                        params.add(new BasicNameValuePair("email", e));
                        JSONObject json = jsonParser.makeHttpRequest(url_person_detials, "GET", params);
                        Log.d("GET Person Details", json.toString());
                        if (json.getInt("success") == 1) {
                            Log.d(TAG, "Done !");
                            JSONArray persons = json.getJSONArray("persons");
                            JSONObject person = persons.getJSONObject(0);
                            String uid = person.getString("id");
                            name = person.getString("name");
                            String email = person.getString("email");
                            String address = person.getString("address");
                            String phone = person.getString("phone");
                            String created_at = person.getString("created_at");
                            // Inserting row in users table
                            db.addUser(name, email, address, phone, uid, created_at);
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

    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }
}