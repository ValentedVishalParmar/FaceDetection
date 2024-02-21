package com.vishal.facedetection.view.activity

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.vishal.facedetection.R
import com.vishal.facedetection.databinding.ActivityFaceDetectionBinding
import com.vishal.facedetection.util.FaceStatus
import com.vishal.facedetection.util.alertDialog
import com.vishal.facedetection.util.camera.CameraManager
import com.vishal.facedetection.util.checkAppPermissions
import com.vishal.facedetection.util.handleOnBackPressed
import com.vishal.facedetection.util.showAlert
import com.vishal.facedetection.util.speak
import com.vishal.facedetection.util.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class FaceDetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceDetectionBinding
    private lateinit var cameraManager: CameraManager
    private var textToSpeech: TextToSpeech? = null
    private var isExit = false
    private var isAlreadyExecutedOnce = false
    private var intCounter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        onClicks()

    }

    private fun init() {
        if (checkAppPermissions()) {
            cameraManager = CameraManager(
                this@FaceDetectionActivity,
                binding.previewViewFinder,
                this,
                binding.graphicOverlayFinder,
                ::manageFaceDetectionResult
            )
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
            cameraManager.flipCameraSide()
            releaseTTS()
            //isAlreadyExecutedOnce = false
        }
    }

    private fun manageFaceDetectionResult(faceStatus: FaceStatus) {
        intCounter += 1
        Log.e("VVVV>>", "This is it  ${faceStatus}: $intCounter")

        var message: String = ""
        if(!isAlreadyExecutedOnce) {
            textToSpeech = TextToSpeech(this@FaceDetectionActivity) { i ->
                if (i != TextToSpeech.ERROR) {
                    textToSpeech?.setLanguage(Locale.UK)
                    lifecycleScope.launch {
                        delay(10 * 1000)
                        when (faceStatus) {
                            FaceStatus.NO_FACE -> {
                                message = getString(R.string.err_face_not_detected)
                                if (checkAppPermissions() && alertDialog?.isShowing?.not() == true) {
                                    showNotification()
                                }
                                speak(message, textToSpeech)
                                if (alertDialog?.isShowing?.not() == true) {
                                    showAlert(getString(R.string.face_detection_result), message) {
                                        releaseTTS()
                                    }
                                }
                            }

                            FaceStatus.NOT_CENTERED -> {
                                message = getString(R.string.err_face_detected_not_center)
                                speak(message, textToSpeech)
                                if (alertDialog?.isShowing?.not() == true) {
                                    showAlert(getString(R.string.face_detection_result), message) {
                                        releaseTTS()
                                    }
                                }
                            }

                            FaceStatus.TOO_FAR -> {
                                message = getString(R.string.err_face_far_detected)
                                speak(message, textToSpeech)
                                if (alertDialog?.isShowing?.not() == true) {
                                    showAlert(getString(R.string.face_detection_result), message) {
                                        releaseTTS()
                                    }
                                }
                            }

                            FaceStatus.VALID -> {
                                message = getString(R.string.err_face_detected)
                                speak(message, textToSpeech)
                                if (alertDialog?.isShowing?.not() == true) {
                                    showAlert(getString(R.string.face_detection_result), message) {
                                        releaseTTS()
                                    }
                                }
                            }
                        }
                    }

                }
            }
            isAlreadyExecutedOnce = true

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseTTS()
    }

    override fun onPause() {
        releaseTTS()
        super.onPause()
    }

    private fun releaseTTS() {
        if (textToSpeech != null) {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
        isAlreadyExecutedOnce = false
    }

    override fun onResume() {
        super.onResume()
        isAlreadyExecutedOnce = false
        intCounter = 0
    }
    @SuppressLint("MissingPermission")
    private fun showNotification() {
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val channelId =
            getString(R.string.default_notification_channel_id).plus(getString(R.string.notification))
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(this, channelId).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.face_detection_result))
                .setContentText(getString(R.string.no_face_detected)).setAutoCancel(true)
                .setSound(defaultSoundUri).setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.app_name).plus(getString(R.string.notification)),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(1, notificationBuilder.build())


    }

    private fun askForPressBackButtonTwiceForExitApp() {
        if (isExit) {
            finishAndRemoveTask()
            return
        }

        isExit = true
        toast(getString(R.string.exit_app))
        lifecycleScope.launch {
            delay(2 * 1000)
            isExit = false
        }
    }
}