package ir.hatamiarash.malayeruniversity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
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
import helper.TypefaceSpan;
import slider.DescriptionAnimation;

public class MainActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    SliderLayout sliderShow;
    public Drawer result = null;
    private AccountHeader headerResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sliderShow = (SliderLayout) findViewById(R.id.slider_last_news);
        HashMap<String, String> url_maps = new HashMap<>();
        url_maps.put("مسجد", "http://malayer.nahadportal.com/uploads/photo_2016-06-14_08-24-46.jpg");
        url_maps.put("ریاست دانشگاه", "http://sqmac.ir/images/DSC06490.jpg");
        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setOnSliderClickListener(this)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra", name);
            sliderShow.addSlider(textSliderView);
        }
        sliderShow.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderShow.setDuration(2500);
        sliderShow.setCustomAnimation(new DescriptionAnimation());
        sliderShow.addOnPageChangeListener(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("خانه").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withSetSelected(true),
                        new PrimaryDrawerItem().withName("ورود").withIcon(FontAwesome.Icon.faw_sign_in).withIdentifier(2),
                        new PrimaryDrawerItem().withName("ثبت نام").withIcon(FontAwesome.Icon.faw_user_plus).withIdentifier(3),
                        new SectionDrawerItem().withName("جزئیات"),
                        new SecondaryDrawerItem().withName("درباره ما").withIcon(FontAwesome.Icon.faw_users).withIdentifier(4),
                        new SecondaryDrawerItem().withName("تماس با ما").withIcon(FontAwesome.Icon.faw_phone).withIdentifier(5)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem.getIdentifier() == 1) {
                            return true;
                        }
                        if (drawerItem != null && drawerItem.getIdentifier() == 2) {
                            return true;
                        }
                        if (drawerItem != null && drawerItem.getIdentifier() == 3) {
                            return true;
                        }
                        if (drawerItem != null && drawerItem.getIdentifier() == 4) {

                            return true;
                        }
                        if (drawerItem != null && drawerItem.getIdentifier() == 5) {
                            return true;
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withDrawerGravity(Gravity.END)
                .build();
    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

    @Override
    protected void onResume() {
        sliderShow.startAutoCycle();
        super.onResume();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, String.valueOf(slider.getBundle().get("extra")), Toast.LENGTH_SHORT).show();
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

    private void MakeToast(String Message) {
        Typeface font = Typeface.createFromAsset(getAssets(), FontHelper.FontPath);
        SpannableString efr = new SpannableString(Message);
        efr.setSpan(new TypefaceSpan(font), 0, efr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Toast.makeText(this, efr, Toast.LENGTH_SHORT).show();
    }
}