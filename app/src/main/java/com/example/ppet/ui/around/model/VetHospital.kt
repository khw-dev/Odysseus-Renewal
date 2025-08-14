package com.example.ppet.ui.around.model

data class VetHospital(
    val id: String,
    val name: String,
    val address: String,
    val phoneNumber: String,
    val distance: Double, // km 단위
    val rating: Float,
    val isOpen: Boolean,
    val openHours: String,
    val imageUrl: String? = null,
    val specialties: List<String> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
