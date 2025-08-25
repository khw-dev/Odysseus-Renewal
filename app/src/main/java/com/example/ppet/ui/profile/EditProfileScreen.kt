package com.example.ppet.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userName: String,
    userEmail: String,
    userProfileImage: String?,
    onBack: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var editedName by remember { mutableStateOf(userName) }
    var editedEmail by remember { mutableStateOf(userEmail) }
    var showImagePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "개인정보 수정",
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            onSave(editedName, editedEmail)
                            onBack()
                        },
                        enabled = editedName.isNotBlank() && editedEmail.isNotBlank()
                    ) {
                        Text(
                            text = "저장",
                            color = OrangePrimary,
                            fontFamily = NotoSansKR,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    if (!userProfileImage.isNullOrEmpty()) {
                        val imageRequest = ImageRequest.Builder(LocalContext.current)
                            .data(userProfileImage)
                            .crossfade(true)
                            .build()

                        AsyncImage(
                            model = imageRequest,
                            contentDescription = "프로필 이미지",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = OrangePrimary.copy(alpha = 0.1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "기본 프로필",
                                tint = OrangePrimary,
                                modifier = Modifier.padding(30.dp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.BottomEnd)
                            .clickable { showImagePicker = true },
                        shape = CircleShape,
                        color = OrangePrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "이미지 변경",
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "프로필 사진 변경",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = NotoSansKR
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("이름", fontFamily = NotoSansKR) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        focusedLabelColor = OrangePrimary
                    )
                )

                OutlinedTextField(
                    value = editedEmail,
                    onValueChange = { editedEmail = it },
                    label = { Text("이메일", fontFamily = NotoSansKR) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        focusedLabelColor = OrangePrimary
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onSave(editedName, editedEmail)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                enabled = editedName.isNotBlank() && editedEmail.isNotBlank()
            ) {
                Text(
                    text = "저장하기",
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }

    if (showImagePicker) {
        AlertDialog(
            onDismissRequest = { showImagePicker = false },
            title = {
                Text(
                    text = "프로필 사진 변경",
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium
                )
            },
            text = {
                Text(
                    text = "아직 프로필 사진 변경 기능을 지원하지 않습니다.",
                    fontFamily = NotoSansKR
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showImagePicker = false }
                ) {
                    Text(
                        text = "확인",
                        color = OrangePrimary,
                        fontFamily = NotoSansKR
                    )
                }
            }
        )
    }
}
