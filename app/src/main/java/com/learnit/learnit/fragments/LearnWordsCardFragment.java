package com.learnit.learnit.fragments;/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learnit.learnit.R;
import com.learnit.learnit.async_tasks.GetRandomUserWordsTask;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.interfaces.IRefreshable;
import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LearnWordsCardFragment
        extends Fragment
        implements IAsyncTaskResultClient, IRefreshable {

    private static final String ARG_POSITION = "position";

    @Bind(R.id.query_word)
    TextView mQueryWord;


    private TaskSchedulerFragment mTaskScheduler;

    public static LearnWordsCardFragment newInstance(int position) {
        LearnWordsCardFragment f = new LearnWordsCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(Constants.LOG_TAG, "fragment is attached");
        initTaskScheduler();
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_learn_words, container, false);
        ButterKnife.bind(this, rootView);
        ViewCompat.setElevation(rootView, 50);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateWordsAsync();
    }

    private void updateWordsAsync() {
        int numOfWords = 4;
        mTaskScheduler.newTaskForClient(
                new GetRandomUserWordsTask(this.getContext(), numOfWords), this);
    }

    @Override
    public String tag() {
        return "learn_words_fragment";
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(Float progress) {

    }

    @Override @SuppressWarnings("unchecked")
    public <OutType> void onFinish(OutType result) {
        if (result instanceof List) {
            List<WordBundle> words = (List<WordBundle>) result;
            mQueryWord.setText(words.get(0).word());
        }
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void refresh() {
        updateWordsAsync();
    }
}