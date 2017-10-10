package com.umnix.log;


import com.umnix.musmarket.log.TimberLog;

import timber.log.Timber;


public class TimberLogImplementation implements TimberLog {

    public static void init() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected String createStackElementTag(StackTraceElement element) {
                return String.format("C:%s:%s",
                        super.createStackElementTag(element),
                        element.getLineNumber());
            }
        });
    }

}
