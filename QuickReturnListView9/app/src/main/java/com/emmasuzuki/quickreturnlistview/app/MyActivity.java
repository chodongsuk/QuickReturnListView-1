package com.emmasuzuki.quickreturnlistview.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;

import com.emmasuzuki.quickreturnlistview.R;


public class MyActivity extends ActionBarActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // Setup swipable tab contents
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOnPageChangeListener(mPageChangeListener);

        // Setup tabs
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab listTab = getSupportActionBar().newTab();
        listTab.setText(getString(R.string.list_view));
        listTab.setTabListener(mTabListener);
        getSupportActionBar().addTab(listTab);

        Tab gridTab = getSupportActionBar().newTab();
        gridTab.setText(getString(R.string.grid_view));
        gridTab.setTabListener(mTabListener);
        getSupportActionBar().addTab(gridTab);
    }

    private ViewPager.SimpleOnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            getSupportActionBar().setSelectedNavigationItem(position);
        }
    };

    private ActionBar.TabListener mTabListener = new ActionBar.TabListener() {

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {

        }
    };

    private static class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new QuickReturnListFragment();

                case 1:
                    return new QuickReturnGridFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
