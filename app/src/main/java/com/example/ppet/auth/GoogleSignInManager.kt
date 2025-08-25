package com.example.ppet.auth

import android.content.Context
import android.content.Intent
import com.example.ppet.data.repository.PpetRepository
import com.example.ppet.utils.FirebaseConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

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

@Singleton
class GoogleSignInManager @Inject constructor(
    private val context: Context,
    private val repository: PpetRepository
) {
    private val _signInState = MutableStateFlow<GoogleSignInResult?>(null)
    val signInState: StateFlow<GoogleSignInResult?> = _signInState.asStateFlow()

    private val googleSignInClient: GoogleSignInClient
    private val firebaseAuth = FirebaseAuth.getInstance()

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(FirebaseConfig.WEB_CLIENT_ID) 
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    suspend fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)

            
            println("Google Sign-In Success: ${account.email}")
            println("ID Token: ${account.idToken}")

            
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()

            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val userInfo = UserInfo(
                    id = firebaseUser.uid,
                    displayName = firebaseUser.displayName,
                    email = firebaseUser.email,
                    profilePictureUrl = firebaseUser.photoUrl?.toString()
                )

                
                saveUserInfoToFirebase(userInfo)

                _signInState.value = GoogleSignInResult(isSuccess = true, userInfo = userInfo)
            } else {
                _signInState.value = GoogleSignInResult(
                    isSuccess = false,
                    errorMessage = "Firebase authentication failed"
                )
            }
        } catch (e: ApiException) {
            
            val errorMessage = when (e.statusCode) {
                10 -> "개발자 오류: Google Sign-In 설정을 확인해주세요. SHA-1 인증서나 클라이언트 ID를 확인하세요."
                7 -> "네트워크 오류: 인터넷 연결을 확인해주세요."
                8 -> "내부 오류가 발생했습니다."
                12500 -> "Google Play 서비스가 최신 버전이 아닙니다."
                12501 -> "사용자가 로그인을 취소했습니다."
                else -> "Google Sign-In failed with error code: ${e.statusCode}"
            }

            println("Google Sign-In Error: Code ${e.statusCode}, Message: $errorMessage")

            _signInState.value = GoogleSignInResult(
                isSuccess = false,
                errorMessage = errorMessage
            )
        } catch (e: Exception) {
            println("General Error: ${e.message}")
            _signInState.value = GoogleSignInResult(
                isSuccess = false,
                errorMessage = "Authentication failed: ${e.message}"
            )
        }
    }

    private fun saveUserInfoToFirebase(userInfo: UserInfo) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val firebaseUserInfo = com.example.ppet.data.model.UserInfo(
                    displayName = userInfo.displayName,
                    email = userInfo.email,
                    profilePictureUrl = userInfo.profilePictureUrl
                )
                repository.saveUserInfo(firebaseUserInfo)
            } catch (e: Exception) {
                
                println("Failed to save user info to Firebase: ${e.message}")
            }
        }
    }

    fun signOut() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseAuth.signOut()
                googleSignInClient.signOut().await()
                _signInState.value = null
            } catch (e: Exception) {
                println("Sign out failed: ${e.message}")
            }
        }
    }

    fun clearSession() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firebaseAuth.signOut()
                googleSignInClient.signOut().await()
                googleSignInClient.revokeAccess().await() 
                _signInState.value = null
            } catch (e: Exception) {
                println("Clear session failed: ${e.message}")
            }
        }
    }

    fun checkLastSignedIn() {
        
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            val userInfo = UserInfo(
                id = firebaseUser.uid,
                displayName = firebaseUser.displayName,
                email = firebaseUser.email,
                profilePictureUrl = firebaseUser.photoUrl?.toString()
            )
            _signInState.value = GoogleSignInResult(isSuccess = true, userInfo = userInfo)
        } else {
            _signInState.value = null
        }
    }
}
