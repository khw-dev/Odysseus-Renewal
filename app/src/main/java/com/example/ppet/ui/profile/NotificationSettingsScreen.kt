package com.example.ppet.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit
) {
    var pushNotifications by remember { mutableStateOf(true) }
    var healthReminders by remember { mutableStateOf(true) }
    var appointmentReminders by remember { mutableStateOf(true) }
    var vaccinationReminders by remember { mutableStateOf(true) }
    var medicationReminders by remember { mutableStateOf(false) }
    var weeklyReports by remember { mutableStateOf(true) }
    var marketingNotifications by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "알림 설정",
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
                NotificationSection(
                    title = "푸시 알림",
                    items = listOf(
                        NotificationItem(
                            title = "푸시 알림 허용",
                            description = "앱의 모든 알림을 받습니다",
                            isEnabled = pushNotifications,
                            onToggle = { pushNotifications = it }
                        )
                    )
                )
            }

            item {
                NotificationSection(
                    title = "건강 관리",
                    items = listOf(
                        NotificationItem(
                            title = "건강 체크 알림",
                            description = "정기적인 건강 체크 알림을 받습니다",
                            isEnabled = healthReminders && pushNotifications,
                            onToggle = { healthReminders = it },
                            enabled = pushNotifications
                        ),
                        NotificationItem(
                            title = "병원 예약 알림",
                            description = "예약된 병원 방문 하루 전에 알림을 받습니다",
                            isEnabled = appointmentReminders && pushNotifications,
                            onToggle = { appointmentReminders = it },
                            enabled = pushNotifications
                        ),
                        NotificationItem(
                            title = "예방접종 알림",
                            description = "예방접종 일정을 미리 알림으로 받습니다",
                            isEnabled = vaccinationReminders && pushNotifications,
                            onToggle = { vaccinationReminders = it },
                            enabled = pushNotifications
                        ),
                        NotificationItem(
                            title = "투약 알림",
                            description = "약물 복용 시간을 알림으로 받습니다",
                            isEnabled = medicationReminders && pushNotifications,
                            onToggle = { medicationReminders = it },
                            enabled = pushNotifications
                        )
                    )
                )
            }

            item {
                NotificationSection(
                    title = "리포트 및 혜택",
                    items = listOf(
                        NotificationItem(
                            title = "주간 리포트",
                            description = "주간 건강 리포트를 받습니다",
                            isEnabled = weeklyReports && pushNotifications,
                            onToggle = { weeklyReports = it },
                            enabled = pushNotifications
                        ),
                        NotificationItem(
                            title = "마케팅 알림",
                            description = "할인 혜택 및 이벤트 정보를 받습니다",
                            isEnabled = marketingNotifications && pushNotifications,
                            onToggle = { marketingNotifications = it },
                            enabled = pushNotifications
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
                            text = "알림 설정 안내",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = NotoSansKR,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "- 푸시 알림을 비활성화하면 모든 알림이 차단됩니다\n- 중요한 건강 관련 알림은 활성화하는 것을 권장합니다\n- 시스템 설정에서도 알림을 관리할 수 있습니다",
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
}

@Composable
private fun NotificationSection(
    title: String,
    items: List<NotificationItem>
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
                    NotificationItemRow(
                        item = item,
                        showDivider = index < items.size - 1
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItemRow(
    item: NotificationItem,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = NotoSansKR,
                    color = if (item.enabled) Color.Black else Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    fontFamily = NotoSansKR,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }

            Switch(
                checked = item.isEnabled,
                onCheckedChange = item.onToggle,
                enabled = item.enabled,
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

data class NotificationItem(
    val title: String,
    val description: String,
    val isEnabled: Boolean,
    val onToggle: (Boolean) -> Unit,
    val enabled: Boolean = true
)
