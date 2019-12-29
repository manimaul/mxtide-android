@file:JvmName("StationPredictionFactory")

package com.mxmariner.mxtide.internal

import androidx.annotation.Keep
import com.mxmariner.mxtide.api.IStationPrediction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

@Keep
@Suppress("unused") //  JNI Function
internal fun createPrediction(epoch: Long,
                              timeZoneId: String,
                              data: Any): StationPrediction<Any> {
    val tz = DateTimeZone.forID(timeZoneId)
    val date = DateTime(epoch, tz)
    return StationPrediction(date, data)
}

@Keep
internal class StationPrediction<out T>(override val date: DateTime,
                                        override val value: T) : IStationPrediction<T>