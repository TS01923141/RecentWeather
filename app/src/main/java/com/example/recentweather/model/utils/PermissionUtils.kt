package com.example.recentweather.model.utils

import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.recentweather.R
import java.lang.StringBuilder

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
    private var requestPermission = ""

    private fun checkFinish() {
        deniedCount = 0
        requestPermission = ""
    }

    fun checkAndRequestPermission(requestPermission: String) {
        this.requestPermission = requestPermission
        when {
            ContextCompat.checkSelfPermission(
                activity,
                requestPermission) == PackageManager.PERMISSION_GRANTED -> {
                    permissionResult.onGranted()
            }
            else -> {
                requestPermissionLauncher.launch(requestPermission)
            }
        }
    }

    fun checkAndRequestPermissionList(permissionList: List<String>) {
        val requestPermissionList = mutableListOf<String>()
        permissionList.forEach {
            if (ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_DENIED) {
                requestPermissionList.add(it)
            }
        }
        if (requestPermissionList.isNotEmpty()){
            requestPermissionListLauncher.launch(requestPermissionList.toTypedArray())
        }
    }

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted){
                permissionResult.onGranted()
                checkFinish()
            } else {
                deniedCount++
                if (deniedCount>=3) {
                    permissionResult.onDenied()
                    checkFinish()
                }
                else {
                    if (deniedCount == 1) showExplainPermissionDialog(requestPermission)
                    else checkAndRequestPermission(requestPermission)
                }
            }
        }

    private val requestPermissionListLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->
            var requestList = mutableListOf<String>()
            var grantAllPermission = true
            for (result in resultMap){
                if (!result.value) {
                    requestList.add(result.key)
                    grantAllPermission = false
                }
            }
            if (grantAllPermission) {
                permissionResult.onGranted()
                checkFinish()
            }else {
                deniedCount++
                if (deniedCount >= 3) {
                    permissionResult.onDenied()
                    checkFinish()
                } else {
                    if (deniedCount == 1) showExplainPermissionListDialog(requestList)
                    else checkAndRequestPermissionList(requestList)
                }
            }
        }

    private fun showExplainPermissionDialog(requestPermission: String) {
        AlertDialog.Builder(activity)
            .setCancelable(false)
            .setMessage(buildRequestMessage(listOf(requestPermission)))
            .setNegativeButton(activity.getString(R.string.no_thanks), DialogInterface.OnClickListener { dialogInterface, i -> permissionResult.onDenied() })
            .setPositiveButton(activity.getString(R.string.ok), DialogInterface.OnClickListener { dialogInterface, i ->
                requestPermissionLauncher.launch(requestPermission)
            }).show()
    }

    private fun showExplainPermissionListDialog(requestPermissionList: List<String>) {
        AlertDialog.Builder(activity)
            .setCancelable(false)
            .setMessage(buildRequestMessage(requestPermissionList))
            .setNegativeButton(activity.getString(R.string.no_thanks), DialogInterface.OnClickListener { dialogInterface, i -> permissionResult.onDenied() })
            .setPositiveButton(activity.getString(R.string.ok), DialogInterface.OnClickListener { dialogInterface, i ->
                requestPermissionListLauncher.launch(requestPermissionList.toTypedArray())
            }).show()
    }

    private fun buildRequestMessage(permissionList: List<String>): String {
        val message : StringBuilder = StringBuilder()
        permissionList.forEach {
            when(it) {
                COARSE_LOCATION -> message.append(activity.getString(R.string.permission_explain_location)).append("\n")
            }
        }
        return message.toString()
    }
}