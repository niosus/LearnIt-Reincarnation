/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.async_tasks;

import android.content.Context;
import android.util.Log;

import com.learnit.learnit.db_handlers.DbHandler;
import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;

import java.util.List;

public class GetHelpWordsTask extends MySmartAsyncTask<String, List<WordBundle>> {

    protected String TAG = "get_help_words_task";
    protected Integer mLimit;

    public GetHelpWordsTask(Context context,
                            String queryWord,
                            Integer limit) {
        super(context, queryWord);
        mLimit = limit;
    }

    @Override
    public void execute() {
        super.execute(mInputData);
    }

    @Override
    public String tag() {
        return TAG;
    }

    @Override
    protected List<WordBundle> doInBackground(String... queryWords) {
        // we expect exactly one string - input word
        if (queryWords.length != 1) {
            return null;
        }
        String queryWord = queryWords[0];
        if (queryWord.isEmpty()) {
            Log.w(Constants.LOG_TAG,
                    "Task tries to fetch all the help words. For now this is not allowed - too slow");
            return null;
        }
        DbHandler dbHandler = DbHandler.Factory.createLocalizedHelper(mContext, DbHandler.DB_HELPER_DICT);
        if (dbHandler == null) {
            Log.e(Constants.LOG_TAG, "db handler is suddenly null while getting help words");
            return null;
        }
        return dbHandler.queryWord(queryWord, Constants.QueryStyle.APPROXIMATE_ENDING, mLimit);
    }
}
