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
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
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
        return loadStations().map<TidesViewState> {
            if (it) {
                TidesViewStateLoadingComplete()
            } else {
                val message = resources.getString(R.string.no_results)
                TidesViewStateLoadingComplete(message)
            }
        }.toObservable().startWith(TidesViewStateLoadingStarted())
    }

    private fun loadStations(): Single<Boolean> {
        return rxLocation.maybeRecentLocation()
                .timeout(3, TimeUnit.SECONDS, Maybe.empty())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    val stations = harmonicsRepo.tidesAndCurrents.findNearestStations(it.latitude, it.longitude, StationType.TIDES)
                    recyclerAdapter.add(stations)
                    stations.isNotEmpty()
                }
                .toSingle(false)
                .compose(snackbarController.showRetryIf(false) {
                    !it // showing retry snackbar if we do not have a location
                })
    }
}