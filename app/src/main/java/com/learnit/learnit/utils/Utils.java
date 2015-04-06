package com.learnit.learnit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Utils {
    private static final String PREFS_NAME = "my_awesome_prefs";

    public static boolean isRunFirstTime(Context context, String activityName) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean(activityName, true)) {
            Log.d(Constants.LOG_TAG, "setting the value of " + activityName + " to 'false'");
            // record the fact that the app has been started at least once
            settings.edit().putBoolean(activityName, false).apply();
            return true;
        }
        return false;
    }

    public static boolean isArticle(final String article, final Constants.LanguageName languageName) {

        return Constants.ARTICLES.containsKey(languageName)
                && (Constants.ARTICLES.get(languageName).contains(article.toLowerCase()));
    }

}
