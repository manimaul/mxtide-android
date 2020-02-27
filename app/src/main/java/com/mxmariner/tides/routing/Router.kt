package com.mxmariner.tides.routing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.mxmariner.globe.activity.GlobeActivity
import com.mxmariner.main.activity.LocationSearchActivity
import com.mxmariner.main.activity.MainActivity
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.station.StationActivity
import com.mxmariner.tides.BuildConfig
import com.mxmariner.tides.extensions.addParams
import com.mxmariner.tides.model.ActivityResult
import com.mxmariner.tides.util.RxActivityResult
import io.reactivex.Single
import javax.inject.Inject

private const val authority = "mxmariner.com"
private const val scheme = "https"

abstract class Route(
        private val uriPath: String,
        private val params: Map<Any, Any>? = null,
        val className: String
) {
    val uri: Uri by lazy {
        Uri.Builder()
                .scheme(scheme)
                .authority(authority)
                .path(uriPath)
                .addParams(params)
                .build()
    }
}

//class RouteNearbyTides : Route("/tides", mapOf("tab" to "nearby_tides"))
//class RouteNearbyCurrents : Route("/tides", mapOf("tab" to "nearby_currents"))
class RouteSettings : Route(uriPath =  "/tides",
                            params = mapOf("tab" to "settings"),
                            className = MainActivity::class.java.name)
class RouteGlobe : Route(uriPath = "/tides/globe",
                         className = GlobeActivity::class.java.name)
//class RouteStation : Route("/tides/station", mapOf("stationName" to "Tacoma, Commencement Bay, Sitcum Waterway, Puget Sound, Washington"))

class RouteStationDetails(station: IStation) : Route("/tides/station", mapOf(
        "stationName" to station.name,
        "stationType" to station.type.name
), StationActivity::class.java.name)

class RouteLocationSearch : Route(uriPath = "/tides/location_search",
        className = LocationSearchActivity::class.java.name)

class Router @Inject constructor(
  private val activity: Activity,
  private val rxActivityResult: RxActivityResult
) {

    private fun routeIntent(route: Route): Intent {
        return Intent().apply {
            setClassName(BuildConfig.APPLICATION_ID, route.className)
            data = route.uri
        }
    }

    fun routeTo(route: Route) {
        activity.startActivityIfNeeded(routeIntent(route), 0)
    }

    fun routeToForResult(route: Route): Single<ActivityResult> {
        return rxActivityResult.startActivityForResultSingle(routeIntent(route))
    }
}
