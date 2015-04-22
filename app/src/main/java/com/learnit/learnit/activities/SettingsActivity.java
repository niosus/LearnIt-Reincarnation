/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jenzz.materialpreference.Preference;
import com.learnit.learnit.R;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);

            Preference prefLandToLearn = (Preference) findPreference(getString(R.string.key_language_to_learn));
            prefLandToLearn.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(android.preference.Preference preference) {
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.hello_world)
                            .items(R.array.entries_languages_to_learn)
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    Preference prefLandToLearn = (Preference) findPreference(getString(R.string.key_language_to_learn));
                                    prefLandToLearn.setSummary(text);
                                    return true;
                                }
                            })
                            .show();
                    return true;
                }
            });
        }
    }
}
