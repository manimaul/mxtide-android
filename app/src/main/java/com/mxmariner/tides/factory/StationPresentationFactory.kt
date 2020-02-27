package com.mxmariner.tides.factory

import android.content.Context
import android.graphics.Color
import android.location.Location
import androidx.core.content.ContextCompat
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.MeasureUnit
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.model.StationPresentation
import com.mxmariner.tides.settings.Preferences
import com.mxmariner.tides.ui.UnitFormats
import com.mxmariner.tides.util.RxLocation
import org.joda.time.DateTime
import org.joda.time.Duration
import javax.inject.Inject

private const val hours = 3

class StationPresentationFactory @Inject constructor(
  private val preferences: Preferences,
  private val unitFormats: UnitFormats,
  private val context: Context,
  private val rxLocation: RxLocation
) {

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
      StationType.TIDES -> Triple(
              ContextCompat.getColor(context, com.mxmariner.tides.R.color.tideColor),
              Color.RED,
              com.mxmariner.tides.R.drawable.ic_tide)
      StationType.CURRENTS -> Triple(
              ContextCompat.getColor(context, com.mxmariner.tides.R.color.currentColor),
              Color.BLACK,
              com.mxmariner.tides.R.drawable.ic_current)
    }

    val distance = (location ?: rxLocation.lastKnownLocation)?.let {
      unitFormats.distanceFormatted(it, station)
    } ?: context.getString(com.mxmariner.tides.R.string.unknown)

    return StationPresentation(prediction, levelNow, station.name, position, station.timeZone,
        distance, start, now, end, rez.first, rez.second, rez.third, abbreviation)
  }
}