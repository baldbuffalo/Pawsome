package com.example.pawsome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pawsome.databinding.ActivityFeedBinding
import com.example.pawsome.databinding.ItemFeedBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var adapter: FeedAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FeedAdapter()
        binding.recyclerView.adapter = adapter

        fetchFeed()
    }

    private fun fetchFeed() {
        db.collection("uploads")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val feedItems = documents.map { document ->
                    FeedItem(
                        document.getString("imageUrl") ?: "",
                        document.getString("username") ?: "",
                        document.getLong("timestamp") ?: 0L
                    )
                }
                adapter.submitList(feedItems)
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }
}

data class FeedItem(
    val imageUrl: String,
    val username: String,
    val timestamp: Long
)

class FeedAdapter : androidx.recyclerview.widget.ListAdapter<FeedItem, FeedAdapter.FeedViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FeedViewHolder(private val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feedItem: FeedItem) {
            binding.usernameTextView.text = feedItem.username
            Glide.with(binding.root.context)
                .load(feedItem.imageUrl)
                .into(binding.catImageView)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FeedItem>() {
        override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return oldItem == newItem
        }
    }
}
