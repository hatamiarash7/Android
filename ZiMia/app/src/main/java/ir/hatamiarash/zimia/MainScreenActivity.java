/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.VideoView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.farsitel.bazaar.IUpdateCheckService;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import helper.FontHelper;
import helper.FormatHelper;
import helper.Helper;
import helper.SQLiteHandlerItem;
import helper.SQLiteHandlerSetup;
import helper.SessionManager;
import volley.AppController;
import volley.Config_TAG;
import volley.Config_URL;

public class MainScreenActivity extends AppCompatActivity {
    private static final String TAG = MainScreenActivity.class.getSimpleName();
    public static MainScreenActivity pointer;  // use to finish activity from anywhere
    public static SQLiteHandlerItem db;        // items database
    public static SQLiteHandlerSetup db3;      // setup database
    public static boolean card_check = false;
    static Typeface persianTypeface;           // persian font typeface
    public Drawer result = null;
    ImageView btnViewResturans;                // resturans list
    ImageView btnViewFastFoods;                // fastfoods list
    ImageView btnViewMarkets;                  // markets list
    ImageView btnViewMap;                      // view map
    Button btnClosePopup;                      // close popup
    Button btnNextPopup;                       // next popup
    ImageView Telegram, Website, Email;        // contact icons
    SessionManager session;                    // session for check user logged
    private long back_pressed;                 // for check back key pressed count
    private PopupWindow popupWindow;           // popup
    private PopupWindow popupWindow2;           // popup
    final private AccountHeader headerResult = null; // Header for drawer
    Button temp_login, temp_signup;            // temp screen buttons
    private VideoView lobby;
    private Boolean is_logged = false;
    WebView about_web;
    static private Menu menu;
    static private MenuItem mi;
    private Vibrator vibrator;
    private Boolean is_lobby = true;
    private Boolean is_popup = false;
    private int p_count = 0;
    private IUpdateCheckService service;
    private UpdateServiceConnection connection;
    public BadgeView badgeView;

    final private View.OnClickListener next_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            popupWindow2.dismiss();
            if (p_count != 1)
                Popup(++p_count);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pointer = this;
        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        boolean isFirstTime = settings.getBoolean("my_first_time", true);
        db = new SQLiteHandlerItem(getApplicationContext());   // items database
        db3 = new SQLiteHandlerSetup(getApplicationContext()); // setup database
        if (settings.getBoolean("my_first_time", true)) db3.CreateTable();
        HashMap<String, String> item = db3.GetProperties();    // get setup data
        if (isFirstTime && item.get("web").equals("-1")) {
            Intent i = new Intent(getApplicationContext(), SetupWeb.class);
            startActivity(i);
            finish();
        }
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        // promote application with need permissions ( specially for map )
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        session = new SessionManager(getApplicationContext()); // session manager for check user logged                                        // point the pointer to this activity for control from anywhere
        if (!card_check) {                                     // check that there is card database or not
            db.CreateTable();                                  // create card table
            card_check = true;
        }
        persianTypeface = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);     // set font for typeface
        if (session.isLoggedIn()) {
            setContentView(R.layout.main_screen);
            is_logged = true;
            is_lobby = false;
            btnViewResturans = (ImageView) findViewById(R.id.btnViewResturans);           // resturans button
            btnViewFastFoods = (ImageView) findViewById(R.id.btnViewFastFoods);           // fastfoods button
            btnViewMarkets = (ImageView) findViewById(R.id.btnViewMarkets);               // markets button
            btnViewMap = (ImageView) findViewById(R.id.btnViewMap);                       // map button
            btnViewResturans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Helper.CheckInternet(MainScreenActivity.this)) {
                        vibrator.vibrate(50);
                        Intent i = new Intent(getApplicationContext(), All_Sellers.class);
                        i.putExtra(Config_TAG.ID, Config_TAG.RESTAURANTS);
                        startActivity(i);
                    }
                }
            });
            btnViewFastFoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Helper.CheckInternet(MainScreenActivity.this)) {
                        vibrator.vibrate(50);
                        Intent i = new Intent(getApplicationContext(), All_Sellers.class);
                        i.putExtra(Config_TAG.ID, Config_TAG.FASTFOODS);
                        startActivity(i);
                    }
                }
            });
            btnViewMarkets.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Helper.CheckInternet(MainScreenActivity.this)) {
                        vibrator.vibrate(50);
                        Intent i = new Intent(getApplicationContext(), All_Sellers.class);
                        i.putExtra(Config_TAG.ID, Config_TAG.MARKETS);
                        startActivity(i);
                    }
                }
            });
            btnViewMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Helper.CheckInternet(MainScreenActivity.this)) {
                        Intent i = new Intent(getApplicationContext(), Map.class);
                        startActivity(i);
                    }
                }
            });

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(FontHelper.getSpannedString(this, getResources().getString(R.string.app_name_fa)));
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
                            new SecondaryDrawerItem().withName("ارسال نظر").withIcon(FontAwesome.Icon.faw_comment).withIdentifier(6).withTypeface(persianTypeface),
                            new SecondaryDrawerItem().withName("راهنما").withIcon(FontAwesome.Icon.faw_info).withIdentifier(7).withTypeface(persianTypeface)
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
                                if (Helper.CheckInternet(MainScreenActivity.this)) {
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
                                result.closeDrawer();
                                return true;
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 5) {
                                Intent i = new Intent(getApplicationContext(), ShopCard.class);
                                // start card activity and NOT-FINISH main activity for return
                                startActivity(i);
                                result.closeDrawer();
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 6) {
                                Intent i = new Intent(getApplicationContext(), Comment.class);
                                // start card activity and NOT-FINISH main activity for return
                                startActivity(i);
                                result.closeDrawer();
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 7) {
                                //Popup(p_count);
                                MakeDialog("ثبت نام انجام شد", "نام کاربری شما تلفن همراهتان می باشد");
                                result.closeDrawer();
                            }
                            return false;
                        }
                    })
                    .withSelectedItem(1)
                    .withSavedInstance(savedInstanceState)
                    .withDrawerGravity(Gravity.END) // set drawer to end of screen ( rtl )
                    .build();                       // build drawer
            NullVolley();
        } else {
            setTheme(R.style.Dark);
            setContentView(R.layout.temp);
            is_logged = false;
            temp_login = (Button) findViewById(R.id.temp_login);
            temp_signup = (Button) findViewById(R.id.temp_signup);
            lobby = (VideoView) findViewById(R.id.lobby);
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.lobby);
            lobby.setVideoURI(uri);
            lobby.start();
            temp_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Helper.CheckInternet(MainScreenActivity.this)) {
                        vibrator.vibrate(50);
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        // start restaurant activity and NOT-FINISH main activity for return
                        startActivity(i);
                    }
                }
            });
            temp_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Helper.CheckInternet(MainScreenActivity.this)) {
                        vibrator.vibrate(50);
                        Intent i = new Intent(getApplicationContext(), Register.class);
                        // start restaurant activity and NOT-FINISH main activity for return
                        startActivity(i);
                    }
                }
            });
            lobby.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                }
            });
        }
        invalidateOptionsMenu();
        if (Helper.CheckInternet(this))
            initService();
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
        if (id == R.id.drawer) {
            result.openDrawer();
            return true;
        } else if (id == R.id.cart) {
            Intent i = new Intent(getApplicationContext(), ShopCard.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if (!is_logged)
            lobby.start();
        try {
            AddBadge();
        } catch (NullPointerException e) {
            Log.e(TAG, "Known Error");
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (!is_logged)
            lobby.pause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        // check if back key press twice in 2 second
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            // kill app completely
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            if (!is_lobby) {
                if (is_popup)
                    if (popupWindow.isShowing()) popupWindow.dismiss();
                result.closeDrawer();
            }
            Helper.MakeToast(this, "برای خروج دوباره کلیک کنید", Config_TAG.WARNING);
        }
        // set current time for counter
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseService();
    }

    private void Popup(int index) {            // build and open popup
        result.closeDrawer();                  // close drawer first
        try {
            Display display = getWindowManager().getDefaultDisplay(); // get display data
            Point size = new Point();                                 // define new point variable
            display.getSize(size);                                    // get phone's screen size
            int width = size.x;                                       // get x var of screen
            int height = size.y;                                      // get y var of screen
            LayoutInflater inflater = (LayoutInflater) MainScreenActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // declare manual layout for run from exception
            View layout = inflater.inflate(R.layout.p1, (ViewGroup) findViewById(R.id.popup_1));
            if (index == 0)
                layout = inflater.inflate(R.layout.p1, (ViewGroup) findViewById(R.id.popup_1));
            if (index == 1)
                layout = inflater.inflate(R.layout.p2, (ViewGroup) findViewById(R.id.popup_2));
            popupWindow2 = new PopupWindow(layout, width, height, true);   // set layout's size
            popupWindow2.showAtLocation(layout, Gravity.CENTER, 0, 0);     // set location to center of screen
            btnNextPopup = (Button) layout.findViewById(R.id.next);        // close button
            btnNextPopup.setOnClickListener(next_button_click_listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void NullVolley() {
        String string_req = "req_fetch";
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.base_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MainScreenActivity.mi = MainScreenActivity.menu.findItem(R.id.cart);
                View c = findViewById(mi.getItemId());
                badgeView = new BadgeView(MainScreenActivity.pointer, c);
                AddBadge();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<>();
                params.put(Config_TAG.TAG, "null");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, string_req);
    }

    public void MakeNotification(String Title, String Message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_alert);
        mBuilder.setContentTitle(Title);
        mBuilder.setContentText(Message);
        Intent intent = new Intent(this, MainScreenActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainScreenActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent result = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(result);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1001, mBuilder.build());
    }

    class UpdateServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IUpdateCheckService.Stub.asInterface(boundService);
            try {
                long vCode = service.getVersionCode("ir.hatamiarash.zimia");
                Log.e(Config_TAG.UPDATE_CHECK, "VersionCode:" + vCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(Config_TAG.UPDATE_CHECK, "onServiceConnected(): Connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.e(Config_TAG.UPDATE_CHECK, "onServiceDisconnected(): Disconnected");
        }
    }

    private void initService() {
        Log.i(Config_TAG.UPDATE_CHECK, "initService()");
        connection = new UpdateServiceConnection();
        Intent intent = new Intent("com.farsitel.bazaar.service.UpdateCheckService.BIND");
        intent.setPackage("com.farsitel.bazaar");
        boolean ret = bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.e(Config_TAG.UPDATE_CHECK, "initService() bound value : " + ret);
    }

    private void releaseService() {
        unbindService(connection);
        connection = null;
        Log.d(Config_TAG.UPDATE_CHECK, "releaseService(): unbound");
    }

    private void MakeDialog(final String Title, final String Message) {
        new MaterialStyledDialog.Builder(this)
                .setTitle(FontHelper.getSpannedString(this, Title))
                .setDescription(FontHelper.getSpannedString(this, Message))
                .setStyle(Style.HEADER_WITH_TITLE)
                .withDarkerOverlay(true)
                .withDialogAnimation(true)
                .setCancelable(true)
                .setPositiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MakeDialog2(Title, Message);
                    }
                })
                .show();
    }

    private void MakeDialog2(String Title, String Message) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(Title)
                .setContentText(Message)
                .show();
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
}