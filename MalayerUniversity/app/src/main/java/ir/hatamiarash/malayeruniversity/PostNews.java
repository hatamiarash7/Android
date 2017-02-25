/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package ir.hatamiarash.malayeruniversity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import org.json.JSONException;
import org.json.JSONObject;

import helper.FontHelper;
import helper.Helper;
import helper.SQLiteHandler;
import helper.SessionManager;
import helper.TypefaceSpan;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class PostNews extends AppCompatActivity {
    private static final String TAG = PostNews.class.getSimpleName();
    EditText post_subject, post_body;
    Button send, pic;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private int PICK_IMAGE_REQUEST = 1;
    ImageView image;
    String filePath;

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
        pic = (Button) findViewById(R.id.add_pic);
        image = (ImageView) findViewById(R.id.image);

        post_subject.setError(FontHelper.getSpannedString(this, "حداکثر 100 کارکتر"));

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Helper.CheckInternet(PostNews.this))
                    if (session.isLoggedIn())
                        if (post_subject.length() <= 100)
                            if (post_subject.length() > 0 && post_body.length() > 0)
                                if (filePath != null)
                                    SendNews(filePath, post_subject.getText().toString(), post_body.getText().toString());
                                else {
                                    SendWithoutImage();
                                }
                            else
                                Helper.MakeToast(PostNews.this, "عنوان و متن خبر را تایپ کنید", Config_TAG.ERROR);
                        else
                            Helper.MakeToast(PostNews.this, "عنوان خبر طولانی است", Config_TAG.ERROR);
                    else {
                        Helper.MakeToast(PostNews.this, "شما وارد نشده اید", Config_TAG.ERROR);
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ActivityCompat.requestPermissions(PostNews.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    private void SendNews(final String imagePath, final String title, final String content) {
        final String uid = db.getUserDetails().get("uid");
        final String name = db.getUserDetails().get("name");
        final String cid = String.valueOf(db.GetCID(db.getUserDetails().get("type")));
        pDialog.setMessage("در حال ارسال ...");
        showDialog();
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
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
                if (error.getMessage() != null) {
                    MakeToast(error.getMessage());
                } else
                    MakeToast("خطایی رخ داده است ، اتصال به اینترنت را بررسی کنید");
                hideDialog();
            }
        });
        smr.addFile("image", imagePath);
        smr.addStringParam(Config_TAG.TAG, "post_news");
        smr.addStringParam("name", name);
        smr.addStringParam("title", title);
        smr.addStringParam("content", content);
        smr.addStringParam("uid", uid);
        smr.addStringParam("cid", cid);
        AppController.getInstance().addToRequestQueue(smr);
    }

    private void SendNewsWithoutImage(final String title, final String content) {
        final String uid = db.getUserDetails().get("uid");
        final String name = db.getUserDetails().get("name");
        final String cid = String.valueOf(db.GetCID(db.getUserDetails().get("type")));
        Log.d(TAG, uid + " " + cid);
        pDialog.setMessage("در حال ارسال ...");
        showDialog();
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
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
                if (error.getMessage() != null) {
                    MakeToast(error.getMessage());
                } else
                    MakeToast("خطایی رخ داده است ، اتصال به اینترنت را بررسی کنید");
                hideDialog();
            }
        });
        smr.addStringParam(Config_TAG.TAG, "post_news2");
        smr.addStringParam("name", name);
        smr.addStringParam("title", title);
        smr.addStringParam("content", content);
        smr.addStringParam("uid", uid);
        smr.addStringParam("cid", cid);
        AppController.getInstance().addToRequestQueue(smr);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri picUri = data.getData();
            filePath = getPath(picUri);
            Log.d("picUri", picUri.toString());
            Log.d("filePath", filePath);
            image.setImageURI(picUri);
        }
    }

    private String getPath(Uri contentUri) {
        String[] project = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, project, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }

    private void SendWithoutImage() {
        new MaterialStyledDialog.Builder(PostNews.this)
                .setTitle(FontHelper.getSpannedString(PostNews.this, "ارسال خبر"))
                .setDescription(FontHelper.getSpannedString(PostNews.this, "خبر را بدون تصویر ارسال می نمایید ؟"))
                .setStyle(Style.HEADER_WITH_TITLE)
                .setHeaderColor(R.color.Theme_Green)
                .withDarkerOverlay(true)
                .withDialogAnimation(true)
                .setCancelable(true)
                .setPositiveText(FontHelper.getSpannedString(PostNews.this, "بله"))
                .setNegativeText(FontHelper.getSpannedString(PostNews.this, "خیر"))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d(TAG, "News Post Requested");
                        SendNewsWithoutImage(post_subject.getText().toString(), post_body.getText().toString());
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d(TAG, "News Post Aborted");
                    }
                })
                .show();
    }
}