/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.async_tasks;

import android.content.Context;
import android.util.Log;

import com.learnit.learnit.db_handlers.DbHandler;
import com.learnit.learnit.utils.Constants;

import java.io.IOException;

public class ExportTask extends MySmartAsyncTask<Void, Integer> {
    public final static int SUCCESS = 111;
    public final static int FAILURE = 222;

    protected String TAG = "export_task";

    public ExportTask(Context context,
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
            if (dbHandler.exportDB()) {
                return SUCCESS;
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "cannot export database");
            e.printStackTrace();
        }
        return FAILURE;
    }
}
