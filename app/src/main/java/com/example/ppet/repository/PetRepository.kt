package com.example.ppet.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.ppet.data.model.Pet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "ppet_pets_prefs", Context.MODE_PRIVATE
    )
    private val gson = Gson()

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: Flow<List<Pet>> = _pets.asStateFlow()

    companion object {
        private const val KEY_PETS = "pets_list"
    }

    init {
        loadPets()
    }

    private fun loadPets() {
        val petsJson = sharedPreferences.getString(KEY_PETS, null)
        if (petsJson != null) {
            try {
                val type = object : TypeToken<List<Pet>>() {}.type
                val pets = gson.fromJson<List<Pet>>(petsJson, type)
                _pets.value = pets
            } catch (e: Exception) {
                _pets.value = emptyList()
            }
        }
    }

    private fun savePets() {
        val petsJson = gson.toJson(_pets.value)
        sharedPreferences.edit()
            .putString(KEY_PETS, petsJson)
            .apply()
    }

    suspend fun addPet(pet: Pet) {
        val currentPets = _pets.value.toMutableList()
        currentPets.add(pet)
        _pets.value = currentPets
        savePets()
    }

    suspend fun updatePet(pet: Pet) {
        val currentPets = _pets.value.toMutableList()
        val index = currentPets.indexOfFirst { it.id == pet.id }
        if (index != -1) {
            currentPets[index] = pet
            _pets.value = currentPets
            savePets()
        }
    }

    suspend fun deletePet(petId: String) {
        val currentPets = _pets.value.toMutableList()
        currentPets.removeAll { it.id == petId }
        _pets.value = currentPets
        savePets()
    }

    suspend fun getPetById(petId: String): Pet? {
        return _pets.value.find { it.id == petId }
    }

    suspend fun getAllPets(): List<Pet> {
        return _pets.value
    }

    suspend fun getHealthRecordsForPet(petId: String): List<com.example.ppet.data.model.HealthRecord> {
        
        
        return emptyList()
    }

    suspend fun addHealthRecord(healthRecord: com.example.ppet.data.model.HealthRecord) {
        
        
    }

    suspend fun updateHealthRecord(healthRecord: com.example.ppet.data.model.HealthRecord) {
        
        
    }

    suspend fun deleteHealthRecord(healthRecordId: String) {
        
        
    }
}
