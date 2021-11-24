package com.example.recentweather.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.recentweather.model.network.TwoDayWeatherEntity
import com.example.recentweather.model.utils.CheckGpsResult
import com.example.recentweather.model.utils.GetLocationResult
import com.example.recentweather.model.utils.GpsUtil
import com.example.recentweather.ui.permission.PermissionRequestActivity
import com.example.recentweather.ui.theme.RecentWeatherTheme
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationSettingsResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
    抓目前位置，轉成地區
    抓API比對地區，沒有的話顯示區域外

    --

    權限要求
    Location粗略權限
    網路權限

    使用API
    目前天氣
    未來兩天天氣

    retrofit
    moshi

    room

    LocationUtil
    取得location轉地區

    repository

    viewModel

    weatherScreen

    MainActivity
 */

//TODO("當area或者twoDayWeatherEntityList改變時，自動檢查，如果兩者都不為空，更新目前的entity")

private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    @Inject lateinit var gpsUtil : GpsUtil
    private val checkGpsResult by lazy { createCheckGpsResult() }
    private val locationCallback by lazy { createLocationCallback() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecentWeatherTheme {
                MainScreen(viewModel = viewModel)
            }
        }
        viewModel.setCheckPermissionResult(
            ContextCompat.checkSelfPermission(this, PermissionRequestActivity.COARSE_LOCATION))
        if (viewModel.checkPermissionResult.value == PackageManager.PERMISSION_DENIED) {
            startActivity(Intent(this, PermissionRequestActivity::class.java))
        }
        //get last location
        gpsUtil.getLastLocation(object : GetLocationResult{
            override fun onSuccess(location: Location) {
//                Log.d(TAG, "onLocationResult: location.lat: ${location.latitude}, lng: ${location.longitude}")
//                viewModel.setCurrentLocation(location)
                viewModel.updateAreaNameByLocation(location)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        //refresh weather entity
        lifecycleScope.launch {
            viewModel.refreshTwoDayWeatherEntityList()
        }
    }

    override fun onResume() {
        super.onResume()
        //update gps
        viewModel.setCheckPermissionResult(
            ContextCompat.checkSelfPermission(this, PermissionRequestActivity.COARSE_LOCATION))
        if (viewModel.checkPermissionResult.value == PackageManager.PERMISSION_GRANTED){
            //show normal view
            gpsUtil.checkGpsIsOpen(this, checkGpsResult)
        } else {
            //show no permission view
        }
    }

    override fun onPause() {
        //stop update gps
        gpsUtil.stopLocationUpdates(locationCallback)
        super.onPause()
    }

    private fun createCheckGpsResult(): CheckGpsResult {
        return object: CheckGpsResult{
            override fun onSuccess(locationSettingResponse: LocationSettingsResponse) {
//                Log.d(TAG, "createCheckGpsResult onSuccess: ")
                gpsUtil.startLocationUpdates(locationCallback)
            }

            override fun onFail(exception: Exception) {
//                Log.d(TAG, "createCheckGpsResult onFail: ")
                exception.printStackTrace()
            }

        }
    }

    private fun createLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                var accurateLocation: Location = locationResult.locations.first()
                //get must accurate location
                for (location in locationResult.locations){
                    if (location.hasAccuracy() && location.accuracy < accurateLocation.accuracy) {
                        accurateLocation = location
                    }
                }
//                Log.d(TAG, "onLocationResult: accurateLocation.lat: ${accurateLocation.latitude}, lng: ${accurateLocation.longitude}")
//                viewModel.setCurrentLocation(accurateLocation)
                viewModel.updateAreaNameByLocation(accurateLocation)
            }
        }
    }
}