package com.example.ppet.data.model

import java.util.Date

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val petId: String? = null,
    val actionData: String? = null, // 추가 액션 데이터 (예: 예약 ID 등)
    val createdAt: Date = Date()
)

enum class NotificationType {
    VACCINATION_REMINDER,    // 예방접종 알림
    CHECKUP_REMINDER,       // 검진 알림
    MEDICATION_REMINDER,    // 투약 알림
    APPOINTMENT_REMINDER,   // 예약 알림
    HEALTH_TIP,            // 건강 팁
    GENERAL               // 일반 알림
}
