/*
 * Copyright (c) 2017  - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.malayeruniversity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
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
import helper.SessionManager;
import helper.TypefaceSpan;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class NewsActivity extends Activity {
    private static final String TAG = NewsActivity.class.getSimpleName();
    ImageView divider;
    TextView title, content, date_author;
    Button delete;
    SessionManager session;
    ProgressDialog pDialog;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);                                      // new dialog
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());

        divider = (ImageView) findViewById(R.id.divider);
        divider.setImageResource(R.drawable.divider);
        title = (TextView) findViewById(R.id.news_title);
        content = (TextView) findViewById(R.id.news_text);
        delete = (Button) findViewById(R.id.delete);
        date_author = (TextView) findViewById(R.id.date_author);

        Intent i = getIntent();
        final String news_id = i.getStringExtra("id");
        final String news_uid = i.getStringExtra("uid");
        final String news_cid = i.getStringExtra("cid");
        String news_author = i.getStringExtra("author");
        String news_title = i.getStringExtra("title");
        String news_content = i.getStringExtra("content");
        String news_url = i.getStringExtra("url");
        String news_created_at = i.getStringExtra("created_at");

        title.setText(news_title);
        content.setText(news_content);
        date_author.setText("در " + news_created_at + " - توسط : " + news_author);
        delete.setVisibility(View.INVISIBLE);

        if (session.isLoggedIn() && db.getUserDetails().get("username").equals("admin"))
            delete.setVisibility(View.VISIBLE);


        if (session.isLoggedIn() && db.getUserDetails().get("uid").equals(news_uid))
            delete.setVisibility(View.VISIBLE);

        delete.setOnClickListener(new View.OnClickListener() {            // register button's event
            public void onClick(View view) {
                if (Helper.CheckInternet(NewsActivity.this))
                    new MaterialStyledDialog.Builder(NewsActivity.this)
                            .setTitle(FontHelper.getSpannedString(NewsActivity.this, "حذف خبر"))
                            .setDescription(FontHelper.getSpannedString(NewsActivity.this, "آیا مطمئن هستید ؟"))
                            .setStyle(Style.HEADER_WITH_TITLE)
                            .withDarkerOverlay(true)
                            .withDialogAnimation(true)
                            .setCancelable(true)
                            .setPositiveText(FontHelper.getSpannedString(NewsActivity.this, "بله"))
                            .setNegativeText(FontHelper.getSpannedString(NewsActivity.this, "خیر"))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Log.d(TAG, "News Delete Requested");
                                    DeleteNews(news_id, news_uid, news_cid);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Log.d(TAG, "News Delete Aborted");
                                }
                            })
                            .show();
            }
        });
    }

    private void DeleteNews(final String news_id, final String news_uid, final String news_cid) {
        String tag_string_req = "req_delete";
        pDialog.setMessage("در حال حذف ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Delete Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        MainActivity.pointer.finish();
                        startActivity(i);
                        finish();
                    } else {
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        MakeToast(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Delete Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    MakeToast(error.getMessage());
                } else
                    MakeToast("خطایی رخ داده است ، اتصال به اینترنت را بررسی کنید");
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "del_news");
                params.put("news_id", news_id);
                params.put("news_uid", news_uid);
                params.put("news_cid", news_cid);
                return params;
            }
        };
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

    private void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }
}