package com.learnit.learnit.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.learnit.learnit.R;
import com.learnit.learnit.utils.Constants;
import com.pixplicity.easyprefs.library.Prefs;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Prefs.getBoolean(context.getString(R.string.key_pref_notifications_active), false))
            Log.d(Constants.LOG_TAG, "need to turn on the notifications");
    }
}
