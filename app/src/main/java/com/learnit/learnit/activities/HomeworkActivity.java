package com.learnit.learnit.activities;


import android.app.ActionBar;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.learnit.learnit.fragments.AbstractLearnFragment;
import com.learnit.learnit.fragments.LearnWordsCardFragment;
import com.learnit.learnit.fragments.TaskSchedulerFragment;
import com.learnit.learnit.interfaces.ILearnFragmentUiEventHandler;
import com.learnit.learnit.types.NotificationBuilder;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class HomeworkActivity
        extends AppCompatActivity
        implements ILearnFragmentUiEventHandler {
    private List<Integer> mHomeworkTypes;
    private List<Integer> mLearnWordIds;

    // TODO: this should be saved when the activity is killed (e.g. when rotating the screen)
    private int mCurrentFragmentNumber = -1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = this.getActionBar();
        if (actionBar != null) { actionBar.setTitle(""); }

        mHomeworkTypes = getIntent().getIntegerArrayListExtra(NotificationBuilder.HOMEWORK_TYPE_TAG);
        mLearnWordIds = getIntent().getIntegerArrayListExtra(NotificationBuilder.IDS_TAG);
        if (mHomeworkTypes == null || mHomeworkTypes.isEmpty()) {
            this.finish();
            return;
        }

        // we have just created this fragment so show the first word
        showNextFragment(Utils.enumValueFromKey(
                mHomeworkTypes.get(++mCurrentFragmentNumber),
                Constants.LearnType.class));
    }

    public void showNextFragment(Constants.LearnType learnType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (learnType) {
            case TRANSLATIONS:
            case MIXED:
                AbstractLearnFragment fragment = LearnWordsCardFragment.newInstance(this);
                // TODO: do not pass all the extras to the fragment.
                // TODO: each fragment should know only about itself and not everyone in the world.
                fragment.setArguments(getIntent().getExtras());
                fragmentManager
                        .beginTransaction()
                        .replace(android.R.id.content, fragment, LearnWordsCardFragment.TAG)
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

    @Override
    public void onAllViewsHidden() {
        Log.d(Constants.LOG_TAG, "activity received on all views hidden");
        // we want to cancel the appropriate notification
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mLearnWordIds.get(mCurrentFragmentNumber));
        // check if there are any new words to show and stop it none are present
        if (mCurrentFragmentNumber + 1 >= mHomeworkTypes.size()) {
            this.finish();
            return;
        }
        // in case there are other words to show - "Do it! Just. Do. It." (c)
        showNextFragment(Utils.enumValueFromKey(
                mHomeworkTypes.get(++mCurrentFragmentNumber),
                Constants.LearnType.class));
    }
}
