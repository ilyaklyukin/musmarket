package com.umnix.musmarket.job;


import android.content.Context;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.birbit.android.jobqueue.config.Configuration;
import com.umnix.musmarket.MusMarketApplication;
import com.umnix.musmarket.bus.StoreBus;

import javax.inject.Inject;

import timber.log.Timber;

public class LoadStoresJob extends Job {

    private JobManager jobManager;

    @Inject
    transient protected StoreBus storeBus;

    public LoadStoresJob(Context context) {
        super(new Params(JobPriority.NORMAL).requireNetwork().groupBy("load-stores").persist());
        Configuration.Builder builder = new Configuration.Builder(context);
        jobManager = new JobManager(builder.build());
    }

    public LoadStoresJob start() {
        jobManager.addJobInBackground(this);
        return this;
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
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        Timber.e(throwable, throwable.getMessage());

        return RetryConstraint.CANCEL;
    }
}
