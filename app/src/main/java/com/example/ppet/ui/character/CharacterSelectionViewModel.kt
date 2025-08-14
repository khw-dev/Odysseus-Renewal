package com.example.ppet.ui.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppet.model.PetCharacter
import com.example.ppet.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterSelectionViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _selectedCharacter = MutableStateFlow<PetCharacter?>(null)
    val selectedCharacter: StateFlow<PetCharacter?> = _selectedCharacter.asStateFlow()

    fun selectCharacter(character: PetCharacter) {
        viewModelScope.launch {
            _selectedCharacter.value = character
            userRepository.saveSelectedCharacter(character)
        }
    }

    fun loadCurrentCharacter() {
        viewModelScope.launch {
            val character = userRepository.getCurrentCharacter()
            _selectedCharacter.value = character
        }
    }
}
