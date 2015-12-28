/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.learnit.learnit.R;
import com.learnit.learnit.async_tasks.PopulateHelpDictTask;
import com.learnit.learnit.fragments.TaskSchedulerFragment;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.interfaces.IFabEventHandler;
import com.learnit.learnit.interfaces.IFabStateController;
import com.learnit.learnit.interfaces.ISnackBarController;
import com.learnit.learnit.types.TabsPagerAdapter;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnItemClick;


public class MainActivity
        extends AppCompatActivity
        implements IAsyncTaskResultClient, IFabStateController, ISnackBarController {
    @Bind(R.id.toolbar)             Toolbar mToolbar;
    @Bind(R.id.tab_layout)          TabLayout mTabLayout;
    @Bind(R.id.pager)               ViewPager mPager;
    @Bind(R.id.drawer_layout)       DrawerLayout mDrawerLayout;
    @Bind(R.id.fab)                 FloatingActionButton mFab;
    @Bind(R.id.coordinator_layout)  CoordinatorLayout mCoordinatorLayout;

    @BindDrawable(R.drawable.logo_white) Drawable mLogo;

    private ActionBarDrawerToggle mDrawerToggle;
    private TaskSchedulerFragment mTaskScheduler;
    private Map<Integer, IFabEventHandler> mFabEventHandlers;

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

        initActionBar();
        initTabbedViewPager();
        initTaskScheduler();
        initFab();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!Utils.languagesAreDefined(this)) {
            Log.w(Constants.LOG_TAG, "Languages are not defined. This should not happen...");
            startSettingsActivity();
        } else {
            Utils.updateHelpDictIfNeeded(this, mTaskScheduler, this);
        }
    }

    @OnItemClick(R.id.nav_drawer_list)
    public void navDrawerOnSelected(int position) {
        Log.d(Constants.LOG_TAG, "selected " + position);
        mDrawerLayout.closeDrawers();
        switch (position) {
            case 0:
                startSettingsActivity();
                break;
        }
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void initFab() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Constants.LOG_TAG, "fab clicked");
                for (IFabEventHandler handler : mFabEventHandlers.values()) {
                    handler.fabClicked(mPager.getCurrentItem());
                }
            }
        });
        // Initialize all those fancy event handlers
        // I still personally think it would be better to embed fab into a fragment,
        // but google devs think differently. Well, who am I to object?
        mFabEventHandlers = new HashMap<>();
    }

    private void initTabbedViewPager() {
        FragmentPagerAdapter mPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(mPagerAdapter);
        mTabLayout.setTabsFromPagerAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                IFabEventHandler currentFabEventHandler = mFabEventHandlers.get(tab.getPosition());
                if (currentFabEventHandler != null && currentFabEventHandler.fabNeeded()) {
                    mFab.show();
                } else {
                    mFab.hide();
                }
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initActionBar() {
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar == null) {
            Log.e(Constants.LOG_TAG, "support action bar is null.");
            return;
        }
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowTitleEnabled(false);
        adjustLogoSize();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.hello_world,
                R.string.hello_world) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void adjustLogoSize() {
        // ugh... a dirty-dirty hack to make logo of normal size... :( redo?
        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(mLogo);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        for (int i = 0; i < mToolbar.getChildCount(); i++) {
            View child = mToolbar.getChildAt(i);
            if (child != null) {
                if (child instanceof ImageView) {
                    ImageView iv2 = (ImageView) child;
                    if (iv2.getDrawable() == mLogo) {
                        iv2.setAdjustViewBounds(true);
                    }
                }
            }
        }
    }

    private void initTaskScheduler() {
        FragmentManager fragmentManager = getSupportFragmentManager();
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

    @Override
    public void showFab() {
        mFab.show();
    }

    @Override
    public void hideFab() {
        mFab.hide();
    }

    @Override
    public void addFabEventHandler(int position, IFabEventHandler handler) {
        mFabEventHandlers.put(position, handler);
        Log.d(Constants.LOG_TAG, "there are " + mFabEventHandlers.size() + " handlers ready for fab events");
    }

    @Override
    public void showSnackBar(final String message, final int duration) {
        int actualDuration;
        switch (duration) {
            case Snackbar.LENGTH_INDEFINITE:
                actualDuration = Snackbar.LENGTH_INDEFINITE;
                break;
            case Snackbar.LENGTH_LONG:
                actualDuration = Snackbar.LENGTH_LONG;
                break;
            case Snackbar.LENGTH_SHORT:
                actualDuration = Snackbar.LENGTH_SHORT;
                break;
            default:
                Log.e(Constants.LOG_TAG, "wrong duration for snack bar");
                return;
        }
        Snackbar snackbar = Snackbar
                .make(mCoordinatorLayout, message, actualDuration);
        snackbar.show();
    }

    @Override
    public void hideSnackBar() {

    }
}
