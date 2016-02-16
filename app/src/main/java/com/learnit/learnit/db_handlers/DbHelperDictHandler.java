package com.learnit.learnit.db_handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;

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
    public List<WordBundle> queryWord(String query, Constants.QueryStyle queryStyle, Integer limit) {
        String limitStr = (limit == null) ? null : String.valueOf(limit);
        SqlMatcher matcher = getMatcherForQuery(query, queryStyle);
        Cursor c = queryFromDB(
                getReadableDatabase(),
                getDatabaseName(),
                ALL_COLUMNS_HELP_DICT,
                matcher.rule(),
                matcher.params(),
                limitStr);
        return bundlesFromCursor(c, getReadableDatabase());
    }

    protected WordBundle wordBundleFromCursor(final Cursor cursor) {
        // TODO: we need to check what dictionary are we using to set the correct parse style
        return new WordBundle.Constructor(mContext)
                .setWord(cursor.getString(cursor.getColumnIndex(WORD_COLUMN_NAME)))
                .parseTrans(
                        cursor.getString(cursor.getColumnIndex(TRANSLATION_COLUMN_NAME)),
                        WordBundle.ParseStyle.BABYLON)
                .setId(cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME)))
                .construct();
    }
}