package com.example.douyinclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.douyinclone.R
import com.example.douyinclone.data.model.VideoItem
import com.example.douyinclone.utils.FormatUtils

class FeedAdapter(
    private val onItemClick: (VideoItem, Int, View) -> Unit
) : ListAdapter<VideoItem, FeedAdapter.FeedViewHolder>(FeedDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_video, parent, false)
        return FeedViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
        holder.itemView.setOnClickListener {
            onItemClick(item, position, holder.getCoverView())
        }
    }
    
    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val ivAvatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tv_author)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tv_like_count)
        private val ivLike: ImageView = itemView.findViewById(R.id.iv_like)
        
        fun getCoverView(): View = ivCover
        
        fun bind(item: VideoItem, position: Int) {
            // 设置共享元素转场名称
            ivCover.transitionName = "cover_$position"
            // 加载封面图
            Glide.with(itemView.context)
                .load(item.coverUrl)
                .apply(RequestOptions().transform(RoundedCorners(16)))
                .placeholder(R.drawable.placeholder_cover)
                .into(ivCover)
            
            // 设置标题
            tvTitle.text = item.title
            
            // 加载头像
            Glide.with(itemView.context)
                .load(item.authorAvatar)
                .circleCrop()
                .placeholder(R.drawable.placeholder_avatar)
                .into(ivAvatar)
            
            // 设置作者名
            tvAuthor.text = item.authorName
            
            // 设置点赞数
            tvLikeCount.text = FormatUtils.formatCount(item.likeCount)
            
            // 设置点赞状态
            ivLike.setImageResource(
                if (item.isLiked) R.drawable.ic_like_filled else R.drawable.ic_like_outline
            )
        }
    }
    
    class FeedDiffCallback : DiffUtil.ItemCallback<VideoItem>() {
        override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
            return oldItem == newItem
        }
    }
}
