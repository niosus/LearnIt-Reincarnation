package com.learnit.learnit.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.learnit.learnit.BuildConfig;
import com.learnit.learnit.activities.IntroActivity;
import com.learnit.learnit.async_tasks.DummyTask;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;


@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class TaskSchedulerFragmentTest implements IAsyncTaskResultClient {

    TaskSchedulerFragment scheduler;
    Context context;

    boolean mOnPreExecuteCalled = false;

    @Before
    public void testNotNull() throws Exception {
        context = RuntimeEnvironment.application;
        scheduler = new TaskSchedulerFragment();
        FragmentTestUtil.startFragment(scheduler);
        assertNotNull(scheduler);
        System.out.println(scheduler.getActivity());
    }

    @Test
    public void testRunningSimpleTask() throws Exception {
        scheduler.newTaskForClient(new DummyTask(context, 5), this);
    }

    @Override
    public String tag() {
        return "task_scheduler_test_tag";
    }

    @Override
    public void onPreExecute() {
        mOnPreExecuteCalled = true;
    }

    @Override
    public void onProgressUpdate(Float progress) {

    }

    @Override
    public <OutType> void onFinish(OutType result) {
        assertTrue(mOnPreExecuteCalled);
        if (result instanceof Integer) {
            assertThat((Integer) result, is(0));
        } else {
            assertThat(true, is(false));
        }
    }

    @Override
    public void onCancelled() {

    }
}