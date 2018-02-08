package com.mxmariner.tides.tides

import com.mxmariner.mxtide.api.IStation
import com.mxmariner.tides.settings.Preferences
import com.mxmariner.tides.tides.model.StationListViewPresentation
import org.joda.time.DateTime
import org.joda.time.Duration
import javax.inject.Inject

class StationListViewPresentationFactory @Inject constructor(private val preferences: Preferences) {

    fun  createPresentation(station: IStation) : StationListViewPresentation {
        val prediction = station.getPredictionRaw(DateTime.now().minusHours(3),
                Duration.standardHours(6), preferences.predictionLevels)
        val position = "${station.latitude}, ${station.longitude}"
        return StationListViewPresentation(prediction, station.name, position, station.timeZone)
    }
}