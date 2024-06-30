package com.example.pawsome

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var scanButton: Button
    private lateinit var profileButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanButton = findViewById(R.id.ScanButton)
        scanButton.setOnClickListener {
            if (isCameraPermissionGranted()) {
                openCameraPreviewActivity()
            } else {
                requestCameraPermission()
            }
        }

        profileButton = findViewById(R.id.ProfileButton)
        profileButton.setOnClickListener {
            openProfileActivity()
        }

        // Initialize UpdateChecker
        val currentVersion = getCurrentVersion()
        val updateChecker = UpdateChecker(this)
        updateChecker.checkForUpdate(currentVersion)
    }

    private fun getCurrentVersion(): String {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName ?: getStoredVersion() ?: "1.0.0"
        } catch (e: Exception) {
            e.printStackTrace()
            getStoredVersion() ?: "1.0.0"
        }
    }

    private fun getStoredVersion(): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("stored_version", null)
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    private fun openCameraPreviewActivity() {
        val intent = Intent(this, CameraPreviewActivity::class.java)
        startActivity(intent)
    }

    private fun openProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
    }
}
