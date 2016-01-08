package com.learnit.learnit.types;

import android.content.Context;
import android.view.View;

import com.learnit.learnit.interfaces.IAnswerChecker;

// TODO: add an interface that would provide smth like onCorrectClicked and onWrongClicked dependent on the click received here
// all the animations should be played from the fragment or from some other class that will handle animations
public class LearnCorrectnessValidator implements View.OnClickListener {
    private IAnswerChecker mChecker;
    private int mCorrectAnswerId;

    public LearnCorrectnessValidator(IAnswerChecker checker) {
            mChecker = checker;
    }

    public void setCorrectAnswerId(final int correctAnswerId) {
        mCorrectAnswerId = correctAnswerId;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mCorrectAnswerId) {
            mChecker.onCorrectAnswerPicked();
        } else {
            mChecker.onWrongAnswerPicked(v);
        }
    }
}
