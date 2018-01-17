package com.mxmariner.tides.tides.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.res.Resources
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.R
import com.mxmariner.tides.main.repository.HarmonicsRepo
import com.mxmariner.tides.main.ui.SnackbarController
import com.mxmariner.tides.main.util.RxLocation
import com.mxmariner.tides.tides.adapter.TidesRecyclerAdapter
import com.mxmariner.tides.tides.model.TidesViewState
import com.mxmariner.tides.tides.model.TidesViewStateLoadingComplete
import com.mxmariner.tides.tides.model.TidesViewStateLoadingStarted
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
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
        private val snackbarController: SnackbarController
) : ViewModel() {

    val recyclerAdapter: TidesRecyclerAdapter = TidesRecyclerAdapter()

    fun viewState(): Observable<TidesViewState> {
        val loadingCompleteNoLocation = TidesViewStateLoadingComplete(resources.getString(R.string.could_not_deterine_location))
        val loadingStarted = TidesViewStateLoadingStarted(resources.getString(R.string.finding_closest_tide_stations))
        return rxLocation.maybeRecentLocation()
                .toObservable()
                .observeOn(Schedulers.io())
                .map {
                    harmonicsRepo.tidesAndCurrents.findNearestStations(it.latitude, it.longitude, StationType.TIDES)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map<TidesViewState> {
                    recyclerAdapter.add(it)
                    TidesViewStateLoadingComplete()
                }
                .timeout(5, TimeUnit.SECONDS, Observable.just(loadingCompleteNoLocation))
                .compose(snackbarController.showRetryIf<TidesViewState> {
                    it == loadingCompleteNoLocation
                })
                .startWith(loadingStarted)
    }
}
