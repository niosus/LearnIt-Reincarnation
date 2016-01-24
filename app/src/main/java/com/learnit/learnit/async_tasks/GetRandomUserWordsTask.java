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

public class GetRandomUserWordsTask extends MySmartAsyncTask<Integer, List<WordBundle>> {

    protected String TAG = "get_random_words_task";
    protected Integer mOmitId = null;

    public GetRandomUserWordsTask(Context context,
                                  Integer numberOfWords) {
        super(context, numberOfWords);
    }

    public GetRandomUserWordsTask(Context context,
                                  Integer numberOfWords,
                                  Integer omitId) {
        super(context, numberOfWords);
        mOmitId = omitId;
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
    protected List<WordBundle> doInBackground(Integer... numberOfWords) {
        // we expect exactly one number - input word
        if (numberOfWords.length != 1) {
            return null;
        }
        Integer num = numberOfWords[0];
        DbHandler dbHandler = DbHandler.Factory.createLocalizedHelper(mContext, DbHandler.DB_USER_DICT);
        if (dbHandler == null) {
            Log.e(Constants.LOG_TAG, "db handler is suddenly null while getting random words from user dict");
            return null;
        }
        return dbHandler.queryRandomWords(num, mOmitId);
    }
}
