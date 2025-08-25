package com.example.ppet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppet.data.model.*
import com.example.ppet.data.repository.PpetRepository
import com.example.ppet.model.Quest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(
    private val repository: PpetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FirebaseUiState())
    val uiState: StateFlow<FirebaseUiState> = _uiState.asStateFlow()

    
    val userPets: Flow<List<FirebasePet>> = repository.getUserPets()

    
    fun saveUserInfo(displayName: String?, email: String?, profilePictureUrl: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val userInfo = UserInfo(
                displayName = displayName,
                email = email,
                profilePictureUrl = profilePictureUrl
            )

            repository.saveUserInfo(userInfo)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "사용자 정보가 저장되었습니다."
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    
    fun savePet(pet: Pet) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.savePet(pet)
                .onSuccess { petId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "펫 정보가 저장되었습니다."
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    
    fun saveHealthRecord(healthRecord: HealthRecord) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.saveHealthRecord(healthRecord)
                .onSuccess { recordId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "건강 기록이 저장되었습니다."
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    
    fun saveQuest(quest: Quest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.saveQuest(quest)
                .onSuccess { questId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "퀘스트가 저장되었습니다."
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    
    fun completeQuest(questId: String, expReward: Int, coinReward: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.completeQuestAndUpdateUser(questId, expReward, coinReward)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "퀘스트를 완료했습니다! 경험치 $expReward, 코인 $coinReward 획득!"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    
    fun saveWalkRecord(petId: String, duration: Int, distance: Double, location: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.saveWalkRecord(petId, duration, distance, location)
                .onSuccess { recordId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "산책 기록이 저장되었습니다."
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    
    fun saveFeedingRecord(petId: String, notes: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.saveFeedingRecord(petId, notes)
                .onSuccess { recordId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "급식 기록이 저장되었습니다."
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    
    fun updateUserLevel(expGain: Int, coinGain: Int = 0) {
        viewModelScope.launch {
            repository.updateUserLevel(expGain, coinGain)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        message = "경험치 $expGain, 코인 $coinGain 획득!"
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
        }
    }

    
    fun createNotification(title: String, message: String, type: String, petId: String? = null) {
        viewModelScope.launch {
            repository.saveNotification(title, message, type, petId)
        }
    }

    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class FirebaseUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)
