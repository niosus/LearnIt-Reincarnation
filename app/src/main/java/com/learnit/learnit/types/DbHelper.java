package com.learnit.learnit.types;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.learnit.learnit.interfaces.IDatabaseInteractions;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper
    implements IDatabaseInteractions {
    enum DbType {
        HELPER_DICT,
        USER_DICT
    };

    private DbType mDbType;

    // name of the database that stores the user-defined words used for learning new words.
    final public static String DB_USER_DICT = "user_dict";
    // database that stores the data generated from a dictionary.
    final public static String DB_HELPER_DICT = "help_dict";
    // names of DB_USER_DICT database fields
    final public static String WORD_COLUMN_NAME = "word";
    final public static String ID_COLUMN_NAME = "id";
    final public static String ARTICLE_COLUMN_NAME = "article";
    final public static String WEIGHT_COLUMN_NAME = "weight";
    final public static String PREFIX_COLUMN_NAME = "prefix";
    final public static String TRANSLATION_COLUMN_NAME = "translation";
    final public static String WORD_TYPE_COLUMN_NAME = "word_type";
    final public static String[] ALL_COLUMNS_USER = new String[] {
            ID_COLUMN_NAME,
            WORD_COLUMN_NAME,
            TRANSLATION_COLUMN_NAME,
            ARTICLE_COLUMN_NAME,
            PREFIX_COLUMN_NAME,
            WEIGHT_COLUMN_NAME,
            WORD_TYPE_COLUMN_NAME
    };

    // names of DB_HELPER_DICT database fields
    final public static String HELPER_ID_COLUMN_NAME = "s_id";
    final public static String HELPER_WORD_COLUMN_NAME = "wname";
    final public static String HELPER_MEANING_COLUMN_NAME = "wmean";
    final public static String[] ALL_COLUMNS_HELP_DICT = new String[] {
            HELPER_ID_COLUMN_NAME,
            HELPER_WORD_COLUMN_NAME,
            HELPER_MEANING_COLUMN_NAME
    };

    protected DbHelper(Context context, String name,
                    SQLiteDatabase.CursorFactory factory,
                    int version, DbType type) {
        super(context, name, factory, version);
        mDbType = type;
    }

    public static class Factory {
        public static DbHelper createLocalizedHelper(Context context, String dbName) {
            DbType type = null;
            if (dbName.equals(DB_USER_DICT)) {
                type = DbType.USER_DICT;
            } else if (dbName.equals(DB_HELPER_DICT)) {
                type = DbType.HELPER_DICT;
            } else {
                Log.e(Constants.LOG_TAG, "Trying to create unspecified database. Failure.");
                return null;
            }
            LanguagePair.LangTags langTags = Utils.getCurrentLanguageTags(context);
            String localizedDbName = String.format("%s_%s_%s",
                    dbName, langTags.langToLearnTag(), langTags.langYouKnowTag());
            return new DbHelper(context, localizedDbName, null, 1, type);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(Constants.LOG_TAG, "--- onCreate database " + getDatabaseName() + "---");
        if (DB_HELPER_DICT.length() != DB_USER_DICT.length()) {
            Log.e(Constants.LOG_TAG, "Current implementation requires that DB_HELPER_DICT and DB_USER_DICT have same lengths. Cannot create database. Doing nothing.");
            return;
        }
        int commonLength = DB_HELPER_DICT.length();
        switch (getDatabaseName().substring(0, commonLength)) {
            case DB_USER_DICT:
                sqLiteDatabase.execSQL("CREATE TABLE " + getDatabaseName() + " ("
                        + ID_COLUMN_NAME + " INTEGER primary key autoincrement,"
                        + ARTICLE_COLUMN_NAME + " TEXT,"
                        + WORD_COLUMN_NAME + " TEXT,"
                        + TRANSLATION_COLUMN_NAME + " TEXT,"
                        + WEIGHT_COLUMN_NAME + " REAL,"
                        + PREFIX_COLUMN_NAME + " TEXT,"
                        + WORD_TYPE_COLUMN_NAME + " INTEGER" + ");");
                break;
            case DB_HELPER_DICT:
                sqLiteDatabase.execSQL("CREATE TABLE " + getDatabaseName() + " ("
                        + HELPER_ID_COLUMN_NAME + " INTEGER primary key autoincrement,"
                        + HELPER_WORD_COLUMN_NAME + " TEXT,"
                        + HELPER_MEANING_COLUMN_NAME + " TEXT" + ");");
                break;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    @Override
    public Constants.AddWordReturnCode addWord(WordBundle wordBundle) {
        Log.d(Constants.LOG_TAG, "DbHelper: adding word '" + wordBundle.word() + "'");
        ContentValues cv = new ContentValues();
        switch (mDbType) {
            case USER_DICT:
                cv.put(ARTICLE_COLUMN_NAME, wordBundle.article());
                cv.put(PREFIX_COLUMN_NAME, wordBundle.prefix());
                cv.put(WORD_COLUMN_NAME, wordBundle.word());
                cv.put(TRANSLATION_COLUMN_NAME, wordBundle.transAsString());
                cv.put(WEIGHT_COLUMN_NAME, wordBundle.weight());
                cv.put(WORD_TYPE_COLUMN_NAME, wordBundle.wordType());
                break;
            case HELPER_DICT:
                cv.put(HELPER_WORD_COLUMN_NAME, wordBundle.word());
                cv.put(HELPER_MEANING_COLUMN_NAME, wordBundle.transAsString());
                break;
            default:
                Log.e(Constants.LOG_TAG, "unknown type of DbHelper dict provided. Failure.");
                return Constants.AddWordReturnCode.FAILURE;
        }
        List<WordBundle> nowInDb = this.queryWord(wordBundle.word(), Constants.QueryStyle.EXACT);
        if (nowInDb == null || nowInDb.isEmpty()) {
            // there is no such word in the database
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(this.getDatabaseName(), null, cv);
            return Constants.AddWordReturnCode.SUCCESS;
        }
        if (nowInDb.contains(wordBundle)) {
            return Constants.AddWordReturnCode.WORD_EXISTS;
        }
        // if we reach this place - there is something similar in the db, but not the same.
        // Requires investigation and update.
        // TODO: actually implement the logic behind the word updated
        Log.e(Constants.LOG_TAG, "reached unimplemented behaviour. Please decide how to update the words.");
        return Constants.AddWordReturnCode.WORD_UPDATED;
    }

    @Override
    public List<WordBundle> queryWord(final String word, final Constants.QueryStyle queryStyle) {
        String matchingRule = null;
        String[] matchingParams = null;
        String currentWordColumnName = null;
        String currentTransColumnName = null;
        switch (mDbType) {
            case USER_DICT:
                currentWordColumnName = WORD_COLUMN_NAME;
                currentTransColumnName= TRANSLATION_COLUMN_NAME;
                break;
            case HELPER_DICT:
                currentWordColumnName = HELPER_WORD_COLUMN_NAME;
                currentTransColumnName= HELPER_MEANING_COLUMN_NAME;
                break;
            default:
                Log.e(Constants.LOG_TAG, "unknown type of DbHelper dict provided. Failure.");
                return null;
        }
        switch (queryStyle) {
            case EXACT:
                matchingRule = currentWordColumnName + " = ? ";
                matchingParams = new String[]{word};
                break;
            case APPROXIMATE_ENDING:
                matchingRule = currentWordColumnName + " like ? ";
                matchingParams = new String[]{word + "%"};
                break;
            case APPROXIMATE_ALL:
                matchingRule = currentWordColumnName + " like ? ";
                matchingParams = new String[]{"%" + word + "%"};
                break;
            default:
                return null;
        }
        return queryFromDB(getDatabaseName(), getReadableDatabase(), matchingRule, matchingParams);
    }

    @Override
    public void deleteDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null) {
            return;
        }
        try {
            db.delete(getDatabaseName(), null, null);
            db.close();
        } catch (SQLiteException e) {
            // cannot delete. Usually means the database is not there yet.
            // do nothing.
            Log.e(Constants.LOG_TAG, "database cannot be deleted.\n" + e.getMessage());
            db.close();
        }
    }

    protected List<WordBundle> queryFromDB(final String dbName,
                                           final SQLiteDatabase db,
                                           final String matchingRule,
                                           final String[] matchingParams) {
        String[] allColumnsNames = null;
        switch (mDbType) {
            case USER_DICT:
                allColumnsNames = ALL_COLUMNS_USER;
                break;
            case HELPER_DICT:
                allColumnsNames = ALL_COLUMNS_HELP_DICT;
                break;
            default:
                Log.e(Constants.LOG_TAG, "unknown type of DbHelper dict provided. Failure.");
                return null;
        }
        Cursor cursor = db.query(dbName, allColumnsNames, matchingRule, matchingParams,
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

    private WordBundle wordBundleFromCursor(final Cursor cursor) {
        WordBundle wordBundle = new WordBundle();
        switch (mDbType) {
            case USER_DICT:
                wordBundle.setWord(cursor.getString(cursor.getColumnIndex(WORD_COLUMN_NAME)))
                        .setTransFromString(cursor.getString(cursor.getColumnIndex(TRANSLATION_COLUMN_NAME)))
                        .setArticle(cursor.getString(cursor.getColumnIndex(ARTICLE_COLUMN_NAME)))
                        .setPrefix(cursor.getString(cursor.getColumnIndex(PREFIX_COLUMN_NAME)))
                        .setWeight(cursor.getFloat(cursor.getColumnIndex(WEIGHT_COLUMN_NAME)))
                        .setId(cursor.getInt(cursor.getColumnIndex(ID_COLUMN_NAME)))
                        .setWordType(cursor.getInt(cursor.getColumnIndex(WORD_TYPE_COLUMN_NAME)));
                break;
            case HELPER_DICT:
                wordBundle.setWord(cursor.getString(cursor.getColumnIndex(HELPER_WORD_COLUMN_NAME)))
                        .setTransFromString(cursor.getString(cursor.getColumnIndex(HELPER_MEANING_COLUMN_NAME)))
                        .setId(cursor.getInt(cursor.getColumnIndex(HELPER_ID_COLUMN_NAME)));
                break;
            default:
                Log.e(Constants.LOG_TAG, "unknown type of DbHelper dict provided. Failure.");
                return null;
        }
        return wordBundle;
    }
}
