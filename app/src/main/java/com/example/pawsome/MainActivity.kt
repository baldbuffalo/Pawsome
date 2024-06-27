package com.example.pawsome

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 100
    private lateinit var scanButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Sets the activity_main.xml as the content view

        scanButton = findViewById(R.id.ScanButton)

        // Set click listener for the scanButton
        scanButton.setOnClickListener {
            // Check if the camera permission is already granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted, open CameraPreview activity
                openCameraPreview()
            } else {
                // Permission not yet granted, request it
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }
    }

    // Function to handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open CameraPreview activity
                openCameraPreview()
            } else {
                // Permission denied, do nothing or handle as needed
                // You may inform the user or disable functionality
            }
        }
    }

    // Function to open CameraPreview activity
    private fun openCameraPreview() {
        // Create an intent to start CameraPreview activity
        startActivity(CameraPreview.newIntent(this))
    }
}
