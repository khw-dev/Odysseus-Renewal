package com.example.ppet.ui.pet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ppet.data.model.Pet
import com.example.ppet.model.PetCharacter
import com.example.ppet.model.PetCharacters
import com.example.ppet.model.PetRarity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetCharacterSelectionScreen(
    pet: Pet,
    onCharacterSelected: (Pet, PetCharacter) -> Unit,
    onBackClick: () -> Unit,
    viewModel: PetCharacterSelectionViewModel = hiltViewModel()
) {
    var selectedCharacter by remember { mutableStateOf<PetCharacter?>(null) }

    // 현재 펫의 캐릭터 로드
    LaunchedEffect(pet.id) {
        pet.characterId?.let { characterId ->
            selectedCharacter = PetCharacters.availableCharacters.find { it.id == characterId }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 상단 바
        TopAppBar(
            title = {
                Text(
                    "${pet.name}의 캐릭터 선택",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_revert),
                        contentDescription = "뒤로가기"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        // 펫 정보 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 현재 캐릭터 이미지 또는 기본 이미지
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedCharacter != null) {
                        Image(
                            painter = painterResource(selectedCharacter!!.imageRes),
                            contentDescription = selectedCharacter!!.name,
                            modifier = Modifier.size(45.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "?",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = pet.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${pet.type} • ${pet.age}살",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    if (selectedCharacter != null) {
                        Text(
                            text = "현재 캐릭터: ${selectedCharacter!!.name}",
                            fontSize = 12.sp,
                            color = Color(0xFF6200EE)
                        )
                    } else {
                        Text(
                            text = "캐릭터를 선택해주세요",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // 캐릭터 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(PetCharacters.availableCharacters) { character ->
                PetCharacterCard(
                    character = character,
                    isSelected = selectedCharacter?.id == character.id,
                    onCharacterClick = { selectedCharacter = character }
                )
            }
        }

        // 선택 버튼
        selectedCharacter?.let { character ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "선택된 캐릭터: ${character.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = character.description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.savePetCharacter(pet, character)
                            onCharacterSelected(pet, character)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE)
                        )
                    ) {
                        Text(
                            if (pet.characterId == character.id) "현재 캐릭터입니다" else "이 캐릭터로 변경",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PetCharacterCard(
    character: PetCharacter,
    isSelected: Boolean,
    onCharacterClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF6200EE) else Color.Transparent
    val backgroundColor = if (isSelected) Color(0xFFE8F5E8) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onCharacterClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 캐릭터 이미지
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(character.imageRes),
                    contentDescription = character.name,
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 캐릭터 이름
            Text(
                text = character.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )

            // 캐릭터 설명
            Text(
                text = character.description,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 희귀도 표시
            PetRarityChip(rarity = character.rarity)
        }
    }
}

@Composable
fun PetRarityChip(rarity: PetRarity) {
    val (color, text) = when (rarity) {
        PetRarity.COMMON -> Color(0xFF9E9E9E) to "일반"
        PetRarity.RARE -> Color(0xFF2196F3) to "레어"
        PetRarity.EPIC -> Color(0xFF9C27B0) to "에픽"
        PetRarity.LEGENDARY -> Color(0xFFFF9800) to "전설"
    }

    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
