package com.vishal.facedetection

import android.app.Application

class FaceDetectionApp: Application() {

    companion object {
        var app: FaceDetectionApp ? = null
    }

    override fun onCreate() {
        super.onCreate()
        app = this@FaceDetectionApp
    }
}