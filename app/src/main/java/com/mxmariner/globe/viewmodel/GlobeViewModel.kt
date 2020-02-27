package com.mxmariner.globe.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mousebird.maply.ComponentObject
import com.mousebird.maply.GlobeController
import com.mousebird.maply.MaplyBaseController
import com.mousebird.maply.MarkerInfo
import com.mousebird.maply.Point2d
import com.mousebird.maply.Point3d
import com.mousebird.maply.ScreenMarker
import com.mousebird.maply.SelectedObject
import com.mxmariner.globe.data.GeoBox
import com.mxmariner.globe.data.globeBox
import com.mxmariner.globe.extensions.setPosition
import com.mxmariner.globe.util.GlobePreferences
import com.mxmariner.globe.util.ShapeFileDao
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.ITidesAndCurrents
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.R
import com.mxmariner.tides.util.Variable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Provider

class GlobeViewModelFactory @Inject constructor(
        private val provider: Provider<GlobeViewModel>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }
}

private const val markerSize = 48.0

class GlobeViewModel @Inject constructor(
    private val tidesAndCurrents: ITidesAndCurrents,
    private val context: Context,
    private val prefs: GlobePreferences,
    private val shapeFileDao: ShapeFileDao
) : ViewModel(), GlobeController.GestureDelegate {

    private val clickSubject = PublishSubject.create<IStation>()
    val stationClickObservable: Observable<IStation>
        get() = clickSubject.hide()

    var displayType = prefs.lastSelection()
        set(value) {
            if (value != field) {
                field = value
                displayMarkers(value)
            }
        }

    private val disposable = CompositeDisposable()
    private var globeController = Variable<GlobeController>()

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

    private fun displayMarkersQuery(gc: GlobeController, target: StationType) {
        disposable.add(
                stationMarkers(type = target, corners = globeBox)
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onSuccess = { markerList ->
                                    when (target) {
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

    private fun displayMarkers(target: StationType) {
        /**
         * https://mousebird.github.io/WhirlyGlobe/tutorial/android/screen-markers.html
         */
        disposable.add(
                globeController.observable.subscribe { gc ->
                    when (target) {
                        StationType.TIDES -> {
                            currentMarkers?.let {
                                gc.disableObject(it, MaplyBaseController.ThreadMode.ThreadAny)
                            }
                            tideMarkers?.let {
                                gc.enableObject(it, MaplyBaseController.ThreadMode.ThreadAny)
                            } ?: {
                                displayMarkersQuery(gc, target)
                            }()
                        }
                        StationType.CURRENTS -> {
                            tideMarkers?.let {
                                gc.disableObject(it, MaplyBaseController.ThreadMode.ThreadAny)
                            }
                            currentMarkers?.let {
                                gc.enableObject(it, MaplyBaseController.ThreadMode.ThreadAny)
                            } ?: {
                                displayMarkersQuery(gc, target)
                            }()
                        }
                    }
                })
    }

    override fun onCleared() {
        disposable.clear()
        globeController.value?.let {
            it.gestureDelegate = null
        }
        globeController = Variable()
    }

    fun initialize(globeControl: GlobeController) {
        globeController.value = globeControl
        globeControl.gestureDelegate = this
        globeControl.setPosition(prefs.lastPosition())
        displayMarkers(displayType)
        disposable.addAll(
                shapeFileDao.graticules().subscribeBy(
                        onSuccess = {
                            globeControl.addVector(it.first, it.second, MaplyBaseController.ThreadMode.ThreadAny)
                        }
                ),
                shapeFileDao.land().subscribeBy(
                        onSuccess = {
                            globeControl.addVector(it.first, it.second, MaplyBaseController.ThreadMode.ThreadAny)
                        }
                )
        )

    }

    fun pause(globeControl: GlobeController) {
        prefs.savePosition(globeControl.positionGeo)
        prefs.saveSelection(displayType)
    }
}