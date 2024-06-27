package com.example.pawsome

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private lateinit var cameraPreview: FrameLayout
    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scanButton: Button = findViewById(R.id.ScanButton)
        cameraPreview = findViewById(R.id.camera_preview)

        scanButton.setOnClickListener {
            showCameraPreview()
        }

        // Initialize LocationManager and LocationListener
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Handle location updates here
                val latitude = location.latitude
                val longitude = location.longitude
                // Do something with latitude and longitude
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}
        }

        // Request location updates
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener as LocationListener
            )
        } else {
            // Handle permission not granted
        }
    }

    private fun showCameraPreview() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            startCameraPreview()
        }
    }

    private fun startCameraPreview() {
        camera = Camera.open()
        camera?.let {
            val preview = CameraPreview(this, it)
            cameraPreview.removeAllViews()
            cameraPreview.addView(preview)
            cameraPreview.visibility = FrameLayout.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startCameraPreview()
            } else {
                // Handle permission denied
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop receiving location updates
        if (locationManager != null && locationListener != null) {
            locationManager!!.removeUpdates(locationListener!!)
        }
        // Release camera
        camera?.release()
        camera = null
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}
