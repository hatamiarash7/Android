/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
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

import dmax.dialog.SpotsDialog;
import helper.FontHelper;
import helper.FormatHelper;
import helper.ProductAdapter;
import helper.SQLiteHandlerItem;
import helper.SessionManager;
import helper.TypefaceSpan;
import ir.hatamiarash.MyToast.CustomToast;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class Seller_Detail extends AppCompatActivity {
    private static final String TAG = Seller_Detail.class.getSimpleName();
    public static Seller_Detail pointer;  // use to finish activity from anywhere
    private ArrayList<HashMap<String, String>> ProductsList;
    private AlertDialog progressDialog;
    private TextView seller_name;
    private TextView seller_open_hour;
    private TextView seller_close_hour;
    private TextView seller_address;
    private TextView other1, other2;
    private Boolean is_open = false;
    private String pid;
    private String SellerType;
    static Typeface persianTypeface;           // persian font typeface
    public Drawer result = null;
    Button btnClosePopup;                      // close popup
    ImageView Telegram, Website, Email;        // contact icons
    SessionManager session;                    // session for check user logged
    private PopupWindow popupWindow;           // popup
    private AccountHeader headerResult = null; // Header for drawer
    WebView about_web;
    private ListView listView;
    private Vibrator vibrator;
    static public Menu menu;
    static public MenuItem mi;
    public BadgeView badgeView;
    public static SQLiteHandlerItem db;

    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            popupWindow.dismiss(); // close popup
        }
    };
    private View.OnClickListener telegram_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=zimia_ir"));
            popupWindow.dismiss(); // close popup
            startActivity(i); // open telegram channel
        }
    };
    private View.OnClickListener website_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory(Intent.CATEGORY_BROWSABLE);
            i.setData(Uri.parse("http://zimia.ir"));
            popupWindow.dismiss(); // close popup
            startActivity(i); // open web browser and go to website
        }
    };
    private View.OnClickListener email_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory(Intent.CATEGORY_BROWSABLE);
            i.setData(Uri.parse("mailto:info@zimia.ir"));
            popupWindow.dismiss(); // close popup
            startActivity(i); // open email application and send email
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_details);
        pointer = this;
        db = new SQLiteHandlerItem(getApplicationContext());
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        seller_name = (TextView) findViewById(R.id.SellerName);
        seller_open_hour = (TextView) findViewById(R.id.SellerOpenHour);
        seller_close_hour = (TextView) findViewById(R.id.SellerCloseHour);
        seller_address = (TextView) findViewById(R.id.SellerAddress);
        other1 = (TextView) findViewById(R.id.textView2);
        other2 = (TextView) findViewById(R.id.textView3);
        seller_name.setText(null);
        seller_open_hour.setText(null);
        seller_close_hour.setText(null);
        seller_address.setText(null);
        other1.setVisibility(View.INVISIBLE);
        other2.setVisibility(View.INVISIBLE);
        progressDialog = new SpotsDialog(this, R.style.CustomDialog);
        progressDialog.setCancelable(false);
        ProductsList = new ArrayList<>();
        Intent i = getIntent();
        pid = i.getStringExtra(Config_TAG.ID);    // get param from top level
        SellerType = i.getStringExtra(Config_TAG.TYPE);
        session = new SessionManager(getApplicationContext());
        listView = (ListView) findViewById(R.id.seller_list);

        if (!session.isLoggedIn()) { // user not logged
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
            SpannableString efr = new SpannableString("null");
            if (SellerType.equals("Restaurants"))
                efr = new SpannableString(getResources().getString(R.string.restaurants_fa));
            if (SellerType.equals("FastFoods"))
                efr = new SpannableString(getResources().getString(R.string.fastfoods_fa));
            if (SellerType.equals("Markets"))
                efr = new SpannableString(getResources().getString(R.string.markets_fa));
            efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            toolbar.setTitle(efr);
            setSupportActionBar(toolbar);
            result = new DrawerBuilder()
                    .withActivity(this)
                    .withAccountHeader(headerResult)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName("خانه").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withTypeface(persianTypeface).withSetSelected(true),
                            new PrimaryDrawerItem().withName("ورود").withIcon(FontAwesome.Icon.faw_sign_in).withIdentifier(2).withTypeface(persianTypeface),
                            new PrimaryDrawerItem().withName("ثبت نام").withIcon(FontAwesome.Icon.faw_user_plus).withIdentifier(3).withTypeface(persianTypeface),
                            new SectionDrawerItem().withName("جزئیات").withTypeface(persianTypeface),
                            new SecondaryDrawerItem().withName("درباره ما").withIcon(FontAwesome.Icon.faw_users).withIdentifier(4).withTypeface(persianTypeface),
                            new SecondaryDrawerItem().withName("تماس با ما").withIcon(FontAwesome.Icon.faw_phone).withIdentifier(5).withTypeface(persianTypeface)
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            if (drawerItem != null && drawerItem.getIdentifier() == 1) {
                                Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
                                // start main activity again and finish this one
                                startActivity(i);
                                finish();
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 2) {
                                if (CheckInternet()) {
                                    Intent i = new Intent(getApplicationContext(), Login.class);
                                    // start login activity and NOT-FINISH main activity for return
                                    startActivity(i);
                                }
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 3) {
                                if (CheckInternet()) {
                                    Intent i = new Intent(getApplicationContext(), Register.class);
                                    // start register activity and NOT-FINISH main activity for return
                                    startActivity(i);
                                }
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 4) {
                                Intent i = new Intent(getApplicationContext(), WebPage.class);
                                i.putExtra(Config_TAG.TITLE, "درباره ما");
                                i.putExtra(Config_TAG.ADDRESS, "about");
                                startActivity(i);
                                result.closeDrawer();
                                return true;
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 5) {
                                Intent i = new Intent(getApplicationContext(), Contact.class);
                                // start card activity and NOT-FINISH main activity for return
                                startActivity(i);
                                return true;
                            }
                            return false;
                        }
                    })
                    .withSavedInstance(savedInstanceState)
                    .withDrawerGravity(Gravity.END)
                    .build();
        } else { // user logged
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
            SpannableString efr = new SpannableString("null");
            if (SellerType.equals("Restaurants"))
                efr = new SpannableString(getResources().getString(R.string.restaurants_fa));
            if (SellerType.equals("FastFoods"))
                efr = new SpannableString(getResources().getString(R.string.fastfoods_fa));
            if (SellerType.equals("Markets"))
                efr = new SpannableString(getResources().getString(R.string.markets_fa));
            efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            toolbar.setTitle(efr);
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
                                }
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
                                Intent i = new Intent(getApplicationContext(), ShopCard.class);
                                // start card activity and NOT-FINISH main activity for return
                                startActivity(i);
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
        }
        FetchSellerDetails();                         // start to fetching data from server
    }

    private void FetchSellerDetails() {
        // Tag used to cancel the request
        String string_req = "req_fetch";
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Seller Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        JSONObject seller = jObj.getJSONObject("seller");
                        String open_hour = seller.getString(Config_TAG.OPEN_HOUR);
                        String close_hour = seller.getString(Config_TAG.CLOSE_HOUR);
                        String name = null;
                        switch (SellerType) {
                            case "Restaurants":
                                name = "رستوران " + seller.getString(Config_TAG.NAME);
                                break;
                            case "FastFoods":
                                name = "فست فود " + seller.getString(Config_TAG.NAME);
                                break;
                            case "Markets":
                                name = "فروشگاه " + seller.getString(Config_TAG.NAME);
                                break;
                        }
                        String address = "آدرس : " + seller.getString(Config_TAG.ADDRESS);
                        other1.setVisibility(View.VISIBLE);
                        other2.setVisibility(View.VISIBLE);
                        seller_name.setText(name);
                        seller_open_hour.setText(open_hour);
                        seller_close_hour.setText(close_hour);
                        seller_address.setText(address);
                        Calendar time = Calendar.getInstance();
                        int current_hour = time.get(Calendar.HOUR_OF_DAY);
                        is_open = current_hour > Integer.parseInt(open_hour) && current_hour < Integer.parseInt(close_hour);
                        FetchSellerProducts();
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
                params.put(Config_TAG.TAG, "seller_details");
                params.put(Config_TAG.TYPE, SellerType);
                params.put(Config_TAG.ID, pid);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    private void FetchSellerProducts() {
        // Tag used to cancel the request
        String string_req = "req_fetch";
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Products Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean(Config_TAG.ERROR);
                    if (!error) {
                        // Products List fetched from server
                        JSONArray product = jObj.getJSONArray("products");
                        for (int i = 0; i < product.length(); i++) {
                            JSONObject products = product.getJSONObject(i);
                            String id = products.getString(Config_TAG.ID);
                            String name = products.getString(Config_TAG.NAME);
                            String price = products.getString(Config_TAG.PRICE);
                            String specification = products.getString(Config_TAG.SPECIFICATION);
                            int picture = products.getInt(Config_TAG.PICTURE);
                            int price_off = products.getInt(Config_TAG.PRICE_OFF);
                            int web3d = products.getInt(Config_TAG.WEB_3D);
                            //String type = products.getString(Config_TAG.TYPE);
                            price += " تومان";
                            HashMap<String, String> map = new HashMap<>();
                            map.put(Config_TAG.ID, id);
                            map.put(Config_TAG.NAME, name);
                            map.put(Config_TAG.PRICE, price);
                            map.put(Config_TAG.SPECIFICATION, specification);
                            map.put(Config_TAG.PRICE_OFF, String.valueOf(price_off));
                            map.put(Config_TAG.WEB_3D, String.valueOf(web3d));
                            String add = "i" + String.valueOf(picture);
                            int pic = getResources().getIdentifier(add, "drawable", getPackageName());
                            map.put(Config_TAG.PICTURE, String.valueOf(pic));
                            ProductsList.add(map);
                            Seller_Detail.mi = Seller_Detail.menu.findItem(R.id.cart);
                            View c = findViewById(mi.getItemId());
                            badgeView = new BadgeView(Seller_Detail.pointer, c);
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    ProductAdapter adapter = new ProductAdapter(ProductsList, Seller_Detail.pointer);
                                    listView.setAdapter(adapter);
                                }
                            });
                        }
                        AddBadge();
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
                params.put(Config_TAG.TAG, "seller_products");
                params.put(Config_TAG.TYPE, Convert(SellerType));
                params.put(Config_TAG.ID, pid);
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
        } else if (id == R.id.cart) {
            Intent i = new Intent(getApplicationContext(), ShopCard.class);
            // start card activity and NOT-FINISH main activity for return
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public void AddBadge() {
        badgeView.setText(FormatHelper.toPersianNumber(String.valueOf(db.getItemCount())));
        badgeView.setBadgeBackgroundColor(getResources().getColor(R.color.Amber));
        badgeView.setTextColor(getResources().getColor(R.color.md_black_1000));
        TranslateAnimation anim = new TranslateAnimation(0, 0, -50, 0);
        anim.setInterpolator(new BounceInterpolator());
        anim.setDuration(1000);
        badgeView.toggle(anim, null);
        badgeView.clearComposingText();
        badgeView.show();
    }

    private String Convert(String seller) {
        String converted = null;
        switch (seller) {
            case "Restaurants":
                converted = seller.substring(0, seller.length() - 1);
                converted += "_Foods";
                break;
            case "FastFoods":
                converted = seller.substring(0, seller.length() - 1);
                converted += "_Foods";
                break;
            case "Markets":
                converted = seller.substring(0, seller.length() - 1);
                converted += "_Products";
                break;
        }
        return converted;
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

    private void MakeToast(String Message, String TAG) {
        if (TAG.equals(Config_TAG.WARNING))
            CustomToast.custom(this, Message, R.drawable.ic_alert, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
        if (TAG.equals(Config_TAG.SUCCESS))
            CustomToast.custom(this, Message, R.drawable.ic_success, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
        if (TAG.equals(Config_TAG.ERROR))
            CustomToast.custom(this, Message, R.drawable.ic_error, getResources().getColor(R.color.black), getResources().getColor(R.color.white), Toast.LENGTH_SHORT, true, true).show();
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}