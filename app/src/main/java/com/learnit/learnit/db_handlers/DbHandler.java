package com.learnit.learnit.db_handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.learnit.learnit.interfaces.IDatabaseInteractions;
import com.learnit.learnit.types.LanguagePair;
import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

import java.util.List;

public abstract class DbHandler extends SQLiteOpenHelper
    implements IDatabaseInteractions {
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

    final public static String[] ALL_COLUMNS_HELP_DICT = new String[] {
            ID_COLUMN_NAME,
            WORD_COLUMN_NAME,
            TRANSLATION_COLUMN_NAME
    };

    protected DbHandler(Context context, String name,
                        SQLiteDatabase.CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    public static class Factory {
        public static DbHandler createLocalizedHelper(Context context, String dbName) {
            LanguagePair.Tags langTags = Utils.getCurrentLanguageTags(context);
            String localizedDbName = String.format("%s_%s_%s",
                    dbName, langTags.langToLearnTag(), langTags.langYouKnowTag());
            switch (dbName) {
                case DB_USER_DICT:
                    return new DbUserDictHandler(context, localizedDbName, null, 1);
                case DB_HELPER_DICT:
                    return new DbHelperDictHandler(context, localizedDbName, null, 1);
                default:
                    Log.e(Constants.LOG_TAG, "Trying to create unspecified database. Failure.");
                    return null;
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(Constants.LOG_TAG, "--- onCreate database " + getDatabaseName() + "---");
        executeDbCreation(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    @Override
    public Constants.AddWordReturnCode addWord(WordBundle wordBundle) {
        Log.d(Constants.LOG_TAG, "DbHandler: adding word '" + wordBundle.word() + "'");
        ContentValues cv = contentValuesFromWordBundle(wordBundle);
        List<WordBundle> nowInDb = this.queryWord(wordBundle.word(), Constants.QueryStyle.EXACT);
        if (nowInDb == null || nowInDb.isEmpty()) {
            // there is no such word in the database
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(this.getDatabaseName(), null, cv);
            db.close();
            return Constants.AddWordReturnCode.SUCCESS;
        }
        if (nowInDb.contains(wordBundle)) {
            return Constants.AddWordReturnCode.WORD_EXISTS;
        }
        // if we reach this place - there is something similar in the db, but not the same.
        // Requires investigation and update.
        // TODO: actually implement the logic behind the word updated
        int differenceCounter = 0;
        for (WordBundle inDbBundle: nowInDb) {
            if (inDbBundle.wordType() == wordBundle.wordType()) {
                Log.e(Constants.LOG_TAG, "reached unimplemented behaviour. Please decide how to update the words.");
                return Constants.AddWordReturnCode.WORD_UPDATED;
            } else {
                differenceCounter++;
            }
        }
        if (differenceCounter == nowInDb.size()) {
            // there were no bundles that are of the same type as the query
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(this.getDatabaseName(), null, cv);
            db.close();
            return Constants.AddWordReturnCode.SUCCESS;
        }
        Log.e(Constants.LOG_TAG, "something unpredicted happened, so we have just failed. Congrats.");
        return Constants.AddWordReturnCode.FAILURE;
    }

    @Override
    public void deleteWord(final WordBundle wordBundle) {
        Log.d(Constants.LOG_TAG, this.getClass().getSimpleName() + " id of deleting = " + wordBundle.id());
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(this.getDatabaseName(),
                ID_COLUMN_NAME + "= ?", new String[]{Integer.toString(wordBundle.id())});
        db.close();

        // TODO: when we will have notifications someone has to know that one needs to be removed
//        // if this word is currently shown - remove it from notifications
//        NotificationManager mNotificationManager
//                = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.cancel(id + NotificationBuilder.mIdStartingValue);
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

    abstract protected ContentValues contentValuesFromWordBundle(final WordBundle wordBundle);

    abstract protected void executeDbCreation(SQLiteDatabase sqLiteDatabase);

    abstract protected List<WordBundle> queryFromDB(final String dbName,
                                           final SQLiteDatabase db,
                                           final String matchingRule,
                                           final String[] matchingParams);

    @Override
    public List<WordBundle> queryWord(final String word, final Constants.QueryStyle queryStyle) {
        return queryWord(word, queryStyle, null);
    }

    @Override
    public List<WordBundle> queryRandomWords(final Integer limit) {
        // TODO: get all ids and weights and then pick 'limit' random words according to weights
        // query the words by id after we have a list of ids
        return queryWord(null, Constants.QueryStyle.RANDOM, limit);
    }

    abstract protected WordBundle wordBundleFromCursor(final Cursor cursor);
}
