package com.learnit.learnit.activities;


import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.learnit.learnit.fragments.LearnWordsCardFragment;
import com.learnit.learnit.fragments.TaskSchedulerFragment;
import com.learnit.learnit.types.NotificationBuilder;
import com.learnit.learnit.utils.Constants;

import java.util.ArrayList;

public class HomeworkActivity extends AppCompatActivity {
    Fragment _uiTranslationsFragment;
    Fragment _uiArticlesFragment;
    private TaskSchedulerFragment mTaskScheduler;
    private FragmentManager mFragmentManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = this.getActionBar();
        if (actionBar != null) { actionBar.setTitle(""); }
        mFragmentManager = getSupportFragmentManager();

        initTaskScheduler();

        _uiTranslationsFragment = new LearnWordsCardFragment();
        // extras contain words, translations and so on that we need to show
        // the data in the homework fragment. We pass them on to the fragment.
        _uiTranslationsFragment.setArguments(getIntent().getExtras());

        ArrayList<Integer> types = getIntent().getIntegerArrayListExtra(NotificationBuilder.HOMEWORK_TYPE_TAG);
        if (types == null || types.isEmpty()) return;
        // TODO: decide how to pick homework
        mFragmentManager.beginTransaction()
                .replace(android.R.id.content, _uiTranslationsFragment, _uiTranslationsFragment.getTag())
                .commit();
    }

    private void initTaskScheduler() {
        mFragmentManager.findFragmentByTag(TaskSchedulerFragment.TAG);
        if (mTaskScheduler == null) {
            mTaskScheduler = new TaskSchedulerFragment();
            mFragmentManager.beginTransaction()
                    .add(mTaskScheduler, TaskSchedulerFragment.TAG)
                    .commit();
        }
    }

    public void replaceFragment(Constants.LearnType learnType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (learnType) {
            case TRANSLATIONS:
                fragmentManager
                        .beginTransaction()
                        .replace(android.R.id.content, _uiTranslationsFragment, LearnWordsCardFragment.TAG)
                        .commit();
                break;
            case ARTICLES:
//                fragmentManager
//                        .beginTransaction()
//                        .replace(android.R.id.content, _uiArticlesFragment, LearnHomeworkArticlesFragment.TAG)
//                        .commit();
                break;
        }
    }

}
