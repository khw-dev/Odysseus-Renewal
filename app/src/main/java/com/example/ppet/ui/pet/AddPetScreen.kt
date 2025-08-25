package com.example.ppet.ui.pet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ppet.data.model.Pet
import com.example.ppet.ui.components.*
import com.example.ppet.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    onBack: () -> Unit,
    onSave: (Pet) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }

    val petTypes = listOf("강아지", "고양이", "토끼", "새", "기타")
    val genders = listOf("수컷", "암컷")

    
    val isFormValid = name.isNotBlank() && type.isNotBlank() && age.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "반려동물 추가",
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            
            PPetTextField(
                value = name,
                onValueChange = { name = it },
                label = "이름",
                placeholder = "반려동물의 이름을 입력하세요",
                leadingIcon = Icons.Default.Pets,
                isError = name.isBlank() && name.isNotEmpty(),
                errorMessage = "이름을 입력해주세요"
            )

            
            PPetDropdownField(
                value = type,
                onValueChange = { type = it },
                label = "동물 종류",
                options = petTypes,
                placeholder = "동물 종류를 선택하세요",
                leadingIcon = Icons.Default.Category,
                isError = type.isBlank() && type.isNotEmpty(),
                errorMessage = "동물 종류를 선택해주세요"
            )

            
            PPetTextField(
                value = breed,
                onValueChange = { breed = it },
                label = "품종",
                placeholder = "품종을 입력하세요 (선택사항)",
                leadingIcon = Icons.Default.Info
            )

            
            PPetTextField(
                value = age,
                onValueChange = { age = it },
                label = "나이",
                placeholder = "나이를 입력하세요",
                leadingIcon = Icons.Default.Cake,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = age.isBlank() && age.isNotEmpty(),
                errorMessage = "나이를 입력해주세요",
                trailingIcon = {
                    Text(
                        text = "세",
                        fontFamily = NotoSansKR,
                        fontSize = 16.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )

            
            PPetTextField(
                value = weight,
                onValueChange = { weight = it },
                label = "체중",
                placeholder = "체중을 입력하세요 (선택사항)",
                leadingIcon = Icons.Default.Scale,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = {
                    Text(
                        text = "kg",
                        fontFamily = NotoSansKR,
                        fontSize = 16.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )

            
            PPetDropdownField(
                value = gender,
                onValueChange = { gender = it },
                label = "성별",
                options = genders,
                placeholder = "성별을 선택하세요 (선택사항)",
                leadingIcon = Icons.Default.Wc
            )

            Spacer(modifier = Modifier.height(20.dp))

            
            Button(
                onClick = {
                    if (isFormValid) {
                        val pet = Pet(
                            id = "",
                            name = name,
                            type = type,
                            breed = breed.ifBlank { null },
                            age = age.toIntOrNull() ?: 0,
                            weight = weight.toDoubleOrNull(),
                            gender = gender.ifBlank { null },
                            imageUrl = null
                        )
                        onSave(pet)
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    disabledContainerColor = GrayLight
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "저장하기",
                    fontFamily = NotoSansKR,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
