package com.example.ppet.ui.home

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
class HomeViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {

    private val _currentCharacter = MutableStateFlow<PetCharacter?>(null)
    val currentCharacter: StateFlow<PetCharacter?> = _currentCharacter.asStateFlow()

    init {
        loadCurrentCharacter()
    }

    private fun loadCurrentCharacter() {
        viewModelScope.launch {
            val character = userRepository.getCurrentCharacter()
            _currentCharacter.value = character
        }
    }

    fun refreshCharacter() {
        loadCurrentCharacter()
    }
}
