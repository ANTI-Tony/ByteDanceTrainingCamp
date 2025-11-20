package com.bytedance.trainingcamp.utils

import com.bytedance.trainingcamp.model.Author
import com.bytedance.trainingcamp.model.Comment
import com.bytedance.trainingcamp.model.VideoItem

object MockDataUtils {
    
    private val authors = listOf(
        Author("1", "旅行达人小王", "https://picsum.photos/200/200?random=1"),
        Author("2", "美食探索者", "https://picsum.photos/200/200?random=2"),
        Author("3", "健身教练张三", "https://picsum.photos/200/200?random=3"),
        Author("4", "摄影师李四", "https://picsum.photos/200/200?random=4"),
        Author("5", "科技博主", "https://picsum.photos/200/200?random=5"),
        Author("6", "时尚达人", "https://picsum.photos/200/200?random=6"),
        Author("7", "游戏解说", "https://picsum.photos/200/200?random=7"),
        Author("8", "音乐创作人", "https://picsum.photos/200/200?random=8")
    )
    
    private val titles = listOf(
        "这个地方太美了,必须来打卡!",
        "教你做一道简单又好吃的家常菜",
        "坚持30天,身材大变样",
        "日落时分的绝美瞬间",
        "最新科技产品体验分享",
        "今年秋冬流行穿搭指南",
        "这款游戏太上头了",
        "原创音乐分享,希望你喜欢"
    )
    
    private val descriptions = listOf(
        "分享生活中的美好瞬间 #旅行 #风景",
        "记录美食制作过程 #美食 #教程",
        "健身打卡第30天 #健身 #运动",
        "用镜头记录世界 #摄影 #日落",
        "科技改变生活 #科技 #数码",
        "时尚就是态度 #穿搭 #时尚",
        "游戏精彩时刻 #游戏 #娱乐",
        "原创音乐作品 #音乐 #原创"
    )
    
    // 使用示例视频URL（实际项目中应该使用真实视频）
    private val videoUrls = listOf(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
    )
    
    fun getMockVideoList(count: Int = 20): List<VideoItem> {
        return (0 until count).map { index ->
            val authorIndex = index % authors.size
            val titleIndex = index % titles.size
            VideoItem(
                id = "video_$index",
                videoUrl = videoUrls[index % videoUrls.size],
                coverUrl = "https://picsum.photos/400/600?random=$index",
                title = titles[titleIndex],
                author = authors[authorIndex].copy(),
                likeCount = (100..10000).random(),
                commentCount = (10..1000).random(),
                isLiked = false,
                description = descriptions[titleIndex]
            )
        }
    }
    
    fun getMockComments(videoId: String, count: Int = 10): List<Comment> {
        val comments = listOf(
            "太棒了!",
            "真的很不错",
            "学到了",
            "支持一下",
            "拍的真好",
            "期待下一期",
            "已收藏",
            "感谢分享",
            "太有用了",
            "继续加油"
        )
        
        return (0 until count).map { index ->
            Comment(
                id = "comment_${videoId}_$index",
                author = authors[index % authors.size].copy(),
                content = comments[index % comments.size],
                likeCount = (0..100).random(),
                isLiked = false,
                timestamp = System.currentTimeMillis() - (index * 3600000L)
            )
        }
    }
}
