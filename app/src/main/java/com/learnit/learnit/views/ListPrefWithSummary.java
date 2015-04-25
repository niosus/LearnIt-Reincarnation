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

public class ListPrefWithSummary extends com.jenzz.materialpreference.Preference {
    private final static int NONE = -1;
    protected int mDefaultEntryIndex = NONE;
    int mEntriesArrayId = NONE;
    Context mContext = null;
    OnPreferenceClickListener mOnClickListener = null;

    @SuppressWarnings("unused")
    public ListPrefWithSummary(Context context) {
        super(context);
        mContext = context;
        init();
    }

    @SuppressWarnings("unused")
    public ListPrefWithSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
        setEntries(attrs);
    }

    @SuppressWarnings("unused")
    public ListPrefWithSummary(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
        setEntries(attrs);
    }

    @SuppressWarnings("unused")
    public ListPrefWithSummary(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
        setEntries(attrs);
    }

    private void init() {
        mOnClickListener = new MyOnPreferenceClickListener();
        setOnPreferenceClickListener(mOnClickListener);
    }

    private void setEntries(AttributeSet attrs) {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.SummarizedPreference);
        mEntriesArrayId = t.getResourceId(R.styleable.SummarizedPreference_array, NONE);
        mDefaultEntryIndex = t.getInt(R.styleable.SummarizedPreference_defaultValueIndex, NONE);
        t.recycle();

        if (mEntriesArrayId == NONE) {
            Log.e(Constants.LOG_TAG, "the array id is not defined in summarized pref");
        }

        Log.d(Constants.LOG_TAG, "the index of default value is " + mDefaultEntryIndex);

        String defaultStr = getContext().getString(R.string.hello_world);
        String summaryToSet = Prefs.getString(getKey(), defaultStr);

        // handle the situation when the default value index is set
        if (mDefaultEntryIndex != NONE && summaryToSet.equals(defaultStr)) {
            // if we store no value yet and default entry is set --> set default val for summary
            String[] array = getContext().getResources().getStringArray(mEntriesArrayId);
            setSummary(array[mDefaultEntryIndex]);
        } else {
            // otherwise, just go for default string e.g. "NONE"
            setSummary(defaultStr);
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
