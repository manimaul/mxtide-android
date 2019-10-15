package com.mxmariner.tides.settings

import android.content.SharedPreferences
import android.content.res.Resources
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.tides.R

class Preferences(kodein: Kodein) {

    private val sharedPreferences: SharedPreferences = kodein.instance()
    private val resources: Resources = kodein.instance()

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