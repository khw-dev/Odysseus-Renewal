package com.example.ppet.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppet.data.model.Pet
import com.example.ppet.data.model.HealthRecord
import com.example.ppet.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets.asStateFlow()

    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = _selectedPet.asStateFlow()

    private val _healthRecords = MutableStateFlow<List<HealthRecord>>(emptyList())
    val healthRecords: StateFlow<List<HealthRecord>> = _healthRecords.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPets()
    }

    private fun loadPets() {
        viewModelScope.launch {
            petRepository.pets.collect { petList ->
                _pets.value = petList
            }
        }
    }

    fun selectPet(pet: Pet) {
        _selectedPet.value = pet
        loadHealthRecordsForPet(pet.id)
    }

    private fun loadHealthRecordsForPet(petId: String) {
        viewModelScope.launch {
            val records = petRepository.getHealthRecordsForPet(petId)
            _healthRecords.value = records
        }
    }

    fun addPet(pet: Pet) {
        viewModelScope.launch {
            petRepository.addPet(pet)
        }
    }

    fun addHealthRecord(healthRecord: HealthRecord) {
        viewModelScope.launch {
            petRepository.addHealthRecord(healthRecord)
            // 현재 선택된 펫의 건강 기록 다시 로드
            _selectedPet.value?.let { pet ->
                loadHealthRecordsForPet(pet.id)
            }
        }
    }

    fun updateHealthRecord(healthRecord: HealthRecord) {
        viewModelScope.launch {
            petRepository.updateHealthRecord(healthRecord)
            // 현재 선택된 펫의 건강 기록 다시 로드
            _selectedPet.value?.let { pet ->
                loadHealthRecordsForPet(pet.id)
            }
        }
    }

    fun deleteHealthRecord(healthRecordId: String) {
        viewModelScope.launch {
            petRepository.deleteHealthRecord(healthRecordId)
            // 현재 선택된 펫의 건강 기록 다시 로드
            _selectedPet.value?.let { pet ->
                loadHealthRecordsForPet(pet.id)
            }
        }
    }
}
