package com.example.douyinclone.ui.video

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.douyinclone.R
import com.example.douyinclone.adapter.CommentAdapter
import com.example.douyinclone.adapter.VideoDetailAdapter
import com.example.douyinclone.data.model.VideoItem
import com.example.douyinclone.databinding.FragmentVideoDetailBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoDetailFragment : Fragment() {
    
    private var _binding: FragmentVideoDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: VideoDetailViewModel by viewModels()
    
    private lateinit var videoAdapter: VideoDetailAdapter
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var commentBehavior: BottomSheetBehavior<View>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置共享元素转场动画
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        parseArguments()
        setupVideoPlayer()
        setupCommentPanel()
        setupBackButton()
        observeViewModel()
    }
    
    private fun parseArguments() {
        arguments?.let { args ->
            val videoListJson = args.getString("videoListJson")
            val startPosition = args.getInt("startPosition", 0)
            
            videoListJson?.let { json ->
                val type = object : TypeToken<List<VideoItem>>() {}.type
                val videoList: List<VideoItem> = Gson().fromJson(json, type)
                viewModel.setInitialVideos(videoList, startPosition)
            }
        }
    }
    
    private fun setupVideoPlayer() {
        videoAdapter = VideoDetailAdapter(
            onVideoClick = {
                viewModel.togglePlayPause()
            },
            onLikeClick = {
                viewModel.likeCurrentVideo()
            },
            onCommentClick = {
                viewModel.showCommentPanel()
            },
            onShareClick = { video ->
                Toast.makeText(context, "分享: ${video.title}", Toast.LENGTH_SHORT).show()
            },
            onDoubleTap = {
                viewModel.doubleTapLike()
                showLikeAnimation()
            }
        )
        
        binding.viewPager.apply {
            adapter = videoAdapter
            orientation = ViewPager2.ORIENTATION_VERTICAL
            offscreenPageLimit = 1
            
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.setCurrentPosition(position)
                    
                    // 预加载更多视频
                    val videoList = viewModel.videos.value.orEmpty()
                    if (position >= videoList.size - 3) {
                        viewModel.loadMoreVideos()
                    }
                }
            })
        }
        
        // 设置初始位置
        viewModel.currentPosition.value?.let { position ->
            binding.viewPager.setCurrentItem(position, false)
        }
    }
    
    private fun setupCommentPanel() {
        commentAdapter = CommentAdapter { comment ->
            // 点赞评论
            Toast.makeText(context, "点赞评论", Toast.LENGTH_SHORT).show()
        }
        
        binding.rvComments.adapter = commentAdapter
        
        // 设置BottomSheet
        commentBehavior = BottomSheetBehavior.from(binding.commentSheet)
        commentBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        commentBehavior.peekHeight = 0
        
        commentBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel.hideCommentPanel()
                    videoAdapter.resumeCurrentVideo()
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    videoAdapter.pauseCurrentVideo()
                }
            }
            
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 调整背景透明度
                binding.viewCommentOverlay.alpha = (slideOffset + 1) / 2
            }
        })
        
        // 点击遮罩关闭评论面板
        binding.viewCommentOverlay.setOnClickListener {
            hideCommentSheet()
        }
        
        // 关闭按钮
        binding.ivCloseComment.setOnClickListener {
            hideCommentSheet()
        }
        
        // 发送评论
        binding.etComment.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val content = binding.etComment.text.toString().trim()
                if (content.isNotEmpty()) {
                    viewModel.addComment(content)
                    binding.etComment.text.clear()
                }
                true
            } else {
                false
            }
        }
        
        binding.tvSendComment.setOnClickListener {
            val content = binding.etComment.text.toString().trim()
            if (content.isNotEmpty()) {
                viewModel.addComment(content)
                binding.etComment.text.clear()
            }
        }
    }
    
    private fun setupBackButton() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun observeViewModel() {
        viewModel.videos.observe(viewLifecycleOwner) { videos ->
            videoAdapter.submitList(videos)
        }
        
        viewModel.currentPosition.observe(viewLifecycleOwner) { position ->
            if (binding.viewPager.currentItem != position) {
                binding.viewPager.setCurrentItem(position, false)
            }
        }
        
        viewModel.showComments.observe(viewLifecycleOwner) { show ->
            if (show) {
                showCommentSheet()
            } else {
                hideCommentSheet()
            }
        }
        
        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            commentAdapter.submitList(comments)
            binding.tvCommentCount.text = "${comments.size}条评论"
        }
        
        viewModel.currentVideo.observe(viewLifecycleOwner) { video ->
            // 更新当前视频信息（如果需要在UI上显示）
        }
    }
    
    private fun showCommentSheet() {
        binding.viewCommentOverlay.visibility = View.VISIBLE
        commentBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
    
    private fun hideCommentSheet() {
        commentBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.viewCommentOverlay.visibility = View.GONE
    }
    
    private fun showLikeAnimation() {
        binding.ivLikeAnimation.apply {
            visibility = View.VISIBLE
            alpha = 1f
            scaleX = 0.3f
            scaleY = 0.3f
            
            animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(200)
                .withEndAction {
                    animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction {
                            visibility = View.GONE
                        }
                        .start()
                }
                .start()
        }
    }
    
    override fun onPause() {
        super.onPause()
        videoAdapter.pauseCurrentVideo()
    }
    
    override fun onResume() {
        super.onResume()
        videoAdapter.resumeCurrentVideo()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
