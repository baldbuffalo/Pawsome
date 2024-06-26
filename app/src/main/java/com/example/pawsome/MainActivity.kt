package com.example.pawsome

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Example data
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
                        // Image contains a cat, add it to the feed
                        val newItem = FeedItem(imageUri, "Uploaded Cat", "Uploaded Cat Description")
                        feedAdapter.addItem(newItem)
                    } else {
                        // Image does not contain a cat, show an error message
                        Toast.makeText(this, "Only cat images are allowed.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Error handling
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

data class FeedItem(val imageUri: Uri, val catName: String, val catDescription: String)

class FeedAdapter(private val feedItems: MutableList<FeedItem>) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val catImageView: ImageView = itemView.findViewById(R.id.catImageView)
        val catNameTextView: TextView = itemView.findViewById(R.id.catNameTextView)
        val catDescriptionTextView: TextView = itemView.findViewById(R.id.catDescriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_feed, parent, false)
        return FeedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feedItem = feedItems[position]
        Glide.with(holder.catImageView.context)
            .load(feedItem.imageUri)
            .into(holder.catImageView)
        holder.catNameTextView.text = feedItem.catName
        holder.catDescriptionTextView.text = feedItem.catDescription
    }

    override fun getItemCount() = feedItems.size

    fun addItem(item: FeedItem) {
        feedItems.add(0, item) // Add new item at the beginning of the list
        notifyItemInserted(0) // Notify adapter about the new item
    }
}
