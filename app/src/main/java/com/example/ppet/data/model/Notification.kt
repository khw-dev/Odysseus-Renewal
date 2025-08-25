package com.example.ppet.data.model

import java.util.Date

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val petId: String? = null,
    val actionData: String? = null, 
    val createdAt: Date = Date()
)

enum class NotificationType {
    VACCINATION_REMINDER,    
    CHECKUP_REMINDER,       
    MEDICATION_REMINDER,    
    APPOINTMENT_REMINDER,   
    HEALTH_TIP,            
    GENERAL               
}
