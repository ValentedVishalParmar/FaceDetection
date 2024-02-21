package com.vishal.facedetection

import android.app.Application
import androidx.camera.core.CameraX

class FaceDetectionApp: Application() {

    companion object {
        var app: FaceDetectionApp ? = null
        const val TAG = "ERROR>>"
    }

    override fun onCreate() {
        super.onCreate()
        app = this@FaceDetectionApp
    }
}