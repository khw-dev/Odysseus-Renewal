package com.example.ppet.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ppet.R
import com.example.ppet.auth.GoogleSignInManager
import com.example.ppet.ui.theme.NotoSansKR
import kotlinx.coroutines.delay

enum class AppState {
    SPLASH,
    LOGIN,
    LOADING,
    MAIN
}

@Composable
fun SplashScreen() {
    var appState by remember { mutableStateOf(AppState.SPLASH) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val googleSignInManager = remember { GoogleSignInManager(context) }
    val signInState by googleSignInManager.signInState.collectAsState()

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        googleSignInManager.handleSignInResult(result.data)
    }

    LaunchedEffect(signInState) {
        isLoading = false
        if (signInState?.isSuccess == true) {
            appState = AppState.LOADING
        } else if (signInState?.isSuccess == false) {
            Toast.makeText(context, signInState?.errorMessage, Toast.LENGTH_SHORT).show()
            appState = AppState.LOGIN
        }
    }

    LaunchedEffect(Unit) {
        delay(1500L)

        googleSignInManager.checkLastSignedIn()

        delay(500L)
        if (signInState?.isSuccess == true) {
            appState = AppState.LOADING
        } else {
            appState = AppState.LOGIN
        }
    }

    when (appState) {
        AppState.SPLASH -> {
            SplashContent()
        }
        AppState.LOGIN -> {
            LoginContent(
                isLoading = isLoading,
                onLoginClick = {
                    if (!isLoading) {
                        isLoading = true
                        val signInIntent = googleSignInManager.getSignInIntent()
                        signInLauncher.launch(signInIntent)
                    }
                }
            )
        }
        AppState.LOADING -> {
            signInState?.userInfo?.let { userInfo ->
                LoadingScreen(
                    displayName = userInfo.displayName,
                    profilePictureUrl = userInfo.profilePictureUrl,
                    onLoadingComplete = {
                        appState = AppState.MAIN
                    }
                )
            }
        }
        AppState.MAIN -> {
            signInState?.userInfo?.let { userInfo ->
                MainTabScreen(
                    userInfo = userInfo,
                    onSignOut = {
                        googleSignInManager.signOut()
                        appState = AppState.LOGIN
                    }
                )
            }
        }
    }
}

@Composable
private fun SplashContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo"
        )
    }
}

@Composable
private fun LoginContent(
    isLoading: Boolean,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn()
            ) {
                Card(
                    modifier = Modifier
                        .width(320.dp)
                        .height(55.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onLoginClick()
                        },
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(
                        width = 2.dp,
                        color = Color(0xFFF7F7F7)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google_logo),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isLoading) "로그인 중..." else "구글 로그인",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontFamily = NotoSansKR,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
