/*
 * Copyright (c) 2017 - Arash Hatami - All Rights Reserved
 */

package helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ir.hatamiarash.malayeruniversity.EditPost;
import ir.hatamiarash.malayeruniversity.Manager;
import ir.hatamiarash.malayeruniversity.R;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class NewsAdapter extends BaseAdapter implements ListAdapter {
    private static final String TAG = NewsAdapter.class.getSimpleName();
    private ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private Context context;
    private ProgressDialog pDialog;

    public NewsAdapter(ArrayList<HashMap<String, String>> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.items, null);
        }
        pDialog = new ProgressDialog(context);                                      // new dialog
        pDialog.setCancelable(false);

        final TextView title = (TextView) view.findViewById(R.id.title);
        final TextView nid = (TextView) view.findViewById(R.id.nid);
        final TextView cid = (TextView) view.findViewById(R.id.cid);
        final TextView uid = (TextView) view.findViewById(R.id.uid);
        final View edit = view.findViewById(R.id.edit);
        final View delete = view.findViewById(R.id.delete);

        title.setText(list.get(position).get(Config_TAG.TITLE));
        nid.setText(list.get(position).get(Config_TAG.ID));
        cid.setText(list.get(position).get(Config_TAG.CID));
        uid.setText(list.get(position).get(Config_TAG.UID));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EditPost.class);
                i.putExtra("nid", nid.getText().toString());
                i.putExtra("uid", uid.getText().toString());
                i.putExtra("cid", cid.getText().toString());
                context.startActivity(i);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.CheckInternet(context))
                    new MaterialStyledDialog.Builder(context)
                            .setTitle(FontHelper.getSpannedString(context, "حذف خبر"))
                            .setDescription(FontHelper.getSpannedString(context, "آیا مطمئن هستید ؟"))
                            .setStyle(Style.HEADER_WITH_TITLE)
                            .withDarkerOverlay(true)
                            .withDialogAnimation(true)
                            .setCancelable(true)
                            .setPositiveText(FontHelper.getSpannedString(context, "بله"))
                            .setNegativeText(FontHelper.getSpannedString(context, "خیر"))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Log.d(TAG, "News Delete Requested");
                                    DeleteNews(nid.getText().toString(), uid.getText().toString(), cid.getText().toString());
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
        return view;
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
                        SQLiteHandler db = new SQLiteHandler(context);
                        Intent i = new Intent(context, Manager.class);
                        Manager.pointer.finish();
                        i.putExtra(Config_TAG.USERNAME, db.getUserDetails().get(Config_TAG.USERNAME));
                        i.putExtra(Config_TAG.TYPE, "news");
                        context.startActivity(i);
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

    private void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(context, efr, Toast.LENGTH_SHORT).show();
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