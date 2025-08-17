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
import com.example.ppet.ui.home.viewmodel.PetViewModel
import com.example.ppet.ui.theme.NotoSansKR
import com.example.ppet.ui.theme.OrangePrimary
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String? = null,
    onNavigateToAround: () -> Unit = {},
    onNavigateToAddPet: () -> Unit = {},
    onNavigateToHealthRecord: (DataPet) -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onNavigateToLocationSetting: () -> Unit = {},
    petViewModel: PetViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val pets by petViewModel.pets.collectAsState()
    val selectedPet by petViewModel.selectedPet.collectAsState()
    val currentCharacter by homeViewModel.currentCharacter.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TodayTipSection()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                HomeHeader(
                    userName = userName ?: "사용자",
                    currentCharacter = currentCharacter,
                    onNotificationClick = onNavigateToNotification,
                    onLocationClick = onNavigateToLocationSetting
                )
            }

            item {
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
                RecentActivitySection(pets = pets)
            }
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
                text = "안녕하세요,",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = NotoSansKR,
                color = Color.Black
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = OrangePrimary)) {
                        append(userName)
                    }
                    withStyle(style = SpanStyle(color = Color.Black)) {
                        append(" 님!")
                    }
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = NotoSansKR
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
            if (petCharacter != null) {
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
        }
    }
}

@Composable
private fun TodayTipSection() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
            .background(
                color = Color(0xFFF7F7F7),
            ),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_fluent_dog),
                contentDescription = "배너 아이콘",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(17.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "중요한 소식이 있어요!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = NotoSansKR,
                    color = Color(0xFF424242),
                    lineHeight = 18.sp
                )
            }
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

data class Activity(
    val id: String,
    val title: String,
    val time: String
)
