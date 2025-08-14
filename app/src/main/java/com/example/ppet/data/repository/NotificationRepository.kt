package com.example.ppet.data.repository

import com.example.ppet.data.model.Notification
import com.example.ppet.data.model.NotificationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: Flow<List<Notification>> = _notifications.asStateFlow()

    init {
        loadSampleNotifications()
    }

    suspend fun addNotification(notification: Notification) {
        val currentNotifications = _notifications.value.toMutableList()
        currentNotifications.add(0, notification) // 최신 알림을 앞에 추가
        _notifications.value = currentNotifications
    }

    suspend fun markAsRead(notificationId: String) {
        val currentNotifications = _notifications.value.toMutableList()
        val index = currentNotifications.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            currentNotifications[index] = currentNotifications[index].copy(isRead = true)
            _notifications.value = currentNotifications
        }
    }

    suspend fun markAllAsRead() {
        val currentNotifications = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = currentNotifications
    }

    suspend fun deleteNotification(notificationId: String) {
        val currentNotifications = _notifications.value.toMutableList()
        currentNotifications.removeAll { it.id == notificationId }
        _notifications.value = currentNotifications
    }

    suspend fun getUnreadCount(): Int {
        return _notifications.value.count { !it.isRead }
    }

    // 반려동물 관련 자동 알림 생성
    suspend fun createVaccinationReminder(petId: String, petName: String, vaccinationType: String, dueDate: Date) {
        val notification = Notification(
            id = UUID.randomUUID().toString(),
            title = "예방접종 알림",
            message = "${petName}의 ${vaccinationType} 예방접종이 필요합니다.",
            type = NotificationType.VACCINATION_REMINDER,
            petId = petId,
            actionData = vaccinationType
        )
        addNotification(notification)
    }

    suspend fun createCheckupReminder(petId: String, petName: String) {
        val notification = Notification(
            id = UUID.randomUUID().toString(),
            title = "건강검진 알림",
            message = "${petName}의 정기 건강검진 시기입니다.",
            type = NotificationType.CHECKUP_REMINDER,
            petId = petId
        )
        addNotification(notification)
    }

    private fun loadSampleNotifications() {
        val sampleNotifications = listOf(
            Notification(
                id = "1",
                title = "예방접종 알림",
                message = "초코의 광견병 예방접종이 다음 주에 예정되어 있습니다.",
                type = NotificationType.VACCINATION_REMINDER,
                petId = "1"
            ),
            Notification(
                id = "2",
                title = "건강 팁",
                message = "겨울철 반려동물 관리 요령을 확인해보세요.",
                type = NotificationType.HEALTH_TIP,
                isRead = true
            ),
            Notification(
                id = "3",
                title = "예약 알림",
                message = "내일 오후 2시 나비의 건강검진 예약이 있습니다.",
                type = NotificationType.APPOINTMENT_REMINDER,
                petId = "2"
            )
        )

        _notifications.value = sampleNotifications
    }
}
