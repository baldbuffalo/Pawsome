package com.example.pawsome

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    override fun onDestroy() {
        super.onDestroy()
        // Stop receiving location updates
        if (locationManager != null && locationListener != null) {
            locationManager!!.removeUpdates(locationListener!!)
        }
    }
}