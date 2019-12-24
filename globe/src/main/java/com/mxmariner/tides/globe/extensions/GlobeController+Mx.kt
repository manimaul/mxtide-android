package com.mxmariner.tides.globe.extensions

import com.mousebird.maply.GlobeController
import com.mxmariner.tides.globe.data.GlobePosition

internal fun GlobeController.setPosition(position: GlobePosition) {
    setPositionGeo(position.x, position.y, position.z)
}