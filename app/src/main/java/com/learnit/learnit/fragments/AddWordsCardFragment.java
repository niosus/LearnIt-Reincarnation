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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.learnit.learnit.R;
import com.learnit.learnit.async_tasks.AddUserDictWordsTask;
import com.learnit.learnit.async_tasks.GetHelpWordsTask;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.interfaces.IUiEvents;
import com.learnit.learnit.interfaces.IFabEventHandler;
import com.learnit.learnit.interfaces.IFabStateController;
import com.learnit.learnit.types.ClearBtnOnClickListener;
import com.learnit.learnit.types.LanguagePair;
import com.learnit.learnit.types.TabsPagerAdapter;
import com.learnit.learnit.types.WordBundleAdapter;
import com.learnit.learnit.types.TextChangeListener;
import com.learnit.learnit.utils.AnimationUtils;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

import java.util.List;

import at.markushi.ui.CircleButton;
import butterknife.Bind;
import butterknife.ButterKnife;

public class AddWordsCardFragment extends Fragment
        implements IUiEvents, IFabEventHandler, IAsyncTaskResultClient {
    private static final String ARG_POSITION = "position";
    private WordBundleAdapter mAdapter;

    private static String TAG = "add_words_card_fragment";

    @Bind(R.id.helper_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.edt_add_word)
    AppCompatEditText mEdtAddWord;
    @Bind(R.id.edt_add_trans)
    AppCompatEditText mEdtAddTrans;
    @Bind(R.id.btn_delete_word_add_words)
    CircleButton mBtnDeleteWord;
    @Bind(R.id.btn_delete_trans_add_words)
    CircleButton mBtnDeleteTrans;

    private TaskSchedulerFragment mTaskScheduler;
    private IFabStateController mFabStateController;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(Constants.LOG_TAG, "fragment is attached");
        initTaskScheduler();
        if (context instanceof IFabStateController) {
            mFabStateController = (IFabStateController) context;
        }
    }

    private void initTaskScheduler() {
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
        Log.d(Constants.LOG_TAG, "creating new instance of fragment");
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
    public void onResume() {
        super.onResume();
        LanguagePair.Names langPair = Utils.getCurrentLanguageNames(getContext());
        mEdtAddWord.setHint(String.format(getString(R.string.add_word_hint), langPair.langToLearn()));
        mEdtAddTrans.setHint(String.format(getString(R.string.add_translation_hint), langPair.langYouKnow()));
        mFabStateController.addFabEventHandler(TabsPagerAdapter.ADD_WORDS_ITEM, this);
        startLoadingHelpWordsAsync();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(Constants.LOG_TAG, "creating view");
        View rootView = inflater.inflate(R.layout.fragment_add_words, container, false);

        ButterKnife.bind(this, rootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new WordBundleAdapter(null,
                R.layout.word_bundle_layout,
                mFabStateController,
                this);
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
        mRecyclerView.setVisibility(View.GONE);

        ViewCompat.setElevation(rootView, 50);
        mEdtAddWord.addTextChangedListener(new TextChangeListener(this, mEdtAddWord.getId()));
        mEdtAddTrans.addTextChangedListener(new TextChangeListener(this, mEdtAddTrans.getId()));

        View.OnClickListener myOnClickListener = new ClearBtnOnClickListener(this);
        mBtnDeleteWord.setOnClickListener(myOnClickListener);
        mBtnDeleteWord.setVisibility(View.INVISIBLE);
        mBtnDeleteTrans.setOnClickListener(myOnClickListener);
        mBtnDeleteTrans.setVisibility(View.INVISIBLE);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateDeleteButtonState(mBtnDeleteWord);
        updateDeleteButtonState(mBtnDeleteTrans);
    }

    private void startLoadingHelpWordsAsync() {
        Integer limit = 20;
        // load new helper words
        mTaskScheduler.newTaskForClient(
                new GetHelpWordsTask(this.getContext(),
                        mEdtAddWord.getText().toString(), limit), mAdapter);
    }

    private void updateDeleteButtonState(final View btnView) {
        EditText editWordToCheck;
        switch (btnView.getId()) {
            case R.id.btn_delete_word_add_words:
                editWordToCheck = mEdtAddWord;
                break;
            case R.id.btn_delete_trans_add_words:
                editWordToCheck = mEdtAddTrans;
                break;
            default:
                Log.e(Constants.LOG_TAG, "undefined case when updating delete button state");
                return;
        }
        int resultVisibility = View.NO_ID;
        if (editWordToCheck.getText().toString().isEmpty()
                && btnView.getVisibility() == View.VISIBLE) {
            resultVisibility = View.INVISIBLE;
        } else {
            if (btnView.getVisibility() == View.INVISIBLE
                    && !editWordToCheck.getText().toString().isEmpty()) {
                resultVisibility = View.VISIBLE;
            }
        }
        if (resultVisibility == View.NO_ID) {
            // there has been no update to visibility, no need to change
            return;
        }
        try {
            AnimationUtils.animateToVisibilityState(btnView, resultVisibility, this);
        } catch (IllegalStateException e) {
            Log.w(Constants.LOG_TAG, "trying to run animation on a detached view. Not sure what exactly causes it.");
            setViewVisibilityState(btnView.getId(), resultVisibility);
        }
    }

    @Override
    public void onTextChanged(EditTextType type) {
        switch (type) {
            case WORD:
                updateDeleteButtonState(mBtnDeleteWord);
                startLoadingHelpWordsAsync();
                break;
            case TRANS:
                updateDeleteButtonState(mBtnDeleteTrans);
                break;
        }
    }

    @Override
    public void setViewVisibilityState(int id, int visibility) {
        Log.d(Constants.LOG_TAG, "changing visibility of " + id + " to " + visibility);
        switch (id) {
            case R.id.btn_delete_word_add_words:
                mBtnDeleteWord.setVisibility(visibility);
                break;
            case R.id.btn_delete_trans_add_words:
                mBtnDeleteTrans.setVisibility(visibility);
                break;
            case R.id.helper_list:
                mRecyclerView.setVisibility(visibility);
                break;
            default:
                Log.e(Constants.LOG_TAG, "unhandled switch setViewVisibilityState");
        }
    }

    @Override
    public void onClearWord(EditTextType type) {
        switch (type) {
            case WORD:
                mEdtAddWord.setText(null);
                break;
            case TRANS:
                mEdtAddTrans.setText(null);
                break;
        }
        if (!isAdded()) {
            // no activity, so we can't do anything
            return;
        }
        Utils.showKeyboard(getActivity());
    }

    @Override
    public void onResultEmpty() {
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onResultFull() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void fabClicked(int viewPagerPos) {
        // TODO: add a notification of the status of the added words. Was it successful? which ones failed?
        Log.d(Constants.LOG_TAG, "fragment knows that fab was clicked from " + viewPagerPos);
        if (viewPagerPos == TabsPagerAdapter.ADD_WORDS_ITEM) {
            // the fab was clicked on the correct screen, we can process the event
            mTaskScheduler.newTaskForClient(new AddUserDictWordsTask(this.getContext(), mAdapter.getSelectedItems()), this);
            mEdtAddWord.setText("");
        }
    }

    @Override
    public boolean fabNeeded() {
        return mAdapter.hasSelectedItems();
    }

    @Override
    public String tag() {
        return null;
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onProgressUpdate(Float progress) {
    }

    @Override @SuppressWarnings("unchecked")
    public <OutType> void onFinish(OutType result) {
        Log.d(Constants.LOG_TAG, "words added");
        if (result instanceof List) {
            List<Constants.AddWordReturnCode> codes = (List<Constants.AddWordReturnCode>) result;
            for (Constants.AddWordReturnCode code: codes) {
                Log.d(Constants.LOG_TAG, "return code is: " + code);
            }
        }
    }

    @Override
    public void onCancelled() {

    }
}