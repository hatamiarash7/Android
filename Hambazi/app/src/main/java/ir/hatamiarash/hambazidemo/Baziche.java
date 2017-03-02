package ir.hatamiarash.hambazidemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.kekstudio.dachshundtablayout.indicators.DachshundIndicator;

import helper.FontHelper;

public class Baziche extends AppCompatActivity {

    private static final String TYPES[] = {"همه", "خارجی", "دخترانه", "مشاغل", "آموزشی", "بوردی"};
    ViewPager viewPager;
    DachshundTabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baziche);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(FontHelper.getSpannedString(this, "بازیچه"));
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        tabLayout = (DachshundTabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setAnimatedIndicator(new DachshundIndicator(tabLayout));
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FragmentList.newInstance(TYPES[position], Baziche.this);
                case 1:
                    return FragmentList.newInstance(TYPES[position], Baziche.this);
                case 2:
                    return FragmentList.newInstance(TYPES[position], Baziche.this);
                case 3:
                    return FragmentList.newInstance(TYPES[position], Baziche.this);
                case 4:
                    return FragmentList.newInstance(TYPES[position], Baziche.this);
                case 5:
                    return FragmentList.newInstance(TYPES[position], Baziche.this);
                default:
                    return FragmentList.newInstance(TYPES[position], Baziche.this);
            }
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TYPES[position];
        }
    }
}