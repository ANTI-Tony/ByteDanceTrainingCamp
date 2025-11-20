package com.bytedance.trainingcamp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytedance.trainingcamp.R
import com.bytedance.trainingcamp.model.Comment

class CommentAdapter(
    private val commentList: MutableList<Comment>,
    private val onLikeClick: (Comment, Int) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        val tvAuthorName: TextView = itemView.findViewById(R.id.tvAuthorName)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val btnLike: LinearLayout = itemView.findViewById(R.id.btnLike)
        val ivLike: ImageView = itemView.findViewById(R.id.ivLike)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)

        fun bind(comment: Comment, position: Int) {
            // 加载头像
            Glide.with(itemView.context)
                .load(comment.author.avatarUrl)
                .circleCrop()
                .placeholder(R.drawable.placeholder_avatar)
                .into(ivAvatar)

            tvAuthorName.text = comment.author.name
            tvContent.text = comment.content
            tvTime.text = formatTimestamp(comment.timestamp)
            tvLikeCount.text = comment.likeCount.toString()

            // 点赞状态
            if (comment.isLiked) {
                ivLike.setColorFilter(itemView.context.getColor(R.color.red))
            } else {
                ivLike.setColorFilter(itemView.context.getColor(R.color.gray))
            }

            btnLike.setOnClickListener {
                onLikeClick(comment, position)
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val diff = System.currentTimeMillis() - timestamp
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            return when {
                days > 0 -> "${days}天前"
                hours > 0 -> "${hours}小时前"
                minutes > 0 -> "${minutes}分钟前"
                else -> "刚刚"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position], position)
    }

    override fun getItemCount(): Int = commentList.size

    fun updateComment(position: Int, comment: Comment) {
        commentList[position] = comment
        notifyItemChanged(position)
    }

    fun addComment(comment: Comment) {
        commentList.add(0, comment)
        notifyItemInserted(0)
    }
}
