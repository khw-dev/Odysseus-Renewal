package com.example.ppet.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    onBack: () -> Unit
) {
    var isDarkMode by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("한국어") }
    var autoBackup by remember { mutableStateOf(true) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "앱 설정",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // 화면 설정
                AppSettingSection(
                    title = "화면 설정",
                    items = listOf(
                        AppSettingToggleItem(
                            title = "다크 모드",
                            description = "어두운 테마를 사용합니다",
                            icon = Icons.Default.Settings,
                            isEnabled = isDarkMode,
                            onToggle = { isDarkMode = it }
                        )
                    )
                )
            }

            item {
                // 언어 및 지역
                AppSettingSection(
                    title = "언어 및 지역",
                    items = listOf(
                        AppSettingClickItem(
                            title = "언어 설정",
                            description = selectedLanguage,
                            icon = Icons.Default.Settings,
                            onClick = { showLanguageDialog = true }
                        )
                    )
                )
            }

            item {
                // 데이터 및 저장소
                AppSettingSection(
                    title = "데이터 및 저장소",
                    items = listOf(
                        AppSettingToggleItem(
                            title = "자동 백업",
                            description = "데이터를 자동으로 백업합니다",
                            icon = Icons.Default.Settings,
                            isEnabled = autoBackup,
                            onToggle = { autoBackup = it }
                        ),
                        AppSettingClickItem(
                            title = "캐시 삭제",
                            description = "임시 파일을 삭제하여 공간을 확보합니다",
                            icon = Icons.Default.Settings,
                            onClick = { /* TODO: 캐시 삭제 기능 */ }
                        )
                    )
                )
            }

            item {
                // 보안 및 개인정보
                AppSettingSection(
                    title = "보안 및 개인정보",
                    items = listOf(
                        AppSettingClickItem(
                            title = "개인정보 처리방침",
                            description = "개인정보 처리방침을 확인합니다",
                            icon = Icons.Default.Settings,
                            onClick = { /* TODO: 개인정보 처리방침 */ }
                        ),
                        AppSettingClickItem(
                            title = "서비스 이용약관",
                            description = "서비스 이용약관을 확인합니다",
                            icon = Icons.Default.Settings,
                            onClick = { /* TODO: 서비스 이용약관 */ }
                        )
                    )
                )
            }

            item {
                // 앱 정보
                AppSettingSection(
                    title = "앱 정보",
                    items = listOf(
                        AppSettingClickItem(
                            title = "버전 정보",
                            description = "앱 버전 1.0.0",
                            icon = Icons.Default.Info,
                            onClick = { showAboutDialog = true }
                        ),
                        AppSettingClickItem(
                            title = "업데이트 확인",
                            description = "최신 버전을 확인합니다",
                            icon = Icons.Default.Info,
                            onClick = { /* TODO: 업데이트 확인 */ }
                        )
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8F9FA)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "⚙️ 설정 안내",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = NotoSansKR,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• 일부 설정은 앱을 재시작한 후 적용됩니다\n• 자동 백업 기능으로 데이터 손실을 방지할 수 있습니다\n• 문의사항이 있으시면 고객센터로 연락해주세요",
                            fontSize = 12.sp,
                            fontFamily = NotoSansKR,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }

    // 언어 선택 다이얼로그
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = "언어 선택",
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium
                )
            },
            text = {
                Column {
                    val languages = listOf("한국어", "English", "日本語")
                    languages.forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = language
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == language,
                                onClick = {
                                    selectedLanguage = language
                                    showLanguageDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = OrangePrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = language,
                                fontFamily = NotoSansKR
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showLanguageDialog = false }
                ) {
                    Text(
                        text = "취소",
                        color = Color.Gray,
                        fontFamily = NotoSansKR
                    )
                }
            }
        )
    }

    // 앱 정보 다이얼로그
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    text = "PPET",
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "버전: 1.0.0\n빌드: 20250814\n\n반려동물과 함께하는 건강한 일상을 위한 앱입니다.",
                        fontFamily = NotoSansKR,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showAboutDialog = false }
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

@Composable
private fun AppSettingSection(
    title: String,
    items: List<Any>
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
                    when (item) {
                        is AppSettingToggleItem -> {
                            AppSettingToggleRow(
                                item = item,
                                showDivider = index < items.size - 1
                            )
                        }
                        is AppSettingClickItem -> {
                            AppSettingClickRow(
                                item = item,
                                showDivider = index < items.size - 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppSettingToggleRow(
    item: AppSettingToggleItem,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = NotoSansKR,
                    color = Color.Black
                )

                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    fontFamily = NotoSansKR,
                    color = Color.Gray
                )
            }

            Switch(
                checked = item.isEnabled,
                onCheckedChange = item.onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = OrangePrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }

        if (showDivider) {
            Divider(
                color = Color(0xFFF0F0F0),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun AppSettingClickRow(
    item: AppSettingClickItem,
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
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = NotoSansKR,
                        color = Color.Black
                    )

                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        fontFamily = NotoSansKR,
                        color = Color.Gray
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "화살표",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (showDivider) {
            Divider(
                color = Color(0xFFF0F0F0),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

data class AppSettingToggleItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isEnabled: Boolean,
    val onToggle: (Boolean) -> Unit
)

data class AppSettingClickItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)
