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

public class DeleteWordFromUserDictTask extends MySmartAsyncTask<List<WordBundle>, Constants.DeleteWordReturnCode> {

    protected String TAG = "add_user_word_task";

    public DeleteWordFromUserDictTask(Context context,
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
    protected Constants.DeleteWordReturnCode doInBackground(List<WordBundle>... wordBundlesList) {
        if (wordBundlesList.length != 1) {
            // something is wrong. There should be only one list
            return null;
        }
        List<WordBundle> wordBundles = wordBundlesList[0];
        DbHandler dbHandler = DbHandler.Factory.createLocalizedHelper(mContext, DbHandler.DB_USER_DICT);
        if (dbHandler == null) {
            Log.e(Constants.LOG_TAG, "oops, database handler not defined in: " + this.getClass().getSimpleName());
            return Constants.DeleteWordReturnCode.FAILURE;
        }
        for (WordBundle bundle: wordBundles) {
            dbHandler.deleteWord(bundle);
        }
        return Constants.DeleteWordReturnCode.SUCCESS;
    }
}
