package com.learnit.learnit.async_tasks;

import android.content.Context;
import android.util.Log;

import com.learnit.learnit.BuildConfig;
import com.learnit.learnit.db_handlers.DbHandler;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
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
import static org.hamcrest.MatcherAssert.assertThat;

@Config(manifest = "src/main/AndroidManifest.xml", sdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class GetUserDictWordsTaskTest implements IAsyncTaskResultClient {
    @Before
    public void setUp() throws Exception {
        Context context = RuntimeEnvironment.application;
        assertThat(context == null, is(false));
        if (context == null) {
            assertThat(true, is(false));
            return;
        }
        DbHandler helper = DbHandler.Factory.createLocalizedHelper(context, DbHandler.DB_USER_DICT);
        assertThat(helper == null, is(false));
        if (helper == null) {
            assertThat(true, is(false));
            return;
        }
        WordBundle wordBundle1 = new WordBundle();
        wordBundle1.setWord("hello");
        wordBundle1.setTrans("hello");
        helper.addWord(wordBundle1);
        WordBundle wordBundle2 = new WordBundle();
        wordBundle2.setWord("hell");
        wordBundle2.setTrans("hello");
        helper.addWord(wordBundle2);
        WordBundle wordBundle3 = new WordBundle();
        wordBundle3.setWord("help");
        helper.addWord(wordBundle3);
        WordBundle wordBundle4 = new WordBundle();
        wordBundle4.setWord("telpfpf");
        helper.addWord(wordBundle4);
        WordBundle wordBundle5 = new WordBundle();
        wordBundle5.setWord("lentet");
        helper.addWord(wordBundle5);
    }

    @Test
    public void testGetWords() {
        Context context = RuntimeEnvironment.application;
        assertThat(context == null, is(false));
        if (context == null) {
            assertThat(true, is(false));
            return;
        }
        MySmartAsyncTask getWordsTask = new GetUserDictWordsTask(context, "el");
        getWordsTask.setResultClient(this);
        getWordsTask.execute();
    }

    @Override
    public void onProgressUpdate(Float progress) {
    }

    @Override
    public <OutType> void onFinish(OutType result) {
        if (result instanceof List) {
            @SuppressWarnings("unchecked")
            List<WordBundle> list = (List<WordBundle>) result;
            assertThat(list.size(), is(4));
            assertThat(list.get(0).word(), is("hello"));
            assertThat(list.get(1).word(), is("hell"));
            assertThat(list.get(2).word(), is("help"));
            assertThat(list.get(3).word(), is("telpfpf"));
        } else {
            // make sure this is not called
            assertThat(true, is(false));
        }
    }

    @Override
    public String tag() {
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