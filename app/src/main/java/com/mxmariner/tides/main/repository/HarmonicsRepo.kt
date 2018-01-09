package com.mxmariner.tides.main.repository

import android.content.Context
import android.os.AsyncTask
import com.mxmariner.mxtide.api.createTidesAndCurrents
import com.mxmariner.tides.R
import com.mxmariner.tides.main.util.PerfTimer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HarmonicsRepo @Inject constructor(private val context: Context) {

    val tidesAndCurrents = createTidesAndCurrents()

    fun initializeAsync() {
        AsyncTask.execute {
            PerfTimer.markEventStart("HarmonicsRepo.initialize()")
            tidesAndCurrents.addHarmonicsFile(context, R.raw.harmonics_dwf_20161231_free_tcd)
            PerfTimer.markEventStop("HarmonicsRepo.initialize()")
        }
    }
}

