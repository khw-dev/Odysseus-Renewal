package com.example.ppet.ui.character

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import com.example.ppet.model.PetCharacter
import com.example.ppet.model.PetCharacters
import com.example.ppet.model.PetRarity
import com.example.ppet.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSelectionScreen(
    onCharacterSelected: (PetCharacter) -> Unit,
    onBackClick: () -> Unit,
    viewModel: CharacterSelectionViewModel = hiltViewModel()
) {
    var selectedCharacter by remember { mutableStateOf<PetCharacter?>(null) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "캐릭터 선택",
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            selectedCharacter?.let { character ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            
                            Surface(
                                modifier = Modifier.size(60.dp),
                                shape = CircleShape,
                                color = getRarityColor(character.rarity).copy(alpha = 0.1f),
                                border = androidx.compose.foundation.BorderStroke(
                                    2.dp,
                                    getRarityColor(character.rarity)
                                )
                            ) {
                                Image(
                                    painter = painterResource(character.imageRes),
                                    contentDescription = character.name,
                                    modifier = Modifier.padding(8.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = character.name,
                                        fontFamily = NotoSansKR,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    RarityChip(character.rarity)
                                }
                                Text(
                                    text = character.description,
                                    fontFamily = NotoSansKR,
                                    fontSize = 14.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.selectCharacter(character)
                                onCharacterSelected(character)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangePrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "이 캐릭터로 결정하기",
                                fontFamily = NotoSansKR,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(PetCharacters.availableCharacters) { character ->
                CharacterCard(
                    character = character,
                    isSelected = selectedCharacter?.id == character.id,
                    onCharacterClick = { selectedCharacter = character }
                )
            }
        }
    }
}

@Composable
fun CharacterCard(
    character: PetCharacter,
    isSelected: Boolean,
    onCharacterClick: () -> Unit
) {
    val borderColor = if (isSelected) OrangePrimary else Color.Transparent
    val backgroundColor = if (isSelected) OrangePrimary.copy(alpha = 0.03f) else Color.White

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable { onCharacterClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = borderColor
        ) else null,
        shadowElevation = 0.dp 
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = getRarityColor(character.rarity).copy(alpha = 0.06f)
            ) {
                Image(
                    painter = painterResource(character.imageRes),
                    contentDescription = character.name,
                    modifier = Modifier.padding(12.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = character.name,
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                RarityChip(character.rarity)
            }

            Spacer(modifier = Modifier.height(8.dp))

            
            Text(
                text = character.description,
                fontFamily = NotoSansKR,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 2,
                lineHeight = 16.sp
            )

            
            if (isSelected) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = OrangePrimary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "선택됨",
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = OrangePrimary,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun RarityChip(rarity: PetRarity) {
    val (label, color) = when (rarity) {
        PetRarity.COMMON -> "일반" to Color(0xFF78909C)
        PetRarity.RARE -> "레어" to Color(0xFF5E35B1)
        PetRarity.EPIC -> "에픽" to Color(0xFFE65100)
        PetRarity.LEGENDARY -> "전설" to Color(0xFFD32F2F)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = label,
                fontFamily = NotoSansKR,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
fun getRarityColor(rarity: PetRarity): Color {
    return when (rarity) {
        PetRarity.COMMON -> Color(0xFF78909C)
        PetRarity.RARE -> Color(0xFF5E35B1)
        PetRarity.EPIC -> Color(0xFFE65100)
        PetRarity.LEGENDARY -> Color(0xFFD32F2F)
    }
}
