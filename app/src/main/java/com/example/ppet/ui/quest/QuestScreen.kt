package com.example.ppet.ui.quest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ppet.ui.quest.model.*
import com.example.ppet.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestScreen(
    modifier: Modifier = Modifier,
    viewModel: QuestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
                UserPointsCard(
                    userPoints = uiState.userPoints
                )
            }

            
            item {
                WalkTrackingCard(
                    walkStats = uiState.walkStats,
                    isWalkingActive = uiState.isWalkingActive,
                    onStartWalk = { viewModel.startWalkTracking() },
                    onStopWalk = { viewModel.stopWalkTracking() }
                )
            }

            
            item {
                CategoryFilterRow(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { viewModel.filterQuestsByCategory(it) }
                )
            }

            
            item {
                SectionTitle("진행 중인 퀘스트")
            }

            
            items(uiState.activeQuests) { quest ->
                QuestCard(
                    quest = quest,
                    onQuestClick = { /* 퀘스트 상세 화면으로 이동 */ },
                    onProgressUpdate = { progress ->
                        viewModel.updateQuestProgress(quest.id, progress)
                        if (progress >= quest.maxProgress) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("퀘스트를 완료했습니다! +${quest.points} 포인트")
                            }
                        }
                    },
                    onRewardClaim = {
                        viewModel.claimQuestReward(quest.id)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("보상을 획득했습니다!")
                        }
                    }
                )
            }

            
            if (uiState.completedQuests.isNotEmpty()) {
                item {
                    SectionTitle("최근 완료된 퀘스트")
                }

                items(uiState.completedQuests.take(5)) { quest ->
                    CompletedQuestCard(quest = quest)
                }
            }

            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun UserPointsCard(
    userPoints: UserPoints
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        text = "내 포인트",
                        fontFamily = NotoSansKR,
                        fontSize = 16.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${userPoints.totalPoints}P",
                        fontFamily = NotoSansKR,
                        fontSize = 24.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "오늘 획득",
                        fontFamily = NotoSansKR,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "+${userPoints.todayEarned}P",
                        fontFamily = NotoSansKR,
                        fontSize = 16.sp,
                        color = OrangePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lv.${userPoints.level}",
                    fontFamily = NotoSansKR,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.width(12.dp))

                LinearProgressIndicator(
                    progress = { userPoints.currentLevelProgress },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = OrangePrimary,
                    trackColor = GrayLight
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "${userPoints.nextLevelPoints}P",
                    fontFamily = NotoSansKR,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun WalkTrackingCard(
    walkStats: WalkStats,
    isWalkingActive: Boolean,
    onStartWalk: () -> Unit,
    onStopWalk: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isWalkingActive) OrangePrimary else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        fontFamily = NotoSansKR,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isWalkingActive) Color.White else TextPrimary
                    )
                    Text(
                        text = if (isWalkingActive) "진행 중" else "산책을 시작해보세요",
                        fontFamily = NotoSansKR,
                        fontSize = 14.sp,
                        color = if (isWalkingActive) Color.White.copy(alpha = 0.8f) else TextSecondary
                    )
                }

                FloatingActionButton(
                    onClick = { if (isWalkingActive) onStopWalk() else onStartWalk() },
                    modifier = Modifier.size(56.dp),
                    containerColor = if (isWalkingActive) Color.White else OrangePrimary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isWalkingActive) Icons.Default.Stop else Icons.AutoMirrored.Filled.DirectionsWalk,
                        contentDescription = if (isWalkingActive) "중지" else "시작",
                        tint = if (isWalkingActive) OrangePrimary else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            if (isWalkingActive && (walkStats.totalDistance > 0 || walkStats.duration > 0)) {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WalkStatItem(
                        label = "거리",
                        value = "%.2f km".format(walkStats.totalDistance / 1000f),
                        isActive = true
                    )
                    WalkStatItem(
                        label = "시간",
                        value = "${walkStats.duration / 60000}분",
                        isActive = true
                    )
                    WalkStatItem(
                        label = "걸음",
                        value = "${walkStats.stepCount}",
                        isActive = true
                    )
                }
            }
        }
    }
}

@Composable
fun WalkStatItem(
    label: String,
    value: String,
    isActive: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = NotoSansKR,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) Color.White else TextPrimary
        )
        Text(
            text = label,
            fontFamily = NotoSansKR,
            fontSize = 12.sp,
            color = if (isActive) Color.White.copy(alpha = 0.8f) else TextSecondary
        )
    }
}

@Composable
fun CategoryFilterRow(
    selectedCategory: QuestCategory,
    onCategorySelected: (QuestCategory) -> Unit
) {
    val categories = listOf(
        QuestCategory.ALL to "전체",
        QuestCategory.CARE to "돌봄",
        QuestCategory.EXERCISE to "운동",
        QuestCategory.HEALTH to "건강",
        QuestCategory.SOCIAL to "소셜"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { (category, label) ->
            CategoryChip(
                label = label,
                isSelected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) OrangePrimary else Color.White,
        border = if (!isSelected) BorderStroke(1.dp, GrayLight) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontFamily = NotoSansKR,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else TextPrimary
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontFamily = NotoSansKR,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun QuestCard(
    quest: Quest,
    onQuestClick: () -> Unit,
    onProgressUpdate: (Int) -> Unit,
    onRewardClaim: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onQuestClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuestIcon(iconType = quest.iconType)

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = quest.title,
                            fontFamily = NotoSansKR,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = quest.description,
                            fontFamily = NotoSansKR,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                QuestTypeChip(type = quest.type)
            }

            Spacer(modifier = Modifier.height(12.dp))

            QuestProgressBar(
                currentProgress = quest.currentProgress,
                maxProgress = quest.maxProgress,
                isCompleted = quest.isCompleted
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "포인트",
                        tint = OrangePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${quest.points}P",
                        fontFamily = NotoSansKR,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangePrimary
                    )
                }

                when {
                    quest.isCompleted && !quest.isRewarded -> {
                        Button(
                            onClick = onRewardClaim,
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = "보상 받기",
                                fontSize = 12.sp,
                                fontFamily = NotoSansKR
                            )
                        }
                    }
                    quest.isRewarded -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SuccessGreen.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "완료",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontFamily = NotoSansKR,
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = "${quest.currentProgress}/${quest.maxProgress}",
                            fontFamily = NotoSansKR,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestIcon(iconType: QuestIconType) {
    val icon = when (iconType) {
        QuestIconType.WALK -> Icons.AutoMirrored.Filled.DirectionsWalk
        QuestIconType.FEED -> Icons.Default.RestaurantMenu
        QuestIconType.HEALTH -> Icons.Default.Favorite
        QuestIconType.PLAY -> Icons.Default.SportsEsports
        QuestIconType.HOSPITAL -> Icons.Default.LocalHospital
        QuestIconType.PHOTO -> Icons.Default.PhotoCamera
        QuestIconType.SPECIAL -> Icons.Default.EmojiEvents
    }

    Surface(
        shape = CircleShape,
        color = OrangePrimary.copy(alpha = 0.1f),
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OrangePrimary,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun QuestTypeChip(type: QuestType) {
    val (label, color) = when (type) {
        QuestType.DAILY -> "일일" to OrangePrimary
        QuestType.WEEKLY -> "주간" to InfoBlue
        QuestType.SPECIAL -> "스페셜" to Color(0xFF9C27B0)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontFamily = NotoSansKR,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
fun QuestProgressBar(
    currentProgress: Int,
    maxProgress: Int,
    isCompleted: Boolean
) {
    val progress = if (maxProgress > 0) currentProgress.toFloat() / maxProgress else 0f

    Column {
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (isCompleted) SuccessGreen else OrangePrimary,
            trackColor = GrayLight
        )
    }
}

@Composable
fun CompletedQuestCard(quest: Quest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundGray.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuestIcon(iconType = quest.iconType)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.title,
                    fontFamily = NotoSansKR,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                quest.completedAt?.let { date ->
                    Text(
                        text = SimpleDateFormat("MM월 dd일 완료", Locale.KOREAN).format(date),
                        fontFamily = NotoSansKR,
                        fontSize = 12.sp,
                        color = TextTertiary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "완료됨",
                tint = SuccessGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
