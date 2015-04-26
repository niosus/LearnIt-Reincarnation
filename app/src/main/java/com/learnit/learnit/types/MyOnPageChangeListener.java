/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.types;

import android.support.v4.view.ViewPager;
import android.util.Log;

import com.learnit.learnit.interfaces.IActionBarEvents;
import com.learnit.learnit.utils.Constants;

public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
    IActionBarEvents mActionBarEvents = null;

    public MyOnPageChangeListener(IActionBarEvents actionBarEvents) {
        mActionBarEvents = actionBarEvents;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset == 0.0
                && (position == 1 || position == 2)) {
            mActionBarEvents.showActionBar();
        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(Constants.LOG_TAG, "page changed");
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
