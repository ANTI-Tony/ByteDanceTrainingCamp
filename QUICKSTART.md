# 字节跳动训练营 - 快速开始

## 项目结构 ✅

```
ByteDanceTrainingCamp/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/bytedance/trainingcamp/
│   │       │   ├── adapter/              # 适配器
│   │       │   │   ├── CommentAdapter.kt
│   │       │   │   ├── VideoCardAdapter.kt
│   │       │   │   └── ViewPagerAdapter.kt
│   │       │   ├── model/                # 数据模型
│   │       │   │   └── VideoItem.kt
│   │       │   ├── ui/                   # UI组件
│   │       │   │   └── VideoListFragment.kt
│   │       │   ├── utils/                # 工具类
│   │       │   │   └── MockDataUtils.kt
│   │       │   ├── MainActivity.kt       # 主Activity
│   │       │   └── VideoDetailActivity.kt # 视频详情
│   │       ├── res/
│   │       │   ├── layout/              # 布局文件
│   │       │   ├── drawable/            # 图片资源
│   │       │   ├── values/              # 配置
│   │       │   └── xml/                 # XML配置
│   │       └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── .gitignore
└── README.md
```

## 如何运行

### 方法1: 使用Android Studio (推荐)

1. 解压 `ByteDanceTrainingCamp.tar.gz`
2. 打开Android Studio
3. File -> Open -> 选择解压后的 `ByteDanceTrainingCamp` 文件夹
4. 等待Gradle同步完成
5. 连接Android设备或启动模拟器
6. 点击运行按钮 ▶️

### 方法2: 使用命令行

```bash
# 解压项目
tar -xzf ByteDanceTrainingCamp.tar.gz
cd ByteDanceTrainingCamp

# 编译项目
./gradlew build

# 安装到设备
./gradlew installDebug
```

## 已实现功能

✅ 双列瀑布流布局 (StaggeredGridLayoutManager)
✅ 顶部Tab切换 (推荐/关注)
✅ 视频详情页 (全屏播放)
✅ ExoPlayer视频播放
✅ 点赞、评论、分享、关注
✅ 评论系统 (BottomSheet)
✅ 下拉加载更多
✅ 底部导航栏

## 技术栈

- Kotlin 100%
- AndroidX
- ViewPager2
- RecyclerView
- ExoPlayer
- Glide
- Material Design

## 核心代码说明

### 1. 瀑布流实现
```kotlin
val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
recyclerView.layoutManager = layoutManager
```

### 2. 视频播放
```kotlin
exoPlayer = ExoPlayer.Builder(this).build()
playerView.player = exoPlayer
exoPlayer?.setMediaItem(MediaItem.fromUri(videoUrl))
exoPlayer?.prepare()
exoPlayer?.playWhenReady = true
```

### 3. 评论弹窗
```kotlin
val bottomSheetDialog = BottomSheetDialog(this)
bottomSheetDialog.setContentView(R.layout.bottom_sheet_comment)
bottomSheetDialog.show()
```

## 注意事项

1. 项目使用模拟数据，实际应替换为网络请求
2. 视频URL使用Google示例视频，可替换为真实URL
3. 需要网络权限才能加载图片和视频
4. 建议在真机上测试视频播放功能

## 系统要求

- Android Studio Hedgehog | 2023.1.1+
- JDK 17
- Android SDK API 34
- Gradle 8.0+
- 最低支持 Android 7.0 (API 24)

## 常见问题

**Q: Gradle同步失败？**
A: 检查网络连接，确保可以访问Maven Central和Google Maven

**Q: 无法播放视频？**
A: 检查网络权限，确保设备联网

**Q: 图片加载失败？**
A: 使用的是Picsum随机图片API，需要网络连接

## 下一步优化建议

1. 集成真实API接口
2. 添加用户登录系统
3. 实现视频上传功能
4. 优化视频预加载
5. 添加本地缓存
6. 性能优化

## License

MIT License
