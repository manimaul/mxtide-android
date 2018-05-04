package com.mxmariner.tides.routing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.google.android.instantapps.InstantApps
import com.mxmariner.tides.extensions.addParams

private const val authority = "mxmariner.com"
private const val scheme = "madrona"

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

//class RouteNearbyTides : Route("/tides?tab=nearby_tides")
//class RouteNearbyCurrents : Route("/tides?tab=nearby_currents")
class RouteSettings : Route("/tides?tab=settings")

// endregion


// region DetailsActivity

class RouteStationDetails(stationName: String) : Route("/tides/station", mapOf("stationName" to stationName))

// endregion

class Router(kodein: Kodein) {

    private val activity: Activity = kodein.instance()

    fun routeTo(route: Route) {
        val intent = if (InstantApps.isInstantApp(activity)) {
            val uri = route.uri.buildUpon().scheme("https").build()
            Intent(Intent.ACTION_VIEW, uri)
        } else {
            val intent = Intent(Intent.ACTION_VIEW, route.uri)
            intent.`package` =  activity.packageName
            intent
        }
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        activity.startActivityIfNeeded(intent, 0)
    }
}
