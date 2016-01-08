package com.learnit.learnit.animators;

import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.nineoldandroids.animation.ObjectAnimator;

public class ZoomOutNoFade extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "scaleX", 1, 0),
                ObjectAnimator.ofFloat(target, "scaleY", 1, 0)
        );
    }
}
