package com.mxmariner.tides.main.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.FragmentManager
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.PermissionChecker
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

private val locationPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

interface RxLocation {
    /**
     * Retrieves a location signal asking permission if necessary.
     */
    fun maybeRecentLocation(): Maybe<Location>
}

class RxLocationImpl @Inject constructor(private val context: Context,
                                         private val locationManager: LocationManager,
                                         private val fragmentManager: FragmentManager) : RxLocation {

    override fun maybeRecentLocation(): Maybe<Location> {
        return locationPermission(fragmentManager).flatMapMaybe { isPermissionGranted ->
            if (isPermissionGranted) {
                recentLocation()
            } else {
                Maybe.empty()
            }
        }
    }

    private fun locationPermission(fragmentManager: FragmentManager): Single<Boolean> {
        return if (PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            Single.just(true)
        } else requestPermissions(fragmentManager, locationPermissions).map {
            it.first().grantResult == PermissionChecker.PERMISSION_GRANTED || it.last().grantResult == PermissionChecker.PERMISSION_GRANTED
        }
    }

    @SuppressLint("MissingPermission")
    private fun recentLocation(): Maybe<Location> {
        return Maybe.create { emitter ->
            locationManager.requestSingleUpdate(Criteria(), object : LocationListener {
                override fun onLocationChanged(location: Location) = emitter.onSuccess(location)
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String?) {}
                override fun onProviderDisabled(provider: String?) {}
            }, null)
        }
    }

}
