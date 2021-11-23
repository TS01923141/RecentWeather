package com.example.recentweather.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.PermissionRequest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.recentweather.ui.permission.PermissionRequestActivity
import com.example.recentweather.ui.theme.RecentWeatherTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
    抓目前位置，轉成地區
    抓API比對地區，沒有的話顯示區域外

    --

    權限要求
    Location粗略權限
    網路權限

    使用API
    目前天氣
    未來36小時天氣

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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecentWeatherTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(color = MaterialTheme.colors.background) {
//                    Greeting("Android")
//                }
                MainScreen(viewModel = viewModel)
            }
        }
        viewModel.setCheckPermissionResult(
            ContextCompat.checkSelfPermission(this, PermissionRequestActivity.COARSE_LOCATION))
        if (viewModel.checkPermissionResult.value == PackageManager.PERMISSION_DENIED) {
            startActivity(Intent(this, PermissionRequestActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.setCheckPermissionResult(
            ContextCompat.checkSelfPermission(this, PermissionRequestActivity.COARSE_LOCATION))
//        if (checkPermissionResult == PackageManager.PERMISSION_GRANTED){
//            //show normal view
//        } else {
//            //show no permission view
//        }
        lifecycleScope.launch {
            viewModel.refreshTwoDayWeatherEntityList()
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RecentWeatherTheme {
        Greeting("Android")
    }
}