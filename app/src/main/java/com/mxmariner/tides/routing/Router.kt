package com.mxmariner.tides.routing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.google.android.instantapps.InstantApps
import com.mxmariner.mxtide.api.IStation
import com.mxmariner.tides.extensions.addParams
import com.mxmariner.tides.model.ActivityResult
import com.mxmariner.tides.util.RxActivityResult
import io.reactivex.Single

private const val authority = "mxmariner.com"
private const val scheme = "https"

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

//class RouteNearbyTides : Route("/tides", mapOf("tab" to "nearby_tides"))
//class RouteNearbyCurrents : Route("/tides", mapOf("tab" to "nearby_currents"))
class RouteSettings : Route("/tides", mapOf("tab" to "settings"))
class RouteGlobe : Route("/tides/globe")
//class RouteStation : Route("/tides/station", mapOf("stationName" to "Tacoma, Commencement Bay, Sitcum Waterway, Puget Sound, Washington"))

// endregion


// region DetailsActivity

class RouteStationDetails(station: IStation) : Route("/tides/station", mapOf(
        "stationName" to station.name,
        "stationType" to station.type.name
))

// endregion

class Router(kodein: Kodein) {

    private val activity: Activity = kodein.instance()
    private val rxActivityResult: RxActivityResult = kodein.instance()

    private fun routeIntent(route: Route): Intent {
        val intent = if (InstantApps.isInstantApp(activity)) {
            Intent(Intent.ACTION_VIEW, route.uri)
        } else {
            val intent = Intent(Intent.ACTION_VIEW, route.uri)
            intent.`package` = activity.packageName
            intent
        }
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        return intent
    }

    fun routeTo(route: Route) {
        activity.startActivityIfNeeded(routeIntent(route), 0)
    }

    fun routeToForResult(route: Route): Single<ActivityResult> {
        return rxActivityResult.startActivityForResultSingle(routeIntent(route))
    }
}
