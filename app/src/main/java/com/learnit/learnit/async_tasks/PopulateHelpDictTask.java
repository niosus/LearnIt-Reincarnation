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

public class PopulateHelpDictTask extends MySmartAsyncTask<String, Integer> {

    protected String TAG = "populate_dict";

    public PopulateHelpDictTask(Context context,
                                String data,
                                IAsyncTaskResultClient asyncEventHandler) {
        super(context, data, asyncEventHandler);
    }

    @Override
    public void execute() {
        super.execute(mInputData);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        // we expect exactly one string - name of the input file
        if (strings.length != 1) {
            return null;
        }

        DbHelper dbHelper = new DbHelper(mContext, DbHelper.DB_HELPER_DICT, null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + dbHelper.getDatabaseName()
                + " (" + DbHelper.HELPER_WORD_COLUMN_NAME + ", "
                + DbHelper.HELPER_MEANING_COLUMN_NAME + ")  VALUES (?, ?)";
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
                mAsyncEventHandler.onProgressUpdate(percent);

                if (isCancelled()) {
                    Log.d(Constants.LOG_TAG, "oh man, seems I am being killed... :( so sad...");
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error: cannot find dictionary file");
            return -1;
        }
        if (!isCancelled()) {
            db.setTransactionSuccessful();
        }
        db.endTransaction();
        db.close();
        return 0;
    }
}
