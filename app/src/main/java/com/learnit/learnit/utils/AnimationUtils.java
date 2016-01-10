package com.learnit.learnit.utils;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.learnit.learnit.interfaces.IAnimationEventListener;
import com.learnit.learnit.types.MyAnimatorListener;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class AnimationUtils {

    public enum MotionOrigin {
        CENTER, TOP_CENTER
    }

    public static void animateToVisibilityCircular(View view, final int visibility, final int duration,
                                                   IAnimationEventListener eventHandler, MotionOrigin origin) {
        if (view == null) {
            return;
        }

        // set the center for the clipping circle
        int cx = 0, cy = 0;
        switch (origin) {
            case CENTER:
                cx = (view.getLeft() + view.getRight()) / 2;
                cy = (view.getTop() + view.getBottom()) / 2;
                break;
            case TOP_CENTER:
                cx = (view.getLeft() + view.getRight()) / 2;
                cy = view.getTop();
                break;
        }


        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        float start;
        float end;
        if (visibility == View.VISIBLE) {
            start = 0;
            end = finalRadius;
        } else {
            start = finalRadius;
            end = 0;
        }
        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, start, end);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(duration);
        animator.addListener(new MyAnimatorListener(eventHandler, view.getId(), visibility));
        animator.start();
    }

}
