/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.types;

import android.text.Editable;
import android.text.TextWatcher;

import com.learnit.learnit.R;
import com.learnit.learnit.interfaces.IUiEvents;

public class TextChangeListener implements TextWatcher {
    IUiEvents mCallback;
    int mViewId;

    public TextChangeListener(IUiEvents callback, final int id) {
        mCallback = callback;
        mViewId = id;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        switch (mViewId) {
            case R.id.addWord:
                mCallback.wordTextChanged();
                break;
            case R.id.edt_search_word:
                mCallback.wordTextChanged();
                break;
        }
    }
}
