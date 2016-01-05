package com.learnit.learnit.async_tasks;

import android.content.Context;

import com.learnit.learnit.BuildConfig;
import com.learnit.learnit.db_handlers.DbHandler;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.types.WordBundle;

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
public class GetHelpWordsTaskTest {
    public static class ResultReceiverHello implements IAsyncTaskResultClient{
        @Override
        public String tag() {
            return "get_words_task_test1";
        }

        @Override
        public void onPreExecute() {

        }

        @Override
        public void onProgressUpdate(Float progress) {

        }

        @Override @SuppressWarnings("unchecked")
        public <OutType> void onFinish(OutType result) {
            if (result instanceof List) {
                List<WordBundle> list = (List<WordBundle>) result;
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
        public void onCancelled() {

        }
    }

    public static class ResultReceiverPararam implements IAsyncTaskResultClient{
        @Override
        public String tag() {
            return "get_words_task_test2";
        }

        @Override
        public void onPreExecute() {

        }

        @Override
        public void onProgressUpdate(Float progress) {

        }

        @Override @SuppressWarnings("unchecked")
        public <OutType> void onFinish(OutType result) {
            if (result instanceof List) {
                List<WordBundle> list = (List<WordBundle>) result;
                assertThat(list.size(), is(2));
                assertThat(list.get(0).word(), is("paraparam"));
                assertThat(list.get(1).word(), is("parapararam"));
            } else {
                // make sure this is not called
                assertThat(true, is(false));
            }
        }

        @Override
        public void onCancelled() {

        }
    }

    @Before
    public void setUp() throws Exception {
        Context context = RuntimeEnvironment.application;
        assertThat(context == null, is(false));
        if (context == null) {
            assertThat(true, is(false));
            return;
        }
        DbHandler helper = DbHandler.Factory.createLocalizedHelper(context, DbHandler.DB_HELPER_DICT);
        if (helper == null) {
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
        wordBundle4.setWord("paraparam");
        helper.addWord(wordBundle4);
        WordBundle wordBundle5 = new WordBundle();
        wordBundle5.setWord("parapararam");
        helper.addWord(wordBundle5);
        WordBundle wordBundle6 = new WordBundle();
        wordBundle6.setWord("pum-purumparapam");
        helper.addWord(wordBundle6);
    }

    @Test
    public void testGetWords() {
        Context context = RuntimeEnvironment.application;
        assertThat(context == null, is(false));
        if (context == null) {
            assertThat(true, is(false));
            return;
        }
        GetHelpWordsTask getWordsTask = new GetHelpWordsTask(context, "hel", null);
        getWordsTask.setResultClient(new ResultReceiverHello());
        getWordsTask.execute();
    }

    @Test
    public void testGetWordsLimited() {
        Context context = RuntimeEnvironment.application;
        assertThat(context == null, is(false));
        if (context == null) {
            assertThat(true, is(false));
            return;
        }
        GetHelpWordsTask getWordsTask = new GetHelpWordsTask(context, "par", 2);
        getWordsTask.setResultClient(new ResultReceiverPararam());
        getWordsTask.execute();
    }
}