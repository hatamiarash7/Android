/*
 * Copyright (c) 2017 - All Rights Reserved - Arash Hatami
 */

package ir.hatamiarash.hambazidemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
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

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import helper.FontHelper;
import helper.Helper;
import helper.SessionManager;
import ir.hatamiarash.MySlider.DescriptionAnimation;
import volley.Config_TAG;

public class MainScreenActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private static final String TAG = MainScreenActivity.class.getSimpleName();
    public static MainScreenActivity pointer;  // use to finish activity from anywhere
    static Typeface persianTypeface;           // persian font typeface
    public Drawer result = null;
    Button btnBaziche, btnShop, btn360;                  // markets list
    SessionManager session;                    // session for check user logged
    private long back_pressed;                 // for check back key pressed count
    final private AccountHeader headerResult = null; // Header for drawer
    Button temp_login, temp_signup;            // temp screen buttons
    private VideoView lobby;
    private Boolean is_logged = false;
    static private Menu menu;
    static private MenuItem mi;
    private Vibrator vibrator;
    private Boolean is_lobby = true;
    private IUpdateCheckService service;
    private UpdateServiceConnection connection;
    private SliderLayout MainSlider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pointer = this;
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        // promote application with need permissions ( specially for map )
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        session = new SessionManager(getApplicationContext()); // session manager for check user logged                                        // point the pointer to this activity for control from anywhere
        persianTypeface = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);     // set font for typeface
        if (session.isLoggedIn()) {
            setContentView(R.layout.main_screen);

            is_logged = true;
            is_lobby = false;

            btnBaziche = (Button) findViewById(R.id.baziche);
            btnShop = (Button) findViewById(R.id.shop);
            btn360 = (Button) findViewById(R.id.web360);
            MainSlider = (SliderLayout) findViewById(R.id.main_slider);

            HashMap<String, Integer> file_maps = new HashMap<>();
            file_maps.put("بازی های بردی", R.drawable.p1);
            file_maps.put("بازی های مادر و کودک", R.drawable.p2);
            file_maps.put("بازی های فیزیکی حرکتی", R.drawable.p3);
            file_maps.put("مسابقات تلوزیونی", R.drawable.p4);
            file_maps.put("بازی های ویدئویی", R.drawable.p5);

            for (String name : file_maps.keySet()) {
                TextSliderView textSliderView = new TextSliderView(this);
                // initialize a SliderLayout
                textSliderView
                        .description(name)
                        .image(file_maps.get(name))
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                        .setOnSliderClickListener(this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", name);

                MainSlider.addSlider(textSliderView);
            }
            MainSlider.setPresetTransformer(SliderLayout.Transformer.Default);
            MainSlider.setDuration(2500);
            MainSlider.setCustomAnimation(new DescriptionAnimation());
            MainSlider.addOnPageChangeListener(this);

            btnBaziche.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Helper.CheckInternet(MainScreenActivity.this)) {
                        vibrator.vibrate(50);
                        Intent i = new Intent(getApplicationContext(), Baziche.class);
                        startActivity(i);
                    }
                }
            });
            btnShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Helper.CheckInternet(MainScreenActivity.this)) {
                        vibrator.vibrate(50);
                        /*Intent i = new Intent(getApplicationContext(), All_Sellers.class);
                        startActivity(i);*/
                    }
                }
            });
            btn360.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Helper.CheckInternet(MainScreenActivity.this)) {
                        vibrator.vibrate(50);
                        /*Intent i = new Intent(getApplicationContext(), WebPage.class);
                        i.putExtra(Config_TAG.ADDRESS, "http://mehrdad.arash-hatami.ir");
                        startActivity(i);*/
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
                                result.closeDrawer();
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 6) {
                                Intent i = new Intent(getApplicationContext(), Comment.class);
                                // start card activity and NOT-FINISH main activity for return
                                startActivity(i);
                                result.closeDrawer();
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 7) {
                                result.closeDrawer();
                            }
                            return false;
                        }
                    })
                    .withSelectedItem(1)
                    .withSavedInstance(savedInstanceState)
                    .withDrawerGravity(Gravity.END) // set drawer to end of screen ( rtl )
                    .build();                       // build drawer
        } else {
            setTheme(R.style.Dark);
            setContentView(R.layout.temp);
            is_logged = false;
            temp_login = (Button) findViewById(R.id.temp_login);
            temp_signup = (Button) findViewById(R.id.temp_signup);
            lobby = (VideoView) findViewById(R.id.lobby);
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        if (session.isLoggedIn())
            MainSlider.stopAutoCycle();
        if (!is_logged)
            lobby.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (session.isLoggedIn())
            MainSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (session.isLoggedIn())
            MainSlider.startAutoCycle();
        if (!is_logged)
            lobby.start();
        super.onResume();
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
                result.closeDrawer();
            }
            Helper.MakeToast(this, "برای خروج دوباره کلیک کنید", Config_TAG.WARNING);
        }
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

    class UpdateServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IUpdateCheckService.Stub.asInterface(boundService);
            try {
                long vCode = service.getVersionCode("ir.hatamiarash.hambazi");
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

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}