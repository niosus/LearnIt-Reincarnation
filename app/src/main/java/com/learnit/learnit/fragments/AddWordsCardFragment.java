package com.learnit.learnit.fragments;/*
 * Copyright (C) 2013 Igor Bogoslavskyi <igor.bogoslavskyi@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.learnit.learnit.R;
import com.learnit.learnit.interfaces.IAddWordsFragmentUiEvents;
import com.learnit.learnit.types.ClearBtnOnClickListener;
import com.learnit.learnit.types.MyAnimatorListener;
import com.learnit.learnit.types.TextChangeListener;
import com.learnit.learnit.utils.Constants;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import at.markushi.ui.CircleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class AddWordsCardFragment extends Fragment
implements IAddWordsFragmentUiEvents {
    private ObservableScrollViewCallbacks mScrollCallback = null;

    private static final String ARG_POSITION = "position";

    @InjectView(R.id.addWordsListView) ObservableListView addWordsListView;
    @InjectView(R.id.addWord) MaterialAutoCompleteTextView edtWord;
    @InjectView(R.id.addTranslation) MaterialEditText edtTrans;
    @InjectView(R.id.btnDeleteWord) CircleButton btnDeleteWord;
    @InjectView(R.id.btnDeleteTrans) CircleButton btnDeleteTrans;

    public static AddWordsCardFragment newInstance(int position) {
        AddWordsCardFragment f = new AddWordsCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.LOG_TAG, "creating fragment");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(Constants.LOG_TAG, "attaching fragment");
        if (activity instanceof ObservableScrollViewCallbacks) {
            mScrollCallback = (ObservableScrollViewCallbacks) activity;
        } else {
            Log.e(Constants.LOG_TAG, "activity " + activity.getLocalClassName()
                    + " does not implement ObservableScrollViewCallbacks interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(Constants.LOG_TAG, "creating view");
        View rootView = inflater.inflate(R.layout.fragment_add_words,container,false);
        ButterKnife.inject(this, rootView);
        if (mScrollCallback != null) {
            Log.d(Constants.LOG_TAG, "setting scroll callback.");
            addWordsListView.setScrollViewCallbacks(mScrollCallback);
        }
        ViewCompat.setElevation(rootView, 50);
        edtWord.addTextChangedListener(new TextChangeListener(this, edtWord.getId()));
        btnDeleteWord.setVisibility(View.INVISIBLE);
        edtTrans.addTextChangedListener(new TextChangeListener(this, edtTrans.getId()));
        btnDeleteTrans.setVisibility(View.INVISIBLE);

        View.OnClickListener myOnClickListener = new ClearBtnOnClickListener(this);
        btnDeleteWord.setOnClickListener(myOnClickListener);
        btnDeleteTrans.setOnClickListener(myOnClickListener);
        return rootView;
    }

    @Override
    public void wordTextChanged() {
        Log.d(Constants.LOG_TAG, "changed add_word text");
        if (edtWord.getText().toString().isEmpty()) {
            this.startAnimation(btnDeleteWord.getId(), View.INVISIBLE);
        } else if (btnDeleteWord.getVisibility() == View.INVISIBLE) {
            this.startAnimation(btnDeleteWord.getId(), View.VISIBLE);
        }
    }

    @Override
    public void translationTextChanged() {
        Log.d(Constants.LOG_TAG, "changed add_trans text");
        if (edtTrans.getText().toString().isEmpty()) {
            this.startAnimation(btnDeleteTrans.getId(), View.INVISIBLE);
        } else if (btnDeleteTrans.getVisibility() == View.INVISIBLE) {
            this.startAnimation(btnDeleteTrans.getId(), View.VISIBLE);
        }
        this.updateTranslationSnackBar();
    }

    @Override
    public void setViewVisibilityState(int id, int visibility) {
        Log.d(Constants.LOG_TAG, "changing visibility of " + id + " to " + visibility);
        switch (id) {
            case R.id.btnDeleteWord:
                btnDeleteWord.setVisibility(visibility);
                break;
            case R.id.btnDeleteTrans:
                btnDeleteTrans.setVisibility(visibility);
                break;
            default:
                Log.e(Constants.LOG_TAG, "unhandled switch setViewVisibilityState");
        }
    }

    @Override
    public void clearWord() {
        edtWord.setText("");
    }

    @Override
    public void clearTrans() {
        edtTrans.setText("");
    }

    private void updateTranslationSnackBar() {
        Activity currentActivity = this.getActivity();
        if (currentActivity == null) {
            Log.e(Constants.LOG_TAG, "cannot show a snack bar");
        }
        SnackbarManager.show(
                Snackbar.with(currentActivity)
                        .text("Single-line snackbar"));
    }

    private void startAnimation(final int id, final int visibility) {
        View myView = null;
        switch (id) {
            case R.id.btnDeleteWord:
                myView = btnDeleteWord;
                break;
            case R.id.btnDeleteTrans:
                myView = btnDeleteTrans;
                break;
        }
        if (myView == null) {
            return;
        }
        // get the center for the clipping circle
        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());
        float start;
        float end;
        if (visibility == View.VISIBLE) {
            start = 0;
            end = finalRadius;
        } else {
            start = finalRadius;
            end = 0;
        }
        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, start, end);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(500);
        animator.addListener(new MyAnimatorListener(this, id, visibility));
        animator.start();
    }
}