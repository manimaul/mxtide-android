package com.mxmariner.tides.main.model

import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import com.mxmariner.mxtide.api.IStationPrediction
import org.joda.time.DateTimeZone


class StationListViewPresentation(
        val prediction: List<IStationPrediction<Float>>,
        val name: String,
        val position: String,
        val timeZone: DateTimeZone,
        @ColorInt val color: Int,
        @DrawableRes val icon: Int
)