package com.mxmariner.mxtide.api

import java.util.*

interface IStationPrediction<T> {
    val date: Date
    val formattedTime: String
    val value: T
}