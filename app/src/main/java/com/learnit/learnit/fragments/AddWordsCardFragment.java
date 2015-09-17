/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

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
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.learnit.learnit.R;
import com.learnit.learnit.interfaces.IActionBarEvents;
import com.learnit.learnit.interfaces.IAddWordsFragmentUiEvents;
import com.learnit.learnit.types.ClearBtnOnClickListener;
import com.learnit.learnit.types.CountryAdapter;
import com.learnit.learnit.types.CountryManager;
import com.learnit.learnit.types.MyAnimatorListener;
import com.learnit.learnit.types.TextChangeListener;
import com.learnit.learnit.utils.Constants;

import at.markushi.ui.CircleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class AddWordsCardFragment extends Fragment
        implements IAddWordsFragmentUiEvents, IActionBarEvents {
    private static final String ARG_POSITION = "position";
    private CountryAdapter mAdapter;

    @InjectView(R.id.list)
    RecyclerView mRecyclerView;
    @InjectView(R.id.addWord)
    AppCompatEditText edtWord;
    @InjectView(R.id.btnDeleteWord)
    CircleButton btnDeleteWord;
    @InjectView(R.id.add_word_layout)
    RelativeLayout addWordLayout;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView countryName;
        public ImageView countryImage;

        public ViewHolder(View itemView) {
            super(itemView);
            countryName = (TextView) itemView.findViewById(R.id.countryName);
            countryImage = (ImageView)itemView.findViewById(R.id.countryImage);
        }

    }

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
//        if (activity instanceof ObservableScrollViewCallbacks) {
//            mScrollCallback = (ObservableScrollViewCallbacks) activity;
//        } else {
//            Log.e(Constants.LOG_TAG, "activity " + activity.getLocalClassName()
//                    + " does not implement ObservableScrollViewCallbacks interface");
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(Constants.LOG_TAG, "creating view");
        View rootView = inflater.inflate(R.layout.fragment_add_words, container, false);

        ButterKnife.inject(this, rootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new CountryAdapter(CountryManager.getInstance().getCountries(), R.layout.row_country, this.getActivity());
        mRecyclerView.setAdapter(mAdapter);

        ViewCompat.setElevation(rootView, 50);
        edtWord.addTextChangedListener(new TextChangeListener(this, edtWord.getId()));

        View.OnClickListener myOnClickListener = new ClearBtnOnClickListener(this);
        btnDeleteWord.setOnClickListener(myOnClickListener);
        btnDeleteWord.setVisibility(View.INVISIBLE);

        if (addWordLayout != null) {
            Log.d(Constants.LOG_TAG, "relative layout initialized");
        }
        return rootView;
    }

    @Override
    public void wordTextChanged() {
        try {
            Log.d(Constants.LOG_TAG, "changed text on edit text");
            if (edtWord.getText().toString().isEmpty()
                    && btnDeleteWord.getVisibility() == View.VISIBLE) {
                this.animateToVisibilityState(btnDeleteWord.getId(), View.INVISIBLE);
            } else if (btnDeleteWord.getVisibility() == View.INVISIBLE
                    && !edtWord.getText().toString().isEmpty()) {
                this.animateToVisibilityState(btnDeleteWord.getId(), View.VISIBLE);
            }
        } catch (IllegalStateException e) {
            Log.w(Constants.LOG_TAG, "trying to run animation on a detached view. Not sure what exactly causes it.");
        }
    }

    @Override
    public void setViewVisibilityState(int id, int visibility) {
        Log.d(Constants.LOG_TAG, "changing visibility of " + id + " to " + visibility);
        switch (id) {
            case R.id.btnDeleteWord:
                btnDeleteWord.setVisibility(visibility);
                break;
            default:
                Log.e(Constants.LOG_TAG, "unhandled switch setViewVisibilityState");
        }
    }

    @Override
    public void clearWord() {
        edtWord.setText("");
    }

    private void animateToVisibilityState(final int id, final int visibility) {
        View myView = null;
        switch (id) {
            case R.id.btnDeleteWord:
                myView = btnDeleteWord;
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

    @Override
    public void hideActionBar(int actionBarHeight) {
        View view = this.getView();
        if (view == null) {
            Log.e(Constants.LOG_TAG, "view is null in " + this.getClass().getCanonicalName());
            return;
        }
        addWordLayout.animate().translationY(-actionBarHeight).setInterpolator(new AccelerateInterpolator(2));
    }

    @Override
    public void showActionBar() {
        View view = this.getView();
        if (view == null) {
            Log.e(Constants.LOG_TAG, "view is null in " + this.getClass().getCanonicalName());
            return;
        }
        addWordLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }
}