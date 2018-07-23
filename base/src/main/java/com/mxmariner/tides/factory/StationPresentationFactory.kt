package com.mxmariner.tides.factory

import android.content.Context
import android.location.Location
import android.support.v4.content.ContextCompat
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.model.StationPresentation
import com.mxmariner.tides.settings.Preferences
import com.mxmariner.tides.ui.UnitFormats
import com.mxmariner.tides.util.RxLocation
import org.joda.time.DateTime
import org.joda.time.Duration

private const val hours = 3

class StationPresentationFactory(kodein: Kodein) {

  private val preferences: Preferences = kodein.instance()
  private val unitFormats: UnitFormats = kodein.instance()
  private val context: Context = kodein.instance()
  private val rxLocation: RxLocation = kodein.instance()

  fun createPresentation(station: IStation,
                         location: Location? = null,
                         hrs: Int = hours,
                         dateTime: DateTime = DateTime.now()
  ): StationPresentation {
    val measureUnit: MeasureUnit
    val abbreviation = if (station.type == StationType.TIDES) {
      measureUnit = preferences.predictionLevels
      unitFormats.levelPostFix
    } else {
      measureUnit = preferences.predictionSpeed
      unitFormats.speedPostFix
    }
    val now = dateTime.toDateTime(station.timeZone)
    val levelValueNow = station.getPredictionRaw(now, Duration.standardMinutes(1), measureUnit).firstOrNull()?.value
    val levelNow = unitFormats.valueFormatted(levelValueNow, measureUnit, station.type)
    val start = now.minusHours(hrs)
    val end = now.plusHours(hrs)
    val prediction = station.getPredictionRaw(start,
        Duration.millis(end.millis - start.millis), measureUnit)
    val position = "${station.latitude}, ${station.longitude}"
    val rez = when (station.type) {
      StationType.TIDES -> ContextCompat.getColor(context, com.mxmariner.tides.R.color.tideColor) to com.mxmariner.tides.R.drawable.ic_tide
      StationType.CURRENTS -> ContextCompat.getColor(context, com.mxmariner.tides.R.color.currentColor) to com.mxmariner.tides.R.drawable.ic_current
    }

    val distance = (location ?: rxLocation.lastKnownLocation)?.let {
      unitFormats.distanceFormatted(it, station)
    } ?: context.getString(com.mxmariner.tides.R.string.unknown)

    return StationPresentation(prediction, levelNow, station.name, position, station.timeZone,
        distance, start, end, rez.first, rez.second, abbreviation)
  }
}