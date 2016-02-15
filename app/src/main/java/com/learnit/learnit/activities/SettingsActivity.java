/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.learnit.learnit.R;
import com.learnit.learnit.preferences.MySwitchPreference;
import com.learnit.learnit.preferences.TimePickerPref;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        TimePickerPref timePickerPref;
        MySwitchPreference mNotificationSwitchPref;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.prefs);

            Preference.OnPreferenceChangeListener changeListener = new MyOnPrefChangeListener(getActivity());

            mNotificationSwitchPref = (MySwitchPreference) findPreference(getString(R.string.key_pref_notifications_active));
            mNotificationSwitchPref.setOnPreferenceChangeListener(changeListener);

            timePickerPref = (TimePickerPref) findPreference(getString(R.string.key_time_to_start));
            timePickerPref.setFragmentManager(getFragmentManager());
            timePickerPref.setOnPreferenceChangeListener(changeListener);
        }

        private boolean notificationsOn() {
            return mNotificationSwitchPref.isChecked();
        }

        public class MyOnPrefChangeListener implements android.support.v7.preference.Preference.OnPreferenceChangeListener {
            Context mContext;

            public MyOnPrefChangeListener(Context context) {
                mContext = context;
            }

            @Override
            public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object newValue) {
                if (preference.getKey().equals(mContext.getString(R.string.key_pref_notifications_active))) {
                    Log.d(Constants.LOG_TAG, "pref changes to " + newValue);
                    boolean isOn = (boolean) newValue;
                    if (isOn) {
                        Utils.startRepeatingTimer(mContext);
                        return true;
                    } else {
                        Utils.cancelRepeatingTimer(mContext, true);
                        return true;
                    }
                } else if (preference.getKey().equals(mContext.getString(R.string.key_time_to_start))) {
                    Log.d(Constants.LOG_TAG, "start time preference changed");
                    if (mNotificationSwitchPref.isChecked()) {
                        Utils.cancelRepeatingTimer(mContext, false);
                        Utils.startRepeatingTimer(mContext);
                    }
                }
                return false;
            }
        }
    }
}
