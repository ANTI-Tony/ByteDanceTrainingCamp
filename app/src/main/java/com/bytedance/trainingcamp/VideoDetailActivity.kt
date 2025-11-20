package com.bytedance.trainingcamp

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytedance.trainingcamp.adapter.CommentAdapter
import com.bytedance.trainingcamp.model.Author
import com.bytedance.trainingcamp.model.Comment
import com.bytedance.trainingcamp.utils.MockDataUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class VideoDetailActivity : AppCompatActivity() {

    private lateinit var playerView: StyledPlayerView
    private var exoPlayer: ExoPlayer? = null

    private lateinit var btnBack: ImageView
    private lateinit var ivAuthorAvatar: ImageView
    private lateinit var btnFollow: ImageView
    private lateinit var btnLike: LinearLayout
    private lateinit var ivLike: ImageView
    private lateinit var tvLikeCount: TextView
    private lateinit var btnComment: LinearLayout
    private lateinit var tvCommentCount: TextView
    private lateinit var btnShare: LinearLayout
    private lateinit var tvAuthorName: TextView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView

    private var videoId: String = ""
    private var isLiked: Boolean = false
    private var isFollowing: Boolean = false
    private var likeCount: Int = 0
    private var commentCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_detail)

        initViews()
        loadIntentData()
        setupPlayer()
        setupListeners()
    }

    private fun initViews() {
        playerView = findViewById(R.id.playerView)
        btnBack = findViewById(R.id.btnBack)
        ivAuthorAvatar = findViewById(R.id.ivAuthorAvatar)
        btnFollow = findViewById(R.id.btnFollow)
        btnLike = findViewById(R.id.btnLike)
        ivLike = findViewById(R.id.ivLike)
        tvLikeCount = findViewById(R.id.tvLikeCount)
        btnComment = findViewById(R.id.btnComment)
        tvCommentCount = findViewById(R.id.tvCommentCount)
        btnShare = findViewById(R.id.btnShare)
        tvAuthorName = findViewById(R.id.tvAuthorName)
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
    }

    private fun loadIntentData() {
        videoId = intent.getStringExtra("video_id") ?: ""
        val videoUrl = intent.getStringExtra("video_url") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val authorName = intent.getStringExtra("author_name") ?: ""
        val authorAvatar = intent.getStringExtra("author_avatar") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        likeCount = intent.getIntExtra("like_count", 0)
        commentCount = intent.getIntExtra("comment_count", 0)
        isLiked = intent.getBooleanExtra("is_liked", false)
        isFollowing = intent.getBooleanExtra("is_following", false)

        // 设置视频信息
        tvTitle.text = title
        tvAuthorName.text = "@$authorName"
        tvDescription.text = description
        tvLikeCount.text = formatCount(likeCount)
        tvCommentCount.text = formatCount(commentCount)

        // 加载作者头像
        Glide.with(this)
            .load(authorAvatar)
            .circleCrop()
            .into(ivAuthorAvatar)

        // 更新点赞和关注状态
        updateLikeUI()
        updateFollowUI()

        // 设置播放器
        setupPlayerWithUrl(videoUrl)
    }

    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        playerView.player = exoPlayer
    }

    private fun setupPlayerWithUrl(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnFollow.setOnClickListener {
            isFollowing = !isFollowing
            updateFollowUI()
        }

        btnLike.setOnClickListener {
            isLiked = !isLiked
            if (isLiked) {
                likeCount++
            } else {
                likeCount--
            }
            updateLikeUI()
        }

        btnComment.setOnClickListener {
            showCommentBottomSheet()
        }

        btnShare.setOnClickListener {
            // 分享功能
        }
    }

    private fun updateLikeUI() {
        if (isLiked) {
            ivLike.setColorFilter(getColor(R.color.red))
        } else {
            ivLike.setColorFilter(getColor(R.color.white))
        }
        tvLikeCount.text = formatCount(likeCount)
    }

    private fun updateFollowUI() {
        if (isFollowing) {
            btnFollow.visibility = View.GONE
        } else {
            btnFollow.visibility = View.VISIBLE
        }
    }

    private fun showCommentBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_comment, null)
        bottomSheetDialog.setContentView(view)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewComments)
        val btnClose = view.findViewById<ImageView>(R.id.btnClose)
        val tvCommentCountDialog = view.findViewById<TextView>(R.id.tvCommentCount)
        val etComment = view.findViewById<android.widget.EditText>(R.id.etComment)
        val btnSendComment = view.findViewById<TextView>(R.id.btnSendComment)

        // 加载评论数据
        val comments = MockDataUtils.getMockComments(videoId, 20).toMutableList()
        tvCommentCountDialog.text = "${comments.size}条评论"

        val commentAdapter = CommentAdapter(comments) { comment, position ->
            // 点赞评论
            comment.isLiked = !comment.isLiked
            comment.likeCount = if (comment.isLiked) {
                comment.likeCount + 1
            } else {
                comment.likeCount - 1
            }
            // 通知adapter更新
            recyclerView.adapter?.notifyItemChanged(position)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = commentAdapter

        btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        btnSendComment.setOnClickListener {
            val commentText = etComment.text.toString()
            if (commentText.isNotEmpty()) {
                // 添加新评论
                val newComment = Comment(
                    id = "new_comment_${System.currentTimeMillis()}",
                    author = Author(
                        id = "current_user",
                        name = "当前用户",
                        avatarUrl = "https://picsum.photos/200/200?random=999"
                    ),
                    content = commentText,
                    likeCount = 0,
                    isLiked = false,
                    timestamp = System.currentTimeMillis()
                )
                commentAdapter.addComment(newComment)
                commentCount++
                tvCommentCount.text = formatCount(commentCount)
                tvCommentCountDialog.text = "${comments.size + 1}条评论"
                etComment.setText("")

                // 滚动到顶部
                recyclerView.scrollToPosition(0)
            }
        }

        bottomSheetDialog.show()
    }

    private fun formatCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1fw", count / 10000.0)
            count >= 1000 -> String.format("%.1fk", count / 1000.0)
            else -> count.toString()
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        exoPlayer = null
    }
}