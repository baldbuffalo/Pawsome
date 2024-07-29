package com.example.pawsome

/*import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class CatPost(
    val id: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val timestamp: Long = 0
)

class FYPAdapter(private val catPostList: List<CatPost>) : RecyclerView.Adapter<FYPAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val descriptionView: TextView = itemView.findViewById(R.id.descriptionView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cat_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val catPost = catPostList[position]
        Glide.with(holder.itemView.context).load(catPost.imageUrl).into(holder.imageView)
        holder.descriptionView.text = catPost.description
    }

    override fun getItemCount(): Int {
        return catPostList.size
    }
}
*/