package com.learnit.learnit.db_handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DbHandlerDictHandler extends DbHandler {
    protected DbHandlerDictHandler(Context context, String name,
                                   SQLiteDatabase.CursorFactory factory,
                                   int version) {
        super(context, name, factory, version);
    }

    protected ContentValues contentValuesFromWordBundle(final WordBundle wordBundle) {
        ContentValues cv = new ContentValues();
        cv.put(HELPER_WORD_COLUMN_NAME, wordBundle.word());
        cv.put(HELPER_MEANING_COLUMN_NAME, wordBundle.transAsString());
        return cv;
    }

    protected void executeDbCreation(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + getDatabaseName() + " ("
                + HELPER_ID_COLUMN_NAME + " INTEGER primary key autoincrement,"
                + HELPER_WORD_COLUMN_NAME + " TEXT,"
                + HELPER_MEANING_COLUMN_NAME + " TEXT" + ");");
    }

    @Override
    public List<WordBundle> queryWord(final String word, final Constants.QueryStyle queryStyle) {
        String matchingRule = null;
        String[] matchingParams = null;
        switch (queryStyle) {
            case EXACT:
                matchingRule = HELPER_WORD_COLUMN_NAME + " = ? ";
                matchingParams = new String[]{word};
                break;
            case APPROXIMATE_ENDING:
                matchingRule = HELPER_WORD_COLUMN_NAME + " like ? ";
                matchingParams = new String[]{word + "%"};
                break;
            case APPROXIMATE_ALL:
                matchingRule = HELPER_WORD_COLUMN_NAME + " like ? ";
                matchingParams = new String[]{"%" + word + "%"};
                break;
            default:
                return null;
        }
        return queryFromDB(getDatabaseName(), getReadableDatabase(), matchingRule, matchingParams);
    }

    protected List<WordBundle> queryFromDB(final String dbName,
                                           final SQLiteDatabase db,
                                           final String matchingRule,
                                           final String[] matchingParams) {
        Cursor cursor = db.query(dbName, ALL_COLUMNS_HELP_DICT, matchingRule, matchingParams,
                null, null, null);
        if (!cursor.moveToFirst()) {
            return null;
        } else {
            ArrayList<WordBundle> wordBundles = new ArrayList<>();
            do {
                wordBundles.add(wordBundleFromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
            return wordBundles;
        }
    }

    protected WordBundle wordBundleFromCursor(final Cursor cursor) {
        WordBundle wordBundle = new WordBundle();
        wordBundle.setWord(cursor.getString(cursor.getColumnIndex(HELPER_WORD_COLUMN_NAME)))
                .setTransFromString(cursor.getString(cursor.getColumnIndex(HELPER_MEANING_COLUMN_NAME)))
                .setId(cursor.getInt(cursor.getColumnIndex(HELPER_ID_COLUMN_NAME)));
        return wordBundle;
    }
}