package com.mxmariner.mxtide.api

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration

interface IStation {

    /**
     * The station's north-south geographical position coordinate.
     */
    val latitude: Double

    /**
     * The station's east-west geographical position coordinate.
     */
    val longitude: Double

    /**
     * The station's local time zone.
     */
    val timeZone: DateTimeZone

    /**
     * The station's name
     */
    val name: String

    /**
     * The station's type
     */
    val type: StationType

    /**
     * Create a tidal prediction for a given date and duration represented by a list of dates and [Float] values.
     *
     * Example:
     * <DateTime> : 11.731
     * <DateTime> : 12.3763
     * <DateTime> : 12.1807
     * <DateTime> : 11.2562
     * <DateTime> : 9.84712
     * <DateTime> : 8.39937
     * <DateTime> : 7.40104
     * <DateTime> : 7.13547
     * <DateTime> : 7.60222
     * <DateTime> : 8.58543
     * <DateTime> : 9.70324
     * <DateTime> : 10.4639
     * <DateTime> : 10.4389
     * <DateTime> : 9.44929
     * <DateTime> : 7.57995
     * <DateTime> : 5.08994
     * <DateTime> : 2.4101
     * <DateTime> : 0.166078
     * <DateTime> : -0.9948
     * <DateTime> : -0.719955
     * <DateTime> : 0.898342
     * <DateTime> : 3.44504
     * <DateTime> : 6.36876
     * <DateTime> : 9.09393
     *
     * @param date the prediction start date.
     * @param duration the prediction duration.
     * @return the tidal prediction values.
     */
    fun getPredictionRaw(date: DateTime,
                         duration: Duration,
                         measureUnit: MeasureUnit): List<IStationPrediction<Float>>
}
