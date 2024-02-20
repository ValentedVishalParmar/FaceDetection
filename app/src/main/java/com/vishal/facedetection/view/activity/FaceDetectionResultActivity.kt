package com.vishal.facedetection.view.activity

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vishal.facedetection.R
import com.vishal.facedetection.databinding.ActivityFaceDetectionResultBinding
import com.vishal.facedetection.util.ExtraKey.EXTRA_KEY_FACE_STATUS_TYPE
import com.vishal.facedetection.util.FaceStatus
import com.vishal.facedetection.util.checkAppPermissions
import com.vishal.facedetection.util.getData
import com.vishal.facedetection.util.setStatusBar
import com.vishal.facedetection.util.setText
import com.vishal.facedetection.util.showAlert
import com.vishal.facedetection.util.speak
import java.util.Locale

class FaceDetectionResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaceDetectionResultBinding
    private var textToSpeech: TextToSpeech ? = null
    private lateinit var strFaceStatus: String
    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBar()
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectionResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        onClick()
    }

    private fun init() {
        strFaceStatus = getData(intent, EXTRA_KEY_FACE_STATUS_TYPE) ?: ""
        initAndManageFaceDetectionResult()
    }

    private fun onClick() {
        binding.btnOk.setOnClickListener {
            finish()
        }
    }

    private fun initAndManageFaceDetectionResult() {
        textToSpeech = TextToSpeech(this@FaceDetectionResultActivity) { i ->
            if (i != TextToSpeech.ERROR) {
                textToSpeech?.setLanguage(Locale.UK)
                when (strFaceStatus) {
                    FaceStatus.NO_FACE.name -> {
                        speak(getString(R.string.err_face_not_detected), textToSpeech)
                        if (checkAppPermissions()) {
                            showNotification()
                        }
                        setText(binding.tvMessage, getString(R.string.err_face_not_detected))

                    }

                    FaceStatus.NOT_CENTERED.name -> {
                        speak(getString(R.string.err_face_detected_not_center), textToSpeech)
                        setText(binding.tvMessage, getString(R.string.err_face_detected_not_center))
                    }

                    FaceStatus.TOO_FAR.name -> {
                        speak(getString(R.string.err_face_far_detected), textToSpeech)
                        setText(binding.tvMessage, getString(R.string.err_face_far_detected))
                    }

                    FaceStatus.VALID.name -> {
                        speak(getString(R.string.err_face_detected), textToSpeech)
                        setText(binding.tvMessage, getString(R.string.err_face_detected))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        releaseTTS()
        super.onDestroy()
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
    }
    @SuppressLint("MissingPermission")
    private fun showNotification() {
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            val channelId = getString(R.string.default_notification_channel_id)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, channelId).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.app_name)).setContentText(getString(R.string.no_face_detected)).setAutoCancel(true).setSound(defaultSoundUri).setContentIntent(pendingIntent)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, getString(R.string.app_name).plus(getString(R.string.notification)), NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
            notificationManager.notify(0, notificationBuilder.build())

    }
}