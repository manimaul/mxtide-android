package com.mxmariner.tides.main.routing

import android.net.Uri
import io.reactivex.Single
import java.io.Serializable

const val baseUri = "https://tides.mxmariner.com"


abstract class Route<out T : Serializable>(
        private val uriPath: String,
        val value: T? = null
) {
    val uri: Uri by lazy {
        Uri.Builder()
                .scheme("https")
                .authority("tides.mxmariner.com")
                .path(uriPath)
                .build()
    }
}

object MainActivityRoutes {
    class NearbyTides : Route<Serializable>("/main/nearby_tides")
    class NearbyCurrents : Route<Serializable>("/main/nearby_tides")
    class Map : Route<Serializable>("/main/map")
    class Settings : Route<Serializable>("/main/settings")
    class StationDetails : Route<Serializable>("details/station")
}

interface Router {
    fun <T : Serializable> routeTo(route: Route<T>)
    fun <T : Serializable, R> routeToForResult(route: Route<T>): Single<R>
    fun back()
}

class EmptyRouter : Router {
    override fun <T : Serializable> routeTo(route: Route<T>) = Unit
    override fun <T : Serializable, R> routeToForResult(route: Route<T>): Single<R> = Single.error(Throwable("Router Not Implemented"))
    override fun back() = Unit
}