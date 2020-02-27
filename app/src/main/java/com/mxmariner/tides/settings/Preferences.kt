package com.mxmariner.tides.settings

import android.content.SharedPreferences
import android.content.res.Resources
import com.mxmariner.di.AppScope
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.tides.R
import javax.inject.Inject

@AppScope
class Preferences @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val resources: Resources
) {

    val predictionLevels: MeasureUnit
        get() = when (sharedPreferences.getString(resources.getString(R.string.PREF_KEY_TIDE_LEVEL), MeasureUnit.METRIC.name)) {
            MeasureUnit.METRIC.name -> MeasureUnit.METRIC
            MeasureUnit.STATUTE.name -> MeasureUnit.STATUTE
            else -> MeasureUnit.METRIC
        }

    val predictionSpeed: MeasureUnit
        get() = when (sharedPreferences.getString(resources.getString(R.string.PREF_KEY_CURRENT_SPEED), MeasureUnit.METRIC.name)) {
            MeasureUnit.METRIC.name -> MeasureUnit.METRIC
            MeasureUnit.STATUTE.name -> MeasureUnit.STATUTE
            MeasureUnit.NAUTICAL.name -> MeasureUnit.NAUTICAL
            else -> MeasureUnit.METRIC
        }
}