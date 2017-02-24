/*
 * Copyright (c) 2017  - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.malayeruniversity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import helper.FontHelper;
import helper.Helper;
import helper.SessionManager;
import helper.TypefaceSpan;
import slider.DescriptionAnimation;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class MainActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static MainActivity pointer;
    SliderLayout sliderShow;
    SliderLayout Slider1, Slider2, Slider3, Slider4, Slider5, Slider6, Slider7, Slider8;
    public Drawer result = null;
    private AccountHeader headerResult = null;
    private long back_pressed;
    private ProgressDialog pDialog;
    SessionManager session;
    private Boolean is_logged = false;
    static Typeface persianTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        persianTypeface = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        session = new SessionManager(getApplicationContext());
        pointer = this;
        pDialog = new ProgressDialog(this); // Progress dialog
        pDialog.setCancelable(true);
        sliderShow = (SliderLayout) findViewById(R.id.slider_last_news);
        Slider1 = (SliderLayout) findViewById(R.id.slider_basij_news);
        Slider2 = (SliderLayout) findViewById(R.id.slider_amoozesh_news);
        Slider3 = (SliderLayout) findViewById(R.id.slider_elmi_news);
        Slider4 = (SliderLayout) findViewById(R.id.slider_farhangi_news);
        Slider5 = (SliderLayout) findViewById(R.id.slider_food_news);
        Slider6 = (SliderLayout) findViewById(R.id.slider_khabgah_news);
        Slider7 = (SliderLayout) findViewById(R.id.slider_herasat_news);
        Slider8 = (SliderLayout) findViewById(R.id.slider_nahad_news);

        if (CheckConnection()) {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(FontHelper.getSpannedString(this, getResources().getString(R.string.app_name_fa)));
            setSupportActionBar(toolbar);
            if (session.isLoggedIn()) {
                is_logged = true;
                result = new DrawerBuilder()
                        .withActivity(this)
                        .withAccountHeader(headerResult)
                        .addDrawerItems(
                                new PrimaryDrawerItem().withName("خانه").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withSetSelected(true).withTypeface(persianTypeface),
                                new PrimaryDrawerItem().withName("حساب کاربری").withIcon(FontAwesome.Icon.faw_credit_card).withIdentifier(2).withTypeface(persianTypeface),
                                new PrimaryDrawerItem().withName("ارسال خبر").withIcon(FontAwesome.Icon.faw_edit).withIdentifier(3).withTypeface(persianTypeface)
                        )
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                if (drawerItem != null && drawerItem.getIdentifier() == 1) {
                                    result.closeDrawer();
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    // start main activity
                                    startActivity(i);
                                    finish();
                                    return true;
                                }
                                if (drawerItem != null && drawerItem.getIdentifier() == 2) {
                                    if (Helper.CheckInternet(MainActivity.this)) {
                                        Intent i = new Intent(getApplicationContext(), UserProfile.class);
                                        // start profile activity and NOT-FINISH main activity for return
                                        startActivity(i);
                                    } else
                                        result.closeDrawer(); // close drawer
                                    return true;
                                }
                                if (drawerItem != null && drawerItem.getIdentifier() == 3) {
                                    result.closeDrawer();
                                    Intent i = new Intent(getApplicationContext(), PostNews.class);
                                    startActivity(i);
                                    return true;
                                }
                                return false;
                            }
                        })
                        .withSelectedItem(1)
                        .withSavedInstance(savedInstanceState)
                        .withDrawerGravity(Gravity.END) // set drawer to end of screen ( rtl )
                        .build();                       // build drawer
            } else {
                is_logged = true;
                result = new DrawerBuilder()
                        .withActivity(this)
                        .withAccountHeader(headerResult)
                        .addDrawerItems(
                                new PrimaryDrawerItem().withName("خانه").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withSetSelected(true).withTypeface(persianTypeface),
                                new PrimaryDrawerItem().withName("ورود").withIcon(FontAwesome.Icon.faw_credit_card).withIdentifier(2).withTypeface(persianTypeface)
                                //new SectionDrawerItem().withName("جزئیات").withTypeface(persianTypeface),
                                //new SecondaryDrawerItem().withName("درباره ما").withIcon(FontAwesome.Icon.faw_users).withIdentifier(4).withTypeface(persianTypeface)
                        )
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                if (drawerItem != null && drawerItem.getIdentifier() == 1) {
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    // start main activity
                                    startActivity(i);
                                    finish();
                                }
                                if (drawerItem != null && drawerItem.getIdentifier() == 2) {
                                    if (Helper.CheckInternet(MainActivity.this)) {
                                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                        // start profile activity and NOT-FINISH main activity for return
                                        startActivity(i);
                                    } else
                                        result.closeDrawer(); // close drawer
                                }
                                if (drawerItem != null && drawerItem.getIdentifier() == 3) {
                                    Intent i = new Intent(getApplicationContext(), WebPage.class);
                                    i.putExtra(Config_TAG.TITLE, "درباره ما");
                                    i.putExtra(Config_TAG.ADDRESS, "about");
                                    startActivity(i);
                                    result.closeDrawer();
                                    return true;
                                }
                                if (drawerItem != null && drawerItem.getIdentifier() == 4) {
                                    Intent i = new Intent(getApplicationContext(), Contact.class);
                                    // start card activity and NOT-FINISH main activity for return
                                    startActivity(i);
                                    result.closeDrawer();
                                    return true;
                                }
                                if (drawerItem != null && drawerItem.getIdentifier() == 5) {
                                    Intent i = new Intent(getApplicationContext(), Comment.class);
                                    // start card activity and NOT-FINISH main activity for return
                                    startActivity(i);
                                    result.closeDrawer();
                                }
                                return false;
                            }
                        })
                        .withSelectedItem(1)
                        .withSavedInstance(savedInstanceState)
                        .withDrawerGravity(Gravity.END) // set drawer to end of screen ( rtl )
                        .build();                       // build drawer
            }

            FetchAllNews();

            sliderShow.setPresetTransformer(SliderLayout.Transformer.Fade);
            sliderShow.setDuration(2500);
            sliderShow.setCustomAnimation(new DescriptionAnimation());
            sliderShow.addOnPageChangeListener(this);

            Slider1.setPresetTransformer(SliderLayout.Transformer.Fade);
            Slider1.setDuration(2500);
            Slider1.setCustomAnimation(new DescriptionAnimation());
            Slider1.addOnPageChangeListener(this);

            Slider2.setPresetTransformer(SliderLayout.Transformer.Fade);
            Slider2.setDuration(2500);
            Slider2.setCustomAnimation(new DescriptionAnimation());
            Slider2.addOnPageChangeListener(this);

            Slider3.setPresetTransformer(SliderLayout.Transformer.Fade);
            Slider3.setDuration(2500);
            Slider3.setCustomAnimation(new DescriptionAnimation());
            Slider3.addOnPageChangeListener(this);

            Slider4.setPresetTransformer(SliderLayout.Transformer.Fade);
            Slider4.setDuration(2500);
            Slider4.setCustomAnimation(new DescriptionAnimation());
            Slider4.addOnPageChangeListener(this);

            Slider5.setPresetTransformer(SliderLayout.Transformer.Fade);
            Slider5.setDuration(2500);
            Slider5.setCustomAnimation(new DescriptionAnimation());
            Slider5.addOnPageChangeListener(this);

            Slider6.setPresetTransformer(SliderLayout.Transformer.Fade);
            Slider6.setDuration(2500);
            Slider6.setCustomAnimation(new DescriptionAnimation());
            Slider6.addOnPageChangeListener(this);

            Slider7.setPresetTransformer(SliderLayout.Transformer.Fade);
            Slider7.setDuration(2500);
            Slider7.setCustomAnimation(new DescriptionAnimation());
            Slider7.addOnPageChangeListener(this);

            Slider8.setPresetTransformer(SliderLayout.Transformer.Fade);
            Slider8.setDuration(2500);
            Slider8.setCustomAnimation(new DescriptionAnimation());
            Slider8.addOnPageChangeListener(this);

        }
    }

    private void set_sliders(String id, String cid, String uid, String author, String title, String content, String url, String created_at, String updated_at) {
        Log.d(TAG, "uname : " + author);
        TextSliderView textSliderView = new TextSliderView(this);
        if (url.equals("null"))
            textSliderView
                    .description(title)
                    .image(R.drawable.nnull)
                    .setOnSliderClickListener(this)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop);
        else
            textSliderView
                    .description(title)
                    .image(ConvertUrl(url))
                    .setOnSliderClickListener(this)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop);
        textSliderView.bundle(new Bundle());
        textSliderView.getBundle().putString("id", id);
        textSliderView.getBundle().putString("uid", uid);
        textSliderView.getBundle().putString("author", author);
        textSliderView.getBundle().putString("cid", cid);
        textSliderView.getBundle().putString("title", title);
        textSliderView.getBundle().putString("content", content);
        textSliderView.getBundle().putString("url", url);
        textSliderView.getBundle().putString("created_at", created_at);
        sliderShow.addSlider(textSliderView);
        switch (cid) {
            case "1":
                Slider4.addSlider(textSliderView);
                break;
            case "2":
                Slider1.addSlider(textSliderView);
                break;
            case "3":
                Slider2.addSlider(textSliderView);
                break;
            case "4":
                Slider3.addSlider(textSliderView);
                break;
            case "5":
                Slider5.addSlider(textSliderView);
                break;
            case "6":
                Slider6.addSlider(textSliderView);
                break;
            case "7":
                Slider7.addSlider(textSliderView);
                break;
            case "8":
                Slider8.addSlider(textSliderView);
                break;
        }
    }

    private void FetchAllNews() {
        // Tag used to cancel the request
        String tag_string_req = "req_fetch";
        pDialog.setMessage(FontHelper.getSpannedString(this, "در حال به روزرسانی ..."));
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Fetch Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Restaurants List fetched from server
                        JSONArray news = jObj.getJSONArray("news");
                        for (int i = 0; i < news.length(); i++) {
                            JSONObject _new = news.getJSONObject(i);
                            String title = _new.getString("title");
                            String url = _new.getString("image");
                            String content = _new.getString("content");
                            String created_at = _new.getString("created_at");
                            String updated_at = _new.getString("updated_at");
                            int cid = _new.getInt("cid");
                            int id = _new.getInt("id");
                            String uid = _new.getString("uid");
                            String author = _new.getString("author");
                            set_sliders(String.valueOf(id), String.valueOf(cid), uid, author, title, content, url, created_at, updated_at);
                        }
                    } else {
                        // Error occurred
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
                Log.e(TAG, "Fetch Error: " + error.getMessage());
                if (error.getMessage() != null) {
                    MakeToast(error.getMessage());
                } else
                    MakeToast("خطایی رخ داده است ، اتصال به اینترنت را بررسی کنید");
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "all_news");
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

    @Override
    public void onBackPressed() {
        // check if back key press twice in 2 second
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            // kill app completely
            android.os.Process.killProcess(android.os.Process.myPid());
        } else
            MakeToast("برای خروج دوباره کلیک کنید");
        // set current time for counter
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        Slider1.stopAutoCycle();
        Slider2.stopAutoCycle();
        Slider3.stopAutoCycle();
        Slider4.stopAutoCycle();
        Slider5.stopAutoCycle();
        Slider6.stopAutoCycle();
        super.onStop();
    }

    @Override
    protected void onPause() {
        sliderShow.stopAutoCycle();
        Slider1.stopAutoCycle();
        Slider2.stopAutoCycle();
        Slider3.stopAutoCycle();
        Slider4.stopAutoCycle();
        Slider5.stopAutoCycle();
        Slider6.stopAutoCycle();
        super.onPause();
    }

    @Override
    protected void onResume() {
        sliderShow.startAutoCycle();
        Slider1.startAutoCycle();
        Slider2.startAutoCycle();
        Slider3.startAutoCycle();
        Slider4.startAutoCycle();
        Slider5.startAutoCycle();
        Slider6.startAutoCycle();
        super.onResume();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
        intent.putExtra("id", String.valueOf(slider.getBundle().get("id")));
        intent.putExtra("uid", String.valueOf(slider.getBundle().get("uid")));
        intent.putExtra("author", String.valueOf(slider.getBundle().get("author")));
        intent.putExtra("cid", String.valueOf(slider.getBundle().get("cid")));
        intent.putExtra("title", String.valueOf(slider.getBundle().get("title")));
        intent.putExtra("content", String.valueOf(slider.getBundle().get("content")));
        intent.putExtra("url", String.valueOf(slider.getBundle().get("url")));
        intent.putExtra("created_at", String.valueOf(slider.getBundle().get("created_at")));
        intent.putExtra("updated_At", String.valueOf(slider.getBundle().get("updated_At")));
        startActivity(intent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.drawershow) {
            result.openDrawer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }

    private boolean CheckConnection() {
        boolean check;
        if (!Helper.CheckInternet(this)) {
            new MaterialStyledDialog.Builder(this)
                    .setTitle(FontHelper.getSpannedString(this, "خطا"))
                    .setDescription(FontHelper.getSpannedString(this, "اتصال اینترنت خود را بررسی کرده و مجدد وارد شوید"))
                    .setStyle(Style.HEADER_WITH_TITLE)
                    .withDarkerOverlay(true)
                    .withDialogAnimation(true)
                    .setCancelable(false)
                    .setPositiveText(FontHelper.getSpannedString(this, "باشه"))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            CheckConnection();
                        }
                    })
                    .show();
            check = false;
        } else
            check = true;
        return check;
    }

    private String ConvertUrl(String url) {
        return "http://mu.zimia.ir/images/" + url;
    }
}