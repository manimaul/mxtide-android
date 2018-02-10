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