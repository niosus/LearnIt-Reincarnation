package com.learnit.learnit.utils;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.learnit.learnit.interfaces.IUiEvents;
import com.learnit.learnit.types.MyAnimatorListener;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class AnimationUtils {

    public static void animateToVisibilityState(View view, final int visibility, IUiEvents eventHandler) {
        if (view == null) {
            return;
        }
        // get the center for the clipping circle
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

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
        animator.setDuration(300);
        animator.addListener(new MyAnimatorListener(eventHandler, view.getId(), visibility));
        animator.start();
    }

}
