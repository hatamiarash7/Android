package ir.hatamiarash.hambazidemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.kekstudio.dachshundtablayout.indicators.DachshundIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import helper.Helper;
import helper.LoadImageTask;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class GameActivity extends AppCompatActivity implements LoadImageTask.Listener {
    private static final String TAG = GameActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    TextView game_name;
    TextView game_description;
    TextView game_point;
    TextView game_duration;
    TextView game_levels;
    ImageView game_logo;
    private static final String TABS[] = {"اطلاعات", "نظرات"};
    ViewPager viewPager;
    DachshundTabLayout tabLayout;
    String uid;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        game_name = (TextView) findViewById(R.id.name);
        game_description = (TextView) findViewById(R.id.description);
        game_point = (TextView) findViewById(R.id.point);
        game_levels = (TextView) findViewById(R.id.levels);
        game_duration = (TextView) findViewById(R.id.duration);
        game_logo = (ImageView) findViewById(R.id.logo);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (DachshundTabLayout) findViewById(R.id.tab_layout);
        layout = (LinearLayout) findViewById(R.id.game_layout);

        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setAnimatedIndicator(new DachshundIndicator(tabLayout));
        layout.setVisibility(View.INVISIBLE);

        Intent i = getIntent();
        uid = i.getStringExtra("uid");
        Log.d(TAG, "Game Unique ID : " + uid);
        FetchGameData(uid);
    }

    private void FetchGameData(final String uid) {
        // Tag used to cancel the request
        String string_req = "req_fetch";
        progressDialog.setMessage("در حال به روزرسانی ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Game Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Products List fetched from server
                        JSONObject game = jObj.getJSONObject("game");
                        String name = game.getString("name");
                        String description = game.getString("description");
                        Double point = game.getDouble("point");
                        String image = game.getString("image");
                        String levels = game.getString("level_count");
                        String duration = game.getString("play_duration");

                        game_name.setText(name);
                        game_description.setText(description);
                        game_duration.setText("زمان : " + duration);
                        game_levels.setText("تعداد مراحل : " + levels);
                        game_point.setText("امتیاز : " + String.valueOf(point));

                        layout.setVisibility(View.VISIBLE);

                        if (!image.equals("null"))
                            new LoadImageTask(GameActivity.this).execute(Config_URL.image_URL + "game_" + image);
                        else
                            game_logo.setImageDrawable(GameActivity.this.getResources().getDrawable(R.drawable.nnull));
                    } else
                        Helper.MakeToast(GameActivity.this, jObj.getString(Config_TAG.ERROR_MSG), Config_TAG.ERROR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e(TAG, "Fetch Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    Helper.MakeToast(GameActivity.this, error.getMessage(), Config_TAG.ERROR);
                } else
                    Helper.MakeToast(GameActivity.this, "خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "game_details");
                params.put(Config_TAG.UID, uid);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FragmentInfo.newInstance(uid, GameActivity.this);
                case 1:
                    return FragmentComment.newInstance(TABS[position], GameActivity.this);
                default:
                    return FragmentInfo.newInstance(TABS[position], GameActivity.this);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TABS[position];
        }
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onImageLoaded(Bitmap bitmap) {
        game_logo.setImageBitmap(bitmap);
    }

    @Override
    public void onError() {
        Helper.MakeToast(this, "مشکل در بارگزاری تصویر", Config_TAG.ERROR);
    }
}