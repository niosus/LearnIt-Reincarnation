package com.learnit.learnit.preferences;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.jenzz.materialpreference.SwitchPreference;
import com.learnit.learnit.utils.Constants;
import com.pixplicity.easyprefs.library.Prefs;

/**
 * Created by igor on 24/10/15.
 */
public class MySwitchPreference extends SwitchPreference {
    public MySwitchPreference(Context context) {
        super(context);
        init();
    }

    public MySwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

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
