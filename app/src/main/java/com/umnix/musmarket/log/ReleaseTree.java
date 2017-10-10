package com.umnix.musmarket.log;

import timber.log.Timber;

import static android.util.Log.ERROR;
import static android.util.Log.WARN;


public class ReleaseTree extends Timber.DebugTree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == ERROR || priority == WARN) {
            //Crashlytics.log(priority, tag, message);
        }
    }
}
