package com.mxmariner.tides.ui

import android.content.res.Resources
import android.location.Location
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.mxtide.api.distanceToPoint
import com.mxmariner.tides.R
import com.mxmariner.tides.settings.Preferences
import java.text.DecimalFormat

class UnitFormats(kodein: Kodein) {

  companion object {
    const val METER_FOOT = 0.3048
    const val METER_MILE = 1609.34
    const val METER_KM = 1000.00
  }

  private val preferences: Preferences = kodein.instance()
  private val resources: Resources = kodein.instance()
  private val distanceFormat = DecimalFormat("0.00")

  val speedPostFix: Int
    get() {
      return getSpeedPostFix(preferences.predictionSpeed)
    }

  private fun getSpeedPostFix(measureUnit: MeasureUnit) : Int {
    return when (measureUnit) {
      MeasureUnit.METRIC -> R.string.kph
      MeasureUnit.STATUTE -> R.string.mph
      MeasureUnit.NAUTICAL -> R.string.kts
    }
  }

  val levelPostFix: Int
    get() {
      return getLevelPostFix(preferences.predictionLevels)
    }

  private fun getLevelPostFix(measureUnit: MeasureUnit): Int {
    return when (measureUnit) {
      MeasureUnit.METRIC -> R.string.mt
      MeasureUnit.STATUTE,
      MeasureUnit.NAUTICAL -> R.string.ft
    }
  }

  fun valueFormatted(level: Float?, unitFormats: MeasureUnit, type: StationType) : String {
    val prefix = when (type) {
      StationType.TIDES -> getLevelPostFix(unitFormats)
      StationType.CURRENTS -> getSpeedPostFix(unitFormats)
    }
    return level?.let {
      "${distanceFormat.format(it)}${resources.getString(prefix)}"
    } ?: resources.getString(R.string.unknown)
  }

  fun distanceFormatted(location: Location, station: IStation) : String {
    val meters = distanceToPoint(location.latitude, location.longitude, station.latitude, station.longitude)
    val value = when (preferences.predictionLevels) {
      MeasureUnit.METRIC -> {
        if (meters > METER_KM) {
          distanceFormat.format(meters / METER_KM) to R.string.km
        } else {
          distanceFormat.format(meters) to R.string.mt
        }
      }
      MeasureUnit.STATUTE,
      MeasureUnit.NAUTICAL -> {
        if (meters > (METER_MILE / 4)) {
          distanceFormat.format(meToMi(meters)) to R.string.mi
        } else {
          distanceFormat.format(meToFt(meters)) to R.string.feet
        }
      }
    }
    return "${value.first} ${resources.getString(value.second)}"
  }

  fun meToFt(meters: Double): Double {
    return Math.round(meters / METER_FOOT * 100).toDouble() / 100
  }

  fun meToMi(meters: Double) : Double {
    return Math.round(meters / METER_MILE * 100).toDouble() / 100
  }
}