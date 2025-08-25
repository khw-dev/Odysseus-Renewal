package com.example.ppet.data.repository

import com.example.ppet.data.model.Pet
import com.example.ppet.data.model.HealthRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class PetRepository {

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: Flow<List<Pet>> = _pets.asStateFlow()

    private val _healthRecords = MutableStateFlow<List<HealthRecord>>(emptyList())

    init {
        
        loadSampleData()
    }

    fun addPet(pet: Pet) {
        val currentPets = _pets.value.toMutableList()
        currentPets.add(pet)
        _pets.value = currentPets
    }

    fun updatePet(pet: Pet) {
        val currentPets = _pets.value.toMutableList()
        val index = currentPets.indexOfFirst { it.id == pet.id }
        if (index != -1) {
            currentPets[index] = pet.copy(updatedAt = Date())
            _pets.value = currentPets
        }
    }

    fun deletePet(petId: String) {
        val currentPets = _pets.value.toMutableList()
        currentPets.removeAll { it.id == petId }
        _pets.value = currentPets

        
        val currentRecords = _healthRecords.value.toMutableList()
        currentRecords.removeAll { it.petId == petId }
        _healthRecords.value = currentRecords
    }

    fun addHealthRecord(healthRecord: HealthRecord) {
        val currentRecords = _healthRecords.value.toMutableList()
        currentRecords.add(healthRecord)
        _healthRecords.value = currentRecords
    }

    fun getHealthRecordsForPet(petId: String): List<HealthRecord> {
        return _healthRecords.value.filter { it.petId == petId }
            .sortedByDescending { it.date }
    }

    fun updateHealthRecord(healthRecord: HealthRecord) {
        val currentRecords = _healthRecords.value.toMutableList()
        val index = currentRecords.indexOfFirst { it.id == healthRecord.id }
        if (index != -1) {
            currentRecords[index] = healthRecord
            _healthRecords.value = currentRecords
        }
    }

    fun deleteHealthRecord(recordId: String) {
        val currentRecords = _healthRecords.value.toMutableList()
        currentRecords.removeAll { it.id == recordId }
        _healthRecords.value = currentRecords
    }

    private fun loadSampleData() {
        val samplePets = listOf(
            Pet(
                id = "1",
                name = "초코",
                type = "강아지",
                breed = "골든 리트리버",
                age = 3,
                weight = 25.5,
                gender = "수컷",
                isNeutered = true
            ),
            Pet(
                id = "2",
                name = "나비",
                type = "고양이",
                breed = "러시안 블루",
                age = 2,
                weight = 4.2,
                gender = "암컷",
                isNeutered = true
            ),
            Pet(
                id = "3",
                name = "몽이",
                type = "강아지",
                breed = "포메라니안",
                age = 5,
                weight = 3.8,
                gender = "암컷",
                isNeutered = false
            )
        )

        _pets.value = samplePets
    }
}
