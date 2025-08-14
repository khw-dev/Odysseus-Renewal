package com.example.ppet.ui.quest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ppet.model.Quest
import com.example.ppet.model.QuestCategory
import com.example.ppet.model.QuestStatus
import com.example.ppet.model.QuestType
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary

data class QuestData(
    val id: String,
    val title: String,
    val description: String,
    val type: String,
    val points: Int,
    val currentProgress: Int,
    val maxProgress: Int,
    val isCompleted: Boolean = false,
    val isRewarded: Boolean = false,
    val iconType: String
)

@Composable
fun QuestScreen(
    modifier: Modifier = Modifier,
    viewModel: QuestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            ModernWalkTrackingCard(
                walkStats = uiState.walkStats,
                isWalkingActive = uiState.isWalkingActive,
                onStartWalk = { viewModel.startWalkTracking() },
                onStopWalk = { viewModel.stopWalkTracking() }
            )
        }

        item {
            ProgressStatsCard(
                todayWalkingTime = uiState.todayWalkingTime,
                weeklyWalkingTime = uiState.weeklyWalkingTime,
                completionRate = uiState.completionRate
            )
        }

        item {
            ModernCategoryFilter(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { viewModel.filterQuestsByCategory(it) }
            )
        }

        item {
            Text(
                text = "진행 중인 퀘스트",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = NotoSansKR,
                color = Color(0xFF1A1A1A)
            )
        }

        items(viewModel.getFilteredQuests()) { quest ->
            ModernQuestCard(
                quest = quest,
                onQuestClick = { /* 퀘스트 상세 화면으로 이동 */ },
                onCompleteClick = { viewModel.markQuestAsCompleted(quest.id) },
                onProgressUpdate = { progress ->
                    viewModel.updateQuestProgress(quest.id, progress)
                }
            )
        }

        if (uiState.completedQuests.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "완료된 퀘스트",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = NotoSansKR,
                    color = Color(0xFF1A1A1A)
                )
            }

            items(uiState.completedQuests.take(5)) { quest ->
                ModernCompletedQuestCard(quest = quest)
            }
        }
    }
}

@Composable
fun ModernWalkTrackingCard(
    walkStats: com.example.ppet.service.WalkStats,
    isWalkingActive: Boolean,
    onStartWalk: () -> Unit,
    onStopWalk: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isWalkingActive) Color(0xFF1A73E8) else Color.White,
        border = BorderStroke(1.dp, Color(0xFFE8E8E8))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "산책 추적",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = NotoSansKR,
                        color = if (isWalkingActive) Color.White else Color(0xFF1A1A1A)
                    )
                    Text(
                        text = if (isWalkingActive) "진행 중" else "시작하기",
                        fontSize = 14.sp,
                        fontFamily = NotoSansKR,
                        color = if (isWalkingActive) Color.White.copy(alpha = 0.8f) else Color(0xFF666666)
                    )
                }

                Surface(
                    modifier = Modifier.clickable {
                        if (isWalkingActive) onStopWalk() else onStartWalk()
                    },
                    shape = CircleShape,
                    color = if (isWalkingActive) Color.White else Color(0xFF1A73E8)
                ) {
                    Icon(
                        imageVector = if (isWalkingActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isWalkingActive) "중지" else "시작",
                        tint = if (isWalkingActive) Color(0xFF1A73E8) else Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (isWalkingActive) {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ModernWalkStat(
                        label = "거리",
                        value = "${(walkStats.totalDistance / 1000f).format(2)} km",
                        isActive = true
                    )
                    ModernWalkStat(
                        label = "시간",
                        value = "${(walkStats.duration / 60000)} 분",
                        isActive = true
                    )
                    ModernWalkStat(
                        label = "속도",
                        value = "${(walkStats.currentSpeed * 3.6f).format(1)} km/h",
                        isActive = true
                    )
                }
            }
        }
    }
}

@Composable
fun ModernWalkStat(label: String, value: String, isActive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) Color.White else Color(0xFF1A73E8)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isActive) Color.White.copy(alpha = 0.8f) else Color(0xFF666666)
        )
    }
}

@Composable
fun ProgressStatsCard(
    todayWalkingTime: Int,
    weeklyWalkingTime: Int,
    completionRate: Float
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE8E8E8))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "이번 주 진행 상황",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = NotoSansKR,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ModernProgressItem(
                    label = "오늘 산책",
                    value = "${todayWalkingTime}분",
                    target = "60분",
                    progress = (todayWalkingTime / 60f).coerceAtMost(1f),
                    color = Color(0xFF1A73E8)
                )
                ModernProgressItem(
                    label = "이번 주",
                    value = "${weeklyWalkingTime}분",
                    target = "1200분",
                    progress = (weeklyWalkingTime / 1200f).coerceAtMost(1f),
                    color = Color(0xFF34A853)
                )
                ModernProgressItem(
                    label = "완료율",
                    value = "${(completionRate * 100).toInt()}%",
                    target = "100%",
                    progress = completionRate,
                    color = Color(0xFFEA4335)
                )
            }
        }
    }
}

@Composable
fun ModernProgressItem(
    label: String,
    value: String,
    target: String,
    progress: Float,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(50.dp),
                color = color,
                strokeWidth = 4.dp,
                trackColor = color.copy(alpha = 0.1f)
            )
            Text(
                text = value,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "목표 $target",
            fontSize = 10.sp,
            color = Color(0xFF999999)
        )
    }
}

@Composable
fun ModernCategoryFilter(
    selectedCategory: QuestCategory?,
    onCategorySelected: (QuestCategory?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ModernCategoryChip(
                label = "전체",
                isSelected = selectedCategory == null,
                onClick = { onCategorySelected(null) }
            )
        }

        items(QuestCategory.values()) { category ->
            ModernCategoryChip(
                label = getCategoryDisplayName(category),
                isSelected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun ModernCategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) Color(0xFF1A73E8) else Color.Transparent,
        border = BorderStroke(
            1.dp,
            if (isSelected) Color(0xFF1A73E8) else Color(0xFFE8E8E8)
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            color = if (isSelected) Color.White else Color(0xFF666666),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ModernQuestCard(
    quest: Quest,
    onQuestClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onProgressUpdate: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onQuestClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE8E8E8))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(quest.category),
                            contentDescription = null,
                            tint = getCategoryColor(quest.category),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = quest.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = NotoSansKR,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                    Text(
                        text = quest.description,
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                ModernQuestTypeChip(quest.type)
            }

            Spacer(modifier = Modifier.height(16.dp))

            val progress = quest.currentProgress.toFloat() / quest.targetValue

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${quest.currentProgress}/${quest.targetValue} ${quest.unit}",
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 12.sp,
                        color = getCategoryColor(quest.category),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            Color(0xFFF0F0F0),
                            RoundedCornerShape(3.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                getCategoryColor(quest.category),
                                RoundedCornerShape(3.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8F9FA)
                    ) {
                        Text(
                            text = "${quest.rewardExp} EXP",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666)
                        )
                    }
                    if (quest.rewardCoins > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF3E0)
                        ) {
                            Text(
                                text = "${quest.rewardCoins} 코인",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFFF8F00)
                            )
                        }
                    }
                }

                if (quest.status != QuestStatus.COMPLETED && !quest.isAutoDetectable) {
                    Surface(
                        modifier = Modifier.clickable { onCompleteClick() },
                        shape = RoundedCornerShape(20.dp),
                        color = getCategoryColor(quest.category)
                    ) {
                        Text(
                            text = "완료",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernCompletedQuestCard(quest: Quest) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8F9FA),
        border = BorderStroke(1.dp, Color(0xFFE8E8E8))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF34A853)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "완료",
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp).size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "${quest.rewardExp} EXP, ${quest.rewardCoins} 코인 획득",
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }
        }
    }
}

@Composable
fun ModernQuestTypeChip(type: QuestType) {
    val (color, text) = when (type) {
        QuestType.DAILY -> Color(0xFF34A853) to "일일"
        QuestType.WEEKLY -> Color(0xFF1A73E8) to "주간"
        QuestType.MONTHLY -> Color(0xFF9C27B0) to "월간"
        QuestType.SPECIAL -> Color(0xFFEA4335) to "특별"
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

private fun getCategoryDisplayName(category: QuestCategory): String {
    return when (category) {
        QuestCategory.EXERCISE -> "운동"
        QuestCategory.CARE -> "돌봄"
        QuestCategory.FEEDING -> "급식"
        QuestCategory.HEALTH -> "건강"
        QuestCategory.SOCIAL -> "사회화"
        QuestCategory.LEARNING -> "학습"
    }
}

private fun getCategoryIcon(category: QuestCategory): ImageVector {
    return when (category) {
        QuestCategory.EXERCISE -> Icons.Default.DirectionsRun
        QuestCategory.CARE -> Icons.Default.Favorite
        QuestCategory.FEEDING -> Icons.Default.Restaurant
        QuestCategory.HEALTH -> Icons.Default.HealthAndSafety
        QuestCategory.SOCIAL -> Icons.Default.People
        QuestCategory.LEARNING -> Icons.Default.School
    }
}

private fun getCategoryColor(category: QuestCategory): Color {
    return when (category) {
        QuestCategory.EXERCISE -> Color(0xFF1A73E8)
        QuestCategory.CARE -> Color(0xFFEA4335)
        QuestCategory.FEEDING -> Color(0xFF34A853)
        QuestCategory.HEALTH -> Color(0xFF9C27B0)
        QuestCategory.SOCIAL -> Color(0xFFFF8F00)
        QuestCategory.LEARNING -> Color(0xFF00BCD4)
    }
}

private fun Float.format(digits: Int) = "%.${digits}f".format(this)
