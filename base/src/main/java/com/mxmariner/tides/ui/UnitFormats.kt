package com.mxmariner.tides.ui

import android.content.res.Resources
import android.location.Location
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.mxtide.api.distanceToPoint
import com.mxmariner.tides.R
import com.mxmariner.tides.settings.Preferences
import java.text.DecimalFormat

class UnitFormats(kodein: Kodein) {

  companion object {
    const val METER_FOOT = 0.3048
  }

  private val preferences: Preferences = kodein.instance()
  private val resources: Resources = kodein.instance()
  private val distanceFormat = DecimalFormat("0.00")

  val speedPostFix: Int
    get() {
      return when (preferences.predictionSpeed) {
        MeasureUnit.METRIC -> R.string.kph
        MeasureUnit.STATUTE -> R.string.mph
        MeasureUnit.NAUTICAL -> R.string.kts
      }
    }

  val levelPostFix: Int
    get() {
      return when (preferences.predictionLevels) {
        MeasureUnit.METRIC -> R.string.mt
        MeasureUnit.STATUTE,
        MeasureUnit.NAUTICAL -> R.string.ft
      }
    }

  fun distanceFormated(location: Location, station: IStation) : String {
    val meters = distanceToPoint(location.latitude, location.latitude, station.latitude, station.longitude)
    val value = when (preferences.predictionLevels) {
      MeasureUnit.METRIC -> distanceFormat.format(meters)
      MeasureUnit.STATUTE,
      MeasureUnit.NAUTICAL -> distanceFormat.format(meToFt(meters))
    }
    return "$value ${resources.getString(levelPostFix)}"
  }

  fun meToFt(meters: Double): Double {
    return Math.round(meters / METER_FOOT * 100).toDouble() / 100
  }
}