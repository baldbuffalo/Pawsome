package com.example.pawsome

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FeedAdapter(private val feedItems: MutableList<FeedItem>) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    // ViewHolder for each item in the RecyclerView
    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val catImageView: ImageView = itemView.findViewById(R.id.catImageView)
        val catNameTextView: TextView = itemView.findViewById(R.id.catNameTextView)
        val catDescriptionTextView: TextView = itemView.findViewById(R.id.catDescriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        // Inflate the item layout and create ViewHolder instance
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_feed, parent, false)
        return FeedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        // Bind data to the ViewHolder
        val feedItem = feedItems[position]
        Glide.with(holder.catImageView.context)
            .load(feedItem.imageUri)
            .into(holder.catImageView)
        holder.catNameTextView.text = feedItem.catName
        holder.catDescriptionTextView.text = feedItem.catDescription
    }

    override fun getItemCount() = feedItems.size

    fun addItem(item: FeedItem) {
        // Add new item at the beginning of the list
        feedItems.add(0, item)
        // Notify adapter about the new item
        notifyItemInserted(0)
    }
}
