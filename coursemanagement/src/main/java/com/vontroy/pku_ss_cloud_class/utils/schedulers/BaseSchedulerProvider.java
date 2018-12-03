package com.vontroy.pku_ss_cloud_class.utils.schedulers;

import android.support.annotation.NonNull;

import rx.Scheduler;

/**
 * Allow providing different types of {@link Scheduler}s.
 */
public interface BaseSchedulerProvider {

    @NonNull
    Scheduler computation();

    @NonNull
    Scheduler io();

    @NonNull
    Scheduler ui();
}
