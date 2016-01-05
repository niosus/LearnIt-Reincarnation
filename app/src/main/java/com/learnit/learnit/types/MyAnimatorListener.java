/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.types;

import android.view.View;

import com.learnit.learnit.interfaces.IAnimationEventListener;
import com.learnit.learnit.interfaces.IUiEvents;

import io.codetail.animation.SupportAnimator;

public class MyAnimatorListener implements SupportAnimator.AnimatorListener {
    int mViewId;
    int mTargetVisibility;
    IAnimationEventListener mCallback = null;

    public MyAnimatorListener(IAnimationEventListener callback, final int viewId, final int targetVisibility) {
        mViewId = viewId;
        mTargetVisibility = targetVisibility;
        mCallback = callback;
    }

    @Override
    public void onAnimationStart() {
        mCallback.onAnimationStarted(mViewId, mTargetVisibility);
    }

    @Override
    public void onAnimationEnd() {
        mCallback.onAnimationFinished(mViewId, mTargetVisibility);
    }

    @Override
    public void onAnimationCancel() {

    }

    @Override
    public void onAnimationRepeat() {

    }
}
