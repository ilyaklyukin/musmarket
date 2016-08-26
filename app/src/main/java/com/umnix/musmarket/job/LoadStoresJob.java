package com.umnix.musmarket.job;


import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.RetryConstraint;
import com.umnix.musmarket.MusMarketApplication;
import com.umnix.musmarket.bus.StoreBus;

import javax.inject.Inject;

import timber.log.Timber;

public class LoadStoresJob extends Job {


    @Inject
    transient protected JobManager jobManager;

    @Inject
    transient protected StoreBus storeBus;

    public LoadStoresJob() {
        super(new Params(JobPriority.NORMAL).requireNetwork().groupBy("load-stores").persist());
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        MusMarketApplication.getComponent().inject(this);

        //jobManager.addJob(new LoadInstrumentsJob());

        //storeBus.setContentUpdate();
    }

    @Override
    protected void onCancel() {
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        Timber.e(throwable, throwable.getMessage());

        return RetryConstraint.CANCEL;
    }
}
