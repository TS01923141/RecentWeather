package com.example.recentweather.model.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface GetLocationResult {
    fun onSuccess(location: Location)
}

interface CheckGpsResult {
    fun onSuccess(locationSettingResponse: LocationSettingsResponse)
    fun onFail(exception: Exception)
}

private const val REQUEST_CHECK_SETTINGS = 0x1
class GpsUtil @Inject constructor(context: Context) {
    private val context = context
    private val fusedLocationClient: FusedLocationProviderClient
    = LocationServices.getFusedLocationProviderClient(context)

    fun checkGpsIsOpen(activity: Activity, checkGpsResult: CheckGpsResult) {
        val builder = LocationSettingsRequest.Builder()
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build()).apply {
            addOnSuccessListener {
                checkGpsResult.onSuccess(it)
            }
            addOnFailureListener {
                if (it is ResolvableApiException){
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        it.startResolutionForResult(activity,
                            REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
                checkGpsResult.onFail(it)
            }
        }
    }

    fun createLocationRequest(): LocationRequest? {
        return LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            numUpdates = 1
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(getLocationResult: GetLocationResult){
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) getLocationResult.onSuccess(it)
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(locationCallback: LocationCallback,
                             locationRequest: LocationRequest? = createLocationRequest()) {
        if (locationRequest == null) return
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun stopLocationUpdates(locationCallback: LocationCallback) {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}