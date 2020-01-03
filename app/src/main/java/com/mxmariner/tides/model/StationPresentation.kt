package com.mxmariner.tides.model

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mxmariner.mxtide.api.IStationPrediction
import com.mxmariner.tides.extensions.differenceHoursMinutes
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class StationPresentation(
        val prediction: List<IStationPrediction<Float>>,
        val predictionNow: String,
        val name: String,
        val position: String,
        val timeZone: DateTimeZone,
        val distance: String,
        val start: DateTime,
        val now: DateTime,
        val end: DateTime,
        @ColorInt val color: Int,
        @ColorInt val nowColor: Int,
        @DrawableRes val icon: Int,
        @StringRes val yValAbrv: Int
) {

  val scaleHours: Float
    get() = end.differenceHoursMinutes(start)
}