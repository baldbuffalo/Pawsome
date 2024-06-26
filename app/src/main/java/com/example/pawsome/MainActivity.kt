package com.example.pawsome

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var uploadButton: Button

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

        //recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val feedItems = mutableListOf<FeedItem>()
        feedAdapter = FeedAdapter(feedItems)
        recyclerView.adapter = feedAdapter

        uploadButton = findViewById(R.id.uploadButton)
        uploadButton.setOnClickListener {
            openGalleryForCatUpload()
        }
    }

    private fun openGalleryForCatUpload() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun handleImageUpload(imageUri: Uri) {
        try {
            val image = InputImage.fromFilePath(this, imageUri)
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

            labeler.process(image)
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
                        val newItem = FeedItem(imageUri, "Uploaded Cat", "Uploaded Cat Description")
                        feedAdapter.addItem(newItem)
                    } else {
                        Toast.makeText(this, "Only cat images are allowed.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
