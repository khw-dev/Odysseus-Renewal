package com.example.ppet.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppet.notification.NotificationManager
import com.example.ppet.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationSettingsUiState(
    val hasPermission: Boolean = false,
    val isNotificationEnabled: Boolean = true,
    val notificationInterval: Int = 2 
)

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val notificationManager: NotificationManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    init {
        checkPermissionStatus()
        loadNotificationSettings()
    }

    private fun checkPermissionStatus() {
        val hasPermission = notificationManager.hasNotificationPermission()
        _uiState.value = _uiState.value.copy(hasPermission = hasPermission)
    }

    private fun loadNotificationSettings() {
        val isEnabled = userRepository.isNotificationEnabled()
        _uiState.value = _uiState.value.copy(isNotificationEnabled = isEnabled)
    }

    fun toggleNotification(enabled: Boolean) {
        userRepository.setNotificationEnabled(enabled)
        _uiState.value = _uiState.value.copy(isNotificationEnabled = enabled)

        if (enabled) {
            notificationManager.startNotificationService()
        } else {
            notificationManager.stopNotificationService()
        }
    }

    fun requestNotificationPermission() {
        checkPermissionStatus()
    }

    fun sendTestNotification() {
        if (_uiState.value.hasPermission && _uiState.value.isNotificationEnabled) {
            notificationManager.startNotificationService()
        }
    }

    fun updatePermissionStatus(hasPermission: Boolean) {
        _uiState.value = _uiState.value.copy(hasPermission = hasPermission)

        if (hasPermission && _uiState.value.isNotificationEnabled) {
            notificationManager.startNotificationService()
        }
    }
}
