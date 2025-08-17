package com.example.ppet.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Face // Pets 대신 Face 아이콘 사용
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary
import com.example.ppet.ui.character.CharacterSelectionScreen
import com.example.ppet.ui.settings.NotificationSettingsScreen
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userName: String? = null,
    userEmail: String? = null,
    userProfileImage: String? = null,
    onSignOut: () -> Unit = {}
) {
    var showEditProfile by remember { mutableStateOf(false) }
    var showNotificationSettings by remember { mutableStateOf(false) }
    var showCharacterSelection by remember { mutableStateOf(false) }
    var showPetCharacterSelection by remember { mutableStateOf(false) }
    var showAppSettings by remember { mutableStateOf(false) }
    var selectedPetForCharacter by remember { mutableStateOf<com.example.ppet.data.model.Pet?>(null) }
    var currentUserName by remember { mutableStateOf(userName ?: "사용자") }
    var currentUserEmail by remember { mutableStateOf(userEmail ?: "이메일 정보 없음") }

    // PetViewModel을 사용하여 펫 목록 가져오기
    val petViewModel: com.example.ppet.ui.home.viewmodel.PetViewModel = hiltViewModel()
    val pets by petViewModel.pets.collectAsState()

    if (showEditProfile) {
        EditProfileScreen(
            userName = currentUserName,
            userEmail = currentUserEmail,
            userProfileImage = userProfileImage,
            onBack = { showEditProfile = false },
            onSave = { newName, newEmail ->
                currentUserName = newName
                currentUserEmail = newEmail
                // TODO: 실제 구현에서는 서버에 저장
            }
        )
    } else if (showNotificationSettings) {
        NotificationSettingsScreen(
            onBack = { showNotificationSettings = false }
        )
    } else if (showCharacterSelection) {
        CharacterSelectionScreen(
            onCharacterSelected = { character ->
                showCharacterSelection = false
            },
            onBackClick = { showCharacterSelection = false }
        )
    } else if (showPetCharacterSelection && selectedPetForCharacter != null) {
        com.example.ppet.ui.pet.PetCharacterSelectionScreen(
            pet = selectedPetForCharacter!!,
            onCharacterSelected = { pet, character ->
                showPetCharacterSelection = false
                selectedPetForCharacter = null
            },
            onBackClick = {
                showPetCharacterSelection = false
                selectedPetForCharacter = null
            }
        )
    } else if (showAppSettings) {
        AppSettingsScreen(
            onBack = { showAppSettings = false }
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProfileHeader(
                    userName = currentUserName,
                    userEmail = currentUserEmail,
                    userProfileImage = userProfileImage
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                SettingsSection(
                    title = "펫 설정",
                    items = listOf(
                        SettingItem("전체 사용자 캐릭터", Icons.Default.Face) {
                            showCharacterSelection = true
                        }
                    )
                )
            }

            if (pets.isNotEmpty()) {
                item {
                    SettingsSection(
                        title = "반려동물별 캐릭터",
                        items = pets.map { pet ->
                            val petCharacter = pet.characterId?.let { characterId ->
                                com.example.ppet.model.PetCharacters.availableCharacters.find { it.id == characterId }
                            }
                            SettingItem(
                                title = "${pet.name}의 캐릭터",
                                subtitle = petCharacter?.name ?: "캐릭터 선택",
                                icon = Icons.Default.Face
                            ) {
                                selectedPetForCharacter = pet
                                showPetCharacterSelection = true
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                SettingsSection(
                    title = "계정 설정",
                    items = listOf(
                        SettingItem("개인정보 수정", Icons.Default.Person) {
                            showEditProfile = true
                        },
                        SettingItem("알림 설정", Icons.Default.Notifications) {
                            showNotificationSettings = true
                        },
                        SettingItem("앱 설정", Icons.Default.Settings) {
                            showAppSettings = true
                        }
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                SettingsSection(
                    title = "기타",
                    items = listOf(
                        SettingItem("로그아웃", Icons.AutoMirrored.Filled.ExitToApp, isLogout = true) {
                            onSignOut()
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    userEmail: String,
    userProfileImage: String?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8F9FA)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!userProfileImage.isNullOrEmpty()) {
                val imageRequest = ImageRequest.Builder(LocalContext.current)
                    .data(userProfileImage)
                    .crossfade(true)
                    .build()

                AsyncImage(
                    model = imageRequest,
                    contentDescription = "User Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = OrangePrimary.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Avatar",
                        tint = OrangePrimary,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = userName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = NotoSansKR,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = NotoSansKR,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    items: List<SettingItem>
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansKR,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    SettingItemRow(
                        item = item,
                        showDivider = index < items.size - 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingItemRow(
    item: SettingItem,
    showDivider: Boolean
) {
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { item.onClick() },
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = if (item.isLogout) Color(0xFFFF5722) else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = NotoSansKR,
                        color = if (item.isLogout) Color(0xFFFF5722) else Color.Black
                    )

                    item.subtitle?.let { subtitle ->
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = NotoSansKR,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                if (!item.isLogout) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "화살표",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (showDivider) {
            HorizontalDivider(
                color = Color(0xFFF0F0F0),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

data class SettingItem(
    val title: String,
    val icon: ImageVector,
    val subtitle: String? = null,
    val isLogout: Boolean = false,
    val onClick: () -> Unit
)
