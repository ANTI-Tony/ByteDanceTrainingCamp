package com.example.douyinclone.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {
    
    fun formatCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1fw", count / 10000f)
            count >= 1000 -> String.format("%.1fk", count / 1000f)
            else -> count.toString()
        }
    }
    
    fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60000 -> "刚刚"
            diff < 3600000 -> "${diff / 60000}分钟前"
            diff < 86400000 -> "${diff / 3600000}小时前"
            diff < 604800000 -> "${diff / 86400000}天前"
            else -> {
                val sdf = SimpleDateFormat("MM-dd", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }
    
    fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
