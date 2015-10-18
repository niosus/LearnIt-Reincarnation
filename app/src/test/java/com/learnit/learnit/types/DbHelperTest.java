/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.types;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.learnit.learnit.BuildConfig;
import com.learnit.learnit.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricGradleTestRunner.class)
public class DbHelperTest extends DbHelper {
    SQLiteDatabase database;

    public DbHelperTest() {
        super(null, null, null, 100, DbType.USER_DICT);
    }

    @Before
    public void setUp() throws Exception {
        Context context = RuntimeEnvironment.application;
        assertThat(context == null, is(false));
        if (context == null) {
            return;
        }
        String filePath;
        filePath = context.getPackageResourcePath() + "/src/test/res/sample.db";
        database = SQLiteDatabase.openDatabase(
                (new File(filePath)).getAbsolutePath(),
                null,
                SQLiteDatabase.OPEN_READONLY);
    }

    @Test
    public void testAddWord() {
        DbHelper helper = new DbHelper(RuntimeEnvironment.application, DbHelper.DB_USER_DICT, null, 1, DbType.USER_DICT);
        String wordToQuery = "test_word";
        String transToExpect = "test_trans";
        WordBundle bundle = new WordBundle();
        bundle.setWord(wordToQuery)
                .setTransFromString(transToExpect)
                .setWeight(0.9f);
        Constants.AddWordReturnCode returnCode = helper.addWord(bundle);
        assertThat(returnCode, is(Constants.AddWordReturnCode.SUCCESS));

        // test that the word is correctly inserted
        List<WordBundle> res = helper.queryWord(wordToQuery, Constants.QueryStyle.EXACT);
        assertThat(res.size(), is(1));
        WordBundle resultBundle = res.get(0);
        assertThat(resultBundle.weight(), is(bundle.weight()));
        assertThat(resultBundle.word(), is(bundle.word()));
        assertThat(resultBundle.transAsString(), is(bundle.transAsString()));
        assertThat(resultBundle.article(), is(bundle.article()));

        // test that we don't insert the word twice
        returnCode = helper.addWord(bundle);
        assertThat(returnCode, is(Constants.AddWordReturnCode.WORD_EXISTS));

        // test that we update similar words
        bundle.setTransFromString(transToExpect + WordBundle.TRANS_DIVIDER + "something new");
        returnCode = helper.addWord(bundle);
        assertThat(returnCode, is(Constants.AddWordReturnCode.WORD_UPDATED));
    }

    @Test
    public void testExactWrongReadFromDb() {
        // Check for failure
        // The word is written in lower case, while it is in upper case in the database.
        // The result should be null.
        String wordToQuery = "apfel";
        String queryRule = WORD_COLUMN_NAME + " = ?";
        String[] queryParams = new String[]{wordToQuery};
        List<WordBundle> res = queryFromDB("test_words", database, queryRule, queryParams);
        assertThat(res == null, is(true));
    }

    @Test
    public void testApproxEndingReadFromDb() {
        String wordToQuery = "gut";
        String queryRule = WORD_COLUMN_NAME + " like ?";
        String[] queryParams = new String[]{wordToQuery + "%"};
        List<WordBundle> res = queryFromDB("test_words", database, queryRule, queryParams);
        assertThat(res == null, is(false));
        assertThat(res.size(), is(3));
        assertThat(res.get(0).word(), is("Gutachter"));
        assertThat(res.get(1).word(), is("gut"));
        assertThat(res.get(2).word(), is("gutig"));
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
                .setWordType(WordBundle.WordType.ADVERB);
        String queryRule = WORD_COLUMN_NAME + " = ?";
        String[] queryParams = new String[]{wordToQuery};
        List<WordBundle> res = queryFromDB("test_words", database, queryRule, queryParams);
        assertThat(res == null, is(false));
        if (res == null) {
            return;
        }
        assertThat(res.size(), is(1));
        WordBundle resultBundle = res.get(0);
        assertThat(resultBundle, is(bundle));
        assertThat(resultBundle.wordType(), is(WordBundle.WordType.ADVERB));

        // and another word
        wordToQuery = "Apfel";
        transToExpect = "apple";
        bundle = new WordBundle();
        bundle.setId(1)
                .setWord(wordToQuery)
                .setTransFromString(transToExpect)
                .setArticle("der")
                .setWeight(0.5f)
                .setWordType(WordBundle.WordType.NOUN);
        queryRule = WORD_COLUMN_NAME + " = ?";
        queryParams = new String[]{wordToQuery};
        res = queryFromDB("test_words", database, queryRule, queryParams);
        assertThat(res == null, is(false));
        if (res == null) {
            return;
        }
        assertThat(res.size(), is(1));
        resultBundle = res.get(0);
        assertThat(resultBundle, is(bundle));
    }
}