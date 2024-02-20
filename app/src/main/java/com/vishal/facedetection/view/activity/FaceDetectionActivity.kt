package com.vishal.facedetection.view.activity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.vishal.facedetection.R
import com.vishal.facedetection.databinding.ActivityFaceDetectionBinding
import com.vishal.facedetection.util.ExtraKey.EXTRA_KEY_FACE_STATUS_TYPE
import com.vishal.facedetection.util.FaceStatus
import com.vishal.facedetection.util.camera.CameraManager
import com.vishal.facedetection.util.checkAppPermissions
import com.vishal.facedetection.util.finishAndNavigateTo
import com.vishal.facedetection.util.handleOnBackPressed
import com.vishal.facedetection.util.navigateTo
import com.vishal.facedetection.util.showAlert
import com.vishal.facedetection.util.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


class FaceDetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceDetectionBinding
    private lateinit var cameraManager: CameraManager
    private var isExit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        onClicks()

    }

    private fun init() {
        if (checkAppPermissions()) {
            cameraManager = CameraManager(this, binding.previewViewFinder, this, binding.graphicOverlayFinder, ::processPicture)
            lifecycleScope.launch {
                delay(500)
                cameraManager.startCamera()
            }
        }

        handleOnBackPressed {
            askForPressBackButtonTwiceForExitApp()
        }
    }
    private fun onClicks() {
        binding.btnSwitch.setOnClickListener {
            cameraManager.changeCameraSelector()
        }
    }
    private fun processPicture(faceStatus: FaceStatus) {
        Log.e("ERROR>>", "This is it ${faceStatus.name}")
        lifecycleScope.launch {
            delay(500)
            finishAndNavigateTo(FaceDetectionResultActivity::class.java) {
                this.putString(EXTRA_KEY_FACE_STATUS_TYPE, faceStatus.name)
            }
        }
    }

    private fun askForPressBackButtonTwiceForExitApp() {
        if (isExit) {
            finishAndRemoveTask()
            return
        }

        isExit = true
        toast(getString(R.string.exit_app))
        lifecycleScope.launch {
            delay(2000)
            isExit = false
        }
    }
}