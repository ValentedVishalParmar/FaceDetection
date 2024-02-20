package com.vishal.facedetection.view.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vishal.facedetection.databinding.ActivitySplashBinding
import com.vishal.facedetection.util.checkAppPermissions
import com.vishal.facedetection.util.finishAndNavigateTo
import com.vishal.facedetection.util.setStatusBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBar()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        lifecycleScope.launch {
            delay(2000)
            if (checkAppPermissions()) {
                finishAndNavigateTo(FaceDetectionActivity::class.java)
            }
        }
    }
}