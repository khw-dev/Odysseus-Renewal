package com.example.ppet.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.ppet.model.Quest
import com.example.ppet.model.QuestStatus
import com.example.ppet.model.QuestTemplates
import com.example.ppet.service.WalkSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "ppet_quest_prefs", Context.MODE_PRIVATE
    )
    private val gson = Gson()

    private val _activeQuests = MutableStateFlow<List<Quest>>(emptyList())
    val activeQuests: Flow<List<Quest>> = _activeQuests.asStateFlow()

    private val _completedQuests = MutableStateFlow<List<Quest>>(emptyList())
    val completedQuests: Flow<List<Quest>> = _completedQuests.asStateFlow()

    private val _walkSessions = MutableStateFlow<List<WalkSession>>(emptyList())
    val walkSessions: Flow<List<WalkSession>> = _walkSessions.asStateFlow()

    companion object {
        private const val KEY_ACTIVE_QUESTS = "active_quests"
        private const val KEY_COMPLETED_QUESTS = "completed_quests"
        private const val KEY_WALK_SESSIONS = "walk_sessions"
        private const val KEY_LAST_QUEST_REFRESH = "last_quest_refresh"
    }

    init {
        loadQuests()
        loadWalkSessions()
        checkAndRefreshQuests()
    }

    private fun loadQuests() {
        
        val activeQuestsJson = sharedPreferences.getString(KEY_ACTIVE_QUESTS, null)
        if (activeQuestsJson != null) {
            try {
                val type = object : TypeToken<List<Quest>>() {}.type
                val quests = gson.fromJson<List<Quest>>(activeQuestsJson, type)
                _activeQuests.value = quests.filter { !isQuestExpired(it) }
            } catch (e: Exception) {
                _activeQuests.value = emptyList()
            }
        }

        
        val completedQuestsJson = sharedPreferences.getString(KEY_COMPLETED_QUESTS, null)
        if (completedQuestsJson != null) {
            try {
                val type = object : TypeToken<List<Quest>>() {}.type
                val quests = gson.fromJson<List<Quest>>(completedQuestsJson, type)
                _completedQuests.value = quests
            } catch (e: Exception) {
                _completedQuests.value = emptyList()
            }
        }
    }

    private fun loadWalkSessions() {
        val walkSessionsJson = sharedPreferences.getString(KEY_WALK_SESSIONS, null)
        if (walkSessionsJson != null) {
            try {
                val type = object : TypeToken<List<WalkSession>>() {}.type
                val sessions = gson.fromJson<List<WalkSession>>(walkSessionsJson, type)
                _walkSessions.value = sessions
            } catch (e: Exception) {
                _walkSessions.value = emptyList()
            }
        }
    }

    private fun saveActiveQuests() {
        val questsJson = gson.toJson(_activeQuests.value)
        sharedPreferences.edit()
            .putString(KEY_ACTIVE_QUESTS, questsJson)
            .apply()
    }

    private fun saveCompletedQuests() {
        val questsJson = gson.toJson(_completedQuests.value)
        sharedPreferences.edit()
            .putString(KEY_COMPLETED_QUESTS, questsJson)
            .apply()
    }

    private fun saveWalkSessions() {
        val sessionsJson = gson.toJson(_walkSessions.value)
        sharedPreferences.edit()
            .putString(KEY_WALK_SESSIONS, sessionsJson)
            .apply()
    }

    private fun checkAndRefreshQuests() {
        val lastRefresh = sharedPreferences.getLong(KEY_LAST_QUEST_REFRESH, 0)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        
        if (lastRefresh < today) {
            generateDailyQuests()

            
            val calendar = Calendar.getInstance()
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                generateWeeklyQuests()
            }

            
            if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
                generateMonthlyQuests()
            }

            sharedPreferences.edit()
                .putLong(KEY_LAST_QUEST_REFRESH, today)
                .apply()
        }
    }

    private fun generateDailyQuests() {
        val currentActive = _activeQuests.value.toMutableList()

        
        currentActive.removeAll { it.type == com.example.ppet.model.QuestType.DAILY }

        
        val newDailyQuests = QuestTemplates.getDailyQuests()
        currentActive.addAll(newDailyQuests)

        _activeQuests.value = currentActive
        saveActiveQuests()
    }

    private fun generateWeeklyQuests() {
        val currentActive = _activeQuests.value.toMutableList()
        currentActive.removeAll { it.type == com.example.ppet.model.QuestType.WEEKLY }

        val newWeeklyQuests = QuestTemplates.getWeeklyQuests()
        currentActive.addAll(newWeeklyQuests)

        _activeQuests.value = currentActive
        saveActiveQuests()
    }

    private fun generateMonthlyQuests() {
        val currentActive = _activeQuests.value.toMutableList()
        currentActive.removeAll { it.type == com.example.ppet.model.QuestType.MONTHLY }

        val newMonthlyQuests = QuestTemplates.getMonthlyQuests()
        currentActive.addAll(newMonthlyQuests)

        _activeQuests.value = currentActive
        saveActiveQuests()
    }

    suspend fun updateQuestProgress(questId: String, progress: Int) {
        val currentQuests = _activeQuests.value.toMutableList()
        val questIndex = currentQuests.indexOfFirst { it.id == questId }

        if (questIndex != -1) {
            val quest = currentQuests[questIndex]
            val updatedQuest = quest.copy(
                currentProgress = minOf(progress, quest.targetValue),
                status = if (progress >= quest.targetValue) QuestStatus.COMPLETED else QuestStatus.IN_PROGRESS
            )

            currentQuests[questIndex] = updatedQuest
            _activeQuests.value = currentQuests
            saveActiveQuests()

            
            if (updatedQuest.status == QuestStatus.COMPLETED) {
                completeQuest(updatedQuest)
            }
        }
    }

    private fun completeQuest(quest: Quest) {
        val currentCompleted = _completedQuests.value.toMutableList()
        currentCompleted.add(quest)
        _completedQuests.value = currentCompleted
        saveCompletedQuests()

        
        val currentActive = _activeQuests.value.toMutableList()
        currentActive.removeAll { it.id == quest.id }
        _activeQuests.value = currentActive
        saveActiveQuests()
    }

    suspend fun saveWalkSession(session: WalkSession) {
        val currentSessions = _walkSessions.value.toMutableList()
        currentSessions.add(session)
        _walkSessions.value = currentSessions
        saveWalkSessions()

        
        updateWalkingQuests(session)
    }

    private suspend fun updateWalkingQuests(session: WalkSession) {
        val walkingMinutes = (session.totalDuration / 60000).toInt()

        
        if (walkingMinutes >= 5) {
            val walkingQuests = _activeQuests.value.filter {
                it.category == com.example.ppet.model.QuestCategory.EXERCISE &&
                it.isAutoDetectable
            }

            walkingQuests.forEach { quest ->
                val newProgress = quest.currentProgress + walkingMinutes
                updateQuestProgress(quest.id, newProgress)
            }
        }
    }

    fun getTodayWalkingTime(): Int {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return _walkSessions.value
            .filter { it.startTime >= today && it.endTime != null }
            .sumOf { (it.totalDuration / 60000).toInt() }
    }

    fun getWeeklyWalkingTime(): Int {
        val weekStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return _walkSessions.value
            .filter { it.startTime >= weekStart && it.endTime != null }
            .sumOf { (it.totalDuration / 60000).toInt() }
    }

    private fun isQuestExpired(quest: Quest): Boolean {
        return Date().after(quest.endDate)
    }

    fun getQuestCompletionRate(): Float {
        val total = _activeQuests.value.size + _completedQuests.value.size
        return if (total > 0) {
            _completedQuests.value.size.toFloat() / total
        } else 0f
    }
}
