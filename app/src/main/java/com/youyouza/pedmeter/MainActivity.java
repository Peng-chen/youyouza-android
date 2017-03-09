package com.youyouza.pedmeter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceFragment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.youyouza.Draw.ChangeColorIconWithText;
import com.youyouza.data.bean.User;


import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static String TAG = "MainActivity";

//    about content_main


    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<>();

    private FragmentPagerAdapter mAdapter;

    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Log.i("MainActivity","onCreate -------111111111111>");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        Thread satartService = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MainActivity.this, PedometerService.class);
                startService(intent);
            }
        });


        satartService.start();


        setOverflowButtonAlways();
        initView();
        initDatas();
        mViewPager.setAdapter(mAdapter);
        initEvent();


//        IntentFilter filter = new IntentFilter(BROADCAST_ACTION_PEDEMETER);

//        myBroadcastReceiver = new MyBroadcastReceiver(stringExtraPedemeter, showPedmeterTev);
//
//        this.registerReceiver(myBroadcastReceiver, filter);


//     through  unregisterReceiver(myBroadcastReceiver);  to unregister


        Log.i(TAG, "detect the onCreate------>");

    }


    @Override
    public void onBackPressed() {


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

//        Log.i(TAG, "detect the onBackPreesed------>");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        Log.i("menu", "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //indicate  the setting button

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.i("menu", "onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.historyStep) {

            Intent intent = new Intent(this, HistoryStep.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.historyDistance) {
            //finish();
            Intent intent = new Intent(this, HistoryDistance.class);
            startActivity(intent);

        } else if (id == R.id.historyCalorie) {

            Intent intent = new Intent(this, HistoryCalories.class);
            startActivity(intent);

        } else if (id == R.id.setting) {

            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);


        } else if (id == R.id.donate) {
            Intent intent = new Intent(this, DonateActivity.class);
            startActivity(intent);

        } else if (id == R.id.aboutMe) {

            Intent intent = new Intent(this, AboutMe.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        Log.i(TAG, "onDestroy------>");

    }


// for content_main

    private void initEvent() {

        mViewPager.addOnPageChangeListener(new ViewPagerListener());

    }

    private void initDatas() {

        stepFragment stepFragment1 = new stepFragment();

        mTabs.add(stepFragment1);

        HistoryFragment historyFragment = new HistoryFragment();

        mTabs.add(historyFragment);

//        TabFragment tabFragment = new TabFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("page", 2);
//        tabFragment.setArguments(bundle);
//        mTabs.add(tabFragment);

//        for (int i = 2; i < mTitles.length - 1; ++i) {
//
//            TabFragment tabFragment = new TabFragment();
//            Bundle bundle = new Bundle();
//            bundle.putInt("page", i);
//            tabFragment.setArguments(bundle);
//            mTabs.add(tabFragment);
//
//        }

        PreferenceFragment pre = new PrefenenceFragment();

        mTabs.add(pre);

        mAdapter = new FragmentPagerAdapter(getFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };
    }


    private void setOverflowButtonAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKey = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKey.setAccessible(true);
            menuKey.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {

        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        ChangeColorIconWithText one = (ChangeColorIconWithText) findViewById(R.id.id_indicator_one);
        mTabIndicators.add(one);
        ChangeColorIconWithText two = (ChangeColorIconWithText) findViewById(R.id.id_indicator_two);
        mTabIndicators.add(two);
//        ChangeColorIconWithText three = (ChangeColorIconWithText) findViewById(R.id.id_indicator_three);
//        mTabIndicators.add(three);
        ChangeColorIconWithText four = (ChangeColorIconWithText) findViewById(R.id.id_indicator_four);
        mTabIndicators.add(four);

        ChangeColorOnWithTextListener listener = new ChangeColorOnWithTextListener();

        one.setOnClickListener(listener);
        two.setOnClickListener(listener);
//        three.setOnClickListener(listener);
        four.setOnClickListener(listener);

        one.setIconAlpha(1.0f);

    }


    class ViewPagerListener implements ViewPager.OnPageChangeListener {


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

//            Log.i(TAG, "onPageScrolled------>");

            if (positionOffset > 0) {
                ChangeColorIconWithText left = mTabIndicators.get(position);
                ChangeColorIconWithText right = mTabIndicators.get(position + 1);
                left.setIconAlpha(1 - positionOffset);
                right.setIconAlpha(positionOffset);
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


    class ChangeColorOnWithTextListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            clickTab(v);
        }

        /**
         * 点击Tab按钮
         *
         * @param v
         */
        private void clickTab(View v) {
            resetOtherTabs();

            switch (v.getId()) {
                case R.id.id_indicator_one:
                    mTabIndicators.get(0).setIconAlpha(1.0f);
                    mViewPager.setCurrentItem(0, false);
                    break;
                case R.id.id_indicator_two:
                    mTabIndicators.get(1).setIconAlpha(1.0f);
                    mViewPager.setCurrentItem(1, false);
                    break;
//                case R.id.id_indicator_three:
//                    mTabIndicators.get(2).setIconAlpha(1.0f);
//                    mViewPager.setCurrentItem(2, false);
//                    break;
                case R.id.id_indicator_four:
                    mTabIndicators.get(2).setIconAlpha(1.0f);
                    mViewPager.setCurrentItem(2, false);
                    break;
            }
        }

        /**
         * 重置其他的TabIndicator的颜色
         */
        private void resetOtherTabs() {
            for (int i = 0; i < mTabIndicators.size(); i++) {
                mTabIndicators.get(i).setIconAlpha(0);
            }
        }

    }


}
// in this section get broadcastreceiver


//    also update UI

class MyBroadcastReceiver extends BroadcastReceiver {

    private String extra;
    private TextView textView;

    public MyBroadcastReceiver(String extra, TextView textView) {
        this.extra = extra;
        this.textView = textView;

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra(extra);

        textView.setText(message);


    }
}






