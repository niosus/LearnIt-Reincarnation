/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.db_handlers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.learnit.learnit.BuildConfig;
import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;

import junit.framework.Assert;

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

@Config(manifest = "src/main/AndroidManifest.xml", sdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricGradleTestRunner.class)
public class DbHandlerTest extends DbUserDictHandler {
    SQLiteDatabase database;

    public DbHandlerTest() {
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
        filePath = context.getPackageResourcePath() + "/src/test/res/sample.db";
        database = SQLiteDatabase.openDatabase(
                (new File(filePath)).getAbsolutePath(),
                null,
                SQLiteDatabase.OPEN_READONLY);
    }

    @Test
    public void testAddWord() {
        DbHandler helper = new DbUserDictHandler(
                RuntimeEnvironment.application,
                DbHandler.DB_USER_DICT,
                null, 1);
        String wordToQuery = "test_word";
        String transToExpect = "test_trans";
        WordBundle bundle = new WordBundle.Constructor()
                .setWord(wordToQuery)
                .setTrans(transToExpect)
                .setWeight(0.9f)
                .construct();
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
        // TODO: have a more sophisticated test for this
        bundle.setTrans(transToExpect + WordBundle.TRANS_DIVIDER + "something new");
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
        WordBundle bundle = new WordBundle.Constructor()
                .setId(2)
                .setWord(wordToQuery)
                .setTrans(transToExpect)
                .setWeight(0.5f)
                .setWordType(WordBundle.WordType.ADVERB)
                .construct();
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
        bundle = new WordBundle.Constructor()
                .setId(1)
                .setWord(wordToQuery)
                .setTrans(transToExpect)
                .setArticle("der")
                .setWeight(0.5f)
                .setWordType(WordBundle.WordType.NOUN)
                .construct();
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

    @Test
    public void testDeleteFromDb() {
        DbHandler helper = new DbUserDictHandler(
                RuntimeEnvironment.application,
                DbHandler.DB_USER_DICT,
                null, 1);
        String wordToQuery = "test_word";
        String transToExpect = "test_trans";
        WordBundle bundle = new WordBundle.Constructor()
                .setWord(wordToQuery)
                .setTrans(transToExpect)
                .setWeight(0.9f)
                .setId(666)
                .construct();
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
        bundle.setTrans(transToExpect + WordBundle.TRANS_DIVIDER + "something new");
        returnCode = helper.addWord(bundle);
        assertThat(returnCode, is(Constants.AddWordReturnCode.WORD_UPDATED));

        bundle.setId(resultBundle.id());
        helper.deleteWord(bundle);
        returnCode = helper.addWord(bundle);
        assertThat(returnCode, is(Constants.AddWordReturnCode.SUCCESS));
    }

    @Test
    public void testGetRandom() {
        DbHandler helper = new DbUserDictHandler(
                RuntimeEnvironment.application,
                DbHandler.DB_USER_DICT,
                null, 1);
        String transToExpect = "test_trans";
        String word1 = "test1";
        String word2 = "test2";
        float weight1 = 0.9f;
        float weight2 = 0.1f;
        helper.addWord(new WordBundle.Constructor()
                .setWord(word1)
                .setTrans(transToExpect)
                .setWeight(weight1)
                .setId(666)
                .construct());
        helper.addWord(new WordBundle.Constructor()
                .setWord(word2)
                .setTrans(transToExpect)
                .setWeight(weight2)
                .setId(666)
                .construct());

        float counter1 = 0.0f;
        float counter2 = 0.0f;
        for (int i = 0; i < 1000; i++) {
            List<WordBundle> res = helper.queryRandomWords(2);
            assertThat(res.size(), is(2));
            WordBundle resultBundle = res.get(0);
            if (resultBundle.word().equals(word1)) {
                counter1++;
            } else if (resultBundle.word().equals(word2)) {
                counter2++;
            }
        }

        // ensures the data is approximately shown with 50/50 chance
        float epsilon = 0.3f;
        Assert.assertEquals(1.0f, counter1 / counter2, epsilon);
    }
}