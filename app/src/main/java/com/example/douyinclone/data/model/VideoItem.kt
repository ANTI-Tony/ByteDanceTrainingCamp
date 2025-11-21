package com.example.douyinclone.data.model

data class VideoItem(
    val id: String,
    val coverUrl: String,
    val videoUrl: String,
    val title: String,
    val authorName: String,
    val authorAvatar: String,
    val likeCount: Int,
    val commentCount: Int,
    val shareCount: Int,
    val isLiked: Boolean = false,
    val description: String = "",
    val musicName: String = "",
    val musicAuthor: String = ""
)
