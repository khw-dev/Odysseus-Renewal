package com.example.ppet.ui.quest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppet.model.Quest
import com.example.ppet.model.QuestCategory
import com.example.ppet.repository.QuestRepository
import com.example.ppet.service.WalkSession
import com.example.ppet.service.WalkStats
import com.example.ppet.service.WalkTrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestUiState(
    val activeQuests: List<Quest> = emptyList(),
    val completedQuests: List<Quest> = emptyList(),
    val isLoading: Boolean = false,
    val selectedCategory: QuestCategory? = null,
    val walkStats: WalkStats = WalkStats(),
    val isWalkingActive: Boolean = false,
    val todayWalkingTime: Int = 0,
    val weeklyWalkingTime: Int = 0,
    val completionRate: Float = 0f
)

@HiltViewModel
class QuestViewModel @Inject constructor(
    private val questRepository: QuestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestUiState())
    val uiState: StateFlow<QuestUiState> = _uiState.asStateFlow()

    private var walkTrackingService: WalkTrackingService? = null

    init {
        loadQuests()
        observeWalkingData()
    }

    private fun loadQuests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            combine(
                questRepository.activeQuests,
                questRepository.completedQuests
            ) { active, completed ->
                _uiState.value = _uiState.value.copy(
                    activeQuests = active,
                    completedQuests = completed,
                    isLoading = false,
                    todayWalkingTime = questRepository.getTodayWalkingTime(),
                    weeklyWalkingTime = questRepository.getWeeklyWalkingTime(),
                    completionRate = questRepository.getQuestCompletionRate()
                )
            }
        }
    }

    private fun observeWalkingData() {
        walkTrackingService?.let { service ->
            viewModelScope.launch {
                service.walkStats.collect { stats ->
                    _uiState.value = _uiState.value.copy(
                        walkStats = stats,
                        isWalkingActive = stats.isWalking
                    )
                }
            }
        }
    }

    fun setWalkTrackingService(service: WalkTrackingService) {
        walkTrackingService = service
        observeWalkingData()
    }

    fun startWalkTracking() {
        walkTrackingService?.let { service ->
            if (service.startWalkTracking()) {
                _uiState.value = _uiState.value.copy(isWalkingActive = true)
            }
        }
    }

    fun stopWalkTracking() {
        walkTrackingService?.stopWalkTracking()
        _uiState.value = _uiState.value.copy(isWalkingActive = false)
    }

    fun filterQuestsByCategory(category: QuestCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun getFilteredQuests(): List<Quest> {
        val currentState = _uiState.value
        return if (currentState.selectedCategory != null) {
            currentState.activeQuests.filter { it.category == currentState.selectedCategory }
        } else {
            currentState.activeQuests
        }
    }

    fun updateQuestProgress(questId: String, progress: Int) {
        viewModelScope.launch {
            questRepository.updateQuestProgress(questId, progress)
        }
    }

    fun markQuestAsCompleted(questId: String) {
        val quest = _uiState.value.activeQuests.find { it.id == questId }
        quest?.let {
            updateQuestProgress(questId, it.targetValue)
        }
    }
}
