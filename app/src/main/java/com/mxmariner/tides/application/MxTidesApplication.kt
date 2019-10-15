package com.mxmariner.tides.application

import android.app.Application
import com.github.salomonbrys.kodein.instance
import com.mxmariner.tides.di.Injector
import com.mxmariner.tides.repository.HarmonicsRepo
import com.mxmariner.tides.util.PerfTimer

class MxTidesApplication : Application() {

    override fun onCreate() {
        PerfTimer.markEventStart("MxTidesApplication.onCreate()")
        super.onCreate()
        val injector = Injector.appScopeAssembly(this)
        registerActivityLifecycleCallbacks(Injector)

        val harmonicsRepo: HarmonicsRepo = injector.instance()
        harmonicsRepo.initializeAsync()

        PerfTimer.markEventStop("MxTidesApplication.onCreate()")
        PerfTimer.markEventStart("Between")
    }
}