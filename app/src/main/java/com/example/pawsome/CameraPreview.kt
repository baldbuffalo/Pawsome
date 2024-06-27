package com.example.pawsome

import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

class CameraPreview(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), SurfaceHolder.Callback {
    private var camera: Camera? = null
    private var holder: SurfaceHolder? = null

    init {
        holder?.addCallback(this)
    }

    constructor(context: Context) : this(context, null)

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
}
