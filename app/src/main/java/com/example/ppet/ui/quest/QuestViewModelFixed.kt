package com.example.ppet.ui.quest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppet.ui.quest.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class QuestUiState(
    val activeQuests: List<Quest> = emptyList(),
    val completedQuests: List<Quest> = emptyList(),
    val isLoading: Boolean = false,
    val selectedCategory: QuestCategory = QuestCategory.ALL,
    val walkStats: WalkStats = WalkStats(),
    val isWalkingActive: Boolean = false,
    val userPoints: UserPoints = UserPoints(0, 0, 1),
    val todayProgress: List<QuestProgress> = emptyList(),
    val questRewards: List<QuestReward> = emptyList()
)

@HiltViewModel
class QuestViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(QuestUiState())
    val uiState: StateFlow<QuestUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        generateSampleQuests()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                userPoints = UserPoints(
                    totalPoints = 1250,
                    todayEarned = 80,
                    level = 5,
                    nextLevelPoints = 1500,
                    currentLevelProgress = 0.83f
                )
            )
        }
    }

    private fun generateSampleQuests() {
        val sampleQuests = listOf(
            Quest(
                id = "1",
                title = "반려동물과 산책하기",
                description = "15분 이상 산책을 해보세요",
                type = QuestType.DAILY,
                points = 50,
                currentProgress = 8,
                maxProgress = 15,
                iconType = QuestIconType.WALK,
                category = QuestCategory.EXERCISE
            ),
            Quest(
                id = "2",
                title = "사료 급여하기",
                description = "정해진 시간에 사료를 급여하세요",
                type = QuestType.DAILY,
                points = 30,
                currentProgress = 2,
                maxProgress = 2,
                isCompleted = true,
                iconType = QuestIconType.FEED,
                category = QuestCategory.CARE
            ),
            Quest(
                id = "3",
                title = "건강 체크리스트",
                description = "반려동물의 건강 상태를 확인하세요",
                type = QuestType.WEEKLY,
                points = 100,
                currentProgress = 3,
                maxProgress = 7,
                iconType = QuestIconType.HEALTH,
                category = QuestCategory.HEALTH
            ),
            Quest(
                id = "4",
                title = "놀아주기",
                description = "장난감으로 30분간 놀아주세요",
                type = QuestType.DAILY,
                points = 40,
                currentProgress = 20,
                maxProgress = 30,
                iconType = QuestIconType.PLAY,
                category = QuestCategory.CARE
            ),
            Quest(
                id = "5",
                title = "사진 촬영하기",
                description = "귀여운 모습을 사진으로 남겨보세요",
                type = QuestType.SPECIAL,
                points = 80,
                currentProgress = 0,
                maxProgress = 3,
                iconType = QuestIconType.PHOTO,
                category = QuestCategory.SOCIAL
            )
        )

        val completedSample = Quest(
            id = "6",
            title = "병원 방문",
            description = "정기 건강검진을 받아보세요",
            type = QuestType.WEEKLY,
            points = 200,
            currentProgress = 1,
            maxProgress = 1,
            isCompleted = true,
            isRewarded = true,
            completedAt = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000),
            iconType = QuestIconType.HOSPITAL,
            category = QuestCategory.HEALTH
        )

        _uiState.value = _uiState.value.copy(
            activeQuests = sampleQuests,
            completedQuests = listOf(completedSample) + generateCompletedQuests(),
            todayProgress = sampleQuests.map {
                QuestProgress(it.id, it.currentProgress, it.maxProgress, it.isCompleted)
            }
        )
    }

    private fun generateCompletedQuests(): List<Quest> {
        return listOf(
            Quest(
                id = "c1",
                title = "물그릇 채우기",
                description = "신선한 물로 교체했어요",
                type = QuestType.DAILY,
                points = 20,
                currentProgress = 1,
                maxProgress = 1,
                isCompleted = true,
                isRewarded = true,
                completedAt = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000),
                iconType = QuestIconType.FEED,
                category = QuestCategory.CARE
            ),
            Quest(
                id = "c2",
                title = "브러싱하기",
                description = "털 관리를 완료했어요",
                type = QuestType.DAILY,
                points = 30,
                currentProgress = 1,
                maxProgress = 1,
                isCompleted = true,
                isRewarded = true,
                completedAt = Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000),
                iconType = QuestIconType.HEALTH,
                category = QuestCategory.HEALTH
            )
        )
    }

    fun startWalkTracking() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isWalkingActive = true,
                walkStats = WalkStats(isWalking = true)
            )
            simulateWalkProgress()
        }
    }

    fun stopWalkTracking() {
        viewModelScope.launch {
            val currentStats = _uiState.value.walkStats
            _uiState.value = _uiState.value.copy(
                isWalkingActive = false,
                walkStats = currentStats.copy(isWalking = false)
            )

            if (currentStats.duration > 0) {
                val walkMinutes = (currentStats.duration / 60000).toInt()
                updateQuestProgress("1", walkMinutes)
            }
        }
    }

    private fun simulateWalkProgress() {
        viewModelScope.launch {
            var duration = 0L
            var distance = 0f
            var steps = 0

            while (_uiState.value.isWalkingActive) {
                kotlinx.coroutines.delay(1000)

                duration += 1000
                distance += 1.2f
                steps += 2

                val avgSpeed = if (duration > 0) distance / (duration / 1000f) else 0f

                _uiState.value = _uiState.value.copy(
                    walkStats = _uiState.value.walkStats.copy(
                        duration = duration,
                        totalDistance = distance,
                        currentSpeed = 1.2f,
                        averageSpeed = avgSpeed,
                        stepCount = steps
                    )
                )
            }
        }
    }

    fun filterQuestsByCategory(category: QuestCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun updateQuestProgress(questId: String, progress: Int) {
        viewModelScope.launch {
            val activeQuests = _uiState.value.activeQuests.map { quest ->
                if (quest.id == questId) {
                    val newProgress = minOf(progress, quest.maxProgress)
                    val isCompleted = newProgress >= quest.maxProgress
                    quest.copy(
                        currentProgress = newProgress,
                        isCompleted = isCompleted,
                        completedAt = if (isCompleted && quest.completedAt == null) Date() else quest.completedAt
                    )
                } else quest
            }

            val (stillActive, newlyCompleted) = activeQuests.partition { !it.isCompleted }
            val updatedCompleted = _uiState.value.completedQuests + newlyCompleted

            _uiState.value = _uiState.value.copy(
                activeQuests = stillActive,
                completedQuests = updatedCompleted,
                todayProgress = stillActive.map {
                    QuestProgress(it.id, it.currentProgress, it.maxProgress, it.isCompleted)
                }
            )

            if (newlyCompleted.isNotEmpty()) {
                val earnedPoints = newlyCompleted.sumOf { it.points }
                val currentPoints = _uiState.value.userPoints
                _uiState.value = _uiState.value.copy(
                    userPoints = currentPoints.copy(
                        totalPoints = currentPoints.totalPoints + earnedPoints,
                        todayEarned = currentPoints.todayEarned + earnedPoints
                    )
                )
            }
        }
    }

    fun claimQuestReward(questId: String) {
        viewModelScope.launch {
            val completedQuests = _uiState.value.completedQuests.map { quest ->
                if (quest.id == questId && quest.isCompleted && !quest.isRewarded) {
                    quest.copy(isRewarded = true)
                } else quest
            }

            _uiState.value = _uiState.value.copy(completedQuests = completedQuests)
        }
    }
}
