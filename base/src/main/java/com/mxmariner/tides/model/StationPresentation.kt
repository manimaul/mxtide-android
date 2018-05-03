package com.mxmariner.tides.model

import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.mxmariner.mxtide.api.IStationPrediction
import org.joda.time.DateTimeZone


class StationPresentation(
        val prediction: List<IStationPrediction<Float>>,
        val name: String,
        val position: String,
        val timeZone: DateTimeZone,
        val distance: String,
        @ColorInt val color: Int,
        @DrawableRes val icon: Int,
        @StringRes val yValAbrv: Int
)