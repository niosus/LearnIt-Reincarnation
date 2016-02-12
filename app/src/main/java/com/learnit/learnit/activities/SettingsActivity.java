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

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
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

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.prefs);

            MySwitchPreference notificationsSwitch = (MySwitchPreference) findPreference(getString(R.string.key_pref_notifications_active));
            notificationsSwitch.setOnPreferenceChangeListener(new MyOnPrefChangeListener(getActivity()));

            timePickerPref = (TimePickerPref) findPreference("time_picker_pref_key");
            timePickerPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                            .setOnTimeSetListener(timePickerPref)
                            .setStartTime(10, 10);
                    rtpd.show(getActivity().getSupportFragmentManager(), "time_picker_frag");
                    return true;
                }
            });
        }

        public static class MyOnPrefChangeListener implements android.support.v7.preference.Preference.OnPreferenceChangeListener {
            Context mContext;

            public MyOnPrefChangeListener(Context context) {
                mContext = context;
            }

            @Override
            public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object newValue) {
                if (preference.getKey().equals(mContext.getString(R.string.key_pref_notifications_active))) {
                    Log.d(Constants.LOG_TAG, "pref changes to " + newValue);
                    if ((boolean) newValue) {
                        Utils.startRepeatingTimer(mContext);
                        return true;
                    } else {
                        Utils.cancelRepeatingTimer(mContext);
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
