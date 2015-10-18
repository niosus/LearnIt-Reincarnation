package com.learnit.learnit.async_tasks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.learnit.learnit.BuildConfig;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.db_handlers.DbHandler;
import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class GetHelpWordsTaskTest implements IAsyncTaskResultClient {
    SQLiteDatabase database;

    @Before
    public void setUp() throws Exception {
        Context context = RuntimeEnvironment.application;
        assertThat(context == null, is(false));
        if (context == null) {
            assertThat(true, is(false));
            return;
        }
        DbHandler helper = DbHandler.Factory.createLocalizedHelper(context, DbHandler.DB_HELPER_DICT);
        WordBundle wordBundle1 = new WordBundle();
        wordBundle1.setWord("hello");
        wordBundle1.setTransFromString("hello");
        helper.addWord(wordBundle1);
        WordBundle wordBundle2 = new WordBundle();
        wordBundle2.setWord("hell");
        wordBundle2.setTransFromString("hello");
        helper.addWord(wordBundle2);
        WordBundle wordBundle3 = new WordBundle();
        wordBundle3.setWord("help");
        helper.addWord(wordBundle3);
    }

    @Test
    public void testGetWords() {
        Context context = RuntimeEnvironment.application;
        assertThat(context == null, is(false));
        if (context == null) {
            assertThat(true, is(false));
            return;
        }
        GetHelpWordsTask getWordsTask = new GetHelpWordsTask(context, "hel");
        getWordsTask.setResulClient(this);
        getWordsTask.execute();
    }

    @Override
    public void onProgressUpdate(Float progress) {
    }

    @Override
    public <OutType> void onFinish(OutType result) {
        if (result instanceof List) {
            List<WordBundle> list = (List<WordBundle>) result;
            assertThat(list == null, is(false));
            assertThat(list.size(), is(3));
            assertThat(list.get(0).word(), is("hello"));
            assertThat(list.get(1).word(), is("hell"));
            assertThat(list.get(2).word(), is("help"));
        } else {
            // make sure this is not called
            assertThat(true, is(false));
        }
    }

    @Override
    public String tag() {
        // TODO: probably something is wrong with the tags over here
        return "get_words_task_test";
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onCancelled() {
        Log.d(Constants.LOG_TAG, "I am cancelled: " + this.getClass().getCanonicalName());
    }
}