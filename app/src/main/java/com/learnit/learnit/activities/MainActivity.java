/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.learnit.learnit.R;
import com.learnit.learnit.types.TabsPagerAdapter;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity
        implements ObservableScrollViewCallbacks {
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.tabs) PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.nav_drawer_list)
    ListView mDrawerListView;

    private int mOldScroll;
    private ActionBarDrawerToggle mDrawerToggle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOldScroll = 0;

        if (Utils.isRunFirstTime(this.getLocalClassName())) {
            Log.d(Constants.LOG_TAG, "running " + this.getLocalClassName() + " for the first time");

            // start intro activity
            startActivity(new Intent(this, IntroActivity.class));
        }

        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // ugh... a dirty-dirty hack to make logo of normal size... :( redo?
        Drawable logo = getResources().getDrawable(R.drawable.logo_white);
        getSupportActionBar().setLogo(logo);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child != null) {
                if (child instanceof ImageView) {
                    ImageView iv2 = (ImageView) child;
                    if (iv2.getDrawable() == logo) {
                        iv2.setAdjustViewBounds(true);
                    }
                }
            }
        }

        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(0);
        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(MainActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == 0.0
                        && (position == 1 || position == 2)) {
                    ActionBar ab = getSupportActionBar();
                    if (ab == null) {
                        return;
                    }
                    if (!ab.isShowing()) {
                        ab.show();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(Constants.LOG_TAG, "page changed");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.hello_world,
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

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (view instanceof TextView) {
                    String[] menuEntries = getResources().getStringArray(R.array.entries_actions_drawer);
                    TextView textView = (TextView) view;
                    if (textView.getText().toString().equals(menuEntries[0])) {
                        startSettingsActivity();
                    }
                }
                Log.d(Constants.LOG_TAG, String.format("clicked view:%s, at pos:%s.", view.toString(), i));
            }
        });
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onScrollChanged(int newScroll, boolean b, boolean b2) {
        ActionBar ab = getSupportActionBar();
        if (ab == null) {
            return;
        }

        // The damping is needed to account for the implicit scroll that
        // occurs when the list view changes its size.
        if (newScroll - mOldScroll > toolbar.getHeight()) {
            hideKeyboard(this);
            mOldScroll = newScroll;
            if (ab.isShowing()) {
                ab.hide();
            }
        }
        if (newScroll - mOldScroll < -toolbar.getHeight()) {
            hideKeyboard(this);
            mOldScroll = newScroll;
            if (!ab.isShowing()) {
                ab.show();
            }
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

}
