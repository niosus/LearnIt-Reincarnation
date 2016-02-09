/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.async_tasks;

import android.content.Context;

import com.learnit.learnit.db_handlers.DbHandler;

public class ImportTask extends MySmartAsyncTask<Void, Integer> {
    public final static int SUCCESS = 555;
    public final static int FAILURE = 666;

    protected String TAG = "import_task";

    public ImportTask(Context context,
                      Void nothing) {
        super(context, nothing);
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
    protected Integer doInBackground(Void... nothing) {
        try {
            DbHandler dbHandler = DbHandler.Factory.createLocalizedHelper(
                    mContext, DbHandler.DB_USER_DICT);
            if (dbHandler == null) {
                return FAILURE;
            }
            if (dbHandler.importDB()) {
                return SUCCESS;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return FAILURE;
    }
}
