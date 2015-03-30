package com.learnit.learnit.interfaces;

public interface IAddWordsFragmentUiEvents {
    void wordTextChanged();
    void translationTextChanged();
    void setViewVisibilityState(final int id, final int visibility);
    void clearWord();
    void clearTrans();
}
