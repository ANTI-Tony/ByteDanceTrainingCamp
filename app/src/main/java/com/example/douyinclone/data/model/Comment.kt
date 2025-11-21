package com.example.douyinclone.data.model

data class Comment(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val content: String,
    val likeCount: Int,
    val createTime: Long,
    val isLiked: Boolean = false
)
