package com.learnit.learnit.types;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.learnit.learnit.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class DbHelperTest extends DbHelper{
    SQLiteDatabase database;

    public DbHelperTest() {
        super(null, null, null, 100);
    }

    @Test
    public void testReadFromDb() {
        assertThat(database == null, is(false));
        String wordToQuery = "fallen";
        String transToExpect = "fall";
        WordBundle bundle = new WordBundle();
        bundle.setId(2)
                .setWord(wordToQuery)
                .setTransFromString(transToExpect)
                .setWeight(0.5f);
        List<WordBundle> res = queryFromDB(wordToQuery, "test_words", database);
        assertThat(res == null, is(false));
        if (res == null) {
            return;
        }
        assertThat(res.size(), is(1));
        WordBundle resultBundle = res.get(0);
        assertThat(resultBundle.id(), is(bundle.id()));
        assertThat(resultBundle.weight(), is(bundle.weight()));
        assertThat(resultBundle.word(), is(bundle.word()));
        assertThat(resultBundle.transAsString(), is(bundle.transAsString()));
        assertThat(resultBundle.article(), is(bundle.article()));
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
}