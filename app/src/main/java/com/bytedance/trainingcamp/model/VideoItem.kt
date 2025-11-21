package com.bytedance.trainingcamp.model

data class VideoItem(
    val id: String,
    val videoUrl: String,
    val coverUrl: String,
    val title: String,
    val author: Author,
    var likeCount: Int,
    val commentCount: Int,
    var isLiked: Boolean = false,
    val description: String = ""
)

data class Author(
    val id: String,
    val name: String,
    val avatarUrl: String,
    var isFollowing: Boolean = false
)

data class Comment(
    val id: String,
    val author: Author,
    val content: String,
    var likeCount: Int,
    var isLiked: Boolean = false,
    val timestamp: Long
)