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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.learnit.learnit.R;
import com.learnit.learnit.async_tasks.GetHelpWordsTask;
import com.learnit.learnit.interfaces.IAddWordsFragmentUiEvents;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.types.ClearBtnOnClickListener;
import com.learnit.learnit.types.WordBundleAdapter;
import com.learnit.learnit.types.MyAnimatorListener;
import com.learnit.learnit.types.TextChangeListener;
import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;

import java.util.List;

import at.markushi.ui.CircleButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class AddWordsCardFragment extends Fragment
        implements IAddWordsFragmentUiEvents {
    private static final String ARG_POSITION = "position";
    private WordBundleAdapter mAdapter;

    private static String TAG = "add_words_card_fragment";

    @Bind(R.id.list)
    RecyclerView mRecyclerView;
    @Bind(R.id.addWord)
    AppCompatEditText edtWord;
    @Bind(R.id.btnDeleteWord)
    CircleButton btnDeleteWord;
    @Bind(R.id.add_word_layout)
    LinearLayout addWordLayout;

    private TaskSchedulerFragment mTaskScheduler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initTaskScheduler(context);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(Constants.LOG_TAG, "creating view");
        View rootView = inflater.inflate(R.layout.fragment_add_words, container, false);

        ButterKnife.bind(this, rootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new WordBundleAdapter(null, R.layout.word_bundle_layout, this.getActivity());
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
                // the word is empty, clear the recycleview
                // mRecyclerView.swapAdapter(null, true);
            } else {
                if (btnDeleteWord.getVisibility() == View.INVISIBLE
                        && !edtWord.getText().toString().isEmpty()) {
                    this.animateToVisibilityState(btnDeleteWord.getId(), View.VISIBLE);
                }
            }
            // load new helper words
            mTaskScheduler.newTaskForClient(
                    new GetHelpWordsTask(this.getContext(),
                            edtWord.getText().toString()), mAdapter);
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
        animator.setDuration(300);
        animator.addListener(new MyAnimatorListener(this, id, visibility));
        animator.start();
    }
}