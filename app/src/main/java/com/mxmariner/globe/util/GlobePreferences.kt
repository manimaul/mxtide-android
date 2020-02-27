package com.mxmariner.globe.util

import android.content.SharedPreferences
import com.mousebird.maply.Point3d
import com.mxmariner.globe.data.GlobePosition
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.mxtide.api.stationTypeFromString
import javax.inject.Inject

private const val positionKey = "positionKey"
private const val displayTypeKey = "displayType"

class GlobePreferences @Inject constructor(
  private val prefs: SharedPreferences
) {
    fun lastPosition(): GlobePosition {
        return GlobePosition.fromString(prefs.getString(positionKey, null))
                ?: GlobePosition(0.003562353551387787, -2.1390063762664795, 0.8240106701850891)
    }

    fun savePosition(position: Point3d) {
        savePosition(GlobePosition(position.z, position.x, position.y))
    }

    private fun savePosition(position: GlobePosition) {
        prefs.edit()
                .putString(positionKey, "$position")
                .apply()
    }

    fun saveSelection(displayType: StationType) {
        prefs.edit()
                .putString(displayTypeKey, displayType.name)
                .apply()
    }

    fun lastSelection(): StationType {
        return stationTypeFromString(prefs.getString(displayTypeKey, null)) ?: StationType.TIDES
    }
}