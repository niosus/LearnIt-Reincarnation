/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.interfaces;

public interface IUiEvents {
    enum EditTextType {
        WORD,
        TRANS
    }
    void onTextChanged(EditTextType type);
    void onClearWord(EditTextType type);

    void onResultEmpty();
    void onResultFull();

    void setViewVisibilityState(int id, int visibility);

    void onWordsSelected();
    void onNoWordsSelected();
}
