package com.example.ppet.ui.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppet.data.model.Pet
import com.example.ppet.model.PetCharacter
import com.example.ppet.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetCharacterSelectionViewModel @Inject constructor(
    private val petRepository: PetRepository
) : ViewModel() {

    private val _selectedCharacter = MutableStateFlow<PetCharacter?>(null)
    val selectedCharacter: StateFlow<PetCharacter?> = _selectedCharacter.asStateFlow()

    fun savePetCharacter(pet: Pet, character: PetCharacter) {
        viewModelScope.launch {
            val updatedPet = pet.copy(characterId = character.id)
            petRepository.updatePet(updatedPet)
            _selectedCharacter.value = character
        }
    }

    fun getPetCharacter(pet: Pet): PetCharacter? {
        return if (pet.characterId != null) {
            com.example.ppet.model.PetCharacters.availableCharacters.find {
                it.id == pet.characterId
            }
        } else {
            null
        }
    }
}
