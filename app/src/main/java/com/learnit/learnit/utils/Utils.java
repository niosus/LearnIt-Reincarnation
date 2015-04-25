/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.utils;

import android.content.Context;
import android.util.Log;

import com.learnit.learnit.R;
import com.pixplicity.easyprefs.library.Prefs;

public class Utils {
    public static boolean isRunFirstTime(String activityName) {
        if (Prefs.getBoolean(activityName, true)) {
            Log.d(Constants.LOG_TAG, "setting the value of " + activityName + " to 'false'");
            // record the fact that the app has been started at least once
            Prefs.putBoolean(activityName, false);
            return true;
        }
        return false;
    }

    public static boolean isArticle(final String article, final Constants.LanguageName languageName) {

        return Constants.ARTICLES.containsKey(languageName)
                && (Constants.ARTICLES.get(languageName).contains(article.toLowerCase()));
    }

    public static boolean areBothNull(final Object obj1, final Object obj2) {
        return obj1 == null && obj2 == null;
    }

    public static boolean languagesHaveChanged(Context context) {
        boolean changed = false;
        final int UNDEFINED = -666;
        int langToLearnIndex = Prefs.getInt(
                context.getString(R.string.key_language_to_learn), UNDEFINED);
        if (langToLearnIndex == UNDEFINED) {
            return false;
        }
        int langYouKnowIndex = Prefs.getInt(
                context.getString(R.string.key_language_you_know), UNDEFINED);
        if (langYouKnowIndex == UNDEFINED) {
            return false;
        }

        // here we have two languages defined
        // the languages are saved as the indexes in R.array.lang_tags_all
        int storedLangToLearnIndex = Prefs.getInt(
                context.getString(R.string.key_stored_lang_to_learn), UNDEFINED);
        int storedLangYouKnowIndex = Prefs.getInt(
                context.getString(R.string.key_stored_lang_you_know), UNDEFINED);

        // we need to get the old saved values for the languages and compare them
        if (storedLangToLearnIndex != langToLearnIndex) {
            changed = true;
            Prefs.putInt(context.getString(R.string.key_stored_lang_to_learn), langToLearnIndex);
        }
        if (storedLangYouKnowIndex != langYouKnowIndex) {
            changed = true;
            Prefs.putInt(context.getString(R.string.key_stored_lang_you_know), langYouKnowIndex);
        }

        // TODO: we need to handle the Auto situation too.

        return changed;
    }

}
