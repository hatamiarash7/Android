/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package ir.hatamiarash.malayeruniversity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helper.FontHelper;
import helper.Helper;
import helper.SQLiteHandler;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class Register extends AppCompatActivity {
    private static final String TAG = Register.class.getSimpleName();
    String[] items = new String[]{"بسیج دانشجویی", "فرهنگی", "علمی پژوهشی", "خوابگاه ها", "تغذیه", "آموزشی", "حراست", "نهاد رهبری"};
    Button btnRegister;
    private EditText inputFullName, inputUsername, inputPassword, inputPassword2, inputJob, inputEmail;
    private ProgressDialog progressDialog;
    SQLiteHandler db;
    private Helper CheckEmail;
    private Spinner inputType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPassword2 = (EditText) findViewById(R.id.password2);
        inputJob = (EditText) findViewById(R.id.job);
        inputEmail = (EditText) findViewById(R.id.email);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        inputType = (Spinner) findViewById(R.id.type);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        inputType.setAdapter(adapter);

        inputPassword.setError("حداقل 8 حرف");
        inputEmail.setError("اختیاری");

        inputUsername.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        inputJob.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                String password2 = inputPassword2.getText().toString();
                String job = inputJob.getText().toString();
                String email = inputEmail.getText().toString();
                String type = inputType.getSelectedItem().toString();
                CheckEmail = new Helper("email", email);
                if (Helper.CheckInternet(Register.this))
                    if (!name.isEmpty() && !username.isEmpty() && !password.isEmpty() && !password2.isEmpty() && !job.isEmpty())
                        if (CheckEmail.isValidEmail() || email.equals(""))
                            if (password.length() >= 8)
                                if (password.equals(password2))
                                    registerUser(name, username, password, job, type, email);
                                else
                                    Helper.MakeToast(Register.this, "کلمه عبور تطابق ندارد", Config_TAG.WARNING);
                            else
                                Helper.MakeToast(Register.this, "کلمه عبور کوتاه است", Config_TAG.WARNING);
                        else
                            Helper.MakeToast(Register.this, "ایمیل را بررسی نمایید", Config_TAG.WARNING);
                    else
                        Helper.MakeToast(Register.this, "تمامی کادر ها را پر نمایید", Config_TAG.WARNING);
            }
        });
    }

    private void registerUser(final String name, final String username, final String password, final String job, final String type, final String email) {
        // Tag used to cancel the request
        String string_req = "req_register";
        progressDialog.setMessage(FontHelper.getSpannedString(this, "در حال ثبت ..."));
        showDialog();
        StringRequest strReq = new StringRequest(Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        MakeDialog("ثبت نام انجام شد", "کاربر با نام " + name + " ثبت شد.");
                    } else {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        Helper.MakeToast(Register.this, errorMsg, Config_TAG.ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    Helper.MakeToast(Register.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(Register.this, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "user_register");
                params.put(Config_TAG.NAME, name);
                params.put(Config_TAG.USERNAME, username);
                if (email.equals(""))
                    params.put(Config_TAG.EMAIL, "NULL");
                else
                    params.put(Config_TAG.EMAIL, email);
                params.put(Config_TAG.JOB, job);
                params.put(Config_TAG.PASSWORD, password);
                params.put(Config_TAG.TYPE, type);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void MakeDialog(String Title, String Message) {
        new MaterialStyledDialog.Builder(this)
                .setTitle(FontHelper.getSpannedString(this, Title))
                .setDescription(FontHelper.getSpannedString(this, Message))
                .setStyle(Style.HEADER_WITH_TITLE)
                .withDarkerOverlay(true)
                .withDialogAnimation(true)
                .setCancelable(true)
                .setPositiveText("باشه")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .show();
    }
}