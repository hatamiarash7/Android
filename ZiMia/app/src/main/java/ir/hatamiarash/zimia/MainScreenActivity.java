package ir.hatamiarash.zimia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
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

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import helper.SessionManager;
import helper.TypefaceSpan;

public class MainScreenActivity extends AppCompatActivity {
    public static MainScreenActivity pointer;
    private static Typeface persianTypeface;
    public Drawer result = null;
    ImageView btnViewResturans;
    ImageView btnViewFastFoods;
    ImageView btnViewMarkets;
    ImageView btnViewMap;
    Button btnClosePopup;
    SessionManager session;
    private PopupWindow pwindo;
    private AccountHeader headerResult = null;
    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pwindo.dismiss();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.drawer);
        pointer = this;
        persianTypeface = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
        btnViewResturans = (ImageView) findViewById(R.id.btnViewResturans);
        btnViewFastFoods = (ImageView) findViewById(R.id.btnViewFastFoods);
        btnViewMarkets = (ImageView) findViewById(R.id.btnViewMarkets);
        btnViewMap = (ImageView) findViewById(R.id.btnViewMap);
        btnViewResturans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternet()) {
                    Intent i = new Intent(getApplicationContext(), AllResturans.class);
                    startActivity(i);
                }
            }
        });
        btnViewFastFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternet()) {
                    Intent i = new Intent(getApplicationContext(), AllFastFoods.class);
                    startActivity(i);
                }
            }
        });
        btnViewMarkets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternet()) {
                    Intent i = new Intent(getApplicationContext(), AllMarkets.class);
                    startActivity(i);
                }
            }
        });
        btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternet()) {
                    Intent i = new Intent(getApplicationContext(), Map.class);
                    startActivity(i);
                }
            }
        });
        // Session manager
        session = new SessionManager(getApplicationContext());
        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            result = new DrawerBuilder()
                    .withActivity(this)
                    .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName("خانه").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withTypeface(persianTypeface),
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
                                startActivity(i);
                                finish();
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 2) {
                                Intent i = new Intent(getApplicationContext(), Login.class);
                                startActivity(i);
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 3) {
                                Intent i = new Intent(getApplicationContext(), Register.class);
                                startActivity(i);
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 4) {
                                initiatePopupWindow(R.id.popup_about);
                                return true;
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 5) {
                                initiatePopupWindow(R.id.popup_contact);
                                return true;
                            }
                            return false;
                        }
                    })
                    .withSavedInstance(savedInstanceState)
                    .withDrawerGravity(Gravity.END)
                    .build();
        } else {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            result = new DrawerBuilder()
                    .withActivity(this)
                    .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName("خانه").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withSetSelected(true).withTypeface(persianTypeface),
                            new PrimaryDrawerItem().withName("حساب کاربری").withIcon(FontAwesome.Icon.faw_credit_card).withIdentifier(2).withTypeface(persianTypeface),
                            new SectionDrawerItem().withName("جزئیات").withTypeface(persianTypeface),
                            new SecondaryDrawerItem().withName("درباره ما").withIcon(FontAwesome.Icon.faw_users).withIdentifier(3).withTypeface(persianTypeface),
                            new SecondaryDrawerItem().withName("تماس با ما").withIcon(FontAwesome.Icon.faw_phone).withIdentifier(4).withTypeface(persianTypeface)
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            if (drawerItem != null && drawerItem.getIdentifier() == 1) {
                                Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
                                startActivity(i);
                                finish();
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 2) {
                                if (CheckInternet()) {
                                    Intent i = new Intent(getApplicationContext(), Profile.class);
                                    startActivity(i);
                                } else {
                                    result.closeDrawer();
                                    Toast.makeText(getApplicationContext(), "اتصال به اینترنت را بررسی نمایید", Toast.LENGTH_LONG).show();
                                }
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 3) {
                                initiatePopupWindow(R.id.popup_about);
                                return true;
                            }
                            if (drawerItem != null && drawerItem.getIdentifier() == 4) {
                                initiatePopupWindow(R.id.popup_contact);
                                return true;
                            }
                            return false;
                        }
                    })
                    .withSelectedItem(1)
                    .withSavedInstance(savedInstanceState)
                    .withDrawerGravity(Gravity.END)
                    .build();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    private void initiatePopupWindow(int id) {
        result.closeDrawer();
        try {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) MainScreenActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.about, (ViewGroup) findViewById(R.id.popup_about));
            if (id == R.id.popup_about)
                layout = inflater.inflate(R.layout.about, (ViewGroup) findViewById(R.id.popup_about));
            if (id == R.id.popup_contact)
                layout = inflater.inflate(R.layout.contact, (ViewGroup) findViewById(R.id.popup_contact));
            if (id == R.id.drawershow)
                result.openDrawer();
            pwindo = new PopupWindow(layout, width - (width / 5), height - (height / 4), true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
            btnClosePopup.setOnClickListener(cancel_button_click_listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        else {
            MakeToast("اتصال به اینترنت را بررسی نمایید");
        }
        return false;
    }

    public void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_LONG).show();
    }
}