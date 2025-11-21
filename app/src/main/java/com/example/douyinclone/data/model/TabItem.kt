package com.example.douyinclone.data.model

data class TabItem(
    val id: Int,
    val title: String,
    val isSelected: Boolean = false
)

object TabConstants {
    val TOP_TABS = listOf(
        TabItem(0, "购"),
        TabItem(1, "经验"),
        TabItem(2, "同城"),
        TabItem(3, "关注"),
        TabItem(4, "商城"),
        TabItem(5, "推荐", true)
    )
    
    val BOTTOM_TABS = listOf(
        TabItem(0, "首页"),
        TabItem(1, "朋友"),
        TabItem(2, ""),  // 中间加号按钮
        TabItem(3, "消息"),
        TabItem(4, "我")
    )
}
