package com.example.ppet.data.model

import java.util.Date

data class Pet(
    val id: String,
    val name: String,
    val type: String, // 강아지, 고양이 등
    val breed: String? = null, // 품종
    val age: Int,
    val weight: Double? = null,
    val gender: String? = null, // 수컷, 암컷
    val birthDate: Date? = null,
    val imageUrl: String? = null,
    val characterId: String? = null, // 선택된 캐릭터 ID 추가
    val healthRecords: List<HealthRecord> = emptyList(),
    val isNeutered: Boolean = false,
    val allergies: List<String> = emptyList(),
    val notes: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

data class HealthRecord(
    val id: String,
    val petId: String,
    val type: HealthRecordType,
    val title: String,
    val description: String? = null,
    val date: Date,
    val veterinaryClinic: String? = null,
    val nextAppointment: Date? = null,
    val medication: String? = null,
    val cost: Double? = null,
    val notes: String? = null,
    val createdAt: Date = Date()
)

enum class HealthRecordType {
    VACCINATION,     // 예방접종
    CHECKUP,        // 건강검진
    TREATMENT,      // 치료
    MEDICATION,     // 투약
    SURGERY,        // 수술
    DENTAL,         // 치과
    EMERGENCY,      // 응급처치
    OTHER          // 기타
}
