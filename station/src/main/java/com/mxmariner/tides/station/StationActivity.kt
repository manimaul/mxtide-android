package com.mxmariner.tides.station

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.ITidesAndCurrents
import com.mxmariner.tides.station.di.StationModuleInjector
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_station.*

class StationActivity : AppCompatActivity() {

    lateinit var tidesAndCurrents: ITidesAndCurrents

    override fun onCreate(savedInstanceState: Bundle?) {
        val injector = StationModuleInjector.activityScopeAssembly(this)
        super.onCreate(savedInstanceState)
        tidesAndCurrents = injector.instance()
        setContentView(R.layout.activity_station)

        //madrona://mxmariner.com/tides/station?stationName=NameUriEncoded
        getStationMessage(intent.data.getQueryParameter("stationName"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    messageLabel.text = it
                }
    }

    private fun getStationMessage(name: String?) : Single<String> {
        return Single.create<String> {
            it.onSuccess(tidesAndCurrents.findStationByName(name)?.let {
                "$name \n position: ${it.latitude} : ${it.longitude}"
            } ?: getString(com.mxmariner.tides.R.string.whoops))
        }.subscribeOn(Schedulers.io())
    }
}
