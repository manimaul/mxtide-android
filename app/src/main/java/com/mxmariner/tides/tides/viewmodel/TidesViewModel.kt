package com.mxmariner.tides.tides.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.res.Resources
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.R
import com.mxmariner.tides.main.extensions.debug
import com.mxmariner.tides.main.repository.HarmonicsRepo
import com.mxmariner.tides.main.ui.SnackbarController
import com.mxmariner.tides.main.util.LocationPermissionResult
import com.mxmariner.tides.main.util.LocationResultPermission
import com.mxmariner.tides.main.util.RxLocation
import com.mxmariner.tides.tides.adapter.TidesRecyclerAdapter
import com.mxmariner.tides.tides.model.TidesViewState
import com.mxmariner.tides.tides.model.TidesViewStateLoadingComplete
import com.mxmariner.tides.tides.model.TidesViewStateLoadingStarted
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Provider


class TidesViewModelFactory @Inject constructor(
        private val tidesViewModelProvider: Provider<TidesViewModel>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return tidesViewModelProvider.get() as T
    }
}

class TidesViewModel @Inject constructor(
        private val harmonicsRepo: HarmonicsRepo,
        private val rxLocation: RxLocation,
        private val resources: Resources,
        private val snackbarController: SnackbarController,
        val recyclerAdapter: TidesRecyclerAdapter
) : ViewModel() {

    fun viewState(): Observable<TidesViewState> {
        val loadingStarted = TidesViewStateLoadingStarted(resources.getString(R.string.finding_closest_tide_stations))
        return rxLocation.singleRecentLocationPermissionResult()
                .toObservable()
                .debug("WBK-location")
                .compose(snackbarController.retryWhenSnackbarUntilType<LocationPermissionResult, LocationResultPermission>())
                .observeOn(Schedulers.computation())
                .map {
                    harmonicsRepo.tidesAndCurrents.findNearestStations(it.location.latitude, it.location.longitude, StationType.TIDES)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map<TidesViewState> {
                    recyclerAdapter.add(it)
                    TidesViewStateLoadingComplete()
                }
                .onErrorReturn { TidesViewStateLoadingComplete(resources.getString(R.string.could_not_deterine_location)) }
                .startWith(loadingStarted)
                .takeUntil {
                    when (it) {
                        is TidesViewStateLoadingComplete -> true
                        else -> false
                    }
                }
                .debug("WBK-all")
    }
}
