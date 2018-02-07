package com.mxmariner.tides.main.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.PermissionChecker
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class LocationPermissionResult
class LocationResultNoPermission : LocationPermissionResult()
class LocationResultTimeOut : LocationPermissionResult()
data class LocationResultPermission(val location: Location) : LocationPermissionResult()

interface RxLocation {
    /**
     * Retrieves a location signal asking permission if necessary.
     */
    fun singleRecentLocationPermissionResult(): Single<LocationPermissionResult>
}

class RxLocationImpl @Inject constructor(private val context: Context,
                                         private val locationManager: LocationManager,
                                         private val rxPermission: RxPermission) : RxLocation {

    override fun singleRecentLocationPermissionResult(): Single<LocationPermissionResult> {
        return locationPermission().flatMap { isPermissionGranted ->
            if (isPermissionGranted) {
                recentLocation().map<LocationPermissionResult> {
                    LocationResultPermission(it)
                }.timeout(5, TimeUnit.SECONDS, Single.just(LocationResultTimeOut()))
            } else {
                Single.just(LocationResultNoPermission())
            }
        }
    }

    private fun locationPermission(): Single<Boolean> {
        return if (PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            Single.just(true)
        } else rxPermission.requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).map {
            it.first().grantResult == PermissionChecker.PERMISSION_GRANTED || it.last().grantResult == PermissionChecker.PERMISSION_GRANTED
        }
    }

    @SuppressLint("MissingPermission")
    private fun recentLocation(): Single<Location> {
        return Single.create { emitter ->
            val location = Location("fake")
            location.latitude = 47.0
            location.longitude = -122.0
            emitter.onSuccess(location)
//            locationManager.requestSingleUpdate(Criteria(), object : LocationListener {
//                override fun onLocationChanged(location: Location) = emitter.onSuccess(location)
//                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
//                override fun onProviderEnabled(provider: String?) {}
//                override fun onProviderDisabled(provider: String?) {}
//            }, null)
        }
    }
}
