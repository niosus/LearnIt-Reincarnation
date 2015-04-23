/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.learnit.learnit.R;
import com.learnit.learnit.utils.Constants;
import com.pixplicity.easyprefs.library.Prefs;

public class ListPreferenceWithSummary extends com.jenzz.materialpreference.Preference {
    private final static int NONE = 0;
    int mEntriesArrayId = NONE;
    Context mContext = null;
    OnPreferenceClickListener mOnClickListener = null;

    public ListPreferenceWithSummary(Context context) {
        super(context);
        mContext = context;
        initOnClickListener();
    }

    public ListPreferenceWithSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initOnClickListener();
        setEntries(attrs);
    }

    public ListPreferenceWithSummary(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initOnClickListener();
        setEntries(attrs);
    }

    public ListPreferenceWithSummary(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initOnClickListener();
        setEntries(attrs);
    }

    private void initOnClickListener() {
        mOnClickListener = new MyOnPreferenceClickListener();
        setOnPreferenceClickListener(mOnClickListener);
        setSummary(Prefs.getString(getKey(), "NONE"));
    }

    private void setEntries(AttributeSet attrs) {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.SummarizedPreference);
        mEntriesArrayId = t.getResourceId(R.styleable.SummarizedPreference_array, NONE);
        t.recycle();

        if (mEntriesArrayId == NONE) {
            Log.e(Constants.LOG_TAG, "the array id is not defined in summarized pref");
        }
    }

    public class MyOnPreferenceClickListener implements OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (mEntriesArrayId == NONE) {
                Log.e(Constants.LOG_TAG, "no array defined for list preference '" + getKey() + "'");
                return false;
            }
            new MaterialDialog.Builder(mContext)
                    .title(getTitleRes())
                    .items(mEntriesArrayId)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            setSummary(text);
                            Prefs.putString(getKey(), text.toString());
                            return true;
                        }
                    })
                    .show();
            return true;
        }
    }
}
