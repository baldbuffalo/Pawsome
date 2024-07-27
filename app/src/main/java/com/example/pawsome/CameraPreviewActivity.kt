package com.example.pawsome

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraPreviewActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private lateinit var loadingSpinner: View
    private lateinit var scanningLine: View
    private lateinit var imageCapture: ImageCapture
    private var isCatDetected = false

    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        previewView = findViewById(R.id.camera_Preview)
        loadingSpinner = findViewById(R.id.progressBar)
        scanningLine = findViewById(R.id.scanningLine)

        // Show loading spinner
        loadingSpinner.visibility = View.VISIBLE

        // Initialize CameraX
        startCamera()

        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    @ExperimentalGetImage
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

                // Hide loading spinner and show scanning line animation
                loadingSpinner.visibility = View.GONE
                startScanningLineAnimation()
            } catch (exc: Exception) {
                // Log or handle exceptions
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startScanningLineAnimation() {
        val animation = TranslateAnimation(
            TranslateAnimation.ABSOLUTE, 0f,
            TranslateAnimation.ABSOLUTE, 0f,
            TranslateAnimation.RELATIVE_TO_PARENT, -0.5f,
            TranslateAnimation.RELATIVE_TO_PARENT, 0.5f
        )
        animation.duration = 1000
        animation.repeatMode = TranslateAnimation.REVERSE
        animation.repeatCount = TranslateAnimation.INFINITE
        scanningLine.visibility = View.VISIBLE
        scanningLine.startAnimation(animation)
    }

    @ExperimentalGetImage
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val labeler = ImageLabeling.getClient(
            ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build()
        )

        labeler.process(image)
            .addOnSuccessListener { labels ->
                for (label in labels) {
                    if (label.text.equals("Cat", ignoreCase = true)) {
                        if (!isCatDetected) {
                            isCatDetected = true
                            takePicture()
                        }
                        break
                    }
                }
                imageProxy.close()
            }
            .addOnFailureListener {
                imageProxy.close()
            }
    }

    private fun takePicture() {
        val photoFile = File(getOutputDirectory(), "${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    // Handle error
                    isCatDetected = false
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    isCatDetected = false
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                    val intent = CatFormActivity.newIntent(this@CameraPreviewActivity, savedUri.toString())
                    startActivity(intent)
                }
            })
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        fun newIntent(context: Context, imageUri: String): Intent {
            val intent = Intent(context, CameraPreviewActivity::class.java)
            intent.putExtra("imageUri", imageUri)
            return intent
        }
    }
}
