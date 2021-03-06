package com.example.recentweather.ui.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.recentweather.model.network.TwoDayWeatherEntity
import com.example.recentweather.model.utils.*
import com.example.recentweather.ui.areachoose.AreaChooseActivity
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

//TODO("可選地點")

/*
    建一個titleBar
    地區移動到TitleBar
    地區右邊新增一個往下的箭頭
    點地區跳到選地區screen

    地區screen
    從資料庫抓所有地區的標題
    列表式呈現

    點選item後
    SingleTop開啟MainActivity

    MainActivity調整
    如果從intent接到area，就不會用gps方式更新area
 */

private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    @Inject lateinit var gpsUtils : GpsUtils
    private val permissionUtils: PermissionUtils by lazy{ PermissionUtils(this, createPermissionResult()) }
    private val checkGpsResult by lazy { createCheckGpsResult() }
    private val locationCallback by lazy { createLocationCallback() }

    private val chooseActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val area = intent?.getStringExtra("location_name")
            if (area != null && area.isNotBlank() && area.isNotEmpty()) {
                gpsUtils.stopLocationUpdates(locationCallback)
                viewModel.setAreaAndStopUpdateLocation(area)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecentWeatherTheme {
                MainScreen(
                    viewModel = viewModel,
                    { startToAreaActivity() }
                )
            }
        }
        with(viewModel) {
            area.observe(this@MainActivity, ::handleArea)
            twoDayWeatherEntityList.observe(this@MainActivity, ::handleTwoDayWeatherEntityList)
        }
        viewModel.setCheckPermissionResult(
//            ContextCompat.checkSelfPermission(this, PermissionRequestActivity.COARSE_LOCATION))
        ContextCompat.checkSelfPermission(this, PermissionUtils.COARSE_LOCATION))
        if (viewModel.checkPermissionResult.value == PackageManager.PERMISSION_DENIED) {
            permissionUtils.checkAndRequestPermission(PermissionUtils.COARSE_LOCATION)
//            permissionUtils.checkAndRequestPermissionList(listOf(PermissionUtils.COARSE_LOCATION))
        }
        //get last location
        gpsUtils.getLastLocation(object : GetLocationResult{
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
        if (viewModel.autoUpdateArea) {
            viewModel.setCheckPermissionResult(
                ContextCompat.checkSelfPermission(this, PermissionUtils.COARSE_LOCATION)
            )
            if (viewModel.checkPermissionResult.value == PackageManager.PERMISSION_GRANTED) {
                //show normal view
                gpsUtils.checkGpsIsOpen(this, checkGpsResult)
            }
        }
    }

    override fun onPause() {
        //stop update gps
        gpsUtils.stopLocationUpdates(locationCallback)
        super.onPause()
    }

    //permission

    private fun createPermissionResult() : PermissionResult {
        return object: PermissionResult{
            override fun onGranted() {
                viewModel.setCheckPermissionResult(
                    ContextCompat.checkSelfPermission(this@MainActivity, PermissionUtils.COARSE_LOCATION))
                gpsUtils.startLocationUpdates(locationCallback)
            }

            override fun onDenied() {
                viewModel.setCheckPermissionResult(
                    ContextCompat.checkSelfPermission(this@MainActivity, PermissionUtils.COARSE_LOCATION))
            }

        }
    }

    //location

    private fun createCheckGpsResult(): CheckGpsResult {
        return object: CheckGpsResult{
            override fun onSuccess(locationSettingResponse: LocationSettingsResponse) {
//                Log.d(TAG, "createCheckGpsResult onSuccess: ")
                gpsUtils.startLocationUpdates(locationCallback)
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

    //live data observe

    private fun handleArea(area: String?){
        if (area == null || area == "") return
        viewModel.refreshCurrentWeatherEntity()
    }

    private fun handleTwoDayWeatherEntityList(twoDayWeatherEntityList: List<TwoDayWeatherEntity>?) {
        if (twoDayWeatherEntityList == null || twoDayWeatherEntityList.isEmpty()) return
        viewModel.refreshCurrentWeatherEntity()
    }

    //intent

    private fun startToAreaActivity(){
//        startActivity(Intent(this, AreaChooseActivity::class.java))
        chooseActivityLauncher.launch(Intent(this, AreaChooseActivity::class.java))
    }
}