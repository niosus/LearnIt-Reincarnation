package com.learnit.learnit.fragments;

import com.learnit.learnit.BuildConfig;
import com.learnit.learnit.CustomRobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;

import static org.junit.Assert.assertNotNull;

@Config(emulateSdk = 21, reportSdk = 21, constants = BuildConfig.class)
@RunWith(CustomRobolectricTestRunner.class)
public class TaskSchedulerFragmentTest {

    @Test
    public void testNotNull() throws Exception {
        TaskSchedulerFragment fragment = new TaskSchedulerFragment();
        FragmentTestUtil.startFragment(fragment);
        assertNotNull(fragment);
        System.out.println(fragment.getActivity());
    }
}