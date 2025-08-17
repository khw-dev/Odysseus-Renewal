package com.example.ppet.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppet.auth.GoogleSignInManager
import com.example.ppet.auth.GoogleSignInResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val googleSignInManager: GoogleSignInManager
) : ViewModel() {

    val signInState: StateFlow<GoogleSignInResult?> = googleSignInManager.signInState

    fun getSignInIntent(): Intent {
        return googleSignInManager.getSignInIntent()
    }

    suspend fun handleSignInResult(data: Intent?) {
        googleSignInManager.handleSignInResult(data)
    }

    fun checkLastSignedIn() {
        googleSignInManager.checkLastSignedIn()
    }

    fun signOut() {
        googleSignInManager.signOut()
    }

    fun clearSession() {
        googleSignInManager.clearSession()
    }
}
