/*
 * Copyright (c) 2016 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.zimia;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.VideoView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.HashMap;

import helper.FontHelper;
import helper.SQLiteHandler;
import helper.SQLiteHandlerItem;
import helper.SQLiteHandlerSetup;
import helper.SessionManager;
import helper.TypefaceSpan;

public class MainScreenActivity extends AppCompatActivity {
    private static final String TAG = MainScreenActivity.class.getSimpleName();
    public static MainScreenActivity pointer;  // use to finish activity from anywhere
    public static SQLiteHandlerItem db;        // items database
    public static SQLiteHandler db2;           // users database
    public static SQLiteHandlerSetup db3;      // setup database
    public static boolean card_check = false;
    static Typeface persianTypeface;           // persian font typeface
    public Drawer result = null;
    ImageView btnViewResturans;                // resturans list
    ImageView btnViewFastFoods;                // fastfoods list
    ImageView btnViewMarkets;                  // markets list
    ImageView btnViewMap;                      // view map
    Button btnClosePopup;                      // close popup
    ImageView Telegram, Website, Email;        // contact icons
    SessionManager session;                    // session for check user logged
    private long back_pressed;                 // for check back key pressed count
    private PopupWindow pwindo;                // popup
    private AccountHeader headerResult = null; // Header for drawer
    Button temp_login, temp_signup;            // temp screen buttons
    private VideoView lobby;
    private Boolean is_logged = false;
    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pwindo.dismiss(); // close popup
        }
    };
    private View.OnClickListener telegram_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=zimia_ir"));
            pwindo.dismiss(); // close popup
            startActivity(i); // open telegram channel
        }
    };
    private View.OnClickListener website_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory(Intent.CATEGORY_BROWSABLE);
            i.setData(Uri.parse("http://zimia.ir"));
            pwindo.dismiss(); // close popup
            startActivity(i); // open web browser and go to website
        }
    };
    private View.OnClickListener email_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory(Intent.CATEGORY_BROWSABLE);
            i.setData(Uri.parse("mailto:info@zimia.ir"));
            pwindo.dismiss(); // close popup
            startActivity(i); // open email application and send email
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
        boolean isFirstTime = settings.getBoolean("my_first_time", true);
        db3 = new SQLiteHandlerSetup(getApplicationContext()); // setup database
        if (settings.getBoolean("my_first_time", true)) db3.CreateTable();
        HashMap<String, String> item = db3.GetProperties();    // get setup data
        Log.d(TAG, "1:" + String.valueOf(isFirstTime) + " " + String.valueOf(item.get("web")));
        if (isFirstTime && item.get("web").equals("-1")) {
            Intent i = new Intent(getApplicationContext(), SetupWeb.class);
            startActivity(i);
            finish();
        }
        // promote application with need permissions ( specially for map )
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        session = new SessionManager(getApplicationContext()); // session manager for check user logged
        pointer = this;                                        // point the pointer to this activity for control from anywhere
        db = new SQLiteHandlerItem(getApplicationContext());   // items database
        db2 = new SQLiteHandler(getApplicationContext());      // users database
        if (!card_check) {                                     // check that there is card database or not
            db.CreateTable();                                  // create card table
            card_check = true;
        }
        persianTypeface = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);     // set font for typeface
        if (session.isLoggedIn()) {
            setContentView(R.layout.drawer);
            is_logged = true;
            btnViewResturans = (ImageView) findViewById(R.id.btnViewResturans);           // resturans button
            btnViewFastFoods = (ImageView) findViewById(R.id.btnViewFastFoods);           // fastfoods button
            btnViewMarkets = (ImageView) findViewById(R.id.btnViewMarkets);               // markets button
            btnViewMap = (ImageView) findViewById(R.id.btnViewMap);                       // map button
            btnViewResturans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CheckInternet()) {
                        Intent i = new Intent(getApplicationContext(), All_Restaurants.class);
                        // start restaurant activity and NOT-FINISH main activity for return
                        startActivity(i);
                    }
                }
            });
            btnViewFastFoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CheckInternet()) {
                        Intent i = new Intent(getApplicationContext(), All_FastFoods.class);
                        // start fastfood activity and NOT-FINISH main activity for return
                        startActivity(i);
                    }
                }
            });
            btnViewMarkets.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CheckInternet()) {
                        Intent i = new Intent(getApplicationContext(), All_Markets.class);
                        // start market activity and NOT-FINISH main activity for return
                        startActivity(i);
                    }
                }
            });
            btnViewMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CheckInternet()) {
                        Intent i = new Intent(getApplicationContext(), Map.class);
                        // start map activity and NOT-FINISH main activity for return
                        startActivity(i);
                    }
                }
            });
            if (!session.isLoggedIn()) { // user not logged
                final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                is_logged = false;
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
                                    } else
                                        result.closeDrawer(); // close drawer
                                }
                                if (drawerItem != null && drawerItem.getIdentifier() == 3) {
                                    if (CheckInternet()) {
                                        Intent i = new Intent(getApplicationContext(), Register.class);
                                        // start register activity and NOT-FINISH main activity for return
                                        startActivity(i);
                                    } else
                                        result.closeDrawer(); // close drawer
                                }
                                if (drawerItem != null && drawerItem.getIdentifier() == 4) {
                                    initiatePopupWindow(R.id.popup_about); // show about popup
                                    return true;
                                }
                                if (drawerItem != null && drawerItem.getIdentifier() == 5) {
                                    initiatePopupWindow(R.id.popup_contact); // show contact popup
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
                                new SecondaryDrawerItem().withName("تماس با ما").withIcon(FontAwesome.Icon.faw_phone).withIdentifier(4).withTypeface(persianTypeface)
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
                                    initiatePopupWindow(R.id.popup_about); // open about popup
                                    return true;
                                }
                                if (drawerItem != null && drawerItem.getIdentifier() == 4) {
                                    initiatePopupWindow(R.id.popup_contact); // open contact popup
                                    return true;
                                }
                                if (drawerItem != null && drawerItem.getIdentifier() == 5) {
                                    Intent i = new Intent(getApplicationContext(), ShopCard.class);
                                    // start card activity and NOT-FINISH main activity for return
                                    startActivity(i);
                                }
                                return false;
                            }
                        })
                        .withSelectedItem(1)
                        .withSavedInstance(savedInstanceState)
                        .withDrawerGravity(Gravity.END) // set drawer to end of screen ( rtl )
                        .build();                       // build drawer
            }
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
                    if (CheckInternet()) {
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        // start restaurant activity and NOT-FINISH main activity for return
                        startActivity(i);
                    }
                }
            });
            temp_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CheckInternet()) {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // show drawer - defined like menus
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // if click menu , open drawer
        if (id == R.id.drawershow) {
            result.openDrawer(); // open drawer
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initiatePopupWindow(int id) { // build and open popup
        result.closeDrawer();                  // close drawer first
        try {
            Display display = getWindowManager().getDefaultDisplay(); // get display data
            Point size = new Point();                                 // define new point variable
            display.getSize(size);                                    // get phone's screen size
            int width = size.x;                                       // get x var of screen
            int height = size.y;                                      // get y var of screen
            LayoutInflater inflater = (LayoutInflater) MainScreenActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // declare manual layout for run from exception
            View layout = inflater.inflate(R.layout.about, (ViewGroup) findViewById(R.id.popup_about));
            if (id == R.id.popup_about)
                layout = inflater.inflate(R.layout.about, (ViewGroup) findViewById(R.id.popup_about));
            if (id == R.id.popup_contact)
                layout = inflater.inflate(R.layout.contact, (ViewGroup) findViewById(R.id.popup_contact));
            if (id == R.id.drawershow)
                result.openDrawer();
            pwindo = new PopupWindow(layout, width - (width / 5), height - (height / 4), true); // set layout's size
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);                                // set location to center of screen
            btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);                 // close button
            Telegram = (ImageView) layout.findViewById(R.id.contact_telegram);                  // telegram button
            Website = (ImageView) layout.findViewById(R.id.contact_website);                    // website button
            Email = (ImageView) layout.findViewById(R.id.contact_email);                        // email button
            btnClosePopup.setOnClickListener(cancel_button_click_listener);                     // close listener
            Telegram.setOnClickListener(telegram_button_click_listener);                        // telegram listener
            Website.setOnClickListener(website_button_click_listener);                          // website listener
            Email.setOnClickListener(email_button_click_listener);                              // email listener
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        if (!is_logged)
            lobby.start();
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
        } else
            MakeToast("برای خروج دوباره کلیک کنید");
        // set current time for counter
        back_pressed = System.currentTimeMillis();
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

    private void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }

    private boolean CheckInternet() { // check network connection for run from possible exceptions
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager Telephone = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PackageManager PM = getPackageManager();
        if (PM.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                return true;
        } else {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                return true;
        }
        MakeToast("اتصال به اینترنت را بررسی نمایید");
        return false;
    }
}