package com.example.pawsome

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.pawsome.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var camera: Camera
    private lateinit var surfaceView: SurfaceView
    private lateinit var surfaceHolder: SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        surfaceView = findViewById(R.id.camera_preview)
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)

        binding.ScanButton.setOnClickListener {
            checkCameraPermission()
        }

        binding.ProfileButton.setOnClickListener {
            // Handle ProfileButton click
        }

        binding.ListButton.setOnClickListener {
            // Handle ListButton click
        }
    }

    private fun checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startCameraPreview()
        }
    }

    private fun startCameraPreview() {
        try {
            camera = Camera.open()
            camera.setDisplayOrientation(90) // Adjust orientation if necessary
            camera.setPreviewDisplay(surfaceHolder)
            camera.startPreview()
            surfaceView.visibility = SurfaceView.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to start camera preview.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraPreview()
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // Surface created, start the preview
        startCameraPreview()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Surface changed, restart the preview
        if (surfaceHolder.surface == null) {
            return
        }
        camera.stopPreview()
        startCameraPreview()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Surface destroyed, stop the preview
        camera.stopPreview()
        camera.release()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}
