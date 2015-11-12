/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.async_tasks;

import android.content.Context;
import android.util.Log;

import com.learnit.learnit.BuildConfig;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.utils.Constants;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

@Config(manifest = "src/main/AndroidManifest.xml", sdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class PopulateHelpDictTaskTest implements IAsyncTaskResultClient {
    private float maxReceivedProgress;

    @Test
    public void testReadTxtDatabase() {
        Context context = RuntimeEnvironment.application;
        String filePath;
        filePath = context.getPackageResourcePath() + "/src/test/res/en-uk.txt";
        System.out.println(filePath);
        PopulateHelpDictTask populateHelpDictTask = new PopulateHelpDictTask(context, filePath);
        populateHelpDictTask.setResultClient(this);
        populateHelpDictTask.execute();
    }

    @Override
    public void onProgressUpdate(Float progress) {
        maxReceivedProgress = Math.max(progress, maxReceivedProgress);
    }

    @Override
    public <OutType> void onFinish(OutType result) {
        assertTrue(maxReceivedProgress > 95f);
        if (result instanceof Integer) {
            assertThat((Integer) result, is(0));
        } else {
            // make sure this is not called
            assertThat(true, is(false));
        }
    }

    @Override
    public String tag() {
        return "populate_dict_test_tag";
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onCancelled() {
        Log.d(Constants.LOG_TAG, "I am cancelled: " + this.getClass().getCanonicalName());
    }
}