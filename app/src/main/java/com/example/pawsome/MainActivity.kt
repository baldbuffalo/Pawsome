package com.example.pawsome

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var cameraPreview: PreviewView
    private lateinit var progressBar: ProgressBar
    private lateinit var scanningLine: View
    private lateinit var uploadButton: Button
    private val cameraRequestCode = 101

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                handleImageUpload(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.ScanButton).setOnClickListener {
            checkCameraPermission()
        }

        findViewById<View>(R.id.BadgesButton).setOnClickListener {
            openBadgesActivity()
        }

        findViewById<View>(R.id.ProfileButton).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                cameraRequestCode
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        setContentView(R.layout.activity_camera) // Switch to the camera layout

        cameraPreview = findViewById(R.id.camera_preview)
        progressBar = findViewById(R.id.progressBar)
        scanningLine = findViewById(R.id.scanningLine)
        uploadButton = findViewById(R.id.uploadButton)

        // Show the progress bar when the camera is loading
        progressBar.visibility = View.VISIBLE

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            preview.setSurfaceProvider(cameraPreview.surfaceProvider)

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner, cameraSelector, preview
                )

                // Show the scanning line
                scanningLine.visibility = View.VISIBLE

                // Animate the scanning line
                animateScanningLine()

                // Show the upload button after the camera is loaded
                uploadButton.visibility = View.VISIBLE
                uploadButton.setOnClickListener {
                    openGalleryForCatUpload()
                }

                // Hide the progress bar after a delay
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000) // Adjust this delay as needed (e.g., 2000 ms = 2 seconds)
                    hideProgressBar()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun animateScanningLine() {
        cameraPreview.post {
            val animator = ObjectAnimator.ofFloat(
                scanningLine, "translationY",
                0f, cameraPreview.height.toFloat()
            )
            animator.duration = 2000 // 2 seconds
            animator.repeatMode = ObjectAnimator.REVERSE
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.start()
        }
    }

    private fun hideProgressBar() {
        // Hide the progress bar
        progressBar.visibility = View.GONE
    }

    private fun openBadgesActivity() {
        val intent = Intent(this, BadgesActivity::class.java)
        startActivity(intent)
    }

    private fun openGalleryForCatUpload() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun handleImageUpload(imageUri: Uri) {
        val image = FirebaseVisionImage.fromFilePath(this, imageUri)
        val labeler = FirebaseVision.getInstance().onDeviceImageLabeler

        labeler.processImage(image)
            .addOnSuccessListener { labels ->
                var hasCat = false
                for (label in labels) {
                    val text = label.text
                    if (text.equals("Cat", ignoreCase = true)) {
                        hasCat = true
                        break
                    }
                }
                if (hasCat) {
                    // Image contains a cat, open CatFormActivity
                    val intent = Intent(this, CatFormActivity::class.java)
                    startActivity(intent)
                } else {
                    // Image does not contain a cat, show an error message
                    Toast.makeText(this, "Only cat images are allowed.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Error handling
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
