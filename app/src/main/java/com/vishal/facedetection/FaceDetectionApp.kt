package com.vishal.facedetection

import android.app.Application
import androidx.camera.core.CameraX

class FaceDetectionApp: Application() {

    companion object {
        var app: FaceDetectionApp ? = null
    }

    override fun onCreate() {
        super.onCreate()
        app = this@FaceDetectionApp
    }
}