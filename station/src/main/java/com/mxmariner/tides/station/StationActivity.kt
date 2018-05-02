package com.mxmariner.tides.station

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.ITidesAndCurrents
import com.mxmariner.mxtide.api.StationType
import com.mxmariner.tides.station.di.StationModuleInjector
import com.mxmariner.tides.ui.UnitFormats
import com.mxmariner.tides.util.LocationResultNoPermission
import com.mxmariner.tides.util.LocationResultPermission
import com.mxmariner.tides.util.RxLocation
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_station.*

class StationActivity : AppCompatActivity() {

  lateinit var tidesAndCurrents: ITidesAndCurrents
  lateinit var rxLocation: RxLocation
  lateinit var unitFormats: UnitFormats

  override fun onCreate(savedInstanceState: Bundle?) {
    val injector = StationModuleInjector.activityScopeAssembly(this)
    super.onCreate(savedInstanceState)
    tidesAndCurrents = injector.instance()
    rxLocation = injector.instance()
    unitFormats = injector.instance()
    setContentView(R.layout.activity_station)

    //madrona://mxmariner.com/tides/station?stationName=NameUriEncoded
    getStationMessage(intent.data.getQueryParameter("stationName"))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = ::bindUi,
            onComplete = ::bindUiError
        )
  }

  private fun getStationMessage(name: String?): Maybe<IStation> {
    return Maybe.create<IStation> { emitter ->
      tidesAndCurrents.findStationByName(name)?.let {
        emitter.onSuccess(it)
      } ?: {
        emitter.onComplete()
      }()
    }.subscribeOn(Schedulers.io())
  }

  private fun bindUi(station: IStation) {
    startColumn.visibility = View.VISIBLE
    when (station.type) {
      StationType.TIDES -> icon.setImageResource(com.mxmariner.tides.R.drawable.ic_tide)
      StationType.CURRENTS -> icon.setImageResource(com.mxmariner.tides.R.drawable.ic_current)
    }
    stationName.text = station.name
    position.text = "${station.latitude}, ${station.longitude}"
    stationTimeZone.text = station.timeZone.toTimeZone().displayName

    rxLocation.singleRecentLocationPermissionResult()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy {
          distanceLabel.text = when (it) {
            is LocationResultNoPermission -> getString(R.string.unknown)
            is LocationResultPermission -> unitFormats.distanceFormatted(it.location, station)
          }
        }
  }

  private fun bindUiError() {
    messageLabel.text = getString(com.mxmariner.tides.R.string.whoops)
  }
}
