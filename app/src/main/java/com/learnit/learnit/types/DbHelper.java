package com.learnit.learnit.types;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.learnit.learnit.interfaces.IDatabaseInteractions;
import com.learnit.learnit.utils.Constants;

public class DbHelper extends SQLiteOpenHelper
    implements IDatabaseInteractions {
    // name of the database that stores the user-defined words.
    // used for learning new words.
    final public static String DB_USER_DICT = "user_dict";

    // database that stores the data generated from a star-dict dictionary.
    final public static String DB_STAR_DICT = "star_dict";

    // names of DB_USER_DICT database fields
    final public String WORD_COLUMN_NAME = "word";
    final public String ID_COLUMN_NAME = "id";
    final public String ARTICLE_COLUMN_NAME = "article";
    final public String WEIGHT_COLUMN_NAME = "weight";
    final public String PREFIX_COLUMN_NAME = "prefix";
    final public String TRANSLATION_COLUMN_NAME = "translation";

    // names of DB_STAR_DICT database fields
    final public String DICT_OFFSET_COLUMN_NAME = "start_offset";
    final public String DICT_CHUNK_SIZE_COLUMN_NAME = "end_offset";


    public DbHelper(Context context, String name,
                    SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(Constants.LOG_TAG, "--- onCreate database " + getDatabaseName() + "---");
        switch (getDatabaseName()) {
            case DB_USER_DICT:
                sqLiteDatabase.execSQL("CREATE TABLE " + getDatabaseName() + " ("
                        + ID_COLUMN_NAME + " INTEGER primary key autoincrement,"
                        + ARTICLE_COLUMN_NAME + " TEXT,"
                        + WORD_COLUMN_NAME + " TEXT,"
                        + TRANSLATION_COLUMN_NAME + " TEXT,"
                        + WEIGHT_COLUMN_NAME + " REAL,"
                        + PREFIX_COLUMN_NAME + " TEXT" + ");");
                break;
            case DB_STAR_DICT:
                sqLiteDatabase.execSQL("CREATE TABLE " + getDatabaseName() + " ("
                        + ID_COLUMN_NAME + " INTEGER primary key autoincrement,"
                        + DICT_OFFSET_COLUMN_NAME + " LONG,"
                        + DICT_CHUNK_SIZE_COLUMN_NAME + " LONG,"
                        + WORD_COLUMN_NAME + " TEXT" + ");");
                break;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    @Override
    public void addWord(WordBundle wordBundle) {
        Log.d(Constants.LOG_TAG, "DbHelper: adding word '" + wordBundle.word() + "'");
        ContentValues cv = new ContentValues();
        cv.put(ARTICLE_COLUMN_NAME, wordBundle.article());
        cv.put(PREFIX_COLUMN_NAME, wordBundle.prefix());
        cv.put(WORD_COLUMN_NAME, wordBundle.word());
        cv.put(TRANSLATION_COLUMN_NAME, wordBundle.transAsString());
        cv.put(WEIGHT_COLUMN_NAME, wordBundle.weight());

        // TODO: check if the word is present in the database
        // if it is - either do nothing or update
        // if it is not - add it
    }

    @Override
    public WordBundle queryWord(String word) {
        // TODO: fetch the full bundle of the @param{word} from database
        return null;
    }
}
