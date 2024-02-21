package com.vishal.facedetection.util

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.vishal.facedetection.BuildConfig
import com.vishal.facedetection.R
import com.vishal.facedetection.view.activity.FaceDetectionActivity
import com.vishal.facedetection.view.activity.PermissionActivity
var builder : AlertDialog.Builder? = null
var alertDialog :AlertDialog? = null
fun Activity.showAlert(title: String, message: String, onButtonClick: () -> Unit) {
    builder = AlertDialog.Builder(this)
    builder?.setTitle(title)
    builder?.setMessage(message)
    builder?.setPositiveButton(android.R.string.yes) { dialog, which ->
        alertDialog?.hide()
       dialog.dismiss()
       onButtonClick.invoke()
    }
    alertDialog = builder?.create()

    if (alertDialog?.isShowing?.not() == true) {
        builder?.show()
    }
}
fun AppCompatActivity.setStatusBar() {
    enableEdgeToEdge()

    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = false
    }
}

fun Context?.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

//START ACTIVITY
fun <T> Activity.navigateTo(mClass: Class<T>, bundle: (Bundle.() -> Unit) = {}) {
    val intent = Intent(this, mClass)
    intent.putExtras(Bundle().apply(bundle))
    startActivity(intent)
}

fun AppCompatActivity.handleOnBackPressed(onBackPressed: () -> Unit = { finish() }) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed.invoke()
        }
    })
}

//CHECK APP PERMISSIONS
fun Activity.checkAppPermissions(): Boolean {
    try {
        return if (checkNotificationPermission()) {

            if (checkCameraPermission()) {
                true

            } else {
                navigateToPermissionScreen()
                false
            }

        } else {
            navigateToPermissionScreen()
            false
        }

    } catch (e: Exception) {
        e.message
        Log.e("Exception>>", "CheckAppPermissions>> ${e.message}")
    }

    return true
}

//CHECK NOTIFICATION PERMISSION
fun Activity.checkNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    } else {
        true
    }
}

//CHECK CAMERA PERMISSION
fun Context.checkCameraPermission(): Boolean {
    return ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED))
}

fun getData(intent: Intent?, data: String): String? {
    return intent?.extras?.getString(data)
}
//OPEN APP SETTINGS
fun Activity.openAppSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
    intent.data = uri
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

private fun Activity.navigateToPermissionScreen() {
    navigateTo(PermissionActivity::class.java)
    finish()
}

//START ACTIVITY
fun <T> Activity.finishAndNavigateTo(mClass: Class<T>, bundle: (Bundle.() -> Unit) = {}) {
    val intent = Intent(this, mClass)
    intent.putExtras(Bundle().apply(bundle))
    startActivity(intent)
    finish()
}

fun AppCompatActivity.speak(message:String, textToSpeech: TextToSpeech?) {
    textToSpeech?.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
}
fun setText(textView: TextView, string: String?) {
    textView.text = string
}