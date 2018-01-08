package com.mxmariner.tides.main.application

import android.app.Application
import android.os.AsyncTask
import com.mxmariner.tides.main.repository.HarmonicsRepo
import com.mxmariner.tides.main.util.PerfTimer
import android.app.Activity
import com.mxmariner.tides.di.components.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject



class MxTidesApplication : Application(), HasActivityInjector {

    @Inject lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        PerfTimer.markEventStart("MxTidesApplication.onCreate()")
        super.onCreate()

        DaggerApplicationComponent.create().inject(this)

        AsyncTask.execute {
            PerfTimer.markEventStart("HarmonicsRepo.initialize()")
            HarmonicsRepo.initialize(this)
            PerfTimer.markEventStop("HarmonicsRepo.initialize()")
        }

        PerfTimer.markEventStop("MxTidesApplication.onCreate()")
        PerfTimer.markEventStart("Between")
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingActivityInjector
    }
}