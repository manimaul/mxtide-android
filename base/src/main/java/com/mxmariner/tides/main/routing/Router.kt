package com.mxmariner.tides.main.routing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.mxmariner.tides.main.extensions.addParams
import javax.inject.Inject

const val authority = "tides.mxmariner.com"
const val scheme = "https"
const val routerIntentAction = "$authority.ACTION_ROUTE"

abstract class Route(
        private val uriPath: String,
        private val params: Map<Any, Any>? = null
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

// region MainActivity

class RouteNearbyTides : Route("/main/nearby_tides")
class RouteNearbyCurrents : Route("/main/nearby_currents")
class RouteMap : Route("/main/map")
class RouteSettings : Route("/main/settings")

// endregion


// region DetailsActivity

class RouteStationDetails(stationName: String) : Route("/details/station", mapOf("stationName" to stationName))

// endregion

class Router @Inject constructor(
        private val activity: Activity
) {

    fun routeTo(route: Route) {
        val intent = Intent(routerIntentAction, route.uri)
        activity.startActivityIfNeeded(intent, 0)
    }
}
