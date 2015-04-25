/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.types;

import android.view.View;

import com.learnit.learnit.interfaces.IAddWordsFragmentUiEvents;

import io.codetail.animation.SupportAnimator;

public class MyAnimatorListener implements SupportAnimator.AnimatorListener {
    int mViewId;
    int mTargetVisibility;
    IAddWordsFragmentUiEvents mCallback = null;

    public MyAnimatorListener(IAddWordsFragmentUiEvents callback, final int viewId, final int targetVisibility) {
        mViewId = viewId;
        mTargetVisibility = targetVisibility;
        mCallback = callback;
    }

    @Override
    public void onAnimationStart() {
        if (mTargetVisibility == View.VISIBLE) {
            mCallback.setViewVisibilityState(mViewId, mTargetVisibility);
        }
    }

    @Override
    public void onAnimationEnd() {
        if (mTargetVisibility == View.INVISIBLE) {
            mCallback.setViewVisibilityState(mViewId, mTargetVisibility);
        }
    }

    @Override
    public void onAnimationCancel() {

    }

    @Override
    public void onAnimationRepeat() {

    }
}
