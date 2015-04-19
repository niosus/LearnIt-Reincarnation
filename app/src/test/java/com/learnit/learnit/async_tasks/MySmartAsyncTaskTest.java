package com.learnit.learnit.async_tasks;

import android.content.Context;

import com.learnit.learnit.BuildConfig;
import com.learnit.learnit.interfaces.IAsyncTaskEvents;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class MySmartAsyncTaskTest implements IAsyncTaskEvents<Integer> {
    private float maxReceivedProgress;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testReadTxtDatabase() {
//        DbHelper helper = new DbHelper(RuntimeEnvironment.application, DbHelper.DB_HELPER_DICT, null, 1);
        Context context = RuntimeEnvironment.application;
        String filePath;
        if (context.getPackageResourcePath().contains("test")) {
            // means that we run from android studio
            filePath = context.getPackageResourcePath() + "/res/en-uk.txt";
        } else {
            // we run from command line
            filePath = context.getPackageResourcePath() + "/src/test/res/en-uk.txt";
        }
        PopulateHelpDictTask populateHelpDictTask = new PopulateHelpDictTask(context, this);
        populateHelpDictTask.execute(filePath);
    }

    @Override
    public void onProgressUpdate(Float progress) {
        maxReceivedProgress = Math.max(progress, maxReceivedProgress);
    }

    @Override
    public void onFinish(Integer result) {
        //we consider progress of more than 95% a success
        assertTrue(maxReceivedProgress > 95f);
        assertThat(result, is(0));
    }

    @Override
    public void onPreExecute() {

    }
}