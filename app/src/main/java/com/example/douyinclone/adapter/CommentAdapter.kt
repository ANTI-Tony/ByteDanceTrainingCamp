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
import com.example.douyinclone.R
import com.example.douyinclone.data.model.Comment
import com.example.douyinclone.utils.FormatUtils

class CommentAdapter(
    private val onLikeClick: (Comment) -> Unit
) : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(CommentDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onLikeClick)
    }
    
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        private val tvUserName: TextView = itemView.findViewById(R.id.tv_user_name)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        private val ivLike: ImageView = itemView.findViewById(R.id.iv_like)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tv_like_count)
        
        fun bind(comment: Comment, onLikeClick: (Comment) -> Unit) {
            // 加载头像
            Glide.with(itemView.context)
                .load(comment.userAvatar)
                .circleCrop()
                .placeholder(R.drawable.placeholder_avatar)
                .into(ivAvatar)
            
            tvUserName.text = comment.userName
            tvContent.text = comment.content
            tvTime.text = FormatUtils.formatTime(comment.createTime)
            tvLikeCount.text = if (comment.likeCount > 0) {
                FormatUtils.formatCount(comment.likeCount)
            } else {
                ""
            }
            
            ivLike.setImageResource(
                if (comment.isLiked) R.drawable.ic_like_filled else R.drawable.ic_like_outline
            )
            
            ivLike.setOnClickListener {
                onLikeClick(comment)
            }
        }
    }
    
    class CommentDiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }
}
