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
import android.widget.LinearLayout;

import com.learnit.learnit.R;
import com.learnit.learnit.async_tasks.AddUserDictWordsTask;
import com.learnit.learnit.async_tasks.GetUserDictWordsTask;
import com.learnit.learnit.interfaces.IUiEvents;
import com.learnit.learnit.interfaces.IFabEventHandler;
import com.learnit.learnit.interfaces.IFabStateController;
import com.learnit.learnit.types.ClearBtnOnClickListener;
import com.learnit.learnit.types.LanguagePair;
import com.learnit.learnit.types.TabsPagerAdapter;
import com.learnit.learnit.types.TextChangeListener;
import com.learnit.learnit.types.WordBundleAdapter;
import com.learnit.learnit.utils.AnimationUtils;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

import at.markushi.ui.CircleButton;
import butterknife.Bind;
import butterknife.ButterKnife;

public class UserDictCardFragment extends Fragment
        implements IUiEvents, IFabEventHandler {
    private static final String ARG_POSITION = "position";
    private WordBundleAdapter mAdapter;

    private static String TAG = "dict_card_fragment";

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

    public static UserDictCardFragment newInstance(int position) {
        Log.d(Constants.LOG_TAG, "creating new instance of fragment");
        UserDictCardFragment f = new UserDictCardFragment();
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
        mFabStateController.addFabEventHandler(TabsPagerAdapter.USER_DICT_ITEM, this);
        startLoadingMyDictWordsAsync();
    }

    private void startLoadingMyDictWordsAsync() {
        // load new words from my dict
        mTaskScheduler.newTaskForClient(
                new GetUserDictWordsTask(this.getContext(),
                        mEditText.getText().toString()), mAdapter);
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
            AnimationUtils.animateToVisibilityState(mDeleteWordButton, View.INVISIBLE, this);
        } else {
            if (mDeleteWordButton.getVisibility() == View.INVISIBLE
                    && !mEditText.getText().toString().isEmpty()) {
                AnimationUtils.animateToVisibilityState(mDeleteWordButton, View.VISIBLE, this);
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
            startLoadingMyDictWordsAsync();
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



    @Override
    public void fabClicked(int viewPagerPos) {
        if (viewPagerPos == TabsPagerAdapter.USER_DICT_ITEM) {
            Log.d(Constants.LOG_TAG, "user dict fragment knows that fab was clicked from " + viewPagerPos);
            // TODO: delete selected items, show a snack bar to undo
        }
    }

    @Override
    public boolean fabNeeded() {
        return mAdapter.hasSelectedItems();
    }
}