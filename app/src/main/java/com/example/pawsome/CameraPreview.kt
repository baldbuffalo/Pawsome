package com.example.pawsome

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.hardware.camera2.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import android.os.Handler
import android.os.HandlerThread

class CameraPreview(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), SurfaceHolder.Callback {
    private var cameraManager: CameraManager? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private lateinit var previewRequest: CaptureRequest
    private var surfaceHolder: SurfaceHolder = holder

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(context as AppCompatActivity, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // This method is required to implement the SurfaceHolder.Callback interface
        // However, in this implementation, it doesn't perform any additional actions
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        cameraDevice?.close()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // No additional setup required in this method
    }

    private fun openCamera() {
        try {
            val cameraId = cameraManager!!.cameraIdList[0]
            cameraManager!!.openCamera(cameraId, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
            cameraDevice = null
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val surface = surfaceHolder.surface
            previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(surface)

            // Use a HandlerThread for handling camera capture session callbacks
            val handlerThread = HandlerThread("CameraPreview")
            handlerThread.start()
            val handler = Handler(handlerThread.looper)

            cameraDevice!!.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    if (cameraDevice == null) return

                    cameraCaptureSession = session
                    try {
                        previewRequest = previewRequestBuilder.build()
                        cameraCaptureSession!!.setRepeatingRequest(previewRequest, null, handler)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    // Handle configuration failure
                }
            }, handler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    init {
        surfaceHolder.addCallback(this)
        surfaceHolder.setKeepScreenOn(true)
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        if (checkCameraPermission()) {
            openCamera()
        } else {
            requestCameraPermission()
        }
    }
}
