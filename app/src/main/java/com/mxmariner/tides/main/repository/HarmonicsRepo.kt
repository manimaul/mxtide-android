package com.mxmariner.tides.main.repository

import android.content.Context
import com.mxmariner.mxtide.api.createTidesAndCurrents
import com.mxmariner.tides.R

object HarmonicsRepo {

    val tidesAndCurrents = createTidesAndCurrents()

    fun initialize(context: Context) {
        tidesAndCurrents.addHarmonicsFile(context, R.raw.harmonics_dwf_20161231_free_tcd)
    }

}

