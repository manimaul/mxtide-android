package com.mxmariner.tides.main.factory

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.tides.main.model.StationListViewPresentation
import com.mxmariner.tides.settings.Preferences
import org.joda.time.DateTime
import org.joda.time.Duration

class StationListViewPresentationFactory(kodein: Kodein) {

    private val preferences: Preferences = kodein.instance()

    fun  createPresentation(station: IStation) : StationListViewPresentation {
        val prediction = station.getPredictionRaw(DateTime.now().minusHours(3),
                Duration.standardHours(6), preferences.predictionLevels)
        val position = "${station.latitude}, ${station.longitude}"
        return StationListViewPresentation(prediction, station.name, position, station.timeZone)
    }
}