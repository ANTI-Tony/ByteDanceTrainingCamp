# 抖音克隆项目 - 技术设计文档

## 项目概述

本项目是一个仿抖音短视频应用的Android客户端Demo，使用Kotlin语言开发，采用MVVM架构模式，实现了双列瀑布流推荐页面、全屏视频播放、评论系统、AI悬浮球等核心功能。

## 技术栈

| 技术 | 版本 | 用途 |
|-----|------|------|
| Kotlin | 1.9.22 | 开发语言 |
| Android Gradle Plugin | 8.4.0 | 构建工具 |
| Gradle | 8.6 | 依赖管理 |
| Hilt | 2.50 | 依赖注入 |
| Navigation | 2.7.6 | 页面导航 |
| ExoPlayer (Media3) | 1.2.0 | 视频播放 |
| Glide | 4.16.0 | 图片加载 |
| ViewPager2 | 1.0.0 | 页面滑动 |
| Material Design 3 | 1.11.0 | UI组件 |

## 项目结构

```
app/src/main/
├── java/com/example/douyinclone/
│   ├── adapter/                    # 适配器
│   │   ├── AiChatAdapter.kt        # AI聊天消息适配器
│   │   ├── CommentAdapter.kt       # 评论列表适配器
│   │   ├── FeedAdapter.kt          # 双列瀑布流适配器
│   │   ├── FeedPagerAdapter.kt     # ViewPager2适配器
│   │   ├── TopTabAdapter.kt        # 顶部Tab适配器
│   │   └── VideoDetailAdapter.kt   # 视频播放适配器
│   │
│   ├── data/                       # 数据层
│   │   ├── model/                  # 数据模型
│   │   │   ├── ChatMessage.kt      # 聊天消息
│   │   │   ├── Comment.kt          # 评论
│   │   │   ├── TabItem.kt          # Tab项
│   │   │   └── VideoItem.kt        # 视频项
│   │   │
│   │   └── repository/             # 数据仓库
│   │       └── VideoRepository.kt  # 视频数据仓库
│   │
│   ├── ui/                         # UI层
│   │   ├── ai/                     # AI功能
│   │   │   ├── AiChatDialogFragment.kt    # AI聊天对话框
│   │   │   └── DraggableFloatingView.kt   # 可拖动悬浮球
│   │   │
│   │   ├── feed/                   # 双列外流
│   │   │   ├── FeedFragment.kt     # 主页面Fragment
│   │   │   ├── FeedContentFragment.kt # 内容Fragment
│   │   │   └── FeedViewModel.kt    # ViewModel
│   │   │
│   │   └── video/                  # 视频内流
│   │       ├── VideoDetailFragment.kt    # 视频详情Fragment
│   │       └── VideoDetailViewModel.kt   # ViewModel
│   │
│   ├── utils/                      # 工具类
│   │   └── FormatUtils.kt          # 格式化工具
│   │
│   ├── DouyinApplication.kt        # Application类
│   └── MainActivity.kt             # 主Activity
│
└── res/
    ├── anim/                       # 动画资源
    ├── drawable/                   # 图形资源
    ├── layout/                     # 布局文件
    ├── mipmap-*/                   # 应用图标
    ├── navigation/                 # 导航图
    └── values/                     # 值资源
```

---

## 功能模块详解

### 1. 双列外流页面 (FeedFragment)

#### 1.1 功能描述
- 双列瀑布流布局展示视频封面
- 顶部TabLayout支持滑动切换（购、经验、同城、关注、商城、推荐）
- 底部导航栏（首页、朋友、+、消息、我）
- 下拉刷新、上拉加载更多
- 点击视频封面进入全屏播放页面

#### 1.2 技术实现

**瀑布流布局**
```kotlin
val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
binding.rvFeed.layoutManager = layoutManager
```

**TabLayout + ViewPager2 联动**
```kotlin
TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
    tab.text = tabTitles[position]
}.attach()
```

**下拉刷新**
```kotlin
binding.swipeRefresh.setOnRefreshListener {
    viewModel.refreshVideos()
}
```

**上拉加载更多**
```kotlin
addOnScrollListener(object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (visibleItemCount + firstVisibleItem >= totalItemCount - 4) {
            viewModel.loadMoreVideos()
        }
    }
})
```

#### 1.3 视频卡片布局
```xml
<MaterialCardView>
    <LinearLayout>
        <ImageView id="iv_cover" />      <!-- 封面图 -->
        <TextView id="tv_title" />        <!-- 标题 -->
        <LinearLayout>                    <!-- 作者信息 -->
            <ImageView id="iv_avatar" />  <!-- 头像 -->
            <TextView id="tv_author" />   <!-- 作者名 -->
            <ImageView id="iv_like" />    <!-- 点赞图标 -->
            <TextView id="tv_like_count" /> <!-- 点赞数 -->
        </LinearLayout>
    </LinearLayout>
</MaterialCardView>
```

---

### 2. 视频内流页面 (VideoDetailFragment)

#### 2.1 功能描述
- 全屏垂直滑动切换视频（ViewPager2）
- ExoPlayer视频播放
- 单击暂停/播放，双击点赞
- 右侧操作栏：头像、点赞、评论、分享、音乐转盘
- 底部信息区：作者名、描述、音乐信息
- 音乐转盘旋转动画

#### 2.2 技术实现

**垂直滑动切换**
```kotlin
binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
binding.viewPager.offscreenPageLimit = 1
```

**ExoPlayer视频播放**
```kotlin
player = ExoPlayer.Builder(context).build().apply {
    playerView.player = this
    repeatMode = Player.REPEAT_MODE_ONE  // 循环播放
    
    addListener(object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> ivCover.visibility = View.GONE
                Player.STATE_BUFFERING -> ivCover.visibility = View.VISIBLE
            }
        }
    })
}

val mediaItem = MediaItem.fromUri(videoUrl)
player?.setMediaItem(mediaItem)
player?.prepare()
player?.playWhenReady = true
```

**双击点赞检测**
```kotlin
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
            }
        }, 300)
    }
}
```

**音乐转盘旋转动画**
```xml
<!-- rotate_disc.xml -->
<rotate
    android:duration="8000"
    android:fromDegrees="0"
    android:toDegrees="360"
    android:repeatCount="infinite"
    android:interpolator="@android:anim/linear_interpolator" />
```

```kotlin
private fun startDiscAnimation() {
    discAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_disc)
    ivMusicDisc.startAnimation(discAnimation)
}
```

#### 2.3 视频生命周期管理
```kotlin
override fun onViewAttachedToWindow(holder: VideoViewHolder) {
    holder.startPlay()  // 开始播放
}

override fun onViewDetachedFromWindow(holder: VideoViewHolder) {
    holder.stopPlay()   // 停止播放
}

override fun onViewRecycled(holder: VideoViewHolder) {
    holder.releasePlayer()  // 释放资源
}
```

---

### 3. 评论系统

#### 3.1 功能描述
- BottomSheet弹出式评论面板
- 评论列表展示
- 发布新评论并置顶显示
- 评论点赞

#### 3.2 技术实现

**BottomSheet配置**
```kotlin
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
})
```

**发布评论**
```kotlin
fun addComment(content: String) {
    val newComment = Comment(
        id = "comment_${System.currentTimeMillis()}",
        content = content,
        ...
    )
    mockComments.add(0, newComment)  // 添加到列表头部
}
```

---

### 4. AI悬浮球功能

#### 4.1 功能描述
- 可拖动的悬浮球UI
- 自动吸附屏幕边缘
- 点击打开AI聊天对话框
- 简单的AI问答交互

#### 4.2 技术实现

**可拖动悬浮球**
```kotlin
class DraggableFloatingView : FrameLayout {
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.rawX - lastX
                val deltaY = event.rawY - lastY
                
                // 边界检查
                x = (x + deltaX).coerceIn(0f, maxX)
                y = (y + deltaY).coerceIn(0f, maxY)
            }
            MotionEvent.ACTION_UP -> {
                if (!isDragging) {
                    onClickListener?.invoke()
                } else {
                    snapToEdge()  // 吸附边缘
                }
            }
        }
    }
    
    private fun snapToEdge() {
        val targetX = if (centerX < parentWidth / 2) 16f else (parentWidth - width - 16)
        animate().x(targetX).setDuration(200).start()
    }
}
```

**AI聊天对话框**
```kotlin
class AiChatDialogFragment : DialogFragment() {
    
    private fun generateAiResponse(userMessage: String) {
        val response = when {
            userMessage.contains("你好") -> "你好！很高兴见到你！"
            userMessage.contains("视频") -> "这个应用是一个短视频平台..."
            else -> "收到你的消息了！这是一个演示版本的AI助手。"
        }
        addMessage(ChatMessage(content = response, isFromUser = false))
    }
}
```

---

### 5. 页面转场动画

#### 5.1 共享元素转场
```kotlin
// FeedFragment - 发起转场
val extras = FragmentNavigatorExtras(
    sharedView to "video_cover_transition"
)
findNavController().navigate(R.id.action_feed_to_video_detail, bundle, null, extras)

// VideoDetailFragment - 接收转场
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    sharedElementEnterTransition = TransitionInflater.from(context)
        .inflateTransition(android.R.transition.move)
}
```

#### 5.2 页面切换动画
```xml
<!-- nav_graph.xml -->
<action
    android:id="@+id/action_feed_to_video_detail"
    app:destination="@id/videoDetailFragment"
    app:enterAnim="@anim/slide_in_bottom"
    app:exitAnim="@anim/fade_out"
    app:popEnterAnim="@anim/fade_in"
    app:popExitAnim="@anim/slide_out_bottom" />
```

---

## 架构设计

### MVVM架构

```
┌─────────────────────────────────────────────────────────┐
│                        View Layer                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │ FeedFragment│  │VideoDetail  │  │ AiChat      │     │
│  │             │  │Fragment     │  │ Dialog      │     │
│  └──────┬──────┘  └──────┬──────┘  └─────────────┘     │
│         │                │                              │
│         ▼                ▼                              │
│  ┌─────────────┐  ┌─────────────┐                      │
│  │FeedViewModel│  │VideoDetail  │                      │
│  │             │  │ViewModel   │                      │
│  └──────┬──────┘  └──────┬──────┘                      │
│         │                │                              │
└─────────┼────────────────┼──────────────────────────────┘
          │                │
          ▼                ▼
┌─────────────────────────────────────────────────────────┐
│                     Data Layer                           │
│  ┌─────────────────────────────────────────────────┐   │
│  │              VideoRepository                     │   │
│  │  ┌─────────────┐  ┌─────────────┐              │   │
│  │  │ Mock Videos │  │Mock Comments│              │   │
│  │  └─────────────┘  └─────────────┘              │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### 依赖注入 (Hilt)

```kotlin
@HiltAndroidApp
class DouyinApplication : Application()

@AndroidEntryPoint
class MainActivity : AppCompatActivity()

@AndroidEntryPoint
class FeedFragment : Fragment()

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: VideoRepository
) : ViewModel()

@Singleton
class VideoRepository @Inject constructor()
```

---

## 数据模型

### VideoItem
```kotlin
data class VideoItem(
    val id: String,
    val coverUrl: String,      // 封面图URL
    val videoUrl: String,      // 视频URL
    val title: String,         // 标题
    val authorName: String,    // 作者名
    val authorAvatar: String,  // 作者头像
    val likeCount: Int,        // 点赞数
    val commentCount: Int,     // 评论数
    val shareCount: Int,       // 分享数
    val isLiked: Boolean,      // 是否已点赞
    val description: String,   // 描述
    val musicName: String,     // 音乐名
    val musicAuthor: String    // 音乐作者
)
```

### Comment
```kotlin
data class Comment(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val content: String,
    val likeCount: Int,
    val createTime: Long,
    val isLiked: Boolean
)
```

### ChatMessage
```kotlin
data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long
)
```

---

## 功能实现状态

| 功能模块 | 功能点 | 难度 | 状态 |
|---------|--------|------|------|
| **技术设计文档** | 目标、技术选型、类图、功能实现思路和难点 | 中等 | ✅ |
| **Demo基本要求** | 整体运行流畅，无崩溃卡死，界面UI和交互风格尽可能统一 | 中等 | ✅ |
| **架构** | 推荐MVVM | 中等 | ✅ |
| **数据** | 抓取和mock一定的图片、文本和视频 | 较容易 | ✅ |
| **双列外流** | 双列外流的UI布局（包括封面、昵称、点赞数和标题） | 较容易 | ✅ |
| **双列外流** | 点击封面进入视频内流 | 较容易 | ✅ |
| **双列外流** | 进入视频内流有画面放大的转场动画 | 中等 | ✅ |
| **双列外流** | 每个卡片页面高度自适应布局 | 中等 | ✅ |
| **双列外流** | 下拉刷新、上拉加载更新数据 | 中等 | ✅ |
| **双列外流** | 顶部和底部bar实现，支持切换 | 中等 | ✅ |
| **双列外流** | 在双列外流左右滑动，跳转不同的顶部bar | 较难 | ✅ |
| **视频内流** | 内流页面布局 | 较容易 | ✅ |
| **视频内流** | 点击暂停、播放（暂停时有暂停图标） | 较容易 | ✅ |
| **视频内流** | 手指上下移动、切换 | 中等 | ✅ |
| **视频内流** | 双击点赞动画 | 中等 | ✅ |
| **视频内流** | 音乐转盘和动画 | 中等 | ✅ |
| **视频内流** | 下拉刷新、上拉加载更新数据 | 中等 | ✅ |
| **视频内流** | 支持头像更换 | 较难 | ❌ |
| **评论页面** | 评论UI布局 | 较容易 | ✅ |
| **评论页面** | 评论页面高度自适应布局 | 中等 | ✅ |
| **评论页面** | 支持发布新评论并展示最顶部 | 中等 | ✅ |
| **AI问答** | 制作一个悬浮球UI，可在页面拖动 | 较难 | ✅ |
| **AI问答** | 点击悬浮球打开聊天页面，和与AI对话 | 较难 | ✅ |

---

## 编译运行

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.6

### 编译步骤
1. 解压项目文件
2. 用Android Studio打开项目
3. 等待Gradle同步完成
4. 连接设备或启动模拟器
5. 点击Run运行

### 权限要求
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

---

## 待优化项

1. **视频预加载** - 提前加载下一个视频，减少切换时的加载等待
2. **视频缓存** - 使用ExoPlayer的缓存功能，避免重复下载
3. **图片缓存优化** - Glide缓存策略优化
4. **内存优化** - ViewHolder复用时及时释放ExoPlayer资源
5. **头像更换功能** - 集成相机/相册，图片裁剪
6. **真实AI对接** - 集成OpenAI或其他AI框架

---

## 作者

ByteDance Android工程训练营项目

## 许可证

仅供学习使用