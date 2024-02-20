package com.vishal.facedetection.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.vishal.facedetection.R
import com.vishal.facedetection.databinding.ActivityPermissionBinding
import com.vishal.facedetection.util.checkCameraPermission
import com.vishal.facedetection.util.checkNotificationPermission
import com.vishal.facedetection.util.finishAndNavigateTo
import com.vishal.facedetection.util.openAppSettings
import com.vishal.facedetection.util.setStatusBar

class PermissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBar()
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        onClick()
    }

    private fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            binding.llNotification.visibility = View.VISIBLE
        }
    }

    private fun onClick() {
        binding.btnNext.setOnClickListener {
            checkPermissions()
        }
    }

    private fun checkPermissions() {

            if (checkNotificationPermission()) {
                Log.d("PermissionActivity", "PermissionActivity>>Notification>>checkNotificationPermission>>True")

                if (checkCameraPermission()) {
                    Log.d("PermissionActivity", "PermissionActivity>>Camera>>checkLocationPermission>>True>>Home")
                    finishAndNavigateTo(FaceDetectionActivity::class.java)
                } else {
                    Log.d("PermissionActivity", "PermissionActivity>>Camera>>checkLocationPermission>>False")
                    requestCameraPermission()
                }

            } else {
                Log.d("PermissionActivity", "PermissionActivity>>Notification>>checkNotificationPermission>>False")
                requestNotificationPermission()
            }
     }

    private fun requestCameraPermission() {
        Log.d("PermissionActivity", "PermissionActivity>>Camera>>Request")
        launchCameraPermission.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.POST_NOTIFICATIONS))
    }

    @SuppressLint("InlinedApi")
    private fun requestNotificationPermission() {

        Log.d("PermissionActivity", "PermissionActivity>>Notification>>Request")
        launchNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
    private val launchCameraPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var allGranted = false

        permissions.entries.forEach { it ->
            val isGranted = it.value
            allGranted = isGranted
        }

        if (allGranted && checkNotificationPermission()) {
            finishAndNavigateTo(FaceDetectionActivity::class.java)

        } else {
            if (checkCameraPermission().not()) {
                showCameraDialog()
            } else {
                showNotificationDialog()
            }
        }
    }

    private val launchNotificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
        Log.d("PermissionActivity", "PermissionActivity>>Notification>>Launcher>>$permission")
        requestCameraPermission()
    }

    private fun showNotificationDialog() {
        AlertDialog.Builder(this@PermissionActivity)
            .setTitle(R.string.title_permission_notification)
            .setMessage(R.string.permission_notification)
            .setPositiveButton(getString(R.string.open_setting)) { _, _ ->
                openAppSettings()

            }.setNegativeButton(getString(R.string.cancel)) { _, _ ->

            }
            .create()
            .show()
    }

    private fun showCameraDialog() {
        AlertDialog.Builder(this@PermissionActivity)
            .setTitle(R.string.title_camera_permission)
            .setMessage( getString(R.string.text_camera_permission))
            .setPositiveButton(getString(R.string.open_setting)) { _, _ ->
                openAppSettings()
            }
            .create()
            .show()
    }

}
