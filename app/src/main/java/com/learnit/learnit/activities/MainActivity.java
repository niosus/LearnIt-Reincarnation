/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.learnit.learnit.R;
import com.learnit.learnit.async_tasks.PopulateHelpDictTask;
import com.learnit.learnit.fragments.TaskSchedulerFragment;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.types.TabsPagerAdapter;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

import butterknife.ButterKnife;
import butterknife.Bind;


public class MainActivity
        extends AppCompatActivity
        implements IAsyncTaskResultClient {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.pager)
    ViewPager mPager;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_drawer_list)
    ListView mDrawerList;

    private TaskSchedulerFragment mTaskScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Utils.isRunFirstTime(this.getLocalClassName())) {
            Log.d(Constants.LOG_TAG, "running " + this.getLocalClassName() + " for the first time");
            // start intro activity
            startActivity(new Intent(this, IntroActivity.class));
        }
        ButterKnife.bind(this);
        mDrawerList = (ListView) findViewById(R.id.nav_drawer_list);

        // Set the adapter for the list view
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startSettingsActivity();
                }
            }
        });
        FragmentPagerAdapter mPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(mPagerAdapter);
        mTabLayout.setTabsFromPagerAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        initActionBar();
        initTaskScheduler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Utils.updateHelpDictIfNeeded(this, mTaskScheduler, this);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void initActionBar() {
        setSupportActionBar(mToolbar);
        adjustLogoSize();
    }

    private static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void adjustLogoSize() {
        // ugh... a dirty-dirty hack to make logo of normal size... :( redo?
        Drawable logo = getResources().getDrawable(R.drawable.logo_white);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(logo);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        for (int i = 0; i < mToolbar.getChildCount(); i++) {
            View child = mToolbar.getChildAt(i);
            if (child != null) {
                if (child instanceof ImageView) {
                    ImageView iv2 = (ImageView) child;
                    if (iv2.getDrawable() == logo) {
                        iv2.setAdjustViewBounds(true);
                    }
                }
            }
        }
    }

    private void initTaskScheduler() {
        FragmentManager fragmentManager = getFragmentManager();
        mTaskScheduler = (TaskSchedulerFragment)
                fragmentManager.findFragmentByTag(TaskSchedulerFragment.TAG);
        if (mTaskScheduler == null) {
            mTaskScheduler = new TaskSchedulerFragment();
            fragmentManager.beginTransaction()
                    .add(mTaskScheduler, TaskSchedulerFragment.TAG)
                    .commit();
        }
    }

    @Override
    public String tag() {
        return "main_activity";
    }
    @Override
    public void onPreExecute() {}
    @Override
    public void onProgressUpdate(Float progress) {}
    @Override
    public <OutType> void onFinish(OutType result) {
        if (result instanceof Integer) {
            if (result == PopulateHelpDictTask.SUCCESS) {
                Log.d(Constants.LOG_TAG, "loaded help dictionary");
            }
        }
    }

    @Override
    public void onCancelled() {

    }
}
