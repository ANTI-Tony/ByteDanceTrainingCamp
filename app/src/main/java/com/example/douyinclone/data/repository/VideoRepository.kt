package com.example.douyinclone.data.repository

import com.example.douyinclone.data.model.Comment
import com.example.douyinclone.data.model.VideoItem
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor() {

    private val mockVideos = mutableListOf<VideoItem>()
    private val mockComments = mutableListOf<Comment>()

    init {
        initMockData()
    }

    private fun initMockData() {
        // Mock视频数据
        val covers = listOf(
            "https://picsum.photos/400/600?random=1",
            "https://picsum.photos/400/600?random=2",
            "https://picsum.photos/400/600?random=3",
            "https://picsum.photos/400/600?random=4",
            "https://picsum.photos/400/600?random=5",
            "https://picsum.photos/400/600?random=6",
            "https://picsum.photos/400/600?random=7",
            "https://picsum.photos/400/600?random=8",
            "https://picsum.photos/400/600?random=9",
            "https://picsum.photos/400/600?random=10"
        )

        val titles = listOf(
            "38集｜喜欢海边五彩斑斓的彩色石头吗？观看建...",
            "人生只要两次幸运就好，一次遇见你，一次走到底...",
            "程摇跨简单易学 饭后十分钟 助消化瘦肚子",
            "苏泊尔折叠式烧水壶，折叠设计，小巧轻便，出差旅...",
            "#适合所有人的健身动 每天坚持锻炼会有不一...",
            "大话西游:归来",
            "《基础摆胯舞》",
            "今日份的美食分享",
            "旅行vlog｜云南之行",
            "教你三分钟学会这道菜"
        )

        val authors = listOf(
            "治愈宝藏美景",
            "向日葵",
            "HuDev",
            "苏泊尔折叠电水壶",
            "健身达人",
            "游戏官方",
            "舞蹈教学",
            "美食家小明",
            "旅行者小红",
            "厨房小白"
        )

        // 使用多个不同的公开测试视频URL
        val videoUrls = listOf(
            "https://www.w3schools.com/html/mov_bbb.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4"
        )

        for (i in 0 until 20) {
            val index = i % 10
            mockVideos.add(
                VideoItem(
                    id = "video_$i",
                    coverUrl = covers[index],
                    videoUrl = videoUrls[index],
                    title = titles[index],
                    authorName = authors[index],
                    authorAvatar = "https://picsum.photos/100/100?random=${i + 100}",
                    likeCount = (100..9999).random(),
                    commentCount = (10..999).random(),
                    shareCount = (5..500).random(),
                    description = titles[index],
                    musicName = "原声音乐",
                    musicAuthor = authors[index]
                )
            )
        }

        // Mock评论数据
        val commentContents = listOf(
            "太棒了！",
            "学到了，感谢分享",
            "这个视频真的很有用",
            "已收藏，准备试试",
            "博主太厉害了",
            "求更多类似的内容",
            "第一次看就爱上了",
            "每天都来看一遍",
            "推荐给朋友了",
            "期待更新"
        )

        for (i in 0 until 30) {
            mockComments.add(
                Comment(
                    id = "comment_$i",
                    userId = "user_$i",
                    userName = "用户${i + 1}",
                    userAvatar = "https://picsum.photos/50/50?random=${i + 200}",
                    content = commentContents[i % commentContents.size],
                    likeCount = (0..999).random(),
                    createTime = System.currentTimeMillis() - (i * 3600000L)
                )
            )
        }
    }

    suspend fun getVideos(page: Int, pageSize: Int = 10): List<VideoItem> {
        delay(500) // 模拟网络延迟
        val startIndex = page * pageSize
        val endIndex = minOf(startIndex + pageSize, mockVideos.size)

        if (startIndex >= mockVideos.size) {
            // 模拟加载更多时生成新数据
            val newVideos = mockVideos.shuffled().mapIndexed { index, video ->
                video.copy(
                    id = "video_${System.currentTimeMillis()}_$index",
                    coverUrl = "https://picsum.photos/400/600?random=${System.currentTimeMillis() + index}"
                )
            }
            return newVideos.take(pageSize)
        }

        return mockVideos.subList(startIndex, endIndex)
    }

    suspend fun refreshVideos(): List<VideoItem> {
        delay(800) // 模拟网络延迟
        return mockVideos.shuffled()
    }

    suspend fun getComments(videoId: String): List<Comment> {
        delay(300)
        return mockComments.shuffled()
    }

    suspend fun addComment(videoId: String, content: String): Comment {
        delay(200)
        val newComment = Comment(
            id = "comment_${System.currentTimeMillis()}",
            userId = "current_user",
            userName = "我",
            userAvatar = "https://picsum.photos/50/50?random=999",
            content = content,
            likeCount = 0,
            createTime = System.currentTimeMillis()
        )
        mockComments.add(0, newComment)
        return newComment
    }

    suspend fun likeVideo(videoId: String): Boolean {
        delay(100)
        val index = mockVideos.indexOfFirst { it.id == videoId }
        if (index >= 0) {
            val video = mockVideos[index]
            mockVideos[index] = video.copy(
                isLiked = !video.isLiked,
                likeCount = if (video.isLiked) video.likeCount - 1 else video.likeCount + 1
            )
            return true
        }
        return false
    }
}