/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.activities;

import android.animation.Animator;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.learnit.learnit.R;
import com.learnit.learnit.async_tasks.PopulateHelpDictTask;
import com.learnit.learnit.fragments.TaskSchedulerFragment;
import com.learnit.learnit.interfaces.IActionBarEvents;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.types.MyOnPageChangeListener;
import com.learnit.learnit.types.TabsPagerAdapter;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity
        extends AppCompatActivity
        implements ObservableScrollViewCallbacks, IActionBarEvents, IAsyncTaskResultClient {
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.tabs) PagerSlidingTabStrip mTabs;
    @InjectView(R.id.pager) ViewPager mViewPager;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.main_linear_layout)
    LinearLayout mParentLayout;
    @InjectView(R.id.nav_drawer_list)
    ListView mDrawerListView;
    FragmentPagerAdapter mPagerAdapter;

    private int mOldScroll;
    private ActionBarDrawerToggle mDrawerToggle;
    private TaskSchedulerFragment mTaskScheduler;
    private boolean mActionBarHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActionBarHidden = false;
        mOldScroll = 0;

        if (Utils.isRunFirstTime(this.getLocalClassName())) {
            Log.d(Constants.LOG_TAG, "running " + this.getLocalClassName() + " for the first time");
            // start intro activity
            startActivity(new Intent(this, IntroActivity.class));
        }
        ButterKnife.inject(this);
        initActionBar();
        initTabbedViewPager();
        initSidePane();
        initTaskScheduler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Utils.updateHelpDictIfNeeded(this, mTaskScheduler, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDrawerLayout.closeDrawer(mDrawerListView);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onScrollChanged(int newScroll, boolean b, boolean b2) {
        // The damping is needed to account for the implicit scroll that
        // occurs when the list view changes its size.
        if (newScroll - mOldScroll > mToolbar.getHeight()) {
            hideKeyboard(this);
            mOldScroll = newScroll;
            hideActionBar();
        }
        if (newScroll - mOldScroll < -mToolbar.getHeight()) {
            hideKeyboard(this);
            mOldScroll = newScroll;
            showActionBar();
        }
    }
    @Override
    public void onDownMotionEvent() {}
    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void showActionBar() {
        if (!mActionBarHidden) {
            // is shown already
            return;
        }
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        mDrawerLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        mActionBarHidden = false;
    }

    @Override
    public void hideActionBar() {
        if (mActionBarHidden) {
            // already hidden
            return;
        }
        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        mDrawerLayout.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        mActionBarHidden = true;
        mParentLayout.requestLayout();
    }

    private void initActionBar() {
        setSupportActionBar(mToolbar);
        adjustLogoSize();
    }

    private void initTabbedViewPager() {
        mPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mPagerAdapter);
        mTabs.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        mTabs.setOnPageChangeListener(new MyOnPageChangeListener(this));
    }

    private void initSidePane() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.str_learn_it, R.string.str_learn_it);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int clickedIndex, long l) {
                if (view instanceof TextView) {
                    if (clickedIndex == 0) {
                        startSettingsActivity();
                    }
                }
                Log.d(Constants.LOG_TAG, String.format("clicked view:%s, at pos:%s.", view.toString(), clickedIndex));
            }
        });
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
