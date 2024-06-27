package com.example.pawsome

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity

import java.io.IOException

class CameraPreview : AppCompatActivity(), SurfaceHolder.Callback {
    private var camera: Camera? = null
    private var holder: SurfaceHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera) // Assumes you have activity_camera.xml for layout

        holder = findViewById<SurfaceView>(R.id.camera_Preview).holder
        holder?.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        this.holder = holder // Initialize the holder
        try {
            camera?.let {
                it.setPreviewDisplay(holder)
                it.startPreview()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Handle surface changes if needed
    }

    fun setCamera(camera: Camera) {
        this.camera = camera
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CameraPreview::class.java)
        }
    }
}
