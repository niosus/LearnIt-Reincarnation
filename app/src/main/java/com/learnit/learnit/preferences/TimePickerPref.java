/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.preferences;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.learnit.learnit.R;
import com.learnit.learnit.utils.Constants;
import com.pixplicity.easyprefs.library.Prefs;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimePickerPref extends android.support.v7.preference.Preference
 implements RadialTimePickerDialogFragment.OnTimeSetListener, Preference.OnPreferenceClickListener {
    Context mContext = null;
    FragmentManager mFragmentManager = null;

    @SuppressWarnings("unused")
    public TimePickerPref(Context context) {
        super(context);
        mContext = context;
        init();
    }

    @SuppressWarnings("unused")
    public TimePickerPref(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @SuppressWarnings("unused")
    public TimePickerPref(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @SuppressWarnings("unused")
    public TimePickerPref(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    private void init() {
        int millisOfDay = Prefs.getInt(mContext.getString(R.string.key_time_to_start), -1);
        LocalTime localTime;
        if (millisOfDay > 0) {
            localTime = LocalTime.fromMillisOfDay(millisOfDay);
        } else {
            localTime = new LocalTime();
        }
        setTimeSummary(localTime);

        this.setOnPreferenceClickListener(this);
    }

    private void setTimeSummary(LocalTime time) {
        DateTimeFormatter pattern = DateTimeFormat.forPattern("HH:mm");
        String timeStr = pattern.print(time);
        setSummary(String.format(mContext.getString(R.string.pref_time_to_start_summary), timeStr));
    }

    public void setFragmentManager(FragmentManager manager) {
        mFragmentManager = manager;
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minuteOfHour) {
        Log.d(Constants.LOG_TAG, "changed time " + hourOfDay + ":" + minuteOfHour);

        LocalTime time = new LocalTime(hourOfDay, minuteOfHour);
        Prefs.putInt(mContext.getString(R.string.key_time_to_start), time.getMillisOfDay());
        setTimeSummary(time);
        OnPreferenceChangeListener listener = this.getOnPreferenceChangeListener();
        if (listener != null) {
            Log.d(Constants.LOG_TAG, "listener: " + listener.toString());
            listener.onPreferenceChange(this, null);
        } else {
            Log.e(Constants.LOG_TAG, "pref changed, but nobody cares");
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Log.d(Constants.LOG_TAG, "clicked on time preference");
        if (mFragmentManager == null) {
            Log.e(Constants.LOG_TAG, "fragment manager was not initialized yet");
            return false;
        }
        // local time to be filled
        LocalTime localTime;
        int millisOfDay = Prefs.getInt(mContext.getString(R.string.key_time_to_start), -1);
        if (millisOfDay > 0) {
            // the time was already set by someone
            localTime = LocalTime.fromMillisOfDay(millisOfDay);
        } else {
            localTime = new LocalTime();
        }
        RadialTimePickerDialogFragment timePicker = new RadialTimePickerDialogFragment()
                .setOnTimeSetListener(this)
                .setStartTime(localTime.getHourOfDay(), localTime.getMinuteOfHour());
        timePicker.show(mFragmentManager, "time_picker_frag");
        return true;
    }
}
