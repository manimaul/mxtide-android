package com.mxmariner.mxtide.internal

import com.mxmariner.mxtide.api.IStationPrediction
import java.util.*

class StationPrediction<out T>(override val date: Date,
                               override val formattedTime: String,
                               override val value: T) : IStationPrediction<T>