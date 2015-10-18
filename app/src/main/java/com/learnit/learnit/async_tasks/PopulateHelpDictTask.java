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

import com.learnit.learnit.db_handlers.DbHandler;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PopulateHelpDictTask extends MySmartAsyncTask<String, Integer> {

    protected String TAG = "populate_dict";
    public static final Integer SUCCESS = 0;
    public static final Integer FAILURE = -1;

    public PopulateHelpDictTask(Context context,
                                String data) {
        super(context, data);
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
    protected Integer doInBackground(String... strings) {
        // we expect exactly one string - name of the input file
        if (strings.length != 1) {
            return FAILURE;
        }

        DbHandler dbHandler = DbHandler.Factory.createLocalizedHelper(mContext, DbHandler.DB_HELPER_DICT);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String sql = "INSERT INTO " + dbHandler.getDatabaseName()
                + " (" + DbHandler.HELPER_WORD_COLUMN_NAME + ", "
                + DbHandler.HELPER_MEANING_COLUMN_NAME + ")  VALUES (?, ?)";
        SQLiteStatement stmt = db.compileStatement(sql);
        db.beginTransaction();

        String filePath = strings[0];
        File file = new File(filePath);
        long fullLength = file.length();
        long currentLength = 0;
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);

            String line;

            while ((line = br.readLine()) != null) {
                String[] split = StringUtils.splitWordFromMeaning(line);
                // split will be null if string format is wrong
                if (split != null) {
                    stmt.bindString(1, split[0]);
                    stmt.bindString(2, split[1]);
                    stmt.execute();
                    stmt.clearBindings();
                }

                currentLength += line.getBytes().length;
                Float percent = 100f * ((float) currentLength / (float) fullLength);
                mAsyncTaskResultClient.onProgressUpdate(percent);

                if (isCancelled()) {
                    Log.d(Constants.LOG_TAG, "oh man, seems I am being killed... :( so sad...");
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error: cannot find dictionary file");
            return FAILURE;
        }
        if (!isCancelled()) {
            db.setTransactionSuccessful();
        }
        db.endTransaction();
        db.close();
        return SUCCESS;
    }
}
