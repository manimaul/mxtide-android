package com.mxmariner.tides.station

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.mxtide.api.ITidesAndCurrents
import com.mxmariner.tides.factory.StationPresentationFactory
import com.mxmariner.tides.model.StationPresentation
import com.mxmariner.tides.station.di.StationModuleInjector
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_station.*

class StationActivity : AppCompatActivity() {

  lateinit var tidesAndCurrents: ITidesAndCurrents
  lateinit var stationPresentationFactory: StationPresentationFactory

  override fun onCreate(savedInstanceState: Bundle?) {
    val injector = StationModuleInjector.activityScopeAssembly(this)
    super.onCreate(savedInstanceState)
    tidesAndCurrents = injector.instance()
    stationPresentationFactory = injector.instance()
    setContentView(R.layout.activity_station)

    //madrona://mxmariner.com/tides/station?stationName=NameUriEncoded
    getStationMessage(intent.data.getQueryParameter("stationName"))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = ::bindUi,
            onComplete = ::bindUiError
        )
  }

  private fun getStationMessage(name: String?): Maybe<StationPresentation> {
    return Maybe.create<IStation> { emitter ->
      tidesAndCurrents.findStationByName(name)?.let {
        emitter.onSuccess(it)
      } ?: {
        emitter.onComplete()
      }()
    }.map { stationPresentationFactory.createPresentation(it) }.subscribeOn(Schedulers.io())

  }

  private fun bindUi(presentation: StationPresentation) {
    startColumn.visibility = View.VISIBLE
    icon.setImageResource(presentation.icon)
    stationName.text = presentation.name
    position.text = presentation.position
    stationTimeZone.text = presentation.timeZone.toTimeZone().displayName
    distanceLabel.text = presentation.distance
    localTime.text = presentation.startToEndFormatted
  }

  private fun bindUiError() {
    messageLabel.text = getString(com.mxmariner.tides.R.string.whoops)
  }
}
