package com.learnit.learnit.animators;

import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.nineoldandroids.animation.ObjectAnimator;

public class ZoomInNoFade extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "scaleX", 0, 1),
                ObjectAnimator.ofFloat(target, "scaleY", 0, 1)
        );
    }
}
