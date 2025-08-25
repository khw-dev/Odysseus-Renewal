package com.example.ppet.model

import java.util.*


enum class QuestType {
    DAILY,      
    WEEKLY,     
    MONTHLY,    
    SPECIAL     
}


enum class QuestCategory {
    EXERCISE,    
    CARE,        
    FEEDING,     
    HEALTH,      
    SOCIAL,      
    LEARNING     
}


enum class QuestStatus {
    NOT_STARTED, 
    IN_PROGRESS, 
    COMPLETED,   
    EXPIRED      
}


data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val category: QuestCategory,
    val type: QuestType,
    val targetValue: Int,           
    val currentProgress: Int = 0,   
    val unit: String,               
    val rewardExp: Int,             
    val rewardCoins: Int = 0,       
    val startDate: Date,            
    val endDate: Date,              
    val status: QuestStatus = QuestStatus.NOT_STARTED,
    val isAutoDetectable: Boolean,  
    val petId: String? = null       
)


object QuestTemplates {

    
    fun getDailyQuests(): List<Quest> {
        val today = Date()
        val tomorrow = Calendar.getInstance().apply {
            time = today
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time

        return listOf(
            Quest(
                id = "daily_walk_${today.time}",
                title = "산책하기",
                description = "반려동물과 함께 30분 이상 산책해보세요",
                category = QuestCategory.EXERCISE,
                type = QuestType.DAILY,
                targetValue = 30,
                unit = "분",
                rewardExp = 50,
                rewardCoins = 10,
                startDate = today,
                endDate = tomorrow,
                isAutoDetectable = true
            ),
            Quest(
                id = "daily_feed_${today.time}",
                title = "규칙적인 급식",
                description = "하루 3번 정해진 시간에 급식하기",
                category = QuestCategory.FEEDING,
                type = QuestType.DAILY,
                targetValue = 3,
                unit = "회",
                rewardExp = 30,
                rewardCoins = 5,
                startDate = today,
                endDate = tomorrow,
                isAutoDetectable = false
            ),
            Quest(
                id = "daily_play_${today.time}",
                title = "놀아주기",
                description = "반려동물과 15분 이상 놀아주세요",
                category = QuestCategory.CARE,
                type = QuestType.DAILY,
                targetValue = 15,
                unit = "분",
                rewardExp = 40,
                rewardCoins = 8,
                startDate = today,
                endDate = tomorrow,
                isAutoDetectable = false
            ),
            Quest(
                id = "daily_water_${today.time}",
                title = "물 갈아주기",
                description = "신선한 물로 갈아주기",
                category = QuestCategory.CARE,
                type = QuestType.DAILY,
                targetValue = 1,
                unit = "회",
                rewardExp = 20,
                rewardCoins = 3,
                startDate = today,
                endDate = tomorrow,
                isAutoDetectable = false
            )
        )
    }

    
    fun getWeeklyQuests(): List<Quest> {
        val today = Date()
        val nextWeek = Calendar.getInstance().apply {
            time = today
            add(Calendar.WEEK_OF_YEAR, 1)
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time

        return listOf(
            Quest(
                id = "weekly_long_walk_${today.time}",
                title = "장거리 산책",
                description = "일주일 동안 총 3시간 이상 산책하기",
                category = QuestCategory.EXERCISE,
                type = QuestType.WEEKLY,
                targetValue = 180,
                unit = "분",
                rewardExp = 200,
                rewardCoins = 50,
                startDate = today,
                endDate = nextWeek,
                isAutoDetectable = true
            ),
            Quest(
                id = "weekly_vet_visit_${today.time}",
                title = "건강검진",
                description = "동물병원 방문하기",
                category = QuestCategory.HEALTH,
                type = QuestType.WEEKLY,
                targetValue = 1,
                unit = "회",
                rewardExp = 150,
                rewardCoins = 30,
                startDate = today,
                endDate = nextWeek,
                isAutoDetectable = false
            ),
            Quest(
                id = "weekly_grooming_${today.time}",
                title = "그루밍하기",
                description = "브러싱 또는 목욕 3회 이상",
                category = QuestCategory.CARE,
                type = QuestType.WEEKLY,
                targetValue = 3,
                unit = "회",
                rewardExp = 100,
                rewardCoins = 25,
                startDate = today,
                endDate = nextWeek,
                isAutoDetectable = false
            )
        )
    }

    
    fun getMonthlyQuests(): List<Quest> {
        val today = Date()
        val nextMonth = Calendar.getInstance().apply {
            time = today
            add(Calendar.MONTH, 1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time

        return listOf(
            Quest(
                id = "monthly_exercise_master_${today.time}",
                title = "운동 마스터",
                description = "한 달 동안 총 20시간 이상 산책하기",
                category = QuestCategory.EXERCISE,
                type = QuestType.MONTHLY,
                targetValue = 1200,
                unit = "분",
                rewardExp = 1000,
                rewardCoins = 200,
                startDate = today,
                endDate = nextMonth,
                isAutoDetectable = true
            ),
            Quest(
                id = "monthly_social_pet_${today.time}",
                title = "사회적 반려동물",
                description = "다른 반려동물과 만나기 10회",
                category = QuestCategory.SOCIAL,
                type = QuestType.MONTHLY,
                targetValue = 10,
                unit = "회",
                rewardExp = 500,
                rewardCoins = 100,
                startDate = today,
                endDate = nextMonth,
                isAutoDetectable = false
            )
        )
    }
}
