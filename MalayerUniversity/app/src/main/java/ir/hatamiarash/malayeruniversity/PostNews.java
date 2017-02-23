package ir.hatamiarash.malayeruniversity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import helper.FontHelper;
import helper.Helper;
import helper.SQLiteHandler;
import helper.SessionManager;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class PostNews extends AppCompatActivity {
    private static final String TAG = PostNews.class.getSimpleName();
    EditText post_subject, post_body;
    Button send;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_post);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        post_body = (EditText) findViewById(R.id.body);
        post_subject = (EditText) findViewById(R.id.subject);
        send = (Button) findViewById(R.id.send);

        post_subject.setError(FontHelper.getSpannedString(this, "100 کارکتر"));

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Helper.CheckInternet(PostNews.this))
                    if (session.isLoggedIn())
                        if (post_subject.length() <= 100)
                            if (post_subject.length() > 0 && post_body.length() > 0)
                                SendNews(post_subject.getText().toString(), post_body.getText().toString());
                            else
                                Helper.MakeToast(PostNews.this, "عنوان و متن خبر را تایپ کنید", Config_TAG.ERROR);
                        else
                            Helper.MakeToast(PostNews.this, "عنوان طولانی است", Config_TAG.ERROR);
                    else {
                        Helper.MakeToast(PostNews.this, "شما وارد نشده اید", Config_TAG.ERROR);
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
            }
        });
    }

    private void SendNews(final String title, final String content) {
        String tag_string_req = "req_post";
        final String uid = db.getUserDetails().get("uid");
        final String cid = String.valueOf(db.GetCID(db.getUserDetails().get("type")));
        pDialog.setMessage("در حال ارسال ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Post Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        MainActivity.pointer.finish();
                        startActivity(i);
                        finish();
                    } else
                        Helper.MakeToast(PostNews.this, jObj.getString(Config_TAG.ERROR_MSG), Config_TAG.ERROR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Post Error: " + error.getMessage());
                if (error.getMessage() != null)
                    Helper.MakeToast(PostNews.this, error.getMessage(), Config_TAG.ERROR);
                else
                    Helper.MakeToast(PostNews.this, "خطایی رخ داده است ، اتصال به اینترنت را بررسی کنید", Config_TAG.ERROR);
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "post_news");
                params.put("title", title);
                params.put("content", content);
                params.put("uid", uid);
                params.put("cid", cid);
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
}