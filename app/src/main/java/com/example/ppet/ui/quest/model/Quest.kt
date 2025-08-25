package com.example.ppet.ui.quest.model

import java.util.Date

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val type: QuestType,
    val points: Int,
    val currentProgress: Int,
    val maxProgress: Int,
    val isCompleted: Boolean = false,
    val isRewarded: Boolean = false,
    val iconType: QuestIconType,
    val deadline: Date? = null,
    val createdAt: Date = Date(),
    val completedAt: Date? = null,
    val category: QuestCategory = QuestCategory.GENERAL
)

enum class QuestType {
    DAILY,    
    WEEKLY,   
    SPECIAL   
}

enum class QuestIconType {
    WALK,     
    FEED,     
    HEALTH,   
    PLAY,     
    HOSPITAL, 
    PHOTO,    
    SPECIAL   
}

enum class QuestCategory {
    ALL,      
    GENERAL,  
    CARE,     
    EXERCISE, 
    HEALTH,   
    SOCIAL    
}

data class UserPoints(
    val totalPoints: Int,
    val todayEarned: Int,
    val level: Int,
    val nextLevelPoints: Int = 0,
    val currentLevelProgress: Float = 0f
)

data class QuestReward(
    val questId: String,
    val points: Int,
    val badge: String? = null,
    val itemReward: String? = null
)

data class WalkStats(
    val totalDistance: Float = 0f,
    val duration: Long = 0L,
    val currentSpeed: Float = 0f,
    val isWalking: Boolean = false,
    val averageSpeed: Float = 0f,
    val stepCount: Int = 0
)

data class QuestProgress(
    val questId: String,
    val progress: Int,
    val maxProgress: Int,
    val isCompleted: Boolean = false
) {
    val progressPercentage: Float
        get() = if (maxProgress > 0) (progress.toFloat() / maxProgress) * 100f else 0f
}
