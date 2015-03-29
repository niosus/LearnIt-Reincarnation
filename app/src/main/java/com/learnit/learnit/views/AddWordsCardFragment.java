package com.learnit.learnit.views;/*
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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.learnit.learnit.R;
import com.learnit.learnit.utils.Constants;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddWordsCardFragment extends Fragment {
    private ObservableScrollViewCallbacks mScrollCallback = null;

    private static final String ARG_POSITION = "position";

    @InjectView(R.id.addWordsListView) ObservableListView addWordsListView;

    private int position;

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
        position = getArguments().getInt(ARG_POSITION);
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
        View rootView = inflater.inflate(R.layout.fragment_card_add_words,container,false);
        ButterKnife.inject(this, rootView);
        if (mScrollCallback != null) {
            Log.d(Constants.LOG_TAG, "setting scroll callback.");
            addWordsListView.setScrollViewCallbacks(mScrollCallback);
        }
        ViewCompat.setElevation(rootView, 50);
        return rootView;
    }
}