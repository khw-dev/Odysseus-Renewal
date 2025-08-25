package com.example.ppet.data.model

import com.google.firebase.database.PropertyName
import java.util.*


data class UserInfo(
    val displayName: String? = null,
    val email: String? = null,
    val profilePictureUrl: String? = null
)


data class FirebaseUser(
    @PropertyName("id") val id: String = "",
    @PropertyName("displayName") val displayName: String? = null,
    @PropertyName("email") val email: String? = null,
    @PropertyName("profilePictureUrl") val profilePictureUrl: String? = null,
    @PropertyName("createdAt") val createdAt: Long = System.currentTimeMillis(),
    @PropertyName("updatedAt") val updatedAt: Long = System.currentTimeMillis(),
    @PropertyName("totalExp") val totalExp: Int = 0,
    @PropertyName("level") val level: Int = 1,
    @PropertyName("coins") val coins: Int = 100,
    @PropertyName("selectedPetCharacterId") val selectedPetCharacterId: String? = null
)


data class FirebasePet(
    @PropertyName("id") val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("type") val type: String = "",
    @PropertyName("breed") val breed: String? = null,
    @PropertyName("age") val age: Int = 0,
    @PropertyName("weight") val weight: Double? = null,
    @PropertyName("gender") val gender: String? = null,
    @PropertyName("birthDate") val birthDate: Long? = null,
    @PropertyName("imageUrl") val imageUrl: String? = null,
    @PropertyName("characterId") val characterId: String? = null,
    @PropertyName("isNeutered") val isNeutered: Boolean = false,
    @PropertyName("allergies") val allergies: List<String> = emptyList(),
    @PropertyName("notes") val notes: String? = null,
    @PropertyName("createdAt") val createdAt: Long = System.currentTimeMillis(),
    @PropertyName("updatedAt") val updatedAt: Long = System.currentTimeMillis(),
    @PropertyName("ownerId") val ownerId: String = ""
)


data class FirebaseHealthRecord(
    @PropertyName("id") val id: String = "",
    @PropertyName("petId") val petId: String = "",
    @PropertyName("type") val type: String = "",
    @PropertyName("title") val title: String = "",
    @PropertyName("description") val description: String? = null,
    @PropertyName("date") val date: Long = System.currentTimeMillis(),
    @PropertyName("veterinaryClinic") val veterinaryClinic: String? = null,
    @PropertyName("nextAppointment") val nextAppointment: Long? = null,
    @PropertyName("medication") val medication: String? = null,
    @PropertyName("cost") val cost: Double? = null,
    @PropertyName("notes") val notes: String? = null,
    @PropertyName("createdAt") val createdAt: Long = System.currentTimeMillis()
)


data class FirebaseQuest(
    @PropertyName("id") val id: String = "",
    @PropertyName("title") val title: String = "",
    @PropertyName("description") val description: String = "",
    @PropertyName("category") val category: String = "",
    @PropertyName("type") val type: String = "",
    @PropertyName("targetValue") val targetValue: Int = 0,
    @PropertyName("currentProgress") val currentProgress: Int = 0,
    @PropertyName("unit") val unit: String = "",
    @PropertyName("rewardExp") val rewardExp: Int = 0,
    @PropertyName("rewardCoins") val rewardCoins: Int = 0,
    @PropertyName("startDate") val startDate: Long = System.currentTimeMillis(),
    @PropertyName("endDate") val endDate: Long = System.currentTimeMillis(),
    @PropertyName("status") val status: String = "",
    @PropertyName("isAutoDetectable") val isAutoDetectable: Boolean = false,
    @PropertyName("petId") val petId: String? = null,
    @PropertyName("userId") val userId: String = ""
)


data class FirebaseNotification(
    @PropertyName("id") val id: String = "",
    @PropertyName("title") val title: String = "",
    @PropertyName("message") val message: String = "",
    @PropertyName("type") val type: String = "",
    @PropertyName("isRead") val isRead: Boolean = false,
    @PropertyName("timestamp") val timestamp: Long = System.currentTimeMillis(),
    @PropertyName("userId") val userId: String = "",
    @PropertyName("petId") val petId: String? = null,
    @PropertyName("actionData") val actionData: Map<String, String>? = null
)


data class FirebaseActivityRecord(
    @PropertyName("id") val id: String = "",
    @PropertyName("petId") val petId: String = "",
    @PropertyName("userId") val userId: String = "",
    @PropertyName("type") val type: String = "", 
    @PropertyName("duration") val duration: Int? = null, 
    @PropertyName("distance") val distance: Double? = null, 
    @PropertyName("notes") val notes: String? = null,
    @PropertyName("timestamp") val timestamp: Long = System.currentTimeMillis(),
    @PropertyName("location") val location: String? = null,
    @PropertyName("weatherCondition") val weatherCondition: String? = null
)
