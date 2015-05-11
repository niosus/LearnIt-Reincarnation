/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.async_tasks;

import android.content.Context;
import android.util.Log;

import com.learnit.learnit.BuildConfig;
import com.learnit.learnit.CustomRobolectricTestRunner;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.utils.Constants;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
@RunWith(CustomRobolectricTestRunner.class)
public class DummyTaskTest implements IAsyncTaskResultClient {
    private float maxReceivedProgress;
    private DummyTask dummyTask;

    @Test
    public void testDummyTask() {
        Context context = RuntimeEnvironment.application;
        dummyTask = new DummyTask(context, 5, this);
        dummyTask.execute();
    }

    @Override
    public String tag() {
        return "dummy_task";
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
    public void onPreExecute() {

    }

    @Override
    public void onCancelled() {
        assertTrue(dummyTask.isCancelled());
        System.out.println(String.format("Task %s has just been cancelled", dummyTask.getClass().getCanonicalName()));
    }
}