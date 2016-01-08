package com.learnit.learnit.types;

import android.view.View;

import com.learnit.learnit.interfaces.IAnswerChecker;

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
            mChecker.onCorrectViewClicked(v);
        } else {
            mChecker.onWrongViewClicked(v);
        }
    }
}
