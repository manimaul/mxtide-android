package com.mxmariner.tides.main.application

import android.app.Activity
import android.app.Application
import com.mxmariner.tides.di.components.DaggerApplicationComponent
import com.mxmariner.tides.main.repository.HarmonicsRepo
import com.mxmariner.tides.main.util.PerfTimer
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject


class MxTidesApplication : Application(), HasActivityInjector {

    @Inject lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var harmonicsRepo: HarmonicsRepo

    override fun onCreate() {
        PerfTimer.markEventStart("MxTidesApplication.onCreate()")
        super.onCreate()

        DaggerApplicationComponent.builder()
                .application(this)
                .build()
                .inject(this)

        harmonicsRepo.initializeAsync()

        PerfTimer.markEventStop("MxTidesApplication.onCreate()")
        PerfTimer.markEventStart("Between")
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingActivityInjector
    }
}