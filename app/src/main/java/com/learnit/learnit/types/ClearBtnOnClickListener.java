package com.learnit.learnit.types;

import android.view.View;

import com.learnit.learnit.R;
import com.learnit.learnit.interfaces.IAddWordsFragmentUiEvents;

public class ClearBtnOnClickListener implements View.OnClickListener {

    IAddWordsFragmentUiEvents mCallback;

    public ClearBtnOnClickListener(IAddWordsFragmentUiEvents callback) {
        mCallback = callback;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDeleteWord:
                mCallback.clearWord();
                break;
            case R.id.btnDeleteTrans:
                mCallback.clearTrans();
                break;
        }
    }
}
