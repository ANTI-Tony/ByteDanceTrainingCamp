# 字节跳动工程训练营 - Android客户端

## 项目简介

这是一个类似小红书的短视频应用，实现了双列瀑布流信息展示、视频播放、互动功能等核心特性。

## 主要功能

### ✅ 已实现功能

1. **双列瀑布流展示** - 首页使用StaggeredGridLayoutManager实现双列瀑布流布局
2. **顶部Tab切换** - 推荐/关注两个Tab页面，支持点击和滑动切换
3. **视频详情页** - 点击卡片进入全屏视频播放页面
4. **视频播放** - 使用ExoPlayer实现视频播放，支持暂停/播放控制
5. **互动功能** - 点赞、评论、分享、关注等核心交互
6. **评论系统** - 评论列表展示、发布新评论、点赞评论
7. **下拉加载更多** - 滚动到底部自动加载更多内容
8. **底部导航栏** - 5个Tab导航（首页、朋友、发布、消息、我的）

### 🚧 待优化功能

- 视频预加载优化
- 图片缓存策略
- 真实网络请求集成
- 用户登录系统
- 视频上传功能
- AI问答集成

## 技术架构

### 核心技术栈

- **语言**: Kotlin
- **最低SDK**: API 24 (Android 7.0)
- **目标SDK**: API 34 (Android 14)
- **架构模式**: MVVM (推荐使用)

### 主要依赖库

- **AndroidX Core**: 核心库
- **Material Design**: UI组件
- **ViewPager2**: 页面切换
- **RecyclerView**: 列表展示
- **Glide**: 图片加载
- **ExoPlayer**: 视频播放
- **Gson**: JSON解析
- **Coroutines**: 协程支持

## 项目结构

```
ByteDanceTrainingCamp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bytedance/trainingcamp/
│   │   │   │   ├── adapter/          # 适配器
│   │   │   │   │   ├── VideoCardAdapter.kt
│   │   │   │   │   ├── CommentAdapter.kt
│   │   │   │   │   └── ViewPagerAdapter.kt
│   │   │   │   ├── model/            # 数据模型
│   │   │   │   │   └── VideoItem.kt
│   │   │   │   ├── ui/               # UI组件
│   │   │   │   │   └── VideoListFragment.kt
│   │   │   │   ├── utils/            # 工具类
│   │   │   │   │   └── MockDataUtils.kt
│   │   │   │   ├── MainActivity.kt   # 主Activity
│   │   │   │   └── VideoDetailActivity.kt  # 视频详情
│   │   │   ├── res/
│   │   │   │   ├── layout/           # 布局文件
│   │   │   │   ├── drawable/         # 图片资源
│   │   │   │   ├── values/           # 配置文件
│   │   │   │   └── xml/              # XML配置
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 核心实现说明

### 1. 双列瀑布流

使用`StaggeredGridLayoutManager`实现，每个卡片高度根据图片比例自适应：

```kotlin
val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
recyclerView.layoutManager = layoutManager
```

### 2. 视频播放

使用ExoPlayer实现流畅的视频播放体验：

```kotlin
exoPlayer = ExoPlayer.Builder(this).build()
playerView.player = exoPlayer
val mediaItem = MediaItem.fromUri(videoUrl)
exoPlayer?.setMediaItem(mediaItem)
exoPlayer?.prepare()
exoPlayer?.playWhenReady = true
```

### 3. 评论系统

使用BottomSheetDialog实现评论弹窗，RecyclerView展示评论列表：

```kotlin
val bottomSheetDialog = BottomSheetDialog(this)
val view = layoutInflater.inflate(R.layout.bottom_sheet_comment, null)
bottomSheetDialog.setContentView(view)
```

### 4. 数据加载

目前使用MockDataUtils生成模拟数据，实际项目中应替换为网络请求：

```kotlin
val mockData = MockDataUtils.getMockVideoList(20)
adapter.updateData(mockData)
```

## 编译运行

### 环境要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 17
- Android SDK API 34
- Gradle 8.0+

### 编译步骤

1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 等待Gradle同步完成
4. 连接Android设备或启动模拟器
5. 点击运行按钮

## 性能优化建议

### 图片加载优化

```kotlin
Glide.with(context)
    .load(imageUrl)
    .placeholder(R.drawable.placeholder)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .into(imageView)
```

### 视频预加载

考虑使用ExoPlayer的预加载功能提升体验：

```kotlin
// 预加载下一个视频
val nextMediaItem = MediaItem.fromUri(nextVideoUrl)
exoPlayer?.addMediaItem(nextMediaItem)
```

### 内存管理

及时释放资源，避免内存泄漏：

```kotlin
override fun onDestroy() {
    super.onDestroy()
    exoPlayer?.release()
    exoPlayer = null
}
```

## 扩展功能建议

### 1. 网络层

使用Retrofit + OkHttp实现网络请求：

```kotlin
interface ApiService {
    @GET("videos/recommend")
    suspend fun getRecommendVideos(): Response<List<VideoItem>>
}
```

### 2. 数据持久化

使用Room数据库缓存数据：

```kotlin
@Entity
data class VideoEntity(
    @PrimaryKey val id: String,
    val videoUrl: String,
    val title: String
)
```

### 3. 视频缓存

集成视频缓存库，提升播放体验：

```kotlin
val cache = SimpleCache(cacheDir, LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024))
```

## 常见问题

### Q1: 视频播放卡顿？
A: 检查网络连接，考虑降低视频清晰度或使用CDN加速

### Q2: 图片加载慢？
A: 使用Glide的缓存策略，预加载可见区域图片

### Q3: 内存占用过高？
A: 及时释放不用的资源，使用内存分析工具定位问题

## 技术亮点

1. ✅ 使用Kotlin协程处理异步任务
2. ✅ StaggeredGridLayoutManager实现瀑布流
3. ✅ ExoPlayer专业级视频播放
4. ✅ Glide高效图片加载
5. ✅ Material Design设计风格
6. ✅ 流畅的交互动画

## 参考资料

- [Android官方文档](https://developer.android.com/)
- [ExoPlayer文档](https://exoplayer.dev/)
- [Glide文档](https://github.com/bumptech/glide)
- [Material Design指南](https://material.io/design)

## License

MIT License

## 联系方式

如有问题，欢迎提Issue或PR。
