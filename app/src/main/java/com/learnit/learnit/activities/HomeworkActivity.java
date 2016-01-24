package com.learnit.learnit.activities;


import android.app.ActionBar;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.learnit.learnit.fragments.learn_fragments.AbstractLearnFragment;
import com.learnit.learnit.fragments.learn_fragments.LearnHomeworkFragment;
import com.learnit.learnit.interfaces.ILearnFragmentUiEventHandler;
import com.learnit.learnit.types.NotificationBuilder;
import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeworkActivity
        extends AppCompatActivity
        implements ILearnFragmentUiEventHandler {
    private static String ACTIVE_IDS_TAG = "active_ids";

    private List<Integer> mHomeworkTypes;
    private List<Integer> mTranslationDirections;
    private List<WordBundle> mWordsToLearn;

    private int mCurrentWordNumber = -1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = this.getActionBar();
        if (actionBar != null) { actionBar.setTitle(""); }

        mTranslationDirections = getIntent().getIntegerArrayListExtra(NotificationBuilder.DIRECTIONS_OF_TRANS_TAG);
        mHomeworkTypes = getIntent().getIntegerArrayListExtra(NotificationBuilder.HOMEWORK_TYPE_TAG);
        mWordsToLearn = getIntent().getParcelableArrayListExtra(NotificationBuilder.WORDS_TAG);
        mCurrentWordNumber = getIntent().getIntExtra(NotificationBuilder.CURRENT_NOTIFICATION_INDEX_TAG, -1);
        if (mHomeworkTypes == null || mHomeworkTypes.isEmpty()) {
            this.finish();
            return;
        }

        Set<String> ids = new HashSet<>();
        for (WordBundle wordBundle: mWordsToLearn) {
            ids.add(String.valueOf(wordBundle.id()));
        }
        Prefs.putStringSet(ACTIVE_IDS_TAG, ids);

        // now let's start showing new words
        showNextFragment(Utils.enumValueFromKey(
                mHomeworkTypes.get(mCurrentWordNumber),
                Constants.LearnType.class));
    }

    private Bundle getCurrentExtras() {
        Bundle currentBundle = new Bundle();
        currentBundle.putParcelable(Constants.QUERY_WORD_KEY, mWordsToLearn.get(mCurrentWordNumber));
        currentBundle.putInt(Constants.TRANS_DIRECTION_KEY, mTranslationDirections.get(mCurrentWordNumber));
        return currentBundle;
    }

    public void showNextFragment(Constants.LearnType learnType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AbstractLearnFragment fragment = null;
        switch (learnType) {
            case TRANSLATIONS:
            case MIXED:
                fragment = LearnHomeworkFragment.newInstance(this);
                break;
            case ARTICLES:
//                fragmentManager
//                        .beginTransaction()
//                        .replace(android.R.id.content, _uiArticlesFragment, LearnHomeworkArticlesFragment.TAG)
//                        .commit();
                break;
        }
        if (fragment == null) {
            Log.e(Constants.LOG_TAG, "unexpectedly, homework fragment is null");
            return;
        }
        fragment.setArguments(getCurrentExtras());
        fragmentManager
                .beginTransaction()
                .replace(android.R.id.content, fragment, fragment.getTag())
                .commit();
    }

    private boolean moreNotificationsAvailable() {
        Set<String> ids = Prefs.getStringSet(ACTIVE_IDS_TAG, null);
        if (ids == null) { return false; }
        Log.d(Constants.LOG_TAG, "active ids are: " + ids.toString());
        for (WordBundle wordBundle: mWordsToLearn) {
            if (ids.contains(String.valueOf(wordBundle.id()))) {
                return true;
            }
        }
        return false;
    }

    private void removeWordFromActiveList(int id) {
        Set<String> ids = Prefs.getStringSet(ACTIVE_IDS_TAG, null);
        if (ids == null) {
            Log.e(Constants.LOG_TAG, "cannot fetch active words from prefs");
            return;
        }
        ids.remove(String.valueOf(id));
        Prefs.putStringSet(ACTIVE_IDS_TAG, ids);
    }

    @Override
    public void onAllViewsHidden() {
        Log.d(Constants.LOG_TAG, "activity received on all views hidden");
        // we want to cancel the appropriate notification
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(
                NotificationBuilder.notificationIdFromWordId(mWordsToLearn.get(mCurrentWordNumber).id()));
        removeWordFromActiveList(mWordsToLearn.get(mCurrentWordNumber).id());

        // check if there are any new words to show and stop it none are present
        if (!moreNotificationsAvailable()) {
            this.finish();
            return;
        }
        // update the word that we are looking for
        mCurrentWordNumber = (mCurrentWordNumber + 1) % mWordsToLearn.size();
        // in case there are other words to show - "Do it! Just. Do. It." (c)
        showNextFragment(Utils.enumValueFromKey(
                mHomeworkTypes.get(mCurrentWordNumber),
                Constants.LearnType.class));
    }
}
