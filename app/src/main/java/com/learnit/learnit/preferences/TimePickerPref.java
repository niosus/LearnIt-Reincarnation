/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.preferences;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.learnit.learnit.R;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.Calendar;

public class TimePickerPref extends android.support.v7.preference.Preference
 implements RadialTimePickerDialogFragment.OnTimeSetListener {
    private final static int NONE = -1;
    protected int mDefaultEntryIndex = NONE;
    int mEntriesArrayId = NONE;
    Context mContext = null;
    OnPreferenceClickListener mOnClickListener = null;

    @SuppressWarnings("unused")
    public TimePickerPref(Context context) {
        super(context);
        mContext = context;
    }

    @SuppressWarnings("unused")
    public TimePickerPref(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @SuppressWarnings("unused")
    public TimePickerPref(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @SuppressWarnings("unused")
    public TimePickerPref(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        Log.d(Constants.LOG_TAG, "changed time " + hourOfDay + ":" + minute);
        setSummary(String.format(mContext.getString(R.string.pref_time_to_start_summary),
                hourOfDay, minute));
    }
}
