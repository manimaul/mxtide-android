package com.mxmariner.tides.globe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.mousebird.maply.*
import java.io.File
import com.mxmariner.tides.R as RR


private const val cacheDirName = "openstreetmap"
private const val openStreetMapUrl = "https://a.tile.openstreetmap.org/{z}/{x}/{y}"
private const val markerSize = 48.0
private val seattleMark = Point2d.FromDegrees(-122.3320708, 47.6062095)

class GlobeFragment : GlobeMapFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, inState: Bundle?): View? {
        super.onCreateView(inflater, container, inState)
        return baseControl.contentView
    }

    override fun chooseDisplayType(): MapDisplayType {
        return MapDisplayType.Globe
    }

    override fun controlHasStarted() {
        globeControl.addLayer(baseLayer())
        globeControl.animatePositionGeo(seattleMark.x, seattleMark.y, .5, 2.0)
        insertMarkers()
    }

    /**
     * https://mousebird.github.io/WhirlyGlobe/tutorial/android/screen-markers.html
     */
    private fun insertMarkers() {
        val markerInfo = MarkerInfo()
        val seattle = ScreenMarker().apply {
            loc = seattleMark
            image = AppCompatResources.getDrawable(requireContext(), RR.drawable.ic_tide)?.toBitmap()
            size = Point2d(markerSize, markerSize)
        }
        globeControl.addScreenMarker(seattle, markerInfo, MaplyBaseController.ThreadMode.ThreadAny)
    }

    private fun baseLayer() : Layer {
        return QuadImageTileLayer(globeControl, SphericalMercatorCoordSystem(), tileSource()).apply {
            imageDepth = 1
            setSingleLevelLoading(false)
            setUseTargetZoomLevel(false)
            setCoverPoles(true)
            setHandleEdges(true)
        }
    }

    private fun tileSource() : QuadImageTileLayer.TileSource {
        return RemoteTileSource(baseControl, remoteTileInfo()).apply {
            context?.let { ctx ->
                setCacheDir(File(ctx.cacheDir, cacheDirName).apply {
                    mkdir()
                })
            }
        }
    }

    private fun remoteTileInfo() : RemoteTileInfo {
        return RemoteTileInfo(openStreetMapUrl, "png", 0, 18)
    }
}