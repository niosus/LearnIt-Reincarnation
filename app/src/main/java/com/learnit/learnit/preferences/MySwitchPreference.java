package com.learnit.learnit.preferences;

import android.content.Context;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.AttributeSet;

import com.pixplicity.easyprefs.library.Prefs;

public class MySwitchPreference extends SwitchPreferenceCompat {
    @SuppressWarnings("unused")
    public MySwitchPreference(Context context) {
        super(context);
        init();
    }

    @SuppressWarnings("unused")
    public MySwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    @SuppressWarnings("unused")
    public MySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    @SuppressWarnings("unused")
    public MySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init() {
        setChecked(Prefs.getBoolean(getKey(), false));
    }

    @Override
    protected void onClick() {
        super.onClick();
        Prefs.putBoolean(getKey(), isChecked());
    }
}
