/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.async_tasks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.types.DbHelper;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DummyTask extends MySmartAsyncTask<Integer, Integer> {

    protected String TAG = "dummy_task";

    public DummyTask(Context context,
                     Integer numOfChunks) {
        super(context, numOfChunks);
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
    protected Integer doInBackground(Integer... numOfChunks) {
        // we expect exactly one integer - number of times we sleep
        if (numOfChunks.length != 1) {
            return null;
        }
        try {
            for (int i = 0; i < numOfChunks[0]; i++) {
                Thread.sleep(100);
                float percent = (i * 100) / (float) numOfChunks[0];
                mAsyncTaskResultClient.onProgressUpdate(percent);
                if (isCancelled()) {
                    return -1;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
