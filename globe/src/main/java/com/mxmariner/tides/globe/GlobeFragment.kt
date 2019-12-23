package com.mxmariner.tides.globe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mousebird.maply.*
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.globe.di.GlobeModuleInjector
import com.mxmariner.tides.routing.RouteStationDetails
import com.mxmariner.tides.routing.Router
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import java.io.File


private const val cacheDirName = "openstreetmap"
private const val openStreetMapUrl = "https://a.tile.openstreetmap.org/{z}/{x}/{y}"
private val seattleMark = Point2d.FromDegrees(-122.3320708, 47.6062095)

class GlobeFragment : GlobeMapFragment() {

    lateinit var kodein: Kodein
    private lateinit var viewModelFactory: GlobeViewModelFactory
    private lateinit var viewModel: GlobeViewModel
    private lateinit var router: Router
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().let {
            kodein = GlobeModuleInjector.activityScopeAssembly(it)
            viewModelFactory = kodein.instance()
            router = kodein.instance()
            viewModel = ViewModelProvider(this, viewModelFactory).get(GlobeViewModel::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        compositeDisposable.add(
                viewModel.stationClickObservable.observeOn(AndroidSchedulers.mainThread())
                        .take(1)
                        .singleElement()
                        .subscribeBy(
                                onSuccess = {
                                    router.routeTo(RouteStationDetails(it.name))
                                }
                        )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        globeControl?.gestureDelegate = null
        compositeDisposable.clear()
    }

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
        viewModel.initialize(globeControl)
    }

    private fun baseLayer(): Layer {
        return QuadImageTileLayer(globeControl, SphericalMercatorCoordSystem(), tileSource()).apply {
            imageDepth = 1
            setSingleLevelLoading(true)
            setUseTargetZoomLevel(false)
            setCoverPoles(true)
            setHandleEdges(true)
        }
    }

    private fun tileSource(): QuadImageTileLayer.TileSource {
        return RemoteTileSource(baseControl, remoteTileInfo()).apply {
            context?.let { ctx ->
                setCacheDir(File(ctx.cacheDir, cacheDirName).apply {
                    mkdir()
                })
            }
        }
    }

    private fun remoteTileInfo(): RemoteTileInfo {
        return RemoteTileInfo(openStreetMapUrl, "png", 0, 18)
    }

    fun select(type: StationType) {
        viewModel.displayType = type
    }
}