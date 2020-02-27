package com.mxmariner.tides.application

import android.app.Application
import com.mxmariner.di.Injector
import com.mxmariner.tides.repository.HarmonicsRepo
import com.mxmariner.tides.util.PerfTimer
import javax.inject.Inject

class MxTidesApplication : Application() {

    @Inject lateinit var harmonicsRepo: HarmonicsRepo

    override fun onCreate() {
        PerfTimer.markEventStart("MxTidesApplication.onCreate()")
        super.onCreate()
        Injector.appInjector(this).inject(this)
        registerActivityLifecycleCallbacks(Injector)
        harmonicsRepo.initializeAsync()

        PerfTimer.markEventStop("MxTidesApplication.onCreate()")
        PerfTimer.markEventStart("Between")
    }
}