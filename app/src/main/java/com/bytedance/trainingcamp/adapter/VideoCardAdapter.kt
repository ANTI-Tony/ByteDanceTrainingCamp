package com.bytedance.trainingcamp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytedance.trainingcamp.R
import com.bytedance.trainingcamp.model.VideoItem

class VideoCardAdapter(
    private val videoList: MutableList<VideoItem>,
    private val onItemClick: (VideoItem, Int) -> Unit
) : RecyclerView.Adapter<VideoCardAdapter.VideoCardViewHolder>() {

    inner class VideoCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)

        fun bind(videoItem: VideoItem, position: Int) {
            // 加载封面图
            Glide.with(itemView.context)
                .load(videoItem.coverUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(ivCover)

            // 加载头像
            Glide.with(itemView.context)
                .load(videoItem.author.avatarUrl)
                .circleCrop()
                .placeholder(R.drawable.placeholder_avatar)
                .into(ivAvatar)

            tvTitle.text = videoItem.title
            tvAuthor.text = videoItem.author.name
            tvLikeCount.text = formatCount(videoItem.likeCount)

            itemView.setOnClickListener {
                onItemClick(videoItem, position)
            }
        }

        private fun formatCount(count: Int): String {
            return when {
                count >= 10000 -> String.format("%.1fw", count / 10000.0)
                count >= 1000 -> String.format("%.1fk", count / 1000.0)
                else -> count.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_card, parent, false)
        return VideoCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoCardViewHolder, position: Int) {
        holder.bind(videoList[position], position)
    }

    override fun getItemCount(): Int = videoList.size

    fun updateData(newList: List<VideoItem>) {
        videoList.clear()
        videoList.addAll(newList)
        notifyDataSetChanged()
    }

    fun addData(newList: List<VideoItem>) {
        val startPosition = videoList.size
        videoList.addAll(newList)
        notifyItemRangeInserted(startPosition, newList.size)
    }
}
