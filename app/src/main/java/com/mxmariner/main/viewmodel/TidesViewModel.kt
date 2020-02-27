package com.mxmariner.main.viewmodel

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mxmariner.main.adapter.TidesRecyclerAdapter
import com.mxmariner.main.model.TidesViewState
import com.mxmariner.main.model.TidesViewStateLoadingComplete
import com.mxmariner.main.model.TidesViewStateLoadingStarted
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.R
import com.mxmariner.tides.repository.HarmonicsRepo
import com.mxmariner.tides.ui.SnackbarController
import com.mxmariner.tides.util.LocationPermissionResult
import com.mxmariner.tides.util.LocationResultPermission
import com.mxmariner.tides.util.RxLocation
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Provider

class TidesViewModelFactory @Inject constructor(private val viewModelProvider: Provider<TidesViewModel>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return viewModelProvider.get() as T
    }
}

class TidesViewModel @Inject constructor(
  private val harmonicsRepo: HarmonicsRepo,
  private val rxLocation: RxLocation,
  private val resources: Resources,
  private val snackbarController: SnackbarController,
  val recyclerAdapter: TidesRecyclerAdapter
) : ViewModel() {

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
