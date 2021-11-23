package com.example.recentweather.ui.permission

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.recentweather.R
import com.example.recentweather.ui.permission.ui.theme.RecentWeatherTheme
import java.util.jar.Manifest

private const val TAG = "PermissionRequestActivi"
class PermissionRequestActivity : ComponentActivity() {

    companion object{
        const val COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION
    }

    private var deniedCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecentWeatherTheme {
                TransparentScreen()
            }
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "onCreate: 1")
                    finish()
                }
            shouldShowRequestPermissionRationale(COARSE_LOCATION) -> {
                Log.d(TAG, "onCreate: 2")
                showExplainPermissionDialog()
            }
            else -> {
                Log.d(TAG, "onCreate: 3")
                requestPermissionLauncher.launch(COARSE_LOCATION)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted){
                finish()
            } else {
                deniedCount++
                Log.d(TAG, "deniedCount: $deniedCount")
                showExplainPermissionDialog()
                if (deniedCount>=3){
                    finish()
                }
            }
        }

    private fun showExplainPermissionDialog() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setMessage(getString(R.string.permission_explain_location))
            .setNegativeButton(getString(R.string.no_thanks), DialogInterface.OnClickListener { dialogInterface, i -> finish() })
            .setPositiveButton(getString(R.string.ok), DialogInterface.OnClickListener { dialogInterface, i ->
                requestPermissionLauncher.launch(COARSE_LOCATION)
            }).show()
    }
}

@Composable
fun TransparentScreen() {
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) {}
}