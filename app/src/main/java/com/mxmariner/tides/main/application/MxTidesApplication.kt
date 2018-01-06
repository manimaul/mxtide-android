package com.mxmariner.tides.main.application

import android.app.Application
import android.os.AsyncTask
import com.mxmariner.tides.main.repository.HarmonicsRepo
import com.mxmariner.tides.main.util.PerfTimer

class MxTidesApplication : Application() {

    override fun onCreate() {
        PerfTimer.markEventStart("MxTidesApplication.onCreate()")
        super.onCreate()

        AsyncTask.execute {
            PerfTimer.markEventStart("HarmonicsRepo.initialize()")
            HarmonicsRepo.initialize(this)
            PerfTimer.markEventStop("HarmonicsRepo.initialize()")
        }

        PerfTimer.markEventStop("MxTidesApplication.onCreate()")
        PerfTimer.markEventStart("Between")
    }
}