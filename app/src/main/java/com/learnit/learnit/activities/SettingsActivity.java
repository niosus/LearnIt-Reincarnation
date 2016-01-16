/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.activities;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.learnit.learnit.R;
import com.learnit.learnit.preferences.MySwitchPreference;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.Utils;

public class SettingsActivity
        extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment
    implements Preference.OnPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);

            MySwitchPreference notificationsSwitch = (MySwitchPreference) findPreference(getString(R.string.key_pref_notifications_active));
            notificationsSwitch.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Log.d(Constants.LOG_TAG, "pref changes to " + newValue);
            if ((boolean) newValue) {
                Utils.startRepeatingTimer(getActivity());
                return true;
            } else {
                Utils.cancelRepeatingTimer(getActivity());
                return true;
            }
        }
    }
}
