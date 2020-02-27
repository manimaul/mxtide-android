package com.mxmariner.tides.repository

import android.content.Context
import android.os.AsyncTask
import com.mxmariner.di.AppScope
import com.mxmariner.mxtide.api.ITidesAndCurrents
import com.mxmariner.tides.util.PerfTimer
import javax.inject.Inject

@AppScope
class HarmonicsRepo @Inject constructor(
    private val context: Context,
    val tidesAndCurrents: ITidesAndCurrents
) {

    fun initializeAsync() {
        AsyncTask.execute {
            PerfTimer.markEventStart("HarmonicsRepo.initialize()")
            tidesAndCurrents.addHarmonicsFile(context, "harmonics_dwf_20190620_free_tcd")
            PerfTimer.markEventStop("HarmonicsRepo.initialize()")
        }
    }
}
