package com.learnit.learnit.db_handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DbUserDictHandler extends DbHandler {
    protected DbUserDictHandler(Context context, String name,
                               SQLiteDatabase.CursorFactory factory,
                               int version) {
        super(context, name, factory, version);
    }

    protected ContentValues contentValuesFromWordBundle(final WordBundle wordBundle) {
        ContentValues cv = new ContentValues();
        cv.put(ARTICLE_COLUMN_NAME, wordBundle.article());
        cv.put(PREFIX_COLUMN_NAME, wordBundle.prefix());
        cv.put(WORD_COLUMN_NAME, wordBundle.word());
        cv.put(TRANSLATION_COLUMN_NAME, wordBundle.transAsString());
        cv.put(WEIGHT_COLUMN_NAME, wordBundle.weight());
        cv.put(WORD_TYPE_COLUMN_NAME, wordBundle.wordType());
        return cv;
    }

    protected void executeDbCreation(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + getDatabaseName() + " ("
                + ID_COLUMN_NAME + " INTEGER primary key autoincrement,"
                + ARTICLE_COLUMN_NAME + " TEXT,"
                + WORD_COLUMN_NAME + " TEXT,"
                + TRANSLATION_COLUMN_NAME + " TEXT,"
                + WEIGHT_COLUMN_NAME + " REAL,"
                + PREFIX_COLUMN_NAME + " TEXT,"
                + WORD_TYPE_COLUMN_NAME + " INTEGER" + ");");
    }

    @Override
    public List<WordBundle> queryWord(String word, Constants.QueryStyle queryStyle, Integer limit) {
        String additionalParams = (limit == null) ? "" : String.format(" limit %s ", limit);
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
            case RANDOM:
                // TODO: a bit hacky, but will do for now. It will be weird if the word is not going to be null
                matchingRule = " is not null";
                matchingParams = null;
                additionalParams = " order by random() " + additionalParams;
                break;
            default:
                return null;
        }
        return queryFromDB(
                getDatabaseName(),
                getReadableDatabase(),
                WORD_COLUMN_NAME + matchingRule + additionalParams,
                matchingParams);
    }

    protected List<WordBundle> queryFromDB(final String dbName,
                                                    final SQLiteDatabase db,
                                                    final String matchingRule,
                                                    final String[] matchingParams) {
        Cursor cursor = db.query(dbName, ALL_COLUMNS_USER, matchingRule, matchingParams,
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
        WordBundle wordBundle;
        wordBundle = new WordBundle.Constructor().setWord(cursor.getString(cursor.getColumnIndex(WORD_COLUMN_NAME)))
                .setTrans(cursor.getString(cursor.getColumnIndex(TRANSLATION_COLUMN_NAME)))
                .setArticle(cursor.getString(cursor.getColumnIndex(ARTICLE_COLUMN_NAME)))
                .setPrefix(cursor.getString(cursor.getColumnIndex(PREFIX_COLUMN_NAME)))
                .setWeight(cursor.getFloat(cursor.getColumnIndex(WEIGHT_COLUMN_NAME)))
                .setId(cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME)))
                .setWordType(cursor.getInt(cursor.getColumnIndex(WORD_TYPE_COLUMN_NAME)))
                .construct();
        return wordBundle;
    }
}