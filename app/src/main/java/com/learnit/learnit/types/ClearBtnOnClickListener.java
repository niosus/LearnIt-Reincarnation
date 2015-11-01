/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.types;

import android.view.View;

import com.learnit.learnit.R;
import com.learnit.learnit.interfaces.IUiEvents;

public class ClearBtnOnClickListener implements View.OnClickListener {

    IUiEvents mCallback;

    public ClearBtnOnClickListener(IUiEvents callback) {
        mCallback = callback;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_delete_word_add_words:
                mCallback.clearWord();
                break;
            case R.id.btn_delete_word_dict:
                mCallback.clearWord();
                break;
        }
    }
}
