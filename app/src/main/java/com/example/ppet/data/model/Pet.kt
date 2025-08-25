package com.example.ppet.data.model

import java.util.Date

data class Pet(
    val id: String,
    val name: String,
    val type: String, 
    val breed: String? = null, 
    val age: Int,
    val weight: Double? = null,
    val gender: String? = null, 
    val birthDate: Date? = null,
    val imageUrl: String? = null,
    val characterId: String? = null, 
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
    VACCINATION,     
    CHECKUP,        
    TREATMENT,      
    MEDICATION,     
    SURGERY,        
    DENTAL,         
    EMERGENCY,      
    OTHER          
}
