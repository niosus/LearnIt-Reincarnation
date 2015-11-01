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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.learnit.learnit.R;
import com.learnit.learnit.interfaces.IAddWordsFragmentUiEvents;
import com.learnit.learnit.interfaces.IFabEventHandler;
import com.learnit.learnit.interfaces.IFabStateController;
import com.learnit.learnit.types.ClearBtnOnClickListener;
import com.learnit.learnit.types.LanguagePair;
import com.learnit.learnit.types.MyAnimatorListener;
import com.learnit.learnit.types.TabsPagerAdapter;
import com.learnit.learnit.types.TextChangeListener;
import com.learnit.learnit.types.WordBundleAdapter;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

import at.markushi.ui.CircleButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class MyDictCardFragment extends Fragment
        implements IAddWordsFragmentUiEvents, IFabEventHandler {
    private static final String ARG_POSITION = "position";
    private WordBundleAdapter mAdapter;

    private static String TAG = "dict_card_fragment";
    private static int POSITION = TabsPagerAdapter.DICT_ITEM;

    @Bind(R.id.lst_my_words)
    RecyclerView mRecyclerView;
    @Bind(R.id.edt_search_word)
    AppCompatEditText mEditText;
    @Bind(R.id.btn_delete_word_dict)
    CircleButton mDeleteWordButton;
    @Bind(R.id.dict_layout)
    LinearLayout mDictLayout;

    private TaskSchedulerFragment mTaskScheduler;
    private IFabStateController mFabStateController;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(Constants.LOG_TAG, "fragment is attached");
        initTaskScheduler(context);
        if (context instanceof IFabStateController) {
            mFabStateController = (IFabStateController) context;
        }
    }

    private void initTaskScheduler(Context context) {
        FragmentManager fragmentManager = getFragmentManager();
        mTaskScheduler = (TaskSchedulerFragment)
                fragmentManager.findFragmentByTag(TaskSchedulerFragment.TAG);
        if (mTaskScheduler == null) {
            mTaskScheduler = new TaskSchedulerFragment();
            fragmentManager.beginTransaction()
                    .add(mTaskScheduler, TaskSchedulerFragment.TAG)
                    .commit();
        }
    }

    public static MyDictCardFragment newInstance(int position) {
        Log.d(Constants.LOG_TAG, "creating new instance of fragment");
        MyDictCardFragment f = new MyDictCardFragment();
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
    public void onResume() {
        super.onResume();
        LanguagePair.Names langPair = Utils.getCurrentLanguageNames(getContext());
        mEditText.setHint(String.format(getString(R.string.my_dict_word_hint),
                langPair.langToLearn(), langPair.langYouKnow()));
        mFabStateController.addFabEventHandler(POSITION, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(Constants.LOG_TAG, "creating view");
        View rootView = inflater.inflate(R.layout.fragment_dict, container, false);

        ButterKnife.bind(this, rootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new WordBundleAdapter(null, R.layout.word_bundle_layout, mFabStateController);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isAdded()) {
                    // means there is no activity to use
                    return;
                }
                int threshold = 2;
                if (Math.abs(dy) > threshold) {
                    Utils.hideKeyboard(getActivity());
                }
            }
        });

        ViewCompat.setElevation(rootView, 50);
        mEditText.addTextChangedListener(new TextChangeListener(this, mEditText.getId()));

        View.OnClickListener myOnClickListener = new ClearBtnOnClickListener(this);
        mDeleteWordButton.setOnClickListener(myOnClickListener);
        mDeleteWordButton.setVisibility(View.INVISIBLE);

        if (mDictLayout != null) {
            Log.d(Constants.LOG_TAG, "relative layout initialized");
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateDeleteButtonStateAnimate();
    }

    private void updateDeleteButtonStateAnimate() {
        if (mEditText.getText().toString().isEmpty()
                && mDeleteWordButton.getVisibility() == View.VISIBLE) {
            this.animateToVisibilityState(mDeleteWordButton.getId(), View.INVISIBLE);
        } else {
            if (mDeleteWordButton.getVisibility() == View.INVISIBLE
                    && !mEditText.getText().toString().isEmpty()) {
                this.animateToVisibilityState(mDeleteWordButton.getId(), View.VISIBLE);
            }
        }
    }

    private void updateDeleteButtonStateInstant() {
        if (mEditText.getText().toString().isEmpty()
                && mDeleteWordButton.getVisibility() == View.VISIBLE) {
            this.setViewVisibilityState(mDeleteWordButton.getId(), View.INVISIBLE);
        } else {
            if (mDeleteWordButton.getVisibility() == View.INVISIBLE
                    && !mEditText.getText().toString().isEmpty()) {
                this.setViewVisibilityState(mDeleteWordButton.getId(), View.VISIBLE);
            }
        }
    }

    @Override
    public void wordTextChanged() {
        try {
            Log.w(Constants.LOG_TAG, "changed text on edit text, should load some words now!");
            updateDeleteButtonStateAnimate();
        } catch (IllegalStateException e) {
            Log.w(Constants.LOG_TAG, "trying to run animation on a detached view. Not sure what exactly causes it.");
            updateDeleteButtonStateInstant();
        }
    }

    @Override
    public void setViewVisibilityState(int id, int visibility) {
        Log.d(Constants.LOG_TAG, "changing visibility of " + id + " to " + visibility);
        switch (id) {
            case R.id.btn_delete_word_dict:
                mDeleteWordButton.setVisibility(visibility);
                break;
            default:
                Log.e(Constants.LOG_TAG, "unhandled switch setViewVisibilityState");
        }
    }

    @Override
    public void clearWord() {
        mEditText.setText(null);
        if (!isAdded()) {
            // no activity, so we can't do anything
            return;
        }
        Utils.showKeyboard(getActivity());
    }

    private void animateToVisibilityState(final int id, final int visibility) {
        View myView = null;
        switch (id) {
            case R.id.btn_delete_word_dict:
                myView = mDeleteWordButton;
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
        animator.setDuration(300);
        animator.addListener(new MyAnimatorListener(this, id, visibility));
        animator.start();
    }

    @Override
    public void fabClicked(int viewPagerPos) {
        Log.d(Constants.LOG_TAG, "fragment knows that fab was clicked from " + viewPagerPos);
    }

    @Override
    public boolean fabNeeded() {
        return mAdapter.hasSelectedItems();
    }
}