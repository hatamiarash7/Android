/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.hambazidemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.readystatesoftware.viewbadger.BadgeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import helper.FontHelper;
import helper.FormatHelper;
import helper.SQLiteHandlerItem;
import helper.SessionManager;
import ir.hatamiarash.MyToast.CustomToast;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class All_Sellers extends AppCompatActivity {
    private static final String TAG = All_Sellers.class.getSimpleName();
    public static All_Sellers pointer;
    //private AlertDialog progressDialog;
    private SweetAlertDialog progressDialog;
    private ArrayList<HashMap<String, String>> RestaurantList;
    private ListView listView;
    static Typeface persianTypeface;           // persian font typeface
    public Drawer result = null;
    SessionManager session;                    // session for check user logged
    private AccountHeader headerResult = null; // Header for drawer
    private Vibrator vibrator;
    static public Menu menu;
    public static SQLiteHandlerItem db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_sellers);
        pointer = this;
        Intent i = getIntent();
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandlerItem(getApplicationContext());
        RestaurantList = new ArrayList<>();
        //progressDialog = new SpotsDialog(this, R.style.CustomDialog);
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setCancelable(false);
        progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.accent));
        listView = (ListView) findViewById(R.id.seller_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = ((TextView) view.findViewById(R.id.vid)).getText().toString();
                String name = ((TextView) view.findViewById(R.id.vname)).getText().toString();
                Intent i = new Intent(getApplicationContext(), WebPage.class);
                i.putExtra(Config_TAG.ADDRESS, "http://hambazi.tv/?video_type=" +name);
                startActivity(i);
            }
        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(FontHelper.getSpannedString(this, "همبازی"));
        setSupportActionBar(toolbar);
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("خانه").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withSetSelected(true).withTypeface(persianTypeface),
                        new PrimaryDrawerItem().withName("حساب کاربری").withIcon(FontAwesome.Icon.faw_credit_card).withIdentifier(2).withTypeface(persianTypeface),
                        new PrimaryDrawerItem().withName("سبد خرید").withIcon(FontAwesome.Icon.faw_shopping_cart).withIdentifier(5).withTypeface(persianTypeface),
                        new SectionDrawerItem().withName("جزئیات").withTypeface(persianTypeface),
                        new SecondaryDrawerItem().withName("درباره ما").withIcon(FontAwesome.Icon.faw_users).withIdentifier(3).withTypeface(persianTypeface),
                        new SecondaryDrawerItem().withName("تماس با ما").withIcon(FontAwesome.Icon.faw_phone).withIdentifier(4).withTypeface(persianTypeface),
                        new SecondaryDrawerItem().withName("ارسال نظر").withIcon(FontAwesome.Icon.faw_comment).withIdentifier(6).withTypeface(persianTypeface)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem.getIdentifier() == 1) {
                            Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
                            // start main activity
                            startActivity(i);
                            finish();
                        }
                        if (drawerItem != null && drawerItem.getIdentifier() == 2) {
                            if (CheckInternet()) {
                                Intent i = new Intent(getApplicationContext(), UserProfile.class);
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
                            return true;
                        }
                        if (drawerItem != null && drawerItem.getIdentifier() == 5) {

                        }
                        if (drawerItem != null && drawerItem.getIdentifier() == 6) {
                            Intent i = new Intent(getApplicationContext(), Comment.class);
                            // start card activity and NOT-FINISH main activity for return
                            startActivity(i);
                            finish();
                        }
                        return false;
                    }
                })
                .withSelectedItem(1)
                .withSavedInstance(savedInstanceState)
                .withDrawerGravity(Gravity.END) // set drawer to end of screen ( rtl )
                .build();                       // build drawer
        FetchAllRestaurants();
    }

    private void FetchAllRestaurants() {
        // Tag used to cancel the request
        String string_req = "req_fetch";
        progressDialog.setTitleText("لطفا منتظر بمانید");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Sellers Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Restaurants List fetched from server
                        JSONArray resturans = jObj.getJSONArray("seller");
                        for (int i = 0; i < resturans.length(); i++) {
                            JSONObject restaurant = resturans.getJSONObject(i);
                            String id = restaurant.getString(Config_TAG.ID);
                            String name = restaurant.getString(Config_TAG.NAME);
                            String title = restaurant.getString(Config_TAG.TITLE);
                            HashMap<String, String> map = new HashMap<>();
                            map.put(Config_TAG.ID, id);
                            map.put(Config_TAG.NAME, name);
                            map.put(Config_TAG.TITLE, title);
                            RestaurantList.add(map);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ListAdapter adapter = new SimpleAdapter(
                                            All_Sellers.this, RestaurantList,
                                            R.layout.item_seller, new String[]{
                                            Config_TAG.ID,
                                            Config_TAG.NAME,
                                            Config_TAG.TITLE
                                    },
                                            new int[]{
                                                    R.id.vid,
                                                    R.id.vname,
                                                    R.id.title
                                            });
                                    //setListAdapter(adapter);
                                    listView.setAdapter(adapter);
                                }
                            });
                        }
                    } else {
                        // Error occurred
                        String errorMsg = jObj.getString(Config_TAG.ERROR_MSG);
                        MakeToast(errorMsg, Config_TAG.ERROR);
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
                    MakeToast(error.getMessage(), Config_TAG.ERROR);
                } else
                    MakeToast("خطایی رخ داده است - اتصال به اینترنت را بررسی نمایید", Config_TAG.ERROR);
                hideDialog();
                finish();
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "videos");
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // if click menu , open drawer
        if (id == R.id.drawer) {
            result.openDrawer(); // open drawer
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void MakeToast(String Message, String TAG) {
        if (TAG.equals(Config_TAG.WARNING))
            CustomToast.custom(this, Message, R.drawable.ic_alert, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
        if (TAG.equals(Config_TAG.SUCCESS))
            CustomToast.custom(this, Message, R.drawable.ic_success, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
        if (TAG.equals(Config_TAG.ERROR))
            CustomToast.custom(this, Message, R.drawable.ic_error, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
    }

    private boolean CheckInternet() { // check network connection for run from possible exceptions
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        PackageManager PM = getPackageManager();
        if (PM.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                return true;
        } else {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                return true;
        }
        vibrator.vibrate(50);
        MakeToast("اتصال به اینترنت را بررسی نمایید", Config_TAG.WARNING);
        return false;
    }
}