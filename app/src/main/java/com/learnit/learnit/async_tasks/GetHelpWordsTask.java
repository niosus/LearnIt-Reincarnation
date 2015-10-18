/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.async_tasks;

import android.content.Context;

import com.learnit.learnit.types.DbHelper;
import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;

import java.util.List;

public class GetHelpWordsTask extends MySmartAsyncTask<String, List<WordBundle>> {

    protected String TAG = "get_help_words_task";

    public GetHelpWordsTask(Context context,
                            String queryWord) {
        super(context, queryWord);
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
        DbHelper dbHelper = DbHelper.Factory.createLocalizedHelper(mContext, DbHelper.DB_HELPER_DICT);
        return dbHelper.queryWord(queryWord, Constants.QueryStyle.APPROXIMATE_ENDING);
    }
}
