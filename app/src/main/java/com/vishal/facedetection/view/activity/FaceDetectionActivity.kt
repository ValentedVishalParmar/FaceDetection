package com.vishal.facedetection.view.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vishal.facedetection.databinding.ActivityFaceDetectionBinding
import com.vishal.facedetection.util.FaceStatus
import com.vishal.facedetection.util.camera.CameraManager
class FaceDetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceDetectionBinding
    private lateinit var cameraManager: CameraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createCameraManager()
        checkForPermission()
        onClicks()

    }


    private fun checkForPermission() {
        if (allPermissionsGranted()) {
            cameraManager.startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun onClicks() {
        binding.btnSwitch.setOnClickListener {
            cameraManager.changeCameraSelector()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraManager.startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun createCameraManager() {
        cameraManager = CameraManager(
            this,
            binding.previewViewFinder,
            this,
            binding.graphicOverlayFinder,
            ::processPicture
        )
    }


    private fun processPicture(faceStatus: FaceStatus) {
        Log.e("facestatus", "This is it ${faceStatus.name}")
        when (faceStatus) {
            FaceStatus.NO_FACE -> {
                Log.d("ERROR>>", "init: Not Face Detected")
            }

            FaceStatus.NOT_CENTERED -> {
                Log.d("ERROR>>", "init: Not Centered Face Detected")
            }

            FaceStatus.TOO_FAR -> {
                Log.d("ERROR>>", "init: Face To Far Detected")

            }

            FaceStatus.VALID -> {
                Log.d("ERROR>>", "init: Face Detected")

            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }
}