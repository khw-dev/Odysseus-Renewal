package com.example.ppet.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ppet.R
import com.example.ppet.ui.theme.NotoSansKR
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import com.example.ppet.ui.theme.OrangePrimary
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    displayName: String?,
    profilePictureUrl: String?,
    onLoadingComplete: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        delay(3000L)
        onLoadingComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        if (!profilePictureUrl.isNullOrEmpty()) {
            val imageRequest = ImageRequest.Builder(LocalContext.current)
                .data(profilePictureUrl)
                .crossfade(true)
                .build()

            AsyncImage(
                model = imageRequest,
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Default Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = OrangePrimary)) {
                    append("${displayName ?: "사용자"}")
                }
                append(" 님\n환영합니다!")
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansKR,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.weight(0.8f))

        Text(
            text = "정보를 불러오는 중입니다...",
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = NotoSansKR,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(bottom = 70.dp)
        )
    }
}
