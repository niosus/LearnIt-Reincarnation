package com.learnit.learnit.types;

import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

// TODO: add an interface that would provide smth like onCorrectClicked and onWrongClicked dependent on the click received here
// all the animations should be played from the fragment or from some other class that will handle animations
public class LearnCorrectnessValidator implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        YoYo.with(Techniques.Shake).duration(700).playOn(v);
    }
}
