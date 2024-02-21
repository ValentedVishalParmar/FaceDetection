package com.vishal.facedetection.util.facedetection

import android.graphics.Rect
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import com.vishal.facedetection.FaceDetectionApp.Companion.TAG
import com.vishal.facedetection.util.FaceStatus
import com.vishal.facedetection.util.camera.BaseImageAnalyzer
import com.vishal.facedetection.util.camera.GraphicOverlay
import com.vishal.facedetection.view.custom.FaceContourGraphic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class FaceContourDetectionProcessor(
    private val activity: AppCompatActivity,
    private val view: GraphicOverlay,
    private val onSuccessCallback: ((FaceStatus) -> Unit)
) : BaseImageAnalyzer<List<Face>>() {
    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setClassificationMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.15f)
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)
    private var isResultSendInCallBack = false
    override val graphicOverlay: GraphicOverlay
        get() = view

    override fun detectInImage(image: InputImage): Task<List<Face>> {
       val result = detector.process(image).addOnSuccessListener { faces ->

        }.addOnFailureListener { e ->
                e.printStackTrace()

            }

        return  result
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun onSuccess(
        results: List<Face>,
        graphicOverlay: GraphicOverlay,
        rect: Rect
    ) {
        graphicOverlay.clear()
        if (results.isNotEmpty()) {
            val faceGraphic = FaceContourGraphic(activity, graphicOverlay, results[results.lastIndex], rect, onSuccessCallback)
            graphicOverlay.add(faceGraphic)

            graphicOverlay.postInvalidate()

        } else {
            this.activity.lifecycleScope.launch {
                delay(1 * 1000)
                if (!isResultSendInCallBack) {
                    onSuccessCallback(FaceStatus.NO_FACE)
                    isResultSendInCallBack = true

                } else {
                 detector.close()
                }
                Log.e(TAG, "Face Detector failed: ")
            }
        }
    }

    override fun onFailure(e: Exception) {
        this.activity.lifecycleScope.launch {
            delay(5 * 1000)
            if (!isResultSendInCallBack) {
                onSuccessCallback(FaceStatus.NO_FACE)
                isResultSendInCallBack = true
            } else {
                detector.close()
            }
            Log.e(TAG, "Face Detector failed. $e:")
        }
    }
}