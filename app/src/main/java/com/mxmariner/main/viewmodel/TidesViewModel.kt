package com.mxmariner.main.viewmodel

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.R
import com.mxmariner.main.adapter.TidesRecyclerAdapter
import com.mxmariner.main.model.TidesViewState
import com.mxmariner.main.model.TidesViewStateLoadingComplete
import com.mxmariner.main.model.TidesViewStateLoadingStarted
import com.mxmariner.tides.repository.HarmonicsRepo
import com.mxmariner.tides.ui.SnackbarController
import com.mxmariner.tides.util.LocationPermissionResult
import com.mxmariner.tides.util.LocationResultPermission
import com.mxmariner.tides.util.RxLocation
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class TidesViewModelFactory(private val kodein: Kodein) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return kodein.instance<TidesViewModel>() as T
    }
}

class TidesViewModel(kodein: Kodein) : ViewModel() {

    private val harmonicsRepo: HarmonicsRepo = kodein.instance()
    private val rxLocation: RxLocation = kodein.instance()
    private val resources: Resources = kodein.instance()
    private val snackbarController: SnackbarController = kodein.instance()
    val recyclerAdapter: TidesRecyclerAdapter = kodein.instance()

    fun viewState(stationType: StationType): Observable<TidesViewState> {
        val loadingStarted = TidesViewStateLoadingStarted(resources.getString(R.string.finding_closest_tide_stations))
        return rxLocation.singleRecentLocationPermissionResult()
                .toObservable()
                .compose(snackbarController.retryWhenSnackbarUntilType<LocationPermissionResult, LocationResultPermission>())
                .observeOn(Schedulers.computation())
                .map {
                    harmonicsRepo.tidesAndCurrents.findNearestStations(it.location.latitude, it.location.longitude, stationType, 25)
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
    }
}
