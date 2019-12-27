package com.mxmariner.tides.routing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.tides.BuildConfig
import com.mxmariner.tides.extensions.addParams
import com.mxmariner.tides.model.ActivityResult
import com.mxmariner.tides.util.RxActivityResult
import io.reactivex.Single

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
                            className = "com.mxmariner.main.activity.MainActivity")
class RouteGlobe : Route(uriPath = "/tides/globe",
                         className = "com.mxmariner.tides.globe.activity.GlobeActivity")
//class RouteStation : Route("/tides/station", mapOf("stationName" to "Tacoma, Commencement Bay, Sitcum Waterway, Puget Sound, Washington"))

class RouteStationDetails(station: IStation) : Route("/tides/station", mapOf(
        "stationName" to station.name,
        "stationType" to station.type.name
), "com.mxmariner.tides.station.StationActivity")

class RouteLocationSearch : Route(uriPath = "/tides/location_search",
        className = "com.mxmariner.main.activity.LocationSearchActivity")

class Router(kodein: Kodein) {

    private val activity: Activity = kodein.instance()
    private val rxActivityResult: RxActivityResult = kodein.instance()

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
