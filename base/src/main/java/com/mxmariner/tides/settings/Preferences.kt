package com.mxmariner.tides.settings

import android.content.SharedPreferences
import android.content.res.Resources
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.tides.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(private val sharedPreferences: SharedPreferences,
                                      private val resources: Resources) {

    val predictionLevels: MeasureUnit
        get() = when (sharedPreferences.getString(resources.getString(R.string.PREF_KEY_TIDE_LEVEL), resources.getString(R.string.meters))) {
            resources.getString(R.string.meters) -> MeasureUnit.METRIC
            resources.getString(R.string.feet) -> MeasureUnit.STATUTE
            else -> MeasureUnit.METRIC
        }

    val predictionSpeed: MeasureUnit
        get() = when (sharedPreferences.getString(resources.getString(R.string.PREF_KEY_CURRENT_SPEED), resources.getString(R.string.kph))) {
            resources.getString(R.string.kph) -> MeasureUnit.METRIC
            resources.getString(R.string.mph) -> MeasureUnit.STATUTE
            resources.getString(R.string.kts) -> MeasureUnit.NAUTICAL
            else -> MeasureUnit.METRIC
        }
}