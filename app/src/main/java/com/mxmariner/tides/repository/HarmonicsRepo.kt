package com.mxmariner.tides.repository

import android.content.Context
import android.os.AsyncTask
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.ITidesAndCurrents
import com.mxmariner.tides.util.PerfTimer

class HarmonicsRepo(kodein: Kodein) {

    private val context: Context = kodein.instance()
    val tidesAndCurrents: ITidesAndCurrents = kodein.instance()

    fun initializeAsync() {
        AsyncTask.execute {
            PerfTimer.markEventStart("HarmonicsRepo.initialize()")
            tidesAndCurrents.addHarmonicsFile(context, "harmonics_dwf_20190620_free_tcd")
            PerfTimer.markEventStop("HarmonicsRepo.initialize()")
        }
    }
}
