/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.learnit.learnit.R;
import com.learnit.learnit.async_tasks.PopulateHelpDictTask;
import com.learnit.learnit.fragments.TaskSchedulerFragment;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.db_handlers.DbHandler;
import com.learnit.learnit.types.LanguagePair;
import com.learnit.learnit.types.WordBundle;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

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

        // get the currently picked languages
        int langToLearnIndex = Prefs.getInt(context.getString(R.string.key_language_to_learn), Constants.UNDEFINED_INDEX);
        if (langToLearnIndex == Constants.UNDEFINED_INDEX) {
            return false;
        }
        int langYouKnowIndex = Prefs.getInt(context.getString(R.string.key_language_you_know), Constants.UNDEFINED_INDEX);
        if (langYouKnowIndex == Constants.UNDEFINED_INDEX) {
            return false;
        }

        // here we have two languages defined
        // the languages are saved as the indexes in R.array.lang_tags_all
        int storedLangToLearnIndex = Prefs.getInt(
                context.getString(R.string.previously_stored_lang_to_learn), Constants.UNDEFINED_INDEX);
        int storedLangYouKnowIndex = Prefs.getInt(
                context.getString(R.string.previously_stored_lang_you_know), Constants.UNDEFINED_INDEX);

        // we need to get the old saved values for the languages and compare them
        if (storedLangToLearnIndex != langToLearnIndex) {
            changed = true;
            Prefs.putInt(context.getString(R.string.previously_stored_lang_to_learn), langToLearnIndex);
        }
        if (storedLangYouKnowIndex != langYouKnowIndex) {
            changed = true;
            Prefs.putInt(context.getString(R.string.previously_stored_lang_you_know), langYouKnowIndex);
        }

        return changed;
    }

    public static LanguagePair.Names getCurrentLanguageNames(Context context) {
        int langToLearnIndex = Prefs.getInt(context.getString(R.string.previously_stored_lang_to_learn), -1);
        int langYouKnowIndex = Prefs.getInt(context.getString(R.string.previously_stored_lang_you_know), -1);
        Resources res = context.getResources();
        String[] allLanguages = res.getStringArray(R.array.languages_all);
        LanguagePair.Names result = new LanguagePair.Names();
        result.setLangToLearn(allLanguages[langToLearnIndex])
                .setLangYouKnow(allLanguages[langYouKnowIndex]);
        return result;
    }

    public static LanguagePair.Tags getCurrentLanguageTags(Context context) {
        LanguagePair.Tags result = new LanguagePair.Tags();
        int langToLearnIndex = Prefs.getInt(context.getString(R.string.previously_stored_lang_to_learn), -1);
        int langYouKnowIndex = Prefs.getInt(context.getString(R.string.previously_stored_lang_you_know), -1);
        if (langToLearnIndex < 0 || langYouKnowIndex < 0) {
            // return some dummy result
            result.setLangToLearnTag("undefined").setLangYouKnowTag("undefined");
            return result;
        }
        Resources res = context.getResources();
        String[] allLangTags = res.getStringArray(R.array.lang_tags_all);
        result.setLangToLearnTag(allLangTags[langToLearnIndex])
                .setLangYouKnowTag(allLangTags[langYouKnowIndex]);
        return result;
    }

    public static int updateLangIndexIfNeeded(Context context, final int currentLangIndex) {
        int newLangIndex = currentLangIndex;
        if (context == null) {
            Log.e(Constants.LOG_TAG, "context is null when updating language index");
            return newLangIndex;
        }
        Resources res = context.getResources();
        String[] langTags = res.getStringArray(R.array.lang_tags_all);
        if (currentLangIndex > langTags.length) {
            Log.e(Constants.LOG_TAG, "currentLangIndex is wrong --> bigger then length of all tags");
            return newLangIndex;
        }

        String autoLangTag = res.getString(R.string.lang_tag_auto);
        if (autoLangTag.equals(langTags[currentLangIndex])) {
            // we need to update index with the index found from auto

            String currentLangTag = Locale.getDefault().getLanguage();
            int foundIndex = Arrays.asList(langTags).indexOf(currentLangTag);
            if (foundIndex < 0) {
                // we haven't found the appropriate code
                Log.e(Constants.LOG_TAG, "the locale language tag '"
                        + currentLangTag + "' is not supported, fixme!");
                return Constants.UNDEFINED_INDEX;
            }
            // found index is legit, so we update our selection.
            newLangIndex = foundIndex;
        }

        return newLangIndex;
    }

    public static File dictFileFromCurrentLanguageTags(LanguagePair.Tags currentLangTags) {
        File sd = Environment.getExternalStorageDirectory();
        sd = new File(sd, "LearnIt");
        sd = new File(sd, String.format("%s-%s",
                currentLangTags.langToLearnTag(),
                currentLangTags.langYouKnowTag()));
        sd = new File(sd, String.format("%s-%s.txt",
                currentLangTags.langToLearnTag(),
                currentLangTags.langYouKnowTag()));
        return sd;
    }

    public static boolean languagesAreDefined(Context context) {
        // get the currently picked languages
        int langToLearnIndex = Prefs.getInt(
                context.getString(R.string.key_language_to_learn), Constants.UNDEFINED_INDEX);
        int langYouKnowIndex = Prefs.getInt(
                context.getString(R.string.key_language_you_know), Constants.UNDEFINED_INDEX);
        return langToLearnIndex != Constants.UNDEFINED_INDEX
                && langYouKnowIndex != Constants.UNDEFINED_INDEX;
    }

    public static void updateHelpDictIfNeeded(Context context,
                                              TaskSchedulerFragment taskScheduler,
                                              IAsyncTaskResultClient resultClient) {
        if (Utils.languagesHaveChanged(context)) {
            LanguagePair.Tags currentLangTags = Utils.getCurrentLanguageTags(context);
            File dictFile = Utils.dictFileFromCurrentLanguageTags(currentLangTags);
            Log.d(Constants.LOG_TAG, "path do dict: " + dictFile.getPath());

            // delete the old database anyway
            DbHandler helper = DbHandler.Factory.createLocalizedHelper(context, DbHandler.DB_HELPER_DICT);
            if (helper == null) {
                Log.e(Constants.LOG_TAG, "helper is null. Exiting.");
                return;
            }
            helper.deleteDatabase();

            if (!dictFile.exists()) {
                Log.e(Constants.LOG_TAG, "dict does not exist in folder");
                return;
            }

            if (taskScheduler == null) {
                Log.e(Constants.LOG_TAG, "no task scheduler to load dict");
                return;
            }
            taskScheduler.newTaskForClient(new PopulateHelpDictTask(context, dictFile.getPath()), resultClient);
        }
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.showSoftInput(view, 0);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int localizedStringResForWordType(int wordType) {
        switch (wordType) {
            case WordBundle.WordType.NOUN:
                return R.string.word_type_noun;
            case WordBundle.WordType.VERB:
                return R.string.word_type_verb;
            case WordBundle.WordType.ADJECTIVE:
                return R.string.word_type_adjective;
            case WordBundle.WordType.ADVERB:
                return R.string.word_type_adverb;
            case WordBundle.WordType.CONJUNCTION:
                return R.string.word_type_conjunction;
            case WordBundle.WordType.PREPOSITION:
                return R.string.word_type_preposition;
        }
        return R.string.word_type_none;
    }
}
