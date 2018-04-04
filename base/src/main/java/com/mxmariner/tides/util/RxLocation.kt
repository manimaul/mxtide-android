package com.mxmariner.tides.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.PermissionChecker
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.mxmariner.tides.R
import com.mxmariner.tides.extensions.evaluateNullables
import io.reactivex.Single
import java.util.concurrent.TimeUnit

sealed class LocationPermissionResult
class LocationResultNoPermission : LocationPermissionResult()
data class LocationResultPermission(val location: Location) : LocationPermissionResult()

interface RxLocation {
    /**
     * Retrieves a location signal asking permission if necessary.
     */
    fun singleRecentLocationPermissionResult(): Single<LocationPermissionResult>
}

class RxLocationImpl(kodein: Kodein) : RxLocation {

    private val context: Context = kodein.instance()
    private val locationManager: LocationManager = kodein.instance()
    private val rxPermission: RxPermission = kodein.instance()
    private val sharedPreferences: SharedPreferences = kodein.instance()

    override fun singleRecentLocationPermissionResult(): Single<LocationPermissionResult> {
        return prefLocation()?.let {
            Single.just<LocationPermissionResult>(LocationResultPermission(it))
        } ?: locationPermission().flatMap { isPermissionGranted ->
            if (isPermissionGranted) {
                recentLocation().map<LocationPermissionResult> {
                    LocationResultPermission(it)
                }
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

    private val lastKnownLocation: Location?
        @SuppressLint("MissingPermission")
        get() {
            return (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER))?.takeIf {
                (System.currentTimeMillis() - it.time) > TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)
            }
        }

    private fun prefLocation(): Location? {
        val key = context.getString(R.string.PREF_KEY_LOCATION)
        return sharedPreferences.getString(key, null)
                ?.split(":")
                ?.takeIf {
                    it.size == 3
                }?.let {
                    val lat = it[1].toDoubleOrNull()
                    val lng = it[2].toDoubleOrNull()
                    evaluateNullables(lat, lng, both = {
                        val location = Location("preferred location")
                        location.latitude = it.first
                        location.longitude = it.second
                        location
                    })
                }
    }

    @SuppressLint("MissingPermission")
    private fun recentLocation(): Single<Location> {
        return Single.create { emitter ->
            lastKnownLocation?.let {
                emitter.onSuccess(it)
            } ?: {
                val criteria = Criteria()
                criteria.isAltitudeRequired = false
                criteria.isSpeedRequired = false
                criteria.bearingAccuracy = Criteria.ACCURACY_LOW
                criteria.verticalAccuracy = Criteria.ACCURACY_LOW
                criteria.horizontalAccuracy = Criteria.ACCURACY_MEDIUM
                criteria.isCostAllowed = true
                locationManager.requestSingleUpdate(criteria, object : LocationListener {
                    override fun onLocationChanged(location: Location) = emitter.onSuccess(location)
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String?) {}
                    override fun onProviderDisabled(provider: String?) {}
                }, null)
            }()
        }
    }


}