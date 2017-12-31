@file:JvmName("StationPredictionFactory")

package com.mxmariner.mxtide.internal

import com.mxmariner.mxtide.api.IStationPrediction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

@Suppress("unused") //  JNI Function
internal fun createPrediction(epoch: Long,
                              timeZoneId: String,
                              data: Any): StationPrediction<Any> {
    val tz = DateTimeZone.forID(timeZoneId)
    val date = DateTime(epoch, tz)
    return StationPrediction(date, data)
}

internal class StationPrediction<out T>(override val date: DateTime,
                                        override val value: T) : IStationPrediction<T>