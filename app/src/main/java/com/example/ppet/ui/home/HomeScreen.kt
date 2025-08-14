package com.example.ppet.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ppet.R
import com.example.ppet.data.model.Pet as DataPet
import com.example.ppet.model.PetCharacter
import com.example.ppet.ui.home.viewmodel.PetViewModel
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String? = null,
    onNavigateToAround: () -> Unit = {},
    onNavigateToAddPet: () -> Unit = {},
    onNavigateToHealthRecord: (DataPet) -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onNavigateToLocationSetting: () -> Unit = {},
    petViewModel: PetViewModel = hiltViewModel(), // remember 대신 hiltViewModel 사용
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val pets by petViewModel.pets.collectAsState()
    val selectedPet by petViewModel.selectedPet.collectAsState()
    val currentCharacter by homeViewModel.currentCharacter.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            // 헤더
            HomeHeader(
                userName = userName ?: "사용자",
                currentCharacter = currentCharacter,
                onNotificationClick = onNavigateToNotification,
                onLocationClick = onNavigateToLocationSetting
            )
        }

        item {
            // 내 반려동물 섹션
            MyPetsSection(
                pets = pets,
                onAddPet = onNavigateToAddPet,
                onPetClick = { pet ->
                    petViewModel.selectPet(pet)
                    onNavigateToHealthRecord(pet)
                }
            )
        }

        item {
            // 빠른 기능 버튼들
            QuickActionsSection(
                onNavigateToAround = onNavigateToAround,
                onHealthRecordClick = {
                    selectedPet?.let { pet: DataPet ->
                        onNavigateToHealthRecord(pet)
                    } ?: run {
                        // 반려동물이 선택되지 않은 경우 첫 번째 반려동물 선택
                        pets.firstOrNull()?.let { firstPet: DataPet ->
                            petViewModel.selectPet(firstPet)
                            onNavigateToHealthRecord(firstPet)
                        }
                    }
                }
            )
        }

        item {
            // 오늘의 팁
            TodayTipSection()
        }

        item {
            // 최근 활동
            RecentActivitySection(pets = pets)
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    currentCharacter: com.example.ppet.model.PetCharacter?,
    onLocationClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "안녕하세요",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = NotoSansKR,
                color = Color.Gray
            )
            Text(
                text = "${userName}님!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = NotoSansKR,
                color = Color.Black
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onLocationClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFFF8F9FA),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "위치",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFFF8F9FA),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "알림",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun MyPetsSection(
    pets: List<DataPet>,
    onAddPet: () -> Unit,
    onPetClick: (DataPet) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "내 반려동물",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = NotoSansKR,
                color = Color.Black
            )

            TextButton(
                onClick = onAddPet
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "추가",
                    tint = OrangePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "추가",
                    color = OrangePrimary,
                    fontSize = 14.sp,
                    fontFamily = NotoSansKR
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pets) { pet ->
                PetCard(
                    pet = pet,
                    onClick = { onPetClick(pet) }
                )
            }
        }
    }
}

@Composable
private fun PetCard(
    pet: DataPet,
    onClick: () -> Unit
) {
    // 펫의 캐릭터 찾기
    val petCharacter = pet.characterId?.let { characterId ->
        com.example.ppet.model.PetCharacters.availableCharacters.find { it.id == characterId }
    }

    Surface(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 캐릭터 이미지 또는 반려동물 이미지
            if (petCharacter != null) {
                // 선택된 캐릭터 이미지 표시
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(petCharacter.imageRes),
                        contentDescription = petCharacter.name,
                        modifier = Modifier.size(45.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            } else if (pet.imageUrl != null) {
                // 실제 반려동물 이미지 표시
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(pet.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = pet.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 기본 아이콘 표시
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = OrangePrimary.copy(alpha = 0.1f)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (pet.type == "강아지") R.drawable.ic_pet_dog else R.drawable.ic_pet_cat
                        ),
                        contentDescription = pet.name,
                        tint = OrangePrimary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pet.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = NotoSansKR,
                color = Color.Black
            )

            Text(
                text = pet.type,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = NotoSansKR,
                color = Color.Gray
            )

            // 캐릭터 이름 표시 (선택적)
            if (petCharacter != null) {
                Text(
                    text = "🎭 ${petCharacter.name}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6200EE),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    onNavigateToAround: () -> Unit,
    onHealthRecordClick: () -> Unit
) {
    Column {
        Text(
            text = "빠른 기능",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansKR,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "병원 찾기",
                description = "주변 동물병원 검색",
                color = OrangePrimary,
                modifier = Modifier.weight(1f)
            ) { onNavigateToAround() }

            QuickActionCard(
                title = "건강 기록",
                description = "접종 및 진료 기록",
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            ) { onHealthRecordClick() }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    description: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = NotoSansKR,
                color = color
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = NotoSansKR,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun TodayTipSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8F9FA)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "💡 오늘의 팁",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = NotoSansKR,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "반려동물의 건강한 겨울나기를 위해 실내 온도를 20-22도로 유지해주세요. 급격한 온도 변화는 스트레스의 원인이 될 수 있습니다.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = NotoSansKR,
                color = Color.Gray,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun RecentActivitySection(pets: List<DataPet>) {
    Column {
        Text(
            text = "최근 활동",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = NotoSansKR,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        pets.forEach { pet ->
            ActivityItem(
                activity = Activity(
                    id = pet.id,
                    title = "${pet.name}의 건강 상태가 업데이트되었습니다.",
                    time = "방금"
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ActivityItem(activity: Activity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(8.dp),
            shape = CircleShape,
            color = OrangePrimary
        ) {}

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = NotoSansKR,
                color = Color.Black
            )

            Text(
                text = activity.time,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = NotoSansKR,
                color = Color.Gray
            )
        }
    }
}

// 데이터 클래스들
data class Activity(
    val id: String,
    val title: String,
    val time: String
)
