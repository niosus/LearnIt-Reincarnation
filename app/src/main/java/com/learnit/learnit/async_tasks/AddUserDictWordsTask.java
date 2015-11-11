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

import java.util.ArrayList;
import java.util.List;

public class AddUserDictWordsTask extends MySmartAsyncTask<List<WordBundle>, List<Constants.AddWordReturnCode>> {

    protected String TAG = "add_user_word_task";

    public AddUserDictWordsTask(Context context,
                                List<WordBundle> bundles) {
        super(context, bundles);
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
    protected List<Constants.AddWordReturnCode> doInBackground(List<WordBundle>... wordBundlesLists) {
        if (wordBundlesLists.length != 1) {
            // something is wrong. There should be only one list
            return null;
        }
        List<WordBundle> wordBundles = wordBundlesLists[0];
        List<Constants.AddWordReturnCode> result = new ArrayList<>();
        DbHandler dbHandler = DbHandler.Factory.createLocalizedHelper(mContext, DbHandler.DB_USER_DICT);
        int progress = 0;
        for (WordBundle bundleToAdd: wordBundles){
            if (dbHandler == null) {
                Log.e(Constants.LOG_TAG, "db handler is suddenly null while adding words to the user dict");
                return null;
            }
            result.add(dbHandler.addWord(bundleToAdd));
            mAsyncTaskResultClient.onProgressUpdate(((float) progress++) / wordBundles.size());
        }
        return result;
    }
}
