/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.types;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.learnit.learnit.BuildConfig;
import com.learnit.learnit.CustomRobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
@RunWith(CustomRobolectricTestRunner.class)
public class DbHelperTest extends DbHelper {
    SQLiteDatabase database;

    public DbHelperTest() {
        super(null, null, null, 100);
    }

    @Before
    public void setUp() throws Exception {
        Context context = RuntimeEnvironment.application;
        assertThat(context == null, is(false));
        if (context == null) {
            return;
        }
        String filePath;
        if (context.getPackageResourcePath().contains("test")) {
            // means that we run from android studio
            filePath = context.getPackageResourcePath() + "/res/sample.db";
        } else {
            // we run from command line
            filePath = context.getPackageResourcePath() + "/src/test/res/sample.db";
        }
        database = SQLiteDatabase.openDatabase(
                (new File(filePath)).getAbsolutePath(),
                null,
                SQLiteDatabase.OPEN_READONLY);
    }

    @Test
    public void testAddWord() {
        DbHelper helper = new DbHelper(RuntimeEnvironment.application, DbHelper.DB_USER_DICT, null, 1);
        String wordToQuery = "test_word";
        String transToExpect = "test_trans";
        WordBundle bundle = new WordBundle();
        bundle.setWord(wordToQuery)
                .setTransFromString(transToExpect)
                .setWeight(0.9f);
        DbHelper.AddWordReturnCode returnCode = helper.addWord(bundle);
        assertThat(returnCode, is(AddWordReturnCode.SUCCESS));

        // test that the word is correctly inserted
        List<WordBundle> res = helper.queryWord(wordToQuery);
        assertThat(res.size(), is(1));
        WordBundle resultBundle = res.get(0);
        assertThat(resultBundle.weight(), is(bundle.weight()));
        assertThat(resultBundle.word(), is(bundle.word()));
        assertThat(resultBundle.transAsString(), is(bundle.transAsString()));
        assertThat(resultBundle.article(), is(bundle.article()));

        // test that we don't insert the word twice
        returnCode = helper.addWord(bundle);
        assertThat(returnCode, is(AddWordReturnCode.WORD_EXISTS));

        // test that we update similar words
        bundle.setTransFromString(transToExpect + WordBundle.TRANS_DIVIDER + "something new");
        returnCode = helper.addWord(bundle);
        assertThat(returnCode, is(AddWordReturnCode.WORD_UPDATED));
    }

    @Test
    public void testExactWrongReadFromDb() {
        // Check for failure
        // The word is written in lower case, while it is in upper case in the database.
        // The result should be null.
        String wordToQuery = "apfel";
        List<WordBundle> res = queryFromDB(wordToQuery, "test_words", database);
        assertThat(res == null, is(true));
    }

    @Test
    public void testExactReadFromDb() {
        assertThat(database == null, is(false));
        String wordToQuery = "fallen";
        String transToExpect = "fall";
        WordBundle bundle = new WordBundle();
        bundle.setId(2)
                .setWord(wordToQuery)
                .setTransFromString(transToExpect)
                .setWeight(0.5f)
                .setWordType(WordBundle.WordType.VERB);
        List<WordBundle> res = queryFromDB(wordToQuery, "test_words", database);
        assertThat(res == null, is(false));
        if (res == null) {
            return;
        }
        assertThat(res.size(), is(1));
        WordBundle resultBundle = res.get(0);
        assertThat(resultBundle, is(bundle));
        assertThat(resultBundle.wordType(), is(WordBundle.WordType.VERB));

        // and another word
        wordToQuery = "Apfel";
        transToExpect = "apple";
        bundle = new WordBundle();
        bundle.setId(1)
                .setWord(wordToQuery)
                .setTransFromString(transToExpect)
                .setArticle("der")
                .setWeight(0.5f);
        res = queryFromDB(wordToQuery, "test_words", database);
        assertThat(res == null, is(false));
        if (res == null) {
            return;
        }
        assertThat(res.size(), is(1));
        resultBundle = res.get(0);
        assertThat(resultBundle, is(bundle));
    }
}