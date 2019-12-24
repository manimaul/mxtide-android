package com.mxmariner.tides.globe.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mousebird.maply.*
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.ITidesAndCurrents
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.R
import com.mxmariner.tides.globe.util.GlobePreferences
import com.mxmariner.tides.globe.data.GeoBox
import com.mxmariner.tides.globe.data.globeBox
import com.mxmariner.tides.globe.extensions.setPosition
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class GlobeViewModelFactory(
        private val kodein: Kodein
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return kodein.instance<GlobeViewModel>() as T
    }
}

private const val markerSize = 48.0

class GlobeViewModel(kodein: Kodein) : ViewModel(), GlobeController.GestureDelegate {

    private val tidesAndCurrents: ITidesAndCurrents = kodein.instance()
    private val context: Context = kodein.instance()
    private val prefs: GlobePreferences = kodein.instance()

    private val clickSubject = PublishSubject.create<IStation>()
    val stationClickObservable: Observable<IStation>
        get() = clickSubject.hide()

    var displayType = StationType.TIDES
        set(value) {
            if (value != field) {
                field = value
                displayMarkers()
            }
        }

    private val disposable = CompositeDisposable()
    private var globeController: GlobeController? = null

    var currentMarkers: ComponentObject? = null
    var tideMarkers: ComponentObject? = null

    override fun userDidSelect(globeControl: GlobeController, selObjs: Array<out SelectedObject>?, loc: Point2d, screenLoc: Point2d) {
        selObjs?.filter {
            it.selObj is ScreenMarker
        }?.map {
            it.selObj as ScreenMarker
        }?.filter {
            it.userObject is IStation
        }?.map {
            it.userObject as IStation
        }?.forEach {
            Log.i("gvm station selected", "$it")
            clickSubject.onNext(it)
        }
    }

    override fun globeDidStopMoving(globeControl: GlobeController, corners: Array<out Point3d>?, userMotion: Boolean) {
    }

    override fun userDidTap(globeControl: GlobeController, loc: Point2d?, screenLoc: Point2d?) {
    }

    override fun globeDidStartMoving(globeControl: GlobeController, userMotion: Boolean) {
    }

    override fun userDidLongPress(globeControl: GlobeController, selObjs: Array<out SelectedObject>?, loc: Point2d, screenLoc: Point2d) {
    }

    override fun globeDidMove(globeControl: GlobeController, corners: Array<out Point3d>?, userMotion: Boolean) {
    }

    override fun userDidTapOutside(globeControl: GlobeController, screenLoc: Point2d?) {
    }

    private fun bitmap(resId: Int): Single<Bitmap> {
        return Single.create<Bitmap> { emitter ->
            AppCompatResources.getDrawable(context, resId)?.toBitmap()?.let {
                emitter.onSuccess(it)
            }
        }.subscribeOn(Schedulers.computation())
    }

    private fun stationMarkers(type: StationType, corners: GeoBox = globeBox): Observable<ScreenMarker> {
        return when (type) {
            StationType.TIDES -> bitmap(R.drawable.ic_tide)
            StationType.CURRENTS -> bitmap(R.drawable.ic_current)
        }.flatMapObservable { tideBitmap ->
            Observable.create<ScreenMarker> { emitter ->
                tidesAndCurrents.findStationsInBounds(corners.north, corners.east, corners.south, corners.west, type).forEach { station ->
                    emitter.takeIf { !it.isDisposed }?.onNext(
                            ScreenMarker().apply {
                                loc = Point2d.FromDegrees(station.longitude, station.latitude)
                                image = tideBitmap
                                size = Point2d(markerSize, markerSize)
                                userObject = station
                                selectable = true
                            }
                    )
                }
                emitter.takeIf { !it.isDisposed }?.onComplete()
            }.subscribeOn(Schedulers.computation())
        }
    }

    private fun displayMarkersQuery(gc: GlobeController) {
        disposable.clear()
        disposable.add(
                stationMarkers(type = displayType, corners = globeBox)
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onSuccess = { markerList ->
                                    when (displayType) {
                                        StationType.TIDES -> tideMarkers = gc.addScreenMarkers(markerList, MarkerInfo(), MaplyBaseController.ThreadMode.ThreadAny)
                                        StationType.CURRENTS -> currentMarkers = gc.addScreenMarkers(markerList, MarkerInfo(), MaplyBaseController.ThreadMode.ThreadAny)
                                    }
                                },
                                onError = {
                                    Log.e("err", "$it")
                                }
                        )
        )
    }

    private fun displayMarkers() {
        /**
         * https://mousebird.github.io/WhirlyGlobe/tutorial/android/screen-markers.html
         */
        globeController?.let { gc ->
            when (displayType) {
                StationType.TIDES -> {
                    currentMarkers?.let {
                        gc.disableObject(it, MaplyBaseController.ThreadMode.ThreadAny)
                    }
                    tideMarkers?.let {
                        gc.enableObject(it, MaplyBaseController.ThreadMode.ThreadAny)
                    } ?: {
                        displayMarkersQuery(gc)
                    }()
                }
                StationType.CURRENTS -> {
                    tideMarkers?.let {
                        gc.disableObject(it, MaplyBaseController.ThreadMode.ThreadAny)
                    }
                    currentMarkers?.let {
                        gc.enableObject(it, MaplyBaseController.ThreadMode.ThreadAny)
                    } ?: {
                        displayMarkersQuery(gc)
                    }()
                }
            }
        }
    }

    override fun onCleared() {
        disposable.clear()
        globeController?.let {
            it.gestureDelegate = null
        }
        globeController = null
    }

    fun initialize(globeControl: GlobeController) {
        globeController = globeControl
        globeControl.gestureDelegate = this
        globeControl.setPosition(prefs.lastPosition())
        displayMarkers()
    }

    fun pause(globeControl: GlobeController) {
        prefs.savePosition(globeControl.positionGeo)
    }
}