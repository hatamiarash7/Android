package ir.hatamiarash.zimia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helper.SQLiteHandler;
import helper.SessionManager;
import volley.AppController;
import volley.Config_URL;

public class Register extends Activity {
    private static final String TAG = Register.class.getSimpleName();
    Button btnRegister;
    Button btnLinkToLogin;
    SessionManager session;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPassword2;
    private EditText inputAddress;
    private EditText inputPhone;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    ProgressBar pb;
    static int i = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPassword2 = (EditText) findViewById(R.id.password2);
        inputAddress = (EditText) findViewById(R.id.address);
        inputPhone = (EditText) findViewById(R.id.phone);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        inputPassword.setError("Enter your password..!");
        inputPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (inputPassword.getText().toString().length() == 0) {
                    inputPassword.setError("کلمه عبور حداقل 6 حرف");
                } else {
                    password_caculation();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
        });
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // Session manager
        session = new SessionManager(getApplicationContext());
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent i = new Intent(getApplicationContext(), Profile.class);
            startActivity(i);
            finish();
        }
        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String password2 = inputPassword2.getText().toString();
                String address = inputAddress.getText().toString();
                String phone = inputPhone.getText().toString();
                if (CheckInternet())
                    if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !password2.isEmpty() && !address.isEmpty() && !phone.isEmpty())
                        if (password.length() < 8)
                            if (password.equals(password2))
                                registerUser(name, email, password, address, phone);
                            else
                                Toast.makeText(getApplicationContext(), "کلمه عبور تطابق ندارد", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(), "کلمه عبور کوتاه است", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(), "تمامی کادر ها را پر نمایید", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "اتصال به اینترنت را بررسی نمایید", Toast.LENGTH_LONG).show();
            }
        });
        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void registerUser(final String name, final String email, final String password, final String address, final String phone) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        pDialog.setMessage("در حال ثبت ...");
        showDialog();
        StringRequest strReq = new StringRequest(Method.POST, Config_URL.url_register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String address = user.getString("address");
                        String phone = user.getString("phone");
                        String created_at = user.getString("created_at");
                        // Inserting row in users table
                        db.addUser(name, email, address, phone, uid, created_at);
                        // Launch login activity
                        Toast.makeText(getApplicationContext(), "ثبت نام انجام شد", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("email", email);
                params.put("address", address);
                params.put("phone", phone);
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

    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    protected void password_caculation() {
        // TODO Auto-generated method stub
        String temp = inputPassword.getText().toString();
        System.out.println(i + " current password is : " + temp);
        i = i + 1;

        int length = 0, uppercase = 0, lowercase = 0, digits = 0, symbols = 0, bonus = 0, requirements = 0;

        int lettersonly = 0, numbersonly = 0, cuc = 0, clc = 0;

        length = temp.length();
        for (int i = 0; i < temp.length(); i++) {
            if (Character.isUpperCase(temp.charAt(i)))
                uppercase++;
            else if (Character.isLowerCase(temp.charAt(i)))
                lowercase++;
            else if (Character.isDigit(temp.charAt(i)))
                digits++;

            symbols = length - uppercase - lowercase - digits;

        }

        for (int j = 1; j < temp.length() - 1; j++) {

            if (Character.isDigit(temp.charAt(j)))
                bonus++;

        }

        for (int k = 0; k < temp.length(); k++) {

            if (Character.isUpperCase(temp.charAt(k))) {
                k++;

                if (k < temp.length()) {

                    if (Character.isUpperCase(temp.charAt(k))) {

                        cuc++;
                        k--;

                    }

                }

            }

        }

        for (int l = 0; l < temp.length(); l++) {

            if (Character.isLowerCase(temp.charAt(l))) {
                l++;

                if (l < temp.length()) {

                    if (Character.isLowerCase(temp.charAt(l))) {

                        clc++;
                        l--;

                    }

                }

            }

        }

        System.out.println("length" + length);
        System.out.println("uppercase" + uppercase);
        System.out.println("lowercase" + lowercase);
        System.out.println("digits" + digits);
        System.out.println("symbols" + symbols);
        System.out.println("bonus" + bonus);
        System.out.println("cuc" + cuc);
        System.out.println("clc" + clc);

        if (length > 7) {
            requirements++;
        }

        if (uppercase > 0) {
            requirements++;
        }

        if (lowercase > 0) {
            requirements++;
        }

        if (digits > 0) {
            requirements++;
        }

        if (symbols > 0) {
            requirements++;
        }

        if (bonus > 0) {
            requirements++;
        }

        if (digits == 0 && symbols == 0) {
            lettersonly = 1;
        }

        if (lowercase == 0 && uppercase == 0 && symbols == 0) {
            numbersonly = 1;
        }

        int Total = (length * 4) + ((length - uppercase) * 2)
                + ((length - lowercase) * 2) + (digits * 4) + (symbols * 6)
                + (bonus * 2) + (requirements * 2) - (lettersonly * length * 2)
                - (numbersonly * length * 3) - (cuc * 2) - (clc * 2);

        System.out.println("Total" + Total);

        if (Total < 30) {
            pb.setProgress(Total - 15);
        } else if (Total >= 40 && Total < 50) {
            pb.setProgress(Total - 20);
        } else if (Total >= 56 && Total < 70) {
            pb.setProgress(Total - 25);
        } else if (Total >= 76) {
            pb.setProgress(Total - 30);
        } else {
            pb.setProgress(Total - 20);
        }

    }
}