package com.example.douyinclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.douyinclone.R
import com.example.douyinclone.data.model.VideoItem
import com.example.douyinclone.utils.FormatUtils

class VideoDetailAdapter(
    private val onVideoClick: () -> Unit,
    private val onLikeClick: (VideoItem) -> Unit,
    private val onCommentClick: (VideoItem) -> Unit,
    private val onShareClick: (VideoItem) -> Unit,
    private val onDoubleTap: (VideoItem) -> Unit
) : ListAdapter<VideoItem, VideoDetailAdapter.VideoViewHolder>(VideoDiffCallback()) {
    
    private var currentPlayingHolder: VideoViewHolder? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_detail, parent, false)
        return VideoViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onVideoClick, onLikeClick, onCommentClick, onShareClick, onDoubleTap)
    }
    
    override fun onViewAttachedToWindow(holder: VideoViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.startPlay()
        currentPlayingHolder = holder
    }
    
    override fun onViewDetachedFromWindow(holder: VideoViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.stopPlay()
        if (currentPlayingHolder == holder) {
            currentPlayingHolder = null
        }
    }
    
    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        holder.releasePlayer()
    }
    
    fun pauseCurrentVideo() {
        currentPlayingHolder?.pausePlay()
    }
    
    fun resumeCurrentVideo() {
        currentPlayingHolder?.resumePlay()
    }
    
    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerView: PlayerView = itemView.findViewById(R.id.player_view)
        private val ivPause: ImageView = itemView.findViewById(R.id.iv_pause)
        private val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        private val ivAvatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tv_author)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        private val tvMusic: TextView = itemView.findViewById(R.id.tv_music)
        private val ivLike: ImageView = itemView.findViewById(R.id.iv_like)
        private val tvLikeCount: TextView = itemView.findViewById(R.id.tv_like_count)
        private val ivComment: ImageView = itemView.findViewById(R.id.iv_comment)
        private val tvCommentCount: TextView = itemView.findViewById(R.id.tv_comment_count)
        private val ivShare: ImageView = itemView.findViewById(R.id.iv_share)
        private val tvShareCount: TextView = itemView.findViewById(R.id.tv_share_count)
        private val ivMusicDisc: ImageView = itemView.findViewById(R.id.iv_music_disc)
        
        private var player: ExoPlayer? = null
        private var currentItem: VideoItem? = null
        private var isPaused = false
        private var lastClickTime = 0L
        private var discAnimation: Animation? = null
        
        fun bind(
            item: VideoItem,
            onVideoClick: () -> Unit,
            onLikeClick: (VideoItem) -> Unit,
            onCommentClick: (VideoItem) -> Unit,
            onShareClick: (VideoItem) -> Unit,
            onDoubleTap: (VideoItem) -> Unit
        ) {
            currentItem = item
            
            // 加载封面
            Glide.with(itemView.context)
                .load(item.coverUrl)
                .into(ivCover)
            
            // 加载头像
            Glide.with(itemView.context)
                .load(item.authorAvatar)
                .circleCrop()
                .into(ivAvatar)
            
            // 设置文本
            tvAuthor.text = "@${item.authorName}"
            tvDescription.text = item.description
            tvMusic.text = "${item.musicName} - ${item.musicAuthor}"
            tvLikeCount.text = FormatUtils.formatCount(item.likeCount)
            tvCommentCount.text = FormatUtils.formatCount(item.commentCount)
            tvShareCount.text = FormatUtils.formatCount(item.shareCount)
            
            // 设置点赞状态
            ivLike.setImageResource(
                if (item.isLiked) R.drawable.ic_like_filled else R.drawable.ic_like_white
            )
            
            // 加载音乐碟片
            Glide.with(itemView.context)
                .load(item.authorAvatar)
                .circleCrop()
                .into(ivMusicDisc)
            
            // 点击事件 - 单击暂停/播放，双击点赞
            playerView.setOnClickListener {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < 300) {
                    // 双击点赞
                    onDoubleTap(item)
                    lastClickTime = 0
                } else {
                    // 单击暂停/播放
                    lastClickTime = currentTime
                    playerView.postDelayed({
                        if (System.currentTimeMillis() - lastClickTime >= 300) {
                            togglePlayPause()
                            onVideoClick()
                        }
                    }, 300)
                }
            }
            
            // 右侧按钮点击事件
            ivLike.setOnClickListener { onLikeClick(item) }
            ivComment.setOnClickListener { onCommentClick(item) }
            ivShare.setOnClickListener { onShareClick(item) }
        }
        
        private fun togglePlayPause() {
            player?.let {
                if (it.isPlaying) {
                    it.pause()
                    isPaused = true
                    ivPause.visibility = View.VISIBLE
                    stopDiscAnimation()
                } else {
                    it.play()
                    isPaused = false
                    ivPause.visibility = View.GONE
                    startDiscAnimation()
                }
            }
        }
        
        fun startPlay() {
            currentItem?.let { item ->
                if (player == null) {
                    player = ExoPlayer.Builder(itemView.context).build().apply {
                        playerView.player = this
                        repeatMode = Player.REPEAT_MODE_ONE
                        
                        addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                when (playbackState) {
                                    Player.STATE_READY -> {
                                        ivCover.visibility = View.GONE
                                    }
                                    Player.STATE_BUFFERING -> {
                                        ivCover.visibility = View.VISIBLE
                                    }
                                }
                            }
                        })
                    }
                }
                
                val mediaItem = MediaItem.fromUri(item.videoUrl)
                player?.setMediaItem(mediaItem)
                player?.prepare()
                player?.playWhenReady = true
                ivPause.visibility = View.GONE
                isPaused = false
                
                // 启动音乐转盘旋转动画
                startDiscAnimation()
            }
        }
        
        private fun startDiscAnimation() {
            if (discAnimation == null) {
                discAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.rotate_disc)
            }
            ivMusicDisc.startAnimation(discAnimation)
        }
        
        private fun stopDiscAnimation() {
            ivMusicDisc.clearAnimation()
        }
        
        fun stopPlay() {
            player?.stop()
            ivCover.visibility = View.VISIBLE
            stopDiscAnimation()
        }
        
        fun pausePlay() {
            player?.pause()
            ivPause.visibility = View.VISIBLE
            stopDiscAnimation()
        }
        
        fun resumePlay() {
            if (!isPaused) {
                player?.play()
                ivPause.visibility = View.GONE
                startDiscAnimation()
            }
        }
        
        fun releasePlayer() {
            player?.release()
            player = null
        }
    }
    
    class VideoDiffCallback : DiffUtil.ItemCallback<VideoItem>() {
        override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
            return oldItem == newItem
        }
    }
}
