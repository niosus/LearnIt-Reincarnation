package com.learnit.learnit.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.learnit.learnit.R;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;
import com.learnit.learnit.views.AddWordsCardFragment;
import com.learnit.learnit.views.SuperAwesomeCardFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity
        implements ObservableScrollViewCallbacks {
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.tabs) PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager) ViewPager pager;

    private Drawable oldBackground = null;
    private int currentColor;
    private int mOldScroll;
    private SystemBarTintManager mTintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOldScroll = 0;

        if (Utils.isRunFirstTime(this, this.getLocalClassName())) {
            Log.d(Constants.LOG_TAG, "running for the first time");

            // start intro activity
            startActivity(new Intent(this, IntroActivity.class));
        }

        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        // create our manager instance after the content view is set
        mTintManager = new SystemBarTintManager(this);
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(0);
//        changeColor(getResources().getColor(R.color.highlight));
        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(MainActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(Constants.LOG_TAG, "pos offset " + positionOffset);
                if (positionOffset > 0.999) {
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
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    private void changeColor(int newColor) {
        tabs.setBackgroundColor(newColor);
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = new ColorDrawable(getResources().getColor(android.R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
        if (oldBackground == null) {
            getSupportActionBar().setBackgroundDrawable(ld);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});
            getSupportActionBar().setBackgroundDrawable(td);
            td.startTransition(200);
        }

        oldBackground = ld;
        currentColor = newColor;
    }

    @Override
    public void onScrollChanged(int newScroll, boolean b, boolean b2) {
//        Log.d(Constants.LOG_TAG, "scroll changed " + newScroll + " oldscroll " + mOldScroll + " " + b + " " + b2);
        ActionBar ab = getSupportActionBar();
        if (ab == null) {
            return;
        }
        if (newScroll - mOldScroll > toolbar.getHeight()) {
            mOldScroll = newScroll;
            if (ab.isShowing()) {
                ab.hide();
            }
        }
        if (newScroll - mOldScroll < -toolbar.getHeight()) {
            mOldScroll = newScroll;
            if (!ab.isShowing()) {
                ab.show();
            }
        }
    }

    @Override
    public void onDownMotionEvent() {
        Log.d(Constants.LOG_TAG, "down motion event");
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        Log.d(Constants.LOG_TAG, "up motion event");
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        private static final int ADD_WORDS_ITEM = 0;
        private static final int DICT_ITEM = 1;
        private static final int LEARN_WORDS_ITEM = 2;

        private final String[] TITLES = {
                getString(R.string.add_words_frag_title),
                getString(R.string.dictionary_frag_title),
                getString(R.string.learn_words_frag_title) };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(Constants.LOG_TAG, "asking frag at pos " + position);
            switch (position) {
                case ADD_WORDS_ITEM:
                    return AddWordsCardFragment.newInstance(position);
                case DICT_ITEM:
                    return SuperAwesomeCardFragment.newInstance(position);
                case LEARN_WORDS_ITEM:
                    return SuperAwesomeCardFragment.newInstance(position);
            }
            return SuperAwesomeCardFragment.newInstance(position);
        }
    }
}
