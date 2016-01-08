package com.learnit.learnit.db_handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DbHelperDictHandler extends DbHandler {
    protected DbHelperDictHandler(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory,
                                  int version) {
        super(context, name, factory, version);
    }

    protected ContentValues contentValuesFromWordBundle(final WordBundle wordBundle) {
        ContentValues cv = new ContentValues();
        cv.put(WORD_COLUMN_NAME, wordBundle.word());
        cv.put(TRANSLATION_COLUMN_NAME, wordBundle.transAsString());
        return cv;
    }

    protected void executeDbCreation(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + getDatabaseName() + " ("
                + ID_COLUMN_NAME + " INTEGER primary key autoincrement,"
                + WORD_COLUMN_NAME + " TEXT,"
                + TRANSLATION_COLUMN_NAME + " TEXT" + ");");
    }

    @Override
    public List<WordBundle> queryWord(String word, Constants.QueryStyle queryStyle, Integer limit) {
        String limitStr = (limit == null) ? "" : String.format(" limit %s ", limit);
        String matchingRule;
        String[] matchingParams;
        switch (queryStyle) {
            case EXACT:
                matchingRule = " = ? ";
                matchingParams = new String[]{word};
                break;
            case APPROXIMATE_ENDING:
                matchingRule = " like ? ";
                matchingParams = new String[]{word + "%"};
                break;
            case APPROXIMATE_ALL:
                matchingRule = " like ? ";
                matchingParams = new String[]{"%" + word + "%"};
                break;
            default:
                return null;
        }
        return queryFromDB(
                getDatabaseName(),
                getReadableDatabase(),
                WORD_COLUMN_NAME + matchingRule + limitStr,
                matchingParams);
    }

    protected List<WordBundle> queryFromDB(final String dbName,
                                           final SQLiteDatabase db,
                                           final String matchingRule,
                                           final String[] matchingParams) {
        Cursor cursor = db.query(dbName, ALL_COLUMNS_HELP_DICT, matchingRule, matchingParams,
                null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        } else {
            ArrayList<WordBundle> wordBundles = new ArrayList<>();
            do {
                wordBundles.add(wordBundleFromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
            return wordBundles;
        }
    }

    protected WordBundle wordBundleFromCursor(final Cursor cursor) {
        // TODO: we need to check what dictionary are we using to set the correct parse style
        return new WordBundle.Constructor().setWord(cursor.getString(cursor.getColumnIndex(WORD_COLUMN_NAME)))
                .parseTrans(cursor.getString(cursor.getColumnIndex(TRANSLATION_COLUMN_NAME)), WordBundle.ParseStyle.BABYLON)
                .setId(cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME))).construct();
    }
}