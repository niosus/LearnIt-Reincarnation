package com.learnit.learnit.interfaces;

import android.view.View;

public interface IAnswerChecker {
    void onCorrectViewClicked(View v);
    void onWrongViewClicked(View v);
}
