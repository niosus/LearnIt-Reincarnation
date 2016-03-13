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
import android.support.design.widget.AppBarLayout;
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
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.learnit.learnit.R;
import com.learnit.learnit.async_tasks.ExportTask;
import com.learnit.learnit.async_tasks.ImportTask;
import com.learnit.learnit.async_tasks.PopulateHelpDictTask;
import com.learnit.learnit.fragments.TaskSchedulerFragment;
import com.learnit.learnit.interfaces.IActionModeController;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.interfaces.IFabEventHandler;
import com.learnit.learnit.interfaces.IFabStateController;
import com.learnit.learnit.interfaces.IRefreshable;
import com.learnit.learnit.interfaces.IRefreshableController;
import com.learnit.learnit.interfaces.ISnackBarController;
import com.learnit.learnit.types.LanguagePair;
import com.learnit.learnit.types.TabsPagerAdapter;
import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnItemClick;


public class MainActivity
        extends AppCompatActivity
        implements IAsyncTaskResultClient,
        IFabStateController,
        ISnackBarController,
        IRefreshableController,
        IActionModeController {
    @Bind(R.id.toolbar)             Toolbar mToolbar;
    @Bind(R.id.tab_layout)          TabLayout mTabLayout;
    @Bind(R.id.pager)               ViewPager mPager;
    @Bind(R.id.drawer_layout)       DrawerLayout mDrawerLayout;
    @Bind(R.id.fab)                 FloatingActionButton mFab;
    @Bind(R.id.coordinator_layout)  CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.my_progress_bar)     ProgressBar mProgressBar;
    @Bind(R.id.app_bar)             AppBarLayout mAppBarLayout;

    @BindDrawable(R.drawable.logo_white) Drawable mLogo;

    private ActionBarDrawerToggle mDrawerToggle;
    private TaskSchedulerFragment mTaskScheduler;
    private Map<Integer, IFabEventHandler> mFabEventHandlers;
    private Map<Integer, IRefreshable> mRefreshableClients;
    private Snackbar mSnackbar;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Utils.isRunFirstTime(this.getLocalClassName())) {
            Log.d(Constants.LOG_TAG, "running " + this.getLocalClassName() + " for the first time");
            // start intro activity
            startActivity(new Intent(this, IntroActivity.class));
            return;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initActionBar();
        initTabbedViewPager();
        initTaskScheduler();
        initFab();
        mProgressBar.bringToFront();
        mProgressBar.setVisibility(View.INVISIBLE);


//        // The View with the BottomSheetBehavior
//        View bottomSheet = mCoordinatorLayout.findViewById(R.id.bottom_sheet);
//        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
//        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                // React to state change
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                // React to dragging events
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!Utils.languagesAreDefined(this)) {
            Log.w(Constants.LOG_TAG, "Languages are not defined. This should not happen...");
            startSettingsActivity();
        } else {
            boolean updating = Utils.updateHelpDictIfNeeded(this, mTaskScheduler, this);
            if (updating) {
                showSnackBar(getString(R.string.snack_loading_help_dict), Snackbar.LENGTH_INDEFINITE);
            }
        }
    }

    @OnItemClick(R.id.nav_drawer_list) @SuppressWarnings("unused")
    public void navDrawerOnSelected(int position) {
        Log.d(Constants.LOG_TAG, "selected " + position);
        mDrawerLayout.closeDrawers();
        switch (position) {
            case 0:
                startSettingsActivity();
                break;
            case 1:
                mTaskScheduler.newTaskForClient(new ExportTask(this, null), this);
                break;
            case 2:
                mTaskScheduler.newTaskForClient(new ImportTask(this, null), this);
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

        // For now our activity is the controller that tells other fragments to update when some
        // of them triggers that the underlying data has changed
        mRefreshableClients = new HashMap<>();
    }

    private void initTabbedViewPager() {
        FragmentPagerAdapter mPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override @SuppressWarnings("deprecated")
            public void onTabSelected(TabLayout.Tab tab) {
                IFabEventHandler currentFabEventHandler = mFabEventHandlers.get(tab.getPosition());
                if (currentFabEventHandler != null && currentFabEventHandler.fabNeeded()) {
                    mFab.setImageDrawable(
                            getResources().getDrawable(currentFabEventHandler.getDrawable()));
                    mFab.show();
                } else {
                    mFab.hide();
                }
                mPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == TabsPagerAdapter.LEARN_WORDS_ITEM) {
                    mAppBarLayout.setExpanded(false);
                } else {
                    mAppBarLayout.setExpanded(true);
                }
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
        mActionBar = getSupportActionBar();
        if (mActionBar == null) {
            Log.e(Constants.LOG_TAG, "support action bar is null.");
            return;
        }
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
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
        mDrawerLayout.addDrawerListener(mDrawerToggle);
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
        if (mDrawerToggle == null) { return; }
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
    public void onPreExecute() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mProgressBar.setIndeterminate(true);
    }
    @Override
    public void onProgressUpdate(Float progress) {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        if (mProgressBar.isIndeterminate()) {
            mProgressBar.setIndeterminate(false);
        }
        mProgressBar.setProgress(Math.round(progress));
    }
    @Override
    public <OutType> void onFinish(OutType result) {
        if (result instanceof Integer) {
            switch ((Integer) result) {
                case PopulateHelpDictTask.SUCCESS:
                    Log.d(Constants.LOG_TAG, "loaded help dictionary");
                    LanguagePair.Names languagePair = Utils.getCurrentLanguageNames(this);
                    String msg = String.format(getString(R.string.snack_loaded_help_dict),
                            languagePair.langToLearn(), languagePair.langYouKnow());
                    showSnackBar(msg, Snackbar.LENGTH_SHORT);
                    break;
                case ExportTask.SUCCESS:
                    showSnackBar(getString(R.string.toast_db_exported), Snackbar.LENGTH_SHORT);
                    break;
                case ExportTask.FAILURE:
                    showSnackBar(getString(R.string.toast_db_export_error), Snackbar.LENGTH_SHORT);
                    break;
                case ImportTask.SUCCESS:
                    showSnackBar(getString(R.string.toast_db_imported), Snackbar.LENGTH_SHORT);
                    break;
                case ImportTask.FAILURE:
                    showSnackBar(getString(R.string.toast_db_import_error), Snackbar.LENGTH_SHORT);
                    break;
            }
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onCancelled() {
    }

    @Override @SuppressWarnings("deprecated")
    public void showFab(int drawableId) {
        if (mSnackbar == null || !mSnackbar.isShown()) {
            mFab.setTranslationY(0);
        }
        mFab.setImageDrawable(getResources().getDrawable(drawableId));
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
        mSnackbar = Snackbar.make(mCoordinatorLayout, message, actualDuration);
        mSnackbar.show();
    }

    @Override
    public void refreshAllClients() {
        for (IRefreshable client : mRefreshableClients.values()) {
            client.refresh();
        }
    }

    @Override
    public void addRefreshableClient(int position, IRefreshable refreshable) {
        mRefreshableClients.put(position, refreshable);
        Log.d(Constants.LOG_TAG, "added refreshable client at pos: " + position);
        Log.d(Constants.LOG_TAG, "there are " + mRefreshableClients.size() + " refreshable clients");
    }

    @Override
    public void showSelected(final List<WordBundle> selectedItems) {
        Log.d(Constants.LOG_TAG, "trying to start action mode");
        startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // TODO: add edit menu if there is only single word selected
                mode.getMenuInflater().inflate(R.menu.action_mode, menu);
                mode.setTitle("selected " + selectedItems.size());
                Log.d(Constants.LOG_TAG, "starting action mode");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Similar to menu handling in Activity.onOptionsItemSelected()
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        //TODO: add real functionality hear
                        // Some remove functionality
                        return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }
}
