package com.example.ppet.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ppet.data.model.Notification
import com.example.ppet.data.model.NotificationType
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    notifications: List<Notification>,
    onBack: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onDeleteNotification: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "알림",
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
                    if (notifications.any { !it.isRead }) {
                        TextButton(onClick = onMarkAllAsRead) {
                            Text(
                                text = "모두 읽음",
                                fontFamily = NotoSansKR,
                                color = OrangePrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.Gray.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "알림이 없습니다",
                    fontFamily = NotoSansKR,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "새로운 알림이 있으면 여기에 표시됩니다",
                    fontFamily = NotoSansKR,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = {
                            onNotificationClick(notification)
                            if (!notification.isRead) {
                                onMarkAsRead(notification.id)
                            }
                        },
                        onMarkAsRead = { onMarkAsRead(notification.id) },
                        onDelete = { onDeleteNotification(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MM월 dd일 HH:mm", Locale.getDefault())

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (notification.isRead) Color.White else OrangePrimary.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (notification.isRead) Color(0xFFF0F0F0) else OrangePrimary.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            
            if (!notification.isRead) {
                Surface(
                    modifier = Modifier.size(8.dp),
                    shape = CircleShape,
                    color = OrangePrimary
                ) {}
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                Spacer(modifier = Modifier.width(20.dp))
            }

            
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = getNotificationTypeColor(notification.type).copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = getNotificationTypeIcon(notification.type),
                    contentDescription = null,
                    tint = getNotificationTypeColor(notification.type),
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    fontFamily = NotoSansKR,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontFamily = NotoSansKR,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = dateFormatter.format(notification.createdAt),
                    fontFamily = NotoSansKR,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            
            Column {
                if (!notification.isRead) {
                    IconButton(
                        onClick = onMarkAsRead,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "읽음 표시",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun getNotificationTypeIcon(type: NotificationType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        NotificationType.VACCINATION_REMINDER -> Icons.Default.Notifications
        NotificationType.CHECKUP_REMINDER -> Icons.Default.Notifications
        NotificationType.MEDICATION_REMINDER -> Icons.Default.Notifications
        NotificationType.APPOINTMENT_REMINDER -> Icons.Default.Notifications
        NotificationType.HEALTH_TIP -> Icons.Default.Notifications
        NotificationType.GENERAL -> Icons.Default.Notifications
    }
}

private fun getNotificationTypeColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.VACCINATION_REMINDER -> Color(0xFF4CAF50)
        NotificationType.CHECKUP_REMINDER -> Color(0xFF2196F3)
        NotificationType.MEDICATION_REMINDER -> Color(0xFF9C27B0)
        NotificationType.APPOINTMENT_REMINDER -> OrangePrimary
        NotificationType.HEALTH_TIP -> Color(0xFF00BCD4)
        NotificationType.GENERAL -> Color(0xFF607D8B)
    }
}
