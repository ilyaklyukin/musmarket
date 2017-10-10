package com.umnix.log;

import com.umnix.musmarket.log.ReleaseTree;
import com.umnix.musmarket.log.TimberLog;

import timber.log.Timber;


public class TimberLogImplementation implements TimberLog {

    public static void init() {
        Timber.plant(new ReleaseTree());
    }
}
