/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.preferences;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.learnit.learnit.R;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;
import com.pixplicity.easyprefs.library.Prefs;

public class ListPrefWithSummary extends com.jenzz.materialpreference.Preference {
    private final static int NONE = -1;
    protected int mDefaultEntryIndex = NONE;
    int mEntriesArrayId = NONE;
    Context mContext = null;
    Preference.OnPreferenceClickListener mOnClickListener = null;

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
        Resources res = getContext().getResources();
        if (res == null) {
            Log.e(Constants.LOG_TAG, "resources not found while setting entries for preference");
            return;
        }

        String defaultStr = res.getString(R.string.lang_undefined);
        int storedLangIndex = Prefs.getInt(getKey(), NONE);

        // if we store no value yet and default entry is set --> set default val for summary
        if (storedLangIndex == NONE && mDefaultEntryIndex != NONE) {
            String[] array = getContext().getResources().getStringArray(mEntriesArrayId);
            setSummary(array[mDefaultEntryIndex]);
            mDefaultEntryIndex = Utils.updateLangIndexIfNeeded(getContext(), mDefaultEntryIndex);
            Prefs.putInt(getKey(), mDefaultEntryIndex);
            return;
        }

        // if we have a stored value for language
        if (storedLangIndex != NONE) {
            String[] array = getContext().getResources().getStringArray(mEntriesArrayId);
            setSummary(array[storedLangIndex]);
            return;
        }

        // no stored value and no default value
        setSummary(defaultStr);
    }

    public class MyOnPreferenceClickListener implements Preference.OnPreferenceClickListener {

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
                        public boolean onSelection(MaterialDialog dialog, View view, int pickedIndex, CharSequence text) {
                            Log.d(Constants.LOG_TAG, "checked list item with index " + pickedIndex);
                            setSummary(text);
                            if (getKey().equals(getContext().getString(R.string.key_language_to_learn))
                                    || getKey().equals(getContext().getString(R.string.key_language_you_know))) {
                                pickedIndex = Utils.updateLangIndexIfNeeded(getContext(), pickedIndex);
                            }
                            // put the appropriate index into shared prefs
                            Prefs.putInt(getKey(), pickedIndex);
                            return true;
                        }
                    })
                    .show();
            return true;
        }
    }
}
