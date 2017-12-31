package com.mxmariner.mxtide.api

import org.joda.time.DateTime

interface IStationPrediction<out T> {
    val date: DateTime
    val value: T
}