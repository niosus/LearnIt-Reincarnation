package com.learnit.learnit.interfaces;

import android.view.View;

public interface IAnswerChecker {
    void onCorrectAnswerPicked();
    void onWrongAnswerPicked(View v);
}
