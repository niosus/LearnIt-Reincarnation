/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.preferences;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.learnit.learnit.R;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;
import com.pixplicity.easyprefs.library.Prefs;

public class TimePickerPref extends android.support.v7.preference.Preference {
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


}
