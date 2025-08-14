package com.example.ppet.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GoogleSignInResult(
    val isSuccess: Boolean,
    val userInfo: UserInfo? = null,
    val errorMessage: String? = null
)

data class UserInfo(
    val id: String,
    val displayName: String?,
    val email: String?,
    val profilePictureUrl: String?
)

class GoogleSignInManager(private val context: Context) {
    private val _signInState = MutableStateFlow<GoogleSignInResult?>(null)
    val signInState: StateFlow<GoogleSignInResult?> = _signInState.asStateFlow()

    private val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            val userInfo = UserInfo(
                id = account.id ?: "",
                displayName = account.displayName,
                email = account.email,
                profilePictureUrl = account.photoUrl?.toString()
            )
            _signInState.value = GoogleSignInResult(isSuccess = true, userInfo = userInfo)
        } catch (e: ApiException) {
            _signInState.value = GoogleSignInResult(
                isSuccess = false,
                errorMessage = "Google Sign-In failed with error code: ${e.statusCode}"
            )
        }
    }

    fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            _signInState.value = null
        }
    }

    fun clearSession() {
        googleSignInClient.signOut()
        googleSignInClient.revokeAccess() // 완전한 세션 초기화
        _signInState.value = null
    }

    fun checkLastSignedIn() {
        // 기존 로그인 정보 확인하여 세션 유지
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            val userInfo = UserInfo(
                id = account.id ?: "",
                displayName = account.displayName,
                email = account.email,
                profilePictureUrl = account.photoUrl?.toString()
            )
            _signInState.value = GoogleSignInResult(isSuccess = true, userInfo = userInfo)
        } else {
            _signInState.value = null
        }
    }
}
