package com.example.recentweather.model.utils

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.recentweather.R

interface PermissionResult{
    fun onGranted()
    fun onDenied()
}

private const val TAG = "PermissionUtils"
class PermissionUtils(private val activity: ComponentActivity, private val permissionResult: PermissionResult) {

    companion object{
        const val COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION
    }

    private var deniedCount = 0

    fun checkAndRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                activity,
                COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                    permissionResult.onGranted()
            }
            activity.shouldShowRequestPermissionRationale(COARSE_LOCATION) -> {
                showExplainPermissionDialog()
            }
            else -> {
                requestPermissionLauncher.launch(COARSE_LOCATION)
            }
        }
    }

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted){
                permissionResult.onGranted()
            } else {
                deniedCount++
                Log.d(TAG, "deniedCount: $deniedCount")
                showExplainPermissionDialog()
                if (deniedCount>=3){
                    permissionResult.onDenied()
                }
            }
        }

    private fun showExplainPermissionDialog() {
        AlertDialog.Builder(activity)
            .setCancelable(false)
            .setMessage(activity.getString(R.string.permission_explain_location))
            .setNegativeButton(activity.getString(R.string.no_thanks), DialogInterface.OnClickListener { dialogInterface, i -> permissionResult.onDenied() })
            .setPositiveButton(activity.getString(R.string.ok), DialogInterface.OnClickListener { dialogInterface, i ->
                requestPermissionLauncher.launch(COARSE_LOCATION)
            }).show()
    }
}