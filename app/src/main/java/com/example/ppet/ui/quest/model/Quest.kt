package com.example.ppet.ui.quest.model

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
    val iconType: QuestIconType
)

enum class QuestType {
    DAILY,    // 일일 퀘스트
    WEEKLY,   // 주간 퀘스트
    SPECIAL   // 특별 퀘스트
}

enum class QuestIconType {
    WALK,     // 산책
    FEED,     // 사료 급여
    HEALTH,   // 건강 관리
    PLAY,     // 놀아주기
    HOSPITAL, // 병원 방문
    PHOTO,    // 사진 촬영
    SPECIAL   // 특별 활동
}

data class UserPoints(
    val totalPoints: Int,
    val todayEarned: Int,
    val level: Int
) {
    val nextLevelPoints: Int
        get() = (level + 1) * 1000

    val currentLevelProgress: Float
        get() = (totalPoints % 1000) / 1000f
}
