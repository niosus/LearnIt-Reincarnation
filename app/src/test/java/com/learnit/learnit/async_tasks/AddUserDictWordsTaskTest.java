package com.learnit.learnit.async_tasks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Config(manifest = "src/main/AndroidManifest.xml", sdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class AddUserDictWordsTaskTest implements IAsyncTaskResultClient {
    SQLiteDatabase database;

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
    }

    @Test
    public void testAddWords() {
        Context context = RuntimeEnvironment.application;
        assertThat(context == null, is(false));
        if (context == null) {
            assertThat(true, is(false));
            return;
        }
        WordBundle bundle = new WordBundle();
        bundle.setWord("try");
        List<WordBundle> bundles = new ArrayList<>();
        bundles.add(bundle);
        bundles.add(bundle);
        MySmartAsyncTask addWordsTask = new AddUserDictWordsTask(context, bundles);
        addWordsTask.setResultClient(this);
        addWordsTask.execute();
    }

    @Override
    public void onProgressUpdate(Float progress) {
    }

    @Override
    public <OutType> void onFinish(OutType result) {
        if (result instanceof List) {
            @SuppressWarnings("unchecked")
            List<Constants.AddWordReturnCode> list = (List<Constants.AddWordReturnCode>) result;
            assertThat(list.size(), is(2));
            assertThat(list.get(0), is(Constants.AddWordReturnCode.SUCCESS));
            assertThat(list.get(1), is(Constants.AddWordReturnCode.WORD_EXISTS));
        } else {
            // make sure this is not called
            assertThat(true, is(false));
        }
    }

    @Override
    public String tag() {
        // TODO: probably something is wrong with the tags over here
        return "add_words_task_test";
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onCancelled() {
        Log.d(Constants.LOG_TAG, "I am cancelled: " + this.getClass().getCanonicalName());
    }
}